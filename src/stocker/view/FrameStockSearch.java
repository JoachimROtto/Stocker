package stocker.view;
import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import stocker.PrefChangeListeners;
import stocker.controller.*;
import stocker.model.*;

import java.awt.*;
import java.awt.event.*;

/**
 * 
 * Die Klasse <code>FrameStockSearch</code> bildet den Suchdialog
 * 
 * @author Joachim Otto
 */

public class FrameStockSearch extends JDialog implements PrefChangeListeners {

    JDesktopPane desktop;
    StockerAppSettings sas ;
    boolean fromWL=false;
    
    /**
     * Das Model wird initialisiert mit  
     * @param sas Den Anwendungseinstellungen
     * 
     */ 

    public FrameStockSearch(StockerAppSettings sas) {
        this(sas, false);
        }

    /**
     * Das Model wird initialisiert mit  
     * @param sas Den Anwendungseinstellungen
     * @param fromWL Herkunft Watchlist?
     * 
     */ 

    //Braucht kein Minimal-Singleton weil modal       
    public FrameStockSearch(StockerAppSettings sas, boolean fromWL) {
       super (sas.getFrame(), "Aktiensuche", Dialog.ModalityType.APPLICATION_MODAL);
       this.desktop=sas.getDesktop();
       this.sas=sas;
       this.fromWL=fromWL;

       //Es passiert nur der Dialogaufbau
       
       setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
       JPanel result=new JPanel();
       result.setLayout (new GridBagLayout());

       GridBagConstraints c = new GridBagConstraints();
       c.fill = GridBagConstraints.BOTH;
       c.weightx = 0.0;
       c.weighty = 0.0;
       c.gridwidth = 1;
       c.insets = new Insets (0, 5, 0, 5);
       c.ipadx = 20;
       c.gridwidth = 1;
       c.fill = GridBagConstraints.NONE;
       c.anchor = GridBagConstraints.LINE_START;

       //Vorgezogen, weil muss in den Listener
       FrameStockSearchModel model = new FrameStockSearchModel();
       
       JLabel label = new JLabel("Suchbegriff");          
       c.gridx = 0;
       c.gridy = 0;
       result.add(label,c);
       
       JTextField textSearchTerm=new JTextField(10);
       
       JButton startSearchButton = new JButton("Suche starten");
       FrameStockSearchListener startSearch= new FrameStockSearchListener(textSearchTerm, model, sas);
       startSearchButton.addActionListener(startSearch) ;
       textSearchTerm.addActionListener(startSearch);
       c.gridx = 0;
       c.fill=GridBagConstraints.HORIZONTAL;
       c.gridy = 1;
       result.add(textSearchTerm,c);
       c.gridx = 1;
       c.gridy = 1;
       result.add(startSearchButton,c);

       label = new JLabel("Ergebnisse");
       c.gridx = 0;
       c.gridy = 2;
       result.add(label,c);
       
       JTable table = new JTable (model);
       JScrollPane scrollingTable = new JScrollPane (table,
                     ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                     ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
       table.setColumnSelectionAllowed(false);
       table.getColumnModel().getColumn(0).setPreferredWidth(sas.getColwidthSearchResult1());
       table.getColumnModel().getColumn(1).setPreferredWidth(sas.getColwidthSearchResult2());
       table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       table.addMouseListener(new FrameStockSearchAccompListener(table, sas, (fromWL ?4 : 1)) );
       
       // Der TableRowSorter wird die Daten des models sortieren
       TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>();
       table.setRowSorter( sorter );
       sorter.setModel( model );
       
       JPopupMenu tableContextMenu = new  JPopupMenu();
       JMenuItem takeMeToTheWatchlist = new JMenuItem("Übernahme in Watchlist");
       JMenuItem takeMeToTheChart = new JMenuItem("Chart aufrufen");
       tableContextMenu.add(takeMeToTheWatchlist);
       tableContextMenu.add(takeMeToTheChart);
       table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
       
       takeMeToTheWatchlist.addActionListener(new FrameStockSearchAccompListener(table, sas, 3));
       takeMeToTheChart.addActionListener(new FrameStockSearchAccompListener(table, sas, 2));
       table.getTableHeader().addMouseListener( new MouseAdapter() {
           @Override  
             public void mouseReleased(MouseEvent arg0)
             {
               sas.setColSearchFrame(table.getColumnModel().getColumn(0).getWidth(),
                       table.getColumnModel().getColumn(1).getWidth());
             }});

       table.setComponentPopupMenu(tableContextMenu);
       c.gridx = 0;
       c.gridy = 3;
       c.gridwidth= 2;
       c.gridheight=5;
       c.fill=GridBagConstraints.BOTH;
       c.weighty=1.0;
       result.add (scrollingTable,c);
       
       JButton acceptSelButton = new JButton("Auswahl akzeptieren");
       acceptSelButton.addActionListener(new FrameStockSearchAccompListener(table, sas, (fromWL ?4 : 1)));
       JButton exitButton = new JButton("Beenden");
       exitButton.addActionListener(new ActionListener() {  
           @Override
           public void actionPerformed (ActionEvent actionEvent) {
               dispose();    }}) ;
       c.gridx = 0;
       c.gridy = 8;
       c.gridwidth=1;
       c.fill=GridBagConstraints.NONE;

       result.add(acceptSelButton,c );
       c.gridx = 1;
       c.gridy = 8;
       c.gridwidth=1;
       c.weighty=0.0;

       result.add(exitButton,c );
       add(result);
       this.addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent e ) {
               dispose();
               }
           });
       setSize(sas.getsizeSearchFrame());
       setMinimumSize(sas.getPrefer().getMinSearchFrame());
       setVisible(true);
       requestFocus();
   }   
    
    /**
     * Der Dialog wird informiert, dass die Voreinstellungen geändert wurden
     */
    
    @Override
    public void notifyPrefChanged(boolean databrokerChanged) {
        //reicht, denn Suche und Pref. können nicht parallel offen sein
        setMinimumSize(sas.getPrefer().getMinSearchFrame());
        // reicht insgesamt, denn der Pull im model wird immer neu gebaut
    }
}
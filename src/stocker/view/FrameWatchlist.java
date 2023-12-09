package stocker.view;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableModel;

import stocker.PrefChangeListeners;
import stocker.controller.*;
import stocker.model.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;

/**
 * 
 * Die Klasse <code>FrameWatchlist</code>bildet die Watchlist ab
 * 
 * @author Joachim Otto
 */

public class FrameWatchlist extends JInternalFrame implements PrefChangeListeners{

    JDesktopPane desktop;
    //Vorgezogen, weil muß in den Listener
    private FrameStockWatchlistModel model;
    StockerAppSettings sas ;
    JTable table;

    private static FrameWatchlist instance;

    /**
     * Liefert eine Referenz auf die Watchlist
     * @param sas Die Programmeinstellungen
     * @return Die Referenz
     */

    public static FrameWatchlist getInstance(StockerAppSettings sas) {

        if (instance==null) {
            instance=new FrameWatchlist(sas);
            sas.getDesktop().add (instance);
            sas.getFrame().add2WindowList(instance);
        }
        instance.show();
        instance.requestFocus();
        instance.setFocusable(false);  
        return instance;
    }
    
    /**
     * Bei stetem Fluss von Handelsdaten versucht die Watchlist sich in den Vordergrund zu drängen. 
     * Insbesondere modale Dialoge können hier Abhilfe schaffen. 
     * 
     */
    

    FrameWatchlist(StockerAppSettings sas) {

        super ("Watchlist", true, true, true, true);
        this.sas=sas;

        //Layout

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        JMenuBar menuBar = new JMenuBar();

        JMenu searchMenu = new JMenu("Aktien hinzufügen");
        searchMenu.setMnemonic(KeyEvent.VK_A);
        searchMenu.setMnemonic('a');

        JMenuItem searchMenuItem = new JMenuItem("Aktien hinzufügen");
        searchMenuItem.setMnemonic('h');
        searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
        searchMenuItem.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                //true=von WL aufgerufen und daher bestätigte Einträge in WL auch wenn es anders Setup steht
                new FrameStockSearch(sas, true);
            }}) ;
        searchMenu.add( searchMenuItem);
        menuBar.add(searchMenu);
        setJMenuBar (menuBar);

        JPanel resultPanel=new JPanel();
        resultPanel.setLayout( new BorderLayout()) ;
        setModel(new FrameStockWatchlistModel(sas));
        table = new JTable (getModel());
        getModel().setTable(table);

        JScrollPane scrollingTable = new JScrollPane (table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        table.setColumnSelectionAllowed(false);
        table.setSelectionBackground(Color.BLUE);
        table.setSelectionForeground(Color.GREEN);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(sas.getColwidthWL1());
        table.getColumnModel().getColumn(1).setPreferredWidth(sas.getColwidthWL2());
        table.getColumnModel().getColumn(2).setPreferredWidth(sas.getColwidthWL3());
        table.getColumnModel().getColumn(3).setPreferredWidth(sas.getColwidthWL4());

        scrollingTable.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));

        //Der neue Renderer für das Farbenspiel
        TableCellRenderer wl =  new ColoredWLCellRenderer(table, this);
        table.setDefaultRenderer(Object.class,wl);

        JPopupMenu tableContextMenu = new  JPopupMenu();
        JMenuItem removeFromWatchlist = new JMenuItem("Aktie entfernen");
        JMenuItem takeMeToTheChart = new JMenuItem("Chart aufrufen");
        tableContextMenu.add(removeFromWatchlist);
        tableContextMenu.add(takeMeToTheChart);

        // Der TableRowSorter wird die Daten des models sortieren
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>();
        table.setRowSorter( sorter );
        sorter.setModel( getModel() );

        table.setAlignmentX(12);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

        removeFromWatchlist.addActionListener(new FrameWatchlistMenuListener(table, getModel(), sas, 1));
        takeMeToTheChart.addActionListener(new FrameWatchlistMenuListener(table, getModel(), sas, 2));
        table.getTableHeader().addMouseListener( new MouseAdapter() {
            @Override  
            public void mouseReleased(MouseEvent arg0)
            {
                sas.setColWLFrame(table.getColumnModel().getColumn(0).getWidth(),
                        table.getColumnModel().getColumn(1).getWidth(),
                        table.getColumnModel().getColumn(2).getWidth(),
                        table.getColumnModel().getColumn(3).getWidth());
            }});
        table.setComponentPopupMenu(tableContextMenu);
        resultPanel.add (scrollingTable);
        add(resultPanel);
        setSize(sas.getsizeWLFrame());
        setMinimumSize(sas.getPrefer().getMinWatchlistFrame());

        //Falls die gesicherte Grösse mittlerweile zu klein ist
        if (getMinimumSize().height>getSize().height || 
                getMinimumSize().width>getSize().width ) {
            setSize(getMinimumSize());
        }
        repaint();

        //Einträge vom letzten Programmende wieder herstellen
        if (sas.getStockIDsWLArray()!=null) {
            if(sas.getStockIDsWLArray().length>0) {
                this.addStocksOnStartup();
            }
        }
        setVisible(true);

        //FrameWatchlist locFrame = this;

        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            // Watchlist persitiert immer, d.h. es gibt einen festen Menueintrag und hier werden keine Einträge gelöscht 
            public void internalFrameClosing(InternalFrameEvent e) {
                //sas.getFrame().removeFromWindowList(locFrame);
                saveBeforeLeave();
                instance.setVisible(false);
                instance.setFocusable(false);  
            }
        });
    }

    /**
     * Trägt eine neue Aktie in die Watchlist ein
     * @param stockID Kennung der Aktie
     * @param name Name der Aktie
     */
    public void addStock(String stockID, String name) {
        getModel().addValue(stockID, name);
    }

    /**
     * Stellt persistierte Watchlisteinträge wieder her 
     */

    public void addStocksOnStartup() {
        if (sas.getStockIDsWLArray()==null) {
            return;
        }
        String [][] stocks=sas.getStockIDsWLArray();
        for (int i=0; i<stocks.length; i++) {
            if (stocks[i][0]!=null) {
                getModel().addValue(stocks[i][0], stocks[i][1]);
            }}}

    /**
     * Persitiert die Einträge.
     */

    public void saveBeforeLeave() {
        //Concurrency-Issue beim Startup umgehen
        sas.setStockIDsWLArray(getModel().getStocksAsArray());
        if (sas.getStockIDsWL()!=null) {
            sas.getStockIDsWL().clear();
        }
        sas.setsizeWLFrame(getSize());
    }

    /**
     * Verkündet Änderungen in den Voreinstellungen
     * @param dataBrokerChanged Geänderter Datenprovider?
     */

    @Override
    public void notifyPrefChanged(boolean dataBrokerChanged) {
        //Neue Mindestgrösse kleiner als aktuelle Grösse?
        setMinimumSize(sas.getPrefer().getMinWatchlistFrame());
        if (getMinimumSize().height>getSize().height || 
                getMinimumSize().width>getSize().width ) {
            setSize(getMinimumSize());
        }
        repaint();
        getModel().receivePrefChangeNotification(dataBrokerChanged);
    }

    /**
     * Liefert das aktuelle Watchlistmodell 
     * @return Das Modell
     */

    public FrameStockWatchlistModel getModel() {
        return model;
    }

    /**
     * Tauscht das aktuelle Watchlistmodell aus 
     * @param model Das neue Modell
     */

    public void setModel(FrameStockWatchlistModel model) {
        this.model = model;
    }}   
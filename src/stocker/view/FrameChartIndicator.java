package stocker.view;

import javax.swing.*;

import stocker.model.*;

import java.awt.*;
import java.awt.event.*;

/**
 * 
 * Die Klasse <code>FrameChartIndicator</code>bildet den Erfassungsdialog 
 * für die Indikatoreinstellungen ab
 * 
 * @author Joachim Otto
 */


public class FrameChartIndicator extends JDialog{

    JDesktopPane desktop;
    StockerAppSettings sas;
    int mode=0;
    FrameChart frame;
    FrameChartIndicatorModel annModel;
    String stockID;
    FrameChartModel chartModel;
    JTextField fieldN;
    JTextField fieldF;
    ChartIndicator item;
    Color itemColor;

    /**
     * Das Model wird initialisiert mit  
     * @param sas Den Programmeinstellungen
     * @param frame Dem zugeordneten Chart-Frame
     * @param chartModel Dem zugeordneten Chart-Model
     */ 

    public FrameChartIndicator (StockerAppSettings sas, FrameChart frame, FrameChartModel chartModel) {

        super (sas.getFrame(), "Indikatoren verwalten", Dialog.ModalityType.APPLICATION_MODAL);
        this.desktop=sas.getDesktop();
        this.chartModel=chartModel;
        this.sas=sas;
        this.frame=frame;
        stockID=frame.getModel().getStockID();
        JComboBox<String> modelist ;

        annModel = new FrameChartIndicatorModel(frame.getModel().getIndicator());

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        JPanel result=new JPanel();
        result.setLayout (new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.insets = new Insets (0, 5, 0, 5);
        c.ipadx = 20;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        JLabel label = new JLabel("Neuer Indikator");          
        c.gridx = 0;
        c.gridy = 0;
        result.add(label,c);

        String [] stringModes={"Gleitender Durchschnitt", "Bollinger Bänder"} ;
        modelist = new JComboBox<String>(stringModes);
        modelist.setSelectedIndex(0);
        modelist.addActionListener(new ActionListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(ActionEvent e) {
                mode= ((JComboBox<String>) e.getSource()).getSelectedIndex();
                fieldF.setEnabled((mode==1));
                fieldF.repaint();
            }});        
        c.gridx = 0;
        c.gridy = 1;
        result.add(modelist,c);


        label = new JLabel ("Angabe n/m");
        c.gridx = 0;
        c.gridy = 3;
        result.add (label, c);         

        fieldN = new JTextField ();
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weightx = 1.0;
        fieldN.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField source =((JTextField) e.getSource());
                checkedInt(source.getText(), source);
            }});
        result.add (fieldN, c);      

        label = new JLabel ("Angabe f");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        result.add (label, c);

        fieldF = new JTextField ();
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 1.0;
        fieldF.setEnabled(false);
        fieldF.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField source =((JTextField) e.getSource());
                checkedDouble(source.getText(), source);
            }});
        result.add (fieldF, c);     

        itemColor=sas.getPrefer().getIndikatorColor();
        JButton butIndi = new JButton("Farbe");
        butIndi.setOpaque(true);
        butIndi.setForeground(itemColor);
        butIndi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(FrameChartIndicator.this,
                        "Wählen Sie eine Farbe", itemColor);
                butIndi.setForeground(newColor);
                itemColor=newColor;
                butIndi.repaint();
            }});  
        c.gridx = 0;
        c.gridy = 4;
        result.add(butIndi,c);


        FrameChartIndicator locFrame=this;
        JButton setIndiButton = new JButton("Indikator hinzufügen");
        setIndiButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                int fieldNAsInt=Integer.valueOf(fieldN.getText());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mode==0) {                    
                            item= new MovingAverage(fieldNAsInt, chartModel, sas.getPrefer(), itemColor);
                        }
                        else {
                            double fieldFAsInt=Double.valueOf(fieldF.getText());               
                            item= new BollingerBand(fieldFAsInt, fieldNAsInt, chartModel, sas.getPrefer(), itemColor);
                        }
                        sas.setsizeIndicatorFrameAdd(getSize());
                        locFrame.receiveIndi(item);
                    }
                }).start();
            }}) ;
        c.gridx = 1;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.gridy = 5;
        result.add(setIndiButton,c);

        label = new JLabel("Gesetzte Indikatoren");
        c.gridx = 0;
        c.gridy = 6;
        result.add(label,c);


        JTable table = new JTable (annModel);
        JScrollPane scrollingTable = new JScrollPane (table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        JPopupMenu tableContextMenu = new  JPopupMenu();
        JMenuItem removeIndi = new JMenuItem("Indikator entfernen");
        tableContextMenu.add(removeIndi);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);


        removeIndi.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                if (table.getSelectedRow() == -1) {
                    JOptionPane.showConfirmDialog(null, "Kein Indikator ausgewählt.", "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
                }
                else {
                    ChartIndicator val =(ChartIndicator) annModel.getRealValueAt(table.getSelectedRow(), 0);
                    annModel.removeValue(val);
                }
            }}) ;

        table.setComponentPopupMenu(tableContextMenu);
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth= 2;
        c.gridheight=2;
        c.fill=GridBagConstraints.BOTH;
        c.weighty=1.0;
        result.add (scrollingTable,c);

        JButton acceptButton = new JButton("Anwenden");
        acceptButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                if (annModel.getValues()!=null) {
                    frame.receiveIndicatorSettings(annModel.getValues());
                }
                sas.setsizeIndicatorFrame(getSize());
                dispose();
            }}) ;

        c.gridx = 0;
        c.gridy = 9;
        c.gridwidth=1;
        c.weighty=0.0;
        result.add(acceptButton,c );

        JButton exitButton = new JButton("Verwerfen");
        exitButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                sas.setsizeIndicatorFrame(getSize());
                dispose();    }}) ;

        c.gridx = 1;
        c.gridy = 9;
        c.gridwidth=1;
        c.weighty=0.0;
        result.add(exitButton,c );

        add(result);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e ) {
                sas.setsizeIndicatorFrame(getSize());
                dispose();
            }
        });
        setSize(sas.getsizeIndicatorFrame());
        setVisible(true);
    }

    /**
     * Liefert die Kennung der Aktie des zugeörigen Charts
     * @return Die Kennung
     */

    public String getStockID() {
        return stockID;
    }

    /**
     * Liefert die Art des hinzugefügten zurück: 0:GD, 1:BB
     * @return Die Kennung
     */

    public int getMode() {
        return mode;
    }
    void receiveIndi(ChartIndicator indi){
        annModel.addValue(indi);
    }

    void checkedDouble(String input, JTextField source) {
        if (input.isEmpty()) {return;}
       try {
           Double.valueOf(input).toString();
       }
       catch (Exception e) {
           JOptionPane.showConfirmDialog(null, "Bitte eine Zahl mit . als Trenner angeben.", "Achtung",
                   JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);  
           source.requestFocus();
       }}
    
    void checkedInt(String input, JTextField source) {
        if (input.isEmpty()) {return;}
        try {
            Integer.valueOf(input);
        }
        catch (Exception e) {
            JOptionPane.showConfirmDialog(null, "Bitte eine Zahl angeben.", "Achtung",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);  
            source.requestFocus();
        }
    }}


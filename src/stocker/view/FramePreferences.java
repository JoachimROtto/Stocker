package stocker.view;

import javax.swing.*;
import javax.swing.event.*;

import stocker.controller.*;
import stocker.model.*;

import java.awt.*;
import java.awt.event.*;

/**
 * 
 * Die Klasse <code>FramePreferences</code> bildet das Fenster Voreinstellungen ab
 *
 * @author Joachim Otto
 *
 */

public class FramePreferences extends JInternalFrame {

    PreferencesModel model ;
    StockerAppSettings sas ;
     FrameDatabrokerAdd brokerAddFrame;
    JComboBox<String> brokerList;
       
    private static FramePreferences instance;
    
    /**
     * Es kann eine Referenz auf eine Instanz bezogen werden 
     * 
     * @param sas den AppSettings 
     * @return Eine Referenz auf den Dialog
     */
    
    public static FramePreferences  getInstance(StockerAppSettings sas) {
        if (instance==null) {
            instance=new FramePreferences(sas);
            sas.getDesktop().add (instance);
            sas.getFrame().add2WindowList(instance);
        }
        
        instance.show();
        instance.requestFocus();
        return instance;
    }

    
    FramePreferences(StockerAppSettings sas) {
        super ("Voreinstellungen", true, true, true, true);
        this.model=new PreferencesModel(sas);
        model.setPreferences();
        this.sas=sas;
        //Eine weitere Referenz für die Verwendung in Listenern 
        FramePreferences locFrame = this;
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLayout (new FlowLayout()); 
      
        JPanel tabPanel = new JPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab ("Datenanbieter", databrokerTab());
        tabbedPane.addTab ("Optionen", OptAppTab());
        tabbedPane.addTab ("Fenstergrößen", windowsMinSize());
        tabPanel.add(tabbedPane);
        add(tabPanel);
        
        JPanel okCancelPanel = new JPanel();
        JButton acceptSelButton = new JButton("Änderungen speichern");
        acceptSelButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                model.savePreferences();
                sas.getFrame().removeFromWindowList(locFrame);
                FramePreferences.instance=null;
                dispose();    }}) ;
        JButton exitButton = new JButton("Abbrechen");
        exitButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                sas.getFrame().removeFromWindowList(locFrame);
                FramePreferences.instance=null;
                sas.setPrefer(sas.getPrefer().getPrefFromJSONFile());
                dispose();    }}) ;
        okCancelPanel.add(acceptSelButton);
        okCancelPanel.add(exitButton);
        add(okCancelPanel, BorderLayout.PAGE_END);
        
        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                sas.getFrame().removeFromWindowList(locFrame);
                sas.setsizePrefFrame(getSize());
                sas.setPrefer(sas.getPrefer().getPrefFromJSONFile());
                instance=null;
                dispose();
                }
            });
        pack();    
        setVisible(true);
        requestFocus();
        setSize(sas.getsizePrefFrame());
        setMinimumSize(sas.getPrefer().getMinPreferencesFrame());
    }
    
    //Panel Mindestgrössen
    JPanel windowsMinSize() {
        JPanel pane=new JPanel();
        pane.setLayout (new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        // Element fuellt Zelle in X- und Y-Richtung aus
        c.fill = GridBagConstraints.BOTH;
        // Keine Ausdehnung in X-Richtung
        c.weightx = 0.0;
        // Keine Ausdehnung in Y-Richtung
        c.weighty = 0.0;
        // Element ist eine Spalte breit
        c.gridwidth = 1;
        // Abstaende links und rechts sind 5 Pixel gross
        c.insets = new Insets (0, 5, 0, 5);
        // Inneren Abstand auf 20 Pixel setzen
        c.ipadx = 20;
        // Eigenschaftsaenderung: Folgende Elemente seien eine Spalte breit
        c.gridwidth = 1;
        // Eigenschaftsaenderung: Folgende Elemente behalten Ihre natuerliche Groesse
        c.fill = GridBagConstraints.NONE;
        // Eigenschaftsaenderung: Folgende Elemente orientieren sich am Zeilenanfang
        c.anchor = GridBagConstraints.LINE_START;
             
        JLabel label = new JLabel ("Minimale Fenstergrößen");
        c.gridx = 0;
        c.gridy = 0;
        pane.add (label, c);         

        label = new JLabel ("Chartfenster");
        c.gridx = 0;
        c.gridy = 1;
        pane.add (label, c);   
        label = new JLabel ("Höhe");
        c.gridx = 0;
        c.gridy = 2;
        pane.add (label, c);
        
        JTextField fieldCH = new JTextField (Integer.toString(model.getMinChartSize().height), 5 );
        JTextField fieldCB = new JTextField (Integer.toString(model.getMinChartSize().width) , 5);
        fieldCH.addFocusListener(new FramePreferencesTextListener(1,1, fieldCH, fieldCB, model));
        fieldCB.addFocusListener(new FramePreferencesTextListener(1,2,fieldCB,fieldCH, model));
        fieldCH.addKeyListener(new FramePreferencesListener());
        fieldCB.addKeyListener(new FramePreferencesListener());   
        c.gridx = 1;
        c.gridy = 2;
        pane.add (fieldCH, c);      
        label = new JLabel ("Breite");
        c.gridx = 2;
        c.gridy = 2;
        pane.add (label, c);
        c.gridx = 3;
        c.gridy = 2;
        pane.add (fieldCB, c);      

        label = new JLabel ("Suchfenster");
        c.gridx = 0;
        c.gridy =3;
        pane.add (label, c);         
        label = new JLabel ("Höhe");
        c.gridx = 0;
        c.gridy = 4;
        pane.add (label, c);
        JTextField fieldSH = new JTextField (Integer.toString(model.getMinSearchSize().height), 5 );
        JTextField fieldSB = new JTextField (Integer.toString(model.getMinSearchSize().width), 5 );
        fieldSH.addFocusListener(new FramePreferencesTextListener(2,1, fieldSH, fieldSB, model));
        fieldSB.addFocusListener(new FramePreferencesTextListener(2,2,fieldSB, fieldSH, model));
        fieldSH.addKeyListener(new FramePreferencesListener());
        fieldSB.addKeyListener(new FramePreferencesListener());
        c.gridx = 1;
        c.gridy = 4;
        pane.add (fieldSH, c);      
        label = new JLabel ("Breite");
        c.gridx = 2;
        c.gridy = 4;
        pane.add (label, c);
        c.gridx = 3;
        c.gridy = 4;
        pane.add (fieldSB, c);      
        
        label= new JLabel ("Watchlist");
        c.gridx = 0;
        c.gridy = 5;
        pane.add (label, c);          
        label = new JLabel ("Höhe");
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        pane.add (label, c);
        JTextField fieldWH = new JTextField (Integer.toString(model.getMinWLSize().height) ,5);
        JTextField fieldWB = new JTextField (Integer.toString(model.getMinWLSize().width), 5);
        fieldWH.addFocusListener(new FramePreferencesTextListener(3,1,fieldWH,fieldWB, model));
        fieldWB.addFocusListener(new FramePreferencesTextListener(3,2,fieldWB,fieldWH, model));
        fieldWH.addKeyListener(new FramePreferencesListener());
        fieldWB.addKeyListener(new FramePreferencesListener());

        c.gridx = 1;
        c.gridy = 6;
        pane.add (fieldWH, c);      
        label = new JLabel ("Breite");
        c.gridx = 2;
        c.gridy = 6;
        c.gridwidth = 1;
        pane.add (label, c);
        c.gridx = 3;
        c.gridy = 6;
        pane.add (fieldWB, c);    
        
        label= new JLabel ("Voreinstellungen");
        c.gridx = 0;
        c.gridy = 7;
        pane.add (label, c);          
        label = new JLabel ("Höhe");
        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = 1;
        pane.add (label, c);
        JTextField fieldPH = new JTextField (Integer.toString(model.getMinPrefSize().height) , 5);
        JTextField fieldPB = new JTextField (Integer.toString(model.getMinPrefSize().width) ,5 );
        fieldPH.addFocusListener(new FramePreferencesTextListener(4,1,fieldPH, fieldPB, model));
        fieldPB.addFocusListener(new FramePreferencesTextListener(4,2,fieldPB, fieldPH, model));
        fieldPH.addKeyListener(new FramePreferencesListener());
        fieldPB.addKeyListener(new FramePreferencesListener());

        c.gridx = 1;
        c.gridy = 8;
        pane.add (fieldPH, c);      
        label = new JLabel ("Breite");
        c.gridx = 2;
        c.gridy = 8;
        c.gridwidth = 1;
        pane.add (label, c);
        c.gridx = 3;
        c.gridy = 8;
        pane.add (fieldPB, c);    
        
        return pane;
    }
    
    //Panel Chartdarstellung und ähnliches
    JPanel OptAppTab() {
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
             
        JLabel label = new JLabel("Übernahme Suchergebnis");
        c.gridx = 0;
        c.gridy = 0;
        result.add (label, c);
        JComboBox <String>transmodelist = new JComboBox<>(model.getSearchTo()) ;
        transmodelist.setSelectedIndex(model.getSearchtToInd());
        transmodelist.addActionListener(new ActionListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setSearchTo(((JComboBox<String>) e.getSource()).getSelectedIndex());
                }});        
        c.gridx = 1;
        c.gridy = 0;
        result.add(transmodelist,c);
        label = new JLabel("Darstellung Chart");
        c.gridx = 0;
        c.gridy = 1;
        result.add (label, c);
        JComboBox<String> modelist = new JComboBox<>(model.getChartModes());
        modelist.setSelectedIndex(model.getCChartModeIndex());
        modelist.addActionListener(new ActionListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setDefaultChartType(((JComboBox<String>) e.getSource()).getSelectedItem().toString());
                }});        
         c.gridx = 1;
         c.gridy = 1;
         result.add(modelist,c);
         
         label = new JLabel("Standardzeitinterval");
         c.gridx = 0;
         c.gridy = 2;
         result.add (label, c);
         JComboBox <String>intervalList = new JComboBox<>(model.getChartIntervalLabels());
         intervalList.setSelectedIndex(model.getCChartIntervalIndex());
         intervalList.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 model.setDefaultChartInterval(intervalList.getSelectedIndex());
             }});        
         c.gridx = 1;
         c.gridy = 2;
         result.add(intervalList,c);
         JButton butAlarm = new JButton("Farbe Alarmlinien");
         butAlarm.setOpaque(true);
         butAlarm.setForeground(model.getCAlarmColor());
         butAlarm.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 Color newColor = JColorChooser.showDialog(FramePreferences.this,
                         "Wählen Sie eine Farbe", model.getCAlarmColor());
                 butAlarm.setForeground(newColor);
                 model.setCAlarmColor(newColor);
                 butAlarm.repaint();
             }});     
         c.gridx = 0;
         c.gridy = 3;
         result.add(butAlarm,c);
        
         JButton butIndi = new JButton("Farbe Indikator");
         butIndi.setOpaque(true);
         butIndi.setForeground(model.getCIndiColor());
         butIndi.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 Color newColor = JColorChooser.showDialog(FramePreferences.this,
                         "Wählen Sie eine Farbe", model.getCIndiColor());
                 butIndi.setForeground(newColor);
                 model.setCIndiColor(newColor);
                 butIndi.repaint();
             }});  

         c.gridx = 1;
         c.gridy = 3;
         result.add(butIndi,c);
         JButton butBB = new JButton("Farbe Bollinger Bänder");
         butBB.setOpaque(true);
         butBB.setForeground(model.getBBColor());
         butBB.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 Color newColor = JColorChooser.showDialog(FramePreferences.this,
                         "Wählen Sie eine Farbe", model.getBBColor());
                 butBB.setForeground(newColor);
                 model.setBBColor(newColor);
                 butBB.repaint();
             }});  

         c.gridx = 1;
         c.gridy = 3;
        // result.add(butBB,c);
         return result;
    }
    
    //Panel Datenprovider
    JPanel databrokerTab() {
        JPanel result=new JPanel();

        result.setLayout (new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.insets = new Insets (0, 5, 0, 5);
        c.ipadx = 10;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;

        JLabel label = new JLabel ("Datenanbieter");
        c.gridx = 0;
        c.gridy = 0;
        result.add (label, c);         

        JTable table = new JTable (model.getTmodel());
        JScrollPane scrollingTable = new JScrollPane (table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPopupMenu tableContextMenu = new  JPopupMenu();
        JMenuItem brokerModify = new JMenuItem("Eintrag ändern");
        JMenuItem brokerDelete = new JMenuItem("Eintrag löschen");
        tableContextMenu.add(brokerModify);
        tableContextMenu.add(brokerDelete);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

        brokerModify.addActionListener(new FramePreferencesListener(table,sas, model.getTmodel(), this, brokerAddFrame));
        brokerDelete.addActionListener(new FramePreferencesListener(table, sas, model.getTmodel(), this, brokerAddFrame));
        table.setComponentPopupMenu(tableContextMenu);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth= 3;
        c.fill=GridBagConstraints.BOTH;
        c.gridheight=8;
        c.weighty=1.0;
        scrollingTable.setMaximumSize(new Dimension(getMaximumSize().width, 300));
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        result.add(scrollingTable, c); 
        JLabel labelBrokerTip = new JLabel("Benutzter Anbieter");
        c.gridx = 0;
        c.gridy = 12;
        c.gridwidth=1;
        c.fill=GridBagConstraints.NONE;

        result.add(labelBrokerTip,c);

        brokerList = new JComboBox<String>();
        brokerList.setModel(new DefaultComboBoxModel<String>(model.getTmodel().getBrokerNames()));
        brokerList.setSelectedIndex(model.getCBrokerIndex());
        brokerList.addActionListener(new ActionListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setCBrokerName( ( (JComboBox<String>) e.getSource()).getSelectedIndex());
            }});
        c.gridx = 1;
        c.gridy = 12;
        result.add(brokerList,c);

        FramePreferences locFrame = this;
        JButton addButton = new JButton("Anbieter hinzufügen");
        addButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                brokerAddFrame= new FrameDatabrokerAdd(locFrame, sas, null);
                }}) ;
        c.gridx = 2;
        c.gridy = 12;
        c.gridwidth=1;
        c.fill=GridBagConstraints.NONE;

        result.add(addButton,c );
        return result;
    }

    /**
     * Ein Datenprovidert kann entfernt werden.
     *
     * @param broker Der Name des Providers
     */
    
    public void remBroker(String broker) {
        //Wird der aktuelle Provider entfernt, ersetzt ihn der erste Listeneintrag
        if (broker.compareTo(model.getCBrokerName())==0) {
            model.setCBrokerName(0);
            brokerList.setSelectedIndex(0);
        }
        model.removeBroker(broker);
        brokerList.removeItem(broker);
        brokerAddFrame=null;
    }
    
    /**
     * Ein Provider kann ergänzt werden .
     *
     * @param broker Ein Array mit Name, Pull-URL, Push-URL, API-Key
     */
    
    public void addBroker(String[] broker) {
        model.addBroker(broker);
        brokerList.addItem(broker[0]);
        brokerAddFrame=null;
    }

    /**
     * Ein Provider kann ersetzt werden
     *
     * @param formerBroker der alte Provider
     * @param broker Ein Array mit Name, Pull-URL, Push-URL, API-Key
     */
    
    public void replaceBroker(String[] broker, String formerBroker) {
        model.removeBroker(formerBroker);
        model.addBroker(broker);
        brokerList.addItem(broker[0]);
        brokerList.removeItem(formerBroker);
        //Wird der aktuelle Provider ersetzt, ersetzt ihn der erste Listeneintrag
        if (model.getCBrokerName().compareTo(formerBroker)==0) {
            model.setCBrokerName(broker[0]);
            brokerList.setSelectedIndex(brokerList.getItemCount()-1);
        }
        brokerAddFrame=null;
    }}
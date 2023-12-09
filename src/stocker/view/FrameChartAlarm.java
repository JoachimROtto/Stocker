package stocker.view;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import stocker.model.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 
 * Die Klasse <code>FrameChartAlarm</code>bildet den Erfassungsdialog 
 * für Alarmeinstellungen ab
 * 
 * @author Joachim Otto
 */

public class FrameChartAlarm extends JDialog {
    JDesktopPane desktop;
    StockerAppSettings sas ;

    /**
     * Die Klasse wird initialisiert mit  
     * @param sas Den Programmeinstellungen
     * @param frame Dem zugeordneten Chart
     * 
     */ 

    public FrameChartAlarm(StockerAppSettings sas, FrameChart frame){
        super (sas.getFrame(), "Alarme verwalten", Dialog.ModalityType.APPLICATION_MODAL);
        this.desktop=sas.getDesktop();
        this.sas=sas;

        //reines Layout

        FrameChartAlarmModel model = new FrameChartAlarmModel(frame.model.getAlarme());
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
        JLabel label = new JLabel("Neuer Alarm");          
        c.gridx = 0;
        c.gridy = 0;
        result.add(label,c);

        JTextField dblAlarm=new JTextField(10);
       dblAlarm.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField source =((JTextField) e.getSource());
                checkedDouble(source.getText(), source);
            }});
        JButton setAlarmButton = new JButton("Alarm hinzufügen");
        setAlarmButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                model.addValue(Double.valueOf(dblAlarm.getText()));
                dblAlarm.setText("");
            }}) ;
        c.gridx = 0;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.gridy = 1;
        result.add(dblAlarm,c);
        c.gridx = 1;
        c.gridy = 1;
        result.add(setAlarmButton,c);

        label = new JLabel("Gesetzte Alarme");
        c.gridx = 0;
        c.gridy = 2;
        result.add(label,c);

        JTable table = new JTable (model);
        JScrollPane scrollingTable = new JScrollPane (table,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        table.setColumnSelectionAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Der TableRowSorter wird die Daten des models sortieren
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>();
        table.setRowSorter( sorter );
        sorter.setModel( model );

        JPopupMenu tableContextMenu = new  JPopupMenu();
        JMenuItem removeAlarm = new JMenuItem("Alarm entfernen");
        tableContextMenu.add(removeAlarm);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);


        removeAlarm.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                if (table.getSelectedRow() == -1) {
                    JOptionPane.showConfirmDialog(null, "Kein Alarm ausgewählt.", "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
                }
                else {
                    double val =(double) model.getValueAt(table.getSelectedRow(), 0);
                    model.removeValue(val);
                }
            }}) ;

        table.setComponentPopupMenu(tableContextMenu);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth= 2;
        c.gridheight=2;
        c.fill=GridBagConstraints.BOTH;
        c.weighty=1.0;
        result.add (scrollingTable,c);

        JButton acceptButton = new JButton("Anwenden");
        acceptButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                frame.receiveAlarmSettings(model.getValues());
                sas.setSizeAlarmFrame(getSize());
                dispose();    }}) ;

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth=1;
        c.weighty=0.0;
        result.add(acceptButton,c );

        JButton exitButton = new JButton("Verwerfen");
        exitButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                sas.setSizeAlarmFrame(getSize());
                dispose();    }}) ;

        result.add(exitButton,c );
        c.gridx = 1;
        c.gridy = 5;
        c.gridwidth=1;
        c.weighty=0.0;

        result.add(exitButton,c );
        add(result);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e ) {
                sas.setSizeAlarmFrame(getSize());
                dispose();
            }
        });
        setSize(sas.getSizeAlarmFrame());
        setVisible(true);
        requestFocus();
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
        }}}
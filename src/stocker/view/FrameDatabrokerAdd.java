package stocker.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import stocker.model.StockerAppSettings;

/**
 * 
 * Die Klasse <code>FrameDatabrokerAdd</code>bildet das Model zum Erfassungsdialog 
 * für Datenprovider ab
 * 
 * @author Joachim Otto
 */


public class FrameDatabrokerAdd  extends JDialog{
    boolean replaceValues=false;
    String formerBrokerName;
    JTextField fieldBroker;
    JTextField fieldPush;
    JTextField fieldPull;
    JTextField fieldAPI;
    StockerAppSettings sas ;

    /**
     * Das Model wird initialisiert mit  
     * @param annFrame Dem zugeordneten Dialog Voreinstellungen
     * @param sas Den Programmeinstellungen
     * @param entry Der Vorbelegung falls bearbeitet wird, sonst null
     * 
     */ 

    public FrameDatabrokerAdd(FramePreferences annFrame,  StockerAppSettings sas, String[] entry) {
        super (sas.getFrame(), "Angaben Datenlieferant", Dialog.ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout (new GridBagLayout());

        //Erstmal reines Layout

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.insets = new Insets (0, 5, 0, 5);
        c.ipadx = 20;

        JLabel label = new JLabel ("Dienstname");
        c.gridx = 0;
        c.gridy = 0;
        add (label, c);         

        fieldBroker = new JTextField ();
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1.0;
        add (fieldBroker, c);      

        label = new JLabel ("Pull-URL");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        add (label, c);

        fieldPull = new JTextField ();
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1.0;
        add (fieldPull, c);     

        label = new JLabel ("Push-URL");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        add (label, c);

        fieldPush = new JTextField ();
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weightx = 1.0;
        add (fieldPush, c);      

        label = new JLabel ("API-Key");
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        add (label, c);

        fieldAPI = new JTextField ();
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weightx = 1.0;
        add (fieldAPI, c);      

        //plausibilisieren aber wie? ausprobieren hiesse immer scheitern ohne netz
        JButton addButton = new JButton ("Erfassen");
        addButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                String[] locArg={
                        fieldBroker.getText(), fieldPull.getText(),
                        fieldPush.getText(), fieldAPI.getText()
                };
                if (replaceValues) {
                    annFrame.replaceBroker(locArg, formerBrokerName);
                }
                else {
                    annFrame.addBroker(locArg);
                }
                annFrame.moveToFront();
                annFrame.brokerAddFrame=null;
                dispose();    }}) ;
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        add (addButton, c); 

        JButton cancelButton = new JButton ("Abbrechen");
        cancelButton.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                annFrame.brokerAddFrame=null;
                dispose();    }}) ;
        c.gridx = 1;
        c.gridy = 4;
        add(cancelButton, c); 
        setSize (350, 200);

        //Wenn es Vorgaben gibt, dann werden sie hier eingetragen

        if (entry!=null) {
            switchToEdit(entry);
        }
        pack();    
        setVisible (true);
        requestFocus();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e ) {
                annFrame.brokerAddFrame=null;
                dispose();
            }
        });    
    }

    void switchToEdit(String[] entry) {
        replaceValues=true;
        formerBrokerName=entry[0];
        fieldBroker.setText(entry[0]);
        fieldPull.setText(entry[1]);
        fieldPush.setText(entry[2]);
        fieldAPI.setText(entry[3]);        
    }
}
package stocker.controller;

import java.awt.event.*;

import javax.swing.*;

import stocker.view.*;
import stocker.model.FramePreferencesTableModel;
import stocker.model.StockerAppSettings;

/**
 * 
 * Der <code>FramePreferencesListener</code> lauscht auf Aktionen im Dialog Voreinstellungen
 * 
 * @author Joachim Otto
 */

public class FramePreferencesListener extends KeyAdapter implements ActionListener {

    JTable assignedTable;
    StockerAppSettings sas;
    FramePreferencesTableModel model;
    FrameDatabrokerAdd brokerAddFrame;
    FramePreferences annFrame;


    /**
     * Der FramePreferencesListener wird initialisiert mit
     * @param Source Der Tabelle Datenprovider
     * @param sas Der Programmumgebung
     * @param model Dem Model der Tabelle Datenprovider
     * @param annFrame Dem Frame Voreinstellungen
     * @param brokerAddFrame Dem Frame zur Bearbeitung der Datenprovider
     */
    public FramePreferencesListener(JTable Source,StockerAppSettings sas,  FramePreferencesTableModel model, 
            FramePreferences annFrame, FrameDatabrokerAdd brokerAddFrame) {
        assignedTable=Source;
        this.model=model;
        this.annFrame=annFrame;
        this.sas=sas;
        this.brokerAddFrame=brokerAddFrame;
    }
    public FramePreferencesListener() { }

    /**
     * Der MovingAverage wird initialisiert mit
     * @param sas Den Programmeinstellungen
     */

    public FramePreferencesListener( StockerAppSettings sas) {
    }

    /**
     * Ereignisse auf der Tabelle wurden ausgelöst
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        int rowSelected= assignedTable.getSelectedRow();
        if (rowSelected == -1) {
            JOptionPane.showConfirmDialog(null, "Keine Aktion ausgewählt.", "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
        }
        else {
            if (e.getSource().toString().contains("löschen")) {
                annFrame.remBroker((String) assignedTable.getValueAt(rowSelected, 0));
            }
            else { //Eintrag ändern
                String[] entry = {(String) assignedTable.getValueAt(rowSelected, 0),
                        (String) assignedTable.getValueAt(rowSelected, 1),
                        (String) assignedTable.getValueAt(rowSelected, 2),
                        (String) assignedTable.getValueAt(rowSelected, 3)};
                if (brokerAddFrame==null) {
                    brokerAddFrame=new FrameDatabrokerAdd(annFrame, sas, entry);
                }}}}}

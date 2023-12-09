package stocker.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import stocker.model.PreferencesModel;
/**
 * 
 * Der Listener <code>FramePreferencesTextListener</code> lauscht auf die Änderungen der 
 * Textfelder Mindestgröße in den Voreinstellungen und leitet sie nach Überprüfung weiter
 *
 * @author Joachim Otto
 */

public class FramePreferencesTextListener  extends FocusAdapter{
    int intField;
    int intDir;
    JTextField sourceField;
    JTextField attField; 
    PreferencesModel model;

    /**
     * Der Listener wird initialisert mit der 
     * @param intField Ziel des Feldes
     * 1=Chart, 2=Suche, 3=Watchlist, 4=Voreinstellungen 
     * @param intDir die Richung, 1=Höhe, 2=Breite 
     * @param sourceField das geänderte Feld
     * @param attField das korrespondierende Feld
     * @param model das Model der Voreinstellungen 
     */
    public  FramePreferencesTextListener(int intField, int intDir, JTextField sourceField, JTextField attField, 
            PreferencesModel model ) {
        this.intField=intField;
        this.intDir=intDir;
        this.sourceField= sourceField;
        this.attField=attField;
        this.model=model;
    }


    /**
     * Ein Ereignis wurde ausgelöst
     */
    @Override
    public void focusLost(FocusEvent e){
        JTextField source =((JTextField) e.getSource());
        source.setBackground(Color.WHITE);
       int size = checkedInt(source.getText(), source, false);
       if (size==0) {return;}
       int attSize = checkedInt(attField.getText(), source, true);
        if (attSize==0) {return;}
        Dimension result;
        if (intDir==1) {
            result = new Dimension(attSize, size);               
        }
        else {
            result = new Dimension(size, attSize);        
        }
        switch (intField) {
        case 1: //Chart
            model.setMinChartSize(result);
            break;
        case 2: //Search
            model.setMinSearchSize(result);
            break;
        case 3://Watchlist
            model.setMinWLSize(result);
            break;
        case 4:    //Preferrences
            model.setMinPrefSize(result);
            break;
        }
    }

    int checkedInt(String input, JTextField source, boolean supressWarning) {
        int result=0;
        try {
            result=Integer.valueOf(input);
        }
        catch (Exception e) {
            if (!supressWarning) {
                JOptionPane.showConfirmDialog(null, "Bitte eine Zahl angeben.", "Achtung",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);                  
//                  source.setBackground(Color.RED);
                source.setText("0");
            }
            source.requestFocus();
            return result;
        }
        return result;
    }}


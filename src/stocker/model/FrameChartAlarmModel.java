package stocker.model;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 * 
 * Die Klasse <code>FrameChartAlarmModel</code>bildet das Model zum Erfassungsdialog 
 * für Alarmeinstellungen ab
 * 
 * @author Joachim Otto
 */

public class FrameChartAlarmModel  extends AbstractTableModel {

    String[] columnNames = { "Betrag"}; 
    Double[] alarms ;
    
    /**
     * Das Model wird initialisiert mit  
     * @param alarms Den bisherigen Alarmen
     * 
     */ 

    public FrameChartAlarmModel(Double[] alarms) {
        this.alarms=alarms;
    }
    
    /**
     * Liefert eine Zeilenüberschrift der Tabelle
     * @return Die Überschrift
     */
    
     @Override
    public String getColumnName (int col) 
    {
        return columnNames [col];
    }
     
    /**
     * Liefert die Spaltenzahl
     * @return Die Spaltenzahl
     */

    @Override
    public int getColumnCount() 
    {
        return columnNames.length;
    }

    /**
     * Liefert die Zeilenzahl
     * @return Die Zeilenzahl
     */

    @Override
    public int getRowCount() 
    {
        return (alarms!=null ? alarms.length : 0);
    }

    /**
     * Liefert die aktuellen Alarme
     * @return Die Alarme
     */
    
    public Double[] getValues() {
        return alarms;
    }

    /**
     * Liefert eine bestimmte Zelle zurück
     * @param row Die Zeile
     * @param column Die Spalte
     * @return Der entsprechende Eintrag
     */    

    @Override    
    public Object getValueAt (int row, int column) 
    {
        return alarms [row];
    }

    /**
     * Fügt einen neuen Alarm hinzu
     * @param alarm Der neue Alarm
     */

    public void addValue(Double alarm) {
        Double[] tmpAlarm;
        if (alarms!=null) {
            if (indexInAlarmlist(alarm)!=-1) {
                JOptionPane.showConfirmDialog(null, "Alarm bereits vorhanden!", "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            tmpAlarm = new Double[alarms.length+1];
            int i=0;
            for (Double locAlarm: alarms) {
                tmpAlarm[i]=locAlarm;
                i++;
            }
            tmpAlarm[i]=alarm;
        }
        else {
            tmpAlarm=new Double [1];
            tmpAlarm[0]=alarm;
        }
        replaceValues(tmpAlarm);
    }

    /**
     * Tauscht die Alarme aus
     * @param newData die neuen Alarme
     */

    public void replaceValues(Double[] newData) {
        alarms = newData;
        fireTableDataChanged();  
    }

    /**
     * Entfernt einen Alarm
     * @param alarm Der zu entfernende Alarm
     */

    public void removeValue(Double alarm) {
        Double[] tmpAlarm= new Double[alarms.length-1];
        int i=0;
        for (Double locAlarm: alarms) {           
            if (locAlarm.compareTo(alarm) != 0) {
                tmpAlarm[i]=locAlarm;               
                i++;
            }
        }
        replaceValues(tmpAlarm);
    }

    int indexInAlarmlist(Double data) {
        int i=0;
        for (Double tmpAlarm: alarms) {           
            if (data.compareTo(tmpAlarm)==0) {
                return i;}
            i++;}
        return -1;
    }
}

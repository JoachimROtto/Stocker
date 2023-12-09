package stocker.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import stocker.model.databroker.DataBrokerPush;

/**
 * 
 * Die Klasse <code>FramePreferencesTableModel</code>bildet das Model zur Tabelle 
 * des Bereichs Datenprovider in den Voreinstellungen ab
 * 
 * @author Joachim Otto
 */

public class FramePreferencesTableModel  extends AbstractTableModel {

    StockerPreferences preferences;
    String[] columnNames = { "Anbieter", "PullURL", "PushURL", "APIKey" }; 
    List<String[]> databroker;
    DataBrokerPush localBroker;

    /**
     * Das Model wird initialisiert mit  
     * @param preferences Den Voreinstellungen
     */ 

    public FramePreferencesTableModel(StockerPreferences preferences) {
        super();
        this.preferences=preferences;
        databroker=preferences.databroker;
    }
    /**
     * Liefert die Zeilenüberschriften der Tabelle
     * @return die Überschriften
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
        return (databroker!=null ? databroker.size() : 0);
    }

    /**
     * Liefert die aktuellen Alarme
     * @return Die Alarme
     */

    @Override
    public Object getValueAt (int row, int column) 
    {
        return databroker.get(row) [column];
    }

    /**
     * Liefert die Zeilenüberschriften der Tabelle
     * @return Die Überschriften
     */

    public String[] getColumnTitle() {
        return columnNames;
    }
    /**
     * Fügt einen neuen Provider hinzu
     * @param broker Name des neuen Provider
     * @param PushURL Die Push-URL
     * @param PullURL Die Pull-URL
     * @param APIKey Der API-Key
     */

    public void addValue(String broker, String PushURL, String PullURL, String APIKey) {
        String[] Value = {broker, PushURL, PullURL, APIKey};
        addValues (Value);
    }

    /**
     * Ersetzt die Provider
     * @param newData Die neuen Provider: Liste mit Name, Pull-URL, Push-URL, API-Key
     */
    void replaceValues(List<String[]>newData) {
        databroker = newData;
        fireTableDataChanged();  
    }

    /**
     * Fügt einen neuen Provider hinzu
     * @param newData Der neue Provider: Name, Pull-URL, Push-URL, API-Key
     */

    public void addValues(String[] newData) {
        databroker.add(newData);
        fireTableDataChanged();
    }

    /**
     * Entfernt einen Provider
     * @param brokerName Der Name des Providers
     */

    public void removeValue(String brokerName) {
        String[] tmpBroker=new String[0];
        for (String [] broker: databroker) {           
            if (broker[0].compareTo(brokerName) == 0) {
                tmpBroker=broker;
            }
        }
        databroker.remove(tmpBroker);
        fireTableDataChanged();
    }

    /**
     * Liefert die Namen aller Provider
     * @return die Namen
     */

    public String[] getBrokerNames() {
        String []result = new String [databroker.size()];   
        int i=0;
        for (String[] broker : databroker) {
            result[i] = broker[0];
            i++;
        }
        return result;
    }
    
    /**
     * Liefert die Einträge aller Provider
     * @return die Einträge
     */
    
    public List<String[]> getDatabroker(){
        return databroker;
    }

    /**
     * Liefert die Zeilennummer des Providers
     * @param brokerName Der Name des Providers
     * @return die Zeilennummer
     */

    public int indexInBrokerlist(String brokerName) {
        int i=0;
        for (String [] broker: preferences.getDatabroker()) {           
            if (broker[0].compareTo(brokerName)==0) {
                return i;}
            i++;}
        return -1;
    }

}

package stocker.model;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
import stocker.PrefChangeListeners;

/**
 * 
 * Die Klasse <code>PreferencesModel</code>bildet das Model 
 * zum Dialog Voreinstellungen ab. Änderungen finden zu Beginn nur im 
 * Modell statt und werden insgesamt gespeichert.
 * 
 * @author Joachim Otto
 */

public class PreferencesModel {

    StockerPreferences preferences;
    FramePreferencesTableModel tmodel;

    Dimension sizeChart;
    Dimension sizeSearch;
    Dimension sizeWL;
    Dimension sizePref;
    int SearchToInd;
    String defaultChartTyp;
    String defaultChartInterval;
    String defaultChartIntervalLabel;
    Color alarmColor;
    Color indikatorColor;
    Color bbColor;
    String cBrokerName;    
    StockerAppSettings sas;
    boolean databrokerChanged=false;

    /**
     * Das Model wird initialisiert mit  
     * @param sas Den Programmeinstellungen
     * 
     */ 

    public PreferencesModel(StockerAppSettings sas) {
        super();
        this.preferences=sas.getPrefer();
        this.sas=sas;
        tmodel = new FramePreferencesTableModel(preferences);
    }

    /**
     * Die geänderten Voreinstellungen werden in die Preferences 
     * zurückgeschrieben und in der entsprechenden Datei gespeichert
     */
    public void savePreferences() {
        preferences.setMinChartFrame(sizeChart);
        preferences.setMinSearchFrame(sizeSearch);
        preferences.setMinWatchlistFrame(sizeWL);
        preferences.setMinPreferencesFrame(sizePref);
        preferences.setSearchToInd(SearchToInd);
        preferences.setDefaultChartTyp(defaultChartTyp);
//??        preferences.setDefaultChartIntervalLabel(defaultChartIntervalLabel);=defaultChartInterval;
        preferences.setAlarmColor(alarmColor);
        preferences.setIndikatorColor(indikatorColor);
//        preferences.bbColor=bbColor;
        databrokerChanged=(preferences.getcBrokerName().compareTo(cBrokerName)!=0);
        preferences.setcBrokerName(cBrokerName);
        preferences.setDefaultChartIntervalLabel(defaultChartIntervalLabel);
        preferences.setDefaultChartInterval(defaultChartInterval);
        preferences.setPrefToJSONFile();
        firePrefChanges();

    }

    void firePrefChanges() {
        for (JInternalFrame f : sas.getFrame().getWindowList()) {
            if (f instanceof PrefChangeListeners) {
                ((PrefChangeListeners) f).notifyPrefChanged(databrokerChanged);   
            }}
        sas.getSCC().notifyPrefChanged(databrokerChanged);
        databrokerChanged=false;
    }

    /**
     * Das Model wird mit den Voreinstellungen überschrieben
     * 
     */

    public void setPreferences() {

        sizeChart=preferences.getMinChartFrame();
        sizeSearch= preferences.getMinSearchFrame();
        sizeWL=preferences.getMinWatchlistFrame();
        sizePref=preferences.getMinPreferencesFrame();
        SearchToInd=preferences.getSearchToInd();
        defaultChartTyp=preferences.getDefaultChartTyp();
        defaultChartInterval= preferences.getDefaultChartIntervalLabel();
        alarmColor= preferences.getAlarmColor();
        indikatorColor=preferences.getIndikatorColor();
//        bbColor = preferences.bbColor;
        defaultChartIntervalLabel=preferences.getDefaultChartIntervalLabel();
        cBrokerName=preferences.getcBrokerName();
        defaultChartInterval=preferences.getDefaultChartInterval();
    }

    /**
     * Liefert die aktuellen, unveränderten Voreinstellungen
     * @return Die Voreinstellungen
     */

    public StockerPreferences getPreferences() {
        return preferences;
    }

    /*
     * 
     * MinSize Panel
     * 
     * 
     */

    /**
     * Liefert Mindestgrösse für Charts
     * @return Die Mindestgrösse
     */

    public Dimension getMinChartSize() {
        return sizeChart;
    }

    /**
     * Ändert die Mindestgrösse für Charts
     * @param dim die neue Mindestgrösse
     */

    public void setMinChartSize(Dimension dim) {
        sizeChart=dim;
    }

    /**
     * Liefert Mindestgrösse für die Suche
     * @return Die Mindestgrösse
     */

    public Dimension getMinSearchSize() {
        return sizeSearch;
    }

    /**
     * Ändert die Mindestgrösse für die Suche
     * @param dim die neue Mindestgrösse
     */

    public void setMinSearchSize(Dimension dim) {
        sizeSearch=dim;
    }

    /**
     * Liefert Mindestgrösse für die Watchlist
     * @return Die Mindestgrösse
     */

    public Dimension getMinWLSize() {
        return sizeWL;
    }

    /**
     * Ändert die Mindestgrösse für die Watchlist
     * @param dim die neue Mindestgrösse
     */

    public void setMinWLSize(Dimension dim) {
        sizeWL=dim;
    }

    /**
     * Liefert Mindestgrösse für die Voreinstellungen
     * @return Die Mindestgrösse
     */

    public Dimension getMinPrefSize() {
        return sizePref;
    }

    /**
     * Ändert die Mindestgrösse für die Voreinstellungen
     * @param dim die neue Mindestgrösse
     */

    public void setMinPrefSize(Dimension dim) {
        sizePref=dim;
    }


    /*
     * 
     * Optionen Panel
     * 
     * 
     */

    /**
     * Ändert das Ziel des Doppelklicks im Suchdialog
     * @param index Das neue Ziel: 0=Watchlist, 1=Chart
     */

    public void setSearchTo(int index) {
        SearchToInd=index;
    }

    /**
     * Liefert die möglichen Ziele für einen Doppelklick im Suchdialog
     * @return Die möglichen Ziele
     */
    public String[] getSearchTo() {
        return preferences.getSearchToMode();
    }

    /**
     * Liefert das aktuelle Ziel für einen Doppelklick im Suchdialog
     * @return Das neue Ziel: 0=Watchlist, 1=Chart
     */
    public int getSearchtToInd() {
        return SearchToInd;
    }

    /**
     * Liefert die Darstellungsvarianten für einen Chart
     * @return Die Darstellungsvarianten
     */

    //read-only
    public String[] getChartModes() {
        return  preferences.getChartModes();
    }

    /**
     * Liefert den Index der aktuellen Standardchartdarstellung 
     * innerhalb der möglichen Chartdarstellungen
     * @return Der Index
     */

    public int getCChartModeIndex() {
        int i=0;
        for(String mode: getChartModes()) {
            if (mode.compareTo(getDefaultChartType())==0) {
                return i;
            }
            i++;
        }
        return -1;

    }

    /**
     * Gibt die Bezeichnungen für die aktuell möglichen Chartintervalle zurück
     * @return Die Bezeichnungen
     */

    public String[] getChartIntervalLabels() {
        return  preferences.getChartIntervalLabels();
    }

    /**
     * Liefert den Index der aktuellen Standardchartintervalls 
     * innerhalb der möglichen Chartintervalle
     * @return Der Index
     */

    public int getCChartIntervalIndex() {
        int i=0;
        for(String mode: getChartIntervalLabels()) {
            if (mode.compareTo(getDefaultChartIntervalLabel())==0) {
                return i;
            }
            i++;
        }
        return -1;  
    }

    /**
     * Liefert den aktuellen Standardcharttyp
     * @return Der aktuelle Standardcharttyp
     */

    public String getDefaultChartType() {
        return defaultChartTyp;
    }

    /**
     * Setzt den aktuellen Standardcharttyp
     * @param dChartType Der neue Standardcharttyp
     */
    public void setDefaultChartType(String dChartType) {
        defaultChartTyp=dChartType;
    }

    /**
     * Liefert das aktuelle Standardchartintervall
     * @return Das aktuelle Standardchartintervall
     */

    public String getDefaultChartInterval() {
        return defaultChartInterval;
    }

    /**
     * Liefert die Bezeichnung des aktuellen Standardchartintervall 
     * @return Die Bezeichnung
     */

    public String getDefaultChartIntervalLabel() {
        return defaultChartIntervalLabel;
    }

    /**
     * Setzt das aktuelle Chartintervall als Index in den Intervallarten
     * @param index Der Index
     */

    public void setDefaultChartInterval(int index) {
        defaultChartIntervalLabel=preferences.getChartIntervalLabels()[index];
        defaultChartInterval=preferences.getChartIntervals()[index];
    }

    /**
     * Liefert die Farbe für die Alarmlinien im Chart
     * @return Die Farbe
     */

    public Color getCAlarmColor() {
        return alarmColor;
    }

    /**
     * Ändert die Farbvorbelegung für Bollinger Bänder im Chart
     * @param newColor Die neue Farbe 
     */

    public void setBBColor(Color newColor) {
        bbColor=newColor;
    }


    /**
     * Liefert die Farbe für die Bollinger Bänder im Chart
     * @return Die Farbe
     */

    public Color getBBColor() {
        return bbColor;
    }

    /**
     * Ändert die Farbvorbelegung für den Alarm im Chart
     * @param newAColor Die neue Farbe 
     */

    public void setCAlarmColor(Color newAColor) {
        alarmColor=newAColor;
    }


    /**
     * Liefert die Farbe für den gleitenden Durchschnitt im Chart
     * @return Die Farbe
     */


    public Color getCIndiColor() {
        return indikatorColor;
    }

    /**
     * Ändert die Farbvorbelegung für den gleitenden Durchsschnitt im Chart
     * @param newIColor Die neue Farbe 
     */

    public void setCIndiColor(Color newIColor) {
        indikatorColor=newIColor;
    }


    /*
     * 
     * BrokerPanel
     * 
     */


    /**
     * Ändert den aktuellen Datenprovider mit Index in der Liste der Provider
     * @param brokerIndex Der Index
     */

    public void setCBrokerName(int brokerIndex) {
        cBrokerName=tmodel.getBrokerNames()[brokerIndex];
    }

    /**
     * Liefert den Index des aktuellen Provider in der Liste der Provider
     * @return Der Index
     */

    public int getCBrokerIndex() {
        return tmodel.indexInBrokerlist(cBrokerName);
    }

    /**
     * Löscht einen Provider aus dem Modell  
     * @param broker Der Name des Brokers
     */

    public void removeBroker(String broker) {
        tmodel.removeValue(broker);
    }

    /**
     * Fügt einen Broker hinzu
     * @param broker Der Datensatz des Brokers: Name, Pull-URL, Push-URL, APIKey
     */

    public void addBroker(String [] broker) {
        tmodel.addValues(broker);
    }

    /**
     * Ändert den vorbelegten Provider
     * @param brokerName Der neue Provider 
     */

    public void setCBrokerName(String brokerName) {
        cBrokerName=brokerName;
    }

    /**
     * Liefert den Namen des aktuellen Datenproviders
     * @return Der Datenprovider
     */

    public String getCBrokerName() {
        return cBrokerName;
    }

    /**
     * Liefert das Modell der Datenprovidertabelle
     * @return Das Modell
     */

    public FramePreferencesTableModel getTmodel() {
        return tmodel;
    }

    /**
     * Ändert das Modell der Datenprovidertabelle
     * @param tmodel Das neue Modell 
     */

    public void setTmodel(FramePreferencesTableModel tmodel) {
        this.tmodel = tmodel;
    }}
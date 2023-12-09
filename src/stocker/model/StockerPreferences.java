package stocker.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * 
 * Die Klasse <code>StockerPreferences</code> bildet die Voreinstellungen ab.
 * Diese werden nach Vorgabe gespeichert
 * 
 * @author Joachim Otto
 */

public class StockerPreferences{
    //Konstanten groß
    final String projectPrefix="stocker";
    final String filePath= projectPrefix + ".json";

    //URLS für den Datenzugriff
    List<String[]> databroker ;
    String cBrokerName;

    //Suchdialog: Übernahme in Watchlist oder Chart
    String[] SearchToMode=  {"Watchlist", "Chart öffnen"}; 
    int SearchToInd;
    /**
     * Kennung Übernahme in die Watchlist 
     */
    public final int SI_WATCHLIST=0;
    /**
     * Kennung Übernahme in ein Chart 
     */
    public int SI_CHART=1;
    /**
     * Kennung Charttyp Kerzendarstellung
     */
    public final int CHARTTYP_CANDLE=0;
    /**
     * Kennung Charttyp Liniendarstellung
     */
    public final int CHARTTYP_LINE=1;
    /**
     * Maximale Anzahl Watchlisteinträge 
     */
    public final int MAX_STOCK_WL=20;
    /**
     * Maximale Anzahl Aktien in Charts 
     */
    public final int MAX_STOCK_GRAPH=20;

    //Chart: Standardtyp und Intervall
    String[] chartModes;
    String defaultChartTyp;
    String[] chartIntervalLabels;
    String[] chartIntervals;
    String defaultChartIntervalLabel;
    String defaultChartInterval;
    transient Color alarmColor;
    transient Color indikatorColor;
    transient Color bbColor;
    long candleCount;
    int priceDecWL=4;
    int priceDecCScale=2;

    //Mindestgrössen Fenster
    transient Dimension minWatchlistFrame;
    int minWatchlistFrameX;
    int minWatchlistFrameY;
    transient Dimension minChartFrame;
    int minChartFrameX;
    int minChartFrameY;
    transient Dimension minPreferencesFrame;
    int minPreferencesFrameX;
    int minPreferencesFrameY;
    transient Dimension minSearchFrame;
    int minSearchFrameX;
    int minSearchFrameY;


    public StockerPreferences() {
        setSPDefaults(); 
    }    

    /** 
     * Der Voreinstellungen werden nach Vorgabe beim ersten Programmstart belegt
     */

    public void setSPDefaults() {
        //Datenbzugriff
        databroker = new ArrayList<String[]>();
        //Kein API-Key für finnhub, im gegensatz zur Matrikelnummer
        String[] dbEntry = {"Finnhub", "https://finnhub.io/api/v1", "wss://ws.finnhub.io/?token=",""};
        databroker.add(dbEntry);
        dbEntry= new String[]{"Generator", "http://localhost:8080", "ws://localhost:8090?token=", "3232190"};
        databroker.add(dbEntry);

        cBrokerName="Finnhub";

        SearchToInd=SI_WATCHLIST;

        defaultChartTyp= "Kerze";
        chartModes= new String[2];
        chartModes[0]="Kerze";
        chartModes[1]= "Linie";
        alarmColor=Color.RED;
        indikatorColor=Color.BLUE;
        bbColor=Color.CYAN;

        chartIntervalLabels= new String[]{"1 Minute", "5 Minuten", "15  Minuten", "30  Minuten", "60 Minuten",
                "1 Tag", "1 Woche", "1 Monat"};
        chartIntervals= new String[]{"1", "5", "15", "30", "60", "D", "W", "M"};
        defaultChartIntervalLabel="1 Tag";
        defaultChartInterval="D";
        setMinChartFrame(new Dimension(400,300));       
        setMinSearchFrame(new Dimension(400,300));
        setMinWatchlistFrame(new Dimension(400,300));       
        setMinPreferencesFrame(new Dimension(600,350));
        candleCount=50;

    }

    /**
     * Gibt die PullURL des aktuellen Datenproviders zurück
     * @return Die URL
     */

    public   String getPullURL() {
        return getCBroker()[1];
    }

    /**
     * Gibt den APIKey des aktuellen Datenproviders zurück
     * @return Der Key
     */

    public  String getAPIKey() {
        return getCBroker()[3];
    }

    /**
     * Gibt die PushURL des aktuellen Datenproviders zurück
     * @return Die URL
     */

    public   String getPushURL() {
        return getCBroker()[2];
    }

    /**
     * Gibt den Namen des aktuellen Datenproviders zurück
     * @return Der Name
     */

    public  String getCBrokerName() {
        return cBrokerName;
    }
    String[] getCBroker() {
        for (String[] dbEntry : databroker) {
            if (dbEntry[0].compareTo(cBrokerName)==0) {
                return dbEntry;}}
        JOptionPane.showConfirmDialog(null, "Keine Datenlieferanten festgelegt!", "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
        return databroker.get(0);
    }


    int indexInBrokerlist(String brokerName) {
        int i=0;
        for (String [] broker: databroker) {           
            if (broker[0].compareTo(brokerName)==0) {
                return i;}
            i++;}
        return -1;
    }

    /**
     * Gibt eine Liste der Werktage ohne Öffnung der New Yorker Börse zurück
     * @return Die Daten
     */

    public List<LocalDate> getNYSEHolydays2021(){
        List<LocalDate> holidays = new ArrayList<>();
        final int year=2021; 
        holidays.add(LocalDate.of(year, 1, 1));
        holidays.add(LocalDate.of(year, 1, 18));
        holidays.add(LocalDate.of(year, 2, 15));
        holidays.add(LocalDate.of(year, 4, 2));
        holidays.add(LocalDate.of(year, 7, 5));
        holidays.add(LocalDate.of(year, 9, 6));
        holidays.add(LocalDate.of(year, 11, 25));
        holidays.add(LocalDate.of(year, 11, 26));
        holidays.add(LocalDate.of(year, 12, 24));
        return holidays;
    }

    /**
     * Gibt den Speicherort der Voreinstellungen zurück
     * @return Der Speicherort
     */

    public String getFilePath() {
        return filePath;
    }

    /**
     * Liest die Voreinstellungen aus einer Datei und gibt sie als Voeinstellungen zurück 
     * @return Die Voreinstellungen
     */

    public  StockerPreferences getPrefFromJSONFile() {
        StockerPreferences result = new StockerPreferences();
        File prefFile = new File(result.getFilePath());
        try {
            Gson gson = new GsonBuilder().create();
            BufferedReader reader = new BufferedReader(new InputStreamReader( new FileInputStream(prefFile)));
            result =  gson.fromJson(reader.readLine(), StockerPreferences.class);
            reader.close();

        } catch (FileNotFoundException e) {
            JOptionPane.showConfirmDialog(null, "Einstellungen konnten nicht gelesen werden!", "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
            //Erstbelegung für finnhub
            result.databroker.remove(0);
            String APIKey=JOptionPane.showInputDialog(null, "finnhub.io benötigt noch einen API-Key!");
            String[] dbEntry = {"Finnhub", "https://finnhub.io/api/v1", "wss://ws.finnhub.io/?token=",((APIKey==null? "": APIKey))};
            result.databroker.add(dbEntry);
            setPrefToJSONFile();
        }
        catch (Exception e) {
            JOptionPane.showConfirmDialog(null, "Fehler\n" + e.getMessage(), "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
        }
        return result;
    }

    /** 
     * Schreibt die Voreinstellungen nach Vorgaben in eine Datei.
     */

    public void setPrefToJSONFile() {
        File prefFile = new File(getFilePath());
        Gson gson = new GsonBuilder().create();
        String JSONresult = gson.toJson(this);
        FileWriter writer;
        try {
            writer = new FileWriter(prefFile);
            writer.write(JSONresult);
            writer.flush();
            writer.close();
            JOptionPane.showConfirmDialog(null, "Einstellungen wurden gespeichert", "Einstellungen", 
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }}


    /*
     * Getter und Setter
     */


    /**
     * Gibt die Einträge der möglichen Datenprovider zurück
     * @return Die Namen
     */

    public List<String[]> getDatabroker() {
        return databroker;
    }

    /**
     * Setzt die Namen der Datenprovider
     * @param databroker Die Namen
     */

    public void setDatabroker(List<String[]> databroker) {
        this.databroker = databroker;
    }

    /**
     * Gibt den Namen des aktuellen Datenproviders zurück
     * @return Der Name
     */

    public String getcBrokerName() {
        return cBrokerName;
    }

    /**
     * Setzt den Namen des aktuellen Datenproviders
     * @param cBrokerName Der Name
     */

    public void setcBrokerName(String cBrokerName) {
        this.cBrokerName = cBrokerName;
    }

    /**
     * Gibt die möglichen Vorgabe, wohin Suchergebnisse übernommen werden, zurück 
     * @return Die möglichen Vorgaben
     */

    public String[] getSearchToMode() {
        return SearchToMode;
    }

    /**
     * Setzt die möglichen Vorgabe, wohin Suchergebnisse übernommen werden, zurück 
     * @param searchToMode Die möglichen Vorgaben
     */

    public void setSearchToMode(String[] searchToMode) {
        SearchToMode = searchToMode;
    }

    /**
     * Gibt die Vorgabe, wohin Suchergebnisse übernommen werden zurück 
     * @return die Vorgabe: SI_WATCHLIST oder SI_CHART
     */

    public int getSearchToInd() {
        return SearchToInd;
    }

    /**
     * Ändert die Vorgabe wohin eine Suche übernommen werden soll
     * @param searchToInd die Vorgabe: SI_WATCHLIST oder SI_CHART
     */

    void setSearchToInd(int searchToInd) {
        SearchToInd = searchToInd;
    }

    String[] getChartModes() {
        return chartModes;
    }

    void setChartModes(String[] chartModes) {
        this.chartModes = chartModes;
    }

    String getDefaultChartTyp() {
        return defaultChartTyp;
    }

    void setDefaultChartTyp(String defaultChartTyp) {
        this.defaultChartTyp = defaultChartTyp;
    }

    /**
     * Gibt die Bezeichnungen der Chartintervalle zurück
     * @return Die Charintervalle
     */

    public String[] getChartIntervalLabels() {
        return chartIntervalLabels;
    }

    void setChartIntervalLabels(String[] chartIntervalLabels) {
        this.chartIntervalLabels = chartIntervalLabels;
    }

    String[] getChartIntervals() {
        return chartIntervals;
    }

    void setChartIntervals(String[] chartIntervals) {
        this.chartIntervals = chartIntervals;
    }

    String getDefaultChartIntervalLabel() {
        return defaultChartIntervalLabel;
    }

    void setDefaultChartIntervalLabel(String defaultChartIntervalLabel) {
        this.defaultChartIntervalLabel = defaultChartIntervalLabel;
    }

    String getDefaultChartInterval() {
        return defaultChartInterval;
    }

    void setDefaultChartInterval(String defaultChartInterval) {
        this.defaultChartInterval = defaultChartInterval;
    }

    /**
     * Gibt die Farbvorgabe für Indikatoren zurück
     * @return Die Farbvorgabe
     */

    public Color getAlarmColor() {
        return alarmColor;
    }

    void setAlarmColor(Color alarmColor) {
        this.alarmColor = alarmColor;
    }

    /**
     * Gibt die Farbvorgabe für Indikatoren zurück
     * @return Die Farbvorgabe
     */

    public Color getIndikatorColor() {
        return indikatorColor;
    }

    /**
     * Ändert die Farbvorgabe für Indikatoren
     * @param indikatorColor die neue Farbe
     */

    public void setIndikatorColor(Color indikatorColor) {
        this.indikatorColor = indikatorColor;
    }

    Color getBbColor() {
        return bbColor;
    }

    void setBbColor(Color bbColor) {
        this.bbColor = bbColor;
    }

    long getCandleCount() {
        return candleCount;
    }

    /**
     * Gibt die Nachkommastellenzahl zum Runden in der Watchlist an
     * @return Die Anzahl Nachkommastellen
     */

    public int getPriceDecWL() {
        return priceDecWL;
    }

    /**
     * Gibt die Nachkommastellenzahl zum Runden in Charts an
     * @return Die Anzahl Nachkommastellen
     */

    public int getPriceDecCScale() {
        return priceDecCScale;
    }

    /**
     * Gibt die minimale Grösse der Watchlist zurück
     * @return Die Grösse
     */

    public Dimension getMinWatchlistFrame() {
        return new Dimension(minWatchlistFrameX, minWatchlistFrameY);
    }

    /** 
     * Ändert die minimale Grösse der Watchlist
     * @param minWatchlistFrame Die minimale Grösse
     */

    public void setMinWatchlistFrame(Dimension minWatchlistFrame) {
        minWatchlistFrameX = minWatchlistFrame.width;
        minWatchlistFrameY = minWatchlistFrame.height;
    }

    /**
     * Gibt die minimale Grösse des Charts zurück
     * @return Die Grösse
     */

    public Dimension getMinChartFrame() {
        return new Dimension(minChartFrameX, minChartFrameY);
    }

    /** 
     * Ändert die minimale Grösse des Charts
     * @param minChartFrame Die minimale Grösse
     */

    public void setMinChartFrame(Dimension minChartFrame) {
        minChartFrameX = minChartFrame.width;
        minChartFrameY = minChartFrame.height;
    }    

    /**
     * Gibt die minimale Grösse des Dialogs Voreinstellungen zurück
     * @return Die Grösse
     */

    public Dimension getMinPreferencesFrame() {
        return new Dimension(minPreferencesFrameX, minPreferencesFrameY);
    }

    /** 
     * Ändert die minimale Grösse des Suchfensters
     * @param minPreferencesFrame Die minimale Grösse
     */

    public void setMinPreferencesFrame(Dimension minPreferencesFrame) {
        minPreferencesFrameX = minPreferencesFrame.width;
        minPreferencesFrameY = minPreferencesFrame.height;
    }

    /**
     * Gibt die minimale Grösse des Suchfensters zurück
     * @return Die Grösse
     */

    public Dimension getMinSearchFrame() {
        return new Dimension(minSearchFrameX, minSearchFrameY);
    }

    /** 
     * Ändert die minimale Grösse des Suchfensters
     * @param minSearchFrame Die minimale Grösse
     */

    public void setMinSearchFrame(Dimension minSearchFrame) {
        minSearchFrameX = minSearchFrame.width;
        minSearchFrameY = minSearchFrame.height;
    }

    String getProjectPrefix() {
        return projectPrefix;
    }}
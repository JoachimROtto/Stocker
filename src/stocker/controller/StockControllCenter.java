package stocker.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import com.google.gson.JsonSyntaxException;
import stocker.PrefChangeListeners;
import stocker.model.*;
import stocker.model.databroker.*;
import stocker.view.*;

/**
 * 
 * Die Klasse <code>StockControllCenter</code> ist die Schnittstelle zwischen
 * Charts und Watchlist und dem Push-Dienst des Datenproviders. Es verwaltet
 * den Push-Dienst, hält die Verbindung offen und reicht Handelsdaten nach
 *  Überprüfung bezüglich Alarm/Indikator-Eintragungen 
 * 
 * @author Joachim Otto
 */

public class StockControllCenter  implements PrefChangeListeners, ActionListener{
    HashMap <String, SCCEntry> entries = new HashMap<String, SCCEntry>();
    DataBrokerPush linkToServer;
    StockerAppSettings sas;
    List<String> stockIDChecked=new ArrayList<String>();


    int maxChartSubscriptions;
    int chartSubscriptions=0;

    private static StockControllCenter instance;

    /**
     * Liefert eine Referenz auf das SCC-Objekt
     * @param sas Die Programmeinstellungen
     * @return Die Referenz
     */

    public static StockControllCenter getInstance (StockerAppSettings sas) {
        if (instance==null) {
            instance= new StockControllCenter(sas);
        }
        return instance;
    }

    StockControllCenter(StockerAppSettings sas){
        this.sas=sas;
        linkToServer = new DataBrokerPush(sas, this);        
        maxChartSubscriptions=sas.getPrefer().MAX_STOCK_GRAPH;
    }

    //der restore und die Alarmcheckung passiert nun in den sas, die Frames werden getrennt persistiert und gecheckt
    void restoreOnStartup() {
    }

    /**
     * Speichert letzte Kurse sowie Alarme zu Aktien die nur in der Watchlist stehen
     */

    public void saveBeforeLeave() {
        //Hier müssen alle Alarme gesichert werden, die nur noch von der Watchlist gehalten werden.
        int size=entries.size();
        SCCEntry item;
        String[] stocksSCC=new String [size];
        Double[][] alarmSCC=new Double [size][];
        //ChartIndicator[][] indicatorSCC=new ChartIndicator [size][];
        Double[] lastPrice=new Double [size];
        int i=0;
        for (String stockID: entries.keySet()) {
            item= entries.get(stockID);
            //Ist in der Watchlist aber nicht in einem Chart
            if (item.isInWatchlist && item.openCharts.isEmpty()) {
                stocksSCC[i]=stockID;
                alarmSCC[i]=getAlarmsAsArray(stockID);
                lastPrice[i]=item.lastPrize;
            }
        }
        sas.setSCCValues(stocksSCC, alarmSCC, lastPrice);
    }

    /**
     * Startet einen Timer, der versucht den Pushclient offen zu halten.
     */

    public void keepAlive() {
        if (!linkToServer.askIfConnected()) {
            Timer reconnectTimer = new Timer(2000, this);
            reconnectTimer.setRepeats(true);
            reconnectTimer.start();             
        }
    }

    /**
     * Funktion die der Timer zum Verbindungstest aufruft
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Versuche Verbindung herzustellen");
        Timer recoTimer = ((Timer) e.getSource());
        try {
            linkToServer.sendPing();
        }
        catch (Exception exc) {}
        if (linkToServer.askIfConnected()) {
            System.out.println("Verbindung hergestellt");
            recoTimer.stop();    }}

    /**
     * Wird aufgerufen wenn der Datenprovider gewechselt werden soll
     */

    public void dataBrokerSwitch() {
        linkToServer.close();
        linkToServer=new DataBrokerPush(sas, this);
        keepAlive();
        JOptionPane.showConfirmDialog(null,
                "Neuer Datenprovider:\nMöglicherweise werden bestimmte Kurse nicht mehr aktualisiert!", 
                "Nachricht vom Datenprovider!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
        //Und jetzt alle Aktien neu abonnieren ->dass der Provider neu ist checkt das PreferencesModel
        for (String stockID: entries.keySet()) {
            //Die Subscription reicht, denn Nachrichten werden den Listenern nur aufgrund stockID weitergereicht
            try {
                linkToServer.subscribeNewStock(stockID);
            } catch (DataBrokerException e) {
                JOptionPane.showConfirmDialog(null,
                        stockID + " Datentransfer gescheitert", 
                        "Nachricht vom Datenprovider!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
            }}}

    /**
     * Empfängt Handelsdaten vom Push-Dienst, ruft <code>checkForAlarm</code> auf und leitet weiter
     * @param trade Das Handelsdatum
     */

    //Nachrichten (Trades) auf Alarme / Indikatoren überprüfen und durchleiten
    public void receiveTrade(Trade trade) {
        SCCEntry tmpItem=entries.get(trade.getSymbol());
        if (tmpItem ==null) {return;}
        //Nachricht an Watchlist und offene Charts weitereichen
        if (tmpItem.isInWatchlist) {
            FrameWatchlist.getInstance(sas).getModel().receivePushNotificaton(trade);
        }
        if (tmpItem.openCharts.size()!=0) {
            for (FrameChartModel cModel : tmpItem.openCharts) {
                cModel.receivePushNotificaton(trade);
            }  
        }
        checkForAlarm(trade.getSymbol(), trade.getPrice());
    }


    /** 
     * Überprüft anhand des quotes ob Alarme/Indikatoren ausgelöst wurden. Anwendung: Neustart
     * @param stockID Die Aktienkennung
     */

    public void checkForAlarmOnStart(String stockID) {
        if (stockIDChecked.contains(stockID)) {
            return;
        }
        boolean failed=false;
        double opening=0;
        try {
            opening = new DataBrokerPull(sas).getOpening(stockID);
        } catch (Exception e) {
            failed=true;
        }
        if (!failed) {
            checkForAlarm(stockID, opening);
            stockIDChecked.add(stockID);
        }
    }

    /** 
     * Überprüft anhand eines neuen Preises ob Alarme/Indikatoren ausgelöst wurden
     * @param stockID Die Aktienkennung
     * @param price Der neue Preis
     */

    public void checkForAlarm(String stockID, double price){
        SCCEntry tmpItem=entries.get(stockID);
        //Alarm ermitteln auf Basis Alarm/Indicatoreneinträge
        boolean priceIsOfConcern=false;
        String message="";
        if (tmpItem.alarm.size()!=0) {
            for (Double alarm : tmpItem.alarm) {
                if (tmpItem.lastPrize>=alarm) {
                    priceIsOfConcern=(alarm>price);
                    message="Preis hat Alarm unterschritten";
                }
                else {
                    priceIsOfConcern=(alarm<price);
                    message="Preis hat Alarm überschritten";
                }
            }}
        if (tmpItem.indicator.size()!=0) {
            for (ChartIndicator tmpIndicator : tmpItem.indicator) { 
                if (tmpIndicator instanceof BollingerBand) {
                    BollingerBand indicator=(BollingerBand) tmpIndicator;
                    if (indicator.getCLowerBB()>price) {
                        priceIsOfConcern=true;
                        message="Preis hat das untere Bollinger Band unterschritten";
                    }
                    if (indicator.getCUpperBB()<price) {
                        priceIsOfConcern=true;
                        message="Preis hat das obere Bollinger Band überschritten";
                    }
                }
                else if(tmpIndicator instanceof MovingAverage) {
                    MovingAverage indicator = (MovingAverage) tmpIndicator;
                    if (tmpItem.lastPrize>=indicator.getLastItem()) {
                        priceIsOfConcern=(indicator.getLastItem()>price);
                        message="Preis hat Gleitenden Durchschnitt unterschritten";
                    }
                    else {
                        priceIsOfConcern=(indicator.getLastItem()<price);
                        message="Preis hat Gleitenden Durchschnitt überschritten";
                    }
                }
            }}
        if (priceIsOfConcern) {
            //Alarm nur melden wenn Aktie nicht schon gemeldet wurde
            if (!tmpItem.alarmIsOn) {
                JOptionPane.showConfirmDialog(null, message, "Aktie: " + price, 
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
            }
            //Alarmzustand wird betreten
            tmpItem.alarmIsOn=true;
        }
        tmpItem.lastPrize=price;
    }

    /**
     * Wird aufgerufen wenn die Voreinstellunge geändert wurden
     */

    @Override
    public void notifyPrefChanged(boolean databrokerChanged) {
        //Es wurde der Datenprovider gewechselt
        if ( databrokerChanged) {
            dataBrokerSwitch();
        }}

    //Subscriptions

    /**
     * Trägt die Watchlist in die Abonenntenliste ein.
     * @param stockID Die Aktienkennung
     * @throws DataBrokerException Bei Problemen mit dem Provider
     * @throws JsonSyntaxException Bei korrupten Dateien
     * @throws IOException Bei Netzproblemen
     */

    public void putSubscriptionFromWL (String stockID) throws DataBrokerException, JsonSyntaxException, IOException{
        putSubscription(stockID, true, null); 
    }

    /**
     * Trägt einen Chart in die Abonenntenliste ein.
     * @param stockID Die Aktienkennung
     * @param model das Modell des Charts
     * @throws DataBrokerException Bei Problemen mit dem Provider
     * @throws JsonSyntaxException Bei korrupten Dateien
     * @throws IOException Bei Netzproblemen
     */

    public void putSubscriptionFromC (String stockID, FrameChartModel model) throws DataBrokerException, JsonSyntaxException, IOException{
        putSubscription(stockID,false, model);
    }

    void putSubscription(String stockID, boolean isFromWL, FrameChartModel chart) throws DataBrokerException, JsonSyntaxException, IOException{
        if (!entries.containsKey(stockID)) {
            //Maximale Anzahl Kurse. Watchlisteinträge werden im dortigen Modell beschränkt 
            if (!isFromWL && chartSubscriptions>maxChartSubscriptions) {
                throw new DataBrokerException("Maximal Anzahl Kurse überschritten!");
            }
            //Gibt es diese Aktien überhaupt, oder sind sie vor einem Providerwechsel bei 
            //der letzten Programmnutzung eingetragen worden?
            DataBrokerPull localBrokerPull= new DataBrokerPull(sas);
            if (localBrokerPull.stockLookup(stockID)==null) {
                throw new DataBrokerException("Aktie nichtvorhanden: " + stockID);  
            }
            linkToServer.subscribeNewStock(stockID);
            entries.put(stockID, new SCCEntry(stockID));
            chartSubscriptions++;
        }
        SCCEntry tmpItem=entries.get(stockID);
        if (isFromWL) {
            tmpItem.isInWatchlist=true;
        }
        else {
            if (chart!=null) {
                tmpItem.openCharts.add(chart);
            }}}

    /**
     * Trägt die Watchlist aus der Abonnentenliste aus
     * @param stockID Die Kennung der Aktie
     */

    public void putCancelationFromWL(String stockID) {
        SCCEntry tmpItem=entries.get(stockID);
        tmpItem.isInWatchlist=false;
        if (tmpItem.openCharts.isEmpty()){
            linkToServer.unSubscribeStock(stockID);
            entries.remove(stockID);
        }
    }

    /**
     * Trägt den Chart aus der Abonnentenliste aus
     * @param StockID Die Kennung der Aktie
     * @param model Das dazugehörige Modell
     */

    public void putCancelationFromC(String StockID, FrameChartModel model) {
        SCCEntry tmpItem=entries.get(StockID);
        tmpItem.openCharts.remove(model);
        //Letzer Chart? Indikatoren löschen, Alarme bleiben für die Watchlist
        if (tmpItem.openCharts.isEmpty()) {
            tmpItem.indicator.clear();
        }
        //Auch nicht mehr in der Watchlist? Beobachtung löschen 
        if (!tmpItem.isInWatchlist &&  tmpItem.openCharts.isEmpty()) {
            linkToServer.unSubscribeStock(StockID);
            entries.remove(StockID);
        }
        chartSubscriptions--;
    }

    /**
     * Überprüft ober der aktuelle Datenprovider eine bestimmte Aktie listes
     * @param stockID Die Kennung der Aktie
     * @return Gelistet?
     * @throws DataBrokerException Bei Problemen mit dem Provider
     * @throws JsonSyntaxException Bei korrupten Dateien
     * @throws IOException Bei Netzproblemen
     */

    public boolean stockIsSupported(String stockID) throws JsonSyntaxException, IOException, DataBrokerException {
        DataBrokerPull linkToServerPull= new DataBrokerPull(sas);
        return (linkToServerPull.stockLookup(stockID)!=null);
    }

    //getter und setter Alarme/Indikatoren für die Charts

    /**
     * Erstellt einen Alarm
     * @param stockID Die Kennung der Aktie
     * @param alarm Der Alarm
     */

    public void putNewAlarm(String stockID, Double alarm) {
        if (!entries.containsKey(stockID)) {
            try {
                putSubscription(stockID, false, null);
            } catch (JsonSyntaxException | DataBrokerException | IOException e) {
                e.printStackTrace();
            }
        }
        if (!entries.get(stockID).alarm.contains(alarm)) {
            entries.get(stockID).alarm.add(alarm);
        }
    }

    /**
     * Löscht einen Alarm einer Aktie
     * @param stockID Die Kennung der Aktie
     * @param alarm Der Alarm
     */

    public void cancelAlarm(String stockID, Double alarm) {
        entries.get(stockID).alarm.remove(alarm);
    }

    /**
     * Löscht die Alarme einer Aktie
     * @param stockID Die Kennung der Aktie
     */

    public void clearAlarms(String stockID) {
        entries.get(stockID).alarm.clear();
    }

    /**
     * Ergänzt eine Liste Alarme
     * @param stockID Die dazugehörige Aktie
     * @param alarms Die Alarme
     */

    public void putAlarms (String stockID, Double[] alarms) {
        if (alarms==null) {
            return;
        }
        for (int i=0; i<alarms.length; i++) {
            putNewAlarm(stockID, alarms[i]);
        }}

    /**
     * Löscht alle Alarme
     */

    public void cancelAllAlarms() {
        if (entries == null || entries.size()==0) {
            return;
            }
        for (String stockID: entries.keySet()) {
            clearAlarms(stockID);
        }}

    /**
     * Erstellt einen Indikator
     * @param stockID Die zugehörige Aktie
     * @param indicator Der Indikator
     */

    public void putIndicator(String stockID, ChartIndicator indicator) {
        //Spezialfall test
        if (!entries.containsKey(stockID)) {
            try {
                putSubscription(stockID, false, null);
            } catch (JsonSyntaxException | DataBrokerException | IOException e) {
                e.printStackTrace();
            }
        }
        if (!entries.get(stockID).indicator.contains(indicator)) {
            entries.get(stockID).indicator.add(indicator);
        }
    }

    /**
     * Überträgt Indikatoren 
     * @param stockID Die zugehörige Aktie
     * @param indicators Die Indikatoren
     */

    public void putIndicators (String stockID, ChartIndicator[] indicators) {
        for (int i=0; i<indicators.length; i++) {
            putIndicator(stockID, indicators[i]);
        }}

    /**
     * Löscht einen Indikator
     * @param stockID Die zugehörige Aktie
     * @param indicator Der Indikator
     */
    public void cancelIndicator(String stockID, ChartIndicator indicator) {
        entries.get(stockID).indicator.remove(indicator);
    }

    /**
     * Gibt die Alarme einer Aktie als Array zurück
     * @param stockID Die Kennung der Aktie
     * @return Die Alarme
     */

    public Double[] getAlarmsAsArray(String stockID) {
        if (entries.get(stockID).alarm.size()==0) {return new Double[0];}
        return getObjectArrayAsDouble(entries.get(stockID).alarm.toArray());
    }

    /**
     * Gibt die Alarme einer Aktie als Array zurück
     * @param stockID Die Kennung der Aktie
     * @return Die Alarme
     */

    public double[] getAlarmsAsdoubleArray(String stockID) {
        double [] result;
        if (entries.get(stockID).alarm.size()==0) {
            return new double[0];
        }
        else {
            result = new double[entries.get(stockID).alarm.size()];
            int i=0;
            for (Double alarm : entries.get(stockID).alarm) {
                result[i]= alarm.doubleValue();
                i++;
            }
        }
        return result;
    }

    Double[] getObjectArrayAsDouble(Object[] items) {
        Double[] result=new Double[items.length];
        for(int i = 0; i<items.length; i++) {
            result[i]=((Double)items[i]);
        }
        return result;
    }

    /**
     * Gibt die Indikatoren einer Aktie als Array zurück
     * @param stockID Die Kennung der Aktie
     * @return Die Indikatoren
     */

    public ChartIndicator[] getIndicatorsAsArray(String stockID) {
        if (entries.get(stockID).indicator.size()==0) {return null;}
        return getObjectArrayAsIndicator(entries.get(stockID).indicator.toArray());
    }

    ChartIndicator[] getObjectArrayAsIndicator(Object[] items) {
        ChartIndicator[] result=new ChartIndicator[items.length];
        for(int i = 0; i<items.length; i++) {
            result[i]=((ChartIndicator)items[i]);
        }
        return result;
    }

    /**
     * Gibt die Bezeichnungen der Indikatoren einer Aktie als Array zurück
     * @param stockID Die Kennung der Aktie
     * @return Die Bezeichnungen
     */ 

    public String[] getIndicatorsLabelsAsArray(String stockID) {
        String[] result=new String[entries.get(stockID).indicator.size()];
        ChartIndicator[] indicator= getIndicatorsAsArray(stockID);
        for (int i=0; i<result.length; i++) {
            if (indicator[i] instanceof MovingAverage) {
                result[i]=((MovingAverage) indicator[i]).getLabel();
            }
            else if (indicator[i] instanceof BollingerBand) {
                result[i]=((BollingerBand) indicator[i]).getLabel();
            }
        }
        return result;
    }

    /**
     * Gibt die Farben der Indikatoren einer Aktie als Array zurück
     * @param stockID Die Kennung der Aktie
     * @return Die Farben der Indikatoren
     */

    public Color[] getIndicatorsColorsAsArray(String stockID) {
        Color[] result=new Color[entries.get(stockID).indicator.size()];
        ChartIndicator[] indicator= getIndicatorsAsArray(stockID);
        for (int i=0; i<result.length; i++) {
            result[i]=indicator[i].getColor();
        }
        return result;
    }

    /**
     * Entfert einen Chart von der Abonenntenliste einer Aktie
     * @param stockID Die Kennung der Aktie
     * @param model Das Model des Charts
     */

    public void removeChart(String stockID, FrameChartModel model) {
        putCancelationFromC(stockID, model);
    }

    /**
     * Setzt den letzten Kurs einer Aktie
     * @param StockID Die Aktie
     * @param lastPrice Der letzte Kurs
     */

    public void setLastPrice(String StockID, double lastPrice) {
        entries.get(StockID).lastPrize=lastPrice;
    }
    
    /**
     * Gibt eine Liste der Aktien, für die Alarme eingetragen sind zurück
     * @return Die Liste
     */
    
    public Set<String> getAlarmStockIds() {
        Set <String> result=new HashSet<String>();
        for (String stockID : entries.keySet()) {
            if (!entries.get(stockID).alarm.isEmpty()) {
                result.add(stockID);
            }
        }
        return result;
    }

    class SCCEntry{
        String stockID;
        List<Double> alarm;
        List<ChartIndicator> indicator;
        double lastPrize;
        boolean alarmIsOn;
        boolean isInWatchlist;
        //Es braucht wirklich keine Verbindung A/I und Charts, offene Charts haben ihre, bei neuen Charts wird es 
        //eine Vorbelegung - einmal alles - geben, Fenster vorholen bei Alarm gibt es dann halt nicht
        List <FrameChartModel> openCharts;
        SCCEntry(String stockID){
            this.stockID=stockID;
            isInWatchlist=false;
            alarm=new ArrayList<Double>();
            indicator=new ArrayList<ChartIndicator>();
            openCharts = new ArrayList<FrameChartModel>();
        }

    }
}
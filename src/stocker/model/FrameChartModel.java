package stocker.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
import com.google.gson.JsonSyntaxException;

import stocker.Pushable;
import stocker.controller.*;
import stocker.model.databroker.*;
import stocker.view.*;

import java.util.*;

/**
 * 
 * Die Klasse <code>FrameChartModel</code> bildet das Model der Chartansicht ab
 *
 * @author Joachim Otto
 */

public class FrameChartModel implements Pushable{
    //long wichtig sonst gibt es in dateIntervall einen Überlauf
    long candleCount;
    String intervall;
    String chartTypName;
    int chartTyp; //1=0
    long startIntervall;
    long stopIntervall;
    String stockID;
    JSONResultCandles dataImport;
    String stockTitle;
    double currentPrice;
    Double[] alarms;
    ChartIndicator[] indicator;
    final String[] resolution;
    final String[] resolTitle;
    //Für die Intervalermittlung
    private final ZoneId NYSE= ZoneId.of("America/New_York");
    private final int minProTag=1440;
    private final int minPerExchangeDay=390;
    private final int minPerKDGDay=840;

    Candle[] items;
    StockerAppSettings sas;
    FrameChart annFrame;
    StockControllCenter SCC;
    DataBrokerPull sourcePull;

    /**
     * Der Timer wird initialisiert mit der 
     * @param sas den AppSettings
     * @param stockID der überwachten Aktie 
     * @param annFrame dem darstellenden Frame
     * @throws DataBrokerException Bei Problemen mit dem Provider
     * @throws JsonSyntaxException Bei korrupten Dateien
     * @throws IOException Bei Netzproblemen
     */
    public FrameChartModel(StockerAppSettings sas, String stockID, FrameChart annFrame) throws DataBrokerException, JsonSyntaxException, IOException {
        this.sas=sas;
        StockerPreferences prefer= sas.getPrefer();
        intervall = prefer.defaultChartInterval;
        chartTypName= prefer.getDefaultChartTyp();
        chartTyp=(chartTypName.compareTo("Kerze")==0? prefer.CHARTTYP_CANDLE : prefer.CHARTTYP_LINE);
        this.candleCount=prefer.getCandleCount();
        this.stockID=stockID;
        this.annFrame= annFrame;
        SCC=sas.getSCC();
        resolution=prefer.getChartIntervals();
        resolTitle=prefer.getChartIntervalLabels();
        SCC.putSubscriptionFromC(stockID,  this);
        populatemodel();
    }        

    /**
     * Füllt das Model mit den entsprechenden Daten
     */

    public void populatemodel() {

        //Bezug der Aktie, um den Titel bilden zu können
        sourcePull=new DataBrokerPull(sas);
        try {
            stockTitle = sourcePull.stockLookup(stockID);
        }
        catch (Exception e) {
            JOptionPane.showConfirmDialog(null, "FrameChartModel (1): " + e.getMessage());
        }

        items = getCandles(candleCount);

        try {
            currentPrice=sourcePull.getCurrPrice(stockID);
        }
        catch (Exception e){
            JOptionPane.showConfirmDialog(null, "FrameChartModel(3): " + e.getMessage(), "Achtung", 
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);                 
            return;
        }
        try {
            items[items.length-1]=redefineLastCandle();
        } catch (JsonSyntaxException | IOException | DataBrokerException | StockerException e) {}

        if (indicator!=null) {
            for (ChartIndicator tmpIndicator : indicator) {
                tmpIndicator.fireDataChanged();
            }
        }
    }

    /**
     * Die fehlende aktuellste Kerze wird aus Kerzen kleinerer Intervalle erstellt
     * @throws DataBrokerException Bei Problemen mit dem Provider
     * @throws JsonSyntaxException Bei korrupten Dateien
     * @throws IOException Bei Netzproblemen
     * @throws StockerException Bei Anwendungsproblemen
     * @return Die letzte Kerze
     */

    public Candle redefineLastCandle() throws JsonSyntaxException, IOException, DataBrokerException, StockerException {
        Candle result= items[items.length-1];
        Candle[] tmpCandles=new Candle[0];
        JSONResultCandles localImport;
        //kerzen ziehen mit kleinerem Intervall und von jetzt bis timestamp -1
        //abgleichen heisst preise mit letzter Kerze abgleichen, timestamp ersetzen
        //if monat tageskerzen und mit letzter abgleichen
        if (intervall.compareTo("M")==0){
            localImport = sourcePull.getCandleFromJSON(stockID, "D", (result.timestamp-1)*1000, System.currentTimeMillis());
            tmpCandles=localImport.getAsCandleArray();
        }
        if (intervall.compareTo("W")==0){
            localImport = sourcePull.getCandleFromJSON(stockID, "D", (result.timestamp-1)*1000, System.currentTimeMillis());
            tmpCandles=localImport.getAsCandleArray();
        }
        if (tmpCandles!=null && tmpCandles.length!=0) {
            result=compareCandle(tmpCandles, result);
            if (tmpCandles!=null && tmpCandles.length!=0) {
                localImport = sourcePull.getCandleFromJSON(stockID, "60", (result.timestamp-1)*1000, System.currentTimeMillis());
                tmpCandles=localImport.getAsCandleArray();
                }
            }
        if (intervall.compareTo("D")==0){
            localImport = sourcePull.getCandleFromJSON(stockID, "60", (result.timestamp-1)*1000, System.currentTimeMillis());
            tmpCandles=localImport.getAsCandleArray();
            if (tmpCandles!=null && tmpCandles.length!=0) {
                result=compareCandle(tmpCandles, result);
                }
        }
        //Wäre intervall =M, W, D, result wäre jetzt stundenaktuell
        localImport = sourcePull.getCandleFromJSON(stockID, "1", result.timestamp*1000, System.currentTimeMillis());
        tmpCandles=localImport.getAsCandleArray();

        if (tmpCandles!=null && tmpCandles.length!=0) {
            result=compareCandle(tmpCandles, result);
            }
            return result;
        }

        Candle compareCandle(Candle[] locSource, Candle locTarget) {
            //High und Low durch Verleich, Opening ist das Opening der ersten Kerze, bleibt also
            //unberührt, Closing ist das Closing der letzten Kerze und wird dann später mit trades überschrieben
            locTarget.closing=locSource[locSource.length-1].closing;
            locTarget.timestamp=locSource[locSource.length-1].timestamp;
            for (int i=0; i<locSource.length; i++) {
                locTarget.high=(locTarget.high<locSource[i].high?locSource[i].high:locTarget.high);
                locTarget.low=(locTarget.low>locSource[i].low?locSource[i].low:locTarget.low);            
            }
            return locTarget;
        }


        /**
         * Der Datenprovider übermittelt aktuelle Handelsdaten
         */
        @Override
        public void receivePushNotificaton(Trade trade) {
            /* Eigentlich: push werden mit der letzen Kerze verglichen: h/l wenn neues min/max, 
             * opening bleibt, closing = push-price.
             */
            //Wenn Trades kommen bevor der Chart steht
            if (items==null) {return;}
            currentPrice=trade.price;
            items[items.length-1].closing=trade.price;
            items[items.length-1].timestamp=trade.timestamp;
            items[items.length-1].high=(items[items.length-1].high<trade.price?trade.price:items[items.length-1].high);
            items[items.length-1].low=(items[items.length-1].low<trade.price?trade.price:items[items.length-1].low);
            annFrame.refreshFooter();
            if (indicator!=null) {
                for (ChartIndicator tmpIndicator : indicator) {
                    tmpIndicator.getUpdateDueNewCandle(trade.price);
                }
            }

            annFrame.revalidate();
        }

        /**
         * Das Model wird aufgegeben
         */
        public void dispose() {
            SCC.putCancelationFromC(stockID, this);
        }

        /**
         * Die Daten haben sich geändert
         */

        public void firemodelDataChanged() {
            annFrame.receiveUpdate();
        }

        /**
         * Gibt den Charttyp gemäß den Konstanten in den <code>Prefernces</code> zurück
         * @return Der Charttyp
         */

        public int getChartTyp() {
            return chartTyp;
        }

        /**
         * Setzt den Charttyp gemäß den Konstanten in den <code>Prefernces</code> zurück
         * @param chartType der Charttyp
         */

        public void setCharttyp(int chartType) {
            //Hier muss nicht neu berechnet werden, denn das ist nur die Darstellung, berchnet wird nur bei Intervalländerung 
            chartTypName=((chartType==sas.getPrefer().CHARTTYP_CANDLE) ? "Kerze" : "Line");
            this.chartTyp=chartType;
            firemodelDataChanged();
        }

        /**
         * Ruft den Dialog zur Indikatorerfassung auf
         */

        public void setIndikator() {
            new FrameChartIndicator(sas, annFrame, this);
        }
        /**
         * Gibt die gesetzten Indikatoren zurück
         * @return die Indikatoren
         */

        public ChartIndicator[] getIndicator() {
            return indicator;
        }

        /**
         * Ersetzt die gesetzten Indikatoren
         * @param newIndicator die neuen Indikatoren
         */

        public void replaceIndicator(ChartIndicator[] newIndicator) {
            this.indicator=newIndicator;
            SCC.putIndicators(stockID, newIndicator);
        }

        /**
         * Löscht die aktuell gesetzten Indikatoren
         */

        public void clearIndicator(){
            this.indicator=null;
        }

        /**
         * Ersetzt die gesetzten Alarme
         * @param alarm die Alarme
         */

        public void replaceAlarm(Double[] alarm) {
            alarms=alarm;
            SCC.putAlarms(stockID, alarm);
        }

        /**
         * Gibt die Alarme zurück
         * @return die Alarme
         */    

        public Double[] getAlarme() {
            return alarms;
        }

        /**
         * Ruft den Dialog zur Alarmerfassung auf
         */

        public void setAlarm(){
            new FrameChartAlarm(sas, annFrame);
        }

        /**
         * Gibt das aktuelle Intervall zurück
         * @return Das Intervall
         */
        public String getIntervallTyp() {
            return intervall;
        }

        /**
         * Ändert das aktuelle Intervall. ACHTUNG: Es findet keine Neuberechnung statt. Dafür:
         * <code>setIntervall</code>
         * @param intervallTyp Name des Intervalls
         */

        public void setIntervallTyp(String intervallTyp) {
            intervall=intervallTyp;
        }

        /**
         * Ändert das aktuelle Intervall 
         * @param target die Position innerhalb der Intervalle
         */

        public void setIntervall(int target) {
            intervall =resolution[target];
            stopIntervall = System.currentTimeMillis();
            startIntervall = dateIntervallStart(stopIntervall, intervall, sas.getPrefer().candleCount);
            populatemodel();
            firemodelDataChanged();
        }

        /**
         * Liefert das aktuelle Intervall 
         * @return das Intervall
         */

        public String getIntervall() {
            return intervall;
        }

        /**
         * Liefert den Namen der dargestellten Aktie 
         * @return der Name
         */

        public String getStockTitle() {
            return stockTitle;
        }
        /**
         * Liefert die Kennung der dargestellten Aktie 
         * @return die Kennung
         */

        public String getStockID() {
            if (stockID==null) {return " ";}
            return stockID;
        }

        /**
         * Liefert den aktuellen Preis der dargestellten Aktie 
         * @return der Preis
         */
        public double getLastPrice() {
            return currentPrice;
        }

        /**
         * Ermittelt den Titel des Frames aus den Einstellungen des Models
         * @return Der Titel
         */

        public String getFrameTitle() {
            return (getStockTitle()+ " - " + ((chartTyp==sas.getPrefer().CHARTTYP_CANDLE) ? "Kerze" : "Linie") + " - " + resolutionToResolTitle());
        }

        String resolutionToResolTitle() {
            for (int i=0; i<resolTitle.length; i++) {
                if (resolution[i].compareTo(intervall)==0) {
                    return resolTitle[i];
                }
            }
            return null;
        }

        /**
         * Liefert eine gewünschte Zahl an Kerzen, wenn der Provider liefert
         * @param locCandleCount die gewünschte Kerzenzahl
         * @return Die gewünschten Kerzen
         */   
        public Candle[] getCandles (long locCandleCount) {
            Candle []result; 
            //Bezug der eigentlichen Daten
            stopIntervall = System.currentTimeMillis();
            //Berechnung des Startpunktes abhängig von Intervallbreite und dem Provider
            startIntervall = dateIntervallStart(stopIntervall, intervall, locCandleCount);
            try {
                dataImport = sourcePull.getCandleFromJSON(stockID, intervall, startIntervall, stopIntervall);
                result = dataImport.getAsCandleArray(locCandleCount);                 
            }
            catch (Exception e){
                JOptionPane.showConfirmDialog(null, "FrameChartModel(2): " + e.getMessage(), "Achtung", 
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
                return null;
            }
            if (result.length==0) {
                JOptionPane.showConfirmDialog(null, "Keine Kerzen! \n" + 
                        timestampToString(startIntervall, "dd-MM-yyyy hh:mm:ss")
                + " " + timestampToString(stopIntervall, "dd-MM-yyyy hh:mm:ss"), "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
            }
            return result;
        }

        /**
         * Setzt die Kerzen die der Chart darstellt
         * @param items Die Kerzen
         */

        public void setCandles(Candle[] items) {
            this.items=items;
        }

        /**
         * Gibt die dargestellten Kerzen zurück
         * @return Die Kerzen
         */

        public Candle[] getCandles() {
            return items;
        }

        /**
         * Berechnet zu einem Intervall und einer gewünschten Kerzenzahl den nötigen Abstand in 
         * Sekunden. Rückgabe enthält Puffer, Kerzenarrays müssen evtl. gekürzt werden
         * @param interEnd Das Enddatum, also meist jetzt
         * @param mode das Intervall, 1,5,15,60, D, W, M
         * @param reqCandleCount die gewünschte Kerzenzahl
         * @return den Abstand in Sekunden
         */
        public long dateIntervallStart(long interEnd, String mode, long reqCandleCount) {
            String res=mode;
            if (isNumber(mode)) {
                res="Min";
            }
            long delay=0;
            switch (res) {
            case "Min":
                //Minutenintervalle können nicht einfach mit Faktor korrigiert werden,
                //Wochenden u.ä. muß mühsamer rausgerechnet werden
                delay= extendIntervalMin(Long.valueOf(mode),  reqCandleCount) * 60 * 1000;
                break;
                //Korrektur (im 1000er)  *7/5 für Wochendende und *1.2 für Feiertage und Sicherheit
            case "D" :
                delay = 24 * 60 * 60* 1680* candleCount;
                break;
                //Ab hier keine Korrektur, denn bei W/M-Kerzen spielen Feiertage keine Rolle 
            case "W":
                delay = 7 * 24 * 60 * 60* 1000 * candleCount;
                break;
            case "M": 
                delay = 12L * 7L * 24L * 60L * 60L* 1000L * candleCount;
                break;
            default:
            }
            return (interEnd-delay);
        }

        boolean  isNumber(String testStr) { 
            try {  
                Integer.parseInt(testStr);  
                return true;
            } catch(NumberFormatException e){  
                return false;  
            }  
        }

        /**
         * Berechnet aus einem Timestamp ein Datum
         * @param timestamp Der Timestamp
         * @param format Eine Vorgabe an das Format gemäß <code>SimpleDateFormat</code>
         * @return Das Datum als String
         */

        public String timestampToString(long timestamp, String format) {
            Date date = new Date(timestamp*1000L); 
            SimpleDateFormat dateForm = new SimpleDateFormat(format);
            dateForm.setTimeZone(TimeZone.getDefault());
            String resultDate = dateForm.format(date);
            return resultDate;
        }

        //Die Minutenkorrektur, Eingabe Intervalbreite und erwartete Kerzenzahl,
        //Rückgabe Minuten zum direkten Abzug, die dann von Timestamp abgezogen werden
        //Anwendung: (n-)Minutenkerzen, die über Finnhub geholt werden
        long extendIntervalMin (long minInterval, long reqCandleCount) {

            //Vorgaben für den KDG
            List<LocalDate> holidays;
            LocalTime now = LocalTime.now();
            holidays = new ArrayList<>();
            int minPerStockDay=minPerKDGDay;
            LocalTime stockOpen = LocalTime.parse("08:00");
            LocalDate nowDay =LocalDate.now();
            LocalDate startDay=LocalDate.now();
            //Korrektur falls finnhub
            if (sas.getPrefer().getPullURL().contains("finnhu")) {
                holidays= sas.getPrefer().getNYSEHolydays2021();
                minPerStockDay=minPerExchangeDay;
                stockOpen=LocalTime.parse("09:30");
                now=LocalTime.now(NYSE);
                nowDay =LocalDate.now(NYSE);
                startDay=LocalDate.now(NYSE);
            }

            long result;
            int bdNeeded=0;
            int minLastDay=0;
            int daysNeeded=0;

            //wieviel Minuten (Kerzen +5 Puffer *  minIntervalle) brauche ich? 
            long minLeft = minInterval*(reqCandleCount + 5);
            //Ist Börsentag ?
            if (nowDay.getDayOfWeek()!=DayOfWeek.SUNDAY &&
                    nowDay.getDayOfWeek()!=DayOfWeek.SATURDAY &&
                    !holidays.contains(nowDay)) {
                int minOpen = (int) ChronoUnit.MINUTES.between(stockOpen, now);
                //Ist die Börse schon offen?
                if  (minOpen>0) {
                    //Und evtl. schon wieder zu?
                    minLastDay=(minOpen>minPerStockDay ? minPerStockDay : minOpen);
                    minLeft=minLeft-minLastDay;
                }
                if (minLeft<=0) {
                    //Die heutige Börsenöffnung reicht aus, es kann einfach der Bedarf zurück gegeben werden
                    return minInterval*(reqCandleCount + 5);
                }}
            //minLeft ist nun das benötigte Gesamtintervall evtl. verringert um die heutige Börsenzeit
            //minLastDay ist diese heutige Börsenzeit

            //wie viele (weitere) Börsentage brauchts
            bdNeeded=0;
            daysNeeded=0;
            //ersten Starttag suchen bei dem die Restzeit <0 ist, also in dessen Öffnungszeit der Beginn fällt
            //TOD warum genau nicht Division?
            while (minLeft>0) {
                minLeft = minLeft - minPerStockDay;
                bdNeeded++;
            }
            startDay= businessDaysBefore(bdNeeded, holidays);
            //Zählt inklusive!
            daysNeeded=(Period.between(startDay, nowDay).getDays());
            //Nachtrag falls heute keine Börsenöffnung (->benötigte Tage gehen vollständig ein)
            minLastDay=(int) ChronoUnit.MINUTES.between(stockOpen, now);
            minLastDay=(minLastDay>minPerStockDay ? minPerStockDay : minLastDay);

            /*
             *Heutige Börsenzeit + (ermittelte Kalendertage) + Überschuß
             *Überschuß: aktuell ist die Zeit bis zur Börsenöffnung berechnet mit minLeft<0
             *mit + minLeft wird diese Grenze wieder nach hinten verschoben.
             *Bsp.: bedarf 2000 min, Börsentag 900 min, heute 100
             *-> 100 + 3*24h (=3*900)  ->Timestamp ist zur Börsenöffnung
             * - 800, Timstamp ist also 800 Minuten nach Börsenöffung
             */
            result = minLastDay + daysNeeded *minProTag + minLeft;
            //   System.out.println(minLastDay + " "+ daysNeeded   +" "+  minLeft);

            return result;
        }

        //gibt einen Zeitraum zurück, der die vorgegebene Zahl  Werktage enthält 
        //V0.1 schmutzig
        LocalDate businessDaysBefore(int requCount, List<LocalDate> holidays) {
            LocalDate start = LocalDate.now(NYSE);
            long tryResult =0;
            long tryDelay = (long) requCount;
            while (tryResult <= requCount) {
                //Schrittweite. Schreitweite abhängig davon ob es  Minutenkerzen sein könnten
                //könnte sein, heißt die Anzahl Arbeitstage ist unter 20 
                tryDelay= tryDelay + (requCount >20 ? 10 : 1 );
                tryResult = countBusinessDaysBetween(start.minusDays(tryDelay), start,  holidays);
            }
            return start.minusDays(tryDelay);
        }

        //Hinweis: hier habe ich mich nach einem Hinweis in der Veranstaltungsnewsgroup weitgehend
        //in einem Blogartikel bedient: https://howtodoinjava.com/java/date-time/calculate-business-days/
        long countBusinessDaysBetween(LocalDate startDate, LocalDate endDate, List<LocalDate> holidays) 
        {
            Predicate<LocalDate> isHoliday = date ->  holidays.contains(date);
            Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                    || date.getDayOfWeek() == DayOfWeek.SUNDAY;  
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            long businessDays = Stream.iterate(startDate, date -> date.minusDays(1)).limit(daysBetween)
                    .filter(isHoliday.or(isWeekend).negate()).count();
            return businessDays;
        }}
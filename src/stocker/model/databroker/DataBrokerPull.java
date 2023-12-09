package stocker.model.databroker;

import java.net.URL;
import com.google.gson.*;
import stocker.model.Stock;
import stocker.model.StockerAppSettings;

import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * 
 * Der Klasse <code>DataBrokerPull</code> bietet eine Schnittstelle zum 
 * Pulldienst des eingestellten Datenprovider
 *
 * @author Joachim Otto
 */


public class DataBrokerPull{

    private final  String pullURL;
    private final  String APIKey;
    /**
     * Die Klasse initialisiert wird mit  
     * @param sas den AppSettings und
     * 
     */

    public DataBrokerPull(StockerAppSettings sas) {
        this.pullURL= sas.getPrefer().getPullURL();
        this.APIKey=sas.getPrefer().getAPIKey();
    }

    /**
     * Zu einer Kennnummer wird eine Aktie gesucht
     * @param stockID die Kennummer
     * @throws DataBrokerException Bei Problemen mit dem Provider
     * @throws JsonSyntaxException Bei korrupten Dateien
     * @throws IOException Bei Netzproblemen
     * @return Der Name oder null
     */

    public String stockLookup(String stockID) throws JsonSyntaxException, IOException, DataBrokerException {
        JSONResultStock tmpStocks = new JSONResultStock();
        tmpStocks = getAktieFromJSON(stockID);
        Stock item = new Stock();
        //Ein Suchergebnis ist immer um zusätzliche, ähnliche Werte angereichert. Es muss gefiltert werden.
        for (Stock tmp : tmpStocks.result) {
            if (stockID !=null) {
                if (tmp.getStockID().compareTo(stockID)==0) {
                    item=tmp;  
                }}}
        return item.getDescription();
    }

    /**
     * Zu einer Kennnummer wird per Quote ein aktueller Preis gesucht
     * @param stockID die Kennummer
     * @throws DataBrokerException Bei Problemen mit dem Provider
     * @throws JsonSyntaxException Bei korrupten Dateien
     * @throws IOException Bei Netzproblemen
     * @return der Preis
     */

    public double getCurrPrice(String stockID) throws JsonSyntaxException, IOException, DataBrokerException {
        return getQuote(stockID).c;
    }
    /**
     * Zu einer Kennnummer wird per Quote ein aktueller Eröffnungspreis gesucht
     * @param stockID die Kennummer
     * @throws DataBrokerException Bei Problemen mit dem Provider
     * @throws JsonSyntaxException Bei korrupten Dateien
     * @throws IOException Bei Netzproblemen
     * @return der Eröffnungspreis
     */

    public double getOpening(String stockID) throws JsonSyntaxException, IOException, DataBrokerException {
        return getQuote(stockID).o;
    }

    //Achtung opening wird evtl. mit letzten Closing überschrieben
    QuoteResult getQuote(String stockID) throws  JsonSyntaxException, IOException, DataBrokerException {

        Gson gson = new GsonBuilder().create();
        String searchResult=searchQuote(stockID);
        QuoteResult result = gson.fromJson(searchResult, QuoteResult.class);
        //kein Kurs weil z.B. Wochende? ->bis zu 5 Tageskerzen anfordern und den Schlußkurs
        //der letzten zurück geben - sollte aber eigentlich nicht auftreten
        if (result.o==0.0) {
            JSONResultCandles tmpCandles = getCandleFromJSON(stockID, "D", 
                    (System.currentTimeMillis()-5 * 24 * 60 * 60* 1000), System.currentTimeMillis());
            result.o= tmpCandles.closing[tmpCandles.closing.length-1];
        }
        return result;
    }
    /**
     * Zu einer Kennnummer werden aktuelle Aktien gesucht
     * @param stockID die Kennummer
     * @throws DataBrokerException Bei Problemen mit dem Provider
     * @throws JsonSyntaxException Bei korrupten Dateien
     * @throws IOException Bei Netzproblemen
     * @return die gefunden Aktien
     */

    public  JSONResultStock getAktieFromJSON(String stockID) throws  JsonSyntaxException, IOException, DataBrokerException {
        Gson gson = new GsonBuilder().create();
        String searchResult=searchStock(stockID);
        if (searchResult.contains("no_data")) {throw new DataBrokerException("Suche erfolglos, Rückgabe: Keine Daten");}
        return gson.fromJson(searchResult, JSONResultStock.class);
    }

    /**
     * Zu einer Kennnummer und einem Zeitraum werden Kerzen gesucht
     * @param stockID die Kennummer
     * @param intervall die Auflösung: 1,5,15,30,60 (Minuten), D,W,M
     * @param from Timestamp des Beginns
     * @param to Timestamp des Endes
     * @throws DataBrokerException Bei Problemen mit dem Provider
     * @throws JsonSyntaxException Bei korrupten Dateien
     * @throws IOException Bei Netzproblemen
     * @return der Eröffnungspreis
     */

    public  JSONResultCandles getCandleFromJSON(String stockID, String intervall, long from, long to) throws JsonSyntaxException, IOException, DataBrokerException {
        Gson gson = new GsonBuilder().create();
        String searchResult=searchCandle(stockID, intervall, from, to);
        if (searchResult.contains("no_data")) {throw new DataBrokerException("Suche erfolglos, Rückgabe: Keine Daten");}
        return gson.fromJson(searchResult, JSONResultCandles.class);    
    }

    /*
     * Aufbau der Such-URLs
     */

    String searchStock(String suchBegriff) throws IOException, DataBrokerException {
        String basisURL= pullURL + "/search?q=" + suchBegriff + "&token=" + APIKey;
        return resPull(basisURL);
    }

    String searchCandle (String Aktie, String resolution, long from, long to)throws IOException, DataBrokerException {
        String basisURL= pullURL + "/stock/candle?symbol=" + Aktie + "&resolution=" + resolution + 
                "&from=" + (from/1000) + "&to=" + (to/1000) + "&token=" + APIKey;
        return resPull(basisURL);
    }

    String searchQuote(String stockID) throws DataBrokerException, IOException{
        String basisURL= pullURL + "/quote?symbol=" + stockID + "&token=" + APIKey;
        return resPull(basisURL);        
    }

    /*
     * Abfrage 
     */

    String resPull(String basisURL) throws  JsonSyntaxException, IOException, DataBrokerException {
        HttpURLConnection con = (HttpURLConnection) new URL(basisURL).openConnection();
        con.setRequestMethod("GET");
        con.connect();
        int code = con.getResponseCode();
        //400 ist eigentlich malformed, tritt aber auch mit der Beispielanfrage aus der Aufgabenst. auf
        if (code > 400) {
            String message = "HTTP-Code: " + code;
            switch (code) {
            case 401:
                message ="Technische Problem beim Provider.";
                break;
            case 403:
                message ="Premium-Account nötig";
                break;
            case 429:
                message ="Zuviele Anfragen(>60) für Free-Account:";
                break;
            default:
            }
            throw new DataBrokerException(message);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); 
        return in.readLine();
    }
    /*
     * innere Klasse um den Quote abzubilden
     * c current
     * h high
     * l low
     * o open
     * pc previous close
     * t timestamp
     */
    class QuoteResult{
        double c;
        double h;
        double l;
        public double o;
        double pc;
        long t;	      
    }
}

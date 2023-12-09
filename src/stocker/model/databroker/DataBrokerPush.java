package stocker.model.databroker;

import java.net.URI;
import javax.swing.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.google.gson.*;
import stocker.controller.StockControllCenter;
import stocker.model.*;

/**
 * 
 * Die Klasse <code>DataBrokerPush</code> bildet den Zugang zum eingestellten Datenprovider 
 * im Push-Mechanismus ab
 * 
 * @author Joachim Otto
 */


public class DataBrokerPush extends WebSocketClient{
    boolean isConnected = false;
    StockerAppSettings sas;
    StockControllCenter SCC;

    /**
     * Die Klasse wird initialisiert mit  
     * @param sas Den Programmeinstellungen
     * @param SCC Dem überwachenden Controller
     * 
     */ 

    public DataBrokerPush(StockerAppSettings sas, StockControllCenter SCC) {
        this(sas);
        this.SCC=SCC;
    }

    DataBrokerPush(StockerAppSettings sas) {
        super(URI.create(sas.getPrefer().getPushURL() + sas.getPrefer().getAPIKey()));
        connect();
        this.sas=sas;
    }

    /**
     * Wird bei Verbindungsöffnung ausgelöst
     */

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Verbindung zum Server ist hergestellt.");
        isConnected = true;
    }

    /**
     * Empfängt Nachrichten
     */

    @Override
    public void onMessage(String message) {

        if (message.contains("{\"type\":\"ping\"}")) {
            return;}
        if (message.contains("\"error\"}")){
            JOptionPane.showConfirmDialog(null, message, "Nachricht vom Datenprovider!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
            return;
        }
        Gson gson = new GsonBuilder().create();
        Trades trades= gson.fromJson(message, Trades.class);
        for (Trade trade : trades.getTrades()) {
            SCC.receiveTrade(trade);
        }
    }

    /**
     * Gibt an ob die geöffnet ist
     * @return Den Öffnungsstatus
     */
    public boolean askIfConnected() {
        return isConnected;
    }

    /**
     * Wird ausgelöst wenn die Verbung unterbrochen wurde
     */

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Die Verbindung wurde beendet vom "
                + (remote ? "Server" : "Client")
                + " Code: " + code
                + " Ursache: " + reason);
        isConnected = false;
    }

    /**
     * Wird bei Fehlern ausgelöst
     */

    @Override
    public void onError(Exception ex) {
        if (ex != null) {
            System.err.println("Ein Fehler ist aufgetreten: "
                    + ex.getMessage());
            ex.printStackTrace();
        }
        else
            System.out.println("Unbekannter Fehler!");
    }

    /**
     * Startet den Bezug Handelsdaten einer Aktie 
     * @param stockID Die Kennung der gewünschten Aktie
     * @throws DataBrokerException Bei Problemen mit dem Provider
     */

    public void subscribeNewStock(String stockID) throws DataBrokerException{
        while (!isConnected) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException iex) {}
        }
        send("{ \"type\": \"subscribe\", \"symbol\": \"" + stockID +"\" }");
    }

    /**
     * Stoppt den Bezug von Handelsdaten zu einer Aktie 
     * @param stockID Die Kennung der Aktie
     */

    public void unSubscribeStock(String stockID) {
        while (!isConnected) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException iex) {}
        }
        send("{ \"type\": \"unsubscribe\", \"symbol\": \"" + stockID +"\" }");        
    }

    /**
     * Schliesst die Verbindung bei fehlendem Bedarf 
     */
    public void closeOnNoSubscription() {
        close();
        System.out.println("Verbindung wegen fehlender Subscriptions beendet!");

    }
}
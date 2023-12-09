
package stocker;

import stocker.model.Trade;
/**
 * 
 * Die Klasse <code>PushableClient</code> war als Eintrag in einer 
 * Beobachterliste des Pushdienstes für Handelsdaten vorgesehen
 *
 *@deprecated
 *
 * @author Joachim Otto
 */

public class PushableClient implements Pushable {
    String stockID;
    Pushable realClient;
    
    public PushableClient(String stockID, Pushable realClient) {
        /**
         * Das Panel wird  mit der 
         * @param stockID dem Verweis auf die beobachtete Aktie 
         * @param realClient dem Beabachter, der  <code>Pushable</code> implementiert
         * initialisiert
         */
        
        this.stockID=stockID;
        this.realClient=realClient;
    }
/**
 * Handelsdaten werden übergeben
 */
    @Override
    public void receivePushNotificaton(Trade trade) {
        realClient.receivePushNotificaton(trade);
    }

}

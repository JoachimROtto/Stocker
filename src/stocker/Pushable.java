package stocker;

import stocker.model.Trade;
/**
 * 
 * Das <code>Pushable</code>-Interface beschreibt 
 * Objekte, die Handelsdaten der Pushdienstes empfangen wollen
 *
 * @author Joachim Otto
 */

public interface Pushable {
    /**
     * Ein Handelsdatum wird übergeben
     *
     * @param trade das Handelsdatum
     */
    void receivePushNotificaton(Trade trade);
}

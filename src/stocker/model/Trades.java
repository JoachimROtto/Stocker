package stocker.model;
/**
 * 
 * Die Klasse <code>Trades</code>bildet die gebündelte Übergabe von Handelsdaten
 * aus dem Pushverfahren ab.
 *
 * @author Joachim Otto
 */

public class Trades {
    Trade[] data;
    String type;
    /**
     * Gibt die Handelsdaten als Array der Klasse <code>Trade</code> wieder
     *
     * @return die Matrikelnummer der/des Studierenden
     */

    public Trade[] getTrades() {
        return data;
    }
  }
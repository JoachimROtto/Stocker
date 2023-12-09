package stocker.model.databroker;
import stocker.model.Stock;

/**
 * 
 * Die Klasse <code>JSONResultStock</code> bildet die gebündelte Rückgabe einer Suchanfrage ab
 * 
 * @author Joachim Otto
 */

public class JSONResultStock {
    int count;
    Stock[] result;
    /**
     * Gibt die Anzahl der Ergebnisse zurück
     * @return Die Anzahl der Ergebnisse
     */

    public int getCount() {
        return count;
    }
    /**
     * Gibt das Ergebnis zurück
     * @return Das Ergebnis
     */

    public Stock[] getResult() {
        return result;
    }
}


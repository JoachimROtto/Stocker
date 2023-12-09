package stocker.model;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * Die Klasse <code>Stock</code> bildet die Rückgabe einer Aktiensuche ab
 *
 * @author Joachim Otto
 */

public class Stock {
    String description;
	String displaySymbol;
	@SerializedName("symbol") String stockID;//das ist eindeutig und relevant
	String type;
	
	/**
	 * Gibt den Namen der Aktie zurück
	 * @return Der Name
	 */
    public String getDescription() {
        return description;
    }
    
    /**
     * Gibt die Kennung der Aktie zurück
     * @return Die Kennung 
     */
    
    public String getStockID() {
        return stockID;
    }}
package stocker.model.databroker;
/**
 * 
 * Die Exception <code>DataBrokerException</code> wird bei Operationen
 * zur Datenabfrage beim jeweiligen Provider geworfen
 *
 * @author Joachim Otto
 */

public class DataBrokerException extends Exception {
    /**
     * Die Exception wird initialisiert mit der 
     * @param message Fehlermeldung
     * 
     */ 
    
	public DataBrokerException(String message) {
        super(message);
        System.out.println("Databroker: " + message);
	
}}

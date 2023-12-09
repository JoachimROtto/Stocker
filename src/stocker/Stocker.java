package stocker;

import javax.swing.SwingUtilities;
import stocker.controller.FrameMainGUI;
/**
 * 
 * Die Klasse <code>Stocker</code> ist die Haupteinstiegsklasse
 * der Anwendung
 *
 * @author Joachim Otto
 */

public class Stocker {

    /**
     * {@link #main (String[] args)} startet die Anwendung 
     * @param args mögliche Argumente der Komandozeile (nicht unterstützt) 
     */
   
	public static void main (String[] args) {
	    //Den Logger stumm schalten
	   // System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "ERROR");
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new FrameMainGUI("Stocker");
            }});}}
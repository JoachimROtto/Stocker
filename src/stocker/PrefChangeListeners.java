package stocker;
/**
 * 
 * Das Interface <code>PrefChangeListeners</code> beschreibt die Schnittstelle 
 * für Klassen, die auf Änderungen der Voreinstellungen lauschen wollen.
 *
 * @author Joachim Otto
 */

public interface PrefChangeListeners {
    /**
     * Die Voreinstellungen wurden geändert
     * @param databrokerChanged insbesondere auch der Datenprovider 
     */
    public void notifyPrefChanged(boolean databrokerChanged);
}

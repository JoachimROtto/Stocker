package stocker.model;

/**
 * 
 * Die Klasse <code>SavedFrame</code> bildet die persistierten
 * Eigenschaften der durch Programmende geschlossenen Fenster ab
 *  
 * @author Joachim Otto
 */

public class SavedFrame {
    /**
     * Konstante für Type Chart
     */
    public final int TYPE_CHART=1;
    /**
     * Konstante für Type Watchlist
     */
    public final int TYPE_WL=2;
    /**
     * Konstante für Type Suche
     */
    public final int TYPE_SEARCH=3;
    /**
     * Konstante für Type Voreinstellungen
     */
    public final int TYPE_PREF=4;

    int type;
    String stockID;
    int offX;
    int offY; 
    int width;
    int heigth;

    /**
     * Gibt den Typen des Frames - 1=Chart, 2=Watchlist, 3= Suche, 4=Voreinstellungen - zurück
     * @return Der Typ
     */

    public int getType() {
        return type;
    }

    /**
     * Setzt den Typen des Frames. 1=Chart, 2=Watchlist, 3= Suche, 4=Voreinstellungen
     * @param type Der Type
     */

    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gibt die beobachtete Aktie zurück
     * @return Die beobachtete Aktie
     */

    public String getStockID() {
        return stockID;
    }

    /**
     * Setzt eine beobachtete Aktie
     * @param stockID Die Kennung der Aktie
     */

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    /**
     * Gibt den horizontalen Abstand zurück
     * @return Der horizontalen Abstand
     */

    public int getOffX() {
        return offX;
    }

    /**
     * Setzt den horizontalen Abstand
     * @param offX Der horizontale Abstand
     */

    public void setOffX(int offX) {
        this.offX = offX;
    }

    /**
     * Gibt den vertikalen Abstand zurück
     * @return Der vertikalen Abstand
     */

    public int getOffY() {
        return offY;
    }

    /**
     * Setzt den vertikalen Abstand
     * @param offY Der vertikalen Abstand
     */

    public void setOffY(int offY) {
        this.offY = offY;
    }

    /**
     * Gibt die Breite zurück
     * @return Die Breite
     */
    public int getWidth() {
        return width;
    }

    /**
     * Setzt die Breite
     * @param width Die Breite
     */

    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gibt die Höhe zurück
     * @return Die Höhe
     */
    public int getHeigth() {
        return heigth;
    }

    /**
     * Setzt die Höhe
     * @param heigth Die Höhe
     */

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }
}

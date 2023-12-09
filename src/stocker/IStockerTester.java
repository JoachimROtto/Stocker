package stocker;

import java.util.Set;

/**
 * 
 * Das <code>StockerTester</code>-Interface beschreibt die Schnittstelle, die
 * für den JUnit-Testlauf verwendet werden muss.
 *
 * @author ProPra-Team FernUni Hagen
 */
public interface IStockerTester {

  /**
   * Gibt die Matrikelnummer der/des bearbeitenden Studierenden zurück.
   *
   * @return die Matrikelnummer der/des Studierenden
   */
  String getMatrNr();

  /**
   * Gibt den kompletten Namen der/des bearbeitenden Studierenden zurück.
   *
   * @return der Name der/des Studierenden
   */
  String getName();

  /**
   * Gibt die E-Mail-Adresse der/des bearbeitenden Studierenden zurück.
   *
   * @return die E-Mail-Adressse der/des Studierenden
   */
  String getEmail();

  /**
   * Die Watchlist wird geleert
   */
  void clearWatchlist();

  /**
   * Die Aktie mit dem als Parameter übergebenen Tickersymbol soll der Watchlist
   * hinzugefügt werden.
   *
   * @param stockId Tickersymbol der Aktie, die hinzugefügt werden soll
   */
  void addWatchlistEntry(String stockId);

  /**
   * Die Aktie mit dem als Parameter übergebenen Tickersymbol soll von der Watchlist
   * entfernt werden.
   *
   * @param stockId Tickersymbol der Aktie, die entfernt werden soll
   */
  void removeWatchlistEntry(String stockId);

  /**
   * Die Tickersymbole aller in der Watchlist enthaltenen Aktien sollen als Array
   * vom Typ String zurückgegeben werden.
   *
   * @return <code>String</code>-Array über alle Tickersymbole, die in der
   *         Watchlist enthalten sind
   */
  String[] getWatchlistStockIds();

  /**
   * Vorhandene Alarme für eine Aktie werden gelöscht.
   *
   * @param stockId Tickersymbol der Aktie, für die die Alarme gelöscht werden sollen.
   */
  void clearAlarms(String stockId);

  /**
   * Vorhandene Alarme für alle Aktien entfernen.
   */
  void clearAllAlarms();

  /**
   * Für die Aktie mit dem Tickersymbol <code>stockId</code> soll für den Wert
   * <code>threshold</code> ein Alarm definiert werden.
   *
   * @param stockId   Tickersymbol, für das der Alarm definiert werden soll
   * @param threshold Wert, bei dem der Alarm definiert werden soll
   */
  void addAlarm(String stockId, double threshold);

  /**
   * Ein für die Aktie mit dem Tickersymbol <code>stockId</code> definierter
   * Alarm mit dem Wert <code>threshold</code> soll entfernt werden.
   *
   * @param stockId   Tickersymbolder Aktie
   * @param threshold Wert, der entfernt werden soll
   */
  void removeAlarm(String stockId, double threshold);

  /**
   * Alle für das als Parameter übergebene Tickersymbol definierten Kursalarme
   * sollen als Array vom Typ double[] zurückgegeben werden.
   *
   * @param stockId Tickersymbol der Aktie, für die die Alarme zurückgegeben
   *                werden sollen
   * @return <code>double</code>-Array über alle Alarme, die für das angegebene
   *         Tickersymbol definiert sind
   */
  double[] getAlarms(String stockId);
  
  /**
   * Alle Symbole, für die ein oder mehrere Alarme definiert wurden, sollen als String-Set
   * zurückgegeben werden.
   *
   * @return <code>Set<String></code> Menge aller Symbole, für die ein oder mehrere
   *         Alarme definiert wurden.
   */
  Set<String> getAlarmStockIds();

  /**
   * Für die im Array <code>stockData</code> vorgegebenen Kurswerte soll der
   * gleitende Durchschnitt über die durch <code>days</code> definierte Anzahl an
   * Tagen berechnet werden. Die einzelnen Werte sollen als Array vom Typ
   * <code>double[]</code> zurückgegeben werden.
   *
   * @param n         Anzahl n von Schlusskursen, über die der gleitende Durchschnitt
   *                  berechnet werden soll
   * @param stockData Array über Kurswerte, für die der gleitende Durchschnitt
   *                  berechnet werden soll
   * @return <code>double</code>-Array der gleitenden Durchschnitte
   */
  double[] getMovingAverage(int n, double[] stockData);

  /**
   * Für die im Array <code>stockData</code> vorgegebenen Kurswerte soll das obere
   * Bollinger-Band mit den Parametern <code>f</code> und <code>n</code> berechnet
   * werden, wobei die einzelnen Werte als Array vom Typ <code>double[]</code>
   * zurückgegeben werden sollen.
   *
   * @param f         Faktor f zur Wichtung der Standardabweichung (Initiale Kanalbreite)
   * @param n         Anzahl n von Schlusskursen, über die der gleitende Durchschnitt
   *                  für die Bollinger-Bänder berechnet werden soll
   * @param stockData Array über Kurswerte, für die das obere Bollinger-Band
   *                  berechnet werden soll
   *
   * @return Array über die <code>double</code>-Werte des oberen Bollinger-Bands
   */
  double[] getUpperBollingerBand(double f, int n, double[] stockData);

  /**
   * Für die im Array <code>stockData</code> vorgegebenen Kurswerte soll das
   * untere Bollinger-Band mit den Parametern <code>f</code> und <code>n</code>
   * berechnet werden, wobei die einzelnen Werte als Array vom Typ
   * <code>double[]</code> zurückgegeben werden sollen.
   *
   * @param f         Faktor f zur Wichtung der Standardabweichung (Initiale Kanalbreite)
   * @param n         Anzahl n von Schlusskursen, über die der gleitende Durchschnitt
   *                  für die Bollinger-Bänder berechnet werden soll
   * @param stockData Array über Kurswerte, für die das untere Bollinger-Band
   *                  berechnet werden soll
   *
   * @return Array über die <code>double</code>-Werte des oberen Bollinger-Bands
   */
  double[] getLowerBollingerBand(double f, int n, double[] stockData);
}
package stocker.model;

import java.awt.Dimension;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Modifier;
import stocker.controller.*;
import stocker.view.*;

/**
 * 
 * Die Klasse <code>StockerAppSettings</code> bildet den Programmzustand ab.
 * Hier ist vermerkt, was persistiert wird.
 *
 * @author Joachim Otto
 */

public class StockerAppSettings {

    //muß nicht statisch weil sas überall bekannt
    final String projectPrefix="stocker";
    final String filePath= projectPrefix + ".persistence";

    transient StockerPreferences prefer;
    transient FrameMainGUI frame;
    transient JDesktopPane desktop;
    transient StockControllCenter SCC;

    transient Dimension sizeFrameMainGUI;
    int sizeFrameMainGUIX;
    int sizeFrameMainGUIY;

    transient Dimension sizeSearchFrame = new Dimension (400,350);
    int sizeSearchFrameX;
    int sizeSearchFrameY;

    int colwidthSearchResult1=10;
    int colwidthSearchResult2=100; 

    transient Dimension sizePrefFrame ;
    int sizePrefFrameX=600;
    int sizePrefFrameY=350;
    transient Dimension sizeChartFrame;
    int sizeChartFrameX=400;
    int sizeChartFrameY=350;
    transient Dimension sizeAlarmFrame;
    int sizeAlarmFrameX=400;
    int sizeAlarmFrameY=350;
    transient Dimension sizeIndicatorFrame;
    int sizeIndicatorFrameX=400;
    int sizeIndicatorFrameY=350;
    transient Dimension sizeIndicatorFrameAdd;
    int sizeIndicatorFrameAddX=400;
    int sizeIndicatorFrameAddY=350;


    transient Dimension sizeWLFrame;
    int sizeWLFrameX;
    int sizeWLFrameY;
    int colwidthWL1;
    int colwidthWL2;
    int colwidthWL3;
    int colwidthWL4;

    String[] stocksSCC;
    Double[][] alarmSCC;
    Double[] lastPrice;

    //Liste offener Fenster & Zusatzinfos
    //String[] spiegelt die Liste im Shutdown (Ungelöste Probleme mit der Concurrency)
    List<String[]> stockIDsWL;
    String [][] stockIDsWLArray;

    List<FrameChartOptions[]> openCharts;
    FrameChartOptions[] openChartsArray;

    List <FrameChartOptions> charts = new ArrayList<FrameChartOptions>();
    SavedFrame[] frames;

    /**
     * Der Timer wird initialisiert mit der 
     * @param prefer den Programmeinstellungen
     */

    public StockerAppSettings(StockerPreferences prefer) {     
        stockIDsWL= new ArrayList<String[]>();
        this.prefer=prefer;
    }

    /**
     * Fügt eine Aktie zur Liste der persistierten Aktien der Watchlist hinzu.
     * @param stockID Die Kennung der Aktie
     * @param Name Der Name der Aktie
     */

    public void addStockID(String stockID, String Name) {
        String [] entry= {stockID, Name};
        if (stockIDsWL==null) {
            stockIDsWL= new ArrayList<String[]>();             
        }
        stockIDsWL.add(entry);
    }

    /**
     * Löscht eine Aktie aus der Liste der persistierten Aktien der Watchlist hinzu.
     * @param stockID Die Kennung der Aktie
     * @param Name Der Name der Aktie
     */

    public void removeStockID(String stockID, String Name) {
        String [] entry= {stockID, Name};
        stockIDsWL.remove(entry);
    }

    /**
     * Vermerkt die Breiten der Spalten im Suchdialog
     * @param colwidthSearchResult1 1. Spalte
     * @param colwidthSearchResult2 2. Spalte
     */

    public void setColSearchFrame(int colwidthSearchResult1, int colwidthSearchResult2) {
        this.colwidthSearchResult1=colwidthSearchResult1;
        this.colwidthSearchResult2=colwidthSearchResult2;
    }

    /**
     * Führt Vorbelegungen für den ersten Programmstart aus
     */

    public void setPreferencesOnFirstStart() {
        //wo geht mit Preferences überschreiben
        sizeSearchFrame =prefer.getMinSearchFrame();
        colwidthSearchResult1=10; 
        colwidthSearchResult2=100; 
        sizeSearchFrame=prefer.getMinSearchFrame();
        sizePrefFrame=new Dimension (600,350);
        sizeChartFrame=prefer.getMinChartFrame();
        sizeWLFrame=prefer.getMinWatchlistFrame();
        sizeFrameMainGUI= new Dimension (900,900);
        sizeFrameMainGUIX=900;
        sizeFrameMainGUIY=900;
        sizeAlarmFrame = new Dimension (400,200);
        sizeAlarmFrameX=400;
        sizeAlarmFrameY=200;
        sizeIndicatorFrame= new Dimension (400,450);
        sizeIndicatorFrameX=400;
        sizeIndicatorFrameY=450;
        sizeSearchFrameX=450;
        sizeSearchFrameY=300;
        sizeWLFrameX=600;
        sizeWLFrameY=300;



    }
    
    /**
     * Gibt die Liste der persistierten Charts zurück
     * @return Die Liste
     */
    
    public FrameChartOptions[] getOpenChartsArray() {
        return openChartsArray;
    }

    /**
     * Gibt die vermerkte  Breite der 1.Spalte der Suche zurück
     * @return Die Breite
     */
    public int getColwidthSearchResult1() {
        return colwidthSearchResult1;
    }

    /**
     * Gibt die vermerkte  Breite der 2.Spalte der Suche zurück
     * @return Die Breite
     */
    public int getColwidthSearchResult2() {
        return colwidthSearchResult2;
    }

    /**
     * Gibt die vermerkte Grösse der Suche zurück
     * @return Die Grösse
     */

    public Dimension getsizeSearchFrame() {
        return new Dimension(sizeSearchFrameX,sizeSearchFrameY);
    }

    /**
     * Gibt die vermerkte Grösse der Watchlist zurück
     * @return Die Grösse
     */
    public Dimension getsizeWLFrame() {
        return new Dimension(this.sizeWLFrameX, sizeWLFrameY);
    }

    /**
     * Gibt die vermerkte  Grösse für Charts zurück
     * @return Die Grösse
     */
    public Dimension getsizeChartFrame() {
        return new Dimension(this.sizeChartFrameX, sizeChartFrameX);
    }

    /**
     * Gibt die vermerkte Grösse des Dialogs Voreinstellungen zurück
     * @return Die Grösse
     */

    public Dimension getsizePrefFrame() {
        return new Dimension(this.sizePrefFrameX, sizePrefFrameY);
    }

    /**
     * Gibt die vermerkte Grösse des Alarmdialogs zurück
     * @return Die Grösse
     */
    public Dimension getSizeAlarmFrame() {
        return new Dimension(this.sizeAlarmFrameX, sizeAlarmFrameY);
    }

    /**
     * Vermerkt die Grösse des Alarmdialogs
     * @param size Die Grösse
     */

    public void setSizeAlarmFrame(Dimension size) {
        sizeAlarmFrameX=size.width;
        sizeAlarmFrameY=size.height;
    }

    /**
     * Gibt die vermerkte Grösse des Indikatordialogs zurück
     * @return Die Grösse
     */

    public Dimension getsizeIndicatorFrame() {
        return new Dimension(sizeIndicatorFrameX, sizeIndicatorFrameY);
    }

    /**
     * Vermerkt die Grösse eines ChartFrames
     * @param size Die Grösse
     */

    public void setsizeIndicatorFrame(Dimension size) {
        sizeIndicatorFrameX=size.width;
        sizeIndicatorFrameY=size.height;
    }
    /**
     * Vermerkt die Grösse eines ChartFrames
     * @param size Die Grösse
     */

    public void setsizeIndicatorFrameAdd(Dimension size) {
        sizeIndicatorFrameAddX=size.width;
        sizeIndicatorFrameAddY=size.height;
    }

    /**
     * Vermerkt die Grösse des Suchfensters
     * @param sizeSearchFrame Die Grösse
     */

    public void setsizeSearchFrame(Dimension sizeSearchFrame) {
        this.sizeSearchFrameX=sizeSearchFrame.width;
        this.sizeSearchFrameY=sizeSearchFrame.height;
    }

    /**
     * Vermerkt die Grösse der Watchlist
     * @param sizeWLFrame Die Grösse
     */

    public void setsizeWLFrame(Dimension sizeWLFrame) {
        this.sizeWLFrameX=sizeWLFrame.width;
        this.sizeWLFrameY=sizeWLFrame.height;
    }

    /**
     * Vermerkt die Grösse eines ChartFrames
     * @param sizeChartFrame Die Grösse
     */

    public void setsizeChartFrame(Dimension sizeChartFrame) {
        this.sizePrefFrameX=sizeChartFrame.width;
        this.sizePrefFrameY=sizeChartFrame.height;
    }

    /**
     * Vermerkt die Grösse des Voreinstellungsdialogs
     * @param sizePrefFrame Die Grösse
     */

    public void setsizePrefFrame(Dimension sizePrefFrame) {
        this.sizePrefFrameX=sizePrefFrame.width;
        this.sizePrefFrameY=sizePrefFrame.height;
    }

    /**
     * Vermerkt die Breite der Spalten der Watchlist
     * @param colwidthWL1 1. Spalte
     * @param colwidthWL2 2. Spalte
     * @param colwidthWL3 3. Spalte
     * @param colwidthWL4 4. Spalte
     */

    public void setColWLFrame(int colwidthWL1, int colwidthWL2,int colwidthWL3, int colwidthWL4) {
        this.colwidthWL1=colwidthWL1;
        this.colwidthWL2=colwidthWL2;
        this.colwidthWL3=colwidthWL3;
        this.colwidthWL4=colwidthWL4;       
    }

    /**
     * Gibt die vermerkte  Breite der 1.Spalte der Watchlist zurück
     * @return Die Breite
     */

    public int getColwidthWL1() {
        return colwidthWL1;
    }

    /**
     * Gibt die vermerkte  Breite der 2.Spalte der Watchlist zurück
     * @return Die Breite
     */

    public int getColwidthWL2() {
        return colwidthWL2;
    }

    /**
     * Gibt die vermerkte  Breite der 3.Spalte der Watchlist zurück
     * @return Die Breite
     */

    public int getColwidthWL3() {
        return colwidthWL3;
    }

    /**
     * Gibt die vermerkte  Breite der 4.Spalte der Watchlist zurück
     * @return Die Breite
     */

    public int getColwidthWL4() {
        return colwidthWL4;
    }

    String getFilePath() {
        return filePath;
    }

    /**
     * Gibt die Referenz auf die Voreinstellungen aus
     * @return Die Voreinstellungen
     */
    public StockerPreferences getPrefer() {
        return prefer;
    }

    /**
     * Gibt die Referenz auf den StockController aus
     * @return Die Referenz
     */

    public StockControllCenter getSCC() {
        return SCC;
    }

    /**
     * Setzt die Referenz auf den StockController
     * @param sCC Der StockController
     */

    public void setSCC(StockControllCenter sCC) {
        SCC = sCC;
    }

    /**
     * Gibt die Liste der notierten Aktien der Watchlist aus.
     * @return stockIDsWLArray Die  Liste als Liste
     */

    public List<String[]> getStockIDsWL() {
        return stockIDsWL;
    }

    /**
     * Tauscht die Liste der notierten Aktien der Watchlist aus.
     * @param stockIDsWL Die neue Liste als Liste
     */

    public void setStockIDsWL(List<String[]> stockIDsWL) {
        this.stockIDsWL = stockIDsWL;
    }

    /**
     * Gibt die Liste der notierten Aktien der Watchlist aus.
     * @return stockIDsWLArray Die  Liste als Array
     */

    public String[][] getStockIDsWLArray() {
        return stockIDsWLArray;
    }

    /**
     * Tauscht die Liste der notierten Aktien der Watchlist aus.
     * @param stockIDsWLArray Die neue Liste als Array
     */

    public void setStockIDsWLArray(String[][] stockIDsWLArray) {
        this.stockIDsWLArray = stockIDsWLArray;
    }

    /**
     * Gibt die Referenz auf das Hauptfenster zurück
     * @return Die Referenz
     */

    public FrameMainGUI getFrame() {
        return frame;
    }

    /**
     * Setzt die Referenz auf das Hauptfenster
     * @param frame Das Hauptfenster
     */
    public void setFrame(FrameMainGUI frame) {
        this.frame = frame;
    }

    /**
     * Setzt die Referenz auf die Voreinstellungen
     * @param prefer Die Voreinstellungen
     */

    public void setPrefer(StockerPreferences prefer) {
        this.prefer = prefer;
    }
    /** 
     * Gibt die Referenz auf das Pane im Hauptfenster zurück
     * @return Das pane
     */
    public JDesktopPane getDesktop() {
        return desktop;
    }

    /**
     * Setzt die Referenz des Panes im Hauptfenster
     * @param desktop Das Pane
     */
    public void setDesktop(JDesktopPane desktop) {
        this.desktop = desktop;
    }
    /**
     * Gibt die Grösse des Hauptfensters zurück
     * @return Die Grösse
     */
    public Dimension getSizeFrameMainGUI() {
        return new Dimension(sizeFrameMainGUIX, sizeFrameMainGUIY);
    }

    /** 
     * Vermerkt die Grösse des Hauptfensters
     * @param sizeFrameMainGUI Die Grösse
     */

    public void setSizeFrameMainGUI(Dimension sizeFrameMainGUI) {
        this.sizeFrameMainGUIX = sizeFrameMainGUI.width;
        this.sizeFrameMainGUIY = sizeFrameMainGUI.height;
    }

    /**
     * Gibt die vermerkten Fenstereinstellungen zurück
     * @return Die Fenstereinstellungen
     */

    public SavedFrame[] getSavedFrames() {
        return frames;
    }

    /**
     * Ermittelt zu persitierende Zustände wie offene Charts und ruft
     * <code>setSetToJSONFile() </code> auf.
     */

    public void saveBeforeLeave() {

        sizeFrameMainGUI=frame.getSize();
        frames = new SavedFrame[frame.getWindowList().size()];
        openChartsArray= new FrameChartOptions[frames.length];
        int i =0;
        int j=0; 
        for (JInternalFrame saveFrame : frame.getWindowList()) {
            frames[i]=new SavedFrame();
            frames[i].setHeigth(saveFrame.getHeight());
            frames[i].setWidth(saveFrame.getWidth());            
            frames[i].setOffX(saveFrame.getX());
            frames[i].setOffY(saveFrame.getY());
            if (saveFrame instanceof FrameChart) {
                openChartsArray[j]=((FrameChart) saveFrame).saveBeforeLeave();
                j++;
            }
            if (saveFrame instanceof FramePreferences) {
                frames[i].setType(frames[i].TYPE_PREF);  
            }
            //Wenn offen dann hier, wenn geschlossen dann dort beim Schliessen
            if (saveFrame instanceof FrameWatchlist) {
                frames[i].setType(frames[i].TYPE_WL);        
                ((FrameWatchlist) saveFrame).saveBeforeLeave();
            }
            i++;
        }
        SCC.saveBeforeLeave();
        setSetToJSONFile();
    }

    /**
     * Liest einen Zustand aus einer Datei nach Vorgabe
     * @return Eine Instanz der Programmeinstellungen
     */

    public  StockerAppSettings getSetFromJSONFile() {
        StockerAppSettings result = new StockerAppSettings(prefer);
        File prefFile = new File(result.getFilePath());
        try {
            Gson gson = new GsonBuilder().create();
            BufferedReader reader = new BufferedReader(new InputStreamReader( new FileInputStream(prefFile)));
            result =  gson.fromJson(reader.readLine(), StockerAppSettings.class);
            reader.close();

        } catch (FileNotFoundException e) {
            JOptionPane.showConfirmDialog(null, "Programmstand konnte nicht gelesen werden\\n Das erste Mal hier?", "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
            result.setPreferencesOnFirstStart();
        }
        catch (Exception e) {
            JOptionPane.showConfirmDialog(null, "Fehler\n" + e.getMessage(), "Dateifehler", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
        }
        return result;     
    }

    /**
     * Speichert den Zustand in eine Datei nach Vorgabe
     */

    public void setSetToJSONFile() {
        File prefFile = new File(getFilePath());
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
                .create();
        String JSONresult = gson.toJson(this);
        FileWriter writer;
        try {
            writer = new FileWriter(prefFile);
            writer.write(JSONresult);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }}

    /**
     * Empfängt zu persistierende Zustände aus dem SCC
     * Die Abfolge der jeweiligen Einträge müssen identisch sein
     * @param stocksSCC Die Kennungen der Aktien
     * @param alarmSCC Ihre Alarme
     * @param lastPrice Ihre letzten Preise
     */

    //Hier werden die  Zustände einfach nur übertragen, denn diese Klasse wird gesichert
    public void setSCCValues(String[] stocksSCC, Double[][] alarmSCC, Double []lastPrice){
        this.stocksSCC= stocksSCC;
        this.alarmSCC= alarmSCC;
        this.lastPrice= lastPrice;
    }

    /**
     * Stellt das SCC aus persistierten Zuständen wieder her. 
     */

    /*
     * Wird zu Beginn einmal ausgeführt um die Watchllist aufzubauen und dort Alarme zu checken 
     * Ähnliches passiert auch in den Charts, konkurrierende Alarmmeldungen gibt es nicht denn 
     * einmal gesetzte Alarme müssen erst wieder zurück genommen werden 
     */
    
    public void restoreSCC() {
        if (stocksSCC==null) {
            return;
        }
        for (int i=0; i<stocksSCC.length; i++) {
            try {
                if (SCC.stockIsSupported(stocksSCC[i])){
                    SCC.putSubscriptionFromC(stocksSCC[i], null);
                    SCC.putAlarms(stocksSCC[i], alarmSCC[i]);
                    SCC.setLastPrice(stocksSCC[i],lastPrice[i]);
                    SCC.checkForAlarmOnStart(stocksSCC[i]);
                }
                else {
                    if (stocksSCC[i]!=null) {
                        JOptionPane.showConfirmDialog(null, "Aktie " + stocksSCC[i] + " wird nicht mehr geführt.", "Wiederherstellung Alarme", JOptionPane.DEFAULT_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                    }}
            } catch (Exception e) {
                JOptionPane.showConfirmDialog(null, e.getMessage(), "Wiederherstellung A/I", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.WARNING_MESSAGE);               
            }}}}
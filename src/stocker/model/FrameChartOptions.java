package stocker.model;

import java.awt.Color;
import java.awt.Dimension;
/**
 * 
 * Die Klasse<code>FrameChartOptions</code> bildet die persistierten
 * Eingenschaften von Charts ab
 *
 * @author Joachim Otto
 */

public class FrameChartOptions {
    transient Dimension frameSize;
    int frameSizeX;
    int frameSizeY;
    int offsetX;
    int offsetY;
    String title;
    String stockID;
    String chartIntervall;
    int chartType;
    String[] labelIndicator;
    int[] n;
    double [] f;
    transient Color[] lineColor;

    Double[] alarms;
    Double lastPriceForAlarm;


    /**
     * Setzt die Indikatoren
     *
     * @param indicator die Indikatoren
     */

    public void setIndicator(ChartIndicator[] indicator) {
        labelIndicator= new String[indicator.length];
        n= new int[indicator.length];
        f= new double[indicator.length];
        lineColor= new Color[indicator.length];
        int i=0;
        for (ChartIndicator tmpIndicator:indicator) {
            if (tmpIndicator instanceof MovingAverage) {
                labelIndicator[i]="GD";
            }
            else if (tmpIndicator instanceof BollingerBand) {
                labelIndicator[i]="BB";
                f[i]=((BollingerBand) tmpIndicator).getF();
            }
            n[i]=tmpIndicator.getN();
            lineColor[i]=tmpIndicator.getColor();
            i++;
        }
    }
    /**
     * Gibt die Indikatoren mit aktualisierter Berechnung zuück
     *
     * @return die Indikatoren
     * @param model das Model für die Berechnung
     * @param prefer die Voreinstellungen
     */

    public ChartIndicator[] getIndicator(FrameChartModel model, StockerPreferences prefer) {
        if (n==null) {
            return null;
        }
        ChartIndicator[] result = new ChartIndicator[n.length];

        for(int i=0;i<n.length; i++) {
            if (labelIndicator[i].compareTo("GD")==0) {
                result[i]=new MovingAverage (n[i], model ,prefer ,lineColor[i]);
            }
            if (labelIndicator[i].compareTo("BB")==0) {
                result[i]=new BollingerBand (f[i], n[i], model ,prefer ,lineColor[i]);
            }
        }
        return result;
    }
    /**
     * Gibt Fenstergösset zurück
     *
     * @return die Fenstergrösse
     */

    public Dimension getFrameSize() {
        return new Dimension(frameSizeX, frameSizeY);
    }
    /**
     * Setzt die Fenstergrösse
     *
     * @param frameSize die Fenstergrösse
     */

    public void setFrameSize(Dimension frameSize) {
        frameSizeX=frameSize.width;
        frameSizeY=frameSize.height;
    }
    /**
     * Gibt die Position auf der X-Achse zurück
     *
     * @return  die Position auf der X-Achse 
     */

    public int getOffsetX() {
        return offsetX;
    }
    /**
     * setzt die Position auf der X-Achse
     *
     * @param offsetX die Position auf der X-Achse
     */

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }
    /**
     * Gibt die Position auf der Y-Achse zurück
     *
     * @return  die Position auf der Y-Achse 
     */

    public int getOffsetY() {
        return offsetY;
    }
    /**
     * setzt die Position auf der Y-Achse
     *
     * @param offsetY die Position auf der Y-Achse
     */

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }
    /**
     * Gibt den Fenstertitel zurück
     *
     * @return der Fenstertitel
     */

    public String getTitle() {
        return title;
    }
    /**
     * Setzt den Fenstertitel
     * @param title der Fenstertitel
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * Gibt die Aktiennummer zurück
     *
     * @return die Aktiennummer
     */

    public String getStockID() {
        return stockID;
    }
    /**
     * Setzt die Aktiennummer
     * 
     * @param  stockID die Aktiennummer
     */

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    /**
     * Gibt das Chartintervall zurück
     *
     * @return das Chartintervall
     */

    public String getChartIntervall() {
        return chartIntervall;
    }

    /**
     * Setzt das Chartintervall
     *
     * @param chartIntervall das Chartintervall
     */

    public void setChartIntervall(String chartIntervall) {
        this.chartIntervall = chartIntervall;
    }

    /**
     * Gibt den Charttyp zurück
     *
     * @return der Charttyp
     */

    public int getChartType() {
        return chartType;
    }

    /**
     * Setzt den Charttyp
     *
     * @param chartType der Charttyp
     */

    public void setChartType(int chartType) {
        this.chartType = chartType;
    }

    /**
     * Gibt die gesetzten Alarme rück
     *
     * @return die Alarme
     */

    public Double[] getAlarms() {
        return alarms;
    }
    /**
     * setzt die Alarme
     *
     * @param alarms die Alarme
     */

    public void setAlarms(Double[] alarms) {
        this.alarms = alarms;
    }

    /**
     * Gibt den letzten Kurs der angezeigten Aktie zurück
     *
     * @return Der letzte Kurs
     */

    public Double getLastPriceForAlarm() {
        return lastPriceForAlarm;
    }

    /**
     * Setzt den letzten Kurs
     *
     * @param lastPriceForAlarm der letzte Kurs
     */

    public void setLastPriceForAlarm(Double lastPriceForAlarm) {
        this.lastPriceForAlarm = lastPriceForAlarm;
    }
}
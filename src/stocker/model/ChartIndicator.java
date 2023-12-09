package stocker.model;

import java.awt.Color;

/**
 * 
 * Die Klasse <code>ChartIndicator</code> ist die Superklasse der Indikatoren
 * 
 * @author Joachim Otto
 */

public abstract class ChartIndicator {
    transient StockerPreferences prefer;
    transient FrameChartModel annModel;

    transient double[] currentMA;
    transient double currentSum;
    transient double lastPrice;
    int locIntervall;
    transient double[] priceCourse;

    Color lineColor;

    /**
     * Liefert das darzustellende Ergebnis.
     * @return Das Ergebnis
     */

    abstract public double[][] getCourse();
    abstract void populateAndCalculate();
    abstract void getUpdateDueNewCandle(double newCourse);
    abstract String getLabel();

    double[] candlesToClosing(Candle[] candles) {
        double[]result = new double[candles.length];
        for (int i=0; i<candles.length; i++) {
            result[i]=candles[i].closing;
        }
        return result;
    }

    /**
     * Berechnet den gleitenden Durchschnitt zu gegebenen Werten
     * @param n Parameter n
     * @param stockData vorgegebene Werte
     * @return Den gleitenden Durchschnitt
     */

    public  double[] getMovingAverage(int n, double[] stockData) {
        double sum=0.0;
        int resLength=((stockData.length-n+1)>0 ? (stockData.length-n+1) : 0);
        double[] result = new double[resLength];
        //Abbruch wegen zu wenig Daten
        if (resLength == 0) {
            return new double[0];
        }
        //Die ersten n Schritte einfach aufsummieren
        //ab n + 1: GD(n) = (bish. Summe - 1. + n+1. closing) /n
        int j=0;
        for (int i=0; i<stockData.length; i++) {
            if (i<n) {
                sum=sum+stockData[i];
                //Spezialfall erstes Resultat (also kein Summandentausch vorgeshen) 
                if (i==n-1) {result[0]=sum / n;}
            }
            else {
                //i-ter Schritt: ein Wert raus, einer rein /n teilen und in die Zelle i-n (weil ja n Schritte bis zum eingentlichen Beginn)
                sum=sum+stockData[i] - stockData[j];
                result[i-n+1]=sum/n;
                j++;
            }}
        //Zwischenstände sichern für Updates bei neuem aktuellen Kurs
        currentSum=sum;
        lastPrice= stockData[stockData.length-1];
        currentMA=result;
        return result;
    }

    /**
     * gibt den aktuell gespeicherten gleitenden Durchschnitt zurück 
     * @return Der aktuelle gleitende Durchschnitt
     */

    public double[] getMovingAverage() {
        return currentMA;
    }

    /**
     * Gibt den Parameter n zurück
     * @return Der Parameter n
     */

    public int getN() {
        return locIntervall;
    }

    /**
     * Fordert zur Neuberechnung auf
     */

    public void fireDataChanged() {
        populateAndCalculate();
    }

    /**
     * Fordert den letzten gleitenden Durchschnitt mit einem neuen Wert zu berechnen
     * @param newPrice Der neue Wert
     */

    public void updateMA(double newPrice) {
        currentSum=currentSum-lastPrice + newPrice;
    //    currentMA[currentMA.length-1]= currentSum/currentMA.length;
    }

    /**
     * Ändert die Darstellungsfarbe
     * @param item Die Darstellungsfarbe
     */

    public void setColor(Color item) {
        lineColor=item;
    }

    /**
     * Gibt die Darstellungsfarbe zurück
     * @return Die Darstellungsfarbe
     */

    public Color getColor() {
        return lineColor;
    }
}
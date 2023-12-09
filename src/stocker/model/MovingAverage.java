package stocker.model;

import java.awt.Color;

import stocker.Pushable;

/**
 * 
 * Die Klasse <code>MovingAverage</code> bildet den Indikator Gleitender Durchschitt ab
 * 
 * @author Joachim Otto
 */

public class MovingAverage extends ChartIndicator implements Pushable{

    /**
     * Der MovingAverage wird initialisiert mit
     * @param n Dem Parameter n
     * @param annModel Der zugeordneten Tabelle
     * @param prefer Den Programmeinstellungen
     * @param color Der Farbe für die Darstellung
     */
    public MovingAverage (int n, FrameChartModel annModel, StockerPreferences prefer, Color color) {
        locIntervall=n;
        this.annModel=annModel;
        this.prefer=prefer;
        populateAndCalculate();
        lineColor =color;
    }

    /**
     * Der MovingAverage wird ohne Parameter initialisiert
     */

    public MovingAverage () {
    }

    /**
     * Gibt den letzten vermerkten Kurs zurück
     * @return Der letzte Kurs
     */

    public double getLastItem() {
        return priceCourse[priceCourse.length-1];  
    }

    /**
     * Gibt die Bezeichnung des gleitenden Durchschnitts zurück
     * @return Die Bezeichnung
     */

    @Override
    public String getLabel() {
        return "GD" + locIntervall;
    }

    /**
     * Gibt den aktuell berechneten gleitenden Durchschntt zurück
     * @return den gleitenden Durchschnitt in [0]
     */
    @Override
    public double[][] getCourse(){
        double [][]result = new double[1][];
        result[0]=currentMA;
        return result;
    }

    /**
     * Bezieht (erneut) die Daten aus einem zugeordneten <code> FrameChartModel </code> 
     * und berechnet den gleitenden Durchschnitt
     */
    @Override
    public void populateAndCalculate(){
        long locCandleCount =locIntervall +prefer.getCandleCount();
        Candle[] candles= annModel.getCandles(locCandleCount);
        priceCourse = candlesToClosing(candles);
        currentMA = calculate(priceCourse);
    }

    /**
     * Berechnet einen Gleitenden Durchschnitt für ein beliebiges Array
     * @param closings Die Quellwerte
     * @return Das Ergebnis
     */

    double [] calculate(double[] closings) {
        return getMovingAverage(locIntervall, closings); 
    }
    /**
     * Ersetzt den letzten Kurs durch ein neues Handelsdatum
     * @param trade Ein neues Handelsdatum
     */

    @Override
    public void receivePushNotificaton(Trade trade) {
        getUpdateDueNewCandle(trade.price);
    }


    @Override
    void getUpdateDueNewCandle(double newCourse) {
        updateMA(newCourse);
    }}

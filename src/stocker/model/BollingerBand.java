package stocker.model;

import java.awt.Color;

import stocker.Pushable;

/**
 * 
 * Die Klasse <code>BollingerBand</code> bildet den Indikator BollingerBand ab
 * Berechnungen erfolgen hier oder im Supertyp
 * 
 * @author Joachim Otto
 */

public class BollingerBand extends ChartIndicator implements Pushable{
    double f;
    int n;
    int m;

    transient double[] stockData;
    transient double[] upperBB;
    transient double[] lowerBB;
    transient double[] annMA; 
    int sizeBB;

    /**
     * Das BollingerBand wird initialisiert mit
     * @param f  Dem Parameter f
     * @param n Dem Parameter n
     * @param annModel Der zugeordneten Tabelle
     * @param prefer Den Programmeinstellungen
     * @param color Der Farbe für die Darstellung
     */
    
    public BollingerBand(double f, int n, FrameChartModel annModel, StockerPreferences prefer, Color color) {
        this.f=f;
        this.n=n;
        m=n;
        locIntervall=n;
        this.annModel=annModel;
        this.prefer=prefer;
        populateAndCalculate();
        lineColor =color;
    }
    
    /**
     * Das BollingerBand wird ohne Anwendungskontext initialisiert
     * 
     * @param f Parameter f
     * @param n Parameter n
     * @param stockData Die Börsendaten zur Berechnung
     */
    
    public BollingerBand(double f, int n, double[] stockData) {
        this.n=n;
        this.f=f;
        this.stockData=stockData;
        locIntervall=n;
        populateAndCalculateNoContext();
    }

    /**
     * Gibt die Bezeichnung des Bollinger Band zurück
     * @return Die Bezeichnung
     */

    @Override
    public String getLabel() {
        return "BB (" + f +", " + n +")";
    }

    /**
     * Gibt den Parameter f zurück.
     * @return f Der Parameter
     */

    public double getF() {
        return f;
    }

    /**
     * Das Bollinger Band wird mit einem neuen Kurs
     * neu berechnet
     * @param newPrice Der neue Kurs
     */

    @Override
    void getUpdateDueNewCandle(double newPrice) {
        updateMA(newPrice);
        annMA=getMovingAverage();
        //updateBB
        upperBB[sizeBB-1] = annMA[sizeBB-1] + f * Math.sqrt(calculateSQRTArgument(sizeBB-1));
        lowerBB[sizeBB-1] = annMA[sizeBB-1] - f * Math.sqrt(calculateSQRTArgument(sizeBB-1));
    }
    
    @Override
    void populateAndCalculate() {
        //Anzahl der darzustellenden Kerzen
        sizeBB=(int) prefer.getCandleCount();
        //Zuzüglich Vorlauf, also 38 bei GD38
        long locCandleCount =locIntervall +sizeBB;
        Candle[] candles= annModel.getCandles(locCandleCount);
        priceCourse = candlesToClosing(candles);
        if (getMovingAverage (locIntervall, priceCourse)==null) {
            return;
        }
        annMA = currentMA; //getMovingAverage (locIntervall, priceCourse);
        //Korrektur falls es zu wenig Kerzen gab
        sizeBB=annMA.length;
        upperBB=new double[sizeBB];
        lowerBB=new double[sizeBB];
        for (int i =0; i<sizeBB; i++) {
            //BB_up = GD(i,n) + f delta (i,m)   --   BB_lower [..] - [..]
            //m=n (lt. KT), GD(i,n) = annMA[i] (bereits berechnet)
            upperBB[i] = annMA[i] + f * Math.sqrt(calculateSQRTArgument(i));
            lowerBB[i] = annMA[i] - f * Math.sqrt(calculateSQRTArgument(i));
        }
    }
    
    void populateAndCalculateNoContext() {
        //Anzahl der darzustellenden Kerzen
        sizeBB=stockData.length-n;
        priceCourse = stockData;
        if (getMovingAverage (locIntervall, priceCourse)==null) {
            return;
        }
        annMA = currentMA; //getMovingAverage (locIntervall, priceCourse);
        //Korrektur falls es zu wenig Kerzen gab
        sizeBB=annMA.length;
        upperBB=new double[sizeBB];
        lowerBB=new double[sizeBB];
        for (int i =0; i<sizeBB; i++) {
            //BB_up = GD(i,n) + f delta (i,m)   --   BB_lower [..] - [..]
            //m=n (lt. KT), GD(i,n) = annMA[i] (bereits berechnet)
            upperBB[i] = annMA[i] + f * Math.sqrt(calculateSQRTArgument(i));
            lowerBB[i] = annMA[i] - f * Math.sqrt(calculateSQRTArgument(i));
        }
    }
    
    

    double calculateSQRTArgument(int pos) {
        // 1/m * sum_j=0^m-1((pred(i,j) - GD(i,n))^2)
        double result=0.0;
        int locPos=0;
        for (int j =0; j<n;j++) {
            //Aufsummieren: zu pos aktuelle und n-1 Vorgänger - jeweils aktuellen MA
            //also pos-j: Bereich der Vorgänger, locIntervall: der Offset, also n
            locPos=n + pos - j-1;
            result = result + Math.pow(priceCourse[locPos]-annMA[pos] , 2);
        }
        return result/locIntervall;
    }

    /**
     * Gibt beide Bollinger Bänder zurück.
     * @return Das obere [0] und untere [1] Bollinger Band
     */

    @Override
    public double[][] getCourse() {
        double [][]result = new double [2][];
        result [0]=upperBB;
        result[1]=lowerBB;
        return result;
    }

    /**
     * Das Bollinger Band erhält ein neues Handelsdatum
     * @param trade Das neue Handelsdatum
     */
    @Override
    public void receivePushNotificaton(Trade trade) {
        getUpdateDueNewCandle(trade.price);
    }

    /**
     * Gibt das untere Bollinger Band zurück.
     * @return Das untere Bollinger Band
     */

    public double getCLowerBB() {
        if (lowerBB==null) {
            return 0.0;
        }
        return lowerBB[sizeBB-1];
    }

    /**
     * Gibt das obere Bollinger Band zurück.
     * @return Das obere Bollinger Band
     */

    public double getCUpperBB() {
        if (upperBB==null) {
            return 0.0;
        }
        return upperBB[sizeBB-1];
    }}

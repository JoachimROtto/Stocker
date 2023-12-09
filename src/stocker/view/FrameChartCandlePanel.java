package stocker.view;

import java.awt.*;
import stocker.model.*;

/**
 * 
 * Die Klasse <code>FrameChartCandlePanel</code> erstellt zu einer Kerzenmenge 
 * eine Kerzendarstellung als JPanel in einem Chart.
 *
 * @author Joachim Otto
 */


public class FrameChartCandlePanel extends FrameChartContentPanel{

    /**
     * Das Panel wird  mit der 
     * @param size Größe 
     * @param model deren Model , 
     * @param annFrame dem Verweis auf das übergeordnete Chart
     * @param prefer den Voreinstellungen 
     * initialisiert
     */

    FrameChartCandlePanel(Dimension size, FrameChartModel model, FrameChart annFrame, StockerPreferences prefer){
        super( size, model, annFrame, prefer);
    }
    /**
     * Die Kerzen werden gezeichnet
     */

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        //Erst die Indikatoren sonst würden die BollingerBänder alles überdecken
        paintAlarmAndIndicators(g);

        //Über den CandleSet gehen und die einzelnen Abschnitte zeichen
        int i=0;
        for(Candle tmp : candles) {
            drawCandleSlice(g, tmp, i);
            i++;
        }
        //Erst der Content dann Scale, sonst sind die Offsets falsch
        paintFixElements(g);

    }
    /**
     * In einzelnen Abschnitten werden die jeweilige Kerze und in 5er-Schritten 
     * eine Beschriftung der x Achse gezeichnet. 
     * @param g das übergeordnete Graphic-Objekt 
     * @param candle die aktuelle Kerze 
     * @param pos die aktuelle Position in der Reihenfolge 
     * 
     */


    public void drawCandleSlice(Graphics g, Candle candle, int pos) {

        /* Konstruktion einer Kerze
         *  Linie am opening und closing, Seitenlinien zwischen Opening und Closing
         *  Dochte von punkt nach oben(low) oder unten (high)
         *  Aber mit der Farbe (rot= op>clos) müssen die Argumente des Rechtecks belegt werden
         *  sonst könnte es negative höhen geben (funktioniert nicht)
         *  Insgesamt muss umgerechnet werden: z.B. closing : 
         *  Höhe - unterer Offset + min = gedachte Nullinie (weit unter Fenster)
         *  closing = gedachte Nullinie - closing
         *  Slicebreite =((GesamtBreite - 2*Offset) /  Kerzenzahl )
         *  Aktueller Offset = Offset + (akt. Kerzennummer *SliceBreite)
         *  Kerzenbreite = Slicebreite - 2 * Kerzenoffset  
         *  X-Mitte (Dochtlinie) = Aktueller Offset + Kerzenoffset + 1/2 Kerzenbreite
         */
        final int cBorder=5;
        int sWidth = locWidth / candles.length; //Offset schon abgezogen
        int cOffset = chartOffset + sWidth * pos;
        int cWidth =  sWidth - 2* cBorder;
        int cMid = cOffset +cBorder + cWidth / 2;

        //Kerze malen, annahme op<cols
        Color boxColor=Color.GREEN;
        int boxY2=coorFromPrice(candle.getOpening());
        int boxY1=coorFromPrice(candle.getClosing());
        //Annahme falsch
        if (candle.getOpening()>candle.getClosing()) {
            boxColor=Color.RED;
            boxY2 = coorFromPrice(candle.getClosing());
            boxY1 = coorFromPrice(candle.getOpening());
        }
        //Dochte
        g.drawLine(cMid ,coorFromPrice(candle.getHigh()), cMid, boxY1);
        g.drawLine(cMid ,coorFromPrice(candle.getLow()), cMid, boxY2);
        //Candle und ihre Umrandung
        g.setColor(boxColor);
        g.fillRect(cOffset + cBorder, boxY1 ,  cWidth, boxY2-boxY1);
        g.setColor(Color.BLACK);
        g.drawRect( cOffset + cBorder, boxY1 , cWidth, boxY2-boxY1);
        //Zwischenlinien
        //notTODO Montagslinien rot wg. Wochenende? nein
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(cOffset, chartOffset, cOffset, chartOffset + locHeight);

        paintScalX( g,  pos, cOffset,  sWidth);
    }}
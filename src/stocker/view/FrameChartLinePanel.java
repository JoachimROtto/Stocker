package stocker.view;
import java.awt.*;
import stocker.model.*;

/**
 * 
 * Die Klasse <code>FrameChartLinePanel</code> erstellt zu einer Kerzenmenge 
 * eine Liniendarstellung als JPanel in einem Chart ab
 *
 * @author Joachim Otto
 */

public class FrameChartLinePanel extends FrameChartContentPanel{
    /**
     * Das Panelwird  mit der 
     * @param size Größe 
     * @param model deren Model , 
     * @param annFrame dem Verweis auf das übergeordnete Chart
     * @param prefer den Voreinstellungen 
     * initialisiert
     */

    FrameChartLinePanel(Dimension size, FrameChartModel model, FrameChart annFrame, StockerPreferences prefer){
        super(size, model, annFrame, prefer);
    }
    /**
     * Die Kerzen werden gezeichnet
     */

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        paintAlarmAndIndicators(g);

        //Über den CandleSet gehen und die einzelnen Abschnitte zeichnen
        for(int i=0; i<candles.length; i++) {
            drawLineSlice(g, i);
        }
        //Erst der Content dann Scale, sonst sind die Offsets falsch
        paintFixElements(g);
    }

    /**
     * In einzelnen Abschnitten werden die jeweilige Kerze und in 5er-Schritten 
     * eine Beschriftung der x Achse gezeichnet. 
     * @param g das übergeordnete Graphic-Objekt 
     * @param pos die aktuelle Position in der Reihenfolge 
     * 
     */

    public void drawLineSlice(Graphics g,  int pos) {

        /* Konstruktion einer Linie
         * closing[n] und closing[n+1] verbinden, x ist dabei die aktuelle und nächste sMid (SliceMitte)
         * y die jeweiligen closings
         * 1 und max? nur bis sMid zeichnen (d.h. erste normal zeichnen letzte verwerfen)
         */
        int sWidth = locWidth / candles.length; //Offset schon abgezogen
        int sOffset = chartOffset + sWidth * pos;
        int sMid = sOffset  + sWidth / 2;        
        int x1, x2, y1, y2;


        if(pos==candles.length-1) {
            paintScalX( g,  pos,  sOffset,  sWidth);
            return;}

        x1=sMid;
        x2=sMid+sWidth;
        y1= coorFromPrice(candles[pos].getClosing());
        y2= coorFromPrice(candles[pos+1].getClosing());

        g.drawLine(x1, y1, x2, y2);
        paintScalX( g,  pos,  sOffset,  sWidth);
    }}
package stocker.view;
import java.awt.Dimension;
import java.util.*;
import java.awt.*;
import java.util.List;
import java.awt.event.*;
import java.awt.geom.Path2D;
import javax.swing.JPanel;
import stocker.model.*;

/**
 * 
 * Die Klasse <code>FrameChartContentPanel</code> ist die Superklasse zur 
 * Linien- und Kerzendarstellung
 *
 * @author Joachim Otto
 */

public class FrameChartContentPanel extends JPanel{
    int chartOffset =30;        
    int chartOffsetEnd;
    Candle[] candles;
    int locWidth;
    int locHeight;
    double max=0, min=Double.MAX_VALUE;
    FrameChartModel model;
    FrameChart annFrame;
    StockerPreferences prefer;
    Font annotFont;
    int mouseX;
    int mouseY;

    FrameChartContentPanel(){}
    FrameChartContentPanel ( Dimension size, FrameChartModel model, FrameChart annFrame, StockerPreferences prefer){
        if (model==null) {return;}
    	candles=model.getCandles();
        this.model=model;
        this.annFrame=annFrame;
        this.prefer=prefer;
        //verfügbarer Bereich        
        locWidth =  size.width - chartOffset-chartOffsetEnd ; 
        locHeight=size.height-chartOffset- chartOffsetEnd ;
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseX=e.getX();
                mouseY=e.getY();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseX=0;
                mouseY=0;
                repaint();
            }});

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX=e.getX();
                mouseY=e.getY();
                repaint();
            }});       

        //min/max über alle Werte +/-5% = Chartwertebereich
        for(Candle tmp : candles) {
            max=(max<tmp.getHigh() ? tmp.getHigh() : max);
            min=(min>tmp.getLow() ? tmp.getLow() : min);
        }

        //relativ wg. pennystocks
        min=min * .90;
        max=max * 1.10;
    }    

    void paintFixElements(Graphics g) {
        setBackground(Color.WHITE);
        //verfügbarer Bereich
        locWidth = getWidth() - 2* chartOffset; 
        locHeight=getHeight()-2*chartOffset;

        paintCrosshair(g);

        //Framework zeichnen ->ausser x/y alles in die Fußzeile
        annotFont=new Font(g.getFont().getFontName(), Font.PLAIN, 10);
        g.setFont(annotFont);
        double annotPrice=0;
        g.setColor(Color.BLACK);
        g.drawLine(chartOffset, chartOffset +locHeight, chartOffsetEnd +3, chartOffset+ locHeight); //X-Achse
        g.drawLine(chartOffsetEnd, chartOffset, chartOffsetEnd, chartOffset + locHeight); //y-Achse
        //erster und letzter Eintrag
        double minRnd = round(min);
        double maxRnd = round(max);

        g.drawLine(chartOffsetEnd, coorFromPrice(minRnd), chartOffsetEnd + 3, coorFromPrice(minRnd));
        g.drawLine(chartOffsetEnd, coorFromPrice(maxRnd), chartOffsetEnd + 3, coorFromPrice(maxRnd));
        //Achsenbeschriftungen (x-Werte kommen in den Slices)
        g.drawString(Double.toString(minRnd), chartOffsetEnd+3, coorFromPrice(minRnd));
        g.drawString(Double.toString(maxRnd), chartOffsetEnd+3, coorFromPrice(maxRnd));

        for (int j = 1; j<10; j++) {
            annotPrice=round(((max-min)/10)*j + min); //Die fehlende zehnte Schleife ist der max
            g.drawLine(chartOffsetEnd, coorFromPrice(annotPrice), chartOffsetEnd + 3, coorFromPrice(annotPrice));
            g.drawString(Double.toString(annotPrice), chartOffsetEnd+3, coorFromPrice(annotPrice));
        }
    }

    void paintAlarmAndIndicators(Graphics g) {

        annotFont=new Font(g.getFont().getFontName(), Font.PLAIN, 10);
        if (model.getAlarme() !=null) {
            for (double alarm : model.getAlarme()){
                g.setColor(prefer.getAlarmColor());
                g.drawLine(chartOffset, coorFromPrice(alarm), chartOffsetEnd +3, coorFromPrice(alarm));
                g.drawString(Double.toString(alarm), chartOffset+3, coorFromPrice(alarm));
            }}
        int posBB=0;

        //Gleitende Durchschnitte müssen nach Bolinger Bändern gezeichnet werden um nicht verdeckt zu werden
        List <MovingAverage> tmpMA=new ArrayList<MovingAverage>(); 

        if (model.getIndicator()!=null) {
            for (ChartIndicator item : model.getIndicator()) {
                if (item instanceof MovingAverage) {
                    tmpMA.add((MovingAverage) item);
                }
                else if (item instanceof BollingerBand) {
                    posBB++;
                    BollingerBand tmpItem=(BollingerBand) item;
                    paintBB(g,tmpItem);
                    g.drawString(tmpItem.getLabel() , 5, chartOffset + 10*posBB);
                }}}
        if (tmpMA.size()!=0) {
            for (ChartIndicator item : tmpMA) {

                double []indicator=item.getCourse()[0];
                //Es konnte kein Indikator berechnet werden
                if (indicator==null) {
                    return;
                }
                g.setColor(item.getColor());
                //Linie in Elementen zeichen, Anfang nach Verfügbarkeit von Indikatoren
                int offSet= (candles.length-indicator.length>0?candles.length-indicator.length:0);
                for(int i=offSet; i<candles.length-1; i++) {
                    g.setColor(item.getColor().brighter());
                    drawGDSlice(g, i, indicator, offSet);
                }
                g.drawString(((MovingAverage) item).getLabel() , 5, coorFromPrice(indicator[0]) +10);
            }}
    }

    void paintBB(Graphics g, BollingerBand item) {

        double [] upperBB=item.getCourse()[0];
        double [] lowerBB=item.getCourse()[1];
        //Offset schon abgezogen, Bezug Kerzen nicht Indikatoren
        int sWidth = locWidth / candles.length; 
        //Korrektur falls zu wenig Indikatoren
        int corrOffset=(candles.length-upperBB.length>0?candles.length-upperBB.length:0);
        corrOffset=corrOffset*sWidth;
        int[] x = new int[upperBB.length * 2];
        int[] y = new int[upperBB.length * 2];

        for (int i=0; i<upperBB.length; i++) {
            //x: Schrittweite aufsummieren vorwärts
            x[i] = chartOffset + corrOffset + sWidth * i + sWidth / 2;
            //y: Koordinate transformieren vorwärts
            y[i]=coorFromPrice(upperBB[i]);
        }
        for (int i=y.length-1; i>=upperBB.length; i--) {
            //x: Schrittweite aufsummieren rükwärts
            x[i] = chartOffset + corrOffset+ sWidth * (x.length-i-1) + sWidth / 2;
            //y: Koordinate transformieren rückwärts
            y[i]=coorFromPrice(lowerBB[x.length-i-1]);
        }

        Graphics2D g2d2= (Graphics2D) g;
        Path2D.Double pathBB = new Path2D.Double();
        g2d2.setColor(item.getColor());
        pathBB.moveTo(x[0],y[0]);
        // Nächste Schleife das ganze abmarschieren. erster Schritt moveto von 0 bis Mitte
        for (int i=1; i<upperBB.length; i++) {
            pathBB.lineTo(x[i], y[i]);
        }
        //Zwischenschritt: Die Enden verbinden
        pathBB.lineTo(x[upperBB.length], y[upperBB.length]);
        //Schleife weiter abmarschieren:  Schritt vom Ende zurück, ab hier aber absteigend sortiert
        for (int i=upperBB.length; i<x.length; i++) {
            pathBB.lineTo(x[i], y[i]);
        }
        //Zwischenschritt: Wieder die Enden verbinden
        pathBB.lineTo(x[0], y[0]);

        pathBB.closePath();
        g2d2.fill(pathBB);
        g2d2.draw(pathBB);


    }

    void drawGDSlice(Graphics g,  int pos, double[] indicator, int offSet) {

        /* Konstruktion einer Linie
         * gd[n] und gd[n+1] verbinden, x ist dabei die aktuelle und nächste sMid (SliceMitte)
         * y die jeweiligen closings
         * 1 und max? Variante die Linie vom ersten/letzten Punkt zur Skala zu verlängern ist Quatsch
         *  -> nur bis sMid zeichnen (d.h. erste normal zeichnen letzte verwerfen)
         */
        //Offset schon abgezogen, Bezug Kerzenzahl nicht Indikatoren
        int sWidth = locWidth / candles.length; 
        int sOffset = chartOffset + sWidth * pos;
        int sMid = sOffset  + sWidth / 2;        
        int x1, x2, y1, y2;
        x1=sMid;
        x2=sMid+sWidth;
        y1= coorFromPrice(indicator[pos-offSet]);
        y2= coorFromPrice(indicator[pos+1-offSet]);

        g.drawLine(x1, y1, x2, y2);
    }
    void paintScalX(Graphics g, int pos, int sOffset, int sWidth){

        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(sOffset, chartOffset, sOffset, chartOffset + locHeight);

        //Seitenefekt: Bei (z.B.) 20 Kerzen springt die Darstellung der Legende alle 20 Pixel, daher wird hier das
        //aktuelle Ende des Charts aktualisiert  (wird erst nach Ende der Schleife  benutzt)
        chartOffsetEnd=sOffset+sWidth;

        //jedes 5. Element Fußzeile 
        if (pos%5==0) {
            g.setColor(Color.BLACK);
            g.drawLine(sOffset, chartOffset, sOffset, chartOffset + locHeight);
            g.setFont(annotFont);
            g.drawString(textForPanelFooter(candles[pos].getTimestamp()), sOffset+2, chartOffset+locHeight+10);                    
        }}


    //heigth / (max-min) = pixel/dollar -> Höhe + offset -(price-min * pixel/dollar) = Koordinate  
    int coorFromPrice(double price) {
        //Verfügbare Höhe / max-min, max-min=Werteberich der Skala
        double ratio = locHeight / (max-min);
        //(verfügbare) Höhe + Offset = Nulllinie, von dort zurück mit ratio * (preis-nullpreis(intervallgrenze)))
        return (int)(locHeight + chartOffset - ((price - min) * ratio));
    }
    //Ermittlung des zugehörigen Wertes für die Fadenkreuzposition Y
    double priceFromCoor(int y) {
        //Verfügbare Höhe / max-min, max-min=Werteberich der Skala
        double ratio = locHeight / (max-min);
        double result;
        //(verfügbare) Höhe + Offset - mouseY = Abstand von der Nulllinie = min
        result = locHeight + chartOffset -mouseY;
        //Betrag = ratio * Abstand + min
        result = result/ratio + min;        //(verfügbare) Höhe + Offset = Nulllinie, von dort zurück mit ratio * (preis-nullpreis(intervallgrenze)))
        return result;
    }

    //Ermittlung der zugehörigen Kerze und der Timestamp für die Fadenkreuzposition X
    String dateFromCoor(int x) {
        //Position - Offset = Position in der Tabelle, Tabellenbreite / Kerzen = Kerzenbreite
        //Tabellenposition / Kerzenbreite = aktuelle Kerze
        int pos = (x-chartOffset) / (locWidth/candles.length);
        //Tabelle verlassen
        if (pos<0 || pos>(candles.length-1)) {
            return " ";
        }
        return textForPanelFooter(candles[pos].getTimestamp());
    }

    //Fadenkreuz zeichnen
    void paintCrosshair(Graphics g){
        //x und y sind immer >0, daher ist das sicher mausExit
        if ((mouseX+mouseY==0)) {return;}
        Graphics2D g2d= (Graphics2D) g;
        g2d.setColor(Color.MAGENTA);

        float[] dashingPattern1 = {2f, 2f};
        Stroke stroke1 = new BasicStroke(2f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 2.0f);

        g2d.setStroke(stroke1);
        g2d.drawLine(5, mouseY, getWidth(), mouseY );
        g2d.drawLine(mouseX, 5, mouseX,getHeight() );
        notifyFooter();
    }

    //Übergabe der Fadenkreuzposition an den Chart und seine Fußzeile
    void notifyFooter() {
        //Frame verlassen
        if ((mouseX+mouseY)==0) {
            annFrame.putToFooter("" , 0.0);
        }
        else {
            annFrame.putToFooter(dateFromCoor(mouseX), round(priceFromCoor(mouseY)));
        }
    }

    //Umrechnen des Timestamp um die Skalierung von X
    String textForPanelFooter(long timestamp) {
        String result= model.timestampToString(timestamp, "dd.MM.");
        switch (model.getIntervall()) {
        case "M" : {
            result= model.timestampToString(timestamp, "MM.YY.");
            break;
        }
        case "W" : {
            result= model.timestampToString(timestamp, "MM.YY.");
            break;
        }
        case "D" : {
            break;
        }
        default : {
            result= model.timestampToString(timestamp, "HH.mm");
            break;
        }
        }
        return result;
    }
    //Runden auf Vorgabe
    double round(double value) {
        double d = Math.pow(10, prefer.getPriceDecCScale());
        return Math.round(value * d) / d;
    }
}
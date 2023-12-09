package stocker.view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;

/**
 * 
 * Die Klasse <code>FrameChartSplashPanel</code> dient als zwischenzeitliche Anzeige während 
 * eine Kerzendarstellung oder Liniendarstellung erzeugt wird 
 *
 * @author Joachim Otto
 */


public class FrameChartSplashPanel extends  FrameChartContentPanel{
    /**
     * Das Panel wird initialisiert mit der 
     * @param y Höhe 
     * @param x Breite  
     * 
     */
    public FrameChartSplashPanel(int y, int x){
        setBorder(BorderFactory.createLineBorder(Color.black));
        setSize(y,x);
    }
    /**
     * Das Panel wird gezeichnet
     */

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //einigermmassen mittig plazieren
        int y=getHeight() / 2;
        int x=(getWidth()-g.getFontMetrics().stringWidth("Still waiting.....")) / 2;
        
        g.drawString("Still waiting.....", x , y);
        g.drawLine(getX()+10, getY()+10 , getX()+getWidth()-20, getY()+getHeight()-20);
        g.drawLine(getX()+getWidth()-20,getY()+10,getX()+10 , getY()+getHeight()-20 );
    }}
package stocker.controller;

import java.awt.event.ActionListener;

import javax.swing.Timer;
/**
 * 
 * Die Klasse <code>ColoredWLCellTimer</code>erweitert einen Timer um die
 * Möglichkeit eine Zahl zu speichern.
 *
 * @author Joachim Otto
 */

public class ColoredWLCellTimer extends Timer{
   int row;
  /**
   * Der Timer wird initialisiert mit  
   * @param count der Dauer 
   * @param al dem lauschenden ActionListener 
   * @param row der Zahl
   * 
   */ 
    public ColoredWLCellTimer (int count, ActionListener al, int row){
        super(count, al);
        this.row=row;
    }
    
    /**
     * Gibt die Zeile zurück für die dieser Timer läuft
     * @return Die Zeile
     */
    
    public int getRow() {
        return row;
    }
}

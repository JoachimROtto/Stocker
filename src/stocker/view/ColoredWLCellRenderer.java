package stocker.view;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import stocker.controller.ColoredWLCellTimer;
import stocker.model.FrameStockWatchlistModel;

/**
 * 
 * Die Klasse <code>ColoredWLCellRenderer</code> erweitert den <code>TableCellRenderer</code>
 * und ermöglicht die farbige Darstellung der Watchlist
 * 
 * @author Joachim Otto
 */

public class ColoredWLCellRenderer extends JLabel 
    implements TableCellRenderer, ActionListener {
    FrameWatchlist frame;
    ColoredWLCellTimer[] delayedReset = new ColoredWLCellTimer[20];
    Color[]stroboStatus;

    /**
     * Die Klasse wird mit der 
     * @param table überwachten Tabelle, 
     * @param frame der Watchlist 
     * initialisiert
     */

    public ColoredWLCellRenderer(JTable table, FrameWatchlist frame) {
        super();   
        this.frame=frame;
    }

    /**
     * Die Klasse wird initialisiert mit  
     * @param isBordered Umrandet?
     * 
     */
    
    public ColoredWLCellRenderer(boolean isBordered) {setOpaque(true);}
    
    /**
     * Der Renderer wird aufgerufen
     */
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
        
        String s = table.getValueAt(row, column).toString();
        setBackground(null);
        //Preise blitzen auf, Farbe=(neuer Preis<Vorheriger Preis) ->passiert im model
        stroboStatus=((FrameStockWatchlistModel) table.getModel()).getPriceColColor();
        if (column==2) {
            //Handlungsbedarf?  Das gibt das model vor, der Timer setzt dann in seinem Kontext zurück
            if (stroboStatus[row]!=Color.WHITE) {
                setBackground (stroboStatus[row]);
                delayedReset[row]= new ColoredWLCellTimer(250, this, row);
                delayedReset[row].setRepeats(false);
                delayedReset[row].start();
                }
            else {
                setBackground(Color.WHITE);
                }}
        
        // Prozente (dauerhaft gefärbt)
        // ((aktueller_Kurs - Eröffnungskurs_des_Tages) / Eröffnungskurs_des_Tages) * 100%
        //((aktueller_Kurs - Schlusskurs_des_Vortages) / Schlusskurs_des_Vortages) * 100%
        if (column==3) {
            if (Double.valueOf(s)>0) {
                setForeground (Color.GREEN);
                }
            else if (Double.valueOf(s)<0) {
                setForeground(Color.RED);
                }
            }
        else {
            setForeground(Color.BLACK);
            }
        if (isSelected) {
            setBackground(Color.lightGray);
            }
        setText(s);
        setOpaque(true);
        repaint();
        return this;
        }
    
    /**
     * Diese Funktion wird von <code>ColoredWLTimer</code>
     *  ausgelöst um die Farbe des Preises zurück zu setzten
     */
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //Nach Ablauf des Timers wird die Kurszell wieder weiss
        int row=((ColoredWLCellTimer) e.getSource()).getRow();
        stroboStatus=((FrameStockWatchlistModel) frame.table.getModel()).getPriceColColor();
        delayedReset[row].stop();
        stroboStatus[row]=Color.WHITE;
        frame.table.repaint();
    }
}
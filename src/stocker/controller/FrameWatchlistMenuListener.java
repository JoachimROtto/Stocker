package stocker.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import stocker.view.*;
import stocker.model.*;

/**
 * 
 * Der Listener <code>FrameWatchlistMenuListener</code> lauscht auf das Kontextmenu der Watchlist
 *
 * @author Joachim Otto
 */

public class FrameWatchlistMenuListener implements ActionListener {
    JTable table;
    FrameStockWatchlistModel model;
    StockerAppSettings sas;
    int source;
    
    /**
     * Der Listener wird initialisiert mit 
     * @param table überwachte Tabelle, 
     * @param model deren Model , 
     * @param sas den AppSettings und
     * @param source einem Modus  0 = Übernahme in Chart,1 = Löschen
     * 
     */
    
    public FrameWatchlistMenuListener (JTable table, FrameStockWatchlistModel model, StockerAppSettings sas, int source) {
        this.table=table;
        this.model=model;
        this.sas=sas;
        this.source= source;
    }
   
    /**
     * Einträge werden gelöscht oder in einen Chart übernommen
     */
   
    @Override
    public void actionPerformed (ActionEvent e) {
        
        if (source==1) {
            int rowSelected= table.getSelectedRow();
            if (rowSelected==-1) {
                JOptionPane.showConfirmDialog(null, "Kein Eintrag markiert!", "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            sas.removeStockID((String)table.getValueAt(rowSelected, 0),
                    (String)table.getValueAt(rowSelected, 1));
            model.removeValue((String)table.getValueAt(rowSelected, 0));
        }
        else {
            int rowSelected= table.getSelectedRow();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FrameChart frameChart =new FrameChart(sas,((String)table.getValueAt(rowSelected, 0)));
                    frameChart.setVisible(true);
                    frameChart.requestFocus();
                }
            }).start();
            }
          }
}

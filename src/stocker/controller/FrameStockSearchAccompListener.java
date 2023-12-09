package stocker.controller;

import javax.swing.*;

import stocker.model.StockerAppSettings;
import stocker.model.StockerPreferences;
import stocker.view.*;

import java.awt.event.*;

/**
 * 
 * Der Listener <code>FrameStockSearchAccompListener</code> lauscht auf bestätigte Suchergebnisse
 * im <code>FrameStockSearch</code>
 *
 * @author Joachim Otto
 */

public class FrameStockSearchAccompListener implements ActionListener, MouseListener {

    JTable assignedTable;
    StockerAppSettings sas;
    //Herkunft durchnummeriert (1=Doppelklick, 2=Kontextmenu WL, 3=KM Chart, 4=Doppelklick aber Aufruf aus WL)
    int nCase;
    StockerPreferences prefer;
    
    /**
     * Das Panel wird  mit der 
     * @param Source Table in  <code>FrameStockSearch</code>
     * @param sas den Applikationssettings 
     * @param nCase dem Verweis auf das ausgelöste Ereignis
     * 1=Doppelklick Tabelle, 2=Aufruf Watchliste aus dem Kontextmenu, 
     * 3 =Aufruf Chart aus dem Kontextmenu, 4=Suchaufruf aus der Watchlist heraus 
     * initialisiert
     */
    
    public FrameStockSearchAccompListener(JTable Source, StockerAppSettings sas, int nCase) {
        assignedTable=Source;
        this.sas=sas;
        this.nCase=nCase;
        prefer=sas.getPrefer();
    }
    /**
     * Ein Ereignis wurde ausgelöst
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        int rowSelected= assignedTable.getSelectedRow();
        if (rowSelected == -1) {
            JOptionPane.showConfirmDialog(null, "Keine Aktie ausgewählt.", "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
        }
        else {
        String stockID =  (String) assignedTable.getValueAt(rowSelected, 0);
        String stockName = (String) assignedTable.getValueAt(rowSelected, 1);

        if (nCase==1) {
            if (sas.getPrefer().getSearchToInd()==prefer.SI_WATCHLIST) {
                openWatchlist(stockID, stockName);
            }
            if(sas.getPrefer().getSearchToInd()==prefer.SI_CHART) {
                openChart( stockID);
            }
        }
        if (nCase==2) {
            openChart(stockID);
        }
        if (nCase==3) {
            openWatchlist(stockID, stockName);
        }
        if (nCase==4) {
            openWatchlist(stockID, stockName);
        }
    }}
    
    void openWatchlist(String stockID, String stockName) {
        FrameWatchlist watchlist = FrameWatchlist.getInstance (sas);
        JOptionPane.showConfirmDialog(null, "Aktie wird übernommen.", stockID + " " + stockName, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                watchlist.addStock(stockID, stockName);
            }
        }).start();

    }
    void openChart(String stockID) {
        FrameChart frameChart= new FrameChart(sas, stockID);
        frameChart.setVisible(true);
    }

    /**
     * Es erfolgte eine Doppelklick auf die Tabelle
     */

    //MouseListener, Adapterklasse finde ich keine
    @Override
    public void mouseClicked(MouseEvent e) {              
        if (e.getClickCount() == 2) {
            String stockID =  (String) assignedTable.getValueAt(assignedTable.getSelectedRow(), 0);
            String stockName = (String) assignedTable.getValueAt(assignedTable.getSelectedRow(), 1);
          
            if (nCase==4) {
                openWatchlist(stockID, stockName);
            }
            else {
                if (prefer.getSearchToInd()==prefer.SI_WATCHLIST) {
           openWatchlist(stockID, stockName);
       }
       if(prefer.getSearchToInd()==prefer.SI_CHART) {
           openChart(stockID);
       }
            }

    }}
    /**
     * wird nicht untersützt
     */
    @Override
    public void mousePressed(MouseEvent e) {}
    /**
     * wird nicht untersützt
     */
   @Override
    public void mouseReleased(MouseEvent e) {}
/**
 * wird nicht untersützt
 */
    @Override
    public void mouseEntered(MouseEvent e) {}
    /**
     * wird nicht untersützt
     */
    @Override
    public void mouseExited(MouseEvent e) {}
}

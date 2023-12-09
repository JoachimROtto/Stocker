package stocker.model;


import java.awt.Color;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import com.google.gson.JsonSyntaxException;

import stocker.Pushable;
import stocker.controller.StockControllCenter;
import stocker.model.databroker.*;

/**
 * 
 * Die Klasse <code>FrameStockWatchlistModel</code>bildet das Model der Watchlist ab
 * 
 * @author Joachim Otto
 */

public class FrameStockWatchlistModel extends AbstractTableModel implements Pushable {

    //Mit Beschränkung auf 20 modell umstellen? nein beim Löschen bleibt der Zauber mit replace also nur so mittelhilfreich   
   String[] columnNames = { "ID", "Name", "Kurs", "%" }; 
   String[][] stocks ;
   HashMap<String, Double> openings  = new HashMap<String, Double> ();
   int maxStocks;
   int currentStocks=0;
   DataBrokerPull localBrokerPull;
   JTable source;
   Color[] priceColColor; 
   StockerAppSettings sas;
   StockControllCenter SCC;
   
   /**
    * Das Model wird initialisiert mit  
    * @param sas den AppSettings
    * 
    */ 
   
public FrameStockWatchlistModel(StockerAppSettings sas) {
       super();
       this.sas=sas;
       SCC=sas.getSCC();
        localBrokerPull= new DataBrokerPull(sas);
        maxStocks=sas.getPrefer().MAX_STOCK_WL;
        priceColColor = new Color[maxStocks];
       }
/**
 * Liefert die Zeilenüberschriften der Tabelle
 * @return die Überschriften
 */
   @Override
   public String getColumnName (int col) 
   {
      return columnNames [col];
   }
   /**
    * Liefert die Spaltenzahl
    * @return Die Spaltenzahl
    */

   @Override
   public int getColumnCount() 
   {
      return columnNames.length;
   }

   /**
    * Liefert die Zeilenzahl
    * @return Die Zeilenzahl
    */

   @Override
   public int getRowCount() 
   {
      return (stocks!=null ? stocks.length : 0);
   }

   /**
    * Liefert eine bestimmte Zelle zurück
    * @param row Die Zeile
    * @param column Die Spalte
    * @return Der entsprechende Eintrag
    */    

   @Override
   public Object getValueAt (int row, int column) 
   {
      return stocks [row][column];
   }

   /**
    * Trägt eine neue Aktie ein
    * @param stockID Die Kennung der neuen Aktie
    * @param name Der Name der neuen Aktie
    */
   public void addValue(String stockID, String name) {
       addEnrichedValue(stockID, name);  
   }
   
   /**
    * Ein Eintrag wird hinzugefügt.
    * @param stockID Kennung der Aktie
    * @param name Name der Aktie
    * @param withOpening Soll der Eröffnungskurs dabei ermittelt werden?
    */
          
   public void addValue(String stockID, String name, boolean withOpening) {
       if (withOpening) {
           addValue(stockID, name);
       }
       else {
           if (currentStocks>=maxStocks) {
               JOptionPane.showConfirmDialog(null, "Nur " + maxStocks + " Einträge zulässig!", "Achtung", 
                       JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
               return;
           }
           openings.put(stockID, 0.0);
           String[] Value = {stockID, name, "0.0", "0.0"};
           addValues (Value, false);           
       }}
   /**
    * Ein Eintrag wird hinzugefügt, der Eröffnungskurs wird dabei ermittelt
    * @param stockID Kennung der Aktie
    * @param name Name der Aktie
    */
   public void addEnrichedValue(String stockID, String name) {
       try {
        if (localBrokerPull.stockLookup(stockID)==null) {
            JOptionPane.showConfirmDialog(null, "Aktie nicht mehr verfügbar", "Achtung", JOptionPane.DEFAULT_OPTION, 
                    JOptionPane.WARNING_MESSAGE);
            return;
            }
    } catch (JsonSyntaxException | IOException | DataBrokerException e1) {
        JOptionPane.showConfirmDialog(null, e1.getMessage() , "Achtung", JOptionPane.DEFAULT_OPTION, 
                JOptionPane.WARNING_MESSAGE);
        return;
  }
       
       if (currentStocks>=maxStocks) {
           JOptionPane.showConfirmDialog(null, "Nur " + maxStocks + " Einträge zulässig!", "Achtung", JOptionPane.DEFAULT_OPTION, 
                   JOptionPane.WARNING_MESSAGE);
           return;
       }
    
       double opening=0.0;
       try {
               opening=getOpening(stockID);
           openings.put(stockID, opening);
       }
       catch(Exception e) {
           if (e.getMessage().contains("Premium")) {
               JOptionPane.showConfirmDialog(null, "Premium-Account erforderlich!", name, JOptionPane.DEFAULT_OPTION, 
                       JOptionPane.WARNING_MESSAGE);
               return;               
           }
           JOptionPane.showConfirmDialog(null, "Kein Eröffnungskurs verfügbar", name, JOptionPane.DEFAULT_OPTION, 
                   JOptionPane.WARNING_MESSAGE);
           openings.put(stockID, 0.0);
       }
       try {
       SCC.putSubscriptionFromWL(stockID);
       double cPrice=localBrokerPull.getCurrPrice(stockID);
       String[] Value = {stockID, name, Double.toString(cPrice), Double.toString(100*(cPrice-opening)/opening)};
       addValues (Value);
       }
       catch (Exception e) {
           JOptionPane.showConfirmDialog(null, e.getMessage(), "Achtung", JOptionPane.DEFAULT_OPTION, 
                   JOptionPane.WARNING_MESSAGE);
       }
   }
   
   /**
    * Die Einträge werden ausgetauscht
    * @param newData Bis zu 20 Einträge bestehend aus Kennung, Name, aktuellem Kurs und Änderung
    * @param withWarning Sollen Warnungen zu Dubletten oder Nichtverfügbarkeit ausgegeben werden
    */
   
    void replaceValues(String[][] newData) {
       stocks = newData;
       fireTableDataChanged();  
   }

    /**
     * Ein Eintrag wird erstellt
     * @param newData Der Eintrag bestehend aus Kennung und Name
     */
    
    void addValues(String[] newData) {
        addValues(newData, true);
    }
    
    /**
     * Ein Eintrag wird erstellt
     * @param newData Der Eintrag bestehend aus Kennung und Name
     * @param withWarning Sollen Warnungen zu Dubletten oder Nichtverfügbarkeit ausgegeben werden
     */
    
    void addValues(String[] newData, boolean withWarning) {
       String [][] tmpStocks ;
       if (stocks!=null) {
           if (indexInStocklist(newData[0])!=-1) {
               if (withWarning) {
                   JOptionPane.showConfirmDialog(null, "Aktie bereits vorhanden!", "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
               }
               return;
           }
       tmpStocks = new String[stocks.length+1][newData.length];
       int i=0;
       for (String [] stock: stocks) {
           tmpStocks[i]=stock;
           i++;
       }
       tmpStocks[i]=newData;
       }
       else {
           tmpStocks=new String [1][newData.length];
           tmpStocks[0]=newData;
       }
       sas.addStockID(newData[0], newData[1]);
       replaceValues(tmpStocks);
       currentStocks++;
   }
    
    /**
     * Ein Eintrag wird gelöscht
     * @param stockID Die Kennung der entsprechenden Aktie
     */
   
   public void removeValue(String stockID) {
       if (stocks==null || stocks.length==0) {
           return;
       }
       String [][] tmpStocks = new String[stocks.length-1][stocks[0].length];
       int i=0;
       boolean found=false;
       for (String [] stock: stocks) {           
           if (stock[0].compareTo(stockID)!=0) {
               tmpStocks[i]=stock;     
               i++;
           }
           else {
               found=true;
           }
           if (i==tmpStocks.length && !found) {
               //Eintrag bisher nicht gefunden: Workaround für letztes Element
               if (stocks[i][0].compareTo(stockID)!=0) {
                   //Existiert nicht
                   return;
               }
               else {
                   //war letzter Eintrag, alles gut
               }
           }
       }
       replaceValues(tmpStocks);
       currentStocks=tmpStocks.length;
       if (SCC!=null) {
           SCC.putCancelationFromWL(stockID);
       }
   }
   
   /**
    * Die Watchlist wird geleert
    */
   
   public void clearModel() {
       stocks=new String[0][4]; 
   }

   /**
    * Das Modell erhält Handelsdaten um die Einträge zu aktualisieren
    * @param trade Das neue Handelsdatum 
    */
   
   @Override
   public void receivePushNotificaton(Trade trade){
       int i;
           i= indexInStocklist(trade.Symbol);
           if (i!=-1) {
               //((aktueller_Kurs - Eröffnungskurs_des_Tages) / Eröffnungskurs_des_Tages) * 100%
               //((aktueller_Kurs - Schlusskurs_des_Vortages) / Schlusskurs_des_Vortages) * 100%
               priceColColor[i]=Color.WHITE;
               if (Double.valueOf(stocks[i][2]) < trade.price) {
                   priceColColor[i]=Color.GREEN;
                   }
               if (Double.valueOf(stocks[i][2]) > trade.price) {
                   priceColColor[i]=Color.RED;
                   }
               stocks[i][3]=Double.toString(round 
                       (100*(trade.price - openings.get(trade.Symbol)) / openings.get(trade.Symbol)));
               //NaN bei % abfangen
               if (openings.get(trade.Symbol)==0.0) {
                   stocks[i][3]="0";
                   }
               //Jetztkurs
               stocks[i][2]=Double.toString(round(trade.price));
           }
           //Markierte Zeilen sichern und wiederherstellen
           int rowSelected = source.getSelectedRow();
           fireTableDataChanged();
           if (rowSelected!=-1) {
               source.addRowSelectionInterval(rowSelected, rowSelected);               
           }
   }
   
   /**
    * Gibt den den Startkurs für einen Eintrag in die Watchlist ein. Meist der letze Schlusskurs 
    * oder der heutige Eröffnungskurs.
    * @param stockID
    * @return
    * @throws JsonSyntaxException
    * @throws IOException
    * @throws DataBrokerException
    */
   double getOpening(String stockID) throws JsonSyntaxException, IOException, DataBrokerException {
       return localBrokerPull.getOpening(stockID);
   }
   
   /**
    * Liefert eine Liste der Farbvorgaben der Preisspalte
    * @return Die Farbvorgaben
    */
   
   public Color[] getPriceColColor() {
       return priceColColor; 
   }
   
   /**
    * Verknüpft das Modell mit einer Tabelle
    * @param source Eine Watchlisttabelle
    */
   
   public void setTable(JTable source) {
       this.source=source;
   }
   /**
    * Gibt die Begrenzung der Anzahl Einträge zurück
    * @return Die maximale Anzahl Einträge in der Watchlist
    */
   
   public int getMaxStocks() {
       return maxStocks;
   }
   
   /**
    * Gibt die Einträge der Watchlist als Array Kennunmer/Name aus
    * @return Die Einträge der Watchlist
    */
   
   public String[][] getStocksAsArray() {
       String [][] result = new String [currentStocks][2];
       if (stocks==null) {
           return null;
           }
       int i=0;
       for (String [] stock: stocks) {
           result[i]= stock;
           i++;
       }
       return result;
   }

   int indexInStocklist(String stockID) {
       //falls der erste Push schneller als die erste Eintragung ist
       if (stocks==null) {return -1;}
       for (int i=0;i <stocks.length;i++){           
           if (stocks[i][0].compareTo(stockID)==0) {
               return i;
               }}
       return -1;
   }
   
   double round(double value) {
       double d = Math.pow(10, sas.getPrefer().getPriceDecWL());
       return Math.round(value * d) / d;
    }
   
   /**
    * Das Modell wird über Änderungen der Voreinstellungen benachrichtigt 
    * @param databrokerChanged Wurde der Provider gewechselt?
    */
   
   public void receivePrefChangeNotification(boolean databrokerChanged) {
       if (databrokerChanged) {
      //Broker neu beziehen für den Fall des Wechsels
      localBrokerPull= new DataBrokerPull(sas);
       }
   }
}
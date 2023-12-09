package stocker.model;

import javax.swing.table.AbstractTableModel;
/**
 * 
 * Die Klasse <code>FrameStockSearchModel</code> ist das Model der Ergebnisliste
 * im Suchfenster
 *
 * @author Joachim Otto
 */

public class FrameStockSearchModel extends AbstractTableModel {


   String[] columnNames = { "ID", "Name" }; 
   String[][] stocks ;
   /**
    * Gibt eine Zeilenüberschrift zuück
    *
    * @return die Zeilenüberschrift
    * @param col die entsprechende Spalte
    */
   @Override
   public String getColumnName (int col) 
   {
      return columnNames [col];
   }
   
   /**
    * Gibt die Spaltenzahl zurück.
    *
    * @return die Spaltenzahl
    */
   
   @Override
   public int getColumnCount() 
   {
      return columnNames.length;
   }

   /**
    * Gibt die Zeilenzahl zurück.
    *
    * @return die Zeilenzahl
    */
   
   @Override
   public int getRowCount() 
   {
      return (stocks!=null ? stocks.length : 0);
   }

   /**
    * Gibt eine Zelle zurück
    *
    * @return der Zelleninhalt
    * @param row die entsprechende Zeile
    * @param column die entsorprechende Spalte  
    */

   @Override
   public Object getValueAt (int row, int column) 
   {
      return stocks [row][column];
   }
   
   /**
    * Ersetzt die Einträge
    *
    * @param newData Die neuen Einträge   
    */
   
   public void replaceValues(String[][] newData) {
       stocks = newData;
       fireTableDataChanged();  
   }
}

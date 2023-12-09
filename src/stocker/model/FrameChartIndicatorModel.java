package stocker.model;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 * 
 * Die Klasse <code>FrameChartIndicatorModel</code>bildet das Model zum Erfassungsdialog 
 * für Indikatoren ab
 * 
 * @author Joachim Otto
 */

public class FrameChartIndicatorModel extends AbstractTableModel {
    String[] columnNames = { "Eintrag"}; 
    ChartIndicator[] indicators;

    /**
     * Das Model wird initialisiert mit  
     * @param indicators den bisherigen Indikatoren
     * 
     */ 

    public FrameChartIndicatorModel(ChartIndicator[] indicators) {
        this.indicators=indicators;
    }
    /**
     * Liefert eine Zeilenüberschrift der Tabelle
     * @return Die Überschrift
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
        return (indicators!=null ? indicators.length : 0);
    }

    /**
     * Liefert die aktuellen Indikatoren
     * @return Die Indikatoren
     */

    public ChartIndicator[] getValues() {
        return indicators;
    }

    /**
     * Liefert einen Eintrag als Indikator zurück
     * @param row Die Zeile
     * @param column Die Spalte
     * @return Der entsprechende Eintrag
     */

    public ChartIndicator getRealValueAt(int row, int column) {
        return indicators [row]; 
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
        return indicators [row].getLabel();
    }

    /**
     * Fügt einen neuen Indikator hinzu
     * @param indicator Der neue Indikator
     */

    public void addValue(ChartIndicator indicator) {
        ChartIndicator[] tmpChartIndicator;
        if (indicators!=null) {
            if (indexInIndicatorList(indicator)!=-1) {
                JOptionPane.showConfirmDialog(null, "Indikator bereits vorhanden!", "Achtung", 
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
                return;
            }
            tmpChartIndicator = new ChartIndicator[indicators.length+1];
            int i=0;
            for (ChartIndicator locIndi: indicators) {
                tmpChartIndicator[i]=locIndi;
                i++;
            }
            tmpChartIndicator[i]=indicator;
        }
        else {
            tmpChartIndicator=new ChartIndicator [1];
            tmpChartIndicator[0]=indicator;
        }
        replaceValues(tmpChartIndicator);
    }

    /**
     * Tauscht die Indikatoren aus
     * @param newData die neuen Indikatoren
     */

    public void replaceValues(ChartIndicator[] newData) {
        indicators = newData;
        fireTableDataChanged();  
    }

    /**
     * Entfernt einen Indikator
     * @param indicator Der zu entfernende Indikator
     */

    public void removeValue(ChartIndicator indicator) {
        ChartIndicator[] tmpIndi= new ChartIndicator[indicators.length-1];
        int i=0;
        for (ChartIndicator locIndi: indicators) {           
            if (locIndi.getLabel().compareTo(indicator.getLabel().toString()) != 0) {
                tmpIndi[i]=locIndi;               
                i++;
            }
        }
        replaceValues(tmpIndi);
    }

    int indexInIndicatorList(ChartIndicator data) {
        int i=0;
        //Noch keine Indikatoren vorhanden
        if (indicators ==null) {return -1;}
        for (ChartIndicator tmpIndi: indicators) {   
            //da im Label der Indikator einheitlich spezifiziert wird, sollte das für Gleicheit reichen
            if (data.getLabel().compareTo(tmpIndi.getLabel().toString())==0) {
                return i;}
            i++;}
        return -1;}}
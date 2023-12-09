package stocker.controller;

import javax.swing.*;

import stocker.model.databroker.*;
import stocker.model.*;

import java.awt.event.*;
/**
 * 
 * Der Listener <code>FrameStockSearchListener</code> lauscht auf den Button und eine Return-Eingabe
 * im Suchfenster
 *
 * @author Joachim Otto
 */
public class FrameStockSearchListener implements ActionListener {

    private JTextField searchTermSource;
    private FrameStockSearchModel resultmodel;
    private StockerAppSettings sas;
  
    /**
     * Der Listener wird initialisiert mit der 
     * @param searchTermSource überwachtes Textfeld 
     * @param resultmodel das Model des Suchfensters, 
     * @param sas den AppSettings 
     * 
     */
  
    public FrameStockSearchListener (JTextField searchTermSource, FrameStockSearchModel resultmodel, StockerAppSettings sas){
        this.searchTermSource= searchTermSource;
        this.resultmodel=resultmodel;
        this.sas=sas;
    }
    /**
     * Eine Suche wurde ausgelöst
     */
    @Override
    public void actionPerformed (ActionEvent actionEvent) {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                search(searchTermSource.getText());
            }});
          }
    void search(String searchString) {
      //  JOptionPane.showConfirmDialog(null, "Die Suche beginnt", "Etwas Geduld!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);        
        try {
            JSONResultStock result = (new DataBrokerPull(sas)).getAktieFromJSON(searchString);
            String resultForModel [][]= new String [result.getResult().length][2];
            if (result.getCount()==0) {
                JOptionPane.showConfirmDialog(null, "Keine Aktie gefunden", "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
            }
            int i=0;
            for (Stock Stock : result.getResult()) {
                resultForModel[i][0]=Stock.getStockID();
                resultForModel[i][1]=Stock.getDescription();
                i++;
            }
            resultmodel.replaceValues(resultForModel);            
        } 
        catch (Exception e) {
            JOptionPane.showConfirmDialog(null, "Exception in StockSearchListener: \n" + e.getMessage(), "Achtung!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
        }}}
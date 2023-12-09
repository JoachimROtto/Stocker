package test;

import java.util.Set;

import stocker.IStockerTester;
import stocker.controller.StockControllCenter;
import stocker.model.*;

/**
 * 
 * Die Klasse <code>StockerTest_nnn_zzz</code> ist meine Implementierung
 * der Testklasse
 * 
 * @author Joachim Otto
 */
public class StockerTest_nnn_zzz implements IStockerTester{
    final String matrNr="zzz";
    final String name="Joachim Otto";
    final String eMail="nnn";

    StockerPreferences prefer= new StockerPreferences();
    StockerAppSettings sas= new StockerAppSettings(prefer);
    FrameStockWatchlistModel wlModel = new FrameStockWatchlistModel(sas);
    StockControllCenter SCC=  StockControllCenter.getInstance(sas);
    
    @Override
    public String getMatrNr() {
        return matrNr;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return eMail;
    }

    @Override
    public void clearWatchlist() {
        wlModel.clearModel();
    }

    @Override
    public void addWatchlistEntry(String stockId) {
        wlModel.addValue(stockId, stockId, false);
    }

    @Override
    public void removeWatchlistEntry(String stockId) {
        wlModel.removeValue(stockId);
    }

    @Override
    public String[] getWatchlistStockIds() {
        String [][] tmpStocks= wlModel.getStocksAsArray();
        String [] result= new String[tmpStocks.length];
        for (int i=0; i<result.length; i++) {
            result[i]= tmpStocks[i][1];
        }
        return result;
    }

    @Override
    public void clearAlarms(String stockId) {
        SCC.clearAlarms(stockId);
    }

    @Override
    public void clearAllAlarms() {
        SCC.cancelAllAlarms();
    }

    @Override
    public void addAlarm(String stockId, double threshold) {
        SCC.putNewAlarm(stockId, threshold);
    }

    @Override
    public void removeAlarm(String stockId, double threshold) {
        SCC.cancelAlarm(stockId, threshold);
    }

    @Override
    public double[] getAlarms(String stockId) {
        return SCC.getAlarmsAsdoubleArray(stockId);
    }
    
    @Override
    public Set<String> getAlarmStockIds() {
        return SCC.getAlarmStockIds();
    }

    @Override
    public double[] getMovingAverage(int n, double[] stockData) {
        MovingAverage tmpItem=new MovingAverage();
        return tmpItem.getMovingAverage(n, stockData);
        }

    @Override
    public double[] getUpperBollingerBand(double f, int n, double[] stockData) {
        BollingerBand tmpItem  = new BollingerBand(f,n, stockData);
        return tmpItem.getCourse()[0];
    }

    @Override
    public double[] getLowerBollingerBand(double f, int n, double[] stockData) {
        BollingerBand tmpItem  = new BollingerBand(f,n, stockData);
        return tmpItem.getCourse()[1];
    }


}

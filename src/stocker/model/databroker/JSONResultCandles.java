package stocker.model.databroker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JOptionPane;

import com.google.gson.annotations.SerializedName;

import stocker.controller.StockerException;
import stocker.model.*;
/**
 * 
 * Die Klasse <code>JSONResultCandles</code> bildet die Kerzenrückgabe durch den Provider ab
 * 
 * @author Joachim Otto
 */

public class JSONResultCandles {
    @SerializedName("c")   double[] closing; 
    @SerializedName("h")  double[] high; 
    @SerializedName("l")  double[] low; 
    @SerializedName("o")  double[] opening; 
    @SerializedName("s") String status;
    @SerializedName("t")  long[] timestamp;
    @SerializedName("v")  int[] volume; 

    /**
     * Gibt alle Kerzen zurück
     * @return Alle Kerzen
     * @throws StockerException Bei Anwendungsproblemen
     */
    public Candle[] getAsCandleArray() throws StockerException {
        long count=closing.length;
        return getAsCandleArray(count);
    }

    /**
     * Beschränkt die Kerzenrückgabe auf eine Höchstzahl und informiert
     *  wenn diese unterschritten wird
     * @param candleCount Die Höchstzahl Kerzen
     * @return Die Kerzen
     * @throws StockerException Bei Anwendungsproblemen
     */

    public Candle[] getAsCandleArray(long candleCount) throws StockerException{

        int offset = closing.length- (int) candleCount;
        if (offset<0) {
            for (int i=0; i<closing.length;i++) {
            }
            JOptionPane.showConfirmDialog(null, "Es konnten nicht genug Kerzen ermittelt werden: " + closing.length 
                    +" statt "+candleCount, "Achtung", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
        }
        int locCCount = ((int)candleCount>closing.length ?  closing.length : (int)candleCount);
        offset=(offset>0?offset:0);
        Candle[] result = new Candle[locCCount];
        for (int i=0; i<locCCount;i++) {
            result[i] = getCandleAtIndex(offset+ i);
            //Rückversicherung: die Kerzen sind sortiert
            if (i!=0) {
                if (result[i].getTimestamp()<result[i-1].getTimestamp()) {
                    throw new StockerException("Kerzen nicht sortiert!");                
                }}
        }
        return result;
    }

    Candle getCandleAtIndex(int i) {
        Candle result=new Candle();
        result.setClosing(closing[i]);
        result.setHigh(high[i]);
        result.setLow(low[i]);
        result.setOpening(opening[i]);
        result.setTimestamp(timestamp[i]);
        result.setDateBegin(timestampToDateString(i));
        result.setVolume(volume[i]);
        return result;
    }

    String timestampToDateString(int i) {
        Date date = new Date(timestamp[i]*1000L); 
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        df.setTimeZone(TimeZone.getDefault());
        String result = df.format(date);
        return result;
    }
}

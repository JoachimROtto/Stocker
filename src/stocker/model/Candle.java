package stocker.model;
/**
 * 
 * Die Klasse <code>Candle </code> bildet die Rückgabe
 * der Kerzenabfrage an
 *  
 * @author Joachim Otto
 *
 */
public class Candle {
	 double closing; 
	 double high; 
	 double low; 
	 double opening; 
	 String status;
	 long timestamp;
     String dateBegin; //wegen w,m
	 int volume;
	
	 /*
	 * getter
	 */
	 
	/**
	 * gibt den Höchstkurs an
	 * @return der Höchstkurs
	 */
    public double getClosing() {
        return closing;
    }
    /**
     * gibt den Höchstkurs an
     * @return der Höchstkurs
     */
    public double getHigh() {
        return high;
    }
    /**
     * gibt den Tiefstkurs an
     * @return der Tiefstkurs
     */
    public double getLow() {
        return low;
    }
    /**
     * gibt den Eröffnungskurs an
     * @return der Eröffnungskurs
     */
    public double getOpening() {
        return opening;
    }
    /**
     * gibt den Status an
     * @return der Status
     */
    public String getStatus() {
        return status;
    }
    /**
     * gibt das Datum als Timestamp an
     * @return das Datum
     */
    public long getTimestamp() {
        return timestamp;
    }
    /**
     * gibt das Datum an
     * @return das Datum
     */
    public String getDateBegin() {
        return dateBegin;
    }
    /**
     * gibt das Volumen an
     * @return das Volumen
     */
    
    public int getVolume() {
        return volume;
    }
    
    /*
     * setter
     */
    
    /**
     * Setzt den Schlußtkurs 
     * @param closing der Schlußkurs
     */
   
    public void setClosing(double closing) {
        this.closing = closing;
    }

    /**
     * Setzt den Höchstkurs 
     * @param high der Höchstkurs
     */
   public void setHigh(double high) {
        this.high = high;
    }

   /**
    * Setzt den Tiefstkurs 
    * @param low der Tiefsstkurs
    */
    public void setLow(double low) {
        this.low = low;
    }

    /**
     * Setzt den Eröffnungskurs 
     * @param opening der Eröffnungskurs
     */
    public void setOpening(double opening) {
        this.opening = opening;
    }

    /**
     * Setzt den Status 
     * @param status der Status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Setzt den Timestamp 
     * @param timestamp der Timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Setzt das Startdatum 
     * @param dateBegin das Startdatum
     */
    public void setDateBegin(String dateBegin) {
        this.dateBegin = dateBegin;
    }

    /**
     * Setzt das Volumen 
     * @param volume das Volumen
     */
    public void setVolume(int volume) {
        this.volume = volume;
    } 
    
}
package stocker.model;

import com.google.gson.annotations.SerializedName;
/**
 * 
 * Die Klasse <code>Trade</code> bildet ein einzelnes, vom Pushdienst
 * geliefertes Handelsdatum ab
 *
 * @author Joachim Otto
 */

public class Trade{
        @SerializedName("c") String[] condition; 
        @SerializedName("p") double price; 
        @SerializedName("s") String Symbol; 
        @SerializedName("t") long timestamp; 
        @SerializedName("v") long volume;
        
        /**
         * Gibt die Handelsart zurück
         * @return Die Handelsart
         */
        
        public String[] getCondition() {
            return condition;
        }
        
        /**
         * Gibt den Preis zurück
         * @return Der Preis
         */
        
        public double getPrice() {
            return price;
        }
        
        /**
         * Setzt den Preis
         * @param price Der Preis
         */
        
        public void setPrice(double price) {
            this.price = price;
        }
        
        /**
         * Gibt die Aktienkennung zurück
         * @return Die Aktienkennung
         */
        
        public String getSymbol() {
            return Symbol;
        }}
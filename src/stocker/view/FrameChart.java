package stocker.view;

import javax.swing.*;
import javax.swing.event.*;

import com.google.gson.JsonSyntaxException;

import stocker.PrefChangeListeners;
import stocker.controller.*;
import stocker.model.*;
import stocker.model.databroker.DataBrokerException;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * 
 * Die Klasse <code>FrameChart</code> bildet die Chartdarstellung ab. Der eigentliche
 * Inhalt wird in <code>FrameChartContentPanel</code> und Subklassen dargestellt.
 * 
 * @author Joachim Otto
 */

public class FrameChart extends JInternalFrame implements PrefChangeListeners {
    boolean isSplashScreen;
    StockerAppSettings sas ;
    StockerPreferences prefer;
    FrameChartModel model;
    FrameChartContentPanel contentPanel;
    FrameChartOptions frameOptions= new FrameChartOptions();
    FrameChart locFrame=this;
    JLabel Date;
    JLabel chPrice;
    JLabel tradePrice;
    JMenuItem annotMenu;

    /**
     * Die Klasse wird initialisiert mit  
     * @param sas Den Programmeinstellungen
     * @param savedChart Einem persistierten Zustand
     * 
     */ 
    public FrameChart(StockerAppSettings sas, FrameChartOptions savedChart) {
        //Aufbau mit Standardvorgaben
        this(sas, savedChart.getStockID());
        //Aktie wegen Providerwechsel nicht mehr vorhanden
        boolean isSupported=false;
        try {
            isSupported=sas.getSCC().stockIsSupported(savedChart.getStockID());
        } catch (JsonSyntaxException | IOException | DataBrokerException e) {}

        if (model==null || !isSupported) {
            return;
        }
        //teilweise überschreiben mit den gesicherten Vorgaben
        if (savedChart.getAlarms()!=null) {
            model.replaceAlarm(savedChart.getAlarms());
        }
        setBounds(savedChart.getOffsetX(), savedChart.getOffsetY(), 
                savedChart.getFrameSize().width, savedChart.getFrameSize().height);
        setMinimumSize(sas.getPrefer().getMinChartFrame());
        setTitle(savedChart.getTitle());
        model.setCharttyp(savedChart.getChartType());
        model.setIntervallTyp(savedChart.getChartIntervall());
        sas.getSCC().setLastPrice(savedChart.getStockID(), savedChart.getLastPriceForAlarm());
        sas.getSCC().checkForAlarmOnStart(savedChart.getStockID());

        model.populatemodel();
        model.firemodelDataChanged();

        //muss am Ende überschrieben werden, um eine Berechnung zu ermöglichen
        model.clearIndicator();
        //Persitierte Indikatoren beziehen. Die werden bei der Initialisierung berechnet
        if (savedChart.getIndicator(model, prefer)!=null) {
            model.replaceIndicator(savedChart.getIndicator(model, prefer));
        }
    }


    /**
     * Die Klasse wird initialisiert mit  
     * @param sas Den Programmeinstellungen
     * @param stockID Der Kennung einer darzustellenden Aktie
     * 
     */ 

    public FrameChart(StockerAppSettings sas, String stockID) {
        super ("Chartfenster " + stockID, true, true, true, true);
        this.sas=sas;
        prefer=sas.getPrefer();
        //Versuch das Model aufzubauen
        try {
            model=new FrameChartModel(sas, stockID, this);
        } catch (Exception e1) {
            JOptionPane.showConfirmDialog(null, e1.getMessage(), "Nachricht vom Datenprovider!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        setJMenuBar(setMenuBar());
        setTitle(model.getFrameTitle());

        //aktuell gesetzte Alarm und Indikatoren beziehen
        if (sas.getSCC().getAlarmsAsArray(stockID)!=null) {
            model.replaceAlarm(sas.getSCC().getAlarmsAsArray(stockID));
        }
        FrameChartSplashPanel waitPanel = new FrameChartSplashPanel(getHeight(), getWidth());
        add(waitPanel);
        repaint();
        //Abbruch wenn model kaputt ist, weil z.B. ein neuer Provider nicht liefert
        if (model.getCandles()==null) {
            dispose();
            return;
        }

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridBagLayout());
        statusPanel.setBackground(Color.WHITE);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.insets = new Insets (0, 5, 0, 5);
        c.ipadx = 20;

        Date = new JLabel (model.getStockID());
        c.gridx = 0;
        statusPanel.add (Date, c);         

        Date = new JLabel ("Position:");
        c.gridx = 1;
        statusPanel.add (Date, c);         
        chPrice = new JLabel ("");
        c.gridx = 2;
        statusPanel.add (chPrice, c);         
        tradePrice = new JLabel ("Kurs: ");
        c.gridx = 4;
        statusPanel.add (tradePrice, c);         
        add(statusPanel, BorderLayout.PAGE_END); 

        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                contentPanel=new FrameChartSplashPanel(getHeight(), getWidth());
                if (model.getChartTyp()==sas.getPrefer().CHARTTYP_CANDLE) {
                    contentPanel=  new FrameChartCandlePanel(getSize(), model, locFrame, prefer);            
                }
                else {
                    contentPanel= new FrameChartLinePanel(getSize(), model, locFrame, prefer);
                }
                remove(waitPanel);
                add(contentPanel);
                locFrame.repaint();
                locFrame.revalidate();
            }});

        this.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                sas.getFrame().removeFromWindowList(locFrame);
                model.dispose();
                dispose();
            }
        });

        setSize(sas.getsizeChartFrame());
        setMinimumSize(sas.getPrefer().getMinChartFrame());
        if (getMinimumSize().height>getSize().height || 
                getMinimumSize().width>getSize().width ) {
            setSize(getMinimumSize());
        }
        sas.getDesktop().add (this);
        sas.getFrame().add2WindowList(this);
        show();
        requestFocus();
    }


    //Ausgelagerter Menuaufbau
    JMenuBar setMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Menu aufbauen
        JMenu chartMenu = new JMenu("Charttyp");
        chartMenu.setMnemonic('c');

        JMenuItem candleItem = new JMenuItem("Kerzen");
        candleItem.setMnemonic('k');
        candleItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK));
        candleItem.addActionListener(new FrameChartviewMenuListener(model, 1,prefer)) ;
        chartMenu.add(candleItem);        

        JMenuItem lineItem = new JMenuItem("Linie");
        lineItem.setMnemonic('i');
        lineItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        lineItem.addActionListener(new FrameChartviewMenuListener(model, 2,prefer)) ;
        chartMenu.add(lineItem);
        menuBar.add(chartMenu);

        JMenu intervallMenu = new JMenu("Intervall");
        intervallMenu.setMnemonic('i');
        String[] label = prefer.getChartIntervalLabels();
        int i=0; 
        JMenuItem interItem;
        for (String tmp : label) {
            interItem= new JMenuItem(tmp);
            interItem.setAccelerator(KeyStroke.getKeyStroke(i + 49, ActionEvent.CTRL_MASK));
            interItem.addActionListener(new FrameChartviewMenuListener(model, i +100,prefer)) ;
            intervallMenu.add(interItem);          
            i++;
        }
        menuBar.add(intervallMenu);

        JMenu indikatorMenu = new JMenu("Indikatoren");
        indikatorMenu.setMnemonic('k');
        JMenuItem indi1Item = new JMenuItem("Indikatoren erfassen");
        indi1Item.setMnemonic('n');
        indi1Item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        indi1Item.addActionListener(new FrameChartviewMenuListener(model, 3,prefer)) ;
        indikatorMenu.add(indi1Item);
        menuBar.add(indikatorMenu);

        JMenu alarmMenu = new JMenu("Alarme");
        alarmMenu.setMnemonic('a');
        JMenuItem alarm1Item = new JMenuItem("Alarme setzen");
        alarm1Item.setMnemonic('k');
        alarm1Item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        alarm1Item.addActionListener(new FrameChartviewMenuListener(model, 4,prefer)) ;
        alarmMenu.add(alarm1Item);
        menuBar.add(alarmMenu);        

        return menuBar;

    }

    //Bildung der Fusszeile
    void putToFooter(String date, double price) {
        Date.setText("Position: " + date);
        chPrice.setText(String.valueOf(price));
    }

    public void refreshFooter() {
        if (model==null) {return;}
        tradePrice.setText("Kurs :" + String.valueOf(round(model.getLastPrice())));
    }

    /**
     * Ersetzt das ContentPanel
     * @param fccp Ein neues ContenPanel
     */

    public void setContenPanel(FrameChartContentPanel fccp) {
        contentPanel=fccp;
    }

    /**
     * Der Chart wird über geänderte Daten informiert
     */
    public void receiveUpdate() {
        this.setTitle(model.getFrameTitle());
        annotMenu.setText(model.getFrameTitle());
        if (contentPanel != null) {
            remove (contentPanel);    
        }
        FrameChartSplashPanel waitPanel=new FrameChartSplashPanel(getHeight(), getWidth());
        add (waitPanel);
        repaint();
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                contentPanel=new FrameChartSplashPanel(getHeight(), getWidth());
                if (model.getChartTyp()==sas.getPrefer().CHARTTYP_CANDLE) {
                    contentPanel=  new FrameChartCandlePanel(getSize(), model, locFrame, prefer);            
                }
                else {
                    contentPanel= new FrameChartLinePanel(getSize(), model, locFrame, prefer);
                }
                remove(waitPanel);
                add(contentPanel);
                locFrame.repaint();
                locFrame.revalidate();
            }});
        setMinimumSize(sas.getPrefer().getMinChartFrame());
    }

    /**
     * Neue Indikatoren werden übergeben
     * @param indicator Die Indikatoren
     */

    public void receiveIndicatorSettings(ChartIndicator[] indicator) {
        model.replaceIndicator(indicator);
        receiveUpdate();
    }

    /**
     * Gibt das zugrunde liegende Modell zurück
     * @return Das Modell
     */

    public FrameChartModel getModel() {
        return model;
    }

    /**
     * Der zugewiesene Eintrag im Fenstermenu wird bekanntgegeben
     * @param annotMenu Der Eintrag
     */

    public void setAnnotMenu(JMenuItem annotMenu) {
        this.annotMenu=annotMenu;
    }

    /**
     * Neue Alarme werden übergeben
     * @param alarms Die Alarme
     */

    public void receiveAlarmSettings(Double[] alarms) {
        model.replaceAlarm(alarms);
        receiveUpdate();
    }

    /**
     * Der Chart wird über die Änderung der Voreinstellungen informiert
     * @param databrokerChanged Hat sich der Datenprovider geändert?
     */

    @Override
    public void notifyPrefChanged(boolean databrokerChanged) {
        setMinimumSize(prefer.getMinChartFrame());
        if (getMinimumSize().height>getSize().height || 
                getMinimumSize().width>getSize().width ) {
            setSize(getMinimumSize());
        }
        repaint();
        //Mehr gibts nicht, alles andere sind Vorgaben, die hier schon bezogen
        //und evtl. geändert sind

    }

    /**
     * Die Eigenschaften des Charts werden zur Persistierung abgegeben
     * @return Die Eigenschaften
     */

    public FrameChartOptions saveBeforeLeave() {
        FrameChartOptions result= new FrameChartOptions();
        FrameChartModel locmodel=model;

        result.setFrameSize(getSize());
        result.setOffsetX(getX());
        result.setOffsetY(getY());
        result.setTitle(locmodel.getFrameTitle());
        result.setChartIntervall(locmodel.getIntervallTyp());
        result.setChartType(locmodel.getChartTyp());
        result.setStockID(model.getStockID());
        if (locmodel.getIndicator()!=null) {
            result.setIndicator(locmodel.getIndicator());
        }
        result.setAlarms(locmodel.getAlarme());
        result.setLastPriceForAlarm(locmodel.getLastPrice());

        return result;
    }
    double round(double value) {
        double d = Math.pow(10, prefer.getPriceDecCScale());
        return Math.round(value * d) / d;
    }

}

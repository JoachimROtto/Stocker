package stocker.controller;

import javax.swing.*;

import stocker.view.*;
import stocker.model.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Die Klasse <code>FrameMainGUI</code>bildet das Hauptfenster der Anwendung ab.
 * Im Konstruktor werden auch <code>StockerAppSettings</code>, <code>StockerControllCenter</code> 
 * und <code>StockerSettings</code> initialisiert.  
 * 
 * @author Joachim Otto
 */

public class FrameMainGUI extends JFrame {

     StockerPreferences prefer;
     StockerAppSettings sas;

    //Liste für Menüpunkt Fenster
    JMenu windowMenu = new JMenu("Fenster");
     List<JInternalFrame> windowList = new ArrayList<JInternalFrame>();

    /**
     * Die Klasse wird initialisiert mit  
     * @param title Dem Titel
     * 
     */ 

    public FrameMainGUI(String title) {
        super(title);
        //Voreinstellungen laden
        prefer= new StockerPreferences();
        prefer= prefer.getPrefFromJSONFile();
        sas= new StockerAppSettings(prefer);
        sas=sas.getSetFromJSONFile();
        sas.setFrame(this);
        sas.setPrefer(prefer);
        sas.setSCC(new StockControllCenter(sas));
        sas.restoreSCC();

        //Layout

        JDesktopPane desktop = new JDesktopPane();
        sas.setDesktop(desktop);

        JMenuBar menuBar = new JMenuBar();
        //Menu aufbauen
        JMenu fileMenu = new JMenu("Datei");
        fileMenu.setMnemonic(KeyEvent.VK_D);
        fileMenu.setMnemonic('d');
        menuBar.add(fileMenu);

        JMenuItem closingItem = new JMenuItem("Beenden");
        closingItem.setMnemonic('b');
        closingItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
        closingItem.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                System.exit(0);    }}) ;
        fileMenu.add(closingItem);

        JMenu fctMenu = new JMenu("Funktionen");
        fctMenu.setMnemonic('f');

        JMenuItem searchMenuItem = new JMenuItem("Aktiensuche");
        searchMenuItem.setMnemonic('s');
        searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        searchMenuItem.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        new FrameStockSearch(sas).setAlwaysOnTop(true);
                    }});
            }}) ;
        fctMenu.add(searchMenuItem);

        JMenuItem prefMenuItem=new JMenuItem("Einstellungen");
        prefMenuItem.setMnemonic('i');
        prefMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
        prefMenuItem.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                FramePreferences.getInstance(sas).moveToFront();
            }});
        fctMenu.add(prefMenuItem);

        JMenuItem wlMenuItem=new JMenuItem("Watchlist");
        wlMenuItem.setMnemonic('w');
        wlMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        wlMenuItem.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                FrameWatchlist.getInstance(sas).show();
            }});
        fctMenu.add(wlMenuItem);

        menuBar.add(fctMenu);

        windowMenu.setMnemonic('f');
        menuBar.add(windowMenu);
        setJMenuBar(menuBar); 

        add(desktop, BorderLayout.CENTER);
        setSize(sas.getSizeFrameMainGUI());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                sas.saveBeforeLeave();}});

        //Aufbau persistierter Fenster
        restoreFromSAS();
        //persistierte Alarme der Watchlist
        sas.getSCC().restoreOnStartup();
        
        setVisible(true);
    }

    /**
     * Trägt Fenster in die Fensterliste ein
     * @param f Das einzutragende Fenster
     */

    public void add2WindowList(JInternalFrame f) {
        windowList.add(f);
        add2WindowMenu(f);
        windowMenu.repaint();
    }

    /**
     * Eintfernt ein Fenster aus der dem Fenstermenu
     * @param f Das zu entfernende Fenster
     */
    public void removeFromWindowList(JInternalFrame f) {
        windowMenu.removeAll();
        windowList.remove(windowList.indexOf(f));
        for (JInternalFrame frame: windowList) {
                    add2WindowMenu(frame);
        }
        windowMenu.repaint();
    }

    /**
     * Trägt Fenster in das Fenstermenu ein
     * @param f Das einzutragende Fenster
     */

    public void add2WindowMenu(JInternalFrame f) {
        int indexList=windowList.indexOf(f) ;
        JMenuItem internalFrame = new JMenuItem(indexList+ 1 + ": " + f.getTitle());
        //VK-Offset 48 um den richtigen ASCII-Code zu berechenen
        internalFrame.setMnemonic(indexList=indexList + 49);
        internalFrame.setAccelerator(KeyStroke.getKeyStroke(indexList, ActionEvent.CTRL_MASK));
        internalFrame.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed (ActionEvent actionEvent) {
                f.moveToFront();
                try {
                    f.setIcon(false);
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
            }}) ;
        windowMenu.add(internalFrame);
        //Charts den Menueintrag mitteilen, damit Eintrag und Fenstertitel synchronisiert werden
        if (f instanceof FrameChart) {((FrameChart) f).setAnnotMenu(internalFrame);}
    }
    
    /**
     * Gibt die Liste offenere Fenster zurück
     * @return Die Fensterliste
     */
    
    public List<JInternalFrame> getWindowList(){
        return windowList;
    }

    /**
     * Baut persistierte Fenster wieder auf
     */
    
    public void restoreFromSAS() {
        boolean wlWasOpen=false;
        if (sas.getSavedFrames()!=null) {    
            JInternalFrame frameLoc = new JInternalFrame(); 
            for (SavedFrame restoredFrame : sas.getSavedFrames()) {
                //SearchFrame wird  nicht persistiert weil modal
                //Chart wird getrennt gehandhabt
                if  (restoredFrame.getType()==restoredFrame.TYPE_WL){
                    frameLoc= FrameWatchlist.getInstance(sas);
                    
                    frameLoc.setBounds(restoredFrame.getOffX(), restoredFrame.getOffY(), restoredFrame.getWidth(), restoredFrame.getHeigth());
                    wlWasOpen=true;
                }
                if  (restoredFrame.getType()==restoredFrame.TYPE_PREF){
                    frameLoc=FramePreferences.getInstance(sas);
                    frameLoc.setBounds(restoredFrame.getOffX(), restoredFrame.getOffY(), restoredFrame.getWidth(), restoredFrame.getHeigth());
                }}}

        //wenn die WL beim letzten Mal Schließen geschlossen war
        if (!wlWasOpen) {
            FrameWatchlist.getInstance(sas);
            }
        

        if (sas.getOpenChartsArray()!=null) {
            for (int i=0; i<sas.getOpenChartsArray().length; i++) {
                if (sas.getOpenChartsArray()[i]!=null) {
                    new FrameChart(sas, sas.getOpenChartsArray()[i]);
                }}}}}
package stocker.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import stocker.model.*;
/**
 * 
 * Der Listener <code>FrameChartviewMenuListener</code> lauscht auf das Menu der Charts
 *
 * @author Joachim Otto
 */

public class FrameChartviewMenuListener implements ActionListener{
    FrameChartModel model;
    StockerPreferences prefer;
    int source;
    /**
     * Der Listener wird initialisiert mit der 
     * @param model dem Model des zughörenden Charts 
     * @param prefer den Grundeinstellungen und
     * @param source dem aufgerufenden Menüeintrag 
     * 1 = Aufruf der Kerzendarstellung, 2=Aufruf der Liniendarstellung
     * 3 = Aufruf der Indikatoreneinstellung, 4=Aufruf der Alarmeinstellung 
     * über100 = Änderung des Intervalls
     * 
     */

    public FrameChartviewMenuListener(FrameChartModel model, int source, StockerPreferences prefer) {
        this.model=model;
        this.source=source;
        this.prefer=prefer;
    }
    /**
     * Ein Ereignis wurde ausgelöst
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      switch (source) {
      case 1:
          model.setCharttyp(prefer.CHARTTYP_CANDLE);
          break;
      case 2:
          model.setCharttyp(prefer.CHARTTYP_LINE);
          break;
      case 3:
          model.setIndikator();
          break;
      case 4:
          model.setAlarm();
          break;
      default :
          if (source>=100) {
              model.setIntervall(source-100);
          }  
      }          
    }
}

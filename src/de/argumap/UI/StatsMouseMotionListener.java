package de.argumap.UI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import uk.ac.leeds.ccg.geotools.GeoShape;
import de.argumap.discussion.Contribution;

/**
 * @author Carsten Keßler, carsten.kessler@uni-muenster.de
 * @version 17.11.2004
 *  
 */
public class StatsMouseMotionListener implements MouseMotionListener {

    private ArguMapWindow applet;
    private int pointID;
    private int lineID;
    private int polygonID;

    public StatsMouseMotionListener(ArguMapWindow applet) {
        super();
        this.applet = applet;
        pointID = -1;
        polygonID = -1;
        lineID = -1;
    }

    public void mouseMoved(MouseEvent e) {
        //nachsehen, obirgendwas gehighlighted ist:
        int hilightPointID = applet.getPointsFromDB().getHighlightManager()
                .getHighlight();
        int highlightLineID = applet.getLinesFromDB().getHighlightManager()
                .getHighlight();
        int highlightPolygonID = applet.getPolygonsFromDB()
                .getHighlightManager().getHighlight();

        //nur in aktion treten, wenn sich eine der IDs geändert hat:
        //TODO: das ist so nicht optimal, weil dann das statspanel NICHT
        //upgedatet wird, wenn der user einen anderen beitrag im baum 
        //ausgewählt hat
        if (hilightPointID != pointID 
                || highlightLineID != lineID
                || highlightPolygonID != polygonID) {
            
            pointID = hilightPointID;
        	lineID = highlightLineID;
        	polygonID = highlightPolygonID;
        
        	SearchAndAnalyzePanel analysisPanel = (SearchAndAnalyzePanel)applet.getTabs().getComponentAt(2);
        	
        	//wenn eine der drei nicht -1 ist, das stats-panel updaten:
        	if(hilightPointID != -1){
        	    int[] iDs = new int[1];
        	    iDs[0] = hilightPointID;
        	    analysisPanel.updateStats(iDs);
        	}else if(highlightLineID != -1){
        	    int[] iDs = new int[1];
        	    iDs[0] = highlightLineID;
        	    analysisPanel.updateStats(iDs);
        	}else if(highlightPolygonID != -1){
        	    int[] iDs = new int[1];
        	    iDs[0] = highlightPolygonID;
        	    analysisPanel.updateStats(iDs);
            }else{
                //dafür sorgen, dass entweder statistiken zu den
                //objekten angezeigt werden, die gehighlighted sind,
                //weil sie zu einem beitrag im baum gehören; oder
                //dass nichts angezeigt ist, weil auf dem viewer
                //nichts hervorgehoben ist
                
                Contribution con = applet.getArguPanel().getSelectedContribution();
                
                //1. fall: im baum ist nichts ausgewählt:
                if(con == null){
                    analysisPanel.updateStats(new int[0]);
                }else{   //2. fall: im baum ist ein beitrag ausgewählt:
                    //alle referenzobjekte holen und ihre IDs in ein array eintragen
                    ArrayList refs = con.getReferenceObjects();
                    int[] iDs = new int[refs.size()];
                    for (int i = 0; i < iDs.length; i++) {
                        iDs[i] = ((GeoShape)refs.get(i)).getID();
                    }
                    //und die stats updaten:
                    analysisPanel.updateStats(iDs);
                }
            }
            	
            	
            	
            
        }

    }

    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

}
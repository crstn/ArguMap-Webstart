package de.argumap.gt_extensions;

import java.awt.Cursor;
import java.util.Iterator;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.GeoShape;
import uk.ac.leeds.ccg.geotools.LineLayer;
import uk.ac.leeds.ccg.geotools.PointLayer;
import uk.ac.leeds.ccg.geotools.PolygonLayer;
import de.argumap.UI.ArguMapWindow;
import de.argumap.discussion.Contribution;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 26.10.2004
 *  
 */
public class ArguMapContributionSelectionTool extends
        uk.ac.leeds.ccg.geotools.SimpleTool {

    public static String NAME = "Select Contributions by Reference";
    public static String DESCRIPTION = "Click a spatial reference object to highlight the contributions that refer to it.";
    
    private ArguMapWindow window;
    
    /**
     * @param window
     */
    public ArguMapContributionSelectionTool(ArguMapWindow window) {
        super();
        this.window = window;
    }
    
    public int getRubberBandShape() {
        return NONE;
    }

    public Cursor getCursor() {
        return new Cursor(Cursor.DEFAULT_CURSOR);
    }

    public String getName() {
        return NAME;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    /*
     * Identifies the reference object which is currently highlighted and makes
     * the ArguPanel highligh those references.
     *  
     */
    public void release() {
        int hilightPointID = window.getPointsFromDB().getHighlightManager()
                .getHighlight();
        int highlightLineID = window.getLinesFromDB().getHighlightManager()
                .getHighlight();
        int highlightPolygonID = window.getPolygonsFromDB()
                .getHighlightManager().getHighlight();
        GeoShape gs;
        
        if(hilightPointID != -1){
            PointLayer pl = (PointLayer) window.getPointsFromDB().getLayer();
            gs = pl.getGeoShape(hilightPointID);          
        }else if(highlightLineID != -1){
            LineLayer ll = (LineLayer) window.getLinesFromDB().getLayer();
            gs=ll.getGeoShape(highlightLineID);
        }else if(highlightPolygonID != -1){
            PolygonLayer pol = (PolygonLayer) window.getPolygonsFromDB().getLayer();
            gs=pol.getGeoShape(highlightPolygonID);
        }else{
            //no feature at this location - cancel...   
            return;
        }
        
        Vector cons = window.getCardPanel().getArguPanel().getAllContributions();
        Vector<Contribution> referencers = new Vector<Contribution>();
        Iterator iterator = cons.iterator();
        while (iterator.hasNext()) {
            Contribution current = (Contribution)iterator.next();
            Iterator iter = current.getReferenceObjects().iterator();
            while (iter.hasNext()) {
                GeoShape currentGS = (GeoShape) iter.next();
                
                if(gs.equals(currentGS)){
                    referencers.add(current);
                }
            }
        }

        //copy the matching contributions to an array:
        Iterator iter = referencers.iterator();
        Contribution[] contributions = new Contribution[referencers.size()];
        int i = 0;
        while(iter.hasNext()){
            Contribution current = (Contribution)iter.next();
            contributions[i] = current;
            i++;
        }
        
        //highlight them:
        window.getCardPanel().getArguPanel().highlightContributions(contributions);
    }

}
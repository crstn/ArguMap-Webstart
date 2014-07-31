package uk.ac.leeds.ccg.dataentry;

import java.awt.Cursor;

import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.PointLayer;
import uk.ac.leeds.ccg.geotools.SimpleTool;
import uk.ac.leeds.ccg.geotools.Tool;

/*
 * PointEntryTool.java
 *
 * Allow a user to build a layer by entering in points.
 * @author  Cameron Shorter
 * 
 */
public class PointEntryTool extends SimpleTool implements Tool {
    private final static boolean DEBUG=false;
    /** The layer to build features into */
    private PointLayer layer;
    /** The id to assign a shape as it is created */
    private int nextId;

    /**
     * Creates new PointEntryTool
     * @param layer The layer to build features into.
     */
    public PointEntryTool(PointLayer layer) {
        this.layer=layer;
        this.nextId=1;
    }

    /**
     * called when a mouse button has been cliked.
     * Add a point to the layer
     */
    public void click(){
        GeoPoint point=new GeoPoint(nextId++,mouse.getMapPoint().x,mouse.getMapPoint().y);
        layer.addGeoPoint(point);
        if(DEBUG)System.out.println(
            "PointEntry.click, id="+nextId
            +" point="+mouse.getMapPoint());
    }
    
    /**
     * find the cursor most suitable for this tool.
     *
     * @return Cursor the best cursor for use on screen with this tool.
     */
    public Cursor getCursor() {
    return new Cursor(Cursor.DEFAULT_CURSOR);
    }
    
    /**
     * provides a description for this tool.
     * The description should briefly describe the purpose of the dool
     *
     * @return String A description of this tool.
     */
    public String getDescription() {
        return "PointEntryTool allows a user to enter point data into a layer";
    }
    
    /**
     * provides a short name for this tool.
     * The name should be suitable for inclusion in a menu or on a button.
     *
     * @author James Macgill JM
     * @since 0.7.9 November 23 2000
     * @return String The name of this tool.
     */
    public String getName() {
        return "PointEntry";
    }
    
    /**
     * Retun one of the above constants to specify which rubber band should be
     * automaticaly displayed during mouse drag events.
     * @return int A value from above representing a circle, rectangle or line (or none)
     */
    public int getRubberBandShape() {
        return NONE;
    }
    
}

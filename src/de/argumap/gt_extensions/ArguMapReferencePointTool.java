package de.argumap.gt_extensions;

import java.awt.Cursor;

import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.LineLayer;
import uk.ac.leeds.ccg.geotools.PointLayer;
import uk.ac.leeds.ccg.geotools.PolygonLayer;
import de.argumap.UI.ArguMapWindow;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 30.07.2004
 * 
 */
public class ArguMapReferencePointTool extends
        uk.ac.leeds.ccg.geotools.SimpleTool {

    public static String DESCRIPTION = "Create a new point to add it to your discussion contribution as a reference.";
    public static String NAME = "Create New Polygon Reference";
    
    private ArguMapWindow applet;
    PointLayer userPoints;
    LineLayer userLines;
    PolygonLayer userPolygons;

    /**
     * @param applet
     */
    public ArguMapReferencePointTool(ArguMapWindow applet, PointLayer userPoints) {
        super();
        this.applet = applet;
        this.userPoints = userPoints;
    }

    public int getRubberBandShape() {
        return NONE;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public String getName() {
        return NAME;
    }

    public Cursor getCursor() {
        return new Cursor(Cursor.DEFAULT_CURSOR);
    }

    public void release() {
        GeoPoint mouseLocation = new GeoPoint(mouse.getMapPoint());
        userPoints.addGeoPoint(mouseLocation);
        applet.getUserPointTheme().setLayer(userPoints);
    }
}
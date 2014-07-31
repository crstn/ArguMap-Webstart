package de.argumap.gt_extensions;

import java.awt.Cursor;
import java.util.Iterator;

import uk.ac.leeds.ccg.geotools.GeoLine;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoPolygon;
import uk.ac.leeds.ccg.geotools.GeoShape;
import uk.ac.leeds.ccg.geotools.LineLayer;
import uk.ac.leeds.ccg.geotools.MixedLayer;
import uk.ac.leeds.ccg.geotools.PointLayer;
import uk.ac.leeds.ccg.geotools.PolygonLayer;
import de.argumap.UI.ArguMapWindow;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 30.07.2004
 * 
 */
public class ArguMapSelectReferenceTool extends
        uk.ac.leeds.ccg.geotools.SimpleTool {

    private static boolean DEBUG = false;

    public static String DESCRIPTION = "Add an existing reference object to your discussion contributon (click again to remove it).";
    public static String NAME = "Add / remove existing reference";

    private ArguMapWindow applet;
    private PointLayer userPoints;
    private LineLayer userLines;
    private PolygonLayer userPolygons;

    /**
     * @param applet
     */
    public ArguMapSelectReferenceTool(ArguMapWindow applet,
            PointLayer userPoints, LineLayer userLines,
            PolygonLayer userPolygons) {
        super();
        this.applet = applet;
        this.userPoints = userPoints;
        this.userLines = userLines;
        this.userPolygons = userPolygons;
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
        int highlightReferencePointID = applet.getUserPointTheme()
                .getHighlightManager().getHighlight();
        int highlightReferenceLineID = applet.getUserLineTheme()
                .getHighlightManager().getHighlight();
        int highlightReferencePolygonID = applet.getUserPolygonTheme()
                .getHighlightManager().getHighlight();
        int hilightPointID = applet.getPointsFromDB().getHighlightManager()
                .getHighlight();
        int highlightLineID = applet.getLinesFromDB().getHighlightManager()
                .getHighlight();
        int highlightPolygonID = applet.getPolygonsFromDB()
                .getHighlightManager().getHighlight();
        int highlightShapeID = applet.getShapeTheme().getHighlightManager()
                .getHighlight();

        if (DEBUG) {
            System.out.println("highlightReferencePointID: "
                    + highlightReferencePointID);
            System.out.println("highlightReferenceLineID: "
                    + highlightReferenceLineID);
            System.out.println("highlightReferencePolygonID: "
                    + highlightReferencePolygonID);
            System.out.println("hilightPointID: " + hilightPointID);
            System.out.println("highlightLineID: " + highlightLineID);
            System.out.println("highlightPolygonID: " + highlightPolygonID);
            System.out.println("highlightShapeID: " + highlightShapeID);
        }
        
        // first check whether the clicked object is already referenced by the
        // user - if it is, it has to be removed from the user's references
        if (highlightReferencePointID != -1) {
            PointLayer pl = (PointLayer) applet.getUserPointTheme().getLayer();
            pl
                    .deletePoint((GeoPoint) pl
                            .getGeoShape(highlightReferencePointID));
        } else if (highlightReferenceLineID != -1) {
            LineLayer ll = (LineLayer) applet.getUserLineTheme().getLayer();
            ll.deleteLine((GeoLine) ll.getGeoShape(highlightReferenceLineID));
        } else if (highlightReferencePolygonID != -1) {
            PolygonLayer pl = (PolygonLayer) applet.getUserPolygonTheme()
                    .getLayer();
            pl.deletePolygon((GeoPolygon) pl
                    .getGeoShape(highlightReferencePolygonID));
        }

        // now check if the reference already exists (created / selected by
        // another user):
        else if (hilightPointID != -1) {
            PointLayer pl = (PointLayer) applet.getPointsFromDB().getLayer();
            GeoPoint p = (GeoPoint) pl.getGeoShape(hilightPointID);
            userPoints.addGeoPoint(p);
            applet.getUserPointTheme().setLayer(userPoints);
        } else if (highlightLineID != -1) {
            LineLayer ll = (LineLayer) applet.getLinesFromDB().getLayer();
            GeoLine l = (GeoLine) ll.getGeoShape(highlightLineID);
            userLines.addGeoLine(l);
            applet.getUserLineTheme().setLayer(userLines);
        } else if (highlightPolygonID != -1) {
            PolygonLayer pl = (PolygonLayer) applet.getPolygonsFromDB()
                    .getLayer();
            GeoPolygon pg = (GeoPolygon) pl.getGeoShape(highlightPolygonID);
            userPolygons.addGeoPolygon(pg);
            applet.getUserPolygonTheme().setLayer(userPolygons);
            // if nothing has been found yet, look it up in the shapefile:
        } else if (highlightShapeID != -1) {
            MixedLayer ml = (MixedLayer) applet.getShapeTheme().getLayer();
            GeoShape gs = null;
            // a mixed layer contains a number of different layers:
            Iterator iter = ml.getLayers().iterator();
            while (iter.hasNext()) {
                Object current = iter.next();
                if (current instanceof PointLayer) {
                    PointLayer pl = (PointLayer) current;
                    gs = (GeoShape) pl.getGeoShape(highlightShapeID);
                    userPoints.addGeoPoint((GeoPoint) gs);
                    applet.getUserPointTheme().setLayer(userPoints);

                } else if (current instanceof LineLayer) {
                    LineLayer ll = (LineLayer) current;
                    gs = (GeoShape) ll.getGeoShape(highlightShapeID);
                    userLines.addGeoLine((GeoLine) gs);
                    applet.getUserLineTheme().setLayer(userLines);

                } else {// so it must be a polygonlayer
                    PolygonLayer pgl = (PolygonLayer) current;
                    gs = (GeoShape) pgl.getGeoShape(highlightShapeID);
                    userPolygons.addGeoPolygon((GeoPolygon) gs);
                    applet.getUserPolygonTheme().setLayer(userPolygons);

                }
            }
            // nothing has been found, so create a new reference object:
        } else {
            /*
             * DO NOTHING... GeoPoint mouseLocation = new
             * GeoPoint(mouse.getMapPoint());
             * userPoints.addGeoPoint(mouseLocation);
             * applet.getUserPointTheme().setLayer(userPoints);
             * 
             */
        }

    }
}
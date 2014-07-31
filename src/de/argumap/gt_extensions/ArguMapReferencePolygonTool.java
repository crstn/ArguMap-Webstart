package de.argumap.gt_extensions;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoPolygon;
import uk.ac.leeds.ccg.geotools.PolygonLayer;
import de.argumap.UI.ArguMapWindow;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 30.07.2004
 * 
 */
public class ArguMapReferencePolygonTool extends
        uk.ac.leeds.ccg.geotools.SimpleTool {

    public static String DESCRIPTION = "Create a new polygon to add it to your discussion contribution as a reference.";
    public static String NAME = "Create new polygon reference";
    
    private ArguMapWindow window;
    PolygonLayer userPolygons;
    int doubleClickTime = 300; // double-click speed in ms
    long timeMouseDown = 0; // last mouse down time
    GeoPoint lastPosition = new GeoPoint(0.0, 0.0); // last x and y
    private Vector<GeoPoint> polygonPointsTemp = new Vector<GeoPoint>();
    private boolean doPaint = false;
    int currentID = 0;
    
    // on-screen coordinates of the last release event, used for
    private Vector<Integer> polygonPointsScreenTempX = new Vector<Integer>();
    private Vector<Integer> polygonPointsScreenTempY = new Vector<Integer>();

    /**
     * @param window
     * @param userPolygons
     */
    public ArguMapReferencePolygonTool(ArguMapWindow window, PolygonLayer userPolygons) {
        super();
        this.window = window;
        this.userPolygons = userPolygons;
    }

    public int getRubberBandShape() {
        return LINE;
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

    /**
     * Does nothing in this tool - just overrinding it to avoid the drag
     * behaviour of SimpleTool
     */
    public void drag() {

    }

    /**
     * If the user has clicked before (to set one point in the polygon), this
     * method draws all temporary polygon segments in a polygon and a final polygon
     * segment following the mouse pointer. This behaviour is continued until
     * the user double-clicks to finish the polygon.
     * 
     */
    public void move() {
        if (doPaint && !(lastPosition.x == 0.0) && !(lastPosition.y == 0.0)) {
            context.repaint();
            int currentX = context.getMouseStatus().getMouseEvent().getX();
            int currentY = context.getMouseStatus().getMouseEvent().getY();
            Rectangle box = new Rectangle(
                    currentX,
                    currentY,
                    polygonPointsScreenTempX.get(polygonPointsScreenTempX.size() - 1),
                    polygonPointsScreenTempY.get(polygonPointsScreenTempY.size() - 1));
            drawRubberBand(box);

            // draw all "confirmed" line segments:
            if (polygonPointsScreenTempX.size() >= 2) {
                for (int i = 0; i < polygonPointsScreenTempX.size() - 1; i++) {
                    Rectangle currentBox = new Rectangle(polygonPointsScreenTempX
                            .get(i), polygonPointsScreenTempY.get(i),
                            polygonPointsScreenTempX.get(i + 1),
                            polygonPointsScreenTempY.get(i + 1));
                    drawRubberBand(currentBox);
                }
            }

        }
    }

    public void release() {
        clear();
        GeoPoint mouseLocation = new GeoPoint(mouse.getMapPoint());

        long currentTime = System.currentTimeMillis();

        // double click: the polygon is finished
        if (lastPosition.equals(mouseLocation)
                && ((currentTime - timeMouseDown) < doubleClickTime)) {
            // DE-activate painting the temporal polygon segments following the
            // mouse pointer
            doPaint = false;

            // create a GeoPolygon from the vector of points
            // copy to an array frst - that's easier to handle
            Object[] points = polygonPointsTemp.toArray();

            // now flush the vectors holding the points:
            polygonPointsTemp.removeAllElements();
            polygonPointsScreenTempX.removeAllElements();
            polygonPointsScreenTempY.removeAllElements();

            // copy coords to separate arrays (required for GeoPolygon
            // constructor)
            double[] xPoints = new double[points.length];
            double[] yPoints = new double[points.length];

            for (int i = 0; i < points.length; i++) {
                xPoints[i] = ((GeoPoint) points[i]).getX();
                yPoints[i] = ((GeoPoint) points[i]).getY();
            }

            userPolygons.addGeoPolygon(new GeoPolygon(currentID, xPoints, yPoints, points.length));
            window.getUserPolygonTheme().setLayer(userPolygons);

            currentID++;
        } else { // single click: add another point to the polygon
            // add the point to the temp. vector for the polygon's points
            polygonPointsTemp.add(mouseLocation);

            // same for the screen coordinates:
            polygonPointsScreenTempX.add(context.getMouseStatus().getMouseEvent()
                    .getX());
            polygonPointsScreenTempY.add(context.getMouseStatus().getMouseEvent()
                    .getY());

            timeMouseDown = currentTime;
            lastPosition = mouseLocation;
            
            // activate painting the temporal polygon segments following the mouse
            // pointer
            doPaint = true;
        }
    }

}
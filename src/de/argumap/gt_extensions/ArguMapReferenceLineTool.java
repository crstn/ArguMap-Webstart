package de.argumap.gt_extensions;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.GeoLine;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.LineLayer;
import de.argumap.UI.ArguMapWindow;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 30.07.2004
 * 
 */
public class ArguMapReferenceLineTool extends
        uk.ac.leeds.ccg.geotools.SimpleTool {

    public static String NAME = "Create new Line Reference";
    public static String DESCRIPTION = "Create a new line to add it to your discussion contribution as a reference.";
    
    private ArguMapWindow window;
    LineLayer userLines;
    int doubleClickTime = 300; // double-click speed in ms
    long timeMouseDown = 0; // last mouse down time
    GeoPoint lastPosition = new GeoPoint(0.0, 0.0); // last x and y
    private Vector<GeoPoint> linePointsTemp = new Vector<GeoPoint>();
    private boolean doPaint = false;
    private int currentID = 0;
    
    // on-screen coordinates of the last release event, used for
    private Vector<Integer> linePointsScreenTempX = new Vector<Integer>();
    private Vector<Integer> linePointsScreenTempY = new Vector<Integer>();

    /**
     * @param window
     * @param userLines
     */
    public ArguMapReferenceLineTool(ArguMapWindow window, LineLayer userLines) {
        super();
        this.window = window;
        this.userLines = userLines;
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
     * If the user has clicked before (to set one point in the polyline), this
     * method draws all temporary line segments in a polyline and a final line
     * segment following the mouse pointer. This behaviour is continued until
     * the user double-clicks to finish the polyline.
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
                    linePointsScreenTempX.get(linePointsScreenTempX.size() - 1),
                    linePointsScreenTempY.get(linePointsScreenTempY.size() - 1));
            drawRubberBand(box);

            // draw all "confirmed" line segments:
            if (linePointsScreenTempX.size() >= 2) {
                for (int i = 0; i < linePointsScreenTempX.size() - 1; i++) {
                    Rectangle currentBox = new Rectangle(linePointsScreenTempX
                            .get(i), linePointsScreenTempY.get(i),
                            linePointsScreenTempX.get(i + 1),
                            linePointsScreenTempY.get(i + 1));
                    drawRubberBand(currentBox);
                }
            }

        }
    }

    public void release() {
        clear();
        GeoPoint mouseLocation = new GeoPoint(mouse.getMapPoint());

        long currentTime = System.currentTimeMillis();

        // double click: the line is finished
        if (lastPosition.equals(mouseLocation)
                && ((currentTime - timeMouseDown) < doubleClickTime)) {
            // DE-activate painting the temporal line segments following the
            // mouse pointer
            doPaint = false;

            // create a GeoLine from the vector of points
            // copy to an array frst - that's easier to handle
            Object[] points = linePointsTemp.toArray();

            // now flush the vectors holding the points:
            linePointsTemp.removeAllElements();
            linePointsScreenTempX.removeAllElements();
            linePointsScreenTempY.removeAllElements();

            // copy coords to separate arrays (required for GeoLine
            // constructor)
            double[] xPoints = new double[points.length];
            double[] yPoints = new double[points.length];

            for (int i = 0; i < points.length; i++) {
                xPoints[i] = ((GeoPoint) points[i]).getX();
                yPoints[i] = ((GeoPoint) points[i]).getY();
            }

            userLines.addGeoLine(new GeoLine(currentID, xPoints, yPoints, points.length));
            window.getUserLineTheme().setLayer(userLines);
            
            currentID++;

        } else { // single click: add another point to the line
            // add the point to the temp. vector for the line's points
            linePointsTemp.add(mouseLocation);

            // same for the screen coordinates:
            linePointsScreenTempX.add(context.getMouseStatus().getMouseEvent()
                    .getX());
            linePointsScreenTempY.add(context.getMouseStatus().getMouseEvent()
                    .getY());

            timeMouseDown = currentTime;
            lastPosition = mouseLocation;
            
            // activate painting the temporal line segments following the mouse
            // pointer
            doPaint = true;
        }
    }

}
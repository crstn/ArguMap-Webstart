/*
 * @(#)GeoLine.java  0.5 22 May 1998  James Macgill
 *
 */

package uk.ac.leeds.ccg.geotools;

/**
 * This class is intended for storing georaphical line data such as roads,
 * rivers etc.
 * <p>
 * <em>To Do:</em><br>
 * 
 * 
 * @author Jamaes Macgill
 * @version 0.5 22 May 1998 fast isNear code contributed by
 *          norbertf@users.sourceforge.net slow isNear code by Ian
 */
public class GeoLine extends GeoPolygon {
    public boolean acurate = false; // this means it will be faster

    /**
     * Number of points in line
     */

    /**
     * Array of points
     */

    /**
     * Minimum bounding box for line
     */

    /**
     * Create an empty polygon
     */
    public GeoLine() {
    } // Empty

    /**
     * Construct a line
     * 
     * @param id
     *            Line ID
     * @param xpoints
     *            array of x values (npoints in size)
     * @param ypoints
     *            array of y values (npoints in size)
     * @param npoints
     *            number of points
     */
    public GeoLine(int id, double[] xpoints, double[] ypoints, int npoints) {
        super(id, xpoints, ypoints, npoints);
    }

    /**
     * Construct a line with no defined centroid or ID
     * 
     * @param xpoints
     *            array of x values (npoints in size)
     * @param ypoints
     *            array of y values (npoints in size)
     * @param npoints
     *            number of points
     */
    public GeoLine(double[] xpoints, double[] ypoints, int npoints) {
        this(-1, xpoints, ypoints, npoints);
    }

    
    /**
     * Construct a GeoPolygon based on an existing GeoPolygon
     * 
     * @param poly
     *            GeoPolygon to clone
     */
    public GeoLine(GeoLine line) {
        this(line.id, line.xpoints, line.ypoints, line.npoints);
    }

    public GeoLine(int id, GeoPoint[] points) {
        super(id, points);
    }

    public void setAcurate(boolean f) {
        acurate = f;
    }

    /**
     * returns true if the specified point is inside the polygon
     */
    public boolean isNear(GeoPoint p, double w) {
        GeoRectangle r = getBounds();
        GeoRectangle biggerRect = new GeoRectangle(r.getX() - w, r.getY() - w,
                r.getWidth() + w * 2, r.getHeight() + w * 2);

        if (biggerRect.contains(p)) {
            if (!acurate) {
                for (int i = 0; i < npoints - 1; i++) {
                    double ax = xpoints[i];
                    double ay = ypoints[i];
                    double bx = xpoints[i + 1];
                    double by = ypoints[i + 1];

                    double cx = p.getX();
                    double cy = p.getY();
                    // side-lengths of the triangle
                    double lc = Math.sqrt((ax - bx) * (ax - bx) + (ay - by)
                            * (ay - by));
                    double la = Math.sqrt((cx - bx) * (cx - bx) + (cy - by)
                            * (cy - by));
                    double lb = Math.sqrt((cx - ax) * (cx - ax) + (cy - ay)
                            * (cy - ay));
                    // area of the triangle
                    double s = (la + lb + lc) / 2;
                    double area = Math.sqrt(s * (s - la) * (s - lb) * (s - lc));
                    // max area:
                    double maxarea = lc * w / 2;
                    // max diagonal:
                    // double maxdiag = Math.sqrt( lc*lc + w*w);
                    double maxdiag = lc;
                    if ((area < maxarea) && (la < maxdiag) && (lb < maxdiag))
                        return true;

                }
            } else { // acurate but slow
                for (int i = 0; i < npoints - 1; i++) {
                    GeoPoint p1 = new GeoPoint(xpoints[i], ypoints[i]);
                    GeoPoint p2 = new GeoPoint(xpoints[i + 1], ypoints[i + 1]);
                    if (p1.getY() > p2.getY()) {
                        GeoPoint tmp = p1;
                        p1 = p2;
                        p2 = tmp;
                    }

                    double dx = p2.getX() - p1.getX();
                    double dy = p2.getY() - p1.getY();
                    double alpha = Math.atan2(dy, dx);
                    dy = (w) * Math.sin(Math.PI / 2.0 - alpha);
                    dx = (w) * Math.cos(Math.PI / 2.0 - alpha);
                    double[] x = new double[4];
                    double[] y = new double[4];
                    x[0] = p1.getX() - dx;
                    y[0] = p1.getY() + dy;
                    x[1] = p2.getX() - dx;
                    y[1] = p2.getY() + dy;
                    x[2] = p2.getX() + dx;
                    y[2] = p2.getY() - dy;
                    x[3] = p1.getX() + dx;
                    y[3] = p1.getY() - dy;
                    GeoPolygon gp = new GeoPolygon(0, 0.0, 0.0, x, y, 4);
                    GeoCircle gc = new GeoCircle(0, p1, (double) w);
                    if (gc.contains(p))
                        return true;
                    gc = new GeoCircle(0, p2, (double) w);
                    if (gc.contains(p))
                        return true;
                    if (gp.contains(p))
                        return true;
                }
            }// acurate
        }

        return false;
    }

    public boolean isNear(double x, double y, double w) {
        GeoPoint p = new GeoPoint(x, y);
        return isNear(p, w);
    }

    /**
     * This method is EXPERIMENTAL & BUGGY. Don't rely on this to work at this
     * point The method checks for an intersection between this object's line
     * and a given line Please note that at this time, a line can only have two
     * points! A geoline object in general can have more than two points. The
     * algorithm used is taken from the comp.graphics.algorithms FAQ, available
     * at http://www.cs.ruu.nl/wais/html/na-dir/graphics/algorithms-faq.html and
     * posted regularly at that newsgroup. Subject 1.03 at this time of writing
     * the algorithm basically solves the two line equations. I've adjusted the
     * algorithm slightly. In the FAQ r is calculated as r =
     * numerator/denumerator, and s = numerator/denumerator As the denumerators
     * are identical, and an intersection exists if both 0<=r<=1 and 0<=s<=1
     * I calculate r = numerator, s = numerator. Then I check for 0<=r<=denumerator
     * and 0<=s<=denumerator this prevents any potential divide by zero as
     * well.
     * 
     * We don't calculate the intersection point, though that should be done
     * easily by solving : Intersection point P.x = Line1.pointA.x + r *
     * (Line1.pointB.x-Line1.pointA.x) Intersection point P.y = Line1.pointA.y +
     * r * (Line1.pointB.y-Line1.pointA.y) (also taken from that faq). One last
     * thing : I'm definitely no expert at this, so don't trust me for
     * implementing this correctly. (though I checked the code before releasing :) )
     * Creation date: (11/22/00 10:51:36 AM)
     * 
     * @return boolean true if lines intersect
     * @param l
     *            uk.ac.leeds.ccg.geotools.GeoLine
     * @author Mathieu van Loon
     * @version 0.1
     */
    public boolean intersects(GeoLine l, boolean debug)
            throws IllegalArgumentException {
        System.err
                .println("Geoline.intersects is called. this code is buggy. Don't use it.");

        // Check to make sure each line has only two points
        if (this.npoints > 2)
            throw new IllegalArgumentException(
                    "This geoline object has more than 2 points. I can't handle that. sorry");
        if (l.npoints > 2)
            throw new IllegalArgumentException(
                    "The given geoline object has more than 2 points. I can't handle that. sorry");

        // calculate stuff
        double r = ((this.ypoints[0] - l.ypoints[0]) * (l.xpoints[1] - l.xpoints[0]))
                - ((this.xpoints[0] - l.xpoints[0]) * (l.ypoints[1] - l.ypoints[0]));
        double s = ((this.ypoints[0] - l.ypoints[0]) * (this.xpoints[1] - this.xpoints[0]))
                - ((this.xpoints[0] - l.xpoints[0]) * (this.ypoints[1] - this.ypoints[0]));
        double denumerator = ((this.xpoints[1] - this.xpoints[0]) * (l.ypoints[1] - l.ypoints[0]))
                - ((this.ypoints[1] - this.ypoints[0]) * (l.xpoints[1] - l.xpoints[0]));

        // determine whether an intersection exists
        if (denumerator == 0) {
            if (r == 0) {
                // The lines are collinear.
                if (debug)
                    System.out.println("Collinear lines found");
                if (debug)
                    System.out.println("r:" + r + "--s:" + s + "--d:"
                            + denumerator);
                return true;
            } else {
                // The lines are parallel
                if (debug)
                    System.out.println("parallel lines found");
                if (debug)
                    System.out.println("r:" + r + "--s:" + s + "--d:"
                            + denumerator);
                return false;
            }
        }
        if (denumerator > 0) {
            if (0 <= r && r <= denumerator && 0 <= s && s <= denumerator) {
                // an intersection exists
                if (debug)
                    System.out.println("Intersection found");
                if (debug)
                    System.out.println("r:" + r + "--s:" + s + "--d:"
                            + denumerator);
                return true;
            } else {
                // no intersection exists
                if (debug)
                    System.out.println("No Intersection found" + s + "---" + r);
                // if(debug)System.out.println("r:"+r+"--s:"+s+"--d:"+denumerator);
                return false;
            }
        } else {
            if (denumerator <= r && r <= 0 && denumerator <= s && s <= 0) {
                // an intersection exists
                if (debug)
                    System.out.println("R->Intersection found");
                if (debug)
                    System.out.println("R->r:" + r + "--s:" + s + "--d:"
                            + denumerator);
                return true;
            } else {
                // no intersection exists
                r = r / denumerator;
                s = s / denumerator;
                if (debug)
                    System.out.println("R->No Intersection found" + s + "---"
                            + r);
                // if(debug)System.out.println("R->r:"+r+"--s:"+s+"--d:"+denumerator);
                return false;
            }
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("GeoLine : [id ");
        sb.append(id);
        sb.append("] ");
        for (int i = 0; i < npoints; i++) {
            sb.append("{");
            sb.append(xpoints[i]);
            sb.append(",");
            sb.append(ypoints[i]);
            sb.append("} ");
        }
        return sb.toString();
    }
}

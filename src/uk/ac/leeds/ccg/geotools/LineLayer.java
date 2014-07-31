package uk.ac.leeds.ccg.geotools;

import java.awt.Graphics;
import java.awt.Polygon;
import java.util.Vector;


public class LineLayer extends uk.ac.leeds.ccg.geotools.PolygonLayer implements
        LockableSize {
    int defaultSize = 3;

    public void addGeoLine(GeoLine line) {
        this.addGeoLine(line, false);
    }

    public void addGeoLine(GeoLine line, boolean keepQuiet) {
        super.addGeoShape(line, keepQuiet);
    }

    public int getID(GeoPoint p, Scaler s) {
        double w = s.toMap(defaultSize);
        for (int i = 0; i < shapeList.size(); i++) {
            GeoLine temp = (GeoLine) shapeList.elementAt(i);
            if (temp.isNear(p, w))
                return temp.getID();
        }
        return -1;
    }

    public void setDefaultSize(int x) {
        defaultSize = x;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public void fatLine(Graphics g, int x1, int y1, int x2, int y2, int w) {
        int x_diff = x1 - x2;
        x_diff *= x_diff;
        int y_diff = y1 - y2;
        y_diff *= y_diff;
        int x_shift = 0;
        int y_shift = 0;
        if (y_diff < x_diff) {
            x_shift = 0;
            y_shift = 1;
        } else {
            y_shift = 0;
            x_shift = 1;
        }

        g.drawLine(x1, y1, x2, y2);
        int shift = 1;
        for (int index = 1; index < w; index++) {
            g.drawLine(x1 - x_shift * shift, y1 - y_shift * shift, x2 - x_shift
                    * shift, y2 - y_shift * shift);

            shift *= -1;
            if (shift > 0)
                shift += 1;
        }
    }

    public void drawPolyFeature(Graphics g, Polygon p, int width) {
        if (width == 1) {
            g.drawPolyline(p.xpoints, p.ypoints, p.npoints);
        } else {
            //oh boy this is gona be fun
            for (int j = 0; j < p.npoints - 1; j++) {
                fatLine(g, p.xpoints[j], p.ypoints[j], p.xpoints[j + 1],
                        p.ypoints[j + 1], width);
            }
        }

    }

    public void fillPolyFeature(Graphics g, Polygon p) {
        //you cant fill lines!
    }

    public void deleteAllLines() {
        super.shapeList = new Vector();
    }

    public void deleteLine(GeoLine line) {
        super.shapeList.remove(line);
        super.notifyLayerChangedListeners(LayerChangedEvent.GEOGRAPHY);
    }

}
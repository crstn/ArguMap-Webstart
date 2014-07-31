/*
 * @(#)PolygonLayer.java  0.5 17 April 1997  James Macgill
 *
 */

package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.HashSet;
import java.util.Vector;

/**
 * A layer for storing geographic features that can be represented by polygons.
 * A polygon layer maintains a list of GeoPolygons. If each polygon is properly
 * assigned an ID then this will be used for both Highlights and for Shade
 * colour lookups.
 * <p>
 * Used (Like other layers) in themes for display in a Viewer.
 * <p>
 * 
 * Improved selection painting speed by implementing method directly insted of
 * using SimpleLayers repeated calles to paintHighlight
 * 
 * Now extends ShapeLayer, which manages much of the common code used by layers
 * containg GeoShape features
 * 
 * @version 0.7.0, 9 Dec 99
 * @author James Macgill
 */
public class PolygonLayer extends ShapeLayer implements Layer,
        ScaleChangedListener {

    /**
     * Holds the scaled version of the map if bufferScale is on
     */
    protected Polygon polys[][];
    protected int ids[];

    /**
     * setting this to true will force the buffer to be updated on the next call
     * to paintScaled. Note this will not disable buffer all together. after the
     * buffer has been updated, this variable will be set to false. if you want
     * to disable buffering (why would you do that?), you'd need to set
     * bufferScale to false.
     */
    protected boolean forceBufferFlush = true;

    ContiguityMatrix matrix = null;

    /**
     * default constructor for an empty layer
     */
    public PolygonLayer() {
        super();
    }

    /**
     * Constructs a PolygonLayer with bounds but no polygons. <br>
     * Usefull if you know what the exact bounds of the layer will be prior to
     * adding all the GeoPolygons to the layer at a later stage, or if you want
     * the PolygonLayer to 'lie' about its extent.
     * 
     * @param bounds
     *            A GeoRectangle representing a box that will atleast contain
     *            all of the polygons in this layer.
     */
    public PolygonLayer(GeoRectangle bounds) {
        super(bounds);
    }

    /**
     * Do we treat anticlockwise polygons as holes? V.Experimental
     */
    protected boolean anticlockHoles = false;

    /**
     * Adds the specified GeoPolygon this PolygonLayer
     * 
     * @param polygon
     *            The GeoPolygon to be added
     */
    public void addGeoPolygon(GeoPolygon polygon) {
        this.addGeoPolygon(polygon, false);
    }

    /**
     * Adds the specified GeoPolygon this PolygonLayer
     * 
     * @param polygon
     *            The GeoPolygon to be added
     * @param keepquiet
     *            Tell the method not to call a LayerChangedEvent after adding
     *            the polgyon. Setting this to true more or less implies that
     *            you will do this yourselve.
     */
    public void addGeoPolygon(GeoPolygon polygon, boolean keepQuiet) {
        this.setForceBufferFlush(true);
        super.addGeoShape(polygon, keepQuiet);
    }

    /**
     * Use to determine if scale buffering is available for this layer. Layers
     * that use this feature should overide this mehtod.
     * 
     * @return boolean true if scale buffering can be used.
     */
    public boolean isScaleBufferImplemented() {
        return true;
    }

    /**
     * Set anticlockwise polygons up as holes. this is v.experimental at the
     * moment. it defaults to off.
     * 
     * @param flag
     *            boolean that is true if anticlockwise polygons should be
     *            holes.
     */
    public void setAnticlockwiseHoles(boolean flag) {
        this.anticlockHoles = flag;
    }

    /**
     * are anticlockwise polygons treated as holes?
     */
    public boolean isAnticlockwiseHoles() {
        return this.anticlockHoles;
    }

    public void drawPolyFeature(Graphics g, Polygon p, int width) {
        g.drawPolygon(p.xpoints, p.ypoints, p.npoints);
    }

    public void fillPolyFeature(Graphics g, Polygon p) {
        g.fillPolygon(p.xpoints, p.ypoints, p.npoints);
    }

    /**
     * Paints a scaled version of the layer to the given graphics contex. <br>
     * Generaly only called by a theme that contains this layer.
     * 
     * @param gg
     *            A GeoGraphics containing all of the info needed to paint this
     *            layer to screen
     */
    public void paintScaled(GeoGraphics gg) {
        Graphics g = gg.getGraphics();
        Scaler scale = gg.getScale();
        Shader shade = gg.getShade();
        GeoData data = gg.getData();
        ShadeStyle style = gg.getStyle();
        Filter filter = gg.getFilter();
        //Tepory polygons used during scaling.
        GeoPolygon scaledPolygon, temp, tempMain;
        int w = style.getLineWidth();

        //Test to see if the scale has not changed since
        //the last redering && see if we keeped a buffer of the
        //map at that scale.
//        if (isExtentSame(scale.getMapExtent()) && bufferScale == true
//                && forceBufferFlush == false) {
//            for (int i = 0; i < countPolygons(); i++) {
//
//                if (filter == null || filter.isVisible(ids[i])) {
//                    for (int j = 0; j < polys[i].length; j++) {
//                        if (polys[i][j] == null)
//                            continue;
//                        g.setColor(shade.getColor(data.getValue(ids[i])));
//                        if (style.isFilled()) {
//                            fillPolyFeature(g, polys[i][j]);
//                            //g.fillPolygon(polys[i][j]);
//                        }
//                        if (style.isOutlined()) {
//                            if (!style.isLineColorFromShader()) {
//                                g.setColor(style.getLineColor());
//                            }
//                            drawPolyFeature(g, polys[i][j], w);
//                            //g.drawPolygon(polys[i][j]);
//                        }
//                    }
//                }
//            }
//        } else {
            //we don't have a pre scaled version to hand
            //so we caclulate it now.
            if (bufferScale) {
                polys = new Polygon[countPolygons()][];
                ids = new int[countPolygons()];
            }
            Polygon awtPoly;

            for (int i = 0; i < countPolygons(); i++) {
                tempMain = (GeoPolygon) shapeList.elementAt(i);
                if (bufferScale) {
                    polys[i] = new Polygon[tempMain.getNumParts()];
                }

                for (int j = 0; j < tempMain.getNumParts(); j++) {

                    temp = (GeoPolygon) tempMain.getPart(j);
                    GeoRectangle bounds = temp.getBounds();
                    //If the polygon is totaly outside of the current view
                    //don't bother to scale it.
                    if (bounds.createIntersect(scale.getMapExtent()) == null) {
                        continue;
                    }

                    scaledPolygon = scale.scalePolygon(new GeoPolygon(temp));
                    awtPoly = scaledPolygon.toAWTPolygon();
                    //Add thematic colour here
                    /*
                     * if(data==null){ buildData(); }
                     */

                    //get a value for this polygon from the GeoData data.
                    int id = temp.getID();
                    double value = data.getValue(id);
                    //look up a colour for that value from the shader.

                    if (filter == null || filter.isVisible(id)) {
                        g.setColor(shade.getColor(value));

                        if (style.isFilled()) {
                            if (!anticlockHoles
                                    || (anticlockHoles && temp.isClockwise()))
                                fillPolyFeature(g, awtPoly);
                        }
                        if (style.isOutlined()) {
                            if (!style.isLineColorFromShader()) {
                                g.setColor(style.getLineColor());
                            }
                            drawPolyFeature(g, awtPoly, w);
                            //g.drawPolygon(awtPoly);
                        }
                    }
                    if (bufferScale) {
                        //keep a record of the scaled version
                        polys[i][j] = awtPoly;
                        ids[i] = id;
                    }
                }

           // }

            if (lastScale != null) {
                lastScale.removeScaleChangedListener(this);
            }
            lastScale = scale;
            lastScale.addScaleChangedListener(this);

            setForceBufferFlush(false);
        }
    }

    /**
     * Scales and plots a highlighted version of one of the polygons <br>
     * Generaly only called by the theme that holds this layer. <br>
     * The highlight style should be definable, but it isn't yet.
     * 
     * @param g
     *            The graphics to paint into
     * @param scale
     *            The scaler to use.
     * @param id
     *            The id of the polygon to highlight
     */
    public void paintHighlight(Graphics g, Scaler scale, int id,
            ShadeStyle style) {
        GeoPolygon scaledPolygon;
        int w = style.getLineWidth();
        for (int i = 0; i < countPolygons(); i++) {
            GeoPolygon temp = (GeoPolygon) shapeList.elementAt(i);
            if (temp.getID() == id) {
                for (int j = 0; j < temp.getNumParts(); j++) {
                    scaledPolygon = scale.scalePolygon((GeoPolygon) temp
                            .getPart(j));

                    //Add thematic colour here
                    if (style.isPaintModeXOR()) {
                        g.setXORMode(Color.black);
                    }
                    if (style.isFilled()) {
                        g.setColor(style.getFillColor());

                        fillPolyFeature(g, scaledPolygon.toAWTPolygon());
                        //g.fillPolygon(scaledPolygon.toAWTPolygon());
                    }
                    if (style.isOutlined()) {
                        g.setColor(style.getLineColor());
                        //g.drawPolygon(scaledPolygon.toAWTPolygon());
                        drawPolyFeature(g, scaledPolygon.toAWTPolygon(), w);
                    }
                    scaledPolygon = null; //Try to help the GC.?
                    g.setPaintMode();
                }
            }
        }
    }

    /**
     * Scales and plots a highlighted version of one of the polygons <br>
     * Generaly only called by the theme that holds this layer. <br>
     * The highlight style should be definable, but it isn't yet. Modified to
     * select all parts of a multi-part polygon.
     * 
     * @param g
     *            The graphics to paint into
     * @param scale
     *            The scaler to use.
     * @param id
     *            The id of the polygon to highlight
     */
    public void paintSelection(Graphics g, Scaler scale, int ids[],
            ShadeStyle style) {
        GeoPolygon scaledPolygon;
        int w = style.getLineWidth();
        for (int i = 0; i < countPolygons(); i++) {
            GeoPolygon temp = (GeoPolygon) shapeList.elementAt(i);
            for (int j = 0; j < ids.length; j++) {
                if (temp.getID() == ids[j]) {
                    for (int k = 0; k < temp.getNumParts(); k++) {
                        scaledPolygon = scale.scalePolygon(new GeoPolygon(
                                (GeoPolygon) temp.getPart(k)));

                        //Add thematic colour here
                        if (style.isPaintModeXOR()) {
                            g.setXORMode(Color.black);
                        }
                        if (style.isFilled()) {
                            g.setColor(style.getFillColor());
                            fillPolyFeature(g, scaledPolygon.toAWTPolygon());
                            //g.fillPolygon(scaledPolygon.toAWTPolygon());
                        }
                        if (style.isOutlined()) {
                            g.setColor(style.getLineColor());
                            //g.drawPolygon(scaledPolygon.toAWTPolygon());
                            drawPolyFeature(g, scaledPolygon.toAWTPolygon(), w);
                        }
                        scaledPolygon = null; //Try to help the GC.?
                        g.setPaintMode();
                    }
                }
            }
        }

    }

    /**
     * Returns the total number of polygons held by the PolygonLayer
     * 
     * @deprecated use countFeatures from super class ShapeLayer
     * @return int The number of polygons in this layer
     */
    public int countPolygons() {
        return countFeatures();
    }

    /**
     * Called when the scale last used to scale this layer has changed. as a
     * result the buffer is of no use and will have to be rebuilt next paint
     * scaled
     * 
     * @param sce
     *            A ScaleChangedEvent as thrown by a scaler.
     */
    public void scaleChanged(ScaleChangedEvent sce) {
        lastScale = null;
    }

    /**
     * Experimental method to track the contiguity matrix for this layer. First
     * call constructs the matrix, unless it has been provided during
     * construction (not yet possible)
     */
    public ContiguityMatrix getContiguityMatrix() {
        if (matrix == null)
            buildContiguityMatrix();
        return matrix;
    }

    public void buildContiguityMatrix() {
        //build one from scratch
        matrix = new ContiguityMatrix();
        for (int i = 0; i < countPolygons(); i++) {
            GeoPolygon temp = (GeoPolygon) shapeList.elementAt(i);
            Vector list = temp.getContiguityList(shapeList);

            HashSet set = new HashSet();
            for (int j = 0; j < list.size(); j++) {
                set.add(list.elementAt(j));
            }
            matrix.addList(temp, set);

        }

    }

    public void setContiguityMatrix(ContiguityMatrix m) {
        matrix = m;
    }

    public void setForceBufferFlush(boolean newValue) {
        forceBufferFlush = newValue;
    }

    public boolean isForceBufferFlush() {
        return forceBufferFlush;
    }

    /**
     * A new experimental method which provides access to the scaled versions of
     * the last set of polygons to be ploted to the screen. <br>
     * In order to work properly scale buffering must be enabled, as this is the
     * <br>
     * default behavour for shapelayers this should not be a problem.
     * 
     * @return Polygon[][] A two dimensional array of awt polygons,(the second
     *         dymension is for multipart polygons).
     */
    public Polygon[][] getScaledShapes() {
        return (Polygon[][]) polys.clone();
    }

    /**
     * Used in conjunction with getScaledPolygons this provides the true id for
     * each shape in the set of scaled polygons
     */
    public int[] getScaledShapesIdList() {
        return (int[]) ids.clone();
    }

    public void deleteAllPolygons() {
        super.shapeList = new Vector();
    }

    public void deletePolygon(GeoPolygon polygon) {
        super.shapeList.remove(polygon);
        super.notifyLayerChangedListeners(LayerChangedEvent.GEOGRAPHY);
    }

}
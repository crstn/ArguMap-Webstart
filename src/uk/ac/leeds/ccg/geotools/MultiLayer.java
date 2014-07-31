package uk.ac.leeds.ccg.geotools;

import java.awt.Graphics;
import java.util.Vector;

/**
 * an <strong>experimental</strong> class to hold multiple layers as if a
 * single layer. Layers are drawn in the order they are added.
 * <p>
 * Ian
 */
public class MultiLayer extends SimpleLayer implements LayerChangedListener {
    static boolean DEBUG = false;
    static String DBC = "ML->";
    public Vector layers = new Vector();
    GeoRectangle bounds = new GeoRectangle();

    public MultiLayer() {
    }

    public void clearLayers(boolean quite) {
        // layers= new Vector();
        layers.clear();
        if (!quite)
            notifyLayerChangedListeners(LayerChangedEvent.DATA);
    }

    public void clearLayers() {
        clearLayers(false);
    }

    public void paintScaled(GeoGraphics gg) {
        GeoRectangle ext = gg.scale.getMapExtent();

        for (int i = 0; i < layers.size(); i++) {
            if (DEBUG) {
                System.out.println(DBC + "painting layer");
            }
            if (((Layer) layers.elementAt(i)).getBounds().intersects(ext)) {
                ((Layer) layers.elementAt(i)).paintScaled(gg);
            } else {
                if (DEBUG) {
                    System.out.println(DBC + "painting layer "
                            + layers.elementAt(i)
                            + " blocked because it is out of bounds");
                    System.out.println(" "
                            + ((Layer) layers.elementAt(i)).getBounds()
                            + " not within " + ext);
                }
            }
        }
    }

    public void paintHighlight(Graphics g, Scaler scale, int id,
            ShadeStyle style) {
        GeoRectangle ext = scale.getMapExtent();
        for (int i = 0; i < layers.size(); i++) {
            if (((Layer) layers.elementAt(i)).getBounds().intersects(ext))
                ((Layer) layers.elementAt(i)).paintHighlight(g, scale, id,
                        style);
        }
    }

    public GeoRectangle getBoundsOf(int id) {
        GeoRectangle ret = new GeoRectangle();
        for (int i = 0; i < layers.size(); i++) {
            ret = ((Layer) layers.elementAt(i)).getBoundsOf(id);
            if (!ret.isEmpty())
                return ret;
        }
        return ret;

    }

    public GeoRectangle getBoundsOf(int[] ids) {
        GeoRectangle ret = new GeoRectangle();
        for (int i = 0; i < layers.size(); i++) {
            ret.add(((Layer) layers.elementAt(i)).getBoundsOf(ids));
        }
        return ret;
    }

    public int getID(double x, double y) {
        int id = -1;
        for (int i = 0; i < layers.size(); i++) {
            id = ((Layer) layers.elementAt(i)).getID(x, y);
            if (id != -1)
                return id;
        }
        return id;
    }

    public int getID(GeoPoint p) {
        return getID(p.x, p.y);
    }

    /**
     * Add a new layer to this multiLayer and send a LayerChanged event for the
     * multiLayer to notify that DATA has changed.
     * 
     * @param l
     *            The layer to add.
     */
    public void addLayer(Layer l) {
        addLayer(l, false);
    }

    /**
     * Add a new layer to this multiLayer.
     * 
     * @param l
     *            The layer to add.
     * @param quite
     *            If false, a LayerChanged event is sent for the multiLayer to
     *            notify that the DATA has changed.
     */
    public void addLayer(Layer l, boolean quite) {
        if (DEBUG) {
            System.out.println("MultiLayer: addLayer ");
        }
        bounds.add(l.getBounds());
        layers.addElement(l);
        l.addLayerChangedListener(this);
        if (!quite)
            notifyLayerChangedListeners(LayerChangedEvent.DATA);
    }

    /**
     * Remove a layer. A LayerChanged event is sent to notify that the DATA has
     * changed.
     * 
     * @param l
     *            The layer to remove
     */
    public void removeLayer(Layer l) {
        removeLayer(l, false);
    }

    /**
     * Remove a layer.
     * 
     * @param l
     *            The layer to remove
     * @param quite
     *            If false, a LayerChanged event is sent to notify that the DATA
     *            has changed
     */
    public void removeLayer(Layer l, boolean quite) {
        if (DEBUG) {
            System.out.println("MultiLayer: removeLayer ");
        }
        layers.removeElement(l);
        l.removeLayerChangedListener(this);
        bounds = new GeoRectangle();
        for (int i = 0; i < layers.size(); i++) {
            bounds.add(((Layer) layers.elementAt(i)).getBounds());
        }
        if (!quite)
            notifyLayerChangedListeners(LayerChangedEvent.DATA);
    }

    /**
     * Return the layers that have been added to this multiLayer.
     */
    public Vector getLayers() {
        return layers; // possibly should clone here?
    }

    public GeoRectangle getBounds() {
        // return bounds;
        // this is a temp fix
        bounds = new GeoRectangle();
        for (int i = 0; i < layers.size(); i++) {
            bounds.add(((Layer) layers.elementAt(i)).getBounds());
        }
        return bounds;
    }

    public void setBounds(GeoRectangle r) {
        bounds = r;
    }

    public void layerChanged(LayerChangedEvent e) {
        if (e.getReason() == LayerChangedEvent.GEOGRAPHY) {
            bounds = new GeoRectangle();
            for (int i = 0; i < layers.size(); i++) {
                bounds.add(((Layer) layers.elementAt(i)).getBounds());
            }
        }
        notifyLayerChangedListeners(e.getReason());
        if (DEBUG) {
            System.out.println("MultiLayer: notifyLayerChanged ");
        }
    }

    public GeoShape getGeoShape(int id) {
        GeoShape result = null;
        for (int i = 0; i < layers.size(); i++) {
            Layer l = (Layer) layers.elementAt(i);
            if (l instanceof ShapeLayer) {
                result = ((ShapeLayer) l).getGeoShape(id);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public int[] getIDs(GeoRectangle box, int mode) {
        int[] ids = new int[0];
        int[] tmp = new int[0];
        for (int i = 0; i < layers.size(); i++) {
            int[] id = ((Layer) layers.elementAt(i)).getIDs(box, mode);
            tmp = ids;
            ids = new int[id.length + ids.length];
            System.arraycopy(tmp, 0, ids, 0, tmp.length);
            System.arraycopy(id, 0, ids, tmp.length, id.length);

        }
        return ids;

    }
}

/*
 * MixedLayer.java
 *
 * Created on August 7, 2001, 2:59 PM
 */

package uk.ac.leeds.ccg.geotools;

/**
 *
 * @author  jamesm
 * @version
 */
public class MixedLayer extends uk.ac.leeds.ccg.geotools.MultiLayer implements uk.ac.leeds.ccg.geotools.Layer {
    protected boolean hasPolygons = false,hasLines = false, hasPoints = false;
    protected PolygonLayer polys;
    protected LineLayer lines;
    protected PointLayer points;
    
    /** Creates new MixedLayer */
    public MixedLayer() {
    }
    
    public MixedLayer(DataSource source){
    }
    
    public void addLayer(Layer l) {
        throw new IllegalArgumentException("Mixed Layer does not support the addition of extra layers, please use MultiLayer instead");
    }
    
    public void addGeoPolygon(GeoPolygon p){
        addGeoPolygon(p,getStatus()!=Layer.COMPLETED);
    }
    
    public void addGeoPoint(GeoPoint p){
        addGeoPoint(p,getStatus()!=Layer.COMPLETED);
    }
    
    public void addGeoLine(GeoLine l){
        addGeoLine(l,getStatus()!=Layer.COMPLETED);
    }
    
    
    
    public void addGeoPolygon(GeoPolygon p,boolean quiet) {
        if(!hasPolygons){
            polys = new PolygonLayer();
            super.addLayer(polys);
            hasPolygons = true;
        }
        polys.addGeoPolygon(p,quiet);
    }
    
    public void addGeoPoint(GeoPoint p,boolean quiet) {
        if(!hasPoints){
            //CK: changed from MarkerLayer to PointLayer because of wrong symbols
            // (MarkerLayer shows triangles instead of circles)
            //points = new MarkerLayer();
            points = new PointLayer();
            super.addLayer(points);
            hasPoints = true;
        }
        points.addGeoPoint(p,quiet);
    }
    
    public void addGeoLine(GeoLine l,boolean quiet) {
        if(!hasLines){
            lines = new LineLayer();
            super.addLayer(lines);
            hasLines = true;
        }
        lines.addGeoLine(l,quiet);
    }
    
    
            
}

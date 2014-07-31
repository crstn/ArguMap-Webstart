package uk.ac.leeds.ccg.geotools;

import java.awt.Graphics;
import java.util.Vector;


public class PointLayer extends uk.ac.leeds.ccg.geotools.ShapeLayer implements LockableSize {
    /**
     * debug switch. used to control debug output. Output will be identified with prefix 'PiLa>'
     */
    private final static boolean DEBUG=false;
    protected int points[][]=null;
    protected int ids[]=null;
    protected int defaultSize = 4;
    
    /**
     * setting this to true will force the buffer to be updated on the next
     * call to paintScaled. Note this will not disable buffer all together.
     * after the buffer has been updated, this variable will be set to false.
     * if you want to disable buffering (why would you do that?), you'd need
     * to set bufferScale to false.
     */
    protected boolean forceBufferFlush = true;
    
    public PointLayer(){
        super(false);
    }
    
    public PointLayer(boolean useQuadtree){
        super(useQuadtree);
    }
    
    public void addGeoPoint(GeoPoint p){
        super.addGeoShape(p);
    }
    public void addGeoPoint(GeoPoint p,boolean quiet){
        super.addGeoShape(p,quiet);
        
    }
    
    /**
     * Paints a scaled version of the layer to the given graphics contex.
     * <br>Generaly only called by a theme that contains this layer.<br>
     * 08/May/2000 - fixed bug#103835 bufferd redraw nologer throws null pointer exception.
     * @author James Macgill JM
     * @param gg A GeoGraphics containing all of the info needed to paint this layer to screen
     */
    public void paintScaled(GeoGraphics gg){
        Graphics g = gg.getGraphics();
        Scaler scale = gg.getScale();
        Shader shade = gg.getShade();
        GeoData data = gg.getData();
        ShadeStyle style = gg.getStyle();
        Filter filter = gg.getFilter();
        int p[],r;
        
        
        
        //if(!style.isFilled()&&!style.isOutlined()) return;
        
        GeoPoint temp;
        r = style.getLineWidth();
        
        int id;
        //Test to see if the scale has not changed since
        //the last redering && see if we keeped a buffer of the
        //map at that scale.
//        if(isExtentSame(scale.getMapExtent()) && bufferScale==true && forceBufferFlush==false){
//            for(int i = 0;i < countFeatures();i++) {
//                if(points[i]==null) continue;
//                p = points[i];
//                if(filter==null || filter.isVisible(ids[i])){
//                    g.setColor(shade.getColor(data.getValue(ids[i])));
//                    if( style.isFilled() ){
//                        g.fillOval(p[0]-r,p[1]-r,2*r+1,2*r+1);
//                    }
//                    if( style.isOutlined() ) {
//                        if(!style.isLineColorFromShader()){
//                            g.setColor(style.getLineColor());
//                        }
//                        g.drawOval(p[0]-r,p[1]-r,2*r+1,2*r+1);
//                    }
//                }
//            }
//        }else{
            //we don't have a pre scaled version to hand
            //so we caclulate it now.
            if(bufferScale){
                points = new int[countFeatures()][2];
                ids = new int[countFeatures()];
            }
            
            
            for(int i = 0;i < shapeList.size() ;i++) {
                temp = (GeoPoint)shapeList.elementAt(i);
                //Clip to visible area.
                if (temp.getBounds().createIntersect(scale.getMapExtent())==null){
                    continue;
                } 
                
                p = scale.toGraphics(temp.getX(),temp.getY());
                
                
                
                //Add thematic colour here
                id = temp.getID();
                if(filter==null || filter.isVisible(id)){
                    double value = data.getValue(id);
                    g.setColor(shade.getColor(value));
                    if(style.isFilled()){
                        g.fillOval(p[0]-r,p[1]-r,2*r+1,2*r+1);
                    }
                    if(style.isOutlined()){
                        if(!style.isLineColorFromShader()){
                            g.setColor(style.getLineColor());
                        }
                        g.drawOval(p[0]-r,p[1]-r,2*r+1,2*r+1);
                    }
                }
                if(bufferScale){
                    //keep a record of the scaled version
                    points[i] = p;
                    ids[i] = id;
                }
                temp = null;
            }
            // we've just updated the buffer, so why force it to flush?
            setForceBufferFlush(false);
        }
//    }
    
    
    public void paintHighlight(Graphics g, Scaler scale, int id, ShadeStyle style) {
        int p[],r;
        r = style.getLineWidth();
        for(int i = 0;i < shapeList.size();i++) {
            GeoPoint temp = (GeoPoint)shapeList.elementAt(i);
            if(temp.getID()==id){
                p = scale.toGraphics(temp.getX(),temp.getY());
                //Add thematic colour here
                g.setColor(style.getFillColor());
                g.fillOval(p[0]-r,p[1]-r,2*r,2*r);
                g.drawOval(p[0]-r,p[1]-r,2*r,2*r);
                return;
            }
        }
    }
    
    
    public void setDefaultSize(int size){
        defaultSize = size;
    }
    
    /**
     * Find the feature that contains this point.
     * <b>Note</b> This method will return the first feature found to contain the point only,
     * even if multiple, overlapping features, contain the same point.
     * @param point The GeoPoint to test each feature against.
     * @return int The ID of the first feature to contain this point, -1 if no feature was found
     */
    public int getID(GeoPoint p,Scaler s){
        double r = s.toMap(defaultSize);
        if(DEBUG)System.out.println("PiLa>Looking");
        for(int i = 0;i < shapeList.size();i++) {
            GeoPoint temp = (GeoPoint)shapeList.elementAt(i);
            
            if(temp.getDistance(p)>=r){continue;}
            if(DEBUG)System.out.println("PiLa>Found one");
            return temp.getID();
        }
        return -1;
    }
    
    /**
     * Called when the scale last used to scale this layer has changed.
     * as a result the buffer is of no use and will have to be rebuilt next
     * paint scaled
     * @param sce A ScaleChangedEvent as thrown by a scaler.
     */
    public void scaleChanged(ScaleChangedEvent sce){
        lastScale = null;
    }
    
    /**
     * Use to determine if scale buffering is available for this layer.
     * Layers that use this feature should overide this mehtod.
     * @return boolean true if scale buffering can be used.
     */
    public boolean isScaleBufferImplemented(){
        return true;
    }
    
    public void setForceBufferFlush(boolean newValue) {
        forceBufferFlush = newValue;
    }
    
    public boolean isForceBufferFlush() {
        return forceBufferFlush;
    }
    
    public void deleteAllPoints() {
        super.shapeList = new Vector();
    }    
    
    public void deletePoint(GeoPoint p){
        super.shapeList.remove(p);
        super.notifyLayerChangedListeners(LayerChangedEvent.GEOGRAPHY);
    }
}


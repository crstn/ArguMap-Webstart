package uk.ac.leeds.ccg.geotools;

import java.awt.Graphics;
public class MapExtentLayer extends SimpleLayer implements uk.ac.leeds.ccg.geotools.Layer
{
    private GeoRectangle box = new GeoRectangle();
    public MapExtentLayer(){
    }
    
    public MapExtentLayer(GeoRectangle r){
        box = r;
    }
        
    public void paintScaled(GeoGraphics g){
        paintScaled(g.getGraphics(),g.getScale(),g.getShade(),g.getData(),g.getStyle());
    } 
    public void paintScaled(Graphics g, Scaler scale,Shader shade,GeoData data,ShadeStyle style)
    {
        box = scale.getMapExtent();
        int p[] = scale.toGraphics(box.x,box.y);
        int w = scale.toGraphics(box.width);
        int h = scale.toGraphics(box.height);
        g.setColor(style.getLineColor());
        g.drawRect(p[0],p[1],w,h);
    }
    
    public void paintHighlight(Graphics g,Scaler scale,int id,ShadeStyle style){
        //do nothing
    }
    
    public GeoRectangle getBounds(){
        return box;
    }
    
    /**
     * This method does not realy make sense for a map extent layer.
     */
     public GeoRectangle getBoundsOf(int id){
        return new GeoRectangle();
     }
     /**
     * This method does not realy make sense for a map extent layer.
     */
     public GeoRectangle getBoundsOf(int[] ids){
        return new GeoRectangle();
     }
     
    
    public int getID(GeoPoint p){
        return 0;
    }
    
    public int getID(double x,double y){
        return 0;
    }
    
    
    
}
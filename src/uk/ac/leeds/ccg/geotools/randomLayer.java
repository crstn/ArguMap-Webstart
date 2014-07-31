package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Graphics;
public class randomLayer extends SimpleLayer implements uk.ac.leeds.ccg.geotools.Layer
{
    private double xl=30,yl=30,rl=30,x,y,r;
    private Color c;
    
    public void setLimits(double xl,double yl,double rl){
        this.xl = xl;
        this.yl = yl;
        this.rl = rl;
    }
    
    public void paintScaled(GeoGraphics g){
        paintScaled(g.getGraphics(),g.getScale(),g.getShade(),g.getData(),g.getStyle());
    }
    
    public void paintScaled(Graphics g, Scaler scale,Shader shade,GeoData data,ShadeStyle style)
    {
        x = Math.random()*xl;
        y = Math.random()*yl;
        r = Math.random()*rl;
        if(shade !=null){
            c = shade.getColor(1);  
            g.setColor(c);
        }
        int p[] = scale.toGraphics(x,y);
        int rs = scale.toGraphics(r);
        if(style.isFilled()){
            g.fillOval(p[0],p[1],rs,rs);
        }
        if(style.isOutlined()){
            g.drawOval(p[0],p[1],rs,rs);
        }
    }
    
    public void paintHighlight(Graphics g,Scaler scale,int id,ShadeStyle style){
        //do nothing
    }
    
    public GeoRectangle getBounds()
    {
        return null;
    }
    
    /**
     * This method does not realy make sense for a random layer.
     */
     public GeoRectangle getBoundsOf(int id){
        return new GeoRectangle();
     }
     /**
     * This method does not realy make sense for a random layer.
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
    
    public GeoData getGeoData(){
        return null;
    }
    
    public void setGeoData(GeoData gd){
        //do nothing;
    }
}
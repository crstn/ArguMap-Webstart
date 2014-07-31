package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Graphics;
public class testLayer extends SimpleLayer implements uk.ac.leeds.ccg.geotools.Layer
{
    private double x,y,r;
    private Color c;
    public testLayer(){
        x = Math.random()*30d;
        y = Math.random()*30d;
        r = Math.random()*30d;
        c = new Color((float)Math.random(),(float)Math.random(),(float)Math.random());
    }
    
    public void setLimits(double xl,double yl,double rl){
        x = Math.random()*xl;
        y = Math.random()*yl;
        r = Math.random()*rl;
    }    

    public void setGeoData(GeoData d){
        //do nothing;
    }
    
    public GeoData getGeoData(){
        return null;
    }

    public void paintScaled(GeoGraphics g){
        paintScaled(g.getGraphics(),g.getScale(),g.getShade(),g.getData(),g.getStyle());
    }
    public void paintScaled(Graphics g, Scaler scale,Shader shade,GeoData data,ShadeStyle style)
    {
        int p[] = scale.toGraphics(x,y);
        int rs = scale.toGraphics(r);
        g.setColor(c);
        g.fillOval(p[0],p[1],rs,rs);
    }
    
    public void paintHighlight(Graphics g,Scaler scale,int id,ShadeStyle style){
        //do nothing
    }
    
    public GeoRectangle getBounds(){
        return new GeoRectangle(0d,0d,30d,30d);
    }
    
    public GeoRectangle getBoundsOf(int id){
        return new GeoRectangle();
    }
    
    public GeoRectangle getBoundsOf(int[] ids){
        return new GeoRectangle();
    }
    
    public int getID(GeoPoint p){
        return -1;
    }
    
    
    public int getID(double x,double y){
        return -1;
    }
}
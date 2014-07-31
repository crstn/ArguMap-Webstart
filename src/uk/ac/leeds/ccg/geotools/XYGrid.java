package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Graphics;

import uk.ac.leeds.ccg.geotools.misc.FormatedString;
public class XYGrid extends uk.ac.leeds.ccg.geotools.SimpleLayer
{
	private final static boolean DEBUG=true;
	double olatStep=200000,olonStep=200000;
    int latDiv=10,lonDiv=10;
		GeoRectangle bounds = new GeoRectangle(-180,-90,360,180);
		public void setBounds(GeoRectangle g){
		  bounds=new GeoRectangle(g);
		}
    public GeoRectangle getBounds()
    {
        return new GeoRectangle(bounds);
    }

    public GeoRectangle getBoundsOf(int id[])
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return null;
    }

    public GeoRectangle getBoundsOf(int id)
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return null;
    }

    public int getID(GeoPoint p)
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return 0;
    }

    public int getID(double x, double y)
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return 0;
    }

    public void paintHighlight(Graphics g, Scaler scale, int id, ShadeStyle style)
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
    }

    public void paintScaled(GeoGraphics gg)
    {
        Graphics g = gg.getGraphics();
        Scaler scale = gg.getScale();
        Shader shade = gg.getShade();
        ShadeStyle style = gg.getStyle();
        
        //System.out.println(scale.getMapExtent());
        GeoRectangle realExtent = scale.getMapExtent();
        GeoRectangle extent = realExtent.createIntersect(bounds);
				if(extent==null) return;
        if(bounds.isContainedBy(extent)) extent=bounds;
        int p[];
        g.setColor(Color.gray);
				double x = extent.getX();
				double y = extent.getY();
				double lonStep = olonStep;
				double latStep = olatStep;
				
				double width = extent.getWidth();
				double height = extent.getHeight();
				while((width/lonStep)<4.0)lonStep/=2.0;
				while((height/latStep)<4.0)latStep/=2.0;
				
//				latStep=lonStep=Math.max(Math.min(latStep,lonStep),5.0);
                if(DEBUG)System.out.println("Step Size "+latStep+":"+lonStep);
        //latStep=lonStep=Math.max(1.0,(int)Math.min(latStep,lonStep));

				int xstart = (int)Math.ceil((int)(x/lonStep)*lonStep-lonStep);
				while(xstart<bounds.x)xstart+=lonStep;
				int xend = (int)Math.floor((int)((x+width)/lonStep)*lonStep+lonStep);
				while(xend>(bounds.x+bounds.width))xend-=lonStep;
				int ystart = (int)Math.ceil((int)(y/latStep)*latStep-latStep);
				while(ystart<bounds.y)ystart+=latStep;
				int yend = (int)Math.floor((int)((y+height)/latStep)*latStep+latStep);
				while(yend>(bounds.y+bounds.height))yend-=latStep;
        
        double inc = Math.min((width/(100.0)),(height/(100.0)));
        if(DEBUG)System.out.println("x/lat "+x+" "+xstart+" "+latStep+" "+inc);

        for(double lon=xstart;lon<=xend;lon+=lonStep){
            for(double lat=extent.y;lat<=(extent.y+extent.height);lat+=inc){
                 p = scale.toGraphics(lon,lat);
                 if(lon==0.0){
                  g.setColor(Color.red);
                 }else{
                  g.setColor(Color.gray);
                 }
                 g.drawOval(p[0],p[1], 1,1);
            }
            g.setColor(Color.black);
						g.setXORMode(Color.white);
						if(extent.contains(lon,extent.y)){
							p = scale.toGraphics(lon,extent.y);
							g.drawString(FormatedString.format(""+lon,0),p[0]+1,p[1]-2);
						}
						g.setPaintMode(); 
        }
       
        if(DEBUG)System.out.println("y/lon "+y+" "+ystart+" "+lonStep+" "+inc);
        for(double lat=ystart;lat<=yend;lat+=latStep){
            for(double lon=extent.x;lon<=(extent.x+extent.width);lon+=inc){
                 p = scale.toGraphics(lon,lat);
                 if(lat==0.0){
                  g.setColor(Color.red);
                 }else{
                  g.setColor(Color.gray);
                 }
                 g.drawOval(p[0],p[1], 1,1);
                 
            }
            g.setColor(Color.black);
						g.setXORMode(Color.white);
						if(extent.contains(extent.x,lat)){
							p = scale.toGraphics(extent.x,lat);
							g.drawString(FormatedString.format(""+lat,0),p[0]+1,p[1]);
						}
						g.setPaintMode(); 
        }
            
    }
    

}

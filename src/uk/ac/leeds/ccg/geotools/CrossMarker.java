package uk.ac.leeds.ccg.geotools;
import java.awt.Color;
import java.awt.Graphics;

public class CrossMarker implements uk.ac.leeds.ccg.geotools.Marker
{
    public void paintScaled(GeoGraphics gg,GeoPoint p,int size)
    {
        Scaler s = gg.getScale();
        ShadeStyle st = gg.getStyle();
        int mid[] = s.toGraphics(p);
				int s4 = (int)Math.ceil(size/4.0);
				int x1 = mid[0];
				int y1 = mid[1];
        Graphics g = gg.getGraphics();
        int x[] = {x1-size,x1-s4,x1-s4,x1+s4,x1+s4,x1+size,x1+size,
				x1+s4,x1+s4,x1-s4,x1-s4,x1-size};
        int y[] = {y1+s4,y1+s4,y1+size,y1+size,y1+s4,y1+s4,y1-s4,y1-s4,y1-size,
				y1-size,y1-s4,y1-s4};
        g.setColor(st.getFillColor());
        g.fillPolygon(x,y,12);
        g.setColor(Color.black);
        g.drawPolygon(x,y,12);
    }
    
    public void paintHighlight(Graphics g,GeoPoint p,int size,Scaler scale,ShadeStyle style) {
     
        int mid[] = scale.toGraphics(p);
				int s4 = (int)Math.ceil(size/4.0);
				int x1 = mid[0];
				int y1 = mid[1];
        
        size+=2;
        int x[] = {x1-size,x1-s4,x1-s4,x1+s4,x1+s4,x1+size,x1+size,
				x1+s4,x1+s4,x1-s4,x1-s4,x1-size};
        int y[] = {y1+s4,y1+s4,y1+size,y1+size,y1+s4,y1+s4,y1-s4,y1-s4,y1-size,
				y1-size,y1-s4,y1-s4};
        g.setColor(style.getFillColor());
        g.fillPolygon(x,y,12);
        g.setColor(Color.black);
        g.drawPolygon(x,y,12);
        
    }
    
}

package uk.ac.leeds.ccg.geotools;
import java.awt.Color;
import java.awt.Graphics;

public class TriangleMarker implements uk.ac.leeds.ccg.geotools.Marker
{
    public void paintScaled(GeoGraphics gg,GeoPoint p,int size)
    {
        Scaler s = gg.getScale();
        ShadeStyle st = gg.getStyle();
        int mid[] = s.toGraphics(p);
        Graphics g = gg.getGraphics();
        int x[] = {mid[0]-size,mid[0],mid[0]+size};
        int y[] = {mid[1]+size,mid[1]-size,mid[1]+size};
        g.setColor(st.getFillColor());
        g.fillPolygon(x,y,3);
        g.setColor(Color.black);
        g.drawPolygon(x,y,3);
    }
    
    public void paintHighlight(Graphics g,GeoPoint p,int size,Scaler scale,ShadeStyle style) {
     
        int mid[] = scale.toGraphics(p);
        
        size+=2;
        int x[] = {mid[0]-size,mid[0],mid[0]+size};
        int y[] = {mid[1]+size,mid[1]-size,mid[1]+size};
        g.setColor(style.getFillColor());
        g.fillPolygon(x,y,3);
        g.setColor(Color.black);
        g.drawPolygon(x,y,3);
        
    }
    
}
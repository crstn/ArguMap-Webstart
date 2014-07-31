package uk.ac.leeds.ccg.geotools;
import java.awt.Color;
import java.awt.Graphics;

public class CircleMarker implements uk.ac.leeds.ccg.geotools.Marker
{
    public void paintScaled(GeoGraphics gg,GeoPoint p,int size)
    {
        Scaler s = gg.getScale();
        ShadeStyle st = gg.getStyle();
        int mid[] = s.toGraphics(p);
        Graphics g = gg.getGraphics();
        //int x[] = {mid[0]-size,mid[0]-size,mid[0]+size,mid[0]+size};
        //int y[] = {mid[1]-size,mid[1]+size,mid[1]+size,mid[1]-size};
        if(st.isFilled()){
          g.setColor(st.getFillColor());
          g.fillOval(mid[0]-size,mid[1]-size,size*2,size*2);
        }
        if(st.isOutlined()){
          g.setColor(Color.black);
          g.drawOval(mid[0]-size,mid[1]-size,size*2,size*2);
        }
    }
    
    public void paintHighlight(Graphics g,GeoPoint p,int size,Scaler scale,ShadeStyle style) {
     
        Scaler s = scale;
        ShadeStyle st = style;
        int mid[] = s.toGraphics(p);
        size = st.getLineWidth();
        //int x[] = {mid[0]-size,mid[0]-size,mid[0]+size,mid[0]+size};
        //int y[] = {mid[1]-size,mid[1]+size,mid[1]+size,mid[1]-size};
        if(st.isFilled()){
          g.setColor(st.getFillColor());
          g.fillOval(mid[0]-size,mid[1]-size,size*2,size*2);
        }
        if(st.isOutlined()){
          g.setColor(Color.black);
          g.drawOval(mid[0]-size,mid[1]-size,size*2,size*2);
        }
        
    }
    
}



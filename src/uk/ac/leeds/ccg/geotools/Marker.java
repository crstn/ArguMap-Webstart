package uk.ac.leeds.ccg.geotools;
import java.awt.Graphics;

public interface Marker
{
    public void paintScaled(GeoGraphics g,GeoPoint p,int size);
    public void paintHighlight(Graphics g,GeoPoint p,int size,Scaler scale,ShadeStyle style);
}
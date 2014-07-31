package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Enumeration;

public class GraphLayer extends PointLayer
{
    Marker m = new TriangleMarker();
    public GraphLayer(GeoData x,GeoData y){
        
        Enumeration ids = x.getIds();
        int id;
        while(ids.hasMoreElements()){
            id = ((Integer)ids.nextElement()).intValue();
            addGeoPoint(new GeoPoint(id,x.getValue(id),y.getValue(id)));
        }
    }
/*
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
*/
    public void paintSelection(Graphics g,Scaler scale,int ids[],ShadeStyle style){
        System.out.println("Painting selecetion");
        g.setColor(Color.green);
        GeoGraphics gg = new GeoGraphics(g, scale, null, null, null, style, null, 0);
        for(int i = 0;i < shapeList.size();i++) {
			GeoPoint temp = (GeoPoint)shapeList.elementAt(i);
			for(int j=0;j<ids.length;j++){
			    if(temp.getID()==ids[j]){
			        m.paintScaled(gg,temp,10);
			    }
			}
		}
	}
    public void paintScaled(GeoGraphics gg)
    {
    
   
        GeoPoint temp;
        for(int i = 0;i < shapeList.size() ;i++) {
			    temp = (GeoPoint)shapeList.elementAt(i);
			    m.paintScaled(gg,temp,10);
		}
        
    }
    
}
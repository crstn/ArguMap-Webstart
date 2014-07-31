package uk.ac.leeds.ccg.geotools;

import java.awt.Graphics;
import java.util.Hashtable;

public class MarkerLayer extends PointLayer implements LockableSize{
    Marker defaultMarker = new TriangleMarker();
    Hashtable markers = new Hashtable();
    
    public void MarkerLayer(){
        defaultSize=3;
    }
    
    public void addMarker(GeoPoint p){
        addGeoPoint(p);
        
        markers.put(new Integer(p.getID()),defaultMarker);
    }
    
    public void addMarker(GeoPoint p,Marker m){
        markers.put(new Integer(p.getID()),m);
        addGeoPoint(p);
    }
    
    public void setDefaultMarker(Marker m){
        defaultMarker = m;
    }
    public void setDefaultSize(int i){
        defaultSize=i;
    }
    
    
    public void paintScaled(GeoGraphics gg) {
        GeoPoint temp;
        Filter filter= gg.getFilter();
        for(int i = 0;i < shapeList.size() ;i++) {
            temp = (GeoPoint)shapeList.elementAt(i);
            Integer id = new Integer(temp.getID());
            if(filter==null || filter.isVisible(id.intValue())){
                Marker m = (Marker)markers.get(id);
                if(m==null) m=defaultMarker;
                m.paintScaled(gg,temp,defaultSize);
            }
        }
        
    }
    
    public void paintHighlight(Graphics g, Scaler scale, int id, ShadeStyle style) {
        int p[],r;
        r = style.getLineWidth();
        for(int i = 0;i < shapeList.size();i++) {
            GeoPoint temp = (GeoPoint)shapeList.elementAt(i);
            if(temp.getID()==id){
                Integer idObj = new Integer(temp.getID());
                Marker m = (Marker)markers.get(idObj);
                if(m==null) m=defaultMarker;
                m.paintHighlight(g,temp,defaultSize,scale,style);
            }
        }
    }
    
    
    
}

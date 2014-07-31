/*
 * CircleRasterLayer.java
 *
 * Created on 20 June 2001, 12:08
 */

package uk.ac.leeds.ccg.raster;
import java.util.Enumeration;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.CircleLayer;
import uk.ac.leeds.ccg.geotools.Filter;
import uk.ac.leeds.ccg.geotools.GeoCircle;
import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoGraphics;
/**
 *
 * @author  James Macgill
 * @version 
 */
public class CircleRasterLayer extends uk.ac.leeds.ccg.raster.RasterLayer {
    GeoData gd=null;
    CircleLayer cl;
    Filter filter;
    private double size = 1000;
    /** Creates new CircleRasterLayer */
    public CircleRasterLayer(CircleLayer cl,GeoData gd) {
        this(cl,gd,1000);
    }
    
    public CircleRasterLayer(CircleLayer cl,GeoData gd,double size) {
        this.gd = gd;
        this.cl = cl;
        this.size = size;
        filterCircles(null);
        //setRaster(new circleRaster(cl.getGeoCircles(),gd,step,cl.getBounds()));
    }
    
    public void paintScaled(GeoGraphics g){
        Filter f=g.getFilter();
        System.out.println("pr filter");
            filterCircles(f);
        System.out.println("Painting");
        paintScaled(g.getGraphics(),g.getScale(),g.getShade(),g.getData(),g.getStyle());
    }
        
    public void filterCircles(Filter f){
        System.out.println("Filtering");
        java.util.Vector list = cl.getShapes();
        Vector newList = new Vector();
        Enumeration enumer = list.elements();
        while(enumer.hasMoreElements()){
            GeoCircle gc = (GeoCircle)enumer.nextElement();
            
            if(f==null || f.isVisible(gc.getID())){
                newList.addElement(gc);
            }
            
            //setRaster(new circleRaster(newList,gd,1000,cl.getBounds()));
        }
        r= new circleRaster(newList,gd,size,cl.getBounds());
    }

}

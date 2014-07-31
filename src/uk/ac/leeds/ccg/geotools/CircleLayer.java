package uk.ac.leeds.ccg.geotools;

import java.awt.Graphics;
import java.util.Vector;

/**
 * A layer for containing a feature set of GeoCircles
 * Circle Layer now extends ShapeLayer which now does a lot of the common work for layers
 *
 * @version 0.7.0, 9 Dec 1999
 * @author James Macgill
 */
public class CircleLayer extends ShapeLayer implements Layer, LockableSize {
    

    
    /**
     * Switches locked radius on and off
     */
    protected boolean lockRadiusOn = false;
  
    
    /**
     * Locked radius size
     */
    public int lockedRadius = 10;
    
    

    /**
     * Adds the specified GeoPolygon to the GeoMap
     * @param polygon The GeoCircle to be added
     */
	public void addGeoCircle(GeoCircle circle) {
		super.addGeoShape(circle);
	}

  
	
	  
	/**
	 * Sets the lockeRadius switch
	 */
	public void setIsLockRadiusOn(boolean flag){
	    lockRadiusOn = flag;
	}
	
	/**
	 * Sets radus in pixels to use if lock radius is enabled
	 */
	public void setLockedRadius(int size){
	    lockedRadius = size;
	}
    /**
     * Paints a scaled version of the layer to the given graphics contex.
     * <br>Generaly only called by a theme that contains this layer.
     * @param gg A GeoGraphics containing all of the info needed to paint this layer to screen
     */
    public void paintScaled(GeoGraphics gg){
   	    Graphics g = gg.getGraphics();
        Scaler scale = gg.getScale();
        Shader shade = gg.getShade();
        GeoData data = gg.getData();
        ShadeStyle style = gg.getStyle();
        Filter filter = gg.getFilter();
		int p[],r;
		
		if(!style.isFilled()&&!style.isOutlined()) return;
		//System.out.println(shapeList.size());
		GeoCircle temp;
		r = lockedRadius;
		int id;
		for(int i = 0;i < shapeList.size() ;i++) {
			temp = (GeoCircle)shapeList.elementAt(i);
			
			p = scale.toGraphics(temp.getX(),temp.getY());
			if(!lockRadiusOn){
			    r = scale.toGraphics(temp.getRadius());
			}
			
			//scaledPolygon = scale.scalePolygon(new GeoPolygon(temp));
			
			//Add thematic colour here
			id = temp.getID();
			if(filter==null || filter.isVisible(id)){
			    double value = data.getValue(id);
			    g.setColor(shade.getColor(value));
			    if(style.isFilled()){
			    g.fillOval(p[0]-r,p[1]-r,2*r+1,2*r+1);
			    }
			    if(style.isOutlined()){
			        if(!style.isLineColorFromShader()){
			            g.setColor(style.getLineColor());
			        }
			        g.drawOval(p[0]-r,p[1]-r,2*r,2*r);
			    }
			}
			
			
			
			
			//polys[i] = scaledPolygon.toAWTPolygon();
			temp = null;
		}
	}

    

    /**
	* Find the feature that contains this point.
	* <b>Note</b> This method will return the first feature found to contain the point only,
	* even if multiple, overlapping features, contain the same point.
	* @param point The GeoPoint to test each feature against.
	* @return int The ID of the first feature to contain this point, -1 if no feature was found
	*/
	public int getID(GeoPoint p,Scaler s){
	    if(!lockRadiusOn){
	        return super.getID(p);
	    }
	    double r = s.toMap(lockedRadius);
	    for(int i = 0;i < shapeList.size();i++) {
			GeoCircle temp = (GeoCircle)shapeList.elementAt(i);
			temp.setRadius(r);
			if(temp.contains(p)){
			    return temp.getID();
			}
		}
		return -1;
	}
	
    /**
	 * Scales and plots a highlighted version of one of the polygons
	 */
    public void paintHighlight(Graphics g,Scaler scale,int id,ShadeStyle style){
		int p[],r;
		
		r = lockedRadius;
		for(int i = 0;i < shapeList.size();i++) {
			GeoCircle temp = (GeoCircle)shapeList.elementAt(i);
			if(temp.getID()==id){
				p = scale.toGraphics(temp.getX(),temp.getY());
				if(!lockRadiusOn){
			        r = scale.toGraphics(temp.getRadius());
			    }
				//Add thematic colour here
				g.setColor(style.getFillColor());
				g.fillOval(p[0]-r,p[1]-r,2*r,2*r);
				g.drawOval(p[0]-r,p[1]-r,2*r,2*r);
				return;
			}
		}
	}
	
	


    /**
     * Returns the total number of circles held by the GeoMap
     * @deprecated use countFeatures from super class instead
     */
	public int countCircles() {
		return super.countFeatures();
	}
	
	
	 
	/**
	 * Returns all of the GeoCircles in this layer in a vector
	 * @deprecated use the getFeatures method from ShapeLayer  instead
	 */
	public Vector getGeoCircles(){	    
		return shapeList;
	}
}

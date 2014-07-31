/*
 * @(#)GeoCircle.java  0.5 17 Feb 1998  James Macgill
 *
 */

package uk.ac.leeds.ccg.geotools;

import java.util.Enumeration;
import java.util.Vector;

/**
* This class is intended for storing circles such as those that make up cartograms
*
*
* @author Jamaes Macgill
* @version 0.6.3 11 October 1999
*/
public class GeoCircle extends GeoShape {
	

	/**
	 * Circle centroid coordinates
	 */
	private double xcent,ycent;

	/**
	 * The circles radius
	 */
	private double radius;

	/**
	 * Minimum bounding box for polygon
	 */
	//private GeoRectangle bBox = new GeoRectangle(); //*The bounding box

	/**
	 * Create an empty polygon
	 */
	public GeoCircle(){setBounds(new GeoRectangle());} //Empty
	public GeoCircle(int id,GeoPoint cent,double r) {
		this(id,cent.getX(),cent.getY(),r);
	}

    /**
     * Construct a circle with full details
     *
     * @param id circle ID
     * @param xcent x coordinate of centroid
     * @param ycent y coordinate of centroid
     * @param radius r radius of circle
     */
	public GeoCircle(int id,double xcent,double ycent,double r) {
		this.id = id;
		this.xcent = xcent;
		this.ycent = ycent;
		radius = r;
		//Update the bounding box
		setBounds(new GeoRectangle());
        extendBounds(xcent-radius,ycent-radius);
        extendBounds(xcent+radius,ycent+radius);
	    //bBox = new GeoRectangle(xcent-radius,ycent+radius,(double)((int)(2d*radius)+1),(double)((int)(2d*radius)+1));
	    //bBox = new GeoRectangle(xcent-radius,ycent-radius,2*radius,2*radius);
	    //bBox = new GeoRectangle(xcent,ycent,2*radius,2*radius);
        
	}

	
	public void setCentre(GeoPoint p){
		setCentre(p.getX(),p.getY());
	}
	public void setCentre(double x, double y){
		xcent=x;
		ycent=y;
		setBounds(new GeoRectangle());
		extendBounds(xcent-radius,ycent-radius);
		extendBounds(xcent+radius,ycent+radius);
	}
	public void setRadius(double r){
		radius=r;
		setBounds(new GeoRectangle());
		extendBounds(xcent-radius,ycent-radius);
		extendBounds(xcent+radius,ycent+radius);
	}
	
    /**
     * Calculates and returns the area of this circle
     * @return double the area of the circle
     */
        public double getArea(){
            return Math.PI*Math.pow(getRadius(),2);
        }
    public GeoPoint getCentroid(){
        return new GeoPoint(xcent,ycent);
    }

    public Vector getPoints(){
        Vector v = new Vector();
        v.addElement(new GeoPoint(xcent,ycent));
        return v;
    }

	/**
	 * Return the  ID
	 */
	 public int getID(){
	    return id;
	 }
	 
	 /**
	  * Return Radius
	  */
	  public double getRadius(){return radius;}
	  
	 /**
	  * Return center
	  */
	  public GeoPoint getCentrid(){
	    return new GeoPoint(xcent,ycent);
	  }
	  
	  public double getX(){
	    return xcent;
	  }
	  public double getY(){return ycent;}
	  


	  /**
	   * returns true if the specified point is inside the circle
	   */
	   public boolean contains(GeoPoint p){
	    if (!getBounds().contains(p)){
	        return false;
	    }
	    double dist = Math.sqrt(Math.pow(p.x-xcent,2)+Math.pow(p.y-ycent,2));
	    if(dist<radius){
	        return true;
	    }
	    return false;
	   
	   }

       GeoPoint testContains = new GeoPoint(0,0); 
	   public boolean contains(double x,double y){
	    testContains.setLocation(x,y);
	    return contains(testContains);
	   }
	   
	   public boolean isContainedBy(GeoCircle c){
	    double dist;
	    GeoPoint p1 = c.getCentroid();
	    GeoPoint p2 = getCentroid();
	    dist = p1.getDistance(p2);
	    return(dist+getRadius()<=c.getRadius());
	   }
	   
	   public boolean isContainedBy(GeoShape container){
	    if(container instanceof GeoCircle){return isContainedBy ((GeoCircle)container);}
	    //if bounding boxes do not interact at all return false
	    if(!getBounds().intersects(container.getBounds())) return false;
	    
	    //work in reverse for circles
	    //if any of the containers points are inside, return false
	    
	    Enumeration e = container.getPoints().elements();
        boolean inside = false;
        //test each point individualy, abort if any point is inside
        while(e.hasMoreElements() && !inside){
            GeoPoint p = (GeoPoint)e.nextElement();
            inside = contains(p);
        }
        return !inside;
	   }
	   
	   /**
	    * This GeoCircle described in a string
	    * @return String the Desction in the form 'GeoCircle [id i] x,y,r
	    */
	    public String toString(){
	        return ("GeoCircle [id "+id+"] "+xcent+","+ycent+","+radius);
	    }
	   
	  
}




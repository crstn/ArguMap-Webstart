/**
 * GeoShape is the abstract base for all GeoTools shapes
 *
 * @(#)GeoShape.java  11 October 1999  James Macgill
 * @since 0.6.3
 * @author James Macgill
 **/

package uk.ac.leeds.ccg.geotools;

import java.util.Enumeration;
import java.util.Vector;


/**
 * The abstract base class for all GeoTools shape objects
 * 
 * @author James Macgill
 * @since 0.6.3
 * Modified 12/May/2000 JM to implement the IDReferenced interface
 */
public abstract class GeoShape implements IDReferenced
{
    /**
    * An ID to reference this shape
    */
    protected int id;
    
    
	/**
	 * Minimum bounding box for shape
	 */
	private GeoRectangle bBox;

    
	protected Vector subParts;    


    
    /**
     * Gets a full list of all the points that make up this shape.
     * For Polygons and rectangles this would be the vertecis, for circles
     * it would be the center point.
     * 
     * @return A Vector of GeoPoints for each point of this shape
     * @since 0.6.3
     */
    public abstract Vector getPoints();
    
    /**
     * Tests to see if the specified point is contained by this shape.
     * Points that lie on the edge of the shape are officialy 
     * undefined. However the intention is to define this as INSIDE.
     * 
     * @param p The GeoPoint to test for containership
     * @return A boolean, true for inside, otherwise false. On edge is undefined
     * @since 0.6.3
     */
    public abstract boolean contains(GeoPoint p);
    
    
    /**
     * Test to see if this shape is inside another shape.
     * This should only be true if the entire of the shape appears
     * to be inside the shape.
     *
     * In this implementation, every point must be inside of
     * container.
     * 
     * @param s The GeoShape to be inside of.
     * @return A boolean, true if this shape is inside container.
     * @since 0.6.3
     */
    public boolean isContainedBy(GeoShape container){
        Enumeration e = getPoints().elements();
        boolean inside = true;
        //test each point individualy, abort if any point is outside
        while(e.hasMoreElements() && inside){
            GeoPoint p = (GeoPoint)e.nextElement();
            inside = container.contains(p);
        }
        return inside;
    }
    
    
    /**
     * Tests to see if the two shapes intersect at all.
     * 
     * in this implementation it returns true if either shape
     * contains any point from the other shape<p>
     * this is error prone.
     *
     * @param s The shape to test for intersection.
     * @return A boolean, true if both shapes cross in some way.
     * @since 0.6.3
     */
    public boolean intersects(GeoShape s){
        Enumeration e = s.getPoints().elements();
        boolean inside = false;
        //quick test of bounding boxes.
        if(!s.getBounds().intersects(getBounds())) return false;
        //test each point individualy, return if any point is inside
        while(e.hasMoreElements() && !inside){
            GeoPoint p = (GeoPoint)e.nextElement();
            inside = contains(p);
        }
        //now test the other way around
        if (inside) return true;
        e=getPoints().elements();
        while(e.hasMoreElements() && !inside){
            GeoPoint p = (GeoPoint)e.nextElement();
            inside = s.contains(p);
        }
        return inside;
    }
    
    /**
     * Gets an ID for this shape.
     * There is no implication of uniquness with this ID.
     * 
     * @return An id holding the ID for this shape.
     * @since 0.6.3
     */
    public int getID(){
        return id;
    }
    
    protected void extendBounds(double x,double y){
        bBox.add(x,y);
    }
    protected void extendBounds(GeoPoint p){
        bBox.add(p);
    }
    protected void setBounds(GeoRectangle b){
        bBox = b;
    }
    
    /**
     * Gets the minimum boudning rectangle for this shape
     * 
     * @return A GeoRectangle that will contain this shape.
     * @since 0.6.3
     */
    public  GeoRectangle getBounds(){
        return bBox;
    }
    
    public GeoRectangle getMultiPartBounds(){
        if(this.subParts==null){
            return getBounds();
        }
        GeoRectangle multiBounds = new GeoRectangle();
        multiBounds.add(getBounds());
        for(int i=0;i<subParts.size();i++){
            multiBounds.add(((GeoShape)subParts.elementAt(i)).getBounds());
        }
        return multiBounds;
    }
    
    
    public int getNumParts(){
	    if(subParts==null)return 1;
	    return subParts.size()+1;
	}
	public boolean isMultiPart(){
	    return getNumParts()>1;
	}
	
	public GeoShape getPart(int i){
	    if(i==0) return this;
	    if(subParts==null)return null;
	    return (GeoShape)subParts.elementAt(i-1);
	}
    
    public void addSubPart(GeoShape sub){
	    if(this.subParts==null){
	        subParts = new Vector();
	    }
	    
	    bBox.add(sub.getBounds());
	    subParts.addElement(sub);
	}
        
        public abstract double getArea();
	
    
    //to be implemented
    //getBufferd(double d)
    
}

package uk.ac.leeds.ccg.geotools;

import java.io.Serializable;
import java.util.Vector;
/**
 * A double presision point.
 * Typicaly used to store coordinates of at a high enough precision for 
 * geographic use.
 */
public class GeoPoint extends GeoShape implements Serializable
{
    public double x,y;
    /**
     * Default constructor, x,y set to 0,0.
     */
    public GeoPoint(){
       this(0,0);
    }
    /**
     * Constructs a double precision point at the given coordinates.
     * @param p A double[] where x = [0] and y = [1]
     */
    public GeoPoint(double[] p){
        this(p[0],p[1]);
    }
    /**
     * Constructs a double precision point at the given coordinates.
     * @param x A double
     * @param y A double
     */
    public GeoPoint(double x,double y){
        this.x = x;
        this.y = y;
        //setBounds(new GeoRectangle(x,y,0,0));
    }
    
    /**
     * Constructs a double precision point at the given coordinates with a set id.
     * @param x A double
     * @param y A double
     * @param id An int of the ID for this point
     */
    public GeoPoint(int id,double x,double y){
        this.x = x;
        this.y = y;
        this.id = id;
    }
    
    /**
     * Constructs a double precision point at the given coordinates.
     * @param p A point to build this point as a copy of.
     */
    public GeoPoint(GeoPoint p){
        this.x = p.x;
        this.y = p.y;
        this.id = p.id;
    }
    
    //As of 0.7.6 geopoints are nolonger imutable, which has implications
    //regarding safety, but should leed to less desruction/construction cycles.
    public void setLocation(double x,double y){
        this.x = x;
        this.y=y;
        setBounds(new GeoRectangle(x,y,0,0));
    }
    
    
    /**
     * gets the x part of this coordinate
     */
    public double getX(){
        return x;
    }
    
    /**
     * gets the y part of this coordinate
     */
    public double getY(){ return y; }
    
    /**
     * As points have no area this always returns 0
     * @return the area of the point i.e. 0
     */
    public double getArea(){return 0;}
    
    /**
     * calcualtes the distance between two points
     * @since 0.6.3
     * @return a double for the distance
     */
     public double getDistance(GeoPoint p){
        return Math.sqrt(((p.x-x)*(p.x-x))+((p.y-y)*(p.y-y)));
     }
     
     /**
      * Gets a description of this point
      * @since 0.7.0
      * @return A string in the form 'GeoPoint x,y'
      */
      public String toString(){
        return ("GeoPoint "+x+","+y);
      }
      
      /**
       * Tests to see if this point contains a given point<br>
       * I feel that points can not contain anything however.
       * @param p A GeoPoint to test
       * @return boolean Always false, as points can not contain anything.
       */
       public boolean contains(GeoPoint p){
        return this.equals(p);
       }
       
       /**
        * Tests to see if the two shapes intersect at all.<p>
        * 
        * This returns true if s contains this point, or s is a point
        * with identical coordinates to this point.
        * 
        * @param s The shape to test for intersection.
        * @return A boolean, true if both shapes cross in some way.
        * @since 0.7.1
        */
       public boolean intersects(GeoShape s){
            if(s instanceof GeoPoint){
                GeoPoint p = (GeoPoint)s;
                return (p.x==x && p.y==y);
            }
            return s.contains(this);
        }
        
        /**
         * Gets the bounds for this point.
         * <br> As points have position but no size, a GeoRectangle is returned with
         * width and height set to 0
         * @return GeoRectangle the bounds of this point
         */
         public GeoRectangle getBounds(){
            return new GeoRectangle(x,y,0,0);
         }
         
         /**
          * Gets this point in a vector
          * @return Vector a vecotor containg a clone of this point.
          */
          public Vector getPoints(){
            Vector v = new Vector();
            GeoPoint p = new GeoPoint(this);
            v.addElement(p);
            return v;
          }
          
          
          /**
           * tests if the specifed point is at the same location as this point
           * returns true if x and y coordinates match
           * @return boolean true if equal
           */
           public boolean equals(Object o){
            if(! (o instanceof GeoPoint)){return false;}
            GeoPoint p = (GeoPoint)o;
            return (p.x==x && p.y == y);
           }
    
            /**
             * Aritmeticaly adds the specified point to this point.
             * the X and Y values of both points are added and stored in this point.
             * 
             * @author James Macgill JM
             * @since 0.7.7.1 10/May/2000
             * @param p The point to add to this point.
             */
             public void add(GeoPoint p){
                setLocation(x+p.x,y+p.y);
             }
             
             /**
             * Aritmeticaly subtracts the specified point to this point.
             * the X and Y values of point p is subtracted from those of this point.
             * 
             * @author James Macgill JM
             * @since 0.7.7.1 10/May/2000
             * @param p The point to subtract from this point.
             */
             public void subtract(GeoPoint p){
                setLocation(x-p.x,y-p.y);
             }
             
             /**
             * Aritmeticaly divides the specified point by the given value.
             * the X and Y values of this point are both divided by the specified value.
             * 
             * @author James Macgill JM
             * @since 0.7.7.1 10/May/2000
             * @param v The double to divide the x and y values of this point by.
             */
             public void divide(double v){
                setLocation(((double)x)/v,((double)y)/v);
             }
             
             /**
             * Aritmeticaly multiplies the specified point by the given value.
             * the X and Y values of this point are both divided by the specified value.
             * 
             * @author James Macgill JM
             * @since 0.7.7.2 9/June/2000
             * @param v The double to multiply the x and y values of this point by.
             */
             public void multiply(double v){
                setLocation(((double)x)*v,((double)y)*v);
             }
             
             /**
              * Calculates the distance between this and the specified point.
              *
              * @author James Macgill JM
              * @since 0.7.7.1 10/May/2000
              * @param p The GeoPoint to calcualte the distance to
              */
              public double dist(GeoPoint p){
                return (Math.sqrt((Math.pow(x-p.x,2)+(Math.pow(y-p.y,2)))));
              }
}
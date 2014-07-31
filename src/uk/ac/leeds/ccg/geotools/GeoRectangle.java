/*
 * @(#)GeoRectangle.java  11 October 1999 James Macgill
 *
 */
package uk.ac.leeds.ccg.geotools;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A double presision rectangle.<p>
 * It is usful for describing a GeoReferanced Rectangle
 * Used by GeoPolygon to return a bounding box.
 *
 * @version 0.50, 17 Apr 1997
 * @author James Macgill
 */

public class GeoRectangle extends GeoShape implements Serializable,Cloneable {

	private final static boolean DEBUG=false;
	private boolean initialised = false;
	public double height;
	public double width;
	public double x;
	public double y;

	public boolean equals(GeoRectangle r){
		if(r==null) return false;
		//System.out.println("x "+(x==r.x));
		//System.out.println("y "+(y==r.y));
		//System.out.println("width "+(width==r.width));
		//System.out.println("height "+(height==r.height));
		return(x==r.x&&y==r.y&&width==r.width&&height==r.height);
	}
	/**
	 * Initalise Rectangle to be infinatly 'inside-out', very useful for bounding box creation
	 */
 	public GeoRectangle() {
		height = 0;
		width = 0;
		x = Double.POSITIVE_INFINITY;
		y = Double.POSITIVE_INFINITY;
		initialised = false;
	}

	/**
	 * Initalise GeoRectangle with another one.
	 *
	 * @param rect Existing GeoRectangle to initalise new one
	 */
	public GeoRectangle(GeoRectangle rect) {
		this.x = rect.x;
		this.y = rect.y;
		this.width = rect.width;
		this.height = rect.height;
		initialised = true;
	}

	/**
	 * Initalise Rectangle with a list of points
	 *
	 * @param x left most coordinate of GeoRectangle
	 * @param y bottom most coordinate of GeoRectangle
	 * @param width Width of GeoRectangle
	 * @param height Height of GeoRectangle
	 */
	public GeoRectangle(double x,double y,double width,double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		initialised = true;
	}

    /**
	 * Initalise Rectangle with a single point
	 *
	 * @param p A GeoPoint
	 */
	public GeoRectangle(GeoPoint p) {
		this(p.x,p.y,0d,0d);
	}

	/**
	* Compares a point with the rectangle, if it is outside then the rectangle is expanded to
	* to fit.
	*
	* @param px X coordinate of point to fit into GeoRectangle
	* @param py Y coordinate of point to fit into GeoRectangle
	*/
	public void add(double px,double py) {
		if(initialised){
		    if (px < x){
		        width = (x+width)-px;
		        x = px;
	    	}
		    if (py < y){
		        height = height + Math.abs(py-y);
		        y = py;
	    	}
		    if (px > x + width) width = px-x;
		    if (py > y + height) height = py-y;
		}
		else{
		    x=px;
		    y=py;
		    initialised = true;
		}
	}

	/** determines if this georectangle is empty or not
	 */
	public boolean isEmpty(){
		return(width==0||height==0);
	}

	/**
	* Compares a point with the rectangle, if it is outside then the rectangle is expanded to
	* to fit.
	*
	* @param p GeoPoint  to fit into GeoRectangle
	*/
	public void add(GeoPoint p) {
	    add(p.x,p.y);
	}
	
	public Object clone() {
	    return new GeoRectangle(this);
	}
	
	/**
	 * Expands the rectangle so that it is large enough to hold the
	 * specified GeoRectangle
	 *
	 * @param rect The GeoRectangle to be inserted
	 */
	public void add(GeoRectangle rect) {
	    if(!rect.initialised)return;
		//turn rectangle into points
		double x1 = rect.x;
		double x2 = rect.x + rect.width;
		double y1 = rect.y;
		double y2 = rect.y + rect.height;
		//Add each point to the GeoRectangle
		this.add(x1,y1);
		this.add(x2,y2);
	}

	/**
	 * Gets the bounds of this georectanle which,<br>
	 * by defenition is a clone of the GeoRectangle
	 * @return The GeoRectangle which bounds this georectangle
	 */
	public GeoRectangle getBounds(){
	    return (GeoRectangle)clone();
	}

        /**
         * Initialise the data in GeoRectangle
         */
	public void setBounds(GeoRectangle r){
            setBounds(r.x,r.y,r.width,r.height);
	}

        /**
         * Initialise the data in GeoRectangle
         */
	public void setBounds(double x_new, double y_new, double width_new, double height_new) {
            x=x_new;
            y=y_new;
            height=height_new;
            width=width_new;
	}

        /**
         * Initialise the data in GeoRectangle based on a
         * <a href="http://opengis.org">Open GIS</a> BBox format, ie
         * "xmin,ymin,xmax,ymax".  If the string is incorrectly formatted,
         * or has invalid data, then the data is set to GeoRectangle() and
         * false is returned.
         * @param BBox A string of the format "xmin,ymin,xmax,ymax".
         * @return Boolean specifying TRUE if the geoRectangle was created
         * successfully, false otherwise.
         */
	public boolean setBounds(String BBox)
        {
            boolean valid=false;
            StringTokenizer st = new StringTokenizer(BBox,",");
            if (st.countTokens()==4) {
                double x1 = Double.valueOf(st.nextToken()).doubleValue();
                double y1 = Double.valueOf(st.nextToken()).doubleValue();
                double x2 = Double.valueOf(st.nextToken()).doubleValue();
                double y2 = Double.valueOf(st.nextToken()).doubleValue();
                setBounds(x1,y1,x2-x1,y2-y1);
                valid=true;
            }
            return valid;
        }


	public void extendBounds(double x,double y){
	    add(x,y);
	}
	public void extendBounds(GeoPoint p){
	    add(p);
	}

	/**
	 * subtracts the given GeoRectangle from this one
	 * @author Mathieu van Loon <mathieu@PLAYcollective.com>
	 */
	public GeoRectangle subtract(GeoRectangle box2) {
		GeoRectangle reply = new GeoRectangle(this.x-box2.x, this.y-box2.y, this.width, this.height);
		return reply;
	}

	public Vector getPoints(){
	    double x1,y1,x2,y2; // coords of the first box

		x1=this.x;
		y1=this.y; // bottom left
		x2=this.x+this.width;
		y2=this.y+this.height; // top right

		Vector p = new Vector();
		p.addElement(new GeoPoint(x1,y1));
		p.addElement(new GeoPoint(x1,y2));
		p.addElement(new GeoPoint(x2,y1));
		p.addElement(new GeoPoint(x2,y2));
		return p;

	}
	
	/**
	 * Tests specificaly for GeoRectangle->GeoRectanle intersectsion
	 * otherwise uses GeoShape.instersects test.
	 * 
	 * @return A boolean, true if intersection occures.
	 */
    public boolean intersects(GeoShape s){
        if(s instanceof GeoRectangle){return intersects((GeoRectangle)s);}
				if(DEBUG)System.out.println("going super");
        return super.intersects(s);
    }
	/**
	 * GeoRectangle specific interesect test
	 * @return true if two GeoRectangles intersect.
	 */
	public boolean intersects(GeoRectangle box2){
	  double b1x1,b1y1,b1x2,b1y2; // coords of the first box
		double b2x1,b2y1,b2x2,b2y2; // coords of the second box

		GeoPoint [] corners1 = new GeoPoint[4];
		GeoPoint [] corners2 = new GeoPoint[4];
		corners1[0]=new GeoPoint(this.x,this.y); // BL
		corners1[1]=new GeoPoint(this.x+this.width,this.y); // BR
		corners1[2]=new GeoPoint(this.x,this.y+this.height); // TL
		corners1[3]=new GeoPoint(this.x+this.width,this.y+this.height); // TR

		corners2[0]=new GeoPoint(box2.x,box2.y); // BL
		corners2[1]=new GeoPoint(box2.x+box2.width,box2.y); // BR
		corners2[2]=new GeoPoint(box2.x,box2.y+box2.height); // TL
		corners2[3]=new GeoPoint(box2.x+box2.width,box2.y+box2.height); // TR
		// do they meet

		for(int i=0;i<4;i++){
			if(this.contains(corners2[i])) return true;
		}
		for(int i=0;i<4;i++){
			if(box2.contains(corners1[i])) return true;
		}
                //the above test is not enough, JM added the following but it is not fully tested
                
                if(createIntersect(box2)==null)return false;

		return true;
	}

	public void intersect(GeoRectangle box2){
	    GeoRectangle r = createIntersect(box2);
	    this.x=r.x;
	    this.y=r.y;
	    this.width=r.width;
	    this.height=r.height;
	}

	public GeoRectangle createIntersect(GeoRectangle box2){
		double b1x1,b1y1,b1x2,b1y2; // coords of the first box
		double b2x1,b2y1,b2x2,b2y2; // coords of the second box
		double nx1,ny1,nx2,ny2; // coords of the new box

		b1x1=this.x;
		b1y1=this.y; // bottom left
		b1x2=this.x+this.width;
		b1y2=this.y+this.height; // top right

		b2x1=box2.x;
		b2y1=box2.y; // bottom left
		b2x2=box2.x+box2.width;
		b2y2=box2.y+box2.height; // top right

		// do they meet

		if(b1x1>b2x2||b2x2<b1x1||b1y1>b2y2||b2y2<b1y1||b1x2<b2x1||b1y2<b2y1){
			return (GeoRectangle)null;
		}

		// find the left edge
		if(b1x1<b2x1){
			nx1=b2x1;
		}else{
			nx1=b1x1;
		}
		// find the right edge
		if(b1x2>b2x2){
			nx2=b2x2;
		}else{
			nx2=b1x2;
		}
		// find the top edge
		if(b1y2>b2y2){
			ny2=b2y2;
		}else{
			ny2=b1y2;
		}
		// find the bottom edge
		if(b1y1>b2y1){
			ny1=b1y1;
		}else{
			ny1=b2y1;
		}

		return(new GeoRectangle(nx1,ny1,(nx2-nx1),(ny2-ny1)));
	}

	/** returns the 4 rectangles that are left after r is removed from this 
		* rectangle. Some or all of the rectangles returned may be null.
		* the side boxes are from base to top of this rectangle, the top/bottom
		* boxes are from the edge to edge of the intersect <br>
		* the return order is left,top,right,bottom
		*/
	public GeoRectangle [] remainder(GeoRectangle r){
		GeoRectangle [] ret = new GeoRectangle[4];
		//if they don't intersect there is no remainder? or we could return r?
		GeoRectangle inter = this.createIntersect(r);
		System.out.println("inter "+inter);
		if(inter==null) return ret;
		// left hand edge
		if((inter.x-this.x)>0)ret[0]=new GeoRectangle(this.x,this.y,(inter.x-this.x),this.height);
		// top edge
		if((inter.width)>0&&(this.y+this.height)-(inter.y+inter.height)>0)ret[1]=new GeoRectangle(inter.x,inter.y+inter.height,
			(inter.width),(this.y+this.height)-(inter.y+inter.height));
		// right hand edge
		if((this.x+this.width)-(inter.x+inter.width)>0)ret[2]=new GeoRectangle(inter.x+inter.width,this.y,(this.x+this.width)-(inter.x+inter.width),this.height);
		// bottom edge
		if(inter.width>0&&(inter.y-this.y)>0)ret[3]=new GeoRectangle(inter.x,this.y, inter.width,(inter.y-this.y));
		return ret;
	}

		

	/**
	* this function returns true if the given rectangle is contained within this rectangle
	* Creation date: (11/21/00 2:52:37 PM)
	* @return boolean
	* @param g uk.ac.leeds.ccg.geotools.GeoRectangle
	* @author Mathieu van Loon <mathieu@PLAYcollective.com>
	*/
	public boolean contains(GeoRectangle g) {
		return(this.contains(g.x,g.y,g.width,g.height));
	}

	public boolean contains(double x, double y, double width, double height) {
		// We check for the following conditions :
		// the top of this rectangle is at least as high as the top of rectangle g
		// the bottom of this rectangle is at most as high as the top of rectangle g
		// the left of this rectangle is at most as right as the left of rectangle g
		// the right of this rectangle is at most as left as the right of rectangle g
		// If all of these conditions hold, rectangle g is contained within this rectangle
		return (this.x <= x && (this.x+this.width) >= (x+width) && (this.y+this.height) >= (y+height) && this.y <= y);
	}


	/**
	 * Returns a standard java.awt.Rectangle version of the GeoRectangle
	 *
	 * @return A new AWT.Rectangle may return null if GeoRectangle can't be converted
	 */
	public Rectangle toAWTRectangle() {
		//This might throw number exceptions, add a try{} or throw it?
		try {
		    return new Rectangle((int)x,(int)y,(int)width,(int)height);
		}
		catch(NumberFormatException e){
		    return null;
		}
	}

	 /**
     * Checks if the specified point lies inside a Georectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public boolean inside(double x, double y) {
	    return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y-this.y) < this.height);
    }


		public double getHeight(){
			return height;
		}
		public double getWidth(){
			return width;
		}

                public double getArea(){
                    return getWidth()*getHeight();
                }

		public double getX(){
			return x;
		}
		public double getY(){
			return y;
		}
    /**
     * Checks if the specified point lies inside a Georectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public boolean contains(double x, double y) {
	    return (x >= getX()) && ((x - getX()) <= getWidth()) && (y >= getY()) && ((y-getY()) <= getHeight());
	    //return (x >= this.x) && ((x - this.x) < this.width) && (y >= this.y) && ((y-this.y) < this.height);
    }
    
    /**
     * Checks if the specified point lies inside a Georectangle.
     * @param p the GeoPoint to test
    
     */
    public boolean contains(GeoPoint p) {
	    return contains(p.x,p.y);
	}
	
	/**
	 * Returns the coordinates of the GeoRectangle as a String
	 * @return A string in the form "X,Y : Width,Height"
	 */
	 public String toString(){
	    String box = new String(String.valueOf(x)+","+String.valueOf(y));
        box = box + ":"+ String.valueOf(width)+","+String.valueOf(height);
        return box;
     }

         /**
          * Returns the coordinates of the GeoRectangle in
          * <a href="http://opengis.org">Open GIS</a> BBox format, ie
          * (xmin,ymin,xmax,ymax).
          * @return A string in the form "xmin,ymin,xmax,ymax".
          */
         public String toBBoxString(){
             double xmin,ymin,xmax,ymax;
             String box;
             if (this.initialised) {
                 // x,y is either bottom left, or top right of box
                 if (width>0){
                     xmin=x;
                     xmax=x+width;
                     ymin=y;
                     ymax=y+height;
                 } else {
                     xmin=x+width;
                     xmax=x;
                     ymin=y+height;
                     ymax=y;
                 }
                 box = new String(
                         String.valueOf(xmin)+","+
                         String.valueOf(ymin)+","+
                         String.valueOf(xmax)+","+
                         String.valueOf(ymax));
             } else {
                 box="";
             }
             return box;
         }

	public static void main(String args[]){
		GeoRectangle r1 = new GeoRectangle(10,10,10,10);
		GeoRectangle r2 = new GeoRectangle(5,5,10,10);
		System.out.println(""+r1);
		System.out.println(""+r2);
		GeoRectangle [] remain;
		remain = r1.remainder(r2);
		System.out.println("R1 remainder r2 "+remain[0]+" "+remain[1]+" "+remain[2]+" "+remain[3]);
		remain = r2.remainder(r1);
		System.out.println("R2 remainder r1 "+remain[0]+" "+remain[1]+" "+remain[2]+" "+remain[3]);
		r1 = new GeoRectangle(0,0,10,10);
		r2 = new GeoRectangle(5,5,4,4);
		System.out.println(""+r1);
		System.out.println(""+r2);
		remain = r1.remainder(r2);
		System.out.println("R1 remainder r2 "+remain[0]+" "+remain[1]+" "+remain[2]+" "+remain[3]);
		remain = r2.remainder(r1);
		System.out.println("R2 remainder r1 "+remain[0]+" "+remain[1]+" "+remain[2]+" "+remain[3]);
		r1 = new GeoRectangle(10,10,10,10);
		r2 = new GeoRectangle(5,40,10,10);
		System.out.println(""+r1);
		System.out.println(""+r2);
		remain = r1.remainder(r2);
		System.out.println("R1 remainder r2 "+remain[0]+" "+remain[1]+" "+remain[2]+" "+remain[3]);
		remain = r2.remainder(r1);
		System.out.println("R2 remainder r1 "+remain[0]+" "+remain[1]+" "+remain[2]+" "+remain[3]);
	}
}

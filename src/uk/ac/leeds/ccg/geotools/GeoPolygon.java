/*
 * @(#)GeoPolygon.java  0.5 17 April 1997  James Macgill
 *
 */

package uk.ac.leeds.ccg.geotools;

import java.awt.Polygon;
import java.util.Vector;

/**
* This class is intended for storing georaphical polygons such as boundry data
* for wards, costlines etc.<p>
*
* @author Jamaes Macgill
* @version 0.5 17 April 1997
*/
public class GeoPolygon extends GeoShape {

	/**
	 * control debugging. For now, either turn it off or on
	 */
	private final static boolean DEBUG=false;
	/**
	 * Performance switch, to turn of use of Vector. use with CARE
	 */
	private boolean useVector = true;

	/**
	 * Polygon centroid coordinates
	 */
	private double xcent,ycent;

	/**
	 * Number of points in polygon
	 */
	public int npoints;

	/**
	 * Area of polygon
	 */
	 private double area;

	/**
	 * Array of points
	 */
	public double xpoints[],ypoints[];
    private Vector storedPoints = null;
    private boolean pointsStored = false;

    private boolean areaDone,centroidDone;


	public static final int OUTSIDE = -1;
	public static final int NA = Integer.MAX_VALUE;

  /**
   * Create an empty polygon
   */
  public GeoPolygon(){

    if ( useVector ) storedPoints = new Vector();
    
    setBounds(new GeoRectangle());
    
  } //Almost Empty

    /**
     * Construct a polygon with full details
     *
     * @param id polygon ID
     * @param xcent x coordinate of centroid
     * @param ycent y coordinate of centroid
     * @param xpoints vector of x values in Doubles (npoints in size)
     * @param ypoints vector of y values in Doubles (npoints in size)
     * @param npoints number of points
     */
  public GeoPolygon(int id,double xcent,double ycent,Vector xpoints,Vector ypoints) {

    if ( useVector ) storedPoints = new Vector();
    
    this.id = id;
    this.npoints = xpoints.size();
    //System.out.println("Building with "+npoints);
    this.xpoints = new double[npoints];
    this.ypoints = new double[npoints];
    
    for(int i=0;i<npoints;i++){
      this.xpoints[i] = ((Double)xpoints.elementAt(i)).doubleValue();
      this.ypoints[i] = ((Double)ypoints.elementAt(i)).doubleValue();
    }
    
    //Update the bounding box
    setBounds(new GeoRectangle());
    for(int i = 0;i < npoints;i++) {
      extendBounds(this.xpoints[i],this.ypoints[i]);
      if(useVector) storedPoints.addElement(new GeoPoint(this.xpoints[i],this.ypoints[i]));
    }
    //calculateArea();
    if(xcent == NA || ycent == NA){
      //calculateCentroidLocation();
    }
    else{
      this.xcent = xcent;
      this.ycent = ycent;
      centroidDone=true;
    }
    
  }

    /**
     * Construct a polygon with full details
     *
     * @param id polygon ID
     * @param xcent x coordinate of centroid
     * @param ycent y coordinate of centroid
     * @param xpoints array of x values (npoints in size)
     * @param ypoints array of y values (npoints in size)
     * @param npoints number of points
     */
  public GeoPolygon(int id,double xcent,double ycent,double[] xpoints,double[] ypoints,int npoints) {

    if ( useVector ) storedPoints = new Vector();
    
    this.id = id;

    this.xpoints = new double[npoints];
    this.ypoints = new double[npoints];
    this.npoints = npoints;
    //Add a try here to catch failed array copy
    System.arraycopy(xpoints,0,this.xpoints,0,npoints);
    System.arraycopy(ypoints,0,this.ypoints,0,npoints);
    //Update the bounding box
    setBounds(new GeoRectangle());
    for(int i = 0;i < npoints;i++) {
      extendBounds(xpoints[i],ypoints[i]);
      if(useVector)storedPoints.addElement(new GeoPoint(xpoints[i],ypoints[i]));
    }
    calculateArea();
    if(xcent == NA || ycent == NA){
      calculateCentroidLocation();
    }
    else{
      this.xcent = xcent;
      this.ycent = ycent;
    }
    //	statsDone=true;
  }

  /**
   * Construct a polygon with no defined centroid
   *
   * @param id Polygon ID
   * @param xpoints array of x values (npoints in size)
   * @param ypoints array of y values (npoints in size)
   * @param npoints number of points
   */
  public GeoPolygon(int id,double[] xpoints,double[] ypoints,int npoints) {
    this(id,NA,NA,xpoints,ypoints,npoints);
  }

    /**
     * Construct a polygon and generate its centroid
     * 08/May/2000 added call to calculateCentroidLocation
     * @author James Macgill JM
     * @param id Polygon ID
     * @param points[] an Array of points to build the polygon off
     */
  public GeoPolygon(int id,GeoPoint[] points) {
    
    if ( useVector ) storedPoints = new Vector();
    
    this.id = id;
    //this.xcent = 0;
    //this.ycent = 0;
    this.xpoints = new double[points.length];
    this.ypoints = new double[points.length];
    this.npoints = points.length;
    setBounds(new GeoRectangle());
    for(int i = 0;i < npoints;i++) {
      xpoints[i] = points[i].x;
      ypoints[i] = points[i].y;
      extendBounds(xpoints[i],ypoints[i]);
      if(useVector)storedPoints.addElement(new GeoPoint(xpoints[i],ypoints[i]));
    }
    calculateArea();
    calculateCentroidLocation();
  }

    /**
	 * Construct a polygon with no defined centroid or ID
	 *
	 * @param xpoints array of x values (npoints in size)
     * @param ypoints array of y values (npoints in size)
     * @param npoints number of points
     */
	public GeoPolygon(double[] xpoints,double[] ypoints,int npoints) {
		this(-1,NA,NA,xpoints,ypoints,npoints);}

  /**
   * Construct an empty polygon with ID and centroid only
   *
   * @param id polygon ID
   * @param xcent x coordinate of centroid
   * @param ycent y coordinate of centroid
   */
  public GeoPolygon(int id,double xcent,double ycent) {
    
    if ( useVector ) storedPoints = new Vector();
    
    this.id = id;
    this.xcent = xcent;
    this.ycent = xcent;
    setBounds(new GeoRectangle());

  }

    /**
     * Construct a GeoPolygon based on an existing GeoPolygon
     *
     * @param poly GeoPolygon to clone
     */
	public GeoPolygon(GeoPolygon poly) {
		this(poly.id,poly.xcent,poly.ycent,poly.xpoints,poly.ypoints,poly.npoints);}

    /**
     * Add a vertex to the polygon
     *
     * @param x x coordinate of point to add
     * @param y y coordinate of point to add
     */
	public void addPoint(double x,double y) {
		if(npoints > 0) {
			double xtemp[];
			double ytemp[];
			xtemp = xpoints;
			ytemp = ypoints;
			xpoints = new double[npoints+1];

			System.arraycopy(xtemp,0,xpoints,0,npoints);
			xtemp=null;
			ypoints = new double[npoints+1];
			System.arraycopy(ytemp,0,ypoints,0,npoints);
			ytemp=null;
		}
		else {
			xpoints = new double[1];
			ypoints = new double[1];
		}
		npoints++;
		xpoints[npoints-1] = x;//-1 to account for 0 array indexing
		ypoints[npoints-1] = y;

		if(useVector)storedPoints.addElement(new GeoPoint(x,y));//is this risky?
		extendBounds(x,y);
		//calculateArea();//TODO JM:v.wastefull, save until a area request is made?
		//calculateCentroidLocation();//TODO JM:v.wastefull, save until a area request is made?
		areaDone=centroidDone=false;
	}




	/**
	 * Add a vertex to the polygon
	 *
	 * @param p A GeoPoint to add
	 */
	 public void addPoint(GeoPoint p){
	    addPoint(p.x,p.y);
	 }

	/**
	* Returns a standard AWT.Polygon from the GeoPolygon
	* <em>note</em> The GeoPolygon used doubles , the Polygon uses ints
	* so there will be precision loss.
	*/
		public Polygon toAWTPolygon() {
		int x[] = new int[npoints];
		int y[] = new int[npoints];
		// This next bit might throw a number exception put in try{} ?
		for(int i=0;i<npoints;i++) {
			x[i] = (int)xpoints[i];//Convert float to integer
			y[i] = (int)ypoints[i];
		}
		return new Polygon(x,y,npoints);
	}



	/**
	 * Return the polygon ID
	 */
	 public int getID(){
	    return id;
	 }
	 public void setID(int id){
		this.id=id;
	 }

	 /**
	  * Return the area of this polygon.
	  * This value is calculated from the polygon data on construction
	  * and upon any changes to the polygon's points.
	  * @since 0.7.7.1 8/May/2000
	  * @author James Macgill
	  * @return double The area of this polygon
	  */
	 public double getArea(){
	    if(!areaDone){
	        calculateArea();
	    }
	    return area;
	 }

	 /**
	  * Calculates and returns the perimeter of this polygon.<br>
	  * Value is currently generated per-request.
	  * @author James Macgill JM
	  * @since 0.7.7.1 8/May/2000
	  * @return double the perimeter of this polygon
	  */
	 public double getPerimeter(){
	    double dist = 0;
        int i;
        for(i=0;i<npoints-1;i++){
            dist+=Math.sqrt(((xpoints[i]-xpoints[i+1])*(xpoints[i]-xpoints[i+1]))+((ypoints[i]-ypoints[i+1])*(ypoints[i]-ypoints[i+1])));
        }
        return dist;
	 }

	 /**
	  * Return the centroid of this polygon.
	  * this value was either supplied to the constructor or has been
	  * calculated automaticaly.<br>
	  * automaticaly calculated centroids are not guaranteed to be inside the
	  * polygon, insted they represent the polygons center of mass.
	  * @since 0.7.7.1 8/May/2000
	  * @author James Macgill JM
	  * @return GeoPoint The centroid of this polygon
	  */
	 public GeoPoint getCentroidLocation(){
	    if(!centroidDone){
	        calculateCentroidLocation();
	    }
	    return new GeoPoint(xcent,ycent);
	 }

	 /**
	  * Return the number of points that make the polygon
	  */
	  public int getNPoints(){
	    return npoints;
	  }

	/**
	  * get the geopoints as as an array of x or y
	  * @author Mathieu van Loon <mathieu@PLAYcollective.com>
	  */
	public double[] getPointsAsArrayX() {
		return xpoints;
	}
	public double[] getPointsAsArrayY() {
		return ypoints;
	}

	/**
	 * Drop the use of a Vector to store the points. This method
	 * is available for performance reasons. You will only want to use
	 * this method if you store a lot of GeoPolygons, and you wish
	 * to decrease the memory load.
	 * @author Mathieu van Loon <mathieu@PLAYcollective.com>
	 */
	public void dropVector() {
		useVector = false;
		storedPoints = null;
	}

	public final Vector getPoints(){
		if(useVector)
		{
          	return storedPoints;
		} else
		{
			Vector reply = new Vector(npoints);
			for(int i=0;i<npoints;i++)
			{
				reply.addElement(new GeoPoint(xpoints[i],ypoints[i]));
			}
			return reply;
		}
	}

	  public boolean equals(Object o){
            if(! (o instanceof GeoPolygon)){return false;}
            GeoPolygon b = (GeoPolygon)o;
            if(b.getID()!=this.getID()){return false;}

            return equalsIgnoreID(b);
         }

          public boolean equalsIgnoreID(GeoPolygon b){
            int iCount = 0;
            Object gpp1,gpp2;
            Vector vP1,vP2;

            vP1 = this.getPoints();
            vP2 = b.getPoints();

            if(vP1.size()!=vP2.size()){
                return false;
            }

            for(int i=0;i<vP1.size();i++){
                gpp1 = vP1.elementAt(i);
                for(int j=0;j<vP2.size();j++){
                    gpp2 = vP2.elementAt(j);
                    if(gpp1.equals(gpp2)){
                        iCount++;
                        break;
                    }
                }
            }
            if(iCount == vP1.size()){
                return true;
            }
            return false;
        }





	  /**
	   * Tests to see if the polygon is Clockwise<br>
	   * anti-clockwise polygons often represent holes<br>
	   * this is computed on the fly so might be expensive.<br>
	   * Addapted from C function writen by Paul Bourke.
	   * @return boolean true if polygon is clockwise.
	   */
	   public boolean isClockwise(){
	    if (npoints < 3) return false;
	    int j,k,count=0;
	    double z;
	    for (int i=0;i<npoints;i++){
	        j = (i + 1) % npoints;
	        k = (i + 2) % npoints;
            z  = (ypoints[j] - xpoints[i]) * (ypoints[k] - ypoints[j]);
            z -= (ypoints[j] - ypoints[i]) * (xpoints[k] - xpoints[j]);
            if (z < 0)
                count--;
            else if (z > 0)
                count++;
        }
        if (count > 0) return false;
        else if (count < 0) return true;
        else return false;//coliniar points?
       }


	   /**
	    * Calculates the area of the polygon
	    * the result is stored in the private member variable area<br>
	    * Must be called during construction and after any changes to the polygons point set.
	    * N.B. Polygons with anti-clockwise point storage will result in a negative area
	    * @author James Macgill
	    * @since 0.7.7.1 08/May/00
	    */
	    private void calculateArea(){
	     //move polygon to origin to remove negative y values and reduce coordinate sizes
	     GeoRectangle box = getBounds();
	     double xShift = 0-box.x;
	     double yShift = 0-box.y;
	     double area;
	     double total =0;
	     //Calculate area of each tapezoid formed by droping lines from each pair of point to the x-axis.
	     //x[i]<x[i-1] will contribute a negative area
	     for(int i=0;i<npoints-1;i++){
	        area = ((xpoints[i+1]+xShift)-(xpoints[i]+xShift))*(((ypoints[i+1]+yShift)+(ypoints[i]+yShift))/2d);
	        total+=area;
	     }
	     this.area = total;
	     //System.out.println("Area calculated");
	     areaDone=true;
	    }

	    /**
	     * Calculates a waighted centroid location for this polygon.<br>
	     * the result is stored in the private member variables xcent and ycent
	     * overwriting any values that may already be stored their.<br>
	     * area must be set before calling this method, e.g. a call to calculateArea should have been made
	     * during construction.<br>
	     * Polygons must have clockwise point encoding in order to have a centroid, holes (encoded
	     * with anti-clockwise points) have undefined behavior at this point.
	     * N.B. the centroid will be the 'center of mass' but this is not guaranteed to be inside the
	     * polygon.
	     * @author James Macgill JM
	     * @since 0.7.7.1 08/May/2000
	     * @see #calculateArea
	     */
	    protected void calculateCentroidLocation(){
	     GeoRectangle box = getBounds();
	     double xShift = 0-box.x;
	     double yShift = 0-box.y;
	     if(!areaDone)calculateArea();
	     if(area == 0){
	        //test and approx fix for polygons that are effectively lines or points.
	        xcent = (box.x+(box.width/2d));
	        ycent = (box.y+(box.height/2d));
	     }
	     else
	     {

	        double total =0;
	        double x=0,y=0,x1,x2,y1,y2;
	        for(int i=0;i<npoints-1;i++){
	            x1 = xpoints[i]+xShift;
	            x2 = xpoints[i+1]+xShift;
	            y1 = ypoints[i]+yShift;
	            y2 = ypoints[i+1]+yShift;
	            x+= ((y1-y2)*(Math.pow(x1,2)+(x1*x2)+Math.pow(x2,2))/(6*area));
	            y+= ((x2-x1)*(Math.pow(y1,2)+(y1*y2)+Math.pow(y2,2))/(6*area));
	        }
	        xcent = x-xShift;
	        ycent = y-yShift;
	     }
	    }

	   /**
	    * Will test the given polygon to see if it is adjacent to this polygon.
	    * <br>This code is somewhat experimental and has not been fully tested.
	    * @param gp The GeoPolygon to test for contiguity
	    * @return boolean, true if this polygon and gp share two or more verteces that are consecetive in both polygons
	    */
	   public boolean isContiguous(GeoPolygon gp){
	    GeoRectangle bBox = getBounds();
	    GeoRectangle safe = new GeoRectangle(bBox.x-1,bBox.y-1,bBox.width+2,bBox.height+2);
	    if(!gp.getBounds().intersects(safe))return false;
	    Vector a = getPoints();
	    Vector b = gp.getPoints();
	    int aLast = a.size()-2; // one for 0 counting one for duplicate final point.
	    int bLast = b.size()-2;

	    for(int i=0;i<=aLast;i++){
	        if(b.contains(a.elementAt(i))){
	            int indexB = b.indexOf(a.elementAt(i));

	            int nextA = (i+1)%(a.size()-1);

	            if(b.contains(a.elementAt(nextA))){

	                int nextB = b.indexOf(a.elementAt(nextA));
	                if((indexB == 0 && nextB == bLast) || (indexB == bLast && nextB==0)) return true;//wrap round end.
	                if(Math.abs(indexB-nextB)==1)return true;//no break in chain
	                //keep trying.
	            }
	        }
	    }
	    if(subParts!=null){
	            for(int i=0;i<subParts.size();i++){
	                if(((GeoPolygon)subParts.elementAt(i)).isContiguous(gp))return true;
	            }
	    }

	     return false;
	   }

	   /**
	    * Calculates the length of all arcs that are common both to this polygon and to the polygon pased in.
	    * @param gp The polygon to measure shared lengths with
	    * <br>This code is somewhat experimental and has not been fully tested.
	    * @return double the total length of all shared arcs between the two polygons, returns as 0 if there are no shared arcs/
	    */
	   public double getContiguousLength(GeoPolygon gp){
	    double dist = 0;
	    //int start = 0;

	    Vector a = getPoints();
	    Vector b = gp.getPoints();
	    int last = a.size()-1;//0 count
	    GeoPoint start = (GeoPoint)a.elementAt(0);
	    GeoPoint end;
	    for(int i=0;i<last;i++){
	        end = (GeoPoint)a.elementAt((i+1)%last);
	        if(gp.areConsecutive(start,end)){
	            dist+=Math.sqrt((start.x-end.x)*(start.x-end.x)+(start.y-end.y)*(start.y-end.y));
	        }
	        start = end;
	    }

	    if(subParts!=null){
	            for(int i=0;i<subParts.size();i++){
	                dist+=(((GeoPolygon)subParts.elementAt(i)).getContiguousLength(gp));
	            }
	        }
	    return dist;

	   }


	   /**
	    * Tests the two provied points to see if they apeare next to each other in any of this
	    * polygons arcs.  The test is regardless of order.
	    *
	    * <br>This code is somewhat experimental and has not been fully tested.
	    * @param p1 the first point to test
	    * @param p2 the second point to test
	    * @return boolean true if p1 follows p2 or p2 follows p1, false in any other case, including p1 and/or p2 being absent from the polygon
	    *
	    */
	    public final boolean areConsecutive(GeoPoint p1,GeoPoint p2){

	        int i1 = storedPoints.indexOf(p1);
	        if(i1== -1) return false;
	        int i2 = storedPoints.indexOf(p2);
	        if( i2 == -1)return false;
	        if(Math.abs(i2-i1)==1){return true;}
	        if(i1 == storedPoints.size()-2 && i2 ==0) return true;
	        if(i2 == storedPoints.size()-2 && i1 ==0) return true;
	        if(subParts!=null){
	            for(int i=0;i<subParts.size();i++){
	                if(((GeoPolygon)subParts.elementAt(i)).areConsecutive(p1,p1))return true;
	            }
	        }
	        return false;
	    }



	   /**
	    * Provides a list of all of the polygons from the provided list that are adjacent to this one
	    *
	    * <br>This code is somewhat experimental and has not been fully tested.
	    * @param polys a Vector of GeoPolgyons to test against for contiguity.
	    * @return Vector a sub set of polys that contains only the polygons that are adjacent to this one.
	    */
	   public Vector getContiguityList(Vector polys){
	        //build contiguity list
	        Vector list = new Vector();
	        for(int i=0;i<polys.size();i++){
	            if(this.isContiguous((GeoPolygon)polys.elementAt(i))){
	                list.addElement(polys.elementAt(i));
	            }
	        }
    	    list.removeElement(this);
    	    if(subParts!=null){
	            Vector subList;
	            for(int i=0;i<subParts.size();i++){
	                subList = (((GeoPolygon)subParts.elementAt(i)).getContiguityList(polys));
	                subList.removeElement(this);
	                for(int j=0;j<subList.size();j++){
	                    if(!list.contains(subList.elementAt(j))){list.addElement(subList.elementAt(j));}
	                }
	                if(DEBUG)System.out.println("Fixed multipart contiguity by adding "+subList.size());
	            }
	            
	        }
	       return list;
    	        
	   }
	   
	  

	   
	   /**
	    * Given the line described by the two points, this method tests to see if it is internal or external with regards to the set
	    * of polygons listed in the vector.
	    * <br>p1 and p2 SHOULD be points that are in this polygon.
	    * <br>This code is somewhat experimental and has not been fully tested.
	    * @param p1 GeoPoint the first endpoint of the line to test
	    * @param p2 GeoPoint the second endpoint of the line to test
	    * @param polys A Vector of polygons to test the line agains for inside/outside ness.
	    */
	   public boolean isOnOutside(GeoPoint p1,GeoPoint p2,Vector polys){
	    boolean outside = true;
	    
	    for(int j=0;j<polys.size();j++){
	        GeoPolygon testPolygon = ((GeoPolygon)polys.elementAt(j));
	        if(testPolygon == this){continue;}
	        if(testPolygon.areConsecutive(p1,p2)){
	            outside = false;
	            j=polys.size();
	        }
	    }
	    return outside;
	   }
	   
	   /**
	    * Tests this polygon to see if contains at least one edge that is on the outside of the set of polygons
	    * described in the provided set.
	    *
	    * <br>This code is somewhat experimental and has not been fully tested.
	    * @param polys A Vector of polygons to test against
	    * @param boolean true if any edge of this polygon is not contiguous with any of the polgyons listed in polys
	    */
	   public boolean isOnOutside(Vector polys){
	    
	        Vector points = getPoints();
    	    boolean done = false;
	        for(int i=0;i<points.size()-1 ;i++){
	            GeoPoint p1 = (GeoPoint)points.elementAt(i);
	            GeoPoint p2 = (GeoPoint)points.elementAt(i+1);
	            if(isOnOutside(p1,p2,polys)){return true;}
	        }
	        return false;
	   }
	    
	    public static GeoPolygon dissolve(Vector polys){
	       return dissolve(0,polys);
	    }
	   
	   /**
	    * A static method that will disolve all internal borders within the provided set to create a single polygon
	    * which describes the outline of all the given polygons combined.
	    * 
	    * <br>This code is somewhat experimental and has not been fully tested.
	    * in particular, if the polygons do not define a single clump then it will not work as expected.
	    * @param polys A Vector of GeoPolgyons to disolve.
	    * @param an id for this new polygon.
	    * @return GeoPolygon the disolved version of the polys set.
	    */
	   public static GeoPolygon dissolve(int id,Vector polys){
	    Vector outList = (Vector)polys.clone();
	    Vector holes = new Vector();Vector fills = new Vector();
	    int count = outList.size();
	    GeoPolygon multiPartTest;
	    for(int i=0;i<count;i++){
	        if(DEBUG)System.out.println("Element "+i+" of "+count);
	        //multiPartTest = (GeoPolygon)outList.elementAt(i);
	        multiPartTest = (GeoPolygon)polys.elementAt(i);
	        if(multiPartTest.isMultiPart() && 2==3){
	            if(DEBUG)System.out.println("Scary dissolve involving a multipart polygon!");
	            for(int j=1;j<=multiPartTest.getNumParts()-1;j++){
	                if(DEBUG)System.out.println("Area of part"+((GeoPolygon)multiPartTest.getPart(j)).getArea());
	                if(((GeoPolygon)multiPartTest.getPart(j)).getArea()>=0){
	                    if(multiPartTest.getPart(j).getPoints().size()<3)
	                    {
	                        if(DEBUG)System.err.println("Multipart polygon only had 2 points.");
	                    }

	                    outList.addElement(multiPartTest.getPart(j));

	                }
	                else{
	                    if(DEBUG)System.out.println("Hole");
	                    /* TODO JM: sort out holes properly by implementing cases 2 and 3, although a change in the represenation of
	                     * polygon toplogy may come first.
	                     * this part represents a hole.
	                     * There are three states that this hole could be in for this zone.
	                     * 1) Empty, with no polygons at all inside it.
	                     * 2) Filled, with polygons that belong to this zone.
	                     * 3) Semi-filled, with some or all of the polygons inside it not belonging to this zone.
	                     * 3 is a hard case that will need to be planed out carefully and proably requires
	                     * more constructive geometry methods than exist at the moment
	                     */

                        //Is the hole empty?
                        GeoPolygon hole = (GeoPolygon)multiPartTest.getPart(j);
                        Vector fillers = hole.getContiguityList(polys);
                        if(fillers.size()==0){
                            //case 1 from above.
                            //hole is only contiguous with the polygon to which it is a hole
                            //therefore keep it!
                            holes.addElement(hole);
                        }
                        else{
                            //case 2 or 3 from above.
                            //There is something attached to this hole, other than the polygon to which it is attached.
                            //for now we will assume a simple case 2 by removing all polygons attached to this hole
                            //this is the danger that a polygon will be attached to a polygon that is attached to this hole.
                            //TODO JM: implement case 2 properly and sort out case 3
                            holes.addElement(hole);
                            for(int k =0;k<fillers.size();k++){
                                GeoPolygon temp = (GeoPolygon)fillers.elementAt(k);
                                if(temp == hole || temp == multiPartTest){continue;}
                                if(DEBUG)System.out.println("Removing hole filler");
                                fills.addElement(temp);
                            }
                        }
                    }
	                                
	            }
	        }
	    }
	    if(DEBUG)System.out.println("Next Stage");        
	    for(int i=0;i<fills.size();i++){
	        outList.removeElement(fills.elementAt(i));
	    }
	    GeoPolygon d = new GeoPolygon(id,-1,-1);
	    for(int i=0;i<polys.size();i++){
	        GeoPolygon temp = (GeoPolygon)polys.elementAt(i);
	        
	        
	        if(!temp.isOnOutside(polys)){
	            //System.out.println("removing "+temp.getID());
	            outList.removeElement(temp);
	        }
	    }
	    Vector toDo = (Vector)outList.clone();
	    
	    //System.out.println(""+(polys.size()-outList.size())+" polygons removed");
	    //find first outside point on first outside polygon
	    GeoPolygon activePolygon = (GeoPolygon)outList.elementAt(0);
	    
	    Vector activePoints = activePolygon.getPoints();
	    
	    boolean found = false;
	    Vector conList = activePolygon.getContiguityList(polys);
	    GeoPoint p1=null,p2=null;
	    int activeIndex=-1;
	    for(int i=0;i<activePoints.size()-1 && !found;i++){
	        
	        activeIndex = i+1;
	        p1 = (GeoPoint)activePoints.elementAt(i);
	        p2 = (GeoPoint)activePoints.elementAt(i+1);
	        found = true;
	        if(!activePolygon.isOnOutside(p1,p2,conList)){
	            found=false;
	            //continue;
	        }
	        if(found == true){if(DEBUG)System.out.println("Found outside point on first outside polygon");}

	    }
	    if(DEBUG)System.out.println("Start trace from "+p1);
	    GeoPoint start = p1;
	    d.addPoint(p1);
	    d.addPoint(p2);
	    GeoPoint last = null;
	    GeoPoint next = null,activePoint;
	    int direction =1;
	    boolean finished = false;
	    while(!finished){
	        toDo.removeElement(activePolygon);
	        //System.out.println("Active polygon is "+activePolygon.getID());

	        //wrap move off end/begining of point list to begining/end
	        if(direction==1){if(activeIndex==activePoints.size()-1)activeIndex=0;}
	        else{if(activeIndex==0)activeIndex=activePoints.size()-1;}

	        activePoint = (GeoPoint)activePoints.elementAt(activeIndex);
	        int nextIndex = activeIndex+direction;
	        if(nextIndex<0){nextIndex=activePoints.size()-2;}
	        if(nextIndex==activePoints.size()){nextIndex=0;}
	        next = (GeoPoint)activePoints.elementAt(nextIndex);
	        //System.out.println(""+activePolygon.getID()+" "+activeIndex+" "+nextIndex);
	        found = true;
	        for(int j=0;j<conList.size();j++){
	            GeoPolygon conPol = (GeoPolygon)conList.elementAt(j);
	            if(conPol==activePolygon)if(DEBUG)System.out.println("Testing aginst self!");
	            if(conPol.areConsecutive(activePoint,next)){
	                found = false;
	                //not outside line
	                continue;
	            }
	        }
	        if(found){
	            //still on the outside.
	            //System.out.println("Still on outside");
	            if(next.equals(start)){//we are done for now, move following code to a clean up section

	                finished=true;
	            }
	            d.addPoint(next);

	            activeIndex+=direction;
	            last = activePoint;
	        }
	        else{
	            //build list of all outside polygons that contiain this point
	            boolean done = false;
	            for(int i=0;i<polys.size()&&!done;i++){
	                GeoPolygon test = (GeoPolygon)polys.elementAt(i);
	                if(test==activePolygon)continue;
	                if(!outList.contains(test))continue;
	                Vector testPoints = test.getPoints();
	               // if(testPoints.size()<3)continue;
	                if(!testPoints.contains(activePoint))continue;

	                //ok, we have found an outside polygon that also contains the active point.
	                //is it the start of an outside edge and if so in which direction

	                if(DEBUG)System.out.println("This polygon has "+testPoints.size()+ "points");

	                int testIndex = testPoints.indexOf(activePoint);
	                //System.out.println("test point found at "+testIndex);
	                //lets try forwards
	                int forwardIndex = testIndex+1;
	                if(testIndex==testPoints.size()-1){
	                    forwardIndex=0;
	                }
	                GeoPoint forward = (GeoPoint)testPoints.elementAt(forwardIndex);
	                //is edge outside
	                boolean fudge = false;
	                if(activePoint.equals(forward)){
	                    if(DEBUG)System.err.println("active and forward are the same");
	                   // fudge = true;
	                    //forward = (GeoPoint)testPoints.elementAt(forwardIndex+1);
	                }
	                if(test.isOnOutside(activePoint,forward,polys)){
	                    //System.out.println("Found the next point by goind forwards");
	                    activeIndex=testIndex;
	                    activePolygon=test;
	                    activePoints=activePolygon.getPoints();
	                    direction = 1;
	                    conList = activePolygon.getContiguityList(polys);
	                    done=true;
	                }
	                else
	                {
	                    //lets try backwards
	                    if(testIndex==0)testIndex=testPoints.size()-1;
	                    GeoPoint backward = (GeoPoint)testPoints.elementAt(testIndex-1);
	                    if(activePoint.equals(backward)){
	                        
	                        if(DEBUG)System.err.println("active and backward are the same");
	                      //  backward = (GeoPoint)testPoints.elementAt(testIndex-2);
	                    }
	                    if(test.isOnOutside(activePoint,backward,polys)){
	                        //System.out.println("Found next point by going backwards");
	                        activeIndex=testIndex;
	                        activePolygon=test;
	                        activePoints=activePolygon.getPoints();
	                        direction = -1;
	                        conList = activePolygon.getContiguityList(polys);
	                        done = true;
	                    }
	                }
	                if(done){last = activePoint;}
	            }
	            if(!done){if(DEBUG)System.err.println("Error? none of the posible polygons contained outside lines. ABORT");return d;}
	        }



	    }//while !finished
	    //re-add any empty holes that were in some of the origional polygons.
	    if(toDo.size()>0){
	       if(DEBUG)System.out.println("Finished with "+toDo.size());
	       d.addSubPart(dissolve(id,toDo));
	    }

	    d.calculateCentroidLocation();
	    return d;
	   }

	   /**
	    * Combines this polygon with the provided polygon to create a new single polygon with the combined outline of both.
	    * <br> both polygons must be contiguous.
	    * @param gp the GeoPolygon to disolve into this one.
	    * @return GeoPolygon the resulting disolved polygon.
	    */
	   public GeoPolygon dissolve(GeoPolygon gp){
	    if(!isContiguous(gp)){throw new IllegalArgumentException("Polygon is not Contiguous, dissolve imposible");}

	    Vector a = getPoints();
	    Vector b = gp.getPoints();
	    int lastA = a.size()-2; // one for 0 counting one for duplicate final point.
	    int lastB = b.size()-2;

	    GeoPolygon d = new GeoPolygon();// construct with same id as this polygon?
	    //find first non shared point
	    int indexA = 0,indexB=0,start = 0;
	    while(b.contains(a.elementAt(indexA))){indexA++;}
	    start = indexA;
	    //System.out.println("Starting at "+indexA);
	    boolean done = false,first = true;
	    final int A=0,B=1,A2B=2,B2A=3;
	    int direction =1;
	    int state = A;
	    while(!done){
	       // System.out.println(""+indexA+" "+indexB+" "+state);
	        switch(state){
	            case A:
	                if(indexA==start && !first){done=true;continue;}
	                first = false;
	                d.addPoint((GeoPoint)a.elementAt(indexA));
	                indexA=(indexA+1)%(lastA+1);
	                if(b.contains(a.elementAt(indexA))){
	                    state=A2B;
	                }
	                break;
	            case B:  
	                d.addPoint((GeoPoint)b.elementAt(indexB));
	                indexB+=1*direction;
	                indexB%=(lastB+1);
	                if(indexB<0)indexB=lastB;
	                if(a.contains(b.elementAt(indexB))){
	                    state = B2A;
	                }
	                break;
	            case A2B:
	                //add the last point of a
	                d.addPoint((GeoPoint)a.elementAt(indexA));
	                //which way round B do we go?
	                int join = b.indexOf(a.elementAt(indexA));
	                if(a.contains(b.elementAt((join+1)%lastB))){
	                    direction = -1;
	                }
	                else{
	                    direction = 1;
	                }
	                indexB = join+direction;
	                state = B;
	                break;
	            case B2A:
	                indexA = a.indexOf(b.elementAt(indexB));
	                state = A;
	                break;
	        }
	        
	    }
	    
	    d.addPoint((GeoPoint)a.elementAt(start));
	    d.calculateCentroidLocation();
	    return d;
	        
	        
	    
	   }
	  /**
	   * Tests to see if a point is contained by this polygon.
	   * @param p The GeoPoint to test.
	   * @return boolean true if point is contained by this polygon.
	   */
	   public boolean contains(GeoPoint p){

	    /*if(!getMultiPartBounds().contains(p)){
	        return false;
	    }*/
	    
	  if(getBounds().contains(p)){
	        
	    
	   
	   
	   
	    /* Start andys code */
	     int number_of_lines_crossed = 0;                                // holds lines intersecting with 
		    number_of_lines_crossed = 0;                                  // set lines crossed = 0 for each polygon
		                                                        // lets us know which part start we're looking for	   
		   for(int i=0;i<npoints-1;i++){
			//GeoPoint A = new GeoPoint();
			//GeoPoint B = new GeoPoint();        
			//A.y = ypoints[i];                                       // get y-coordinate
			//A.x = xpoints[i];                                       // get x-coordinate 
			//B.y = ypoints[i+1];                                     // get next y-coordinate
			//B.x = xpoints[i+1];                                     // get next x-coordinate
			
			if (p.y!=ypoints[i+1]&&(p.y <= Math.max(ypoints[i],ypoints[i+1])) && (p.y >= Math.min(ypoints[i],ypoints[i+1])) && ((xpoints[i] >= p.x) || (xpoints[i+1] >= p.x))) {         // if polygon contains a suitable value
			    if(((xpoints[i] >= p.x) && (xpoints[i+1] >= p.x))){
			        number_of_lines_crossed++;//simple case
			    }
			    else{
			        double gradient;                                          //   calc. gradient
			        if (xpoints[i] > xpoints[i+1]) 
				        gradient = ((xpoints[i] - xpoints[i+1])/(ypoints[i] - ypoints[i+1]));
    			    else 										
				        gradient = ((xpoints[i+1] - xpoints[i])/(ypoints[i+1] - ypoints[i]));				
		            double x_intersection_axis = (xpoints[i] - (gradient * ypoints[i]));                     // calc. intersect with x-axis
			        double x_intersection_line = (gradient*p.y) + x_intersection_axis;  // calc. intersect with y=const
					                                                                       //  line extending from location 
			        if ((x_intersection_line <= Math.max(xpoints[i],xpoints[i+1])) &&                          // check intersect inside polygon 
				        (x_intersection_line >= Math.min(xpoints[i],xpoints[i+1])) && (x_intersection_line >= p.x)) {
				        number_of_lines_crossed++;                                               // increment line counter
			        } // end check for inside polygon
			    }
			} // end of if polygon points suitable
		    } // end of run through points in polygon

		    if ((number_of_lines_crossed != 0) &&				// if number of polygon lines crossed
			(((number_of_lines_crossed % 2) == 1))) {                       //   by a line in one direction from the
			return true;		                                        //   initial location is odd, the location

		    				                                        //   lies in that polygon

		 }	// end of run through polygons

	  } 
	  if(this.isMultiPart()){
        for(int i=0;i<this.subParts.size();i++){
            if(((GeoPolygon)subParts.elementAt(i)).contains(p)){return true;}
        }
      }
      return false;                                                         // return stuffing if things get this far

	} // end of method whichPolygon


    /**
     * Calcualtes and returns the perimiter of this polygon
     * @deprecated miss spelling use getPerimeter instead.
     * @see getPerimeter
     * @return double the perimiter of this polygon
     */
     public double getPerimiter(){
        double dist = 0;
        int i;
        for(i=0;i<npoints-1;i++){
            dist+=Math.sqrt(((xpoints[i]-xpoints[i+1])*(xpoints[i]-xpoints[i+1]))+((ypoints[i]-ypoints[i+1])*(ypoints[i]-ypoints[i+1])));
        }
       return dist;
     }
     
     /**
      * Calculates and returns the length of the segment between the specifed points
      * @param start int of the index of the start point
      * @param end int of the index of the end point
      * @return double length of segment
      */
      public double getSegmentLength(int start,int end){
        double dist = 0;
        if(end<start){end = npoints+end;}
        int index,next;
        for(int i=start;i<end;i++){
            
            index = (i%npoints);
            next = (i+1)%npoints;
            
            dist+=Math.sqrt(((xpoints[index]-xpoints[next])*(xpoints[index]-xpoints[next]))+((ypoints[index]-ypoints[next])*(ypoints[index]-ypoints[next])));
            //System.out.println("index "+index+" "+next+" "+npoints+" x "+xpoints[index]+" y "+ypoints[index]+" dist "+ dist);
        }
        return dist;
      }
	public String toString(){
	 StringBuffer sb = new StringBuffer();
	 sb.append("GeoPolygon : [id ");
	 sb.append(id);
	 sb.append("] ");
	 for(int i=0;i<npoints;i++){
	    sb.append("{");
	    sb.append(xpoints[i]);
	    sb.append(",");
	    sb.append(ypoints[i]);
	    sb.append("} ");
	 }
	 return sb.toString();
	    
	}
	   public boolean contains(double x,double y){
	    GeoPoint p = new GeoPoint(x,y);
	    return contains(p);
	   }

}				
				
/*
 * $Log: GeoPolygon.java,v $
 * Revision 1.1  2005/09/19 10:31:28  CarstenKessler
 * Carsten Keßler:
 * First Version submitted to CVS.
 *
 * Revision 1.19  2001/08/01 12:32:18  ianturton
 * modification submited by Michael Becke <becke@u.washington.edu> to reduce
 * memory usage.
 *
 *
 *
 */
   
	
	

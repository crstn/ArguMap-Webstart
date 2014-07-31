package uk.ac.leeds.ccg.geotools.projections;

import uk.ac.leeds.ccg.geotools.GeoRectangle;

public class Gnomonic implements uk.ac.leeds.ccg.geotools.projections.Projection
{
    double phi_0=0,phi_1=0,lambda_0 = 0;
    
    
    public double[] project(double lon, double lat)
    {
        double p[] = new double[2];
        double lambda, phi,x,y;
        //to radians
        lambda = (Math.PI/180d)*lon;
        phi   = (Math.PI/180d)*lat;
        
        //do calculations
        double cosc = Math.sin(phi_1)*Math.sin(phi)+Math.cos(phi_1)*Math.cos(phi)*Math.cos(lambda-lambda_0); 
       
        
        x = (Math.cos(phi)*Math.sin(lambda-lambda_0))/cosc;
        y = (Math.cos(phi_1)*Math.sin(phi)-Math.sin(phi_1)*Math.cos(phi)*Math.cos(lambda-lambda_0))/cosc;
        
        //from radians
        p[0] = x * 180d/Math.PI;
        p[1] = y * 180d/Math.PI;
        return p;
    }

    public double[] unproject(double x, double y)
    {
        double p[] = new double[2];
        //to radians
        x = (Math.PI/180d)*x;
        y = (Math.PI/180d)*y;
        
        //do calcualtaions
        double dist = Math.sqrt((x*x)+(y*y));
        double theta = Math.atan(dist);
        
        double phi,lambda;
        phi = Math.asin(Math.cos(theta)*Math.sin(phi_1)+((y*Math.sin(theta)*Math.cos(theta)*Math.cos(phi_1))/dist));
        lambda = lambda_0 + Math.atan((x*Math.sin(theta))/(dist * Math.cos(phi_1) * Math.cos(theta) - y * Math.sin(phi_1) * Math.sin(theta)));
       
        
        p[0] = lambda * 180d/Math.PI;
        p[1] = phi * 180d/Math.PI;
        return p;
    }
    
    /** given a geographical extent work out the minimum bounding rectangle
   *  that contains that rectangle when projected - you may clip the
   * rectangle returned to reflect what is sensible for this projection
   * NB needs writing properly for this projection!
   */
  public GeoRectangle projectedExtent(GeoRectangle r){
    double x = r.getX();
    double y = r.getY();
    double w = r.getWidth();
    double h = r.getHeight();
		double upper;
		if(y<-85) y=-85;
		if((y+h)>85) 
			upper=85;
		else
			upper=y+h;
    double b1[] = project(lambda_0,Math.min(y,upper));
    double b2[] = project(x,y);
    double base = Math.min(b1[1],b2[1]);
    double l1[] = project(x,phi_0);
    double l2[] = project(x,y);
    double left = Math.min(l1[0],l2[0]);
    double t1[] = project(lambda_0,upper);
    double t2[] = project(x,upper);
    double top = Math.max(t1[1],t2[1]);
    double r1[] = project(x+w,phi_0);
    double r2[] = project(x+w,Math.min(y,upper));
    double right = Math.max(r1[0],r2[0]);
 
    System.out.println(""+left+","+base+":"+right+","+top);
    GeoRectangle gr = new GeoRectangle();
    gr.add(left,base);
    gr.add(top,right);
    return gr;
  }
  
  public GeoRectangle unprojectedExtent(GeoRectangle r){
	    return r;
	    
	}
	
	public GeoRectangle clipToSafe(GeoRectangle r){
	    return r;
	}


}
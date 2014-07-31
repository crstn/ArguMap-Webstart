package uk.ac.leeds.ccg.geotools.projections;
import uk.ac.leeds.ccg.geotools.GeoRectangle;

public class ElipsoidalMercator implements Projection
{
		final static double a = 6378.137; // GRS80
		final static double b = 6356.7523; 
		final static double f = 1/298.257;
		final static double e = 0.081819221;
		final static double tol = 0.01;
		double lambda_0 = 0.0d; // prime meridian
    double phi_0 = 0.0d; // equator


    public double[] project(double lon,double lat){
        //do magic stuff
        double p[] = new double[2];
        
        double rx = (Math.PI/180d)*lon;
        double ry = (Math.PI/180d)*lat;
        
     
				double t = e*Math.sin(ry);
        p[0] = a*rx;
        p[1] = a*Math.log(Math.tan(Math.PI/4.0 + ry/2.0)*
					Math.pow(((1.0-t)/(1.0+t)),e/2.0));
        
        
    //    p[0] = p[0] * 180d/Math.PI;
        //p[1] = p[1] * 180d/Math.PI;
        //System.out.println(lon+","+lat+"   "+p[0]+","+p[1]);
        return p;
    }
    
    public double[] unproject(double x,double y){
        double p[] = new double[2];
        
        double rx = x;
        double ry = y;
        
        
        p[0] = rx/a;
				double t = Math.pow(Math.E,(-ry/a));
				double newp = Math.PI/2.0-2.0*Math.atan(t);
				double oldp= Double.MAX_VALUE;
				int count = 0;
				while((count++)<10&&Math.abs(newp-oldp)>tol){
					oldp=newp;
					double tmp = e*Math.sin(oldp);
					newp=Math.PI/2.0 - 2*Math.atan(t*Math.pow((1-tmp)/(1+tmp),e/2));
				}					
        //p[1] = 2*Math.atan(Math.pow(Math.E,ry))-(Math.PI/2d);
				p[1]=newp;
        
				if(ry==Double.NEGATIVE_INFINITY){
					//System.out.println("reset y to -90.0");
					p[1] = -Math.PI/2.0;
				}
				if(ry==Double.POSITIVE_INFINITY){
					//System.out.println("reset y to 90.0");
					p[1] = Math.PI/2.0;
				}
        p[0] = p[0] * 180d/Math.PI;
        p[1] = p[1] * 180d/Math.PI;
        //System.out.println(""+rx+" "+ry+" lon,lat "+p[0]+","+p[1]);
        return p;
        
        
    }
 /** given a geographical extent work out the minimum bounding rectangle
   *  that contains that rectangle when projected - you may clip the
   * rectangle returned to reflect what is sensible for this projection
   */
  public GeoRectangle projectedExtent(GeoRectangle r){
		//System.out.println("raw "+r);
    double x = r.getX();
    double y = r.getY();
    double w = r.getWidth();
    double h = r.getHeight();
		double upper=y+h;
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
 
    //System.out.println("proj "+left+","+base+":"+right+","+top);
    GeoRectangle gr = new GeoRectangle();
    gr.add(left,base);
    gr.add(right,top);
    return gr;
  }
  public GeoRectangle unprojectedExtent(GeoRectangle r){
		//System.out.println("raw "+r);
    double x = r.getX();
    double y = r.getY();
    double w = r.getWidth();
    double h = r.getHeight();
		double []lq = project(lambda_0,phi_0);
		double upper=y+h;
    double b1[] = unproject(lq[0],Math.min(y,upper));
    double b2[] = unproject(x,y);
    double base = Math.max(b1[1],b2[1]);
		if(Double.isNaN(b1[1])) base = b2[1];
		if(Double.isNaN(b2[1])) base = b1[1];
		//System.out.println("base "+base);
    double l1[] = unproject(x,lq[1]);
    double l2[] = unproject(x,y);
    double left = Math.max(l1[0],l2[0]);
    double t1[] = unproject(lq[0],upper);
    double t2[] = unproject(x,upper);
    double top = Math.min(t1[1],t2[1]);
		if(Double.isNaN(upper)) top= 90.0;
    double r1[] = unproject(x+w,lq[1]);
    double r2[] = unproject(x+w,Math.min(y,upper));
    double right = Math.min(r1[0],r2[0]);
 
    //System.out.println("unproj "+left+","+base+":"+right+","+top);
    GeoRectangle gr = new GeoRectangle();
    gr.add(left,base);
    gr.add(right,top);
    return gr;
  }

	public GeoRectangle clipToSafe(GeoRectangle r){
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
    GeoRectangle gr = new GeoRectangle();
    gr.add(x,y);
    gr.add(x+w,upper);
    return gr;
	}
}

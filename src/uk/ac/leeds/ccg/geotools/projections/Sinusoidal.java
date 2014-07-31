package uk.ac.leeds.ccg.geotools.projections;

import uk.ac.leeds.ccg.geotools.GeoRectangle;

public class Sinusoidal implements uk.ac.leeds.ccg.geotools.projections.Projection
{
		double lambda_0 = 0.0d; // prime meridian
		double phi_0 = 0.0d; // equator
    public double[] project(double lon,double lat){
        double p[] = new double[2];
        
        double rx = (Math.PI/180d)*lon;
        double ry = (Math.PI/180d)*lat;
				double rl = (Math.PI/180d)*lambda_0;
        
        
        
        p[0] = (rx-rl) * Math.cos(ry);
        p[1] = ry;
        
        p[0] = p[0] * 180d/Math.PI;
        p[1] = p[1] * 180d/Math.PI;
        return p;
    }
    
    public double[] unproject(double x,double y){
        double p[] = new double[2];
        
        double rx = (Math.PI/180d)*x;
        double ry = (Math.PI/180d)*y;
				double rl = (Math.PI/180d)*lambda_0;
        
        
        
        p[0] = rl+(rx/Math.cos(ry));
        p[1] = ry;
        
        p[0] = p[0] * 180d/Math.PI;
        p[1] = p[1] * 180d/Math.PI;
        return p;
    }


	/** given a geographical extent work out the minimum bounding rectangle
	 *  that contains that rectangle when projected - you may clip the
	 * rectangle returned to reflect what is sensible for this projection
	 */
	public GeoRectangle projectedExtent(GeoRectangle r){
		//System.out.println("project raw :"+r);
		
		double x = r.getX();
		double y = r.getY();
		double w = r.getWidth();
		double h = r.getHeight();
		double b1[] = project(lambda_0,Math.min(y,y+h));
		double b2[] = project(x,y);
		double base = Math.min(b1[1],b2[1]);
		double l1[] = project(x,phi_0);
		double l2[] = project(x,y);
		double left = Math.min(l1[0],l2[0]);
		double t1[] = project(lambda_0,y+h);
		double t2[] = project(x,y+h);
		double top = Math.max(t1[1],t2[1]);
		double r1[] = project(x+w,phi_0);
		double r2[] = project(x+w,Math.min(y,y+h));
		double right = Math.max(r1[0],r2[0]);
		
		GeoRectangle gr = new GeoRectangle();
		gr.add(left,base);
		gr.add(right,top);
		//System.out.println("Projected :"+gr);
		return gr;
	}
	
	public GeoRectangle unprojectedExtent(GeoRectangle r){
		//System.out.println("unproject raw :"+r);
		
		double x = r.getX();
		double y = r.getY();
		double w = r.getWidth();
		double h = r.getHeight();
		double []lq = project(lambda_0,phi_0);
		double b1[] = unproject(lq[0],Math.min(y,y+h));
		double b2[] = unproject(x,y);
		double base = Math.max(b1[1],b2[1]);
		double l1[] = unproject(x,lq[1]);
		double l2[] = unproject(x,y);
		double left = Math.max(l1[0],l2[0]);
		double t1[] = unproject(lq[0],y+h);
		double t2[] = unproject(x,y+h);
		double top = Math.min(t1[1],t2[1]);
		double r1[] = unproject(x+w,lq[1]);
		double r2[] = unproject(x+w,Math.min(y,y+h));
		double right = Math.min(r1[0],r2[0]);
		
		GeoRectangle gr = new GeoRectangle();
		gr.add(left,base);
		gr.add(right,top);
		//System.out.println("unProjected :"+gr);
		return gr;
	    
	}
		
	public GeoRectangle clipToSafe(GeoRectangle r){
		return r;
	}

}

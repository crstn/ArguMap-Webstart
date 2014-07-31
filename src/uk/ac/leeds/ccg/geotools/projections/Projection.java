package uk.ac.leeds.ccg.geotools.projections;

import uk.ac.leeds.ccg.geotools.GeoRectangle;

public interface Projection{
    
    public double[] project(double lon,double lat);
    public double[] unproject(double x,double y);
		/** given a geographical extent work out the minimum bounding rectangle
		 *  that contains that rectangle when projected - you may clip the
		 * rectangle returned to reflect what is sensible for this projection
		 */
		public GeoRectangle 
			projectedExtent(uk.ac.leeds.ccg.geotools.GeoRectangle r);
    public GeoRectangle unprojectedExtent(GeoRectangle r);
    public GeoRectangle clipToSafe(GeoRectangle r);    
}

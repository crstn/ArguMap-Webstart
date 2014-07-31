package uk.ac.leeds.ccg.geotools;

import java.awt.Rectangle;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.projections.Projection;


/**
 * Used to convert real world co-ordinates to and from screen co-ordinates.
 * Once set up with a geographic region and a screen region the scaler can
 * perform all the calculations needed to scale a point from one to the other.
 */
public final class Scaler extends java.awt.Component
{
	private final boolean DEBUG=false;
    /**
     * The Coordinates of the full extent of the world
     */
    GeoRectangle mapExtent;

	/**
	 * The coordinates of the Graphics context
	 */
	Rectangle graphicsExtent;
	boolean Isotrophic = true;
	/**
	 * The scale factor requried to transform the map data
	 */
    double scaleFactor=Double.NaN,xScaleFactor=Double.NaN,yScaleFactor=Double.NaN;

    /**
     * Listeners for changes in scale
     */
    Vector listeners = new Vector();

    /**
     * If not null then this is used to project co-ordinates
     */
     Projection projection = null;
     
    //Constructs a new scaler;
	
	/**
	 * Contructs an empty scaller, it WILL NOT work.
	 */
	public Scaler()
	{
	}

    /**
     * Construct a working scaler.
     * @param world A GeoRectangle that defines the bounds of the geographic region to map to the screen.
     * @param screen A Rectangle that defines the size of the on-screen area to map the 'world' into.
     */
	public Scaler(GeoRectangle world,Rectangle screen){
	    graphicsExtent = screen;
	    setMapExtent(world);
 	    calcScaleFactor();
 	    if(DEBUG)System.out.println("New Scaler created "+scaleFactor);
			if(scaleFactor == Double.POSITIVE_INFINITY) {
				scaleFactor = 1.0d;
			}
    }

		public String toString(){
			return(super.toString()+" "+scaleFactor);
		}
	
	
    /**
     * Changes the extent of the reagion of geographic interest
     * the scaler will recalcualte the scale factor needed to fit this
     * new region to the screen and subsequently notify all listeners of the
     * change in scale.
     * If a projection is in use then the extent will be projected before
     * the scale factor is calculated.
     * @param extent A GeoRectangle that defines the new region.
     */
    public void setMapExtent(GeoRectangle extent){
        setMapExtent(extent,false);
    }
    
    /**
     * Changes the extent of the reagion of geographic interest
     * the scaler will recalcualte the scale factor needed to fit this
     * new region to the screen and subsequently notify all listeners of the
     * change in scale ONLY IF keepQuite is false.
     * This keepQuite option is designed to stop infinate rescaling of
     * two viewers that are litening to each other for scale changes.
     * @param extent A GeoRectangle that defines the new region.
     * @param keepQuiet A boolean stating if the scaler should NOT tell listenres.
     */
    public void setMapExtent(GeoRectangle extent,boolean keepQuiet){
        mapExtent = project(extent);
        calcScaleFactor(keepQuiet);
        //notifyScaleChanged();
    }
    
    public void setProjectedMapExtent(GeoRectangle extent){
        mapExtent = extent;
        calcScaleFactor(false);
    }
    
    /**
     * Changes the extent of the reagion of the onscreen view in pixels
     * the scaler will recalcualte the scale factor needed to fit this
		 * new region to the screen and subsequently notify all listeners of the
     * change in scale.
     * @param extent A Rectangle that defines the new region.
     */
    public void setGraphicsExtent(Rectangle extent){
        graphicsExtent = new Rectangle(extent);
        calcScaleFactor(true);
        //notifyScaleChanged();
    }

    /**
     * Gets the geographic extent of the region currenly scaled to fit into
     * the on-screen region.
     * @return GeoRectangle the current map extent.
     */
    public GeoRectangle getMapExtent(){
        return unproject(mapExtent);
    }
    
    public GeoRectangle getProjectedMapExtent(){
        return mapExtent;
    }


    private void calcScaleFactor(){
        calcScaleFactor(false);
    }
    /**
     * Caclulates the scaling factor required to fit the current mapExtent into
     * the on-screen region.
     * Internal use only.
     */
    private void calcScaleFactor(boolean keepQuiet){
	        //Set scaleFactor here...
	if(Isotrophic){
	        if (mapExtent.width / graphicsExtent.width > mapExtent.height / graphicsExtent.height)
	        {
	            scaleFactor = (double)mapExtent.width/ (double)graphicsExtent.width;
	        }else{
	            scaleFactor = (double)mapExtent.height/ (double)graphicsExtent.height;
	        }
		if(scaleFactor == Double.POSITIVE_INFINITY) {
			scaleFactor = 1.0d;
			if(DEBUG)System.out.println("Fixed");
		}
	}else{
		xScaleFactor = (double)mapExtent.width/ (double)graphicsExtent.width;
		if(DEBUG)System.out.println("X "+mapExtent.width+"/"+graphicsExtent.width+" "+
			xScaleFactor);
		yScaleFactor = (double)mapExtent.height/ (double)graphicsExtent.height;
		if(DEBUG)System.out.println("Y "+mapExtent.height+"/"+graphicsExtent.height+" "+
			yScaleFactor);
		if(xScaleFactor == Double.POSITIVE_INFINITY) {
			xScaleFactor = 1.0d;
			if(DEBUG)System.out.println("Fixed");
		}
		if(yScaleFactor == Double.POSITIVE_INFINITY) {
			yScaleFactor = 1.0d;
			if(DEBUG)System.out.println("Fixed");
		}
	}
    double[] far=toProj(graphicsExtent.width,0);
    
	double xshift,yshift,width,height;
	width=mapExtent.width;
	height=mapExtent.height;
    mapExtent.add(new GeoPoint(far[0],far[1]));
	xshift=(mapExtent.width-width)/2d;
	yshift=(mapExtent.height-height)/2d;
	mapExtent=new GeoRectangle(mapExtent.x-xshift,mapExtent.y-yshift,mapExtent.width,mapExtent.height);
   // System.out.println("Projected  : "+mapExtent);
   // System.out.println("Unprojected: "+unproject(mapExtent)+"/n");
        if(!keepQuiet){
            if(DEBUG)System.out.println("Fireing scale changed event");
            notifyScaleChanged();}
    }

    //will scale a single value
    //DO NOT USE TO SCALE A value that will be used in a co-ordinate
    /**
     * Converts a single geographic scaler value into on-screen units.
     * DO NOT USE TO SCALE a value that will be used in a CO-ORDINATE.<br>
     * If you wish to scale a point use the point version.
     * @param value The double to be scaled to on-screen units.
     * @return int The on-screen units equivelent of value.
     * @see toMap
     */
	public int toGraphics(double value){
		value /= scaleFactor;
		return (int)Math.round(value);
	}
	public final int toXGraphics(double value){
		value /= xScaleFactor;
		return (int)value;
	}
	public final int toYGraphics(double value){
		value /= yScaleFactor;
		return (int)value;
	}

    public final double[] project(double x,double y){
        double p[] = new double[2];
        if(projection==null){p[0] = x;p[1] = y;return p;}
        p = projection.project(x,y);
        return p;
    }
    
    public GeoRectangle project(GeoRectangle g){
       
        if(projection==null){return new GeoRectangle(g);}
        return projection.projectedExtent(g);
    }
        
    
    public final double[] unproject(double x,double y){
        double p[] = new double[2];
        if(projection==null){p[0] = x;p[1]=y;return p;}
        p = projection.unproject(x,y);
        return p;
    }
    
    public GeoRectangle unproject(GeoRectangle g){
       
        if(projection==null){return new GeoRectangle(g);}
        return projection.unprojectedExtent(g);
    }
    
        
    //can be used to scale a point
    /**
     * returns the screen co-ordinate equvelent of a geographic point.
     * It will both scale and TRANSLATE the point to the correct screen location.
     * therefore ONLY USE TO SCALE CO-ORDINATE PAIRS.<br>
     * @param x The double that makes up the x part of the co-ordinate
     * @param y The double that makes up the y part of the co-ordinate
     * @return int[] The scaled values of x and y, int[0]=scaledx,int[1]=scaledy;
     * @see toMap
     */
	public int[] toGraphics(double x,double y){
		if(Isotrophic){
		    
		    double p[] = project(x,y); //project it
		    x=p[0];
		    y=p[1];
			
			int scaled[] = {0,0};
			x -= mapExtent.x;
			x /= scaleFactor;

			y -= mapExtent.y;
			y /= scaleFactor;
			y = graphicsExtent.height - y;//Flip map the right way up.
			scaled[0] = (int)Math.round(x);
			scaled[1] = (int)Math.round(y);

			return scaled;
		}else{
			int scaled[] = {0,0};
			x -= mapExtent.x;
			x /= xScaleFactor;

			y -= mapExtent.y;
			y /= yScaleFactor;
			y = graphicsExtent.height - y;//Flip map the right way up.

			scaled[0] = (int)x;
			scaled[1] = (int)y;

			return scaled;
		}
	}		

	/**
	 * returns the screen co-ordinate equvelent of a geographic point.
     * @param p The GeoPoint to scale to screen co-ordinates.
     * @return int[] The scaled values of x and y, int[0]=scaledx,int[1]=scaledy;
     * @see toMap
     */
	public int[] toGraphics(GeoPoint p){
	    return toGraphics(p.x,p.y);
	}

    /**
     * Converts a single screen units value into geographic unints.
     * DO NOT USE TO SCALE a value that will be used in a CO-ORDINATE.<br>
     * @param value The int of the number of onscreen units to scale to geographic units.
     * @return double The geographic equivelent of value.
     * @see toGraphics
     */
	public double toMap(double value){
	    if(Isotrophic){
		value *= scaleFactor;
		return value;
	   }else{
		 /* this doesn't really work */
		scaleFactor=Math.min(xScaleFactor,yScaleFactor);
		value *= scaleFactor;
		return value;
	}
			
		//throw new InternalError("bad scale call ");
	}

    /*
     * returns the geographic co-ordinate equvelent of an on-screen point.
     * It will both scale and TRANSLATE the point to the correct geographic location.
     * therefore ONLY USE TO SCALE CO-ORDINATE PAIRS.<br>
     * @param x The int that makes up the x part of the co-ordinate
     * @param y The int that makes up the y part of the co-ordinate
     * @return double[] The scaled values of x and y, int[0]=scaledx,int[1]=scaledy;
     * @see toGraphics
     */
     public double[] toMap(int x,int y){
        return toMap(x,y,true);
     }
     
     public double[] toProj(int x,int y){
        return toMap(x,y,false);
     }
     
	public double[] toMap(int x,int y,boolean doProjection){
	    if(Isotrophic){
		double scaled[] = {0,0};
	    double dx,dy;
	    dx = (double)x;
	    dy = (double)y;
	    dx *= scaleFactor;
	    dx += mapExtent.x;

	    dy = graphicsExtent.height - y;
	    dy *= scaleFactor;
	    dy += mapExtent.y;
        
	    //scaled[0] = dx;
	    //scaled[1] = dy;
	    if(doProjection){
	        scaled = unproject(dx,dy);
	    }else{
	        scaled[0] = dx;
	        scaled[1] = dy;
	    }
	    return scaled;
		}else{
		 double scaled[] = {0,0};
	    double dx,dy;
	    dx = (double)x;
	    dy = (double)y;
	    dx *= xScaleFactor;
	    dx += mapExtent.x;

	    dy = graphicsExtent.height - dy;
	    dy *= yScaleFactor;
	    dy += mapExtent.y;

	    scaled[0] = dx;
	    scaled[1] = dy;
	    return scaled;
}
	}

    public void setProjection(Projection p){
        projection = p;
        calcScaleFactor();
    }

    //Sets the scale factor, not sure this should be available
	/**
	 * Sets a new scale Factor, DO NOT USE.
	 * This is a very low level call that should not be available.<br>
	 * @param scaleFactor A double for the new scaling factor
	 * @deprecated
	 */
	public void setScaleFactor(double scaleFactor)
	{
			this.scaleFactor = scaleFactor;
	}

    //Gets the scale factor, not sure if this is usefull
	/**
	 * Gets the current scaling factor used by this scaler.
	 * @return double the current scale factor
	 */
	public double getScaleFactor()
	{
		return this.scaleFactor;
	}

    /**
     * Any listeners added will be notified when ever the scale changes.
     * @param sce The ScaleChangedListener to add.
     */
	public synchronized void
	addScaleChangedListener(ScaleChangedListener sce) {
	    listeners.addElement(sce);
	}

    /**
     * Stop notification of scale change events to the given listener
     * @param sce The ScaleChangedListener to remove.
     */
	public synchronized void
	removeScaleChangedListener(ScaleChangedListener sce) {
	    listeners.removeElement(sce);
	}

    /**
     * Notifies all listeners of scale change event.
     */
	protected void notifyScaleChanged() {
	    Vector l;

			if(DEBUG)System.out.println("Scale notify");
	    ScaleChangedEvent sce = new ScaleChangedEvent(this,scaleFactor);
	    synchronized(this) {l = (Vector)listeners.clone(); }

			/* if we do this in reverse order we actually tell the viewer 
			 * that sent the event first rather than last which I think 
			 * looks nicer if it takes any time at all to do the redraw
			 * if you can't see the redraw then you won't notice, if you do
			 * this way looks right in my code any way! Ian
			 */
			if(l.size()>0){
				for (int i = l.size()-1;i>=0;--i) {
					if(DEBUG)System.out.println("notifying "+l.elementAt(i));
	        ((ScaleChangedListener)l.elementAt(i)).scaleChanged(sce);
				}
			}
	}
	public void SetIsotrophic(boolean f){
		Isotrophic=f;
		calcScaleFactor(false);
	}
   /**
    * Convinence method for scaling whole polygons to screen co-ordinates.
    * After calling you may want to call GeoPolygon.toAWTPolygon();
	*
	* @param poly the GeoPolygon to scale.
	* @return GeoPolygon scaled version of polygon.
	*/
	public GeoPolygon scalePolygon(GeoPolygon poly) {
		double x[] = new double[poly.xpoints.length];
		double y[] = new double[poly.ypoints.length];
		int p[] = {0,0};
		for(int i = 0;i < poly.xpoints.length;i++) {
			p = toGraphics(poly.xpoints[i],poly.ypoints[i]);
			x[i] = p[0];
			y[i] = p[1];
		}
		return new GeoPolygon(poly.getID(),x,y,poly.getNPoints());
	}




}

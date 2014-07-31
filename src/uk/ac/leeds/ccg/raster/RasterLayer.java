package uk.ac.leeds.ccg.raster;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.io.Serializable;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.GeoCircle;
import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoGraphics;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoRectangle;
import uk.ac.leeds.ccg.geotools.LayerChangedEvent;
import uk.ac.leeds.ccg.geotools.Scaler;
import uk.ac.leeds.ccg.geotools.SelectionManager;
import uk.ac.leeds.ccg.geotools.ShadeStyle;
import uk.ac.leeds.ccg.geotools.Shader;
/**
 * @author <a href="http://www.geog.leeds.ac.uk/staff/i.turton/i.turton.html">
 * Ian Turton</a> Centre for
 * Computaional Geography, University of Leeds, LS2 9JT, 1998.<br>
 * <a href="mailto:ian@geog.leeds.ac.uk">i.turton@geog.leeds.ac.uk</a>
 **/

public class RasterLayer extends uk.ac.leeds.ccg.geotools.SimpleLayer
implements Serializable,ChangedListener
{
	static final boolean debug=false; // set true for al sorts of debuging info
	static final boolean grid=false; // set true to turn on a grid
	/**
	 * The geographic origin of the layer
	 */
	private GeoPoint origin;
	/**
	 * the raster to be drawn
	 */
	protected Raster r;
	/**
	 * the image representation of the raster
	 */
	Image image;
	/**
	 * The data used to produce the image
	 */
	private int data[];

	public RasterLayer(){
		if(debug)System.out.println("---->uk.ac.leeds.ccg.raster.RasterLayer constructed. Will identify itself as RaL->");
		r=null;
	}
	public RasterLayer(Raster ras) {
		if(debug)System.out.println("---->uk.ac.leeds.ccg.raster.RasterLayer constructed. Will identify itself as RaL->");
		r=ras;
		if(ras!=null)
		r.addChangedListener(this);
	}
	/**
	 * Open a raster from the ascii grid file named.
	 * @param name the file name of the ascii grid file
	 * @exception java.io.IOException if anything goes wrong when opening the
	 * file
	 * @see uk.ac.leeds.ccg.raster.Raster
	 */
	public RasterLayer(String name) throws java.io.IOException{
		if(debug)System.out.println("---->uk.ac.leeds.ccg.raster.RasterLayer constructed. Will identify itself as RaL->");
		set=false;
		r=new Raster(name);
		r.addChangedListener(this);
	}
	public RasterLayer(Image im, int width, int height, GeoRectangle gr){
		if(debug)System.out.println("---->uk.ac.leeds.ccg.raster.RasterLayer constructed. Will identify itself as RaL->");
		r = new Raster(im,width,height,gr);
		set=false;
		r.addChangedListener(this);
	}
	public void newData(int h,int w){
		data = new int[(h)*(w)];
	}
	public Raster getRaster(){
		return r;
	}
	public void setRaster(Raster rr){
		r=rr;
		set=false;
		r.addChangedListener(this);
		notifyLayerChangedListeners(LayerChangedEvent.GEOGRAPHY);
	}
	/**
	 * Open a raster based on the centres of the circles
	 * @param an array of GeoCircles
	 * @see uk.ac.leeds.ccg.raster.Raster
	 */
	public RasterLayer(GeoCircle[] circles){
		r=new circleRaster(circles);
		set=false;
		r.addChangedListener(this);
	}

	/**
	 * Open a raster based on the values of the circles
	 * @param an array of GeoCircles
	 * @param size the cellsize of the raster in geographic coordinates
	 * @param in the data to be rasterized one value per circle
	 * @see uk.ac.leeds.ccg.raster.Raster
	 */
	public RasterLayer(GeoCircle[] circles,double size,double in[]){
		r=new circleRaster(circles,size,in);
		set=false;
		r.addChangedListener(this);
	}
	/**
	 * Open a raster based on the values of the circles
	 * @param an array of GeoCircles
	 * @param size the cellsize of the raster in geographic coordinates
	 * @param in the data to be rasterized one value per circle
	 * @param m mapextent to be used
	 * @see uk.ac.leeds.ccg.raster.Raster
	 */
	public RasterLayer(GeoCircle[] circles,double size,double in[],GeoRectangle
			m){
		r=new circleRaster(circles,size,in,m);
		set=false;
		r.addChangedListener(this);
	}
	/** 
	 * paints a highlight <br> 
	 * does nothing at the moment since I can't decide what a highlit raster
	 * means.
	 */
	public void paintHighlight(Graphics g, Scaler s,int id,ShadeStyle style){
		// nothing	
	}

	/**
	 * @return the bounding box of the raster contained in the layer
	 */
	public GeoRectangle getBounds(){
		if(r!=null)
			return(r.getBounds());
		else
			return(new GeoRectangle());
	}
	public void paintScaled(GeoGraphics g){
        paintScaled(g.getGraphics(),g.getScale(),g.getShade(),g.getData(),g.getStyle());
    }
	Shader lastshader = null;
	Vector colors = new Vector();
	Vector values = new Vector();
	/**
	 * paints the relevant area of the raster to the viewer
	 * @param g the graphics layer to be drawn to
	 * @param scale the scale to draw at
	 * @param shade the shader to colour the raster with. <br>
	 * If this is null a
	 * default shading from black to red will be used. 0=black, 255+ = red
	 * @deprecated 
	 */
	 private int off=0,dw,dh;
	 private boolean set = false ; // is the data layer set
	public void paintScaled(Graphics g, Scaler scale, Shader shade, GeoData dat,ShadeStyle style){

	  int h,w;
	  Canvas obs = new Canvas();
		MemoryImageSource source;

		GeoRectangle gr = scale.getMapExtent();
		if(debug)System.out.println("RaL->gr "+gr);
		GeoRectangle me = getBounds();
		if(debug)System.out.println("RaL->me "+me);

		// first see if we are on screen
		GeoRectangle out = gr.createIntersect(me);
		//if(debug)System.out.println("RaL->gr "+gr+"\nme "+me+"\nout "+out);
		if(out!=null){ // we're on screen so do something
			if(debug)System.out.println("RaL->out "+out.getX()+" "+out.getY());
			// first adjust our output origin to be a cell boundary
			double	cellsize=r.getCellSize();

			if(!set){ // this is a first draw for this raster
				int here[];
				int pt=0;
				if(debug)System.out.println("RaL->out "+out);
				if(debug)System.out.println("RaL->x:"+((out.x-me.x)/cellsize)+" "+(out.height/cellsize));
				data = new int[r.getHeight()*r.getWidth()];
				h=r.getHeight();
				w=r.getWidth();
				dw=w;
				dh=h;
				off=0;
				// set up the underlying image data
				for (int i=0;i<h;i++){
					for (int j=0; j<w;j++){
									data[pt++]=shade.getRGB(r.getCell(i,j));
					}
				}
				if(debug)System.out.println("RaL->Painted "+(pt)+" pixels");
				//set=true;
			}
			/* to prevent jumping we need the output area to start and end on a 
			 * cell boundary. Thus we move the out rectangle origin down and to 
			 * the left and adjust the width and height so we only draw whole
			 * cells even if the viewer doesn't see all of them
			 */
			double newy1 = ((int)Math.floor(out.getY()/cellsize))*cellsize;
			double newx1 = ((int)Math.floor(out.getX()/cellsize))*cellsize;
			double newy2 = ((int)Math.ceil((out.getY()+out.getHeight())/cellsize))*cellsize;
			double newx2 = ((int)Math.ceil((out.getX()+out.getWidth())/cellsize))*cellsize;
			if(debug)System.out.println("RaL->out "+out);
			/* now we need to check we haven't actually moved beyond the extent
			 * of the raster 
			 */
			if(newx1<me.x){
				if(debug)System.out.println("RaL->too left "+newx1+" -> "+me.x);
				newx1=me.x;
			}
			if(newy1<me.y) {
				if(debug)System.out.println("RaL->too low "+newy1+" -> "+me.y);
				newy1=me.y;
			}
			if(newx2>(me.x+me.width)){
				if(debug)System.out.println("RaL->too wide "+newx2+" -> "+(me.x+me.width));
				newx2=me.x+me.width;
			}
			if(newy2>(me.y+me.height)) {
				if(debug)System.out.println("RaL->too high "+newy2+" -> "+(me.y+me.height));
				newy2=me.y+me.height;
			}
			out=new GeoRectangle();
			out.add(newx1,newy1);
			out.add(newx2,newy2);
			if(debug)System.out.println("RaL->new "+out);
			if(debug)System.out.println("RaL->new x "+(out.x/cellsize));
			if(debug)System.out.println("RaL->new y "+(out.y/cellsize));
			if(debug)System.out.println("RaL->raster paint part short cut");
			// now find offset into the data array and calc new width and height

			w=(int)Math.round((out.width/cellsize));
			h=(int)Math.round(((out.height)/cellsize));
			if(debug)System.out.println("RaL->width "+w+" "+out.width+" "+(out.width/cellsize));
			if(debug)System.out.println("RaL->height "+h+" "+out.height+" "+(out.height/cellsize));
			int startx = r.getCellCol(out.x);
			int starty = r.getCellRow(out.y+out.height);
			if(debug)System.out.println("RaL->offsets "+startx+" "+(starty)+" "+out.y+" "+out.height);
		// now draw the data
			if(debug)System.out.println("RaL->w/h 1"+w+" "+h);
			/* in theory these never get used and could be deleted but I'm
			 * paranoid!
			 */
			if(h>r.getHeight()){
				if(debug)System.out.println("RaL->too high 2");
				h=r.getHeight();
			}
			if(w>r.getWidth()){
				if(debug)System.out.println("RaL->too wide 2");
				w=r.getWidth();
			}
			if(startx<0){
				if(debug)System.out.println("RaL->too left 2");
				startx=0;
			}
			if(starty<0){
				if(debug)System.out.println("RaL->too low 2");
				starty=0;
			}
			/* given an offset and output width we can use memoryimagesource to 
			 * do the actual drawing 
			 */
			off= (int)((starty)*dw+startx);
			/* the following works arround a bug(?) in netscape 4.05 where it
       * crashes if you try to draw part of the array in the bottom R
       * corner. Since it doesn't do this in Appletviewer or ME IE4/5 
			 * I blame netscape and work round it, the problem may be in my code
			 * or my understanding of how memoryimage source handles offsets
			 */
      if(debug)System.out.println("RaL->dw "+dw+" "+w+" "+(dw-w));
      if(debug)System.out.println("RaL->dh "+dh+" "+h+" "+(dh-h));
      int first_offset = dw*(dh-h); // the whole block at the top
      int second_offset = (dw-w)*h; // the bit down the left side
      int out_bit = w*h; // the actual image to draw
      // the sum of these three bits must be less than the total image
      if(debug)System.out.println("RaL->off "+first_offset+" "+second_offset+" "+
        out_bit+" -> "+(first_offset+second_offset+out_bit)+" "+data.length);
      if(first_offset+second_offset+out_bit>data.length){ return;} // this is bad
      /* It seems that this netscape fix breaks other vms - what do I know*/
      //if(first_offset+second_offset+out_bit==data.length){ h--;} // this is netscape

			if(debug)System.out.println("RaL->offsets "+startx+" "+(starty)+" "+off);
			if(debug)System.out.println("RaL->w/h 2"+w+" "+h);
			if(debug)System.out.println("RaL->data "+data.length+" off:"+off+" "+(w*h)+" "+(off+w*h));
			source = new MemoryImageSource(w,h,data,off,dw);
			image = obs.createImage(source);
			/* now we need to know where to draw the image, I think that this can
			 * cause a slight jump to the output raster as we're moving from a
			 * double to the nearest int pixel position but thats life!
			 */
			int origin[]=scale.toGraphics(out.x,out.y);
			int gh=scale.toGraphics(out.height);
			int gw=scale.toGraphics(out.width);
			if(debug)System.out.println("RaL->out "+out.x+" "+out.y);
			if(debug)System.out.println("RaL-> -->"+origin[0]+" "+origin[1]+" "+(origin[1]-gh));
			//	boolean fred = g.drawImage(image,origin[0],origin[1]-gh,obs);
			// we should use image.getScaledInstance here
			boolean fred = g.drawImage(image,origin[0],origin[1]-gh,
			    gw,gh,obs);
			if(debug)g.setColor(Color.yellow);
			if(debug)g.drawRect(origin[0],origin[1]-gh,gw,gh);
			if(grid){
				for(int i = origin[0];i<origin[0]+gw;i+=scale.toGraphics(cellsize)){
					g.drawLine(i,origin[1]-gh,i,origin[1]);
				}
				for(int i = origin[1];i<origin[1]+gh;i+=scale.toGraphics(cellsize)){
					g.drawLine(origin[0],i-gh,origin[0]+gw,i-gh);
				}
				int r=(int)(out.y/cellsize);
				int c=(int)(out.x/cellsize);
				int c2=scale.toGraphics(cellsize)/2;
				g.setColor(Color.black);
				for(int j = origin[1];j<origin[1]+gh;j+=scale.toGraphics(cellsize)){
					for(int i = origin[0];i<origin[0]+gw;i+=scale.toGraphics(cellsize)){
						g.drawString((""+(c++)+","+r),i,j-gh+c2);
					}
					r++;
					c=(int)(out.x/cellsize);
				}
			}
		} else{ // off screen
		}
	}

	/**
	 * @return the id of the point
	 */
	public int getID(GeoPoint p){
		return r.getCellPos(p.x,p.y);
	}
	/**
	 * @return the id of the point
	 */
	public int getID(double x, double y){
		return r.getCellPos(x,y);
	}

  /**
   * Returns all grid cell IDs indicated by rect and mode.
   *
   * @param rect the rectange that describes the region to select from
   * @param mode the mode to use when selecting gridCells
   *
   * @return an array of grid cell IDs
   *
   * @see SelectionManager
   **/
  public int[] getIDs( GeoRectangle rect, int mode ) {

    double originX = r.getOriginx();
    double originY = r.getOriginy();
    
    double cellSize = r.getCellSize();

    int minXCoord = 0;
    double minX = 0;
    int minYCoord = 0;
    double minY = 0;
    
    int xWidth = 0;
    int yWidth = 0;    

    switch ( mode ) {
    case SelectionManager.CONTAINS:

      minXCoord = (int)Math.ceil( (rect.getX() - originX) / cellSize );
      minX = r.getXCell( minXCoord );
      minYCoord = (int)Math.ceil( (rect.getY() - originY) / cellSize );
      minY = r.getYCell( minYCoord );
      
      xWidth = (int)Math.floor( (rect.getX() + rect.getWidth() - minX)
                                / cellSize );
      yWidth = (int)Math.floor( (rect.getY() + rect.getHeight() - minY)
                                / cellSize );

      break;
    case SelectionManager.CROSSES:

      minXCoord = (int)Math.floor( (rect.getX() - originX) / cellSize );
      minX = r.getXCell( minXCoord );
      minYCoord = (int)Math.floor( (rect.getY() - originY) / cellSize );
      minY = r.getYCell( minYCoord );
      
      xWidth = (int)Math.ceil( (rect.getX() + rect.getWidth() - minX)
                               / cellSize );
      yWidth = (int)Math.ceil( (rect.getY() + rect.getHeight() - minY)
                               / cellSize );

      
      break;
    default:

      throw new RuntimeException
        ( "invalid mode: expected either SelectionManager.CONTAINS or "
          + "SelectionManager.CROSSES" );

    }
    
    int[] arr = new int[ xWidth * yWidth ];
    int index = 0;

    for ( int x = 0; x < xWidth; x ++ ) {
      for ( int y = 0; y < yWidth; y ++ ) {
        arr[ index ] = r.getCellID( minXCoord + x, minYCoord + y );
        index++;
      }      
    }

    return arr;
    
  }
  
	/**
	 * @return the maximum value of the contained raster
	 */
	public double getMax(){
		return r.getMax();
	}
	/**
	 * @return the minimum value of the contained raster
	 */
	public double getMin(){
		return r.getMin();
	}
	public double getNZMin(){
		return r.getNZMin();
	}
	/**
	 * return the data associated with this layer
	 */
  public GeoData getGeoData(){
    // shoud do something in future
		return r;
  }
	/**
	 * set the data associated with this layer
	 */
  public void setGeoData(GeoData data){
		if(data instanceof Raster){
			r=(Raster)data;
		}else{
		// complain loudly
		}

  }


  public double getSparseness(){
		return r.getSparseness();
	}
	public boolean isSparse(){
		return r.isSparse();
	}
	public void changeRaster(Raster n){
		r=n;
		set=false;
		System.gc();
		notifyLayerChangedListeners(LayerChangedEvent.GEOGRAPHY);	
	}

	public void Changed(ChangedEvent e){
		set=false;
		notifyLayerChangedListeners(e.getReason());
	}

	public GeoRectangle getBoundsOf(int id){
		int row = id%r.getWidth();
		int col = id/r.getWidth();
		double x = r.getXCell(col);
		double y = r.getYCell(row);
		return new GeoRectangle(x,y,r.getCellSize(),r.getCellSize());
	}
	public GeoRectangle getBoundsOf(int id[]){
		GeoRectangle gr =  new GeoRectangle();
		for(int i=0;i<id.length;i++){
			gr.add(getBoundsOf(id[i]));
		}
		return gr;
	}

	public Image getImage(Shader shade){// returns a copy of the current screen image
	  Canvas obs = new Canvas();
		MemoryImageSource source;
		data = new int[r.getHeight()*r.getWidth()];
		int h=r.getHeight();
		int w=r.getWidth();
		int off=0;
		int pt =0;
		// set up the underlying image data
		for (int i=0;i<h;i++){
			for (int j=0; j<w;j++){
					data[pt++]=shade.getRGB(r.getCell(i,j));
			}
		}
		if(debug)System.out.println("RaL->Painted "+(pt)+" pixels");
		set=false;
		source = new MemoryImageSource(w,h,data,off,w);
		image = obs.createImage(source);
		return image;
	}


}


/*
 * $Log: RasterLayer.java,v $
 * Revision 1.1  2005/09/19 10:31:32  CarstenKessler
 * Carsten Keﬂler:
 * First Version submitted to CVS.
 *
 * Revision 1.7  2001/08/01 12:32:46  ianturton
 * modification submited by Michael Becke <becke@u.washington.edu> to reduce
 * memory usage.
 *
 *
 *
 */

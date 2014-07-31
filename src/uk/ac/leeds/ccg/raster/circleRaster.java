package uk.ac.leeds.ccg.raster;

import java.util.Vector;

import uk.ac.leeds.ccg.geotools.CircleLayer;
import uk.ac.leeds.ccg.geotools.GeoCircle;
import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoPolygon;
import uk.ac.leeds.ccg.geotools.GeoRectangle;
import uk.ac.leeds.ccg.geotools.LayerChangedEvent;
import uk.ac.leeds.ccg.geotools.PolygonLayer;
import uk.ac.leeds.ccg.geotools.RampShader;
import uk.ac.leeds.ccg.geotools.Shader;
import uk.ac.leeds.ccg.geotools.Theme;
import uk.ac.leeds.ccg.geotools.Viewer;
import uk.ac.leeds.ccg.geotools.XYDisplay;

public final class circleRaster extends Raster{

	private final static boolean DEBUG=false;
	private boolean quantizeOn = true;
    public boolean isQuantizeOn(){
        return quantizeOn;
    }
    public void setQuantizeOn(boolean flag){
        quantizeOn=flag;
    }

	/**
	 * Builds a raster surface based on an array of circles,
	 * produces the
	 * surface by using a density kernel based on the value of the circle.
	 * The mapextent is calculated from the circles.
	 * @see uk.ac.leeds.ccg.raster.circleRaster#quantize
	 * @param circles[] the array of circles
	 * @param size the size of the cells in the raster
	 * @param data[] the values of the circles
	 */
	public circleRaster(){
		super();
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.raster.circleRaster constructed. Will identify itself as CRa->");
	}
	public circleRaster(GeoCircle[] circles,double size,double data[]){
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.raster.circleRaster constructed. Will identify itself as CRa->");
		sparseness=0.0d;
		sparse=true;
		GeoRectangle extent = new GeoRectangle();
		cellsize=size; // we should calculate this?
		for(int i=0; i<circles.length;i++)
			extent.add(circles[i].getBounds());
		originx=extent.x;
		originx=Math.floor(extent.x/cellsize)*cellsize;
		originy=extent.y;
		originy=Math.floor(extent.y/cellsize)*cellsize;
		width=(int)Math.ceil(extent.width/cellsize);
		height=(int)Math.ceil(extent.height/cellsize);
		cells= new double[height*width];
		for(int i=0; i<circles.length;i++){
			if(onSurface(circles[i].getX(),circles[i].getY())){
				quantize(circles[i].getX(),circles[i].getY(),circles[i].getRadius(),
					data[i]);
			}
		}
		if(getSparseness()>50.0d) {
			setSparse(true);
		}
		min=Double.MAX_VALUE;
		max=Double.MIN_VALUE;
		for(int i=0;i<(height);i++){
			for(int j=0;j<width;j++){
				min=Math.min(min,getCell(i,j));
				max=Math.max(max,getCell(i,j));
			}
		}
	}
	public circleRaster(Vector circles,double size,double data[]){
		sparseness=0.0d;
		sparse=true;
		GeoRectangle extent = new GeoRectangle();
		cellsize=size; // we should calculate this?
		for(int i=0; i<circles.size();i++)
			extent.add(((GeoCircle)circles.elementAt(i)).getBounds());
		originx=extent.x;
		originx=Math.floor(extent.x/cellsize)*cellsize;
		originy=extent.y;
		originy=Math.floor(extent.y/cellsize)*cellsize;
		width=(int)Math.ceil(extent.width/cellsize);
		height=(int)Math.ceil(extent.height/cellsize);
		cells= new double[height*width];
		GeoCircle c;
		for(int i=0; i<circles.size();i++){
			c=(GeoCircle)circles.elementAt(i);
			if(onSurface(c.getX(),c.getY())){
				quantize(c.getX(),c.getY(),c.getRadius(),
					data[i]);
			}
		}
		if(getSparseness()>50.0d) {
			setSparse(true);
		}
		min=Double.MAX_VALUE;
		max=Double.MIN_VALUE;
		for(int i=0;i<(height);i++){
			for(int j=0;j<width;j++){
				min=Math.min(min,getCell(i,j));
				max=Math.max(max,getCell(i,j));
			}
		}
	}
	public circleRaster(Vector circles,double size,GeoData data){
		sparseness=0.0d;
		sparse=true;
		GeoRectangle extent = new GeoRectangle();
		cellsize=size; // we should calculate this?
		for(int i=0; i<circles.size();i++)
			extent.add(((GeoCircle)circles.elementAt(i)).getBounds());
		originx=extent.x;
		originx=Math.floor(extent.x/cellsize)*cellsize;
		originy=extent.y;
		originy=Math.floor(extent.y/cellsize)*cellsize;
		width=(int)Math.ceil(extent.width/cellsize);
		height=(int)Math.ceil(extent.height/cellsize);
		cells= new double[height*width];
		GeoCircle c;
		for(int i=0; i<circles.size();i++){
			c=(GeoCircle)circles.elementAt(i);
			if(onSurface(c.getX(),c.getY())){
				quantize(c.getX(),c.getY(),c.getRadius(),
					data.getValue(c.getID()));
			}
		}
		if(getSparseness()>50.0d) {
			setSparse(true);
		}
		min=Double.MAX_VALUE;
		max=Double.MIN_VALUE;
		for(int i=0;i<(height);i++){
			for(int j=0;j<width;j++){
				min=Math.min(min,getCell(i,j));
				max=Math.max(max,getCell(i,j));
			}
		}
	}
	/**
   * Builds a raster surface based on an array of circles,
   * produces the
   * surface by using a density kernel based on the value of the circle.
   * @see uk.ac.leeds.ccg.raster.circleRaster#quantize
   * @param circles[] the array of circles
   * @param size the size of the cells in the raster
   * @param data[] the values of the circles
	 * @param m the mapextent of the raster.
   */

	public circleRaster(GeoCircle[] circles,double size,double data[],GeoRectangle
			m ){
	    this(circles,size,data,m,true);
	        }
	public circleRaster(Vector circles,double size,double data[],GeoRectangle
			m ){
	    this(circles,size,data,m,true);
		}
	public circleRaster(GeoCircle[] circles,double size,double data[],GeoRectangle
			m ,boolean quantize){
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.raster.circleRaster constructed. Will identify itself as CRa->");
		setQuantizeOn(quantize);
		sparseness=0.0d;
		GeoRectangle extent = m;
		cellsize=size; // we should calculate this?
		originx=extent.x;
		originx=Math.floor(extent.x/cellsize)*cellsize;
		originy=extent.y;
		originy=Math.floor(extent.y/cellsize)*cellsize;
		width=(int)Math.ceil(extent.width/cellsize);
		height=(int)Math.ceil(extent.height/cellsize);
		cells= new double[height*width];
		for(int i=0; i<circles.length;i++){
			if(onSurface(circles[i].getX(),circles[i].getY())){
				quantize(circles[i].getX(),circles[i].getY(),circles[i].getRadius(),
					data[i]);
			}
		}
		if(getSparseness()>50.0d) {
			setSparse(true);
		}
		min=Double.MAX_VALUE;
		max=Double.MIN_VALUE;
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				min=Math.min(min,getCell(i,j));
				max=Math.max(max,getCell(i,j));
			}
		}
	}
	public circleRaster(Vector circles,double size,double data[],GeoRectangle
			m ,boolean quantize){
	    setQuantizeOn(quantize);     
		sparseness=0.0d;
		GeoRectangle extent = m;
		cellsize=size; // we should calculate this?
		originx=extent.x;
		originx=Math.floor(extent.x/cellsize)*cellsize;
		originy=extent.y;
		originy=Math.floor(extent.y/cellsize)*cellsize;
		width=(int)Math.ceil(extent.width/cellsize);
		height=(int)Math.ceil(extent.height/cellsize);
		cells= new double[height*width];
		GeoCircle c;
		for(int i=0; i<circles.size();i++){
		 c = (GeoCircle)circles.elementAt(i);
			if(onSurface(c.getX(),c.getY())){
				quantize(c.getX(),c.getY(),c.getRadius(),
					data[i]);
			}
		}
		if(getSparseness()>50.0d) {
			setSparse(true);
		}
		min=Double.MAX_VALUE;
		max=Double.MIN_VALUE;
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				min=Math.min(min,getCell(i,j));
				max=Math.max(max,getCell(i,j));
			}
		}
	}
	
	
	
	/**
   * Builds an empty raster surface based
   * @see uk.ac.leeds.ccg.raster.circleRaster#quantize
   * @param size the size of the cells in the raster
   * @param m the mapextent of the raster.
   */

	public circleRaster(double size,GeoRectangle m ){
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.raster.circleRaster constructed. Will identify itself as CRa->");
		sparseness=0.0d;
		GeoRectangle extent = m;
		cellsize=size; // we should calculate this?
		originx=extent.x;
		originx=Math.floor(extent.x/cellsize)*cellsize;
		originy=extent.y;
		originy=Math.floor(extent.y/cellsize)*cellsize;
		width=(int)Math.ceil(extent.width/cellsize);
		height=(int)Math.ceil(extent.height/cellsize);
		cells= new double[height*width];

		if(getSparseness()>50.0d) {
			setSparse(true);
		}
		min=Double.MAX_VALUE;
		max=Double.MIN_VALUE;

	}


	/**
	 * Builds a raster surface of the centres of the circles
	 * @param circles the circles to be used
	 * @param size the size of the cells in the raster in geographic units
	 */
	public circleRaster(GeoCircle[] circles,double size){
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.raster.circleRaster constructed. Will identify itself as CRa->");
		GeoRectangle extent = new GeoRectangle();
		cellsize=size; // we should calculate this?
		for(int i=0; i<circles.length;i++)
			extent.add(circles[i].getBounds());
		originx=extent.x;
		originx=Math.floor(extent.x/cellsize)*cellsize;
		originy=extent.y;
		originy=Math.floor(extent.y/cellsize)*cellsize;
		width=(int)Math.ceil(extent.width/cellsize);
		height=(int)Math.ceil(extent.height/cellsize);
		cells= new double[height*width];
		for(int i=0; i<circles.length;i++){
			if(onSurface(circles[i].getX(),circles[i].getY())){
				quantize(circles[i].getX(),circles[i].getY(),circles[i].getRadius(),1);
			}
		}
		min=Double.MAX_VALUE;
		max=Double.MIN_VALUE;
		for(int i=0;i<(height*width);i++){
			min=Math.min(min,getValue(i));
			max=Math.max(max,getValue(i));
		}
	}
	/**
	 * Builds a raster surface of the circles
	 * @param circles the vector of circles to be used
	 * @param data the geodata of the circles to be used
	 * @param size the size of the cells in the raster in geographic units
	 */
	public circleRaster(Vector circles,GeoData data ,double size){
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.raster.circleRaster constructed. Will identify itself as CRa->");
		//System.out.println("CRa->Build a raster - calc extent");
		GeoRectangle extent = new GeoRectangle();
		cellsize=size; // we should calculate this?
		for(int i=0; i<circles.size();i++)
			extent.add(((GeoCircle)circles.elementAt(i)).getBounds());
		buildRaster(circles,data,size,extent);
		//System.out.println("CRa->Built a raster - calc extent");
	}
	public circleRaster(Vector circles,GeoData data ,double size,GeoRectangle extent){
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.raster.circleRaster constructed. Will identify itself as CRa->");
		//System.out.println("CRa->Build a raster - given extent");
		buildRaster(circles,data,size,extent);
		//System.out.println("CRa->Built a raster - given extent");
	}
	public void buildRaster(Vector circles,GeoData data ,double size,GeoRectangle extent){
		sparseness=0.0d;
		cellsize=size; // we should calculate this?
		originx=extent.x;
		originx=Math.floor(extent.x/cellsize)*cellsize;
		originy=extent.y;
		originy=Math.floor(extent.y/cellsize)*cellsize;
		width=(int)Math.ceil(extent.width/cellsize);
		height=(int)Math.ceil(extent.height/cellsize);
		cells= new double[height*width];
		//System.out.println("CRa->About to build a raster "+circles.size()+" Circles");
		for(int i=0; i<circles.size();i++){
	//		if(onSurface(((GeoCircle)circles.elementAt(i)).getX(),((GeoCircle)circles.elementAt(i)).getY())){
				quantize(((GeoCircle)circles.elementAt(i)).getX(),((GeoCircle)circles.elementAt(i)).getY(),
					((GeoCircle)circles.elementAt(i)).getRadius(),
					data.getValue(((GeoCircle)circles.elementAt(i)).getID()));
			//}
		}
		//System.out.println("CRa->half finished building a raster "+circles.size()+" Circles");
		min=Double.MAX_VALUE;
		max=Double.MIN_VALUE;
		for(int i=0;i<(height*width);i++){
			min=Math.min(min,getValue(i));
			max=Math.max(max,getValue(i));
		}
		//System.out.println("CRa->finished building a raster "+circles.size()+" Circles");
	}
	/**
	 * Builds a raster surface of the centres of the circles, cellsize is one
	 * geographic unit.
	 * @param circles the circles to be used
	 */
	public circleRaster(GeoCircle[] circles){
		this(circles,1.0);
	}
	/**
	 * produce a kernel density surface at x,y with radius r and height value
	 * after Epanechnikov (1969) and Brunsdon (1990)
	 */
	double lastRadius = -1;
	int m;
	int numb;
	double xker[];
	double xadr[];
	double yadr[];
	
	
	protected final void quantize(double x,double y,double radius,double value){
		if(radius<(cellsize)){
			addToCell(x,y,value);
			return;
		}
		if(radius !=lastRadius){
		    lastRadius=radius;
		    double rsq = radius*radius;
		    double min= -radius-cellsize/2.0;
		    double max= radius+cellsize/2.0;

		    
		    numb=(int)(Math.ceil((2.0*(radius+cellsize)))/cellsize);
		    xker = new double[numb*numb];
		    xadr = new double[numb*numb];
		    yadr = new double[numb*numb];
		    double yy,xx,xxsq,dis,sum;
		    xx=min;
		    m=0;
		    sum=0;
		    for(int i=0;i<=numb;i++){
			    yy=max;
			    xxsq=xx*xx;
			    for (int j=0;j<=numb;j++){
				    dis=xxsq+yy*yy;
				    if(dis<=rsq){
					    //System.out.println("CRa->x "+xx+" y "+yy+" "+dis+" "+rsq);
					    xadr[m]=xx;
					    yadr[m]=yy;
					    xker[m]=1.0d-dis/rsq;
					    sum+=xker[m];
					    m++;
				    }
				    yy-=cellsize;
			    }
			    xx+=cellsize;
		    }
		    if(sum>0.0d){
			    sum=(1.0d/sum);
		    }
		    for(int j=0;j<m;j++){
			    xker[j]*=sum;
		    }
		}
	// now apply it to the cells
		for(int j=0;j<m;j++){
			if(quantizeOn)
				addToCell(x+xadr[j],y+yadr[j],(value*xker[j]));
			else
				addToCell(x+xadr[j],y+yadr[j],value);
		}
			
	}
	
	
	
	
	public final void addGeoCircles(Vector circles, Vector scores){
		GeoRectangle out = new GeoRectangle(getBounds());
		GeoCircle gc;
		for(int i=0;i<circles.size();i++){
			gc=(GeoCircle)circles.elementAt(i);
			out.add(gc.getBounds());
			setBounds(out);
			quantize(gc.getX(),gc.getY(),gc.getRadius(),
				((Double)scores.elementAt(i)).doubleValue());
		}
		notifyChangedListeners(LayerChangedEvent.GEOGRAPHY);
	}	
	public final void addGeoCircles(Vector circles, double data){
		GeoRectangle out = new GeoRectangle(getBounds());
		GeoCircle gc;
		for(int i=0;i<circles.size();i++){
			gc=(GeoCircle)circles.elementAt(i);
			out.add(gc.getBounds());
			setBounds(out);
			quantize(gc.getX(),gc.getY(),gc.getRadius(),data);
		}
		notifyChangedListeners(LayerChangedEvent.GEOGRAPHY);
	}	


	public final void addGeoCircle(GeoCircle circle,double data){
		//if(!onSurface(circle.getX(),circle.getY())){
			GeoRectangle out = new GeoRectangle(getBounds());
			out.add(circle.getBounds());
			setBounds(out);
		//}
		quantize(circle.getX(),circle.getY(),circle.getRadius(),
			data);
		notifyChangedListeners(LayerChangedEvent.GEOGRAPHY);
	}
	public static void main(String args[]){
		java.awt.Frame f = new java.awt.Frame ("Circle Raster test");
		Viewer v = new Viewer();
		v.setSize(200,200);
		v.setBackground(java.awt.Color.blue);
		f.setLayout(new java.awt.BorderLayout());
		f.add(v,"Center");
		GeoRectangle gr = new GeoRectangle(0.0,0.0,20.0,20.0);
		circleRaster cr = new circleRaster(0.45,gr);
		//Raster cr = new Raster(gr,1.0);
		GeoCircle c = new GeoCircle(1,15.0,15.0,4);
		//GeoPoint gp = new GeoPoint(50.5,50.5);
		cr.addGeoCircle(c,100);

		
		for(int i = 0; i < 3;i++){
			for(int j = 0; j < 3;j++){
				if(DEBUG)System.out.println("CRa->row "+cr.getCellRow((double)(i+2))+" col "+cr.getCellCol((double)(j+2)));
				cr.addToCell((double)(i+2),(double)(j+2),(i*j+1));
			}
		}
		double [] x = new double[4];
		double [] y = new double[4];
		x[0]=2.0;y[0]=2.0;
		x[1]=5.0;y[1]=2.0;
		x[2]=5.0;y[2]=5.0;
		x[3]=2.0;y[3]=5.0;
		GeoPolygon gp = new GeoPolygon(1,x,y,4);
		PolygonLayer pl = new PolygonLayer();
		pl.addGeoPolygon(gp);
		
		RasterLayer rl = new RasterLayer(cr);
		Shader sh = new RampShader(0.0,10.0);
		sh.setMissingValueCode(0.0);
		Theme t = new Theme(rl,sh);
//		PointLayer pl = new PointLayer();

		//pl.addGeoPoint(gp);
		Theme t2 = new Theme(pl);
		t2.getShadeStyle().setLineWidth(5);
		t2.getShadeStyle().setLineColor(java.awt.Color.black);
		t2.getShadeStyle().setIsFilled(false);
		CircleLayer cl = new CircleLayer();
		cl.addGeoCircle(c);
		Theme t3 = new Theme(cl);
		t3.getShadeStyle().setIsFilled(false);
		v.addTheme(t);
		v.addTheme(t2);
		v.addTheme(t3);
		XYDisplay xyd=new XYDisplay();
		v.addMouseMotionListener(xyd);
		v.addMouseListener(xyd);
		java.awt.Panel p = new java.awt.Panel();
		p.setLayout(new java.awt.FlowLayout());
		p.add(new uk.ac.leeds.ccg.widgets.ToolBar(v));
		p.add(xyd);
		f.add(p,"South");
		f.pack();
		f.setVisible(true);
	}
}

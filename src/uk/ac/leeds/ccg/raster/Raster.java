package uk.ac.leeds.ccg.raster;

import java.awt.Canvas;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoRectangle;
import uk.ac.leeds.ccg.geotools.SimpleGeoData;

/**
 * A class to hold raster data sets 
 * @author <a href="http://www.geog.leeds.ac.uk/staff/i.turton/i.turton.html">
 * Ian Turton</a> Centre for
 * Computaional Geography, University of Leeds, LS2 9JT, 1998.<br>
 * <a href="mailto:ian@geog.leeds.ac.uk">i.turton@geog.leeds.ac.uk</a>
 **/

/**
 * It is common to want to distinguish between a missing value (nodata) and a 
 * value that is zero or another value that does not effect the result of a
 * calulation (e.g. The value 0.0d in addition and subtraction, and the value 
 * 1.0d as an exponent power or as a value in a multiplication or the 
 * denominator in a division).
 *
 * A default value can be defined as the value which every cell has if it is not
 * in an (id,list) hashtable containing the other values. In version 1.9 of 
 * Raster the default value was always 0.0d. This was changed to be nodata in 
 * version 1.10 and get and set methods for this value were provided.
 *
 * There are advantages in terms of memory usage to using a hashtable if more 
 * than half the values are the default value (because of the need to store the 
 * id otherwise in the hashtable).
 *
 * There are various options for Raster storage that should be considered for 
 * geoTools2. The nature of the values in a Raster and its size determine what
 * what storage is best in terms of memory usage and speed of getting and 
 * setting values.
 **/

public class Raster extends SimpleGeoData implements Serializable{

private final static boolean DEBUG=false;
String name="Raster";
/**
 * the value below which a cell is considered zero, defaults to 1e-7
 * @see #getTol()
 * @see #setTol()
 */
double TOL =1e-7;
/**
 * proportion of raster with missing values
 */
double sparseness;
/**
 * The left hand edge of the raster
 * @see #getOriginx()
 * @see #getOrigin()
 */
protected double originx;
/**
 * The bottom edge of the raster
 * @see #getOriginy()
 * @see #getOrigin()
 */
protected double originy;
/**
 * The cell size of the raster
 * @see #getCellSize()
 */
protected double cellsize;
/**
 * The nodata value of the raster
 * @see #getNoDataValue()
 */
protected double nodata;
/**
 * GeoData missing value code
 * double MISSING=Double.NaN;
 * @see #getMissingValueCode()
 */
double missing = GeoData.MISSING;
/**
 * The minimum value of the raster
 * @see #getMin()
 */
protected double min;
protected double nzmin;
/**
 * The maximum value of the raster
 * @see #getMax()
 */
protected double max;
/**
 * The height of the raster in cells
 * @see #getHeight()
 */
protected int height;
/**
 * The width of the raster in cells
 * @see #getWidth()
 */
protected int width;
/**
 * the values of the raster
 * @see #sparse
 */
protected double cells[];
/**
 * Vector to store a sparse raster
 * @see #sparse
 */
//private Vector scells = new Vector(1,2000);
protected Hashtable scells = new Hashtable();

/**
 * Is this Raster stored in a sparse form or not
 * @see #setSparse()
 * @see #isSparse()
 */
protected boolean sparse=false;
/**
 * @see #public class MyInt()
 */
MyInt in = new MyInt();

    /** 
     * build a new empty raster of size 0.
     */
    public Raster() {
        this(new GeoRectangle(),1.0);
    }

    /**
    * builds a new raster from an image.
    */
    public Raster(Image im, int width, int height, GeoRectangle gr) {
        this(gr,gr.getWidth()/(double)width);
        double xsize=gr.getWidth()/(double)width;
        double ysize = gr.getHeight()/(double)(height);
        if(DEBUG)System.out.println("Ra-->width "+width+" -> "+this.width+" "+xsize);
        if(DEBUG)System.out.println("Ra-->height "+height+" -> "+this.height+" "+ysize);
        if(DEBUG)System.out.println("Ra-->cellsize "+cellsize);
        Canvas obs = new Canvas();
        //int width = im.getWidth(obs);
        //int height = im.getHeight(obs);
        int[] data = new int[(height*width)];
        if(DEBUG)System.out.println("Ra-->image "+width+" by "+height);
        PixelGrabber pg = new PixelGrabber(im,0,0,width,height,data,0,width);
        if(DEBUG)System.out.println("Ra-->about to grab");
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        System.err.println("Ra-->interrupted waiting for pixels!");
            return;
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("Ra-->image fetch aborted or errored");
            setBounds(new GeoRectangle());
            return ;
        }
        if(DEBUG)System.out.println("Ra-->Post grab");
        im=null;
        System.gc();
        ColorModel cm = ColorModel.getRGBdefault();
        int k=0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                //int alpha = (data[k] >> 24) & 0xff;
                //int red   = (data[k] >> 16) & 0xff;
                //int green = (data[k] >>  8) & 0xff;
                //int blue  = (data[k]      ) & 0xff;
                //int alpha = cm.getAlpha(data[k]);
                //int red   = cm.getRed(data[k]);
                //int green = cm.getGreen(data[k]);
                //int blue  = cm.getBlue(data[k]);
                //addToCell(originx+i*xsize,originy+j*ysize,(double)green);
                //addToCell(originx+i*xsize,originy+j*ysize,(double)green);
                //System.out.println("Ra-->on? "+onSurface(j,i));
                addToCell(j,i,(double)(data[k]));
                //System.out.println("Ra-->"+(j)+ " "+(i)+ " "+red+" "+green+" "+blue+" "+getCell(j,i));
                k++;
            }
        }
    }

    /**
     * build a new empty raster 
     * @param top the top of the raster in geographic space
     * @param left the left edge of the raster in geographic space
     * @param size the cellsize of the raster in geographic units
     * @param h the height of the raster in cells
     * @param w the width of the raster in cells
     */
    public Raster(double top, double left, double size, int h, int w){
        if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.raster.Raster constructed. Will identify itself as Ra-->");
	cellsize=size;
	originx=left;
	originx=Math.floor(left/cellsize)*cellsize; // Integerizes originx!
	originy=top-h;
	originy=Math.floor(originy/cellsize)*cellsize; // Integerizes originy!
	height=h;
	width=w;
	sparse=true;
	//cells= new double[h*w];
    }

    public Raster(GeoRectangle m,double size,double[] data){
        if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.raster.Raster constructed. Will identify itself as Ra-->");
	cellsize=size;
	originx=m.x;
	originx=Math.floor(m.x/cellsize)*cellsize;
	originy=m.y;
	originy=Math.floor(m.y/cellsize)*cellsize;
	this.height=(int)Math.round(m.height/cellsize);
	this.width=(int)Math.round(m.width/cellsize);
	cells=new double[height*width];
	//if(DEBUG)System.out.println("Ra-->h = "+height);
	//if(DEBUG)System.out.println("Ra-->w = "+width);
	int p=0;
	for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
		//if(DEBUG)System.out.println("Ra-->p = "+p+" d = "+data[p]);
		addToCell(i,j,data[p++]); // alternatively putCell(i,j,data[p]); p++;
            }
	}
	if(getSparseness()>50.0d) {
            setSparse(true);
	}
    }

    public Raster(GeoRectangle m,double size,XYVData[] data){
        if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.raster.Raster constructed. Will identify itself as Ra-->");
        cellsize=size;
        originx=m.x;
        originx=Math.floor(m.x/cellsize)*cellsize;
        originy=m.y;
        originy=Math.floor(m.y/cellsize)*cellsize;
        sparseness=0d;
        height=(int)Math.round(m.height/cellsize);
        width=(int)Math.round(m.width/cellsize);
        sparse=true;
        scells=new Hashtable();
        //cells=new double[height*width];
        cells=new double[1];
        min=Double.MAX_VALUE;
        nzmin=Double.MAX_VALUE;
        max=Double.MIN_VALUE;
        if(data==null) {
           min=0.0;
           max=0.0;
           return;
        }
        for(int i=0;i<data.length;i++){
            addToCell(data[i].x,data[i].y,data[i].value);
            min=Math.min(min,data[i].value);
            max=Math.max(max,data[i].value);
            if(data[i].value>0.0) nzmin=Math.min(nzmin,data[i].value);
        }
        if(getSparseness()<50.0d) {
            setSparse(false);
        }
    }

    public Raster(GeoRectangle m,double size,RCVData[] data){
        cellsize=size;
        originx=m.x;
        originx=Math.floor(m.x/cellsize)*cellsize;
        originy=m.y;
        originy=Math.floor(m.y/cellsize)*cellsize;
        sparseness=0d;
        height=(int)Math.round(m.height/cellsize);
        width=(int)Math.round(m.width/cellsize);
        sparse=true;
        scells=new Hashtable();
        //cells=new double[height*width];
        min=Double.MAX_VALUE;
        nzmin=Double.MAX_VALUE;
        max=Double.MIN_VALUE;
        if(data==null) return;
        for(int i=0;i<data.length;i++){
            addToCell(data[i].row,data[i].col,data[i].value);
            min=Math.min(min,data[i].value);
            max=Math.max(max,data[i].value);
            if(data[i].value>0.0) nzmin=Math.min(nzmin,data[i].value);
	}
	if(getSparseness()<50.0d) {
            setSparse(false);
	}
    }

    public Raster(GeoRectangle m, double size){
        if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.raster.Raster constructed. Will identify itself as Ra-->");
        cellsize=size;
        originx=m.x;
        originx=Math.floor(m.x/cellsize)*cellsize;
        originy=m.y;
        originy=Math.floor(m.y/cellsize)*cellsize;
        sparseness=0d;
        height=(int)Math.round(m.height/cellsize);
        width=(int)Math.round(m.width/cellsize);
        sparse=true;
        scells=new Hashtable();
        cells=new double[1];
    }
    
    /**
     * read in an arc/info ascii grid file 
     * @param name the name of the file to read from
     * @exception IOException if there are any problems with the file
     */
    public Raster(String name) throws java.io.IOException { // this is broken - originx and origin y are probably wrong!!!
        String tok;
        int type,n;
        double val,left,bot,size;
        InputStream in = new FileInputStream(name);
        Reader r = new BufferedReader(new InputStreamReader(in));
        StreamTokenizer st = new StreamTokenizer(r);
        st.parseNumbers();
        st.wordChars('_','_');
        st.eolIsSignificant(false);
        st.lowerCaseMode(true);
        // get ncols
        type=st.nextToken();
        type=st.nextToken();
        width=(int)st.nval;
        // get nrows
        type=st.nextToken();
        type=st.nextToken();
        height=(int)st.nval;
        // get xllcorner
        type=st.nextToken();
        type=st.nextToken();
        originx=st.nval;
        // get yllcorner
        type=st.nextToken();
        type=st.nextToken();
        originy=st.nval;
        // get cellsize
        type=st.nextToken();
        type=st.nextToken();
        cellsize=st.nval;
        // try to get nodata - its optional!
        type=st.nextToken();
        if(type==StreamTokenizer.TT_NUMBER){
            st.pushBack(); // put it back if its a number - thats data
            //nodata=0.0d;
            nodata=missing;
        }else{
            type=st.nextToken();
            nodata=st.nval;
        }
        st.ordinaryChars('E','E');
        // Set array to store the data;
        cells= new double[height*width];
        int pt=0;
        max=Double.NEGATIVE_INFINITY;
        min=Double.POSITIVE_INFINITY;
        /*if (DEBUG) {
            System.out.println("ncols "+ncols);
            System.out.println("nrows "+nrows);
            System.out.println("xllcorner "+xllcorner);
            System.out.println("yllcorner "+yllcorner);
            System.out.println("noDataValue "+noDataValue);
        }*/
        // Read and write values.
        double d1;
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                st.nextToken();
                d1=st.nval;
                type=st.nextToken();
                if (type!=StreamTokenizer.TT_NUMBER && type!=StreamTokenizer.TT_EOF) { 
                    /* Either an exponent term number or end of file marker or something is wrong (eg. grid value is non-numeric)! */
                    st.nextToken();
                    d1=d1*Math.pow(10.0,st.nval);
                    //if (DEBUG) {System.out.println("Exponent!");}
                }
                else {
                    st.pushBack();
                }
                putCell(i,j,d1);
                /* see putCell()
                if (getCell(i,j)!=nodata) {
                    min=Math.min(getCell(i,j));
                    max=Math.max(max,getCell(i,j));
                }*/
            }
	}
        //recalc();
        if(getSparseness()>50.0d) {
            setSparse(true);
        }
    }

/**
 * determine if the geographic coordinate given is on the surface.
 * @param x  the x location in geographic units
 * @param y  the y location in geographic units
 */
public final boolean onSurface(double x,double y){
    return(x>=originx&&x<originx+(width*cellsize)&&y>=originy&&y<originy+((height)*cellsize));
}
public final boolean onSurface(int row,int col){
    return(row>=0&&row<(height)&&col>=0&&col<width);
}

protected final int getCellRow(double y){
    // System.out.println("Ra-->h "+(height)+" y "+y+" "+originy+" cell "+cellsize+
    //" = "+((((double)(height)*cellsize-y+originy)/cellsize))+
    //" = "+((int)Math.round(((double)(height)*cellsize-y+originy)/cellsize)));
    return((int)Math.floor(((double)(height)*cellsize-y+originy)/cellsize));
}

protected final int getCellCol(double x){
    return((int)Math.floor((x-originx)/cellsize));
}

protected final double getXCell(int col){
    return (originx+(double)col*(double)cellsize);
}

protected final double getYCell(int row){
    return (originy+((double)((height)-row)*(double)cellsize));
}

/**
 * convert a geographic location to a pointer to the array of data
 * @param x  the x location in geographic units
 * @param y  the y location in geographic units
 * @return position in the array
 */
protected final int getCellPos(double x,double y){
    //int row=(int)(((height)*cellsize-y+originy)/cellsize);
    //int col=(int)((x-originx)/cellsize);
    int row = getCellRow(y);
    int col = getCellCol(x);
    //if(col<0||col>=width)System.out.println("Ra-->X = "+x+"-> "+col+" w "+width);
    //if(row<0||row>=height)System.out.println("Ra-->Y = "+y+"-> "+row+" h "+height);
    return getCellID( col, row );
}

/**
 * Converts a x/y grid coordinate to a location in the array of data (ID).
 * @param xCoord the x coordinate of a gridCell (column)
 * @param yCoord the y coordinate of a gridCell (row)
 * @return the ID for this grid coordinate
 **/
protected final int getCellID( int xCoord, int yCoord ) {
    return (yCoord * getWidth()) + xCoord;
}

final public double getTol(){
    return TOL;
}

final public void setTol(double t){
    TOL=t;
    recalc();
}

/**
 * @return the minimium value in the raster
 */
final public double getMin(){
    return min;
}
final public double getNZMin(){
    return nzmin;
}

/**
 * @return the maximum value in the raster
 */
final public double getMax(){
    return max;
}

/**
 * @param row the row required in cells (from the top).
 * @param col the column required in cells (from the left).
 * @return the value at the row and col of the raster.
 */
final public double getCell(int row,int col){
    if(sparse){
        in.setValue(row*width+col);
        Double v=(Double)scells.get(in);
        if(v!=null){
        //if(!v.isNaN()){
            //if(DEBUG)System.out.println("Ra--><-"+row+" "+col+" "+in+" "+v);
            return v.doubleValue();
        }else{
            //return(0.0d);
            //return missing;
            return nodata;
        }
    }else{
        return(cells[row*width+col]);
    }
}

/**
 * @param x location in geographic space
 * @param y location in geographic space
 * @return the value at (x,y) of the raster or GeoData.MISSING
 * @see uk.ac.leeds.ccg.geotools.GeoData
 */
final public double getCell(double x,double y){
    if(onSurface(x,y)){
        int row=getCellRow(y);
        int col=getCellCol(x);
        return getCell(row,col);
    }else{
        //return(0.0d);
        //return missing;
	return nodata;
    }
}

/**
 * Adds a value to a cell 
 * @param row the row required in cells (from the top).
 * @param col the column required in cells (from the left).
 * @param value the value to be added to the cell
 */
final public double addToCell(int row,int col,double value){
    double v=getCell(row,col);
    if (v!=missing && v!=nodata) {
        if (value!=nodata && value!=missing) {
            return putCell(row,col,v+value);
        }
        else {
            // What is in the cell is right already so do nothing!
            return v;
        }
    }
    else {
        return putCell(row,col,value);
    }    
    /*
    // In some cases this is slower than the above. When what is in the cell is
    // the correct answer already this still calls the putCell(int,int,double) 
    // method rather than simply returning the answer that it has already got.
    if (v!=missing && v!=nodata) {
        if (value!=nodata && value!=missing) {
            v+=value;
        }
    }
    else {
        v=value;
    }
    return putCell(row,col,v);
    */
    /*if(onSurface(row,col)){
	if(sparse){
	    if(Math.abs(value-0.0d)>TOL){
		in.setValue(row*width+col);
		Double v=(Double)scells.get(in);
		//if(DEBUG)System.out.println("Ra-->+>"+in+" "+value+" ("+v+")");
		if(v!=null){
		    scells.remove(in);
		    scells.put(in,new Double(v.doubleValue()+value));
		    min=Math.min(min,v.doubleValue()+value);
		    nzmin=Math.min(nzmin,v.doubleValue()+value);
		    max=Math.max(max,v.doubleValue()+value);
		}else{
		    sparseness+=1.0;
		    scells.put(in,new Double(value));
		    min=Math.min(min,value);
		    nzmin=Math.min(nzmin,value);
		    max=Math.max(max,value);
		}
	    }
	}else{
	    if(Math.abs(value-0.0d)>TOL&&(Math.abs(cells[row*width+col]-0.0d)<TOL))
		sparseness+=1.0;
	    // if there is already a value then adding to it doesn't increment
	    // the full cell count.
	    cells[row*width+col]+=value;
	    min=Math.min(min,cells[row*width+col]);
	    nzmin=Math.min(nzmin,cells[row*width+col]);
	    max=Math.max(max,cells[row*width+col]);
	}
    }*/
}

/**
 * Adds a value to a cell 
 * @param x the x location of the cell 
 * @param y the y location of the cell
 * @param value the value to be added to the cell
 */
final public double addToCell(double x,double y,double value){
    int row=getCellRow(y);
    int col=getCellCol(x);
    return addToCell(row,col,value);
}
	
/**
 * Puts a value in to the cell at row,col of the raster.
 * carries out a check to see if the cell is valid and ignores the call
 * if it is not.
 * @see #onSurface()
 */
final public double putCell(int row,int col,double value){
    if(onSurface(row,col)){
        if (value!=nodata) {
            //!!!Warning min may not be the current minimum value of the Raster.
            //   (e.g consider the case where the value in row col was the 
            //    unique minimum value in the Raster and it was replaced by a 
            //    higher number.)
            //!!!min really stores the minimum value this Raster has ever 
            //   contained!!!
            //!!!Warning max may not be the current maximum value of the Raster.
            //   (e.g consider the case where the value in row col was the 
            //    unique maximum value in the Raster and it was replaced by a 
            //    lower number.)
            //!!!max really stores the maximum value this Raster has ever 
            //   contained!!!
            //!!!There are various ways around this problem.
            min=Math.min(min,value);
            max=Math.max(max,value);
        }
	if(sparse){
            in.setValue(row*width+col);
            // Remove and correct sparseness if there is a data value there already
            if (getCell(row,col)!=nodata) {
                scells.remove(in);
                sparseness-=1.0d;
            }
            if (value!=nodata) {
                sparseness+=1.0d;
                scells.put(in,new Double(value));
            }
        }
        else{
            if (value!=nodata) {
                sparseness+=1.0d;
            }
            if (cells[row*width+col]!=nodata) {
                sparseness-=1.0d;
            }
            cells[row*width+col]=value;
        }
        //recalc();
        return value;
    }
    else{
        //if(DEBUG)System.out.println("Ra-->"+row+" "+col+" off surface");
        return -1.0d;
    }
}
					
/**
 * Puts a value in to the cell at x,y of the raster.
 * carries out a check to see if the cell is valid and ignores the call
 * if it is not.
 * @see #onSurface()
 */
final public double putCell(double x,double y,double value) {
    if(onSurface(x,y)) {
        int row=getCellRow(y);
        int col=getCellCol(x);
        return putCell(row,col,value);
    }
    return -1.0d;
}

public double getMissingValueCode(){
    return missing;
}

public void setMissingValueCode(double mv){
    missing=mv;
}

public void setNoDataValue(double d) {
    nodata=d;
    //setMissingValueCode(d);
}

public double getNoDataValue() {
    return nodata;
}

/**
 * calculate and return the bounding box of the raster.
 * @return the bounding box of the raster
 */
public GeoRectangle getBounds(){
    GeoRectangle box = new
    GeoRectangle(originx,originy,(double)width*(double)cellsize,(double)height*(double)cellsize);
    //if(DEBUG)System.out.println("Ra-->Ras "+box);
    return(box);
}

/**
 * set the bounds of the raster to a new value
 * the new bounding box may be larger or smaller as required, if the new
 * bounding box is smaller information may be lost if it falls outside
 * the new bounding box.
 */
public void setBounds(GeoRectangle m){
    XYVData[] data=getXYVData();
    //if(DEBUG)System.out.println("Ra-->old "+getBounds());
    // set the origin to a round number of cellsizes !!
    originx=Math.floor(m.x/cellsize)*cellsize;
    //if(DEBUG)System.out.println("Ra-->mx "+m.x+" ox "+originx);
    originy=Math.floor(m.y/cellsize)*cellsize;
    //if(DEBUG)System.out.println("Ra-->my "+m.y+" oy "+originy);
    width=(int)((m.width)/cellsize);
    height=(int)((m.height)/cellsize);
    //if(DEBUG)System.out.println("Ra-->new "+getBounds());
    sparseness=0.0d;
    if(sparse) {
        scells=new Hashtable();
    }else{
        cells=new double[width*height];
        if(data!=null){
            for(int i=0;i<data.length;i++){
                putCell(data[i].x,data[i].y,data[i].value);
            }
        }
    }
}

/**
 * set originx
 */
public void setOriginX(double d) {
    originx=d;
}

/**
 * set originy
 */
public void setOriginY(double d) {
    originy=d;
}


/**
 * @return the cell size of the raster
 */
public double getCellSize(){
    return cellsize;
}

/**
 * @return the width of the raster in cells
 */
public int getWidth(){
    return width;
}

/**
 * @return the height of the raster in cells
 */
public int getHeight(){
    return height;
}

/**
 * @return the origin of the raster in geographic space
 */
public GeoPoint getOrigin(){
    GeoPoint p = new GeoPoint(originx,originy);
    return(p);
}

/**
 * @return the x origin of the raster in geographic space
 */
public double getOriginx(){
    return originx;
}

/**
 * @return the y origin of the raster in geographic space
 */
public double getOriginy(){
    return originy;
}

/**
 * @return the name of the raster
 */
public String getName(){
    return name;
}

/**
 * Sets the name of the raster.
 */
public void setName(String n){
    name=n;
}
			
/**
 * gives a value that may be the coresponding cell id
 * @param id the cell id
 * @return the cell value or a GeoData.MISSING if too high or low.
 */
public double getValue(int id){
    if(id>=width*height){
	//return missing;
        //return 0.0d;
	return nodata;
    }
    if(id<0){
	//return missing;
        //return 0.0d;
	return nodata;
    }
    if(sparse){
        in.setValue(id);
        Double v=(Double)scells.get(in);
        if(v!=null){
            return v.doubleValue();
        }else{
            //return 0.0d;
            //return missing;
            return nodata;
        }
    }else{
        return(cells[id]);
    }
}

/**
 * @return the size of the raster in cells
 */
public int getSize(){
    return(height*width);
}

public String getText(int id){
    return new Double(getValue(id)).toString();
}

/** 
 * @return if the raster is stored sparsly
 */
public boolean isSparse(){
    return sparse;
}

/**
 * change if the raster is to be stored in a sparse form
 * @param flag the state to be set.
 */
public void setSparse(boolean flag){
    if(flag==sparse) return;
    if(flag==true){
       switchtosparse();
    }else{
        switchtofull();
    }
}

private void recalc(){
//public void recalc(){
    int p=0;
    if(sparse){
        switchtofull();
        if(getSparseness()>50)switchtosparse();
    }else{
        switchtosparse();
        if(getSparseness()<50)switchtofull();
    }
}

private void switchtosparse(){
    int p=0;
    sparseness=0d;
    sparse=true;
    for(int i=0;i<height;i++){
        for(int j=0;j<width;j++){
            //if(Math.abs(cells[p]-0.0d)>TOL){
            //if (cells[p]!=nodata) {
                putCell(i,j,cells[p]);
            //}
            p++;
        }
    }
    cells=new double[1];
    System.gc();// we need to free cells 
    return;
}

private void switchtofull(){
    int p=0;
    cells=new double[height*width];
    for(int i=0;i<height;i++){
        for(int j=0;j<width;j++){
            cells[p++]=getCell(i,j);
        }
    }
    scells = new Hashtable(1);
    System.gc();
    sparse=false;
    return; 
}

/**
 * Returns the percentage of the raster that is non-zero
 */
public double getSparseness(){
    return 100.0d -(sparseness/(width*height)*100.0d);
}

public int getMissingCount(){
    return (int)((width*height)-sparseness);
}

/**
 * returns a double array containing the data of the raster
 * @see #getSize()
 */
public double[] getData(){
    double out[]=new double[height*width];
    for(int i=0;i<height;i++){
        for(int j=0;j<width;j++){
            out[i*width+j]=getCell(i,j);
        }
    }
    return(out);
}

public void setData(double[] in){
    for(int i=0;i<height;i++){
        for(int j=0;j<width;j++){
            //if(DEBUG)System.out.println("Ra-->"+i+" "+j+" "+(i*width+j)+" "+in[i*width+j]);
            putCell(i,j,in[i*width+j]);
            //if(DEBUG)System.out.println("Ra-->"+in[i*width+j]+" "+getCell(i,j));
        }
    }
    if(getSparseness()>50.0d&&isSparse()==false) {
        if(DEBUG)System.out.println("Ra-->Going sparse");
        setSparse(true);
    }
    if(getSparseness()<50.0d&&isSparse()==true) {
        if(DEBUG)System.out.println("Ra-->Going full");
        setSparse(false);
    }
}	

/**
 * set cellsize
 */
public void setCellSize(double d) {
    cellsize=d;
}

/**
 * internal class that stores raster data in row column value form
 * @see #XYVData
 */
static class RCVData{
    int row,col;
    double value;
}

/**
 * internal class that stores raster data in geographic coordinates value
 */
class XYVData{
    double x,y;
    double value;
}

/** 
* save a raster to the file named in name.
* @throws java.io.IOException
* @deprecated new programs should use writeObject.
*/
public void save(String name) throws java.io.IOException{
    DataOutputStream out = new DataOutputStream(new FileOutputStream(name));
    out.writeUTF(this.name);
    out.writeDouble(originx);
    out.writeDouble(originy);
    out.writeInt(height);
    out.writeInt(width);
    out.writeDouble(cellsize);
    out.writeDouble(nodata);
    //out.writeDouble(TOL);
    out.writeBoolean(sparse);
    out.writeDouble(sparseness);
    if(sparse){
        RCVData a[]=getRCVData();
        for(int i=0;i<a.length;i++){
            out.writeInt(a[i].row);
            out.writeInt(a[i].col);
            out.writeDouble(a[i].value);
        }
    }else{
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                out.writeDouble(getCell(i,j));
            }
        }
    }
}

/** 
* load a raster from a URL 
* @throws java.io.IOException
*/
public static Raster load(URL url) throws java.io.IOException{
    URLConnection uc = url.openConnection();
    int len = uc.getContentLength();
    if(len <=0){
        throw new IOException("File fetched from URL was of zero length or could not be found");
    }
    byte data[];
    data = new byte[len];
    BufferedInputStream inn = new BufferedInputStream(uc.getInputStream());
    int jj=0,k=0;
    while(k<len || jj==-1){
        jj = inn.read(data,k,len-k);
        k+=jj;
    }
    ByteArrayInputStream bais = new ByteArrayInputStream (data);
    BufferedInputStream bin = new BufferedInputStream(bais);
    DataInputStream in = new DataInputStream(bin);
    return streamLoad (in);
}

/** 
* load a raster from file named in name.
* @throws java.io.IOException
* @deprecated new programs should use readObject.
*/
public static Raster load(String name) throws java.io.IOException{
    InputStream dis = new FileInputStream(name);
    BufferedInputStream bin = new BufferedInputStream(dis);
    DataInputStream in = new DataInputStream(bin);
    return streamLoad (in);
}
	
/** 
* load a raster from the Stream.
* @throws java.io.IOException
* @deprecated new programs should use readObject.
*/
public static Raster streamLoad (DataInputStream in) throws java.io.IOException {
    String fname=in.readUTF();
    double originx=in.readDouble();
    double originy=in.readDouble();
    int height=in.readInt();
    int width=in.readInt();
    double cellsize=in.readDouble();
    GeoRectangle m = new GeoRectangle(originx,originy,(double)width*(double)cellsize,(double)height*(double)cellsize);
    double nodata=in.readDouble();
    //double TOL=in.readDouble();
    boolean sparse=in.readBoolean();
    double sparseness=in.readDouble();
    if(sparse){
        //Raster out=new Raster();
        Raster.RCVData a[]=new RCVData[(int)sparseness];
        for(int i=0;i<a.length;i++){
            a[i]=new RCVData();
            a[i].row=in.readInt();
            a[i].col=in.readInt();
            a[i].value=in.readDouble();
        }
        Raster out=new Raster(m,cellsize,a);
        //out.setName(name);
        return out;
    }else{
        double c[]=new double[height*width];
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                c[i*width+j]=in.readDouble();
            }
        }
        Raster out=new Raster(m,cellsize,c);
        //out.setName(name);
        return out;
    }
}
		
/**
 * convert the data of the raster into an array of XYVData objects. 
 *
 * XYVData is a portable way of passing raster data about.
 * @return an array of XYVData objects.
 */
public XYVData[] getXYVData(){
    XYVData out[]=new XYVData[(int)sparseness];
    double val;
    int p=-1;
    if(sparseness==0.0){
        return null;
    }
    for(int i=0;i<height;i++){
        for(int j=0;j<width;j++){
            val=getCell(i,j);
            //if(DEBUG)System.out.println("Ra-->"+i+" "+j+" "+val);
            if(Math.abs(val-0.0d)>=TOL){
                p++;
                out[p]=new XYVData();
                out[p].value=val;
                out[p].x=getXCell(j);
                out[p].y=getYCell(i);
                //if(DEBUG)System.out.println("Ra-->j "+j+" -> x "+out[p].x);
                //if(DEBUG)System.out.println("Ra-->i "+i+" -> y "+out[p].y);
            }
        }
    }
    p++;
    if(p!=0)return(out);
    else
    return null;
}

public RCVData[] getRCVData(){
    RCVData out[]=new RCVData[(int)sparseness];
    double val;
    int p=0;
    for(int i=0;i<height;i++){
        for(int j=0;j<width;j++){
            val=getCell(i,j);
            if(Math.abs(val-0.0d)>TOL){
                out[p]=new RCVData();
                out[p].value=val;
                out[p].row=i;
                out[p].col=j;
                p++;
            }
        }
    }
    return(out);
}
		
public Object clone(){
    //if(DEBUG)System.out.println("Ra-->cloning ");
    Raster t= new Raster(getBounds(),getCellSize(),(XYVData[])null);
    t.sparse = this.sparse;
    t.sparseness = this.sparseness;
    t.TOL = this.TOL;
    if(t.sparse){
        t.scells=(Hashtable)this.scells.clone();
    }else{
        t.cells=new double[width*height];
        System.arraycopy(this.cells,0,t.cells,0,this.cells.length);
    }
    t.min=this.min;
    t.nzmin=this.nzmin;
    t.max=this.max;
    return t;
}

/**
 * @param a the raster to be added
 * @exception RasterMathException if the raster cellsizes differ
 */
public Raster RasterMath(Raster a,RasterOp op) throws RasterMathException{
    if(a==null) {
        throw new RasterMathException("Null Raster");
    }
    if(getCellSize()!=a.getCellSize()){
        // this won't work if the rasters are different scales
        throw new RasterMathException("Cell Size Mismatch\n "+getName()+" "+getCellSize()+" "+a.getName()+" "+a.getCellSize());
    }
    GeoRectangle out = new GeoRectangle(getBounds());
    out.add(a.getBounds());
    //Raster t1=(Raster)this.clone();
    Raster t1=this;
    GeoRectangle inter = getBounds().createIntersect(a.getBounds());
    t1.setBounds(out);
    //Raster t2 = new Raster(out,cellsize,d2);
    //Raster t2=(Raster)a.clone();
    Raster t2=a;
    //t2.setBounds(out);
    //t1.setName(getName());
    //t2.setName(a.getName());
    Raster t3 = new Raster(out,cellsize,(RCVData [])null);
    if(t1.getSparseness()==100.0d){
        return (Raster)t2.clone();
    }
    if(t2.getSparseness()==100.0d){
        return (Raster)t1.clone();
    }
    // now step through the two rasters applying op to them.
    if(!(t1.sparse&&t2.sparse)){ // if they are both sparse we can save time by just working through the non-zero cells
        double v;
        t3.setName("Output");
        t3.min=Double.MAX_VALUE;
        t3.nzmin=Double.MAX_VALUE;
        t3.max=Double.MIN_VALUE;
        for(int i=0;i<inter.height;i++){
            for(int j=0;j<inter.width;j++){
                v=op.calc(t1.getCell(i,j),t2.getCell(i,j));
                if(!Double.isInfinite(v)&&!Double.isNaN(v)){
                    t3.min=Math.min(t3.min,v);
                    if(v>0.0) t3.nzmin=Math.min(t3.nzmin,v);
                    t3.max=Math.max(t3.max,v);
                    t3.putCell(i,j,v);
                }
            }
        }
        return t3;
    }else{
        Double v1,v2;
        double v;
        t3.min=Double.MAX_VALUE;
        t3.nzmin=Double.MAX_VALUE;
        t3.max=Double.MIN_VALUE;
        MyInt pos;
        // this probably doesn't work properly if a op b != b op a and 0 is not an identity operation.
        java.util.Enumeration k = t1.scells.keys();
        while (k.hasMoreElements()){
            pos = (MyInt) k.nextElement();
            v1=(Double)t1.scells.get(pos);
            if(t2.scells.containsKey(pos)){
                v2=(Double)t2.scells.get(pos);
                v=op.calc(v1.doubleValue(),v2.doubleValue());
            }else{
                v=op.calc(v1.doubleValue(),0.0d);
            }
            if(Math.abs(v-0.0d)>TOL){
                t3.sparseness++;
            }
            if(!Double.isInfinite(v)&&!Double.isNaN(v)){
                t3.scells.put(pos, new Double(v));
                t3.min=Math.min(t3.min,v);
                if(v>0.0) t3.nzmin=Math.min(t3.nzmin,v);
                if(v!=Double.POSITIVE_INFINITY)t3.max=Math.max(t3.max,v);
            }
        }
        if(t3.min>0.0d) t3.min=0.0d; // no zeros in a sparse raster
        return t3;
    }
}

public final Raster RasterMath(double a,RasterOp op) throws RasterMathException{
    double v;
    Raster t1=(Raster)this.clone();
    if(a==0.0d&&op instanceof mult){ // mult by zero is zero
        t1.scells=new Hashtable();
        t1.cells=new double[1];
        t1.min=0.0d;
        t1.max=0.0d;
        t1.sparse=true;
        t1.sparseness=0.0;
        return t1;
    }
    if(a==1.0d&&(op instanceof mult||op instanceof divide)){ // identity
        return t1;
    }
    if(a==0.0d&&(op instanceof add||op instanceof sub)){ // identity
        return t1;
    }
    t1.min=Double.MAX_VALUE;
    t1.max=Double.MIN_VALUE;
    if(!t1.sparse){
        for(int i=0;i<t1.height;i++){
            for(int j=0;j<t1.width;j++){
                v=op.calc(getCell(i,j),a);
                t1.min=Math.min(t1.min,v);
                if(v>0.0)t1.nzmin=Math.min(t1.nzmin,v);
                t1.max=Math.max(t1.max,v);
                t1.putCell(i,j,v);
            }
        }
    }else{ /* sparse*/
        Double v1;
        MyInt pos;
        java.util.Enumeration k = t1.scells.keys();
        while (k.hasMoreElements()){
            pos = (MyInt) k.nextElement();
            v1=(Double)t1.scells.get(pos);
            t1.scells.remove(pos);
            v=op.calc(v1.doubleValue(),a);
            if(Math.abs(v-0.0d)>TOL&&Math.abs(v1.doubleValue()-0.0d)<TOL)
            t1.sparseness++; 
            t1.scells.put(pos,new Double(v));
            t1.min=Math.min(t1.min,v);
            t1.max=Math.max(t1.max,v);
        }
        t1.nzmin=t1.min;
        if(t1.min>0.0d)t1.min=0.0d; // if its sparse then we haven't seen any zeros
    }
    return t1;
}

public java.util.Enumeration getIds(){
    return new ID();
}

    class ID implements java.util.Enumeration{

    boolean more = true;
    int next=0;

        public boolean hasMoreElements(){
            return more;
        }

        public Object nextElement(){
            Integer n = new Integer(next++);
            if(next>width*height){
                more = false;
            }
            return n;
        }
    }


private void writeObject(java.io.ObjectOutputStream out) throws IOException{
    out.writeUTF(this.name);
    out.writeDouble(originx);
    out.writeDouble(originy);
    out.writeInt(height);
    out.writeInt(width);
    out.writeDouble(cellsize);
    out.writeDouble(nodata);
    //out.writeDouble(TOL);
    out.writeBoolean(sparse);
    out.writeDouble(sparseness);
    if(sparse){
        RCVData a[]=getRCVData();
        for(int i=0;i<a.length;i++){
            out.writeInt(a[i].row);
            out.writeInt(a[i].col);
            out.writeDouble(a[i].value);
        }
    }else{
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                out.writeDouble(getCell(i,j));
            }
        }
    }
}

private Object readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{ 
    String fname=in.readUTF();
    double originx=in.readDouble();
    double originy=in.readDouble();
    int height=in.readInt();
    int width=in.readInt();
    double cellsize=in.readDouble();
    GeoRectangle m = new GeoRectangle(originx,originy,(double)width*(double)cellsize,(double)height*(double)cellsize);
    double nodata=in.readDouble();
    //double TOL=in.readDouble();
    boolean sparse=in.readBoolean();
    double sparseness=in.readDouble();
    if(sparse){
        //Raster out=new Raster();
        Raster.RCVData a[]=new RCVData[(int)sparseness];
        for(int i=0;i<a.length;i++){
            a[i]=new RCVData();
            a[i].row=in.readInt();
            a[i].col=in.readInt();
            a[i].value=in.readDouble();
        }
        Raster out=new Raster(m,cellsize,a);
        //out.setName(name);
        return out;
    }else{
        double c[]=new double[height*width];
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                c[i*width+j]=in.readDouble();
            }
        }
        Raster out=new Raster(m,cellsize,c);
        out.setName(name);
        return out;
    }
}

static public void main(String args[]){
    // test function
    Raster t1=null,t2=null;
    ObjectInputStream in ;
    long start,inter;
    start=System.currentTimeMillis();
    try{
        in = new ObjectInputStream(new BufferedInputStream(new
        FileInputStream("/home/preston/ian/java/javashape/w3acc4.ras"),2048));
        t1=(Raster)in.readObject();
        inter=System.currentTimeMillis();
        if(DEBUG)System.out.println("Ra-->Loaded 1st file "+((inter-start)/1000));
        start=System.currentTimeMillis();
        in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("/home/preston/ian/java/javashape/w3pop4.ras"),10000));
        t2=(Raster)in.readObject();
        inter=System.currentTimeMillis();
        if(DEBUG)System.out.println("Ra-->Loaded 2nd file "+((inter-start)/1000));
    }
    catch(IOException e){System.out.println("Ra-->ArcInfo Load Failed "+e);}
    catch(ClassNotFoundException cne){System.out.println("Ra-->Class not found "+cne);}
    try{
        start=System.currentTimeMillis();
        t1=t1.RasterMath(t2,new add());
        inter=System.currentTimeMillis();
        if(DEBUG)System.out.println("Ra-->added "+((inter-start)/1000));
    }catch(RasterMathException e){System.out.println("Ra-->"+e);}
}

/* this sort of fudges the changes up to the layer */
Vector listeners = new Vector();

public void removeChangedListener(ChangedListener lcl) {
    listeners.removeElement(lcl);
}

public void addChangedListener(ChangedListener lcl){
   listeners.addElement(lcl);
}

public void notifyChangedListeners(int reason){
    Vector l;
    ChangedEvent lce = new ChangedEvent(this,reason);
    synchronized(this) {l = (Vector)listeners.clone(); }
    for (int i = 0; i < l.size();i++) {
        ((ChangedListener)l.elementAt(i)).Changed(lce);
    }
}

public void writeArcGrid(File f){
    BufferedOutputStream os ;
    try{
        os = new BufferedOutputStream(new FileOutputStream(f));
    }catch(Exception e){System.err.println("Ra-->Error in save "+e);return;}
    PrintWriter pw = new PrintWriter(os);
    pw.println("Ncols "+width+"\nnrows "+height);
    pw.println("xllcorner "+originx+"\nyllcorner "+originy);
    pw.println("cellsize "+cellsize);
    pw.println("NODATA_value "+nodata);
    for(int j=0;j<height;j++){
        for(int i=0;i<width;i++){
            pw.print(""+getCell(j,i)+" ");
        }
        pw.println();
    }
    pw.close();
}

    class MyInt {
    int value;
    MyInt(){}
    MyInt(int v){
        value=v;
    }
        void setValue(int v){
            value=v;
        }
        int intValue(){
            return value;
        }
        public int hashCode(){return value;}
        public String toString(){return ""+value;}
    }

}

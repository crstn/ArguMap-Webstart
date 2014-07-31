package uk.ac.leeds.ccg.geotools;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;


/**
 * Shape layer is holds all of the common code used by layers that contain GeoShape features
 * Much of the code in this class used to be duplicated multiple times by a number of classes
 * @since 0.7.0
 * @author James Macgill
 */
public abstract class ShapeLayer extends SimpleLayer implements Layer
{   
   /**
    * The list of shape featurs that make up the layer.
    */
    protected Vector shapeList;
    protected Quadtree shapeTree;
    //TODO JM when Quadtree is fixed, remove this option
    protected boolean useQuadtree = false;
    
    
     
   /**
    * The bounding rectangle that will contain all of the features in this layer
    */
    protected GeoRectangle bbox = new GeoRectangle();

   /**
    * Switch to turn the scale buffer on and off
    */
    protected boolean bufferScale = true;

    /**
     * The last scaler used to scale this map
     */
    protected Scaler lastScale = null;
    protected transient GeoRectangle lastExtent = null;

   /**
    * default constructor for an empty layer
    */
    public ShapeLayer(){
        shapeList = new Vector();
        shapeTree = new Quadtree();
    }

    /**
    * tempory constructor constructor for an empty layer with switch for using quadtree
    * TODO JM remove method once quadtree is fixed, and therefore, always used.
    */
    public ShapeLayer(boolean quadtreeEnabled){
        useQuadtree = quadtreeEnabled;
        shapeList = new Vector();
        shapeTree = new Quadtree();
    }

   /**
    * Constructs an empty layer, but with an inital expected bounds set
    * @param bounds A GeoRectangle that describes the initial bounds of this layer
    */
    public ShapeLayer(GeoRectangle bounds){
        this();
        bbox = bounds;
    }

   /**
    * Adds the specified shape to the list of features in this layer
    * Each layer type should implement a specific version of this for their own type
    * @param shape The GeoShape to add to the layer
    */
    protected void addGeoShape(GeoShape shape){
		this.addGeoShape(shape,false);
	}

   /**
    * Adds the specified shape to the list of features in this layer
    * Each layer type should implement a specific version of this for their own type
	* @param shape The GeoShape to add to the layer
	* @param keepQuiet Tell the method not to fire a LayerChangedEvent. this
	* more or less implies that you will do this yourself.
    */
    protected void addGeoShape(GeoShape shape, boolean keepQuiet){
        bbox.add(shape.getMultiPartBounds());
        shapeList.addElement(shape);
        //TODO JM remove if block when quadtree is fixed
        if(useQuadtree){shapeTree.add(shape);}
        if(!keepQuiet) this.notifyLayerChangedListeners(LayerChangedEvent.GEOGRAPHY);
    }


   /**
    * Gets the bounding box that will contain all of the features in this layer
    * @return GeoRectanle that defines the bounding rectangle
    */
    public GeoRectangle getBounds(){
        return (GeoRectangle)bbox.clone();
    }

   /**
	* Gets the bounding box of the specified feature
	* @since 0.6.5
	* @param id The ID of the feature to find the bounding box for
	* @return GeoRectangle the bounds of the specified feature
	*/
	public GeoRectangle getBoundsOf(int id){
	    for(int i = 0;i < shapeList.size();i++) {
	        GeoShape temp = (GeoShape)shapeList.elementAt(i);
		    if(temp.getID()==id){
		        return temp.getMultiPartBounds();
		    }
	    }
	    return new GeoRectangle(); //empty rect, should this be null?
	}
            	       
   /**
	* Gets the bounding box that contains all of the given features
	* @since 0.6.5
	* @param id[] The IDs of the features to find a bounding box for
	* @return GeoRectangle the bounds of the specified features
	*/ 
	public GeoRectangle getBoundsOf(int[] ids){
	    //needs optimizing!
	    GeoRectangle bounds = new GeoRectangle();
	    for(int i=0;i<ids.length;i++){
	        bounds.add(getBoundsOf(ids[i]));
	        //System.out.println("adding "+bounds);
	    }
	    //System.out.println("Slected final = "+bounds);
	    return bounds;
	}
            	        
   /**
	* Returns a count of the number of features in the layer
	* @return int The total number of features in this layer
	*/
	public int countFeatures(){
	return shapeList.size();
	}
            	         
   /**
	* Find the feature that contains this point.
	* <b>Note</b> This method will return the first feature found to contain the point only,
	* even if multiple, overlapping features, contain the same point.
	* @param point The GeoPoint to test each feature against.
	* @return int The ID of the first feature to contain this point, -1 if no feature was found
	*/
	public int getID(GeoPoint p){
	    //TODO JM remove if block once quadtree is working
	    if(useQuadtree){
	    GeoShape s = shapeTree.find(p);
	    if(s!=null)return s.getID();
	    return -1;
	    }
	    else{
	      for(int i = 0;i < shapeList.size();i++) {
			GeoShape temp = (GeoShape)shapeList.elementAt(i);
			if(temp.contains(p)){
			    return temp.getID();
			}
		}
		return -1;
        }
	}

   /**
	* Find the feature that contains this point.
	* <b>Note</b> This method will return the first feature found to contain the point only,
	* even if multiple, overlapping features, contain the same point.
	* @param x The x ordinate to test each feature against.
	* @param y The y ordinate to test each feature against.
	* @return int The ID of the first feature to contain this point, -1 if no feature was found
	*/
	public int getID(double x, double y){
	    return getID(new GeoPoint(x,y));
	}

   /**
	* Find the IDs features that are contained by, or intersect (mode dependent) the given rectangle
	* @param box The GeoRectangle to test each feature against
	* @param mode Selects between CONTAINS and INTERSECTS
	* @return int The IDs of the features to contain this point, zero length if no feature was found
	*/
	public int[] getIDs(GeoRectangle box,int mode){
	    //TODO JM remove if block once quadtree is working
	    if(useQuadtree){
	        Collection list = shapeTree.find(box);
	        Iterator it = list.iterator();
            int ids[] = new int[list.size()];
            int i=0;
            while(it.hasNext()){
                ids[i] = ((GeoShape)it.next()).getID();
                i++;
            }
            return ids;
        }
        else{

	    Vector list = new Vector();

		if(mode==SelectionManager.CONTAINS)
		{
			for(int i = 0;i < shapeList.size();i++) {
			    GeoShape temp = (GeoShape)shapeList.elementAt(i);
				GeoRectangle tempBox = temp.getBounds();

				if(!(tempBox.x > (box.x+box.width) || (tempBox.x+tempBox.width) < box.x ||
					tempBox.y > (box.y+box.height) || (tempBox.y+tempBox.height) < box.y))
				{
					if(box.contains(tempBox)) {
						list.addElement(temp);
					}

				}
			}
		}
		else if(mode==SelectionManager.CROSSES) // mode = INTERSECTS
		{
			/* For each feature in the shapelayer, check if the bounding box of the feature
			   intersects with the selected rectangle
			   Note this is not completely correct, since the bounding box of the feature
			   does not equal the feature (unless the feature has the shape of a rectangle itself.
			   So it's possible for the bounding box of the feature to intersect with the selected
			   rectangle, while the feature itself does not intersect
			*/
			for(int i = 0;i < shapeList.size();i++) {
				GeoShape temp = (GeoShape)shapeList.elementAt(i);
				GeoRectangle intersect = box.createIntersect(temp.getBounds());
				if(intersect!=null && intersect.width>0 && intersect.height>0)
				{
				    list.addElement(temp);
				}
			}
		}

		int[] IDs = new int[list.size()];
		for(int i=0;i<list.size();i++){
		    GeoShape temp = (GeoShape)list.elementAt(i);
		    IDs[i]=temp.getID();
		}
        return IDs;
        }
    }

    /**
	 * Switches the 'scaled version' buffer on and off
	 * Use this if the map will be redrawn without changing
	 * scale for some reason
	 * Typical applications involve animation of the layers shading.
	 */
	public void setScaleBuffer(boolean flag){
	    bufferScale = flag;
	    //force a rescale and rebuffer
		lastScale = null;
	}

	/**
	 * Find out if the scaleBuffer flag is switched on
	 * As not all layers suport scaleBuffering a result of true
	 * is not always a guarante that it will be used.
	 * @return boolean true if the scaleBuffer option is swiched on
	 */
	 public boolean isScaleBuffer(){
	    return bufferScale;
	 }


	 /**
	  * Use to determine if scale buffering is available for this layer.
	  * Layers that use this feature should overide this mehtod.
	  * @return boolean true if scale buffering can be used.
	  */
	  public boolean isScaleBufferImplemented(){
	    return false;
	  }

    public Vector getGeoShapes(){
        return (Vector)shapeList.clone();
    }
		/** 
		 * returns the GeoShape with the given id.
		 */
		public GeoShape getGeoShape(int id){
	    for(int i = 0;i < shapeList.size();i++) {
	        GeoShape temp = (GeoShape)shapeList.elementAt(i);
		    if(temp.getID()==id){
		        return temp;
		    }
	    }
			return null;	
		}

    protected boolean isExtentSame(GeoRectangle g){
        if(lastExtent!=null){
            if(g.equals(this.lastExtent)){
                return true;
            }
        }
        lastExtent = new GeoRectangle(g);
        return false;
    }

    /**
     * test method, probably not safe
     * need to clone all contents and build new vector before this method is usable
     */
    public Vector getShapes(){
        return shapeList;
    }


}

package uk.ac.leeds.ccg.geotools;

import java.awt.Graphics;

/**
 * A layer contains geographic feature of some kind that can be displayed in a map.
 * Anything that wants to end up on screen in a Viewer will need to 
 * implement this.<p>
 * Understanding Layers is key to understanding how geoTools works and if
 * you plan to add a new type of geographic feature then the layer interface is the place to
 * start.<p>
 * Layers are held in one or more themes which in turn are held in one or more
 * viewers.<p>
 * If you do write your own type of layer then extending simpleLayer will make 
 * life easyer for you.
 */
public interface Layer
{
    
		public static final int LOADING = 1;
		public static final int PENDING = 2;
		public static final int ERRORED = 4;
		public static final int ABORTED = 8;
		public static final int COMPLETED = 16;

    /**
     * Paints the layer to the screen.
     * The very hart of the layer mechanism, paintScaled is where layers
     * have to do most of their work.<br>
     * Calling and filling in of this method is handled by the theme to which
     * the layer has been added.<p>
     * The GeoGraphics object should provide you with everything
     * needed to plot a feature onto the screen.<br>
     * Inside gg you can use or not use the facilies provided as you see fit.<p>
     * gg.getGraphics() A Graphics object to which you should direct all of your output.
     * gg.getScale() A Scaler which you can use to convert real world(tm) co-ordinates
     * into on screen co-ordinates for use with the Graphics g object.
     * gg.getShade() shade A Shader, if you want to color your features based on a value (perhaps from the data peramiter) then
     * use shade.getColor(double value); to obtain the colours.
     * gg.getGeoData() A GeoData object, use this if your features have id's then you can obtain a coresponding value
     * from data.
     * gg.getStyle() style A style with hints on how to display the features
     */
    
    public void paintScaled(GeoGraphics gg);
    
    /**
     * Highligts the specified feature.
     * A simple version of paintScaled, this is used to paint a single feature
     * in some way that make it stand out from the other features.<br>
     * Todate, this has been by using a bright red colour, a more user-definable
     * system maybe implemented in later versions.<br>
     * Again the only thing that sould call this is a Theme<br>
     * @param g A Graphics object to paint the highlighted feature to.
     * @param scale A scaler that can be used to scale the feature.
     * @param id An int for the ID of the feature to be highlighted.
     * @param style A style with hints on how to display the highlight
     * @see uk.ac.leeds.ccg.geotools.Theme
     */
    public void paintHighlight(Graphics g,Scaler scale,int id,ShadeStyle style);
    
     /**
     * Dispays the specified features.
     * A simple version of paintScaled, this is used to paint a group of features
     * in some way that make it stand out from the other features.<br>
     * Todate, this has been by using a bright red colour, a more user-definable
     * system maybe implemented in later versions.<br>
     * Again the only thing that sould call this is a Theme<br>
     * @param g A Graphics object to paint the selected features to.
     * @param scale A scaler that can be used to scale the feature.
     * @param ids An int[] for the IDs of the features to be shown as selected.
     * @param style A style with hints on how to display the selection
     * @see uk.ac.leeds.ccg.geotools.Theme
     */
    public void paintSelection(Graphics g,Scaler scale,int ids[],ShadeStyle style);
    
    
    /**
     * Gets the smallest GeoRectangle that will contain all of this layers features. 
     * When implementing this, set up a new GeoRectangle that will define the region of interest for your
     * layer.<p>
     * @return GeoRectangle A GeoRectangle that will contain all of the features in the layer; For layers without geographic bounds it should return a null e.g. a scale bar or a norh arrow.
     */   
    public GeoRectangle getBounds();
    
    /**
     * Gets the smallest GeoRectangle that will contain the feature identified by the given ID.
     * @since 0.6.5
     * @param id The id of the feature to fetch the bounds of
     * @return GeoRectangle The bounds of the requested feature
     */
     public GeoRectangle getBoundsOf(int id);
     
     /**
     * Gets the smallest GeoRectangle that will contain the features identified by the given IDs.
     * @since 0.6.5
     * @param id[] The ids of the features to fetch the bounding rectangle
     * @return GeoRectangle The bounds of the requested feature
     */
     public GeoRectangle getBoundsOf(int id[]);
     
    
    /**
     * gets the ID of the feature that can be assosiated with a given point.
     * For example, the ID of a feature that contains the point.
     * @param p The GeoPoint of a location to test for a feature.
     * @return int The id of the feature assosiated with this point; return 0 if no feature is associated.
     */
    public int getID(GeoPoint p);
    
    /**
     * gets the ID of the features that can be assosiated with a given region.
     * For example, the IDs of all features that are within the rectangle.
     * @param box The GeoGeoractangle of the region of the query.
     *
     * @return int[] The ids of the features assosiated with this region; returns new int[0] if no features are selected.
     */
    public int[] getIDs(GeoRectangle box,int mode);
    
    /**
     * A convinence method that is the same as getID(GeoPoint) but witout having to construct a geoPoint.
     * @param x a double, the x co-ordinate.
     * @param y a double, the y co-ordinate.
     * @return int The id of the feature assosiated with this point; return 0 if no feature is associated.
     */
    public int getID(double x,double y);
    /*{
        GeoPoint p = new GeoPoint(x,y);
        return getID(p);
    }*/

		/** 
		 * get the status of this layer: in most cases this should be 
		 * Layer.COMPLETED to show that the layer is ready to paint.
		 * Where a layer has posponed loading it can be 
		 * Layer.PENDING, Layer.LOADING, Layer.ERRORED or Layer.ABORTED
		 */
		public int getStatus();
		/** 
		 * set the status of this layer: in most cases this should be 
		 * Layer.COMPLETED to show that the layer is ready to paint.
		 * Where a layer has posponed loading it can be 
		 * Layer.PENDING, Layer.LOADING, Layer.ERRORED or Layer.ABORTED<br>
                 * When the status becomes COMPLETED for the first time, a
                 * LayerChangedEvent is sent.
		 * <strong>Should only be called by the Layer Producer or 
		 * the Layer itself</strong>
		 */
		public void setStatus(int status);
    
    /**
     * Adds a LayerChangedListener to the layer, typicaly called by Theme
     * writen for you if you extend simpleLayer();
     */
    public void addLayerChangedListener(LayerChangedListener lcl);
    
    /**
     * Removes a LayerChangedListener to the layer, typicaly called by Theme
     * writen for you if you extend simpleLayer();
     */
    public void removeLayerChangedListener(LayerChangedListener lcl);
    
    /**
     * Notifis all LayerChangedListener's that this layer has been modified.
     * It should only normaly be called by the layer itself.
     * To make life easyer it has been writen for you if you extend simpleLayer.
     * Make sure you call it if some aspect of your feature changes such that you would like it
     * to be re-painted.<p>
     * @param reason An int code indicating what type of change has occured; LayerChangedEvent provides GEOGRAPHY and DATA as two
     * constants to use as reason codes.
     * @see LayerChangedListener
     */
    public void notifyLayerChangedListeners(int reason);

    /** Set the name of this layer so it can be viewed in toString() and
     * getName() later on.
     * @param n the name of the layer
     */
    public void setName(String n);
    
    /**
     * Get the name of this layer.  If it has not been set in setName(), then
     * the layer will be called "Unknown".
     */
    public String getName();
    
    /**
     * Get the name of this layer.  If it has not been set in setName(), then
     * the layer will be called "Unknown".
     */
    public String toString();

}

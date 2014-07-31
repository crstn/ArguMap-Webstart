package uk.ac.leeds.ccg.geotools;


import java.awt.Graphics;

/**
 * The GeoGraphics class is a wrapper for a standard awt.Graphics object
 * that also conains all of the style, filtering and scaleing objects
 * needed by a layer to paint itself.<p>
 * GeoGraphics are passed to Layers by theme to the paintScaled(GeoGraphics gg)
 * methiod.<p>
 * Origionaly, layers used a paintScaled(Graphics g, Scaler s, Style st...) method<br>
 * however, each additional item required a re-write of the Layer interface.<p>
 * By passing a GeoGraphics object, additonal features can be added more easely.<p>
 * @author James Macgill
 * @since 0.6
 * @version 0.7.1 12/Jan/00
 */
public class GeoGraphics
{
    /**
     * Layers can be passed a reason code to tell them why they have to repaint
     * at the moment there is only one reason, 0, but this may be used for
     * further expantion
     */
	protected int reason = (int)0;
	/**
	 * Contains info on filling, outlining and the like
	 */
	protected ShadeStyle style = (uk.ac.leeds.ccg.geotools.ShadeStyle)null;
	/**
	 * contains id/value information that can be used for passing to the shader
	 */
	protected GeoData data = (GeoData)null;
        /**
	 * contains id/text information that can be used for display as a label
	 */
	protected GeoData labelData = (GeoData)null;
	/**
	 * will process value/colour lookups for the layer.
	 */
	protected Shader shade = (uk.ac.leeds.ccg.geotools.Shader)null;
	/**
	 * Provides scaling services to match the view that this layer will be 
	 * drawn in.
	 */
	protected Scaler scale = (uk.ac.leeds.ccg.geotools.Scaler)null;
	/**
	 * A standard awt.Graphics object.
	 */
	protected java.awt.Graphics graphics = null;
	/**
	 * When not null, layers should check against the filter to see if
	 * each feature should actualy be drawn by passing the filter its id.
	 */
	protected Filter filter = null;
    /**
     * Used to store the stroke width for lines.
     */
    protected int width = 1;
    
    /**
     * Default constructor, not acutaly of much use to anyone.
     */
    public GeoGraphics(){
        
    }
    
    /**
     * A full constructor for a GeoGraphics object.
     * @version 0.7.1
     * 
     * @param g A standard awt.Graphics object, where the painting should take palce
     * @param scale Used to convert GeoPoints into screen coordinates for the viewer
     * @param shade Provides value/colour lookup for layers
     * @param data Provides id/value lookup for representing data.
     * @param style Contains information on filling and outlineing.
     * @param filter Provides id/visible lookup for filtering out features
     * @param reason A reason code for why a redraw is needed, not used at present.
     */
    public GeoGraphics(Graphics g,Scaler scale,Shader shade,GeoData data,GeoData labelData,ShadeStyle style,Filter filter,int reason){
        graphics = g;
        this.scale = scale;
        this.shade = shade;
        this.data = data;
        this.style = style;
        this.reason = reason;
        this.filter = filter;
        this.labelData = labelData;
    }
    
    /**
     * Find out why this GeoGraphics was created
     * not used at present, but may be used for future expantion
     * @return reason an int value that marks the code for the reason, not used yet.
     */
	public int getReason()
	{
		return reason;
	}
    
    /**
     * Get the awt.Graphics object so that some painting can actualy be done.<br>
     * This will tipicaly give you inderict access to the Viewers display.<br>
     * The graphics object will actualy be to some form of offscreen buffer, because of this
     * there is no need for users to implement any form of buffering themselves.
     *
     * @return Graphics An awt Graphics object that can be drawn to as normal.
     */
    public Graphics getGraphics(){
        return graphics;
    }
    
    /**
     * Change the graphics object contained by this object.
     * Probaly not needed, as it would be better to create a new GeoGraphics object instead.
     */
    public void setGraphics(Graphics g){
        graphics = g;
    }

	public void setReason(int propValue)
	{
		int temp = reason;
		reason = propValue;
	}
	
	/**
	 * Get the style object so that info on filling, outlining can be obtained.
	 * @return ShadeStyle the Style object.
	 */
	public ShadeStyle getStyle()
	{
		return style;
	}

	public void setStyle(uk.ac.leeds.ccg.geotools.ShadeStyle propValue)
	{
		uk.ac.leeds.ccg.geotools.ShadeStyle temp = style;
		style = propValue;
	}
	
	/**
	 * Get the data which should be used in this display
	 * @return GeoData A GeoData object against which id/value lookups should be performed.
	 */
	public GeoData getData()
	{
		return data;
	}
        
        /**
	 * Get the label data which should be used in this display
	 * @return GeoData A GeoData object against which id/text lookups should be performed.
	 */
	public GeoData getLabelData()
	{
		return labelData;
	}

	public void setData(GeoData propValue)
	{
		GeoData temp = data;
		data = propValue;
	}
	
	/**
	 * Any value/colour lookups should be performed by passing them through the
	 * Shader object provided by this method.
	 * @return Shader the Shader object to perform value/colour lookups against.
	 */
	public Shader getShade()
	{
		return shade;
	}

	public void setShade(uk.ac.leeds.ccg.geotools.Shader propValue)
	{
		uk.ac.leeds.ccg.geotools.Shader temp = shade;
		shade = propValue;
	}
	
	/**
	 * Any geographic coordinates can be converted into screen coordiates
	 * apropreate for the Graphics object by using the functions provied by the 
	 * scaler that is returned by this mehtod.
	 * @return Scaler for performing scaleing operations.
	 */
	public Scaler getScale()
	{
		return scale;
	}

    
	public void setScale(uk.ac.leeds.ccg.geotools.Scaler propValue)
	{
		uk.ac.leeds.ccg.geotools.Scaler temp = scale;
		scale = propValue;
	}
	
	
	public void setFilter(Filter f){
	    filter = f;
	}
	
	/**
	 * Filters allow featutres to be 'switched off' by id.
	 * By passing the id of any feature to the filter provided by this
	 * method, layers can check what features should be filtered out.
	 * @return Filter N.B. can be null, in which case no filtering should be performed.
	 */
	public Filter getFilter(){
	    return filter;
	}

}
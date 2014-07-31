package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Vector;

/**
 * A theme holds all of the information needed by a viewer to display a thematic map, including a layer, shadeing and highlightng scheem.
 * A theme acts as a container for all of the aspects that make up a thematic display of a geographic feature.  It
 * also handles all of the comunication between viewers, layers and some highlights.
 * Any clicks in the viewer can be set to pass to a theme where it will be converted to an ID from the layer and then finaly set in the highlightManager.
 * Once constructed a theme can be added to multiple viewers without problems.<p>
 * There are a number of parts that make up a theme, and this is reflected by the
 * number of constructers, the following is a list of all the parts. <br>
 * Constructors that lack parts will make their own as needed.<p>
 * The main parts that make up a <b>full</b> theme are:
 * <ul>
 * <li><b>layer: </b>Contains the geographic features for this theme.
 * <li><b>shade: </b>Defines the shading scheme to use when displaying.
 * <li><b>data: </b>The GeoData that will provide data to the layer.
 * <li><b>style: </b>Defines how a feature should be painted.
 * <li><b>highlightStyle: </b>Defines how highlighting should look.
 * <li><b>selectionStyle: </b>Defines how selections should look.
 * <li><b>highlight: </b>A highlight manager, probably shared with other themes
 * <li><b>selectionMgr: </b>A selection manager, can be shared with other themes
 * <li><b>name: </b>A name for this theme.
 * </ul>
 */
public class Theme extends Object implements FilterChangedListener,SelectionPositionChangedListener,HighlightPositionChangedListener,HighlightChangedListener,LayerChangedListener, SelectionChangedListener, SelectionRegionChangedListener,ShaderChangedListener,Serializable{
private final static boolean DEBUG=false;
protected Layer layer;
protected Shader shade = new MonoShader(Color.lightGray);
protected Filter filter = null; //i.e. no filtering
protected GeoData data = new SimpleGeoData();
protected GeoData tipData = new SimpleGeoData();
protected GeoData labelData = new SimpleGeoData(); //store for labels
protected GeoData[] complexTipData;
protected String tipformat;
protected ShadeStyle style = new ShadeStyle();
protected ShadeStyle highlightStyle = new ShadeStyle(true, true, Color.red,Color.red,false);
protected ShadeStyle selectionStyle = new ShadeStyle(true, true, Color.blue,Color.blue,false);
protected HighlightManager highlight;
protected Vector listeners;
protected SelectionManager selectionMgr;
protected String name = "Unnamed Theme";
public static final String cvsid = "$Id: Theme.java,v 1.1 2005/09/19 10:31:28 CarstenKessler Exp $";
    /**
     * A full constructor for a theme, requiering the provision of all parts.
     * @param l The layer containing the geographic features.
     * @param s The shader to use when colouring in the layer.
     * @param n A String describing or nameing this theme.
     * @param hm A HighlightManager, clicks in the viewer converted into highlight requests.
     * @param d The GeoData object to base link feature IDs in the layer with attribute values.
     * @param t The GeoData object to base link feature IDs in the layer with ToolTip info.
     * @param style A ShadeStyle object to pass to the layer.
     */
    public Theme(Layer l,Shader s,String n,HighlightManager hm,GeoData d,GeoData t,ShadeStyle style){
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.geotools.Theme constructed. Will identify itself as T--->");
		layer = l;
        if(s!=null){
            shade = s;
        }
        shade.addShaderChangedListener(this);
        //shade = s;
        name = n;
        if(d!=null){
            data = d;
        }
        if(t!=null){
            tipData = t;
        }
        /*else{
            tipData = data;
        }*/
        if(style!=null){
            this.style = style;
        }
        else
        {
            if(l instanceof LineLayer){
                this.style.setLineColorFromShader(true);
                if(s==null){shade = new MonoShader(Color.black);}
            }
        }
        
        setHighlightManager(hm);
        listeners = new Vector();
        layer.addLayerChangedListener(this);  
    }
    
    /**
     * A full constructor for a theme, requiering the provision of all parts except the new tipData .
     * @param l The layer containing the geographic features.
     * @param s The shader to use when colouring in the layer.
     * @param n A String describing or nameing this theme.
     * @param hm A HighlightManager, clicks in the viewer converted into highlight requests.
     * @param d The GeoData object to base link feature IDs in the layer with attribute values.
     * @param style A ShadeStyle object to pass to the layer.
     */
    public Theme(Layer l,Shader s,String n,HighlightManager hm,GeoData d,ShadeStyle style){
        this(l,s,n,hm,d,null,null);
    }
    
    /**
     * The old full constructor for a theme, requiering the provision of all parts except the style object
     * @param l The layer containing the geographic features.
     * @param s The shader to use when colouring in the layer.
     * @param n A String describing or nameing this theme.
     * @param hm A HighlightManager, clicks in the viewer converted into highlight requests.
     * @param d The GeoData object to base link feature IDs in the layer with attribute values.
     */
    public Theme(Layer l,Shader s,String n,HighlightManager hm,GeoData d){
        this(l,s,n,hm,d,null);
    }
    

    /**
     * The default constructor lacks too many of the requred paramiters to be of general use.
     */
    public Theme(){
        //shade = new MonoShader(Color.lightGray);
        listeners = new Vector();
    }

    /**
     * Constructs a very simple mono shaded (lightGray) theme from the given layer.
     * It will have no name,highlightManager or data and all features will be shaded light gray
     * @param l the Layer to base this theme on.
     */
    public Theme(Layer l){
        this(l,null,"Unknown",null,null);
    }
    
    /**
     * Constructs a simple theme from a layer and a shader.
     * As this theme has no GeoData object this constructor only makes sence
     * if the layer has its own built in data or if the shader is designed to give
     * sensible shading from the feature IDs alone.
     * @param l The layer to base this theme on.
     * @param s A shader for this theme.
     */
    public Theme(Layer l,Shader s){
        this(l,s,"Unknown",null,null);
    }
    
    /**
     * Constructs a simple named theme from a layer and a shader.
     * As this theme has no GeoData object this constructor only makes sence
     * if the layer has its own built in data or if the shader is designed to give
     * sensible shading from the feature IDs alone.
     * @param l The layer to base this theme on.
     * @param s A shader for this theme.
     * @param n A String that names this shader
     */
    public Theme(Layer l,Shader s,String n){
        this(l,s,n,null,null);
    }
    
    /**
     * Constructs a named theme from a layer and a shader.
     * Linked displays can be constructed if a theme in a different viewer
     * shares the same HighlightManager.
     * @param l The layer to base this theme on.
     * @param s A shader for this theme.
     * @param n A String that names this shader
     * @param hm The HighlightManager to comunicarte highlight requests through.
     */
    public Theme(Layer l,Shader s,String n,HighlightManager hm){
        this(l,s,n,hm,null);  
    }
    
   
    /**
     * Gets the current HighlightManager that can be used to set up linked views to this theme.
     */
    public HighlightManager getHighlightManager(){
        return highlight;
    }
    
    /**
     * Gets the current SelectionManager that can be used to set up linked views to this theme.
     */
    public SelectionManager getSelectionManager(){
        return this.selectionMgr;
    }

    /**
     * Sets the current shader for this theme
     * @param shader The Shader to use.
     */
    public void setShader(Shader s){
        if(shade!=null)shade.removeShaderChangedListener(this);
        shade=s;
        if(layer instanceof PolygonLayer){
            s.setKeyStyle(Shader.BOX);
            System.out.println("Set style as box");
        }
        if(layer instanceof LineLayer){
            s.setKeyStyle(Shader.LINE);
            System.out.println("Set style as line");
        }
        if(layer instanceof PointLayer){
            s.setKeyStyle(Shader.POINT);
            System.out.println("Set style as point");
        }
        shade.addShaderChangedListener(this);
        notifyThemeChangedListeners(ThemeChangedEvent.SHADE);
        
    }
    
    /**
     * Called when the properties of the current shader have changed
     * @param shader The Shader to use.
     */
    public void shaderChanged(ShaderChangedEvent e){
        if(DEBUG)System.out.println("T--->("+name+")The shader has been modified");
        notifyThemeChangedListeners(ThemeChangedEvent.SHADE);
    }
        
    
    /**
     * Get a key for this theme from its shader
     * @return Key the key object for this themes shader
     */
     public Key getKey(){
        return shade.getKey();
     }
    
    /**
     * Sets the ShadeStyle for this theme
     * @param style The ShadeStyle for this themes layer to use.
     */
    public void setStyle(ShadeStyle style){
        this.style = style;
    }
    
    /**
     * Gets the ShadeStyle currently in use by this theme
     * @return ShadeStyle the ShadeStyle object
     */
    public ShadeStyle getShadeStyle(){
        return this.style;
    }
    
    /**
     * Sets the HighlightShadeStyle for this theme
     * @param style The ShadeStyle for this themes layer to use.
     */
    public void setHighlightStyle(ShadeStyle style){
        this.highlightStyle = style;
    }
    
    /**
     * Gets the HighlightShadeStyle currently in use by this theme
     * @return ShadeStyle the HighlightShadeStyle object
     */
    public ShadeStyle getHighlightShadeStyle(){
        return this.highlightStyle;
    }    
    
        
    /**
     * Sets the SelectionShadeStyle for this theme
     * @param style The ShadeStyle for this themes layer to use.
     */
    public void setSelectionStyle(ShadeStyle style){
        this.selectionStyle = style;
    }

    /**
     * Gets the HighlightShadeStyle currently in use by this theme
     * @return ShadeStyle the HighlightShadeStyle object
     */
    public ShadeStyle getSelectionShadeStyle(){
        return this.selectionStyle;
    }        
    
    /**
     * Sets the highlightManager for this theme.  The theme will then take care of 
     * highlight comunication with its layer.
     * @param h A HighlightManager.
     */
    public void setHighlightManager(HighlightManager h){
        if(highlight!=null){
            highlight.removeHighlightChangedListener(this);
        }
        highlight = h;
        if(highlight !=null){
            highlight.addHighlightChangedListener(this);
        }
    }
   
    private boolean selectionLock = false;

    /**
      * 
      * @return value of selectionLock.
      */
     public boolean isSelectionLock() {
         return selectionLock;
     }
     
     /**
      * Set the value of selectionLock. If selectionLock is true, then the
      * current selections within this Theme are not allowed to change in 
      * response to selection events from the View. This
      * allows items to be selected from one Theme, then locked in their
      * selection state while items are selected from other Themes.
      *     
      * @param v  Value to assign to selectionLock.      */
     public void setSelectionLock(boolean  v) {
         this.selectionLock = v;
     }

    /**
     * Sets the selectionManager for this theme.  The theme will then take care of 
     * selection comunication with its layer.
     * @param s A SelectionManager.
     */
    public void setSelectionManager(SelectionManager s){
        if(selectionMgr!=null){
            selectionMgr.removeSelectionChangedListener(this);
        }
        selectionMgr = s;
        if(selectionMgr !=null){
            selectionMgr.addSelectionChangedListener(this);
        }
    }
    
    
    /**
     * Sets the GeoData for this theme, any viwers containing this theme will be 
     * notifed in order to repaint and display the new information.
     * @param d A GeoData containing the new data for this theme.
     */
    public void setGeoData(GeoData d){
        if(data!=d){
            data=d;
            notifyThemeChangedListeners(ThemeChangedEvent.GEOGRAPHY);
        }
        //???change this to .DATA
    }
    
    /**
     * Viewers have the ability to display short popup messages
     * in the form of tool tips when the users mouse rests over features
     * in the map.<p>
     * By setting the TipData for a theme it is posible to set what this string
     * should be for each feature.<br>
     * The GeoData supplied to this method sould assosiate feature ids found in this
     * theme with numeric or string values to display in the tool tips.<p>
     *
     * An example of use might be:<br>
     * <code>theme.setTipData(shapefileReader.readData("Names"));<br>
     * </code>
     * 
     * @author James Macgill
     * @see #getTipText
     * @param d A GeoData containing the new Tool Tip data for this theme.
     */
    public void setTipData(GeoData t){
        if(tipData!=t){
            tipData=t;
          //  notifyThemeChangedListeners(ThemeChangedEvent.GEOGRAPHY);
        }
        //???change this to .DATA
    } 
    
    /**
     * Some layers have the ability to display short labels for each feature.<p>
     * By setting the LabelData for a theme it is posible to set what this string
     * should be for each feature.<br>
     * The GeoData supplied to this method sould assosiate feature ids found in this
     * theme with numeric or string values to display in the labels.<p>
     *
     * An example of use might be:<br>
     * <code>theme.setLabelData(shapefileReader.readData("Names"));<br>
     * </code>
     * 
     * @author James Macgill
     * @param d A GeoData containing the new Label data for this theme.
     */
    public void setLabelData(GeoData labels){
        if(labelData!=labels){
            System.out.println("Label data set to "+labels);
            labelData = labels;
            this.notifyThemeChangedListeners(ThemeChangedEvent.DATA);
        }
    }
    
    /**
     * 
     * @author James Macgill
     * @return A GeoData containing the label data for this theme.
     */
     public GeoData getLabelData(){
        return labelData;
     }
    
     /**
     * Viewers have the ability to display short popup messages
     * in the form of tool tips when the users mouse rests over features
     * in the map.<p>
     * This method provides flexible tooltips at the expence of being a little less
     * straight forward than setTipData().<br>
     * To construct setup a tooltip this method needs a format string wich is a string
     * containing special characters that will be parsed, and an array of GeoData columns
     * to be put into the formatstring when the tooltip is shown<p>
     *
     * An example of use might be:<br>
     * <code>
     *      TooltipArray[0]=shapefileReader.readData("Country");
     *      TooltipArray[1]=shapefileReader.readData("Population");
     *      theme.setComplexTipData(TooltipArray, "%s: %s");<br>
     * </code>
     * This would produce tooltips of the form "Norway: 40000000" when run. Note that more than 
     * one element can be used, and static strings can be a part of the tooltip.
     *
     * 
     * @author Kjetil Thuen
     * @see #setTipData
     * @see #getTipText
     * @param t An array of GeoDatas containing the new Tool Tip data for this theme.
     * @param ts A formatstring. 
     */
    public void setComplexTipData(GeoData t[], String ts) {
      if (complexTipData != t) {
         complexTipData = t;
      }
      tipformat = ts;
    }
    
    
    /**
     * gets the current geoData.
     */
    public GeoData getGeoData(){
        return data;
    }
    
    /**
     * Filters can be used to prevent a theme from showing all of the features in the layer it contains.
     * Once a filter is in place, the id of each feature to be ploted is pased to the filter and the feature is only
     * drawn if the filter OKs it.
     * @author James Macgill
     * @param filter An implementation of the filter interface that will be used.
     */
     public void setFilter(Filter filter){
        if(this.filter!=null){
            this.filter.removeFilterChangedListener(this);
        }
        this.filter = filter;
        this.filter.addFilterChangedListener(this);
     }
     
     /**
      * Returns the current filter in use by this theme.
      * @return Filter the current filter, if none has been set then this will be NULL
      */
     public Filter getFilter(){
        return filter;
     }
    
    /**
     * When this method is called by a viewer it will carry out all actions needed to
     * paint this theme into the graphics provided.
     * @param g A Graphics context to paint into.
     * @param scale the Scaler to use when displaying this theme
     */
    protected void paintScaled(Graphics g,Scaler scale){
//        if(layer.getStatus()!=layer.COMPLETED){return;}
        //System.out.println(name+" Constructing geographics with "+labelData);
        GeoGraphics gg = new GeoGraphics(g,scale,shade,data,labelData,style,filter,1);
        layer.paintScaled(gg);
    }
    
    /**
     * gets the geographic bounds from this themes layer
     * @return GeoRectangle the bounds for this theme (from its layer).
     */
    public GeoRectangle getBounds(){
        return layer.getBounds();
    }
 
    /**
     * Finds the id of the feature in this themes layer assoiated with the given point.
     * and then passes that ID onto this themes HighlightManager.
     * @param p The GeoPoint to set the highlight to.
     */
    public void setHighlight(GeoPoint p){
        if(highlight != null){
            highlight.setHighlight(layer.getID(p));
        }
    }
    
    //The ID of the highlight in the highlight manager has changed

    /**
     * Called to notify this theme that the highlight has changed.
     * Theme will handle notifing all viwers that contain it that
     * its highlight needs repainting.<br>
     * Required by highlightChangedListener interface and should only
     * be called by a highlighManager.
     * @param hce a HighlightChangedEvent.
     */
    public void highlightChanged(HighlightChangedEvent hce){
        //notify viewers
        notifyThemeChangedListeners(ThemeChangedEvent.HIGHLIGHT);
    }
    
    /**
     * Called to notify this theme that the selection has changed.
     * Theme will handle notifing all viwers that contain it that
     * its selectino needs repainting.<br>
     * Required by selectionChangedListener interface and should only
     * be called by a selectionManager.
     * @param sce a SelectionChangedEvent.
     */
    public void selectionChanged(SelectionChangedEvent sce){
        //notify viewers
        notifyThemeChangedListeners(ThemeChangedEvent.SELECTION);
    }
    
    
    /**
     * Used by the themes layer to pass informaion about a change in state
     * @param lce A LayerChangedEvent.
     */
    public void layerChanged(LayerChangedEvent lce){
				if(DEBUG)System.out.println("theme changed "+lce.getReason());
                                if(lce.getReason()==lce.DATA){
                                    shade.setRange(this.data);
                                }
        notifyThemeChangedListeners(lce.getReason());
    }
    
    /**
     * Used by the themes filter to pass information about a change in state
     * @param fce A FilterChangedEvent
     */
     public void filterChanged(FilterChangedEvent fce){
        notifyThemeChangedListeners(fce.getReason());
     }

    /**
     * Used to request a change in highligt to the feature that contains
     * the given point.
     *
     * Generaly only used by viewers that contain this theme.
     * @param hpce A HighlightPositionChangedEvent
     */
    public void highlightPositionChanged(HighlightPositionChangedEvent hpce){
        if(highlight != null){
        if(!hpce.isValid()){
            highlight.setHighlight(-1);
            return;
        }

            if(hpce.getSource() instanceof Viewer){
                Viewer v = (Viewer)hpce.getSource();
                if(!v.isThemeVisible(this)){
                    return;
                }
                if(layer instanceof LockableSize){
                    highlight.setHighlight(((LockableSize)layer).getID(hpce.getPosition(),v.getScale()));
                    return;
                }
            }
            highlight.setHighlight(layer.getID(hpce.getPosition()));
        }
    }

    /**
     * Used to request a change selection region to the features
     * contained by the given rectangle.
     *
     * Generaly only used by viewers that contain this theme.
     * @param hpce A SelectionRegionChangedEvent
     */
    public void selectionRegionChanged(SelectionRegionChangedEvent srce){
        if(selectionMgr != null && ! isSelectionLock()){
            selectionMgr.setSelection(layer.getIDs(srce.getRegion(),selectionMgr.CONTAINS));
        }
    }

   /**
     * Used to request to togle the selected state of the feature at this
     * location to the features
     * contained by the given rectangle.
     *
     * Generaly only used by viewers that contain this theme.
     * @param hpce A SelectionRegionChangedEvent
     */
    public void selectionPositionChanged(SelectionPositionChangedEvent spce){
        //System.out.println("T--->("+name+")Received selection position changed event");
	  if(selectionMgr != null && ! isSelectionLock()){
            selectionMgr.toggleSelection(layer.getID(spce.getLocation()));
//			int[] id = {layer.getID(spce.getLocation())};
//			selectionMgr.setSelection(id);

		}
    }

    /**
      * Gets the bounding rectangle that will fit round all of the currently selected featrues
      * @since 0.6.5
      * @return GeoRectangle that defines the bounds of the selection
      */
      public GeoRectangle getSelectionMapExtent(){
        GeoRectangle sme = new GeoRectangle();
        if(selectionMgr == null){return sme;}
        int[] selected = selectionMgr.getSelection();
       // System.out.println("T--->("+name+")Getting map extent for "+selected+" "+selected[0]);
        return layer.getBoundsOf(selected);
      }
        
        
        
	

    /**
     * A convineince method to set the highlight for this theme.
     * A call could be made to the themes HighlightManager directly if it is
     * available, if not this method will do that for you.<br>
     * Any themes that share this themes HighlightManager will have their highlights 
     * changed as well.
     * @param id The int representing the id of the feature to highlight.
     */
    public void setHighlight(int id){
        if(highlight != null){
            highlight.setHighlight(id);
        }
    }
    
    public void setName(String n){
        name = n;
    }
    
    public String getName(){
        return name;
    }
    
    /**
     * Fetches the id of the feature found in the layer at the given point.
     * @param p A GeoPoint for the location to test.
     * @return int The id found at that point.
     */
    public int getID(GeoPoint p){
       /* if(layer instanceof LockableSize){
            return layer.getID(p,v.getScale());
        }*/
        return layer.getID(p);
    }
    
    /**
     * looks up a short string relating to the feature specified by the
     * provided ID.<br>
     * Viewer is able to take this string and display it over the feature in the form
     * of a popup tool tip (hence the name).<br>
     * The data for the tip text is set by calling setTipData.<p>
     *
     * JM 08/May/2000 updated documentation.
     *
     * @see #setTipData
     * @see #setComplexTipData
     * @author James Macgill
     * @param id An int containing the id of the feature to fetch the tool tip text for.
     * @return String A short text description for the chosen feature. Returns null if no tipData is set or no feature matches the specifed ID.
     **/
    public String getTipText(int id){
        if (complexTipData != null && tipformat != null) {
            int arraycount=0;
            int max=complexTipData.length;
            char[]format = tipformat.toCharArray();
            String tooltip = new String("");
            String tmp = new String(" ");

            for (int i=0; i<format.length; i++) {
               if (format[i]=='%') {
                  i++;
                  if (i<format.length) {
                     switch (format[i]){
                        case 's':
                           if (arraycount<max) {
                              tmp = complexTipData[arraycount].getText(id);       
                              arraycount++;
                           }
                           break;
                        case 'S':
                           if (arraycount<max) {
                              tmp = complexTipData[arraycount].getText(id).toUpperCase();       
                              arraycount++;
                           }
                           break;
                        case '%':
                           tmp = "%";
                           arraycount++;
                           break;
                        // Other format characters could be defined here.
                     }
                     if (!tmp.equals(" ")) {
                        tooltip =tooltip.concat(tmp.trim());
                        tmp=" ";
                     }
                     else return null;
                  }
               }
               else tooltip = tooltip.concat(String.valueOf(format,i,1));
            }
            return tooltip;
        }
        else if(tipData!=null){
            return tipData.getText(id);
        }
        
        return null;
    }
    
    /**
     * looks up a short string relating to the first feature found to contain the specified point.<br>
     * Viewer is able to take this string and display it over the feature in the form
     * of a popup tool tip (hence the name).<br>
     * The data for the tip text is set by calling setTipData.<p>
     *
     * JM 08/May/2000 updated documentation.
     *
     * @see #setTipData
     * @author James Macgill
     * @param p A GeoPoint specifing the location of interest to fetch the tool tip text for.
     * @return String A short text description for the chosen feature. Returns null if no tipData is set or no feature contains the specified point.
     **/
    public String getTipText(GeoPoint p,Scaler scale){
        if(layer instanceof LockableSize){
            return getTipText(((LockableSize)layer).getID(p,scale));
        }
        return getTipText(getID(p));
    }
        
    /**
     * A cut down version of paintScaled that only paints the
     * highlight.
     * Called by viewer when needed.
     * @param g A graphics context to paint into
     * @param scale The Scaler to use.
     */
    protected void paintHighlight(Graphics g,Scaler scale){
         if(layer.getStatus()!=layer.COMPLETED){return;}
        if(highlight!=null){
            layer.paintHighlight(g,scale,highlight.getHighlight(),highlightStyle);
        }
    }
    
    /**
     * A cut down version of paintScaled that only paints the
     * current selection.
     * Called by viewer when needed.
     * @param g A graphics context to paint into
     * @param scale The Scaler to use.
     */
    protected void paintSelection(Graphics g,Scaler scale){
         if(layer.getStatus()!=layer.COMPLETED){return;}
        //System.out.println("T--->("+name+")Do we have a selection manager?");
        if(selectionMgr!=null){
           // System.out.println("T--->("+name+")We have a selection manager!");
            layer.paintSelection(g,scale,selectionMgr.getSelection(),selectionStyle);
        }
    }
    
    /**
     * request to be notified of theme changes.
     * Listeners will be notified if any major aspect of this theme
     * changes, the resuting ThemeChangedEvent contains a reson code
     * to describe the nature of the change.<br>
     * Used by viewers to keep uptodate with their themes.
     * @param tcl a ThemeChangedListener to add
     */
    public void addThemeChangedListener(ThemeChangedListener tcl){
        listeners.addElement(tcl);
    }
    
    /**
     * request to end being notified of theme changes.
     * @param tcl the ThemeChangedListener to remove.
     */
    public void removeThemeChangedListener(ThemeChangedListener tcl){
        listeners.removeElement(tcl);
    }
    
    /**
     * Notify all theme change listeners of a change in this theme.
     * @param reason an int reason code describing what has changed in this theme; take from ThemeChangedEvent.DATA/GEOGRAPHY
     */
    protected void notifyThemeChangedListeners(int reason){
        Vector l;
	    ThemeChangedEvent tce = new ThemeChangedEvent(this,reason);
	    synchronized(this) {l = (Vector)listeners.clone(); }
	    
	    for (int i = 0; i < l.size();i++) {
//            System.out.println("notifying "+(ThemeChangedListener)l.elementAt(i)+" about theme change ("+this.getName()+")");
	        ((ThemeChangedListener)l.elementAt(i)).themeChanged(tce);
	    }
	}
	
	public Layer getLayer(){
	    return layer;
	}
        //ian's gaz widget seems to need this
        public void setLayer(Layer l){
						layer.removeLayerChangedListener(this);
            layer = l;
						layer.addLayerChangedListener(this);  
            this.notifyThemeChangedListeners(ThemeChangedEvent.GEOGRAPHY);
        }

	public String toString() {
		return "uk.ac.leeds.ccg.geotools.Theme ("+name+")";
	}
        

}

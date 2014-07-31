package uk.ac.leeds.ccg.geotools;

import java.util.Hashtable;

/**
 * A simple label that can display items from a GeoData object.
 * The class implements Highlight changed listener so that it can be
 * attached to a highlight manager, in this way it will automaticaly display
 * information relating to the currently highlighted item.
 */
public class GeoLabel extends java.awt.Label implements uk.ac.leeds.ccg.geotools.HighlightChangedListener
{
    GeoData data;
    Hashtable table = new Hashtable();
    /**
     * A default constructor, not much use unless you sunsequently call
     * setGeoData afterwards.
     */
    public GeoLabel(){
        data = new SimpleGeoData();
    }
    
    /**
     * Constructs the GeoLabel and sets the data source to be the given GeoData
     * @param d A GeoData object containg the infomration for this label to display.
     */
    public GeoLabel(GeoData d){
        data = d;
    }
    
    /**
     * Sets the GeoData object to be used by this label
     * @param d The GeoData to use.
     * @deprecated
     */
    public void setGeoData(GeoData d){
        data = d;
    }
    
    /**
     * allows the GeoLabel to listen to multiple highlight managers
     */
    public void addHighlightManager(HighlightManager hm,GeoData d){
	table.put(hm,d);
	hm.addHighlightChangedListener(this);
    }
	

    /**
     * Gets the GeoData object currently used by this label
     */
    public GeoData getGeoData(){
        return data;
    }
    
    /**
     * Updates the label to reflect the information for the
     * newly highlighted feature
     * @param hce The HighlightChangedEvent
     */
    public void highlightChanged(HighlightChangedEvent hce)
    {
	  //System.out.println("Looking up a GD for "+hce.getSource());	
        GeoData d = (GeoData)table.get((HighlightManager)hce.getSource());
	  if(d!=null){
		//System.out.println("Found a geodata for that hm");
		setText(d.getText(hce.getHighlighted()).trim());
		invalidate();
		return;
        }
	  if(data!=null){
            setText(data.getText(hce.getHighlighted()));
            invalidate();
        }
    }
}

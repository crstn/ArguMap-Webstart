package uk.ac.leeds.ccg.geotools;

import java.util.Hashtable;

import uk.ac.leeds.ccg.geotools.misc.FormatedString;

/**
 * A simple label that can display items from a GeoData object.
 * The class implements Highlight changed listener so that it can be
 * attached to a highlight manager, in this way it will automaticaly display
 * information relating to the currently highlighted item.
 *
 * To use it set up a GeoData object with the data to be displayed and make and attach a HighlightManager to a Theme.<br>
 * Then call addHighlightManager(HighlightManager hm,GeoData data);
 */
public class GeoFormLabel extends java.awt.Label implements uk.ac.leeds.ccg.geotools.HighlightChangedListener
{
    GeoData data;//no longer used except by depricated methods.
    
    int dp=2; // number of decimal places to display
    
    //Hashtable to hold the HighlightManager - GeoData pairs
    Hashtable table = new Hashtable();
    
    /**
     * A default constructor, not much use unless you sunsequently call
     * addHighlightManager(HighlightManager hm,GeoData d);
     */
    public GeoFormLabel(){
        data = new SimpleGeoData();
    }
    
    /**
     * Constructs a GeoFormLabel with an inital HighlightManager and GeoData pair.<br>
     * Additional HighlightManagers can be added with calls to addHighlightManager(HighlightManager hm,GeoData data);
     *
     * @param hm A highlightmanager to listen to.
     * @param data A GeoData object to associate with the specified HighlightManager.
     **/
    public GeoFormLabel(HighlightManager hm,GeoData data){
        data = new SimpleGeoData();  //will be removed once depricated methods are removed.
        addHighlightManager(hm,data);
    }
    
    /**
     * Constructs the GeoLabel and sets the data source to be the given GeoData
     * @param d A GeoData object containg the infomration for this label to display.
     * @deprecated Eather construct as empty and then add with addHighlightManager or use full constructor
     */
    public GeoFormLabel(GeoData d){
        data = d;
    }
		public void setData(GeoData d){
			data=d;
		}
    
    /**
     * Sets the GeoData object to be used by default in this label
     * @param d The GeoData to use.
     * @deprecated Use addHighlightManager(HighlightManger, GeoData d) instead.
     */
    public void setGeoData(GeoData d){
        data = d;
    }
    
    /**
     * Adds the label as a listener to the specifed HighlightManager and associates a GeoData
     * object with it.<br>
     * 
     * 
     *
     * Whenever a highlightChanged event is generated by the highlight manager the contents of the label will
     * change to represent the data for the highlighted feature stored in the provied geodata.
     *
     * @param hm A HighlightManager to listen to.
     * @param d A GeoData object to associate with the HighlightManager.
     */
    public void addHighlightManager(HighlightManager hm,GeoData d){
	table.put(hm,d);
	hm.addHighlightChangedListener(this);
    }
	

    /**
     * Gets the GeoData object currently used by this label
     * @depricated
     */
    public GeoData getGeoData(){
        return data;
    }
    
    /**
     * Called as a result of a highlight change event in any of the highlight managers that
     * this label is attached as a listener to.
     *
     * Updates the label to reflect the information for the
     * newly highlighted feature<br>
     *
     * Required by HighlightChangedListener interface.
     *
     * @param hce The HighlightChangedEvent
     */
    public void highlightChanged(HighlightChangedEvent hce)
    {
	//GeoFormLabels can listen to more than one highlight manager at once so find
        //out wich one generated the event and get the GeoData associated with it.
        GeoData d = (GeoData)table.get((HighlightManager)hce.getSource());
	
        if(d!=null){  //we have some data for that highlight manager
		//get the text for the newly highlighted feature
		String s=d.getText(hce.getHighlighted());
                //set the text in the label, formating to required decimal places (only if string represents a number
		setText(FormatedString.format(s,dp));
                invalidate();//update the lable.
		return;
        }
        //no data specificaly for that highlight manager, so use default
        //this can only happen if the depricated setGeoData method has been used insteaad of the new
        //addHighlightManager method.
	  if(data!=null){
		String s=data.getText(hce.getHighlighted());
		int i = s.lastIndexOf('.');
		if(i!=-1&&dp>0){
			setText(FormatedString.format(""+s,dp));
		}else{
			setText(s);
		}
		//setText(data.getText(hce.getHighlighted()));
		invalidate();
        }
    }
    
    /**
     * When the label is displaying numbers then this controls the number of decimal places used
     *
     * @param dp An int representing the number of decimal places to use.
     **/
    public void setDP(int dp){
			this.dp=dp;
    }
}

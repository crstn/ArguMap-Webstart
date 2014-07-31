package uk.ac.leeds.ccg.geotools;

import java.util.Vector;

/**
 * Comunicates between GeoTools components so that multiple views can share and
 * display the same highlight information.<br>
 * By assigning the same HighlightManager to multiple GeoTools components,
 * they will automaticaly produce a form of linked display, such that
 * highlight selections in one view will show up instanly in all the others.
 * <p>
 * Each feature in GeoTools should have an id value associated with it, this is
 * the case for GeoPolygons and GeoCircles for example.<br>
 * The HighlightManager keeps track of one id value.  Themes that contain a HighlightManager
 * will thus instruct their layers to display any features that match that id to be drawn in
 * a distinctive manner.
 *
 * N.B. HighlightManagers are only capable of highlighting one id at a time.<br>
 * SelectionManagers are used to handle multiple id groups.
 * @see SelectionManager
 * 
 */

public class HighlightManager extends java.awt.Component
{
    /**
     * Listeners for changes in highlight id
     */
    Vector listeners = new Vector();
    
    /**
     * The id to be highlighted
     */
    protected int highlight;

    /**
     * The default constuctor.
     **/
	public HighlightManager()
	{
	}

    /**
     * Set the current highlight id.<p>
     * All HighlightChangedListeners will be notified of this change.<br>
     * Notably, themes which use this manager will be informed, and the 
     * feature highlights updates to reflect this.
     *
     * @param id The new id of features to be highlighted.
     **/
	public void setHighlight(int id)
	{
	        if(highlight!=id){
			    highlight = id;
			    notifyHighlightChanged();
			}
			//System.out.println("Highlight Changed to "+id);
			
	}

    /**
     * Returns the current id that is being highlighted by this manager
     * @return An int containg the current id.
     **/
	public int getHighlight()
	{
		return highlight;
	}
	
	/**
	 * Register a HighlightChangedListener with this manager.<p>
	 * It will be notified when ever the id to highlight changes.
	 * @param hce The HighlightChangedListener to register.
	 **/
	public synchronized void
	addHighlightChangedListener(HighlightChangedListener hce) {
	    listeners.addElement(hce);
	}
	
	/**
	 * To remove a HighlightChangedListener from this manager.<p>
	 * It will no longer be notified about changes in highlight id
	 * @param hce the HighlightChangeListener to remove
	 */
	public synchronized void 
	removeHighlightChangedListener(HighlightChangedListener hce) {
	    listeners.removeElement(hce);
	}
	
	/**
	 * Calls highlightChanged on all listeners
	 **/
	protected void notifyHighlightChanged() {
	    Vector l;
	    HighlightChangedEvent hce = new HighlightChangedEvent(this,highlight);
	    synchronized(this) {l = (Vector)listeners.clone(); }
	    
	    for (int i = 0; i < l.size();i++) {
	        ((HighlightChangedListener)l.elementAt(i)).highlightChanged(hce);
	    }
	}

}


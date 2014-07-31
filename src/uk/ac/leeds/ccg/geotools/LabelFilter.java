/*
 * LabelFilter.java
 *
 * Created on 27 June 2001, 12:10
 */

package uk.ac.leeds.ccg.geotools;

/**
 *
 * @author  James Macgill
 * @version 
 */
public class LabelFilter extends uk.ac.leeds.ccg.geotools.SimpleFilter implements uk.ac.leeds.ccg.geotools.Filter {

    /** Holds value of property data. */
    private uk.ac.leeds.ccg.geotools.GeoData data;
    
    /** Utility field used by bound properties. */
    private java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    
    /** Holds value of property match. */
    private String match;
    
    /** Creates new LabelFilter */
    public LabelFilter() {
    }

    /**
     * Check the given id against the filter,
     * @return boolean returns as true if features with this id should be included in any displays
     */
    public boolean isVisible(int id) {
        return data.getText(id).equalsIgnoreCase(match);
    }
    
    /** Add a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }
    
    /** Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
    
    /** Getter for property data.
     * @return Value of property data.
     */
    public uk.ac.leeds.ccg.geotools.GeoData getData() {
        return data;
    }
    
    /** Setter for property data.
     * @param data New value of property data.
     */
    public void setData(uk.ac.leeds.ccg.geotools.GeoData data) {
        uk.ac.leeds.ccg.geotools.GeoData oldData = this.data;
        this.data = data;
        propertyChangeSupport.firePropertyChange("data", oldData, data);
    }
    
    /** Getter for property match.
     * @return Value of property match.
     */
    public String getMatch() {
        return match;
    }
    
    /** Setter for property match.
     * @param match New value of property match.
     */
    public void setMatch(String match) {
        String oldMatch = this.match;
        this.match = match;
        propertyChangeSupport.firePropertyChange("match", oldMatch, match);
    }
    
    public String toString(){
        return ""+data.getName()+" matches "+match;
    }
    
    public Object clone(){
        LabelFilter lf = new LabelFilter();
        lf.setData(data);
        lf.setMatch(match);
        return lf;
    }
}

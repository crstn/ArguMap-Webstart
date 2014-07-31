/*
 * ClockFlilter.java
 *
 * Created on 26 June 2001, 16:45
 */

package uk.ac.leeds.ccg.geotools;

/**
 *
 * @author  James Macgill
 * @version 
 */
public class ClockFilter extends uk.ac.leeds.ccg.geotools.SimpleFilter implements Cloneable{
    
    boolean isSimple = false;
    
    public uk.ac.leeds.ccg.geotools.GeoData data;
    
    /** Utility field used by bound properties. */
    private java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    
    /** Holds value of property cycleStart. */
    private double cycleStart;
    
    /** Holds value of property cycleLength. */
    private double cycleLength;
    
    /** Holds value of property startPoint. */
    private double startPoint;
    
    /** Holds value of property span. */
    private double span;
    
    public ClockFilter(){}
    
    /** Creates new ClockFlilter */
    public ClockFilter(GeoData d) {
        data = d;
        setCycle(d.getMin(),d.getMax()-d.getMin()+1);
        setActiveSlice(cycleStart,cycleLength);
       
    }

    public String getHeader(){
        return "CF"+data.getName()+"startPoint,CF"+data.getName()+"span";
    }
    
    public String getAsRow(){
        return ""+startPoint+","+span;
    }
    
    public void setCycle(double start,double length){
        setCycleStart(start);
        setCycleLength(length);
    }
    
    public void setActiveSlice(double start,double span){
        setStartPoint(start);
        setSpan(span);
    }
    
    
    private void updateTestType(){
        if((startPoint+span)<(cycleStart+cycleLength)){
            //does not cross cycle limit
            isSimple=true;
        }
        else{
            isSimple=false;
        }
    }
        
    /**
     * Check the given id against the filter,
     * @return boolean returns as true if features with this id should be included in any displays
     */
    public boolean isVisible(int id) {
        double value = data.getValue(id);
        return isVisible(value);
    }
    
    public boolean isVisible(double value){
        if(isSimple){
            return value>=startPoint && value<startPoint+span;
        }
        else{
            return value>=startPoint || value<  ((startPoint+span)%(cycleStart+cycleLength)+cycleStart);
        }
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
        setCycle(data.getMin(),data.getMax()-data.getMin()+1);
        setActiveSlice(cycleStart,cycleLength);
        propertyChangeSupport.firePropertyChange("data", oldData, data);
    }
    
    /** Getter for property cycleStart.
     * @return Value of property cycleStart.
     */
    public double getCycleStart() {
        return cycleStart;
    }
    
    /** Setter for property cycleStart.
     * @param cycleStart New value of property cycleStart.
     */
    public void setCycleStart(double cycleStart) {
        double oldCycleStart = this.cycleStart;
        this.cycleStart = cycleStart;
        updateTestType();
        super.notifyFilterChangedListeners(FilterChangedEvent.DATA);
        propertyChangeSupport.firePropertyChange("cycleStart", new Double(oldCycleStart), new Double(cycleStart));
    }
    
    /** Getter for property cycleLength.
     * @return Value of property cycleLength.
     */
    public double getCycleLength() {
        return cycleLength;
    }
    
    /** Setter for property cycleLength.
     * @param cycleLength New value of property cycleLength.
     */
    public void setCycleLength(double cycleLength) {
        double oldCycleLength = this.cycleLength;
        this.cycleLength = cycleLength;
        updateTestType();
        super.notifyFilterChangedListeners(FilterChangedEvent.DATA);
        propertyChangeSupport.firePropertyChange("cycleLength", new Double(oldCycleLength), new Double(cycleLength));
    }
    
    /** Getter for property startPoint.
     * @return Value of property startPoint.
     */
    public double getStartPoint() {
        return startPoint;
    }
    
    /** Setter for property startPoint.
     * @param startPoint New value of property startPoint.
     */
    public void setStartPoint(double startPoint) {
        double oldStartPoint = this.startPoint;
        if(startPoint<cycleStart)startPoint=cycleStart;
        this.startPoint = startPoint;
        updateTestType();
        super.notifyFilterChangedListeners(FilterChangedEvent.DATA);
        propertyChangeSupport.firePropertyChange("startPoint", new Double(oldStartPoint), new Double(startPoint));
    }
    
    /** Getter for property span.
     * @return Value of property span.
     */
    public double getSpan() {
        return span;
    }
    
    /** Setter for property span.
     * @param span New value of property span.
     */
    public void setSpan(double span) {
        double oldSpan = this.span;
        if(span>cycleLength)span=cycleLength;
        this.span = span;
        updateTestType();
        super.notifyFilterChangedListeners(FilterChangedEvent.DATA);
        propertyChangeSupport.firePropertyChange("span", new Double(oldSpan), new Double(span));
    }
    
    public String toString(){
        return "Clock Filter "+data.getName()+"From "+startPoint+" for "+span+ " to "+((startPoint+span)%(cycleStart+cycleLength)+cycleStart);
    }
    
    public Object clone(){
        ClockFilter clone = new ClockFilter(data);
        clone.setCycle(getCycleStart(),getCycleLength());
        clone.setActiveSlice(getStartPoint(),getSpan());
        return clone;
    }
    
    
    
}

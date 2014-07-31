package uk.ac.leeds.ccg.geotools;

public class ScaleChangedEvent extends java.util.EventObject
{
    double scaleFactor;
    
    //Constructs a new ScaleChangedEvent
    //ScaleChangedEvent(java.awt.Component source,double factor){
    public ScaleChangedEvent(Scaler source,double factor){
        super(source);
        scaleFactor = factor;
    }
    
    //access method for scale factor
    public double getScaleFactor() {
        return scaleFactor;
    }   
}

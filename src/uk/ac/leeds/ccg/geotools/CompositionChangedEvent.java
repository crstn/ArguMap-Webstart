package uk.ac.leeds.ccg.geotools;


public class CompositionChangedEvent extends java.util.EventObject
{
    public static final int ADDED=1,REMOVED=2,VISIBILITY=4,ORDER=8;
	private int reason;
    
    public CompositionChangedEvent(java.awt.Component source, int r){
        super(source);
        reason =r;
    }
    
    public int getReason(){
        return reason;
    }
}
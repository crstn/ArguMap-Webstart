package uk.ac.leeds.ccg.geotools;

public class LayerChangedEvent extends java.util.EventObject
{
    private int reason;
    public final static int GEOGRAPHY=1,DATA=2,ANIMATION=16;

    public LayerChangedEvent(Object source, int why){
        super(source);

        reason = why;

    }

    public int getReason(){
        return reason;
    }
    
    
    public boolean testReason(int test){
        return (test&reason)>0;
    }
    
}

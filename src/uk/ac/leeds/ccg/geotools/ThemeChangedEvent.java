package uk.ac.leeds.ccg.geotools;

public class ThemeChangedEvent extends java.util.EventObject
{
    private int reason;
    public final static int GEOGRAPHY=1,DATA=2,SHADE=4,HIGHLIGHT=8,ANIMATION=16,SELECTION=32;

    public ThemeChangedEvent(Object source, int why){
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
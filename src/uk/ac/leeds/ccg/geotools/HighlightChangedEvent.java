package uk.ac.leeds.ccg.geotools;

public class HighlightChangedEvent extends java.util.EventObject
{
    private int highlighted;
    private int count;
    
    public HighlightChangedEvent(java.awt.Component source, int id){
        super(source);
       
        highlighted = id;
        
    }
    
    public int getHighlighted(){
        return highlighted;
    }
    
 
        
}
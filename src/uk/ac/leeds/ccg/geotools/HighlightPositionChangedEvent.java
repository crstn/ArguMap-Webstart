package uk.ac.leeds.ccg.geotools;


/**
 * An event used to notify listeners that the user has selected a new position to
 * highlight.
 */
public class HighlightPositionChangedEvent extends java.util.EventObject
{
    private GeoPoint position;
    private int count;
    private final boolean isValid;

    public HighlightPositionChangedEvent(java.awt.Component source, GeoPoint p){
        super(source);
        isValid = (p!=null);
        position = p;

    }

    public GeoPoint getPosition(){
        return position;
    }

    
    public boolean isValid(){
        return isValid;
    }
    int selected;

}


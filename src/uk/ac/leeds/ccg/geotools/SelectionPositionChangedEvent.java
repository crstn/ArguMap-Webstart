package uk.ac.leeds.ccg.geotools;


/**
 * An event used to notify listeners that the user has selected a new position to
 * highlight.
 */
public class SelectionPositionChangedEvent extends java.util.EventObject
{
    private GeoPoint location;
    

    public SelectionPositionChangedEvent(java.awt.Component source, GeoPoint p){
        super(source);

        location = p;

    }

    public GeoPoint getLocation(){
        return location;
    }

   
}
package uk.ac.leeds.ccg.geotools;


/**
 * An event used to notify listeners that the user has selected a new position to
 * highlight.
 */
public class SelectionRegionChangedEvent extends java.util.EventObject
{
    private GeoRectangle region;
    

    public SelectionRegionChangedEvent(java.awt.Component source, GeoRectangle r){
        super(source);

        region = r;

    }

    public GeoRectangle getRegion(){
        return region;
    }

   
}
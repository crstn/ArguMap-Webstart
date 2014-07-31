package uk.ac.leeds.ccg.geotools;

/**
 * ViewerClickedEvent, thrown when a user clicks on a Viewer.
 * @since 0.6.2
 * @author James Macgill
 */
public class ViewerClickedEvent extends java.util.EventObject
{
    private GeoPoint point;

    /**
     * Thrown whenever a user clicks in a Viewer window.
     * 
     * @param source The viewer that generated this event.
     * @param p A GeoPoint for the real world location coresponding to the point that has just been clicked on.
     */
    public ViewerClickedEvent(Object source,GeoPoint p){
        super(source);
        point = p;
    }
    /**
     * Gets the realworld coordinates of the point clicked on to generate this event.
     * 
     * @return A GeoPoint for the source coordinate.
     */
    
    public GeoPoint getLocation(){
        return point;
    }
}
    

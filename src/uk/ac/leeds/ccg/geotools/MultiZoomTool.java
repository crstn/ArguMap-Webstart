/**
 * Provides the zoom funtionality for viewers.
 * ZoomTool is one of the basic set of tools that can be pluged into viewer and provides
 * the key functionality of zooming.<br>
 * This MultiZoomTool can work on the click and by this centering the click point and zooming into it. 
 * Also it allows to draw a boundary box and zoom in it with a presetted zoom factor.
 * Zoom Factor is a number which is used as a scale.
 * Zoom factor which is more than 1 will zoom in, less than 1 will zoom out. For example z.f = 2 
 * will magnify an image twice.  
 * @author James Macgill, Alexandre Djioev
 */


package uk.ac.leeds.ccg.geotools;
import java.awt.Cursor;

public class MultiZoomTool extends SimpleTool{

    public static boolean DEBUG=false;


    // flags which describes tool behavior
    boolean dragIsOn=true;
    boolean clickIsOn=true;

    // this flag is used to determine if the map was draged.
    boolean wasDraged=false;    	

    // zoom factor	
    double zoomFactor;


    /**
     *  default constractor, zoom Factor = 2, drag and click are true.
     **/
    public MultiZoomTool () {
        zoomFactor=2.0;
        dragIsOn=true;
        clickIsOn=true;
    }



    /**
     *  constractor, lets set up zoom Factor, drag and click are true.
     **/   	
    public MultiZoomTool (double zoomFactor) {
        this.zoomFactor=zoomFactor;
        dragIsOn=true;
        clickIsOn=true;
    }


    /**
     *  constractor, lets set up zoom Factor, drag and click.
     **/ 
    public MultiZoomTool (double zoomFactor, boolean allowDrag, boolean allowClick) {
        this.zoomFactor=zoomFactor;
        dragIsOn = allowDrag;
        clickIsOn = allowClick;
    }

    public void setAllowDrag(boolean flag) {
        this.dragIsOn = flag;	
    }

    public void setAllowClick(boolean flag) {
        this.clickIsOn = flag;	
    }

    /**
     * Returns the default cursor type for this tool.
     * In this instance, a CROSSHAIR.
     * @author James Macgill, Alexandre Djioev
     * @return Cursor the default cursor for this tool
     */     
    public Cursor getCursor(){
        return  new Cursor(Cursor.CROSSHAIR_CURSOR);
    }


    /**
     * Called by update in SimpleTool when an update request has been fired as a result of a drag event.<p>
     **/
    public void drag() {
        super.drag();
        wasDraged=true;		
    }

    /**
     * Called by update in SimpleTool when an update request has been fired as a result of a drag event.<p>
     * First, this method creates a new extent with a click point as a center.
     * Next, it zooms with setted zoomFactor.
     **/

    public void click() {
        if (clickIsOn) {

            double xc = mouse.getMapPoint().x;  // future x and y center coordinates (mouse click)
            double yc = mouse.getMapPoint().y;
            GeoRectangle b1 = context.scale.getProjectedMapExtent();		  

            double xnew = xc - (b1.width*zoomFactor)/2;
            double ynew = yc - (b1.height*zoomFactor)/2;
            GeoRectangle b2 = new GeoRectangle(xnew, ynew, b1.width*zoomFactor, b1.height*zoomFactor);  //new extent

            context.scale.setProjectedMapExtent(b2);

        }// if
    }


    /**
     * Set method to set up a Zoom Factor.
     **/
    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor=zoomFactor;    	
    }

    /**
     * Returns zoom factor
     **/     	
    public double getZoomFactor() {
        return this.zoomFactor;    	
    }

    /**
     * called when a mouse button has been released.
     * If a valid drag has been made then this method sets the new map extent for the
     * viewer that this tool is attached to.
     *
     * @author James Macgill
     * @since 0.7.7.2 07 June 2000
     */     
    public void release(){
        if (wasDraged) {
            if(DEBUG)System.out.println("Released");
            super.release();//clean up
            if(!mouse.isValidDrag()){
                return;
            }

            if(zoomFactor>1){

                GeoRectangle b1 = context.scale.getProjectedMapExtent();
                GeoRectangle b2 = new GeoRectangle(b1.x-(mouse.projectedDragBox.x-b1.x),b1.y-(mouse.projectedDragBox.y-b1.y),b1.width+(b1.width-mouse.projectedDragBox.width),b1.height+(b1.height-mouse.projectedDragBox.height));
                context.scale.setProjectedMapExtent(b2);
                super.release();
                return;
            }
            context.scale.setMapExtent(mouse.dragBox);
            wasDraged=false;
        }//wasDraged
    }

    /**
     * Get the rubber band style to use during drag events for this tool.
     * In this case a rectangle is required.
     **/
    public int getRubberBandShape(){
        return RECTANGLE;
    }

    /**
     * provides a short name for this tool.
     * The name should be suitable for inclusion in a menu or on a button.
     */
    public String getName(){
        return "Zoom";
    }

    /**
     * provides a description for this tool.
     * The description should briefly describe the purpose of the dool
     *
     * @author James Macgill JM
     * @since 0.7.9 November 23 2000
     * @return String A description of this tool.
     */
    public String getDescription(){
        return "Zoom into a dragged out reagion (shift to zoom out)";
    }
}

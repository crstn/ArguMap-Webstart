package uk.ac.leeds.ccg.geotools;
import java.awt.Cursor;
/**
 * Provides the zoom funtionality for viewers.
 * ZoomTool is one of the basic set of tools that can be pluged into viewer and provides
 * the key functionality of zooming.<br>
 *
 * @since 0.7.7.2 07 June 2000
 * @author James Macgill JM
 */
public class ZoomTool extends SimpleTool{

	public static boolean DEBUG=false;
    /**
     * Returns the default cursor type for this tool.
     * In this instance, a CROSSHAIR.
     * @author James Macgill
     * @since 0.7.7.2 07 June 2000
     * @return Cursor the default cursor for this tool
     */
    public Cursor getCursor(){
        return  new Cursor(Cursor.CROSSHAIR_CURSOR);
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
        if(DEBUG)System.out.println("Released");
        super.release();//clean up
        if(!mouse.isValidDrag()){
            return;
        }
       
        if(mouse.isShiftDown()){
        
                GeoRectangle b1 = context.scale.getProjectedMapExtent();
                GeoRectangle b2 = new GeoRectangle(b1.x-(mouse.projectedDragBox.x-b1.x),b1.y-(mouse.projectedDragBox.y-b1.y),b1.width+(b1.width-mouse.projectedDragBox.width),b1.height+(b1.height-mouse.projectedDragBox.height));
                context.scale.setProjectedMapExtent(b2);
                //super.release();
                return;
         }
        context.scale.setMapExtent(mouse.dragBox);
        
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
   *
   * @author James Macgill JM
   * @since 0.7.9 November 23 2000
   * @return String The name of this tool.
   */
   public String getName(){
    return "Drag to zoom in; Press shift & drag to zoom out.";
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
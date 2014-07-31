package uk.ac.leeds.ccg.geotools;
import java.awt.Cursor;
/**
 * Provides the zoom funtionality for viewers.
 * OneClickZoomInTool is a simple alternative to the zoom tool that zooms in on a point.
 * 
 *
 * @since 0.7.9.0 22 November 2000
 * @author James Macgill JM
 */
public class CenterOnPointTool extends SimpleTool{
   
   
    /**
     * Returns the default cursor type for this tool.
     * In this instance, a HAND_CURSOR.
     * @author James Macgill
     * @since 0.7.9.0 22 November 2000
     * @return Cursor the default cursor for this tool
     */
    public Cursor getCursor(){
        return  new Cursor(Cursor.HAND_CURSOR);
    }
    

    
    /**
     * called when a mouse button has been clciked.
     * The view is centered on the point of the click.
     * 
     * @author James Macgill
     * @since 0.7.9.0 22 November 2000
     */
    public void click(){     
        
       
        context.centerOnPoint(mouse.getMapPoint());
        super.click();//clean up
    }
    public int getRubberBandShape(){
        return NONE;
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
    return "Center";
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
    return "Centers the map on selected point";
   }
}
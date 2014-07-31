package uk.ac.leeds.ccg.geotools;

import java.awt.Cursor;

public class SelectTool extends uk.ac.leeds.ccg.geotools.SimpleTool {
	/**
	  * isDragged is used to flag whether the mouse has actually been
	  * dragged before it is released. Otherwise, when you click, the remains
	  * of a (possible) previous drag will interfere, causing the release
	  * method to think that you've actually dragged.
      */
	private boolean isDragged = false;

	public Cursor getCursor(){

        return new Cursor(Cursor.CROSSHAIR_CURSOR);
    }

    public void release(){
        if(!isDragged || !mouse.isValidDrag()){
            return;
        }
        context.notifySelectionRegionChanged(mouse.dragBox);
        super.release();
		isDragged = false;
    }

	public void drag() {
		isDragged = true;
		super.drag();
	}

	public void click(){
        context.notifySelectionPositionChanged(mouse.getMapPoint());
    }
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
    return "Select";
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
    return "Select a region of the map by dragging with the mouse";
   }
}
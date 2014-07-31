package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 * A Pan tool for use with Viewers.  It provides an interactive pan action for navigating the map.<br>
 * When Pan is the current tool in a Viewer all mouse drags are interprited as a pan request.<br>
 * All drags are acompanied by a realtime movement of the current visible map area.<p>
 *
 * The class extends the helper class SimpleTool which provides a basic implentation of the Tool interface and also
 * rebroadcasts update event reason codes to individual methods, in this case picked up by drag and release.
 **/
public class PanTool extends uk.ac.leeds.ccg.geotools.SimpleTool
{
    //An image object to hold the visible map area so that it can be moved interactivly.
    Image panBuffer;

	/**
	 * set whether to allow the pantool to wander beyond the boundaries of the themes
	 */
	boolean allowWander = true;

	public PanTool() {
		super();
	}

	public PanTool(boolean allowWander) {
		super();
		this.allowWander = allowWander;
	}

	/**
     * Gets the prefered cursor for use in the viewer when this is the active tool.
     *
     * Required by the Tool interface.
     *
     * @return Cursor The Cursor to use.
     **/
    public Cursor getCursor(){
        return new Cursor(Cursor.HAND_CURSOR);
    }



	/**
     * Called by update in SimpleTool when an update request has been fired as a result of a drag event.<p>
     * This method makes no changes to the true position of the map, inststead it causes an image copy of
     * the current map display to follow the mouse pointer around as it is dragged, this action takes place in the
     * toolGraphics context.
     **/
    public void drag(){
        toolGraphics.setPaintMode();
        Rectangle bounds = context.getBounds();
        if(panBuffer==null){panBuffer = context.createImage(bounds.width,bounds.height);}
	    Graphics pb = panBuffer.getGraphics();
	    pb.setColor(context.getBackground());
	    pb.fillRect(0,0,bounds.width,bounds.height);
	    pb.drawImage(context.getScreenBuffer(),mouse.screen_xy[0]-mouse.screenDragStart[0],mouse.screen_xy[1]-mouse.screenDragStart[1],context);
	    toolGraphics.drawImage(panBuffer,0,0,context);
	    toolGraphics.setXORMode(Color.blue);
	}

    /**
     * Called by update in SimpleTool when an update request has been fired as a result of a mouse release event.<p>
     * This is the method that actualy actions the change in map position set up during the previous drag event.
     **/
    public void release(){
        GeoRectangle b1 = context.scale.getProjectedMapExtent();
	    GeoRectangle b2 = new GeoRectangle(b1.x+(mouse.projectedDragStart[0]-mouse.proj_xy[0]),b1.y+(mouse.projectedDragStart[1]-mouse.proj_xy[1]),b1.width,b1.height);

		// check to make sure we don't leave the image
        if(!allowWander)
        {
			if(context.getFullMapExtent().intersects(b2))
			{
				context.scale.setProjectedMapExtent(b2);
			}
		}
        else{
            context.scale.setProjectedMapExtent(b2);
        }

    }

    /**
     * Requred by the Tool interface, in this instance NONE is returned as Pan provides its own visual reprentation of the
     * pan event.
     *
     * @return int A code reprenting the prefered 'rubber band' to be dispayed during drag events, in this case NONE.
     **/
    public int getRubberBandShape(){
        return NONE;
    }

	public boolean isAllowWander() {
		return allowWander;
	}

	public void setAllowWander(boolean allowWander) {
		this.allowWander = allowWander;
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
    return "Pan";
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
    return "Pan the map by dragging with the mouse";
   }


}
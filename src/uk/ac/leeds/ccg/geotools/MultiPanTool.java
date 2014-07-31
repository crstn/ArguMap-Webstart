/**
 * A Pan tool which allows panning by dragging the mouse, and will also pan to
 * a point when a user clicks on a point.
 * When Pan is the current tool in a Viewer all mouse drags are interprited as
 * a pan request.<br>
 * All drags are acompanied by a realtime movement of the current visible map
 * area.<p>
 *
 * The class extends the helper class SimpleTool which provides a basic
 * implentation of the Tool interface and also
 * rebroadcasts update event reason codes to individual methods, in this case
 * picked up by drag and release.
 **/

package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;




public class MultiPanTool extends uk.ac.leeds.ccg.geotools.SimpleTool
{
    //An image object to hold the visible map area so that it can be moved interactivly.
    Image panBuffer;

    // flags which describes tool behavior
    boolean dragIsOn=true;
    boolean clickIsOn=true;

    // this flag is used to determine if the map was draged.
    boolean wasDraged=false;

    // set whether to allow the pantool to wander beyond the boundaries of the themes
    boolean allowWander = true;



    /**
     *  default constractor
     **/
    public MultiPanTool() {
        super();
        dragIsOn = true;
        clickIsOn = true;
    }

    /**
     *   constractor lets user set Drag and/or Click support
     **/
    public MultiPanTool(boolean allowDrag, boolean allowClick) {
        super();
        dragIsOn = allowDrag;
        clickIsOn = allowClick;
    }



    public MultiPanTool(boolean allowWander) {
        super();
        this.allowWander = allowWander;
    }


    /**
     *   sets drag mode
     **/	
    public void setAllowDrag(boolean flag) {
        this.dragIsOn = flag;	
    }


    /**
     *   sets click mode
     **/
    public void setAllowClick(boolean flag) {
        this.clickIsOn = flag;	
    }


    /**
     * Gets the prefered cursor for use in the viewer when this is the active tool.
     * Required by the Tool interface.
     * @return Cursor The Cursor to use.
     **/
    public Cursor getCursor(){
        return new Cursor(Cursor.HAND_CURSOR);
    }



    /**
     * Called by update in SimpleTool when an update request has been fired as a result of a click event.<p>
     **/
    public void click() {


        if (clickIsOn) {	
            // move the center of the map to the clicked point.
            double xc = mouse.getMapPoint().x;  // future x and y center coordinates (mouse click)
            double yc = mouse.getMapPoint().y;

            GeoRectangle b1 = context.scale.getProjectedMapExtent();

            double xnew = xc-b1.width/2;	// get new extent
            double ynew = yc-b1.height/2;

            GeoRectangle b2 = new GeoRectangle(xnew, ynew, b1.width, b1.height);  //new extent

            if(!allowWander)
            {
                if(context.getFullMapExtent().intersects(b2))
                {
                    context.scale.setProjectedMapExtent(b2);
                }
            }
            else {    context.scale.setProjectedMapExtent(b2);}
        }//if 
    }



    /**
     * Called by update in SimpleTool when an update request has been fired as a result of a drag event.<p>
     * This method makes no changes to the true position of the map, inststead it causes an image copy of
     * the current map display to follow the mouse pointer around as it is dragged, this action takes place in the
     * toolGraphics context.
     **/
    public void drag(){

        if (dragIsOn) {
            wasDraged = true;
            toolGraphics.setPaintMode();
            Rectangle bounds = context.getBounds();
            if(panBuffer==null){panBuffer = context.createImage(bounds.width,bounds.height);}
            Graphics pb = panBuffer.getGraphics();
            pb.setColor(context.getBackground());
            pb.fillRect(0,0,bounds.width,bounds.height);
            pb.drawImage(context.getScreenBuffer(),mouse.screen_xy[0]-mouse.screenDragStart[0],mouse.screen_xy[1]-mouse.screenDragStart[1],context);
            toolGraphics.drawImage(panBuffer,0,0,context);
            toolGraphics.setXORMode(Color.blue);
        }// if 
    }


    /**
     * Called by update in SimpleTool when an update request has been fired as a result of a mouse release event.<p>
     * This is the method that actualy actions the change in map position set up during the previous drag event.
     **/
    public void release(){

        // NOTICE: wasDraged flag is used because click() method can invoke this method as well.         
        if (wasDraged) {
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
            wasDraged = false;
        }//if

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
     * @return String The name of this tool.
     */
    public String getName(){
        return "MultiPanTool";
    }

    /**
     * provides a description for this tool.
     * The description should briefly describe the purpose of the dool
     *
     */
    public String getDescription(){
        return "Pan the map by dragging with the mouse, or clicking on a point";
    }
}

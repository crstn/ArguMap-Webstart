package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * An abstract implemtation of the Tool interface. This class provides some base
 * functionality that might be useful to a wide range of tools and extending it
 * would make the life of a tool developer much easier. <br>
 * Essentialy this call splits the update(reason) method into seperate calls,
 * defalt versions of which are provided. <br>
 * The class also provides some simple rubber band tools that can be used by
 * extending classes automaticaly. In order to make a concrete extention of this
 * class all that is needed is a getCursor and a getRubberBandShape method
 * although such a tool would not actualy do anything.
 * 
 * @author James Macgill JM
 * @since 0.7.7.2 07 June 2000
 */
public abstract class SimpleTool implements uk.ac.leeds.ccg.geotools.Tool {
    /**
     * All tools only operate on one viewer at a time. This variable holds a
     * reference to that viewer.
     */
    protected Viewer context;
    /**
     * A reference to a Graphics object onto which tools can place tempory
     * graphics.
     */
    Graphics toolGraphics;
    /**
     * Tracks at all times, the mouse status for the viewer this tool is
     * attached to. Provides information on mouse movments and drag regions in
     * screen, geographic and projected space.
     */
    protected MouseStatus mouse;

    /**
     * RubberBandShape constant
     */
    public static final int NONE = -1, CIRCLE = 1, RECTANGLE = 2, LINE = 3;

    /**
     * Locks the rubber bands to boxes and circles instead of ellipes and
     * rectangles.
     */
    protected boolean lockAspect = false;

    public void setLockAspect(boolean flag) {
        lockAspect = flag;
    }

    public boolean isLockAspect() {
        return lockAspect;
    }

    /**
     * Retun one of the above constants to specify which rubber band should be
     * automaticaly displayed during mouse drag events.
     * 
     * @return int A value from above representing a circle, rectangle or line
     *         (or none)
     */
    public abstract int getRubberBandShape();

    /**
     * called by the viewer which has recived this tool in order to set that
     * viewer as the context for this tool. <br>
     * It also initalizes the mouse status object. Once set up mouse provides
     * useful features for querying user acctions. e.g. mouse.getScreenDragBox()
     * mouse.getMapDragBox();
     * 
     * @param v
     *            The viewer to which this tool should now be attached.
     */
    public void setContext(Viewer v) {
        context = v;
        mouse = context.getMouseStatus();
    }

    /**
     * Called by the viewer to which the tool is attached (context) The tool
     * uses this to update its display and behavior based on the reason code
     * passed in and current mouse status.
     * 
     * The method rebradcasts the reason code to seperate methods:
     * <p>
     * clear() <br>
     * drag() <br>
     * release() <br>
     * click() <br>
     * 
     * @author James Macgill JM
     * @since 0.7.7.2 07 June 2000
     * 
     * @param g
     *            A Graphics object that cen be used for transient tool
     *            graphics. It is pre set to XORMode.
     * @param reason
     *            An int that references the reason why this method has been
     *            called.
     */
    public void update(Graphics g, int reason) {
        toolGraphics = g;
        switch (reason) {
        case CLEAR:
            clear();
            break;
        case M_DRAG:
            drag();
            break;
        case M_RELEASE:
            release();
            break;
        case M_CLICK:
            clear();
            click();
            break;
        case M_MOVE:
            move();
        }
    }

    /**
     * A default implemention of the percistent paint method. most tools will
     * NOT need to overide this method and can instead use the toolGraphics
     * object set up in the update method.
     * 
     * @author James Macgill JM
     * @since 0.7.7.2 07 June 2000
     * @param g
     *            A Graphics object that references the viewers static buffer.
     */
    public void paint(Graphics g) {
        //do nothing
    }

    /**
     * Called by update when the reason code is MOVE. 
     * 
     * @author Carsten Keﬂler
     * @since 0.7.7.2 12 september 2005
     */
    public void move() {
        //do nothing
    }
    
    public Graphics getToolGraphics(){
        return toolGraphics;
    }

    
    /**
     * Called by update when the reason code is DRAG. Provides automatic suport
     * for drawing a 'rubber band' of the shape specified by
     * getRubberBandShape().
     * 
     * @author James Macgill JM
     * @since 0.7.7.2 07 june 2000
     */
    public void drag() {
        if (!mouse.isDragStart()) {
            clear();
        }
        drawRubberBand(context.getMouseStatus().getScreenDragBox());
    }

    /**
     * Called when update is called with reason code CLICK. This default
     * implementation does nothing, overide to add behavior.
     * 
     * @author James Macgill
     * @since 0.7.7.2 07 June 2000
     */
    public void click() {
        //do nothing
    }

    /**
     * Called when update is called with reason code RELEASE. This default
     * implementaion calls clear() to remove any rubberBand marks, overide to
     * add behavior but try to call clean
     * 
     * @author James Macgill
     * @since 0.7.7.2 07 June 2000
     */
    public void release() {
        clear();
        //do nothing
    }

    /**
     * Called when update is called with reason code CLEAR whenever the old
     * rubber band needs to be removed.
     * 
     * @author James Macgill
     * @since 0.7.7.2 07 June 2000
     */
    public void clear() {
        drawRubberBand(context.getMouseStatus().getOldScreenDragBox());
    }

    /**
     * Uses XOR painting (preset in the toolGraphics) to draw a removable shape
     * to the screen.
     * 
     * @author James Macgill
     * @since 0.7.7.2 07 June 2000
     * @param box
     *            A Rectangle defining the start and finish points of the rubber
     *            band to draw.
     */
    public void drawRubberBand(Rectangle box) {
        toolGraphics.setColor(Color.red);
        toolGraphics.setXORMode(Color.blue);
        int x, y, width, height;
        x = box.x;
        y = box.y;
        if (lockAspect) {
            width = height = Math.max(box.width, box.height);
        } else {
            width = box.width;
            height = box.height;
        }
        switch (getRubberBandShape()) {
        case RECTANGLE:
            toolGraphics.drawRect(x, y, width, height);
            break;
        case CIRCLE:
            toolGraphics.drawOval(x - width, y - height, width * 2, height * 2);
            break;
        case LINE:
            toolGraphics.drawLine(x, y, width, height);
            break;
        }
        toolGraphics.setPaintMode();
    }
}
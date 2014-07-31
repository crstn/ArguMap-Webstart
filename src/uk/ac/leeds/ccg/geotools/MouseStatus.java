package uk.ac.leeds.ccg.geotools;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * Mouse Status is a new class designed to take some of load (and code bloat)
 * out of the main Viewer class. <br>
 * Although MouseStatus objects should only be setup and configured by Viewer
 * objects MouseStatus objects will be usefull for objects that are to act as
 * tools in conjunction with a Viewer as the MouseStatus should carry all of the
 * information any tool should need to know about what and where a user has been
 * interacting with the mouse on the viewer. <br>
 * The MouseStatus class tracks mouse movements (particularly drags) in three
 * co-ordinate systems. <br>
 * Screen space <br>
 * 'GeoGraphic' space. <br>
 * projected space. <br>
 */

public class MouseStatus {
    /**
     * debug switch. used to control debug output. Output will be identified by
     * prefix 'MoS->'
     */
    private final static boolean DEBUG = false;
    /**
     * The last mouse event to be generated
     */
    private MouseEvent lastEvent;

    /**
     * The viewer to which this MouseStatus is attached.
     */
    private Viewer context;

    /**
     * Is the user draging the mouse at the moment.
     */
    boolean draging = false;

    /**
     * Is the pointer inside the viewer at the moment.
     */
    protected boolean isPointerInside = true;

    /**
     * A GeoRectangle that represents the bounds of the current drag region in
     * the same units as the origional geography.
     */
    protected GeoRectangle dragBox;

    /**
     * A GeoRectangle that represents the bounds of the current drag region
     * projected to the current projection system.
     */
    protected GeoRectangle projectedDragBox;

    /**
     * The geographic coordinates of the start position of the most recently
     * started drag
     */
    protected double dragStart[];

    /**
     * The projected coordinates of the start position of the most recently
     * started drag
     */
    protected double projectedDragStart[];
    /**
     * An AWT Rectangle that represents the bounds of the current drag region in
     * on screen pixel coordinates.
     */
    protected Rectangle screenDragBox;
    protected Rectangle oldScreenDragBox;

    /**
     * The on screen coordinates of the start position of the most recently
     * started drag
     */
    protected int screenDragStart[];
    protected boolean isDragStart = false;
    protected int dragCount = 0;

    /**
     * A special timer that tracks how long the mouse has been stationary for.
     * <br>
     * Its main role is to inform the viewer when the mouse has rested long
     * enough for it to consider displaying a ToolTip over the feature under the
     * pointer.
     */
    private MouseIdle mouseIdle;

    /**
     * Notes whether or not the mouse has been idle for at least a set period of
     * time. <br>
     * timeout.
     */
    private boolean mouseStill = true;

    /**
     * The period of time for which the mouse has to be idle before it is
     * considered Still.
     */
    protected int timeout = 250;

    /**
     * current 'on_screen' coordinates
     */
    protected int screen_xy[] = { 0, 0 };

    /**
     * location of the on_screen coordinates in the real world
     */
    protected double map_xy[] = { 0, 0 };
    /**
     * location of the on_screen coordinates in the real world, stored as a
     * GeoPoint
     */
    protected GeoPoint map_point = new GeoPoint(0, 0);

    /**
     * location of the on_screen coordinates in projected space
     */
    protected double proj_xy[] = { 0, 0 };

    /**
     * contruct a MouseStatus object. This must only be called by the viewer
     * that is passed in as the argument.
     * 
     * @param v
     *            The viewer which is constructing this MouseStatus object, and
     *            to which the status values will relate.
     */
    public MouseStatus(Viewer v) {
        if (DEBUG)
            System.out
                    .println("---->uk.ac.leeds.ccg.geotools.MouseStatus constructed. Will be identified as MoS->");
        dragBox = new GeoRectangle();
        projectedDragBox = new GeoRectangle();
        screenDragBox = new Rectangle();
        oldScreenDragBox = new Rectangle();
        mouseIdle = new MouseIdle();
        mouseIdle.start();
        context = v;
        context.addMouseMotionListener(new StatusMouseMotion());
        context.addMouseListener(new StatusMouse());

    }

    /**
     * Find out which viewer this object is attached to
     * 
     * @return Viewer The viewer that this MouseStatus object is attached to
     */
    public Viewer getContext() {
        return context;
    }

    public GeoPoint getMapPoint() {
        return map_point;
    }

    public GeoRectangle getMapDragBox() {
        return dragBox;
    }

    /**
     * The bounds of the most recently started drag box.
     * 
     * @return Rectangle an AWT rectangle containing the current drag region
     *         coordinates
     */
    public Rectangle getScreenDragBox() {
        return screenDragBox;
    }

    public Rectangle getOldScreenDragBox() {
        return oldScreenDragBox;
    }

    protected void clickTo(int x, int y) {
        map_xy = context.scale.toMap(x, y); //need to change scale to allow
                                            // returning a point
        map_point.setLocation(map_xy[0], map_xy[1]);
        proj_xy = context.scale.toProj(x, y);
        if (DEBUG)
            System.out.println("Mos>Psudo micro drag");
        draging = false;
        //dragTo(x,y);
        //dragTo(x,y);
    }

    /**
     * The pointer has left the viewer.
     */
    protected void exitAt(int x, int y) {
        if (mouseIdle != null) {
            mouseIdle.noGo();
        }

        if (mouseStill) {
            //The mouse is no longer still, so clear any toolTips or similer.
            mouseStill = false;
            context.repaint();
        }
    }

    protected void moveTo(int x, int y) {
        screen_xy[0] = x;
        screen_xy[1] = y;

        map_xy = context.scale.toMap(x, y); //need to change scale to allow
                                            // returning a point
        map_point.setLocation(map_xy[0], map_xy[1]);
        proj_xy = context.scale.toProj(x, y);

        if (mouseIdle != null) {
            mouseIdle.noGo();
        }

        if (mouseStill) {
            //The mouse is no longer still, so clear any toolTips or similer.
            mouseStill = false;
            context.repaint();
        }
    }

    protected void pressed(int x, int y) {
        mouseIdle.noGo();
        draging = false;
        dragTo(x, y);
    }

    protected void release(int x, int y) {
        draging = false;
        mouseIdle.noGo();
    }

    /**
     * On some ocasions the user may click and hold the mouse button and move
     * the pointer a very small distance by accident. In most cases this should
     * not be interprited as a valid drag.
     * <p>
     * This method checks to see if the last drag distance was grater than a
     * small threshold.
     * 
     * @return boolean true only if the last drag was more than a small
     *         threshold value.
     */
    public boolean isValidDrag() {
        System.out.println("Mos>valid drag? "
                + !(screenDragBox.width < 5 || screenDragBox.height < 5));
        return !(screenDragBox.width < 5 || screenDragBox.height < 5);
    }

    protected void dragTo(int x, int y) {
        if (!draging) {
            draging = true;
            isDragStart = true;
            //dragCount = 0;
            screen_xy[0] = x;
            screen_xy[1] = y;
            map_xy = context.scale.toMap(x, y);
            map_point.setLocation(map_xy[0], map_xy[1]);
            proj_xy = context.scale.toProj(x, y);
            dragStart = context.scale.toMap(x, y);
            projectedDragStart = context.scale.toProj(x, y);

            screenDragStart = new int[2];
            screenDragStart[0] = x;
            screenDragStart[1] = y;
            screenDragBox.x = x;
            screenDragBox.y = y;
            screenDragBox.width = 0;
            screenDragBox.height = 0;

            projectedDragBox.x = proj_xy[0];
            projectedDragBox.y = proj_xy[1];
            projectedDragBox.width = 0;
            projectedDragBox.height = 0;

            dragBox.x = map_xy[0];
            dragBox.y = map_xy[1];
            dragBox.width = 0;
            dragBox.height = 0;
        } else {
            isDragStart = false;
            screen_xy[0] = x;
            screen_xy[1] = y;
            map_xy = context.scale.toMap(x, y);
            map_point.setLocation(map_xy[0], map_xy[1]);
            proj_xy = context.scale.toProj(x, y);
            //dragBox.add(world_xy[0],world_xy[1]);
            dragBox.x = Math.min(dragStart[0], map_xy[0]);
            dragBox.y = Math.min(dragStart[1], map_xy[1]);
            dragBox.width = Math.abs(map_xy[0] - dragStart[0]);
            dragBox.height = Math.abs(map_xy[1] - dragStart[1]);

            projectedDragBox.x = Math.min(dragStart[0], proj_xy[0]);
            projectedDragBox.y = Math.min(dragStart[1], proj_xy[1]);
            projectedDragBox.width = Math.abs(proj_xy[0] - dragStart[0]);
            projectedDragBox.height = Math.abs(proj_xy[1] - dragStart[1]);
            oldScreenDragBox.x = screenDragBox.x;
            oldScreenDragBox.y = screenDragBox.y;
            oldScreenDragBox.width = screenDragBox.width;
            oldScreenDragBox.height = screenDragBox.height;
            screenDragBox.x = Math.min(screenDragStart[0], x);
            screenDragBox.y = Math.min(screenDragStart[1], y);
            screenDragBox.width = Math.abs(x - screenDragStart[0]);
            screenDragBox.height = Math.abs(y - screenDragStart[1]);
            if (DEBUG) {
                System.out.println("MoS updated dragboxes");
            }
        }

    }

    /**
     * Test to see if the mouse has not moved for a while. <br>
     * Useful for deciding if ToolTips or similer should be displayed.
     * 
     * @return boolean true if the mouse has not moved for timeout milliseconds.
     */
    public boolean isMouseStill() {
        return mouseStill;
    }

    /**
     * Test to see if the user is currently performing a drag operation
     * 
     * @return boolean true if the user has started but not finished a drag.
     */
    public boolean isDraging() {
        return draging;
    }

    public boolean isPointerInside() {
        return isPointerInside;
    }

    public boolean isDragStart() {
        return isDragStart;
    }

    public void finalize() {
        System.out.println("Mos>Cleaning up MouseStatus");
        mouseIdle.kill();
    }

    public MouseEvent getMouseEvent() {
        return lastEvent;
    }

    public boolean isShiftDown() {
        return lastEvent.isShiftDown();
    }

    class MouseIdle extends Thread {
        private final static boolean DEBUG = true;
        private boolean skip = false;
        private boolean finished = false;

        public void kill() {
            finished = true;
        }

        public void noGo() {
            skip = true;
        }

        public void run() {
            while (!finished) {
                try {
                    sleep(context.timeout);
                    if (!skip && !draging && !mouseStill) {
                        mouseStill = true;
                        context.repaint();
                    } else {
                        skip = false;
                        //System.out.println("Mos>Skip by logic");
                    }
                } catch (InterruptedException ie) {
                    //System.out.println("Mos>skip by interupt");
                } catch (Exception e) {
                    //System.out.println("Mos>skip boo by "+e);
                }

            }
            if (DEBUG)
                System.out.println("Mos>MouseIdle stoped");
        }
    }

    class StatusMouseMotion extends java.awt.event.MouseMotionAdapter {
        public void mouseMoved(java.awt.event.MouseEvent event) {
            lastEvent = event;
            moveTo(event.getX(), event.getY());
        }

        public void mouseDragged(java.awt.event.MouseEvent event) {
            lastEvent = event;
            dragTo(event.getX(), event.getY());
        }
    }

    class StatusMouse extends java.awt.event.MouseAdapter {
        public void mousePressed(java.awt.event.MouseEvent event) {
            lastEvent = event;
            pressed(event.getX(), event.getY());
        }

        public void mouseClicked(java.awt.event.MouseEvent event) {
            lastEvent = event;
            clickTo(event.getX(), event.getY());
        }

        public void mouseExited(java.awt.event.MouseEvent event) {
            lastEvent = event;
            isPointerInside = false;
            exitAt(event.getX(), event.getY());
        }

        public void mouseEntered(java.awt.event.MouseEvent event) {
            lastEvent = event;
            isPointerInside = true;
            exitAt(event.getX(), event.getY());
        }

        public void mouseReleased(java.awt.event.MouseEvent event) {
            lastEvent = event;
            if (DEBUG)
                System.out.println("Mos>Mouse status release");
            dragTo(event.getX(), event.getY());
            release(event.getX(), event.getY());
        }

    }

}
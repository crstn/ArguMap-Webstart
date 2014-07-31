package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.projections.Projection;
import uk.ac.leeds.ccg.widgets.ThemePanel;

/**
 * Viewer is a medium for displaying geographic maps in applets and
 * applications. The map to display is built up from information stored in a
 * number of thmes.
 * <p>
 * 
 * To construct a working viewer you need to understand how to construct themes
 * and layers, a task that will be made easer over time as more helper classes
 * are writen.
 * <p>
 * 
 * A viewer has the ability to build up a scaled image from upto three parts
 * 1)any number of static themes - not animated at all 2)a sequence of single
 * static themes 3)animated themes
 * 
 * 
 * @author James Macgill
 * @author Ian Turton
 * @version 0.8.0 GeoTools is a Java class library for developing interactive
 *          mapping applets and applications. Copyright (C) 1999 James Macgill,
 *          Ian Turton.
 * 
 * This file along with the whole GeoTools library is available under the GNU
 * LGPL
 * 
 * You can contact the author at James Macgill Center for Computational
 * Geography School of Geography Leeds University Leeds W.Yorkshire UK LS2 9JT
 * 
 * j.macgill@geog.leeds.ac.uk
 * 
 * Thanks to Joachim Bröckl (saltag) for bug fixes
 */

public class Viewer extends java.awt.Component implements ScaleChangedListener,
        ThemeChangedListener {
    /**
     * Version information
     */
    public static final String cvsid = "$Id: Viewer.java,v 1.1 2005/09/19 10:31:28 CarstenKessler Exp $";
    /**
     * Constants that represent the different tool modes for a viewer
     */
    public static final int ZOOM = 0, PAN = 1, NAVIGATE = 2, SELECT = 3;
    public Dimension pd = new Dimension(50, 50), maxd, mind = new Dimension(50,
            50);

    /**
     * The current tool mode for the viewer
     */
    // private int toolMode = ZOOM;
    /**
     * The current tool
     */
    private Tool tool;
    private Graphics toolGraphics;

    /**
     * Scaler responsible for converting realworld values to scale
     */
    public Scaler scale;

    /**
     * keep count of how many themes we are displaying
     */
    private int themeCount = 0;
    private final boolean debug = false;
    String name = "Un-named Viewer ";
    
    /**
     * Used to store an image of the compleated scaled map sections Buffer,
     * Holds full image before display.<br>
     * static holds the static unchanging themes.<br>
     * sequence is for an animated section.<br>
     * pan if for smooth paning of the image.<br>
     * selection is experimental but is for speeding up selections.
     */
    private transient Image screenBuffer, staticBuffer, sequenceBuffer[],
            panBuffer, selectionBuffer;

    /**
     * A vector containing all the static themes in this viewer
     */
    private Vector staticThemes = new Vector();

    /**
     * A vector containing all the static themes to draw
     */
    private Vector visibleThemes = new Vector();

    /**
     * An array of themes that make up the sequence part of the display.
     */
    private Theme sequenceTheme[], animationTheme;

    /**
     * A record of the last known size of this component Used to check for
     * changes in the dimentions of commponent
     */
    // private Rectangle last_size;
    /**
     * used to track the current status of the mouse. It contains info on the
     * curent mouse position (in screen, projection and geographic space) as
     * well as details of the last drag operation (start and end point in all
     * three systems)
     */
    private MouseStatus mouseStatus;

    /**
     * geoRectangle bounding box that will hold all layers
     */
    private GeoRectangle fullMapExtent = new GeoRectangle();

    /**
     * Notes wheter the backdrop has been set up yet
     */
    private boolean displaying = false;

    /**
     * Stores the current frame number for the sequence buffer
     */
    private int frame = 0;

    /**
     * highlighting modes INSTANT = As mouse moves around screen now on REQUEST
     * by default;
     */
    public final static int INSTANT = 0;
    /**
     * highlighting modes REQUEST = As mouse clicks on object now on REQUEST by
     * default;
     */
    public final static int REQUEST = 1;
    /**
     * Holds the highlighting mode INSTANT = As mouse moves around screen
     * REQUEST = As mouse clicks on object now on INSTANT by default;
     */
    private int highlightMode = INSTANT;

    /**
     * Holds the ID of the object currently under the pointer
     */
    private int currentID = 0;

    /**
     * Holds all of the listeners for ID changed events
     */
    private Vector listeners = new Vector();

    /**
     * Holds all of the click Listeners
     */
    private Vector clickedListeners = new Vector();

    /**
     * Holds the composition listeners
     */
    private Vector compositionChangedListeners = new Vector();

    /**
     * Holds all the highlight position change listeners
     */
    private Vector hpcListeners = new Vector();

    /**
     * Holds all the selection position change listeners
     */
    private Vector spcListeners = new Vector();

    /**
     * Holds all the selection region change listeners
     */
    private Vector srcListeners = new Vector();

    private SelectionRegionChangedListener srcFinal = null;

    /**
     * Hoads a flag for each frame in the sequence buffer to say if it is
     * up-to-date or not
     */
    private boolean sequenceFrameValid[];

    /**
     * Notes if the selection has changed since the last repaint
     */
    private boolean selectionChanged = true;

    /**
     * Holds the last know size of the viewer as a check for when it changes
     */
    private Rectangle lastSize = new Rectangle();

    private boolean showTips = true;

    // private MouseIdle mouseIdle;
    // private boolean mouseStill = true;
    protected int timeout = 250;

    protected ThemeStack themeStack = new ThemeStack();

    public Viewer() {
        this(true);
    }

    /**
     * Default constructor for a viewer. Sets up and initalises a new viewer, it
     * won't do much however untill it has at least one theme added to it.
     * 
     * @see addTheme
     * @see addSaticTheme
     * @see addSequenceTheme
     */
    public Viewer(boolean activeMouse) {
        if (debug)
            System.out
                    .println("---->uk.ac.leeds.ccg.geotools.Viewer constructed, will identify itself as V--->");
        if (debug)
            System.out.println("V--->Viewer Started");
        // last_size = new Rectangle();
        // last_size.add(this.getBounds());
        mouseStatus = new MouseStatus(this);
        setTool(new ZoomTool());
        scale = new Scaler(fullMapExtent, this.getBounds());

        if (activeMouse) {
            ViewerMouse aViewerMouse = new ViewerMouse();
            this.addMouseListener(aViewerMouse);
            ViewerMouseMotion aViewerMouseMotion = new ViewerMouseMotion();
            this.addMouseMotionListener(aViewerMouseMotion);

            ComponentAdapt adapt = new ComponentAdapt();
            this.addComponentListener(adapt);
        }

    }

    public void addTheme(Theme t, int waight) {
        addStaticTheme(t, waight);
    }

    /**
     * Add a new theme. There is some duplicatin here as addTheme simply calls
     * addStaticTheme
     */
    public void addTheme(Theme t) {
        addStaticTheme(t, 0);
    }

    public Dimension getPreferredSize() {
        if (debug)
            System.out.println("V--->" + name + "Pref " + pd.width + " "
                    + pd.height);
        return pd;
    }

    public Dimension getMinimumSize() {
        // System.out.println("V--->"+name+"Min "+mind.width+" "+mind.height);
        if (debug)
            System.out.println("V--->Min size called");
        return mind;
    }

    public Dimension getMaximumSize() {
        if (debug)
            System.out.println("V--->" + name + "Max " + maxd.width + " "
                    + maxd.height);
        return maxd;
    }

    public void setPreferredSize(Dimension d) {
        if (debug)
            System.out.println("V--->" + name + "Pref " + d.width + " "
                    + d.height);
        pd = d;
    }

    public void setMinimumSize(Dimension d) {
        if (debug)
            System.out.println("V--->" + name + "Min " + d.width + " "
                    + d.height);
        mind = d;
    }

    public void setMaximumSize(Dimension d) {
        if (debug)
            System.out.println("V--->" + name + "Max " + d.width + " "
                    + d.height);
        maxd = d;
    }

    public void setBounds(int x, int y, int w, int h) {
        if (w >= 0 && h >= 0) {
            super.setBounds(x, y, w, h);
            pd = new Dimension(w, h);
            mind = new Dimension(w, h);
            maxd = new Dimension(w, h);
        }
    }

    /**
     * Remove a theme
     */
    public void removeTheme(Theme t) {
        removeStaticTheme(t);
        themeStack.removeTheme(t);
    }

    public void removeStaticTheme(Theme t) {
        if (visibleThemes.contains(t)) {
            visibleThemes.removeElement(t);
        }
        if (staticThemes.contains(t)) {
            staticThemes.removeElement(t);
            removeHighlightPositionChangedListener(t);
            removeSelectionPositionChangedListener(t);
            removeSelectionRegionChangedListener(t);
            themeStack.removeTheme(t);
            themeCount--;
            updateStaticBuffer();
        }
        notifyCompositionChanged(CompositionChangedEvent.REMOVED);
    }

    /**
     * Add an array of themes These can then be steped through in sequence or
     * displayed on request
     * 
     * @param t[]
     *            an array of themes
     */
    public void setSequenceTheme(Theme t[]) {
        if (sequenceTheme == null) {
            themeCount++;
        }
        createSequenceBuffer(t.length);
        sequenceTheme = t;
        // add all the themes to list of highlight position changed listeners
        for (int i = 0; i < t.length; i++) {
            // addHighlightPositionChangedListener(t[i]);
            t[i].addThemeChangedListener(this);
            if (debug)
                System.out.println("V--->" + name
                        + "Adding a theme to Bounds\n" + fullMapExtent);
            fullMapExtent.add(t[i].getBounds());
            if (debug)
                System.out.println("V--->" + name + "Added a theme to Bounds\n"
                        + fullMapExtent);
        }
        if (themeCount == 1) {
            setupScale();
        }
        /* updateSequenceBuffer(); */
        invalidateSequenceBuffer();

    }

    /**
     * Selects witch of the sequence frames to display
     */
    public void setSequenceFrame(int f) {
        frame = f;
        if (debug)
            System.out.println("V--->Frame set to" + f);
        // map_point = new GeoPoint(map_xy[0],map_xy[1]);
        sequenceTheme[frame].setHighlight(mouseStatus.map_point);
        update(this.getGraphics());
    }

    /**
     * adds the theme that will be used to hold any animations
     */
    public void setAnimationTheme(Theme t) {
        if (animationTheme != null) {
            animationTheme.removeThemeChangedListener(this);
        }
        animationTheme = t;
        if (animationTheme != null) {
            t.addThemeChangedListener(this);
        }
    }

    /**
     * used internaly to create the sequence buffer
     */
    private void createSequenceBuffer(int size) {
        if (getBounds().width == 0
                || this.createImage(getBounds().width, getBounds().height) == null) {
            // throw an exception ???
            // throw(new Error("viewers MUST be added to a peer (like applet or
            // frame etc. before they can be used)"));
            return;
        }
        sequenceBuffer = new Image[size];
        sequenceFrameValid = new boolean[size];
        for (int i = 0; i < size; i++) {
            sequenceBuffer[i] = this.createImage(getBounds().width,
                    getBounds().height);
            sequenceFrameValid[i] = false;
        }
    }

    /**
     * set whether highlights are done instantly or on request viewer.INSTANT /
     * viewer.REQUEST
     */
    public void setHighlightMode(int flag) {
        highlightMode = flag;
    }

    /**
     * set the timeout before displaying a tooltip for a feature
     */
    public void setToolTipTimeout(int t) {
        timeout = t;
    }

    /**
     * get the timeout before displaying a tooltip for a feature
     */
    public int getToolTipTimeout() {
        return timeout;
    }

    /**
     * update the contents of the sequenceBuffer may be prossesor intesive!!!
     */
    private void updateSequenceBuffer() {
        Graphics g = null;
        if (sequenceBuffer != null) {
            for (int i = 0; i < sequenceBuffer.length; i++) {
                g = sequenceBuffer[i].getGraphics();
                if (staticBuffer != null) {
                    g.drawImage(staticBuffer, 0, 0, this);
                }
                sequenceTheme[i].paintScaled(g, scale);
                sequenceFrameValid[i] = true;
                // setSequenceFrame(i);//experimental, needs a switch as this
                // may not always be desired
            }
        }
    }

    private void updateSequenceBuffer(int frame) {
        Graphics g = null;
        if (sequenceBuffer != null) {
            g = sequenceBuffer[frame].getGraphics();
            if (staticBuffer != null) {
                g.drawImage(staticBuffer, 0, 0, this);
            }
            sequenceTheme[frame].paintScaled(g, scale);
            sequenceFrameValid[frame] = true;
        }
    }

    /**
     * clear the up-to-date flags for all of the sequence buffer
     */
    public void invalidateSequenceBuffer() {
        if (sequenceBuffer != null) {
            for (int i = 0; i < sequenceBuffer.length; i++) {
                sequenceFrameValid[i] = false;
            }
        }
    }

    public void invalidateStaticBuffer() {
        updateStaticBuffer();
        repaint();
        notifyCompositionChanged(CompositionChangedEvent.VISIBILITY);
    }

    /**
     * Add a new static theme
     * 
     * @param t
     */
    public void addStaticTheme(Theme t) {
        addStaticTheme(t, 0);
    }

    public void addStaticTheme(Theme t, int waight) {
        themeStack.addTheme(t, waight, true);
        staticThemes.addElement(t);
        visibleThemes.addElement(t);
        themeCount++;
        // if no scale has been set then scale to this theme
        if (t.getBounds().width > 0) {
            if (debug)
                System.out.println("V--->" + name
                        + "Adding a theme to Bounds\n" + fullMapExtent);
            fullMapExtent.add(t.getBounds());
            if (debug)
                System.out.println("V--->" + name + "Added a theme to Bounds\n"
                        + fullMapExtent);
        }
        if (themeCount == 1) { // is this the first?
            setupScale();
        }
        // update the staticBuffer to acount for this addition
        updateStaticBuffer();
        // add theme to those notified when highlight position changes
        addHighlightPositionChangedListener(t);
        addSelectionPositionChangedListener(t);
        addSelectionRegionChangedListener(t);
        t.addThemeChangedListener(this);
        notifyCompositionChanged(CompositionChangedEvent.ADDED);
        // if(debug)System.out.println("New theme added");
    }

    private void setupScale() {
        lastSize = getBounds();
        if (scale != null) {
            if (debug)
                System.out.println("V--->" + name
                        + "Setting up a non null scaler");
            scale.removeScaleChangedListener(this);
        }
        // scale = new Scaler(fullMapExtent,this.getBounds());
        scale.addScaleChangedListener(this);
        scale.setGraphicsExtent(getBounds());
        scale.setMapExtent(fullMapExtent, true);

    }

    /**
     * Called when something has happend that requires the staticBuffer be
     * redrawn this may be a chane in scale or an aditional theme being added
     */
    private synchronized void updateStaticBuffer() {
        Cursor old = this.getCursor();
        if (debug)
            System.out.println("V--->" + name + "Updating Static Buffer");
        selectionChanged = true;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (staticBuffer == null && getBounds().width > 0)
            staticBuffer = this.createImage(getBounds().width,
                    getBounds().height);
        if (staticBuffer == null) {
            this.setCursor(old);
            return;
        }
        Graphics g = staticBuffer.getGraphics();
        Theme t;
        // if(debug)System.out.println("Updating static buffer");
        g.setColor(this.getBackground());
        Rectangle r = this.getBounds();
        g.fillRect(0, 0, r.width, r.height);
        // for (Enumeration e = staticThemes.elements() ; e.hasMoreElements() ;)
        // {
        ThemeStack.ThemeInfo[] list = themeStack.getOrderedThemeInfos();
        if (debug)
            System.out.println("V--->" + name + "Ordered list contains "
                    + list.length + " entries");
        for (int i = 0; i < list.length; i++) {

            if (!list[i].isVisible())
                continue;
            t = list[i].getTheme();
            // while(it.hasNext()){
            // t = (Theme)it.next();
            // if(visibleThemes.contains(t)){
            if (debug)
                System.out.println("V--->" + name
                        + "Painting theme in updatestatic buffer");
            // if(visibleThemes.contains(t)){
            if (debug)
                System.out.println("Drawing " + t);
            t.paintScaled(g, scale);
            // }
        }
        /*
         * for (Enumeration e = visibleThemes.elements() ; e.hasMoreElements() ;) {
         * t=(Theme)e.nextElement();
         * if(debug)System.out.println("V--->"+name+"Painting theme in
         * updatestatic buffer"); //if(visibleThemes.contains(t)){
         * if(debug)System.out.println("Drawing "+t); t.paintScaled(g,scale);
         * //} }
         */
        this.setCursor(old);
        // force update of selection buffer to reflect changes in this buffer
        // for now seting flag, but could possibly call updateSelectionBuffer
        // directly?
        this.selectionChanged = true;
        // repaint();
    }

    /**
     * Called when something has happend that requires the staticBuffer be
     * redrawn this may be a chane in scale or an aditional theme being added
     */
    private void updateSelectionBuffer() {
        Cursor old = this.getCursor();
        if (debug)
            System.out.println("V--->" + name + "Updating Selection Buffer");
        selectionChanged = false;
        // this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if (selectionBuffer == null && getBounds().width > 0)
            // System.out.println("Creating static buffer");
            selectionBuffer = this.createImage(getBounds().width,
                    getBounds().height);
        if (selectionBuffer == null) {
            // System.out.println("Null static buffer");
            return;
        }
        Graphics g = selectionBuffer.getGraphics();
        Theme t;
        // System.out.println("Painting selections");
        g.drawImage(staticBuffer, 0, 0, this);
        paintSelections(g);
    }

    private void paintHighlights(Graphics g) {
        Theme t;
        for (Enumeration e = staticThemes.elements(); e.hasMoreElements();) {
            t = (Theme) e.nextElement();
            if (isThemeVisible(t)) {
                t.paintHighlight(g, scale);
            }
        }
        try {
            if (sequenceBuffer[frame] != null)
                sequenceTheme[frame].paintHighlight(g, scale);// experiment
        } catch (Exception e) {
        }

    }

    private void paintSelections(Graphics g) {
        Theme t;
        for (Enumeration e = staticThemes.elements(); e.hasMoreElements();) {
            if (debug) {
                System.out.println("V--->painting themes selections");
            }
            t = (Theme) e.nextElement();
            t.paintSelection(g, scale);
        }
        try {
            if (sequenceBuffer[frame] != null)
                sequenceTheme[frame].paintSelection(g, scale);// experiment
        } catch (Exception e) {
        }
        if (debug) {
            System.out.println("V--->Done");
        }
        selectionChanged = false;
    }

    /**
     * Gets the map extent that fits round all currently selected features.
     * 
     * @since 0.6.5
     * @return GeoRectangle representing the bounding box of the selected
     *         features
     */
    public GeoRectangle getSelectionMapExtent() {
        GeoRectangle sme = new GeoRectangle();
        Theme t;
        for (Enumeration e = staticThemes.elements(); e.hasMoreElements();) {
            t = (Theme) e.nextElement();
            sme.add(t.getSelectionMapExtent());
        }

        return sme;
    }

    /**
     * Scales the map so that the given theme fills the viewer
     * 
     * @param bounds
     *            A theme that defines the new visible extents
     */
    public void setMapExtent(Theme t) {
        if (themeCount > 0) {
            scale.setMapExtent(t.getBounds());
        } else {
            fullMapExtent.add(t.getBounds());
            setupScale();
        }
    }

    /**
     * sets the maximum mapextent to use for resets and drawing until another
     * theme is added.
     */

    public void setMaximumMapExtent(Theme t) {
        setMaximumMapExtent(t.getBounds());
    }

    public void setMaximumMapExtent(GeoRectangle r) {
        fullMapExtent = r;
    }

    /**
     * Scales the map so that the given point is at the center of the view.
     * 
     * @param p
     *            The point to center on.
     */
    public void centerOnPoint(GeoPoint p) {
        GeoRectangle bounds = scale.getMapExtent();
        double xOffset = bounds.width / 2;
        double yOffset = bounds.height / 2;
        GeoRectangle newBounds = new GeoRectangle(p.x - xOffset, p.y - yOffset,
                xOffset * 2, yOffset * 2);
        setMapExtent(newBounds);
    }

    /**
     * Scales the map so that the given point is at the center of the view, and
     * then zooms to the specified amount
     * 
     * @param p
     *            The point to center on.
     * @param percent
     *            The ammount to zoom in by.
     */
    public void zoomOnPoint(GeoPoint p, double percent) {
        GeoRectangle bounds = scale.getMapExtent();
        double newWidth = fullMapExtent.width * (100d / percent);
        double newHeight = fullMapExtent.height * (100d / percent);

        double xOffset = newWidth / 2d;
        double yOffset = newHeight / 2d;

        GeoRectangle newBounds = new GeoRectangle(p.x - xOffset, p.y - yOffset,
                xOffset * 2, yOffset * 2);
        if (debug)
            System.out.println("V--->old " + bounds + "\n new " + newBounds);
        setMapExtent(newBounds);
    }

    /**
     * Scales the map so that the current center remains whilst the zoom level
     * is set to a percentage of the full size.
     * 
     * @param p
     *            The point to center on.
     * @param percent
     *            The ammount to zoom in by.
     */
    public void zoomPercent(double percent) {
        GeoRectangle bounds = scale.getMapExtent();
        GeoPoint p = new GeoPoint(bounds.x + bounds.width / 2, bounds.y
                + bounds.height / 2);

        double newWidth = fullMapExtent.width * (100d / percent);
        double newHeight = fullMapExtent.height * (100d / percent);

        double xOffset = newWidth / 2d;
        double yOffset = newHeight / 2d;

        GeoRectangle newBounds = new GeoRectangle(p.x - xOffset, p.y - yOffset,
                xOffset * 2, yOffset * 2);

        if (debug)
            System.out.println("V--->old " + bounds + "\n new " + newBounds);
        setMapExtent(newBounds);
    }

    /**
     * Calculates the value of the current zoom level as a percentage of the
     * full map size. return double The current zoom factor as a percentage.
     */
    public double getZoomAsPercent() {
        return Math.max((100d / scale.getMapExtent().getWidth())
                * fullMapExtent.width,
                (100d / scale.getMapExtent().getHeight())
                        * fullMapExtent.height);
    }

    /**
     * Scales the map so that the given point is at the center of the view, and
     * then zooms out by the specified amount
     * 
     * @param p
     *            The point to center on.
     * @param percent
     *            The ammount to zoom out by.
     */
    public void zoomOutOnPoint(GeoPoint p, double percent) {
        double per = getZoomAsPercent();
        if (debug)
            System.out.println("Zoom out from " + per);
        per -= (percent / 100d) * per;
        if (debug)
            System.out.println("Zoom out to " + per);
        zoomOnPoint(p, per);
    }

    /**
     * Scales the map so that the given point is at the center of the view, and
     * then zooms in by the specified amount
     * 
     * @param p
     *            The point to center on.
     * @param percent
     *            The ammount to zoom in by.
     */
    public void zoomInOnPoint(GeoPoint p, double percent) {
        double per = getZoomAsPercent();
        if (debug)
            System.out.println("Zoom in from " + per);
        per += (percent / 100d) * per;
        if (debug)
            System.out.println("Zoom in to " + per);
        zoomOnPoint(p, per);
    }

    /**
     * Scales the map so that the given rectangle fills the viewer
     * 
     * @param bounds
     *            A Georectangle that defines the new visible extents
     */
    public void setMapExtent(GeoRectangle bounds) {
        setMapExtent(bounds, false);
    }

    /**
     * Scales the map so that the given rectangle fills the viewer
     * 
     * @param bounds
     *            A Georectangle that defines the new visible extents
     * @param quiet
     *            If quiet=true, then do not trigger a repaint
     */
    public void setMapExtent(GeoRectangle bounds, boolean quiet) {
        if (themeCount > 0) {
            scale.setMapExtent(bounds, quiet);

            // I commented out the following. When quiet it set=true, the
            // viewer should NOT be updated. If you want the viewer updated,
            // then set quiet=false.
            // Cameron Shorter 29/10/1
            //
            // I think this is needed to actually change the viewer! (says who?)
            // if(quiet)scaleChanged(new ScaleChangedEvent(scale,1.0));
        } else {
            fullMapExtent.add(bounds);
            setupScale();
        }
    }

    /**
     * Set the map extent to fit in all layers
     */
    public void setMapExtentFull() {
        setMapExtentFull(false);
    }

    /**
     * Set the map extent to fit in all layers
     */
    public void setMapExtentFull(boolean quiet) {
        if (themeCount > 0) {
            setMapExtent(fullMapExtent, quiet);
        }
    }

    /**
     * Scales the map by the given zoomfactor. The lower bound of zooming is the
     * full map containing all themes. there is no upper bound. this is probably
     * deprecated, and will be removed in the near future
     * 
     * @params zoomFactor A positive value means zoom out. A negative value
     *         means zoom in
     * @author Mathieu van Loon
     */
    public void setMapExtentByFactor(double zoomFactor) {
        if (themeCount > 0 && zoomFactor != 1) {
            GeoRectangle currentMap = scale.getMapExtent();
            double newX, newY, newWidth, newHeight;
            double tmp;

            tmp = (currentMap.height * zoomFactor) - currentMap.height;
            newHeight = currentMap.height + tmp;
            newY = currentMap.y - (tmp / 2);

            tmp = (currentMap.width * zoomFactor) - currentMap.width;
            newWidth = currentMap.width + tmp;
            newX = currentMap.x - (tmp / 2);
            if (zoomFactor < 1
                    || fullMapExtent.contains(newX, newY, newWidth, newHeight)) {
                scale.setMapExtent(new GeoRectangle(newX, newY, newWidth,
                        newHeight));
            } else {
                scale.setMapExtent(fullMapExtent);
            }
        }
    }

    /**
     * specify an absolute scaleFactor. This method is likely to changed in the
     * near future.
     * 
     * @author Mathieu van Loon
     */
    public void setMapExtentByValue(double scaleFactor) {
        if (themeCount > 0) {
            setMapExtentByFactor(scaleFactor / scale.getScaleFactor());
        }
    }

    /**
     * Set the map extent to fit all selected features
     * 
     * @since 0.6.5
     */
    public void setMapExtentSelected() {
        if (themeCount > 0) {
            GeoRectangle r = getSelectionMapExtent();
            if (r.getBounds().width > 0) {
                scale.setMapExtent(r);
            }
        }
    }

    /**
     * Set the scaler used to scale the map to and from the screen
     * <p>
     * probably not a good idea to use this unless you know what you are doing.
     * <p>
     * the setMapExtent is probaly what you are after
     */
    public void setScale(Scaler scale_) {
        scale.removeScaleChangedListener(this);
        this.scale = scale_;
        scale.addScaleChangedListener(this);
    }

    public void setProjection(Projection proj) {
        scale.setProjection(proj);
    }

    /**
     * Gets the scaler that is being used to transform the map to and from the
     * screen
     */
    public Scaler getScale() {
        return this.scale;
    }

    /**
     * Navigation tool methods
     * 
     * @deprecated see the new NavigateTool object for details
     */
    public void setNavigationBounds(GeoRectangle b) {
        if (tool instanceof NavigateTool) {
            ((NavigateTool) tool).setNavigationBounds(b);
        }
    }

    /**
     * Navigation tool methods
     * 
     * @deprecated see the new NavigateTool object for details
     */
    public GeoRectangle getNavigationBounds() {
        if (tool instanceof NavigateTool) {
            return ((NavigateTool) tool).getNavigationBounds();
        }
        return null;

    }

    /**
     * Navigation tool methods
     * 
     * @deprecated see the new NavigateTool object for details
     */
    public void setNavigationTarget(Viewer v) {
        if (tool instanceof NavigateTool) {
            if (debug)
                System.out.println("V--->Setting nav target in nav tool");
            ((NavigateTool) tool).setTarget(v);
        }

    }

    /**
     * set which tool mode we are using
     * 
     * @deprecated use setTool instead
     */
    public void setToolMode(int mode) {
        switch (mode) {
        case (SELECT):
            setTool(new SelectTool());
            break;
        case (ZOOM):
            setTool(new ZoomTool());
            break;
        case (PAN):
            setTool(new PanTool());
            break;
        case (NAVIGATE):
            setTool(new NavigateTool());
            break;
        }
    }

    /**
     * Sets the active tool for this viewer.<br>
     * Call this method to change the tool for this viewer, tools available by
     * default include ZoomTool,PanTool and SelectTool. <br>
     * an example call would be
     * <p>
     * view.setTool(new ZoomTool());
     * 
     * @author James Macgill JM
     * @since 0.7.7.2 June 6 2000
     * @param t
     *            The new tool to use.
     */
    public void setTool(Tool t) {
        tool = t;
        tool.setContext(this);
        setCursor(tool.getCursor());
    }

    /**
     * Returns the active tool being used by this viewer.
     */
    public Tool getTool() {
        return tool;
    }

    public void update(Graphics g) {
        paint(g);
    }

    public Vector getThemes() {

        ThemeStack.ThemeInfo[] infos = themeStack.getOrderedThemeInfos();
        Vector v = new Vector();
        for (int i = 0; i < infos.length; i++) {
            v.addElement(infos[i].getTheme());
        }
        return v;
    }

    /**
     * Set a Theme to be visable or not, this will trigger a repaint.
     * 
     * @param index
     *            the index of the theme that this command is refering to
     * @param flag
     *            if true then set the theme visible
     */
    public void setThemeIsVisible(int index, boolean flag) {
        themeStack.setIsVisible(themeStack.getThemeByWaight(index), flag);
    }

    public boolean isThemeVisible(Theme t) {
        return themeStack.isVisible(t);
    }

    /**
     * Set a Theme to be visable or not, this will trigger a repaint.
     * 
     * @param theme
     *            the theme that this command is refering to
     * @param flag
     *            if true then set the theme visible
     */
    public void setThemeIsVisible(Theme theme, boolean flag) {
        setThemeIsVisible(theme, flag, true);
    }

    /**
     * Set a Theme to be visable or not.
     * 
     * @param theme
     *            the theme that this command is refering to
     * @param flag
     *            if true then set the theme visible
     * @param update
     *            if true then trigger a repaint
     */
    public void setThemeIsVisible(Theme theme, boolean flag, boolean update) {
        if (theme == null || !staticThemes.contains(theme)) {
            return;
        }
        /*
         * if(flag && !visibleThemes.contains(theme)){
         * if(debug)System.out.println(theme+" added to vis");
         * visibleThemes.addElement(theme); } if(!flag &&
         * visibleThemes.contains(theme)){ if(debug)System.out.println(theme+"
         * removed from vis"); visibleThemes.removeElement(theme); }
         */
        themeStack.setIsVisible(theme, flag);
        if (update) {
            updateStaticBuffer();
            repaint();
            notifyCompositionChanged(CompositionChangedEvent.VISIBILITY);
        }
    }

    public Image getScreenBuffer() {
        return screenBuffer;
    }

    /**
     * Get the current mouse information. Covers information on the mouse
     * pointers position in screen, geographic and projected space as well as
     * the latest drag information in all three
     * 
     * @return MouseStatus the current status of the mouse pointer and drag
     *         regions
     */
    public MouseStatus getMouseStatus() {
        return mouseStatus;
    }

    /**
     * returns the real world values for the current x,y location of the mouse
     */
    public double[] getMapPoint() {
        return mouseStatus.map_xy;
    }

    /**
     * return the geographical bounds of the viewer
     */
    public GeoRectangle getFullMapExtent() {
        return fullMapExtent;
    }

    /**
     * returns the projected coordinate value for the current x,y location of
     * the mouse
     */
    public double[] getProjPoint() {
        return mouseStatus.proj_xy;
    }

    /**
     * returns the real world values for the current x,y location of the mouse
     * as a GeoPoint
     */
    public GeoPoint getMapGeoPoint() {
        return mouseStatus.getMapPoint();
    }

    /**
     * an internal convinence method, this simple returns a graphics object for
     * this viewer preset to XORMode.
     * 
     * @return Graphics a Graphics object for use by the update method in Tools.
     */
    public Graphics getToolGraphics() {
        toolGraphics = this.getGraphics();
        toolGraphics.setXORMode(Color.blue);
        return toolGraphics;
    }

    public void paint(java.awt.Graphics g) {

        if (debug)
            System.out.println("V--->Painting!");
        if (this.getBounds().width <= 0) {
            if (debug)
                System.out.println("V--->Viewer of zero Size");
            return;
        }
        if (themeCount > 0 && !lastSize.equals(this.getBounds())) {
            if (debug)
                System.out.println("V--->" + name
                        + "Hey!, who changed my size!");
            if (debug)
                System.out.println("V--->" + name + "Last " + lastSize);
            if (debug)
                System.out.println("V--->" + name + "New " + getBounds());
            Rectangle oldSize = new Rectangle(lastSize);
            setupScale();

            // EXPERIMENTAL CODE
            // if the new & the old size have the same width & height
            // there might be no reason to flush all the buffers.
            if (oldSize.width != getBounds().width
                    || oldSize.height != getBounds().height) {
                screenBuffer = null;
                panBuffer = null;
                selectionBuffer = null;
                if (staticBuffer != null) {
                    staticBuffer = null;
                }
                // moved by ian so rendering off screen will work
                updateStaticBuffer();
                if (sequenceTheme != null) {
                    createSequenceBuffer(sequenceTheme.length);
                }
            }
        }
        // Go through each of the themes, add them to the buffer and then plot
        // the buffer
        if (screenBuffer == null) {
            screenBuffer = this.createImage(getBounds().width,
                    getBounds().height);
        }
        if (screenBuffer == null)
            return;
        if (debug)
            System.out.println("V--> screenBuffer " + screenBuffer);
        Graphics sg = screenBuffer.getGraphics();
        if (debug)
            System.out.println("V--> staticBuffer " + staticBuffer);
        if (staticBuffer != null) {
            if (selectionChanged) {
                if (debug) {
                    System.out.println("V--->Call selections? ");
                }
                updateSelectionBuffer();
            }

            if (selectionBuffer != null) {
                sg.drawImage(selectionBuffer, 0, 0, this);
            }
        }

        // add sequeceBuffer frame x
        // try{

        if (sequenceBuffer != null && sequenceBuffer[frame] != null) {
            if (!sequenceFrameValid[frame]) {
                updateSequenceBuffer(frame);
            }
            sg.drawImage(sequenceBuffer[frame], 0, 0, this);
        }
        // sequenceTheme[frame].paintHighlight(sg,scale);//experiment
        // }catch(Exception e){}

        // add selections to the static buffer?
        // paintSelections(sg);

        paintHighlights(sg);

        // add animation on top
        if (animationTheme != null)
            animationTheme.paintScaled(sg, scale);

        // finaly add any tool related things
        if (showTips && mouseStatus.isMouseStill() && mouseStatus
                .isPointerInside()) {
            String tip = "";
            Enumeration e = visibleThemes.elements();
            while (e.hasMoreElements()) {
                Theme theme = (Theme) e.nextElement();
                if (isThemeVisible(theme)) {
                    String tempTip = (theme).getTipText(getMapGeoPoint(),
                            this.scale);
                    if (tempTip != null && !tempTip.trim().equals("")) {
                        tip = tempTip;
                    }
                }
            }
            if (animationTheme != null) {
                String tempTip = (animationTheme.getTipText(getMapGeoPoint(),
                        this.scale));
                if (tempTip != null && !tempTip.trim().equals("")) {
                    tip = tempTip;
                }
            }

            if (!tip.trim().equals("")) {
                paintTip(sg, tip);
            }
        }
        tool.paint(sg);

        // now we can plot to screen
        if (debug) {
            System.out.println("V--->Plot to the screen");
        }
        g.drawImage(screenBuffer, 0, 0, this);
    }

    /**
     * A quick method to handle the effect of a themes contemts changing
     * unfinished !!!
     */
    public void themeChanged(ThemeChangedEvent tce) {
        if (debug)
            System.out.println("V--->" + name + "Update for reason code "
                    + tce.getReason());
        if (tce.getReason() == tce.GEOGRAPHY) {
            if (debug)
                System.out.println("V--->" + name
                        + "Adding a theme to Bounds\n" + fullMapExtent);
            if (fullMapExtent.height == 0 || fullMapExtent.width == 0) {
                fullMapExtent.add(((Theme) tce.getSource()).getBounds());
                this.setMapExtentFull();
            } else {
                fullMapExtent.add(((Theme) tce.getSource()).getBounds());
            }
            if (debug)
                System.out.println("V--->" + name + "Added a theme to Bounds\n"
                        + fullMapExtent);
            updateStaticBuffer();
        }
        // scale.setMapExtent(
        if (tce.getReason() == tce.DATA || tce.getReason() == tce.SHADE) {
            updateStaticBuffer();
        }
        if (tce.getReason() == tce.SELECTION) {
            selectionChanged = true;
        }
        if (tce.getReason() == tce.ANIMATION) {
            // System.out.println("Animation Frame");
        }
        repaint();
    }

    public void repaint() {
        update(this.getGraphics());
    }

    public void paintTip(Graphics g, String s) {
        s = s.trim();
        int maxr = this.getBounds().width;
        int x = mouseStatus.screen_xy[0];
        int y = mouseStatus.screen_xy[1];
        FontMetrics fm = g.getFontMetrics();
        fm.stringWidth(s);
        int h = fm.getHeight();
        int xOffset = 5;
        int yOffset = -h - 2;
        int xPad = 10;
        int yPad = 4;
        x += xOffset;
        y += yOffset;
        if (x + fm.stringWidth(s) + xPad > maxr) {
            // System.out.print("Correcting "+x);
            int dif = maxr - (x + fm.stringWidth(s) + xPad);
            x = x + dif;
            // System.out.println(" "+x+" "+dif);
        }
        // System.out.println("Painting tip'"+s+"'");
        g.setColor(Color.black);
        g.drawRect(x, y, fm.stringWidth(s) + xPad, h + yPad);
        g.setColor(new Color(.8f, .8f, .4f));
        g.fillRect(x, y, fm.stringWidth(s) + xPad, h + yPad);
        g.setColor(Color.black);
        g.drawString(s, x + (xPad / 2), y + h - yPad / 2);
    }

    static public void main(String args[]) {
        class DriverFrame extends java.awt.Frame {
            public DriverFrame() {
                addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent event) {
                        dispose(); // free the system resources
                        System.exit(0); // close the application
                    }
                });
                setLayout(new java.awt.BorderLayout());
                setSize(300, 300);
                add(new Viewer());
            }
        }

        new DriverFrame().setVisible(true);
    }

    class ViewerMouse extends java.awt.event.MouseAdapter {
        public void mouseEntered(java.awt.event.MouseEvent event) {
            Object object = event.getSource();
            if (object == Viewer.this)
                Viewer_MouseEntered(event);
        }

        public void mouseClicked(java.awt.event.MouseEvent event) {
            Object object = event.getSource();
            if (object == Viewer.this)
                // notifyClickEvent();
                Viewer_MouseClicked(event);
        }

        public void mouseReleased(java.awt.event.MouseEvent event) {
            Object object = event.getSource();
            if (object == Viewer.this)
                Viewer_MouseRelease(event);
        }

        public void mouseExited(java.awt.event.MouseEvent event) {
            Object object = event.getSource();
            if (object == Viewer.this)
                Viewer_MouseExit(event);
        }
    }

    void Viewer_MouseExit(java.awt.event.MouseEvent event) {
        if (highlightMode == INSTANT)
            notifyHighlightPositionChanged(null);
        
    }

    class ViewerMouseMotion extends java.awt.event.MouseMotionAdapter {
        public void mouseMoved(java.awt.event.MouseEvent event) {
            Object object = event.getSource();
            if (object == Viewer.this)
                Viewer_MouseMove(event);
        }

        public void mouseDragged(java.awt.event.MouseEvent event) {
            if (themeCount < 1) {
                return;
            }
            tool.update(getToolGraphics(), tool.M_DRAG);
        }
    }

    void Viewer_MouseRelease(java.awt.event.MouseEvent event) {
        if (themeCount < 1) {
            return;
        }
        tool.update(getToolGraphics(), tool.M_RELEASE);
    }

    public void scaleChanged(ScaleChangedEvent sce) {
        Scaler s = (Scaler) sce.getSource();
        if (debug)
            System.out.println("V--->" + name
                    + "Viewer reports that scale has changed");
        if (s == scale) {
            if (debug)
                System.out.println("V--->" + name
                        + " updating due to scale change");

            updateStaticBuffer();
            updateSelectionBuffer();
            invalidateSequenceBuffer();
            update(this.getGraphics());
        }
    }

    void Viewer_MouseMove(java.awt.event.MouseEvent event) {
        if (themeCount > 0) {
            tool.update(getToolGraphics(), tool.M_MOVE);
            if (highlightMode == INSTANT)
                notifyHighlightPositionChanged(mouseStatus.getMapPoint());

        }
    }

    /**
     * three methods to suport IDChangedEvent notification to listeners
     */
    public synchronized void addIDChangedListener(IDChangedListener ice) {
        listeners.addElement(ice);
    }

    public synchronized void removeIDChangedListeners(IDChangedListener ice) {
        listeners.removeElement(ice);
    }

    protected void notifyIDChanged() {
        Vector l;
        IDChangedEvent ice = new IDChangedEvent(this, currentID);
        synchronized (this) {
            l = (Vector) listeners.clone();
        }

        for (int i = 0; i < l.size(); i++) {
            ((IDChangedListener) l.elementAt(i)).idChanged(ice);
        }
    }

    /**
     * Add Highlight Position Changed Listener
     */
    public synchronized void addHighlightPositionChangedListener(
            HighlightPositionChangedListener hpcl) {
        hpcListeners.addElement(hpcl);
    }

    public synchronized void removeHighlightPositionChangedListener(
            HighlightPositionChangedListener hpcl) {
        hpcListeners.removeElement(hpcl);
    }

    protected void notifyHighlightPositionChanged(GeoPoint p) {
        Vector l;
        if (debug)
            System.out.println("V--->Hilight note " + highlightMode);
        HighlightPositionChangedEvent hpce = new HighlightPositionChangedEvent(
                this, p);
        synchronized (this) {
            l = (Vector) hpcListeners.clone();
        }

        for (int i = 0; i < l.size(); i++) {
            ((HighlightPositionChangedListener) l.elementAt(i))
                    .highlightPositionChanged(hpce);
        }
        if (sequenceBuffer != null && sequenceBuffer[frame] != null) {
            ((HighlightPositionChangedListener) sequenceTheme[frame])
                    .highlightPositionChanged(hpce);
        }

    }

    /**
     * Add Selection Position Changed Listener
     */
    public synchronized void addSelectionPositionChangedListener(
            SelectionPositionChangedListener hpcl) {
        spcListeners.addElement(hpcl);
    }

    public synchronized void removeSelectionPositionChangedListener(
            SelectionPositionChangedListener hpcl) {
        spcListeners.removeElement(hpcl);
    }

    protected void notifySelectionPositionChanged(GeoPoint p) {
        if (debug)
            System.out
                    .println("V--->Notifying Selection Position changed listerners");
        Vector l;
        if (debug)
            System.out.println("V--->Hilight note " + highlightMode);
        SelectionPositionChangedEvent spce = new SelectionPositionChangedEvent(
                this, p);
        synchronized (this) {
            l = (Vector) spcListeners.clone();
        }

        for (int i = 0; i < l.size(); i++) {
            ((SelectionPositionChangedListener) l.elementAt(i))
                    .selectionPositionChanged(spce);
        }
        if (sequenceBuffer != null && sequenceBuffer[frame] != null) {
            ((SelectionPositionChangedListener) sequenceTheme[frame])
                    .selectionPositionChanged(spce);
        }

    }

    public synchronized void addCompositionChangedListener(
            CompositionChangedListener hpcl) {
        compositionChangedListeners.addElement(hpcl);
    }

    public synchronized void removeCompositionChangedListener(
            CompositionChangedListener ccl) {
        compositionChangedListeners.removeElement(ccl);
    }

    protected void notifyCompositionChanged(int reason) {
        if (debug)
            System.out
                    .println("V--->Notifying Composition  changed listerners");
        Vector l;
        if (debug)
            System.out.println("V--->Composition note " + highlightMode);
        CompositionChangedEvent cce = new CompositionChangedEvent(this, reason);
        synchronized (this) {
            l = (Vector) compositionChangedListeners.clone();
        }

        for (int i = 0; i < l.size(); i++) {
            ((CompositionChangedListener) l.elementAt(i))
                    .compositionChanged(cce);
        }

    }

    public ThemePanel getThemePanel() {
        ThemePanel tp = new ThemePanel(getThemes(), this);
        addCompositionChangedListener(tp);
        return tp;
    }

    /**
     * Methods to suport ViewerClickedEvent notification to listeners
     */
    public synchronized void addViewerClickedListener(ViewerClickedListener vcl) {
        clickedListeners.addElement(vcl);
    }

    /**
     * Methods to suport ViewerClickedEvent notification to listeners
     */
    public synchronized void removeViewerClickedListener(
            ViewerClickedListener vcl) {
        clickedListeners.removeElement(vcl);
    }

    protected void notifyClickEvent() {

        Vector list;
        ViewerClickedEvent vce = new ViewerClickedEvent(this, new GeoPoint(
                mouseStatus.getMapPoint()));
        synchronized (this) {
            list = (Vector) clickedListeners.clone();
        }

        for (int i = 0; i < list.size(); i++) {
            ((ViewerClickedListener) list.elementAt(i)).viewerClicked(vce);
        }
    }

    void Viewer_MouseClicked(java.awt.event.MouseEvent event) {
        notifyClickEvent();
        tool.update(getToolGraphics(), tool.M_CLICK);
        if (highlightMode == Viewer.REQUEST)
            notifyHighlightPositionChanged(mouseStatus.getMapPoint());
    }

    void Viewer_MouseEntered(java.awt.event.MouseEvent event) {
        // to do: code goes here.
    }

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public synchronized void addSelectionRegionChangedListener(
            SelectionRegionChangedListener hpcl) {
        srcListeners.addElement(hpcl);
    }

    public synchronized void removeSelectionRegionChangedListener(
            SelectionRegionChangedListener hpcl) {
        srcListeners.removeElement(hpcl);
    }

    /**
     * a special selection region changed listener that will be notifed only
     * after all other region changed listeners have been notified...
     */
    public void setFinalSelectionRegionChangedListener(
            SelectionRegionChangedListener selectionregionchangedlistener) {
        srcFinal = selectionregionchangedlistener;
    }

    protected void notifySelectionRegionChanged(GeoRectangle r) {
        Vector l;
        // if(debug)System.out.println("V--->Hilight note "+highlightMode);
        SelectionRegionChangedEvent hpce = new SelectionRegionChangedEvent(
                this, r);
        synchronized (this) {
            l = (Vector) srcListeners.clone();
        }

        for (int i = 0; i < l.size(); i++) {
            ((SelectionRegionChangedListener) l.elementAt(i))
                    .selectionRegionChanged(hpce);
        }
        if (sequenceBuffer != null && sequenceBuffer[frame] != null) {
            ((SelectionRegionChangedListener) sequenceTheme[frame])
                    .selectionRegionChanged(hpce);
        }
        if (srcFinal != null)
            srcFinal.selectionRegionChanged(hpce);

    }

    public void finalize() {
        if (debug)
            System.out.println("V--->Cleaning up");
        mouseStatus = null;
    }

    class ComponentAdapt extends java.awt.event.ComponentAdapter {
        boolean done = false;

        /**
         * The first time the viewer appears on the screen it is often empty.
         * Once a zoom, reset or pan has been performed the map appears
         * properly. The reasons for this bug are unclear but have something to
         * do with the viewer setting up the scaler before it exists on the
         * screen, making caluclations based on having zero size. This method
         * gets called when a component is resized, this includes its first
         * appearence on the screen. As it stands it forces a repaint() of the
         * viewer and updates the static buffers. This is slow and need not
         * hapen again after the viewer has been displayed for the first time,
         * as internal methods handle the situation if the viewer is resized
         * after its first apperence.
         * <p>
         * This is not a good fix, but it does appear to work.<br>
         */
        public void componentResized(java.awt.event.ComponentEvent event) {
            if (done)
                return;
            updateStaticBuffer();
            repaint();
            done = true;
        }
    }

    public void setThemeToBottom(Theme t) {
        themeStack.setToBottom(t);
        updateStaticBuffer();// this needs to be automated
        notifyCompositionChanged(CompositionChangedEvent.ORDER);
        repaint();
    }

    public void setThemeWaighting(Theme t, int waight) {
        themeStack.setWaight(t, waight);
        updateStaticBuffer();// this needs to be automated
        notifyCompositionChanged(CompositionChangedEvent.ORDER);
        repaint();
        // themeWaights.remove(t);
        // themeWaights.put(t,new Integer(waight));
    }

    public int getThemeWaighting(Theme t) {
        return themeStack.getWaight(t);
        // return ((Integer)themeWaights.get(t)).intValue();
    }

    public void swapThemes(Theme a, Theme b) {
        themeStack.swapThemes(a, b);
        updateStaticBuffer();// this needs to be automated
        repaint();
        notifyCompositionChanged(CompositionChangedEvent.ORDER);
    }

    public void swapThemes(int a, int b) {
        themeStack.swapThemes(a, b);
        updateStaticBuffer();// this needs to be automated
        repaint();
        notifyCompositionChanged(CompositionChangedEvent.ORDER);
    }

}

package de.argumap.gt_extensions;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import uk.ac.leeds.ccg.geotools.LineLayer;
import uk.ac.leeds.ccg.geotools.PointLayer;
import uk.ac.leeds.ccg.geotools.PolygonLayer;
import uk.ac.leeds.ccg.geotools.ScaleChangedEvent;
import uk.ac.leeds.ccg.geotools.ScaleChangedListener;
import uk.ac.leeds.ccg.geotools.Scaler;
import uk.ac.leeds.ccg.geotools.Tool;
import uk.ac.leeds.ccg.geotools.Viewer;
import de.argumap.UI.ArguMapWindow;

/**
 * A simple toolbar to save me (ian) adding all this code to control a viewer
 * everytime I write a quick applet, then I decided it might be useful to
 * others. Simply construct with the viewer to control and a boolean to decide
 * if you want select enabled in the bar now makes use of tools rather than the
 * deprecated int method in viewer. 23/11/00 New version makes use of tool's new
 * found ability to know thier own name to construct the toolbar. 28/11/00 added
 * support for linking viewers together with a single tool bar 25/6/01
 */
public class ArguMapToolBar extends Panel implements ActionListener,
        ItemListener, ScaleChangedListener, MouseListener {
    static final boolean DEBUG = true;
    static final String DBC = "TB >";
    Vector<Viewer> viewers = new Vector<Viewer>();
    Viewer v;
    int views = 1;
    boolean selection = false;
    boolean checkbox = false;
    public CheckboxGroup cbg = new CheckboxGroup();
    static final int MAX_VIEWS = 5;
    Vector tools[] = new Vector[MAX_VIEWS];
    JButton resetButton, zoomButton, referenceButton, selectButton,
            pointButton, lineButton, polygonButton;
    Vector controls = new Vector();
    private ArguMapWindow window;

    PointLayer userPoints = new PointLayer();
    LineLayer userLines = new LineLayer();
    PolygonLayer userPolygons = new PolygonLayer();
    
    private static String LOGINTIP = "Please log in to use this tool.";

    /**
     * Custom toolbar for the ArguMapApplet.
     * 
     * @param view
     * @param tools2
     * @param window
     */
    public ArguMapToolBar(Viewer ve, ArguMapWindow window) {
        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.LEFT);
        this.setLayout(fl);
        // init tools - reading tools first
        ArguMapZoomTool zoom = new ArguMapZoomTool(window);
        ArguMapPanTool pan = new ArguMapPanTool(window);
        ArguMapContributionSelectionTool sel = new ArguMapContributionSelectionTool(
                window);
        // "writing" tools
        ArguMapSelectReferenceTool ref = new ArguMapSelectReferenceTool(window,
                userPoints, userLines, userPolygons);
        ArguMapReferencePointTool newPoint = new ArguMapReferencePointTool(
                window, userPoints);
        ArguMapReferenceLineTool newLine = new ArguMapReferenceLineTool(window,
                userLines);
        ArguMapReferencePolygonTool newPolygon = new ArguMapReferencePolygonTool(
                window, userPolygons);

        viewers.addElement(ve);
        ve.getScale().addScaleChangedListener(this);
        tools[0] = new Vector();
        views = 0;
        resetButton = new JButton(new ImageIcon(ArguMapToolBar.class
                .getResource("img/reset.gif")));
        resetButton.setToolTipText("Reset");
        resetButton.addMouseListener(this);
        controls.addElement(resetButton);
        add(resetButton);

        addTool(zoom);
        addTool(pan);
        addTool(sel);
        addTool(ref);
        addTool(newPoint);
        addTool(newLine);
        addTool(newPolygon);

        resetButton.addActionListener(this);

        this.window = window;
        enableLoggedInTools(false);
        // the zoom tool is active on start-up
        ActionEvent e = new ActionEvent(zoomButton, 1, "cmd");
        this.actionPerformed(e);
    }

    public void addViewer(Viewer ve) {
        Tool t;
        viewers.addElement(ve);
        ve.getScale().addScaleChangedListener(this);
        tools[++views] = new Vector();
        for (int i = 0; i < tools[0].size(); i++) {
            Class c = tools[0].elementAt(i).getClass();
            try {
                tools[views].addElement(t = (Tool) c.newInstance());
                ve.setTool(t);
            } catch (Exception e) {
                System.out.println(DBC + "Exception " + e);
            }
        }
    }

    private void addTool(Tool t) {
        tools[views].addElement(t);
        if (!checkbox) {
            JButton b;
            if (t instanceof ArguMapZoomTool) {
                b = new JButton(new ImageIcon(ArguMapToolBar.class
                        .getResource("img/zoom.gif")));
                zoomButton = b;
            } else if (t instanceof ArguMapPanTool) {
                b = new JButton(new ImageIcon(ArguMapToolBar.class
                        .getResource("img/pan.gif")));
            } else if (t instanceof ArguMapSelectReferenceTool) {
                b = new JButton(new ImageIcon(ArguMapToolBar.class
                        .getResource("img/reference.gif")));
                referenceButton = b;
            } else if (t instanceof ArguMapContributionSelectionTool) {
                b = new JButton(new ImageIcon(ArguMapToolBar.class
                        .getResource("img/select.gif")));
                selectButton = b;
            } else if (t instanceof ArguMapReferenceLineTool) {
                b = new JButton(new ImageIcon(ArguMapToolBar.class
                        .getResource("img/newline.gif")));
                lineButton = b;
            } else if (t instanceof ArguMapReferencePointTool) {
                b = new JButton(new ImageIcon(ArguMapToolBar.class
                        .getResource("img/newpoint.gif")));
                pointButton = b;
            } else if (t instanceof ArguMapReferencePolygonTool) {
                b = new JButton(new ImageIcon(ArguMapToolBar.class
                        .getResource("img/newpolygon.gif")));
                polygonButton = b;
            } else {
                b = new JButton("error");
            }
            b.setToolTipText(t.getName());
            b.addMouseListener(this);
            controls.addElement(b);
            add(b);
            b.addActionListener(this);
        } else {
            Checkbox c = new Checkbox(t.getName(), true, cbg);
            controls.addElement(c);
            add(c);
            c.addItemListener(this);
        }
        for (int i = 0; i < viewers.size(); i++) {
            ((Viewer) viewers.elementAt(i)).setTool(t);
        }
    }

    public Vector getControls() {
        return controls;
    }

    public void actionPerformed(ActionEvent e) {
        // System.out.println(e.getSource().getClass().toString());
        JButton source = (JButton) e.getSource();
        String cmd = source.getToolTipText();
        // aktiven button farblich hervorheben -
        // aber zuerst alle wieder auf grau setzen
        // und das gaze nur, wenn nicht auf reset geklickt wurde!
        if (!cmd.equals("Reset")) {
            Iterator iter = controls.iterator();
            while (iter.hasNext()) {
                JButton current = (JButton) iter.next();
            }
        }

        if (cmd.equals("Reset")) {
            for (int i = 0; i < viewers.size(); i++) {
                ((Viewer) viewers.elementAt(i)).setMapExtentFull(false);
                ((Viewer) viewers.elementAt(i)).repaint();
                ((Viewer) viewers.elementAt(i)).getParent().repaint();
            }
            window.reloadWMC();
            return;
        }
        Tool t;
        for (int i = 0; i < tools[0].size(); i++) {
            t = (Tool) tools[0].elementAt(i);
            if (cmd.equals(t.getName())) {
                for (int j = 0; j < viewers.size(); j++) {
                    if (DEBUG)
                        System.out.println(DBC + "setting "
                                + (Viewer) viewers.elementAt(j) + " to "
                                + (Tool) tools[j].elementAt(i));
                    ((Viewer) viewers.elementAt(j)).setTool((Tool) tools[j]
                            .elementAt(i));
                }
                return;
            }
        }
    }

    public void itemStateChanged(ItemEvent e) {
        String cmd = (String) e.getItem();
        Tool t;
        for (int i = 0; i < tools[0].size(); i++) {
            t = (Tool) tools[0].elementAt(i);
            if (cmd.equals(t.getName())) {
                for (int j = 0; j < viewers.size(); j++) {
                    if (DEBUG)
                        System.out.println(DBC + "setting "
                                + (Viewer) viewers.elementAt(j) + " to "
                                + (Tool) tools[j].elementAt(i));
                    ((Viewer) viewers.elementAt(j)).setTool((Tool) tools[j]
                            .elementAt(i));
                }
                return;
            }
        }
    }

    public void scaleChanged(ScaleChangedEvent sce) {
        /**
         * find the source and then tell all the OTHER viewers to QUIETLY change
         * mapextent
         */
        Scaler source = (Scaler) sce.getSource();
        if (DEBUG)
            System.out.println(DBC + "Scale change source " + source);
        for (int j = 0; j < viewers.size(); j++) {
            if (((Viewer) viewers.elementAt(j)).getScale() != source) {
                if (DEBUG)
                    System.out.println(DBC + "Sending scale change " + " to "
                            + (Viewer) viewers.elementAt(j));
                ((Viewer) viewers.elementAt(j)).setMapExtent(source
                        .getMapExtent(), true);
            }
        }
    }

    /**
     * This method returns the first WMCReferenceTool in this Toolbar. (It
     * actually makes no sense to have more than one...). However, if there is
     * no WMCReferenceTool in this Toolbar, it will return null, so be careful!
     * 
     * @return The (first) WMCReferenceTool in this Toolbar.
     */
    public ArguMapSelectReferenceTool getWMCReferenceTool() {
        Vector toolz = tools[views];
        Iterator iter = toolz.iterator();
        while (iter.hasNext()) {
            Tool current = (Tool) iter.next();
            if (current instanceof ArguMapSelectReferenceTool) {
                return (ArguMapSelectReferenceTool) current;
            }
        }
        return null;
    }

    /**
     * Switches the tools in the toolbar that can only be used when logged in on
     * or off. Called when the user logs in / out. Leaves just the zoom and pan
     * tools active.
     * 
     * @param enable -
     *            Determines whether to enable the tools or not.
     */
    public void enableLoggedInTools(boolean enable) {

        // we have to check some things in case the tools are deactivated:
        if (enable == false) {
            // TODO find out whether one of the tools that are going to be
            // deactivated is currently active
            // in this case, the zoomTool should be activated

            // TODO check whether any objects have been referenced by the user
            // and not send to the database
            // the user should be warned with a popup - if the user continues,
            // the referenced objects will be
            // deleted, otherwise he will not be logged out, so that he can
            // submit his reference objects
            // deactivate the tools used when writing new contributions:
            
            // set tool tips:
            referenceButton.setToolTipText(LOGINTIP);
            pointButton.setToolTipText(LOGINTIP);
            lineButton.setToolTipText(LOGINTIP);
            polygonButton.setToolTipText(LOGINTIP);
        }else{
            // set tool tips:
            referenceButton.setToolTipText(ArguMapSelectReferenceTool.NAME);
            pointButton.setToolTipText(ArguMapReferencePointTool.NAME);
            lineButton.setToolTipText(ArguMapReferenceLineTool.NAME);
            polygonButton.setToolTipText(ArguMapReferencePolygonTool.NAME);
        }
        referenceButton.setEnabled(enable);
        pointButton.setEnabled(enable);
        lineButton.setEnabled(enable);
        polygonButton.setEnabled(enable);

    }

    public void flushUserReferences() {
        userPoints.deleteAllPoints();
        userLines.deleteAllLines();
        userPolygons.deleteAllPolygons();

    }

    public void mouseEntered(MouseEvent e) {
        JButton source = (JButton) e.getSource();
        String cmd = source.getToolTipText();
        if (cmd.equals(ArguMapContributionSelectionTool.NAME)) {
            window.getStatusInformationPanel().showInfo(
                    ArguMapContributionSelectionTool.DESCRIPTION);
        } else if (cmd.equals(ArguMapPanTool.NAME)) {
            window.getStatusInformationPanel().showInfo(
                    ArguMapPanTool.DESCRIPTION);
        } else if (cmd.equals(ArguMapReferenceLineTool.NAME)) {
            window.getStatusInformationPanel().showInfo(
                    ArguMapReferenceLineTool.DESCRIPTION);
        } else if (cmd.equals(ArguMapReferencePointTool.NAME)) {
            window.getStatusInformationPanel().showInfo(
                    ArguMapReferencePointTool.DESCRIPTION);
        } else if (cmd.equals(ArguMapReferencePolygonTool.NAME)) {
            window.getStatusInformationPanel().showInfo(
                    ArguMapReferencePolygonTool.DESCRIPTION);
        } else if (cmd.equals(ArguMapSelectReferenceTool.NAME)) {
            window.getStatusInformationPanel().showInfo(
                    ArguMapSelectReferenceTool.DESCRIPTION);
        } else if (cmd.equals(ArguMapZoomTool.NAME)) {
            window.getStatusInformationPanel().showInfo(
                    ArguMapZoomTool.DESCRIPTION);
        }
    }

    public void mouseExited(MouseEvent arg0) {
        // set back status info panel to previous state:
        window.getStatusInformationPanel().rollback();
    }

    // The following methods do nothing - just added them to implement the
    // interface
    public void mouseClicked(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent arg0) {
    }

    public void mouseReleased(MouseEvent arg0) {
    }

}

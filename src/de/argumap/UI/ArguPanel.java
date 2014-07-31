package de.argumap.UI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoLine;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoPolygon;
import uk.ac.leeds.ccg.geotools.GeoRectangle;
import uk.ac.leeds.ccg.geotools.GeoShape;
import uk.ac.leeds.ccg.geotools.SimpleGeoData;
import de.argumap.UI.treetable.DiscussionTreeModel;
import de.argumap.UI.treetable.JTreeTable;
import de.argumap.UI.treetable.TreeTableModelAdapter;
import de.argumap.UI.treetable.JTreeTable.TreeTableCellRenderer;
import de.argumap.discussion.Contribution;

/**
 * @author Carsten Keßler, carsten.kessler@uni-muenster.de
 * @version 25.10.2004
 * 
 */
public class ArguPanel extends JPanel implements TreeSelectionListener,
        ActionListener {

    private static String ANSWERTIP = "Answer to the contribution "
            + "which is currently selected in the discussion tree.";
    private static String NEWTHREADTIP = "Start a new thread on a new topic.";
    private static String LOGINTIP = "Please log in to activate this tool.";

    private JTextArea messagePane;
    private JEditorPane infoPane;
    private CardPanel container;
    private JPanel buttons;
    private JButton answer, newThread;
    private boolean nodeSelected, willInvokeMultipleSelection;
    private JScrollPane treeView;
    private JTreeTable treeTable;
    private JCheckBox zoomToContext;
    private MatchIndicator matchIndicator;

    public ArguPanel(CardPanel container) {

        willInvokeMultipleSelection = false;
        nodeSelected = false;
        this.container = container;
        setLayout(new BorderLayout());
        // panel mit den buttons aufbauen:
        buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        answer = new JButton("Answer");
        answer.setToolTipText(LOGINTIP);
        newThread = new JButton("Start new thread");
        newThread.setToolTipText(LOGINTIP);
        // discussion tools are deactivated if no one is logged in:
        if (container.getArguMapWindow().getLoggedIn() == null) {
            setDiscussEnabled(false);
        } else {
            setDiscussEnabled(true);
        }
        answer.addActionListener(this);
        newThread.addActionListener(this);
        buttons.setBackground(Color.white);
        buttons.add(answer);
        buttons.add(newThread);

        treeTable = new JTreeTable(new DiscussionTreeModel(container
                .getArguMapWindow()));
        TreeTableCellRenderer ttcr = treeTable.getTreeTableCellRenderer();
        ttcr.setRootVisible(false);
        ttcr.addTreeSelectionListener(this);

        treeView = new JScrollPane(treeTable);
        infoPane = new JEditorPane();
        infoPane.setEditable(false);
        infoPane.setBackground(Color.lightGray);

        // Create the message viewing pane.
        messagePane = new JTextArea();
        messagePane.setEditable(false);
        messagePane.setLineWrap(true);
        messagePane.setWrapStyleWord(true);
        messagePane.setFont(infoPane.getFont().deriveFont(12.0f));
        JScrollPane messageScrollPane = new JScrollPane(messagePane);

        JPanel msgViewer = new JPanel();
        msgViewer.setLayout(new BorderLayout());
        msgViewer.add(infoPane, "North");
        msgViewer.add(messageScrollPane, "Center");

        JPanel treeAndOptions = new JPanel(new BorderLayout());
        zoomToContext = new JCheckBox("Reload author's map configuration",
                false);
        zoomToContext.addActionListener(this);
        treeAndOptions.add(treeView, "Center");
        treeAndOptions.add(zoomToContext, "South");

        // initialize the matchIndicator
        matchIndicator = new MatchIndicator(this);
        treeAndOptions.add(matchIndicator, "East");

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                treeAndOptions, msgViewer);

        treeAndOptions.setPreferredSize(new Dimension(100, 300));

        add(splitPane, "Center");
        add(buttons, "South");
        
        openTree();
    }

    /**
     * Indicates whether the user wants the map configurations associated with
     * the contributions.
     * 
     * @return True, if the according checkbox is checked.
     */
    public boolean shallLoadContext() {
        return zoomToContext.isSelected();
    }

    public Vector getAllContributions() {
        return treeTable.getTreeTableModelAdapter().getNodes();
    }

    public void valueChanged(TreeSelectionEvent e) {

        Contribution node = (Contribution) treeTable.getTreeTableCellRenderer()
                .getLastSelectedPathComponent();
        if (node == null)
            return;

        // show message, infostring + references only if just one path is
        // selected in the tree:
        if (!willInvokeMultipleSelection) {
            displayContribution(node);
            // ...show info:
            infoPane.setText(node.getInfoString());
            highlightReferences(node);
            // update the statspanel, so that the stats of the references of
            // this contribution are shown:

            ArrayList references = node.getReferenceObjects();
            int[] refIDs = new int[references.size()];
            for (int i = 0; i < refIDs.length; i++) {
                refIDs[i] = ((GeoShape) references.get(i)).getID();
            }

            SearchAndAnalyzePanel ap = (SearchAndAnalyzePanel) container
                    .getArguMapWindow().getTabs().getComponentAt(2);
            ap.updateStats(refIDs);

            // load context for this contribution, if the corresponding check
            // box is activated:
            if (shallLoadContext()) {
                loadContext(node);
            }
        } else {
            // clear everything if more than one path is highlighted
            messagePane.setText("");
            infoPane.setText("");
            // deactivate the answer-button:
            answer.setEnabled(false);
            // clear the selection so that nothing is highlighted:
            clearHighlightedReferences();
        }
        nodeSelected = true;
        // activate the answer button if a user is loggedd in:
        if (container.getArguMapWindow().getLoggedIn() != null
                && !willInvokeMultipleSelection) {
            answer.setEnabled(true);
        }
    }

    /**
     * Removes the highlighting from the references layers. Called when more
     * than one contribution is selected in the tree.
     * 
     */
    private void clearHighlightedReferences() {
        container.getArguMapWindow().getPointsFromDB().getSelectionManager()
                .clearSelection();
        container.getArguMapWindow().getLinesFromDB().getSelectionManager()
                .clearSelection();
        container.getArguMapWindow().getPolygonsFromDB().getSelectionManager()
                .clearSelection();
    }

    /**
     * Loads the corrsponding Wep Map Context for the node into the server. The
     * server returns the new bounding box for the map, and the map viewer is
     * updated with these coordinates.
     * 
     * @param node
     */
    private void loadContext(Contribution node) {
        try {
            // load WMC document into the core
            URL loadContextURL = new URL(container.getArguMapWindow()
                    .getServletBase()
                    + "loadcontext;jsessionid="
                    + container.getArguMapWindow().getSessionID()
                    + "?id="
                    + node.getContributionID());
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(loadContextURL
                    .openStream()));
            // the servlet returns the new bounding box. read it:
            String box = in.readLine();
            String[] boxCoords = box.split(" : ");
            double minX = (new Double(boxCoords[0])).doubleValue();
            double maxX = (new Double(boxCoords[1])).doubleValue();
            double minY = (new Double(boxCoords[2])).doubleValue();
            double maxY = (new Double(boxCoords[3])).doubleValue();
            double width = maxX - minX;
            double height = maxY - minY;
            // set the scaler accordingly
            GeoRectangle newBox = new GeoRectangle(minX, minY, width, height);
            container.getArguMapWindow().getViewer().scale.setMapExtent(newBox);
            in.close();
            // update layerselectionpanel
            container.getArguMapWindow().updateLayerSelectionPanel();
            // reload WMS map:
            container.getArguMapWindow().reloadWMC();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * @param msg
     */
    private void displayContribution(Contribution con) {
        if (con != null) {
            messagePane.setText(con.getMsg() + "\n \n ");
        } else {
            messagePane.setText("File Not Found");
        }
    }

    /**
     * Sets the JscrollPane's ViewPort so that the path (i.e. its root) is
     * visible for the user. That means that the ViewPort only changes when the
     * path is not visible.
     * 
     * @param path -
     *            The TreePath that should be visible in the ViewPort.
     */
    public void scrollToPath(TreePath path) {
        // dermine the size of the current view port
        Rectangle currentView = treeView.getViewport().getViewRect();
        int minY = currentView.y;
        int maxY = currentView.y + currentView.height;

        // determine the top left corner of the path
        TreeTableCellRenderer ttcr = treeTable.getTreeTableCellRenderer();
        Rectangle box = ttcr.getPathBounds(path);

        if (minY > box.y || maxY < box.y) {
            treeView.getViewport().setViewPosition(new Point(0, box.y));
        }
    }

    /**
     * Highlights the reference objects referenced by contribution on the map by
     * putting them in the selection of the corresponding map layers.
     * 
     * @param contribution -
     *            This contribution's reference objects will be highlighted.
     */
    private void highlightReferences(Contribution contribution) {

        // first make sure that all the previous selections are deleted:
        clearHighlightedReferences();

        Iterator iter = contribution.getReferenceObjects().iterator();

        while (iter.hasNext()) {
            Object current = iter.next();
            if (current instanceof GeoPoint) {
                container.getArguMapWindow().getPointsFromDB()
                        .getSelectionManager().addToSelection(
                                ((GeoPoint) current).getID());
            } else if (current instanceof GeoLine) {
                container.getArguMapWindow().getLinesFromDB()
                        .getSelectionManager().addToSelection(
                                ((GeoLine) current).getID());
            } else { // polygon
                container.getArguMapWindow().getPolygonsFromDB()
                        .getSelectionManager().addToSelection(
                                ((GeoPolygon) current).getID());
            }
        }
    }

    /**
     * @return Returns the ArguPanel's container.
     */
    public CardPanel getContainer() {
        return container;
    }

    /**
     * Attention! This one returns null if no contribution in the tree is
     * selected.
     * 
     * @return The contribution-object that is associated with the tree-node
     *         that is currently highlighted / selected.
     */
    public Contribution getSelectedContribution() {
        Contribution node = (Contribution) treeTable.getTreeTableCellRenderer()
                .getLastSelectedPathComponent();
        // return the object tied to the node which is currently highlighted
        if (node == null) {
            return null; // if no node is selected
        } else {
            return node;
        }
    }

    /**
     * Determins whether the buttons that allow the user to answer a topic or
     * start a new thread should be enabled. Usually called when a user logs in /
     * out.
     * 
     * @param enabled
     */
    public void setDiscussEnabled(boolean enabled) {

        if (enabled) {
            newThread.setToolTipText(NEWTHREADTIP);
            newThread.setEnabled(true);
            answer.setToolTipText(ANSWERTIP);
            // check whether a node is selected in the tree - otherwise, the
            // answer-button must not be activated!
            if (nodeSelected) {
                answer.setEnabled(true);
            }

        } else {
            newThread.setToolTipText(LOGINTIP);
            newThread.setEnabled(false);
            answer.setToolTipText(LOGINTIP);
            answer.setEnabled(false);
        }
    }

    /**
     * Called when "send" or "cancel" are clicked.
     */
    public void actionPerformed(ActionEvent e) {
        SendPanel sendPanel = container.getSendPanel();
        CardPanel container = sendPanel.getContainer();
        CardLayout cl = (CardLayout) container.getLayout();

        if (e.getActionCommand() == "Answer") {
            // tell sendPanel to answer contribution
            sendPanel.setParentContribution(container.getArguPanel()
                    .getSelectedContribution());
            sendPanel.setHasParentContribution(true);
            highlightReferences(new Contribution(container.getArguMapWindow()));
            cl.show(container, "sendPanel");
        } else if (e.getActionCommand() == "Start new thread") {
            // tell SendPanel to start a new thread
            sendPanel.setHasParentContribution(false);
            highlightReferences(new Contribution(container.getArguMapWindow()));
            cl.show(container, "sendPanel");
        } else if (e.getActionCommand() == zoomToContext.getText()) {
            // check whether ONE contribution is selected:
            TreeTableCellRenderer ttcr = treeTable.getTreeTableCellRenderer();
            try {
                if (ttcr.getSelectionPaths().length == 1) {
                    // dann den context nachladen
                    Contribution node = (Contribution) ttcr
                            .getLastSelectedPathComponent();
                    loadContext(node);
                } // noithing happens if more than 1 contributions are
                // selected
            } catch (NullPointerException n) { // in case NO contribution is
                // selected
                System.out
                        .println("can't load context - no contribution selected!");
            }
        }

    }

    /**
     * Highlights the contributions in the discussion tree.
     * 
     * @param contributions
     */
    public void highlightContributions(Contribution[] contributions) {

        if (contributions.length > 1) {
            willInvokeMultipleSelection = true;
        } else {
            willInvokeMultipleSelection = false;
        }
        TreeTableModelAdapter ttma = treeTable.getTreeTableModelAdapter();
        TreeTableCellRenderer ttcr = treeTable.getTreeTableCellRenderer();
        // clear previous selection:
        ttcr.clearSelection();
        // expand paths to the nodes so that they are visible if they are not
        // on the top level:
        for (int i = 0; i < contributions.length; i++) {
            Contribution current = contributions[i];
            TreePath tp = ttma.getPathToNode(current);
            ttcr.makeVisible(tp);
            // add path to selection:
            ttcr.addSelectionPath(tp);
            // in case this value change is caused by a search operation,
            // it might be the case that the node has been selected is not
            // visible in the JScrollPane.
            // so let's move the pane's ViewPort, but only if it's the first
            // node:
            if (i == 0) {
                scrollToPath(tp);
            }
        }
        willInvokeMultipleSelection = false;

        // update the matchIndicator
        matchIndicator.update(contributions);
    }


    /**
     * opens the whole discussion tree.
     * 
     * @param contributions
     */
    public void openTree() {
        TreeTableModelAdapter ttma = treeTable.getTreeTableModelAdapter();
        TreeTableCellRenderer ttcr = treeTable.getTreeTableCellRenderer();
        
        Object[] contributions = getAllContributions().toArray();
        
        // expand paths to the nodes so that they are visible if they are not
        // on the top level:
        for (int i = 0; i < contributions.length; i++) {
            Contribution current = (Contribution) contributions[i];
            TreePath tp = ttma.getPathToNode(current);
            ttcr.makeVisible(tp);            
        }        
    }
    
    /**
     * Retrieves a contribution by its ID.
     * 
     * @param contributionID
     * @return The contribution with contributionID. If there is no contribution
     *         with this ID, null is returned.
     */
    public Contribution getContributionByID(int contributionID) {
        Iterator iter = getAllContributions().iterator();
        while (iter.hasNext()) {
            Contribution current = (Contribution) iter.next();
            if (contributionID == current.getContributionID()) {
                return current;
            }
        }
        return null;
    }

    public JTreeTable getTreeTable() {
        return treeTable;
    }

    /**
     * Shows only the solutions in the array.
     * 
     * @param contributions
     */
    // public void filterContributions(Contribution[] contributions){
    // //TODO
    // }
    public GeoData getGeoData() {
        // speichert die referenzobjekte:
        ArrayList<GeoShape> refs = new ArrayList<GeoShape>();
        // speichert, wie oft auf das entsprechende objekt aus der
        // anderen arraylist referenziert wird:
        ArrayList<Double> numberOfReferences = new ArrayList<Double>();
        Vector allCons = getAllContributions();
        Iterator iter = allCons.iterator();
        // all beiträge durchgehen
        while (iter.hasNext()) {
            Contribution current = (Contribution) iter.next();
            Iterator conIter = current.getReferenceObjects().iterator();
            // alle referenzobjekte des aktuellen beitrags durchgehen
            while (conIter.hasNext()) {
                GeoShape currentShape = (GeoShape) conIter.next();
                // wenn der shape noch nicht in der ArrayList ist, hinzufügen:
                if (!refs.contains(currentShape)) {
                    refs.add(currentShape);
                    // und den entsprechenden zähler mit 1 initialisieren:
                    numberOfReferences.add(new Double(1));
                } else {
                    int index = refs.indexOf(currentShape);
                    Double count = (Double) numberOfReferences.get(index);
                    int c = count.intValue();
                    c++;
                    numberOfReferences.set(index, new Double(c));
                }
            }
        }

        // ausgabe vorbereiten:
        SimpleGeoData gd = new SimpleGeoData();
        Iterator refI = refs.iterator();
        Iterator countI = numberOfReferences.iterator();
        while (refI.hasNext()) {
            int index = ((GeoShape) refI.next()).getID();
            double value = ((Double) countI.next()).doubleValue();
            gd.setValue(index, value);
        }

        return gd;
    }

    public ArrayList getReferenceObjects() {
        // speichert die referenzobjekte:
        ArrayList<GeoShape> refs = new ArrayList<GeoShape>();
        Vector allCons = getAllContributions();
        Iterator iter = allCons.iterator();
        // all beiträge durchgehen
        while (iter.hasNext()) {
            Contribution current = (Contribution) iter.next();
            Iterator conIter = current.getReferenceObjects().iterator();
            // alle referenzobjekte des aktuellen beitrags durchgehen
            while (conIter.hasNext()) {
                GeoShape currentShape = (GeoShape) conIter.next();
                // wenn der shape noch nicht in der ArrayList ist, hinzufügen:
                if (!refs.contains(currentShape)) {
                    refs.add(currentShape);
                }
            }
        }
        return refs;
    }

}
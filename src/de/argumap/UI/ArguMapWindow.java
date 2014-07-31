package de.argumap.UI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import uk.ac.leeds.ccg.geotools.GeoLine;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoPolygon;
import uk.ac.leeds.ccg.geotools.GeoRectangle;
import uk.ac.leeds.ccg.geotools.HSVShader;
import uk.ac.leeds.ccg.geotools.HighlightManager;
import uk.ac.leeds.ccg.geotools.Layer;
import uk.ac.leeds.ccg.geotools.LayerChangedEvent;
import uk.ac.leeds.ccg.geotools.LayerChangedListener;
import uk.ac.leeds.ccg.geotools.LineLayer;
import uk.ac.leeds.ccg.geotools.MonoShader;
import uk.ac.leeds.ccg.geotools.PointLayer;
import uk.ac.leeds.ccg.geotools.PolygonLayer;
import uk.ac.leeds.ccg.geotools.Scaler;
import uk.ac.leeds.ccg.geotools.SelectionManager;
import uk.ac.leeds.ccg.geotools.ShadeStyle;
import uk.ac.leeds.ccg.geotools.ShapefileReader;
import uk.ac.leeds.ccg.geotools.SimpleGeoData;
import uk.ac.leeds.ccg.geotools.Theme;
import uk.ac.leeds.ccg.geotools.Viewer;
import uk.ac.leeds.ccg.raster.ImageLayer;
import de.argumap.discussion.Participator;
import de.argumap.gt_extensions.ArguMapToolBar;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * 
 */
public class ArguMapWindow extends Frame implements ActionListener,
        LayerChangedListener {
    // user interface elements:
    private JPanel mapPanel;
    private Viewer view;
    private CardPanel cardPanel;
    private JLabel loginfo;
    private JButton logInOrOutButton;
    private LayerSelectionPanel layerSelectionPanel;
    private ArguMapToolBar controls;
    private StatusInformationPanel statusInfo;

    // the different themes shown on the map:
    private Theme userPointTheme, userLineTheme, userPolygonTheme,
            imgTheme, shapeTheme, pointsFromDB, linesFromDB, polygonsFromDB;

    // other fields
    private Participator loggedIn;
    private ShapefileReader loader;
    private String dbaddress;
    private JTabbedPane tabs;

    // private AnalysisPanel analysisPanel;
    private SearchAndAnalyzePanel searchAndAnalyzePanel;
    private String sessionID;

    // configuration arguments loaded in the constructor
    private static String shapeFile, shapeName, shaderColumn, tooltipColumn,
            servletBase;
    
    public ArguMapWindow(String[] args) {
        // load configuration arguments (the only function of the constructor):
        shapeFile = args[0];
        shapeName = args[1];
        shaderColumn = args[2];
        tooltipColumn = args[3];
        servletBase = args[4];
    }

    /**
     * Sets up the components for display.
     */
    public void init() {
        servletBase = getServletBase();
        this.setBackground(Color.lightGray);
        // generate panels
        mapPanel = new JPanel();
        mapPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
        mapPanel.setBackground(Color.white);
        cardPanel = new CardPanel(this);
        JPanel discussionPanel = new JPanel();
        JPanel logInfoPanel = new JPanel();
        // generate + add layouts
        discussionPanel.setLayout(new BorderLayout());
        logInfoPanel.setLayout(new GridLayout(1, 2, 5, 5));
        logInfoPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
        discussionPanel.setBackground(Color.white);
        discussionPanel.setMinimumSize(new Dimension(10, 10));
        mapPanel.setLayout(new BorderLayout());
        // this.setLayout(new GridLayout(1, 1));
        this.setLayout(new BorderLayout());
        // components for loginforPanel:
        loginfo = new JLabel(" Log in to discuss: ");
        loginfo.setForeground(Color.gray);
        logInOrOutButton = new JButton("Log in...");
        logInOrOutButton.addActionListener(this);
        // add it...
        logInfoPanel.add(loginfo);
        logInfoPanel.add(logInOrOutButton);
        // load shapefile
        shaderColumn = getShaderColumn();
        try {
            loader = new ShapefileReader(new URL(getShapeFile()));
        } catch (MalformedURLException e) {
            System.out.println("Unable to load shapefile " + getShapeFile()
                    + ": " + e);
        }
        // generate viewer + add to mapPanel
        view = new Viewer();
        view.addMouseMotionListener(new StatsMouseMotionListener(this));
        // generate toolbar
        controls = new ArguMapToolBar(view, this);
        JPanel controlsAndScaleBar = new JPanel();
        controlsAndScaleBar.setLayout(new BorderLayout());
        controlsAndScaleBar.add(controls, "Center");
        JLabel leer = new JLabel("");
        leer.setSize(150, 8);
        // add components to viewer
        mapPanel.add(view, "Center");
        mapPanel.add(controlsAndScaleBar, "North");
        mapPanel.setMinimumSize(new Dimension(10, 10));
        mapPanel.setPreferredSize(new Dimension(100, 100));
        // add both panels (map left / argu right) to applet
        discussionPanel.add(logInfoPanel, "North");
        discussionPanel.add(cardPanel, "Center");
        layerSelectionPanel = new LayerSelectionPanel(this);
        searchAndAnalyzePanel = new SearchAndAnalyzePanel(this);
        // JTabbedPane, allows users to switch between discussion / map layers /
        // query
        tabs = new JTabbedPane();
        tabs.add("Discussion", discussionPanel);
        tabs.add("Map Layers", layerSelectionPanel);
        tabs.add("Search and Analyze", searchAndAnalyzePanel);
        JSplitPane holder = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                mapPanel, tabs);
        tabs.setMinimumSize(new Dimension(10, 10));
        tabs.setPreferredSize(new Dimension(100, 100));
        holder.setPreferredSize(new Dimension(10, 10));
        holder.setResizeWeight(0.7);
        holder.setOneTouchExpandable(true);
        add(holder, BorderLayout.CENTER);
        // status info:
        statusInfo = new StatusInformationPanel();
        add(statusInfo, BorderLayout.SOUTH);
    }

    /**
     * Loads the map and adds it to the window's layout.
     */
    public void start() {
        // Construct a Theme from the Shapefile
        HSVShader shapeShade = new HSVShader(100.0, 58900.0, Color.green, Color.orange, true);
        //RampShader shapeShade = new RampShader();
        double[] range = shapeShade.getRange();
        System.out.println("rampshader min "+range[0]);
        System.out.println("rampshader max "+range[1]);
        //TODO: getTheme doesn't work correctly... the min and max values for the shaders are not set!!
        shapeTheme = loader.getTheme(shapeShade, shaderColumn);
        //workaround for the error above - setting the min + max manually:
        shapeShade.setRange(100.0, 58900.0);
        shapeShade.setMissingValueCode(0.0);
        range = shapeShade.getRange();
        System.out.println("rampshader min "+range[0]);
        System.out.println("rampshader max "+range[1]);
        // add HighlightManager + ToolTips
        shapeTheme.setHighlightManager(new HighlightManager());
        shapeTheme.setTipData(loader.readData(tooltipColumn));
        shapeTheme.setHighlightStyle(new ShadeStyle(true, true, Color.cyan,
                Color.black, false));
        
        // add layer for user-points (empty on startup):
        // ...layer which holds the points added by the currently logged in user
        PointLayer userPointsLayer = new PointLayer();
        MonoShader shade = new MonoShader(Color.red);
        userPointTheme = new Theme(userPointsLayer, shade);
        ShadeStyle userStyle = new ShadeStyle();
        userStyle.setLineWidth(3);
        userStyle.setLineColor(Color.red);
        userStyle.setFillColor(Color.red);
        userPointTheme.setHighlightManager(new HighlightManager());
        userPointTheme.setStyle(userStyle);
        
        // repeat this for lines and polygons:
        LineLayer userLineLayer = new LineLayer();
        userLineTheme = new Theme(userLineLayer, shade);
        userLineTheme.setStyle(userStyle);
        userLineTheme.setHighlightManager(new HighlightManager());
        PolygonLayer userPolygonLayer = new PolygonLayer();
        userPolygonTheme = new Theme(userPolygonLayer, shade);
        userPolygonTheme.setHighlightManager(new HighlightManager());
        // add a layer for the user-points from db:
        PointLayer dbPointsLayer = new PointLayer();
        MonoShader shadeDB = new MonoShader(Color.orange);
        ShadeStyle hilight = new ShadeStyle();
        hilight.setLineWidth(5);
        hilight.setLineColor(Color.black);
        hilight.setFillColor(Color.orange);
        
        pointsFromDB = new Theme(dbPointsLayer, shadeDB);
        pointsFromDB.setHighlightStyle(hilight);
        pointsFromDB.setHighlightManager(new HighlightManager());
        pointsFromDB.setSelectionStyle(userStyle);
        pointsFromDB.setSelectionManager(new SelectionManager());
        
        // repeat for lines and polygons:
        LineLayer dbLineLayer = new LineLayer();
        linesFromDB = new Theme(dbLineLayer, shadeDB);
        linesFromDB.setHighlightStyle(hilight);
        linesFromDB.setHighlightManager(new HighlightManager());
        linesFromDB.setSelectionStyle(userStyle);
        linesFromDB.setSelectionManager(new SelectionManager());
        PolygonLayer dbPolygonLayer = new PolygonLayer();
        polygonsFromDB = new Theme(dbPolygonLayer, shadeDB);
        ShadeStyle polygonhi = new ShadeStyle(true, true, Color.green,
                Color.black, 1, false);
        
        polygonsFromDB.setStyle(polygonhi);
        polygonsFromDB.setHighlightStyle(polygonhi);
        polygonsFromDB.setHighlightManager(new HighlightManager());
        polygonsFromDB.setSelectionStyle(userStyle);
        polygonsFromDB.setSelectionManager(new SelectionManager());
        // load references:
        refreshReferencesFromDB();
        
        // load WMC image
        ImageLayer laya = null;
        
        try {
            // initiate the image layer with some bounding box... will be
            // updated within the next steps
            laya = new ImageLayer(new URL(servletBase + "image;jsessionid="
                    + getSessionID()), shapeTheme.getBounds());
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
        
        // we need to be notified when the image layer changes so that we can
        // update the status info panel:
        laya.addLayerChangedListener(this);
        imgTheme = new Theme(laya);
        // add themes to viewer
        view.addTheme(imgTheme);
        view.addTheme(shapeTheme);
        view.addTheme(polygonsFromDB);
        view.addTheme(linesFromDB);
        view.addTheme(pointsFromDB);
        view.addTheme(userPointTheme);
        view.addTheme(userLineTheme);
        view.addTheme(userPolygonTheme);
        
        //make sure the WMS map is reloaded whenever the viewer is resized:
        view.addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e) {
                Viewer v = (Viewer) e.getComponent();
                ArguMapWindow window = (ArguMapWindow) v.getParent().getParent().getParent();
                window.reloadWMC();
            }
        });
        // check whether the servlet params contain a bounding box (optional)
        /*
         * TODO !! if (getParameter("north") != null && getParameter("south") !=
         * null && getParameter("east") != null && getParameter("west") != null) {
         * double north = (new Double(getParameter("north"))).doubleValue();
         * double south = (new Double(getParameter("south"))).doubleValue();
         * double east = (new Double(getParameter("east"))).doubleValue();
         * double west = (new Double(getParameter("west"))).doubleValue();
         * double x = west; double y = south; double width = east - west; double
         * height = north - south; GeoRectangle box = new GeoRectangle(x, y,
         * width, height); view.getScale().setMapExtent(box); }
         */

        // add window adapter
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing down...");
                //kill the server-side session:
                //TODO
                
                System.exit(0);
            }
            
        });

        // show me ...
        setTitle("ArguMap Prototype");
        setIconImage((new ImageIcon(ArguMapWindow.class.getResource("img/argumapicon.png")).getImage()));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.setSize(screenSize.width - 160, screenSize.height - 160);
        setLocation(80, 80);
        setPreferredSize(screenSize);
        
        // workaround to make sure the map shows the coorect extent on start-up
        while(laya.getStatus() != Layer.COMPLETED){
            // waiting for image layer to load...
        }
        reloadWMC();
        
        pack();
        setVisible(true);

    }

    /**
     * @return The applet's viewer.
     */
    public Viewer getViewer() {
        return view;
    }

    /**
     * @return The Theme holding the WMS-image.
     */
    public Theme getImgTheme() {
        return imgTheme;
    }

    public void updateLayerSelectionPanel() {
        layerSelectionPanel = new LayerSelectionPanel(this);
        tabs.setComponentAt(1, layerSelectionPanel);
    }

    /**
     * Reloads the image from the web map client servlet so that it shows the
     * same extend as the viewer.
     * 
     */
    public void reloadWMC() {
        Scaler scale = view.getScale();
        GeoRectangle extend = scale.getMapExtent();
        // set the WMC to the coordinates of the Map (the ZoomServlet's job)
        int width = view.getWidth();
        int height = view.getHeight();
        double maxx = extend.getX() + extend.getWidth();
        double maxy = extend.getY() + extend.getHeight();
        try {
            String urlString = servletBase + "update;jsessionid="
                    + getSessionID() + "?minx=" + extend.getX() + "&miny="
                    + extend.getY() + "&maxx=" + maxx + "&maxy=" + maxy
                    + "&width=" + width + "&height=" + height + "&layers="
                    + layerSelectionPanel.getActiveLayers();
            urlString.replaceAll(" ", "%20");
            URL url = new URL(urlString);
            BufferedReader in = new BufferedReader(new InputStreamReader(url
                    .openStream()));
            // System.out.println(in.readLine());
            in.close();
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }
        // update the theme holding the WMC image:
        GeoRectangle rect;
        rect = new GeoRectangle(extend.getX(), extend.getY(),
                extend.getWidth(), extend.getHeight());
        ImageLayer il = (ImageLayer) this.getImgTheme().getLayer();
        try {
            il.changeImage(new URL(servletBase + "image;jsessionid="
                    + getSessionID()), rect);
        } catch (MalformedURLException e1) {
            System.out.println(e1.getMessage());
        }
    }

    /**
     * Called when the log-in/log-out-button is clicked. Either shows the log-in
     * panel or logs off the user currently logged in.
     */
    public void actionPerformed(ActionEvent e) {
        // login:
        if (e.getActionCommand() == "Log in...") {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "loginPanel");
            logInOrOutButton.setText("Log out");
            logInOrOutButton.setEnabled(false);
        } else {
            // logout:
            this.setLoggedIn(null);
            cardPanel.getLoginPanel().reset();
            logInOrOutButton.setText("Log in...");
            controls.enableLoggedInTools(false);
            cardPanel.getArguPanel().setDiscussEnabled(false);
        }
    }

    /**
     * @param loggedIn
     *            The loggedIn to set.
     */
    public void setLoggedIn(Participator loggedIn) {
        this.loggedIn = loggedIn;
        if (loggedIn == null) {
            loginfo.setText(" Please log in: ");
            cardPanel.getArguPanel().setDiscussEnabled(false);
            controls.enableLoggedInTools(false);
        } else {
            loginfo.setText(" Logged in: " + loggedIn.getFirstName() + " "
                    + loggedIn.getLastName());
            logInOrOutButton.setEnabled(true);
            cardPanel.getArguPanel().setDiscussEnabled(true);
            controls.enableLoggedInTools(true);
            logInOrOutButton.setEnabled(true);
        }
    }

    /**
     * @return Returns the loggedIn.
     */
    public Participator getLoggedIn() {
        return loggedIn;
    }

    /**
     * @return Returns a Vector of GeoShapes that contains all points added by
     *         the user.
     */
    public Vector getUserPoints() {
        PointLayer pl = (PointLayer) userPointTheme.getLayer();
        return (pl.getGeoShapes());
    }

    /**
     * @return Returns a Vector of GeoShapes that contains all lines added by
     *         the user.
     */
    public Vector getUserLines() {
        LineLayer ll = (LineLayer) userLineTheme.getLayer();
        return (ll.getGeoShapes());
    }

    /**
     * @return Returns a Vector of GeoShapes that contains all polygons added by
     *         the user.
     */
    public Vector getUserPolygons() {
        PolygonLayer pl = (PolygonLayer) userPolygonTheme.getLayer();
        return (pl.getGeoShapes());
    }

    /**
     * @return The theme that holds the layer with the points added by the user.
     */
    public Theme getUserPointTheme() {
        return userPointTheme;
    }

    /**
     * @return The theme that holds the layer with the lines added by the user.
     */
    public Theme getUserLineTheme() {
        return userLineTheme;
    }

    /**
     * @return The theme that holds the layer with the polygons added by the
     *         user.
     */
    public Theme getUserPolygonTheme() {
        return userPolygonTheme;
    }

    /**
     * Reloads the references (points, lines, polygons) from the database into
     * the corresponding layers that displays them. Usually called after
     * references have been added to the database.
     */
    public void refreshReferencesFromDB() {
        // TEST:
        // try {
        // URL featureURL = new URL(getServletbase() +
        // "getfeatures;jsessionid="+getSessionID());
        // BufferedReader in = new BufferedReader(new InputStreamReader(
        // featureURL.openStream()));
        // System.out.println(in.readLine());
        // in.close();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        //
        // -------- POINTS first -----------------
        PointLayer newPoints = new PointLayer();
        // data for tooltips:
        SimpleGeoData geodata = new SimpleGeoData();
        String pointString = "";
        try {
            URL pointURL = new URL(getServletBase() + "allpoints;jsessionid="
                    + getSessionID());
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    pointURL.openStream()));
            pointString = in.readLine();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (pointString != null) {
            String[] points = pointString.split(" /// ");
            for (int i = 0; i < points.length; i++) {
                String[] pointdata = points[i].split(" - ");
                int id = (new Integer(pointdata[0])).intValue();
                double x = (new Double(pointdata[1])).doubleValue();
                double y = (new Double(pointdata[2])).doubleValue();
                newPoints.addGeoPoint(new GeoPoint(id, x, y));
                // get tooltips
                geodata.setText(id, getTooltip(id));
            }
            pointsFromDB.setLayer(newPoints);
            // set style for Layer
            ShadeStyle dbpointsStyle = new ShadeStyle();
            dbpointsStyle.setLineWidth(3);
            pointsFromDB.setStyle(dbpointsStyle);
            // update tip data
            pointsFromDB.setTipData(geodata);
        }
        // -------------------- next: LINES ------------------------------
        LineLayer newLines = new LineLayer();
        // data for tooltips:
        geodata = new SimpleGeoData();
        String lineString = "";
        try {
            URL lineURL = new URL(getServletBase()
                    + "linesandpolygons;jsessionid=" + getSessionID()
                    + "?typ=GeoLine");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    lineURL.openStream()));
            lineString = in.readLine();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lineString != null) {
            String[] lines = lineString.split(" /// ");
            for (int i = 0; i < lines.length; i++) {
                String[] linedata = lines[i].split(" - ");
                int id = (new Integer(linedata[0])).intValue();
                int start_co = (new Integer(linedata[1])).intValue();
                int end_co = (new Integer(linedata[2])).intValue();
                int numPoints = end_co - start_co + 1;
                String coordsString = "";
                try {
                    URL coordsURL = new URL(getServletBase()
                            + "coordinates;jsessionid=" + getSessionID()
                            + "?start=" + start_co + "&numPoints=" + numPoints);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(coordsURL.openStream()));
                    coordsString = in.readLine();
                    in.close();
                } catch (IOException ioe) {
                }
                String[] coords = coordsString.split(" - ");
                GeoPoint[] punkte = new GeoPoint[numPoints];
                int w = 0;
                int k = 0;
                while (w < coords.length) {
                    double x = (new Double(coords[w])).doubleValue();
                    w++;
                    double y = (new Double(coords[w])).doubleValue();
                    w++;
                    GeoPoint aktuell = new GeoPoint(x, y);
                    punkte[k] = aktuell;
                    k++;
                }
                newLines.addGeoLine(new GeoLine(id, punkte));
                // tooltips holen:
                geodata.setText(id, getTooltip(id));
            }
            linesFromDB.setLayer(newLines);
            // Style f¸r den Layer setzen:
            ShadeStyle dbpointsStyle = new ShadeStyle();
            dbpointsStyle.setLineWidth(3);
            linesFromDB.setStyle(dbpointsStyle);
            // tip data aktualisieren:
            linesFromDB.setTipData(geodata);
        }
        // -------------------- next: POLYGONS ------------------------
        PolygonLayer newPolygons = new PolygonLayer();
        // data for tooltips:
        geodata = new SimpleGeoData();
        String polygonString = "";
        try {
            URL polygonURL = new URL(getServletBase()
                    + "linesandpolygons;jsessionid=" + getSessionID()
                    + "?typ=GeoPolygon");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    polygonURL.openStream()));
            polygonString = in.readLine();
            // System.out.println("fetching polygons - server response:
            // "+polygonString);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (polygonString != null) {
            String[] polygons = polygonString.split(" /// ");
            System.out.println(polygons.length + " polygons loaded from db");
            for (int i = 0; i < polygons.length; i++) {
                String[] polygondata = polygons[i].split(" - ");
                int id = (new Integer(polygondata[0])).intValue();
                int start_co = (new Integer(polygondata[1])).intValue();
                int end_co = (new Integer(polygondata[2])).intValue();
                int numPoints = end_co - start_co + 1;
                String coordsString = "";
                try {
                    URL coordsURL = new URL(getServletBase()
                            + "coordinates;jsessionid=" + getSessionID()
                            + "?start=" + start_co + "&numPoints=" + numPoints);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(coordsURL.openStream()));
                    coordsString = in.readLine();
                    in.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                String[] coords = coordsString.split(" - ");
                GeoPoint[] punkte = new GeoPoint[numPoints];
                int w = 0;
                int k = 0;
                while (w < coords.length) {
                    double x = (new Double(coords[w])).doubleValue();
                    w++;
                    double y = (new Double(coords[w])).doubleValue();
                    w++;
                    GeoPoint aktuell = new GeoPoint(x, y);
                    punkte[k] = aktuell;
                    k++;
                }
                newPolygons.addGeoPolygon(new GeoPolygon(id, punkte));
                // get tooltips
                geodata.setText(id, getTooltip(id));
            }
            polygonsFromDB.setLayer(newPolygons);
            // set style for Layer
            polygonsFromDB.setStyle(new ShadeStyle(true, true, Color.green,
                    Color.blue, 1, false));
            // update tip data
            polygonsFromDB.setTipData(geodata);
        }
    }

    /**
     * @return Returns the WMCToolBar holding the controls.
     */
    public ArguMapToolBar getControls() {
        return controls;
    }

    /**
     * @return Returns the logInOrOutButton.
     */
    public JButton getLogInOrOutButton() {
        return logInOrOutButton;
    }

    /**
     * @return Returns the servletbase which has been set in the Applet's
     *         parameters.
     */
    public String getServletBase() {
        return "http://medis.uni-muenster.de:7000/wmc/";
        // return servletbase;
    }

    /**
     * @return Returns the pointsFromDB.
     */
    public Theme getPointsFromDB() {
        return pointsFromDB;
    }

    /**
     * @return Returns the shapeTheme.
     */
    public Theme getShapeTheme() {
        return shapeTheme;
    }

    /**
     * @return Returns the cardPanel.
     */
    public CardPanel getCardPanel() {
        return cardPanel;
    }

    /**
     * @return Returns the layerSelectionPanel.
     */
    public LayerSelectionPanel getLayerSelectionPanel() {
        return layerSelectionPanel;
    }

    /**
     * @return Returns the dbaddress.
     */
    public String getDbaddress() {
        return dbaddress;
    }

    public Theme getLinesFromDB() {
        return linesFromDB;
    }

    public Theme getPolygonsFromDB() {
        return polygonsFromDB;
    }

    private String getTooltip(int locationID) {
        String tooltip = "";
        try {
            URL tooltipURL = new URL(getServletBase() + "tooltip;jsessionid="
                    + getSessionID() + "?location=" + locationID);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    tooltipURL.openStream()));
            tooltip = in.readLine();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tooltip;
    }

    public JTabbedPane getTabs() {
        return tabs;
    }

    public ArguPanel getArguPanel() {
        return cardPanel.getArguPanel();
    }

    /**
     * @return Returns the column.
     */
    public static String getShaderColumn() {
        return shaderColumn;
    }

    /**
     * @return Returns the shapefile.
     */
    public static String getShapeFile() {
        return shapeFile;
    }

    /**
     * @return Returns the shapename.
     */
    public static String getShapeName() {
        return shapeName;
    }

    public void layerChanged(LayerChangedEvent tce) {
        int reason = tce.getReason();
        switch (reason) {
        case 111:
            statusInfo.statusChanged(StatusInformationPanel.ready);
            break;
        case 222:
            statusInfo.statusChanged(StatusInformationPanel.loadingMap);
            break;
        default:
            // do nothing
        }
    }
    
    public StatusInformationPanel getStatusInformationPanel(){
        return statusInfo;
    }
    
    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
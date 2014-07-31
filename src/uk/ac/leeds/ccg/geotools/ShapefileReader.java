package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import uk.ac.leeds.ccg.dbffile.Dbf;
import uk.ac.leeds.ccg.dbffile.DbfFileException;
import uk.ac.leeds.ccg.shapefile.ShapeArc;
import uk.ac.leeds.ccg.shapefile.ShapeArcM;
import uk.ac.leeds.ccg.shapefile.ShapePoint;
import uk.ac.leeds.ccg.shapefile.ShapePolygon;
import uk.ac.leeds.ccg.shapefile.Shapefile;
import uk.ac.leeds.ccg.shapefile.ShapefileShape;

/**
 * A class to simplify the process of loading an ESRI(r) Shapefile. It is
 * effectivly a wrapper for the Shapefile class.
 * <p>
 * This class will open the shapefile (both .shp and .dbf if avaiable) after
 * which calls to readLines,readPolygons and readData will extract the necasary
 * information out of the shapefile.
 * <p>
 * The class also has a series of getTheme() methods that encapsulate the
 * process of building themes (complet with layer and shaders) as a v.easy way
 * of setting up a theme for inclusion in a viewer.
 * 
 * @author James Macgill, Center for computational Geography.
 * @author David Robison ArcM code and bug fixes
 */
public class ShapefileReader extends java.lang.Object implements DataSource {
    public static final String cvsid = "$Id: ShapefileReader.java,v 1.1 2005/09/19 10:31:29 CarstenKessler Exp $";
    private final static boolean DEBUG = false;
    public Shapefile sf = null;
    public Dbf dbf = null;
    private int idCol = 3;//best general guess
    private String name = "none";
    public MixedLayer map = new MixedLayer(this);
    private Loader loader;

    /**
     * Creates and opens a new ShapefileReader
     * 
     * @param baseFilename:
     *            name of file without extention e.g. 'name' not 'name.shp'
     */
    public ShapefileReader(String baseFilename) {

        if (DEBUG)
            System.out
                    .println("---->uk.ac.leeds.ccg.geotools.ShapefileReader constructed. Will identify itself as SfR->");
        name = baseFilename;
        try {
            sf = new Shapefile(baseFilename + ".shp");
        } catch (Exception e) {
            System.out.println("SfR->" + e);
            return;
        }

        try {
            File dbffile = new File(baseFilename + ".dbf");
            dbf = new Dbf(dbffile);
            getIds();
        } catch (Exception e) {
            System.out.println("SfR->" + e);
            return;
        }
    }

    public ShapefileReader(String baseFilename, int idCol) {
        if (DEBUG)
            System.out
                    .println("---->uk.ac.leeds.ccg.geotools.ShapefileReader constructed. Will identify itself as SfR->");
        this.idCol = idCol;
        name = baseFilename;
        try {
            sf = new Shapefile(baseFilename + ".shp");
        } catch (Exception e) {
            System.out.println("SfR->" + e);
        }

        try {
            File dbffile = new File(baseFilename + ".dbf");
            dbf = new Dbf(dbffile);
            getIds();
        } catch (Exception e) {
            System.out.println("SfR->" + e);
        }
    }

    URL base;
    int status = 0;

    /**
     * Creates and opens a new ShapefileReader. If at all posible, this is the
     * contstuctor that should be used.
     * 
     * @param base:
     *            url of file without extention e.g. 'name' not 'name.shp'
     * @param idCol
     *            An int giving the number of the colum to take the feature ids
     *            from.
     */
    public ShapefileReader(URL base, int idCol) {
        if (DEBUG)
            System.out
                    .println("---->uk.ac.leeds.ccg.geotools.ShapefileReader constructed. Will identify itself as SfR->");
        //if(base.toString().endsWith(".shp")
        this.idCol = idCol;
        this.base = base;
        map.setStatus(map.PENDING);
        name = base.getFile();
        loader = new Loader();
        //loader.start();

        //the following block of code effectivly dissables the threaded loading
        // system for now
        /*
         * try{ loader.join(); } catch(InterruptedException ie){
         * System.err.println("Shapefile not loaded by loader "+ie); }
         */
        loader.run();//bypass threading and run from here
        System.out
                .println("Shapefile Constructor Finished - tread loading disabled");
    }

    /**
     * Creates and opens a new ShapefileReader
     * 
     * @param shp:
     *            InputStream to the .shp file
     * @param dbf:
     *            InputStream to the .dbf file (can be null)
     * @param idCol
     *            An int giving the number of the colum to take the feature ids
     *            from.
     */
    public ShapefileReader(InputStream shpIn, InputStream dbfIn, int idCol) {
        if (DEBUG)
            System.out
                    .println("---->uk.ac.leeds.ccg.geotools.ShapefileReader constructed. Will identify itself as SfR->");
        this.idCol = idCol;
        //name = ???
        try {
            sf = new Shapefile(shpIn);
            if (dbfIn != null) {
                dbf = new Dbf(dbfIn);
            }
            getIds();
        } catch (Exception e) {
            System.err.println("SfR->" + e);
        }
    }

    /**
     * Creates and opens a new ShapefileReader
     * 
     * @param shp:
     *            InputStream to the .shp file
     * @param dbf:
     *            InputStream to the .dbf file (can be null)
     * @param idCol
     *            An int giving the number of the colum to take the feature ids
     *            from.
     */
    public ShapefileReader(InputStream shpIn, InputStream dbfIn) {
        if (DEBUG)
            System.out
                    .println("---->uk.ac.leeds.ccg.geotools.ShapefileReader constructed. Will identify itself as SfR->");
        //name = ???

        try {
            sf = new Shapefile(shpIn);
            if (dbfIn != null) {
                dbf = new Dbf(dbfIn);
            }
        } catch (Exception e) {
            System.err.println("SfR->" + e);
        }
        guessIdCol();
        getIds();
    }

    /**
     * Constructs a shapefile reader from a base url. This is the best
     * constructor to use if you do not know the id column for the shapefile
     * 
     * @param base
     *            The base url for the shapefile (i.e. leave the .shp off the
     *            end if possible)
     */
    public ShapefileReader(URL base) {
        this(base, -1);
        if (DEBUG)
            System.out
                    .println("---->uk.ac.leeds.ccg.geotools.ShapefileReader constructed. Will identify itself as SfR->");
        //try and figure out id col

    }

    private void guessIdCol() {
        switch (sf.getShapeType()) {
        case (Shapefile.POLYGON):
            idCol = 2;
            break;
        case (Shapefile.ARC_M):
            idCol = 5;
            break;
        case (Shapefile.ARC):
            idCol = 5;
            break;
        case (Shapefile.POINT):
            idCol = 3;
            break;
        }
        if (DEBUG)
            System.out.println("SfR->ID col first guessed at " + idCol + " "
                    + dbf.getNumFields());
        // attempt to solve sourceforge bug #122495 - ian
        if (dbf == null)
            return;
        if (idCol > dbf.getNumFields() - 1) {// whoops guessed wrong!
            idCol = 0; // probably not right but safe?
        }
        if (DEBUG)
            System.out.println("SfR->ID col guessed at " + idCol + " "
                    + dbf.getNumFields());
    }

    public void setIdCol(int col) {
        idCol = col;
    }

    public void setIdCol(String name) {
        int col = dbf.getFieldNumber(name);
        idCol = (col >= 0) ? col : idCol;
        getIds();
    }

    public int getIdCol() {
        return idCol;
    }

    /**
     * gets the shape type for this shapefile.
     * 
     * @return int The type of feature, constants defined in shapefile match the
     *         values given, for example Shapefile.POLYGON;
     *  
     */
    public int getShapeType() {
        return sf.getShapeType();
    }

    /**
     * Reads the data in this shapefile and produces a theme for use in a
     * Viewer.
     * 
     * @return Theme a theme containing the features of the shapefile
     */
    public Theme getTheme() {
        Theme t = new Theme(this.getLayer());
        t.setName(name);
        return t;
    }

    /**
     * Reads the data in this shapefile and produces a layer for use in a Theme.
     * 
     * @return Layer a Layer containing the features of the shapefile
     */
    public Layer getLayer() {
        return map;

        //return l;
    }

    /**
     * Gets a theme based on the given shader and colName as a bonus the method
     * configures the shaders range for you by reading the min and max values
     * from the named column
     * 
     * @param shade
     *            a RampShader to shade this theme (includes HSV,sat and val
     *            shaders)
     * @param colName
     *            the name of the column to grab the data from
     */
    public Theme getTheme(Shader shade, String colName) {
        Theme t = getTheme();
        SimpleGeoData data = new SimpleGeoData();
        data.setName(colName);
        t.setGeoData(data);
        shade.setRange(data);
        t.setShader(shade);
        AddDataWhenReady dataWatch = new AddDataWhenReady(data, colName);
        dataWatch.start();
        return t;
    }

    class AddDataWhenReady extends Thread {
        SimpleGeoData data;
        String colName;
        int col;

        public AddDataWhenReady(SimpleGeoData data, String colName) {
            this.data = data;
            this.colName = colName;
        }

        public AddDataWhenReady(SimpleGeoData data, int col) {
            this.data = data;
            this.colName = colName;
        }

        public void run() {
            System.out.println("Waiting for data loader to finish");
            try {
                loader.join();
            } catch (InterruptedException ie) {
                System.err.println("AddDataWhenReady stuck because " + ie);
                return;
            }
            System.out.println("Apparently data loader has now finished");
            if (colName != null) {
                col = dbf.getFieldNumber(colName);
            }
            if (col >= 0) {
                readDataNow(data, col);
            } else {
                if (DEBUG)
                    System.err.println("SfR->Column " + colName
                            + " not found in .dbf file");
            }

            //map.notifyLayerChangedListeners(LayerChangedEvent.DATA);

        }
    }

    /**
     * convinience version of get theme (shade,colname) provides a default
     * satShader with the specified color
     * 
     * @param colName
     *            A string containing the name of the .dbf col to shade by
     * @param c
     *            A color to shade with.
     */
    public Theme getTheme(String colName, Color c) {
        return getTheme(new SatShader(c), colName);
    }

    /**
     * Reads the feature information from the shapefile and produces a LineLayer
     * for use in a Theme. This will only work if the feature type is line or
     * polygon otherwise it will fail
     * 
     * @see getShapeType
     * @return LineLayer a LineLayer containing the lines from this shapefile.
     */
    public Layer readLines() {
        // LineLayer map = new LineLayer();
        GeoLine gp;
        ShapefileShape polys[] = sf.getShapes();
        ShapeArc poly;
        ShapePoint p[] = null;
        double px[], py[];
        int nPoints, nParts;
        int count = sf.getRecordCount();
        if (ids == null)
            getIds();
        for (int i = 0; i < count; i++) {
            poly = (ShapeArc) polys[i];
            nPoints = poly.getNumPoints();
            nParts = poly.getNumParts();

            for (int j = 0; j < nParts; j++) {
                p = poly.getPartPoints(j);

                //  px = new double[p.length];
                //  py = new double[p.length];

                //for(int k=0;k<p.length;k++){
                //  px[k]=p[k].getX();
                // py[k]=p[k].getY();
                //}
                // gp = new GeoPolygon(i+1,0,0,px,py,p.length);
                gp = new GeoLine(ids[i], p);
                map.addGeoLine(gp);
            }

        }
        return map;
    }

    public Layer readLinesM() {
        //LineLayer map = new LineLayer();
        GeoLine gp;
        ShapefileShape polys[] = sf.getShapes();
        ShapeArcM poly;
        ShapePoint p[] = null;
        double px[], py[];
        int nPoints, nParts;
        int count = sf.getRecordCount();
        for (int i = 0; i < count; i++) {
            poly = (ShapeArcM) polys[i];
            nPoints = poly.getNumPoints();
            nParts = poly.getNumParts();

            for (int j = 0; j < nParts; j++) {
                p = poly.getPartPoints(j);

                //px = new double[p.length];
                //py = new double[p.length];

                //for(int k=0;k<p.length;k++){
                //   px[k]=p[k].getX();
                //  py[k]=p[k].getY();
                //}
                gp = new GeoLine(i + 1, p);
                map.addGeoLine(gp);
            }

        }
        return map;
    }

    /**
     * Fills geodata objects with all of the data found in the shapefiles .dbf
     * file and indexes it by the shapefiles idColumn. If during construction no
     * ID collumn was given then it will be guessed based on the feature type
     * (not always successfull)
     * 
     * @return GeoData[] An array of SimpleGeoData's, one for each collum in the
     *         .dbf
     */
    public GeoData[] readData() {
        if (dbf != null) {
            GeoData data[] = new SimpleGeoData[0];
            if (ids == null)
                getIds();
            data = new SimpleGeoData[dbf.getNumFields()];
            for (int j = 0; j < dbf.getNumFields(); j++) {
                data[j] = readData(j);
                char t = dbf.getFieldType(j);
                switch (t) {
                case 'C':
                    data[j].setDataType(GeoData.CHARACTER);
                    break;
                case 'N':
                case 'F':
                    if (dbf.fielddef[j].fieldnumdec == 0)
                        data[j].setDataType(GeoData.INTEGER);
                    else
                        data[j].setDataType(GeoData.FLOATING);
                    break;
                }
            }
            return data;
        }
        return null;
    }

    private int ids[];

    private void getIds() {
        if (ids != null)
            return;
        if (DEBUG)
            System.out.println("in getIDS " + ids + " " + idCol);
        if (dbf != null) {
            try {
                if (idCol < dbf.getNumFields()) {
                    if (dbf.getFieldType(idCol) == 'N') {
                        Integer[] fids = dbf.getIntegerCol(idCol);
                        ids = new int[fids.length];
                        for (int i = 0; i < fids.length; i++) {
                            ids[i] = fids[i].intValue();
                        }
                        if (DEBUG)
                            System.out
                                    .println("got real ids from file in getIDS "
                                            + ids);
                        return;
                    }
                    if (dbf.getFieldType(idCol) == 'C') { // hmm see if we can
                                                          // convert
                        if (DEBUG)
                            System.out
                                    .println("Trying for Character based IDS");
                        String[] fids = dbf.getStringCol(idCol);
                        ids = new int[fids.length];
                        for (int i = 0; i < fids.length; i++) {
                            ids[i] = Integer.parseInt(fids[i].trim());
                        }
                        if (DEBUG)
                            System.out
                                    .println("got char ids from file in getIDS "
                                            + ids);
                        return;
                    }
                }
            } catch (Exception e) {
                if (DEBUG)
                    System.out.println("" + e);
            }
            if (DEBUG)
                System.err
                        .println("SfR->No ID column found, using sequential ids");
            ids = new int[dbf.getLastRec()];
            for (int i = 0; i < dbf.getLastRec(); i++) {
                ids[i] = i + 1;

            }
            if (DEBUG)
                System.out.println("got sequential ids (dbf) in getIDS " + ids);
            return;
        } // dbf == null
        ids = new int[sf.getRecordCount()];
        for (int i = 0; i < sf.getRecordCount(); i++) {
            ids[i] = i + 1;
        }
        if (DEBUG)
            System.out.println("got sequential ids in getIDS " + ids);
    }

    /**
     * Fills a geodata objects with the data found in the shapefiles .dbf file
     * from the specified collumg and indexes it by the shapefiles idColumn. If
     * during construction no ID collumn was given then it will be guessed based
     * on the feature type (not always successfull)
     * 
     * @param col
     *            An int representing the col to read the data from
     * @return GeoData A SimpleGeoData
     */

    /**
     * Fills a geodata objects with the data found in the shapefiles .dbf file
     * from the specified collumg and indexes it by the shapefiles idColumn. If
     * during construction no ID collumn was given then it will be guessed based
     * on the feature type (not always successfull)
     * 
     * @param col
     *            An int representing the col to read the data from
     * @return GeoData A SimpleGeoData
     */
    public GeoData readData(int col) {
        SimpleGeoData data = null;
        try {
            if (ids == null)
                getIds();
            data = new SimpleGeoData();
            //data.setMissingValueCode(this)
            data.setName(dbf.getFieldName(col).toString());
        } catch (Exception e) {
            System.err.println("SfR->ShapeFileReader error "
                    + "in readData(int col,int ids[]):" + e);
        }
        return readData(data, col);
    }

    public GeoData readData(SimpleGeoData data, int col) {
        AddDataWhenReady dataWatch = new AddDataWhenReady(data, col);
        dataWatch.start();
        return data;
    }

    private GeoData readDataNow(SimpleGeoData data, int col) {

        if (dbf.getFieldType(col) == 'C') {
            try {
                String s[] = dbf.getStringCol(col);
                for (int k = 0; k < s.length; k++) {
                    data.setText(ids[k], s[k]);
                    data.setDataType(GeoData.CHARACTER);
                }
            } catch (Exception e) {
                // errm nothing
            }
        } else {
            try {
                if (ids == null)
                    getIds();
                Float f[] = dbf.getFloatCol(col);
                double missing = data.getMissingValueCode();
                if (dbf.fielddef[col].fieldnumdec == 0)
                    data.setDataType(GeoData.INTEGER);
                else
                    data.setDataType(GeoData.FLOATING);
                for (int k = 0; k < f.length; k++) {
                    if (null != f[k]) {
                        try {
                            data.setValue(ids[k], f[k].doubleValue());
                        } catch (NumberFormatException ne) {
                            data.setValue(ids[k], missing);
                        }
                    } else {
                        data.setValue(ids[k], missing);
                    }
                }
            } catch (DbfFileException d) {
            } catch (IOException io) {
            }
        }
        return data;
    }

    /**
     * Fills a geodata objects with the data found in the shapefiles .dbf file
     * from the specified collumn and indexes it by the shapefiles idColumn. If
     * during construction no ID collumn was given then it will be guessed based
     * on the feature type (not always successfull)
     * 
     * @param colName
     *            A String representing the col to read the data from
     * @return GeoData A SimpleGeoData
     */
    public GeoData readData(String colName) {
        SimpleGeoData data = new SimpleGeoData();
        data.setName(colName);
        AddDataWhenReady dataWatch = new AddDataWhenReady(data, colName);
        dataWatch.run();//run directly instead of as a thread...
        return data;
    }

    /**
     * Reads the feature information from the shapefile and produces a
     * PolygonLayer for use in a Theme. This will only work if the feature type
     * is polygon otherwise it will fail.
     * 
     * Modidied JM 10/May/2000 Polygons created without 0,0 centroid so that
     * GeoPolygon will auto calculate this
     * 
     * @see getShapeType
     * @return LineLayer a LineLayer containing the lines from this shapefile.
     */
    public Layer readPolygons() {
        //sort out ids...
        if (ids == null)
            getIds();

        //PolygonLayer map = new PolygonLayer();
        GeoPolygon gp;
        ShapefileShape polys[] = sf.getShapes();
        ShapePolygon poly;
        ShapePoint p[] = null;
        double px[], py[];
        int nPoints, nParts;
        int count = sf.getRecordCount();
        for (int i = 0; i < count; i++) {
            poly = (ShapePolygon) polys[i];
            nPoints = poly.getNumPoints();
            nParts = poly.getNumParts();

            p = poly.getPartPoints(0);//main polygon
            // px = new double[p.length];
            // py = new double[p.length];

            // for(int k=0;k<p.length;k++){
            //   px[k]=p[k].getX();
            //   py[k]=p[k].getY();
            // }
            gp = new GeoPolygon(ids[i], p);
            if (nParts > 1) {
                if (DEBUG)
                    System.out.println("SfR->Building multipart polygon");
                for (int j = 1; j < nParts; j++) {
                    p = poly.getPartPoints(j);

                    // px = new double[p.length];
                    //  py = new double[p.length];

                    //  for(int k=0;k<p.length;k++){
                    //     px[k]=p[k].getX();
                    //     py[k]=p[k].getY();
                    //   }
                    //int k = gotIds?id[i].intValue():i+1;
                    gp.addSubPart((new GeoPolygon(ids[i], p)));

                }
            }
            map.addGeoPolygon(gp);

        }
        return map;

    }

    /**
     * Reads the feature information from the shapefile and produces a
     * PointLayer for use in a Theme. This will only work if the feature type is
     * point otherwise it will fail.
     * 
     * @see getShapeType
     * @return PointLayer a PointLayer containing the points from this
     *         shapefile.
     */
    public Layer readPoints() {
        //sort out ids...
        if (ids == null)
            getIds();

        //PointLayer map = new PointLayer();
        GeoPoint gp;
        ShapefileShape points[] = sf.getShapes();

        ShapePoint p = null;
        int count = sf.getRecordCount();
        for (int i = 0; i < count; i++) {
            p = (ShapePoint) points[i];
            gp = new GeoPoint(ids[i], p.getX(), p.getY());
            map.addGeoPoint(gp);
        }
        return map;

    }

    class Loader extends Thread {
        public void run() {
            //System.out.println("Hello from sf threaded loader!");

            String sub = "";
            if (name.indexOf('?') >= 0) {
                sub = name.substring(name.indexOf('?'), name.lastIndexOf('/'));
            }
            if (DEBUG)
                System.out.println("SfR->Sub " + sub);
            map.setStatus(map.LOADING);
            boolean shpZip = false, dbfZip = false;
            try {
                if (DEBUG)
                    System.out.println("SfR->Looking for .zip version of "
                            + name);
                String ext = ".zip";
                String noExt = base.getFile();
                if (DEBUG)
                    System.out.println("SfR->No Ext " + noExt);

                if (noExt.toLowerCase().endsWith(".shp")
                        || noExt.toLowerCase().endsWith(".zip")) {
                    noExt = name.substring(0, name.length() - 4);
                }
                if (DEBUG)
                    System.out.println("SfR->No Ext " + noExt);

                //if(base.getFile().endsWith(".zip") ||
                // base.getFile().endsWith(".shp")){ext = "";}

                URL zipURL = new URL(base.getProtocol(), base.getHost(), base
                        .getPort(), noExt + ext);
                if (DEBUG)
                    System.out.println("SfR->zip url = " + zipURL);
                URLConnection uc = zipURL.openConnection();
                uc.setUseCaches(false);
                uc.setDefaultUseCaches(false);
                if (DEBUG)
                    System.out.println("SfR->Opening zis");
                int size = uc.getContentLength();

                if (size != -1) {
                    byte[] buf1 = new byte[size];
                    byte[] got = new byte[size / 10];
                    InputStream is = uc.getInputStream();
                    int grab = 0;
                    int total = 0;

                    while ((grab = is.read(got)) > -1) {

                        System.arraycopy(got, 0, buf1, total, grab);
                        total += grab;

                    }
                    got = null;

                    ByteArrayInputStream zipfile = new ByteArrayInputStream(
                            buf1);
                    ZipInputStream zis = new ZipInputStream(zipfile);
                    ZipEntry ze;

                    //while(zis.available()<uc.getContentLength()){System.out.println(zis.available());}
                    if (DEBUG)
                        System.out.println("SfR->Looking for entries");
                    while (((ze = zis.getNextEntry()) != null)
                            && (!shpZip || !dbfZip)) {
                        if (DEBUG)
                            System.out.println("SfR->Found entry");
                        if (DEBUG)
                            System.out.println(ze);
                        if (DEBUG)
                            System.out.println("SfR->Entry: " + ze.getName());
                        if (DEBUG)
                            System.out.println("SfR->Getting entry");
                        //ze = zis.getNextEntry();

                        if (ze.getName().toLowerCase().endsWith(".shp")) {
                            name = ze.getName();
                            if (DEBUG)
                                System.out
                                        .println("SfR->Found .shp in zip file");
                            size = (int) ze.getSize();
                            byte[] buf = new byte[size];
                            byte[] in = new byte[size];

                            int n = 0;
                            int index = 0;

                            while ((n = zis.read(in)) > -1) {

                                System.arraycopy(in, 0, buf, index, n);
                                index += n;
                                //System.out.println("Index :"+index+" n "+n+"
                                // of "+size);
                            }

                            zis.closeEntry();
                            ByteArrayInputStream bais = new ByteArrayInputStream(
                                    buf);
                            sf = new Shapefile(bais);
                            shpZip = true;
                        } else {
                            if (ze.getName().toLowerCase().endsWith(".dbf")) {
                                if (DEBUG)
                                    System.out
                                            .println("SfR->Found .dbf in zip file");
                                size = (int) ze.getSize();
                                byte[] buf = new byte[size];
                                byte[] in = new byte[size];

                                int n;
                                int index = 0;

                                while ((n = zis.read(in)) > -1) {

                                    System.arraycopy(in, 0, buf, index, n);
                                    index += n;
                                    // System.out.println("Index :"+index+" n
                                    // "+n+" of "+size);
                                }

                                zis.closeEntry();
                                ByteArrayInputStream bais = new ByteArrayInputStream(
                                        buf);
                                dbf = new Dbf(bais);
                                dbfZip = true;
                                //System.out.println("SfR->DBF done");
                            }
                        }
                    }
                    if (shpZip && dbfZip) {
                        if (DEBUG)
                            System.out.println("SfR->Zip file version used "
                                    + name);
                        populate();
                        //getIds();
                        return;
                    }

                }

                //    zipURL.openStream();
            } catch (ZipException ze) {
                System.err.println("SfR->zip read failed " + ze);
            } catch (IOException ie) {
                System.err
                        .println("SfR->zip read not possible, looking for .shp and .dbf ");
            } catch (Exception e2) {
                System.err.println("SfR->General exception in zip read ");
                if (DEBUG) {
                    System.err.println(e2);
                    e2.printStackTrace();
                }
            }
            if (DEBUG)
                System.out
                        .println("SfR->Looking for files outside of zip file");
            try {
                String ext = ".shp";
                String noExt = base.getFile();
                if (noExt.toLowerCase().endsWith(".shp")
                        || noExt.toLowerCase().endsWith(".zip")) {
                    noExt = name.substring(0, name.length() - 4);
                }

                URL shapeURL = new URL(base.getProtocol(), base.getHost(), base
                        .getPort(), noExt + ext);
                if (!shpZip) {
                    sf = new Shapefile(shapeURL);
                }

                ext = ".dbf";

                URL dbfURL = new URL(base.getProtocol(), base.getHost(), base
                        .getPort(), noExt + ext);
                if (!dbfZip) {
                    dbf = new Dbf(dbfURL);
                }
                URL shxURL = new URL(base.getProtocol(), base.getHost(), base
                        .getPort(), noExt + ext);
                sf.readIndex(shxURL.openStream());
                getIds();

            } catch (Exception e) {
                map.setStatus(map.ERRORED);
                System.err.println("SfR->" + e);
            }
            populate();
        }

        protected void populate() {
            /*
             * try{ System.out.println("Silly psudo lag start");
             * sleep((int)(Math.random()*50000)); System.out.println("Silly
             * psudo lag finish"); } catch(Exception
             * e){System.out.println("Error in wait "+e);};
             */
            if (idCol == -1) {
                guessIdCol();
                getIds();
            }

            switch (getShapeType()) {
            case (Shapefile.POLYGON):
                readPolygons();
                break;
            case (Shapefile.ARC):
                readLines();
                break;
            case (Shapefile.POINT):
                readPoints();
                break;
            case (Shapefile.ARC_M):
                readLinesM();
                break;
            default:
                map.setStatus(map.ERRORED);
            }
            map.setStatus(map.COMPLETED);
            sf = null;//is this needed any more? lets see.
        }
    }

}
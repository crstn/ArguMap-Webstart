package uk.ac.leeds.ccg.geotools;



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



/**

 * A class to load geopoints from a DBF (xBase) file without using a shapefile.

 * This class file read a given dbf file and create a layer with geopoints

 * The dbf file should contain (at least) the following fields :

 * X coordinate ( numerical field )

 * Y coordinate ( numerical field )

 * ID field     ( character field )

 * The column where each of these field are located can be set by you, or it will be guessed

 * The file can either be a .dbf file, or a .zip file containing a .dbf file

 *

 * The class also has a series of getTheme() methods that encapsulate the process

 * of building themes (compleet with layer and shaders) as a v.easy way of setting up a theme

 * for inclusion in a viewer.

 * Creation date: (11/24/00 1:04:36 PM)

 * @author: Mathieu van Loon

 */

public class DBFFileReader implements FeatureReader{

	private int xCol; // number of column where x coordinate is stored

	private int yCol; // number of column where y coordinate is stored

	private int idCol; // number of column where id is stored

	public uk.ac.leeds.ccg.dbffile.Dbf dbf = null; // instance of class that will contain the representation of dbf file

	private java.lang.String name = "none"; // name of file without extension, also name of theme

	public boolean debug = false;

	private int[] ids = null;

/**

 * DBFFileReader constructor comment.

 * TODO explain constructor

 */

public DBFFileReader(String file, int idCol, int xCol, int yCol) {

	this.idCol=idCol;

	this.xCol = xCol;

	this.yCol = yCol;



	name = file;



	String sub="";

	if(name.indexOf('?')>=0){sub = name.substring(name.indexOf( '?' ),name.lastIndexOf('/'));}

	if(debug)System.out.println("DBFFileReader: Sub "+sub);



	boolean dbfZip = false;



/*

	try{

		System.out.println("DBFileReader: Looking for .zip version of "+name);

		String ext = ".zip";

		String noExt = file;

		if(debug)System.out.println("DBFileReader: No Ext "+noExt);



		if(noExt.toLowerCase().endsWith(".shp") || noExt.toLowerCase().endsWith(".zip")){

			noExt = name.substring(0,name.length()-4);

		}

		if(debug)System.out.println("DBFileReader: No Ext "+noExt);





		if(debug)System.out.println("DBFileReader: Opening zis (ZipInputStream)");

		int size = uc.getContentLength();

		if(debug)System.out.println("DBFileReader: size equals :"+size);

		if(size!=-1){

			byte [] buf1 = new byte [size];

			byte [] got = new byte[size/10];

			InputStream is = uc.getInputStream();

			int grab=0;

			int total = 0;



			while ((grab = is.read(got)) > -1)

			{

				System.arraycopy(got, 0, buf1, total, grab);

				total+=grab;

			}

			got=null;



			ByteArrayInputStream zipfile = new ByteArrayInputStream(buf1);

			ZipInputStream zis = new ZipInputStream(zipfile);

			ZipEntry ze;



			if(debug)System.out.println("DBFileReader: Looking for entries");

			while(((ze= zis.getNextEntry())!=null)  && !dbfZip){

				if(debug)System.out.println("DBFileReader: Found entry");

				if(debug)System.out.println("DBFileReader: "+ze);

				if(debug)System.out.println("DBFileReader: Entry: "+ze.getName());

				if(debug)System.out.println("DBFileReader: Getting entry");



				if(ze.getName().toLowerCase().endsWith(".dbf"))

				{

					if(debug)System.out.println("DBFileReader: Found .dbf in zip file");

					size = (int)ze.getSize();

					byte [] buf = new byte [size];

					byte [] in = new byte[size];



					int n;

					int index = 0;



					while ((n = zis.read(in)) > -1)

					{

						System.arraycopy(in, 0, buf, index, n);

						index+=n;

					}



					zis.closeEntry();

					ByteArrayInputStream bais = new ByteArrayInputStream(buf);

					dbf = new Dbf(bais);

					dbfZip = true;

					if(debug)System.out.println("DBFileReader: DBF done");

				}

			}

			if(dbfZip){System.out.println("DBFileReader: Zip file version used "+name); return;}

		}



	}

	catch(ZipException ze){System.err.println("DBFileReader: zip read failed "+ze);}

	catch(IOException ie){System.err.println("DBFileReader: zip read not possible, looking for .dbf ");}

	catch(Exception e2){System.err.println("DBFileReader: General exception in zip read ");}

*/

	if(!dbfZip)

	{

		if(debug)System.out.println("DBFFileReader: Looking for files outside of zip file");

		try{

			String noExt = file;

			if(noExt.toLowerCase().endsWith(".zip")){

				noExt = name.substring(0,name.length()-4);

			}

	 		String ext = ".dbf";

	 		File fl = new File(file+ext);

			dbf = new Dbf(fl);

		}

		catch(Exception e)

		{

			System.err.println("DBFFileReader: Unable to locate or load a .dbf file either in a zip file or the file self");

			System.err.println(e);

		}

	}

	this.init();

}

/**

 * DBFFileReader constructor comment.

 * TODO

 */

public DBFFileReader(URL base, int idCol, int xCol, int yCol) {

	this.idCol=idCol;

	this.xCol = xCol;

	this.yCol = yCol;



	name = base.getFile();



	String sub="";

	if(name.indexOf('?')>=0){sub = name.substring(name.indexOf( '?' ),name.lastIndexOf('/'));}

	if(debug)System.out.println("DBFFileReader: Sub "+sub);



	boolean dbfZip = false;

	try{

		System.out.println("DBFFileReader: Looking for .zip version of "+name);

		String ext = ".zip";

		String noExt = base.getFile();

		if(debug)System.out.println("DBFFileReader: No Ext "+noExt);



		if(noExt.toLowerCase().endsWith(".shp") || noExt.toLowerCase().endsWith(".zip")){

			noExt = name.substring(0,name.length()-4);

		}

		if(debug)System.out.println("DBFFileReader: No Ext "+noExt);



		URL zipURL = new URL(base.getProtocol(),base.getHost(),base.getPort(),noExt+ext);

		if(debug)System.out.println("DBFFileReader: zip url = "+zipURL);

		URLConnection uc = zipURL.openConnection();

		uc.setUseCaches(false);

		uc.setDefaultUseCaches(false);

		if(debug)System.out.println("DBFFileReader: Opening zis (ZipInputStream)");

		int size = uc.getContentLength();

		if(debug)System.out.println("DBFFileReader: size equals :"+size);

		if(size!=-1){

			byte [] buf1 = new byte [size];

			byte [] got = new byte[size/10];

			InputStream is = uc.getInputStream();

			int grab=0;

			int total = 0;



			while ((grab = is.read(got)) > -1)

			{

				System.arraycopy(got, 0, buf1, total, grab);

				total+=grab;

			}

			got=null;



			ByteArrayInputStream zipfile = new ByteArrayInputStream(buf1);

			ZipInputStream zis = new ZipInputStream(zipfile);

			ZipEntry ze;



			if(debug)System.out.println("DBFFileReader: Looking for entries");

			while(((ze= zis.getNextEntry())!=null)  && !dbfZip){

				if(debug)System.out.println("DBFFileReader: Found entry");

				if(debug)System.out.println("DBFFileReader: "+ze);

				if(debug)System.out.println("DBFFileReader: Entry: "+ze.getName());

				if(debug)System.out.println("DBFFileReader: Getting entry");



				if(ze.getName().toLowerCase().endsWith(".dbf"))

				{

					if(debug)System.out.println("DBFFileReader: Found .dbf in zip file");

					size = (int)ze.getSize();

					byte [] buf = new byte [size];

					byte [] in = new byte[size];



					int n;

					int index = 0;



					while ((n = zis.read(in)) > -1)

					{

						System.arraycopy(in, 0, buf, index, n);

						index+=n;

					}



					zis.closeEntry();

					ByteArrayInputStream bais = new ByteArrayInputStream(buf);

					dbf = new Dbf(bais);

					dbfZip = true;

					if(debug)System.out.println("DBFFileReader: DBF done");

				}

			}

			if(dbfZip){System.out.println("DBFFileReader: Zip file version used "+name); return;}

		}



	}

	catch(ZipException ze){System.err.println("DBFFileReader: zip read failed "+ze);}

	catch(IOException ie){System.err.println("DBFFileReader: zip read not possible, looking for .dbf ");}

	catch(Exception e2){System.err.println("DBFFileReader: General exception in zip read ");}



	if(!dbfZip)

	{

		if(debug)System.out.println("DBFFileReader: Looking for files outside of zip file");

		try{

			String noExt = base.getFile();

			if(noExt.toLowerCase().endsWith(".zip")){

				noExt = name.substring(0,name.length()-4);

			}

	 		String ext = ".dbf";

			URL dbfURL = new URL(base.getProtocol(),base.getHost(),base.getPort(),noExt+ext);

			dbf = new Dbf(dbfURL);

		}

		catch(Exception e)

		{

			System.err.println("DBFFileReader: Unable to locate or load a .dbf file either in a zip file or the file self");

			System.err.println(e);

		}

	}

	this.init();

}

/**

 * TODO add descriptive comment

 * Creation date: (11/24/00 5:08:41 PM)

 * @return int[]

 */

private int[] getIds() throws IOException,DbfFileException{

	Float fids[];

	int ids[];

	int count;

	if(dbf.getFieldType(idCol)=='N')

	{

		fids = dbf.getFloatCol(idCol);

		count = fids.length;

		ids = new int[count];

		for(int i=0;i<count;i++)

		{

			ids[i]=fids[i].intValue();

		}

	}

	else

	{

		if(debug)System.out.println("DBFFileReader: ID column given is not numerical, using sequential ids");

		count = dbf.getLastRec();



		ids = new int[count];

		for(int i=0;i<count;i++)

		{

			ids[i]=i+1;

		}

	}

	if(debug)System.out.println("DBFFileReader: DBF file contains "+count+" records");

	return ids;

}

/**

 * This function creates a theme from the points in the dbf file.

 * It sets the name of the theme to the name of the base

 * of the file ( the filename without extension)

 * Creation date: (11/24/00 4:18:55 PM)

 * @return uk.ac.leeds.ccg.geotools.Theme

 */



public Theme getTheme() {

	if(debug)System.out.println("DBFFileReader: Starting with getTheme()");

	Layer l = null;

	l=readPoints();



	Theme t = new Theme(l);

	t.setName(name);



	if(debug)System.out.println("DBFFileReader: Finished with getTheme()");

	return t;

}

public Layer getLayer() {

	if(debug)System.out.println("DBFFileReader: Starting with getTheme()");

	Layer l = null;

	l=readPoints();

        return l;
}

/**

 * This method is called by all constructors, and does some standard init stuff

  * Creation date: (12/7/00 3:33:40 PM)

 */

private void init() {

	if(debug)System.out.println("DBFFileReader: running init()");



	try

	{

		ids = this.getIds();

	}

	catch(IOException e)

	{

		System.err.println("DBFFileReader: Error getting ID's");

		System.err.println(e);

	}

	catch(DbfFileException e)

	{

		System.err.println("DBFFileReader: Error getting ID's");

		System.err.println(e);

	}



}

/**

 * Currently, this function funtions as a wrapper for the readData(int col) function

 * What this function does is call readData(idCol). There is no support for reading

 * and storing more fields at this time.

 * Creation date: (11/24/00 4:56:35 PM)

 * @return uk.ac.leeds.ccg.geotools.GeoData[]

 */

public GeoData[] readData() {

	GeoData data[]=new SimpleGeoData[0];

	try

	{

		data = new SimpleGeoData[1]; // Only one data field support now

		data[0]=readData(idCol);

	}

	catch(Exception e){System.err.println("DBFFileReader error :"+e);}

	return data;

}

/**

 * This method reads all information from a given column in the dbf file

 * and returns it as a geodata object. the data in the geodata object

 * is indexed on the same id as the geopoint objects are stored by the

 * readPoints() function

 * Creation date: (11/24/00 4:56:35 PM)

 * @return uk.ac.leeds.ccg.geotools.GeoData[]

 */

public GeoData readData(int col) {

  SimpleGeoData data = null;

  try

  {

	data=new SimpleGeoData();

	data.setName(dbf.getFieldName(col).toString());

	if(dbf.getFieldType(col)!='N')

	{

	  String s[] = dbf.getStringCol(col);

	  for(int k=0;k<s.length;k++)

	  {

		 data.setText(ids[k],s[k]);

	  }

	}

	else

	{

		Float f[] = dbf.getFloatCol(col);

		for(int k=0;k<f.length;k++)

		{

			data.setValue(ids[k],f[k].doubleValue());

		}

	}

  }

  catch(Exception e){System.err.println("DBFFileReader: error :"+e);}

  return data;

}

/**

 * This method does the actual read of the x,y coordinates from the dbf file

 * and places them in as geopoints in a pointlayer

 *

 * Creation date: (11/24/00 4:21:58 PM)

 * @return uk.ac.leeds.ccg.geotools.PointLayer

 */

public PointLayer readPoints() {



        // DEBUG STUFF

        System.gc();

        Runtime rn = Runtime.getRuntime();







        if(debug)System.out.println("DBFFileReader: Reading points");

        if(debug)System.out.println("DBFFileReader: starting with "+rn.freeMemory()+" (free) out of "+rn.totalMemory()+" (total) memory");





        //sort out ids...

	boolean gotIds = false;

	Float xCoordinates[]=null;

	Float yCoordinates[]=null;



	try

	{

		xCoordinates = dbf.getFloatCol(xCol);

		yCoordinates = dbf.getFloatCol(yCol);

	}

	catch(IOException e)

	{

		System.err.println("DBFFileReader: xcoordinates and/or ycoordinates could not be read from .dbf file " + e);

	}

	catch(DbfFileException e)

	{

		System.err.println("DBFFileReader: xcoordinates and/or ycoordinates could not be read from .dbf file " + e);

	}

	catch(NullPointerException e)

	{

		System.err.println("DBFFileReader: null pointer caught");

		e.printStackTrace();

		System.exit(0);

	}

	if(debug)System.out.println("DBFFileReader: creating new map");

	PointLayer map = new PointLayer();



	GeoPoint gp;

	int count = dbf.getLastRec();

	if(debug)System.out.println("DBFFileReader: number of points to add is "+count);

	for(int i = 0;i<count;i++){

		gp = new GeoPoint(ids[i], xCoordinates[i].doubleValue(),yCoordinates[i].doubleValue());

		map.addGeoPoint(gp);

		gp = null;

	}

	if(debug)System.out.println("DBFFileReader: Bounds are :"+map.getBounds());

	if(debug)System.out.println("DBFFileReader: ended with "+rn.freeMemory()+" (free) out of "+rn.totalMemory()+" (total) memory");

	return map;

}

}


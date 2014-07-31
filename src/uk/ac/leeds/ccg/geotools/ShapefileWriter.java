package uk.ac.leeds.ccg.geotools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import uk.ac.leeds.ccg.dbffile.Dbf;
import uk.ac.leeds.ccg.dbffile.DbfFileException;
import uk.ac.leeds.ccg.dbffile.DbfFileWriter;
import uk.ac.leeds.ccg.shapefile.ShapeArc;
import uk.ac.leeds.ccg.shapefile.ShapePoint;
import uk.ac.leeds.ccg.shapefile.ShapePolygon;
import uk.ac.leeds.ccg.shapefile.Shapefile;
import uk.ac.leeds.ccg.shapefile.ShapefileShape;

/**
 * A class to simplify the process of writing an ESRI(r) Shapefile.
 * It can be constructed from a theme or an array of themes.
 * @author James Macgill, Center for computational Geography.
 * @author Ian Turton, Center for computational Geography.
 * $Id: ShapefileWriter.java,v 1.1 2005/09/19 10:31:28 CarstenKessler Exp $ 
 */
public class ShapefileWriter {
	private final static boolean DEBUG=false;
	private final static String DBC="SfW->";
	public Shapefile sf = null;
	public Dbf dbf = null;
	SimpleGeoData sgd,area,perimeter;
	boolean asLine = false;
	ShapefileShape[] sfShapes;
	double[] bbox;
	//best general guess
	private String name = "none";


	/**
	 * Creates and opens a new ShapefileWriter
	 */
	public ShapefileWriter(String base,Theme t)throws IOException,DbfFileException{
		if(DEBUG)System.out.println("constructing ShapefileWriter - single theme"+
		"\nwill identify as "+DBC);
		Theme [] ta =new Theme[1];
		ta[0]= t;
		init(ta);
		write(base);

	}
	public ShapefileWriter(String base,Theme[] t)throws IOException,DbfFileException{
		if(DEBUG)System.out.println("constructing ShapefileWriter - multiple theme"+
		"\nwill identify as "+DBC);
		init(t);
		write(base);

	}
	private void init(Theme [] t){
		area = new SimpleGeoData();
		area.setName("Area");
		perimeter = new SimpleGeoData();
		perimeter.setName("perimeter");


		ShapeLayer l[] = new ShapeLayer[t.length];
		for(int k=0;k<t.length;k++){
			l[k] = (ShapeLayer)t[k].getLayer();
		}
		GeoRectangle rec = new GeoRectangle();
		int count = 0;
		for(int i=0;i<t.length;i++){
			//get the shapes
			l[i] = (ShapeLayer)t[i].getLayer();
			rec.add(l[i].getBounds());
			if(DEBUG)System.out.println(DBC+"theme "+i+" shapes "+l[i].countFeatures());
			count+=l[i].countFeatures();
			if(l[i] instanceof LineLayer){
				asLine = true;
			}
		}
		//set up the bounds
		bbox = convertBounds(rec);
		ShapefileShape temp;
		if(DEBUG)System.out.println(DBC+"total count ="+count);
		sfShapes = new ShapefileShape[count];


		int i=0;
		sgd = new SimpleGeoData();
		if(t[0].getGeoData()!=null)sgd.setName(t[0].getGeoData().getName());
		SimpleGeoData newold = new SimpleGeoData();
		SimpleGeoData layers = new SimpleGeoData();
		for(int k=0;k<t.length;k++){
			//construct the ShapefileShapes
			GeoData gd = t[k].getGeoData();
			if(gd!=null){
				if(DEBUG)System.out.println(DBC+gd+" "+gd.getSize());
				int id;
				Enumeration e = gd.getIds();
				while(e.hasMoreElements()){
					id=((Integer)e.nextElement()).intValue();
					if(DEBUG)System.out.println("id "+id+" "+i+" "+gd.getText(id));
					if(gd.getDataType()!=GeoData.CHARACTER){
						sgd.setValue(i,gd.getValue(id));
					}else{
						sgd.setText(i,gd.getText(id));
					}
					newold.setValue(i,id);
					layers.setValue(i,k);
					i++;
				}
			}else{ // no geodata - improvise!
				if(DEBUG)System.out.println(DBC+"improvising "+l[k].countFeatures()+" ids");
				for(int j=0;j<l[k].countFeatures();j++){
					sgd.setValue(i,j);
					newold.setValue(i,j); // removed j+1 - ????
					layers.setValue(i,k);
					i++;
				}
			}

		}
		i=0;
			if(DEBUG)System.out.println(DBC+"now extracting "+sgd.getSize()+" shapes");
			Enumeration e = sgd.getIds();
			int id,newid,layerid;
			while(e.hasMoreElements()){
				newid=((Integer)e.nextElement()).intValue();
				id=(int)newold.getValue(newid);
				layerid = (int)layers.getValue(newid);
				GeoShape item = (GeoShape)l[layerid].getGeoShape(id);
				if(item==null){
					System.err.println(DBC+"theme "+layerid+" problem with shape "+id);
					continue;
				}
				if(item instanceof GeoPoint){
					sfShapes[i] = convertGeoShape((GeoPoint)item);
				}
				if(item instanceof GeoLine){
					sfShapes[i] = convertGeoShapeAsLine((GeoLine)item);
				}
				if(item instanceof GeoPolygon){
					if(asLine){
						sfShapes[i] = convertGeoShapeAsLine((GeoPolygon)item);
					}
					else{
						area.setValue(newid,((GeoPolygon)item).getArea());
						perimeter.setValue(newid,((GeoPolygon)item).getPerimeter());
						sfShapes[i] = convertGeoShape((GeoPolygon)item);
					}
				}
				i++;
			}
			if(i==0&&count!=0){
				if(DEBUG)System.out.println(DBC+"looks like no data");
				// probably no data 
				for(i=0;i<count;i++){
					GeoShape item = (GeoShape)l[0].getGeoShape(i);
					if(item==null){
						System.err.println(DBC+i+" is null? ");
						continue;
					}
					if(item instanceof GeoPoint){
						sfShapes[i] = convertGeoShape((GeoPoint)item);
					}
					if(item instanceof GeoLine){
						sfShapes[i] = convertGeoShapeAsLine((GeoLine)item);
					}
					if(item instanceof GeoPolygon){
						if(asLine){
							sfShapes[i] = convertGeoShapeAsLine((GeoPolygon)item);
						}
						else{
							area.setValue(i,((GeoPolygon)item).getArea());
							perimeter.setValue(i,((GeoPolygon)item).getPerimeter());
							sfShapes[i] = convertGeoShape((GeoPolygon)item);
						}
					}
				}
			}
			if(DEBUG)System.out.println(DBC+i+" = "+count+"?");
	}
	private void write(String base)throws IOException,DbfFileException{
		Shapefile shapefile;
		OutputStream shp = new FileOutputStream(base+".shp");
		OutputStream shx = new FileOutputStream(base+".shx");
		File dbffile = new File(base+".dbf");
		if(DEBUG)System.out.println(DBC+"writing "+(sfShapes.length)+" shapes"+
		" of type "+sfShapes[0].getShapeType());
		shapefile = new Shapefile(sfShapes[0].getShapeType(),bbox,sfShapes);
		if(DEBUG)System.out.println(DBC+"writing .shp to "+shp);
		shapefile.writeShapefile(shp);
		if(DEBUG)System.out.println(DBC+"writing .shx to "+shx);
		shapefile.writeIndex(shx);
		DbfFileWriter dbf = new DbfFileWriter(""+dbffile);
		GeoData [] out;
		if(sfShapes[0].getShapeType()==Shapefile.POLYGON){
			if(DEBUG)System.out.println(DBC+" all three!");
			out=new GeoData[3];
			out[0] = area;
			out[1] = perimeter;
			out[2] = sgd;
		} else{
			if(DEBUG)System.out.println(DBC+" just one!");
			out=new GeoData[1];
			out[0] = sgd;
		}

		dbf.writeGeoDatas(out);

		//  shapefile.readIndex(test);

	}


	public ShapePoint convertGeoShape(GeoPoint p){
		return new ShapePoint(p.x,p.y);
	}

	public double[] convertBounds(GeoRectangle bounds){
		double bbox[] = new double[4];
		bbox[0] = bounds.x;
		bbox[1] = bounds.y;
		bbox[2] = bounds.x+bounds.width;
		bbox[3] = bounds.y+bounds.height;
		return bbox;
	}


	public ShapePolygon convertGeoShape(GeoPolygon p){
		int partCount = p.getNumParts();
		int totalPoints = 0;
		for(int i=0;i<partCount;i++){
			totalPoints+=((GeoPolygon)p.getPart(i)).getNPoints();
		}
		ShapePoint[] points = new ShapePoint[totalPoints];
		int parts[] = new int[partCount];
		int index = 0;
		for(int i=0;i<p.getNumParts();i++){
			parts[i] = index;
			Vector partPoints = p.getPart(i).getPoints();
			for(int j=0;j<partPoints.size();j++){
				points[index] = convertGeoShape((GeoPoint)partPoints.elementAt(j));
				index++;
			}
		}
		double[] bbox = convertBounds(p.getBounds());
		return new ShapePolygon(bbox,parts,points);
	}

	public ShapeArc convertGeoShapeAsLine(GeoPolygon p){
		int partCount = p.getNumParts();
		int totalPoints = 0;
		for(int i=0;i<partCount;i++){
			totalPoints+=((GeoPolygon)p.getPart(i)).getNPoints();
		}
		ShapePoint[] points = new ShapePoint[totalPoints];
		int parts[] = new int[p.getNumParts()];
		int index = 0;
		for(int i=0;i<p.getNumParts();i++){
			parts[i] = index;
			Vector partPoints = p.getPart(i).getPoints();
			for(int j=0;j<partPoints.size();j++){
				points[index++] = convertGeoShape((GeoPoint)partPoints.elementAt(j));
			}
		}
		double[] bbox = convertBounds(p.getBounds());
		return new ShapeArc(bbox,parts,points);
	}




	public static void main(String[] args)throws Exception{
		//testing method
		    if(args.length!=2){
			System.out.println("ShapefileWriter infile outfile");
			System.exit(2);
		}
		File file = new File(args[0]);
		URL url = new URL("file://"+file);
		if(DEBUG)System.out.println(DBC+"Loading from "+file);
		ShapefileReader read = new ShapefileReader(url);
		Theme t = read.getTheme();
		SimpleGeoData data = (SimpleGeoData)read.readData("city");
		t.setGeoData(data);
		ShapefileWriter writer = new ShapefileWriter(args[1],t);
		if(DEBUG)System.out.println(DBC+"Done");
		//read = new ShapefileReader(new URL("file:///"+file));
	}
}

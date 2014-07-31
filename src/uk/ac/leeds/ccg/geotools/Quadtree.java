package uk.ac.leeds.ccg.geotools;

//import java.util.Vector;
//import java.util.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

public class Quadtree {

	private final static boolean DEBUG=false;
	boolean top = false,clean=true;
	int count=0;
	int depth=0;
	int MaxDepth=7;
	int cacheSize = 20;
	double minSize = 2000.0;
	GeoRectangle bounds,tbounds;
	GeoRectangle r[] = new GeoRectangle[4];
	Quadtree q[] = new Quadtree[4];
	Vector shapes = null;
	public Quadtree(){
		this(null,0);
		top=true;

	}
	private Quadtree(GeoRectangle b,int d){
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.geotools.Quadtree constructed. Will identify itself as Q--->");
		//System.out.println("Q--->new qt");
		setBounds(b);
		//if(b==null) return;
		//double len = Math.max(b.height,b.width);
		//while(len/MaxDepth>minSize) MaxDepth--;
		//System.out.println("Q--->Dynamic depth = "+MaxDepth);
		depth=d+1;
		//if(b!=null)System.out.println("Q--->Depth "+depth+" "+b.width);
	}

	public void setBounds(GeoRectangle b){
		bounds=b;
		r=split(b);
	}
	public void resetBounds(GeoRectangle b){
		bounds=new GeoRectangle(b);
		clean=false;
		count++;
		if(count>cacheSize) resetBounds();
	}
	public void resetBounds(){
		r=split(bounds);
		if(DEBUG)System.out.println("Q--->Rebuilding "+shapes.size());
		Vector tmp = new Vector(shapes);
		shapes=new Vector(tmp.size(),cacheSize);
		for(int i=0;i<tmp.size();i++){
			if(DEBUG)System.out.println("Q--->reading "+i+" id:"+((GeoShape)tmp.elementAt(i)).getID());
			add((GeoShape)tmp.elementAt(i));
		}
		clean=true;
		count=0;
	}

	public GeoRectangle getBounds(){
		if(bounds==null) return null;
		return new GeoRectangle(bounds);
	}

	public Object elementAt(int i){
		return shapes.elementAt(i);
	}
	final public void addGeoShape(GeoShape s){
		add(s);
	}
	public void add(GeoShape s){
		//System.out.println("Q--->"+top+" adding "+" "+s.getID()+" "+getBounds());
		if(shapes==null) shapes=new Vector();
		shapes.addElement(s);
		//if(top)System.out.println("Q--->Size "+shapes.size()+" "+clean);
		if(getBounds()==null){
			//System.out.println("Q--->Null bounds");
			resetBounds(s.getBounds());
			return;
		}
		if(top&&!s.getBounds().isContainedBy(getBounds())){ 
			//System.out.println("Q--->out of extent ");
			//System.out.println("Q--->"+s.getBounds().createIntersect(bounds));
			// its outside the exent of the quadtree
			GeoRectangle g = new GeoRectangle(bounds.getX(),bounds.getY(),bounds.getWidth(),bounds.getHeight());
			GeoRectangle b=s.getBounds();
			g.add( new GeoRectangle(b.getX(),b.getY(),b.getWidth(),b.getHeight()));
			//System.out.println("Q--->"+g);
			resetBounds(g);
			return;
		}
		if(depth==MaxDepth||Math.min(bounds.getHeight(),bounds.getWidth())<minSize){
			//System.out.println("Q--->too small - returning");
			return;
		}
		for(int i=0;i<4;i++){
			if(r[i]!=null&&r[i].intersects(s.getBounds())){
				//System.out.println("Q--->intersect "+i);
				if(q[i]==null)q[i]=new Quadtree(r[i],depth);
				q[i].add(s);
			}
		}
	}

	public GeoShape find(GeoPoint p){
		if(!clean) resetBounds();
		if(depth==MaxDepth||Math.min(bounds.getHeight(),bounds.getWidth())<minSize){
			GeoShape s = null;
			for(int i=0;i<shapes.size();i++){
				s=(GeoShape)shapes.elementAt(i);
				if(s.contains(p)) return s;
			}
			return null;
		}
		for(int i=0;i<4;i++){
			if(q[i]==null) break;
			if(r[i].contains(p)) return q[i].find(p);
		}
		return null;
	}
	public Collection find(GeoRectangle rec){
	   
	    
		if(!clean) resetBounds();
		HashSet h = new HashSet();
		if(bounds.isContainedBy(rec)){
		  
			return shapes;
		}
		if(depth==MaxDepth||Math.min(bounds.getHeight(),bounds.getWidth())<minSize){
			GeoShape s = null;
			for(int i=0;i<shapes.size();i++){
				s=(GeoShape)shapes.elementAt(i);
				if(s.isContainedBy(rec)||s.intersects(rec)) h.add(s);
			}
			
			return h;
		}
		for(int i=0;i<4;i++){
			if(q[i]==null) break;
			if(r[i].intersects(rec)) h.addAll(q[i].find(rec));
		}
	
		return h;
	}


		
	private GeoRectangle[] split(GeoRectangle r){
		GeoRectangle [] out= new GeoRectangle[4];
		q = new Quadtree[4];
		System.gc();

		if(r==null) return out;
		//System.out.println("Q--->Splitting "+r);
		double w = r.getWidth();
		double h = r.getHeight();
		double x = r.getX();
		double y = r.getY();

		out[0]= new GeoRectangle(x,y,w/2.0,h/2.0);
		out[1]= new GeoRectangle(x+w/2.0,y,w/2.0,h/2.0);
		out[2]= new GeoRectangle(x+w/2.0,y+h/2.0,w/2.0,h/2.0);
		out[3]= new GeoRectangle(x,y+h/2.0,w/2.0,h/2.0);

		return out;
	}

/*	public static void main(String[] args){
		ShapefileReader sr= null;
		try{
		sr = new ShapefileReader(new
			URL("http://www.ccg.leeds.ac.uk/ian/errorsda2"));
		}catch(Exception e){}
		PolygonLayer pl = sr.readPolygons();
		java.util.Vector polys = pl.getShapes();
		GeoRectangle b = pl.getBounds();
		if(DEBUG)System.out.println("Q--->About to build QT");
		Quadtree qt = new Quadtree();
		if(DEBUG)System.out.println("Q--->built QT");
		GeoPolygon pol;
		for(int i=0;i<polys.size();i++){
			pol=(GeoPolygon)polys.elementAt(i);
			if(DEBUG)System.out.println("Q--->adding "+pol.getID());
			qt.add(pol);
		}

		for(int i=0;i<50;i++){
			double x = Math.random()*b.getWidth()+b.getX();
			double y = Math.random()*b.getHeight()+b.getY();
			GeoShape sh = qt.find(new GeoPoint(x,y));
			if(DEBUG)System.out.print("Q--->finding "+x+" "+y+" -> ");
			if(sh!=null)System.out.println("Q--->"+sh.getID());
			else if(DEBUG)System.out.println("Q--->Null");
		}

		Collection fred =
			qt.find(new GeoRectangle(b.getX(),b.getY(),b.width/3.0,b.height/3.0));
		Iterator f = fred.iterator();
		while(f.hasNext())
			System.out.println("Q--->"+(((GeoPolygon)f.next()).getID()));
	if(DEBUG)System.out.println("Q--->PL Bounds "+pl.getBounds());
	if(DEBUG)System.out.println("Q--->qt Bounds "+qt.getBounds());
	}
*/
			

}

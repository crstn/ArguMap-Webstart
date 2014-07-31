package uk.ac.leeds.ccg.geotools;
 
import java.awt.Graphics;
import java.util.Vector;

/** an <strong>experimental</strong> class to hold multiple layers in a
	* single theme. Layers are drawn in the order they are added. 
	* Other than painting this object behaves as a theme with the first layer
	* being used for highlighting/selection etc. Feel free to modify this
	* behaviour if you need.
	* <p>Ian
	*/
public class MultiTheme extends Theme{
	static boolean DEBUG=false;
	static String DBC="MT->";
	Vector layers = new Vector();
	GeoRectangle bounds = new GeoRectangle();

	public MultiTheme(Layer l){
		super(l);
		layers.addElement(l);
		bounds.add(l.getBounds());
	}
	public MultiTheme(Layer l, Shader s){
    super(l,s);
    layers.addElement(l);
		bounds.add(l.getBounds());
  } 
	public MultiTheme(Layer l, Shader s, java.lang.String n){
    super(l,s,n);
    layers.addElement(l);
		bounds.add(l.getBounds());
  } 
	public MultiTheme(Layer l, Shader s, java.lang.String n, HighlightManager
hm) {
    super(l,s,n,hm);
    layers.addElement(l);
		bounds.add(l.getBounds());
  } 
	public MultiTheme(Layer l, Shader s, java.lang.String n, HighlightManager
hm, GeoData d){
    super(l,s,n,hm,d);
    layers.addElement(l);
		bounds.add(l.getBounds());
  } 
  public MultiTheme(Layer l, Shader s, java.lang.String n, HighlightManager hm, GeoData d, GeoData t, ShadeStyle style){
    super(l,s,n,hm,d,t,style);
    layers.addElement(l);
		bounds.add(l.getBounds());
  } 
  public MultiTheme(Layer l, Shader s, java.lang.String n, HighlightManager
hm, GeoData d, ShadeStyle style){
    super(l,s,n,hm,d,style);
    layers.addElement(l);
		bounds.add(l.getBounds());
  } 
	protected void paintScaled(Graphics g,Scaler scale){
		GeoGraphics gg = new GeoGraphics(g,scale,shade,data,null, style,filter,1);
		GeoRectangle ext = scale.getMapExtent();
		for(int i=0;i<layers.size();i++){
			if(DEBUG)System.out.println(DBC+getName()+" Drawing layer "+i);
			if(((Layer)layers.elementAt(i)).getBounds().intersects(ext))
				((Layer)layers.elementAt(i)).paintScaled(gg);
		}
	}
	public void addLayer(Layer l){
		bounds.add(l.getBounds());
		layers.addElement(l);
		l.addLayerChangedListener(this);
	}
	public void removeLayer(Layer l){
		layers.remove(l);
	}
	public Vector getLayers(){
		return layers; // possibly should clone here?
	}
	public GeoRectangle getBounds(){
		return bounds;
	}
}


package uk.ac.leeds.ccg.geotools;

/** A class to handle Level Of Detail filtering in themes.
  * A GeoData with values between 0-9 for each feature in the theme is 
	* provided. Items with a value of 0 are always displayed, items with a
	* value of 9 are only displayed when the user has zoomed in a lot.
	* At present there is a degree of abitariness about when something is
	* displayed. It seems all right to me but others may want to develop better
	* methods.
	* @author Ian Turton
	*/
public class LODFilter extends SimpleFilter{
	static final boolean DEBUG=false;
	static final String DBC="LODf>";
	GeoData data;
	Scaler scale;
	Viewer view;
	double lod,len,ratio;
	int den;
	GeoRectangle rec;
	/** Constructor 
	 */
	public LODFilter(GeoData g,Scaler s){
		data=g;
		scale=s;
	}
	public LODFilter(GeoData g,Viewer v){
		data=g;
		view=v;
	}
	static final double levels[] = {100,120,150,170,200,300,400,500,1000,2000,4000};
	public final boolean isVisible(int id){
		lod = data.getValue(id);
		if(lod<=0) return true;
		if(scale!=null){
			lod = (10-lod)*10;
			len = scale.toGraphics(lod);
			//System.out.println("lod "+lod+"-> "+len+" "+(len >= 1.0));
			return (len >= 1.0);
		}
		if(view!=null){
			double perc = view.getZoomAsPercent();
                        //System.out.println("lod "+lod+" per "+perc);
			if(lod>=levels.length) return perc>4000+lod*100.0;
			return perc > levels[(int)lod];
		}
		return false;

	}
        
        public Object clone() {
            LODFilter c = new LODFilter(data,scale);//should possible clode data and scale as well
            return c;
        }        
        
}

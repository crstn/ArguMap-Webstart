package uk.ac.leeds.ccg.geotools;


public class ValueFilter extends uk.ac.leeds.ccg.geotools.SimpleFilter
{
    private final boolean debug=false;
	GeoData data = new SimpleGeoData();
	double val;
	double tol=0.0001;
	public ValueFilter(){
		val=0;
		data.setMissingValueCode(val);
	}
	
	public ValueFilter(GeoData dat,double v){
		data=dat;
		val=v;
	}
	public void setValue(double v){
		val=v;
		if(debug)System.out.println("seting value in filter");
		notifyFilterChangedListeners(FilterChangedEvent.DATA);
	}
	public void setTolerance(double t){
		tol=t;
	}
	public void setBegin(double v){
		val=v;
	}
	public void setFinish(double v){
		val=v;
	}
	public double getBegin(){
		return val;
	}
	public double getFinish(){
		return val;
	}
	public boolean isVisible(int id){
		double d=data.getValue(id);
		return(Math.abs(d-val)<tol);
	}
        
        public Object clone() {
            ValueFilter c = new ValueFilter(data,val);
            c.setTolerance(this.tol);
            return c;
        }        
        
}
package uk.ac.leeds.ccg.geotools;

public class RangeFilter extends SimpleFilter implements Cloneable{
    GeoData data;
    double min,range;
    public RangeFilter(GeoData d,double min,double range){
        this.min = min;
        this.range = range;
        
        data = d;
    }
    public boolean isVisible(int id){
        double value = data.getValue(id);
        
        return value>=min && value<=min+range;
    }
    public void setRange(double r){
        range =r;
        this.notifyFilterChangedListeners(ThemeChangedEvent.DATA);
   //     System.out.println("Range has been set to"+r);
    }
    public void setMin(double min){
        this.min = min;
        this.notifyFilterChangedListeners(ThemeChangedEvent.DATA);
        
    }
    
    public String getHeader(){
        return "RF"+data.getName()+"startPoint,RF"+data.getName()+"span";
    }
    
    public String getAsRow(){
        return ""+min+","+range;
    }
    
    public double getMin(){
        return min;
    }
    
    public double getRange(){
        return range;
    }
    
    public String toString(){
        return (""+data.getName() +"in range ["+(min)+":"+(min+range)+"]");
    }
    
    public Object clone(){
        RangeFilter f = new RangeFilter(data,min,range);
        return f;
    }
}



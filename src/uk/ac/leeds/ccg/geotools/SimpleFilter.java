package uk.ac.leeds.ccg.geotools;
import java.io.Serializable;
import java.util.Vector;

public abstract class SimpleFilter implements Filter,Serializable{
	
	Vector listeners = new Vector();
   
    public void removeFilterChangedListener(FilterChangedListener lcl)
    {
       listeners.removeElement(lcl); 
    }
    public void addFilterChangedListener(FilterChangedListener lcl)
    {
        listeners.addElement(lcl);
    }
    
    public void notifyFilterChangedListeners(int reason){
    Vector l;
            FilterChangedEvent lce = new FilterChangedEvent(this,reason);
            synchronized(this) {l = (Vector)listeners.clone(); }
            
            for (int i = 0; i < l.size();i++) {
                ((FilterChangedListener)l.elementAt(i)).filterChanged(lce);
            }   
    }
    
    public String getHeader(){
        return "noInfo";
    }
    
    public String getAsRow(){
        return "na";
    }

    public abstract Object clone();
	
	
}

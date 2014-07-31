package uk.ac.leeds.ccg.geotools;

import java.util.Vector;

public class IDFilter extends uk.ac.leeds.ccg.geotools.SimpleFilter
{
    Vector list;
    NullShape idObj = new NullShape(0);
    
    public boolean isVisible(int id)
    {
        synchronized(idObj){
            idObj.setID(id);
            if(list.contains(idObj)){
                return false;
            }
            return true;
        }
            
        
    }
    
    public synchronized void setIsVisible(int id,boolean visible){
        Integer i = new Integer(id);
        if(visible && list.contains(i)){
            list.removeElement(i);
            return;
        }
        
        if(!visible && !list.contains(i)){
            list.addElement(i);
            return;
        }
        
        
    }
    
    public Object clone() {
        IDFilter c = new IDFilter();
        for(int i=0;i<list.size();i++){
            Integer id = (Integer)list.elementAt(i);
            c.setIsVisible(id.intValue(),false);
        }
        return c;
    }    

}
package uk.ac.leeds.ccg.geotools;

import java.util.Enumeration;
import java.util.Hashtable;
public class Table extends java.lang.Object
{
    int colCount = 0;
    protected Hashtable table;
    public Table()
    {
        table = new Hashtable();
        //Not sure what to do here yet!
    }
    
    public void addCol(String name,Hashtable col)
    {
        table.put(name,col);
        colCount++;
    }
    
    public double getDouble(String col,int id){
        Hashtable h;
        h=(Hashtable)table.get(col);
        Double value = (Double)h.get(new Integer(id));
        if (value != null)
            return value.doubleValue();
        else
            return -9999;
    }
    
    public double[] getMinMax(){
        Hashtable h;
        double value;
        double range[] = {Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY};
        for(Enumeration e = table.elements();e.hasMoreElements();){
            h = (Hashtable)e.nextElement();
            for(Enumeration f = h.elements();f.hasMoreElements();){
                value = ((Double)f.nextElement()).doubleValue();
                range[0] = Math.min(value,range[0]);
                range[1] = Math.max(value,range[1]);
            }
        }    
        return range;
    }
    
    public int getColCount(){
        return colCount;
    }
    
}
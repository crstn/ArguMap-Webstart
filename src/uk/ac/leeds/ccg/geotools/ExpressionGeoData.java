package uk.ac.leeds.ccg.geotools;

import java.util.Enumeration;
public class ExpressionGeoData  implements uk.ac.leeds.ccg.geotools.GeoData
{
    protected GeoData opA,opB;
    protected SimpleGeoData result;
    //only does a ratio at the moment
    public ExpressionGeoData(GeoData a,GeoData b){
        opA = a;
        opB = b;
        result = new SimpleGeoData();
       int id;
        for( Enumeration ids = opA.getIds();ids.hasMoreElements();){
            id = ((Integer)ids.nextElement()).intValue();
            result.setValue(id,(double)a.getValue(id)/(double)b.getValue(id)*100d);
        }
    }
    
    public Enumeration getIds()
    {
        return result.getIds();
    }

    public double getMax()
    {
        return result.getMax();
    }

    public double getMin()
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.GeoData
        // to do: code goes here
        return result.getMin();
    }
    
    public int getDataType(){
        return result.getDataType();
    }

    public void setDataType(int type){
        result.setDataType(type);
    }
    
    public double getMissingValueCode()
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.GeoData
        // to do: code goes here
        return 0.0d;
    }
    
    public int getMissingCount(){
        return result.getMissingCount();
    }

    public String getName()
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.GeoData
        // to do: code goes here
        return null;
    }

    public int getSize()
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.GeoData
        // to do: code goes here
        return result.getSize();
    }

    public String getText(int id)
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.GeoData
        // to do: code goes here
        return null;
    }

    public double getValue(int id)
    {
       return result.getValue(id);
    }

    public void setMissingValueCode(double mv)
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.GeoData
        // to do: code goes here
    }

    public void setName(String name_)
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.GeoData
        // to do: code goes here
    }

}
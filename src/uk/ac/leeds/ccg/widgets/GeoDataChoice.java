package uk.ac.leeds.ccg.widgets;
import uk.ac.leeds.ccg.geotools.GeoData;

public class GeoDataChoice extends java.awt.Choice
{
    private GeoData dataSet[]; 
    public GeoDataChoice(GeoData[] data){
        dataSet = data;
        for(int i=0;i<dataSet.length;i++){
            add(dataSet[i].getName());
        }
        
    }
    
	public GeoData getSelectedGeoData()
	{
		return dataSet[getSelectedIndex()];
	}


}
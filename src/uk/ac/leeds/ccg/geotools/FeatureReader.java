/*
 * FeatureReader.java
 *
 * Created on 11 March 2001, 13:38
 */

package uk.ac.leeds.ccg.geotools;

/**
 *
 * @author  James Macgill
 * @version 
 */
public interface FeatureReader {
    public Theme getTheme();
    public Layer getLayer();
    public GeoData[] readData();
    public GeoData readData(int colNumber);
}


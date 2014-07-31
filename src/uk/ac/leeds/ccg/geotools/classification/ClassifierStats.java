/*
 * ClassifierStats.java
 *
 * Created on March 29, 2001, 4:41 PM
 */

package uk.ac.leeds.ccg.geotools.classification;
import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoDataUtils;
/**
 *
 * @author  jamesm
 * @version 
 */
public class ClassifierStats  {

    /**
     * Counts how many of the values in the GeoData fall into the specified bin.
     *
     * @return int The number of values which fell into the specified bin.
     * @param classifier The classifier to use when binning.
     * @param data A GeoData containing the values to sort into bins.
     * @param binNumber The id of the bin to count values in.
     *
     * @author jamesm
     *
     * TODO Implementation of this method is VERY poor in terms of performance.
     */
    public static int countForBin(Classifier classifier,GeoData data,int binNumber){
        int[] all = countForEachBin(classifier,data);
        return all[binNumber];
    }
    
    /**
     * Puts all of the values in the GeoData into bins and counts how many values
     * fall into each bin.
     *
     * @return int[] An array of counts with a value for each bin.
     * @author jamesm
     * 
     * TODO Curent implementation is poor and does not take advantage of sorting
     **/
    public static int[] countForEachBin(Classifier classifier,GeoData data){
        ArrayList sorted = GeoDataUtils.sort(data);
        Iterator iterator = sorted.iterator();
        int counts[] = new int[classifier.getBinCount()];
        Double value;
        int binIndex;
        while(iterator.hasNext()){
            value = (Double)iterator.next();
            binIndex = classifier.classify(value.doubleValue());
            if(binIndex>=0){
                counts[binIndex]++;
            }
        }
        return counts;
    }
    
}

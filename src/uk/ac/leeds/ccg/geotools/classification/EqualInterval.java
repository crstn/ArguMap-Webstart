/*
 * Equalinterval.java
 *
 * Created on March 29, 2001, 12:05 PM
 */

package uk.ac.leeds.ccg.geotools.classification;
import java.util.ArrayList;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoDataUtils;

/**
 * This classification splits the range of data into equal sized bins.
 * Some bins may contain no values.
 * @author  jamesm
 * @version
 */
public class EqualInterval extends SimpleClassifier {
    
    /** Creates new Equalinterval */
    public EqualInterval(GeoData source, int binCount) {
        buildBins(source,binCount);
    }
    
    private void buildBins(GeoData data,int binCount){
        
        bins.clear();
        Bin bin;
        
        ArrayList list = GeoDataUtils.sort(data);
        
        System.out.println("Using Equal Interval");
        System.out.println("Number of categories desired "+binCount);
        // (NB. list.size()=data.getSize())
        System.out.println("Number of records to assign "+data.getSize());
        double thisDataValue=data.getMissingValueCode();
        double nextDataValue=data.getMissingValueCode();
        int numberOfDifferentValues=0;
        for (int j=1;j<=list.size();j++){
            thisDataValue=((Double)list.get(j-1)).doubleValue();
            if(thisDataValue!=data.getMissingValueCode()){
                if(thisDataValue!=nextDataValue){
                    numberOfDifferentValues=numberOfDifferentValues+1;
                    nextDataValue=thisDataValue;
                }
            }
        }
        System.out.println("Number of different data values to assign "+numberOfDifferentValues);
        System.out.println("Missing value code "+data.getMissingValueCode());
        System.out.println("Number of missing values "+data.getMissingCount());
        System.out.println("Minimum value "+data.getMin());
        System.out.println("Maximum value "+data.getMax());
        
        // Classify missing values
        
        if(data.getMissingCount()>0)bins.add(new Bin(data.getMissingValueCode(),data.getMissingValueCode()));
        
        // Handle case where number of categories desired cat is >= to the number of different data values to assign
        if (binCount>=numberOfDifferentValues){
            System.out.println("The number of different data values to assign is greater than the number of categories desired. Why not try specifying fewer categories or use a different classification?");
        }
        
        double length = (data.getMax()-data.getMin())/(double)binCount;
        thisDataValue=data.getMin();
        for(int i=1;i<=binCount;i++){
            if(i==binCount){
                //bin = new Bin(thisDataValue,data.getMax()+Double.MIN_VALUE);
                //bin = new Bin(thisDataValue,data.getMax()+Long.MIN_VALUE);
                //This is so the upper_exclusion of the top bin is set high enough to include the maximum data value.
                //It may be better to have a special case of bin so that the first and last bins can be open ended. For
                //example, 7 and under, or 60+
                //Adding a small number is a temporary fix for now like that in DIFFERENCE and QUANTILE
                bin = new Bin(thisDataValue,data.getMax()+0.0000001);
                //System.out.println("Last bin "+thisDataValue+" "+(data.getMax()+0.0000001)+" "+data.getMax());
                //System.out.println("Testing "+bin.contains(data.getMax()));
            }
            else{
                bin = new Bin(thisDataValue,data.getMin()+(i*length));               
            }
            int counter=0;
                /*if(showInCatCount){
                    for (int j=1;j<=list.size();j++){
                        nextDataValue=((Double)list.get(j-1)).doubleValue();
                        if (nextDataValue!=data.getMissingValueCode()){
                            if (nextDataValue>=thisDataValue && nextDataValue<=data.getMin()+(i*length)){
                                counter=counter+1;
                            }
                        }
                    }
                    catName=catName+" ("+counter+")";
                }*/
            
            
            bins.add(bin);
            thisDataValue = data.getMin()+(i*length);
        }
    }
}

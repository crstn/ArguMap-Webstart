/*
 * Quantile.java
 *
 * Created on March 28, 2001, 2:58 PM
 */

package uk.ac.leeds.ccg.geotools.classification;
import java.util.ArrayList;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoDataUtils;
/**
 * This classification assigns each class a quota of values given by the number of data values
 * divided by the number of catogories desired plus a dealt remainder if there is any. Ordered
 * data values are then assigned to each class in the given quotas. This is not strictly quantiles
 * which should be evenly distributed about the median.
 * If cat is greater than the number of different data values to assign then each different data value
 * is coloured uniquely and the key displays the value in [] and the number of such data values in ()
 * if showInCatCount is true.  If cat is less than the number of different data values to assign then
 * categories are created and the key displays; the range of a class in [], and the number of data
 * values in the class in () if showInCatCount is true.
 * @author  jamesm,andyt
 * @version 0.1
 */
public class Quantile extends SimpleClassifier implements Classifier {

    /** Creates new Quantile */
    public Quantile(GeoData source,int binCount) {
        buildBins(source,binCount);
    }
    
    private void buildBins(GeoData data,int binCount){
        bins.clear();
        Bin bin;
        
        ArrayList list = GeoDataUtils.sort(data);
        
        System.out.println("Number of categories desired "+binCount);
        double thisDataValue,nextDataValue;
        System.out.println("Number of records to assign "+data.getSize());
        thisDataValue=data.getMissingValueCode();
        nextDataValue=data.getMissingValueCode();
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
       
        // Handle case where number of categories desired cat is >= to the number of different data values to assign
        int counter=0;
        if (binCount>=numberOfDifferentValues){
         
            for(int i=1;i<=binCount;i++){
                if (i>(list.size()-data.getMissingCount())){
                    break;
                }
                thisDataValue=((Double)list.get(i-1)).doubleValue();
                counter=counter+1;
                if(thisDataValue!=nextDataValue){
                    // Because no setCat() method for controlling cat colourFactor is used to assign sensible colours
                    bins.add(new Bin(thisDataValue,thisDataValue));
                    counter=0;
                }
                nextDataValue=thisDataValue;
            }
        }
        else{
            //System.out.println("double division = "+((double)(data.getSize()-data.getMissingCount())/(double)cat));
            //System.out.println("integer division = "+((data.getSize()-data.getMissingCount())/cat));
            int leftOver=(data.getSize()-data.getMissingCount())-binCount*((data.getSize()-data.getMissingCount())/binCount);
            System.out.println("number left over="+leftOver);
            thisDataValue=data.getMin();
            int quanta = (data.getSize()-data.getMissingCount())/binCount;
            System.out.println("quanta="+quanta);
            for(int i=1;i<=binCount;i++){
                if(i==binCount){
                    //bin = new Bin(thisDataValue,data.getMax()+Double.MIN_VALUE);
                    //bin = new Bin(thisDataValue,data.getMax()+Long.MIN_VALUE);
                    //This is so the upper_exclusion of the top bin is set high enough to include the maximum data value.
                    //It may be better to have a special case of bin so that the first and last bins can be open ended. For
                    //example, 7 and under, or 60+
                    //Adding a small number is a temporary fix for now like that in DIFFERENCE and EQUAL_INTERVAL
                    bin = new Bin(thisDataValue,data.getMax()+0.0000001);
                    //System.out.println("Last bin "+thisDataValue+" "+(data.getmax()+0.0000001)+" "+data.getMax());
                    //System.out.println("Testing "+bin.contains(data.getMax()));
                }
                else{
                    if (leftOver>0){
                        nextDataValue=((Double)list.get((i*quanta)+data.getMissingCount()-1)).doubleValue();
                        leftOver=leftOver-1;
                        counter=quanta+1;
                    }
                    else{
                        nextDataValue=((Double)list.get((i*quanta)+data.getMissingCount()-1)).doubleValue();
                        counter=quanta ;
                    }
                    //System.out.println("nextDataValue="+nextDataValue);
                    bin = new Bin(thisDataValue,nextDataValue);
                }
              
                thisDataValue = nextDataValue;
               
                bins.add(bin);
            }
        }
      
    }
    
    
    
}

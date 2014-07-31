/*
 * Difference.java
 *
 * Created on March 29, 2001, 12:16 PM
 */

package uk.ac.leeds.ccg.geotools.classification;
 import java.util.ArrayList;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoDataUtils;
/**
 * This classification calculates the difference between each ordered data value in the sorted list.
 * It then classifies the data using the binCount greatest differences to seperate the classes.
 * If binCount is greater than the number of different data values to assign then each different data value
 * is coloured uniquely and the key displays the value in [] and the number of such data values in ()
 * if showInCatCount is true.  If binCount is less than the number of different data values to assign then
 * categories are created and the key displays; the range of a class in [], and the number of data
 * values in the class in () if showInCatCount is true.
 *
 * @author  jamesm
 * @version
 */
public class Difference extends uk.ac.leeds.ccg.geotools.classification.SimpleClassifier {
   
    
    /** Creates new Difference */
    public Difference(GeoData source,int binCount) {
        buildBins(source,binCount);
    }
    
    private void buildBins(GeoData data, int binCount){
        bins.clear();
        Bin bin;
        
        ArrayList list = GeoDataUtils.sort(data);
        
        System.out.println("Using Difference");
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
        // Handle case where number of categories desired binCount is >= to the number of different data values to assign
        int counter=0;
        if (binCount>=numberOfDifferentValues){
            int colourFactor=binCount/(list.size()-data.getMissingCount());;
            for(int i=1;i<=binCount;i++){
                if (i>(list.size()-data.getMissingCount())){
                    break;
                }
                thisDataValue=((Double)list.get(i-1)).doubleValue();
                counter=counter+1;
                if(thisDataValue!=nextDataValue){
                    // Because no setCat() method for controlling binCount colourFactor is used to assign sensible colours
                    bins.add(new Bin(thisDataValue,thisDataValue));
                    counter=0;
                }
                nextDataValue=thisDataValue;
            }
        }
        else{
            // Calculate the difference between values
            //double averageDifference=0.0;
            thisDataValue=((Double)list.get(0)).doubleValue();
            double differences[] = new double[list.size()];
            for(int i=1;i<list.size();i++){
                nextDataValue=((Double)list.get(i)).doubleValue();
                if (thisDataValue!=data.getMissingValueCode()){
                    differences[i]=nextDataValue-thisDataValue;
                    //averageDifference+=differences[i];
                }
                //System.out.println("Difference "+differences[i]);
                thisDataValue=nextDataValue;
            }
            //if(list.size()!=0) averageDifference=averageDifference/list.size();
            // Store the largest binCount differences largestDifferences[j] and
            // store their list indexes as differenceIndex[j]
            double largestDifferences[] = new double[binCount];
            int differenceIndex[] = new int[binCount];
            for (int j=0;j<binCount;j++){
                largestDifferences[j] = Double.NEGATIVE_INFINITY;
                differenceIndex[j]=-1;
            }
            for(int j=0;j<binCount;j++){
                for(int i=0;i<list.size();i++){
                    if(largestDifferences[j]<differences[i]){
                        largestDifferences[j]=differences[i];
                        differenceIndex[j]=i;
                    }
                }
                differences[differenceIndex[j]]=Double.NEGATIVE_INFINITY;
                //System.out.println("index "+differenceIndex[j]+" difference "+largestDifferences[j]);
            }
            
            // Sort differencesIndex[j] and store the sortedDifferenceIndex[j] so the key can be added
            // to in the correct order
            int sortedDifferenceIndex[] = new int[binCount];
            int differenceIndex2[] = new int[binCount];
            for (int j=0;j<binCount;j++){
                sortedDifferenceIndex[j]=-1;
                differenceIndex2[j]=-1;
            }
            for(int j=0;j<binCount;j++){
                for(int i=0;i<binCount;i++){
                    if(sortedDifferenceIndex[j]<differenceIndex[i]){
                        sortedDifferenceIndex[j]=differenceIndex[i];
                        differenceIndex2[j]=i;
                    }
                }
                differenceIndex[differenceIndex2[j]]=-1;
                System.out.println("sorted index "+sortedDifferenceIndex[j]);
            }
            
            thisDataValue=data.getMin();
            // The following two variables are required for the shortcut in calculating
            // the number of data values in each class
            int thisSortedDifferenceIndex=data.getMissingCount();
            //int thisSortedDifferenceIndex=sortedDifferenceIndex[0];
            int nextSortedDifferenceIndex=0;
            double nextDataValue2=data.getMissingValueCode();
            for(int i=1;i<=binCount;i++){
                nextDataValue=((Double)list.get(sortedDifferenceIndex[binCount-i])).doubleValue();
                //System.out.println("nextDataValue="+nextDataValue);
                if(i==binCount){
                    //bin = new Bin(thisDataValue,data.getMax()+Double.MIN_VALUE);
                    //bin = new Bin(thisDataValue,data.getMax()+Long.MIN_VALUE);
                    //This is so the upper_exclusion of the top bin is set high enough to include the maximum data value.
                    //It may be better to have a special case of bin so that the first and last bins can be open ended. For
                    //example, 7 and under, or 60+
                    //Adding a small number is a temporary fix for now like that in EQUAL_INTERVAL and QUANTILE
                    bin = new Bin(thisDataValue,data.getMax()+0.0000001);
                    //System.out.println("Last bin "+thisDataValue+" "+(data.getMax()+0.0000001)+" "+data.getMax());
                    //System.out.println("Testing "+bin.contains(data.getMax()));
                }
                else{
                    bin = new Bin(thisDataValue,nextDataValue);
                }
                /*
                // If required calculate the counts and add classes to the key
                if (showInCatCount){
                    if (data.getMissingValueCode()<data.getMin()){
                        if(i==binCount){
                            nextSortedDifferenceIndex=data.getSize();
                        }
                        else{
                            nextSortedDifferenceIndex=sortedDifferenceIndex[binCount-i-1];
                        }
                        counter=nextSortedDifferenceIndex-thisSortedDifferenceIndex;
                        catName=catName+" ("+counter+")";
                        thisSortedDifferenceIndex=nextSortedDifferenceIndex;
                    }//Shortcut
                    else{
                        counter=0;
                        for (int j=1;j<=list.size();j++){
                            nextDataValue2=((Double)list.get(j-1)).doubleValue();
                            //if (nextDataValue2!=data.getMissingValueCode()){
                            if (thisDataValue!=data.getMissingValueCode()){
                                if (nextDataValue2>thisDataValue && nextDataValue2<=nextDataValue){
                                    counter=counter+1;
                                }
                            }
                        }
                        catName=catName+" ("+counter+")";
                    }
                }*/
                bins.add(bin);
                thisDataValue=nextDataValue;
            }
        }
    }
}

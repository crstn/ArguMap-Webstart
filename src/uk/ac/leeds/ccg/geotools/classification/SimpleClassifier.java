/*
 * SimpleClassifier.java
 *
 * Created on March 28, 2001, 2:21 PM
 */

package uk.ac.leeds.ccg.geotools.classification;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;



/** A trivial implementation of the classifier interface.
 * It is of little use in its own right but it provides a usefull class for other
 * classifiers to extend.
 *
 * @author jamesm
 * @version 0.1
 */
public class SimpleClassifier implements Classifier {

    /** Holds the bins used by this classifier.
     */    
    protected List bins = null;
    
    /** A protected constructor, only for use by subclasses.
     */
    protected SimpleClassifier(){
        bins = new LinkedList();
    }
    
    /** Creates new SimpleClassifier 
     * A classifier with only one bin which contains the range specided by the two paramiters
     * @param lowerInclusion A double value for the lowest number included in the range of this classifier
     * @param upperExclusion A double value for the smallest number NOT to be included in the range of this classifier
     */
    public SimpleClassifier(double lowerInclusion,double upperExclusion) {
        Bin bin = new Bin(lowerInclusion,upperExclusion);
        bins = new LinkedList();
        bins.add(bin);
    }

    /** The full set of bins provided by this classifer, in order from smallest to largest.
     * There must be no overlap between bin ranges.
     * @return A List containing all of the Bins for this classifier
     */
    public List getBins() {
        return bins;//This is perhaps not safe and a clone should be used instead.
    }
    
    /** Finds which bin the specided value falls into.
     * @return the index of the Bin which holds the specified value.  More infomration on the returned bin can be obtained by calling getBin(binNumber);
     * @param value a double to classify by finding the approproate bin.
     */
    public int classify(double value) {
        if(value < ((Bin)bins.get(0)).getLowerInclusion()){
            return BELOW_RANGE;
        }
        Iterator iterate = bins.iterator();
        int i=0;
        Bin bin;
        
        //loop through all bins and test for containment.
        //This could probably be optimised quite a bit with some thought
        while(iterate.hasNext()){
            bin = (Bin)iterate.next();
            if(bin.contains(value)){
                return i;
            }
            i++;
        }
        return ABOVE_RANGE; //This behaviour needs to be thought about carefully.
    }
    
    /** Gets the full range of values that this classifier can return a sane result for.
     * @return Bin A special bin which specifies the full range of values which the classifier can sensibly handle.
     */
    public Bin getValidRange() {
        return new Bin(((Bin)bins.get(0)).getLowerInclusion(),((Bin)bins.get(bins.size()-1)).getUpperExclusion());
    }
    
    /** Get an individual bin by index number.
     * @param binNumber The int index of the bin to retrive.  Bins indexes start from 0 as the bin holding the smallest value.
     * @return Bin, the requested bin.
     */
    public Bin getBin(int binNumber) {
        return (Bin)bins.get(binNumber);
    }
    
    /**
     * @return int The number of seperate bins in this classifier
     **/
    public int getBinCount(){
        return bins.size();
    }
    
}

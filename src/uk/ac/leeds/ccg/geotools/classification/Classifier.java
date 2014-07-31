/*
 * Classifier.java
 *
 * Created on March 28, 2001, 1:49 PM
 */

package uk.ac.leeds.ccg.geotools.classification;

import java.util.List;



/** Defines the Classifier interface.
 * Once construced a classifer specified a set of bins into which numbers fit.
 * Using the methods in this interface is is possible to find out which bin a given number falls into:
 *
 * When implementing this interface there are a number of things to remember.
 * There must be no gaps in the range of values which the classifier suports.
 * You must make sure that the largest value you want to include in the range of valid numbers is
 * actualy included in the last bin of the range.
 *
 *
 * @author jamesm
 * @version 0.1
 * @see For an example of an object which uses classifiers.
 */
public interface Classifier {
    
    /* returned by classify when the specided number is less than the smallest valid value.*/
    public static final int BELOW_RANGE = -1;
    
    /* returned by classify when the specided number is more than the highest valid value.*/
    public static final int ABOVE_RANGE = -2;
    
    /* returned by classify when the specided number matches the missing value code.*/
    public static final int MISSING_VALUE = -3;
    
    
    
    /** The full set of bins provided by this classifer, in order from smallest to largest.
     * There must be no overlap between bin ranges.
     * @return A List containing all of the Bins for this classifier
     */    
    public List getBins();
    
    /** Get an individual bin by index number.
     * @param binNumber The int index of the bin to retrive.  Bins indexes start from 0 as the bin holding the smallest value.
     * @return Bin, the requested bin.
     */    
    public Bin getBin(int binNumber);
    
    /** Gets the full range of values that this classifier can return a sane result for.
     * @return Bin A special bin which specifies the full range of values which the classifier can sensibly handle.
     */    
    public Bin getValidRange();
    
    /** Finds which bin the specided value falls into.
     * @return the index of the Bin which holds the specified value.  More infomration on the returned bin can be obtained by calling getBin(binNumber);
     * @param value a double to classify by finding the approproate bin.
     */    
    public int classify(double value);
    
    /**
     * @return int The number of seperate bins in this classifier
     **/
    public int getBinCount();
    
}


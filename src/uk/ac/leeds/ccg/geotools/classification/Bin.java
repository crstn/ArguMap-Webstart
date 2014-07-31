/*
 * Bin.java
 *
 * Created on March 28, 2001, 1:06 PM
 */

package uk.ac.leeds.ccg.geotools.classification;
import uk.ac.leeds.ccg.geotools.misc.FormatedString;

/** A bin specifies a range of values between two doubles.
 * The range is specified as including everyting from the lower value, which is itself included,
 * through to anything smaller than the higher value, which is not itself included in the range.
 *
 * Bins can be produced by hand but their most comon source a Classifier object which will automaticaly 
 * produce bins from sets of numbers.
 *
 *
 * @author jamesm
 * @version 0.1
 */
public class Bin implements Cloneable, java.io.Serializable{

    /** The smallest value to be included in the range of this Bin
     */    
    protected double lowerInclusion;
    
    /** The smallest value NOT to be included in the range of this bin
     */    
    protected double upperExclusion;
    
    /** Creates new Bin
     * @param lowerInclusion the smalest value to be included in the range of this bin 
     * @param upperExclusion the smalest value NOT to be included in the range of this bin 
     */
    public Bin(double lowerInclusion, double upperExclusion) {
        this.lowerInclusion = lowerInclusion;
        this.upperExclusion = upperExclusion;
    }

    /** Returns a string suitable for labeling this bin.
     * Produced in the form (lowerInclusion : upperExclusion]
     * @return String A text description of the range of this bin
     */    
    public java.lang.String toString() {
        return "("+FormatedString.format(""+lowerInclusion,2).trim()+
                ","+FormatedString.format(""+upperExclusion,2).trim()+"]";
    }
    
    /** Test for equality, only true if range of both bins match exactly.
     *
     * @param obj The object to test equality against. foo bar
     * @return boolean 
     */    
    public boolean equals(java.lang.Object obj) {
        if(obj instanceof Bin){
            if(((Bin)obj).getLowerInclusion() == lowerInclusion && ((Bin)obj).getUpperExclusion() == upperExclusion){
                return true;
            }
        }
        return false;
    }
    
    /** Constructs a new instance of Bin with an identical range to this bin
     * @return Bin a clone of this bin
     * @throws CloneNotSupportedException Never thrown by Bin as it does support cloning. 
     */    
    protected java.lang.Object clone() throws java.lang.CloneNotSupportedException {
        return new Bin(lowerInclusion,upperExclusion);
    }
    
    /** Get the range of this Bin as an array.
     *
     * @return double[] The range of the bin as two doubles, [0] is the smallest value included inthe range whilst [1] is the smallest value NOT included inthe range
     */    
    public double[] getRange() {
       double range[]  = {lowerInclusion,upperExclusion};
       return range;
    }
    
    /** Get the smallest value that would be included in the range.
     *
     * @return double The smallest value included in the range of this bin
     */    
    public double getLowerInclusion() {return lowerInclusion;}
    
    /** Gets the smallest value NOT to be included in the range of this bin.
     * @return double The smallest value NOT to be included in the range of this bin
     */    
    public double getUpperExclusion() {return upperExclusion;}
    
    /** Tests the specified value to see if it falls inside the range of the Bin.
     *
     * @return boolean true only if value >= lowerInclusion  AND < upperExclusion
     *  if value = lowerInclusion = upperExclusion value is considerd to be contained
     * @param value The value to test for containment. 
     */    
    public boolean contains(double value) {
        if(lowerInclusion == upperExclusion && value == lowerInclusion)return true;
        return (value >= lowerInclusion && value < upperExclusion);
    }
    
}

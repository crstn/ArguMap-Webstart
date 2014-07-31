package uk.ac.leeds.ccg.geotools;

import java.util.Comparator;

/**
 * An implementation of the collection Comparator interface that allows
 * for the comparing of objects that implement the IDReferenced interface.
 * This can be used in conjenction with the collection packages that support sorting
 * to provide efficient storage and retrival of features by id.
 * 
 * @since 0.7.7.1 12/May/2000
 * @author James Macgill JM
 */
public class IDComparator implements Comparator
{
    /**
     * Compares the id values of the two objects.<br>
     * Both objects must implement the IDReferenced interface otherwise a ClassCastException will be thrown<p>
     * 
     * @author James Macgill JM
     * @since 0.7.7.1 12/May/2000
     * @param a The first object to be commpared id, must implement IDReferenced. (Note all classes which extend GeoShape do this)
     * @param b The second object to be compared by id, must implement IDReferenced.
     * @return int The result of the comparason, -ve if a has a lower id than b +ve if b has a lower id than a.  
     * A value of 0 indecates that both have the same ID.
     * N.B. This does not imply equality, simply that both features share the same ID value.
     */
    public int compare(Object a,Object b){
    
        return ((IDReferenced)a).getID()-((IDReferenced)b).getID();
    }
}
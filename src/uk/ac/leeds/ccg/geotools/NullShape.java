package uk.ac.leeds.ccg.geotools;

import java.util.Vector;
/**
 * A null shape can be used as an IDReferenced object without the need for any
 * feature information.<br>
 * A null shape might be used as a placeholder for a missing or non existent feature, but
 * its main use is for quickly seting up an IDReferenced object that others can be compared against.
 * <br>For example, to perform a binarySearch on a sorted list of IDReferenced features the following could be used:<p>
 * <pre>
 * <code>
 * Comparator order = new Comparator(new IDComparator);
 * Collections.sort(list,order);
 * if(Collections.binarySearch(list,new NullShape(x),order){
 *    //list contains IDReferenced object with an id of x
 * }
 * </code>
 * </pre>
 * @author James Macgill JM
 * @since 0.7.7.1 12/May/2000
 */
public class NullShape extends uk.ac.leeds.ccg.geotools.GeoShape
{
    /**
     * Constructs a NullShape with the specified ID.
     * <br>In effect a NullShape is nothing but an encapsulated id and a getID method.
     */
    public NullShape(int id){
        this.id = id;
    }
    
    /**
     * a null shape can nither be contained nore contains any other feature.
     * @return boolean Always false.
     */
    public boolean contains(GeoPoint p)
    {
        return false;
    }

    /**
     * Sets the ID of this shape
     * @param id the new ID for this shape
     */
     public void setID(int id){
        this.id = id;
     }

    /**
     * Null shapes have no location or points so this returns null
     */
    public Vector getPoints()
    {
        return null;
    }
    
    /**
     * A null shape has no physical pressence, and hence no area.
     * @return double The area of the shape, i.e. 0
     */
    public double getArea(){return 0;}
}
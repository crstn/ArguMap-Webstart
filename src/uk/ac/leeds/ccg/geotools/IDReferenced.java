package uk.ac.leeds.ccg.geotools;
/**
 * In order to relate geographic features to each other and to associated data
 * each can be given a specific ID No.  This concept is used in a number of places
 * throught GeoTools, most notably in the way features in Layers are reladed to data
 * in GeoData objects. All items/features that would like to be related in this way
 * should implement this interface.
 * <br> Items providing this interface can also be compared and sorted through the use
 * of the IDComparator object which should improve the cataloging of both data and objects.
 * <p>
 * TODO JM There are issues related to using such a simple referencing system, most notably
 * is that features loaded from different sources may co-incidentaly share the same ID value whilst
 * not actualy being related to each other at all.  Eventualy a more extensive referencing system may be
 * required.  However, as assosiation has to be explicitly stated, through the use of shared highlight or selection
 * managers, the problem of unrelated shared ids may not be all that large.<p>
 *
 * @author James Macgill JM
 * @since 0.7.7.1 12/May/2000
 **/
public interface IDReferenced
{
    /**
     * Returns an ID for this object that is unique within this objects feature set.
     * <br>In many cases any two objects within one geographic feature will be treated as if
     * they were a single item.  For example, highlighting one feature will highlight all other features
     * in the same layer which share its ID.  
     * <br>This should be avoided if possible as a number of operations can get confused, most notably
     * the dissolve operation.  The prefered solution is to form multi-part objects, though it is appreciated
     * that multi-part support is not yet as stable as it should be.<br>
     *
     * @see IDComparator
     * @author James Macgill JM
     * @since 0.7.7.1
     * @return int The unique id for this item.
     */
    public int getID();
}
/*
 * DummyFilter.java
 *
 * Created on 04 July 2001, 10:39
 */

package uk.ac.leeds.ccg.geotools;

/**
 *
 * @author  James Macgill
 * @version 
 */
public class DummyFilter extends uk.ac.leeds.ccg.geotools.SimpleFilter implements uk.ac.leeds.ccg.geotools.Filter {

    /** Creates new DummyFilter */
    public DummyFilter() {
    }

    public String getHeader() {
        return "dummy";
    }
    
    public String getAsRow() {
        return "1";
    }
    
    /**
     * Check the given id against the filter,
     * @return boolean returns as true if features with this id should be included in any displays
     */
    public boolean isVisible(int id) {
        return true;
    }
    
    public Object clone() {
        return new DummyFilter();
    }
    
}

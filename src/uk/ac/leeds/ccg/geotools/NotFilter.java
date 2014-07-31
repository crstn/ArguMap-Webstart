/*
 * NotFilter.java
 *
 * Created on 03 July 2001, 10:35
 */

package uk.ac.leeds.ccg.geotools;

/**
 *
 * @author  James Macgill
 * @version 
 */
public class NotFilter extends uk.ac.leeds.ccg.geotools.SimpleFilter implements FilterChangedListener,Filter
{

    Filter filter;
    /** Creates new NotFilter */
    public NotFilter() {
    }
    
    public NotFilter(Filter f){
        filter = f;
        filter.addFilterChangedListener(this);
    }

    /**
     * Check the given id against the filter,
     * @return boolean returns as true if features with this id should be included in any displays
     */
    public boolean isVisible(int id) {
        return !filter.isVisible(id);
    }
    
    public String getAsRow() {
        return filter.getAsRow();
    }
    
    public String getHeader() {
        return filter.getHeader();
    }
 
    public void filterChanged(FilterChangedEvent e){
       this.notifyFilterChangedListeners(e.getReason());
    }
    
    public Object clone(){
        return new NotFilter(filter);
    }
}

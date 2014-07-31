/*
 * AddFilter.java
 *
 * Created on 21 June 2001, 09:21
 */

package uk.ac.leeds.ccg.geotools;

public class LogicalOrFilter extends SimpleFilter implements FilterChangedListener{
Filter[] filters;
    /** Creates new AddFilter 
     * Any one of the filters in the array must return true if the LogicalOrFilter is to return true.
     * For best performance, place the filters most likely to return true first in the array.
     * @param filters An array of filters to test together.
     **/
    public LogicalOrFilter(Filter[] filters) {
        this.filters = filters;
        for(int i=0;i<filters.length;i++){
            filters[i].addFilterChangedListener(this);
        }
    }
    

    /**
     * Check the given id against the filter,
     * @return boolean returns as true if features with this id should be included in any displays
     */
    public boolean isVisible(int id) {
        if(filters.length==0)return true;
        for(int i=0;i<filters.length;i++){
            if(filters[i].isVisible(id)){
                return true;
            }
        }
        //All of the filters must have returned false, so...
        return false;
    }

    public void setFilters(Filter[] f){
        if(filters!=null){
            for(int i=0;i<filters.length;i++){
                filters[i].removeFilterChangedListener(this);
            }
        }
        filters = f;
        for(int i=0;i<filters.length;i++){
            filters[i].addFilterChangedListener(this);
        }
        this.notifyFilterChangedListeners(FilterChangedEvent.DATA);
    }
    
    public void filterChanged(FilterChangedEvent ce) {
        notifyFilterChangedListeners(ce.getReason());
    }
    
    public Object clone(){
        return new LogicalOrFilter(filters);
    }
}

/*
 * AddFilter.java
 *
 * Created on 21 June 2001, 09:21
 */

package uk.ac.leeds.ccg.geotools;

/**
 *
 * @author  James Macgill
 * @version
 */
public class LogicalAddFilter extends uk.ac.leeds.ccg.geotools.SimpleFilter implements FilterChangedListener{
    Filter[] filters;
    /** Creates new AddFilter
     * All of the filters in the array must return true if the AddFilter is to return true.
     * For best performance, place the filters most likely to return false first in the array.
     * @param filters An array of filters to add together.
     **/
    public LogicalAddFilter(Filter[] filters) {
        this.filters = filters;
        for(int i=0;i<filters.length;i++){
            filters[i].addFilterChangedListener(this);
        }
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
        
    public Filter[] getFilters(){
        return filters;
    }
    
        /**
         * Check the given id against the filter,
         * @return boolean returns as true if features with this id should be included in any displays
         */
        public boolean isVisible(int id) {
            if(filters.length==0)return true;
            for(int i=0;i<filters.length;i++){
                if(!filters[i].isVisible(id)){
                    return false;
                }
            }
            //All of the filters must have returned true, so...
            return true;
        }
        
        public void filterChanged(FilterChangedEvent ce) {
            notifyFilterChangedListeners(ce.getReason());
        }
        
        public String getHeader(){
            String row = "";
             row+=filters[0].getHeader();
            for(int i=1;i<filters.length;i++){
               row+=","+filters[i].getHeader();
            }
            return row;
        }
        
        public String getAsRow(){
            String row = "";
            row+=filters[0].getAsRow();
            for(int i=1;i<filters.length;i++){
               row+=","+filters[i].getAsRow();
            }
            return row;
        }
        
        public String toString(){
            String desc = "LAF: ";
            for(int i=0;i<filters.length;i++){
               desc+=filters[i].toString();
            }
            return desc;
        }
        
        //shallow clone
        public Object clone(){
            return new LogicalAddFilter(filters);
        }
    
    }

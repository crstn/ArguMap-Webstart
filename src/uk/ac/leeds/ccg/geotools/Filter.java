package uk.ac.leeds.ccg.geotools;
 
public interface Filter {
    /**
     * Check the given id against the filter,
     * @return boolean returns as true if features with this id should be included in any displays
     */
	public boolean isVisible(int id);
	public void addFilterChangedListener(FilterChangedListener l);
	public void removeFilterChangedListener(FilterChangedListener l);
	public void notifyFilterChangedListeners(int reason);
        
        public String getHeader();
        public String getAsRow();
        public Object clone();
}

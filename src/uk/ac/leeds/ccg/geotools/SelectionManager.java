package uk.ac.leeds.ccg.geotools;

import java.util.Vector;

/**
 * Handles feature selection.
 * redesigned 8/Jul/99 for much more flexibility.
 * note: invert selection and select all are not possible as the full range of
 * ids is not always known
 * @version 0.6.1
 * @author James Macgill 
 * This code is (c)James Macgill and is covered by the GPL.
 */
public class SelectionManager extends java.awt.Component
{
    
    static public final int CONTAINS=0,CROSSES=1;

	//protected int[] selection = new int[0];
	protected Vector selected = new Vector();
	 Vector listeners = new Vector();
    
	public SelectionManager()
	{
	}

	public void setSelection(int[] selection)
	{
			selected = new Vector();
			for(int i=0;i<selection.length;i++){
			    selected.addElement(new Integer(selection[i]));
			}
			notifySelectionChanged();
	}

    public final void addToSelection(int id){
        if(!selected.contains(new Integer(id))){
            selected.addElement(new Integer(id));
		//System.out.println("Added "+id);
            notifySelectionChanged();
        }
    }
    
    public void addToSelection(int[] ids){
        for(int i=0;i<ids.length;i++){
            addToSelection(ids[i]);
		//System.out.println("Added "+ids[i]);

        }
    }
    
    public final void removeFromSelection(int id){
        if(selected.contains(new Integer(id))){
            selected.removeElement(new Integer(id));
            notifySelectionChanged();
        } 
    }
    
    public void removeFromSelection(int[] ids){
        for(int i=0;i<ids.length;i++){
            removeFromSelection(ids[i]);
        }
    }
    
    
    public void toggleSelection(int id){
	  //System.out.println("Toggled "+id);
        if(isSelected(id)){
            removeFromSelection(id);
        }
        else
        {
            addToSelection(id);
        }
    }
    
    public void toggleSelection(int[] ids){
        for(int i=0;i<ids.length;i++){
            toggleSelection(ids[i]);
        }
    }
    
    public boolean isSelected(int id){
        if(selected.contains(new Integer(id))){
                return true;
        }
        return false;
    }
    
    public void clearSelection(){
        selected = new Vector();
        notifySelectionChanged();
    }
       
	public int[] getSelection()
	{
	    int[] selectArray = new int[selected.size()];
	    for(int i=0;i<selected.size();i++){
	        selectArray[i] = ((Integer)selected.elementAt(i)).intValue();
	    }
		return selectArray;
	}

    /**
     * @deprecated - this method serves no real use as far as I can see that isSelected can not
     * do better.
     */
	public int getSelection(int nIndex)
	{
		return ((Integer)selected.elementAt(nIndex)).intValue();
	}
	
	public synchronized void
	addSelectionChangedListener(SelectionChangedListener hce) {
	    listeners.addElement(hce);
	}
	
	public synchronized void 
	removeSelectionChangedListener(SelectionChangedListener hce) {
	    listeners.removeElement(hce);
	}
	
	protected void notifySelectionChanged() {
	    Vector l;
	    SelectionChangedEvent hce = new SelectionChangedEvent(this,getSelection());
	    synchronized(this) {l = (Vector)listeners.clone(); }
	    
	    for (int i = 0; i < l.size();i++) {
	        ((SelectionChangedListener)l.elementAt(i)).selectionChanged(hce);
	    }
	}


}

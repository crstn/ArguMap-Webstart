package uk.ac.leeds.ccg.widgets;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.SelectionChangedEvent;
import uk.ac.leeds.ccg.geotools.SelectionManager;
public class GeoList extends java.awt.List implements uk.ac.leeds.ccg.geotools.SelectionChangedListener, ItemListener
{

	private final static boolean DEBUG=false;
    GeoData data;
    SelectionManager sm;
    int indexLUT[];
    public GeoList(GeoData d){
	   if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.widgets.GeoList constructed. Will identify itself as GLs->");
		data = d;
        setMultipleMode(true);
        //sm =s;
        if(DEBUG)System.out.println("GLs->setting up list");
        //sm.addSelectionChangedListener(this);
        indexLUT = new int[d.getSize()];
        Enumeration ids = data.getIds();
        int i=0;
        while(ids.hasMoreElements()){
            int id = ((Integer)ids.nextElement()).intValue();
            indexLUT[i] = id;
            String text = d.getText(id);
            if(DEBUG)System.out.println("GLs->"+text);
            this.add(text);
            i++;
        }
        if(DEBUG)System.out.println("GLs->Done");
        this.addItemListener(this);// listen to myself???
    }
    
    public void setSelectionManager(SelectionManager s){
        sm = s;
        sm.addSelectionChangedListener(this);
    }
    
    public void itemStateChanged(ItemEvent e){
        if(DEBUG)System.out.println("GLs->The selection on the list has changed");
        if(sm!=null){
            sm.setSelection(indexesToIds(getSelectedIndexes()));
        }
    }
    public void selectionChanged(SelectionChangedEvent sce)
    {
       if(DEBUG)System.out.println("GLs->The selection in the selection manager has changed");
       int ids[] = sce.getSelection();
       int indexes[] = idsToIndexes(ids);
       int active[] = this.getSelectedIndexes();
       for(int i=0;i<active.length;i++){
        deselect(active[i]);
       }
       for(int i=0;i<indexes.length;i++){
        select(indexes[i]);
       }
       
       
    }
    
    private int[] indexesToIds(int[] indexes){
        int[] ids = new int[indexes.length];
        for(int i=0;i<indexes.length;i++){
            ids[i] = indexToId(indexes[i]);
        }
        return ids;
    }
    
    private int[] idsToIndexes(int[] ids){
        int[] indexes = new int[ids.length];
        for(int i=0;i<ids.length;i++){
            indexes[i] = idToIndex(ids[i]);
        }
        return indexes;
    }
    
    private int indexToId(int index){
       return this.indexLUT[index];
    }
    
    private int idToIndex(int id){
        for(int i=0;i<indexLUT.length;i++){
            if(indexLUT[i] ==id) return i;
        }
        return -1;
    }

}

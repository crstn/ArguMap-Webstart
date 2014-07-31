package uk.ac.leeds.ccg.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.HighlightChangedEvent;
import uk.ac.leeds.ccg.geotools.HighlightChangedListener;
import uk.ac.leeds.ccg.geotools.HighlightManager;
import uk.ac.leeds.ccg.geotools.SelectionChangedEvent;
import uk.ac.leeds.ccg.geotools.SelectionChangedListener;
import uk.ac.leeds.ccg.geotools.SelectionManager;

public class SelectionList extends java.awt.List implements SelectionChangedListener,HighlightChangedListener,
ActionListener,ItemListener{

	static final boolean DEBUG=false;
	static final String DBC="SLi->";
	GeoData data;
	HighlightManager hm=null;
	SelectionManager sm=null;
	public Vector idx = new Vector();
	public SelectionList(GeoData data){
		super();
		this.data = data;
		addActionListener(this);
		addItemListener(this);
	}
	public SelectionList(GeoData data,int rows){
		super(rows);
		this.data = data;
		addActionListener(this);
		addItemListener(this);
	}
	public void setGeoData(GeoData d){
		data=d;
	}
	public void setHighlightManager(HighlightManager h){
		hm=h;
		hm.addHighlightChangedListener(this);
	}
	public void setSelectionManager(SelectionManager s){
		sm=s;
		sm.addSelectionChangedListener(this);
	}
	public void highlightChanged(HighlightChangedEvent hce){
		int id = hce.getHighlighted();
		deselect(getSelectedIndex());
		for(int i=0;i<idx.size();i++){
			if(((Integer)idx.elementAt(i)).intValue()==id){
				select(i);
				makeVisible(i);
				return;
			}
		}
	}

	public void selectionChanged(SelectionChangedEvent sce){
		int [] ids = sce.getSelection();
		if(ids.length==0){ 
			clearSelection();
			return;
		}
		if(DEBUG)System.out.print(DBC);
		for(int i=0;i<ids.length;i++){
			if(DEBUG)System.out.print(""+ids[i]+" ");
			if(sce.isSelected(ids[i])&&!idx.contains(new Integer(ids[i]))){
				if(!data.getText(ids[i]).trim().equals("")){
					add(data.getText(ids[i]).trim());
					idx.add(new Integer(ids[i]));
				}
			}
		}
		if(DEBUG)System.out.println("");
		Iterator e = idx.iterator();
		while(e.hasNext()){
			Integer I = (Integer)e.next();
			int i = I.intValue();
				if(!(sce.isSelected(i))){
					remove(data.getText(i).trim());
					e.remove();
				}
		}

	}
	public void clearSelection(){
		removeAll();
		idx=new Vector();
	}
	public void actionPerformed(ActionEvent e){
		if(sm!=null){
			int [] i=new int[1];
			i[0]= ((Integer)idx.elementAt(getSelectedIndex())).intValue();
			sm.clearSelection();
			sm.setSelection(i);
		}
	}
	public void itemStateChanged(ItemEvent e) {
		if(hm!=null){
			hm.setHighlight(((Integer)idx.elementAt(getSelectedIndex())).intValue());
		}
	}
}
	

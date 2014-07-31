package uk.ac.leeds.ccg.widgets;

import java.awt.Button;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.CenterOnPointTool;
import uk.ac.leeds.ccg.geotools.OneClickZoomInTool;
import uk.ac.leeds.ccg.geotools.OneClickZoomOutTool;
import uk.ac.leeds.ccg.geotools.SelectTool;
import uk.ac.leeds.ccg.geotools.Viewer;

public class OneClickToolBar extends ToolBar{
	public OneClickToolBar(Viewer ve){
		this(ve,false);
	}
	public OneClickToolBar(Viewer ve, boolean sel){
		this(ve,sel,true);
	}
	public OneClickToolBar(Viewer ve,boolean sel, boolean check){
    viewers.addElement(ve);
    ve.getScale().addScaleChangedListener(this);
    views=0;
    tools[0]= new Vector();
    checkbox = check;
    reset = new Button("Reset");
    add(reset);
    controls.addElement(reset);
    reset.addActionListener(this);
    addTool(new OneClickZoomInTool());
    addTool(new OneClickZoomOutTool());
    addTool(new CenterOnPointTool());
    if(sel)addTool(new SelectTool());
	}
}
		

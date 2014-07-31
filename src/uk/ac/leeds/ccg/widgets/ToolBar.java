package uk.ac.leeds.ccg.widgets;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.PanTool;
import uk.ac.leeds.ccg.geotools.ScaleChangedEvent;
import uk.ac.leeds.ccg.geotools.ScaleChangedListener;
import uk.ac.leeds.ccg.geotools.Scaler;
import uk.ac.leeds.ccg.geotools.SelectTool;
import uk.ac.leeds.ccg.geotools.Tool;
import uk.ac.leeds.ccg.geotools.Viewer;
import uk.ac.leeds.ccg.geotools.ZoomTool;

/** A simple toolbar to save me (ian) adding all this code to control a viewer everytime
 * I write a quick applet, then I decided it might be useful to others. 
 * Simply construct with the viewer to control and a boolean to decide if you want select enabled in the bar
 * now makes use of tools rather than the deprecated int method in viewer. 23/11/00
 * New version makes use of tool's new found ability to know thier own name to construct the toolbar. 28/11/00
 * added support for linking viewers together with a single tool bar 25/6/01
 */
public class ToolBar extends Panel implements ActionListener,ItemListener,ScaleChangedListener{
	static final boolean DEBUG = false;
	static final String DBC ="TB >";
	Vector viewers = new Vector(); 
	Viewer v;
	int views=1;
	boolean selection=false;
	boolean checkbox = true;
	CheckboxGroup cbg = new CheckboxGroup();
	static final int MAX_VIEWS = 5;
	Vector tools[] = new Vector[MAX_VIEWS];
	Button reset;
	Vector controls = new Vector();

	public ToolBar(){
	}
  /** generate a basic tool bar with reset, zoom and pan buttons to opperate on viewer ve
   */
	public ToolBar(Viewer ve){
		this(ve,false);
	}

		
	/** basic tool bar with reset, zoom and pan, and optionally selection (based on value of boolean s) to
	 * operate on viewer ve.
	 */
	public ToolBar(Viewer ve, boolean s){
		this(ve,s,true);
	}
	/** create toolbar to work on viewer ve with an optional selection button
	 * and if checks == true a set of check boxes instead of buttons
	 */
	public ToolBar(Viewer ve, boolean sel, boolean checks){
		viewers.addElement(ve);
		ve.getScale().addScaleChangedListener(this);
		views=0;
		tools[0]= new Vector();
		checkbox = checks;
    reset = new Button("Reset");
    add(reset);
		controls.addElement(reset);
    reset.addActionListener(this);
    //addTool(new resetTool());
		addTool(new ZoomTool());
		addTool(new PanTool());
		if(sel)addTool(new SelectTool());
	}
	/** custom tool bar, contains reset button and then adds tools from vector t.
	 * I don't think you would ever want a toolbar with out a reset button, but I've been wrong before.
	 */
	 
	public ToolBar(Viewer ve, Vector t){
		viewers.addElement(ve);
		ve.getScale().addScaleChangedListener(this);
		tools[0]=new Vector();
		views=0;
    reset = new Button("Reset");
		controls.addElement(reset);
    add(reset);
    reset.addActionListener(this);
    for(int i=0;i<t.size();i++){
      addTool((Tool)t.elementAt(i));
    }
  }
    
	public void addViewer(Viewer ve){
		Tool t;
		viewers.addElement(ve);
		ve.getScale().addScaleChangedListener(this);
		tools[++views] = new Vector();
		for(int i=0;i<tools[0].size();i++){
			Class c = tools[0].elementAt(i).getClass();
			try{
				tools[views].addElement(t=(Tool)c.newInstance());
				ve.setTool(t);
			}catch(Exception e){
				System.out.println(DBC+"Exception "+e);
			}
		}
	}

	public void setResetActionListener(ActionListener al){
		reset.removeActionListener(this);
		reset.addActionListener(al);
	}
	public void addTool(Tool t){
	  tools[views].addElement(t);
		if(!checkbox){
			Button b = new Button(t.getName());
			controls.addElement(b);
			add(b);
			b.addActionListener(this); 
		}else{
			Checkbox c = new Checkbox(t.getName(),true,cbg);
			controls.addElement(c);
			add(c);
			c.addItemListener(this);
		}
		for(int i=0;i<viewers.size();i++){
			((Viewer)viewers.elementAt(i)).setTool(t);
		}
  }

	public Vector getControls(){
		return controls;
	}

	public void actionPerformed(ActionEvent e){
		String cmd = e.getActionCommand();
		if(cmd.equals("Reset")){
			if(DEBUG)System.out.println("RESET");
			for(int i=0;i<viewers.size();i++){
				((Viewer)viewers.elementAt(i)).setMapExtentFull(false);
			}
		  return;
	  }
    Tool t;
    for(int i=0;i<tools[0].size();i++){
      t=(Tool)tools[0].elementAt(i);
      if(cmd.equals(t.getName())){
				for(int j=0;j<viewers.size();j++){
					if(DEBUG)System.out.println(DBC+"setting "
						+(Viewer)viewers.elementAt(j)+" to "+(Tool)tools[j].elementAt(i));
					((Viewer)viewers.elementAt(j)).setTool((Tool)tools[j].elementAt(i));
				}
        return;
      }
    }
  }
	public void itemStateChanged(ItemEvent e){
		String cmd = (String)e.getItem();
    Tool t;
    for(int i=0;i<tools[0].size();i++){
      t=(Tool)tools[0].elementAt(i);
      if(cmd.equals(t.getName())){
				for(int j=0;j<viewers.size();j++){
					if(DEBUG)System.out.println(DBC+"setting "
						+(Viewer)viewers.elementAt(j)+" to "+(Tool)tools[j].elementAt(i));
					((Viewer)viewers.elementAt(j)).setTool((Tool)tools[j].elementAt(i));
				}
        return;
      }
    }
  }
	public void scaleChanged(ScaleChangedEvent sce){
		/** find the source and then tell all the <strong>OTHER</strong> viewers to
		 * <strong>Quitely</strong> change mapextent
		 */
		Scaler source = (Scaler)sce.getSource();
		if(DEBUG)System.out.println(DBC+"Scale change source "+source);
		for(int j=0;j<viewers.size();j++){
			if(((Viewer)viewers.elementAt(j)).getScale()!=source){
				if(DEBUG)System.out.println(DBC+"Sending scale change "+
				" to "+(Viewer)viewers.elementAt(j));
				((Viewer)viewers.elementAt(j)).setMapExtent(source.getMapExtent(),true);
			}
		}
	}
}

		


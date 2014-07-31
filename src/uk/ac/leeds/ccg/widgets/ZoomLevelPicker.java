/*
 * ZoomLevel.java
 *
 * Created on January 24, 2001, 1:31 PM
 */

package uk.ac.leeds.ccg.widgets;

import java.awt.Choice;
import java.awt.event.ItemListener;

import uk.ac.leeds.ccg.geotools.ScaleChangedEvent;
import uk.ac.leeds.ccg.geotools.ScaleChangedListener;
import uk.ac.leeds.ccg.geotools.Viewer;
import uk.ac.leeds.ccg.geotools.misc.FormatedString;

/**
 *
 * @author  jamesm
 * @version
 */
public class ZoomLevelPicker extends Choice implements java.io.Serializable, ScaleChangedListener, ItemListener {

	protected final static boolean DEBUG=false;
    protected String sampleProperty;
    protected Viewer view;


		protected double TOL=1.0;
    protected double currentLevel;
    protected double levels[] = {50,100,150,200,400,1000,2000,4000};

    /** Creates new ZoomLevel */
    public ZoomLevelPicker() {
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.widgets.ZoomLevelPicker constructed. Will identify itself as ZLP->");
        fillLevelList();
				setCurrentZoomLevel(100.0);
        this.addItemListener(this);
    }
    
    public ZoomLevelPicker(Viewer v){
        this();
        view = v;
        view.scale.addScaleChangedListener(this);
        
    }
    
    public void setViewer(Viewer v){
        if(view!=null && view!=v){
            view.scale.removeScaleChangedListener(this);
        }
        view = v;
    }
    
    private void fillLevelList(){
        removeAll();
        for(int i=0;i<levels.length;i++){
            this.addItem(FormatedString.format(""+levels[i],1));
        }
    }
    
    public void setLevelList(double[] list){
        if(DEBUG)System.out.println("ZLP->Setting new Levels");
        levels = list;
        fillLevelList();
    }

    public double[] getLevelList(){
        return levels;
    }

		public void setTolerance(double t){
			TOL=t;
		}
		public double getTolerance(){
			return TOL;
		}
    public void setCurrentZoomLevel(double percent){

        currentLevel = percent;
				for(int i=0;i<levels.length;i++){
					if(Math.abs(levels[i]-currentLevel)<TOL){
						select(i);
						return;
					}
				}
        select(FormatedString.format(""+currentLevel,1));
				if(this.getItemCount()>levels.length){this.remove(this.getItemCount()-1);}
				String level = FormatedString.format(""+currentLevel,1);
				this.addItem(level);
				this.select(level);
    }





    public void scaleChanged(ScaleChangedEvent sce) {
       this.setCurrentZoomLevel(view.getZoomAsPercent());
    }

    public void itemStateChanged(java.awt.event.ItemEvent p1) {
        double d = new Double(getSelectedItem()).doubleValue();
        if(DEBUG)System.out.println("ZLP->Setting zoom level to "+d);
        if(view!=null){
            view.zoomPercent(d);
        }
    }
    
}

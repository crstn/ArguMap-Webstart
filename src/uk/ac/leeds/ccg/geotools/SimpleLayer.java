package uk.ac.leeds.ccg.geotools;

import java.awt.Graphics;
import java.util.Vector;

public abstract class SimpleLayer extends java.lang.Object implements uk.ac.leeds.ccg.geotools.Layer
{
  int status = Layer.COMPLETED;
  Vector listeners = new Vector();
  protected String name = "Unknown";
	static final boolean DEBUG=false;
   
  public void removeLayerChangedListener(LayerChangedListener lcl)
  {
    listeners.removeElement(lcl); 
  }
  public void addLayerChangedListener(LayerChangedListener lcl)
  {
    listeners.addElement(lcl);
  }
    
  public void notifyLayerChangedListeners(int reason){
    Vector l;
    LayerChangedEvent lce = new LayerChangedEvent(this,reason);
    synchronized(this) {l = (Vector)listeners.clone(); }
	    
    for (int i = 0; i < l.size();i++) {
      ((LayerChangedListener)l.elementAt(i)).layerChanged(lce);
    }   
  }

  public int[] getIDs(GeoRectangle box,int mode){
    return new int[0];
  }

  public int getStatus(){	
    return status;
  }
  public void setStatus(int stat){
		if(DEBUG){
			System.out.print(this+" status changed to "+stat);
			switch(stat){
				case Layer.COMPLETED: 
					System.out.println(" (COMPLETED)");
					break;
				case Layer.LOADING: 
					System.out.println(" (LOADING)");
					break;
				case Layer.PENDING: 
					System.out.println(" (PENDING)");
					break;
				case Layer.ERRORED: 
					System.out.println(" (ERRORED)");
					break;
				case Layer.ABORTED: 
					System.out.println(" (ABORTED)");
					break;
				default:
					System.out.println(" BROKEN - HELP");
			}
		}
    if(status != Layer.COMPLETED&&stat==Layer.COMPLETED){
      status=stat;
      if(DEBUG)System.out.println("Notifing listeners of change");
      notifyLayerChangedListeners(LayerChangedEvent.GEOGRAPHY);
    } else {
      status=stat;
    }
  }
    
  /*public void paintScaled(GeoGraphics g){
    paintScaled(g.getGraphics(),g.getScale(),g.getShade(),g.getData(),g.getStyle());
    }*/
    
  //Rough implementation, you should write your own!
  public void paintSelection(Graphics g,Scaler scale,int ids[],ShadeStyle style){
    for(int i=0;i<ids.length;i++){
      paintHighlight(g,scale,ids[i],style);
    }
  }
    
  public void setName(String n){
    name = n;
  }
    
  public String getName(){
    return name;
  }
    
  public String toString() {
    return name;
  }

}

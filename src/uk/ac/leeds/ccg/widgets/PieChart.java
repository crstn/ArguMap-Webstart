package uk.ac.leeds.ccg.widgets;

import java.awt.Color;
import java.awt.Graphics;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.HighlightChangedEvent;

/** a simple class to do basic pie charts (as percentages)
 * a fairly simple extension to bar charts - I hope!
 * @author Ian Turton (ian@geog.leeds.ac.uk)
 *
 * TO DO
 * break group out as seperate class
 * add labels to larger(?) groups
 * try drawing lines from centre to arcs to hide rounding error!
 */
public class PieChart extends BarGraph{

  double total=0.0; // we need to know how many there are 
	public void paint(Graphics g){
	// x,y,w,h describe the whole chart and we don't need to change them!
    int height = getBounds().height;
    int width = getBounds().width;
    int l = Math.min(height,width)-1; // well it'll look nicer as a circle!

    if(id<=0)return; // not in polygon?
    int count = groups.size();
    total=0.0;
    for(int i=0;i<count;i++){
      Group gr = (Group)groups.elementAt(i); 
      total+=gr.data.getValue(id);
    }
  // arcs start at 3.00 (+90) and negative is clockwise (I'm sure this made someone's life easier!)
  // x,y,w,h describe the whole chart and we don't need to change them!
    int angle=0,thisAngle; // my maths teacher said you should always start at 3.00!
    for(int i=0;i<count;i++){
        Group group = (Group)groups.elementAt(i);
        thisAngle = (int)Math.round(360.0*group.data.getValue(id)/total); // ID is from the highlighter
        //System.out.println("x"+id+" "+i+" "+group.data.getValue(id)+" "+thisAngle);

        g.setColor(group.color);
        g.fillArc(0,0,l,l,angle,thisAngle);
        angle+=thisAngle;
        
    }
    g.setColor(Color.black);
    g.drawArc(0,0,l,l,0,360);
    //g.drawRect(0,0,4,height);
    //g.drawRect(0,0,width,4);
  }
  public void highlightChanged(HighlightChangedEvent hce){
  
    maxValue = Double.MIN_VALUE;
    this.id = hce.getHighlighted();
    for(int i=0;i<groups.size();i++){
        double value = ((Group)groups.elementAt(i)).data.getValue(id);
        if(value>maxValue){maxValue = value;}
    }
    this.repaint();
  }
  public void addGroup(GeoData data,Color color){
    Group g = new Group(data,color);
    groups.addElement(g);
        
  }

  class Group{
    public Group(GeoData d,Color c){
      data = d;
      color = c;
    }
    public GeoData data;
    public Color color;
  }
}

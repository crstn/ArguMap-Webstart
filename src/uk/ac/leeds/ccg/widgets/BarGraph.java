package uk.ac.leeds.ccg.widgets;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.HighlightChangedEvent;
import uk.ac.leeds.ccg.geotools.HighlightChangedListener;
/**
 * BarGraph.java - A simple bar graph that can listen to a highlight manager
 *
 * If you have a map with multiple values per region, showing for example, the number of people who
 * voted for each party in an election then this BarGraph may be of interest.<p>
 *
 * @author James Macgill
 */
public class BarGraph extends java.awt.Component implements HighlightChangedListener
{
    Vector groups = new Vector();
    int id=0;
    double maxValue;
    
    public void paint(Graphics g){
        int height = getBounds().height;
        int width = getBounds().width;
        
        int count = groups.size();
        int barWidth = width/count;
        
        double scale = (double)height/(double)maxValue;
        
        for(int i=0;i<count;i++){
            Group group = (Group)groups.elementAt(i);
            int length = (int)(scale*group.data.getValue(id));
            g.setColor(Color.black);
            g.drawRect(i*barWidth,height-length,barWidth,length);
            g.setColor(group.color);
            g.fillRect(i*barWidth,height-length,barWidth,length);
        }
        
        
        
        
        
        //g.drawRect(0,0,10,getBounds().height);
        //g.drawRect(10,height/2,10,height/2);
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
        
    public void update(Graphics g){
        paint(g);
    }
    
    public void addGroup(GeoData data,Color color){
        groups.addElement(new Group(data,color));
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
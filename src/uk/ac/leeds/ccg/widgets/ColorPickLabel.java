package uk.ac.leeds.ccg.widgets;

import java.awt.Color;
import java.awt.Frame;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
//import javax.swing.Action;

public class ColorPickLabel extends java.awt.Label
{
	private final static boolean DEBUG=false;

	public ColorPickLabel(Color c){
	    super("["+c.getRed()+","+c.getGreen()+","+c.getBlue()+"]");
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.widgets.ColorPickLabel constructed. will identify itself as CoPL>");
		setPickColor(c);
	    addMouseListener(new Clicked());

	}

	public java.awt.Color getPickColor()
	{
		//To do: Add code to calculate a value for the property...
		return getBackground();
	}

	public void setPickColor(java.awt.Color propValue)
	{
	    setBackground(propValue);
		setText("["+propValue.getRed()+","+propValue.getGreen()+","+propValue.getBlue()+"]");
		if(DEBUG)System.out.println("CoPL>Color set to ["+propValue.getRed()+","+propValue.getGreen()+","+propValue.getBlue()+"]");
		changes.firePropertyChange("pickColor", getPickColor(), propValue);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		changes.removePropertyChangeListener(listener);
	}
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		changes.addPropertyChangeListener(listener);
	}

	class Clicked extends java.awt.event.MouseAdapter
    {
        public void mouseClicked(java.awt.event.MouseEvent e){
            if(DEBUG)System.out.println("CoPL>Someone clicked on the color "+e.getClickCount());
            if(e.getClickCount()==1){
                Frame f = new Frame();
               // f.setLocation(getLocationOnScreen());
                if(DEBUG)System.out.println("CoPL>"+getLocationOnScreen());
                ColorDialog cd = new ColorDialog(f,true,getBackground());
                cd.setLocation(getLocationOnScreen());
                cd.show();
                setPickColor(cd.getColor());
            }
        }
    }

	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public static void main(String argsp[]){
	    Frame f = new Frame();
	    f.setSize(200,200);
	    f.add(new ColorPickLabel(Color.green));
	    f.show();
	}



}

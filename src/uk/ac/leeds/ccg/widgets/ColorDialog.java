package uk.ac.leeds.ccg.widgets;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

public class ColorDialog extends java.awt.Dialog
{
    ColorPicker pick;
	public ColorDialog(Frame parent,Color c)
	{
		super(parent);
		//{{INIT_CONTROLS
		setLayout(new BorderLayout(0,0));
		setSize(170,102);
		setVisible(false);
		setTitle("ColorDialog");
		//}}
        pick = new ColorPicker(c);
        add(pick,"Center");
        Button ok = new Button("Ok");
        add(ok,"South");
		//{{REGISTER_LISTENERS
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		ok.addActionListener(new SymAction());
		//}}
	}

	public ColorDialog(Frame parent, boolean modal,Color c)
	{
		this(parent,c);
		setModal(modal);;
	}
	
	public Color getColor(){
	    return pick.getColor();
	}

	public void addNotify()
	{
		// Record the size of the window prior to calling parents addNotify.
		Dimension d = getSize();

		super.addNotify();

		if (fComponentsAdjusted)
			return;

		// Adjust components according to the insets
		Insets ins = getInsets();
		setSize(ins.left + ins.right + d.width, ins.top + ins.bottom + d.height);
		Component components[] = getComponents();
		for (int i = 0; i < components.length; i++)
		{
			Point p = components[i].getLocation();
			p.translate(ins.left, ins.top);
			components[i].setLocation(p);
		}
		fComponentsAdjusted = true;
	}

	// Used for addNotify check.
	boolean fComponentsAdjusted = false;

	public ColorDialog(Frame parent, String title, boolean modal,Color c)
	{
		this(parent, modal,c);
		setTitle(title);
	}

	public void setVisible(boolean b)
	{
		if (b)
		{
			Rectangle bounds = getParent().getBounds();
			Rectangle abounds = getBounds();

			setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
				bounds.y + (bounds.height - abounds.height)/2);
		}
		super.setVisible(b);
	}

	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == ColorDialog.this)
				ColorDialog_WindowClosing(event);
		}
	}
	
	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
				dispose();
		}
	}


	void ColorDialog_WindowClosing(java.awt.event.WindowEvent event)
	{
		dispose();
	}
	//{{DECLARE_CONTROLS
	//}}
	
	public static void main(String args[]){
	    Frame f = new Frame();
	    ColorDialog cd = new ColorDialog(f,Color.gray);
	    cd.show();
	}

}
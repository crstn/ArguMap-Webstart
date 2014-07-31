package uk.ac.leeds.ccg.geotools;

import java.awt.Frame;
import java.awt.Rectangle;

public abstract class ShaderConfigurator extends java.awt.Dialog
{
    
    public ShaderConfigurator(Frame f,boolean mode)
        {
            super(f,mode);
            
        }
        
	public abstract void update();
        
        public abstract void actionChanges();
            
        
        public abstract void addItems();
           


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
			if (object == ShaderConfigurator.this)
				ShaderConfigurator_WindowClosing(event);
		}
	}

	void ShaderConfigurator_WindowClosing(java.awt.event.WindowEvent event)
	{
		dispose();
	}
	//{{DECLARE_CONTROLS
	//}}

}
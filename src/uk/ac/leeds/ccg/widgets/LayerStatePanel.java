package uk.ac.leeds.ccg.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;


public class LayerStatePanel extends Panel
{
	public LayerStatePanel()
	{
		setLayout(null);
		setBounds(15,8,234,24);
		setBackground(new Color(12632256));

		visibleCheck = new java.awt.Checkbox("");
		visibleCheck.setBounds(22,-1,20,23);
		add(visibleCheck);
		orderLabel = new java.awt.Label("1");
		orderLabel.setBounds(2,0,19,20);
		orderLabel.setFont(new Font("Dialog", Font.BOLD, 10));
		add(orderLabel);
		layerName = new java.awt.Label("Layer Name");
		layerName.setBounds(47,3,170,15);
		add(layerName);
		
	}
	//{{DECLARE_CONTROLS
	Panel borderPanel1;
	java.awt.Checkbox visibleCheck;
	java.awt.Label orderLabel;
	java.awt.Label layerName;
	//}}

	public void setLayer(java.lang.Object layer)
	{
		this.layer = layer;
	}

	public java.lang.Object getLayer()
	{
		return this.layer;
	}

	

	public void setName(java.lang.String name)
	{
			this.name = name;
			layerName.setText(name);
	}

	public java.lang.String getName()
	{
		return this.name;
	}

	public void setShowVisible(int showVisible)
	{
			this.showVisible = showVisible;
	}

	public int getShowVisible()
	{
		return this.showVisible;
	}

	public void setPosition(int position)
	{
			this.position = position;
	}

	public int getPosition()
	{
		return this.position;
	}

	static public void main(String args[])
	{
		class DriverFrame extends java.awt.Frame {
			public DriverFrame() {
				addWindowListener(new java.awt.event.WindowAdapter() {
					public void windowClosing(java.awt.event.WindowEvent event)
					{
						dispose();	  // free the system resources
						System.exit(0); // close the application
					}
				});
				this.setLayout(new java.awt.BorderLayout());
				this.setSize(300,300);
				this.add(new LayerStatePanel());
			}
		}

		new DriverFrame().show();
	}

	protected boolean visible;
	protected java.lang.Object layer;
	protected java.lang.String name;
	protected int position;
	protected int showVisible;

}
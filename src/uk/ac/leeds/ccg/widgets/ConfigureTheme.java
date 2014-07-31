package uk.ac.leeds.ccg.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import uk.ac.leeds.ccg.dbffile.Dbf;
import uk.ac.leeds.ccg.geotools.ClassificationShader;
import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.ShadeStyle;
import uk.ac.leeds.ccg.geotools.Shader;
import uk.ac.leeds.ccg.geotools.ShapefileReader;
import uk.ac.leeds.ccg.geotools.Theme;
import uk.ac.leeds.ccg.shapefile.Shapefile;

public class ConfigureTheme extends java.awt.Dialog
{
    ShapefileReader source;
	public ConfigureTheme(Frame parent,ShapefileReader reader)
	{
		super(parent);
		source = reader;
		//{{INIT_CONTROLS
		setLayout(null);
		setSize(503,270);
		setVisible(false);
		add(choiceShadeBy);
		choiceShadeBy.setBounds(120,72,156,25);
		add(choiceToolTip);
		choiceToolTip.setBounds(120,108,156,25);
		add(choiceID);
		choiceID.setBounds(120,36,156,25);
		label1.setText("ID Column");
		add(label1);
		label1.setBounds(12,36,97,25);
		label2.setText("Data Column");
		add(label2);
		label2.setBounds(12,72,93,24);
		label3.setText("Tool Tip");
		add(label3);
		label3.setBounds(12,108,100,27);
		button1.setLabel("OK");
		add(button1);
		button1.setBackground(java.awt.Color.lightGray);
		button1.setBounds(312,228,95,34);
		fill.setState(true);
		fill.setLabel("Fill Features");
		add(fill);
		fill.setBounds(12,156,108,29);
		outline.setState(true);
		outline.setLabel("Outline Features");
		add(outline);
		outline.setBounds(12,192,132,33);
		lineShade.setLabel("Line Color from Shader");
		add(lineShade);
		lineShade.setBounds(180,192,156,29);
		choiceLineColor.addItem("Black");
		choiceLineColor.addItem("Red");
		choiceLineColor.addItem("Green");
		choiceLineColor.addItem("Blue");
		try {
			choiceLineColor.select(0);
		}
		catch (IllegalArgumentException e) { }
		add(choiceLineColor);
		choiceLineColor.setBounds(348,192,121,25);
		setTitle("ConfigureTheme");
		//}}

		//{{REGISTER_LISTENERS
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymAction lSymAction = new SymAction();
		button1.addActionListener(lSymAction);
		SymItem lSymItem = new SymItem();
		lineShade.addItemListener(lSymItem);
		fill.addItemListener(lSymItem);
		outline.addItemListener(lSymItem);
		//}}
		
		//set up choice boxes
		Dbf dbf = reader.dbf;
		Shapefile sf = reader.sf;
		
		int fieldCount = dbf.getNumFields();
		for(int i=0;i<fieldCount;i++){
		    char type = dbf.getFieldType(i);
		    String name = dbf.getFieldName(i).toString();
		    if(type=='N'){
		        choiceID.add(name);
		        choiceShadeBy.add(name);
		    }
		    //if(type=='C'){
		        choiceToolTip.add(name);
		    //}
		try{    
		    switch(sf.getShapeType()){
        case(Shapefile.POLYGON):
            choiceID.select(dbf.getFieldName(2).toString());
            break;
        case(Shapefile.ARC):
            choiceID.select(dbf.getFieldName(5).toString());
            lineShade.setState(true);
            break;
        case(Shapefile.POINT):
            //idCol = ?
            break;
       }
        }
        catch(ArrayIndexOutOfBoundsException e){
        }
		    
		}
	}

	public ConfigureTheme(Frame parent, boolean modal,ShapefileReader reader)
	{
		this(parent,reader);
		setModal(modal);;
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

	public ConfigureTheme(Frame parent, String title, boolean modal,ShapefileReader reader)
	{
		this(parent, modal,reader);
		setTitle(title);
	}

    public Theme getTheme(){
        
        source.setIdCol(choiceID.getSelectedItem());
        GeoData data = source.readData(choiceShadeBy.getSelectedItem());
        //Shader shade = new RampShader();
        //Shader shade = new HSVShader(Color.yellow,Color.green);
        Shader shade = new ClassificationShader(data,3,ClassificationShader.EQUAL_INTERVAL);
        //shade.setRange(data.getMin(),data.getMax());
        
        GeoData tips = source.readData(choiceToolTip.getSelectedItem());
        
        Theme t = source.getTheme(shade,choiceShadeBy.getSelectedItem());
        ShadeStyle style= t.getShadeStyle();
        style.setLineColorFromShader(lineShade.getState());
        if(!lineShade.getState()){
        
            Color lineColor;
            switch(choiceLineColor.getSelectedIndex()){
                case 0: lineColor = Color.black;break;
                case 1: lineColor = Color.red;break;
                case 2: lineColor = Color.green;break;
                case 3: lineColor = Color.blue;break;
                default : lineColor = Color.gray;break;
            }
            style.setLineColor(lineColor);
        }
        
        style.setIsFilled(fill.getState());
        style.setIsOutlined(outline.getState());
        
        t.setTipData(tips);
        
        t.setName(choiceShadeBy.getSelectedItem());
      //  t.setGeoData(data);
        return t;
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
			if (object == ConfigureTheme.this)
				ConfigureTheme_WindowClosing(event);
		}
	}

	void ConfigureTheme_WindowClosing(java.awt.event.WindowEvent event)
	{
		dispose();
	}
	//{{DECLARE_CONTROLS
	java.awt.Choice choiceShadeBy = new java.awt.Choice();
	java.awt.Choice choiceToolTip = new java.awt.Choice();
	java.awt.Choice choiceID = new java.awt.Choice();
	java.awt.Label label1 = new java.awt.Label();
	java.awt.Label label2 = new java.awt.Label();
	java.awt.Label label3 = new java.awt.Label();
	java.awt.Button button1 = new java.awt.Button();
	java.awt.Checkbox fill = new java.awt.Checkbox();
	java.awt.Checkbox outline = new java.awt.Checkbox();
	java.awt.Checkbox lineShade = new java.awt.Checkbox();
	java.awt.Choice choiceLineColor = new java.awt.Choice();
	//}}


	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == button1)
				button1_ActionPerformed(event);
		}
	}

	void button1_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
			 
		button1_ActionPerformed_Interaction1(event);
	}

	void button1_ActionPerformed_Interaction1(java.awt.event.ActionEvent event)
	{
		try {
			// ConfigureTheme Hide the ConfigureTheme
			this.setVisible(false);
		} catch (java.lang.Exception e) {
		}
	}

	class SymItem implements java.awt.event.ItemListener
	{
		public void itemStateChanged(java.awt.event.ItemEvent event)
		{
			Object object = event.getSource();
			if (object == lineShade)
				lineShade_ItemStateChanged(event);
			else if (object == fill)
				fill_ItemStateChanged(event);
			else if (object == outline)
				outline_ItemStateChanged(event);
		}
	}

	void lineShade_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.
			 
		lineShade_ItemStateChanged_Interaction1(event);
	}

	void lineShade_ItemStateChanged_Interaction1(java.awt.event.ItemEvent event)
	{
		try {
			// choice1 Toggle enabled
			choiceLineColor.setEnabled(!choiceLineColor.isEnabled());
		} catch (java.lang.Exception e) {
		}
	}

	void fill_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.
	}

	void outline_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		// to do: code goes here.
			 
		outline_ItemStateChanged_Interaction1(event);
	}

	void outline_ItemStateChanged_Interaction1(java.awt.event.ItemEvent event)
	{
		try {
			// lineShade Toggle enabled
			lineShade.setEnabled(!lineShade.isEnabled());
		} catch (java.lang.Exception e) {
		}
	}
}
package uk.ac.leeds.ccg.geotools;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;

public class KeyHolder extends java.awt.Panel
{
    GridBagConstraints c = new GridBagConstraints();
	public KeyHolder()
	{
		setLayout(new GridBagLayout());
		
	    c.fill = c.BOTH;
	    c.insets = new Insets(0,0,0,0);
	    c.gridx = 0;
	    c.weightx = 0; c.weighty = 0;
	    c.fill = c.BOTH;
	}
	
	public void addKey(Key key){
	    add(key,c);
	}
	public void addKey(Key key,String title){
	    add(new Label(title),c);
	    add(key,c);
	}
	
	public void addKey(Shader shader){
	    add(shader.getKey(),c);
	}
}
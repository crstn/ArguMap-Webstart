package uk.ac.leeds.ccg.widgets;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentListener;

class ColorPicker extends java.awt.Panel implements AdjustmentListener
{
    Panel color;
    Scrollbar rgb[];
    public ColorPicker(){
        this(Color.blue);
    }
    public ColorPicker(Color old){
        setLayout(new GridLayout(1,2));
        color = new Panel();
        color.setBackground(old);
        Panel sliders = new Panel();
        sliders.setLayout(new GridLayout(1,3));
        rgb = new Scrollbar[3];
        for(int i=0;i<3;i++){
            rgb[i] = new Scrollbar(Scrollbar.VERTICAL, 0, 10, 0, 265);
            //rgb[i].setMaximum(255);
            //rgb[i].setMinimum(0);
            rgb[i].addAdjustmentListener(this);
            sliders.add(rgb[i]);
        }
        rgb[0].setBackground(Color.red);
        rgb[0].setValue(255-old.getRed());
        rgb[1].setBackground(Color.green);
        rgb[1].setValue(255-old.getGreen());
        rgb[2].setBackground(Color.blue);
        rgb[2].setValue(255-old.getBlue());
        add(sliders);
        add(color);
    }
  
    
    public Color getColor(){
        return color.getBackground();
    }
  
    public void adjustmentValueChanged(java.awt.event.AdjustmentEvent e){
       Color c = new Color(255-rgb[0].getValue(),255-rgb[1].getValue(),255- rgb[2].getValue()); 
       color.setBackground(c);
    }
        
    
    public static void main(String args[]){
        Frame f = new Frame();
        f.setSize(100,50);
        ColorPicker cp = new ColorPicker();
        f.add(cp);
        f.show();
    }
}
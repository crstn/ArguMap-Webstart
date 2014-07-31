package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class KeyBox extends java.awt.Component
{
        Shader shade;
        double value;
        String label; 
        private int maxHight = 20;
        Dimension dim = new Dimension(80,maxHight);
        
    
		public KeyBox(Shader s,double value,String label){
		    shade =s;
		    this.label = label;
		    this.value = value;
		}
                
		public Dimension getPreferredSize() {
                    try{
		    FontMetrics f = this.getFontMetrics(getFont());
                     dim = new Dimension((f.stringWidth(label)+40),maxHight);
    	    return dim;
                    }
                    catch(Exception e){
                        return new Dimension(40,maxHight);
                    }
		    //System.out.println(f.stringWidth(label));
    	   
        }
        
        public Dimension getMaximumSize() {
            //System.out.println("Max Size Requested");
            return new Dimension(dim.width,20);
        }
		
		public void setShader(Shader s){
		    this.shade=s;
		}
		
		public void paint(Graphics g){
			if(shade==null) return;
			
			int h=this.getSize().height;
			int w=this.getSize().width;
			
			g.setColor(shade.getColor(value));
			g.fillRect(2,2,30,h-4);
			g.setColor(Color.black);
			g.drawRect(2,2,30,h-4);
            g.drawString(label, 38, h/2+(h/4));
		}

}
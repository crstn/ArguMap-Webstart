package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Graphics;

/*LineKeyBox class allows the colour of the line for the key to 
/be chosen and for the dimensions of the box and text to be 
/taken from the relevant layers.
/author@ "Alison Copeland". 
*/
public class LineKeyBox extends KeyBox
{
      /*  Shader shade;
        double value;
        String label; 
        private int maxHight = 20;
        Dimension dim = new Dimension(80,maxHight);
        */
    
		public LineKeyBox(Shader s,double value,String label){
		    super(s,value,label);
		}
	/*	
		public Dimension getPreferredSize() {
		    FontMetrics f = this.getFontMetrics(getFont());
		    //System.out.println(f.stringWidth(label));
    	    dim = new Dimension((f.stringWidth(label)+40),maxHight);
    	    return dim;
        }
        
        public Dimension getMaximumSize() {
            //System.out.println("Max Size Requested");
            return new Dimension(dim.width,20);
        }
		
		public void setShader(Shader s){
		    this.shade=s;
		}
	*/	
		public void paint(Graphics g){
			if(shade==null) return;
			
			int h=this.getSize().height;
			int w=this.getSize().width;

			g.setColor(Color.white);
			g.drawRect(2,2,30,h-4);
			g.setColor(shade.getColor(value));
			g.drawLine(2,15,30,26);
            g.drawString(label, 38, h/2+(h/4));
		}

}
package uk.ac.leeds.ccg.geotools;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.image.MemoryImageSource;

import uk.ac.leeds.ccg.geotools.misc.FormatedString;

public class RampKey extends uk.ac.leeds.ccg.geotools.Key
{
    KeyBar bar;   
    Label top=new Label("0"),bottom=new Label("0");
     
	
	
	public RampKey(Shader s){
	    super(s);
        setLayout(new BorderLayout());
        top = new Label("0");
        bottom = new Label("0");
        add(top,"North");
        add(bottom,"South");
        bar = new KeyBar(shader);
        bar.setShader(s);
        add(bar,"Center");
        updateKey();
    }
    
    public void setShader(Shader s){
        super.setShader(s);
    }
    
    public void updateLabels(){
        //System.out.println("Updating label"+(shader.getRange()[1]));
        top.setText(""+FormatedString.format(shader.getRange()[0]));
        bottom.setText(""+FormatedString.format(shader.getRange()[1]));
    }
    
    
    class KeyBar extends Component{
        Shader shade;
        
    
		public KeyBar(Shader s){
		    shade =s;
		}
		
		public Dimension getPreferredSize() {
	        Dimension dim = new Dimension(10,100);
    	    return dim;
        }
		
		public void setShader(Shader s){
		    this.shade=s;
		    updateLabels();
		}
		
		public void paint(Graphics g){
			//System.out.println("Painting ");
			if(shade==null) return;
			int h=this.getSize().height;
			int w=this.getSize().width;
			int data[]=new int[h*w];
			int pt=0;
			MemoryImageSource source;
			Image image;
			double [] range=shade.getRange();
			double min=range[0];
			double max=range[1];

			double k;

			//System.out.println("key size "+w+" "+h);
			//System.out.println("key range "+min+" "+max);
			for (int i=0;i<h;i++){
				k=(((max-min)/(double)h)*(double)i+min);
				for (int j=0; j<w;j++){
					if(shade != null){
						data[pt++]=shade.getRGB(k);
					} else{
						data[pt++]=new Color(Math.min(255,(int)k),0,0).getRGB();
					}
				}
			}
				source = new MemoryImageSource(w,h,data,0,w);
				image = this.createImage(source);
				boolean fred = g.drawImage(image,0,0,
						w,h,new Color(255,255,255),new Canvas());
				//image=null;

		}
}

}

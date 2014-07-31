package uk.ac.leeds.ccg.geotools;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.util.Hashtable;

public final class UniqueShader extends SimpleShader{
	Hashtable h = new Hashtable();

	public UniqueShader(){
		super();
	}
	public int getRGB(double value){
		Double v = new Double(value);
		Color c = (Color)h.get(v);
		if(c==null) c=missingColor ;
		return c.getRGB();
	}

	public void setColor(double value, Color c){
		Double v = new Double(value);
		h.put(v,c);
	}

	public UniqueShader(Image im, int width, int height){
    Canvas obs = new Canvas();
    //int width = im.getWidth(obs);
    //int height = im.getHeight(obs);
    int[] data = new int[(height*width)];
    PixelGrabber pg = new PixelGrabber(im,0,0,width,height,data,0,width);
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
      System.err.println("interrupted waiting for pixels!");
		}
    if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
      System.err.println("image fetch aborted or errored");
      return ;
    }
    System.out.println("Post grab");
    for (int j = 0; j < width*height; j++) {
			h.put(new Double(data[j]),new Color(data[j]));
		}
	}

	public String getName(){
		return "Unique Shader";
	}
}
		


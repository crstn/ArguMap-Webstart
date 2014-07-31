package uk.ac.leeds.ccg.geotools;
import java.awt.Color;

public class valShader extends HSVShader{

public valShader(Color first){
        start = new float[3];
        end = new float[3];
        Color.RGBtoHSB(first.getRed(),first.getGreen(),first.getBlue(),start);
        //Color.RGBtoHSB(second.getRed(),second.getGreen(),second.getBlue(),end);
        end[0]=start[0];
        end[1]=start[1];
        end[2]=start[2];
        start[2]=0.0f;
        //setRange(0,1);
        //System.out.println("From : H:"+start[0]+" S:"+start[1]+" V:"+start[2]);
        //System.out.println("To : H:"+end[0]+" S:"+end[1]+" V:"+end[2]);
        setRange(0,100);
    }
    
    public void setColor(Color color){
                setFirstColor(color);
    }
    
    public Color getColor(){
        return getFirstColor();
    }
    
    /**
     * Gets a descriptive name for this shader type
     */
    public String getName(){
        return "Value Shader";
    }
}
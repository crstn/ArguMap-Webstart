/*
 * MultiPointHSVShader.java
 *
 * Created on March 20, 2001, 12:46 PM
 */

package uk.ac.leeds.ccg.geotools;
import java.awt.Color;
import java.util.Vector;
/**
 *
 * @author  jamesm
 * @version 
 */
public class MultiPointHSVShader extends RampShader implements Shader{
    public static final String name = "Multi Point HSV Shader";
    Vector shaders;
    
    double[] values;
    /** Creates new MultiPointHSVShader */
    public MultiPointHSVShader(Color[] colors,double[] values) {
        int rampCount = values.length-1;
        if(rampCount<1) throw new IllegalArgumentException("MPHS>You must specify at least two coulor/value pairs");
        System.out.println("There are "+rampCount+" ramps to build");
        for(int i=0;i<rampCount;i++){
            
            shaders.addElement(new HSVShader(values[i],values[i+1],colors[i],colors[i+1],true));
            System.out.println("Added "+i);
        }
        this.values = values;
    }
    
    public String getName(){
        return name;
    }

    
    public Color getColor(double value){
        if(value < values[0] || value > values[values.length-1]) return missingColor;
        
        for(int i=1;i<values.length;i++){
            if(value<=values[i]){
                return ((HSVShader)shaders.elementAt(i-1)).getColor(value);
            }
        }
        //should never get this far...
        return missingColor;
    }
    
    public void setKeyStyle(int styleCode) {
    }
    
}

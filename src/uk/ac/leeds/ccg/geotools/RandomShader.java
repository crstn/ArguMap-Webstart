package uk.ac.leeds.ccg.geotools;

import java.awt.Color;

public class RandomShader extends SimpleShader
{
    public Color getShade(double index)
    {
        return new Color((float)Math.random(),(float)Math.random(),(float)Math.random());
    }
    
    public Color getColor(double index){
        return getShade(index);
    }
    
    /**
     * Gets a descriptive name for this shader type
     */
    public String getName(){
        return "Random Shader";
    }
}
package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
/**
 * An interface to be used by shaders.
 * Shaders define color sceems for use in maps<p>
 * They work by returning a colour for any given value.<p>
 * How they work out what that colour should be is down to each shader to 
 * work out for itself.<p>
 * A very simple example is that of the monoShader which simply returns the same
 * color every time regardless of the value passed to it.<p>
 * Shaders are probably THE easyest and most useful thing for others to write if
 * they want to add to the geoTools package.<p>
 * 
 */
public interface Shader
{
    public final int BOX=0,LINE=1,POINT=2;
    
    /**
     * Gets RGB colour for the given value.
     * @param value A double that the shader should look up and return a colour for. 
     * @return an int RGB of the colour.
     */
    public int getRGB(double value);
    
    /**
     * get the color that reprisents this value
     * @return the color for this value
     */
    public Color getColor(double value);
    
    /**
     * Set the code that represents a missing value.
     * Many data sets have missing values in them represented
     * by a code, my using this method you can tell the shader what
     * that value is.
     * e.g. Arc/Info often uses -9999
     * @see #setMissingValueColor
     * @param code The double used to represent a missing value.
     */
    public void setMissingValueCode(double code);
    
    /**
     * Gets the current code used to represent a missing value.
     * Many data sets have missing values in them represented
     * by a code, my using this method you can ask the shader what
     * value it is currently using.
     * @see #getMissingValueColor
     * @return The value used to represent a missing value
     */
    public double getMissingValueCode();
    
    /**
     * Sets the color used to represent missing data values.
     * @param color The color to return when getColor/getRGB are passed a missing value code
     * @see #setMissingValueCode
     * @see #getRGB
     * @see #getColor
     */
    public void setMissingValueColor(Color color);
    
    /**
     * Gets the color currently used by the shader to represent missing values.
     * @return The color that represents a missing value
     * @see #getMissingValueCode
     */
    public Color getMissingValueColor();   
    
    /**
     * Sets the range of values that this shader will be expected to work
     * beween.
     * @param min A double for the lowest expected value;
     * @param max A double for the highest expected value;
     */
    public void setRange(double min,double max);
		public double[] getRange();
    
    /**
     * Sets the range of values that this shader will be expected to work
     * beween.
     * @param d A GeoData object from which to pull range info
     */
    public void setRange(GeoData d);
    
    /**
     * Gets a key panel that represents the values covered by this shader
     */
    public Key getKey(); 
   // public LineKey getLineKey();
   // public PointKey getPointKey();

    public void setKeyStyle(int styleCode);
    public int getKeyStyle();
    
    /**
     * Add a listener for shader change events
     */
     public void addShaderChangedListener(ShaderChangedListener scl);
     /**
      * remove a listened for shader change events
      */
     public void removeShaderChangedListener(ShaderChangedListener scl);
     
     public String getName();
    
}

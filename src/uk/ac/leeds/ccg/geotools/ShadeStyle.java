package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.io.Serializable;

/**
 * A shade style is passed to layers when they require re-painting.
 * The style gives simple information regarding how the layer should display itself.
 * Not all of the options are applicable to all layer types, and individual layer types are free
 * to ignore any aspects of the style if they do not fit.
 * e.g. Raster layers can ignore line color!
 * 
 * 1.1 Added line witdh as a new property
 *
 * @author James Macgill
 * @version 1.1
 */

public class ShadeStyle implements Serializable
{
    private Color lineColor = Color.black;
    private Color fillColor = Color.gray;
    private boolean lineColorFromShader = false;
    private int lineWidth = 1;
    private boolean fillColorFromShader = true;
    private boolean outlined = true;
    private boolean filled = true;
    private boolean paintModeXOR = false;
    
    public ShadeStyle(){
    }

    /**
     * Simple constructor with options for filling and outlineing.
     * 
     * @param isFilled A boolean flag, set to true if the layer should try to fill in the features that it contains.
     * @param isOutlined A boolean flag, true if the layers should try to draw a line around each feature.
     */
    
    public ShadeStyle(boolean isFilled,boolean isOutlined){
       this(isFilled,isOutlined,false);   
    }
    
    public ShadeStyle(boolean isFilled, boolean isOutlined, Color lineColor){
        this(isFilled,isOutlined,false);
        this.lineColor = lineColor;
        
    }
    
       
    public ShadeStyle(boolean isFilled,boolean isOutlined,boolean lineFromShade){
        outlined = isOutlined;
        filled = isFilled;
        lineColorFromShader = lineFromShade;
        lineWidth = 1;
    }
    
    /**
     * constructor for ShadeStyle.
     * note the use of null Colors to set the use of a shader.
     * 
     * @param isFilled A boolean flag, set to true if features should be filled in.
     * @param isOutlined A boolean flag, set to true if features should be outlined.
     * @param fill A Color to use when filling in features, set to <b>null</b> if the color should be obtained from a shader instead.
     * @param line A Color to use when outlining features, set to <b>null</b> if the color should be obtained from a shader instead.
     * @param useXOR A boolean flag, Should the features be displayed using XOR (useful for highlight and selection styles)
     */
    public ShadeStyle(boolean isFilled,boolean isOutlined,Color fill,Color line,boolean useXOR){
       this(isFilled,isOutlined,fill,line,1,useXOR);
    }

    
    /**
     * Full constructor for ShadeStyle.
     * note the use of null Colors to set the use of a shader.
     * 
     * @param isFilled A boolean flag, set to true if features should be filled in.
     * @param isOutlined A boolean flag, set to true if features should be outlined.
     * @param fill A Color to use when filling in features, set to <b>null</b> if the color should be obtained from a shader instead.
     * @param line A Color to use when outlining features, set to <b>null</b> if the color should be obtained from a shader instead.
     * @param width An int representing the line with to use.
     * @param useXOR A boolean flag, Should the features be displayed using XOR (useful for highlight and selection styles)
     */
    public ShadeStyle(boolean isFilled,boolean isOutlined,Color fill,Color line,int width,boolean useXOR){
        outlined = isOutlined;
        filled = isFilled;
        if(line == null)
        {
            lineColorFromShader = true;
        }
        else
        {
            lineColor = line;
        }
        if(fill == null)
        {
            fillColorFromShader = true;
        }
        else
        {
            fillColor = fill;
        }   
        paintModeXOR = useXOR;
    }

    /**
     * if outlineing has been switched on then this sets the colour that should be used to do that.
     * Without calling this method outlineing will default to black.
     * Note. This color will be ignored if 'lineColorFromShader' is swithed on.
     * 
     * @param c Color to draw outline in.
     * @see #setLineColorFromShader
     */
    
    public void setLineColor(Color c){
        lineColor = c;
    }
    
    public Color getLineColor(){
        return lineColor;
    }
    
    public void setLineWidth(int w){
        lineWidth = w;
    }
    
    public int getLineWidth(){
        return lineWidth;
    }
    
    public void setIsFilled(boolean flag){
        this.filled = flag;
    }
    
    public boolean isFilled(){
        return this.filled;
    }
    
    public Color getFillColor(){
        return this.fillColor;
    }
    
    public void setFillColor(Color c){
        fillColor = c;
    }
    
    public void setLineColorFromShader(boolean flag){
        this.lineColorFromShader = flag;
    }
    
    public boolean isLineColorFromShader(){
        return this.lineColorFromShader;
    }
    
    public void setIsOutlined(boolean flag){
        this.outlined =flag;
    }
    
    public boolean isOutlined(){
        return this.outlined;
    }
    
    public void setIsPaintModeXOR(boolean flag){
        this.paintModeXOR = flag;
    }
    
    public boolean isPaintModeXOR(){
        return this.paintModeXOR;
    }
}

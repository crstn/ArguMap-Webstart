/*
 * ChunkLayer.java
 *
 * Created on August 7, 2001, 5:13 PM
 */

package uk.ac.leeds.ccg.geotools;

/**
 *
 * @author  jamesm
 * @version 
 */
public class ChunkLayer extends uk.ac.leeds.ccg.geotools.MultiLayer {
    public ChunkProvider chunker;
    /** Creates new ChunkLayer */
    public ChunkLayer(ChunkProvider chunker) {
    }

    /**
     * Paints the layer to the screen.
     * The very hart of the layer mechanism, paintScaled is where layers
     * have to do most of their work.<br>
     * Calling and filling in of this method is handled by the theme to which
     * the layer has been added.<p>
     * The GeoGraphics object should provide you with everything
     * needed to plot a feature onto the screen.<br>
     * Inside gg you can use or not use the facilies provided as you see fit.<p>
     * gg.getGraphics() A Graphics object to which you should direct all of your output.
     * gg.getScale() A Scaler which you can use to convert real world(tm) co-ordinates
     * into on screen co-ordinates for use with the Graphics g object.
     * gg.getShade() shade A Shader, if you want to color your features based on a value (perhaps from the data peramiter) then
     * use shade.getColor(double value); to obtain the colours.
     * gg.getGeoData() A GeoData object, use this if your features have id's then you can obtain a coresponding value
     * from data.
     * gg.getStyle() style A style with hints on how to display the features
     */
    public void paintScaled(GeoGraphics gg) {
    }
    
}

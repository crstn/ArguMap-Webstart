package de.argumap.UI;

import javax.swing.JCheckBox;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 23.08.2004
 *
 */
public class LayerSelectionCheckBox extends JCheckBox {

    private String layerName;
    
    public LayerSelectionCheckBox(String layerTitle, boolean selected, String layerName){
        super(layerTitle, selected);
        this.layerName = layerName;
    }
    /**
     * @return Returns the layerName.
     */
    public String getLayerName() {
        return layerName;
    }
}

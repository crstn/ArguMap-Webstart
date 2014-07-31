package de.argumap.gt_extensions;

import uk.ac.leeds.ccg.geotools.PanTool;
import de.argumap.UI.ArguMapWindow;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 08.07.2004
 *  
 */
public class ArguMapPanTool extends PanTool {

    public static String NAME = "Pan";
    public static String DESCRIPTION = "Pan the map by dragging with the mouse";
    
    private ArguMapWindow applet;

    public ArguMapPanTool(ArguMapWindow applet) {
        super();
        this.applet = applet;
    }

    public void release() {
        super.release();
        applet.reloadWMC();
    }
    
    public String getName(){
        return NAME;
    }
    
    public String getDescription(){
        return DESCRIPTION;
    }
}
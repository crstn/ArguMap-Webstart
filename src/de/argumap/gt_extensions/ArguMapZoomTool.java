package de.argumap.gt_extensions;

import uk.ac.leeds.ccg.geotools.ZoomTool;
import de.argumap.UI.ArguMapWindow;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 *  
 */
public class ArguMapZoomTool extends ZoomTool {
    private ArguMapWindow applet;

    public static String NAME = "Zoom";
    public static String DESCRIPTION = "Drag a box to zoom in; press shift and drag a box to zoom out.";
    
    public ArguMapZoomTool(ArguMapWindow applet) {
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
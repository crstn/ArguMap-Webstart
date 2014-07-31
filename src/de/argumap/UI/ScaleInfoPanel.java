package de.argumap.UI;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;

import uk.ac.leeds.ccg.geotools.Viewer;
import uk.ac.leeds.ccg.widgets.ScaleBar;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 30.10.2004
 *
 */
public class ScaleInfoPanel extends Panel {
    
    //TODO hier soll auch noch das animierte GIF rein, das anzeigt,
    // wenn das Applet l‰dt
    public ScaleInfoPanel(Viewer view){
        Panel links = new Panel();
        Panel rechts = new Panel();
        FlowLayout linksLayout = new FlowLayout();
        linksLayout.setAlignment(FlowLayout.LEFT);
        FlowLayout rechtsLayout = new FlowLayout();
        rechtsLayout.setAlignment(FlowLayout.RIGHT);
        this.setLayout(new GridLayout(1,2));
        rechts.add(new ScaleBar(view));
        add(links);
        add(rechts);
    }

}

package de.argumap.UI;

import java.awt.GridLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 18.08.2004
 *
 */
public class HelpPanel extends JPanel{
    
    private JEditorPane htmlPane;
    private ArguMapWindow applet;
    
    public HelpPanel(ArguMapWindow applet){
        this.applet = applet;
        setLayout(new GridLayout(1,1));
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        JScrollPane htmlView = new JScrollPane(htmlPane);
        add(htmlView);
        //add(htmlPane);
//        try {
//            htmlPane.setPage(new URL(applet.getCodeBase()+"help.htm"));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}

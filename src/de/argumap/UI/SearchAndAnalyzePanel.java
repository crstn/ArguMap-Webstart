package de.argumap.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoLine;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoPolygon;
import uk.ac.leeds.ccg.geotools.GeoRectangle;
import uk.ac.leeds.ccg.geotools.GeoShape;
import uk.ac.leeds.ccg.geotools.HSVShader;
import uk.ac.leeds.ccg.geotools.MixedLayer;
import uk.ac.leeds.ccg.geotools.ShadeStyle;
import uk.ac.leeds.ccg.geotools.Theme;
import de.argumap.UI.treetable.JTreeTable;
import de.argumap.discussion.Contribution;

/**
 * @author Carsten Keßler, carsten.kessler@uni-muenster.de
 * @version 21.11.2004
 * 
 */
public class SearchAndAnalyzePanel extends JPanel implements ActionListener {

    JTextField searchterm;
    private ArguMapWindow window;
    private JCheckBox limitToExtent;
    private JCheckBox highlightConflictAreas;
    private Theme conflictAreas;
    private JPanel conflictPanel;
    private ColorScaleInfo colorScaleInfo;
    private JLabel numCons, proL, conL, neutralL, suggestionL, questionL;
    private Vector allContributions;

    public SearchAndAnalyzePanel(ArguMapWindow window) {
        this.window = window;

        JPanel top = new JPanel();
        top.setLayout(new FlowLayout(FlowLayout.LEFT));

        setLayout(new GridLayout(3, 0, 6, 6));

        searchterm = new JTextField(20);
        JButton go = new JButton("search");
        // JButton advanced = new JButton("advanced options...");
        go.addActionListener(this);
        // advanced.addActionListener(this);

        limitToExtent = new JCheckBox("Limit to current map extent");

        JLabel infoPane = new JLabel("Enter one or more search terms below. ");
        JLabel infoPane2 = new JLabel("If more than one term is entered,");
        JLabel infoPane3 = new JLabel("all contributions matching");
        JLabel infoPane4 = new JLabel("ANY of the terms will be found.");

        top.add(infoPane);
        top.add(infoPane2);
        top.add(infoPane3);
        top.add(infoPane4);
        top.add(searchterm);
        top.add(go);
        top.add(limitToExtent);
        top.setBorder(BorderFactory.createLineBorder(Color.darkGray));

        limitToExtent
                .setToolTipText("Limit the search to contributions with references in the map extent which is currently shown");

        updateContributions();

        highlightConflictAreas = new JCheckBox("Identify conflict areas");
        highlightConflictAreas.addActionListener(this);

        JPanel statsPanel = new JPanel();
        conflictPanel = new JPanel();

        statsPanel.setBackground(Color.white);
        statsPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray));

        statsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel statsElements = new JPanel();
        statsElements.setLayout(new GridLayout(3,1));
        statsElements.setBackground(Color.white);
        statsElements.setSize(statsPanel.getSize());
        
        JPanel conflictElements = new JPanel();
        conflictElements.setLayout(new FlowLayout(FlowLayout.LEFT));
        conflictPanel.setLayout(new GridLayout(3,1));

        numCons = new JLabel("Number of contributions referring to these objects: 0");

        JPanel typeCount = new JPanel();
        typeCount.setLayout(new GridLayout(2, 5, 50, 10));
        typeCount.setBackground(Color.white);

        proL = new JLabel("0");
        conL = new JLabel("0");
        neutralL = new JLabel("0");
        suggestionL = new JLabel("0");
        questionL = new JLabel("0");

        typeCount.add(new JLabel(new ImageIcon(JTreeTable.class.getResource("icons/pro.gif"))));
        typeCount.add(new JLabel(new ImageIcon(JTreeTable.class.getResource("icons/contra.gif"))));
        typeCount.add(new JLabel(new ImageIcon(JTreeTable.class.getResource("icons/neutral.gif"))));
        typeCount.add(new JLabel(new ImageIcon(JTreeTable.class.getResource("icons/suggestion.gif"))));
        typeCount.add(new JLabel(new ImageIcon(JTreeTable.class.getResource("icons/question.gif"))));

        typeCount.add(proL);
        typeCount.add(conL);
        typeCount.add(neutralL);
        typeCount.add(suggestionL);
        typeCount.add(questionL);

        statsPanel.add(new JLabel(
                "Statistics on the currently highlighted map features"));
        statsElements.add(numCons);
        statsElements.add(typeCount);
        statsElements.setSize(statsPanel.getSize());
        statsPanel.add(statsElements);

        conflictPanel.add(new JLabel(
                "Check to generate color shading for conflict areas"));
        conflictPanel.add(highlightConflictAreas);
        conflictElements.setBorder(BorderFactory.createLineBorder(Color.darkGray));

        conflictElements.add(conflictPanel);
        add(top);
        add(statsPanel);
        add(conflictElements);

    }

    public void updateContributions() {
        allContributions = window.getArguPanel().getAllContributions();
    }

    public void updateStats(int[] iDs) {
        // alles auf 0 setzen
        Vector referencingContributions = new Vector();
        int pro = 0;
        int con = 0;
        int neutral = 0;
        int suggestion = 0;
        int question = 0;

        // alle contributions durchgehen und für die, die
        // eines der referenzobjekte, deren IDs übergeben wurden,
        // referenzieren, die entsprechenden angaben hinzufügen:
        Iterator iter = allContributions.iterator();
        while (iter.hasNext()) {
            Contribution current = (Contribution) iter.next();
            ArrayList refs = current.getReferenceObjects();

            for (int i = 0; i < iDs.length; i++) {
                Iterator refIter = refs.iterator();
                while (refIter.hasNext()) {
                    GeoShape currentShape = (GeoShape) refIter.next();
                    if (iDs[i] == currentShape.getID()
                            && !referencingContributions.contains(current)) {
                        referencingContributions.add(current);

                        if (current.getType().equals("pro")) {
                            pro++;
                        } else if (current.getType().equals("contra")) {
                            con++;
                        } else if (current.getType().equals("neutral")) {
                            neutral++;
                        } else if (current.getType().equals("question")) {
                            question++;
                        } else if (current.getType().equals("suggestion")) {
                            suggestion++;
                        }
                    }
                }
            }
        }
        // die entsprechenden felder setzen:
        numCons.setText("Number of contributions referring to these objects: " + referencingContributions.size());
        proL.setText("" + pro);
        conL.setText("" + con);
        neutralL.setText("" + neutral);
        questionL.setText("" + question);
        suggestionL.setText("" + suggestion);
    }

    

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("search")) {
            GeoRectangle extent = window.getViewer().getScale().getMapExtent();
            String search = searchterm.getText();
            String[] searchterms = search.split(" ");
            Vector cons = window.getCardPanel().getArguPanel()
                    .getAllContributions();
            Vector matches = new Vector();
            Iterator iterator = cons.iterator();
            // alle contributions durchgehen:
            while (iterator.hasNext()) {
                boolean textmatch = false;
                boolean match = false;
                Contribution current = (Contribution) iterator.next();
                // für jeden term nachschauen, ob er im text oder im titel
                // der aktuellen contribution vorkommt
                for (int i = 0; i < searchterms.length; i++) {
                    // die suche unterscheidet nicht zwischen gr./kl. schreibung
                    if (current.getTitle().toUpperCase().indexOf(
                            searchterms[i].toUpperCase()) > -1
                            || current.getMsg().toUpperCase().indexOf(
                                    searchterms[i].toUpperCase()) > -1) {
                        textmatch = true;
                    }
                }

                // falls die entsprechende checkbox aktiviert ist,
                // nachsehen, ob der beitrag referenzobjekt im aktuellen
                // kartenausschnitt hat:
                if (limitToExtent.isSelected() && textmatch) {
                    // alle referenzobjekte durchgehen:
                    ArrayList refs = current.getReferenceObjects();
                    Iterator ior = refs.iterator();
                    while (ior.hasNext()) {
                        GeoShape gs = (GeoShape) ior.next();
                        if (extent.intersects(gs)) {
                            match = true;
                        }
                    }

                    // wenn die checkbox nicht geklickt wurde, aber ein treffer
                    // im text gefunden wurde, ist der beitrag ein treffer:
                } else if (!limitToExtent.isSelected() && textmatch) {
                    match = true;
                }

                if (match) {
                    matches.add(current);
                }
            }

            if (matches.size() > 0) { // wenn es treffer gibt:
                // die gefundenen beiträge in ein array übertragen:
                Iterator iter = matches.iterator();
                Contribution[] contributions = new Contribution[matches.size()];
                int i = 0;
                while (iter.hasNext()) {
                    Contribution current = (Contribution) iter.next();
                    contributions[i] = current;
                    i++;
                }

                // highlighten auslösen:
                window.getCardPanel().getArguPanel().highlightContributions(
                        contributions);

                // und das ArguPanel anzeigen:
                window.getTabs().setSelectedIndex(0);
            } else { // wenn es keine treffer gibt, meldung zeigen:
                JOptionPane
                        .showMessageDialog(
                                this,
                                "There are no contributions in the discussion matching your criteria",
                                "No results found",
                                JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Identify conflict areas")) {
            if (highlightConflictAreas.isSelected()) { // activate color shading:
                //First add all reference objects to a new layer:
                MixedLayer layer = new MixedLayer();
                
                ArrayList shapes = window.getArguPanel().getReferenceObjects();
                Iterator iter = shapes.iterator();
                while (iter.hasNext()) {
                    Object current = iter.next();
                    if (current instanceof GeoPoint) {
                        layer.addGeoPoint((GeoPoint) current);
                    } else if (current instanceof GeoLine) {
                        layer.addGeoLine((GeoLine) current);
                    } else if (current instanceof GeoPolygon) {
                        layer.addGeoPolygon((GeoPolygon) current);
                    } else {
                        System.out
                                .println("Error generating conflict area shading: "
                                        + "Feature could not be assigned to a valid type.");
                    }
                }

                // create the data that will be used for shading:
                GeoData data = window.getArguPanel().getGeoData();

                ShadeStyle style = new ShadeStyle(true, true, null, null, false);
                style.setLineWidth(3);
               
                
                // create shader:
                HSVShader shade = new HSVShader(data.getMin(), data.getMax(),
                        Color.yellow, Color.red, true);
                
                // add layer to a "fresh" Theme:
                conflictAreas = new Theme(layer);
                conflictAreas.setStyle(style);
                conflictAreas.setGeoData(data);
                conflictAreas.setShader(shade);
                
                // und zum viewer hinzufügen:
                window.getViewer().addTheme(conflictAreas);
                colorScaleInfo = new ColorScaleInfo((int) data.getMin(),
                        (int) data.getMax());
                conflictPanel.add(colorScaleInfo);

            } else { // deactivate
                // einfach das Theme aus dem viewer wieder rausnehmen.
                window.getViewer().removeTheme(conflictAreas);
                conflictPanel.remove(colorScaleInfo);
            }
            window.getViewer().paint(window.getViewer().getGraphics());
            getParent().paint(getParent().getGraphics());
        }
    }

    private class ColorScaleInfo extends JPanel {

        public ColorScaleInfo(int min, int max) {

            setLayout(new BorderLayout());
            add(new JLabel("" + min), BorderLayout.WEST);
            add(new JLabel("" + max), BorderLayout.EAST);
            add(new JLabel(new ImageIcon(SearchAndAnalyzePanel.class.getResource("img/verlauf.jpg"), "verlauf")),
                    BorderLayout.CENTER);
        }
    }

}

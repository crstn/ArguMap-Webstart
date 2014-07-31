package de.argumap.UI;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import uk.ac.leeds.ccg.geotools.Theme;
import de.argumap.discussion.Contribution;

/**
 * This Panel allows the user to switch the single Layers of the Map on and off.
 * 
 * @author Carsten Keßler, carsten.kessler@uni-muenster.de
 * @version 14.08.2004; last update 02.11.2004
 *  
 */
public class LayerSelectionPanel extends JPanel implements ActionListener {

	private ArguMapWindow window;

	//array mit den checkboxen für die WMC layer -
	//die anderen checkboxen sind da nicht drin!!
	private LayerSelectionCheckBox[] checkBoxArray;

	private String activeLayers;

	private JCheckBox dbPointsCheckBox;

	public LayerSelectionPanel(ArguMapWindow window) {
		this.window = window;
		try {
			JPanel holder = new JPanel();
			//checkboxen für die layer mit den punkten und shapes:
			JCheckBox userReferencesCheckBox = new JCheckBox(
					"Personal references", true);
			dbPointsCheckBox = new JCheckBox("Other users' references", true);
			JCheckBox shapeCheckBox = new JCheckBox(window
					.getShapeName(), true);
			userReferencesCheckBox.addActionListener(this);
			dbPointsCheckBox.addActionListener(this);
			shapeCheckBox.addActionListener(this);

			//checkboxen für die layer aus dem WMC:
			URL layerURL = new URL(window.getServletBase() + "layers;jsessionid="+window.getSessionID());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					layerURL.openStream()));
			String layersString = in.readLine();
			in.close();
			//2teiliges array: im 1. teil stehen die names, im 2. die titles
			String[] layerNamesAndTitlesAndHidden = layersString.split(" --- ");
			String[] layerNamesArray = layerNamesAndTitlesAndHidden[0]
					.split(" /// ");
			String[] layerTitlesArray = layerNamesAndTitlesAndHidden[1]
					.split(" /// ");
			String[] layerHiddenArray = layerNamesAndTitlesAndHidden[2]
					.split(" /// ");

			//die strings aus dem layerHiddenArray in ein äquivalentes Array
			// aus booleans übertragen:
			boolean[] boolLayerHiddenArray = new boolean[layerHiddenArray.length];
			for (int i = 0; i < layerHiddenArray.length; i++) {
				boolean current = new Boolean(layerHiddenArray[i])
						.booleanValue();
				boolLayerHiddenArray[i] = current;
			}

			checkBoxArray = new LayerSelectionCheckBox[layerNamesArray.length];
			holder.setLayout(new GridLayout(layerNamesArray.length + 5, 1, 10,
					10));
			this.setLayout(new GridLayout(1,1));

            holder.add(new JLabel("Check the layers you want to view on the map."));
            holder.add(new JLabel(" "));
			holder.add(userReferencesCheckBox);
			holder.add(dbPointsCheckBox);
			holder.add(shapeCheckBox);

			String layers = "";
			for (int i = 0; i < layerNamesArray.length; i++) {
				//CAREFUL: the layers which are set to true in the array are HIDDEN,
                // so they need to be set to false in the new array (which indicates visibility)
				checkBoxArray[i] = new LayerSelectionCheckBox(
						layerTitlesArray[i], !boolLayerHiddenArray[i],
						layerNamesArray[i]);
				checkBoxArray[i].addActionListener(this);
				holder.add(checkBoxArray[i]);
				//den layer zu den activelayers hinzufügen, falls er nicht
				// ausgeblendet ist:
				if (!boolLayerHiddenArray[i]) {
					layers += "," + checkBoxArray[i].getLayerName();
				}
			}
			
//			add(new JScrollPane(holder, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
            add(new JScrollPane(holder));
			this.activeLayers = layers;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof LayerSelectionCheckBox) {
			String layers = "";
			for (int i = 0; i < checkBoxArray.length; i++) {
				if (checkBoxArray[i].isSelected()) {
					layers += "," + checkBoxArray[i].getLayerName();
				}
			}
			this.activeLayers = layers;
			//falls alle wmc layer ausgeschaltet sind, den entsprechenden
			//layer im viewer ausblenden, weil sonst evtl. eine fehlermeldung
			//vom wms angezeigt wird:
			if (activeLayers.equals("")) {
				window.getViewer().setThemeIsVisible(window.getImgTheme(),
						false);
			} else {
				window.getViewer()
						.setThemeIsVisible(window.getImgTheme(), true);
				window.reloadWMC();
			}
		} else {
			if (e.getActionCommand() == "Personal references") {
				//rausfinden, ob das theme sichtbar ist:
				Theme t = window.getUserPointTheme();
				boolean visible = window.getViewer().isThemeVisible(t);

				window.getViewer().setThemeIsVisible(t, !visible);
				window.getViewer().setThemeIsVisible(window.getUserLineTheme(),
						!visible);
				window.getViewer().setThemeIsVisible(
						window.getUserPolygonTheme(), !visible);
			} else if (e.getActionCommand() == "Other users' references") {

				Theme[] themes = new Theme[3];
				themes[0] = window.getPointsFromDB();
				themes[1] = window.getLinesFromDB();
				themes[2] = window.getPolygonsFromDB();
				
				int i = 0;
				while (i < 3) {
					Theme t = themes[i];
					i++;
					//Theme thigh = themes[i];
					//i++;
					boolean visible = window.getViewer().isThemeVisible(t);
					//boolean highvisible = window.getViewer().isThemeVisible(
						//	thigh);

					//wenn beide layer ausgeblendet sind, kann es sein, dass
					//der highlightlayer ausgeblendet wurde, als ein beitrag
					// ohne
					//refrenzpunkte angeklickt wurde - das muss man hier
					// beachten:

//					if (visible == false && highvisible == false) {
//						Contribution selected = window.getCardPanel()
//								.getArguPanel().getSelectedContribution();
//						// >> den highlight layer nur anzeigen, wenn zum aktuell
//						// gewählten beitrag referenzpunkte vorhanden sind:
//						// abfragen, ob überhaupt ein beitrag ausgewählt ist:
//						if (selected != null)
//							if (selected.getReferenceObjects().size() != 0)
//								window.getViewer().setThemeIsVisible(thigh,
//										true);
//
//						window.getViewer().setThemeIsVisible(t, true);
//					} else if (visible == true && highvisible == true) {
//						window.getViewer().setThemeIsVisible(t, false);
//						window.getViewer().setThemeIsVisible(thigh, false);
//					} else if (visible == true && highvisible == false) {
//						window.getViewer().setThemeIsVisible(t, false);
//					}
                    
                    if (visible == false) {
                        Contribution selected = window.getCardPanel()
                                .getArguPanel().getSelectedContribution();
                        window.getViewer().setThemeIsVisible(t, true);
                    } else if (visible == true) {
                        window.getViewer().setThemeIsVisible(t, false);
                    } 
				}
			} else if (e.getActionCommand().equals(
					window.getShapeName())) {
				Theme t = window.getShapeTheme();
				boolean visible = window.getViewer().isThemeVisible(t);
				window.getViewer().setThemeIsVisible(t, !visible);
			}
		}
	}

	/**
	 * @return Returns the activeLayers, comma-seperated and without spaces
	 *         (spaces are replaced by '%20' so that the String can be used in
	 *         URLs).
	 */
	public String getActiveLayers() {
		return activeLayers.replaceAll(" ", "%20");
	}

	/**
	 * @return Returns the dbPointsCheckBox.
	 */
	public JCheckBox getDbPointsCheckBox() {
		return dbPointsCheckBox;
	}
}
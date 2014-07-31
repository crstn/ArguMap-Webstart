package de.argumap.UI;

import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This Panel shows information on the state of the prototype. The default
 * message is "Ready", which is shown whenever the prototype is not busy (i.e.
 * loading a WMS background map, reading / writing discussion contributions to
 * the database, etc.)
 * 
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 27.09.2005
 * 
 */
public class StatusInformationPanel extends JPanel {

    // message codes:
    public static int ready = 0;
    public static int loadingMap = 1;
    public static int savingPost = 2;
    public static int reloadingDiscussion = 3;

    // ... and the corresponding messages:
    private static String readyMSG = "  Ready.";
    private static String loadingMapMSG = "Loading Map...";
    private static String savingPostMSG = "Uploading your discussion contribution to the server...";
    private static String reloadingDiscussionMSG = "Updating discussion with the latest contributions from server...";

    private JLabel info;
    private ImageIcon sandglass;

    private String lastInfo;
    private Icon lastIcon;

    private int lastReasonCode = -1;

    public StatusInformationPanel() {
        // the initial text needs to be extended a bit, because otherwise some
        // of the texts which will be filled in here won't fit:
        this.info = new JLabel("Initializing...", JLabel.LEFT);
        sandglass = new ImageIcon(StatusInformationPanel.class
                .getResource("img/clock.gif"));
        info.setIcon(new ImageIcon(StatusInformationPanel.class
                .getResource("img/info.gif")));
        setLayout(new GridLayout(1,1,10,10));
        add(info);
    }

    /*
     * Displays predefined information texts on frequent events in the
     * prototype. The reason codes can be obtained from the static integers in
     * this class.
     */
    public void statusChanged(int reason) {
        // make sure we only do something if the status has really changed:
        if (reason != lastReasonCode) {
            switch (reason) {
            case 0:
                // workaround:
                // prevent the status to show "ready" between sending a
                // contribution and updating the discussion
                if (lastReasonCode != savingPost) {
                    info.setIcon(null);
                    info.setText(readyMSG);
                }
                break;
            case 1:
                info.setIcon(sandglass);
                info.setText(loadingMapMSG);
                break;
            case 2:
                info.setIcon(sandglass);
                info.setText(savingPostMSG);
                break;
            case 3:
                info.setIcon(sandglass);
                info.setText(reloadingDiscussionMSG);
                break;
            default:
                System.out.println(this.getClass().getName()
                        + ": unknown reason code ("+reason+") for status change");
                break;
            }
            lastReasonCode = reason;
        }
    }

    /**
     * Displays any information as defined in argument "info". Stores the last displayed information
     * which can be retreived by calling rollback();
     * 
     */
    public void showInfo(String infotext) {
        // backup current status of text and icon for rollback:
        lastInfo = info.getText();
        lastIcon = info.getIcon();

        // show info icon and new info text:
        info.setIcon(new ImageIcon(StatusInformationPanel.class
                .getResource("img/info.gif")));
        info.setText(infotext);
    }

    /**
     * Restores the previous state of the panel.
     * 
     */
    public void rollback() {
        info.setIcon(lastIcon);
        info.setText(lastInfo);
    }

}

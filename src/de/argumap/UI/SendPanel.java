package de.argumap.UI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import uk.ac.leeds.ccg.geotools.GeoLine;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoPolygon;
import uk.ac.leeds.ccg.geotools.GeoShape;
import de.argumap.discussion.Contribution;

/**
 * @author Carsten Keßler, carsten.kessler@uni-muenster.de
 * @version 14.07.2004
 * 
 */
public class SendPanel extends Panel implements ActionListener {

    private JTextField title;
    private JTextArea msg;
    private CardPanel container;
    private Contribution parentContribution;
    private boolean hasParentContribution;
    CheckboxGroup cbg;
    private boolean jobDone = true;

    public SendPanel(CardPanel container) {

        this.container = container;
        this.hasParentContribution = false;
        setLayout(new BorderLayout());

        // Labels und Eingabefeld für Titel:
        JLabel titelLabel = new JLabel("Your topics's title:");
        title = new JTextField();

        msg = new JTextArea("Enter your message here.");
        msg.setBorder(BorderFactory.createLineBorder(Color.black));
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        msg.setFont(titelLabel.getFont().deriveFont(12.0f));

        Panel head = new Panel();
        head.setLayout(new GridLayout(2, 0, 5, 5));
        head.add(titelLabel);
        head.add(title);

        // CheckBoxen für die Auswahl des Beitragstypes:
        Panel boxen = new Panel();
        boxen.setLayout(new GridLayout(2, 3));
        cbg = new CheckboxGroup();
        boxen.add(new JLabel("Type:"));
        boxen.add(new Checkbox("suggestion", cbg, false));
        boxen.add(new Checkbox("question", cbg, false));
        boxen.add(new Checkbox("pro", cbg, false));
        boxen.add(new Checkbox("contra", cbg, false));
        boxen.add(new Checkbox("neutral", cbg, true));

        Panel oben = new Panel();
        oben.setLayout(new GridLayout(2, 1));
        oben.add(head);
        oben.add(boxen);

        // buttons:
        Panel buttons = new Panel();
        buttons.setBackground(Color.white);
        JButton send = new JButton("Send");
        send.addActionListener(this);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        buttons.add(send);
        buttons.add(cancel);

        add(oben, "North");
        add(msg, "Center");
        add(buttons, "South");

    }

    public CardPanel getContainer() {
        return container;
    }

    public String getContributionType() {
        return cbg.getSelectedCheckbox().getLabel();
    }

    /**
     * Sets the parent contribution of the conrtibution that is currently being
     * written.
     * 
     * @param object
     */
    public void setParentContribution(Contribution contribution) {
        hasParentContribution = true;
        this.parentContribution = contribution;
        title.setText("Re: " + parentContribution.getTitle());
        msg.setText(parentContribution.getMsg());
    }

    /**
     * @param hasParentContribution
     * 
     */
    public void setHasParentContribution(boolean hasParentContribution) {
        this.hasParentContribution = hasParentContribution;
    }

    /**
     * Uploads a new contribution to the server by fetching the information from
     * the form fields, and by collecting the reference objects from the
     * corresponding map layers. Make sure this runs in a separate thread
     * whenever it is called, because this tends to take some seconds and
     * freezes the GUI if it is not in a separate thread!
     * 
     */
    public void uploadContribution() {

        // db-entry for msg body / title:
        int child_of = 0;
        if (hasParentContribution) {
            child_of = parentContribution.getContributionID();
        }

        int c_id;
        try {
            // insert contrbution-text into db:
            c_id = insertContributionByHttpPOST(title.getText(), msg.getText(),
                    container.getArguMapWindow().getLoggedIn().getMemberID(),
                    child_of, getContributionType());

            // insert reference objects:

            // -------------------------points--------------
            Vector points = container.getArguMapWindow().getUserPoints();
            if (points.size() > 0) {
                Iterator iter = points.iterator();
                while (iter.hasNext()) {
                    GeoPoint p = (GeoPoint) iter.next();
                    int locationid = insertFeature(p);
                    // add reference-entry to db:
                    insertReference(locationid, c_id);

                }
            }

            // -------------------- lines ----------------

            Vector lines = container.getArguMapWindow().getUserLines();

            if (lines.size() > 0) {
                Iterator ior = lines.iterator();
                while (ior.hasNext()) {
                    GeoLine line = (GeoLine) ior.next();
                    int locationid = insertFeature(line);
                    insertReference(locationid, c_id);
                }
            }

            // -------------------- polygons ----------------

            Vector polygons = container.getArguMapWindow().getUserPolygons();
            if (polygons.size() > 0) {
                Iterator ior = polygons.iterator();
                while (ior.hasNext()) {
                    GeoPolygon pol = (GeoPolygon) ior.next();
                    int locationid = insertFeature(pol);
                    insertReference(locationid, c_id);
                }
            }

            // tell the server to store the map context of this contribution
            String storeContextString = container.getArguMapWindow()
                    .getServletBase()
                    + "storecontext;jsessionid="
                    + container.getArguMapWindow().getSessionID()
                    + "?id="
                    + c_id;
            URL storeContextURL = new URL(storeContextString);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    storeContextURL.openStream()));
            in.close();

            // reset WMCReferenceTool so that the references inserted into
            // the db are deleted...
            container.getArguMapWindow().getControls().flushUserReferences();
            // ... because they will show up when the references from the
            // database are reloaded:
            container.getArguMapWindow().refreshReferencesFromDB();

        } catch (IOException eg) {
            eg.printStackTrace();
        }

        // post is saved, now the discussion will be reloaded. show
        // corresponding info:
        container.getArguMapWindow().getStatusInformationPanel().statusChanged(
                StatusInformationPanel.reloadingDiscussion);

        // update ArguPanel
        // TODO just reload the tree, it's not necessary to create a
        // complete new panel
        container.setArguPanel(new ArguPanel(container));

        // empty input fields
        title.setText(null);
        msg.setText(null);

        // return to the discussion
        CardLayout cl = (CardLayout) container.getLayout();
        cl.show(container, "arguPanel");

        // refresh the stats-panel
        SearchAndAnalyzePanel ap = (SearchAndAnalyzePanel) container
                .getArguMapWindow().getTabs().getComponentAt(2);
        ap.updateContributions();
        container.getArguMapWindow().getCardPanel().getSendPanel().setEnabled(
                true);
        jobDone = true;
        container.getArguMapWindow().getStatusInformationPanel().statusChanged(
                StatusInformationPanel.ready);

    }

    public boolean isJobDone() {
        return jobDone;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Send") {

            String inputMsg = msg.getText().trim();
            String inputTitle = title.getText().trim();

            // check if everything has been filled in:
            if (inputTitle.length() == 0 || inputMsg.length() == 0) {
                jobDone = true;
                // fehlermeldung:
                JOptionPane.showMessageDialog(container.getParent(),
                        "Please fill in both title and message. ", "error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            jobDone = false;

            // Do work outside of EDT
            new Thread("Upload new discussion contribution") {
                public final void run() {
                    while (!jobDone) {
                        SwingUtilities.invokeLater(new Runnable() {
                            // Change GUI inside EDT
                            public final void run() {
                                // deactive the sendPanel, so that the user
                                // cannot edit his post while it is sent to the
                                // server
                                container.getArguMapWindow().getCardPanel()
                                .getSendPanel().setEnabled(false);
                                
                                // show info:
                                container
                                        .getArguMapWindow()
                                        .getStatusInformationPanel()
                                        .statusChanged(
                                                StatusInformationPanel.savingPost);
                            }
                        });
                        uploadContribution(); // Does the heavy work

                    }
                }

            }.start();
        } else if (e.getActionCommand() == "Cancel") {
            // empty input fields
            title.setText(null);
            msg.setText(null);
            // return to discussion:
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "arguPanel");
        }

    }

    /**
     * Adds a reference between a location and a contribution to the database.
     * 
     * @param locationid -
     *            the location's id.
     * @param c_id -
     *            the contribution's id.
     */
    private void insertReference(int locationid, int c_id) {
        URL insertReferenceURL;
        try {
            insertReferenceURL = new URL(container.getArguMapWindow()
                    .getServletBase()
                    + "newreference;jsessionid="
                    + container.getArguMapWindow().getSessionID()
                    + "?location=" + locationid + "&contribution=" + c_id);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    insertReferenceURL.openStream()));
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insters a new feature into the database.
     * 
     * @param The
     *            feature to insert.
     * @return The new feature's database-id.
     */
    private int insertFeature(GeoShape feature) {
        int response = -1; // error code

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        BufferedReader inStream = null;
        String boundary = "*****";

        Vector pts = feature.getPoints();
        Iterator iter = pts.iterator();

        double[] xpoints = new double[pts.size()];
        double[] ypoints = new double[pts.size()];

        int j = 0;

        while (iter.hasNext()) {
            GeoPoint current = (GeoPoint) iter.next();
            xpoints[j] = current.x;
            ypoints[j] = current.y;
            j++;
        }

        try {
            String urlString = container.getArguMapWindow().getServletBase()
                    + "newfeature;jsessionid="
                    + container.getArguMapWindow().getSessionID();
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(conn.getOutputStream());

            for (int i = 0; i < xpoints.length; i++) {
                dos.writeBytes(xpoints[i] + " ::: ");
            }

            // separate x und y coords by " --- "
            dos.writeBytes(" --- ");

            for (int i = 0; i < ypoints.length; i++) {
                dos.writeBytes(ypoints[i] + " ::: ");
            }

            // tell the servlet whether this is a point, line or a polygon
            if (feature instanceof GeoLine)
                dos.writeBytes(" --- GeoLine");
            else if (feature instanceof GeoPolygon)
                dos.writeBytes(" --- GeoPolygon");
            else
                dos.writeBytes(" --- GeoPoint");

            dos.flush();
            dos.close();

            inStream = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            response = new Integer(inStream.readLine()).intValue();
            inStream.close();
            System.out.println("Feature-ID from DB: " + response);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Inserts a new contribution into the database.
     * 
     * @param title
     * @param msg
     * @param creator
     * @param childof
     * @param contributionType
     * @return The new contribution's ID.
     */
    private int insertContributionByHttpPOST(String title, String msg,
            int creator, int childof, String contributionType) {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        BufferedReader inStream = null;

        String boundary = "*****";

        String urlString = container.getArguMapWindow().getServletBase()
                + "/newcontribution;jsessionid="
                + container.getArguMapWindow().getSessionID();

        try {
            // ------------------ CLIENT REQUEST

            // open a URL connection to the Servlet
            URL url = new URL(urlString);
            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) url.openConnection();
            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(title + " ::: " + msg + " ::: " + creator + " ::: "
                    + childof + " ::: " + contributionType);

            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // ------------------ read the SERVER RESPONSE

        String output = "-1";
        try {
            inStream = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            output = inStream.readLine();
            inStream.close();
        } catch (IOException ioex) {
            System.out.println("OIException (ServerResponse): " + ioex);
        }
        return (new Integer(output)).intValue();

    }

}
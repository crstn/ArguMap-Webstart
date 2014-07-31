package de.argumap.discussion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.GeoLine;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoPolygon;
import uk.ac.leeds.ccg.geotools.GeoShape;
import de.argumap.UI.ArguMapWindow;

/**
 * @author Carsten Keßler, carsten.kessler@uni-muenster.de
 * @version 14.07.2004
 *  
 */
public class Contribution {
    Object[] children;
    private int contributionID, parentNode;
    private String title, msg;
    private Participator creator;
    private ArrayList referenceObjects;
    private Calendar timestamp;
    private ArguMapWindow applet;
    private String type;

    /**
     * @return Returns the timestamp.
     */
    public Calendar getTimestamp() {
        if (timestamp != null) {
            return timestamp;
        } else {
            Calendar added = Calendar.getInstance();
            added.set(0, 0, 0, 0, 0, 0);
            return added;
        }

    }

    /**
     * @param contributionID
     * @param parentNode
     * @param title
     * @param msg
     * @param creator
     */
    public Contribution(int contributionID, int parentNode, String title,
            String msg, Participator creator, ArrayList referenceObjects,
            Calendar timestamp, ArguMapWindow applet, String type) {
        this.contributionID = contributionID;
        this.parentNode = parentNode;
        this.title = title;
        this.msg = msg;
        this.creator = creator;
        this.referenceObjects = referenceObjects;
        this.timestamp = timestamp;
        this.applet = applet;
        this.type = type;
    }

    /**
     * Creates an empty contribution, which can be used as the invisible
     * root of the discussion tree. 
     * 
     */
    public Contribution(ArguMapWindow window) {
        this.creator = new Participator(-1, "", "", "");
        this.referenceObjects = new ArrayList();
        this.timestamp = null;
        this.applet = window;
        this.contributionID = 0;
        this.type = "";
    }

    /**
     * @return Returns the contributionID.
     */
    public int getContributionID() {
        return contributionID;
    }

    /**
     * @param contributionID
     *            The contributionID to set.
     */
    public void setContributionID(int contributionID) {
        this.contributionID = contributionID;
    }

    /**
     * @return Returns the creator.
     */
    public Participator getCreator() {
        return creator;
    }

    /**
     * @param creator
     *            The creator to set.
     */
    public void setCreator(Participator creator) {
        this.creator = creator;
    }

    /**
     * @return Returns the msg.
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg
     *            The msg to set.
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return Returns the parentNode.
     */
    public int getParentNode() {
        return parentNode;
    }

    /**
     * @param parentNode
     *            The parentNode to set.
     */
    public void setParentNode(int parentNode) {
        this.parentNode = parentNode;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        return title;
    }

    /**
     * @return Returns the referencePoints.
     */
    public ArrayList getReferenceObjects() {
        return referenceObjects;
    }

    /**
     * @param referencePoints
     *            The referencePoints to set.
     */
    public void setReferencePoints(ArrayList referencePoints) {
        this.referenceObjects = referencePoints;
    }

    public String getInfoString() {
        String month = "error";
        switch (timestamp.get(2)) {
        case 0:
            month = "January";
            break;
        case 1:
            month = "February";
            break;
        case 2:
            month = "March";
            break;
        case 3:
            month = "April";
            break;
        case 4:
            month = "May";
            break;
        case 5:
            month = "June";
            break;
        case 6:
            month = "July";
            break;
        case 7:
            month = "August";
            break;
        case 8:
            month = "September";
            break;
        case 9:
            month = "October";
            break;
        case 10:
            month = "November";
            break;
        case 11:
            month = "Dacember";
            break;
        }
        String output = creator.getFirstName() + " " + creator.getLastName()
                + " (" + creator.getEMail() + ")\n" + month + " "
                + timestamp.get(5) + ", " + timestamp.get(1) + "  ";

        for (int i = 11; i < 13; i++) {
            int s = timestamp.get(i);
            String str;
            if (s < 10) {
                str = "0" + s;
            } else {
                str = "" + s;
            }
            output = output + str + ":";
        }
        //delete last colon
        output = output.substring(0, output.length() - 1);
        return output;
    }

    public String getDateInfoString() {
        String output = "";
        try {
            output = (timestamp.get(2))+1 + "-" + timestamp.get(5) + ", "
                    + timestamp.get(1) + "  ";

            int s = timestamp.get(11);
            String str;
            if (s < 10) {
                str = "0" + s;
            } else {
                str = "" + s;
            }
            output = output + str + ":";

            s = timestamp.get(12);
            if (s < 10) {
                str = "0" + s;
            } else {
                str = "" + s;
            }
            output = output + str;

        } catch (NullPointerException e) {
            output = "";
        }

        return output;
    }

    public int getNumberOfReferenceObjects() {
        return referenceObjects.size();
    }

    /**
     * Loads the children, caching the results in the children ivar.
     */
    public Object[] getChildren() {
        if (children != null) {
            return children;
        }
        try {
            Vector<Contribution> resultCons = new Vector<Contribution>();

            URL contributionURL = new URL(applet.getServletBase()
                    + "children;jsessionid="+applet.getSessionID()+"?contribution=" + this.getContributionID());
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    contributionURL.openStream()));
            String contributionString = in.readLine();
            in.close();

            //jetzt die rückgabe des servlets auseinanderbauen;
            //zuerst die einzelnen contributions:
            if (contributionString != null) {
                //System.out.println(contributionString);
                String[] cons = contributionString.split(" ::: ");
                //dann für jede der contributions die einzelnen daten:
                for (int i = 0; i < cons.length; i++) {
                    String[] condata = cons[i].split(" /// ");

                    String title = condata[0];
                    String firstname = condata[1];
                    String lastname = condata[2];
                    String email = condata[3];
                    String msg = condata[4];
                    int c_id = (new Integer(condata[5])).intValue();
                    int child_of = (new Integer(condata[6])).intValue();
                    int creatorID = (new Integer(condata[7])).intValue();
                    String date = condata[8];
                    String contype = condata[9];

                    //determine date:
                    int year = (new Integer(date.substring(0, 4))).intValue();
                    int month = ((new Integer(date.substring(4, 6))).intValue()-1);
                    int day = (new Integer(date.substring(6, 8))).intValue();
                    int hour = (new Integer(date.substring(8, 10))).intValue();
                    int min = (new Integer(date.substring(10, 12))).intValue();
                    int sec = (new Integer(date.substring(12, 14))).intValue();

                    Calendar added = Calendar.getInstance();
                    added.set(year, month, day, hour, min, sec);

                    Participator creator = new Participator(creatorID,
                            firstname, lastname, email);

                    //jetzt die georeferenzen holen:
                    ArrayList<GeoShape> references = new ArrayList<GeoShape>();
                    URL referenceURL = new URL(applet.getServletBase()
                            + "references;jsessionid=" + applet.getSessionID() + "?contribution=" + c_id);
                    in = new BufferedReader(new InputStreamReader(referenceURL
                            .openStream()));
                    String referenceString = in.readLine();
                    in.close();

                    if(referenceString!=null){
                    String[] refs = referenceString.split(" /// ");
                    for (int j = 0; j < refs.length; j++) {
                        String[] refdata = refs[j].split(" - ");
                        int locationid = (new Integer(refdata[0])).intValue();
                        int start = (new Integer(refdata[1])).intValue();
                        int end = (new Integer(refdata[2])).intValue();
                        int numPoints = end - start + 1;
                        String type = refdata[3];

                        if (end == 0)
                            numPoints = 1;

                        GeoShape ref;
                        URL coordsURL = new URL(applet.getServletBase()
                                + "coordinates;jsessionid="+ applet.getSessionID() +"?start=" + start + "&numPoints="
                                + numPoints);
                        in = new BufferedReader(new InputStreamReader(
                                coordsURL.openStream()));
                        String coordsString = in.readLine();
                        in.close();

                        String[] coords = coordsString.split(" - ");

                        if (type.equals("GeoPoint")) {
                            double x = (new Double(coords[0])).doubleValue();
                            double y = (new Double(coords[1])).doubleValue();
                            ref = new GeoPoint(locationid, x, y);
                        } else if (type.equals("GeoLine")) {
                            GeoPoint[] punkte = new GeoPoint[numPoints];
                            int v = 0;
                            int k = 0;
                            
                            while (v < coords.length) {
                                double x = (new Double(coords[v]))
                                        .doubleValue();
                                v++;
                                double y = (new Double(coords[v]))
                                        .doubleValue();
                                v++;
                                GeoPoint aktuell = new GeoPoint(x, y);
                                punkte[k] = aktuell;
                                k++;
                            }
                            ref = new GeoLine(locationid, punkte);

                        } else if (type.equals("GeoPolygon")) {

                            GeoPoint[] punkte = new GeoPoint[numPoints];
                            int v = 0;
                            int k = 0;
                            while (v < coords.length) {
                                double x = (new Double(coords[v]))
                                        .doubleValue();
                                v++;
                                double y = (new Double(coords[v]))
                                        .doubleValue();
                                v++;
                                GeoPoint aktuell = new GeoPoint(x, y);
                                punkte[k] = aktuell;
                                k++;
                            }
                            ref = new GeoPolygon(locationid, punkte);
                        } else {
                            ref = new GeoPolygon();
                        }

                        references.add(ref);
                    }
                    }
                    //TODO
                    Contribution childContribution = new Contribution(c_id,
                            child_of, title, msg, creator, references, added,
                            this.applet, contype);
                    resultCons.add(childContribution);
                }
            }
            children = new Contribution[resultCons.size()];
            Iterator iter = resultCons.iterator();
            int i = 0;
            while (iter.hasNext()) {
                Contribution current = (Contribution) iter.next();
                children[i] = current;
                i++;
            }
        } 

        catch (MalformedURLException e) {
            System.out.println("Malformed url exception:");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("IO exception:");
            System.out.println(e.getMessage());
        }

        return children;
    }
    
    public String getType(){
        return type;
    }
}
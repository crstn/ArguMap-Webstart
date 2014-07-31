package uk.ac.leeds.ccg.geotools;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * A concrete implementation of the GeoData interface, it is suitable for most
 * uses and may be extended for more specialist GeoData implementations.
 * 
 * @author James Macgill JM
 */

public class SimpleGeoData implements GeoData, Serializable {
    /**
     * Stores the name of this GeoData, returned by getName.
     */
    private String name = "Unknown";
    private int type = GeoData.FLOATING;
    double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
    Hashtable data;
    double missingValue = -99999;
    int missingCount = 0;

    /**
     * the default constructor for SimpleGeoData
     * 
     * @author James Macgill JM
     */
    public SimpleGeoData() {
        data = new Hashtable();
    }

    /**
     * Constructs a SimpleGeoData by pulling the values from a Hashtable.<br>
     * The hashtable should be built with Integer objects holding ids as keys
     * and Double objects or strings for the values.
     * 
     * @author James Macgill JM
     * @param h
     *            A hashtable containg the id keys and values for this
     *            SimpleGeoData object
     */
    public SimpleGeoData(Hashtable h) {
        data = h;
        doMinMax();
    }

    /**
     * Updates the values for min and max that are returned by getMin and
     * getMax.<br>
     * This method iterates through all of the data stored to find the current
     * Min and Max values.
     * 
     */
    public void doMinMax() {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        missingCount = 0;
        for (Enumeration e = data.elements(); e.hasMoreElements();) {
            double value = ((Double) e.nextElement()).doubleValue();
            if (value != missingValue) {
                min = Math.min(value, min);
                max = Math.max(value, max);
            } else {
                missingCount++;
            }
        }
    }

    /**
     * Not all posible ids will have a value stored in the GeoData object, so
     * when a call is made to getValue with an id that is not stored a special
     * value is returned to signify that a value for this id is missing.<br>
     * By default that value is set to MISSING, however this behavoir can be
     * changed by calling this method with a new value.<br>
     * 
     * @see #getMissingValueCode
     * @author James Macgill JM
     * @param mv
     *            A double containing the new value to represent missing data.
     */
    public void setMissingValueCode(double mv) {
        missingValue = mv;
        doMinMax();
    }

    /**
     * Not all posible ids will have a value stored in the GeoData object, so
     * when a call is made to getValue with an id that is not stored a special
     * value is returned to signify that a value for this id is missing.<br>
     * A call to this method will return the current code in use to represent
     * that situation.<br>
     * 
     * @see #setMissingValueCode
     * @author James Macgill JM
     * @return double The current value representing missing data.
     */
    public double getMissingValueCode() {
        return missingValue;
    }

    /**
     * Stores the id/value pair into this GeoData.<br>
     * If the id specifed already has a value associated with it then the old
     * value is returned.<br>
     * If the id is a new one then the returned value will match the current
     * missingValueCode
     * 
     * @author James Macgill JM
     * @param id
     *            An int holding the id to set the value for.
     * @param value
     *            The value to associate with the specifed id.
     * @return The old value associated with id, or missingValueCode if the id
     *         was previusly not set.
     */
    public double setValue(int id, double value) {
        if (value != missingValue) {
            min = Math.min(value, min);
            max = Math.max(value, max);
        } else {
            missingCount++;
        }
        Double old = (Double) data.put(new Integer(id), new Double(value));

        if (old != null) {
            if (old.doubleValue() == missingValue) {
                missingCount--;
            }
            return old.doubleValue();
        }
        return missingValue;
    }

    /**
     * Returns the number of values stored that equal the current missing value
     * code.
     */
    public int getMissingCount() {
        return missingCount;
    }

    /**
     * Stores the id/text pair into this GeoData.<br>
     * If the id specifed already has text associated with it then the old text
     * is returned.<br>
     * If the id is a new one then the returned String will be null
     * 
     * @author James Macgill JM
     * @param id
     *            An int holding the id to set the value for.
     * @param text
     *            The String to associate with the specifed id.
     * @return The old text associated with id, or null if the id was previusly
     *         not set.
     */
    public String setText(int id, String text) {
        setDataType(GeoData.CHARACTER);
        String old = (String) data.put(new Integer(id), text);
        return old;
    }

    /**
     * All GeoData objects can have a name associated with them.<br>
     * Typicaly the name will match the Column heading from which the data came
     * from.<br>
     * Names can be important when the GeoData is used in thematic maps as this
     * is the name that will be placed in the key by default.<br>
     * 
     * @author James Macgill JM
     * @param name_
     *            The name to be associated with this GeoData.
     */
    public void setName(String name_) {
        name = name_;
    }

    /**
     * All GeoData objects can have a name associated with them.<br>
     * Typicaly the name will match the Column heading from which the data came
     * from.<br>
     * Names can be important when the GeoData is used in thematic maps as this
     * is the name that will be placed in the key by default.<br>
     * 
     * @author James Macgill JM
     * @return String The name associated with this GeoData.
     */
    public String getName() {
        return name;
    }

    /**
     * look up a value for the given id if the GeoData object is completly empty
     * with no values set at all then it bounces the id back to the caller. this
     * is an experimental new behavior for getValue
     */
    public double getValue(int id) {
        if (data.isEmpty()) {
            return id;
        }
        try {
            Double d = (Double) data.get(new Integer(id));
            if (d != null) {
                return d.doubleValue();
            }
        } catch (NumberFormatException e) {
            return missingValue;
        }
        return missingValue;
    }

    /**
     * A quick statistic relating to the values stored in the GeoData object.<br>
     * 
     * @author James Macgill JM
     * @return double The smallest value currently stored in this GeoData. The
     *         missingValueCode is not included in this test.
     */
    public double getMin() {
        return min;
    }

    /**
     * A quick statistic relating to the values stored in the GeoData object.<br>
     * 
     * @author James Macgill JM
     * @return double The largest value currently stored in this GeoData. The
     *         missingValueCode is not included in this test.
     */
    public double getMax() {
        return max;
    }

    /**
     * The total number of stored id/value pairs stored in this GeoData.
     * 
     * @author James Macgill JM
     * @return int The number of values stored in this GeoData.
     */
    public int getSize() {
        return data.size();
    }

    /**
     * Looks up and retreves a string for the specifed feature id.<br>
     * Used for example by the ToolTip feature in Themes to provide tool tip
     * text for each feature.
     * 
     * @author James Macgill JM
     * @param id
     *            An int specifying the feature to retreve the text for.
     * @return String A piece of text for the chosen feature id. If no id
     *         matches then an empty string should be returned " "
     */
    public String getText(int id) {
        Object o = data.get(new Integer(id));
        if (o != null) {
            return o.toString();
        }
        return (" ");// if there is no space this returns a null
    }

    /**
     * In order to allow systems to iterate through all of the data contained
     * within the GeoData object this method provides a list of all of the IDs
     * which have associated values stored.
     * 
     * @author James Macgill JM
     * @return Enumeration An enumeration of all of the IDs which can then be
     *         iterated through.
     */
    public Enumeration getIds() {
        return data.keys();
    }

    /**
     * Returns the name of the GeoData object. This can be handy when you want
     * to include GeoData objets in lists.
     * 
     * @return Strin the name of this GeoData
     */
    public String toString() {
        return name;
    }

    public int getDataType() {
        return type;
    }

    public void setDataType(int t) {
        type = t;
    }

    public void clear() {
        data = new Hashtable();
        doMinMax();
    }
}

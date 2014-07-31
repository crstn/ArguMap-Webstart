package uk.ac.leeds.ccg.geotools;

import java.util.Enumeration;

/**
 * GeoData objects are used in a large number of places throught GeoTools.<br>
 * The role of a GeoData object is to associate id values with data values.<br>
 * Examples of use include matching feature ids to data values for thematic mapping,
 * Storing tool tip texts for displaying with features and for providing data to be
 * graphed or otherwise ploted.<p>
 *
 * GeoData objects can store both text and numeric data, this was done to provide a single
 * interface to columns of data.  getText on a numeric GeoData will correctly return a string
 * of that value, getValue on a text geoData however will fail.<p>
 *
 * In theory, as GeoData is quite a simple interface it should be posible to implement 
 * classes that link back to databases through JDBC for example.<p>
 *
 * The ids stored in the GeoData must match those found in the features to which the 
 * data relates, therefor any class that provides for loading spatial data with associated
 * data should provide a method to retreve pre-built GeoData objects.<p>
 *
 * For example, the ShapeFileReader class provides the following:<br>
 * <code>public GeoData readData(int col) - Provides a GeoData for specified column number<br>
 * public GeoData readData(String colName) - As above, but looks for the column by name.
 * GeoData[] readData() - Provides an array of GeoDatas for the entrire shapefile
 * </code>
 *
 * @see Theme#setGeoData
 *
 * @author James Macgill
 * @since 0.5.0
 */
public interface GeoData{
    /**
     * Most geodata sets contain features for which there is no data, or the data is missing.<br>
     * In these cases a specific value is often used to represent these special cases.<p>
     * The static final value MISSING is the default value used by GeoDatas to represent
     * these cases.
     * @see #setMissingValueCode
     * @see #getMissingValueCode
     */
    public static final double MISSING = Double.NaN;

		/** 
		 * All geodata have a type - this is particularly important when
		 * interfacing with other data sources/formats.
		 * @see #setDataType
		 * @see #getDataType
		 */
		public static final int CHARACTER = 0;
		public static final int INTEGER = 1;
		public static final int FLOATING = 2;
    
    /**
     * All GeoData objects can have a name associated with them.<br>
     * Typicaly the name will match the Column heading from which the data came from.<br>
     * Names can be important when the GeoData is used in thematic maps as this is the
     * name that will be placed in the key by default.<br>
     * @author James Macgill JM
     * @return String The name associated with this GeoData.
     */
    String getName();
    /**
     * All GeoData objects can have a name associated with them.<br>
     * Typicaly the name will match the Column heading from which the data came from.<br>
     * Names can be important when the GeoData is used in thematic maps as this is the
     * name that will be placed in the key by default.<br>
     *
     * @author James Macgill JM
     * @param name_ The name to be associated with this GeoData.
     */
    void setName(String name_);
    
   
    /**
     * looks up and matches a value to the specifed feature id.<br>
     * Used for example by shaders to obtain vaules for thematic mapping.<br>
     *
     * @author James Macgill JM
     * @param id An int specifying the feature id to retreve a value for.
     * @return double The value for the specified id, if no id matches the one given then the value specifed by setMissingValue should be returned.
     * @see #setMissingValue
     */
    double getValue(int id);
    
    /**
     * Looks up and retreves a string for the specifed feature id.<br>
     * Used for example by the ToolTip feature in Themes to provide tool tip text for each feature.
     *
     * @author James Macgill JM
     * @param id An int specifying the feature to retreve the text for.
     * @return String A piece of text for the chosen feature id.  If no id matches then an empty string should be returned " "
     */
    String getText(int id);
    
    /**
     * In order to allow systems to iterate through all of the data contained within the GeoData object this
     * method provides a list of all of the IDs which have associated values stored.
     *
     * @author James Macgill JM
     * @return Enumeration An enumeration of all of the IDs which can then be iterated through.
     */
    Enumeration getIds();
    
    /**
     * Not all posible ids will have a value stored in the GeoData object, so when a call is made to getValue with an id that is
     * not stored a special value is returned to signify that a value for this id is missing.<br>
     * By default that value is set to MISSING, however this behavoir can be changed by calling this method with a new value.<br>
     * 
     * @see #getMissingValueCode
     * @author James Macgill JM
     * @param mv A double containing the new value to represent missing data.
     */
    public void setMissingValueCode(double mv);
    
    /**
     * Not all posible ids will have a value stored in the GeoData object, so when a call is made to getValue with an id that is
     * not stored a special value is returned to signify that a value for this id is missing.<br>
     * A call to this method will return the current code in use to represent that situation.<br>
     *
     * @see #setMissingValueCode
     * @author James Macgill JM
     * @return double The current value representing missing data.
     */    
    public double getMissingValueCode();
   
    /**
     * A quick statistic relating to the values stored in the GeoData object.<br>
     * 
     * @author James Macgill JM
     * @return double The largest value currently stored in this GeoData.  The missingValueCode is not included in this test.
     */
    double getMax();
    
    /**
     * A quick statistic relating to the values stored in the GeoData object.<br>
     * 
     * @author James Macgill JM
     * @return double The smallest value currently stored in this GeoData.  The missingValueCode is not included in this test.
     */
    double getMin();
    
    /**
     * The total number of stored id/value pairs stored in this GeoData.
     * @author James Macgill JM
     * @return int The number of values stored in this GeoData.
     */
    int getSize();
    
    /**
     * The total number of stored values stored in this GeoData which equal the missing value code.
     * @author James Macgill JM
     * @return int The number of missing values stored in this GeoData.
     */
    int getMissingCount();

		/** 
		 * Gets the type of data stored in the geodata<br>
		 * <ul><li>String - GeoData.CHARACTER</li> 
		 * <li>Integer - GeoData.INTEGER</li>
		 * <li>Double - GeoData.FLOATING</li></ul>
		 */
		 int getDataType();
		/** 
		 * Sets the type of data stored in the geodata<br>
		 * <ul><li>String - GeoData.character</li> 
		 * <li>Integer - GeoData.integer</li>
		 * <li>Double - GeoData.float</li></ul>
		 */
		 void setDataType(int type );
}

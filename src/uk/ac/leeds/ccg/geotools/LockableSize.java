package uk.ac.leeds.ccg.geotools;
/* some layers contain shapes that do not change size with scale.
 * circle layers for example can have the radius of each circle locked to one mesured in pixels
 * rather than geographic coordinates.<br>
 * For such layers, the getID test needs to have an additional scale peramiter included.<br>
 * Any layer which implements LockableSize will have this getID test called insted of the normal one.
 */
 
public interface LockableSize
{
    public int getID(GeoPoint p, Scaler s);
}
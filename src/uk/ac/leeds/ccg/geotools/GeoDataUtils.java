/*
 * GeoDataUtils.java
 *
 * Created on March 28, 2001, 3:11 PM
 */

package uk.ac.leeds.ccg.geotools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author  jamesm
 * @version 
 */
public class GeoDataUtils {

    /** Creates new GeoDataUtils */
    public GeoDataUtils() {
    }

    public static ArrayList sort(GeoData target) {
        class doubleComp implements Comparator{
            public int compare(Object a,Object b){
		if(((Double)a).doubleValue()<((Double)b).doubleValue())return -1;
		if(((Double)a).doubleValue()>((Double)b).doubleValue())return 1;
		return 0;
            }
        }
        ArrayList list = new ArrayList();
	java.util.Enumeration ids = target.getIds();
	while(ids.hasMoreElements()){
	    list.add(new Double(target.getValue(((Integer)ids.nextElement()).intValue())));
	}
	Collections.sort(list,new doubleComp());
	return list;
    }
    
}

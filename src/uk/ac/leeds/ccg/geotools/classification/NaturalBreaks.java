/*
 * NaturalBreaks.java
 *
 * Created on 02 November 2001, 19:08
 */

package uk.ac.leeds.ccg.geotools.classification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.misc.FormatedString;
/**
 *
 * @author  James Macgill
 * @version
 */
public class NaturalBreaks extends uk.ac.leeds.ccg.geotools.classification.SimpleClassifier implements uk.ac.leeds.ccg.geotools.classification.Classifier {
    
    /** Creates new NaturalBreaks */
    public NaturalBreaks(GeoData source,int binCount) {
        buildBins(source,binCount);
    }
    
    public void buildBins(GeoData data,int cat){
        int items = data.getSize();
        int span = (int) (Math.ceil((double) items / (double) cat));
        double min = data.getMin();
        double max = data.getMax();
        double next;
        double last = min;
        
        ArrayList list = new ArrayList();
        java.util.Enumeration ids = data.getIds();
        while (ids.hasMoreElements()) {
            list.add(new Double(data.getValue(((Integer)
            ids.nextElement()).intValue())));
        }
        Collections.sort(list, new doubleComp());
        int index = span;
        
        
        int[] kclass = getJenksBreaks(list, cat); //Jenks ã?®æœ€é?©åŒ­
        
        
        last = ((Double) list.get(0)).doubleValue();
        
        
        int oldActive = 0;
        
        Bin bin;
        
        
        for (int i = 1; i <= cat; i++) {
            int active;
            if(i==cat){
                active = kclass[cat - 1];
                System.out.println("And everything that is left");
                next = ((Double)list.get(active)).doubleValue();
                System.out.println("val "+(i*span)+":"+last+":"+next);
                //value = new RangeNumber(last,next);
                //value.includeMax=true;
                bin = new Bin(last,next+0.0000001);
            }
            else{
                active = kclass[i - 1];
                next = ((Double)list.get(active)).doubleValue();
                System.out.println("val "+(i*span)+":"+last+":"+next);
                bin = new Bin(last,next);
            }
            String name = FormatedString.format(""+last,2)+" - "+FormatedString.format(""+next);
            
            
            last = next;
            //RangeItem k = new RangeItem(value,color.getColor(i),name);
            bins.add(bin);
            //keys.addElement(k);
        }
        
        
        
        
        
        
        
    }
    
    
    /**
     * @return int[]
     * @param list com.sun.java.util.collections.ArrayList
     * @param numclass int
     */
    public int[] getJenksBreaks(ArrayList list, int numclass) {
        
        
        //int numclass;
        int numdata = list.size();
        
        
        double[][] mat1 = new double[numdata + 1][numclass + 1];
        double[][] mat2 = new double[numdata + 1][numclass + 1];
        double[] st = new double[numdata];
        
        
        for (int i = 1; i <= numclass; i++) {
            mat1[1][i] = 1;
            mat2[1][i] = 0;
            for (int j = 2; j <= numdata; j++)
                mat2[j][i] = Double.MAX_VALUE;
        }
        double v = 0;
        for (int l = 2; l <= numdata; l++) {
            double s1 = 0;
            double s2 = 0;
            double w = 0;
            for (int m = 1; m <= l; m++) {
                int i3 = l - m + 1;
   /*
   s2 += st[i3 - 1] * st[i3 - 1];
   s1 += st[i3 - 1];
    */
                double val = ((Double)list.get(i3 -1)).doubleValue();
                
                
                s2 += val * val;
                s1 += val;
                
                
                w++;
                v = s2 - (s1 * s1) / w;
                int i4 = i3 - 1;
                if (i4 != 0) {
                    for (int j = 2; j <= numclass; j++) {
                        if (mat2[l][j] >= (v + mat2[i4][j - 1])) {
                            mat1[l][j] = i3;
                            mat2[l][j] = v + mat2[i4][j - 1];
                            
                            
      /*
      if((l == 668) && (j == 2))
        System.out.println(mat1[l][j]);
       */
                        };
                    };
                };
            };
            mat1[l][1] = 1;
            mat2[l][1] = v;
        };
        int k = numdata;
        
        
        int[] kclass = new int[numclass];
        
        
        kclass[numclass - 1] = list.size() - 1;
        
        
        for (int j = numclass; j >= 2; j--) {
            System.out.println("rank = " + mat1[k][j]);
            int id =  (int) (mat1[k][j]) - 2;
            System.out.println("val = " + list.get(id));
            //System.out.println(mat2[k][j]);
            
            
            kclass[j - 2] = id;
            
            
            k = (int) mat1[k][j] - 1;
            
            
        };
        return kclass;
    }
    
    class doubleComp implements Comparator {
    public int compare(Object a, Object b) {
     if (((Double) a).doubleValue() < ((Double) b).doubleValue())
      return -1;
     if (((Double) a).doubleValue() > ((Double) b).doubleValue())
      return 1;
     return 0;
    }
    }

    
}

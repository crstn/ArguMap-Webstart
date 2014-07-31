package uk.ac.leeds.ccg.geotools;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ContiguityMatrix implements java.io.Serializable
{
   HashMap items = new HashMap();
   
   int maxN=0;
   
   public void addList(IDReferenced id,HashSet list){   
    items.put(new Double(id.getID()),list);
    maxN = Math.max(maxN,list.size());
   }
   
   public int getMaxN(){
    return maxN;
   }
   
   public HashSet getList(Double id){
       return (HashSet)items.get(id);
   }
   
   public HashSet getList(IDReferenced id){
    return (HashSet)items.get(new Double(id.getID()));
    
   }
   
   public Set getIDs(){
       return items.keySet();
   }
   
   public int getSize(){
       return items.size();
   }
   
   public String toString(){
    //warning may produce VERY long output
    Collection c = items.values();
    //c is now a collection of hashsets...
    
    return "N/A";
   }
   
   public void save(java.io.PrintWriter out){
       Set keys = items.keySet();
       Iterator i = keys.iterator();
       while(i.hasNext()){
           Double key = (Double)i.next();
           HashSet list = (HashSet)items.get(key);
           Iterator j = list.iterator();
           out.println(""+(int)key.doubleValue());
           while(j.hasNext()){
                IDReferenced feature = (IDReferenced)j.next();
                out.print(" "+feature.getID());
           }
           out.println();
       }
       out.flush();
       out.close();
   }
   
   public void load(PolygonLayer layer,java.io.BufferedReader in) throws java.io.IOException{
       String idCode = in.readLine();
       while(idCode !=null){
            HashSet list = new HashSet();
            String ids = in.readLine();
            java.util.StringTokenizer st = new java.util.StringTokenizer(ids);
            while(st.hasMoreTokens()){
                int id = Integer.parseInt(st.nextToken());
                list.add(layer.getGeoShape(id));
            }
            items.put(new Double(idCode),list);
            idCode = in.readLine();
       }
       layer.setContiguityMatrix(this);
   }
            
       
}
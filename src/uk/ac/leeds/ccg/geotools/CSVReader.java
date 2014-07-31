package uk.ac.leeds.ccg.geotools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * This class provides methods for reading data from CVS files and produces GeoData objects from their contents.
 */
 
 
public class CSVReader
{
   /**
    * Reads csv data from an input stream and returns an array of GeoData objects, one for each column in the file.<p>
    * In order to parse the file correctly it needs to know whether the file has IDs (which must be in the first column if 
    * present) and if it has column names (which must be in the first row if present)
    *
    *
    * @author James Macgill
    * @since 0.7.9
    * @version 0.1
    * @param in An InputStream to pull the data from.
    * @param hasIDs true if the first column has the id for each item, false if the file has no ids at all.
    * @param hasNames true if the first row of the file contains column names.
    */
   public static synchronized GeoData[] getData(InputStream in, boolean hasIDs, boolean hasNames) throws IOException{
    BufferedReader buff = new BufferedReader(new InputStreamReader(in));
    
    String line = buff.readLine();


    if(line==null)return new GeoData[0];//no data at all in this file!
    
    StringTokenizer st = new StringTokenizer(line,",");

    int id = 0;
    int cols = st.countTokens();
    
    SimpleGeoData[] data = new SimpleGeoData[cols];
    
    if(hasNames){
        for(int i=0;i<cols;i++){
            data[i] = new SimpleGeoData();
            data[i].setName(st.nextToken());
        }
        line = buff.readLine();
        if(line==null)return new GeoData[0];//no data at all in this file!
        st = new StringTokenizer(line,",");        
    }
    else{
        for(int i=0;i<cols;i++){
            data[i] = new SimpleGeoData();
            data[i].setName("Column "+i);
        }
    }
    
    //now read the data
    while(line!=null){
       //System.out.println("ID is now "+id);
      
        for(int i=0;i<cols;i++){
            String tok = st.nextToken();
            if(tok.startsWith("\"") && !tok.trim().endsWith("\"")){
               boolean quoteClosed=false;
               while(!quoteClosed){
                  tok +=","+st.nextToken();  
                  if(tok.trim().endsWith("\"")){quoteClosed=true;}
               }
            }
            if(i==0){
                if(hasIDs){
                    id = (int)(new Double(tok).doubleValue());
                }
                else{
                    id++;
                }
            }
          //  System.out.println("id is "+id);
            try{
                Double value = new Double(tok);
                //System.out.println("Adding data value "+value);
                data[i].setValue(id,value.doubleValue());
            }
            catch(NumberFormatException nfe){
                data[i].setText(id, tok);
            }
        }
        //read the next line
        line = buff.readLine();
        if(line==null)break;
        st = new StringTokenizer(line,",");
        
    }
    System.out.println("Done");    
        
    return data;
   }
   
   
   public static GeoData[] getData(URL url,boolean hasIDs, boolean hasNames) throws IOException{
        return getData(url.openConnection().getInputStream(),hasIDs,hasNames);
   }
    
}
/*
 * @(#)GeoDataStream.java  0.5 17 April 1997  James Macgill
 *
 */

package uk.ac.leeds.ccg.geotools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Hashtable;

/**
* An extention of the DataInputStream that handels
* the Input of Geo Primatives
* This version extracts polygon maps from Arc/Info Generate files.
* latter vesrions may deal with higher format files.
*
* @version 0.50 17 April 1997
* @author James Macgill
*/

public class ArcInfoReader extends BufferedReader {

    /**
     * switch
     */

    boolean ignoreIDs = false;
    int sequentialID = 0;

    /**
     * Set up new GeoDataStream
     * @param in Input stream to take data from
     */
    public ArcInfoReader(Reader in){
        super(in);
    }


    /**
     * sets the ignoreIDs option
     *
     * many of the file types read by this class come with an ID code for each entry
     * you may under some circumstances want to ignore this and use a
     * sequential one instead, if so use this method and set flag to
     * true, note that by default, if you do not call this method the flag will be
     * set to false, i.e. ids will be read from file.
     * NB, incompeat, only affects some read methods, contact author if required for others.
     *
     * @param flag boolean switch, set to true to ignore ids stored in files
     */
    public void setIgnoreIDs(boolean flag){
        ignoreIDs = flag;
    }

    /**
     * Reads a GeoPolygon
     * If the ignoreIDs flag is set to true sequential ids will be used.
     * @return The GeoPolygon read
     */
    public final synchronized GeoPolygon readGeoPolygon() throws IOException {
        /**
         * The empty GeoPolygon
         */
        GeoPolygon poly=null;

        /**
         * Polygon ID
         */
        int id = 0;

        /**
         * The polygons Centroid
         */
		float xcent = 0;
		float ycent = 0;

		/**
		 * Temp storage for each point as loaded
		 */
		float x,y;

		/**
		 * String to hold the 'END' of Polygon and File
		 * Markers found in the Ungenerate file format
		 */
		String marker;

		/**
		 * Holds one line of the Ungenerate file
		 */
		String line;

			//Read first line
			line = readLine();
			marker = line.substring(0,3);
			if(marker.equals("END")) return null; //Finished i.e. end of file

            //Read polygon header
			try {
				id = Integer.parseInt(line.substring(4,10).trim());

				if(id !=-99999){ //Closed Polygon
				    sequentialID++;
				    if(ignoreIDs)
				        id = sequentialID;
					//System.out.println("ID was "+id);
					xcent = new Float(line.substring(11,28).trim()).floatValue();
					ycent = new Float(line.substring(29,44).trim()).floatValue();
					//poly = new GeoPolygon(id,xcent,ycent);
				}
				else{
				    id = -99999;
				    xcent=0;ycent=0;
					//poly = new GeoPolygon(-99999,0,0);
					System.out.println("!Null Polygon!");
				}
				
				
		
		        double xtemp[],xpoints[];
			    double ytemp[],ypoints[];
			    xpoints = new double[500];
			    ypoints = new double[500]; 
			    int npoints = -1;//inc'd to 0 before first use...
			    int limit = 500,grow = 20;
				while(true) {
					//Read co-ordinates
					line = readLine();
					if(line.substring(0,3).trim().equals("END")) break;//
					//Above line made from ...
					//marker = line.substring(0,3).trim();
					//if(marker.equals("END")) break; //End of polygon
					npoints++;
					if(npoints>=limit){//expand array to fit
					    xtemp = xpoints;
			            ytemp = ypoints;
			            xpoints = new double[limit+grow];
			            ypoints = new double[limit+grow];
			            limit+=grow;
			            System.arraycopy(xtemp,0,xpoints,0,npoints);
			            System.arraycopy(ytemp,0,ypoints,0,npoints);
			        }
					x = new Float(line.substring(4,18).trim()).floatValue();
					y = new Float(line.substring(19,line.length()).trim()).floatValue();
					xpoints[npoints]=x;
					ypoints[npoints]=y;
					
				}
				if(npoints<limit){//pack array down to size
					    xtemp = xpoints;
			            ytemp = ypoints;
			            xpoints = new double[npoints];
			            ypoints = new double[npoints];
			            limit+=grow;
			            System.arraycopy(xtemp,0,xpoints,0,npoints);
			            System.arraycopy(ytemp,0,ypoints,0,npoints);
			        }
		    return new GeoPolygon(id,xcent,ycent,xpoints,ypoints,npoints);
		    }
		    catch(NumberFormatException e) {
		        System.err.println("Invalid Polygon ID");
		        return null;
		        // Should throw a PolygonFormatException
		        // When I work out how!
		    }
    }

    /**
     * Reads all of the data from the InputStream and produces
     * a PolygonLayer
     *
     * @return The assembled PolygonLayer
     */
    public final synchronized PolygonLayer readUngenerateFile() throws IOException {
        PolygonLayer map = new PolygonLayer();
        GeoPolygon poly;
        //System.out.println("Reading Map Data");
        while(true) {

            poly = readGeoPolygon();
            if (poly == null) break;
            //System.out.println("Adding Polygon");
            map.addGeoPolygon(poly);
            //System.out.println("Bounds "+map.getBounds());
        }
        //System.out.println("Read "+sequentialID+" polys");
        return map;
        //Need to add 'InvalidPolygon throwing
    }

    /**
     * Reads an attribute file of the form
     * id,value
     * NB id's <= 0 are skiped
     * If this is a problem a switch will be added
     */
    public final synchronized Hashtable readAttributes() throws IOException{
              //Hashtable t = new Table();
              Hashtable col = new Hashtable();
	          StreamTokenizer st = new StreamTokenizer(this);
              st.eolIsSignificant(false);
              st.whitespaceChars(',',',');
              boolean done = false;

              while (!done) {
                int c = StreamTokenizer.TT_EOF;
                   c = st.nextToken();
                switch (c) {
                  case StreamTokenizer.TT_EOF:
                      done = true;
                      break;
                  case StreamTokenizer.TT_NUMBER:
                      int id = (int)st.nval;
                      c = st.nextToken();
                      double value = (int)st.nval;
                      if(id > 0){
                        col.put(new Integer(id),new Double(value));
                      }
                      break;
                }
	          }

	          return col;
	}

    /**
     * Reads an attribute file of the form
     * id,value
     * NB id's <= 0 are skiped
     * If this is a problem a switch will be added
     */
    public final synchronized GeoData readGeoData() throws IOException{
              //Hashtable t = new Table();
              //Vector store = new Vector();
	          SimpleGeoData store = new SimpleGeoData();
	          StreamTokenizer st = new StreamTokenizer(this);
              st.eolIsSignificant(false);
              st.whitespaceChars(',',',');
              boolean done = false;

              while (!done) {
                int c = StreamTokenizer.TT_EOF;
                   c = st.nextToken();
                switch (c) {
                  case StreamTokenizer.TT_EOF:
                      done = true;
                      break;
                  case StreamTokenizer.TT_NUMBER:
                      int id = (int)st.nval;
                      c = st.nextToken();
                      if(c==StreamTokenizer.TT_NUMBER){
                        double value = (int)st.nval;
                        store.setValue(id,value);
                      }
                      else{
                        String text = st.sval;
                        store.setText(id,text);
                      }
                      
                      break;
                }
	          }
              
	          return store;
	}


    /**
     * Reads a circle file of the form
     * id,x,y,r
     * NB id's <= 0 are skiped
     * If this is a problem a switch will be added
     */
    public final synchronized CircleLayer readCircles() throws IOException{
              //Hashtable t = new Table();
              CircleLayer cl = new CircleLayer();
              double x,y,r;
              int id;
	          StreamTokenizer st = new StreamTokenizer(this);
              st.eolIsSignificant(false);
              st.whitespaceChars(',',',');
              boolean done = false;

              while (!done) {
                int c = StreamTokenizer.TT_EOF;
                   c = st.nextToken();
                switch (c) {
                  case StreamTokenizer.TT_EOF:
                      done = true;
                      break;
                  case StreamTokenizer.TT_NUMBER:
                      id = (int)st.nval;
                      st.nextToken();
                      x = (double)st.nval;
                      st.nextToken();
                      y = (double)st.nval;
                      st.nextToken();
                      r = (double)st.nval;
                      if(id > 0){
                        cl.addGeoCircle(new GeoCircle(id,x,y,r));
                      }
                      break;
                }
	          }

	          return cl;
	}

	/**
     * Reads an animation file of the form
     * first line - number of zones per frame
     * value for each ID, sequentialy
     * NB id's <= 0 are skiped ???
     * If this is a problem a switch will be added
     */
    public final synchronized Table readZdes() throws IOException{
              Table t = new Table();
              double value;

	          StreamTokenizer st = new StreamTokenizer(this);
              st.eolIsSignificant(false);
              st.whitespaceChars(',',',');
              boolean done = false;
              st.nextToken();
              int zones = (int)st.nval;
              int j = 1;
              Hashtable col;
              while (!done) {
                j++;
                col = new Hashtable();
                st.nextToken();//skip outer polygon...
                for(int i = 1;i <zones && !done;i++){
                  int c = StreamTokenizer.TT_EOF;
                    c = st.nextToken();
                  switch (c) {
                    case StreamTokenizer.TT_EOF:
                      done = true;
                      break;
                    case StreamTokenizer.TT_NUMBER:
                      //c = st.nextToken();
                      value = st.nval;
                     //System.out.print(""+value+",");
                      col.put(new Integer(i),new Double(value));
                      break;
                  }

                }//for
                t.addCol(""+j+" zones",col);
               // System.out.println("adding "+j+" zones file");
	          }

	          return t;
	}



    public final synchronized PolygonLayer readUngenerateFile2() throws IOException {
        /**
         * Polygon ID
         */
        int id = 0;

        /**
         * The polygons Centroid
         */
		float xcent = 0;
		float ycent = 0;

		/**
		 * Temp storage for each point as loaded
		 */
		float x,y;
		double point[] = {0,0};

		int pair = 1;

		double header[] = {0,0,0};
		int headerSegment = 0;

        PolygonLayer map = new PolygonLayer();
        GeoPolygon poly = null;
        //System.out.println("Reading Map Data (Tokenized Method)");
        StreamTokenizer st = new StreamTokenizer(this);
        st.eolIsSignificant(true);
        boolean done = false;
        boolean readingHeader = true; //Polygon centroid header/Point data switch

        while (!done) {
            int c = StreamTokenizer.TT_EOF;
            try {
                c = st.nextToken();
            } catch (IOException e) {break; } //Out of while loop
            switch (c) {
                case StreamTokenizer.TT_EOF:
                    done = true;
                    break;
                case StreamTokenizer.TT_EOL:
                    if(readingHeader){
                        poly = new GeoPolygon(-99999,0,0);
                        readingHeader=false;
                        break;
                    }
                    else{
                        poly.addPoint(point[0],point[1]);
                        break;
                    }
                case StreamTokenizer.TT_WORD:
                    if(readingHeader){
                        done = true;
                        break;
                    }
                    readingHeader = true;
                    headerSegment = 0;
                    map.addGeoPolygon(poly);
                    break;
                case StreamTokenizer.TT_NUMBER:
                    if(readingHeader){
                        header[headerSegment] = st.nval;
                        headerSegment++;
                        if (headerSegment > 2){
                            poly = new GeoPolygon((int)header[0],(float)header[1],(float)header[2]);
                            readingHeader = false;
                        }
                    }
                    else
                    {
                        point[pair-1] = st.nval;
                        pair = 3-pair; //alternates 1-2-1-2-1-2-1....
                    }
                    break;
            }
        }
        return map;
    }
}
package uk.ac.leeds.ccg.geotools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

/** A reader class for ARC/INFO ungenerate files. Ungenerate files are
 * ascii text files that are exported from arc/info. They have a very basic
 * structure but for some reason remain popular with some data suppliers.
 * @author Ian Turton <a mailto:ian@geog.leeds.ac.uk>ian@geog.leeds.ac.uk</a>
 */

public class UngenerateReader{

	StreamTokenizer tok;
	int seqid = 0;
	public UngenerateReader(InputStream is){
		tok = new StreamTokenizer(new BufferedReader(new InputStreamReader(is)));
		tok.parseNumbers();
		//tok.eolIsSignificant(true);
		tok.whitespaceChars((int)',',(int)',');
	}

	public PolygonLayer readPolygons(){
		PolygonLayer map = new PolygonLayer();
		GeoPolygon poly;	
		try{
			while((poly = readGeoPolygon())!=null){
				//System.out.println(""+poly.getID());
				map.addGeoPolygon(poly);
			}
		}catch(IOException e){
			System.err.println("File read error in UngenerateReader"+e);
			return null;
		}
		return map;
	}

	GeoPolygon readGeoPolygon() throws IOException{
		double x,y;
		int id;
		int ret = tok.nextToken();
		if(ret==tok.TT_WORD||ret==tok.TT_EOF){
			if(tok.sval.equalsIgnoreCase("end")) return null;
		}
		if(ret==tok.TT_NUMBER){
			id = (int)tok.nval;
			if(id==0||id==-99999)id=seqid++;

			ret=tok.nextToken();
			if(ret==tok.TT_NUMBER){
				x = tok.nval;
				ret=tok.nextToken();
				if(ret==tok.TT_NUMBER){
					y = tok.nval;
				}else return null;
			}else return null;
		}else return null;
		
		GeoPolygon p = new GeoPolygon(id,x,y);
		while(true){
			ret=tok.nextToken();
			if(ret==tok.TT_WORD||ret==tok.TT_EOF){
				if(tok.sval.equalsIgnoreCase("end")) return p;
				else return null;
			}
			if(ret==tok.TT_NUMBER){
				x = tok.nval;
				ret=tok.nextToken();
				if(ret==tok.TT_NUMBER){
					y = tok.nval;
				}else return null;
			}else return null;
			p.addPoint(x,y);
		}
	}
}

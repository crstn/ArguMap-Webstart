package uk.ac.leeds.ccg.dbffile;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.SimpleGeoData;
import uk.ac.leeds.ccg.geotools.misc.FormatedString;
import cmp.LEDataStream.LEDataOutputStream;

/** a class for writing dbf files
 * @author Ian Turton
 */

public class DbfFileWriter implements DbfConsts{

	private final static boolean DEBUG=false;
	private final static String DBC="DbFW>";
  int NoFields =1;
  int NoRecs = 0;
  int recLength = 0;
  DbfFieldDef fields[];
  LEDataOutputStream ls;
	private boolean header = false;
  public DbfFileWriter(String file) throws IOException{
	if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.dbffile.DbfFileWriter constructed. Will identify itself as "+DBC);
	ls = new LEDataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
  }
  public void writeHeader(DbfFieldDef f[], int nrecs) throws IOException{
    NoFields = f.length;
    NoRecs = nrecs;
    fields = new DbfFieldDef[NoFields];
    for(int i=0;i<NoFields;i++){
      fields[i]=f[i];
    }
    ls.writeByte(3); // ID - dbase III with out memo
    // sort out the date
    Calendar calendar = new GregorianCalendar();
    Date trialTime = new Date();
    calendar.setTime(trialTime);
    ls.writeByte(calendar.get(Calendar.YEAR) - DBF_CENTURY);
    ls.writeByte(calendar.get(Calendar.MONTH));
    ls.writeByte(calendar.get(Calendar.DAY_OF_MONTH));
    int dataOffset = 32 * NoFields+32 + 1;
    for(int i=0;i<NoFields;i++){
      recLength+=fields[i].fieldlen;
    }
    recLength++; // delete flag
    if(DEBUG)System.out.println(DBC+"rec length "+recLength);
    ls.writeInt(NoRecs);
    ls.writeShort(dataOffset); // length of header
    ls.writeShort(recLength);
    for(int i=0;i<20;i++) ls.writeByte(0); // 20 bytes of junk!
    // field descriptions
    for(int i=0;i<NoFields;i++){


      ls.writeBytes(fields[i].fieldname.toString());
      ls.writeByte(fields[i].fieldtype);
      for(int j=0;j<4;j++) ls.writeByte(0); // junk
      ls.writeByte(fields[i].fieldlen);
      ls.writeByte(fields[i].fieldnumdec);
      for(int j=0;j<14;j++) ls.writeByte(0); // more junk
    }
    ls.writeByte(0xd);
  	header=true;  
  }
  public void writeRecords(Vector [] recs) throws DbfFileException,IOException{
		if(!header){
			throw(new DbfFileException("Must write header before records"));
		}
    int i=0;
    try{
			if(DEBUG)System.out.println(DBC+":writeRecords writing "+recs.length+" records");
      for(i=0;i<recs.length;i++){
			if(recs[i].size()!=NoFields) throw new DbfFileException("wrong number of records in "+ i+"th record "+
      recs[i].size()+" expected "+NoFields);
        writeRecord(recs[i]);
      }
    }catch(DbfFileException e){throw new DbfFileException(DBC+"at rec "+i+"\n"+e);}
  }
  public void writeRecord(Vector rec)throws DbfFileException,IOException{
		if(!header){
			throw(new DbfFileException(DBC+"Must write header before records"));
		}
    if(rec.size()!=NoFields) throw new DbfFileException(DBC+"wrong number of fields "+
      rec.size()+" expected "+NoFields);
    String s;
    ls.writeByte(' ');
		int len;
		StringBuffer tmps;
    for(int i=0;i<NoFields;i++){
      len = fields[i].fieldlen;
      Object o = rec.elementAt(i);

      switch(fields[i].fieldtype){
        case 'C':
        case 'c':
        
        case 'L':
        case 'M':
        case 'G':
          //chars
					tmps = new StringBuffer((String)o);
					tmps.setLength(fields[i].fieldlen);
          ls.writeBytes(tmps.toString());
          break;
        case 'N':
        case 'n':
          // int?
          if(fields[i].fieldnumdec==0){ 
						ls.writeBytes(FormatedString.format(((Integer)o).intValue(),fields[i].fieldlen));
						break;
					}
        case 'F':
        case 'f':
          //double
          s = ((Double)o).toString();
					String x = FormatedString.format(s,fields[i].fieldnumdec,fields[i].fieldlen);
          ls.writeBytes(x);
          break;
      }// switch
    }// fields
  }
  public void close() throws IOException{
    ls.writeByte(0x1a); // eof mark
    ls.close();
  }
	int dp = 2; // default number of decimals to write
	public void writeGeoDatas(GeoData[] g) throws DbfFileException,IOException{
		writeGeoDatas(g,false);
	}
	public void writeGeoDatas(GeoData[] g,boolean ids) throws DbfFileException,IOException{
		// build a field def for the geodatas
		GeoData[] gds;
		if(ids){
			// generate new IDS
			SimpleGeoData idx = new SimpleGeoData();
			idx.setName("ID");
			idx.setDataType(GeoData.INTEGER);
			java.util.Enumeration e = g[0].getIds();
			while(e.hasMoreElements()){
				int id=((Integer)e.nextElement()).intValue();
				idx.setValue(id,id);
			}
			gds = new GeoData[g.length+1];
			gds[0]=idx;
			for(int i=0;i<g.length;i++){
				gds[i+1]=g[i];
			}
		}else{
			gds=g;
		}
		DbfFieldDef df[] = new DbfFieldDef[gds.length];
		int width = 0;
		for(int k=0;k<gds.length;k++){
			int type = gds[k].getDataType();
			char ct = ' ';
			width=0;
			if(type==GeoData.INTEGER||type==GeoData.FLOATING){
				// how wide is the dat ****NOT INCLUDING***** the decimal point
				double max = gds[k].getMax();
				if(max<0) max*= -1;
				if(DEBUG)System.out.println(DBC+"max of "+k+" "+gds[k].getMax()+" "+max);
				width =  (int)Math.ceil(Math.log(max)/Math.log(10.0))+1;
				if(gds[k].getMin()<0) width+=1; // the minus sign
			}
			if(type==GeoData.CHARACTER){
				for(int i=0;i<gds[k].getSize();i++){
					width=Math.max(width,gds[k].getText(i).length());
				}
			}
			if(width<0) width=0;
			if(DEBUG)System.out.println(DBC+" "+k+" width "+width+" type->"
				+gds[k].getDataType()+"*");
			
			switch(type){
				case GeoData.INTEGER:
					ct = 'N';
					df[k] = new DbfFieldDef(gds[k].getName().trim(),ct,width,0);
					break;
				case GeoData.CHARACTER:
					ct = 'C';
					df[k] = new DbfFieldDef(gds[k].getName().trim(),ct,width,0);
					break;
				case GeoData.FLOATING:
					ct = 'F';
					df[k] = new DbfFieldDef(gds[k].getName().trim(),ct,width+dp+1,dp);
					break;
				default:
					throw new DbfFileException("No data type set in geodata "+gds[k].getName());
			}
			if(DEBUG)System.out.println(DBC+df[k]);
		}
		//width =  (int)Math.ceil(Math.log(gds[0].getSize())/Math.log(10.0));
		//System.out.println(DBC+" id width "+width);
		//df[0] = new DbfFieldDef("ID",'N',width,0);

		// write the header 
		writeHeader(df,gds[0].getSize());


		// write the records
		if(DEBUG)System.out.println(DBC+"writeGeoDatas writing "+gds[0].getSize()
			+" records");
		Integer id=null;
		Vector rec=new Vector(gds.length);
		Enumeration e = gds[0].getIds();
		while(e.hasMoreElements()){
			id=(Integer)e.nextElement();
			//rec.add(0,id);
			for(int k=0;k<gds.length;k++){
				switch(gds[k].getDataType()){
					case GeoData.FLOATING:
						rec.addElement(new Double(gds[k].getValue(id.intValue())));
						break;
					case GeoData.INTEGER:
						rec.addElement(new Integer((int)gds[k].getValue(id.intValue())));
						break;
					case GeoData.CHARACTER:
						rec.addElement(new String(gds[k].getText(id.intValue())));
						break;
				}
			}
			writeRecord(rec);
			rec = new Vector();
		}
		// close the file
		close();
	}
}

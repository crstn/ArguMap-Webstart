package uk.ac.leeds.ccg.dbffile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Vector;

import cmp.LEDataStream.LEDataInputStream;
/**
 *
 * This class represents a DBF (or DBase) file.<p>
 * Construct it with a filename (including the .dbf)
 * this causes the header and field definitions to be read.<p>
 * Later queries return rows or columns of the database.
 *<hr>
 * @author <a href="mailto:ian@geog.leeds.ac.uk">Ian Turton</a> Centre for
 * Computaional Geography, University of Leeds, LS2 9JT, 1998.
 *
 */
public class DbfFile implements DbfConsts{
    static final boolean DEBUG=false;
    int dbf_id;
    int last_update_d,last_update_m,last_update_y;
    int last_rec;
    int data_offset;
    int rec_size;
    boolean hasmemo;
    LEDataInputStream dFile;
    RandomAccessFile rFile;
    int filesize,numfields;
    public DbfFieldDef fielddef[];

		/**
			* Constructor, opens the file and reads the header infomation.
			* @param file The file to be opened, includes path and .dbf
			* @exception java.io.IOException If the file can't be opened.
			* @exception DbfFileException If there is an error reading header.
			*/
    public DbfFile(String file) throws java.io.IOException,DbfFileException{
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.dbffile.DbfFile constructed. Will identify itself as DbFi>");
		InputStream in = new FileInputStream(file);
		LEDataInputStream sfile = new LEDataInputStream(in);
		rFile=new RandomAccessFile(new File(file),"r");
		init(sfile);
    }

		/**
			* Returns the date of the last update of the file as a string.
			*/
    public String getLastUpdate(){
      String date=last_update_d+"/"+last_update_m+"/"+last_update_y;
      return date;
    }

		/**
			* Returns the number of records in the database file.
			*/
    public int getLastRec(){
      return last_rec;
    }
		/**
			* Returns the size of the records in the database file.
			*/
    public int getRecSize(){
      return  rec_size;
    }
		/**
			* Returns the number of fields in the records in the database file.
			*/
    public int getNumFields(){
      return numfields;
    }
		/**
			* Returns the size  of the database file.
			*/
    public int getFileSize(){
      return  filesize;
    }

		/**
		  * initailizer, allows the use of multiple constructers in later
			* versions.
			*/

    private void init(LEDataInputStream sfile)throws
      IOException,DbfFileException {
      DbfFileHeader head = new DbfFileHeader(sfile);
      int widthsofar;

      dFile=sfile;

      fielddef = new DbfFieldDef[numfields];
      widthsofar=1;
      for(int index=0;index<numfields;index++){
        fielddef[index] = new DbfFieldDef();
        fielddef[index].setup(widthsofar,dFile);
        widthsofar+=fielddef[index].fieldlen;
      }
      sfile.skipBytes(1); // end of field defs marker
    }
	/**
		* Internal Class to hold information from the header of the file
		*/
  class DbfFileHeader{


		/**
			* Reads the header of a dbf file.
			* @param LEDataInputStream file Stream attached to the input file
			* @exception IOException read error.
			*/
    public  DbfFileHeader(LEDataInputStream file) throws IOException {
      getDbfFileHeader(file);
    }
    private void getDbfFileHeader(LEDataInputStream file) throws IOException {
        
      int len;
      dbf_id=(int)file.readUnsignedByte();
      if(DEBUG)System.out.print("DbFi>Header id ");
      if(DEBUG)System.out.println(dbf_id);
      if(dbf_id==3) hasmemo=false;
      else hasmemo=true;

      last_update_y=(int)file.readUnsignedByte()+DBF_CENTURY;
      last_update_m=(int)file.readUnsignedByte();
      last_update_d=(int)file.readUnsignedByte();
      if(DEBUG)System.out.print("DbFi>last update ");
      if(DEBUG)System.out.print(last_update_d);
      if(DEBUG)System.out.print("/");
      if(DEBUG)System.out.print(last_update_m);
      if(DEBUG)System.out.print("/");
      if(DEBUG)System.out.println(last_update_y);

      last_rec=file.readInt();
      if(DEBUG)System.out.print("DbFi>last rec ");
      if(DEBUG)System.out.println(last_rec);

      data_offset=file.readShort();
			//data_offset=0;
			//System.out.println("x = "+file.readUnsignedByte()+" " +
				//file.readUnsignedByte());
      if(DEBUG)System.out.print("DbFi>data offset ");
      if(DEBUG)System.out.println(data_offset);

      rec_size=file.readShort();
      if(DEBUG)System.out.print("DbFi>rec_size ");
      if(DEBUG)System.out.println(rec_size);

      filesize=(rec_size * last_rec) + data_offset+1;
      numfields = (data_offset - DBF_BUFFSIZE -1)/DBF_BUFFSIZE;

      if(DEBUG)System.out.print("DbFi>num fields ");
      if(DEBUG)System.out.println(numfields);
      if(DEBUG)System.out.print("DbFi>file size ");
      if(DEBUG)System.out.println(filesize);
      file.skipBytes(20);
    }

  }

	/**
		* gets the next record and returns it as a string. This method works on
		* a sequential stream and can not go backwards. Only useful if you want
		* to read the whole file in one.
		* @exception java.io.IOException on read error.
		*/
  public StringBuffer GetNextDbfRec()throws java.io.IOException{
    StringBuffer record = new StringBuffer(rec_size+numfields);

    for(int i=0;i< rec_size;i++){
    // we could do some checking here.
      record.append((char)rFile.readUnsignedByte());
    }
  return record;
  }
  /**
   * fetches the <i>row</i>th row of the file
   * @param row - the row to fetch
   * @exception java.io.IOException on read error.
   */
  public StringBuffer GetDbfRec(int row)throws java.io.IOException{
      StringBuffer record = new StringBuffer(rec_size+numfields);
      
      rFile.seek(data_offset+(rec_size*row));
      //Multi byte character modification thanks to Hisaji ONO 
      byte[] strbuf = new byte[rec_size]; // <---- byte array buffer fo storing string's byte data
      for(int i=0;i<rec_size;i++){
          //************************//
          strbuf[i] = dFile.readByte(); // <---- read string's byte data
      }
      record.append(new String(strbuf)); // <- append byte array to String Buffer
      return record;
  }
	/**
		* fetches the <i>row</i>th row of the file and parses it into an vector
		* of objects.
		* @param row - the row to fetch
		* @exception java.io.IOException on read error.
		*/
  public Vector ParseDbfRecord(int row)throws java.io.IOException{
		return ParseRecord(GetDbfRec(row));
	}
	/**
		* Parses the record stored in the StringBuffer rec into a vector of
		* objects
		* @param StringBuffer rec - the record to be parsed.
		*/

  public Vector ParseRecord(StringBuffer rec){
    Vector record=new Vector(numfields);
    String t;
    Integer I=new Integer(0);
    Double F=new Double(0.0);
    t = rec.toString();
    for(int i=0;i<numfields;i++){
      if(DEBUG)System.out.println("DbFi>type "+fielddef[i].fieldtype);
      if(DEBUG)System.out.println("DbFi>start "+fielddef[i].fieldstart);
      if(DEBUG)System.out.println("DbFi>len "+fielddef[i].fieldlen);
      if(DEBUG)System.out.println(t.substring(fielddef[i].fieldstart,
                  fielddef[i].fieldstart+fielddef[i].fieldlen));
      switch(fielddef[i].fieldtype){
        case 'C':
          record.addElement(t.substring(fielddef[i].fieldstart,
            fielddef[i].fieldstart+fielddef[i].fieldlen));
          break;
        case 'N':
					try{
						record.addElement(I.decode(t.substring(
							fielddef[i].fieldstart,fielddef[i].fieldstart+fielddef[i].fieldlen)));
					}catch(java.lang.NumberFormatException e){
						record.addElement(new Integer(0));
					}
          break;
        case 'F':
					try{
						record.addElement(F.valueOf(t.substring(
							fielddef[i].fieldstart,fielddef[i].fieldstart+fielddef[i].fieldlen)));
					}catch(java.lang.NumberFormatException e){
						record.addElement(new Float(0.0));
					}
          break;
        default:
          if(DEBUG)System.out.println("DbFi>Oh - don't know how to parse "
            +fielddef[i].fieldtype);
      }
    }
    return record;
  }

	/**
		* Fetches a column of Integers from the database file.
		* @param int col - the column to fetch
		* @exception java.io.IOException - on read error
		* @exception DbfFileException - column is not an Integer.
		*/
  public Integer[] getIntegerCol(int col )
	throws java.io.IOException,DbfFileException
	{
		return getIntegerCol(col,0,last_rec);
	}
	/**
		* Fetches a part column of Integers from the database file.
		* @param int col - the column to fetch
		* @param int start - the row to start fetching from
		* @param int end - the row to stop fetching at.
		* @exception java.io.IOException - on read error
		* @exception DbfFileException - column is not an Integer.
		*/
  public Integer[] getIntegerCol(int col, int start, int end) 
		throws java.io.IOException,DbfFileException {
    Integer column[]=new Integer[end-start];
    String record = new String();
    StringBuffer sb = new StringBuffer(numfields);
    int k=0,i=0;
    if(col>=numfields)
      throw new DbfFileException("DbFi>No Such Column in file: "+col);
    if(fielddef[col].fieldtype!='N')
      throw new DbfFileException("DbFi>Column "+col+" is not Integer");
    // move to start of data
    try{
      rFile.seek(data_offset+(rec_size*start));
      for(i=start;i<end;i++){
				sb.setLength(0);
        for(k=0;k<rec_size;k++)
          sb.append((char)rFile.readUnsignedByte());
        record=sb.toString();
				try{
					column[i-start]=new Integer(record.substring(fielddef[col].fieldstart,
						fielddef[col].fieldstart+fielddef[col].fieldlen));
				}catch(java.lang.NumberFormatException e){
					column[i-start]=new Integer(0);
				}
      }
    }
    catch(java.io.EOFException e){
      System.err.println("DbFi>"+e);
      System.err.println("DbFi>record "+i+" byte "+k+" file pos "
      +rFile.getFilePointer());}
    catch(java.io.IOException e){
      System.err.println("DbFi>"+e);
      System.err.println("DbFi>record "+i+" byte "+k+" file pos "
      +rFile.getFilePointer());}
    return column;
  }
	/**
		* Fetches a column of Double from the database file.
		* @param int col - the column to fetch
		* @exception java.io.IOException - on read error
		* @exception DbfFileException - column is not an Integer.
		*/
  public Double[] getFloatCol(int col) throws DbfFileException,
  java.io.IOException{
		return getFloatCol(col,0,last_rec);
	}
	/**
		* Fetches a part column of Double from the database file.
		* @param int col - the column to fetch
		* @param int start - the row to start fetching from
		* @param int end - the row to stop fetching at.
		* @exception java.io.IOException - on read error
		* @exception DbfFileException - column is not an Integer.
		*/
  public Double[] getFloatCol(int col,int start,int end)
		throws DbfFileException, java.io.IOException{
    Double column[]=new Double[end-start];
    String record,st;
    StringBuffer sb = new StringBuffer(rec_size);
    int k=0,i=0;
    if(col>=numfields)
      throw new DbfFileException("DbFi>No Such Column in file: "+col);
    if(fielddef[col].fieldtype!='F')
      throw new DbfFileException("DbFi>Column "+col+" is not Double "
      +fielddef[col].fieldtype);
    // move to start of data
    try{
      rFile.seek(data_offset+(rec_size*start));
      
      for(i=start;i<end;i++){
				sb.setLength(0);
				// we should be able to skip to the start here.
        for(k=0;k<rec_size;k++)
          sb.append((char)rFile.readUnsignedByte());
        record=sb.toString();
        st=new String(record.substring(fielddef[col].fieldstart,
          fielddef[col].fieldstart+fielddef[col].fieldlen));
				if(st.indexOf('.')==-1){
					st=st+".0";
				}
				try{
					column[i-start]=new Double(st);
				}catch(java.lang.NumberFormatException e){
					column[i-start]=new Double(0.0);
				}
      }
    }
    catch(java.io.EOFException e){
      System.err.println("DbFi>"+e);
      System.err.println("DbFi>record "+i+" byte "+k+" file pos "
      +rFile.getFilePointer());}
    catch(java.io.IOException e){
      System.err.println("DbFi>"+e);
      System.err.println("DbFi>record "+i+" byte "+k+" file pos "
      +rFile.getFilePointer());}
    return column;
  }
	/**
		* Fetches a column of Strings from the database file.
		* @param int col - the column to fetch
		* @exception java.io.IOException - on read error
		* @exception DbfFileException - column is not an Integer.
		*/
	public String[] getStringCol(int col) throws DbfFileException,
		java.io.IOException{

		return getStringCol(col,0,last_rec);
	}
	/**
		* Fetches a part column of Strings from the database file.
		* @param int col - the column to fetch
		* @param int start - the row to start fetching from
		* @param int end - the row to stop fetching at.
		* @exception java.io.IOException - on read error
		* @exception DbfFileException - column is not an Integer.
		*/
	public String[] getStringCol(int col,int start,int end)
		throws DbfFileException, java.io.IOException {

		String column[]=new String[end-start];
		String record = new String();
		//StringBuffer sb = new StringBuffer(numfields);
		int k=0,i=0;
		if(col>=numfields)
			throw new DbfFileException("DbFi>No Such Column in file: "+col);
		if(fielddef[col].fieldtype!='C')
			throw new DbfFileException("DbFi>Column "+col+" is not a String");

		// move to start of data
		try{
			rFile.seek(data_offset+(start*rec_size));
			for(i=start;i<end;i++){
				//sb.setLength(0);
				//*** initialize buffer for record ***
				byte[] strbuf = new byte[rec_size];
				for(k=0;k<rec_size;k++) {
					strbuf[k] = rFile.readByte(); //*** get byte data
				}
				//sb.append((char)rFile.readUnsignedByte());
				//record=sb.toString();
				//*** convert buffer data to String ***
				record = new String(strbuf);
				//column[i-start]=new String(record.substring(fielddef[col].fieldstart,fielddef[col].fieldstart+fielddef[col].fieldlen));

				//***  Extract string data from record
				column[i-start] = new String(strbuf,fielddef[col].fieldstart, fielddef[col].fieldlen);
			}
		}
		catch(java.io.EOFException e){
			System.err.println("DbFi>"+e);
			System.err.println("DbFi>record "+i+" byte "+k+" file pos "
				+rFile.getFilePointer());}
		catch(java.io.IOException e){
			System.err.println("DbFi>"+e);
			System.err.println("DbFi>record "+i+" byte "+k+" file pos "
				+rFile.getFilePointer());}
		return column;
	}

}

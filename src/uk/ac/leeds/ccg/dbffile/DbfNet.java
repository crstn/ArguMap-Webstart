package uk.ac.leeds.ccg.dbffile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import cmp.LEDataStream.LEDataInputStream;

/** 
 * 
 * This class represents a DBF (or DBase) file.<p>
 * Construct it with a URL (including the .dbf) 
 * this causes the header and field definitions to be read.<p>
 * Later queries return rows or columns of the database.
 *<hr>
 * @author <a href="mailto:ian@geog.leeds.ac.uk">Ian Turton</a> Centre for
 * Computaional Geography, University of Leeds, LS2 9JT, 1998.
 * <br>
 * mod to getStringCol by James Macgill.
 */
public final class DbfNet implements DbfConsts{
    static final boolean DEBUG=false;
    int dbf_id;
    int last_update_d,last_update_m,last_update_y;
    int last_rec;
    int data_offset;
    int rec_size;
		StringBuffer records[];
		int position=0;
    boolean hasmemo;
    LEDataInputStream dFile;
    int filesize,numfields;
    public DbfFieldDef fielddef[];

		/** 
			* Constructor, opens the file and reads the header infomation.
			* @param file The file to be opened, includes path and .dbf
			* @exception java.io.IOException If the file can't be opened.
			* @exception DbfFileException If there is an error reading header.
			*/
	public DbfNet(URL url) throws java.io.IOException,DbfFileException{
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.dbffile.DbfNet constructed. Will identify itself as DbN->");
		URLConnection uc = url.openConnection();
		InputStream in = uc.getInputStream();
		LEDataInputStream sfile = new LEDataInputStream(in);
		init(sfile);
	}

	public DbfNet(InputStream in) throws java.io.IOException,DbfFileException{
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.dbffile.DbfNet constructed. Will identify itself as DbN->");
		LEDataInputStream sfile = new LEDataInputStream(in);
		init(sfile);
    }

	public DbfNet(String name) throws java.io.IOException,DbfFileException{
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.dbffile.DbfNet constructed. Will identify itself as DbN->");
		URL url = new URL(name);
		URLConnection uc = url.openConnection();
		InputStream in = uc.getInputStream();
		LEDataInputStream sfile = new LEDataInputStream(in);
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
     * looks up the field number for the given named column
     * @param name A String for the name to look up
     * @return int The col number for the field, -1 if field could not be found
     */
	public int getFieldNumber(String name){
		for(int i=0;i<numfields;i++){
			//System.out.println(i);
			// System.out.println(fielddef[i].fieldname.toString()+" "+name);
			if(name.trim().equalsIgnoreCase(fielddef[i].fieldname.toString().trim())){
				return i;
			}
		}
		return -1;//not found
	}
		/**
			* Returns the size  of the database file.
			*/
	public int getFileSize(){
		return  filesize;
	}
	public StringBuffer getFieldName(int col){
		if(fielddef.length - 1 < col) throw new IllegalArgumentException("DbN->column number specified is invalid. It's higher than the amount of columns available");
		return  fielddef[col].fieldname;
	}
	public char getFieldType(int col){
		if(fielddef.length - 1 < col) throw new IllegalArgumentException("DbN->column number specified is invalid. It's higher than the amount of columns available");
		return fielddef[col].fieldtype;
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
        fielddef[index] = new DbfFieldDef(widthsofar);
        widthsofar+=fielddef[index].fieldlen;
      }
      //System.out.println("Skippint two bytes");
      sfile.skipBytes(1); // end of field defs marker
			records=GrabFile();
    }
	/**
		* Internal Class to hold information from the header of the file
		*/
  final class DbfFileHeader{


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
      if(DEBUG)System.out.print("DbN->Header id ");
      if(DEBUG)System.out.println(dbf_id);
      if(dbf_id==3) hasmemo=true;
      else hasmemo=false;

      last_update_y=(int)file.readUnsignedByte();
      last_update_m=(int)file.readUnsignedByte();
      last_update_d=(int)file.readUnsignedByte();
      if(DEBUG)System.out.print("DbN->last update ");
      if(DEBUG)System.out.print(last_update_d);
      if(DEBUG)System.out.print("/");
      if(DEBUG)System.out.print(last_update_m);
      if(DEBUG)System.out.print("/");
      if(DEBUG)System.out.println(last_update_y);

      last_rec=file.readInt();
      if(DEBUG)System.out.print("DbN->last rec ");
      if(DEBUG)System.out.println(last_rec);

      data_offset=file.readShort();
			//data_offset=0;
			//System.out.println("x = "+file.readUnsignedByte()+" " +
				//file.readUnsignedByte());
      if(DEBUG)System.out.print("DbN->data offset ");
      if(DEBUG)System.out.println(data_offset);

      rec_size=file.readShort();
      if(DEBUG)System.out.print("DbN->rec_size ");
      if(DEBUG)System.out.println(rec_size);

      filesize=(rec_size * last_rec) + data_offset+1;
      numfields = (data_offset - DBF_BUFFSIZE -1)/DBF_BUFFSIZE;

      if(DEBUG)System.out.print("DbN->num fields ");
      if(DEBUG)System.out.println(numfields);
      if(DEBUG)System.out.print("DbN->file size ");
      if(DEBUG)System.out.println(filesize);
      file.skipBytes(20);
    }

  }
	/**
		* Internal class to hold infomation about the fields in the file
		*/
  final class DbfFieldDef{
    public StringBuffer fieldname = new StringBuffer(DBF_NAMELEN);
    public char fieldtype;
    public int  fieldstart;
    public int  fieldlen;
    public int  fieldnumdec;
    DbfFieldDef(int pos) throws IOException {
      byte[] strbuf = new byte[DBF_NAMELEN]; // <---- byte array buffer fo storing string's byte data
      int j=-1;
			int term =-1;
      for(int i=0;i<DBF_NAMELEN;i++){
        byte b = dFile.readByte();
        if(b==0){
					if(term== -1 )
						term=j;
					continue;
				}
        j++;
        strbuf[j] = b; // <---- read string's byte data
      }
			if(term==-1) term=j;
      String name = new String(strbuf, 0, term+1);
      if(DEBUG)System.out.println("DbN->Loaded as "+name);
      fieldname.append(name); // <- append byte array to String Buffer
      if(DEBUG)System.out.println("DbN->Fieldname "+fieldname);
      fieldtype=(char)dFile.readUnsignedByte();
      fieldstart=pos;
      dFile.skipBytes(4);
      switch(fieldtype){
        case 'C':
        case 'c':
        case 'L':
        case 'M':
        case 'G':
        case 'N':
        case 'n':
          fieldlen=(int)dFile.readUnsignedByte();
          fieldnumdec=(int)dFile.readUnsignedByte();
          fieldnumdec=0;
          break;
        case 'F':
        case 'f':
          fieldlen=(int)dFile.readUnsignedByte();
          fieldnumdec=(int)dFile.readUnsignedByte();
          break;
        default:
          System.out.println("DbN->Help - wrong field type: "+fieldtype);
        }
      if(DEBUG)System.out.println("DbN->Fieldtype "+fieldtype+" width "+fieldlen+
        "."+fieldnumdec);

      dFile.skipBytes(14);


    }
  }
	/**
		* gets the next record and returns it as a string. This method works on
		* a sequential stream and can not go backwards. Only useful if you want
		* to read the whole file in one.
		* @exception java.io.IOException on read error.
		*/
  public  StringBuffer GetNextDbfRec()throws java.io.IOException{
		return records[position++];
	}

  private StringBuffer GrabNextDbfRec()throws java.io.IOException{
    //modifed for two byte character sets.
    //StringBuffer record = new StringBuffer(rec_size+numfields);
    byte[] strbuf = new byte[rec_size+numfields];
    for(int i=0;i< rec_size;i++){
    // we could do some checking here.
      strbuf[i] = dFile.readByte();

    }
    StringBuffer record = new StringBuffer(new String(strbuf));
    //System.out.println(record);
  return record;
  }

	private StringBuffer[] GrabFile() throws java.io.IOException{
		StringBuffer records[] = new StringBuffer[last_rec];
		for(int i=0;i<last_rec;i++) {
			records[i]=GrabNextDbfRec();
		}
		return records;
	}





	/**
		* fetches the <i>row</i>th row of the file
		* @param row - the row to fetch
		* @exception java.io.IOException on read error.
		*/
 public StringBuffer GetDbfRec(int row)throws java.io.IOException{
    StringBuffer record;// = new StringBuffer(rec_size);
		if(DEBUG)System.out.println("DbN->GetDbfRec("+row+") "+records[row]);
		return record = new StringBuffer(records[row].toString());
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
    Float F=new Float(0.0);
    t = rec.toString();
    for(int i=0;i<numfields;i++){
      if(DEBUG)System.out.println("DbN->type "+fielddef[i].fieldtype);
      if(DEBUG)System.out.println("DbN->start "+fielddef[i].fieldstart);
      if(DEBUG)System.out.println("DbN->len "+fielddef[i].fieldlen);
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
          if(DEBUG)System.out.println("DbN->Oh - don't know how to parse "
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
      throw new DbfFileException("DbN->No Such Column in file: "+col);
    if(fielddef[col].fieldtype!='N')
      throw new DbfFileException("DbN->Column "+col+" is not Integer");
		if(start<0)
			throw new DbfFileException("DbN->Start must be >= 0");
		if(end>last_rec)
			throw new DbfFileException("DbN->End must be <= "+last_rec);
    // move to start of data
    try{
      for(i=start;i<end;i++){
				sb.setLength(0);
				sb=GetDbfRec(i);
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
      System.err.println("DbN->"+e);
      System.err.println("DbN->record "+i+" byte "+k+" file pos "
      );}
    catch(java.io.IOException e){
      System.err.println("DbN->"+e);
      System.err.println("DbN->record "+i+" byte "+k+" file pos "
      );}
    return column;
  }
	/**
		* Fetches a column of Floats from the database file.
		* @param int col - the column to fetch
		* @exception java.io.IOException - on read error
		* @exception DbfFileException - column is not an Integer.
		*/
  public Float[] getFloatCol(int col) throws DbfFileException,
  java.io.IOException{
		return getFloatCol(col,0,last_rec);
	}
	/**
		* Fetches a part column of Floats from the database file.
		* @param int col - the column to fetch
		* @param int start - the row to start fetching from
		* @param int end - the row to stop fetching at.
		* @exception java.io.IOException - on read error
		* @exception DbfFileException - column is not an Integer.
		*/
  public Float[] getFloatCol(int col,int start,int end)
		throws DbfFileException, java.io.IOException{
    Float column[]=new Float[end-start];
    String record,st;
    StringBuffer sb = new StringBuffer(rec_size);
    int k=0,i=0;
    if(col>=numfields)
      throw new DbfFileException("DbN->No Such Column in file: "+col);
    if(fielddef[col].fieldtype!='F')
      throw new DbfFileException("DbN->Column "+col+" is not Float or Numeric it is of type "
      +fielddef[col].fieldtype);
		if(start<0)
			throw new DbfFileException("DbN->Start must be >= 0");
		if(end>last_rec)
			throw new DbfFileException("End must be <= "+last_rec);
    // move to start of data
    try{
      for(i=start;i<end;i++){
				sb.setLength(0);
				sb=GetDbfRec(i);
        record=sb.toString();
        st=new String(record.substring(fielddef[col].fieldstart,
          fielddef[col].fieldstart+fielddef[col].fieldlen));
				if(st.indexOf('.')==-1){
					st=st+".0";
				}
				try{
				    column[i-start]=new Float(st);
                }
                catch(NumberFormatException nfe){
                    column[i-start]=new Float(0.0);
                }
      }
    }
    catch(java.io.EOFException e){
      System.err.println("DbN->"+e);
      System.err.println("DbN->record "+i+" byte "+k+" file pos ");}
    catch(java.io.IOException e){
      System.err.println("DbN->"+e);
      System.err.println("DbN->record "+i+" byte "+k+" file pos ");}

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
	throws DbfFileException, java.io.IOException{
    String column[]=new String[end-start];
    String record = new String();
    StringBuffer sb = new StringBuffer(numfields);
    int k=0,i=0;
    if(col>=numfields)
      throw new DbfFileException("DbN->No Such Column in file: "+col);
    //if(fielddef[col].fieldtype!='C')
      //throw new DbfFileException("Column "+col+" is not a String");
		if(start<0)
			throw new DbfFileException("DbN->Start must be >= 0");
		if(end>last_rec)
			throw new DbfFileException("DbN->End must be <= "+last_rec);
    // move to start of data
		try{
			for(i=start;i<end;i++){
				//sb.setLength(0);
				byte[] strbuf = new byte[rec_size];

				sb=GetDbfRec(i);
				record=sb.toString();
				column[i-start]=new String(record.substring(fielddef[col].fieldstart,
					fielddef[col].fieldstart+fielddef[col].fieldlen));
			}
    }
    catch(java.io.EOFException e){
      System.err.println(e);
      System.err.println("DbN->record "+i+" byte "+k+" file pos ");}
    catch(java.io.IOException e){
      System.err.println(e);
      System.err.println("DbN->record "+i+" byte "+k+" file pos ");}
    return column;
  }





}

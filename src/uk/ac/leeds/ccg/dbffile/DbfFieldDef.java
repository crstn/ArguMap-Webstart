package uk.ac.leeds.ccg.dbffile;

import java.io.IOException;

import cmp.LEDataStream.LEDataInputStream;
/**
* class to hold infomation about the fields in the file
*/
public class DbfFieldDef implements DbfConsts{
  static final boolean DEBUG=false;
  public StringBuffer fieldname = new StringBuffer(DBF_NAMELEN);
  public char fieldtype;
  public int  fieldstart;
  public int  fieldlen;
  public int  fieldnumdec;
	public DbfFieldDef(){ /* do nothing*/ }
	public DbfFieldDef(String fieldname,char fieldtype,int fieldlen, int
		fieldnumdec){
		this.fieldname = new StringBuffer(fieldname);
		this.fieldname.setLength(DBF_NAMELEN);
		this.fieldtype = fieldtype;
		this.fieldlen = fieldlen;
		this.fieldnumdec = fieldnumdec;
	}
	public String toString(){
		return new String(""+fieldname+" "+fieldtype+" "+fieldlen+
			"."+fieldnumdec);
	}
  public void setup(int pos, LEDataInputStream dFile) throws IOException {

  //two byte character modification
  byte[] strbuf = new byte[DBF_NAMELEN]; // <---- byte array buffer for storing string's byte data 
  for(int i=0;i<DBF_NAMELEN;i++){ 
    strbuf[i] = dFile.readByte(); // <---- read string's byte data 
  } 
  fieldname.append(new String(strbuf).trim()); // <- append byte array to String Buffer 

  if(DEBUG)System.out.println("Fieldname "+fieldname);
  fieldtype=(char)dFile.readUnsignedByte();
  fieldstart=pos;
  dFile.skipBytes(4);
  switch(fieldtype){
    case 'C':
    case 'c':
    case 'D':
    case 'L':
    case 'M':
    case 'G':
      fieldlen=(int)dFile.readUnsignedByte();
      fieldnumdec=(int)dFile.readUnsignedByte();
      fieldnumdec=0;
      break;
		case 'N':
		case 'n':
    case 'F':
    case 'f':
      fieldlen=(int)dFile.readUnsignedByte();
      fieldnumdec=(int)dFile.readUnsignedByte();
      break;
    default:
      System.out.println("Help - wrong field type: "+fieldtype);
  }
  if(DEBUG)System.out.println("Fieldtype "+fieldtype+" width "+fieldlen+
    "."+fieldnumdec);

  dFile.skipBytes(14);
    

  }
}

/*
 * $Id: ShapeArc.java,v 1.1 2005/09/19 10:31:33 CarstenKessler Exp $
 *
 */

package uk.ac.leeds.ccg.shapefile;

import java.io.IOException;
import java.io.Serializable;

import cmp.LEDataStream.LEDataInputStream;
import cmp.LEDataStream.LEDataOutputStream;

/**
 * Wrapper for a Shapefile arc.
 */
public class ShapeArc implements ShapefileShape, Serializable {

  protected double[] box = new double[4];
  protected int numParts;
  protected int numPoints;
  protected int[] parts;

  /**
   * stores point data, points are stored in x,y order starting at 0.
   **/
  private double[] points;
  
  protected ShapeArc(){}//For use by ShapePolygon

  public ShapeArc( LEDataInputStream file ) throws IOException {

    file.setLittleEndianMode(true);
    int shapeType = file.readInt();

    for ( int i = 0; i<4; i++ ){
      box[i] = file.readDouble();
    }

    numParts = file.readInt();
    numPoints = file.readInt();

    parts = new int[numParts];

    points = new double[ numPoints << 1 ];
    
    for ( int i = 0; i < numParts; i++ ){
      parts[i]=file.readInt();
    }
    
    for ( int i = 0; i < numPoints; i++ ) {
      double x = file.readDouble();
      double y = file.readDouble();

      setPoint( i, x, y );
    }
    
  }
    
  public ShapeArc(double[] box,int[] parts,ShapePoint[] points){

    this.box = box;
    this.parts = parts;
    this.numParts = parts.length;
    this.numPoints = points.length;

    this.points = new double[ numPoints << 1 ];
    
    for ( int i = 0; i < points.length; i++ ) {
      setPoint( i, points[i].getX(), points[i].getY() );
    }
    
  }

  protected void setPoint( int index, double x, double y ) {

    if ( points == null ) points = new double[ getNumPoints() << 1 ];
    
    points[ index << 1 ] = x;
    points[ (index << 1) + 1] = y;
    
  }

  protected double getX( int index ) {
    return points[ index << 1 ];
  }

  protected double getY( int index ) {
    return points[ (index << 1) + 1 ];
  }
  
  public void write(LEDataOutputStream file)throws IOException{

    file.setLittleEndianMode(true);
    file.writeInt(getShapeType());
        

    for(int i = 0;i<4;i++){
      file.writeDouble(box[i]);
    }

    file.writeInt(numParts);
    file.writeInt(numPoints);

    for(int i = 0;i<numParts;i++){
      file.writeInt(parts[i]);
    }

    for(int i = 0;i<numPoints;i++){
      file.writeDouble(getX(i));
      file.writeDouble(getY(i));
    }
  }

  /**
   * Find out how many parts make up this arc.
   * @return The number of parts in this arc */
  public int getNumParts(){
    return numParts;
  }
    
  /**
   * Find out how many points make up the entire of this arc
   * @return The number of points in this arc
   */
  public int getNumPoints(){
    return numPoints;
  }

  protected ShapePoint getPoint( int index ) {
    return new ShapePoint( getX( index ), getY( index ) );
  }
  
  /**
   * Get a copy of ALL the points that make up this arc
   * @return Array ShapePoints
   */
  public ShapePoint[] getPoints(){

    ShapePoint[] newPoints = new ShapePoint[ numPoints ];

    for ( int i = 0; i < newPoints.length; i++ )
      newPoints[i] = getPoint(i);
    
    return newPoints;
    
  }
    
  /**
   * Gets an array of indexes to the start of each part in the point array
   * returned by getPoints.
   * @return array of indexs
   * @see #getPoints();
   */
  public int[] getPartOffsets(){
    return parts;
  }

  /**
   * Get all the points for a given part<p>
   * a non-existent part returns <b>null</b> (would you prefer an exception?)
   * @param part id of part,[first is 0]
   */
  public ShapePoint[] getPartPoints(int part){
    if(part>numParts-1){return null;}
        
    int start,finish,length;
        
    start = parts[part];
    if(part == numParts-1){finish = numPoints;}
    else
      {
        finish=parts[part+1];
      }
    length = finish-start;
        
        
    ShapePoint[] partPoints = new ShapePoint[length];
    for(int i =0;i<length;i++){
      partPoints[i] = getPoint( i + start );
    }
        
    return partPoints;
  }

  /**
   * Find the bounding box for this shape
   * @return double array in form {xMin,yMin,xMax,yMax}
   */
  public double[] getBounds(){
    return box;
  }

  /**
   * Get the type of shape stored (Shapefile.ARC)
   */
  public int getShapeType(){
    return Shapefile.ARC;
  }
    
  public int getLength(){
    return (44+(4*numParts));
  }
  
}

/*
 * $Log: ShapeArc.java,v $
 * Revision 1.1  2005/09/19 10:31:33  CarstenKessler
 * Carsten Ke�ler:
 * First Version submitted to CVS.
 *
 * Revision 1.2  2001/08/01 12:33:29  ianturton
 * modification submited by Michael Becke <becke@u.washington.edu> to reduce
 * memory usage.
 *
 * 
 *
 */

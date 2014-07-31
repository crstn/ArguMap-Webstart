package uk.ac.leeds.ccg.widgets;

/**
 * Represents a scatter plot point
 *
 *
 * @author      Juliet Luiz   
 * @since       0.7.8.1
 * @see         java.awt.Point
 */
import java.awt.Point;


public class SPlotPoint extends Point
{
    // Flag to indicate if this point should be highlighted
    private boolean isHilited_;


    // The id of the point - corresponding to the dbf row number (?) confirm
    private int id_;


   /**
    * Constructor with x and y coordinates
    *
    * @param    x - the x coordinate of the scatter plot point 
    * @param    y - the y coordinate of the scatter plot point
    */    
    public SPlotPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
   /**
    * Returns the flag which indicates whether the scatter plot point
    * should be highlighted or not
    *
    * @return boolean true if point is to be highlighted
    * @since BluePrint 1.0
    */
   public boolean getIsHilited()
   {
      return isHilited_;
   }


   /**
    * Sets the isHilited_
    *
    * @param isHilited_
    * @since BluePrint 1.0
    */
   public void setIsHilited(final boolean isHilited)
   {
      isHilited_ = isHilited;
   }
   /**
    * Returns the ID of the scatter plot point
    *
    * @return int The id of the scatter plot point
    */
   public int getId()
   {
      return id_;
   }


   /**
    * Sets the ID of the scatter plot point
    *
    * @param ID The id of the scatter plot point
    */
   public void setId(final int id)
   {
      id_ = id;
   }



}
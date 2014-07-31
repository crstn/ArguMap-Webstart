package uk.ac.leeds.ccg.geotools;

import java.awt.Cursor;
/**
 * this class is for testing only
 * I want to plot a circle as a tool
 */
public class DummyTool extends SimpleTool
{
    
    public int getRubberBandShape(){
        return CIRCLE;
    }
    public Cursor getCursor(){
        return new Cursor(Cursor.DEFAULT_CURSOR);
    }
    
    /**
   * provides a short name for this tool.
   * The name should be suitable for inclusion in a menu or on a button.
   *
   * @author James Macgill JM
   * @since 0.7.9 November 23 2000
   * @return String The name of this tool.
   */
   public String getName(){
    return "Dummy";
   }
   
   /**
   * provides a description for this tool.
   * The description should briefly describe the purpose of the dool
   *
   * @author James Macgill JM
   * @since 0.7.9 November 23 2000
   * @return String A description of this tool.
   */
   public String getDescription(){
    return "A fake tool";
   }
    
    
}
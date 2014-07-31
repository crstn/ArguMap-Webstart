package uk.ac.leeds.ccg.geotools;
import java.awt.Cursor;
import java.awt.Graphics;
/**
 * Tools will be used by Viewers to represent all forms of user interaction.
 * A default set of basic tools already implement this interface: Zoom, Pan, and Select.<br>
 * Other developers are free to add aditional tools as they see fit.<br>
 * 
 * Perhaps the only tricky thing of note with tools is that they effectivly have two paint options.
 * A transient paint option, which covers anything you do in the update method.<br>
 * A fixed paint option, which covers anything you do in the paint method.<br>
 * For almost all tools you should only need to use the transient paint option, please try to clean up after yourself!
 * A few tools will use the paint option, see for example the NavigateTool.
 *
 * It is strongly recomended that you extend or at least look at SimpleTool when developing a new tool as this will make your
 * life much easier.
 * 
 * If you are not extending SimpleTool then you will probably need to try some of the following:
 * <br>In setContext make a reference to the MouseStatus returned by v.getMouseStatus() so that you will always know what the user has been doing.
 * @see MouseStatus
 * @author James Macgill
 * @version 0.7.7.2 2 June 2000
 * @see SimpleTool
 *
 */

public interface Tool 
{
  /**
   * This is the set of reason codes that can be sent to the update mehtod.<br>
   * If you are using SimpleTool then these will be recast to seperate methods, however
   * feel free to handle them directly in your own tool inside the update method if you wish.<br>
   * Reasons M_ refer to mouse actions.<br>
   */   
  public static final int M_MOVE=0,M_DRAG=1,M_RELEASE=2,M_CLICK=3,REDRAW=4,CLEAR=5;  
  /**
   * Your tool will have this method called with one of the reason codes listed above whenever
   * the Viewer thinks that something has happend that you might be interested in.
   * <br>If you extend SimpleTool then the different reson codes will be dispached to seperate methods.
   *
   * @author James Macgill JM
   * @since 0.7.7.2 June 6 2000
   * @param reason One of the above reason codes will provided to give you some indication of why the viewer thinks you might be interested.
   * @param g A graphics object onto which you can scribble, draw or whatever onto.  Its already set to XOR mode, just make sure to clean up!
   */ 
  public void update(Graphics g,int reason);
  /**
   * When a tool is pased to a view the viwer will call this method to tell to tool who it belongs to.
   * SimpleTool used this to good effect, and if you extend that then you will probably not have to worry about this method at all.
   * <br>If you do want to build your tool from scratch however, then there are a couple of things you should consider doing in this method<br>
   * First, and most obviusly, store a reference to v (perhaps in a variable called context)
   * <br>Second, grab a reference to v.getMouseStatus() as this will always track what the user has been doing.
   * 
   *
   * @author James Macgill JM
   * @since 0.7.7.2 June 6 2000
   * @param v The viewer to which this tool should be attached. set to null if tool should detach.
   * @see MouseStatus
   * @see SimpleTool#setContext
   */
  public void setContext(Viewer v);
  /**
   * For painting persitent graphics only. MOST TOOLS DO NOT NEED TO DO ANYTHING IN THIS METHOD.
   * <br>Of all the tools provided by default only Navigate uses this as it requires a permenent on screen box.<br>
   * Most tools should use the graphics object passed into the update method.
   *
   *
   * @author James Macgill JM
   * @since 0.7.7.2 June 6 2000
   * @param g A Graphics object that references the viewers static screen buffer.
   */
  public void paint(Graphics g);//most should not implement this
  /**
   * find the cursor most suitable for this tool.
   *
   * @author James Macgill JM
   * @since 0.7.7.2 June 6 2000
   * @return Cursor the best cursor for use on screen with this tool.
   */
  public Cursor getCursor();
  
  /**
   * provides a short name for this tool.
   * The name should be suitable for inclusion in a menu or on a button.
   *
   * @author James Macgill JM
   * @since 0.7.9 November 23 2000
   * @return String The name of this tool.
   */
   public String getName();
   
   /**
   * provides a description for this tool.
   * The description should briefly describe the purpose of the dool
   *
   * @author James Macgill JM
   * @since 0.7.9 November 23 2000
   * @return String A description of this tool.
   */
   public String getDescription();
   
   
}
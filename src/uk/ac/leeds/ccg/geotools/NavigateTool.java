package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;


public class NavigateTool extends uk.ac.leeds.ccg.geotools.SimpleTool implements ScaleChangedListener
{
    public static final int TOP_EDGE=1,BOTTOM_EDGE=2,RIGHT_EDGE=4,LEFT_EDGE=8,NO_EDGE=-1;
    private static int dragStartPoint=NO_EDGE;
    private boolean resizing=false;
    /**
     * Box used by the Navigation tool (world)
     */
    private GeoRectangle navigationBounds = new GeoRectangle();
    /**
     * Box used by the Navigation tool (screen)
     */
    private Rectangle navScreenBox = new Rectangle(0,0,0,0);
    /**
     * When in naviagtion mode, this is the viewer this viewer is controling.
     * (you follow?)
     */
    private Viewer target;
    
    public NavigateTool(){
    }
    
    public NavigateTool(Viewer target){
        this();
        setTarget(target);
    }
    
    public Cursor getCursor(){
  
        return new Cursor(Cursor.HAND_CURSOR);
    }
    
    public void setContext(Viewer v){
        super.setContext(v);
        if(target==null){
            setTarget(target);
        }
    }
    
    public void setTarget(Viewer t){
        if(target!=null){
            target.scale.removeScaleChangedListener(this);
        }
        target = t;
        setNavigationBounds(target.scale.getMapExtent());
        target.scale.addScaleChangedListener(this);
    }
  
  
    /**
     * NavigationTool does want to have a perminent on-screen representation so it does uses this
     * method.
     */
    public void paint(Graphics g){
        //System.out.println("paint drag box");
        //g.setXORMode(Color.red);
        g.setColor(Color.red);
        g.drawRect(navScreenBox.x,navScreenBox.y,navScreenBox.width,navScreenBox.height);
        g.drawRect(navScreenBox.x+2,navScreenBox.y+2,navScreenBox.width-4,navScreenBox.height-4);
        g.setColor(Color.black);
        g.drawRect(navScreenBox.x+1,navScreenBox.y+1,navScreenBox.width-2,navScreenBox.height-2);
        //g.setPaintMode();
    }
    
    public void scaleChanged(ScaleChangedEvent sce){
         setNavigationBounds(target.scale.getProjectedMapExtent());
    }
    
    public void release(){
        resizing = false;
        //context.setCursor(cursor
        context.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public void click(){
        System.out.println("Updating nav");
        double xOffset = navigationBounds.width/2;
		    double yOffset = navigationBounds.height/2;
		    GeoRectangle newNav = new GeoRectangle(mouse.getMapPoint().x-xOffset,mouse.getMapPoint().y-yOffset,xOffset*2,yOffset*2);
		    target.setMapExtent(newNav);
		    setNavigationBounds(newNav);
	}
        
        public void drag(){   
             if(resizing){
                 double yOffset = navigationBounds.height/2;
                 double old = navigationBounds.y-yOffset;
                 double distance = old-mouse.getMapPoint().y;
                 System.out.println("Change by "+(100d/old)*distance);
                 return;
             }
             if(checkEdge()!=NO_EDGE){
              int edge = checkEdge();
              System.out.println(edge);
              
              switch(edge){
                  case RIGHT_EDGE+TOP_EDGE:
                      System.out.println("Top Right");
                     /* double xOffset = navigationBounds.width/2;
		      double yOffset = navigationBounds.height/2;
                      double newXOffset =xOffset - ((mouse.getMapPoint().x-navigationBounds.x+xOffset)/2d);
                      double newYOffset =yOffset - ((mouse.getMapPoint().y-navigationBounds.y+yOffset)/2d);
                     // GeoRectangle newNav = new GeoRectangle(navigationBounds.x,navigationBounds.y,newXOffset*2,newYOffset*2);
                          GeoRectangle newNav = new GeoRectangle(navigationBounds.x,navigationBounds.y,mouse.getMapPoint().x-navigationBounds.x,mouse.getMapPoint().y-navigationBounds.y);
                      System.out.println("Drag to "+mouse.getMapPoint().x+" , "+mouse.getMapPoint().y);
		      System.out.println("Old nav "+navigationBounds);
                      System.out.println("New nav "+newNav);
                      target.setMapExtent(newNav);
		     
                      setNavigationBounds(newNav);*/
                      context.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
                     // context.setCursor();
                      resizing=true;
                      break;
                  case RIGHT_EDGE + BOTTOM_EDGE:
                      System.out.println("Bottom Right");
                      break;
                  case LEFT_EDGE+TOP_EDGE:
                      System.out.println("Top Left");
                      break;
                  case LEFT_EDGE+BOTTOM_EDGE:
                      System.out.println("Bottom Left");
                      break;
                  default :
                      System.out.println("Other Bit");
              }
              
                 
             }
            double xOffset = navigationBounds.width/2;
		    double yOffset = navigationBounds.height/2;
		    GeoRectangle newNav = new GeoRectangle(mouse.getMapPoint().x-xOffset,mouse.getMapPoint().y-yOffset,xOffset*2,yOffset*2);
		    target.setMapExtent(newNav);
		    setNavigationBounds(newNav);
	}
        
        
            
        
       
           
       
            
        public int checkEdge(){
            Rectangle outer = new Rectangle(navScreenBox.x,navScreenBox.y,navScreenBox.width,navScreenBox.height);
            Rectangle inner = new Rectangle(navScreenBox.x+4,navScreenBox.y+4,navScreenBox.width-8,navScreenBox.height-8);
            int x = mouse.screen_xy[0];
            int y = mouse.screen_xy[1];
            if(outer.contains(x,y)&!inner.contains(x,y))
            {
               int edge=0;
               if(x<navScreenBox.x+4) edge+=LEFT_EDGE; else edge+=RIGHT_EDGE;
               if(y<navScreenBox.y+4) edge+=TOP_EDGE; else edge+=BOTTOM_EDGE;
               return edge;
            }
            else{
                return NO_EDGE;
            }
            // return (outer.contains(mouse.screen_xy[0],mouse.screen_xy[1]));
        }
            
    
    public void setNavigationBounds(GeoRectangle b){
        if(context==null)return;
        navigationBounds = b;
        int[] np = context.scale.toGraphics(b.x,b.y);
        int w = context.scale.toGraphics(b.width);
        int h = context.scale.toGraphics(b.height);
        navScreenBox = new Rectangle(np[0],np[1]-h,w,h);
       // if(debug)System.out.println("Nav Box =  "+navScreenBox);
        context.repaint();
    }
    
    public GeoRectangle getNavigationBounds(){
        return navigationBounds;
    }
    public int getRubberBandShape(){
        return NONE;
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
    return "Navigate Mode";
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
    return "Control the positioning of another map with this one";
   }
}
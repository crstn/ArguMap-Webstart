package uk.ac.leeds.ccg.widgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.HighlightChangedEvent;
import uk.ac.leeds.ccg.geotools.HighlightChangedListener;
import uk.ac.leeds.ccg.geotools.HighlightManager;


/**
 * This class creates a scatter plot based on data in a GeoData object
 * ******<B>NOTE!</B> <i> In this class, geodata values < 0 are assumed to be
 *                   invalid.  This may not be true for all Geodata that
 *                   will be plotted.  Ensure the variable INVALID_VALUE is
 *                   changed accordingly</i>
 * Changes to 1.1 over 1.0 by James Macgill<br>
 * Moved into widget package.<br>
 * As java 1.1 awt objects do not have getX and getY methods these have been removed.
 * As java 1.1 vector objects use elementAt and not get then these have been changed.
 * As java 1.1 vector objects use addElement and not add then these have been changed.
 * 
 * @author      Juliet Luiz   
 * @version     1.1
 */
 public class ScatterPlot extends Component implements HighlightChangedListener,
                                                       MouseMotionListener             
 {
     //**********************CONSTANTS*********************************//
     // Represents invalid data values in the GeoData. (Values less than
     // this are invalid)
     private final int INVALID_VALUE = 0;


     // dimensions of oval (scatter plot point)
     private final int OVAL_RADIUS = 3;
     private final int OVAL_DIAMETER = OVAL_RADIUS * 2;


     // The max length of the axis - used to calculate the factor
     // by which to divide the data points
     private final int MAX_AXIS_LENGTH = 300;
     
     // added to axes and all points to offset the plot a bit from the edge of 
     // the screen
     private final int OFFSET = 55;


     // added to x label so it is slightly below top of screen
     private final int X_LABEL_OFFSET = 10;
     
     // added to y label so it is slightly below bottom of y axis
     // TODO - rotate text!!!!
     private final int Y_LABEL_OFFSET = (OFFSET + 25);


     // The x and y coordinates where the plot description text should start
     private final int PLOT_DESCRIP_X = 10;
     private final int PLOT_DESCRIP_Y = MAX_AXIS_LENGTH + Y_LABEL_OFFSET + 30;


     // The number of markers desired on axis
     final int NUM_MARKERS = 5;


     // The size to repaint the ovals.  Somehow 15 works well despite the fact that
     // the oval diameter is only 10
     final int OVAL_REPAINT_SIZE = 15;
     //**********************END CONSTANTS*********************************//
     
     
     // Vector to hold set of points that make up scatter plot
     final Vector pointVector_ = new Vector();


     // The graphics of this component
     private Graphics graphics_;
     
     // The GeoData that contains the info to plot.  Must extract the desired 
     // data
     private GeoData xData_;
     private GeoData yData_;
     
     // The highlight Manager used between the scatter plot and map
     private HighlightManager highlightManager_;


     //origin of the component
     private int xOrigin_;
     private int yOrigin_;


     // Column names from .dbf.  To be used as labels for axes
     private String xLabel_;
     private String yLabel_;


     // The currently highlighted point
     private SPlotPoint currHilitePoint_;
     private SPlotPoint prevHilitePoint_ ;


     // Flag to indicate if previously hilited point should be repainted to 
     // normal color
     private boolean resetColor_ = false;


     // The size of the Vector containint the scatterplot points
     private int xSize_;


     // The scaling factor used when plotting markers on axes
     private int xFactor_;
     private int yFactor_;


     // The max values in the GeoData, used to calculate the scaling factor
     private int maxX_;
     private int maxY_;


   /**
    * Default constructor 
    */     
     public ScatterPlot()
     {}
   
   /**
    * Constructor with all params
    *
    * @param    highlightManager - the highlight manager for this component 
    * @param    xGeoData - the GeoData object that holds all the data 
    *           from the .dbf to be plotted on the x axis
    * @param    yGeoData - the GeoData object that holds all the data 
    *           from the .dbf to be plotted on the y axis
    * @param    xLabel - the label for the x axis (Column name from .dbf)
    * @param    yLabel - the label for the y axis (Column name from .dbf)
    */     
     public ScatterPlot(HighlightManager highlightManager, 
                        GeoData xGeoData, 
                        GeoData yGeoData,
                        String xLabel,
                        String yLabel)
     {
            xData_ = xGeoData;
            yData_ = yGeoData;
            highlightManager_ = highlightManager;
            xLabel_ = xLabel;
            yLabel_ = yLabel;


            highlightManager_.addHighlightChangedListener(this);


            // Set up the mouse motion listener
            addMouseMotionListener(this);


            // Get the size of the GeoData (assuming the size of the xGeoData
            // equals the size of the yGeoData
            xSize_ = xData_.getSize(); 


            fillVector();
     }


   /**
    * Fills the vector with scatterplot points
    *
    */
     private void fillVector()
     {
         // The x and y coordinates of a point
         int xPoint;
         int yPoint;
         
         // The x and y coordinates of a point with the displacement of the plot 
         // origin from the component origin taken into consideration
         int xPointOffset, yPointOffset;
         
         // The point to be stored in the vector
         SPlotPoint plotPoint;


         // Get the max of the data types to figure out how to scale plot
         maxX_ = (new Double(xData_.getMax())).intValue();
         maxY_ = (new Double(yData_.getMax())).intValue();
         
         // Want max axes to be  max of 200.  Need to calculate the scaling factor
         xFactor_ = maxX_/MAX_AXIS_LENGTH;
         yFactor_ = maxY_/MAX_AXIS_LENGTH;
         
         // Extract x and y values, generate SPlotPoints and store in vector
         for (int i = 0; i < xSize_; i++)
         {
            xPoint = (int)(xData_.getValue(i) / xFactor_);
            yPoint = (int)(yData_.getValue(i) / yFactor_);       


            // The xy coordinates where to plot the point (after taking the 
            // offset btn the plot origin and the component origin into account
            xPointOffset = xPoint+OFFSET;
            yPointOffset = yPoint+OFFSET;
            
            // Generate SPlotPoints and store in vector
            plotPoint = new SPlotPoint(xPointOffset,
                                       yPointOffset);
            
            // Set the id of the scatter plot point (corresponds to the row
            // number in the .dbf
            // Must add one to compensate for geodata starting at 1 and vector
            // index starting at 0
            plotPoint.setId(i+1);
            
            pointVector_.addElement(plotPoint);
        }
     }



   /**
    * Paints the scatter plot on the component
    *
    * @param  graphics - the graphics part of the ScatterPlot component
    */
     public void paint (Graphics graphics)
     {
        graphics_ = graphics;
        SPlotPoint point;
        
        drawAxes();


        // Plot the points, highlighting those who are set to be highlighted
        for (int i = 0; i<pointVector_.size(); i++)
        {
            point = (SPlotPoint)pointVector_.elementAt(i);
            
            // The color for non-highlighted scatter plot points
            graphics_.setColor(Color.blue);
            
            // If the scatter plot point should be highlighted, draw it red
            if (point.getIsHilited() == true)
            {
                graphics_.setColor(Color.red);
            }


            // test for previous highlighted point and set that to blue
            if ( resetColor_ == true )
            {
                graphics_.setColor(Color.blue);
                resetColor_ = false;
            }


            // draw point if values are valid
            graphics_.fillOval((int)point.x, 
                               (int)point.y, 
                               OVAL_DIAMETER, 
                               OVAL_DIAMETER);


        }// End for
     }
      


       /**
        * Draws the axes of the plot
        *
        */
         private void drawAxes()
         {
             Rectangle rectangle = this.getBounds();
             rectangle.setBounds(0,0,300,300);
             
             xOrigin_ = (int)(rectangle.x);
             yOrigin_ = (int)(rectangle.y);
    
             // Draw x axis
             graphics_.drawLine(xOrigin_ + OFFSET,
                                yOrigin_ + OFFSET,
                                MAX_AXIS_LENGTH + OFFSET,
                                yOrigin_ + OFFSET);
    
             // Draw y axis
             graphics_.drawLine(xOrigin_ + OFFSET,
                                yOrigin_ + OFFSET,
                                xOrigin_ + OFFSET,
                                MAX_AXIS_LENGTH + OFFSET);
    
             // Put labels on Axes
             graphics_.drawString(xLabel_, 
                                  MAX_AXIS_LENGTH, 
                                 (yOrigin_+ X_LABEL_OFFSET));
             
             graphics_.drawString(yLabel_, 
                                 (xOrigin_ + X_LABEL_OFFSET), 
                                 (MAX_AXIS_LENGTH + Y_LABEL_OFFSET));
             
             // Write description of plot below graph
             graphics_.drawString("PLOT OF " + xLabel_ + " VS " + yLabel_, 
                                   PLOT_DESCRIP_X,
                                   PLOT_DESCRIP_Y);
    
             // Draw tick marks on x axis
             final int axisMarkerDist = MAX_AXIS_LENGTH/NUM_MARKERS;
             
             // Temporary variable representing location where tick marks 
             // should be drawn
             int axisTemp;
    
             for (int i=1; i<=NUM_MARKERS; i++)
             {
                 axisTemp = i*axisMarkerDist;
                 
                 // Draw tick mark
                 graphics_.drawLine(OFFSET + (axisTemp),
                                    OFFSET,
                                    OFFSET + (axisTemp),
                                    OFFSET - 5 );
    
                 // Draw value above tick mark
                 graphics_.drawString(String.valueOf( (maxX_/NUM_MARKERS)*i ),
                                     (OFFSET + (axisTemp)),
                                     OFFSET - 20);
             }
    
             // Draw tick marks on y axis
             for (int i=1; i<=NUM_MARKERS; i++)
             {
                 axisTemp = i*axisMarkerDist;
                 // Draw tick mark
                 graphics_.drawLine( OFFSET - 5,
                                    (OFFSET + (axisTemp)),
                                     OFFSET,
                                    (OFFSET + (axisTemp)));
                 
                 // Draw value above tick mark
                 graphics_.drawString(String.valueOf((maxY_/NUM_MARKERS)*i),
                                     xOrigin_ + X_LABEL_OFFSET,
                                     (OFFSET + (axisTemp)));
             }


     }


   /**
    * Provides the ability to return the SPlotPoint that requires highlighting 
    *
    * @return SPlotPoint - the point to be highlighted, or null if none 
    */
    private SPlotPoint getCurrentHighlight()
    {
        SPlotPoint plotPoint = null;
        SPlotPoint point;


        // Go through Vector of points to find the point that requires 
        // highlighting
        for (int i = 0; i<pointVector_.size(); i++)
        {
            point = (SPlotPoint)pointVector_.elementAt(i);
            if (point.getIsHilited() == true)
            {   
               plotPoint = point;
               
               //break out of the loop once the highlighted point is found
               break;
            }
            else
            {
               plotPoint = null;
            }
        }
        return plotPoint;
    }


   /**
    * Finds the closest scatter plot point to the mouse, every time the mouse
    * moves
    *
    * @param mousePoint The point representing the mouse position
    * @return SPlotPoint The closest scatter plot point to the mouse, which 
    *                    should be highlighted
    */
    private SPlotPoint findClosestPoint( Point mousePoint )
    {
        SPlotPoint plotPoint;


        // The distance between cursor position and given plot point
        double distance;
        
        // A large amount - used to isolate the closest plot point to the 
        // mouse,in the case of the cursor being within the radius of two 
        // plot points
        double minDistance = 300;


        SPlotPoint closestPoint = null;
        
        // Holder in loop for points
        SPlotPoint currPoint;


        for (int i = 0; i<pointVector_.size(); i++)
        {
            currPoint = (SPlotPoint)pointVector_.elementAt(i);
            distance =  calcDistance(mousePoint, currPoint);
            
            // If the cursor is on a plot point,
            if (distance < OVAL_DIAMETER)
            {
                // Isolate the closest plot point to the cursor
                if (distance < minDistance)
                {
                    closestPoint = currPoint;


                    // Reset the distance
                    minDistance = distance;
                }
            }
        }
        return closestPoint;
    }


   /**
    * Listener for mouse movement. Detects location of mouse.  If it's over
    * a point, that point will be highlighted
    *
    * @param  mouseEvent - the mouseEvent generated everytime the mouse moves
    */
    public void mouseMoved(MouseEvent mouseEvent)
    {   
        // Holder in loop for points
        SPlotPoint currPoint;
        
        SPlotPoint closestPoint = findClosestPoint(mouseEvent.getPoint());


        SPlotPoint currHilitePoint = getCurrentHighlight();


        currHilitePoint_ = currHilitePoint;
        
        // Mouse is not on a point, but there is a currently highlighted point
        // Repaint, setting color of all points to non-highlighted
        if ((closestPoint == null) && (currHilitePoint != null))
        {
            for (int i = 0; i<pointVector_.size(); i++)
            {
                currPoint = (SPlotPoint)pointVector_.elementAt(i);
                currPoint.setIsHilited(false);
            }
            repaint( ((int)currHilitePoint.x - (OVAL_RADIUS)), 
                     ((int)currHilitePoint.y - (OVAL_RADIUS)), 
                        OVAL_REPAINT_SIZE, 
                        OVAL_REPAINT_SIZE );


            // remove the highlighting from the map
            highlightManager_.setHighlight(0);


        }
        // If there is a closest point that should be highlighted, paint it as 
        // highlited
        else if ( (closestPoint != null) && (currHilitePoint == null))
        {
            closestPoint.setIsHilited(true);


            repaint( ((int)(closestPoint.x) - (OVAL_RADIUS)), 
                     ((int)(closestPoint.y) - (OVAL_RADIUS)), 
                       OVAL_REPAINT_SIZE, 
                       OVAL_REPAINT_SIZE );


            // highlight the corresponding state on the map
            //problem...must unhighlight state when mouse moves off closestPoint 
            highlightManager_.setHighlight(closestPoint.getId());
        }


        // if previous highlighted point exists, then color that 
        // back to blue
        if ((prevHilitePoint_ != null)  
                                 && 
                                (prevHilitePoint_ != currHilitePoint_))
        {
           repaint( (int)(prevHilitePoint_.x - (OVAL_RADIUS)), 
                    (int)(prevHilitePoint_.y - (OVAL_RADIUS)), 
                     OVAL_REPAINT_SIZE, 
                     OVAL_REPAINT_SIZE );
        }


        // Store the currently highlighted point as the previous 
        // highlighted point
        prevHilitePoint_ = currHilitePoint_;


    }
        
   /**
    * This method needed to fulfill the mousemotionlistener interface
    */
    public void mouseDragged(MouseEvent mouseEvent)
    {   
        // Do nothing for this
    }


    /**
     * Provides the ability to calculate the distance between two points:
     * the current mouse position and a given point
     *
     * @param a one of the points that defines one end of a line 
     *          whose 'distance' will be calculated
     * @param b the point that defines the other end of a line
     *          whose 'distance' will be calculated 
     * @return  double The distance between the two points
     */
    public final double calcDistance( Point a, Point b )
    {
        return( Math.sqrt( (Math.pow(a.y - b.y, 2)) +
                      (Math.pow(a.x - b.x, 2)) ) );
    }


   /**
    * Returns the xy coordinates of point currently highlighted
    *
    * @return Point - the currently highlighted point 
    */
   public Point getCurrHilitePoint()
   {
      
       SPlotPoint tempPoint = null;
       SPlotPoint highPoint = null;


       // Go through vector of points and return the point (if any) that
       // is currently highlighted
       for (int i = 0; i<pointVector_.size(); i++)
       {
            tempPoint = (SPlotPoint)pointVector_.elementAt(i);
            if (tempPoint.getIsHilited() == true)
            {
                highPoint = tempPoint;
            }
       }
       return highPoint;
   }


   /**
    * Provides the ability to listen for changes in the highlighting of the 
    * map and highlight the corresponding point on the scatter plot
    * 
    * @param highlightChangedEvent - the object that represents what kind of 
    *                                change took place (?)
    */
   public void highlightChanged(HighlightChangedEvent highlightChangedEvent)
   {
       // get the currently highlighted state id, 
       // get the x and y of the point with this id
       // repaint the corresponding portion of the component
       int hiliteID = highlightManager_.getHighlight();
       SPlotPoint thePoint;
       
       // The x and y coordinates of the point to be highlighted
       int x,y;
       
       if (hiliteID > 0)
       {
           // The -1 is there to compensate for vectors starting at index 0
           thePoint = (SPlotPoint)pointVector_.elementAt(hiliteID -1);
           thePoint.setIsHilited(true);
           currHilitePoint_ = thePoint;
           
           // Get the x and y coordinates of the point
           x = (int)thePoint.x;
           y = (int)thePoint.y;
           
           // Only repaint the point if it has valid data (in this case >0)
           if ((x > INVALID_VALUE) || (y > INVALID_VALUE))
           {
                repaint( ((int)(x) - (OVAL_RADIUS)), 
                         ((int)(y) - (OVAL_RADIUS)), 
                            OVAL_REPAINT_SIZE, 
                            OVAL_REPAINT_SIZE );


                if (prevHilitePoint_ != null)
                {
                    prevHilitePoint_.setIsHilited(false);


                    repaint( ((int)prevHilitePoint_.x - (OVAL_RADIUS)), 
                             ((int)prevHilitePoint_.y - (OVAL_RADIUS)), 
                                OVAL_REPAINT_SIZE, 
                                OVAL_REPAINT_SIZE );
                }
           }
           prevHilitePoint_ = currHilitePoint_;
                      
        }


       // If cursor is over a state (and the corresponding plot point is
       // highlighted), and then the cursor leaves the map area and enters the
       // white area around it (with value -1 in this case), 
       // we want the highlighted scatter plot point to return to its normal color.
       if (hiliteID < INVALID_VALUE)
       {
           // Unhighlight any highlighted scatter plot point when the cursor
           // leaves the map and enters the surrounding white area around it
           if (currHilitePoint_ != null)
           {
               currHilitePoint_.setIsHilited(false);


               repaint( ((int)currHilitePoint_.x - (OVAL_RADIUS)), 
                        ((int)currHilitePoint_.y - (OVAL_RADIUS)), 
                           OVAL_REPAINT_SIZE, 
                           OVAL_REPAINT_SIZE );
           } 
       }
   } 


    
 } // EOC



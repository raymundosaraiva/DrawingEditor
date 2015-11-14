
package drawingeditor;

/**
 *
 * @author raymundosaraiva
 */
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.*;

@SuppressWarnings("serial")
public class Drawing extends JFrame
{
    public static final int PEN = 1, LINE = 2, CIRCLE = 3, RECT = 4, TRIANGLE = 5;
    
    boolean focus = false;
    
    // Makes sure the float for transparency only shows 2 digits
    DecimalFormat dec = new DecimalFormat("#.##");
		
    // Contains all of the rules for drawing 
    Graphics2D graphSettings;
		
    // Going to be used to monitor what shape to draw next
    int currentAction = PEN;
		
    // Transparency of the shape
    float transparentVal = 1.0f;
                
    // Stroke width
    int strokeWidth = 1;
                
    // Default stroke and fill colors
    Color strokeColor = Color.BLACK, fillColor = Color.BLACK;

    public Drawing()
    {
        this.setSize(900, 600);

        this.getContentPane().setBackground(Color.WHITE);
            
        // Make the drawing area take up the rest of the frame  
        this.add(new DrawingBoard(), BorderLayout.CENTER);
        
        // Show the frame
        this.setVisible(true);
        
    }
        
    private class DrawingBoard extends JComponent
    {
        	
        	// ArrayLists that contain each shape drawn along with
        	// that shapes stroke and fill
        	
                ArrayList<Shape> shapes = new ArrayList<Shape>();
                ArrayList<Color> shapeFill = new ArrayList<Color>();
                ArrayList<Color> shapeStroke = new ArrayList<Color>();
                ArrayList<Float> transPercent = new ArrayList<Float>();
                ArrayList<Integer> strokeVal = new ArrayList<Integer>();
                
                Point drawStart, drawEnd;

                // Monitors events on the drawing area of the frame
                public DrawingBoard()
                {
                	
                        this.addMouseListener(new MouseAdapter()
                          {
                        	
                            public void mousePressed(MouseEvent e)
                            {
                            	
                            	if(currentAction != PEN){
                            	// When the mouse is pressed get x & y position
                            	drawStart = new Point(e.getX(), e.getY());
                            	drawEnd = drawStart;
                                repaint();
                                
                            	} else{
                                    drawStart = new Point(e.getX(), e.getY());
                                }
                            	
                                
                            }

                            public void mouseReleased(MouseEvent e)
                            {
                            	
                            if(currentAction != PEN){
                            	
                            	  // Create a shape using the starting x & y
                            	  // and finishing x & y positions
                            	Shape aShape = null;
                            	
                            	if (currentAction == LINE){
                            		aShape = drawLine(drawStart.x, drawStart.y,
                            				e.getX(), e.getY());
                            	} else 
                            	
                            	if (currentAction == CIRCLE){
                            		aShape = drawEllipse(drawStart.x, drawStart.y,
                            				e.getX(), e.getY());
                            	} else 
                            	
                            	if (currentAction == RECT) {
                            		
                                    // Create a new rectangle using x & y coordinates
                                    aShape = drawRectangle(drawStart.x, drawStart.y,
                                    		e.getX(), e.getY());
                            	} else 
                            	if (currentAction == TRIANGLE) {
                                    int xTriangle[] = {drawStart.x, drawStart.x - (e.getX()-drawStart.x), e.getX()};
                                    int yTriangle[] = {drawStart.y, e.getY(), e.getY()};
                                    aShape = new Polygon(xTriangle, yTriangle, 3);
                                    //drawRectangle(drawStart.x, drawStart.y,e.getX(), e.getY());
                            	} 
                            	
                                  
                                  // Add shapes, fills and colors to there ArrayLists
                                  shapes.add(aShape);
                                  shapeFill.add(fillColor);
                                  shapeStroke.add(strokeColor);
                                  
                                  // Add transparency value to ArrayList
                                  transPercent.add(transparentVal);
                                  strokeVal.add(strokeWidth);
                                  
                                  
                                  drawStart = null;
                                  drawEnd = null;
                                  
                                  // repaint the drawing area
                                  repaint();
                            }    
                            }
                          });

                        this.addMouseMotionListener(new MouseMotionAdapter()
                        {
                        	
                          public void mouseDragged(MouseEvent e)
                          {
                        	  
                        	  // If this is a brush have shapes go on the screen quickly  
                        	  if(currentAction == PEN){  
                      			int x = e.getX();
                      			int y = e.getY();
                      			
                      			strokeColor = fillColor;
                      			
                      			Shape aShape = drawLine(drawStart.x, drawStart.y,x, y);
         
                                        drawStart = new Point(x, y);
                      			
                      			shapes.add(aShape);
                                        shapeFill.add(fillColor);
                                        shapeStroke.add(strokeColor);
                                  
                                  // Add the transparency value
                                  
                                  transPercent.add(transparentVal);
                                  strokeVal.add(strokeWidth);
                      		} 
                        	  
                        	// Get the final x & y position after the mouse is dragged
                        	  
                        	drawEnd = new Point(e.getX(), e.getY());
                            repaint();
                          }
                        } );
                }

                public void paint(Graphics g)
                {
                	// Class used to define the shapes to be drawn
                        graphSettings = (Graphics2D)g;

                        // Antialiasing cleans up the jagged lines and defines rendering rules
                        graphSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

                        // Iterators created to cycle through strokes and fills
                        Iterator<Color> strokeCounter = shapeStroke.iterator();
                        Iterator<Color> fillCounter = shapeFill.iterator();
                        
                        // Iterator for transparency
                        Iterator<Float> transCounter = transPercent.iterator();
                        
                        Iterator<Integer> StrokeWidthCounter = strokeVal.iterator();
                        
                        for (Shape s : shapes)
                        {
                        	
                            // Sets the shapes transparency value
                            graphSettings.setComposite(AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, transCounter.next()));
                            
                             // Defines the line width of the stroke
                            graphSettings.setStroke(new BasicStroke(StrokeWidthCounter.next()));
                        	
                            // Grabs the next stroke from the color arraylist
                            graphSettings.setPaint(strokeCounter.next());
                        	
                            graphSettings.draw(s);
                        	
                            // Grabs the next fill from the color arraylist
                            graphSettings.setPaint(fillCounter.next());
                        	
                            graphSettings.fill(s);
                        }

                        // Guide shape used for drawing
                        if (drawStart != null && drawEnd != null  && currentAction != PEN)
                        {
                        	// Makes the guide shape transparent
                            
                            graphSettings.setComposite(AlphaComposite.getInstance(
                                    AlphaComposite.SRC_OVER, 0.40f));
                        	
                            // Make guide shape gray for professional look
                            
                        	graphSettings.setPaint(Color.LIGHT_GRAY);
                        	
                        	Shape aShape = null;
                        	
                        	if (currentAction == LINE){
                        		aShape = drawLine(drawStart.x, drawStart.y,
                                		drawEnd.x, drawEnd.y);
                        	} else 
                        	
                        	if (currentAction == CIRCLE){
                        		aShape = drawEllipse(drawStart.x, drawStart.y,
                                		drawEnd.x, drawEnd.y);
                        	} else 
                        	
                        	if (currentAction == RECT) {
                        		
                        		// Create a new rectangle using x & y coordinates
                        		
                                aShape = drawRectangle(drawStart.x, drawStart.y,
                                		drawEnd.x, drawEnd.y);
                        	} else 
                            	if (currentAction == TRIANGLE) {
                                    int xTriangle[] = {drawStart.x, drawStart.x - (drawEnd.x - drawStart.x), drawEnd.x};
                                    int yTriangle[] = {drawStart.y, drawEnd.y, drawEnd.y};
                                    aShape = new Polygon(xTriangle, yTriangle, 3);
                            	} 

                            graphSettings.draw(aShape);
                        }
                }

                private Rectangle2D.Float drawRectangle(
                        int x1, int y1, int x2, int y2)
                {
                	// Get the top left hand corner for the shape
                	// Math.min returns the points closest to 0
                	
                        int x = Math.min(x1, x2);
                        int y = Math.min(y1, y2);
                        
                        // Gets the difference between the coordinates and 
                        
                        int width = Math.abs(x1 - x2);
                        int height = Math.abs(y1 - y2);

                        return new Rectangle2D.Float(
                                x, y, width, height);
                }
                
                private Ellipse2D.Float drawEllipse(
                        int x1, int y1, int x2, int y2)
                {
                        int x = Math.min(x1, x2);
                        int y = Math.min(y1, y2);
                        int width = Math.abs(x1 - x2);
                        int height = Math.abs(y1 - y2);

                        return new Ellipse2D.Float(
                                x, y, width, height);
                }
                
                private Line2D.Float drawLine(
                        int x1, int y1, int x2, int y2)
                {

                        return new Line2D.Float(
                                x1, y1, x2, y2);
                }
                
                private Ellipse2D.Float drawBrush(
                        int x1, int y1, int brushStrokeWidth, int brushStrokeHeight)
                {
                	
                	return new Ellipse2D.Float(
                            x1, y1, brushStrokeWidth, brushStrokeHeight);
                	
                }

        }
           
}
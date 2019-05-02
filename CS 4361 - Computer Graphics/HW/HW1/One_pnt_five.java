

//graphics
import java.awt.*;
//extra geometry
import java.awt.geom.*;
//so I can nicely close the window I make
import java.awt.event.*;
//drawing such
import javax.swing.*;


public class One_pnt_five extends Frame{

	public static void main(String[] args)
        { new One_pnt_five(); }

	One_pnt_five()
	{
            super("HW1 - 1.5");
	    addWindowListener(new WindowAdapter()
            { public void windowClosing(WindowEvent e)
                    {System.exit(0);}
            });
	    setSize (500, 400);
	    add("Center", new Lines());
            setVisible(true);
	}


}
class Lines extends Canvas
{
        //guesstimated values with dashlength = 20 like shown in book
	int xA = 50;
	int yA = 50;
	int xB = 400;
	int yB = 300;
	int dashlength =20;
	Lines()
	{}

        public void paint(Graphics g)
        {
            //draw the rectangle/square. Super simple
            g.drawRect(xA,yA,xB,yB);
            //time to do what was asked and draw the dashed lines
            Lines.dashedLine(g,xA,yA,xB,yB,dashlength);
        }

        //get the end point of a dash
	private static Point2D getEndPoint(Point2D p, double length, int angle)
	{
            //returned value
	    Point2D retVal = p;
	    //calculate the slope and such since it's at an angle
            double x = p.getX() + length*Math.cos(Math.toRadians(angle));
	    double y = p.getY() +length*Math.sin(Math.toRadians(angle));
	    //now that we have where it ends, set it and return
	    retVal.setLocation(x,y);
	    return retVal;
	}

        //required by the
	private static void dashedLine(Graphics g, int xA, int yA, int xB, int yB, int dashedLength2)
        {

            //where it's at currently
            Point2D current = new Point(xA,yA);
            //the end point
            Point2D end = getEndPoint(current, ((dashedLength2*100.0)/20.0),45);
            //draw a dotted line
            Stroke dotted = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]
                                                            {dashedLength2,(float) ((dashedLength2/20.0)*8.0)}, 0);
            //get ready to draw iner
            Graphics2D squares = (Graphics2D)g;
            //get the coords
            int startx = xA;
            int starty =yA;
            int endX =(int)end.getX();
            int endY =(int)end.getY();
            //finally draw line
            Line2D line = new Line2D.Double(startx,starty,endX,endY);

            //get new point
            Point2D current2 = new Point(xA+xB,yA);
            //find the end
            Point2D end2 = getEndPoint(current2,(int) ((dashedLength2*100.0)/20.0),135);
            //draw the line
            Line2D line2 = new Line2D.Double(xA+xB,yA,end2.getX(),end2.getY());

            //get new point
            Point2D current3 = new Point(xA,yA+yB);
            //find the end
            Point2D end3 = getEndPoint(current3,(int) ((dashedLength2*100.0)/20.0),-45);
            //draw the line
            Line2D line3 = new Line2D.Double(xA,yA+yB,end3.getX(),end3.getY());

            //get a point
            Point2D current4 = new Point(xA+xB,yA+yB);
            //Take a guess. I mean I'm getting tired of typing "find the end"
            Point2D end4 = getEndPoint(current4,(int) ((dashedLength2*100.0)/20.0),-135);
            //draw the line
            Line2D line4 = new Line2D.Double(xA+xB,yA+yB,end4.getX(),end4.getY());
            //we should know have 45, -45, 135, -135 which completes the square

            //set the stroke to dotted
            squares.setStroke(dotted);
            //draws the lines - now dotted!
            squares.draw(line);
            squares.draw(line2);
            squares.draw(line3);
            squares.draw(line4);
            //draw the inner rectagle as dotted
            g.drawRect(endX,endY,(int)end4.getX()-endX,(int)end4.getY()-endY);
	}
}



//Imports
import java.awt.*;
import java.awt.event.*;

public class Two_pnt_one extends Frame
{
    public static void main(String[] args){new Two_pnt_one();}

    Two_pnt_one()
    {
        //I'm running out of name ideas
        super("I AM A TITLE FOR 2.1!");
	addWindowListener(new WindowAdapter()
            {public void windowClosing(WindowEvent e){System.exit(0);}});
	//make visible, make a size, and cursor change. SO I know. I guess I could not have it. But it feels right
        setSize(500,500);
        setVisible(true);
	add("Center", new Square());
	setCursor(Cursor.getPredefinedCursor(CROSSHAIR_CURSOR));
	show();
    }
}

//makes the square
class Square extends Canvas
{
    int x1,y1,x2,y2;
    boolean clicked;
    //points in spaaaace. Note: don't un-new these or it gets angry
    Point from = new Point();
    Point to = new Point();

    Square()
    {
        //lazy boolean
        clicked = false;
        //the clicky
	addMouseListener(new MouseAdapter()
            {   public void mousePressed(MouseEvent click)
                {
                    //if they haven't clicked once already
                    if (!clicked)
	            {
                        //get the point and say they've clicked once
                        from = click.getPoint();
	                clicked = true;
	            }
	            else
	            {
                        //otherwise, get the point, reset and repaint the whole scene
                        to = click.getPoint();
	                clicked = false;
	                repaint();
	            }
	         }
	    });
    }

    //draw the square! Well, draw a bunch of lines that make a square. Clever work arounds
    public void paint(Graphics g)
    {
        //where it's started at
        x1 = from.x;
        y1 = from.y;
        //where it will be going to
        x2 = to.x;
        y2 = to.y;

        //calculates end point in space for point C
        //basic idea: take length and get rid/add in height difference
        int cx = (x2-(y1-y2));
        int cy = (y2-(x2-x1));
        //calculates end point in space for point D
        int dx = (x1-(y1-y2));
        int dy = (y1-(x2-x1));
        //draw!
        g.drawLine(x1,y1,x2,y2);//A to B
        g.drawLine(x2,y2,cx,cy);//B to C
        g.drawLine(cx,cy,dx,dy);//C to D
        g.drawLine(dx,dy,x1,y1);//D to A
    }

}


import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

// Boilerplate set up
public class Pythagoras extends Frame
{
    PythagCanvas canvas;
    public static void main(String[] args)
        {new Pythagoras();}

    public Pythagoras()
    {
        //set up the window
	super("Homework 3 - Pythagoras.");
	addWindowListener(new WindowAdapter()
        {public void windowClosing(WindowEvent e)
            {System.exit(0);}});

	//Adds a scrolly
	ScrollPane s = new ScrollPane();
	s.setSize(400, 300);
	setSize(400, 300);
	//makes the canvas
        canvas = new PythagCanvas();
	canvas.setSize(4000, 3000);
	s.add("Center", canvas);
	add("Center", s);

	//Menu bar! in case, y'know, it wasn't obvious
	MenuBar menubar = new MenuBar();

        //What it's called
	Menu menu = new Menu("Menu");
        //what options are
	MenuItem py = new MenuItem("Pythagoras");
        MenuItem quit = new MenuItem("Quit");
	//what each do
        py.setActionCommand("py");
	quit.setActionCommand("quit");
        //add them in
	menu.add(py);
	menu.add(quit);

        //what each item actually does
	menu.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(e.getActionCommand().equals("py"))
                canvas.menuClicked();
		// quit
		else
                    System.exit(0);
            }
        });

	//add it all in, crosshard clicky, and make it seen
	menubar.add(menu);
	setMenuBar(menubar);
	setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	setVisible(true);
    }
}

class PythagCanvas extends Canvas
{
    int cX, cY;
    float pSize, width = 10.0F, height = 10.0F,
        actualWidth, actualHeight;
    float r = -1, hexHeight, hexWidth;
    boolean listening = false;
    Point2F a, b;

    PythagCanvas()
    {
        MouseAdapter listener = new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                // Save points
		if(a == null && listening)
                    a = fp(e.getPoint());
		else if (b == null && listening)
                   b = fp(e.getPoint());

		repaint();
            }
        };
        addMouseListener(listener);
    }

    //Menu clicked. Get ready to find out what's clicked
    public void menuClicked()
    {
        listening = true;
	a = b = null;
	repaint();
    }

    //isotropic mapping because it gets weird otherwise
    protected void isotrop()
    {
        Dimension d = getSize();
        int maxX = d.width - 1, maxY = d.height - 1;
	pSize = Math.max(width / maxX, height / maxY);
	cX = maxX / 2;
	cY = maxY / 2;

	// Since pixel size is max of width/height over their device sizes, one of these
	// values will actually be larger. This should be used to use all of the canvas
	actualWidth = maxX * pSize;
	actualHeight = maxY * pSize;
    }


    // Recursively draws a tree of pythagoras as requested in book
    public void drawTreeOfPythagoras(Graphics g, Point2F A, Point2F B)
    {
        // Quit if distance is <= 2
        if(A.distanceSq(B) <= pSize)
            return;
        // Calculate C and D
        // Find vector AB
        Point2F AB = B.subtract(A);
        // Rotate by 90 degrees CCW
        Point2F ABrotated = new Point2F(-AB.y, AB.x);

        Point2F C = B.add(ABrotated);
        Point2F D = A.add(ABrotated);

        // Calculate E
        Point2F E = new Point2F((C.x + D.x) / 2, (C.y + D.y) / 2).add(ABrotated.scale(.5f));

        // Fill the square and triangle
        g.fillPolygon(new int[] {iX(A.x), iX(B.x), iX(C.x), iX(D.x)}, new int[] {iY(A.y), iY(B.y), iY(C.y), iY(D.y)}, 4);
        g.fillPolygon(new int[] {iX(D.x), iX(C.x), iX(E.x)}, new int[] {iY(D.y), iY(C.y), iY(E.y)}, 3);

        // Draw the square and triangle
        g.drawLine(iX(A.x), iY(A.y), iX(B.x), iY(B.y));
        g.drawLine(iX(B.x), iY(B.y), iX(C.x), iY(C.y));
        g.drawLine(iX(C.x), iY(C.y), iX(D.x), iY(D.y));
        g.drawLine(iX(D.x), iY(D.y), iX(A.x), iY(A.y));
        g.drawLine(iX(D.x), iY(D.y), iX(C.x), iY(C.y));
        g.drawLine(iX(C.x), iY(C.y), iX(E.x), iY(E.y));
        g.drawLine(iX(E.x), iY(E.y), iX(D.x), iY(D.y));

        //And back to the top
        drawTreeOfPythagoras(g, D, E);
        drawTreeOfPythagoras(g, E, C);
    }

    // Convenience class to work with 2d points in logical coordinates
    class Point2F
    {
        public float x,y;
        public Point2F(float x, float y)
        {
            this.x = x;
            this.y = y;
        }
        // Returns new Point2F that is the addition of this and p
        public Point2F add(Point2F p)
        {return new Point2F(x + p.x, y + p.y);}

        // Returns new Point2F scaled by this scalar
        public Point2F scale(float s)
        {return new Point2F(x*s, y*s);}

        // REturns negative of whatever it's at
        public Point2F negative()
        {return scale(-1);}

        // Returns new Point2F that is the subtraction of this and p
        public Point2F subtract(Point2F p)
        {return add(p.negative());}

        // Returns distance squared to point p
        public float distanceSq(Point2F p)
        {return (p.x-x)*(p.x-x) + (p.y-y)*(p.y-y);}
    }

    @Override
    public void paint(Graphics g)
    {
        isotrop();
        //We should have points. Draw it!
        if(b != null)
            drawTreeOfPythagoras(g, a, b);
    }

    // Functions taken from the book
    int iX(float x)
    {return Math.round(cX + x / pSize);}
    int iY(float y)
    {return Math.round(cY - y / pSize);}
    float fx(int X)
    {return (X - cX) * pSize;}
    float fy(int Y)
    {return (cY - Y) * pSize;}
    Point2F fp(Point2D p)
    {return new Point2F(fx((int) p.getX()), fy((int) p.getY()));}
}

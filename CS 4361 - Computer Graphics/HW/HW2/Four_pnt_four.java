

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

//Class
public class Four_pnt_four extends Frame
{

    public static void main(String[] args) throws IOException
    { new Four_pnt_four();  }


    Four_pnt_four()
    {
        //copy the title of the thing
        super("Bresenham");

        addWindowListener(new WindowAdapter()
        { public void windowClosing(WindowEvent e) {System.exit(0);}});

        //Set size, center it, and set visible
        setSize(500, 500);
        add("Center", new DotCanvas());
        setVisible(true);
    }
}

//draws the dots
class DotCanvas extends Canvas
{
    //circle width, height, and size of pixel
    float circWidth = 10.0F, circHeight = 7.5F, pixelSize;

    //center of x and y coord, dgrid=10, and max x and y
    int midX, midY, dGrid = 10, maxX, maxY;


    void initializeGrid()
    {
        //the grid
        Dimension d;

        //gets the size of our available space
        d = getSize();

        //gets max X, max Y (-1 for fitting)
        maxX = d.width - 1;
        maxY = d.height - 1;
        //size of a pixel is max of  height/space there of x and y
        pixelSize = Math.max(circWidth / maxX, circHeight / maxY);
        //center is obviously dead middle of maxes
        midX = maxX / 2;
        midY = maxY / 2;
    }

    //deals with drawing each "pixel". Goes in Line and Circle
    void drawPixel(Graphics g, int xP, int yP)
    {
        //x value * dGrid and y*dGrid
        int x = xP * dGrid;
        int y = yP * dGrid;
        //height value using dGrid
        int h = dGrid / 2;

        //Draw oval/circle, which fits better
        g.drawOval(x - h, y - h, dGrid, dGrid);
    }

    //draws the lines via Bresenham
    void drawLine(Graphics g, int xP, int yP, int xQ, int yQ)
    {

        int c, m;
        int dVal = 0;
        int xInc=1, yInc = 1;
        int x = xP;
        int y = yP;
        int hX = xQ - xP;
        int hY = yQ - yP;

        //part of bresenham for x movement
        if (hX < 0)
        {
            xInc = -1;
            hX = -hX;
        }
        //part of bresenham for y movement
        if (hY < 0)
        {
            yInc = -1;
            hY = -hY;
        }

        //if they're the same, do the case 3
        if (hY <= hX)
        {
            c = 2 * hX;
            m = 2 * hY;

            while(true)
            {
                drawPixel(g, x, y);
                if (x == xQ)
                    break;

                x += xInc;
                dVal += m;

                if (dVal > hX)
                {
                    y += yInc;
                    dVal -= c;
                }
            }
        }
        //still part of case 3.
        else
        {
            c = 2 * hY;
            m = 2 * hX;

            while(true)
            {
                drawPixel(g, x, y);
                if (y == yQ)
                    break;

                y += yInc;
                dVal += m;
                if (dVal > hY)
                {
                    x += xInc;
                    dVal -= c;
                }
            }
        }
    }

    //draws the circles via Bresenham
    void drawCircle(Graphics g, int xC, int yC, int r)
    {
        int x = 0, E = 0;
        int y = r;
        int u = 1;
        int v = 2 * r - 1;

        //look, if you've got to here and you've noticed comments lacking, that's cuz I don't really know the
        //math, I'm just translating it to code, so. . . I mean it's working
        while (x < y)
        {
            drawPixel(g, xC + x, yC + y);
            drawPixel(g, xC + y, yC - x);
            drawPixel(g, xC - x, yC - y);
            drawPixel(g, xC - y, yC + x);

            x++;
            E += u;
            u += 2;

            if (v < 2 * E)
            {
                y--;
                E -= v;
                v -= 2;
            }

            //lots of trial and error to this point, but if we've completed the circle
            //it's time to stop, otherwise drawing!
            if (x > y)
                break;
            drawPixel(g, xC + y, yC + x);
            drawPixel(g, xC + x, yC - y);
            drawPixel(g, xC - y, yC - x);
            drawPixel(g, xC - x, yC + y);
        }
    }

    //draws the little dotted grid
    void displayGrid(Graphics g)
    {
        //draw x
        for (int x = dGrid; x <= maxX; x += dGrid)
            //draw y
            for (int y = dGrid; y <= maxY; y += dGrid)
                g.drawLine(x, y, x, y);
    }

    public void paint(Graphics g)
    {
        //make grid, show grid, draw lines up, and draw circles
        initializeGrid();
        displayGrid(g);

        try
        {
            //get file
            Scanner input = new Scanner(new File("input.txt"));
            //get the number of counting in lazish manner
            String num = input.nextLine();
            //System.out.println(Integer.parseInt(num));

            //using what I hate in Java to make it draw one line until the end of instructions
            //I hate Java and pointers.
            for(int i =0; i<Integer.parseInt(num); i++)
            {
                drawLine(g, input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt());
            }
            //draws the ending circle
            while(input.hasNextLine())
                drawCircle(g, input.nextInt(), input.nextInt(), input.nextInt());

        }
        catch (Exception e)
        {
            System.out.println("No file so here's a default!");
            //draws a line
            drawLine(g, 1, 1, 12, 5);
            //draws a circle (g, x, y, radius)
            drawCircle(g, 23, 10, 8);
        }
    }
}

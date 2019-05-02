

import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class curves extends Frame
{
    public static void main(String[] args)
    {
        //I used dragon, so where's file?
        if (args.length < 0)
            System.out.println("Program arg must be file name >=(.");
        else
            new curves(args[0]);
    }

    curves(String file)
    {
      super("Left click = forward. Right = back.");
      addWindowListener(new WindowAdapter()
         {public void windowClosing(WindowEvent e)
                {System.exit(0);}});
      setSize (800, 600);
      add("Center", new Curvy(file));
      show();
    }
}

class Curvy extends Canvas
{
    Graphics g;
    int maxX, maxY, stage = 1;
    String fileName, axiom, F, f, X, Y;
    private static final double CORNER_FRACTION = .3;
    double xLast, yLast, dir, lastDir, rotation, sDirection, fxStart, fyStart, length, reductFact;

    Curvy(String fileName)
    {
        Input inp = new Input(fileName);
        if (inp.fails())
           error("Cannot open input file.");

        //set everything
        axiom = inp.readString(); inp.skipRest();
        F = inp.readString(); inp.skipRest();
        f = inp.readString(); inp.skipRest();
        X = inp.readString(); inp.skipRest();
        Y = inp.readString(); inp.skipRest();
        rotation = inp.readFloat(); inp.skipRest();
        sDirection = inp.readFloat(); inp.skipRest();
        fxStart = inp.readFloat(); inp.skipRest();
        fyStart = inp.readFloat(); inp.skipRest();
        length = inp.readFloat(); inp.skipRest();
        reductFact = inp.readFloat();

        if (inp.fails())
           error("Input file incorrect.");

        addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent evt)
            {
                if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
                {
                    stage--;       // Right mouse button decreases stage

                    if (stage < 1)
                      stage = 1;
                }
                else
                    stage++;     // Left mouse button increases stage
                repaint();
            }
        });

    }

    int iX(double x)
    {return (int)Math.round(x);}

    int iY(double y)
    {return (int)Math.round(maxY-y);}

    //just draw the thing
    void justDraw(Graphics g, double dx, double dy)
    {g.drawLine(iX(xLast), iY(yLast), iX(xLast + dx ) ,iY(yLast + dy));}

    //There was an issue
    void error(String str)
    {
        System.out.println(str);
        System.exit(1);
    }

    //to draw from x to y
    void toDraw(Graphics g, double dx, double dy)
    {
        g.drawLine(iX(xLast), iY(yLast), iX(xLast + dx) ,iY(yLast + dy));
        xLast = xLast + dx;
        yLast = yLast + dy;
    }

    //move it to
    void moveTo(Graphics g, double x, double y)
    {
        xLast = x;
        yLast = y;
    }

    //paint
    public void paint(Graphics g)
    {
        Dimension d = getSize();
        maxX = d.width - 1;
        maxY = d.height - 1;
        xLast = fxStart * maxX;
        yLast = fyStart * maxY;
        dir = sDirection; // Initial direction in degrees
        lastDir = sDirection;
        String instructions = axiom;
        double finalLen = length * maxY;

        // So just expand the string for however many levels we need
        for(int k=0;k<stage;k++)
        {
            String instruct = "";
            for(int j=0;j<instructions.length();j++)
            {
                char c = instructions.charAt(j);
                switch(c)
                {
                    case 'F':
                        instruct += F;
                        break;
                    case 'f':
                        instruct += f;
                        break;
                    case 'X':
                        instruct += X;
                        break;
                    case 'Y':
                        instruct += Y;
                        break;
                    default:
                        instruct += c;
                        break;
                }
            }
            instructions = instruct;
            finalLen *= length;
        }
       // curved lines, no touch!
       instructions = instructions.replaceAll("[^Ff\\+\\-\\]\\[]", "");
       // Remove angles that cancel each other out, makes coding corners easier
       instructions = instructions.replaceAll("\\+\\-|\\-\\+", "");
       //now draw
       turtleGraphics(g, instructions, stage, finalLen);
    }

    //A TURTLE HAS DRAWN IT TO THE WATER!
    public void turtleGraphics(Graphics g, String instruction,int depth, double len)
    {
        double xMark=0, yMark=0, dirMark=0;
        double rad, dx, dy;
        for (int i=0;i<instruction.length();i++)
        {
            char ch = instruction.charAt(i),
            nextCh = (instruction.length() > i+1) ? instruction.charAt(i+1) : '_',
            prevCh = (i > 0) ? instruction.charAt(i-1) : '_';
            switch(ch)
            {
                case 'F': // Step forward and draw
                    // Start: (xLast, yLast), direction: dir, steplength: len
                    rad = Math.PI/180 * dir; // Degrees -> radians
                    dx = len * Math.cos(rad);
                    dy = len * Math.sin(rad);
                    if((nextCh != '_' && (nextCh == '+' || nextCh == '-')) || (prevCh != '_' && (prevCh == '+' || prevCh == '-')))
                        toDraw(g, dx*(1-CORNER_FRACTION), dy*(1-CORNER_FRACTION));
                    else
                        toDraw(g, dx, dy);
                    break;
                case 'f': // Step forward without drawing
                    // Start: (xLast, yLast), direction: dir, steplength: len
                    rad = Math.PI/180 * dir; // Degrees -> radians
                    dx = len * Math.cos(rad);
                    dy = len * Math.sin(rad);
                    moveTo(g, xLast + dx, yLast + dy);
                    break;
                case '+': // Turn right
                    if((rotation == 90 || rotation == -90) && prevCh != '_' && prevCh == 'F' && nextCh != '_' && nextCh == 'F')
                    {
                        double cornerLen = len * Math.sqrt(CORNER_FRACTION*CORNER_FRACTION + CORNER_FRACTION*CORNER_FRACTION);
                        rad = Math.PI/180 * ((dir-rotation/2) % 360); // Degrees -> radians
                        dx = cornerLen * Math.cos(rad);
                        dy = cornerLen * Math.sin(rad);
                        toDraw(g, dx, dy);
                    }
                    dir -= rotation;
                    break;
                case '-': // Turn left
                    if((rotation == 90 || rotation == -90) && prevCh != '_' && prevCh == 'F' && nextCh != '_' && nextCh == 'F')
                    {
                        double cornerLen = len * Math.sqrt(CORNER_FRACTION*CORNER_FRACTION + CORNER_FRACTION*CORNER_FRACTION);
                        rad = Math.PI/180 * ((dir+rotation/2) % 360); // Degrees -> radians
                        dx = cornerLen * Math.cos(rad);
                        dy = cornerLen * Math.sin(rad);
                        toDraw(g, dx, dy);
                    }
                    dir += rotation;
                    break;
                case '[': // Save position and direction
                    xMark = xLast;
                    yMark = yLast;
                    dirMark = dir;
                    break;
                case ']': // Back to saved position and direction
                    xLast = xMark;
                    yLast = yMark;
                    dir = dirMark;
                    break;
            }
        }
    }
}


class Input
{
    private boolean ok = true;
    private boolean eof = false;
    private PushbackInputStream pbis;

    Input()
    {pbis = new PushbackInputStream(System.in);}

    Input(String file)
    {
        try
        {
           InputStream in = new FileInputStream(file);
           pbis = new PushbackInputStream(in);
        }
        catch(IOException ioe)
        {ok = false;}
    }

    //failed?
    boolean fails()
    {return !ok;}

    //end of file
    boolean eof()
    {return eof;}

    //clear
    void clear()
    {ok = true;}

    //go back
    void pushBack(char ch)
    {
        try
        {pbis.unread(ch);}
        catch(IOException ioe){}
    }

    //skip
    void skipRest()
    {
        char ch;
        do ch = readChar();
            while (!(eof()  || ch == '\n'));
    }

    //close up
    void close()
    {
        if (pbis != null)
        try
        {pbis.close();}
        catch(IOException ioe)
        {ok = false;}
    }

    //read a character
    char readChar()
    {
        int ch=0;
        try
        {
            ch = pbis.read();
            if (ch == -1)
            {
                eof = true;
                ok = false;
            }
        }
        catch(IOException ioe)
        {ok = false;}
        return (char)ch;
    }

    // Read first string between quotes (").
    String readString()
    {
        String str = " ";
        char ch;
        do ch = readChar();
            while (!(eof()  || ch == '"'));
                                                    // Initial quote
        for (;;)
        {
            ch = readChar();
            if (eof()  ||   ch == '"') // Final quote (end of string)
                break;
            str += ch;
        }
        return str;
    }

    //read int
    int readInt()
    {
        boolean neg = false;
        char ch;
        do {ch = readChar();}
            while (Character.isWhitespace(ch));

       if (ch == '-')
       {
           neg = true;
           ch = readChar();
       }
       if (!Character.isDigit(ch))
       {
           pushBack(ch);
           ok = false;
           return 0;
       }

       int x = ch - '0';
       for (;;)
       {
           ch = readChar();
           if (!Character.isDigit(ch))
            {
                pushBack(ch);
                break;
            }
           x = 10 * x + (ch -  '0');
       }
       return (neg ? -x : x);
     }

    //read a float
    float readFloat()
    {
        char ch;
        int nDec = -1;
        boolean neg = false;
        do{ch = readChar();}
            while (Character.isWhitespace(ch));

        if (ch == '-')
        {
            neg = true;
            ch = readChar();
        }
        if (ch == '.')
        {
            nDec = 1;
            ch = readChar();
        }
        if (!Character.isDigit(ch))
        {
            ok = false;
            pushBack(ch);
            return 0;
        }
        float x = ch - '0';
        for (;;)
        {
            ch = readChar();
            if (Character.isDigit(ch))
            {
                x = 10 * x + (ch - '0');
                if (nDec >= 0)
                    nDec++;
            }
            else if (ch == '.' && nDec == -1)
                nDec = 0;
            else
                break;
        }

        while (nDec > 0)
        {
            x *= 0.1;
            nDec--;
        }
        if (ch == 'e'  ||   ch == 'E')
        {
            int exp = readInt();
            if (!fails())
            {
                while (exp < 0)
                {
                    x *= 0.1;
                    exp++;
                }
                while (exp > 0)
                {
                    x *= 10;
                    exp--;
                }
            }
        }
        else
            pushBack(ch);
        return (neg ? -x : x);
    }
}

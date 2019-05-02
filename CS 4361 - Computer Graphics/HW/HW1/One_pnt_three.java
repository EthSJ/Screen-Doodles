

//I'm sort of lazy with imports. This is for Graphics - namely 2d
import java.awt.*;
//Again, import laziness. Needed for the frames.Somewhat copied from personal project
import javax.swing.*;

public class One_pnt_three
{
    public static void main(String[] args)
    {
        //make a frame
	JFrame frame = new JFrame();
	//give it a size
        frame.setSize(400, 300);
        //make it be seen
	frame.setVisible(true);
        //when you close it, the program stops
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //panel that everything is made in
	frame.add(new JPanel()
        {
            //what will be drawing the squares
            @Override
	    protected void paintComponent(Graphics g)
            {
                //the squares. Or A square. Depends
                Graphics2D squares = (Graphics2D)g;

                //the rotation by 45°
                double rotation = Math.toRadians(45);
                //the scale factor. What keeps them scaled right
                double sFactor = 1 / (Math.sin(rotation) + Math.cos(rotation));
                //initial square size
                double size = 200;

                //moves it into middle of screen. Some guesswork and tweaking to
                //make it look like book's "looks like this."
                squares.translate(size, size-75);

                //Loop through drawing squares
                for (int i = 0; i < 20; i++)
                {
                    //the current square's size
                    int intSize = (int) Math.round(size);
                    //draw a square.
                    squares.drawRect(-intSize / 2, -intSize / 2, intSize, intSize);
                    //scale it down
                    size = size * sFactor;
                    //rotate the square
                    squares.rotate(rotation);
                }
            }
	});
    }
}

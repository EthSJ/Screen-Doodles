
//imports for print to console. (Mostly debug)
import java.io.*;

public class Stairs
{
    public static void main(String[] args)throws IOException
    {
        //was a parameter missed?
        if (args.length < 3 || args.length > 3)
        {
            System.out.println("Did you miss a parameter?");
            System.exit(1);
        }

        //Set up. Start at 7 like example shows
        int n = 0;
        double a = 7.0, rotation = 0;
        //try assigning values
        try
        {
            //assign parameters
            n = Integer.parseInt(args[0]);
            rotation = Double.parseDouble(args[1]);

            //If the parameter's bad
            if (n <= 0 || a < 0.5)
            {
                System.out.println("Bad parameter scale value!");
                System.exit(1);
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Them's not a number! >=I. Fix your parameters!");
            System.exit(1);
        }

        //now go!
        new GenStairs(n, a, rotation * Math.PI / 180, args[2]);
    }
}
//point. Literally just helps make a point.
class Point3D
{
    float x, y, z;
    Point3D(double x, double y, double z)
    {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }
}

//generate stairs
class GenStairs
{
    //File writer and set up for calculations
    FileWriter writer;
    int n2 = 2 * 20, n3 = 3 * 20, n4 = 4 * 20, m, temp=0;
    float cache =0;

    //Generate the stairs
    GenStairs(int n, double a, double turn, String file) throws IOException
    {
        //Set up writer
        writer = new FileWriter(file);
        //set up point array and start values
        Point3D[] P = new Point3D[9];
        double b = a - 6;

        //Give an idea of points
        P[1] = new Point3D(a, -b, 0);
        P[2] = new Point3D(a,  b, 0);
        P[3] = new Point3D(b,  b, 0);
        P[4] = new Point3D(b, -b, 0);
        P[5] = new Point3D(a, -b, 0.2);
        P[6] = new Point3D(a,  b, 0.2);
        P[7] = new Point3D(b,  b, 0.2);
        P[8] = new Point3D(b, -b, 0.2);

        //generate vertices for the Beam
        for (int k=0; k<n; k++)
        {
            double phi = k * turn, cosPhi = Math.cos(phi), sinPhi = Math.sin(phi),x,y;
            float x1=0,y1=0,z1=0, newX=0,newY=0,newZ=0;

            //calculate, rotate, and write
            for (int i=1; i<=8; i++)
            {
                x = P[i].x; y = P[i].y;
                x1 = (float)(x * cosPhi - y * sinPhi);
                y1 = (float)(x * sinPhi + y * cosPhi);
                z1 = (float)((P[i].z + k));
                if(i==1||i==2)
             	    {
                        newX =x1+newX;
                        newY=y1+newY;
                        newZ=(z1+newZ);
                    }
                temp++;
                writer.write((temp) + " " + x1 + " " + y1 + " " + z1 +"\r\n");
            }

            //generate vertices for the railing
            temp++;
            writer.write((temp) + " " + newX/2 + " " + newY/2 + " " + newZ/2 +"\r\n");
            temp++;
            if(temp==10)
            {
                cache=(newX/2);
                cache+=2;
                writer.write((temp) + " " + newX/2 + " " + newY/2 + " " + (cache) +"\r\n");
            }
            else
            {
                cache+=1;
                writer.write((temp) + " " + newX/2 + " " + newY/2 + " " + (cache) +"\r\n");
            }
        }

        temp++;

        //generate vertices for cylinder
        genCylin(n, Float.valueOf((float) (0.2/2)), 0);
        writer.write("Faces:\r\n");
        //generate faces for cylinder
        genFaces(n,n2);

        //generate faces for the beam
        for (int k=0; k<n; k++)
        {
            // Beam k again:
            int m = 10 * k;
            face(m, 1, 2, 6, 5);
            face(m, 4, 8, 7, 3);
            face(m, 5, 6, 7, 8);
            face(m, 1, 4, 3, 2);
            face(m, 2, 3, 7, 6);
            face(m, 1, 5, 8, 4);
        }

        //generate faces for railing
        int railX=9,railY=10;
        writer.write(railX+" "+(railY)+".\r\n");

        for(int f=1;f<n;f++)
        {
            railX++;railY+=10;
            writer.write(railX+" "+(railY)+".\r\n");
            railX+=9;
            writer.write(railX+" "+(railY)+".\r\n");

        }
        writer.close();
    }

    //writes out the faces
    void face(int m, int a, int b, int c, int d) throws IOException
    {
        a += m;
        b += m;
        c += m;
        d += m;
        writer.write(a + " " + b + " " + c + " " + d + ".\r\n");
    }

    //generates vertices for cylinder
    void genCylin(int n, float radOut, float radIn) throws IOException
    {
        //set up
        double delt=2* Math.PI / n;
	double height = (n/25)*15;

        //go through and make
        for (int i=1; i<=n; i++)
	{
            double rot=i* delt, cosa = Math.cos(rot), sina = Math.sin(rot);
            double r = radOut ;

            //if we're rotating outwards
            if (r > 0)
                for (int bottom=0; bottom<2; bottom++)
                {
                    int k = (2 * 0 +  bottom)* n + i;

                    // Vertex numbers for i = 1:
                    // Top:      1 (outer) 2n+1 (inner)
                    // Bottom: n+1 (outer) 3n+1 (inner)
                    // w = write, i = int, r = real
                    wI(k+temp);
                    wR(r * cosa*10); //x
                    wR(r * sina*10); // and y
                    if((k+temp>temp+25)&& (k+temp<temp+51))
                        wI(0);
                    else
                        wI((int) ((2 - bottom)*height)); // bottom: z = 0; top: z = 1
                    writer.write("\r\n");
                }
	}
    }

    //Method to generate faces for cylinder
    private void genFaces(int n, int n2)
    {
        try
        {
            for (int i=1; i<=n; i++)
                wI(i+temp);

            writer.write(".\r\n");

            // Bottom boundary face:
            for (int i=n2; i>=n+1; i--)
                wI(i+temp);

            writer.write(".\r\n");

            // Vertical, rectangular faces:
            for (int i=1; i<=n; i++)
            {
                int j = i % n + 1;
                // Outer rectangle:
                    wI(j+temp); wI(i+temp); wI(i + n+temp); wI(j + n+temp); writer.write(".\r\n");
            }
	}
        catch (IOException e)
	{e.printStackTrace();}
    }

    void wI(int x) throws IOException
    {
        writer.write(" " + String.valueOf(x));
    }

    void wR(double x) throws IOException
    {
	if (Math.abs(x) < 1e-9) x = 0;
	writer.write(" " + String.valueOf((float)x));
    }
}

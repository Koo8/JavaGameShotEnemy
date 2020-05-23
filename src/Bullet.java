import java.awt.*;

public class Bullet
{
    // FIELDS
    private double x;
    private double y;
    private int r;

    private double dx;
    private double dy;
    private double radian;
    private double speed;

    private Color color1;

    // CONSTRUCTOR
    public Bullet(double angle, double x, double y)
    {
        this.x = x;
        this.y = y;
        r = 2;
        radian =Math.toRadians(angle); // convert angle to radian value

        speed = 15;
        dx = Math.cos(radian) * speed;
        dy = Math.sin(radian) * speed;
        color1 = Color.YELLOW;
    }

    // FUNCTIONS
    public double getx() {return x;}
    public double gety() {return y;}
    public double getr() {return r;}

    public boolean update()
    {
        // update x and y with user input
        x += dx;
        y += dy;

        // if bullets shot out of boundry, remove them from the list,
        if( x < -r || y < -r || x > GamePanel.WIDTH + r || y > GamePanel.HEIGHT + r )
        {
            // when x or y is out of the boundry of the game screen
            return true;
        }
        return false;
    }

    public void draw(Graphics2D g)
    {
        g.setColor(color1);
        g.fillOval((int)(x-r),(int)(y-r), 2*r, 2*r);
    }

}
import java.awt.*;

/**
 This class only go downwards with a slow speed,
 once it is off screen, it should be removed
 It has 3 types with different powerUp capabilities
 NOTE: whenever enemy dies, there is a chance the enemy drops a powerUp square,
 it only  occurs when enemy dies
 **/

public class PowerUp
{

    // FIELDS
    private double x;
    private double y;
    private int r;

    private int type;
    private Color color1;


    // CONSTRUCTOR
    PowerUp(int type, double x, double y)
    {
        this.type = type;
        this.x = x;
        this.y =y;
        if(type ==1) {color1 = Color.PINK; r = 3;}
        if(type == 2) { color1 = Color.ORANGE; r = 4;}
        if(type == 3) { color1 = Color.GREEN; r =5; }
    }

    // GETTERS
    public double getx() {return x;}
    public double gety() {return y;}
    public int getr() {return r;}
    public int getThpe() {return type;}

    //FUNCTIONS
    boolean update()
    {
        // setup  downward speed;
        y += 2; // 2 is the speed, it goes downwards.
        // remove it when off screen
//        if( y > GamePanel.HEIGHT + r)
//            return true;
//        return false;
        return ( y > GamePanel.HEIGHT + r);
    }

    void draw(Graphics2D g)
    {
        g.setColor(color1);
        g.fillRect((int)(x-r),(int)(y-r),2*r,2*r);

        g.setStroke(new BasicStroke(3));
        g.setColor(color1.darker());
        g.drawRect((int)(x-r),(int)(y-r),2*r,2*r);
        g.setStroke(new BasicStroke(1));

    }

}
import java.awt.*;

class Enemy
{
    // FIELDS
    private double x;
    private double y;
    private int r;

    private double dx;
    private double dy;
    private double speed;

    private int health;
    private int type;
    private int rank;

    private Color color1;

    private boolean ready;
    private boolean dead;


    // CONSTRUCTOR
    Enemy(int type, int rank)
    {
        this.type = type;
        this.rank = rank;

        // default emeny
        if(type == 1)
        {
            color1 = Color.BLUE;
            // the weakest enemy
            if(rank == 1 )
            {
                speed = 2.0;
                r = 5;
                health = 1;
            }
        }

        // set random x and y position
        x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4.0;
        y = -r; // set y offScreen

        // set angle downward from 20degree to 160 degree
        double angle = Math.random() * 140 + 20;
        double radian = Math.toRadians(angle);

        dx = Math.cos(radian) * speed;
        dy = Math.sin(radian) * speed;

        ready = false;
        dead = false;
    }

    // FUNCTIONS
    double getx() {return x;}
    double gety() {return y;}
    double getr() {return r;}
    int getType() { return type;}
    int getRank() { return rank; }
    boolean isDead() { return dead;}

    void hit()
    {
        health--;
        if(health <= 0)
            dead = true;
    }

    void update(){
        x += dx;
        y += dy;
        // when x and y are within the game boundry, set ready to true
        if(!ready)
        {
            if(x > r && x < GamePanel.WIDTH -r &&
                    y > r && y < GamePanel.HEIGHT -r)
            {
                ready = true;
            }
        }
        // make enemy bouncing off the wall when it goes within r
        if (x < r && dx < 0) dx = -dx;
        if (y < r && dy < 0) dy = -dy;
        if (x > GamePanel.WIDTH -r && dx > 0) dx = -dx;
        if (y > GamePanel.HEIGHT -r && dy > 0) dy = -dy;


    }

    void draw(Graphics2D g)
    {
        g.setColor(color1);
        g.fillOval( (int)(x-r),(int)(y-r), 2*r, 2*r);

        g.setStroke(new BasicStroke(3));
        g.setColor(color1.darker());
        g.drawOval((int) (x-r),(int) (y-r), 2*r, 2*r);

        g.setStroke(new BasicStroke(1));
    }


}
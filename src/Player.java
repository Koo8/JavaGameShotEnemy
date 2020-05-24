import java.awt.*;

/*
creat a player of circle, not sprite is needed in this simple game.
*/

public class Player
{
    // FIELDS
    private int x;
    private int y;
    private int r;

    private int dx;
    private int dy;
    private double speed;

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;

    // bullets firing variables
    private boolean firing;
    private long firingTimer;
    private long firingDelay;

    // player - enemy collision
    private boolean recovering;
    private long recoveryTimer;

    private int lives;
    private int score;

    private Color color1;
    private Color color2;

    // CONSTRUCTOR
    Player()
    {
        x = GamePanel.WIDTH / 2;
        y = GamePanel.HEIGHT /2;
        r = 5;

        dx = 0;
        dy = 0;
        speed = 5.0;

        lives = 3;
        score = 0;
        color1 = Color.WHITE;
        color2 = Color.RED;

        firing = false;
        firingTimer = System.nanoTime(); // set bullet firing timer to start
        firingDelay = 200; // millisecond 5 shots per second

        recovering = false;
        recoveryTimer = 0;
    }

    // SETTERS
    void setLeft(boolean b) {left = b;}
    void setRight(boolean b) {right = b;}
    public void setUp(boolean b)  {up = b;}
    void setDown(boolean b)  {down = b;}
    void addScore(int i) { score += i;}

    boolean setFiring(boolean b) {return firing = b;}

    // GETTERS
    int getx() {return x;}
    int gety() {return y;}
    int getr() {return r;}
    int getLives() {return lives;}
    int getScore() {return score; }

    boolean isRecovering() { return recovering; }


    // FUNCTIONS
    // when collide with an enemy
    void loseLife()
    {
        lives --;
        recovering = true;
        recoveryTimer = System.nanoTime();
    }


    // when keyListener receive the input, update the x and y of the circle
    void update()
    {
        //find where to move to
        if(left) dx -= speed;
        if(right)  dx += speed;
        if(up) dy -= speed;
        if(down) dy += speed;

        // pass the dx and dy to x and y
        x += dx;
        y += dy;

        // set bounds to keep the circle within the panel boundry
        //left bound
        if(x < r) x = r;
        //up bound
        if(y < r ) y = r;
        // right bound
        if ( x > (GamePanel.WIDTH - r)) x = GamePanel.WIDTH - r;
        // bottom bound
        if (y > (GamePanel.HEIGHT -r)) y = GamePanel.HEIGHT - r;

        // reset dx, dy
        dx = 0;
        dy = 0;

        if(firing)
        {
            // check elpased time
            long elpased1 = (System.nanoTime()- firingTimer) /1000000; // millisecond
            if (elpased1 > firingDelay)
            {
                // when time is ready to fire a new bullet
                GamePanel.bullets.add(new Bullet(270, x,y)); // 270 is to up, 90 is to down
                // reset timer
                firingTimer = System.nanoTime();
            }
        }
        // for colide with enemy
        long elpased2 = (System.nanoTime() - recoveryTimer) / 1000000;
        // 2 seconds
        int waitForRecoveryTime = 2000;
        if (elpased2 > waitForRecoveryTime)
        {
            recovering = false;
            recoveryTimer = 0;
        }
    }
    // draw a player
    void draw(Graphics2D g)
    {
        // when collide with enemy, color will changed to color2
        if(recovering)
        {
            g.setColor(color2);
            g.fillOval(x-r, y-r, 2*r, 2*r); // set the oval centered in the panel

            g.setStroke(new BasicStroke(3));
            g.setColor(color2.darker());
            g.drawOval(x-r, y-r, 2*r, 2*r);

            // reset stroke back to 1 after the oval
            g.setStroke(new BasicStroke(1));

        } else
        {
            g.setColor(color1);
            g.fillOval(x-r, y-r, 2*r, 2*r); // set the oval centered in the panel

            g.setStroke(new BasicStroke(3));
            g.setColor(color1.darker());
            g.drawOval(x-r, y-r, 2*r, 2*r);

            // reset stroke back to 1 after the oval
            g.setStroke(new BasicStroke(1));
        }

    }


}
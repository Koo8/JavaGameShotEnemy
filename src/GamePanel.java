import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

public class GamePanel extends JPanel implements Runnable, KeyListener
{
    //FIELDS
    public static int WIDTH = 400;
    public static int HEIGHT = 400;

    private Thread thread;
    private boolean running;

    // this is our canvas to draw image on
    private BufferedImage image;
    // this is our pantbrush
    private Graphics2D g;

    // control update speed
    private int FPS = 30; // frame per second
    private double averageFPS;

    // player
    public static Player player;
    //Bullets
    public static ArrayList<Bullet> bullets;
    // Enemies
    public static ArrayList<Enemy> enemies;

    // CONSTRUCTOR
    public GamePanel()
    {
        super(); // call  JPanel super() constructor // create a new JPanel with a double buffer and a flow layout
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // set game panel focusable, this is important to get keyboard input from the user,
        // set focus in constructor during instantiation stage makes keylistener etc be able to catch the key  movement
        setFocusable(true);
        requestFocus();
    }

    // FUNCTIONS
    @Override
    public void addNotify()
    {
        super.addNotify(); // override the JPanel addNotify method

        // instantiate the thread once
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
        // add keyListener - to listen to input for control player movement
        addKeyListener(this);
    }

    @Override
    public void run()
    {
        running = true;

        // instantiate canvas and paintbrush
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D)image.getGraphics(); // note: this graphics is for offScreen image drawing, check gameDraw to see how to get to game Screen graphics object

        // instantiate a player
        player = new Player();
        //instantiate bullets
        bullets = new ArrayList<Bullet>();
        // instantiate enemies
        enemies = new ArrayList<Enemy>();
        // add 5 enemies by computer
        for (int i = 0; i < 5; i++ )
        {
            enemies.add(new Enemy(1, 1));
        }

        // for controlling update speed
        long startTime; // in nanoSecond
        long URDTimeMillis; // this is Update + Render + Draw time in millisecond
        long waitTime; // in millisecond
        long totalTime = 0; // in nanoSecond
        long targetTime = 1000 / FPS; // the milliseconds for one loop to run to achieve 30 FPS;

        int frameCount = 0;
        int maxFrameCount = 30;

        // Game Loop
        while(running)
        {
            // To run in each frame update, reset at end of each second (when FPS reach maxFPS)
            // for controlling update speed - set the speed limit for the loop
            startTime = System.nanoTime(); // get current time in nano sceond

            // running
            gameUpdate();
            gameRender();
            gameDraw();

            // get Time passed after update + render + draw methods
            URDTimeMillis = (System.nanoTime() - startTime) / 1000000; // 1 nanosecond =1e-6 millisecond
            // after run all the methods, if targetTime is not reached (to maintain the 30 FPS speed), we need to wait till the targetTime is satisfied
            waitTime = targetTime - URDTimeMillis;
            try
            {
                Thread.sleep(waitTime);
            }catch(Exception e) {}

            // add up totalTime
            totalTime += System.nanoTime() - startTime;
            frameCount ++;

            // when FPS reach its max for 1 second,get averageFPS and reset
            if (frameCount == maxFrameCount)
            {
                //******* TODO: not sure why averageFPS is needed?
                averageFPS = 1000.0 / ((totalTime /frameCount)/1000000);
                totalTime = 0;
                frameCount = 0;
            }
        }
    }

    private void gameUpdate()
    {
        // update player
        player.update();

        // remove bullets when they go over the board
        for(int i = 0; i<bullets.size(); i++ )
        {
            boolean toRemove = bullets.get(i).update();
            if(toRemove)
            {
                bullets.remove(i);
                i--;
            }
        }

        // update enemies // set ready // enemies bounce off the walls
        for (int i = 0; i < enemies.size(); i++ )
        {
            enemies.get(i).update();
        }

        // check for collision bullet-enemy
        for( int i = 0; i < bullets.size(); i++ )
        {
            Bullet b = bullets.get(i);
            double bx = b.getx();
            double by = b.gety();
            double br = b.getr();
            for (int j = 0; j < enemies.size(); j++)
            {
                Enemy e = enemies.get(j);
                double ex = e.getx();
                double ey = e.gety();
                double er = e.getr();

                double dx = bx - ex;
                double dy = by - ey;

                double dist = Math.sqrt(dx * dx + dy * dy);

                if ( dist < br + er)
                {
                    e.hit();
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }

        // remove dead enemy from enemyList
        for(int i = 0; i < enemies.size(); i++ )
        {
            if(enemies.get(i).isDead())
            {
                enemies.remove(i);
                i--;
            }
        }



    }

    private void gameRender()
    {
        // use double buffering by Jpanel
        // test Run - draw to offScreen. you need gameDraw() to bring the image to the front game panel to be shown
        g.setColor(new Color(0, 100, 255));
        g.fillRect(0,0,WIDTH,HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("FPS " + averageFPS, 10, 10);
        g.drawString("num bullets " + bullets.size(), 10,20);
        // render a playerj
        player.draw(g);

        // render a list of bullets
        for(int i = 0; i<bullets.size(); i++ )
        {
            bullets.get(i).draw(g);
        }

        // draw enemies
        for (int i = 0; i < enemies.size(); i++ )
        {
            enemies.get(i).draw(g);
        }
    }

    private void gameDraw() // you basically only need this 3 lines for gameDraw always
    {
        // use double buffering
        // draw to game panel (this)
        Graphics g2  = this.getGraphics(); // this graphics is for game  panel drawImage as a whole. You won't see the image is actually drawn bit by bit or line by line
        g2.drawImage(image, 0,0,null);
        g2.dispose();
    }

    // implements 3 methods of keylistener
    public void keyPressed(KeyEvent key)
    {
        // move player with keys
        int keyCode = key.getKeyCode();
        if(keyCode == KeyEvent.VK_LEFT)
            player.setLeft(true);
        if(keyCode == KeyEvent.VK_RIGHT)
            player.setRight(true);
        if(keyCode == KeyEvent.VK_UP)
            player.setUp(true);
        if(keyCode == KeyEvent.VK_DOWN)
            player.setDown(true);

        //fire bullets with keys // this is the time to add bullets to its arrayList
        // NOTE: adding bullets to its list is controlled by player through listener
        if(keyCode == KeyEvent.VK_Z)
            player.setFiring(true);
    }


    public void keyReleased(KeyEvent key)
    {
        int keyCode = key.getKeyCode();
        if(keyCode == KeyEvent.VK_LEFT)
            player.setLeft(false);
        if(keyCode == KeyEvent.VK_RIGHT)
            player.setRight(false);
        if(keyCode == KeyEvent.VK_UP)
            player.setUp(false);
        if(keyCode == KeyEvent.VK_DOWN)
            player.setDown(false);

        if(keyCode == KeyEvent.VK_Z)
            player.setFiring(false);
    }

    public void keyTyped(KeyEvent key)
    {

    }
}

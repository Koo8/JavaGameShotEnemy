import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

public class GamePanel extends JPanel implements Runnable, KeyListener
{
    //FIELDS
    static int WIDTH = 400;
    static int HEIGHT = 400;

    private Thread thread;

    // this is our canvas to draw image on
    private BufferedImage image;
    // this is our pantbrush
    private Graphics2D g;

    /* player */
    private static Player player;
    //Bullets
    static ArrayList<Bullet> bullets;
    // Enemies
    private static ArrayList<Enemy> enemies;
    // PowerUP
    private static ArrayList<PowerUp> powerUps;

    // Wave Spawning System
    private long waveStartTimer;
    private long waveStartTimerDiff; // track time passed by and compare with waveDelay requirement
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000; // 2 seconds delay


    // CONSTRUCTOR
    GamePanel()
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
//        boolean running = true;

        // instantiate canvas and paintbrush
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D)image.getGraphics(); // note: this graphics is for offScreen image drawing, check gameDraw to see how to get to game Screen graphics object
        // aa for the graphics
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // instantiate a player
        player = new Player();
        //instantiate bullets
        bullets = new ArrayList<>();
        // instantiate enemies
        enemies = new ArrayList<>();
        // for testing add 5 enemies by computer
        //for (int i = 0; i < 5; i++ )
        //{
        //	enemies.add(new Enemy(1, 1));
        //}

        // Instantiate powerups //
        powerUps = new ArrayList<>();

        // instantiate enemy wave spawning system
        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true; // or false is fine
        waveNumber = 0;

        // for controlling update speed
        long startTime; // in nanoSecond
        long URDTimeMillis; // this is Update + Render + Draw time in millisecond
        long waitTime; // in millisecond
        long totalTime = 0; // in nanoSecond
        // control update speed
        // frame per second
        int FPS = 30;
        long targetTime = 1000 / FPS; // the milliseconds for one loop to run to achieve 30 FPS;

        int frameCount = 0;
        int maxFrameCount = 30;

        // Game Loop
        while(true)
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
            }catch(Exception ignored) {}

            // add up totalTime
            totalTime += System.nanoTime() - startTime;
            frameCount ++;

            // when FPS reach its max for 1 second,get averageFPS and reset
            if (frameCount == maxFrameCount)
            {
                //******* TODO: not sure why averageFPS is needed?
                double averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000.0);
                totalTime = 0;
                frameCount = 0;
            }
        }
    }

    // create waves, players, enemies, bullets, remove bullets, enemies, define collisions

    private void gameUpdate()
    {
        // new Wave - each time to reset timer and after all enemies died
        if (waveStartTimer == 0 && enemies.size() == 0) // game hasn't start yet && no enemy has been created
        {
            waveNumber ++; // waveNumber starts to count from 1;
            waveStart = false; // don't create enemy yet, give time to show "WAVE 1" on screen with waveDelay set up
            waveStartTimer = System.nanoTime(); //wave timer start
        } else
        {
            waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000 ; // in millisecond
            if (waveStartTimerDiff >= waveDelay)  // if waited for 2 second
            {
                waveStart = true;
                waveStartTimer =0; // timer reset to 0, wait all enemies die before start another wave
                waveStartTimerDiff = 0;
            }
        }

        //create enemies according to waveStart
        if(waveStart && enemies.size() == 0) // enemies are created together, wait till all enemies are died before the new wave of creating enemies
        {
            createNewEnemies();
        }

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
        for (Enemy enemy : enemies) {
            enemy.update();
        }

        // powerUps updates
        for( int i = 0; i < powerUps.size(); i++ )
        {
            boolean remove = powerUps.get(i).update();
            if(remove)
            {
                powerUps.remove(i);
                i--;
            }
        }

        // check for collision bullet-enemy
        for( int i = 0; i < bullets.size(); i++ )
        {
            Bullet b = bullets.get(i);
            double bx = b.getx();
            double by = b.gety();
            double br = b.getr();
            for (Enemy e : enemies) {
                double ex = e.getx();
                double ey = e.gety();
                double er = e.getr();

                double dx = bx - ex;
                double dy = by - ey;

                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < br + er) {
                    e.hit();
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }

        // remove dead enemy from enemyList // and meanwhile drop randomly a powerUp square
        for(int i = 0; i < enemies.size(); i++ )
        {
            Enemy e = enemies.get(i);
            if(e.isDead())
            {
                // by chances, to produce powerup squares when enemy dies
                double rand = Math.random();
                if (rand < 0.001)
                    powerUps.add(new PowerUp(1, e.getx(), e.gety()));
                else if (rand < 0.02) powerUps.add(new PowerUp(3, e.getx(), e.gety()));
                else if (rand < 0.12) powerUps.add(new PowerUp(2, e.getx(), e.gety()));

                player.addScore(e.getType() + e.getRank());
                enemies.remove(i);
                i--;
            }
        }

        // player-enemy collision
        if( !player.isRecovering() )  // when not get hit by enemy
        {
            double px = player.getx();
            double py = player.gety();
            double pr = player.getr();

            for (Enemy enemy : enemies) {
                double ex = enemy.getx();
                double ey = enemy.gety();
                double er = enemy.getr();

                double dx = px - ex;
                double dy = py - ey;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < pr + er) player.loseLife();
            }
        }
    }

    // draw everything onto the screen

    private void gameRender()
    {
        // use double buffering by Jpanel
        // test Run - draw to offScreen. you need gameDraw() to bring the image to the front game panel to be shown

        // draw background
        g.setColor(new Color(0, 100, 255));
        g.fillRect(0,0,WIDTH,HEIGHT);


        // render a playerj
        player.draw(g);

        // render a list of bullets
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }

        // draw enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        // draw powerups
        for (PowerUp powerUp : powerUps) {
            powerUp.draw(g);
        }

        // draw wave number notification
        if(waveStartTimer != 0)
        {
            g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
            String s = "- W A V E   " + waveNumber + "   -";
            // string s pixel length
            int length = (int) g.getFontMetrics().getStringBounds(s,g).getWidth();
            // make the string sign fade out using sine - pulsate in and out effect, using alpha transparent
            double alphaIndex = Math.sin(3.14 * waveStartTimerDiff / waveDelay);  // the alpha turn from 255 to 0 following sin from 0 to 3.14
            int alpha = (int) (255 * alphaIndex);
            // make sure alpha is kept within 255;
            if(alpha > 255) alpha = 255;
            g.setColor(new Color(255, 255, 255, alpha));
            g.drawString(s, WIDTH/2 - length/2, HEIGHT/2);
            // g.drawString("" + alphaIndex, 10,10);
        }

        // draw player lives
        for (int i = 0; i < player.getLives(); i++)
        {
            // draw player circle
            g.setColor(Color.WHITE);
            g.fillOval(20 + (20 * i), 20, player.getr() *2, player.getr() *2);
            // draw player circle boundry
            g.setStroke(new BasicStroke(3));
            g.setColor(Color.WHITE.darker());
            g.drawOval(20 + (20 * i), 20, player.getr() *2, player.getr() *2);
            // reset stroke
            g.setStroke(new BasicStroke(1));
        }

        // draw player score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
        g.drawString("Score: " + player.getScore(), WIDTH - 100,30);
    }

    private void gameDraw() // you basically only need this 3 lines for gameDraw always
    {
        // use double buffering
        // draw to game panel (this)
        Graphics g2  = this.getGraphics(); // this graphics is for game  panel drawImage as a whole. You won't see the image is actually drawn bit by bit or line by line
        g2.drawImage(image, 0,0,null);
        g2.dispose();
    }

    private void createNewEnemies()
    {
        // for a sanity check, clear enemies arrayList
        enemies.clear();
        //Enemy e;

        if(waveNumber == 1)
        {
            for(int i =0; i<4;i++)
            {
                enemies.add(new Enemy(1,1));
            }
        }
        if (waveNumber == 2)
        {
            for(int i =0; i<8;i++)
            {
                enemies.add(new Enemy(1,1));
            }
        }
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

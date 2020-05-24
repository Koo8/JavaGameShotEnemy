import javax.swing.JFrame;

/** This is the main class to run the game
This only contains a main method **/

public class Game
{
	public static void main (String[] args)
	{
		// set up the window for drawing
		JFrame window = new JFrame("First Game");

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		window.setContentPane(new GamePanel());

		window.pack(); // this set window size to whatever it is

		window.setVisible(true);
	}
}

import javax.swing.*;

/** 
 * The DriverJSON class instantiates a JFrame 
 * with a JPanel DisplayPanel on it. 
 */
public class DriverJSON {
	private static JFrame frame;
	public static void main(String[] args) {
		/*
		 * Instantiates a JFrame labeled "JSON Editor" with
		 * specified position and size, with a DisplayPanel JPanel on it.
		 */
		frame = new JFrame("JSON Editor"); 
		frame.setLocation(400, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200,  700);
		frame.setContentPane(new DisplayPanel(frame));	
		frame.setVisible(true);

	}

}

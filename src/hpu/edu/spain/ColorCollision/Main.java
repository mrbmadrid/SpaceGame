package hpu.edu.spain.ColorCollision;
import javax.swing.JFrame;

public class Main {
	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 500);
		frame.setResizable(true);
		frame.add(new ColorCollision());
		frame.setVisible(true);
	}
}

package edu.hpu.spain.Game;
import javax.swing.JFrame;

public class Main {
	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 500);
		frame.setResizable(true);
		frame.add(new SpaceGame());
		frame.setVisible(true);
	}
}

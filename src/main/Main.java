package main;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import view.MainFrame;

/**
 * 
 * Main class ,which is the entry of the program,the main method creates GUI and
 * runs it.
 * 
 */
public class Main {
	// create a static file to edit or play.(for other three frames to use)
	public static File file;

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {

					createGUI();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public static void createGUI() throws IOException {
		MainFrame mainFrame = new MainFrame();
	}
}

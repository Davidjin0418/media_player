package se206_a03;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

public class Main {
	//create a static method to edit or play.(for other three frames to use)
	 public static File file;
	 public static void main(String[] args){
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
		public static void createGUI() throws IOException{
			MainFrame mainFrame=new MainFrame();
		}
}
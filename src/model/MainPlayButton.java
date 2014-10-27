package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JTextField;

import control.FileControl;
import main.Main;
import view.PlayFrame;
/**
 * 
 * the button that opens the play frame 
 *
 */
public class MainPlayButton extends JButton implements ActionListener {
	JTextField currentFIle;
	/**
	 * 
	 * @param field the text field that displays the current file in main frame
	 */
	public MainPlayButton(JTextField field) {
		currentFIle=field;
		this.setText("Play");
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this) {
			if (Main.file != null) {
				try {
					FileControl.writeToHistory();

					PlayFrame playframe = new PlayFrame(this,currentFIle);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				this.setEnabled(false);
			}
		}

	}

}

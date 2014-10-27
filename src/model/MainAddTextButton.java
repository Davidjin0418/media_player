package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import main.Main;
import view.TextFrame;
import control.FileControl;

/**
 * 
 * The button in the main frame which create the text frame
 * 
 */
public class MainAddTextButton extends JButton implements ActionListener {

	public MainAddTextButton() {

		setText("Add Text");
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this) {
			if (Main.file != null) {
				// check if the file is video file
				if (FileControl.isAudioVideoFile(Main.file).equals("video")) {
					TextFrame textframe = new TextFrame(this);
					this.setEnabled(false);
				} else {
					JOptionPane
							.showMessageDialog(
									null,
									"ERROR: "
											+ Main.file.getName()
											+ " is an audio file, can't apply text onto it");
				}
			}
		}

	}
}

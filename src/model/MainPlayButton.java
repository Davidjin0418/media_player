package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;

import main.Main;
import view.PlayFrame;

public class MainPlayButton extends JButton implements ActionListener {
	public MainPlayButton() {
		this.setText("Play");
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this) {
			if (Main.file != null) {
				try {
					String root = System.getProperty("user.home");
					File history = new File(root + "/.vamix/history.txt");
					// write to history
					if (!history.exists()) {
						history.createNewFile();
					}
					FileWriter fw = new FileWriter(history, true);
					fw.write(Main.file.getAbsolutePath() + "\n");
					fw.close();

					PlayFrame playframe = new PlayFrame(this);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				this.setEnabled(false);
			}
		}

	}

}

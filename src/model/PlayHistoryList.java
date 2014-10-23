package model;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import main.Main;

public class PlayHistoryList extends JList {
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
    public PlayHistoryList(EmbeddedMediaPlayerComponent player) throws IOException{
    	mediaPlayerComponent=player;
    	this.setBorder(null);
    	this.setBackground(Color.LIGHT_GRAY);
    	
    	BufferedReader in = null;
		String line;
		@SuppressWarnings("rawtypes")
		DefaultListModel listModel = new DefaultListModel();
		try {
			String root = System.getProperty("user.home");
			in = new BufferedReader(
					new FileReader(root + "/.vamix/history.txt"));
			while ((line = in.readLine()) != null) {
				listModel.addElement(line);
			}
		} catch (IOException ex) {

		} finally {
			if (in != null) {
				in.close();
			}
		}
		this.setModel(listModel);
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				@SuppressWarnings("rawtypes")
				JList list = (JList) evt.getSource();
				if (evt.getClickCount() == 2) {
					Main.file = new File(list.getSelectedValue().toString());
					if (Main.file != null) {
						mediaPlayerComponent.getMediaPlayer().playMedia(
								Main.file.getAbsolutePath());
					} else {
						JOptionPane.showMessageDialog(null,
								"Can not find the file");
					}
				}
			}
		});

    }
}

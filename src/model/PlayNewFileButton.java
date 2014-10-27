package model;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import control.FileControl;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import view.PlayFrame;

/**
 * The button that opens a new file in the play frame
 * 
 */
public class PlayNewFileButton extends JButton implements ActionListener {
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private PlayFrame frame;

	/**
	 * 
	 * @param player
	 *            the media player component
	 * @param f
	 *            the play frame
	 */
	public PlayNewFileButton(EmbeddedMediaPlayerComponent player, PlayFrame f) {
		mediaPlayerComponent = player;
		frame = f;
		this.setIcon(new ImageIcon(PlayFrame.class
				.getResource("/se206_a03/png/open140.png")));
		this.addActionListener(this);
		this.setForeground(Color.LIGHT_GRAY);
		this.setVerticalTextPosition(AbstractButton.BOTTOM);
		this.setHorizontalTextPosition(AbstractButton.CENTER);
		this.setContentAreaFilled(false);
		this.setBorder(new EmptyBorder(0, 0, 0, 10));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this) {
			try {
				// choose a new file
				FileControl.chooseNewMediaFile(mediaPlayerComponent, frame);
			} catch (IOException ex) {

				ex.printStackTrace();
			}

		}
	}

}

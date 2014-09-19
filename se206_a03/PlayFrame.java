package se206_a03;



import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

import java.awt.Button;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;


public class PlayFrame extends JFrame {

	private JPanel contentPane;
	private final JPanel panel = new JPanel();
	//problem loading video,need to fix it on a Linux machine;
	//private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JButton btnPause;
	private JButton btnForward;
	private JButton btnBackward;
	private JButton btnPlay;

	/**
	 * Create the frame.
	 */
	public PlayFrame() {
		//mediaPlayerComponent =new EmbeddedMediaPlayerComponent();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//setContentPane(mediaPlayerComponent);
		//mediaPlayerComponent.getMediaPlayer().playMedia(Main.file.getAbsolutePath());
		contentPane.setLayout(null);
		panel.setBounds(0, 0, this.getWidth(), this.getHeight()-100);
		contentPane.add(panel);
		
		btnPlay = new JButton("Play",new ImageIcon(PlayFrame.class.getResource("/se206_a03/png/play43.png")));
		btnPlay.setBounds(10,this.getHeight()-100, 70, 70);
		btnPlay.setVerticalTextPosition(AbstractButton.BOTTOM);
	    btnPlay.setHorizontalTextPosition(AbstractButton.CENTER);
		contentPane.add(btnPlay);
		
		btnPause = new JButton("Pause",new ImageIcon(PlayFrame.class.getResource("/se206_a03/png/pause15.png")));
		btnPause.setVerticalTextPosition(AbstractButton.BOTTOM);
	    btnPause.setHorizontalTextPosition(AbstractButton.CENTER);
		btnPause.setBounds(80, this.getHeight()-100, 70, 70);
		contentPane.add(btnPause);
		
		btnForward = new JButton("Forward",new ImageIcon(PlayFrame.class.getResource("/se206_a03/png/next10.png")));
		btnForward.setVerticalTextPosition(AbstractButton.BOTTOM);
	    btnForward.setHorizontalTextPosition(AbstractButton.CENTER);
		btnForward.setBounds(150,this.getHeight()-100, 70, 70);
		contentPane.add(btnForward);
		
		btnBackward = new JButton("Backward",new ImageIcon(PlayFrame.class.getResource("/se206_a03/png/back12.png")));
		btnBackward.setVerticalTextPosition(AbstractButton.BOTTOM);
	    btnBackward.setHorizontalTextPosition(AbstractButton.CENTER);
		btnBackward.setBounds(220,this.getHeight()-100, 70, 70);
		contentPane.add(btnBackward);
		this.setVisible(true);
	}
}

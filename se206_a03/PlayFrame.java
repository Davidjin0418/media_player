package se206_a03;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.ImageIcon;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class PlayFrame extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JPanel panel;
	// problem loading video,need to fix it on a Linux machine;
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JButton btnPause;
	private JButton btnForward;
	private JButton btnBackward;
	private JButton btnPlay;
	private JButton btnStrip;
	private JButton btnMute;
	private JButton btnOverlay;
	private JButton btnReplace;

	/**
	 * Create the frame.
	 */
	public PlayFrame() {
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		this.setVisible(true);
		this.setTitle(Main.file.getName());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 400);
		contentPane = new JPanel();
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		panel.setBounds(0, 0, this.getWidth(), this.getHeight() - 100);
		btnPlay = new JButton("Play", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/play43.png")));
		btnPlay.setBounds(10, this.getHeight() - 100, 70, 70);
		btnPlay.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnPlay.setHorizontalTextPosition(AbstractButton.CENTER);
		btnPlay.addActionListener(this);
		contentPane.add(btnPlay);

		btnPause = new JButton("Pause", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/pause15.png")));
		btnPause.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnPause.setHorizontalTextPosition(AbstractButton.CENTER);
		btnPause.setBounds(90, this.getHeight() - 100, 70, 70);
		btnPause.addActionListener(this);
		contentPane.add(btnPause);

		btnForward = new JButton("Forward", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/next10.png")));
		btnForward.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnForward.setHorizontalTextPosition(AbstractButton.CENTER);
		btnForward.setBounds(170, this.getHeight() - 100, 70, 70);
		btnForward.addActionListener(this);
		contentPane.add(btnForward);

		btnBackward = new JButton("Backward", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/back12.png")));
		btnBackward.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnBackward.setHorizontalTextPosition(AbstractButton.CENTER);
		btnBackward.setBounds(260, this.getHeight() - 100, 70, 70);
		btnBackward.addActionListener(this);
		contentPane.add(btnBackward);

		btnStrip = new JButton("Strip");
		btnStrip.addActionListener(this);
		btnStrip.setBounds(350, this.getHeight() - 100, 70, 70);
		contentPane.add(btnStrip);

		btnMute = new JButton("Mute");
		btnMute.addActionListener(this);
		btnMute.setBounds(440, this.getHeight() - 100, 70, 70);
		contentPane.add(btnMute);

		btnOverlay = new JButton("Overlay");
		btnOverlay.addActionListener(this);
		btnOverlay.setBounds(530, this.getHeight() - 100, 70, 70);
		contentPane.add(btnOverlay);

		btnReplace = new JButton("Replace");
		btnReplace.addActionListener(this);
		btnReplace.setBounds(620, this.getHeight() - 100, 80, 70);
		contentPane.add(btnReplace);

		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		panel.setVisible(true);
		panel.add(mediaPlayerComponent, BorderLayout.CENTER);
		contentPane.add(panel);
		setContentPane(contentPane);
		// panel.setBackground(Color.black);
		mediaPlayerComponent.getMediaPlayer().playMedia(
				Main.file.getAbsolutePath());
	}
    public boolean fileExist(String s){
    	File f=new File(s);
    	if (f.exists()){
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    public boolean 
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnPlay) {
			//play the media.
			mediaPlayerComponent.getMediaPlayer().play();
		} else if (e.getSource() == btnPause) {
			//pause the media.
			mediaPlayerComponent.getMediaPlayer().pause();
		} else if (e.getSource() == btnForward) {
			// need to keep forward or backward
			mediaPlayerComponent.getMediaPlayer().skip(10000);
		} else if (e.getSource() == btnBackward) {
			mediaPlayerComponent.getMediaPlayer().skip(-10000);
		} else if (e.getSource() == btnMute) {
			mediaPlayerComponent.getMediaPlayer().mute();
		} else if (e.getSource() == btnOverlay) {
               
		} else if (e.getSource() == btnReplace) {
             
		} else if (e.getSource() == btnStrip) {
			String outputFileName = JOptionPane
					.showInputDialog("Please enter the output file name");
			// get from http://www.rgagnon.com/javadetails/java-0370.html choose
			// a directory to save file
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Chose a diretory to save audio file");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			//
			// disable the "All files" option.
			//
			chooser.setAcceptAllFileFilterUsed(false);
			//
			String path = "";
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				path = chooser.getSelectedFile().getAbsolutePath();
				String cmd = "avconv -i " + Main.file +" "
						+ path + "/" + outputFileName;
				ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
				pb.redirectErrorStream(true);
				try {
					Process process = pb.start();
					int exit = process.waitFor();
					if (exit == 0) {
						JOptionPane
						.showMessageDialog(this,
								"Strip successfully");
						//choose to play the original file or striped file
						
						
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
	}
}

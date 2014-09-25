package se206_a03;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JButton;
import javax.swing.ImageIcon;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;

import java.awt.Font;

import javax.swing.JSlider;

public class PlayFrame extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JPanel panel;
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JButton btnPause;
	private JButton btnForward;
	private JButton btnBackward;
	private JButton btnPlay;
	private JButton btnMute;
	private JButton btnEdit;
	private JSlider time;
	private JSlider volume;
	private JProgressBar editProgressBar;
	private BackwardFarwardWorker skipworker;
	private VideoWorker videoworker;
	private Timer timer;
	private JButton btnMainFramePlay;
	
	private boolean isAutomaticSlide = false;

	/**
	 * Create the frame.
	 * @param btnPlay2 
	 */
	public PlayFrame(JButton btn) {
		
		btnMainFramePlay = btn;
		//initialize the workers
		videoworker=new VideoWorker("");
		videoworker.cancel(true);
		skipworker = new BackwardFarwardWorker(0);
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		this.setVisible(true);
		this.setTitle(Main.file.getName());

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 791, 450);
		contentPane = new JPanel();
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		panel.setBounds(0, 0, this.getWidth(), this.getHeight() - 100);
		btnPlay = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/play43.png")));
		btnPlay.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnPlay.setBounds(10, this.getHeight() - 100, 70, 70);
		btnPlay.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnPlay.setHorizontalTextPosition(AbstractButton.CENTER);
		btnPlay.addActionListener(this);
		contentPane.add(btnPlay);

		btnPause = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/pause15.png")));
		btnPause.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnPause.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnPause.setHorizontalTextPosition(AbstractButton.CENTER);
		btnPause.setBounds(90, this.getHeight() - 100, 70, 70);
		btnPause.addActionListener(this);
		contentPane.add(btnPause);

		btnForward = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/next10.png")));
		btnForward.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnForward.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnForward.setHorizontalTextPosition(AbstractButton.CENTER);
		btnForward.setBounds(170, this.getHeight() - 100, 70, 70);
		btnForward.addActionListener(this);
		contentPane.add(btnForward);

		btnBackward = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/back12.png")));
		btnBackward.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnBackward.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnBackward.setHorizontalTextPosition(AbstractButton.CENTER);
		btnBackward.setBounds(249, 350, 70, 70);
		btnBackward.addActionListener(this);
		contentPane.add(btnBackward);

		btnMute = new JButton("Mute");
		btnMute.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnMute.addActionListener(this);
		btnMute.setBounds(331, 350, 70, 70);
		contentPane.add(btnMute);

		btnEdit = new JButton("Edit Video");
		btnEdit.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnEdit.addActionListener(this);
		btnEdit.setBounds(516, 365, 70, 20);
		contentPane.add(btnEdit);

		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		panel.setVisible(true);
		panel.add(mediaPlayerComponent, BorderLayout.CENTER);
		contentPane.add(panel);
        
		
		setContentPane(contentPane);

		volume = new JSlider();
		volume.setMaximum(200);
		volume.setValue(50);
		volume.setBounds(400, 374, 118, 29);
		volume.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mediaPlayerComponent.getMediaPlayer().setVolume(
						volume.getValue());

			}
		});
		contentPane.add(volume);

		mediaPlayerComponent.getMediaPlayer().playMedia(
				Main.file.getAbsolutePath());
		
		time = new JSlider();
         
		// get time of media
		mediaPlayerComponent.getMediaPlayer().parseMedia();
		time.setMaximum((int) mediaPlayerComponent.getMediaPlayer().getMediaMeta().getLength() );
		time.setMinimum(0);
		
		time.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
			if (isAutomaticSlide == false) {
				mediaPlayerComponent.getMediaPlayer().setTime(
					time.getValue());
			}
			else {
				isAutomaticSlide = false;
			}
			}
		});
		
		panel.add(time, BorderLayout.SOUTH);
		editProgressBar = new JProgressBar();
		editProgressBar.setBounds(598, 365, 146, 20);
		contentPane.add(editProgressBar);
        timer=new Timer(1000,new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(time.getValue()<time.getMaximum()){
					time.setValue((int)mediaPlayerComponent.getMediaPlayer().getTime());
					isAutomaticSlide = true;
				}
			}
        	
        });
        
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				btnMainFramePlay.setEnabled(true);
				mediaPlayerComponent.getMediaPlayer().stop();
				videoworker.cancel(true);
			}
		});
      
		timer.start();
	}

	// save a file
	public String chooseOutputFileName() {
		String path = choosePath();
		String outputFileName = JOptionPane
				.showInputDialog("Please enter the output file name");
		File f = new File(path + "/" + outputFileName);
		// check if file exists
		if (f.exists()) {
			String[] options = { "Yes", "Cancel" };
			int selection = JOptionPane.showOptionDialog(null, outputFileName
					+ " exists,Do you want to override it?", "Warning",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			if (selection == 0) {
				return path + "/" + outputFileName;
			} else {
				JOptionPane.showMessageDialog(this,
						"Please choose another output file name");
				this.chooseOutputFileName();
				return null;
			}
		} else {
			return path + "/" + outputFileName;
		}
	}

	// choose the directory in order to save the file
	public String choosePath() {
		// from http://www.rgagnon.com/javadetails/java-0370.html
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Chose a diretory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//
		// disable the "All files" option.
		//
		chooser.setAcceptAllFileFilterUsed(false);
		// path default to root directory
		String path = "/";
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			path = chooser.getSelectedFile().getAbsolutePath();
		}
		return path;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnPlay) {
			// play the media.
			cancelWorker();
			mediaPlayerComponent.getMediaPlayer().play();
		} else if (e.getSource() == btnPause) {
			// pause the media.
			cancelWorker();
			mediaPlayerComponent.getMediaPlayer().pause();
		} else if (e.getSource() == btnForward) {
			cancelWorker();
			skipworker = new BackwardFarwardWorker(0);
			skipworker.execute();
		} else if (e.getSource() == btnBackward) {
			cancelWorker();
			skipworker = new BackwardFarwardWorker(1);
			skipworker.execute();
		} else if (e.getSource() == btnMute) {
			mediaPlayerComponent.getMediaPlayer().mute();
			volume.setValue(0);
		} else if (e.getSource() == btnEdit) {
			if (MainFrame.isAudioVideoFile(Main.file).equals("audio")) {
				JOptionPane
						.showMessageDialog(this, "Can not edit a audio file");
			} else {
				editProgressBar.setMinimum(0);
				editProgressBar.setMaximum(100);
				if(!videoworker.isDone()){
					String[] options = { "Yes", "No" };
					int selection = JOptionPane.showOptionDialog(null,
							"Video is being edited ,would you like to cancel it?", "Warning",
							JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
							null, options, options[0]);
					if(selection==0){
						editVideo();
					}else{
						//do nothing
					}
				}else{
				editVideo();
				}

			}
		}
	}

	private void editVideo() {
		String[] options = { "Overlay", "Replace", "Strip","Extract" };
		int selection = JOptionPane.showOptionDialog(null,
				"Which one would you like to do", "Option",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		String cmd = "";
		if (selection == 0) {
			try {
				// overlay
				String inputFile = chooseInputAudioFile();
				String outputFile = chooseOutputFileName();
				cmd = "avconv -y -i "
						+ Main.file.getAbsolutePath()
						+ " -i "
						+ inputFile
						+ " -filter_complex amix=inputs=2 -strict experimental "
						+ outputFile+".mp4";
				System.out.println(cmd);
				videoworker = new VideoWorker(cmd);
				videoworker.execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (selection == 1) {
			// replace
			try {
				String inputFile = chooseInputAudioFile();
				String outputFile = chooseOutputFileName();
				cmd = "avconv -y -i "
						+ inputFile
						+ " -i "
						+ Main.file.getAbsolutePath()
						+ " -map 0:0 -map 1:0 -acodec copy -vcodec copy -shortest "
						+ outputFile+".mp4";
				videoworker = new VideoWorker(cmd);
				videoworker.execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (selection == 2) {
			// check if the audio signals exists
			if (mediaPlayerComponent.getMediaPlayer()
					.getAudioTrackCount() == 0) {
				JOptionPane.showMessageDialog(this,
						"No audio signal exists");

			} else {
               //Strip doesn't save audio file
				String output = chooseOutputFileName();
				cmd = "avconv -i " + Main.file + " -an " + output+".mp4";
				videoworker=new VideoWorker(cmd);
				videoworker.execute();
			}
		}else if(selection==3){
			String outputFileName = chooseOutputFileName();
			cmd = "avconv -i " + Main.file + " " + outputFileName+".mp3";
			videoworker=new VideoWorker(cmd);
			videoworker.execute();
		}
	}

	// using file chooser ,can only choose file with extension mp3
	public String chooseInputAudioFile() throws IOException {

		final JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Audio/mp3",
				"mp3"));
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showOpenDialog(PlayFrame.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			if (MainFrame.isAudioVideoFile(file).equals("audio")) {
				return file.getAbsolutePath();
			} else {
				JOptionPane.showMessageDialog(this, "ERROR: " + file.getName()
						+ " does not refer to a valid audio file");
				chooseInputAudioFile();

			}
		}
		return null;

	}

	

	// strip
	// backward forward
	public class BackwardFarwardWorker extends SwingWorker<Void, Void> {
		int options;

		// options:0 for forward ,1 for backward.
		public BackwardFarwardWorker(int option) {
			options = option;
		}

		@Override
		protected Void doInBackground() throws Exception {
			if (options == 0) {
				while (!this.isCancelled()) {
					Thread.sleep(100);
					mediaPlayerComponent.getMediaPlayer().skip(500);
				}
			} else if (options == 1) {
				while (!this.isCancelled()) {
					Thread.sleep(100);
					mediaPlayerComponent.getMediaPlayer().skip(-500);
				}
			}
			return null;
		}

	}
    //method to cancel skipworker
	public void cancelWorker() {
		if (!skipworker.isCancelled()) {
			skipworker.cancel(true);
		}
	}

	public class VideoWorker extends SwingWorker<Void, Integer> {
		private String _cmd;

		public VideoWorker(String cmd) {
			_cmd = cmd;
		}

		protected void process(List<Integer> chunks) {
			if (!isCancelled()) {
				for (int i : chunks) {
					editProgressBar.setValue(i);
				}
			}
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", _cmd);

			pb.redirectErrorStream(true);
			Process process;
			if (!isCancelled()) {
				for (int i = 0; i <= 100; i++) {
					process = pb.start();
					publish(i);
					
				}
			}
			return null;
		}

	}

}

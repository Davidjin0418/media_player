package se206_a03;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private JLabel lblProgress;
	private boolean volumeEnable;

	/**
	 * Create the frame.
	 */
	public PlayFrame() {
		// initialize the workers
		videoworker = new VideoWorker("");
		videoworker.cancel(true);
		skipworker = new BackwardFarwardWorker(0);
		//for vlcj
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		
		//set frame
		this.setVisible(true);
		this.setTitle(Main.file.getName());

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		panel = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		btnPlay = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/play43.png")));
		btnPlay.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnPlay.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnPlay.setHorizontalTextPosition(AbstractButton.CENTER);
		btnPlay.addActionListener(this);
		SpringLayout sl_contentPane = new SpringLayout();
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnPlay, -70,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, panel, -100,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnPlay, 0,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, panel, 0,
				SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, panel, 0,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, panel, 0,
				SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnPlay, 10,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnPlay, 80,
				SpringLayout.WEST, contentPane);
		contentPane.setLayout(sl_contentPane);
		contentPane.add(btnPlay);

		btnPause = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/pause15.png")));
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnPause, -70,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnPause, 90,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnPause, 0,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnPause, 160,
				SpringLayout.WEST, contentPane);
		btnPause.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnPause.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnPause.setHorizontalTextPosition(AbstractButton.CENTER);
		btnPause.addActionListener(this);
		contentPane.add(btnPause);

		btnForward = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/next10.png")));
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnForward, -70,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnForward, 170,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnForward, 0,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnForward, 240,
				SpringLayout.WEST, contentPane);
		btnForward.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnForward.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnForward.setHorizontalTextPosition(AbstractButton.CENTER);
		btnForward.addActionListener(this);
		contentPane.add(btnForward);

		btnBackward = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/back12.png")));
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnBackward, -70,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnBackward, 249,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnBackward, 0,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnBackward, 319,
				SpringLayout.WEST, contentPane);
		btnBackward.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnBackward.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnBackward.setHorizontalTextPosition(AbstractButton.CENTER);
		btnBackward.addActionListener(this);
		contentPane.add(btnBackward);

		btnMute = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/muted.png")));
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnMute, -70,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnMute, 331,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnMute, 0,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnMute, 401,
				SpringLayout.WEST, contentPane);
		btnMute.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnMute.addActionListener(this);
		contentPane.add(btnMute);

		btnEdit = new JButton("Edit ");
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnEdit, -70,
				SpringLayout.SOUTH, btnPlay);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnEdit, 520,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnEdit, -50,
				SpringLayout.SOUTH, contentPane);
		btnEdit.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnEdit.addActionListener(this);
		contentPane.add(btnEdit);

		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		panel.setVisible(true);
		SpringLayout sl_panel = new SpringLayout();
		sl_panel.putConstraint(SpringLayout.NORTH, mediaPlayerComponent, 0,
				SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, mediaPlayerComponent, 0,
				SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, mediaPlayerComponent, 0,
				SpringLayout.EAST, panel);
		panel.setLayout(sl_panel);
		panel.add(mediaPlayerComponent);
		contentPane.add(panel);

		setContentPane(contentPane);

		volume = new JSlider();
		sl_contentPane.putConstraint(SpringLayout.NORTH, volume, -50,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, volume, 400,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, volume, -25,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, volume, 518,
				SpringLayout.WEST, contentPane);
		volume.setMaximum(200);
		volume.setValue(50);
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
		sl_panel.putConstraint(SpringLayout.NORTH, time, -5,
				SpringLayout.SOUTH, mediaPlayerComponent);
		sl_panel.putConstraint(SpringLayout.SOUTH, mediaPlayerComponent, -20,
				SpringLayout.SOUTH, time);
		sl_panel.putConstraint(SpringLayout.SOUTH, time, 0, SpringLayout.SOUTH,
				panel);
		sl_panel.putConstraint(SpringLayout.EAST, time, 0, SpringLayout.EAST,
				panel);
		time.setPaintLabels(true);
		sl_panel.putConstraint(SpringLayout.WEST, time, 0, SpringLayout.WEST,
				panel);

		// get time of media
		mediaPlayerComponent.getMediaPlayer().parseMedia();
		time.setMaximum((int) mediaPlayerComponent.getMediaPlayer()
				.getMediaMeta().getLength());
		time.setMinimum(0);

		time.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				mediaPlayerComponent.getMediaPlayer().setTime(time.getValue());
			}
		});

		panel.add(time);
		editProgressBar = new JProgressBar();
		editProgressBar.setStringPainted(true);
		sl_contentPane.putConstraint(SpringLayout.NORTH, editProgressBar, -45,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, editProgressBar, 600,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, editProgressBar, 0,
				SpringLayout.SOUTH, volume);
		contentPane.add(editProgressBar);

		lblProgress = new JLabel("Progress:");
		sl_contentPane.putConstraint(SpringLayout.WEST, lblProgress, 6,
				SpringLayout.EAST, volume);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblProgress, -197,
				SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, lblProgress, 0,
				SpringLayout.SOUTH, volume);
		contentPane.add(lblProgress);
		timer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (time.getValue() < time.getMaximum()) {
					time.setValue((int) mediaPlayerComponent.getMediaPlayer()
							.getTime());
				}
			}

		});
		volumeEnable = true;
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				// if the media is muted before closing ,set mute to false
				// otherwise it would be in mute when
				// open it next time.
				if (mediaPlayerComponent.getMediaPlayer().isMute()) {
					mediaPlayerComponent.getMediaPlayer().mute(false);
				}
				mediaPlayerComponent.getMediaPlayer().stop();
				videoworker.cancel(true);
			}
		});

		timer.start();
	}

	// choose output file name to save it,default by "output"
	public String chooseOutputFileName() {
		String path = choosePath();
		String outputFileName = JOptionPane
				.showInputDialog("Please enter the output file name","output");

		File f1 = new File(path + "/" + outputFileName+".mp3");
		File f2 = new File(path + "/" + outputFileName+".mp4");
		// check if file exists
		if (f1.exists() || f2.exists()) {
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
		chooser.setDialogTitle("Choose a diretory to save file");
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
			volumeEnable = !volumeEnable;
			volume.setEnabled(volumeEnable);
		} else if (e.getSource() == btnEdit) {
			if (MainFrame.isAudioVideoFile(Main.file).equals("audio")) {
				JOptionPane
						.showMessageDialog(this, "Can not edit a audio file");
			} else {
				editProgressBar.setMinimum(0);
				editProgressBar.setMaximum(100);
				if (!videoworker.isDone()) {
					String[] options = { "Yes", "No" };
					int selection = JOptionPane
							.showOptionDialog(
									null,
									"Video is being edited ,would you like to cancel it?",
									"Warning", JOptionPane.DEFAULT_OPTION,
									JOptionPane.WARNING_MESSAGE, null, options,
									options[0]);
					if (selection == 0) {
		
						videoworker.cancel(true);
						editProgressBar.setValue(0);
						editProgressBar.setString("0%");
						editVideo();
					} else {
						// do nothing
					}
				} else {
					editProgressBar.setValue(0);
					editProgressBar.setString("0%");
					editVideo();
				}

			}
		}
	}

	private void editVideo() {
		String[] options = { "Overlay", "Replace", "Strip", "Extract" };
		int selection = JOptionPane.showOptionDialog(null,
				"Which one would you like to do", "Option",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				options, options[0]);
		String cmd = "";
		if (selection == 0) {
			try {
				// overlay
				String inputFile = chooseInputAudioFile();
				String outputFile = chooseOutputFileName();
				cmd = "avconv -y -i "
						+ "\""
						+ Main.file.getAbsolutePath()
						+ "\""
						+ " -i "
						+ "\""
						+ inputFile
						+ "\""
						+ " -filter_complex amix=inputs=2 -strict experimental "
						+ "\"" + outputFile + ".mp4" + "\"";
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
						+ "\""
						+ inputFile
						+ "\""
						+ " -i "
						+ "\""
						+ Main.file.getAbsolutePath()
						+ "\""
						+ " -map 0:0 -map 1:0 -acodec copy -vcodec copy -shortest "
						+ "\"" + outputFile + ".mp4" + "\"";
				videoworker = new VideoWorker(cmd);
				videoworker.execute();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (selection == 2) {
			// check if the audio signals exists
			if (mediaPlayerComponent.getMediaPlayer().getAudioTrackCount() == 0) {
				JOptionPane.showMessageDialog(this, "No audio signal exists");

			} else {
				// Strip (doesn't save audio file)
				String output = chooseOutputFileName();
				cmd = "avconv -y -i " + "\"" + Main.file.getAbsolutePath() + "\""
						+ " -map 0:v " + "\"" + output + ".mp4" + "\"";
				videoworker = new VideoWorker(cmd);
				videoworker.execute();
			}
		} else if (selection == 3) {
			if (mediaPlayerComponent.getMediaPlayer().getAudioTrackCount() == 0) {
				JOptionPane.showMessageDialog(this, "No audio signal exists");

			} else {
				String outputFileName = chooseOutputFileName();
				cmd = "avconv -y -i " + "\"" + Main.file.getAbsolutePath() + "\""
						+ " " + "\"" + outputFileName + ".mp3" + "\"";
				videoworker = new VideoWorker(cmd);
				videoworker.execute();
			}
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
				return chooseInputAudioFile();

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
					mediaPlayerComponent.getMediaPlayer().skip(50);
				}
			} else if (options == 1) {
				while (!this.isCancelled()) {
					mediaPlayerComponent.getMediaPlayer().skip(-50);
				}
			}
			return null;
		}

	}

	// method to cancel skipworker
	public void cancelWorker() {
		if (!skipworker.isCancelled()) {
			skipworker.cancel(true);
		}
	}

	public class VideoWorker extends SwingWorker<Void, Integer> {
		private String _cmd;
		private int exitStatus;
		private double _timeLength;

		public VideoWorker(String cmd) {
			_cmd = cmd;
		}

		protected void process(List<Integer> chunks) {
			if (!isCancelled()) {
				for (int i : chunks) {
					String progress = Integer.toString(i);
					editProgressBar.setValue(i);
					editProgressBar.setString(progress + "%");
				}
			}
		}

		protected void done() {
			if (!isCancelled()) {
				if (exitStatus == 0) {
					editProgressBar.setValue(100);
					editProgressBar.setString("Successful");
				} else {
					editProgressBar.setValue(0);
					editProgressBar.setString("Error");
				}
			}

		}

		protected void parseDuration() {
			StringBuilder durationCmd = new StringBuilder("avconv");
			durationCmd.append(" -i " + "\"" + Main.file.getAbsolutePath()
					+ "\"");

			ProcessBuilder dBuilder = new ProcessBuilder("/bin/bash", "-c",
					durationCmd.toString());
			dBuilder.redirectErrorStream(true);
			_timeLength = 0;
			Process dProcess;
			try {

				dProcess = dBuilder.start();

				// getting the input stream to read the command output
				InputStream stdout = dProcess.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(
						new InputStreamReader(stdout));
				String line;

				// 22 character (0 -21 index)
				Pattern durationPat = Pattern
						.compile("Duration:\\s(\\d\\d):(\\d\\d):(\\d\\d.\\d\\d),");

				// time[s]*bitrate[kbps] = size[MB]
				while ((line = stdoutBuffered.readLine()) != null
						&& !isCancelled()) {
					Matcher durationMatcher = durationPat.matcher(line);
					if (durationMatcher.find()) {
						double second = Double.parseDouble(durationMatcher
								.group(3));
						double minute = Double.parseDouble(durationMatcher
								.group(2));
						double hour = Double.parseDouble(durationMatcher
								.group(1));

						_timeLength = (3600 * hour) + (60 * minute) + second;
						System.out.println(_timeLength);
					}
				}

				dProcess.getInputStream().close();
				dProcess.getOutputStream().close();
				dProcess.getErrorStream().close();
				dProcess.destroy();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground() throws Exception {
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", _cmd);
			parseDuration();
			pb.redirectErrorStream(true);
			Process process;
			process = pb.start();
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(
					new InputStreamReader(stdout));
			String line;

			double bitrate = 0;
			double currentSize = 0;
			// 22 character (0 -21 index)
			
			Pattern progressPat = Pattern
					.compile("L?size=\\s*(\\d*)kB\\stime=\\d*.\\d*\\sbitrate=\\s(\\d*.\\d)kbits/s");

			// time[s]*bitrate[kbps] = size[MB]
			while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
                
				Matcher progressMatcher = progressPat.matcher(line);
				if (progressMatcher.find()) {
					currentSize = Double.parseDouble(progressMatcher.group(1)) / 1024;
					bitrate = Double.parseDouble(progressMatcher.group(2));
					System.out.println("current:"+currentSize+"bitrate:"+bitrate);
					double finalSize = _timeLength * bitrate / (8 * 1024);
					double percentage = (currentSize / finalSize) * 100;
					publish((int) percentage);

				}
				System.out.println(line);
			}
			if (!isCancelled()) {
				exitStatus = process.waitFor();
			}

			process.getInputStream().close();
			process.getOutputStream().close();
			process.getErrorStream().close();
			process.destroy();
			return null;

		}
	}
}

package view;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.ImageIcon;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;

import control.BackwardFarwardWorker;
import control.FileControl;
import control.VideoWorker;

import java.awt.Font;

import javax.swing.JSlider;

import java.awt.BorderLayout;

import javax.swing.JList;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.JScrollPane;

import main.Main;

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
	private boolean volumeEnable;

	private JButton btnMainFramePlay;

	private boolean isAutomaticSlide = false;
	private JLabel lblTime;
	private JPanel historyPanel;

	/**
	 * Create the frame.
	 * 
	 * @param btnPlay2
	 * @throws IOException
	 */

	@SuppressWarnings("unchecked")
	public PlayFrame(JButton btn) throws IOException {

		btnMainFramePlay = btn;
		// initialize the workers
		videoworker = new VideoWorker("", editProgressBar);
		videoworker.cancel(true);
		// for vlcj
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		// set frame
		this.setVisible(true);
		this.setTitle(Main.file.getName());

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		// set layout
		SpringLayout sl_contentPane = new SpringLayout();
		sl_contentPane.putConstraint(SpringLayout.SOUTH, panel, -50,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, panel, -200,
				SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.NORTH, panel, 0,
				SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, panel, 0,
				SpringLayout.WEST, contentPane);
		contentPane.setLayout(sl_contentPane);

		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		panel.setVisible(true);
		SpringLayout sl_panel = new SpringLayout();
		sl_panel.putConstraint(SpringLayout.NORTH, mediaPlayerComponent, 0,
				SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, mediaPlayerComponent, 0,
				SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, mediaPlayerComponent, -50,
				SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, mediaPlayerComponent, 0,
				SpringLayout.EAST, panel);
		panel.setLayout(sl_panel);
		panel.add(mediaPlayerComponent);
		contentPane.add(panel);

		setContentPane(contentPane);

		mediaPlayerComponent.getMediaPlayer().playMedia(
				Main.file.getAbsolutePath());

		time = new JSlider();
		time.setBackground(Color.LIGHT_GRAY);
		sl_panel.putConstraint(SpringLayout.NORTH, time, -50,
				SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, time, 100, SpringLayout.WEST,
				panel);
		sl_panel.putConstraint(SpringLayout.EAST, time, 0, SpringLayout.EAST,
				panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, time, 0, SpringLayout.SOUTH,
				panel);
		time.setPaintLabels(true);

		// get time of media
		mediaPlayerComponent.getMediaPlayer().parseMedia();
		time.setMaximum((int) mediaPlayerComponent.getMediaPlayer()
				.getMediaMeta().getLength());
		time.setMinimum(0);
		// set the function of time bar
		time.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (isAutomaticSlide == false) {
					mediaPlayerComponent.getMediaPlayer().setTime(
							time.getValue());
				} else {
					isAutomaticSlide = false;
				}
			}
		});

		panel.add(time);

		lblTime = new JLabel("0:00:00" + "/" + "0:00:00");

		sl_panel.putConstraint(SpringLayout.NORTH, lblTime, -50,
				SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, lblTime, 0,
				SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, lblTime, 0,
				SpringLayout.SOUTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, lblTime, 0,
				SpringLayout.WEST, time);

		panel.add(lblTime);

		historyPanel = new JPanel();
		historyPanel.setBorder(null);
		sl_contentPane.putConstraint(SpringLayout.NORTH, historyPanel, 0,
				SpringLayout.NORTH, panel);
		sl_contentPane.putConstraint(SpringLayout.WEST, historyPanel, 6,
				SpringLayout.EAST, panel);

		sl_contentPane.putConstraint(SpringLayout.SOUTH, historyPanel, -15,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, historyPanel, 190,
				SpringLayout.EAST, panel);
		contentPane.add(historyPanel);

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
		historyPanel.setLayout(new BorderLayout(0, 0));
		@SuppressWarnings("unchecked")
		JList list = new JList(listModel);
		list.setBorder(null);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(list);
		historyPanel.add(scrollPane, BorderLayout.CENTER);
		list.addMouseListener(new MouseAdapter() {
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
		list.setBackground(Color.LIGHT_GRAY);

		JPanel btnPanel = new JPanel();
		btnPanel.setBackground(Color.LIGHT_GRAY);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnPanel, 0,
				SpringLayout.SOUTH, panel);
		sl_contentPane.putConstraint(SpringLayout.WEST, btnPanel, 0,
				SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnPanel, 0,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnPanel, -10,
				SpringLayout.WEST, historyPanel);
		contentPane.add(btnPanel);
		btnPlay = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/play43.png")));
		btnPanel.add(btnPlay);
		btnPlay.setForeground(Color.LIGHT_GRAY);
		btnPlay.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnPlay.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnPlay.setHorizontalTextPosition(AbstractButton.CENTER);
		btnPlay.setContentAreaFilled(false);
		btnPlay.setBorder(new EmptyBorder(0, 0, 0, 10));

		btnPlay.addActionListener(this);

		editProgressBar = new JProgressBar();
		historyPanel.add(editProgressBar, BorderLayout.SOUTH);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, editProgressBar, -168,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, editProgressBar, 0,
				SpringLayout.EAST, historyPanel);
		editProgressBar.setBackground(Color.LIGHT_GRAY);
		editProgressBar.setStringPainted(true);

		btnEdit = new JButton("Edit ");
		historyPanel.add(btnEdit, BorderLayout.NORTH);

		btnEdit.addActionListener(this);

		btnPause = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/pause15.png")));

		btnPause.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnPause.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnPause.setHorizontalTextPosition(AbstractButton.CENTER);
		btnPause.addActionListener(this);
		btnPause.setBorder(new EmptyBorder(0, 0, 0, 10));

		btnPause.setContentAreaFilled(false);
		btnPanel.add(btnPause);

		btnForward = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/next10.png")));
		btnPanel.add(btnForward);
		btnForward.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnForward.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnForward.setHorizontalTextPosition(AbstractButton.CENTER);
		btnForward.addActionListener(this);
		btnForward.setBorder(new EmptyBorder(0, 0, 0, 10));

		btnForward.setContentAreaFilled(false);

		btnBackward = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/back12.png")));
		btnPanel.add(btnBackward);

		btnBackward.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnBackward.setVerticalTextPosition(AbstractButton.BOTTOM);
		btnBackward.setBorder(new EmptyBorder(0, 0, 0, 10));

		btnBackward.setHorizontalTextPosition(AbstractButton.CENTER);
		btnBackward.addActionListener(this);
		btnBackward.setContentAreaFilled(false);

		btnMute = new JButton("", new ImageIcon(
				PlayFrame.class.getResource("/se206_a03/png/muted.png")));
		btnPanel.add(btnMute);

		btnMute.setFont(new Font("Lucida Bright", Font.BOLD, 10));
		btnMute.addActionListener(this);
		btnMute.setBorder(new EmptyBorder(0, 0, 0, 10));
		btnMute.setContentAreaFilled(false);
		// set volume bar
		volume = new JSlider();
		volume.setBorder(UIManager.getBorder("Button.border"));
		btnPanel.add(volume);
		volume.setBackground(Color.LIGHT_GRAY);

		volume.setMaximum(200);
		volume.setValue(50);
		volume.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				mediaPlayerComponent.getMediaPlayer().setVolume(
						volume.getValue());

			}
		});
		timer = new Timer(800, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (time.getValue() < time.getMaximum()) {
					time.setValue((int) mediaPlayerComponent.getMediaPlayer()
							.getTime());
					isAutomaticSlide = true;
					// get total time
					int totalTime = (int) mediaPlayerComponent.getMediaPlayer()
							.getMediaMeta().getLength() / 1000;
					int hour = totalTime / 3600;
					int minute = totalTime / 60 - hour * 60;
					int second = (totalTime % 3600) % 60;
					String t = String.valueOf(hour) + ":"
							+ String.valueOf(minute) + ":"
							+ String.valueOf(second);
					// get current time
					int currentTime = (int) mediaPlayerComponent
							.getMediaPlayer().getTime() / 1000;
					int currentHour = currentTime / 3600;
					int currentMinute = currentTime / 60 - currentHour * 60;
					int currentSecond = (currentTime % 3600) % 60;
					String current = String.valueOf(currentHour) + ":"
							+ String.valueOf(currentMinute) + ":"
							+ String.valueOf(currentSecond);
					lblTime.setText(current + "/" + t);
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
				btnMainFramePlay.setEnabled(true);
				mediaPlayerComponent.getMediaPlayer().stop();
				videoworker.cancel(true);
			}
		});
		skipworker = new BackwardFarwardWorker(0, mediaPlayerComponent);
		timer.start();
	}

	// choose output file name to save it,default by "output"
	public String chooseOutputFileName() {
		String path = choosePath();
		if (path != null) {
			String outputFileName = JOptionPane.showInputDialog(
					"Please enter the output file name without extension",
					"output");
			if (!(outputFileName == null)) {
				File f1 = new File(path + "/" + outputFileName + ".mp3");
				File f2 = new File(path + "/" + outputFileName + ".mp4");
				File f3 = new File(path + "/" + outputFileName + ".png");
				// check if file exists
				if (f1.exists() || f2.exists() || f3.exists()) {
					String[] options = { "Yes", "Cancel" };
					int selection = JOptionPane.showOptionDialog(null,
							outputFileName
									+ " exists,Do you want to override it?",
							"Warning", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options,
							options[0]);
					if (selection == 0) {
						return path + "/" + outputFileName;
					} else {
						JOptionPane.showMessageDialog(this,
								"Please choose another output file name");
						return this.chooseOutputFileName();

					}
				} else {
					return path + "/" + outputFileName;
				}
			}
		} else {
			return null;
		}
		return null;
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
			return path;
		} else {
			return null;
		}

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
			// keep forward
			cancelWorker();
			skipworker = new BackwardFarwardWorker(0, mediaPlayerComponent);
			skipworker.execute();
		} else if (e.getSource() == btnBackward) {
			// keep backword
			cancelWorker();
			skipworker = new BackwardFarwardWorker(1, mediaPlayerComponent);
			skipworker.execute();
		} else if (e.getSource() == btnMute) {
			// mute the media
			mediaPlayerComponent.getMediaPlayer().mute();
			volumeEnable = !volumeEnable;
			volume.setEnabled(volumeEnable);
		} else if (e.getSource() == btnEdit) {
			if (FileControl.isAudioVideoFile(Main.file).equals("audio")) {
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
						// if cancel the video worker
						videoworker.cancel(true);
						editProgressBar.setValue(0);
						editProgressBar.setString("0%");
						editVideo();
					} else {
						// if doesn't cancel, do nothing
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
		String[] options = { "Overlay", "Replace", "Strip", "Extract",
				"Screenshot" };
		int selection = JOptionPane.showOptionDialog(null,
				"Which one would you like to do", "Option",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				options, options[0]);
		String cmd = "";
		if (selection == 0) {
			try {
				// overlay
				String inputFile = FileControl.chooseInputAudioFile();
				if (inputFile != null) {
					String outputFile = chooseOutputFileName();
					if (outputFile != null) {
						// command for overlay
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
						videoworker = new VideoWorker(cmd, editProgressBar);
						videoworker.execute();
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (selection == 1) {
			// replace
			try {
				String inputFile = FileControl.chooseInputAudioFile();
				if (inputFile != null) {
					String outputFile = chooseOutputFileName();
					if (outputFile != null) {
						// command for replace
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
						videoworker = new VideoWorker(cmd, editProgressBar);
						videoworker.execute();
					}
				}
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
				if (output != null) {
					cmd = "avconv -y -i " + "\"" + Main.file.getAbsolutePath()
							+ "\"" + " -map 0:v " + "\"" + output + ".mp4"
							+ "\"";
					videoworker = new VideoWorker(cmd, editProgressBar);
					videoworker.execute();
				}
			}
		} else if (selection == 3) {
			// check if the audio signals exists
			if (mediaPlayerComponent.getMediaPlayer().getAudioTrackCount() == 0) {
				JOptionPane.showMessageDialog(this, "No audio signal exists");

			} else {
				String outputFileName = chooseOutputFileName();
				if (outputFileName != null) {
					// command for extract
					cmd = "avconv -y -i " + "\"" + Main.file.getAbsolutePath()
							+ "\"" + " " + "\"" + outputFileName + ".mp3"
							+ "\"";
					videoworker = new VideoWorker(cmd, editProgressBar);
					videoworker.execute();
				}
			}
		} else if (selection == 4) {
			int totalTime = (int) mediaPlayerComponent.getMediaPlayer()
					.getMediaMeta().getLength() / 1000;
			int hour = totalTime / 3600;
			// get total time of the media
			SpinnerNumberModel hourModel = new SpinnerNumberModel(0, 0, hour, 1);
			SpinnerNumberModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1);
			SpinnerNumberModel secondModel = new SpinnerNumberModel(0, 0, 59, 1);
			JSpinner hourSpinner = new JSpinner(hourModel);
			JSpinner minuteSpinner = new JSpinner(minuteModel);
			JSpinner SecondSpinner = new JSpinner(secondModel);
			Object[] message = { "Hour:", hourSpinner, "Minute:",
					minuteSpinner, "Second", SecondSpinner, };
			int option = JOptionPane.showConfirmDialog(null, message,
					"Choose the time", JOptionPane.OK_CANCEL_OPTION);
			if (option == 0) {
				int selectHour = (int) hourSpinner.getValue();
				int selectMinute = (int) minuteSpinner.getValue();
				int selectSecond = (int) SecondSpinner.getValue();
				if ((selectHour * 3600 + selectMinute * 60 + selectSecond) > totalTime) {
					JOptionPane.showMessageDialog(null, "Invalid time, the time exceeds the total time");
				} else {
					// cmd for screenshot
					String outputFileName = chooseOutputFileName();
					if (outputFileName != null) {
						String time = String.valueOf(selectHour) + ":"
								+ String.valueOf(selectMinute) + ":"
								+ String.valueOf(selectSecond);
						cmd = "avconv -y -i " + "\""
								+ Main.file.getAbsolutePath() + "\"" + " "
								+ "-ss" + " " + time + " " + "-vframes 1" + " "
								+ "\"" + outputFileName + ".png" + "\"";
						System.out.println(cmd);
						videoworker = new VideoWorker(cmd, editProgressBar);
						videoworker.execute();
					}
				}

			}
		}
	}

	// method to cancel skipworker
	public void cancelWorker() {
		if (!skipworker.isCancelled()) {
			skipworker.cancel(true);
		}
	}

}

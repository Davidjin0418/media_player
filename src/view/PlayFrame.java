package view;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.ImageIcon;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;

import control.BackwardFarwardWorker;
import control.VideoWorker;

import java.awt.Font;

import javax.swing.JSlider;

import java.awt.BorderLayout;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.JScrollPane;

import main.Main;
import model.MainPlayButton;
import model.PlayEditButton;
import model.PlayHistoryList;
import model.PlayNewFileButton;
import javax.swing.border.LineBorder;

public class PlayFrame extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JPanel panel;
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JButton btnPause;
	private JButton btnForward;
	private JButton btnBackward;
	private PlayNewFileButton btnNewFile;
	private JButton btnMute;
	private PlayEditButton btnEdit;
	private JSlider time;
	private JSlider volume;
	private JProgressBar editProgressBar;
	private BackwardFarwardWorker skipworker;
	private VideoWorker videoworker;
	private Timer timer;
	private boolean volumeEnable;

	private MainPlayButton btnMainFramePlay;
	private JTextField currentFIle;
	private boolean isAutomaticSlide = false;
	private JLabel lblTime;
	private JPanel historyPanel;

	private boolean isPlaying = true;

	/**
	 * Create the frame.
	 * 
	 * @param btnPlay2
	 * @throws IOException
	 */

	public PlayFrame(MainPlayButton btn, JTextField field) throws IOException {
		currentFIle = field;
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
		panel.setBorder(null);
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
		sl_contentPane.putConstraint(SpringLayout.NORTH, historyPanel, 0, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, historyPanel, 0, SpringLayout.SOUTH, contentPane);
		historyPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		sl_contentPane.putConstraint(SpringLayout.WEST, historyPanel, 6,
				SpringLayout.EAST, panel);
		sl_contentPane.putConstraint(SpringLayout.EAST, historyPanel, 190,
				SpringLayout.EAST, panel);
		contentPane.add(historyPanel);

		historyPanel.setLayout(new BorderLayout(0, 0));
		PlayHistoryList list = new PlayHistoryList(mediaPlayerComponent);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(list);
		historyPanel.add(scrollPane, BorderLayout.CENTER);

		JPanel btnPanel = new JPanel();
		sl_contentPane.putConstraint(SpringLayout.WEST, btnPanel, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnPanel, -6, SpringLayout.WEST, historyPanel);
		btnPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		btnPanel.setBackground(Color.LIGHT_GRAY);
		sl_contentPane.putConstraint(SpringLayout.NORTH, btnPanel, 0,
				SpringLayout.SOUTH, panel);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnPanel, 0,
				SpringLayout.SOUTH, contentPane);
		contentPane.add(btnPanel);
	    btnNewFile = new PlayNewFileButton(mediaPlayerComponent,this);
		btnPanel.add(btnNewFile);

		editProgressBar = new JProgressBar();
		historyPanel.add(editProgressBar, BorderLayout.SOUTH);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, editProgressBar, -168,
				SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, editProgressBar, 0,
				SpringLayout.EAST, historyPanel);
		editProgressBar.setBackground(Color.LIGHT_GRAY);
		editProgressBar.setStringPainted(true);

		btnEdit = new PlayEditButton(videoworker, editProgressBar,
				mediaPlayerComponent);
		historyPanel.add(btnEdit, BorderLayout.NORTH);

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
					String t = String.valueOf(minute) + ":"
							+ String.valueOf(second);
					// get current time
					int currentTime = (int) mediaPlayerComponent
							.getMediaPlayer().getTime() / 1000;
					int currentHour = currentTime / 3600;
					int currentMinute = currentTime / 60 - currentHour * 60;
					int currentSecond = (currentTime % 3600) % 60;
					String current = String.valueOf(currentMinute) + ":"
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
				currentFIle.setText(Main.file.getAbsolutePath() + " is chosen");
			}
		});
		skipworker = new BackwardFarwardWorker(0, mediaPlayerComponent);
		timer.start();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnPause) {
			// pause the media.
			isPlaying = !isPlaying;
			skipworker.cancelWorker();
			if (isPlaying) {
				btnPause.setIcon(new ImageIcon(PlayFrame.class
						.getResource("/se206_a03/png/pause15.png")));
				mediaPlayerComponent.getMediaPlayer().play();
			} else {
				btnPause.setIcon(new ImageIcon(PlayFrame.class
						.getResource("/se206_a03/png/play43.png")));
				mediaPlayerComponent.getMediaPlayer().pause();
			}

		} else if (e.getSource() == btnForward) {
			// keep forward
			skipworker.cancelWorker();
			skipworker = new BackwardFarwardWorker(0, mediaPlayerComponent);
			skipworker.execute();
		} else if (e.getSource() == btnBackward) {
			// keep backword
			skipworker.cancelWorker();
			skipworker = new BackwardFarwardWorker(1, mediaPlayerComponent);
			skipworker.execute();
		} else if (e.getSource() == btnMute) {
			// mute the media
			mediaPlayerComponent.getMediaPlayer().mute();
			volumeEnable = !volumeEnable;
			volume.setEnabled(volumeEnable);
		}
	}

}

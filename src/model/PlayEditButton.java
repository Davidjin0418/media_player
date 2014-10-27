package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import view.SubtitleFrame;
import main.Main;
import control.FileControl;
import control.VideoWorker;

/**
 * 
 * the button that performs the editing function in the play frame
 * 
 */
public class PlayEditButton extends JButton implements ActionListener {
	private VideoWorker videoworker;
	private JProgressBar editProgressBar;
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;

	/**
	 * 
	 * @param worker
	 *            the video worker for editing
	 * @param bar
	 *            the progress bar that shows status
	 * @param player
	 *            the media player component
	 */
	public PlayEditButton(VideoWorker worker, JProgressBar bar,
			EmbeddedMediaPlayerComponent player) {
		this.addActionListener(this);
		this.setText("Edit");
		videoworker = worker;
		editProgressBar = bar;
		mediaPlayerComponent = player;
	}

	private void editVideo() {
		String[] options = { "Overlay", "Replace", "Strip", "Extract",
				"Screenshot", "Add subtitle","Create subtitle" };
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
					String outputFile = FileControl.chooseOutputFileName();
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
								+ "\"" + outputFile + ".avi" + "\"";
						videoworker = new VideoWorker(cmd, editProgressBar);
						videoworker.execute();
					}
				}
			} catch (IOException e1) {

				e1.printStackTrace();
			}

		} else if (selection == 1) {
			// replace
			try {
				String inputFile = FileControl.chooseInputAudioFile();
				if (inputFile != null) {
					String outputFile = FileControl.chooseOutputFileName();
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
								+ "\"" + outputFile + ".avi" + "\"";
						videoworker = new VideoWorker(cmd, editProgressBar);
						videoworker.execute();
					}
				}
			} catch (IOException e1) {

				e1.printStackTrace();
			}

		} else if (selection == 2) {
			// check if the audio signals exists
			if (mediaPlayerComponent.getMediaPlayer().getAudioTrackCount() == 0) {
				JOptionPane.showMessageDialog(this, "No audio signal exists");

			} else {
				// Strip (doesn't save audio file)
				String output = FileControl.chooseOutputFileName();
				if (output != null) {
					cmd = "avconv -y -i " + "\"" + Main.file.getAbsolutePath()
							+ "\"" + " -map 0:v " + "\"" + output + ".avi"
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
				String outputFileName = FileControl.chooseOutputFileName();
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
			// screen shot
			int totalTime = (int) mediaPlayerComponent.getMediaPlayer()
					.getMediaMeta().getLength() / 1000;
			int hour = totalTime / 3600;
			// get total time of the media

			SpinnerNumberModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1);
			SpinnerNumberModel secondModel = new SpinnerNumberModel(0, 0, 59, 1);

			JSpinner minuteSpinner = new JSpinner(minuteModel);
			JSpinner SecondSpinner = new JSpinner(secondModel);
			Object[] message = { "Minute:", minuteSpinner, "Second",
					SecondSpinner, };
			int option = JOptionPane.showConfirmDialog(null, message,
					"Choose the time", JOptionPane.OK_CANCEL_OPTION);
			if (option == 0) {

				int selectMinute = (int) minuteSpinner.getValue();
				int selectSecond = (int) SecondSpinner.getValue();
				if ((selectMinute * 60 + selectSecond) > totalTime) {
					JOptionPane.showMessageDialog(null,
							"Invalid time, the time exceeds the total time");
				} else {
					// cmd for screenshot
					String outputFileName = FileControl.chooseOutputFileName();
					if (outputFileName != null) {
						String time = "00" + ":" + String.valueOf(selectMinute)
								+ ":" + String.valueOf(selectSecond);
						cmd = "avconv -y -i " + "\""
								+ Main.file.getAbsolutePath() + "\"" + " "
								+ "-ss" + " " + time + " " + "-vframes 1" + " "
								+ "\"" + outputFileName + ".png" + "\"";
						videoworker = new VideoWorker(cmd, editProgressBar);
						videoworker.execute();
					}
				}

			}
		} else if (selection == 5) {
			// add subtitle
			File f = FileControl.chooseSubtitle();
			mediaPlayerComponent.getMediaPlayer().setSubTitleFile(f);
		}else if(selection==6){
			SubtitleFrame frame=new SubtitleFrame();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this) {
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
}

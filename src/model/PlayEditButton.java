package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import main.Main;
import control.FileControl;
import control.VideoWorker;

public class PlayEditButton extends JButton implements ActionListener {
   private VideoWorker videoworker;
   private JProgressBar editProgressBar;
   private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	public PlayEditButton(VideoWorker worker,JProgressBar bar,EmbeddedMediaPlayerComponent player ){
		this.addActionListener(this);
		this.setText("Edit");
		videoworker=worker;
		editProgressBar=bar;
		mediaPlayerComponent=player;
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
				String output = FileControl.chooseOutputFileName();
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
					JOptionPane.showMessageDialog(null,
							"Invalid time, the time exceeds the total time");
				} else {
					// cmd for screenshot
					String outputFileName = FileControl.chooseOutputFileName();
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this){
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

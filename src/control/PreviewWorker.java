package control;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

/**
 * 
 * preview worker extends TextFilterWorker it used for preview the change added
 * to the video file.
 * 
 */
public class PreviewWorker extends TextFilterWorker {

	private int exitStatus;
	private int option;
	private double timeLength;
	private JPanel panel;

	/**
	 * Constructor
	 * 
	 * @param file
	 *            the video file
	 * @param textForOpen
	 *            text for open scene
	 * @param textForClose
	 *            text for close scene
	 * @param previewButton
	 *            the button that preview the change
	 * @param option
	 *            0 for open scene,1 for close scene
	 */
	public PreviewWorker(File file, JTextPane textForOpen,
			JTextPane textForClose, JButton previewButton, int o, JPanel p) {
		super();
		videoFile = file;
		openTextFont = textForOpen.getFont();
		openTextColour = textForOpen.getForeground();

		closeTextFont = textForClose.getFont();
		closeTextColour = textForClose.getForeground();
		openText = textForOpen.getText();
		closeText = textForClose.getText();
		button = previewButton;
		option = o;
		panel = p;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		parseDuration();

		// command to process the video
		StringBuilder cmd = new StringBuilder("avconv -y ");
		// path to input file, textfilter for open scene
		cmd.append("-i \"" + videoFile.getAbsolutePath() + "\"");
		cmd.append(" -vf \"drawtext=fontfile='");
		if(option==0){
			cmd.append(this.createOpenTextParameter());
		}
		else{
			cmd.append(this.createTextParameter());
		}

		// using the same audio from the source file so don't need to re encode
		// the audio file again.
		// cmd.append(" -c:a copy ");
		String open = System.getProperty("user.home") + "/.vamix/open.png";
		String close = System.getProperty("user.home") + "/.vamix/close.png";
		if (option == 0) {
			cmd.append(" -ss 00:00:01 -vframes 1 " + open);
		} else {
			timeLength = super.parseDuration();
			int hour = (int) (timeLength / 3600);
			int minute = (int) (timeLength / 60 - hour * 60);
			int second = (int) ((timeLength % 3600) % 60);
			String t = String.valueOf(hour) + ":" + String.valueOf(minute)
					+ ":" + String.valueOf(second);
			cmd.append(" -ss " + t + " -vframes 1 " + close);
		}

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c",
				cmd.toString());
		builder.redirectErrorStream(true);
		System.out.println(cmd);
		Process process;
		try {

			process = builder.start();

			// getting the input stream to read the command output
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(
					new InputStreamReader(stdout));
			String line;
			while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {

			}

			if (!isCancelled()) {
				exitStatus = process.waitFor();
			}

			process.getInputStream().close();
			process.getOutputStream().close();
			process.getErrorStream().close();
			process.destroy();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}

	public void done() {
		button.setEnabled(true);
		// update the image
		BufferedImage myPicture = null;
		if (option == 0) {
			try {
				myPicture = ImageIO.read(new File(System
						.getProperty("user.home") + "/.vamix/open.png"));
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		} else {

			try {
				myPicture = ImageIO.read(new File(System
						.getProperty("user.home") + "/.vamix/close.png"));
			} catch (IOException e1) {

				e1.printStackTrace();
			}

			
		}

		Image i = myPicture.getScaledInstance(panel.getWidth(),
				panel.getHeight(), Image.SCALE_SMOOTH);
		JLabel l = new JLabel(new ImageIcon(i));
		panel.removeAll();
		panel.add(l);
		panel.validate();
		panel.repaint();
		//this.removeTempFile();
	}

}

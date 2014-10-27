package control;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

/**
 * 
 * The class extends Swingworker,it can add the text and embed it to the video
 * file.
 * 
 */
public class TextFilterWorker extends SwingWorker<Integer, Integer> {

	protected File videoFile;
	protected JProgressBar bar;
	protected Font openTextFont;
	protected Color openTextColour;

	protected Font closeTextFont;
	protected Color closeTextColour;

	protected String openText;
	protected String closeText;
	protected JButton button;
	protected int exitStatus;
	protected double timeLength;

	protected File closeTempFile;
	protected File openTempFile;

	protected File outputFile;

	/**
	 * 
	 * @param file
	 *            the media file
	 * @param progress
	 *            the progress bar that displays the status
	 * @param textForOpen
	 *            text for open scene
	 * @param textForClose
	 *            text for close scene
	 * @param okButton
	 *            the button that activate the worker
	 * @param output
	 *            the saved output file
	 */
	public TextFilterWorker(File file, JProgressBar progress,
			JTextPane textForOpen, JTextPane textForClose, JButton okButton,
			File output) {
		videoFile = file;
		bar = progress;
		openTextFont = textForOpen.getFont();
		openTextColour = textForOpen.getForeground();
		openText = textForOpen.getText();

		closeTextFont = textForClose.getFont();
		closeTextColour = textForClose.getForeground();
		closeText = textForClose.getText();
		button = okButton;
		outputFile = output;
	}

	public TextFilterWorker() {

	}

	@Override
	protected Integer doInBackground() throws Exception {

		// command to get the duration
		timeLength=parseDuration();

		// command to process the video
		StringBuilder cmd = new StringBuilder("avconv ");
		// path to input file, textfilter for open scene
		cmd.append(" -y -i " + "\"" + videoFile.getAbsolutePath() + "\""
				+ " -vf \"drawtext=fontfile='");

		cmd.append(this.createTextParameter());
		// using the same audio from the source file so don't need to re encode
		// the audio file again.
		cmd.append(" -vcodec copy -strict experimental ");

		cmd.append("\"" + outputFile.getAbsolutePath() + "\"");
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
			String duration = null;
			String progress = null;
			double bitrate = 0;
			double currentSize = 0;
			// 22 character (0 -21 index)
			Pattern durationPat = Pattern
					.compile("Duration:\\s(\\d\\d):(\\d\\d):(\\d\\d.\\d\\d),");
			Pattern progressPat = Pattern
					.compile("L?size=\\s*(\\d*)kB\\stime=\\d*.\\d*\\sbitrate=\\s*(\\d*.\\d)kbits/s");

			// time[s]*bitrate[kbps] = size[MB]
			while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {

				Matcher progressMatcher = progressPat.matcher(line);
				if (progressMatcher.find()) {
					progress = progressMatcher.group(0);
					currentSize = Double.parseDouble(progressMatcher.group(1)) / 1024;
					bitrate = Double.parseDouble(progressMatcher.group(2));
					double finalSize = timeLength * bitrate / (8 * 1024);
					double percentage = (currentSize / finalSize) * 100;
					publish((int) percentage);

				}

			}

			if (!isCancelled()) {
				exitStatus = process.waitFor();
			}

			process.getInputStream().close();
			process.getOutputStream().close();
			process.getErrorStream().close();
			process.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void done() {
		if (!isCancelled()) {
			if (exitStatus == 0) {
				bar.setValue(100);
				bar.setString("Done");
			} else {
				// need indicate to the user.
			}
		} else {
			bar.setValue(0);
			bar.setString("Cancelled");
		}

		button.setEnabled(true);
		//removeTempFile();
	}

	public void process(List<Integer> n) {
		for (Integer currentPercentage : n) {
			bar.setValue(currentPercentage);
			bar.setString(currentPercentage + "%");
		}
	}

	protected double parseDuration() {
		StringBuilder durationCmd = new StringBuilder("avconv");
		durationCmd.append(" -i " + "\"" + videoFile.getAbsolutePath() + "\"");

		ProcessBuilder dBuilder = new ProcessBuilder("/bin/bash", "-c",
				durationCmd.toString());
		dBuilder.redirectErrorStream(true);
		double time = 0;
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
			while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
				Matcher durationMatcher = durationPat.matcher(line);
				if (durationMatcher.find()) {
					double second = Double
							.parseDouble(durationMatcher.group(3));
					double minute = Double
							.parseDouble(durationMatcher.group(2));
					double hour = Double.parseDouble(durationMatcher.group(1));

					time = (3600 * hour) + (60 * minute) + second;
					return time;
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
		return time;
	}
    protected String createOpenTextParameter(){
    	StringBuilder cmd = new StringBuilder();
		String fontPath;
		if (openTextFont.getName().equals("Ubuntu Light")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-L.ttf':";
		} else if (openTextFont.getName().equals("Ubuntu Medium")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-M.ttf':";
		} else if (openTextFont.getName().equals("Ubuntu Mono")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-R.ttf':";
		} else if (openTextFont.getName().equals("Ubuntu Condensed")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-C.ttf':";
		} else {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-R.ttf':";
		}
		cmd.append(fontPath);

		BufferedWriter bw;
		try {
			openTempFile = new File(System.getProperty("user.home")+"/.vamix/openTempFile.txt");
			openTempFile.createNewFile();
			bw = new BufferedWriter(new FileWriter(openTempFile, false));
			PrintWriter pw = new PrintWriter(bw);

			// create a string builder to for making the entry string
			StringBuilder textInformation = new StringBuilder();

			// opening scene
			textInformation.append(openText);
			textInformation.append(System.getProperty("line.separator"));

			// append the entry to the file
			pw.append(textInformation.toString());
			pw.close();
			bw.close();
		} catch (IOException e2) {

			e2.printStackTrace();
		}
		cmd.append("textfile='" + openTempFile.getAbsolutePath() + "':");
		cmd.append("x=((W/2)-(W/4)):");
		cmd.append("y=((H/2)-(h/2)):");
		cmd.append("fontsize=" + openTextFont.getSize() + ":");

		String red = Integer.toHexString(openTextColour.getRed());
		if (red.length() == 1) {
			red = "0" + red;
		}
		String green = Integer.toHexString(openTextColour.getGreen());
		if (green.length() == 1) {
			green = "0" + green;
		}
		String blue = Integer.toHexString(openTextColour.getBlue());
		if (blue.length() == 1) {
			blue = "0" + blue;
		}
		cmd.append("fontcolor=0x" + red + green + blue + ":");

		cmd.append("draw='lt(t,10)':\"");
       return cmd.toString();
    }
    
	protected String createTextParameter() {

		StringBuilder cmd = new StringBuilder();
		String fontPath;
		if (openTextFont.getName().equals("Ubuntu Light")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-L.ttf':";
		} else if (openTextFont.getName().equals("Ubuntu Medium")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-M.ttf':";
		} else if (openTextFont.getName().equals("Ubuntu Mono")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-R.ttf':";
		} else if (openTextFont.getName().equals("Ubuntu Condensed")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-C.ttf':";
		} else {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-R.ttf':";
		}
		cmd.append(fontPath);

		BufferedWriter bw;
		try {
			openTempFile = new File(System.getProperty("user.home")+"/.vamix/openTempFile.txt");
			openTempFile.createNewFile();
			bw = new BufferedWriter(new FileWriter(openTempFile, false));
			PrintWriter pw = new PrintWriter(bw);

			// create a string builder to for making the entry string
			StringBuilder textInformation = new StringBuilder();

			// opening scene
			textInformation.append(openText);
			textInformation.append(System.getProperty("line.separator"));

			// append the entry to the file
			pw.append(textInformation.toString());
			pw.close();
			bw.close();
		} catch (IOException e2) {

			e2.printStackTrace();
		}
		cmd.append("textfile='" + openTempFile.getAbsolutePath() + "':");
		cmd.append("x=((W/2)-(W/4)):");
		cmd.append("y=((H/2)-(h/2)):");
		cmd.append("fontsize=" + openTextFont.getSize() + ":");

		String red = Integer.toHexString(openTextColour.getRed());
		if (red.length() == 1) {
			red = "0" + red;
		}
		String green = Integer.toHexString(openTextColour.getGreen());
		if (green.length() == 1) {
			green = "0" + green;
		}
		String blue = Integer.toHexString(openTextColour.getBlue());
		if (blue.length() == 1) {
			blue = "0" + blue;
		}
		cmd.append("fontcolor=0x" + red + green + blue + ":");

		cmd.append("draw='lt(t,10)':");

		// text filter for closing scene
		cmd.append(",drawtext=fontfile='");
		if (closeTextFont.getName().equals("Ubuntu Light")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-L.ttf':";
		} else if (closeTextFont.getName().equals("Ubuntu Medium")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-M.ttf':";
		} else if (closeTextFont.getName().equals("Ubuntu Mono")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-R.ttf':";
		} else if (closeTextFont.getName().equals("Ubuntu Condensed")) {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-C.ttf':";
		} else {
			fontPath = "/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-R.ttf':";
		}
		cmd.append(fontPath);

		try {
			closeTempFile = new File(System.getProperty("user.home")+"/.vamix/closeTempFile.txt");
			closeTempFile.createNewFile();
			bw = new BufferedWriter(new FileWriter(closeTempFile, false));
			PrintWriter pw = new PrintWriter(bw);

			// create a string builder to for making the entry string
			StringBuilder textInformation = new StringBuilder();

			// closing scene
			textInformation.append(closeText);
			textInformation.append(System.getProperty("line.separator"));

			// append the entry to the file
			pw.append(textInformation.toString());
			pw.close();
			bw.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		cmd.append("textfile='" + closeTempFile.getAbsolutePath() + "':");
		cmd.append("x=((W/2)-(W/4)):");
		cmd.append("y=((H/2)+(h/2)):");
		cmd.append("fontsize=" + closeTextFont.getSize() + ":");

		String redClose = Integer.toHexString(closeTextColour.getRed());
		if (redClose.length() == 1) {
			redClose = "0" + redClose;
		}
		String greenClose = Integer.toHexString(closeTextColour.getGreen());
		if (greenClose.length() == 1) {
			greenClose = "0" + greenClose;
		}
		String blueClose = Integer.toHexString(closeTextColour.getBlue());
		if (blueClose.length() == 1) {
			blueClose = "0" + blueClose;
		}

		cmd.append("fontcolor=0x" + redClose + greenClose + blueClose + ":");
		cmd.append("draw='gt(t," + ((int) timeLength - 10) + ")':\"");
		return cmd.toString();
		
	}
    
	

}

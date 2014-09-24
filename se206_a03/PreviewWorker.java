package se206_a03;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

public class PreviewWorker extends SwingWorker<Integer,Integer> {
	
	File _videoFile;
	Font _openTextFont;
	Color _openTextColour;
	
	Font _closeTextFont;
	Color _closeTextColour;
	String _openText;
	String _closeText;
	JButton _button;
	int _exitStatus;
	
	public PreviewWorker(File file , JTextPane _textForOpen, JTextPane _textForClose, JButton _previewButton) {
		_videoFile = file;
		_openTextFont = _textForOpen.getFont();
		_openTextColour = _textForOpen.getForeground();
		
		_closeTextFont = _textForClose.getFont();
		_closeTextColour = _textForClose.getForeground();
		_openText= _textForOpen.getText();
		_closeText = _textForClose.getText();
		_button = _previewButton;
	}
	
	@Override
	protected Integer doInBackground() throws Exception {
		// TODO Auto-generated method stub
		StringBuilder durationCmd = new StringBuilder("avconv");
		durationCmd.append(" -i " + "\"" + _videoFile.getAbsolutePath() + "\"");
		
		ProcessBuilder dBuilder = new ProcessBuilder("/bin/bash", "-c", durationCmd.toString());
		dBuilder.redirectErrorStream(true);
		double timeLength = 0;
		Process dProcess;
		try {
			
			dProcess = dBuilder.start();

			//getting the input stream to read the command output
			InputStream stdout = dProcess.getInputStream();		
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line;
			
			//22 character (0 -21 index)
			Pattern durationPat = Pattern.compile("Duration:\\s(\\d\\d):(\\d\\d):(\\d\\d.\\d\\d),");
			

			// time[s]*bitrate[kbps] = size[MB]
			while ((line = stdoutBuffered.readLine()) != null  && !isCancelled()) {
				Matcher durationMatcher = durationPat.matcher(line);
				if (durationMatcher.find()) {
					double second = Double.parseDouble(durationMatcher.group(3));
					double minute = Double.parseDouble(durationMatcher.group(2));
					double hour = Double.parseDouble(durationMatcher.group(1));
					
					timeLength = (3600*hour) + (60*minute) + second;
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
		
		
		
		//command to process the video
		StringBuilder cmd  = new StringBuilder("avplay ");		
		//path to input file, textfilter for open scene
		cmd. append("-vf \"drawtext=fontfile='");
		String fontPath;
		if (_openTextFont.getName().equals("Ubuntu Light")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-L.ttf':";
		}
		else if (_openTextFont.getName().equals("Ubuntu Medium")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-M.ttf':";
		}
		else if (_openTextFont.getName().equals("Ubuntu Mono")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-R.ttf':";
		}
		else if (_openTextFont.getName().equals("Ubuntu Condensed")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-C.ttf':";
		}	
		else {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-R.ttf':";
		}
		cmd.append(fontPath);
		cmd.append("text='" + _openText + "':");
		cmd.append("x=50:");
		cmd.append("y=50:");
		cmd.append("fontsize=" + _openTextFont.getSize() + ":");

		String red = Integer.toHexString(_openTextColour.getRed());
		if (red.length() == 1) {
			red = "0" + red;
		}
		String green = Integer.toHexString(_openTextColour.getGreen());
		if (green.length() == 1) {
			green = "0" + green;
		}
		String blue = Integer.toHexString(_openTextColour.getBlue());
		if (blue.length() == 1) {
			blue = "0" + blue;
		}
		cmd.append("fontcolor=0x" + red+green+blue + ":");
		
		cmd.append("draw='lt(t,10)':");
		
		//text filter for closing scene
		cmd.append(",drawtext=fontfile='");
		if (_closeTextFont.getName().equals("Ubuntu Light")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-L.ttf':";
		}
		else if (_closeTextFont.getName().equals("Ubuntu Medium")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-M.ttf':";
		}
		else if (_closeTextFont.getName().equals("Ubuntu Mono")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/UbuntuMono-R.ttf':";
		}
		else if (_closeTextFont.getName().equals("Ubuntu Condensed")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-C.ttf':";
		}
		else {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-R.ttf':";
		}
		cmd.append(fontPath);
		cmd.append("text='" + _closeText + "':");
		cmd.append("x=50:");
		cmd.append("y=200:");
		cmd.append("fontsize=" + _closeTextFont.getSize() + ":");
		
		String redClose = Integer.toHexString(_closeTextColour.getRed());
		if (redClose.length() == 1) {
			redClose = "0" + redClose;
		}
		String greenClose = Integer.toHexString(_closeTextColour.getGreen());
		if (greenClose.length() == 1) {
			greenClose = "0" + greenClose;
		}
		String blueClose = Integer.toHexString(_closeTextColour.getBlue());
		if (blueClose.length() == 1) {
			blueClose = "0" + blueClose;
		}
		
		cmd.append("fontcolor=0x" + redClose+greenClose+blueClose + ":");
		cmd.append("draw='gt(t," + ((int)timeLength-10) +")':\"");
		
		//using the same audio from the source file so don't need to re encode the audio file again.
		//cmd.append(" -c:a copy ");
		cmd.append(" -i \"" + _videoFile.getAbsolutePath() + "\"");
		System.out.println(cmd.toString());
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd.toString());
		builder.redirectErrorStream(true);
		
		Process process;
		try {
			
			process = builder.start();

			//getting the input stream to read the command output
			InputStream stdout = process.getInputStream();		
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line;
			while ((line = stdoutBuffered.readLine()) != null  && !isCancelled()) {
				System.out.println(line);
			}
			
			
			if (!isCancelled()) {
				_exitStatus = process.waitFor();
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
		_button.setEnabled(true);
	}

}

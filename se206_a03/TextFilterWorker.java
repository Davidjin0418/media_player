package se206_a03;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;

public class TextFilterWorker extends SwingWorker<Integer, Integer> {
	
	File _videoFile;
	JProgressBar _bar;
	Font _textFont;
	Color _textColour;
	String _openText;
	String _closeText;
	JButton _button;
	int _exitStatus;
	
	public TextFilterWorker(File file , JProgressBar progress, JTextPane _textForOpen, JTextPane _textForClose, JButton _okButton) {
		_videoFile = file;
		_bar = progress;
		_textFont = _textForOpen.getFont();
		_textColour = _textForOpen.getForeground();
		_openText= _textForOpen.getText();
		_closeText = _textForClose.getText();
		_button = _okButton;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		// TODO Auto-generated method stub
		//command to get the duration
		StringBuilder durationCmd = new StringBuilder("avconv");
		durationCmd.append(" -i " + _videoFile.getAbsolutePath());
		
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
		StringBuilder cmd  = new StringBuilder("avconv ");		
		//path to input file, textfilter for open scene
		cmd. append(" -y -i " + _videoFile.getAbsolutePath() + " -vf \"drawtext=fontfile='");
		String fontPath;
		if (_textFont.getName().equals("Ubuntu Light")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-L.ttf':";
		}
		else if (_textFont.getName().equals("Ubuntu Medium")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-M.ttf':";
		}
		else if (_textFont.getName().equals("Ubuntu Mono")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/UbuntMono-R.ttf':";
		}
		else if (_textFont.getName().equals("Ubuntu Condensed")) {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-C.ttf':";
		}	
		else {
			fontPath ="/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-R.ttf':";
		}
		cmd.append(fontPath);
		cmd.append("text='" + _openText + "':");
		cmd.append("x=50:");
		cmd.append("y=50:");
		cmd.append("fontsize=" + _textFont.getSize() + ":");

		String red = Integer.toHexString(_textColour.getRed());
		if (red.length() == 1) {
			red = "0" + red;
		}
		String green = Integer.toHexString(_textColour.getGreen());
		if (green.length() == 1) {
			green = "0" + green;
		}
		String blue = Integer.toHexString(_textColour.getBlue());
		if (blue.length() == 1) {
			blue = "0" + blue;
		}
		cmd.append("fontcolor=0x" + red+green+blue + ":");
		
		cmd.append("draw='lt(t,10)':");
		
		//text filter for closing scene
		cmd.append(",drawtext=fontfile='");
		cmd.append(fontPath);
		cmd.append("text='" + _closeText + "':");
		cmd.append("x=50:");
		cmd.append("y=200:");
		cmd.append("fontsize=" + _textFont.getSize() + ":");
		cmd.append("fontcolor=0x" + red+green+blue + ":");
		cmd.append("draw='gt(t," + ((int)timeLength-10) +")':\"");
		
		//using the same audio from the source file so don't need to re encode the audio file again.
		cmd.append(" -c:a copy ");
		
		cmd.append("[TEXTFILTER]" + _videoFile.getName());
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd.toString());
		builder.redirectErrorStream(true);
		
		Process process;
		try {
			
			process = builder.start();

			//getting the input stream to read the command output
			InputStream stdout = process.getInputStream();		
			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
			String line;
			String duration = null;
			String progress = null;
			double bitrate = 0;
			double currentSize = 0;
			//22 character (0 -21 index)
			Pattern durationPat = Pattern.compile("Duration:\\s(\\d\\d):(\\d\\d):(\\d\\d.\\d\\d),");
			Pattern progressPat = Pattern.compile("L?size=\\s*(\\d*)kB\\stime=\\d*.\\d*\\sbitrate=\\s(\\d*.\\d)kbits/s");
			

			// time[s]*bitrate[kbps] = size[MB]
			while ((line = stdoutBuffered.readLine()) != null  && !isCancelled()) {
				
				Matcher progressMatcher = progressPat.matcher(line);
				if (progressMatcher.find()) {
					progress = progressMatcher.group(0);
					currentSize = Double.parseDouble(progressMatcher.group(1)) / 1024;
					bitrate = Double.parseDouble(progressMatcher.group(2));
					double finalSize = timeLength*bitrate /(8*1024);
					double percentage = (currentSize/finalSize)*100;
					publish((int)percentage);
					
				}
				
				
				
				//System.out.println(line);
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
		if (!isCancelled()) {
			if (_exitStatus == 0) {
				_bar.setValue(100);
				_bar.setString("Done");
			}
			else {
				//need indicate to the user. 
			}	
		}
		else {
			_bar.setValue(0);
			_bar.setString("Cancelled");
		}
		_button.setEnabled(true);
		
	}
	
	public void process(List<Integer> n) {
		for (Integer currentPercentage : n) {
			_bar.setValue(currentPercentage);
			_bar.setString(currentPercentage + "%");
		}
	}

}

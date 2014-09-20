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

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class TextFilterWorker extends SwingWorker<Integer, Integer> {
	
	File _videoFile;
	JProgressBar _bar;
	Font _textFont;
	Color _textColour;
	String _text;
	int _exitStatus;
	
	public TextFilterWorker(File file , JProgressBar progress, Font font, Color color, String msg) {
		_videoFile = file;
		_bar = progress;
		_textFont = font;
		_textColour = color;
		_text= msg;
	}

	@Override
	protected Integer doInBackground() throws Exception {
		// TODO Auto-generated method stub
		StringBuilder cmd  = new StringBuilder("avconv ");		
		//path to input file
		cmd. append(" -y -i " + _videoFile.getAbsolutePath() + " -vf \"drawtext=fontfile='");
		//path to font
		//_textFont.getFile
		if (_textFont.getName().equals("Ubuntu")) {
			cmd.append("/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-R.ttf':");
		}
		else if (_textFont.getName().equals("Ubuntu Light")) {
			cmd.append("/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-L.ttf':");
		}
		else if (_textFont.getName().equals("Ubuntu Medium")) {
			cmd.append("/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-M.ttf':");
		}
		else if (_textFont.getName().equals("Ubuntu Mono")) {
			cmd.append("/usr/share/fonts/truetype/ubuntu-font-family/UbuntMono-R.ttf':");
		}
		else if (_textFont.getName().equals("Ubuntu Condensed")) {
			cmd.append("/usr/share/fonts/truetype/ubuntu-font-family/Ubuntu-C.ttf':");
		}	
	
		cmd.append("text='" + _text + "':");
		cmd.append("x=50:");
		cmd.append("y=50:");
		cmd.append("fontsize=" + _textFont.getSize() + ":");
		//33914F
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
		cmd.append("fontcolor=0x" + red+green+blue + ":\"");
		
		
		
		if (_videoFile.getAbsolutePath().contains(".mp4")) {
			cmd.append(" -strict experimental ");
		}
		
		cmd.append("[TEXTFILTER]" + _videoFile.getName());
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
			String duration = null;
			String progress = null;
			double timeLength = 0;
			double bitrate = 0;
			double currentSize = 0;
			//22 character (0 -21 index)
			Pattern durationPat = Pattern.compile("Duration:\\s(\\d\\d):(\\d\\d):(\\d\\d.\\d\\d),");
			Pattern progressPat = Pattern.compile("L?size=\\s*(\\d*)kB\\stime=\\d*.\\d*\\sbitrate=\\s(\\d*.\\d)kbits/s");
			

			// time[s]*bitrate[kbps] = size[MB]
			while ((line = stdoutBuffered.readLine()) != null  && !isCancelled()) {
				Matcher durationMatcher = durationPat.matcher(line);
				if (durationMatcher.find()) {
					double second = Double.parseDouble(durationMatcher.group(3));
					double minute = Double.parseDouble(durationMatcher.group(2));
					double hour = Double.parseDouble(durationMatcher.group(1));
					
					timeLength = (3600*hour) + (60*minute) + second;
				}
				
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
			}
			else {
				//need indicate to the user. 
			}
		}
		
	}
	
	public void process(List<Integer> n) {
		for (Integer currentPercentage : n) {
			_bar.setValue(currentPercentage);
		}
	}

}

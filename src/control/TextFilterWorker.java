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

public class TextFilterWorker extends SwingWorker<Integer, Integer> {
	
	protected File _videoFile;
	protected JProgressBar _bar;
	protected Font _openTextFont;
	protected Color _openTextColour;
	
	protected Font _closeTextFont;
	protected Color _closeTextColour;
	
	protected String _openText;
	protected String _closeText;
	protected JButton _button;
	protected int _exitStatus;
	protected double _timeLength;
	
    protected File _closeTempFile;
    protected File _openTempFile;
    
    protected File _outputFile;
	
	public TextFilterWorker(File file , JProgressBar progress, JTextPane _textForOpen, JTextPane _textForClose, JButton _okButton, File outputFile) {
		_videoFile = file;
		_bar = progress;
		_openTextFont = _textForOpen.getFont();
		_openTextColour = _textForOpen.getForeground();
		_openText= _textForOpen.getText();
		
		_closeTextFont = _textForClose.getFont();
		_closeTextColour = _textForClose.getForeground();
		_closeText = _textForClose.getText();
		_button = _okButton;
		_outputFile = outputFile;
	}

	public TextFilterWorker() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Integer doInBackground() throws Exception {
		// TODO Auto-generated method stub
		//command to get the duration
		parseDuration();
		
		//command to process the video
		StringBuilder cmd  = new StringBuilder("avconv ");		
		//path to input file, textfilter for open scene
		cmd. append(" -y -i " + "\"" + _videoFile.getAbsolutePath() + "\"" + " -vf \"drawtext=fontfile='");
		
		cmd.append(this.createTextParameter());
		//using the same audio from the source file so don't need to re encode the audio file again.
		cmd.append(" -c:a copy ");
		
		cmd.append("\"" + _outputFile.getAbsolutePath() + "\"");
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
			Pattern progressPat = Pattern.compile("L?size=\\s*(\\d*)kB\\stime=\\d*.\\d*\\sbitrate=\\s*(\\d*.\\d)kbits/s");
			

			// time[s]*bitrate[kbps] = size[MB]
			while ((line = stdoutBuffered.readLine()) != null  && !isCancelled()) {
				
				Matcher progressMatcher = progressPat.matcher(line);
				if (progressMatcher.find()) {
					progress = progressMatcher.group(0);
					currentSize = Double.parseDouble(progressMatcher.group(1)) / 1024;
					bitrate = Double.parseDouble(progressMatcher.group(2));
					double finalSize = _timeLength*bitrate /(8*1024);
					double percentage = (currentSize/finalSize)*100;
					publish((int)percentage);
					
				}
	
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
		removeTempFile();
	}
	
	public void process(List<Integer> n) {
		for (Integer currentPercentage : n) {
			_bar.setValue(currentPercentage);
			_bar.setString(currentPercentage + "%");
		}
	}
	
	protected void parseDuration () {
		StringBuilder durationCmd = new StringBuilder("avconv");
		durationCmd.append(" -i " + "\""+ _videoFile.getAbsolutePath() + "\"");
		
		ProcessBuilder dBuilder = new ProcessBuilder("/bin/bash", "-c", durationCmd.toString());
		dBuilder.redirectErrorStream(true);
		_timeLength = 0;
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
					
					_timeLength = (3600*hour) + (60*minute) + second;
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
	
	protected String createTextParameter() {
		
		StringBuilder cmd = new StringBuilder();
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
		

		BufferedWriter bw;
		try {
			_openTempFile = new File("openTempFile.txt");
			_openTempFile.createNewFile();
			bw = new BufferedWriter(new FileWriter(_openTempFile, false));
			PrintWriter pw = new PrintWriter(bw);
			
			//create a string builder to for making the entry string
			StringBuilder textInformation = new StringBuilder();
			
			//opening scene
			textInformation.append(_openText);
			textInformation.append(System.getProperty("line.separator"));
			
			//append the entry to the file 
			pw.append(textInformation.toString());
			pw.close();
			bw.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		cmd.append("textfile='" + _openTempFile.getAbsolutePath() + "':");
		cmd.append("x=((W/2)-(W/4)):");
		cmd.append("y=((H/2)-(h/2)):");
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
		
		try {
			_closeTempFile = new File("closeTempFile.txt");
			_closeTempFile.createNewFile();
			bw = new BufferedWriter(new FileWriter(_closeTempFile, false));
			PrintWriter pw = new PrintWriter(bw);
			
			//create a string builder to for making the entry string
			StringBuilder textInformation = new StringBuilder();
			
			//closing scene
			textInformation.append(_closeText);
			textInformation.append(System.getProperty("line.separator"));
			
			//append the entry to the file 
			pw.append(textInformation.toString());
			pw.close();
			bw.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		cmd.append("textfile='" + _closeTempFile.getAbsolutePath() + "':");
		cmd.append("x=((W/2)-(W/4)):");
		cmd.append("y=((H/2)+(h/2)):");
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
		cmd.append("draw='gt(t," + ((int)_timeLength-10) +")':\"");
		return cmd.toString();
	}
	
	protected void removeTempFile() {
		if (_closeTempFile.exists()) {
			_closeTempFile.delete();
		}
		if (_openTempFile.exists()) {
			_openTempFile.delete();
		}
	}

}

package control;

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

public class PreviewWorker extends TextFilterWorker {
	
	/*File _videoFile;
	Font _openTextFont;
	Color _openTextColour;
	
	Font _closeTextFont;
	Color _closeTextColour;
	String _openText;
	String _closeText;
	JButton _button;*/
	int _exitStatus;
	
	public PreviewWorker(File file , JTextPane _textForOpen, JTextPane _textForClose, JButton _previewButton) {
		super();
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
		parseDuration();
				
		//command to process the video
		StringBuilder cmd  = new StringBuilder("avplay ");		
		//path to input file, textfilter for open scene
		cmd.append("-vf \"drawtext=fontfile='");
		cmd.append(this.createTextParameter());
		
		//using the same audio from the source file so don't need to re encode the audio file again.
		//cmd.append(" -c:a copy ");
		cmd.append(" -i \"" + _videoFile.getAbsolutePath() + "\"");
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
		removeTempFile();
	}

}

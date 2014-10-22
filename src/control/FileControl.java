package control;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import view.PlayFrame;
import main.Main;

public class FileControl {
	public static void chooseFile(JTextField currentFile) throws IOException {

		final JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Audio/mp3",
				"mp3"));
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Video/mp4",
				"mp4"));
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Video/avi",
				"avi"));
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			if (isAudioVideoFile(file).equals("audio")
					|| isAudioVideoFile(file).equals("video")) {
				Main.file = file;
				currentFile
						.setText(Main.file.getCanonicalPath() + " is chosen");
			} else {
				JOptionPane.showMessageDialog(null, "ERROR: " + file.getName()
						+ " does not refer to a valid audio/video file");
				chooseFile(currentFile);
			}
		}
	}

	/**
	 * Checking if the file is audio or video file
	 * 
	 * @param File
	 *            file
	 * @return a string indicating what type of file it is. It will return
	 *         "audio" if it's audio file, return "video" if it's video file.
	 *         Return "error" if there is an error trying to identify the file
	 *         type Return "other" if the file is other file type
	 */
	public static String isAudioVideoFile(File file) {
		String s = "file --mime-type " + "\"" + file.getAbsolutePath() + "\"";
		ProcessBuilder pb = new ProcessBuilder("bash", "-c", s);
		pb.redirectErrorStream(true);
		Process process;
		try {
			process = pb.start();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			// used string contain method rather than java regex pattern and
			// matcher to avoid dealing with special characters
			String out;
			while ((out = stdout.readLine()) != null) {
				if (out.contains(file.getAbsolutePath() + ": audio"))
					return "audio";
				if (out.contains(file.getAbsolutePath() + ": video"))
					return "video";
			}

			if (process.waitFor() != 0) {
				return "error";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "other";
	}

	public static boolean checkFileExist(String fileName) {
		StringBuilder cmd = new StringBuilder();

		cmd.append("test -e ");
		cmd.append("\"");
		cmd.append(fileName);
		cmd.append("\"");

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c",
				cmd.toString());

		Process process;
		try {
			process = builder.start();

			// 1 mean false, the file doesn't exist
			int exitStatus = process.waitFor();
			process.waitFor();
			process.getInputStream().close();
			process.getOutputStream().close();
			process.getErrorStream().close();
			process.destroy();
			if (exitStatus == 1) {
				return false;
			} else {
				return true;
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	public static boolean isOverWrite(String fileName) {

		int overWrite = JOptionPane
				.showConfirmDialog(
						null,
						fileName
								+ " already exists. Do you want to overwrite the current file ?",
						"Message", JOptionPane.YES_NO_OPTION);

		return (overWrite == 0);
	}
	
	public static String chooseInputAudioFile() throws IOException {

		final JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Audio/mp3",
				"mp3"));
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			if (FileControl.isAudioVideoFile(file).equals("audio")) {
				return file.getAbsolutePath();
			} else {
				JOptionPane.showMessageDialog(null, "ERROR: " + file.getName()
						+ " does not refer to a valid audio file");
				return chooseInputAudioFile();

			}
		} else if (returnVal == JFileChooser.CANCEL_OPTION) {
			return null;
		}
		return null;

	}
}

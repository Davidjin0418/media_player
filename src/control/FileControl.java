package control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import view.PlayFrame;
import main.Main;

/**
 * this class has all the static method for the file control
 */
public class FileControl {
	/**
	 * @param currentFile
	 *            the text field that display the current file
	 * @throws IOException
	 */
	public static void chooseInpuMediaFile(JTextField currentFile)
			throws IOException {
		// choose the file
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
			// check the file
			if (isAudioVideoFile(file).equals("audio")
					|| isAudioVideoFile(file).equals("video")) {
				Main.file = file;
				currentFile
						.setText(Main.file.getCanonicalPath() + " is chosen");
			} else {
				JOptionPane.showMessageDialog(null, "ERROR: " + file.getName()
						+ " does not refer to a valid audio/video file");
				chooseInpuMediaFile(currentFile);
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

			e.printStackTrace();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		return "other";
	}

	/**
	 * check if the file exists
	 * 
	 * @param filename
	 *            if the file exists
	 * @return true for file exists false for not exists
	 */
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
			e.printStackTrace();
		}

		return false;

	}

	/**
	 * check if it overwrites the existing file.
	 * 
	 * @param file
	 *            to overwrite
	 * @return true for overwriting ,false for not overwriting
	 */
	public static boolean isOverWrite(String fileName) {

		int overWrite = JOptionPane
				.showConfirmDialog(
						null,
						fileName
								+ " already exists. Do you want to overwrite the current file ?",
						"Message", JOptionPane.YES_NO_OPTION);

		return (overWrite == 0);
	}

	/**
	 * choose the audio file for input.
	 * 
	 * @throws IOException
	 * @return the file name that was chosen.
	 */
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

	/**
	 * choose the path
	 * 
	 * @return the path chosen
	 */
	public static String choosePath() {
		// from http://www.rgagnon.com/javadetails/java-0370.html
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Choose a diretory to save file");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//
		// disable the "All files" option.
		//
		chooser.setAcceptAllFileFilterUsed(false);
		// path default to root directory
		String path = "/";
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			path = chooser.getSelectedFile().getAbsolutePath();
			return path;
		} else {
			return null;
		}

	}

	/**
	 * choose the name for the output file. the path will be chosen first
	 * 
	 * @see choosePath()
	 * 
	 * @return the name for the output file ,it also includes path
	 */
	public static String chooseOutputFileName() {
		String path = FileControl.choosePath();
		if (path != null) {
			String outputFileName = JOptionPane.showInputDialog(
					"Please enter the output file name without extension",
					"output");
			if (!(outputFileName == null)) {
				File f1 = new File(path + "/" + outputFileName + ".mp3");
				File f2 = new File(path + "/" + outputFileName + ".mp4");
				File f3 = new File(path + "/" + outputFileName + ".png");
				// check if file exists
				if (f1.exists() || f2.exists() || f3.exists()) {
					String[] options = { "Yes", "Cancel" };
					int selection = JOptionPane.showOptionDialog(null,
							outputFileName
									+ " exists,Do you want to override it?",
							"Warning", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options,
							options[0]);
					if (selection == 0) {
						// override it
						return path + "/" + outputFileName;
					} else {
						// choose the new file name
						JOptionPane.showMessageDialog(null,
								"Please choose another output file name");
						return chooseOutputFileName();

					}
				} else {
					// no file with same name exists
					return path + "/" + outputFileName;
				}
			}
		} else {
			return null;
		}
		return null;
	}

	/**
	 * It chooses a new media file in the play frame
	 * 
	 * @param player
	 *            the media player component
	 * @param f
	 *            the play frame
	 * @throws IOException
	 */
	public static void chooseNewMediaFile(EmbeddedMediaPlayerComponent player,
			PlayFrame f) throws IOException {
		player.getMediaPlayer().pause();
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
				f.setTitle(Main.file.getName());
				player.getMediaPlayer().playMedia(Main.file.getAbsolutePath());
				writeToHistory();
			} else {
				JOptionPane.showMessageDialog(null, "ERROR: " + file.getName()
						+ " does not refer to a valid audio/video file");

			}
		}
	}

	/**
	 * write the file that is played to the history file under /.vamix folder
	 * ,it does not add the same file twice.
	 * 
	 * @throws IOException
	 */
	public static void writeToHistory() throws IOException {
		String root = System.getProperty("user.home");
		File history = new File(root + "/.vamix/history.txt");
		// write to history
		if (!history.exists()) {
			history.createNewFile();
		}
		boolean isRepeat = false;
		BufferedReader br = new BufferedReader(new FileReader(history));
		try {

			String line = br.readLine();

			while (line != null) {
				// check if the file already exists
				if (Main.file.getAbsolutePath().equals(line)) {
					isRepeat = true;
					break;
				}
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		if (!isRepeat) {
			FileWriter fw = new FileWriter(history, true);
			fw.write(Main.file.getAbsolutePath() + "\n");
			fw.close();
		}
	}

	/**
	 * method to ask user if the file is open source.
	 * 
	 * @return if the file is open source ,return true ,else return false.
	 */
	public static boolean isOpenSource() {
		int yn = JOptionPane.showConfirmDialog(null,
				"Is the media file open-source?", "Warning",
				JOptionPane.YES_NO_OPTION);
		if (yn == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * the method to choose subtitle file
	 * 
	 * @return the chosen subtitle file
	 */
	public static File chooseSubtitle() {
		final JFileChooser fc = new JFileChooser();
		fc.addChoosableFileFilter(new FileNameExtensionFilter("subtitle/srt",
				"srt"));
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file;

		} else if (returnVal == JFileChooser.CANCEL_OPTION) {
			return null;
		}
		return null;
	}
}

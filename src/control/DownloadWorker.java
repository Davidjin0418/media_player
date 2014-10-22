package control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import view.MainFrame;

    public class DownloadWorker extends SwingWorker<Void, Integer> {
	private String fileName;
	private int exitStatus;
	private String url;
	private JProgressBar progressBar;
	private JButton btnStartDownload;
	private boolean isDownloading;

	public DownloadWorker(String link, JProgressBar bar, JButton button,
			boolean download) {
		btnStartDownload = button;
		url = link;
		progressBar = bar;
		isDownloading = download;
	}

	@Override
	protected Void doInBackground() throws Exception {
		// String cmd = "wget " + "\"" + url + "\" -O ./" + fileName;
		ProcessBuilder pb = new ProcessBuilder("wget", url, "-O", fileName);

		pb.redirectErrorStream(true);

		Process process;
		try {
			// start the wget command
			process = pb.start();

			// getting the input stream to read the command output
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(
					new InputStreamReader(stdout));
			String line;

			// reading each line of the command's output
			while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
				// parsing each line to find the % completion of the mp3
				// file
				Pattern patt = Pattern.compile("(\\d{1,3})%");
				Matcher mat = patt.matcher(line);
				if (mat.find()) {
					// checking where the download is on the table and
					// publish the %
					publish(Integer.parseInt(mat.group(1)));
				}
			}

			// wait for the wget command to finish if the worker is not
			// cancelled
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

	protected void process(List<Integer> chunks) {
		if (!isCancelled()) {
			for (int i : chunks) {
				String progress = Integer.toString(i);
				progressBar.setValue(i);
				progressBar.setString(progress + "%");
			}
		}
	}

	protected void done() {
		if (!isCancelled()) {
			boolean removeFile = true;
			progressBar.setString("Error");
			switch (exitStatus) {

			case 0:
				progressBar.setString("Download Completed");
				removeFile = false;
				break;
			case 1:
				JOptionPane.showMessageDialog(null,
						"Download Error: Generic error code");
				break;
			case 2:
				JOptionPane.showMessageDialog(null,
						"Download Error: Parse Error");
				break;
			case 3:
				JOptionPane.showMessageDialog(null,
						"Download Error: File I/O error");
				break;
			case 4:
				JOptionPane.showMessageDialog(null,
						"Download Error: Network Failure");
				break;
			case 5:
				JOptionPane.showMessageDialog(null,
						"Download Error: SSL Vertification Faiure");
				break;
			case 6:
				JOptionPane
						.showMessageDialog(null,
								"Download Error: Username/Password authentications Failure");
				break;
			case 7:
				JOptionPane.showMessageDialog(null,
						"Download Error: Protocol error");
				break;
			case 8:
				JOptionPane.showMessageDialog(null,
						"Download Error: Server issued error response");
				break;
			}

			if (removeFile == true) {
				StringBuilder cmd = new StringBuilder();

				cmd.append("rm " + "\"" + fileName + "\"");

				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c",
						cmd.toString());

				Process process;
				try {
					process = builder.start();

					// 1 mean false, the file doesn't exist
					process.waitFor();
					process.getInputStream().close();
					process.getOutputStream().close();
					process.getErrorStream().close();
					process.destroy();
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			progressBar.setString("Download Cancelled");
		}

		btnStartDownload.setText("Start Download");
		isDownloading = false;
	}

}
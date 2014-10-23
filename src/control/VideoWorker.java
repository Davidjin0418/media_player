package control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import main.Main;

//swingworker for editing the video
	public class VideoWorker extends SwingWorker<Void, Integer> {
		private String cmd;
		private int exitStatus;
		private double _timeLength;
     private JProgressBar editProgressBar;
		public VideoWorker(String c,JProgressBar bar) {
			editProgressBar=bar;
			cmd = c;
		}

		protected void process(List<Integer> chunks) {
			if (!isCancelled()) {
				for (int i : chunks) {
					String progress = Integer.toString(i);
					editProgressBar.setValue(i);
					editProgressBar.setString(progress + "%");
				}
			}
		}

		protected void done() {
			if (!isCancelled()) {
				if (exitStatus == 0) {
					editProgressBar.setValue(100);
					editProgressBar.setString("Successful");
				} else {
					editProgressBar.setValue(0);
					editProgressBar.setString("Error");
				}
			}

		}

		protected void parseDuration() {
			StringBuilder durationCmd = new StringBuilder("avconv");
			durationCmd.append(" -i " + "\"" + Main.file.getAbsolutePath()
					+ "\"");

			ProcessBuilder dBuilder = new ProcessBuilder("/bin/bash", "-c",
					durationCmd.toString());
			dBuilder.redirectErrorStream(true);
			_timeLength = 0;
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
				while ((line = stdoutBuffered.readLine()) != null
						&& !isCancelled()) {
					Matcher durationMatcher = durationPat.matcher(line);
					if (durationMatcher.find()) {
						double second = Double.parseDouble(durationMatcher
								.group(3));
						double minute = Double.parseDouble(durationMatcher
								.group(2));
						double hour = Double.parseDouble(durationMatcher
								.group(1));

						_timeLength = (3600 * hour) + (60 * minute) + second;
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

		@Override
		protected Void doInBackground() throws Exception {
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			parseDuration();
			pb.redirectErrorStream(true);
			Process process;
			process = pb.start();
			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(
					new InputStreamReader(stdout));
			String line;

			double bitrate = 0;
			double currentSize = 0;
			// 22 character (0 -21 index)

			Pattern progressPat = Pattern
					.compile("L?size=\\s*(\\d*)kB\\stime=\\d*.\\d*\\sbitrate=\\s*(\\d*.\\d)kbits/s");

			// time[s]*bitrate[kbps] = size[MB]
			while ((line = stdoutBuffered.readLine()) != null && !isCancelled()) {
				Matcher progressMatcher = progressPat.matcher(line);
				if (progressMatcher.find()) {
					currentSize = Double.parseDouble(progressMatcher.group(1)) / 1024;
					bitrate = Double.parseDouble(progressMatcher.group(2));
					double finalSize = _timeLength * bitrate / (8 * 1024);
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
			return null;

		}
		
	}
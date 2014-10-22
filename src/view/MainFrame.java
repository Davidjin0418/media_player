package view;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JProgressBar;

import main.Main;
import model.FrameAdapter;
import model.MainAddTextButton;
import model.MainChooseButton;
import control.*;

import java.awt.Dimension;

public class MainFrame extends FrameAdapter implements ActionListener {

	private JPanel mainPane;
	private JTextField downloadURL;
	private JTextField currentFIle;
	private JButton btnStartDownload;
	private MainChooseButton btnChooseAFile;
	private JButton btnPlay;
	private MainAddTextButton btnAddText;
	private JProgressBar progressBar;

	private DownloadWorker currentDownloadWorker = null;
	private boolean isDownloading = false;

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	public MainFrame() throws IOException {
		this.initialisation();
		JTabbedPane mainTabPane = new JTabbedPane();
		JPanel downloadPanel = new JPanel();

		downloadPanel.setBackground(new Color(238, 238, 238));
		downloadPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		downloadPanel.setLayout(null);
		setTitle("Welcome to VAMIX");

		this.addWindowListener(this);
		mainPane = new JPanel();
		mainPane.setBackground(new Color(238, 238, 238));
		mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		// setContentPane(mainPane);
		setContentPane(mainTabPane);
		mainPane.setLayout(null);

		mainTabPane.add("Main", mainPane);
		mainTabPane.add("Download", downloadPanel);

		downloadURL = new JTextField();
		downloadURL.setBounds(13, 32, 259, 28);
		downloadURL.setColumns(10);
		downloadPanel.add(downloadURL);

		btnStartDownload = new JButton("Start download");
		btnStartDownload.addActionListener(this);

		JLabel lblEnterTheUrl = new JLabel("Enter the URL to download:");
		lblEnterTheUrl.setBounds(12, 12, 200, 16);
		downloadPanel.add(lblEnterTheUrl);
		btnStartDownload.setBounds(275, 31, 175, 29);
		downloadPanel.add(btnStartDownload);

		JLabel lblDownloadProgress = new JLabel("Download Progress:");
		lblDownloadProgress.setBounds(12, 72, 150, 20);
		downloadPanel.add(lblDownloadProgress);

		progressBar = new JProgressBar();
		// original width 146
		progressBar.setBounds(167, 72, 284, 20);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setStringPainted(true);
		progressBar.setString("");
		downloadPanel.add(progressBar);

		JLabel lblTheCurrentFile = new JLabel("The current file is:");
		// lblTheCurrentFile.setBounds(6, 88, 200, 16);
		lblTheCurrentFile.setBounds(12, 12, 200, 16);
		mainPane.add(lblTheCurrentFile);

		currentFIle = new JTextField();
		// currentFIle.setBounds(5, 116, 272, 28);
		currentFIle.setBounds(13, 32, 302, 28);
		currentFIle.setColumns(10);
		mainPane.add(currentFIle);

		btnChooseAFile = new MainChooseButton(currentFIle);
		btnChooseAFile.setBounds(327, 31, 126, 29);
		mainPane.add(btnChooseAFile);

		btnPlay = new JButton("Play");
		btnPlay.addActionListener(this);

		btnPlay.setBounds(137, 72, 75, 29);
		mainPane.add(btnPlay);

		btnAddText = new MainAddTextButton();
		btnAddText.setBounds(247, 72, 101, 29);
		mainPane.add(btnAddText);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(new Dimension(482, 171));
		this.setPreferredSize(new Dimension(482, 171));
		this.setResizable(false);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == btnAddText) {
			// create a text window.
			if (Main.file != null) {
				if (FileControl.isAudioVideoFile(Main.file).equals("video")) {
					TextFrame textframe = new TextFrame(btnAddText);
					btnAddText.setEnabled(false);
				} else {
					JOptionPane
							.showMessageDialog(
									null,
									"ERROR: "
											+ Main.file.getName()
											+ " is an audio file, can't apply text onto it");
				}
			}
		} else if (arg0.getSource() == btnPlay) {
			// create a play frame window.
			if (Main.file != null) {
				try {
					String root = System.getProperty("user.home");
					File history = new File(root + "/.vamix/history.txt");
					// write to history
					if (!history.exists()) {
						history.createNewFile();
					}
					FileWriter fw = new FileWriter(history, true);
					fw.write(Main.file.getAbsolutePath() + "\n");
					fw.close();

					PlayFrame playframe = new PlayFrame(btnPlay);
				} catch (IOException e) {
					e.printStackTrace();
				}
				btnPlay.setEnabled(false);
			}
		} else if (arg0.getSource() == btnStartDownload) {
			if (isDownloading == false) {
				if (downloadURL.getText().equals("")) {
					JOptionPane
							.showMessageDialog(this,
									"ERROR: The url is empty, please enter an url to download");
					return;
				}

				// Retrieve the url from the text field and cut out the end of
				// it
				String url = downloadURL.getText();
				String fileName = url.substring(url.lastIndexOf('/') + 1);
				if (FileControl.checkFileExist(fileName)
						&& !FileControl.isOverWrite(fileName))
					return;
				DownloadWorker worker = new DownloadWorker(url, progressBar,
						btnStartDownload, isDownloading);
				isDownloading = true;
				btnStartDownload.setText("Cancel Download");
				currentDownloadWorker = worker;
				worker.execute();
			} else {
				if (currentDownloadWorker != null) {
					currentDownloadWorker.cancel(true);
					progressBar.setValue(0);
				}

			}
		}

	}

	private void initialisation() {
		File file = new File(System.getProperty("user.home") + "/.vamix");
		if (!file.exists()) {
			file.mkdir();
		}
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (isDownloading == false) {
			if (this.btnAddText.isEnabled() == false
					| this.btnPlay.isEnabled() == false) {
				int yn = JOptionPane
						.showConfirmDialog(
								this,
								"Closing this window will close Play Frame or Text Editing Frame as well, do you want to continue ?",
								"Warning", JOptionPane.YES_NO_OPTION);

				if (yn == 0) {
					System.exit(0);
				}
			} else {
				System.exit(0);
			}
		} else {
			int yn = JOptionPane
					.showConfirmDialog(
							this,
							"The program is downloading a file, would you like to cancel the download and quit the program ?",
							"Warning", JOptionPane.YES_NO_OPTION);

			if (yn == 0) {
				if (this.btnAddText.isEnabled() == false) {
					int confirm = JOptionPane
							.showConfirmDialog(
									this,
									"Closing this window will close Play Frame or Text Editing Frame as well, do you want to continue ?",
									"Warning", JOptionPane.YES_NO_OPTION);

					if (confirm == 0) {
						this.currentDownloadWorker.cancel(true);
						System.exit(0);

					}
				} else {
					System.exit(0);
				}
			}
		}
	}

}

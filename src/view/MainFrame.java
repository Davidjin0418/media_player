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

import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JProgressBar;

import model.MainAddTextButton;
import model.MainChooseButton;
import model.MainDownloadButton;
import model.MainPlayButton;
import control.*;

import java.awt.Dimension;

/**
 * 
 * @author bojun
 * 
 */
public class MainFrame extends JFrame {

	private JPanel mainPane;
	private JTextField downloadURL;
	private JTextField currentFIle;
	private JButton btnStartDownload;
	private MainChooseButton btnChooseAFile;
	private MainPlayButton btnPlay;
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
		setBackground(Color.LIGHT_GRAY);
		this.initialisation();
		JTabbedPane mainTabPane = new JTabbedPane();
		mainTabPane.setBackground(Color.LIGHT_GRAY);
		JPanel downloadPanel = new JPanel();

		downloadPanel.setBackground(Color.LIGHT_GRAY);
		downloadPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		downloadPanel.setLayout(null);
		setTitle("Welcome to VAMIX");

		mainPane = new JPanel();
		mainPane.setBackground(Color.LIGHT_GRAY);
		mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainTabPane);
		mainPane.setLayout(null);

		mainTabPane.add("Main", mainPane);
		mainTabPane.add("Download", downloadPanel);

		downloadURL = new JTextField();
		downloadURL.setBounds(13, 32, 259, 28);
		downloadURL.setColumns(10);
		downloadPanel.add(downloadURL);

		JLabel lblEnterTheUrl = new JLabel("Enter the URL to download:");
		lblEnterTheUrl.setBounds(12, 12, 200, 16);
		downloadPanel.add(lblEnterTheUrl);

		JLabel lblDownloadProgress = new JLabel("Download Progress:");
		lblDownloadProgress.setBounds(12, 72, 150, 20);
		downloadPanel.add(lblDownloadProgress);

		progressBar = new JProgressBar();
		progressBar.setBounds(167, 72, 284, 20);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setStringPainted(true);
		progressBar.setString("");
		downloadPanel.add(progressBar);

		JLabel lblTheCurrentFile = new JLabel("The current file is:");

		lblTheCurrentFile.setBounds(12, 12, 200, 16);
		mainPane.add(lblTheCurrentFile);

		currentFIle = new JTextField();
		currentFIle.setBounds(13, 32, 302, 28);
		currentFIle.setColumns(10);
		mainPane.add(currentFIle);

		btnChooseAFile = new MainChooseButton(currentFIle);
		btnChooseAFile.setBounds(327, 31, 126, 29);
		mainPane.add(btnChooseAFile);

		btnPlay = new MainPlayButton(currentFIle);
		btnPlay.setBounds(137, 72, 75, 29);
		mainPane.add(btnPlay);

		btnAddText = new MainAddTextButton();
		btnAddText.setBounds(247, 72, 101, 29);
		mainPane.add(btnAddText);

		btnStartDownload = new MainDownloadButton(isDownloading, downloadURL,
				progressBar, currentDownloadWorker);
		btnStartDownload.setBounds(275, 31, 175, 29);
		downloadPanel.add(btnStartDownload);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(new Dimension(482, 171));
		this.setPreferredSize(new Dimension(482, 171));
		this.setResizable(false);
		this.setVisible(true);

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (isDownloading == false) {
					if (btnAddText.isEnabled() == false
							| btnPlay.isEnabled() == false) {
						int yn = JOptionPane
								.showConfirmDialog(
										null,
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
									null,
									"The program is downloading a file, would you like to cancel the download and quit the program ?",
									"Warning", JOptionPane.YES_NO_OPTION);

					if (yn == 0) {
						if (btnAddText.isEnabled() == false) {
							int confirm = JOptionPane
									.showConfirmDialog(
											null,
											"Closing this window will close Play Frame or Text Editing Frame as well, do you want to continue ?",
											"Warning",
											JOptionPane.YES_NO_OPTION);

							if (confirm == 0) {
								currentDownloadWorker.cancel(true);
								System.exit(0);

							}
						} else {
							System.exit(0);
						}
					}
				}
			}

		});
	}

	private void initialisation() {
		File file = new File(System.getProperty("user.home") + "/.vamix");
		if (!file.exists()) {
			file.mkdir();
		}
		Dimension dimension=Toolkit.getDefaultToolkit().getScreenSize();
		int x= (int)((dimension.getWidth()-this.getWidth())/2);
		int y= (int)((dimension.getHeight()-this.getHeight())/2);
		this.setLocation(x, y);
	}

}

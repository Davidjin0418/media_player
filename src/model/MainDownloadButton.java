package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import control.DownloadWorker;
import control.FileControl;

public class MainDownloadButton extends JButton implements ActionListener{
	private Boolean isDownloading;
	private JTextField downloadURL;
	private JProgressBar progressBar;
	private DownloadWorker currentDownloadWorker;
    public MainDownloadButton(Boolean download,JTextField field, JProgressBar bar,DownloadWorker worker){
    	this.setText("Start download");
    	this.addActionListener(this);
    	isDownloading=download;
    	downloadURL=field;
    	progressBar=bar;
    	currentDownloadWorker=worker;
    	
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this){
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
						this, isDownloading);
				isDownloading = true;
				this.setText("Cancel Download");
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

}

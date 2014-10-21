package se206_a03;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import java.awt.Dimension;


public class MainFrame extends JFrame implements ActionListener, WindowListener {
    
    private JPanel mainPane;
    private JTextField downloadURL;
    private JTextField currentFIle;
    private JButton btnStartDownload;
    private JButton btnChooseAFile;
    private JButton btnPlay;
    private JButton btnAddText;
    private JProgressBar progressBar;
    
    private DownloadWorker _currentDownloadWorker = null;
    private boolean isDownloading = false;
    
    /**
     * Create the frame.
     * @throws IOException 
     */
    public MainFrame() throws IOException {
    	this.initialisation();
    	JTabbedPane mainTabPane = new JTabbedPane();
    	JPanel downloadPanel = new JPanel();
    	
    	downloadPanel.setBackground(new Color(238,238,238));
    	downloadPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    	downloadPanel.setLayout(null);
        setTitle("Welcome to VAMIX");
        
        this.addWindowListener(this);
        mainPane = new JPanel();
        mainPane.setBackground(new Color(238, 238, 238));
        mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        //setContentPane(mainPane);
        setContentPane(mainTabPane);
        mainPane.setLayout(null);
        
        mainTabPane.add("Main",mainPane);
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
        //original width 146
        progressBar.setBounds(167, 72, 284, 20);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        progressBar.setString("");
        downloadPanel.add(progressBar);
       
        
        JLabel lblTheCurrentFile = new JLabel("The current file is:");
        //lblTheCurrentFile.setBounds(6, 88, 200, 16);
        lblTheCurrentFile.setBounds(12, 12, 200, 16);
        mainPane.add(lblTheCurrentFile);
        
        currentFIle = new JTextField();
        //currentFIle.setBounds(5, 116, 272, 28);
        currentFIle.setBounds(13, 32, 302, 28);
        currentFIle.setColumns(10);
        mainPane.add(currentFIle);
        
        btnChooseAFile = new JButton("Choose a file");
        btnChooseAFile.addActionListener(this);
        //btnChooseAFile.setBounds(282, 116, 126, 29);
        btnChooseAFile.setBounds(327, 31, 126, 29);
        mainPane.add(btnChooseAFile);
        
        btnPlay = new JButton("Play");
        btnPlay.addActionListener(this);
        //btnPlay.setBounds(97, 156, 75, 29);
        btnPlay.setBounds(137, 72, 75, 29);
        mainPane.add(btnPlay);
        
        btnAddText = new JButton("Add Text");
        btnAddText.addActionListener(this);
        //btnAddText.setBounds(247, 156, 101, 29);
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
            //create a text window.
        	if (Main.file !=null ) {
        		if (isAudioVideoFile(Main.file).equals("video")) {
        			TextFrame textframe = new TextFrame(btnAddText);
        			btnAddText.setEnabled(false);
        		}
        		else {
        			JOptionPane.showMessageDialog(this,
                            "ERROR: " + Main.file.getName()
                            + " is an audio file, can't apply text onto it");
        		}
        	}
        } else if (arg0.getSource() == btnChooseAFile) {
            try {
                chooseFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (arg0.getSource() == btnPlay) {
            //create a play frame window.
        	if (Main.file !=null ) {
        		PlayFrame playframe=new PlayFrame(btnPlay);
        		btnPlay.setEnabled(false);
        	}
        } else if (arg0.getSource() == btnStartDownload) {
        	if (isDownloading == false) {
	            if (downloadURL.getText().equals("")) {
	            	JOptionPane.showMessageDialog(this,"ERROR: The url is empty, please enter an url to download");
	            	return;
	            }
	            
	            //Retrieve the url from the text field and cut out the end of it
				String url = downloadURL.getText();
				String fileName = url.substring(url.lastIndexOf('/')+1);
	            if (checkFileExist(fileName) && !isOverWrite(fileName)) return;
	            DownloadWorker worker=new DownloadWorker();
	            isDownloading = true;
	            btnStartDownload.setText("Cancel Download");
	            _currentDownloadWorker = worker;
	            worker.execute();
        	}
        	else {
        		if (_currentDownloadWorker != null) {
        			_currentDownloadWorker.cancel(true);
        			progressBar.setValue(0);
        		}
        		
        	}
        }
        
    }
    class DownloadWorker extends SwingWorker<Void, Integer>{
        String fileName;
    	int exitStatus;
		@Override
		protected Void doInBackground() throws Exception {
			String url = downloadURL.getText();
			fileName = url.substring(url.lastIndexOf('/')+1);
			//String cmd = "wget " + "\"" + url + "\" -O ./" + fileName;
			ProcessBuilder pb = new ProcessBuilder("wget", url, "-O", fileName);

			pb.redirectErrorStream(true);
			
			Process process;
			try {
				//start the wget command
				process = pb.start();
				
				//getting the input stream to read the command output
				InputStream stdout = process.getInputStream();		
				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				String line;
				
				//reading each line of the command's output
				while ((line = stdoutBuffered.readLine()) != null  && !isCancelled()) {
					//parsing each line to find the % completion of the mp3 file
					Pattern patt = Pattern.compile("(\\d{1,3})%");
					Matcher mat = patt.matcher(line);
					if (mat.find()){
						//checking where the download is on the table and publish the % 
						publish(Integer.parseInt(mat.group(1)));
					}			
				}
				
				//wait for the wget command to finish if the worker is not cancelled
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
			if (!isCancelled())	 {
	    	 for (int i : chunks) {
	    		 String progress = Integer.toString(i);
	    		 progressBar.setValue(i);
	    	 	 progressBar.setString(progress + "%");
	    	 }
			}
	     }
		 protected void done(){
			 if (!isCancelled()) {
				 /*String fileName = downloadURL.getText().substring(
							downloadURL.getText().lastIndexOf('/') + 1,
							downloadURL.getText().length());
				 currentFIle.setText(fileName+" has been downloaded");*/
				 boolean removeFile = true;
				 progressBar.setString("Error");
					switch (exitStatus){
					
					case 0: progressBar.setString("Download Completed");
							removeFile = false;
							break;
					case 1: JOptionPane.showMessageDialog(MainFrame.this, "Download Error: Generic error code");
							break;
					case 2: JOptionPane.showMessageDialog(MainFrame.this, "Download Error: Parse Error");
							break;
					case 3: JOptionPane.showMessageDialog(MainFrame.this, "Download Error: File I/O error");
							break;
					case 4:JOptionPane.showMessageDialog(MainFrame.this, "Download Error: Network Failure");
							break;
					case 5: JOptionPane.showMessageDialog(MainFrame.this, "Download Error: SSL Vertification Faiure");
							break;
					case 6: JOptionPane.showMessageDialog(MainFrame.this, "Download Error: Username/Password authentications Failure");
							break;
					case 7: JOptionPane.showMessageDialog(MainFrame.this, "Download Error: Protocol error");
							break;
					case 8: JOptionPane.showMessageDialog(MainFrame.this, "Download Error: Server issued error response");
							break;
					}
					
					if (removeFile == true) {
						StringBuilder cmd = new StringBuilder();
						
						cmd.append("rm " + "\"" + fileName + "\"" );
						
						ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd.toString());
						
						Process process;
						try {
							process = builder.start();
							
							//1 mean false, the file doesn't exist
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
				 
			 }
			 else {
				 progressBar.setString("Download Cancelled");
			 }

			 btnStartDownload.setText("Start Download");
			 isDownloading = false;
		 }
    	
    }
    
    
    private void chooseFile() throws IOException {
        //using file chooser ,can only choose file with extension mp3 or mp4
        //but some file without extension may also be a valid file,need more reasearch later
        
        final JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Audio/mp3", "mp3"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Video/mp4", "mp4"));
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(MainFrame.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
           
            if ( isAudioVideoFile(file).equals("audio") || isAudioVideoFile(file).equals("video") ) {
            	Main.file = file;
                currentFIle.setText(Main.file.getCanonicalPath() + " is chosen");
            } else {
                JOptionPane.showMessageDialog(this,
                                              "ERROR: " + file.getName()
                                              + " does not refer to a valid audio/video file");
                chooseFile();
            }
        }
    }
    
    private void initialisation() {
		File file = new File(System.getProperty("user.home")+"/.vamix");
		if (!file.exists()) {
			file.mkdir();
		}
	}
    
    /**
     * Checking if the file is audio or video file
     * @param File file
     * @return a string indicating what type of file it is. 
     * It will return "audio" if it's audio file, return "video" if it's video file. 
     * Return "error" if there is an error trying to identify the file type 
     * Return "other" if the file is other file type
     */
    public static String isAudioVideoFile(File file) {
        String s = "file --mime-type " + "\"" +file.getAbsolutePath() +"\"";
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", s);
        pb.redirectErrorStream(true);
        Process process;
		try {
			process = pb.start();
			BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        
			//used string contain method rather than java regex pattern and matcher to avoid dealing with special characters
			String out;
	        while ((out = stdout.readLine()) != null ) {
				if (out.contains(file.getAbsolutePath() + ": audio")) return "audio";
				if (out.contains(file.getAbsolutePath() + ": video")) return "video";
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
    
    private boolean isOverWrite(String fileName) {
		
		int overWrite = JOptionPane.showConfirmDialog (
				this,
				fileName + " already exists. Do you want to overwrite the current file ?",
				"Message",
				 JOptionPane.YES_NO_OPTION);
		
		return (overWrite == 0);
	}
    
    private boolean checkFileExist(String fileName) {
		StringBuilder cmd = new StringBuilder();
		
		cmd.append("test -e ");
		cmd.append("\"");
		cmd.append(fileName);
		cmd.append("\"");
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd.toString());
		
		Process process;
		try {
			process = builder.start();
			
			//1 mean false, the file doesn't exist
			int exitStatus = process.waitFor();
			process.waitFor();
			process.getInputStream().close();
	        process.getOutputStream().close();
	        process.getErrorStream().close();
	        process.destroy();
			if (exitStatus == 1) {
				return false;
			}
			else  {
				return true; 
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
		
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		if (isDownloading == false) {
			if (this.btnAddText.isEnabled() == false | this.btnPlay.isEnabled()== false) {
				int yn = JOptionPane.showConfirmDialog(this,
					    "Closing this window will close Play Frame or Text Editing Frame as well, do you want to continue ?",
					    "Warning",
					    JOptionPane.YES_NO_OPTION);
				
				if (yn == 0) {
					System.exit(0);
				}
			}
			else {
				System.exit(0);
			}
		}
		else {
			int yn = JOptionPane.showConfirmDialog(this,
				    "The program is downloading a file, would you like to cancel the download and quit the program ?",
				    "Warning",
				    JOptionPane.YES_NO_OPTION);
			
			if (yn == 0) {
				if (this.btnAddText.isEnabled() == false) {
					int confirm = JOptionPane.showConfirmDialog(this,
						    "Closing this window will close Play Frame or Text Editing Frame as well, do you want to continue ?",
						    "Warning",
						    JOptionPane.YES_NO_OPTION);
					
					if (confirm == 0) {
						this._currentDownloadWorker.cancel(true);
						System.exit(0);
						
					}
				}
				else {
					System.exit(0);
				}
			}
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}

package se206_a03;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class MainFrame extends JFrame implements ActionListener {
    
    private JPanel mainPane;
    private JTextField downloadURL;
    private JTextField currentFIle;
    private JButton btnStartDownload;
    private JButton btnChooseAFile;
    private JButton btnPlay;
    private JButton btnAddText;
    private JProgressBar progressBar;
    
    private String fileType = "none";
    
    /**
     * Create the frame.
     * @throws IOException 
     */
    public MainFrame() throws IOException {
        setTitle("Welcome to VAMIX");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        mainPane = new JPanel();
        mainPane.setBackground(new Color(238, 238, 238));
        mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(mainPane);
        mainPane.setLayout(null);
        
        JLabel lblEnterTheUrl = new JLabel("Enter the URl to download:");
        lblEnterTheUrl.setBounds(5, 5, 167, 16);
        mainPane.add(lblEnterTheUrl);
        
        downloadURL = new JTextField();
        downloadURL.setBounds(5, 26, 272, 28);
        downloadURL.setColumns(10);
        mainPane.add(downloadURL);
        
        btnStartDownload = new JButton("Start download");
        btnStartDownload.addActionListener(this);
        btnStartDownload.setBounds(282, 26, 138, 29);
        mainPane.add(btnStartDownload);
        
        JLabel lblTheCurrentFile = new JLabel("The current file is:");
        lblTheCurrentFile.setBounds(6, 88, 115, 16);
        mainPane.add(lblTheCurrentFile);
        
        currentFIle = new JTextField();
        currentFIle.setBounds(5, 116, 272, 28);
        currentFIle.setColumns(10);
        mainPane.add(currentFIle);
        
        btnChooseAFile = new JButton("Choose a file");
        btnChooseAFile.addActionListener(this);
        btnChooseAFile.setBounds(282, 116, 126, 29);
        mainPane.add(btnChooseAFile);
        
        btnPlay = new JButton("Play");
        btnPlay.addActionListener(this);
        btnPlay.setBounds(97, 156, 75, 29);
        mainPane.add(btnPlay);
        
        btnAddText = new JButton("Add Text");
        btnAddText.addActionListener(this);
        btnAddText.setBounds(247, 156, 101, 29);
        mainPane.add(btnAddText);
        
        progressBar = new JProgressBar();
        progressBar.setBounds(138, 56, 146, 20);
        mainPane.add(progressBar);
        
        JLabel lblDownloadProgress = new JLabel("Download Progress:");
        lblDownloadProgress.setBounds(5, 56, 146, 20);
        mainPane.add(lblDownloadProgress);
    }
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (arg0.getSource() == btnAddText) {
            //create a text window.
        	if (Main.file !=null ) {
             TextFrame textframe = new TextFrame();
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
        		PlayFrame playframe=new PlayFrame();
        	}
        } else if (arg0.getSource() == btnStartDownload) {
            progressBar.setMinimum(0);
            progressBar.setMaximum(100);
            DownloadWorker worker=new DownloadWorker();
            worker.execute();
        }
        
    }
    class DownloadWorker extends SwingWorker<Void, Integer>{
        
    	int exitStatus;
		@Override
		protected Void doInBackground() throws Exception {
			/*for(int i=0;i<=100;i++){
				publish(i);
				String url = downloadURL.getText();
				String cmd = "wget " + url;
				ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
				pb.redirectErrorStream(true);
				pb.start();
				
			}*/
			String url = downloadURL.getText();
			String cmd = "wget " + url;
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			
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
				
	    	 for (int i : chunks)
	    		 progressBar.setValue(i);
	     }
		 protected void done(){
			 String fileName = downloadURL.getText().substring(
						downloadURL.getText().lastIndexOf('/') + 1,
						downloadURL.getText().length());
			 currentFIle.setText(fileName+" has been downloaded");
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
            Main.file = fc.getSelectedFile();
            String s = "file --mime-type " + Main.file.getPath();
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", s);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader stdout = new BufferedReader(new InputStreamReader(
                                                                             process.getInputStream()));
            String out = stdout.readLine();
            
            //used java regex to see if the output from file command contain video or audio
            //then store it into a  variable to use it.
            Pattern fileTypePattern = Pattern.compile(Main.file.getAbsolutePath()+": (audio|video)/.*");
            Matcher patternMatcher = fileTypePattern.matcher(out);
            if (patternMatcher.find()) {
            	fileType = patternMatcher.group(1);
            }
            //System.out.println(out);
            //System.out.println(Main.file.getPath() + ": audio/mpeg");
            if ( fileType.equals("audio") | fileType.equals("video")) {
                currentFIle
                .setText(Main.file.getCanonicalPath() + " is chosen");
            } else {
                JOptionPane.showMessageDialog(this,
                                              "ERROR:" + Main.file.getName()
                                              + " does not refer to a valid audio/video file");
                Main.file = null;
                chooseFile();
            }
        }
    }
}


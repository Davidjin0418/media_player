package se206_a03;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.ImageIcon;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import java.awt.Font;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

public class PlayFrame extends JFrame implements ActionListener {
    
    private JPanel contentPane;
    private JPanel panel;
    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private JButton btnPause;
    private JButton btnForward;
    private JButton btnBackward;
    private JButton btnPlay;
    private JButton btnMute;
    private JButton btnEdit;
    private JSlider Time;
    private JSlider volume;
    
    /**
     * Create the frame.
     */
    public PlayFrame() {
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        this.setVisible(true);
        this.setTitle(Main.file.getName());
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 791, 450);
        contentPane = new JPanel();
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        panel.setBounds(0, 0, this.getWidth(), this.getHeight() - 100);
        btnPlay = new JButton("Play", new ImageIcon(
                                                    PlayFrame.class.getResource("/se206_a03/png/play43.png")));
        btnPlay.setFont(new Font("Lucida Bright", Font.BOLD, 10));
        btnPlay.setBounds(10, this.getHeight() - 100, 70, 70);
        btnPlay.setVerticalTextPosition(AbstractButton.BOTTOM);
        btnPlay.setHorizontalTextPosition(AbstractButton.CENTER);
        btnPlay.addActionListener(this);
        contentPane.add(btnPlay);
        
        btnPause = new JButton("Pause", new ImageIcon(
                                                      PlayFrame.class.getResource("/se206_a03/png/pause15.png")));
        btnPause.setFont(new Font("Lucida Bright", Font.BOLD, 10));
        btnPause.setVerticalTextPosition(AbstractButton.BOTTOM);
        btnPause.setHorizontalTextPosition(AbstractButton.CENTER);
        btnPause.setBounds(90, this.getHeight() - 100, 70, 70);
        btnPause.addActionListener(this);
        contentPane.add(btnPause);
        
        btnForward = new JButton("Forward", new ImageIcon(
                                                          PlayFrame.class.getResource("/se206_a03/png/next10.png")));
        btnForward.setFont(new Font("Lucida Bright", Font.BOLD, 10));
        btnForward.setVerticalTextPosition(AbstractButton.BOTTOM);
        btnForward.setHorizontalTextPosition(AbstractButton.CENTER);
        btnForward.setBounds(170, this.getHeight() - 100, 70, 70);
        btnForward.addActionListener(this);
        contentPane.add(btnForward);
        
        btnBackward = new JButton("Backward", new ImageIcon(
                                                            PlayFrame.class.getResource("/se206_a03/png/back12.png")));
        btnBackward.setFont(new Font("Lucida Bright", Font.BOLD, 10));
        btnBackward.setVerticalTextPosition(AbstractButton.BOTTOM);
        btnBackward.setHorizontalTextPosition(AbstractButton.CENTER);
        btnBackward.setBounds(249, 350, 70, 70);
        btnBackward.addActionListener(this);
        contentPane.add(btnBackward);
        
      
        btnMute = new JButton("Mute");
        btnMute.setFont(new Font("Lucida Bright", Font.BOLD, 10));
        btnMute.addActionListener(this);
        btnMute.setBounds(331, 350, 70, 70);
        contentPane.add(btnMute);
        
        btnEdit = new JButton("Edit Video");
        btnEdit.setFont(new Font("Lucida Bright", Font.BOLD, 10));
        btnEdit.addActionListener(this);
        btnEdit.setBounds(586, 350, 70, 70);
        contentPane.add(btnEdit);
        
      
        
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        panel.setVisible(true);
        panel.add(mediaPlayerComponent, BorderLayout.CENTER);
        contentPane.add(panel);
        
        Time = new JSlider();
        panel.add(Time, BorderLayout.SOUTH);
        setContentPane(contentPane);
        
        volume = new JSlider();
        volume.setMaximum(200);
        volume.setValue(0);
        volume.setBounds(400, 374, 118, 29);
        contentPane.add(volume);
        mediaPlayerComponent.getMediaPlayer().playMedia(
                                                        Main.file.getAbsolutePath());
    }
    public void Stripaudio(){
        String[] options = { "Yes", "No" };
        int selection = JOptionPane
        .showOptionDialog(
                          null,
                          "Do you want to override the original file or save a new file?",
                          "Warning",
                          JOptionPane.DEFAULT_OPTION,
                          JOptionPane.WARNING_MESSAGE, null,
                          options, options[0]);
        if(selection ==0){
            
        }else{
            
        }
    }
    public String chooseFileName(){
    	String path=choosePath();
        String outputFileName = JOptionPane
        .showInputDialog("Please enter the output file name");
        File f=new File(outputFileName);
        //check if file exists
        if (f.exists()){
            String[] options = { "Yes", "Cancel" };
            int selection = JOptionPane
            .showOptionDialog(
                              null,
                              outputFileName
                              + " exists,Do you want to override it?",
                              "Warning",
                              JOptionPane.DEFAULT_OPTION,
                              JOptionPane.WARNING_MESSAGE, null,
                              options, options[0]);
            if (selection == 0) {
                return path+"/"+outputFileName;
            } else {
                JOptionPane
                .showMessageDialog(this,
                                   "Please choose another output file name");
                this.chooseFileName();
                return null;
            }
        }
        else {
            return path+"/"+outputFileName;
        }
    }
    public String choosePath(){
    	 // get from http://www.rgagnon.com/javadetails/java-0370.html choose
        // a directory to save file
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Chose a diretory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        // path default to root directory
        String path ="/";
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
        	path = chooser.getSelectedFile().getAbsolutePath();
        }
        return path;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnPlay) {
            //play the media.
            mediaPlayerComponent.getMediaPlayer().play();
        } else if (e.getSource() == btnPause) {
            //pause the media.
            mediaPlayerComponent.getMediaPlayer().pause();
        } else if (e.getSource() == btnForward) {
            // need to keep fast forward by setting rate at 2.0
            mediaPlayerComponent.getMediaPlayer().setRate((float) 2.0);
        } else if (e.getSource() == btnBackward) {
            //problem
            mediaPlayerComponent.getMediaPlayer().skip(-10000);
        } else if (e.getSource() == btnMute) {
            mediaPlayerComponent.getMediaPlayer().mute();
        } else if (e.getSource() == btnEdit) {
            if(MainFrame.isAudioVideoFile(Main.file).equals("audio")){
            	JOptionPane
                .showMessageDialog(this,
                                   "Can not edit a audio file");
            }else{
            	 String[] options = { "Overlay", "Replace" ,"Strip"};
                 int selection = JOptionPane
                 .showOptionDialog(
                                   null,
                                   "Which one would you like to do",
                                   "Option",
                                   JOptionPane.DEFAULT_OPTION,
                                   JOptionPane.WARNING_MESSAGE, null,
                                   options, options[0]);
                 if(selection ==0){
                	 try {
                		 //Overlay
						overlayAudio();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                     
                 }else if(selection==1){
                     //replace
                 }else{
                	 try {
						extractAudio();
					} catch (IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                 }
            }
        } 
    }
    public String chooseFile() throws IOException {
        //using file chooser ,can only choose file with extension mp3
        
        final JFileChooser fc = new JFileChooser();
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Audio/mp3", "mp3"));
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showOpenDialog(PlayFrame.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
           
            if ( MainFrame.isAudioVideoFile(file).equals("audio")) {
            	return file.getAbsolutePath();
            } else {
                JOptionPane.showMessageDialog(this,
                                              "ERROR: " + file.getName()
                                             + " does not refer to a valid audio file");
                chooseFile();           
                
            }
        }
		return null;

    }
    public void overlayAudio() throws IOException{
    	String inputAudio=chooseFile();
    	String cmd="avconv -i "+Main.file+" -i "+inputAudio+" -c copy test.avi";
    	ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        BufferedReader stdout = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = stdout.readLine()) != null) {

			System.out.println(line);
		}
    }
    public void extractAudio() throws IOException, InterruptedException{
    	String outputFileName=chooseFileName();
    	String cmd = "avconv -i " + Main.file +" "
                + outputFileName;
    	 ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
         pb.redirectErrorStream(true);
         Process process = pb.start();
         BufferedReader stdout = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = stdout.readLine()) != null) {
	
				System.out.println(line);
			}
         int exit =process.waitFor();
         if(exit==0){
        	 JOptionPane
             .showMessageDialog(this,
                                "Strip successfully");
         }else{
        	 JOptionPane
             .showMessageDialog(this,
                                "Error");
         }
         
         
        
    }
    public void stripAudio(){
    	
    }
//if close the media ,the play should stop


}

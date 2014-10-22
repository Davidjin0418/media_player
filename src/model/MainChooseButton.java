package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import control.FileControl;
import main.Main;
import view.MainFrame;

public class MainChooseButton extends JButton{
    public MainChooseButton(final JTextField currentFile){
    	this.setText("Choose a file");
    	this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
                    try {
						FileControl.chooseFile(currentFile);
					} catch (IOException e1) {
						e1.printStackTrace();
					}				
			}
		});
    }
    
    
    
}

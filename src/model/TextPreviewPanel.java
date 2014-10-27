package model;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

import javax.swing.JPanel;

public class TextPreviewPanel extends JPanel  {
      private Image image;
      public TextPreviewPanel(Image i){
    	  image=i;
    	  this.setSize(new Dimension(100,100));
      }
	@Override
	  protected void paintComponent(Graphics g) {
        
	    super.paintComponent(g);
	        g.drawImage(image, 0, 0, null);
	}
}

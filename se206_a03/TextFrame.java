package se206_a03;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;


public class TextFrame extends JFrame {
	
	private JTextArea _textForOpen;
	private JTextArea _textForClose;
	
	private JLabel _openLabel;
	private JLabel _closeLabel;
	
	private JComboBox _fontDropBox;
	
	private JSpinner _fontSizeSpinner;
	
	private JButton _colourButton;
	
	private JLabel _fontStyleLabel;
	private JLabel _fontSizeLabel;
	
	private JButton _okButton;
	private JButton _cancelButton;
	private JButton _applyButton;
	
	public TextFrame() {
		createAndShowGui();
		
	};

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TextFrame textWindow = new TextFrame();

			}

			
		});;

	}
	
	private void createAndShowGui() {
		// TODO Auto-generated method stub
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		JPanel fontSettingPanel = new JPanel();
		fontSettingPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		JPanel confirmationPanel = new JPanel();
		confirmationPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		_textForOpen = new JTextArea(14,45);
		_textForOpen.setWrapStyleWord(true);
		_textForOpen.setLineWrap(true);
		_textForOpen.setBorder(loweredetched); 
		
		_textForClose = new JTextArea(14,45);
		
		_textForClose.setWrapStyleWord(true);
		_textForClose.setLineWrap(true);
		_textForClose.setBorder(loweredetched); 
		
		
		_openLabel = new JLabel("Enter text below for opening scene");
		_closeLabel = new JLabel("Enter text below for closing scene");
		
		_fontSizeSpinner = new JSpinner(new SpinnerNumberModel(12, 5, 64, 1));
		_colourButton = new JButton("colour");
		
		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		_fontDropBox = new JComboBox(fonts);
		
		_fontSizeLabel = new JLabel("Font Size");
		_fontStyleLabel = new JLabel("Font Style");
		
		_okButton = new JButton("Ok");
		_cancelButton = new JButton("Cancel");
		_applyButton = new JButton("Apply");
		
		fontSettingPanel.add(_fontStyleLabel);
		fontSettingPanel.add(_fontDropBox);
		
		fontSettingPanel.add(_colourButton);
		fontSettingPanel.add(_fontSizeLabel);
		fontSettingPanel.add(_fontSizeSpinner);
		
		confirmationPanel.add(_okButton);
		confirmationPanel.add(_applyButton);
		confirmationPanel.add(_cancelButton);
		
		
		JPanel textPanel = (JPanel) this.getContentPane();
		JPanel editingPanel = new JPanel();
		JPanel previewPanel = new JPanel();
		
		GroupLayout layout = new GroupLayout(editingPanel);
		editingPanel.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				      .addComponent(_openLabel)
				      .addComponent(_textForOpen, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				      .addComponent(_closeLabel)
				      .addComponent(_textForClose, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				      .addComponent(fontSettingPanel)
				      .addComponent(confirmationPanel))
		);
		
		layout.setVerticalGroup(
				   layout.createSequentialGroup()
				           .addComponent(_openLabel)
				           .addComponent(_textForOpen, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				           .addComponent(_closeLabel)
				           .addComponent(_textForClose, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				           .addComponent(fontSettingPanel)
				           .addComponent(confirmationPanel)
				);

		
		textPanel.setLayout(new BorderLayout());
		
		textPanel.add(previewPanel, BorderLayout.LINE_END);
		textPanel.add(editingPanel, BorderLayout.LINE_START);
		
		
		
		setTitle("Text Editing");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//create a tab panel to hold other panel
		
		this.setSize(1280,720);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		//this.pack();
		this.setVisible(true);
	}
}

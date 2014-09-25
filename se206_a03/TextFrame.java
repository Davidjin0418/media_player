package se206_a03;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.NumberFormatter;
import javax.swing.text.ParagraphView;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;


public class TextFrame extends JFrame implements ActionListener, WindowListener {
	
	
	private JTextPane _textForOpen;
	private JTextPane _textForClose;
	
	private JScrollPane _scrollForOpen;
	private JScrollPane _scrollForClose;
	private JLabel _openLabel;
	private JLabel _closeLabel;
	
	private JComboBox _openFontDropBox;
	private JSpinner _openFontSizeSpinner;
	private JButton _openColourButton;
	
	private JComboBox _closeFontDropBox;
	private JSpinner _closeFontSizeSpinner;
	private JButton _closeColourButton;	
	
	private JLabel _openfontStyleLabel;
	private JLabel _openfontSizeLabel;
	
	private JLabel _closefontStyleLabel;
	private JLabel _closefontSizeLabel;
	
	private JButton _okButton;
	private JButton _quitButton;
	private JButton _cancelButton;
	private JButton _saveTextButton;
	private JButton _previewButton;
	
	private StyledDocument _doc;
	
	private JProgressBar _TextProgressBar;
	
	private TextFilterWorker _currentTextWorker = null;
	private PreviewWorker _currentPreviewWorker = null;
	
	private JButton _btnAddText;
	
	public TextFrame(JButton btnAddText) {
		_btnAddText = btnAddText;
		createAndShowGui();
	};
	
	private void createAndShowGui() {
		// TODO Auto-generated method stub

		
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		JPanel openfontSettingPanel = new JPanel();
		openfontSettingPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		JPanel closefontSettingPanel = new JPanel();
		closefontSettingPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		JPanel confirmationPanel = new JPanel();
		confirmationPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		
		//preferredSize width=495, height 210
		_textForOpen = new JTextPane();
		
		//taken from http://stackoverflow.com/questions/8666727/wrap-long-words-in-jtextpane-java-7 written by Stanislav Lapitsky
		//to wrap long word within text pane to the next line
		_textForOpen.setEditorKit(new WrapEditorKit());
        //---------------------------------------------------------------------------------------
		_textForOpen.setStyledDocument(new DefaultStyledDocument()  {;
		
		int max = 30;
		@Override
	    public void insertString(int offs, String str, AttributeSet attr)throws BadLocationException{
	        if ((getLength() + str.length()) <= max) {
	            super.insertString(offs, str, attr);
	        }
	        else {
	            Toolkit.getDefaultToolkit().beep();
	        }
	    }
	} );
		
		_textForOpen.setPreferredSize(new Dimension(495,210));
		_scrollForOpen = new JScrollPane(_textForOpen);
		
		_textForClose = new JTextPane();
		
		//taken from http://stackoverflow.com/questions/8666727/wrap-long-words-in-jtextpane-java-7 written by Stanislav Lapitsky
		//to wrap long word within text pane to the next line
		_textForClose.setEditorKit(new WrapEditorKit());
		//---------------------------------------------------------------------------------------			
		_textForClose.setStyledDocument(new DefaultStyledDocument()  {;
					//We decided the maximum amount of character is 30 because is in between 20 and 40 therefore 
					//the sentence or phrase doesn't have to be too short as well as not too many word on the screen. 
					int max = 30;
					@Override
				    public void insertString(int offs, String str, AttributeSet attr)throws BadLocationException{
				        if ((getLength() + str.length()) <= max) {
				            super.insertString(offs, str, attr);
				        }
				        else {
				            Toolkit.getDefaultToolkit().beep();
				        }
				    }
				} );
		  	
		
		_textForClose.setPreferredSize(new Dimension(495,210)); 
		_scrollForClose = new JScrollPane(_textForClose);
		
		_TextProgressBar = new JProgressBar(0, 100);
		_TextProgressBar.setStringPainted(true);
		
		_openLabel = new JLabel("Enter text below for opening scene");
		_closeLabel = new JLabel("Enter text below for closing scene");
		
		Font defaultFont = new Font ("Ubuntu",_textForOpen.getFont().getStyle(), _textForOpen.getFont().getSize());
		String ubuntuFont[] = {"Ubuntu", "Ubuntu Condensed", "Ubuntu Light", "Ubuntu Medium", "Ubuntu Mono"};
		//font setting for opening scene --------------------------------------------------------------------------
		_openFontSizeSpinner = new JSpinner(new SpinnerNumberModel(_textForOpen.getFont().getSize(), 5, 72, 1));
		_openColourButton = new JButton("colour");
		
		_textForOpen.setFont(defaultFont);
		_openFontDropBox = new JComboBox(ubuntuFont);
		for (int i =0; i<ubuntuFont.length; i++) {
			if (_textForOpen.getFont().getName().equals(ubuntuFont[i]) ) {
				_openFontDropBox.setSelectedIndex(i);
			}
		}
		_openfontSizeLabel = new JLabel("Font Size");
		_openfontStyleLabel = new JLabel("Font Style");
		//---------------------------------------------------------------------------------------------
		
		//font setting for closing scene----------------------------------------------------------------------------------------
		_closeFontSizeSpinner = new JSpinner(new SpinnerNumberModel(_textForOpen.getFont().getSize(), 5, 72, 1));
		_closeColourButton = new JButton("colour");
		
		_textForClose.setFont(defaultFont);
		_closeFontDropBox = new JComboBox(ubuntuFont);
		
		for (int i =0; i<ubuntuFont.length; i++) {
			if (_textForOpen.getFont().getName().equals(ubuntuFont[i]) ) {
				_closeFontDropBox.setSelectedIndex(i);
			}
		}
		//------------------------------------------------------------------------------------------------
		_openfontSizeLabel = new JLabel("Font Size");
		_openfontStyleLabel = new JLabel("Font Style");
		
		_closefontSizeLabel = new JLabel("Font Size");
		_closefontStyleLabel = new JLabel("Font Style");
		
		_saveTextButton = new JButton ("Save Text");
		_okButton = new JButton("Ok");
		_quitButton = new JButton("Quit");
		_cancelButton = new JButton("Cancel");
		_previewButton = new JButton("Preview");
		//--------------------------------------------------------------------------------------------------
		closefontSettingPanel.add(_closefontStyleLabel);
		closefontSettingPanel.add(_closeFontDropBox);
		
		closefontSettingPanel.add(_closeColourButton);
		closefontSettingPanel.add(_closefontSizeLabel);
		closefontSettingPanel.add(_closeFontSizeSpinner);
		
		
		openfontSettingPanel.add(_openfontStyleLabel);
		openfontSettingPanel.add(_openFontDropBox);
		
		openfontSettingPanel.add(_openColourButton);
		openfontSettingPanel.add(_openfontSizeLabel);
		openfontSettingPanel.add(_openFontSizeSpinner);
		
		confirmationPanel.add(_previewButton);
		confirmationPanel.add(_saveTextButton);
		confirmationPanel.add(_okButton);
		confirmationPanel.add(_cancelButton);
		confirmationPanel.add(_quitButton);
		//--------------------------------------------------------------------------------------------------------
		
		_openColourButton.addActionListener(this);
		_openFontDropBox.addActionListener(this);
		
		_closeColourButton.addActionListener(this);
		_closeFontDropBox.addActionListener(this);
		
		_quitButton.addActionListener(this);
		_okButton.addActionListener(this);
		_cancelButton.addActionListener(this);
		_saveTextButton.addActionListener(this);
		_previewButton.addActionListener(this);
		
		_openFontSizeSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				SpinnerModel dataModel = _openFontSizeSpinner.getModel();
		        if (dataModel.getValue() instanceof Integer ) {
		        	String fontName = _textForOpen.getFont().getName();
		        	int style = _textForOpen.getFont().getStyle();
					_textForOpen.setFont(new Font(fontName, style, (int) dataModel.getValue()));
		        }
			}
			
		});
		
		JFormattedTextField txt = ((JSpinner.NumberEditor) _openFontSizeSpinner.getEditor()).getTextField();
		((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
		
		_closeFontSizeSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				SpinnerModel dataModel = _closeFontSizeSpinner.getModel();
		        if (dataModel.getValue() instanceof Integer ) {
		        	String fontName = _textForClose.getFont().getName();
		        	int style = _textForClose.getFont().getStyle();
		        	_textForClose.setFont(new Font(fontName, style, (int) dataModel.getValue()));
		        }
			}
			
		});
		
		txt = ((JSpinner.NumberEditor) _closeFontSizeSpinner.getEditor()).getTextField();
		((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
		
		//--------------------------------------------------------------------------------------------------------
		
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
				      .addComponent(_scrollForOpen, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				      .addComponent(openfontSettingPanel)
				      .addComponent(_closeLabel)
				      .addComponent(_scrollForClose, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				      .addComponent(closefontSettingPanel)
				      .addComponent(confirmationPanel))
		);
		
		layout.setVerticalGroup(
				   layout.createSequentialGroup()
				           .addComponent(_openLabel)
				           .addComponent(_scrollForOpen, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				           .addComponent(openfontSettingPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				           .addComponent(_closeLabel)
				           .addComponent(_scrollForClose, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				           .addComponent(closefontSettingPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				           .addComponent(confirmationPanel)
				);

		
		textPanel.setLayout(new BorderLayout());
		
		textPanel.add(previewPanel, BorderLayout.LINE_END);
		textPanel.add(editingPanel, BorderLayout.LINE_START);
		textPanel.add(_TextProgressBar, BorderLayout.SOUTH);

		
		setTitle("Text Editing");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		checkTextSetting();
		
		
		this.addWindowListener(this);
		this.setSize(1280,800);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == _quitButton) {
			int yn = JOptionPane.showConfirmDialog(TextFrame.this,
				    "Would you like to quit editing text ?",
				    "Warning",
				    JOptionPane.YES_NO_OPTION);
			
			//quiting
			if (yn == 0) {
				TextFrame.this.dispatchEvent(new WindowEvent(TextFrame.this, WindowEvent.WINDOW_CLOSING));
			}
		}
		else if (e.getSource() == _openFontDropBox) {
			String fontName = (String) _openFontDropBox.getSelectedItem();
			int style = _textForOpen.getFont().getStyle();
			int size = _textForOpen.getFont().getSize();
			_textForOpen.setFont(new Font(fontName, style, size));
			
		}
		else if (e.getSource() == _closeFontDropBox) {
			String fontName = (String) _closeFontDropBox.getSelectedItem();
			int style = _textForClose.getFont().getStyle();
			int size = _textForClose.getFont().getSize();
			_textForClose.setFont(new Font(fontName, style, size));
			
		}
		else if (e.getSource() == _openColourButton) {
			Color color = JColorChooser.showDialog(TextFrame.this, "Text Color", _textForOpen.getForeground());
			if (color != null) {
				_textForOpen.setForeground(color);
				_textForOpen.updateUI();
			}
		}
		else if (e.getSource() == _closeColourButton) {
			Color color = JColorChooser.showDialog(TextFrame.this, "Text Color", _textForClose.getForeground());
			if (color != null) {
				_textForClose.setForeground(color);
				_textForClose.updateUI();
			}
		}
		else if (e.getSource() == _okButton) {
			File file = new File(Main.file.getAbsolutePath());
			TextFilterWorker worker = new TextFilterWorker(file, _TextProgressBar, _textForOpen, _textForClose, _okButton);
			_currentTextWorker = worker;
			worker.execute();
			_okButton.setEnabled(false);
		}
		else if (e.getSource() == _cancelButton) {
			if (_currentTextWorker != null) {
				_currentTextWorker.cancel(true);
			}
			
		}
		else if (e.getSource() == _previewButton) {
			PreviewWorker worker = new PreviewWorker(new File(Main.file.getAbsolutePath()), _textForOpen, _textForClose, _previewButton); 
			worker.execute();
			_previewButton.setEnabled(false);
			
		}
		else if (e.getSource() == _saveTextButton) {
			File textConfigFile = new File(System.getProperty("user.home")+"/.vamix/[TEXTCONFIG]["+ Main.file.getName()+"].txt");
			if (!textConfigFile.exists()){
				try {
					textConfigFile.createNewFile();
				} catch (IOException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				} 
			}
			
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter(textConfigFile, false));
				PrintWriter pw = new PrintWriter(bw);
				
				//create a string builder to for making the entry string
				StringBuilder textInformation = new StringBuilder();
				
				//opening scene
				textInformation.append(_textForOpen.getText());
				textInformation.append(System.getProperty("line.separator"));
				
				//closing scene
				textInformation.append(_textForClose.getText());
				textInformation.append(System.getProperty("line.separator"));
				
				//open font colour
				textInformation.append(_textForOpen.getForeground().getRGB());
				textInformation.append(System.getProperty("line.separator"));
				
				//open font name
				textInformation.append(_textForOpen.getFont().getName());
				textInformation.append(System.getProperty("line.separator"));
				
				//open font size
				textInformation.append(_textForOpen.getFont().getSize());
				textInformation.append(System.getProperty("line.separator"));
				
				//open font style
				textInformation.append(_textForOpen.getFont().getStyle());
				textInformation.append(System.getProperty("line.separator"));
				
				//close font colour
				textInformation.append(_textForClose.getForeground().getRGB());
				textInformation.append(System.getProperty("line.separator"));
				
				//close font name
				textInformation.append(_textForClose.getFont().getName());
				textInformation.append(System.getProperty("line.separator"));
				
				//close font size
				textInformation.append(_textForClose.getFont().getSize());
				textInformation.append(System.getProperty("line.separator"));
				
				//close font style
				textInformation.append(_textForClose.getFont().getStyle());
				textInformation.append(System.getProperty("line.separator"));
				
				//append the entry to the file 
				pw.append(textInformation.toString());
				pw.close();
				bw.close();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
				
		}
		
		

	}
	

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		if (_currentTextWorker != null) {
			if (_currentTextWorker.isDone() == false) {
				int yn = JOptionPane.showConfirmDialog(TextFrame.this,
					    "The video is still processing, do you want to quit ? \n(Quitting will abandon the operation)",
					    "Warning",
					    JOptionPane.YES_NO_OPTION);
				
				if (yn == 0) {
					_currentTextWorker.cancel(true);
					this._btnAddText.setEnabled(true);
					this.dispose();
					
				}
			}
			else {
				this._btnAddText.setEnabled(true);
				this.dispose();
				
			}
		}
		else {
			this._btnAddText.setEnabled(true);
			this.dispose();
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	private void checkTextSetting (){
		File settingFile = new File(System.getProperty("user.home")+"/.vamix/[TEXTCONFIG]["+ Main.file.getName()+"].txt");
		if (settingFile.exists()) {
			try {
				
				//create a bufferedreader to read the log file
				BufferedReader br = new BufferedReader(new FileReader(settingFile));
				String line;
				int lineCount = 1;
				String openFontName = "Ubuntu";
				int openFontSize = 12;
				int openFontStyle = 0;
				
				String closeFontName = "Ubuntu";
				int closeFontSize = 12;
				int closeFontStyle = 0;
				//read and publish each line until it reach to end of file
				//and setting isEmpty 
				while ((line = br.readLine()) != null) {
					switch (lineCount) {
					case 1: _textForOpen.setText(line);
							break;
					case 2: _textForClose.setText(line);
							break;
					case 3: _textForOpen.setForeground(new Color(Integer.parseInt(line)));
							break;
					case 4: openFontName = line;
							for ( int i =0; i<_openFontDropBox.getSelectedObjects().length; i++ ) {
								_openFontDropBox.setSelectedItem(line);
							}
							break;
					case 5: openFontSize = Integer.parseInt(line);
							_openFontSizeSpinner.setValue(openFontSize);
							break;
					case 6: openFontStyle = Integer.parseInt(line);
							break;
					case 7: _textForClose.setForeground(new Color(Integer.parseInt(line)));
							break;
					case 8: closeFontName = line;
							for ( int i =0; i<_closeFontDropBox.getSelectedObjects().length; i++ ) {
								_closeFontDropBox.setSelectedItem(line);
							}
							break;
					case 9: closeFontSize = Integer.parseInt(line);
							_closeFontSizeSpinner.setValue(closeFontSize);
							break;
					case 10: closeFontStyle = Integer.parseInt(line);
							break;
					}
					lineCount++;
				}
				_textForOpen.setFont(new Font(openFontName,openFontStyle, openFontSize));
				_textForClose.setFont(new Font(closeFontName,closeFontStyle, closeFontSize));
				br.close();
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}

//taken from http://stackoverflow.com/questions/8666727/wrap-long-words-in-jtextpane-java-7 written by Stanislav Lapitsky
//to wrap long word within text pane to the next line
//---------------------------------------------------------------------------
class WrapEditorKit extends StyledEditorKit {
    ViewFactory defaultFactory=new WrapColumnFactory();
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }

}

class WrapColumnFactory implements ViewFactory {
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new WrapLabelView(elem);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                return new ParagraphView(elem);
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
                return new BoxView(elem, View.Y_AXIS);
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
                return new ComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
                return new IconView(elem);
            }
        }

        // default to text display
        return new LabelView(elem);
    }
}

class WrapLabelView extends LabelView {
    public WrapLabelView(Element elem) {
        super(elem);
    }

    public float getMinimumSpan(int axis) {
        switch (axis) {
            case View.X_AXIS:
                return 0;
            case View.Y_AXIS:
                return super.getMinimumSpan(axis);
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

}
//-----------------------------------------------------------------------------


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
import java.io.File;

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
	
	private JLabel _openLabel;
	private JLabel _closeLabel;
	
	private JComboBox _fontDropBox;
	
	private JSpinner _fontSizeSpinner;
	
	private JButton _colourButton;
	
	private JLabel _fontStyleLabel;
	private JLabel _fontSizeLabel;
	
	private JButton _okButton;
	private JButton _quitButton;
	private JButton _cancelButton;
	
	private StyledDocument _doc;
	
	private JProgressBar _TextProgressBar;
	
	private TextFilterWorker _currentTextWorker = null;
	
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
		
		//preferredSize width=495, height 210
		_textForOpen = new JTextPane();
		
		//bug fix from http://java-sl.com/tip_java7_text_wrapping_bug_fix.html written by Stanislav Lapitsky
		_textForOpen.setEditorKit(new MyStyledEditorKit());
		
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
		_doc = _textForOpen.getStyledDocument();
        _doc.addDocumentListener(new DocumentListener() {
             public void insertUpdate(DocumentEvent e) {
                insert();
            }
 
            public void removeUpdate(DocumentEvent e) {
                insert();
            }
 
            public void changedUpdate(DocumentEvent e) {
                insert();
            }
 
            public void insert() {
                SwingUtilities.invokeLater(new Runnable() {
                     public void run() {
                        Style defaultStyle = _textForOpen.getStyle(StyleContext.DEFAULT_STYLE);
                        _doc.setCharacterAttributes(0, _doc.getLength(), defaultStyle, false);
                    }
                });
            }
        });
        //---------------------------------------------------------------------------------------
		
		_textForOpen.setPreferredSize(new Dimension(495,210));
		_textForOpen.setBorder(loweredetched); 
		
		_textForClose = new JTextPane();
		
		//bug fix from http://java-sl.com/tip_java7_text_wrapping_bug_fix.html written by Stanislav Lapitsky
		_textForClose.setEditorKit(new MyStyledEditorKit());
				
		_textForClose.setStyledDocument(new DefaultStyledDocument()  {;
					
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
				_doc = _textForClose.getStyledDocument();
		        _doc.addDocumentListener(new DocumentListener() {
		             public void insertUpdate(DocumentEvent e) {
		                insert();
		            }
		 
		            public void removeUpdate(DocumentEvent e) {
		                insert();
		            }
		 
		            public void changedUpdate(DocumentEvent e) {
		                insert();
		            }
		 
		            public void insert() {
		                SwingUtilities.invokeLater(new Runnable() {
		                     public void run() {
		                        Style defaultStyle = _textForClose.getStyle(StyleContext.DEFAULT_STYLE);
		                        _doc.setCharacterAttributes(0, _doc.getLength(), defaultStyle, false);
		                    }
		                });
		                }
		  });
		  //---------------------------------------------------------------------------------------		
		
		_textForClose.setPreferredSize(new Dimension(495,210));
		_textForClose.setBorder(loweredetched); 
		
		
		_TextProgressBar = new JProgressBar(0, 10000);
		
		_openLabel = new JLabel("Enter text below for opening scene");
		_closeLabel = new JLabel("Enter text below for closing scene");
		
		_fontSizeSpinner = new JSpinner(new SpinnerNumberModel(_textForOpen.getFont().getSize(), 5, 72, 1));
		_colourButton = new JButton("colour");
		
		
		Font defaultFont = new Font ("Ubuntu",_textForOpen.getFont().getStyle(), _textForOpen.getFont().getSize());
		_textForOpen.setFont(defaultFont);
		_textForClose.setFont(defaultFont);
		String ubuntuFont[] = {"Ubuntu", "Ubuntu Condensed", "Ubuntu Light", "Ubuntu Medium", "Ubunutu Mono"};
		_fontDropBox = new JComboBox(ubuntuFont);
		
		for (int i =0; i<ubuntuFont.length; i++) {
			if (_textForOpen.getFont().getName().equals(ubuntuFont[i]) ) {
				_fontDropBox.setSelectedIndex(i);
			}
		}
		
		_fontSizeLabel = new JLabel("Font Size");
		_fontStyleLabel = new JLabel("Font Style");
		
		_okButton = new JButton("Ok");
		_quitButton = new JButton("Quit");
		_cancelButton = new JButton("Cancel");
		
		
		
		fontSettingPanel.add(_fontStyleLabel);
		fontSettingPanel.add(_fontDropBox);
		
		fontSettingPanel.add(_colourButton);
		fontSettingPanel.add(_fontSizeLabel);
		fontSettingPanel.add(_fontSizeSpinner);
		
		confirmationPanel.add(_okButton);
		confirmationPanel.add(_cancelButton);
		confirmationPanel.add(_quitButton);
		
		_colourButton.addActionListener(this);
		_fontDropBox.addActionListener(this);
		_quitButton.addActionListener(this);
		_okButton.addActionListener(this);
		_cancelButton.addActionListener(this);
		
		_fontSizeSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				SpinnerModel dataModel = _fontSizeSpinner.getModel();
		        if (dataModel.getValue() instanceof Integer ) {
		        	String fontName = _textForOpen.getFont().getName();
		        	int style = _textForOpen.getFont().getStyle();
					_textForOpen.setFont(new Font(fontName, style, (int) dataModel.getValue()));
		        }
			}
			
		});
		
		
		
		JFormattedTextField txt = ((JSpinner.NumberEditor) _fontSizeSpinner.getEditor()).getTextField();
		((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
		
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
				      .addComponent(fontSettingPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				      .addComponent(confirmationPanel))
		);
		
		layout.setVerticalGroup(
				   layout.createSequentialGroup()
				           .addComponent(_openLabel)
				           .addComponent(_textForOpen, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				           .addComponent(_closeLabel)
				           .addComponent(_textForClose, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				           .addComponent(fontSettingPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,GroupLayout.PREFERRED_SIZE)
				           .addComponent(confirmationPanel)
				);

		
		textPanel.setLayout(new BorderLayout());
		
		textPanel.add(previewPanel, BorderLayout.LINE_END);
		textPanel.add(editingPanel, BorderLayout.LINE_START);
		textPanel.add(_TextProgressBar, BorderLayout.SOUTH);

		
		setTitle("Text Editing");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//create a tab panel to hold other panel
		
		this.addWindowListener(this);
		this.setSize(1280,720);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		//this.pack();
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
		else if (e.getSource() == _fontDropBox) {
			String fontName = (String) _fontDropBox.getSelectedItem();
			int style = _textForOpen.getFont().getStyle();
			int size = _textForOpen.getFont().getSize();
			_textForOpen.setFont(new Font(fontName, style, size));
			_textForClose.setFont(new Font(fontName, style, size));
			
		}
		else if (e.getSource() == _colourButton) {
			Color color = JColorChooser.showDialog(TextFrame.this, "Text Color", _textForOpen.getForeground());
			if (color != null) {
				_textForOpen.setForeground(color);
				_textForOpen.updateUI();
				_textForClose.setForeground(color);
				_textForClose.updateUI();
			}
		}
		else if (e.getSource() == _okButton) {
			File file = new File("/home/john/workspace/vlc/test.mp4");
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
					this.dispose();
				}
			}
			else {
				this.dispose();
			}
		}
		else {
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


}

//code taken http://java-sl.com/tip_java7_text_wrapping_bug_fix.html written by Stanislav Lapitsky 
//--------------------------------------------------------------------------------------------------
class MyStyledEditorKit extends StyledEditorKit {
    private MyFactory factory;
 
    public ViewFactory getViewFactory() {
        if (factory == null) {
            factory = new MyFactory();
        }
        return factory;
    }
}
 
class MyFactory implements ViewFactory {
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new MyLabelView(elem);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                return new MyParagraphView(elem);
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
 
class MyParagraphView extends ParagraphView {
 
    public MyParagraphView(Element elem) {
        super(elem);
    }
    public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        super.removeUpdate(e, a, f);
        resetBreakSpots();
    }
    public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        super.insertUpdate(e, a, f);
        resetBreakSpots();
    }
 
    private void resetBreakSpots() {
        for (int i=0; i<layoutPool.getViewCount(); i++) {
            View v=layoutPool.getView(i);
            if (v instanceof MyLabelView) {
                ((MyLabelView)v).resetBreakSpots();
            }
        }
    }
}
 
class MyLabelView extends LabelView {
 
    boolean isResetBreakSpots=false;
 
    public MyLabelView(Element elem) {
        super(elem);
    }
    public View breakView(int axis, int p0, float pos, float len) {
        if (axis == View.X_AXIS) {
            resetBreakSpots();
        }
        return super.breakView(axis, p0, pos, len);
    }
    
    public void resetBreakSpots() {
        isResetBreakSpots=true;
        removeUpdate(null, null, null);
        isResetBreakSpots=false;
   }
 
    public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        super.removeUpdate(e, a, f);
    }
 
    public void preferenceChanged(View child, boolean width, boolean height) {
        if (!isResetBreakSpots) {
            super.preferenceChanged(child, width, height);
        }
    }
}
//---------------------------------------------------------------------------



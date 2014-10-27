package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.NumberFormatter;
import javax.swing.text.StyledDocument;
import control.PreviewWorker;
import control.TextFilterWorker;
import main.Main;
import model.MainAddTextButton;
import model.TextPreviewPanel;
import model.WrapEditorKit;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class TextFrame extends JFrame implements ActionListener {

	private JTextPane textForOpen;
	private JTextPane textForClose;

	private JScrollPane scrollForOpen;
	private JScrollPane scrollForClose;
	private JLabel closeLabel;

	private JComboBox<?> openFontDropBox;
	private JSpinner openFontSizeSpinner;
	private JButton openColourButton;

	private JComboBox<?> closeFontDropBox;
	private JSpinner closeFontSizeSpinner;
	private JButton closeColourButton;

	private JLabel openfontStyleLabel;
	private JLabel openfontSizeLabel;

	private JLabel closefontStyleLabel;
	private JLabel closefontSizeLabel;

	private JButton okButton;
	private JButton quitButton;
	private JButton cancelButton;
	private JButton saveTextButton;
	private JButton previewButton;
	private JProgressBar TextProgressBar;

	private TextFilterWorker currentTextWorker = null;
	private MainAddTextButton btnAddText;

	private JPanel openPanel;
	private JPanel closePanel;

	public TextFrame(MainAddTextButton btn) {
		btnAddText = btn;
		createAndShowGui();
	};

	private void createAndShowGui() {
		// TODO Auto-generated method stub

		BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED);
		JPanel openfontSettingPanel = new JPanel();
		openfontSettingPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		openfontSettingPanel.setBackground(Color.LIGHT_GRAY);
		openfontSettingPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

		JPanel closefontSettingPanel = new JPanel();
		closefontSettingPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		closefontSettingPanel.setBackground(Color.LIGHT_GRAY);
		closefontSettingPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

		JPanel confirmationPanel = new JPanel();
		confirmationPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		confirmationPanel.setBackground(Color.LIGHT_GRAY);
		confirmationPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

		// preferredSize width=495, height 210
		textForOpen = new JTextPane();

		// taken from
		// http://stackoverflow.com/questions/8666727/wrap-long-words-in-jtextpane-java-7
		// written by Stanislav Lapitsky
		// to wrap long word within text pane to the next line
		textForOpen.setEditorKit(new WrapEditorKit());
		// ---------------------------------------------------------------------------------------
		textForOpen.setStyledDocument(new DefaultStyledDocument() {
			;

			int max = 30;

			@Override
			public void insertString(int offs, String str, AttributeSet attr)
					throws BadLocationException {
				if ((getLength() + str.length()) <= max) {
					super.insertString(offs, str, attr);
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		});

		textForOpen.setPreferredSize(new Dimension(495, 210));
		scrollForOpen = new JScrollPane(textForOpen);

		textForClose = new JTextPane();

		// taken from
		// http://stackoverflow.com/questions/8666727/wrap-long-words-in-jtextpane-java-7
		// written by Stanislav Lapitsky
		// to wrap long word within text pane to the next line
		textForClose.setEditorKit(new WrapEditorKit());
		// ---------------------------------------------------------------------------------------
		textForClose.setStyledDocument(new DefaultStyledDocument() {
			;
			// We decided the maximum amount of character is 30 because is in
			// between 20 and 40 therefore
			// the sentence or phrase doesn't have to be too short as well as
			// not too many word on the screen.
			int max = 30;

			@Override
			public void insertString(int offs, String str, AttributeSet attr)
					throws BadLocationException {
				if ((getLength() + str.length()) <= max) {
					super.insertString(offs, str, attr);
				} else {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		});

		textForClose.setPreferredSize(new Dimension(495, 210));
		scrollForClose = new JScrollPane(textForClose);

		TextProgressBar = new JProgressBar(0, 100);
		TextProgressBar.setStringPainted(true);
		closeLabel = new JLabel("Enter text below for closing scene");

		Font defaultFont = new Font("Ubuntu", textForOpen.getFont().getStyle(),
				textForOpen.getFont().getSize());
		String ubuntuFont[] = { "Ubuntu", "Ubuntu Condensed", "Ubuntu Light",
				"Ubuntu Medium", "Ubuntu Mono" };
		// font setting for opening scene
		// --------------------------------------------------------------------------
		openFontSizeSpinner = new JSpinner(new SpinnerNumberModel(textForOpen
				.getFont().getSize(), 5, 72, 1));
		openColourButton = new JButton("colour");
		openColourButton.setForeground(UIManager.getColor("Button.foreground"));
		openColourButton.setBackground(UIManager.getColor("Button.background"));

		textForOpen.setFont(defaultFont);
		openFontDropBox = new JComboBox<Object>(ubuntuFont);
		openFontDropBox.setBackground(UIManager.getColor("Button.background"));
		for (int i = 0; i < ubuntuFont.length; i++) {
			if (textForOpen.getFont().getName().equals(ubuntuFont[i])) {
				openFontDropBox.setSelectedIndex(i);
			}
		}
		openfontSizeLabel = new JLabel("Font Size");
		openfontStyleLabel = new JLabel("Font Style");
		// ---------------------------------------------------------------------------------------------

		// font setting for closing
		// scene----------------------------------------------------------------------------------------
		closeFontSizeSpinner = new JSpinner(new SpinnerNumberModel(textForOpen
				.getFont().getSize(), 5, 72, 1));
		closeColourButton = new JButton("colour");

		textForClose.setFont(defaultFont);
		closeFontDropBox = new JComboBox<Object>(ubuntuFont);

		for (int i = 0; i < ubuntuFont.length; i++) {
			if (textForOpen.getFont().getName().equals(ubuntuFont[i])) {
				closeFontDropBox.setSelectedIndex(i);
			}
		}
		// ------------------------------------------------------------------------------------------------
		openfontSizeLabel = new JLabel("Font Size");
		openfontStyleLabel = new JLabel("Font Style");

		closefontSizeLabel = new JLabel("Font Size");
		closefontStyleLabel = new JLabel("Font Style");

		saveTextButton = new JButton("Save Text");
		okButton = new JButton("Ok");
		quitButton = new JButton("Quit");
		cancelButton = new JButton("Cancel");
		previewButton = new JButton("Preview");
		// --------------------------------------------------------------------------------------------------
		closefontSettingPanel.add(closefontStyleLabel);
		closefontSettingPanel.add(closeFontDropBox);

		closefontSettingPanel.add(closeColourButton);
		closefontSettingPanel.add(closefontSizeLabel);
		closefontSettingPanel.add(closeFontSizeSpinner);

		openfontSettingPanel.add(openfontStyleLabel);
		openfontSettingPanel.add(openFontDropBox);

		openfontSettingPanel.add(openColourButton);
		openfontSettingPanel.add(openfontSizeLabel);
		openfontSettingPanel.add(openFontSizeSpinner);

		confirmationPanel.add(previewButton);
		confirmationPanel.add(saveTextButton);
		confirmationPanel.add(okButton);
		confirmationPanel.add(cancelButton);
		confirmationPanel.add(quitButton);
		// --------------------------------------------------------------------------------------------------------

		openColourButton.addActionListener(this);
		openFontDropBox.addActionListener(this);

		closeColourButton.addActionListener(this);
		closeFontDropBox.addActionListener(this);

		quitButton.addActionListener(this);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		saveTextButton.addActionListener(this);
		previewButton.addActionListener(this);

		openFontSizeSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				SpinnerModel dataModel = openFontSizeSpinner.getModel();
				if (dataModel.getValue() instanceof Integer) {
					String fontName = textForOpen.getFont().getName();
					int style = textForOpen.getFont().getStyle();
					textForOpen.setFont(new Font(fontName, style,
							(int) dataModel.getValue()));
				}
			}

		});

		JFormattedTextField txt = ((JSpinner.NumberEditor) openFontSizeSpinner
				.getEditor()).getTextField();
		((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

		closeFontSizeSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

				SpinnerModel dataModel = closeFontSizeSpinner.getModel();
				if (dataModel.getValue() instanceof Integer) {
					String fontName = textForClose.getFont().getName();
					int style = textForClose.getFont().getStyle();
					textForClose.setFont(new Font(fontName, style,
							(int) dataModel.getValue()));
				}
			}

		});

		txt = ((JSpinner.NumberEditor) closeFontSizeSpinner.getEditor())
				.getTextField();
		((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);

		// --------------------------------------------------------------------------------------------------------

		JPanel textPanel = (JPanel) this.getContentPane();
		JPanel editingPanel = new JPanel();
		editingPanel.setBackground(Color.LIGHT_GRAY);

		openPanel = new JPanel();
		
		openPanel.setBackground(Color.LIGHT_GRAY);
		openPanel.setPreferredSize(new Dimension(250,220));
	    

		closePanel = new JPanel();
		
		closePanel.setBackground(Color.LIGHT_GRAY);
		closePanel.setPreferredSize(new Dimension(250,220));

		GroupLayout layout = new GroupLayout(editingPanel);
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(closefontSettingPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(scrollForOpen, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(closeLabel)
						.addComponent(scrollForClose, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
						.addComponent(confirmationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(openfontSettingPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addGap(18)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(openPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(closePanel, GroupLayout.PREFERRED_SIZE, 245, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGap(21)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(scrollForOpen, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(openfontSettingPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(closeLabel))
						.addComponent(openPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(scrollForClose, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(closefontSettingPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(confirmationPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(closePanel, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE))
					.addGap(12))
		);

		editingPanel.setLayout(layout);

		layout.setAutoCreateContainerGaps(true);

		layout.setAutoCreateGaps(true);

		textPanel.setLayout(new BorderLayout());
		textPanel.add(editingPanel, BorderLayout.LINE_START);
		textPanel.add(TextProgressBar, BorderLayout.SOUTH);

		setTitle("Text Editing");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		checkTextSetting();

		this.setSize(575, 669);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.pack();
		this.setVisible(true);

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				if (currentTextWorker != null) {
					if (currentTextWorker.isDone() == false) {
						int yn = JOptionPane
								.showConfirmDialog(
										TextFrame.this,
										"The video is still processing, do you want to quit ? \n(Quitting will abandon the operation)",
										"Warning", JOptionPane.YES_NO_OPTION);

						if (yn == 0) {
							currentTextWorker.cancel(true);
							btnAddText.setEnabled(true);
							dispose();

						}
					} else {
						btnAddText.setEnabled(true);
						dispose();

					}
				} else {
					btnAddText.setEnabled(true);
					dispose();
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == quitButton) {
			int yn = JOptionPane.showConfirmDialog(TextFrame.this,
					"Would you like to quit editing text ?", "Warning",
					JOptionPane.YES_NO_OPTION);

			// quiting
			if (yn == 0) {
				TextFrame.this.dispatchEvent(new WindowEvent(TextFrame.this,
						WindowEvent.WINDOW_CLOSING));
			}
		} else if (e.getSource() == openFontDropBox) {
			String fontName = (String) openFontDropBox.getSelectedItem();
			int style = textForOpen.getFont().getStyle();
			int size = textForOpen.getFont().getSize();
			textForOpen.setFont(new Font(fontName, style, size));

		} else if (e.getSource() == closeFontDropBox) {
			String fontName = (String) closeFontDropBox.getSelectedItem();
			int style = textForClose.getFont().getStyle();
			int size = textForClose.getFont().getSize();
			textForClose.setFont(new Font(fontName, style, size));

		} else if (e.getSource() == openColourButton) {
			Color color = JColorChooser.showDialog(TextFrame.this,
					"Text Color", textForOpen.getForeground());
			if (color != null) {
				textForOpen.setForeground(color);
				textForOpen.updateUI();
			}
		} else if (e.getSource() == closeColourButton) {
			Color color = JColorChooser.showDialog(TextFrame.this,
					"Text Color", textForClose.getForeground());
			if (color != null) {
				textForClose.setForeground(color);
				textForClose.updateUI();
			}
		} else if (e.getSource() == okButton) {
			JFileChooser fileChooser = new JFileChooser() {

				@Override
				public void approveSelection() {
					File file = getSelectedFile();

					// check if the file exist already and ask the user if they
					// want to over write the file
					if (file.getAbsolutePath().equals(
							Main.file.getAbsolutePath())) {
						JOptionPane.showMessageDialog(this, "ERROR: "
								+ "Cannot overwrite the source file("
								+ Main.file.getName() + ")");
						return;
					}
					if (file.exists() && getDialogType() == SAVE_DIALOG) {
						int result = JOptionPane.showConfirmDialog(this,
								file.getName() + " already"
										+ " exist. Do you want to overwrite ?",
								"OverWrite ?", JOptionPane.YES_NO_OPTION);
						switch (result) {
						case JOptionPane.YES_OPTION:
							super.approveSelection();
							return;
						case JOptionPane.NO_OPTION:
							return;
						case JOptionPane.CLOSED_OPTION:
							return;
						}
					}
					super.approveSelection();
				}

			};
			// add a mp3 file filter to the file chooser so the user can choose
			// to only see mp3 file

			fileChooser.setSelectedFile(new File("(TEXTFILTER)"
					+ Main.file.getName()));

			int returnVal = fileChooser.showSaveDialog(null);

			// the user confirm the selected file, the file will pass onto
			// outputFile ad
			// the name of the file will be shown on output filed text field
			File outputFile;
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				File selectedFile = fileChooser.getSelectedFile();
				outputFile = selectedFile;
			} else {
				return;
			}
			File file = new File(Main.file.getAbsolutePath());
			TextFilterWorker worker = new TextFilterWorker(file,
					TextProgressBar, textForOpen, textForClose, okButton,
					outputFile);
			currentTextWorker = worker;
			worker.execute();
			okButton.setEnabled(false);
		} else if (e.getSource() == cancelButton) {
			if (currentTextWorker != null) {
				currentTextWorker.cancel(true);
			}

		} else if (e.getSource() == previewButton) {
			PreviewWorker workeropen = new PreviewWorker(new File(
					Main.file.getAbsolutePath()), textForOpen, textForClose,
					previewButton, 0,openPanel);
			workeropen.execute();
		
			PreviewWorker workerclose = new PreviewWorker(new File(
					Main.file.getAbsolutePath()), textForOpen, textForClose,
					previewButton, 1,closePanel);
			workerclose.execute();
		

		} else if (e.getSource() == saveTextButton) {
			// save the text file
			File textConfigFile = new File(System.getProperty("user.home")
					+ "/.vamix/[TEXTCONFIG][" + Main.file.getName() + "].txt");
			if (!textConfigFile.exists()) {
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

				// create a string builder to for making the entry string
				StringBuilder textInformation = new StringBuilder();

				// opening scene
				textInformation.append(textForOpen.getText());
				textInformation.append(System.getProperty("line.separator"));

				// closing scene
				textInformation.append(textForClose.getText());
				textInformation.append(System.getProperty("line.separator"));

				// open font colour
				textInformation.append(textForOpen.getForeground().getRGB());
				textInformation.append(System.getProperty("line.separator"));

				// open font name
				textInformation.append(textForOpen.getFont().getName());
				textInformation.append(System.getProperty("line.separator"));

				// open font size
				textInformation.append(textForOpen.getFont().getSize());
				textInformation.append(System.getProperty("line.separator"));

				// open font style
				textInformation.append(textForOpen.getFont().getStyle());
				textInformation.append(System.getProperty("line.separator"));

				// close font colour
				textInformation.append(textForClose.getForeground().getRGB());
				textInformation.append(System.getProperty("line.separator"));

				// close font name
				textInformation.append(textForClose.getFont().getName());
				textInformation.append(System.getProperty("line.separator"));

				// close font size
				textInformation.append(textForClose.getFont().getSize());
				textInformation.append(System.getProperty("line.separator"));

				// close font style
				textInformation.append(textForClose.getFont().getStyle());
				textInformation.append(System.getProperty("line.separator"));

				// append the entry to the file
				pw.append(textInformation.toString());
				pw.close();
				bw.close();
			} catch (IOException e2) {

				e2.printStackTrace();
			}

		}

	}

	private void checkTextSetting() {
		File settingFile = new File(System.getProperty("user.home")
				+ "/.vamix/[TEXTCONFIG][" + Main.file.getName() + "].txt");
		if (settingFile.exists()) {
			try {

				// create a bufferedreader to read the log file
				BufferedReader br = new BufferedReader(new FileReader(
						settingFile));
				String line;
				int lineCount = 1;
				String openFontName = "Ubuntu";
				int openFontSize = 12;
				int openFontStyle = 0;

				String closeFontName = "Ubuntu";
				int closeFontSize = 12;
				int closeFontStyle = 0;
				// read and publish each line until it reach to end of file
				// and setting isEmpty
				while ((line = br.readLine()) != null) {
					switch (lineCount) {
					case 1:
						textForOpen.setText(line);
						break;
					case 2:
						textForClose.setText(line);
						break;
					case 3:
						textForOpen.setForeground(new Color(Integer
								.parseInt(line)));
						break;
					case 4:
						openFontName = line;
						for (int i = 0; i < openFontDropBox
								.getSelectedObjects().length; i++) {
							openFontDropBox.setSelectedItem(line);
						}
						break;
					case 5:
						openFontSize = Integer.parseInt(line);
						openFontSizeSpinner.setValue(openFontSize);
						break;
					case 6:
						openFontStyle = Integer.parseInt(line);
						break;
					case 7:
						textForClose.setForeground(new Color(Integer
								.parseInt(line)));
						break;
					case 8:
						closeFontName = line;
						for (int i = 0; i < closeFontDropBox
								.getSelectedObjects().length; i++) {
							closeFontDropBox.setSelectedItem(line);
						}
						break;
					case 9:
						closeFontSize = Integer.parseInt(line);
						closeFontSizeSpinner.setValue(closeFontSize);
						break;
					case 10:
						closeFontStyle = Integer.parseInt(line);
						break;
					}
					lineCount++;
				}
				textForOpen.setFont(new Font(openFontName, openFontStyle,
						openFontSize));
				textForClose.setFont(new Font(closeFontName, closeFontStyle,
						closeFontSize));
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

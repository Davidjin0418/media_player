package view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Color;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import main.Main;

/**
 * This frame is used to create subtitle file
 * 
 * @author bjin718
 * 
 */
public class SubtitleFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private int number;
	private JTextArea textArea;

	/**
	 * Create the frame.
	 */
	public SubtitleFrame() {
		number = 1;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblStart = new JLabel("start Time:");
		lblStart.setBounds(12, 12, 88, 15);
		contentPane.add(lblStart);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(12, 80, 426, 143);
		contentPane.add(textArea);

		JLabel lblEnd = new JLabel("End Time:");
		lblEnd.setBounds(237, 12, 70, 15);
		contentPane.add(lblEnd);

		textField = new JTextField();
		textField.setBounds(22, 29, 396, 25);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel lblNewLabel = new JLabel(":");
		lblNewLabel.setBounds(155, 12, 70, 15);
		contentPane.add(lblNewLabel);

		JLabel label = new JLabel(":");
		label.setBounds(348, 12, 44, 15);
		contentPane.add(label);

		final JSpinner startMinute = new JSpinner();
		startMinute.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		startMinute.setBounds(102, 10, 44, 20);
		contentPane.add(startMinute);

		final JSpinner startSecond = new JSpinner();
		startSecond.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		startSecond.setBounds(159, 10, 44, 20);
		contentPane.add(startSecond);

		final JSpinner endMinute = new JSpinner();
		endMinute.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		endMinute.setBounds(305, 10, 44, 20);
		contentPane.add(endMinute);

		final JSpinner endSecond = new JSpinner();
		endSecond.setModel(new SpinnerNumberModel(0, 0, 59, 1));
		endSecond.setBounds(358, 10, 44, 20);
		contentPane.add(endSecond);

		JButton btnAdd = new JButton("Add to subtitle");
		// create subtitle
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.append(String.valueOf(number) + "\n");
				textArea.append("00:" + String.valueOf(startMinute.getValue())
						+ ":" + String.valueOf(startSecond.getValue()) + ",000"
						+ " --> " + "00:"
						+ String.valueOf(endMinute.getValue()) + ":"
						+ String.valueOf(endSecond.getValue()) + ",000" + "\n");
				textArea.append(textField.getText() + "\n");
				textArea.append("\n");
				number++;
			}
		});
		btnAdd.setBounds(147, 53, 144, 25);
		contentPane.add(btnAdd);
		// save to srt file
		JButton btnSave = new JButton("Save to srt");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = textArea.getText();
				Writer writer = null;
				try {
					writer = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(Main.file.getPath()+".srt"), "utf-8"));
					writer.write(s);
				} catch (IOException ex) {
				} finally {
					try {
						writer.close();
					} catch (Exception ex) {
					}
				}
				File f=new File(Main.file.getPath()+".srt");
				if(f.exists()){
				
					JOptionPane.showMessageDialog(null, "srt file is generated successfully");
				}
			}
		});
		btnSave.setBounds(159, 235, 117, 25);
		contentPane.add(btnSave);
	}
}

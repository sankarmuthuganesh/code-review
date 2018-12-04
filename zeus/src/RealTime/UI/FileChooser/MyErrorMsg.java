/**
 * MyErrorMsg.java
 *
 * @author  Zhongli Ding
 * @author  Serm 3/01/2003 change from label to text area to display multiple lines.
 * Last Modified: April 11, 2003 by Ding to fix some displaying problem
 */

package RealTime.UI.FileChooser;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.border.EtchedBorder;

/**
 * @author Jaewook Kim
 *
 */
public class MyErrorMsg extends javax.swing.JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private javax.swing.JPanel bckPanel;

	private javax.swing.JTextArea msgTextArea;

	private javax.swing.JScrollPane msgScrollPane;

	private javax.swing.JPanel msgPanel;

	private javax.swing.JButton button;

	private javax.swing.JPanel buttonPanel;

	private String msg = "";

	// Constructor
	public MyErrorMsg(java.awt.Frame parent, String msg) {
		super(parent, "Error Message", true);
		this.msg = msg;
		initComponents();
	}

	// Called from the constructor to initialize the dialog window
	private void initComponents() {

		bckPanel = new javax.swing.JPanel();
		msgTextArea = new javax.swing.JTextArea(3, 50);
		msgTextArea.setLineWrap(true);
		msgTextArea.setText(this.msg);
		msgTextArea.setEditable(false);
		msgScrollPane = new javax.swing.JScrollPane();
		msgPanel = new javax.swing.JPanel();
		button = new javax.swing.JButton("OK");
		buttonPanel = new javax.swing.JPanel();

		this.setResizable(false);
		this.setLocation(350, 250);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt) {
				closeDialog(evt);
			}
		});

		bckPanel.setPreferredSize(new java.awt.Dimension(400, 150));

		msgTextArea.setFont(new java.awt.Font("Dialog", 1, 14));
		msgScrollPane.setPreferredSize(new java.awt.Dimension(380, 80));
		msgScrollPane.getViewport().add(msgTextArea);

		msgPanel.setPreferredSize(new java.awt.Dimension(390, 90));
		msgPanel.add(msgScrollPane);

		bckPanel.add(msgPanel);

		buttonPanel.setPreferredSize(new java.awt.Dimension(380, 40));

		button.setPreferredSize(new java.awt.Dimension(100, 30));
		button.setFont(new java.awt.Font("Dialog", 1, 12));
		button.setBorder(new javax.swing.border.EtchedBorder(
				EtchedBorder.RAISED));
		button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button_actionPerformed(e);
			}
		});

		buttonPanel.add(button);

		bckPanel.add(buttonPanel);

		this.getContentPane().add(bckPanel);
		pack();
	}

	private void closeDialog(WindowEvent e) {
		this.setVisible(false);
		this.dispose();
	}

	private void button_actionPerformed(ActionEvent e) {
		this.setVisible(false);
		this.dispose();
	}

}

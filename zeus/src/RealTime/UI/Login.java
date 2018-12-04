package RealTime.UI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import RealTime.GitAccess.Progress;
import RealTime.UI.Utilities.ImageDrawer;
public class Login {

	private JFrame frame;
	private JTextField fieldUsername;
	private JPasswordField fieldPassword;
	private JTextField fieldGITURL;

	/**
	 * ***********************Starting point of Gravity.***********************
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//Logging Starts
					Login window = new Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
				}
			}
		});
	}
	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public Login() throws IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
		frame = new JFrame();
		frame.getContentPane().setForeground(new Color(192, 192, 192));
		frame.getContentPane().setFont(new Font("Broadway", Font.BOLD, 11));
		frame.setBounds(100, 100, 400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		//For setting a Image as Background 
		frame.setContentPane(new JLabel(){
			protected void paintComponent(Graphics g){
				URL url=RepositoryBranchSelection.class.getResource("/edd.jpg");
				BufferedImage img;
				try {
					img = ImageIO.read(url);
					Image dimg = img.getScaledInstance(2000, 1000, Image.SCALE_SMOOTH);
					ImageIcon imageIcon=new ImageIcon(dimg);
					if(imageIcon!=null){
						//For Maximized window image expansion
						ImageDrawer.drawScaledImage(imageIcon.getImage(),this,g);
					}
				} catch (IOException e) {
				}
			}
		});

		//Label - Gravity 
		JLabel labelGravity = new JLabel("Gravity");
		labelGravity.setForeground(new Color(102, 205, 170));
		labelGravity.setHorizontalAlignment(SwingConstants.CENTER);
		labelGravity.setFont(new Font("Magneto", Font.BOLD, 40));
		labelGravity.setBounds(92, 21, 196, 53);
		frame.getContentPane().add(labelGravity);
		
		//Label - Version
		JLabel labelVersion = new JLabel("v11");
		labelVersion.setForeground(new Color(0, 250, 154));
		labelVersion.setFont(new Font("Times New Roman", Font.BOLD, 10));
		labelVersion.setBounds(274, 48, 46, 14);
		frame.getContentPane().add(labelVersion);
		
		//Label - Username 
		JLabel labelUsername = new JLabel("Username");
		labelUsername.setForeground(new Color(255, 228, 181));
		labelUsername.setFont(new Font("Times New Roman", Font.BOLD, 18));
		labelUsername.setBounds(56, 92, 77, 30);
		frame.getContentPane().add(labelUsername);

		//Texbox - Username 
		fieldUsername = new JTextField();
		fieldUsername.setBounds(143, 97, 171, 25);
		frame.getContentPane().add(fieldUsername);
		fieldUsername.setColumns(10);

		//Label - Password 
		JLabel labelPassword = new JLabel("Password");
		labelPassword.setForeground(new Color(255, 228, 181));
		labelPassword.setFont(new Font("Times New Roman", Font.BOLD, 18));
		labelPassword.setBounds(56, 133, 77, 30);
		frame.getContentPane().add(labelPassword);

		//Textbox - Password 
		fieldPassword = new JPasswordField();
		fieldPassword.setBounds(143, 133, 171, 25);
		frame.getContentPane().add(fieldPassword);

		//Button - Login
		JButton buttonLogin = new JButton("Login");
		buttonLogin.setHorizontalTextPosition(AbstractButton.CENTER);
		buttonLogin.setVerticalTextPosition(AbstractButton.CENTER);
		buttonLogin.setForeground(Color.BLACK);
		buttonLogin.setFont(new Font("Times New Roman", Font.BOLD, 17));
		buttonLogin.setBounds(183, 169, 89, 30);
		frame.getContentPane().add(buttonLogin);
		frame.getRootPane().setDefaultButton(buttonLogin);
		//Button Action -  for Button Login
		buttonLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Progress gitCall=new Progress();
				try {
					Map<Boolean, String> resultMap = gitCall.checkLoginValidity(fieldUsername.getText(), fieldPassword.getText(),fieldGITURL.getText());
					if(resultMap.containsKey(false)){
						JOptionPane.showMessageDialog(null, "Login Failed,, Please Try Again");
						frame.setVisible(false);
						frame.dispose();
						Login.main(null);
					}
					else{
						frame.setVisible(false);
						frame.dispose();
						String userName=resultMap.get(true);
						List<String> repoList = gitCall.getListOfRepositories();
						RepositoryBranchSelection.callFromAnotherFrame(userName,repoList,gitCall);
					}
				} catch (IOException  e1) {
				}
			}
		});
		
		//Label - GitURL
		JLabel labelGitURL = new JLabel("Git URL");
		labelGitURL.setFont(new Font("Times New Roman", Font.BOLD, 12));
		labelGitURL.setForeground(new Color(255, 250, 205));
		labelGitURL.setBounds(10, 233, 46, 14);
		frame.getContentPane().add(labelGitURL);
		
		//TextBox - IP for Git
		fieldGITURL = new JTextField("192.168.41.136");
		fieldGITURL.setForeground(Color.DARK_GRAY);
		fieldGITURL.setBackground(Color.WHITE);
		fieldGITURL.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		fieldGITURL.setBounds(56, 229, 148, 20);
		frame.getContentPane().add(fieldGITURL);
		fieldGITURL.setColumns(10);
	}
}

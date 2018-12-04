package RealTime.UI.FileChooser;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import RealTime.GitAccess.Progress;
import RealTime.UI.Utilities.ImageDrawer;

public class NewTry {

  private JFrame frame;
  private String sourceLabel = "THE";

  /**
   * ***********************Starting point of Gravity.***********************
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          // Logging Starts
          NewTry window = new NewTry();
          window.frame.setVisible(true);
        } catch (Exception e) {
        }
      }
    });
  }

  /**
   * Create the application.
   * 
   * @throws IOException
   */
  public NewTry() throws IOException {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   * 
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

    // Button - Login
    JButton buttonLogin = new JButton("Login");
    buttonLogin.setHorizontalTextPosition(AbstractButton.CENTER);
    buttonLogin.setVerticalTextPosition(AbstractButton.CENTER);
    buttonLogin.setForeground(Color.BLACK);
    buttonLogin.setFont(new Font("Times New Roman", Font.BOLD, 17));
    buttonLogin.setBounds(183, 169, 89, 30);
    frame.getContentPane().add(buttonLogin);
    frame.getRootPane().setDefaultButton(buttonLogin);
    // Button Action - for Button Login
    buttonLogin.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        importSourceSchema_actionPerformed(e);
      }
    });
  }

  private void importSourceSchema_actionPerformed(ActionEvent e) {
    JFileChooser fc = new JFileChooser();
    MyFileFilter xlsFilter = new MyFileFilter(new String[] {"xls"}, "Excel File");
    fc.addChoosableFileFilter(xlsFilter);
    fc.setFileFilter(xlsFilter);
    int returnVal = fc.showOpenDialog(frame);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      if (file.exists() == false) {
        String msg = "The file does not exist!";
        MyErrorMsg error = new MyErrorMsg(frame, msg);
        error.setVisible(true);
      }
      // To generate the tree model for XSD file
      else {
        String sourceFilePath = file.getAbsolutePath(); // the absolute XSD file name
        String fshortname = file.getName();
        System.out.println(fshortname + " is chosen");
      }
    } else {
      return;
    }
  }
}

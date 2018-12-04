package RealTime.UI;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.eclipse.jgit.api.errors.GitAPIException;
import RealTime.GitAccess.Progress;
import RealTime.GitAccess.Progress.Task;
import RealTime.UI.Utilities.ImageDrawer;
import com.github.javaparser.ParseException;

public class RepositoryBranchSelection  implements PropertyChangeListener{

	private  JFrame frame;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	JList list;
	static List<String> repoList=new ArrayList<>();
	static Progress gitCall;
	JProgressBar progressBar ;
	private Progress.Task task;
	public static RepositoryBranchSelection window;
	JLabel lblCloningBranches;
	static List<String> filesPutInToList;
	static String userNameField;
	private Map<String,List<String>> eachRepoBranches=new HashMap<>();
	private Map<String, List<String>> selectedRepoAndBranches=new HashMap<>();

	public void setProgress(int percentCompleted){

		progressBar.setValue(percentCompleted);

		//progressBar.update(progressBar.getGraphics());
	}


	/**
	 * Launch the application.
	 */
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					window = new RepositoryBranchSelection();
					filesPutInToList=new ArrayList<>();
					//window.branches=branchesPassed;
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void callFromAnotherFrame(String userName, List<String> repoListPassed,Progress gitCallPassed){
		//System.out.println(branchesPassed);
		//System.out.println(gitCallPassed);
		gitCall=gitCallPassed;
		userNameField=userName;
		repoList=repoListPassed;
		RepositoryBranchSelection.main(null);
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public RepositoryBranchSelection() throws IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		AtomicInteger  selectedBranchesManuallyIndex=new AtomicInteger(0);
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(0, 0, 0));
		frame.getContentPane().setForeground(new Color(0, 0, 0));
		frame.setBounds(200, 200, 725, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		//frame.setUndecorated(true);

		//frame.pack();
//		URL url=Branch.class.getResource("/edd.jpg");
//	
//		BufferedImage	img = ImageIO.read(url);
//			Image dimg = img.getScaledInstance(2000, 1000, Image.SCALE_SMOOTH);
//			ImageIcon imageIcon=new ImageIcon(dimg);
//			frame.setContentPane(new JLabel(imageIcon));
			
		frame.setContentPane(new JLabel(){
		protected void paintComponent(Graphics g){
			URL url=RepositoryBranchSelection.class.getResource("/edd.jpg");
				BufferedImage img;
				try {
					img = ImageIO.read(url);
					Image dimg = img.getScaledInstance(2000, 1000, Image.SCALE_SMOOTH);
					ImageIcon imageIcon=new ImageIcon(dimg);
					if(imageIcon!=null){
						ImageDrawer.drawScaledImage(imageIcon.getImage(),this,g);
					}
				} catch (IOException e) {
				}
			}
		});

		ArrayList<String> arrayListRepos=new ArrayList<String>();
		int countRepos=0;
		while(countRepos<repoList.size()){
			//StringBuilder item=new StringBuilder("<html>"+(countRepos+1)+".&nbsp;"+"<i><b>"+repoList.get(countRepos)+"</b></i>"+"</html>");
			arrayListRepos.add(repoList.get(countRepos));
			countRepos++;
		}

		//		
		//		for(String eachRepo:repoList){
		//			try {
		//				eachRepoBranches.put(eachRepo,gitCall.getAllBranchesForTheSelectedRepository(eachRepo));
		//			} catch (GitAPIException e1) {
		//				// TODO Auto-generated catch block
		//				e1.printStackTrace();
		//			}
		//		}
		//		
		//	}else{
		//		try {
		//			branches=gitCall.getAllBranchesForTheSelectedRepository(listRepos.getSelectedValue().toString());
		//		} catch (GitAPIException e1) {
		//			// TODO Auto-generated catch block
		//			e1.printStackTrace();
		//		}
		//	}
		//		


		JScrollPane scrollPaneRepos = new JScrollPane();
		scrollPaneRepos.setBounds(45, 41, 258, 285);
		scrollPaneRepos.setVisible(false);

		JScrollPane scrollPaneBranches = new JScrollPane();
		scrollPaneBranches.setBounds(277, 60, 258, 287);

		JButton btnMoveOn = new JButton("Select a Functionality");
		btnMoveOn.setBackground(new Color(0, 128, 128));
		btnMoveOn.setBounds(306, 372, 191, 33);
		btnMoveOn.setToolTipText("Click to Move to Selecting a Functionality...");
		btnMoveOn.setFont(new Font("Times New Roman", Font.BOLD, 15));
		btnMoveOn.setForeground(new Color(0, 0, 0));

		JTextPane textPane = new JTextPane();
		textPane.setBounds(295, 312, 217, 23);
		textPane.setBackground(Color.WHITE);
		textPane.setToolTipText("River Versions!");

		JRadioButton radioButtonSelectManually = new JRadioButton("Select Branches Manually");
		radioButtonSelectManually.setBounds(17, 102, 234, 33);
		radioButtonSelectManually.setForeground(new Color(0, 0, 0));
		radioButtonSelectManually.setToolTipText("Select Branches By their Actual Branch Name...");
		radioButtonSelectManually.setBackground(new Color(255, 255, 255));
		radioButtonSelectManually.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));

		JRadioButton radioButtonUseVersions = new JRadioButton("Input River Branch Versions");
		radioButtonUseVersions.setBounds(17, 171, 234, 34);
		radioButtonUseVersions.setForeground(new Color(0, 0, 0));
		radioButtonUseVersions.setToolTipText("Use the Version Number (1709) to Select Branches..NOTE: This is for river Branches only...");
		radioButtonUseVersions.setBackground(new Color(255, 255, 255));
		radioButtonUseVersions.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 15));

		progressBar = new JProgressBar(0, 100);
		progressBar.setBounds(293, 452, 217, 33);
		progressBar.setToolTipText("The Branches are getting Cloned...");
		progressBar.setPreferredSize(new Dimension(200,300));
		progressBar.setStringPainted(true);

		lblCloningBranches = new JLabel("Cloning Branches...");
		lblCloningBranches.setBounds(294, 416, 215, 25);
		lblCloningBranches.setForeground(new Color(255, 255, 204));
		lblCloningBranches.setHorizontalAlignment(SwingConstants.CENTER);
		lblCloningBranches.setFont(new Font("Times New Roman", Font.BOLD, 15));

		JButton btnNewButton = new JButton("<html><b><i>Please Input the Versions.</i></b><br>"
				+ "<b><i>Examples:</i></b><br>"
				+ "1. >1709<br>"
				+ "2. &lt;1803 <br>"
				+ "3. >1709&lt;1711 <br>"
				+ "4. 1709,1803 <br>"
				+ "5. 1812 <br>"
				+ "6. river <br>"
				+ "<i>NOTES:</i> > - GreaterThan <br>"
				+ "&lt; - LesserThan<br>"
				+ "<i>TYPE BELOW</i></html>");
		btnNewButton.setBounds(292, 94, 222, 180);
		btnNewButton.setVerticalAlignment(SwingConstants.TOP);
		btnNewButton.setVisible(false);

		ArrayList<StringBuilder> arrayListBranches=new ArrayList<StringBuilder>();
		frame.getContentPane().setLayout(null);


		JLabel lblHi = new JLabel("<html> Hi! <b>"+userNameField+",</b></html>");
		lblHi.setBounds(20, 11, 585, 47);
		lblHi.setForeground(new Color(204, 255, 255));
		lblHi.setFont(new Font("PMingLiU-ExtB", Font.BOLD, 18));
		frame.getContentPane().add(lblHi); 

		/*JButton btnNewButton_1 = new JButton("Specific File Names");
		try{
			Image img=ImageIO.read(new File("images/edd.jpg"));
			btnNewButton_1.setIcon(new ImageIcon(img));
			btnNewButton_1.setMargin(new Insets(0,0,0,0));
			btnNewButton_1.setBorder(null);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		btnNewButton_1.setBounds(590, 336, 130, 23);
		frame.getContentPane().add(btnNewButton_1);*/

		UIManager.put("TabbedPane.contentOpaque", false);
		UIManager.put("TabbedPane.borderHightlightColor", Color.BLACK);
		UIManager.put("TabbedPane.darkShadow", Color.BLACK);
		UIManager.put("TabbedPane.light", Color.BLACK);
		UIManager.put("TabbedPane.selectHighlight", Color.BLACK);
		UIManager.put("TabbedPane.focus", Color.BLACK);
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Times New Roman", Font.BOLD, 15));
		tabbedPane.setBorder(null);


		//tabbedPane.setOpaque(false);
		//tabbedPane.setBackground(new Color(0,0,0,0.5f));
		//tabbedPane.setBackground(new Color(0,0,0));
		tabbedPane.setBounds(20, 70, 655, 558);
		frame.getContentPane().add(tabbedPane);
		JPanel tabOne=new JPanel(new GridBagLayout()){
			@Override
			protected void paintComponent(Graphics g){
				super.paintComponent(g);
				URL url=Login.class.getResource("/edd.jpg");
				BufferedImage img;
				try {
					img = ImageIO.read(url);
					Image dimg = img.getScaledInstance(1000, 1000, Image.SCALE_SMOOTH);
					g.drawImage(dimg, 0, 0, getWidth(), getHeight(), this);
					Graphics2D g2d=(Graphics2D)g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
					//g2d.setColor(Color.yellow);
					//g2d.fillRect(0, 0, 1200, 600);
				} catch (IOException e) {
				}

			}
			@Override
			public Dimension getPreferredSize(){
				return new Dimension(400,300);
			}
		};
		//tabOne.setOpaque(false);
		tabOne.setBorder(null);
		//tabOne.setBackground(new Color(0,0,0,0.5f));
		tabOne.add(scrollPaneRepos);
		//frame.getContentPane().add(list);

		JList listRepos = new JList(arrayListRepos.toArray());
		listRepos.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		scrollPaneRepos.setViewportView(listRepos);
		listRepos.setToolTipText("Use Cntrl or Shift to select Multiple Repositories");
		listRepos.setVisibleRowCount(20);
		listRepos.setVisible(false);

		listRepos.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(listRepos.getSelectedValuesList().size()>1){
					radioButtonSelectManually.setVisible(false);
				}	else{
					radioButtonSelectManually.setVisible(true);
				}
			}
		});

		//tabTwo.setBackground(new Color(0,0,0,0.5f));
		JPanel tabTwo=new JPanel(new GridBagLayout()){
			@Override
			protected void paintComponent(Graphics g){
				super.paintComponent(g);
				URL url=Login.class.getResource("/edd.jpg");
				BufferedImage img;
				try {
					img = ImageIO.read(url);
					Image dimg = img.getScaledInstance(1000, 1000, Image.SCALE_SMOOTH);
					g.drawImage(dimg, 0, 0, getWidth(), getHeight(), this);
					Graphics2D g2d=(Graphics2D)g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
				} catch (IOException e) {
				}
			}
			@Override
			public Dimension getPreferredSize(){
				return new Dimension(400,300);
			}
		};
		tabTwo.setBorder(null);

		//tabTwo.setOpaque(false);
		tabbedPane.add("   Repository   ",tabOne);
		tabbedPane.setBackgroundAt(0, new Color(0, 0, 0));
		tabOne.setLayout(null);
		//frame.setIconImage();

		JComboBox comboBox = new JComboBox(repoList.toArray());
		comboBox.setFont(new Font("Times New Roman", Font.BOLD, 15));
		comboBox.setBounds(45, 41, 258, 20);
		tabOne.add(comboBox);
		AtomicInteger whichMethodIsChosen=new AtomicInteger(0);
		JToggleButton toggleButton = new JToggleButton("+");
		toggleButton.setToolTipText("Click to Select Multiple Repositories.");
		toggleButton.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 15));
		toggleButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				int state=e.getStateChange();
				if(state==ItemEvent.SELECTED){
					comboBox.setVisible(false);
					listRepos.setVisible(true);
					scrollPaneRepos.setVisible(true);
					whichMethodIsChosen.set(1);
				}
				else if(state==ItemEvent.DESELECTED){
					comboBox.setVisible(true);
					listRepos.setVisible(false);
					scrollPaneRepos.setVisible(false);
					whichMethodIsChosen.set(0);
				}
			}
		});
		toggleButton.setBounds(313, 41, 49, 20);

		JLabel lblSelectedRepositories = new JLabel();
		lblSelectedRepositories.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblSelectedRepositories.setForeground(new Color(255, 255, 204));
		lblSelectedRepositories.setBounds(5, 10, 300, 50);
		tabTwo.add(lblSelectedRepositories);
		JLabel lblGettingBranchesList = new JLabel("Getting Branches List For You.. Please Wait...");
		lblGettingBranchesList.setForeground(new Color(102, 205, 170));
		lblGettingBranchesList.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblGettingBranchesList.setBounds(206, 501, 284, 14);
		tabOne.add(lblGettingBranchesList);
		lblGettingBranchesList.setVisible(false);
	
		tabTwo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				lblGettingBranchesList.setVisible(true);
			
			}
		});
		
		//frame.getContentPane().add(lblGettingBranchesList);
		
		//		tabTwo.addMouseListener(new MouseAdapter() {
		//			@Override
		//			public void mouseClicked(MouseEvent e) {
		//				
		//			}
		//		});
		
		JButton btnBackToChoose = new JButton("Logout");
		btnBackToChoose.setBackground(new Color(102, 205, 170));
		btnBackToChoose.setFont(new Font("Eras Medium ITC", Font.BOLD, 10));
		btnBackToChoose.setToolTipText("Move to Login Page");
		btnBackToChoose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//passFrame.setVisible(true);
				Login.main(null);
				frame.setVisible(false);
				frame.dispose();
			}
		});
		btnBackToChoose.setBounds(600, 11, 70, 26);
		frame.getContentPane().add(btnBackToChoose);
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
//				if(tabbedPane.getSelectedIndex()==0){
//					lblGettingBranchesList.setVisible(true);
//				}
				buttonGroup.clearSelection();
				scrollPaneBranches.setVisible(false);
				String selectedRepoInComboBox=null;
				if(tabbedPane.getSelectedIndex()==1){
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					//lblGettingBranchesList.setVisible(true);
					Map<String,List<String>> repoCollection=new HashMap<>();
					if(whichMethodIsChosen.get()==0){
						btnNewButton.setVisible(false);
						textPane.setVisible(false);
						
						selectedRepoInComboBox=String.valueOf(comboBox.getSelectedItem());
						repoCollection.put("singleRepo",Arrays.asList(selectedRepoInComboBox));
					}
					else if((whichMethodIsChosen.get()==1)&&listRepos.getSelectedIndices().length==1){
						btnNewButton.setVisible(false);
						textPane.setVisible(false);
						
						selectedRepoInComboBox=listRepos.getSelectedValue().toString();
						repoCollection.put("singleRepo",Arrays.asList(listRepos.getSelectedValue().toString()));
					}
					else if(whichMethodIsChosen.get()==1&&listRepos.getSelectedIndices().length!=1){
						btnNewButton.setVisible(false);
						textPane.setVisible(false);
						
						int array[]=listRepos.getSelectedIndices();
						List<String> indicesListRepos=new ArrayList<>();
						int countOfIndices=0;
						while(countOfIndices<array.length){
							indicesListRepos.add(Integer.toString(array[countOfIndices]));
							countOfIndices++;
						}
						repoCollection.put("multipleRepos", indicesListRepos);
					}
					//					if(repoCollection.get("multipleRepos").size()>1){
					//						radioButtonSelectManually.setVisible(false);
					//					}
					try {
						selectedRepoAndBranches=gitCall.decideSelectedReposAndMakeCorrespondingBranches(repoCollection);
						frame.setCursor(null);
						//lblGettingBranchesList.setVisible(false);
					} catch (GitAPIException e1) {
					}
					StringBuilder repoColl=new StringBuilder();
					selectedRepoAndBranches.entrySet().stream().forEach(repos ->{
						repoColl.append(repos.getKey()+", ");
					});
					lblSelectedRepositories.setText("<html>"+repoColl.substring(0, repoColl.length()-2)+"</html>");
					
					lblGettingBranchesList.setVisible(false);

					if(whichMethodIsChosen.get()==0||listRepos.getSelectedIndices().length==1){
						arrayListBranches.clear();
						int count=0;
						while(count<selectedRepoAndBranches.get(selectedRepoInComboBox).size()){
							StringBuilder item=new StringBuilder("<html>"+(count+1)+".&nbsp;"+"<b>"+selectedRepoAndBranches.get(selectedRepoInComboBox).get(count).replace("refs/heads/", "")+"</b>"+"</html>");
							arrayListBranches.add(item);
							count++;
						}
						list = new JList(arrayListBranches.toArray());
						list.setToolTipText(arrayListBranches.size() +" Branches");
						list.setFont(new Font("Times New Roman", Font.PLAIN, 15));
						// model=new DefaultBoundedRangeModel();
						list.setVisibleRowCount(20);
						list.setVisible(false);
						list.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								btnMoveOn.setVisible(true);
							}
						});
						scrollPaneBranches.setViewportView(list);
						radioButtonSelectManually.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								if(!list.isSelectionEmpty()){
									btnMoveOn.setVisible(true);
								}
								else{
									btnMoveOn.setVisible(false);
								}
								list.setVisible(true);
								scrollPaneBranches.setVisible(true);
								btnNewButton.setVisible(false);

								list.setFocusable(true);
								textPane.setVisible(false);
								//if (e.getActionCommand().equals("Select Branches Manually")){	

								selectedBranchesManuallyIndex.set(1);
								// }	
							}
						});
					}
				}
			}
		});

		tabOne.add(toggleButton);

		JLabel lblYourRepositories = new JLabel("Your Repositories");
		lblYourRepositories.setForeground(new Color(255, 255, 204));
		lblYourRepositories.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblYourRepositories.setBounds(45, 11, 141, 19);
		tabOne.add(lblYourRepositories);
		
		tabbedPane.add("   Branches   ",tabTwo);
		tabbedPane.setBackgroundAt(1, new Color(0, 0, 0));
		tabTwo.setLayout(null);
		//frame.getContentPane().add(list);

		tabTwo.add(scrollPaneBranches);
		tabTwo.add(btnMoveOn);
		tabTwo.add(textPane);
		tabTwo.add(radioButtonSelectManually);
		tabTwo.add(radioButtonUseVersions);

		buttonGroup.add(radioButtonSelectManually);

		radioButtonUseVersions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//if(e.getActionCommand().equals("Use Versions To Select Branches")){
				textPane.setVisible(true);
				btnMoveOn.setVisible(false);
				btnNewButton.setVisible(true);
				if(whichMethodIsChosen.get()==0){
					list.setVisible(false);
				}
				scrollPaneBranches.setVisible(false);
				//}
				selectedBranchesManuallyIndex.set(2);
			}
		});

		buttonGroup.add(radioButtonUseVersions);
		tabTwo.add(progressBar);
		tabTwo.add(lblCloningBranches);
		tabTwo.add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("\u00A9 Gravity Sankar ");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 4));
		lblNewLabel.setForeground(new Color(153, 204, 204));
		lblNewLabel.setBackground(new Color(0, 0, 0));
		lblNewLabel.setBounds(615, 639, 153, 14);
		frame.getContentPane().add(lblNewLabel);

		lblCloningBranches.setVisible(false);
		progressBar.setVisible(false);
		textPane.setVisible(false);

		textPane.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				btnMoveOn.setVisible(true);
			}
		});	
		btnMoveOn.setVisible(false);

		btnMoveOn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			
				//lblStatus.setVisible(true);

				//Get the list 
				Map<Integer,List<String>> indexAndSelectedBranches=new HashMap<>();
				if(selectedBranchesManuallyIndex.get()==1){
					int array[]=list.getSelectedIndices();
					List<String> indicesList=new ArrayList<>();
					int countOfIndices=0;
					while(countOfIndices<array.length){
						indicesList.add(Integer.toString(array[countOfIndices]));
						countOfIndices++;

					}
					indexAndSelectedBranches.put(1,indicesList);

				}else{
					List<String> theFormat=new ArrayList<>();
					theFormat.add(textPane.getText());
					indexAndSelectedBranches.put(2, theFormat);
				}

				try {
					//gitCall.gettingTheSelectedBranches(indexAndSelectedBranches);
					//System.out.println(gitCall.gettingTheSelectedBranches(indexAndSelectedBranches));
					//System.out.println(indexAndSelectedBranches);
					AtomicInteger noBranches=new AtomicInteger(0);
					Map<String, List<String>> returnedMapAndBranches = gitCall.gettingTheSelectedBranches(indexAndSelectedBranches,whichMethodIsChosen);
					returnedMapAndBranches.entrySet().stream().forEach(repo -> {
						if(repo.getValue().size()==0){
							noBranches.set(1);
						}
						else{
							noBranches.set(0);
						}
					});

					if(noBranches.get()==1){
						JOptionPane.showMessageDialog(null, "There are no branches as per your needs,, try again pls..");
					}
					else{		
						btnMoveOn.setVisible(false);
						lblCloningBranches.setVisible(true);
						progressBar.setVisible(true);
						//File listOfFiles=new File("C:\\Users\\"+gitCall.gitUserNameField+"\\AppData\\Local\\Temp\\listoffiles.txt");
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						task = gitCall.new Task(frame);
						task.addPropertyChangeListener(window);
						task.execute();
						//JOptionPane.showMessageDialog(null, "The branches are getting cloned...");
						//JOptionPane.showMessageDialog(null, "OK its time to choose a Functionality");
						//frame.dispose();
					}					

				} catch (GitAPIException | IOException | ParseException
						| InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});

		/*URL urll=GravityLogin.class.getResource("/information.png");
		ImageIcon ico=new ImageIcon(urll,"Information!");
		JLabel lblInformation = new JLabel("lsakdjf",ico,JLabel.CENTER);
		Border bor=new MatteBorder(5,10,5,10,ico);
		lblInformation.setBorder(bor);
		lblInformation.setBounds(605, 419, 150, 33);
		frame.getContentPane().add(lblInformation);*/
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		} 	
		/*if(progressBar.getValue()<100){
			lblCloningBranches.setText("Cloning Branches...");
		}*/
		if(progressBar.getValue()==100){
			lblCloningBranches.setText("Cloned!");
		}
	}


	public static void putFiles(JFrame branchJFrame) {
		Map<String, Map<MultiKey, List<String>>> repoBranchesAndgroupedFiles =Progress.repoItsbranchesAndTheirListofFiles;
		Functionalities.callFromBranch(repoBranchesAndgroupedFiles,gitCall,branchJFrame);
	}
}

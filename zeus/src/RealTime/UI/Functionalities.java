package RealTime.UI;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.MultiKey;

import RealTime.Author.GiveInputToAuthors;
import RealTime.Author.MakeFilesOfAnAuthor;
import RealTime.ChartCreation.MakeInputToChart;
import RealTime.Entity.ActualErrorCollectionDetailed;
import RealTime.Entity.ErrorsAndWarnings;
import RealTime.Entity.ErrorsCollection;
import RealTime.Entity.ErrorsCollectionDetailed;
import RealTime.Entity.FileUnit;
import RealTime.FolderStructure.FolderStructure;
import RealTime.GitAccess.Progress;
import RealTime.GroupingBy.GroupingBy;
import RealTime.GroupingBy.GroupingByy;
import RealTime.IndexFileError.IndexError;
import RealTime.KeywordSearch.FindKeywordInAFile;
import RealTime.MethodFind.MethodFinder;
import RealTime.Optimus.CompatibleOutput;
import RealTime.Optimus.DefectSheet;
import RealTime.Optimus.GravityConversion.Categorizer;
import RealTime.Optimus.GravityConversion.MoreCategoriser;
import RealTime.Optimus.GravityConversion.OptimusMain;
import RealTime.Optimus.GravityConversion.OptimusOutputMoreCategoriezed;
import RealTime.Optimus.GravityConversion.SubSystemEpicGrouping;
import RealTime.SessionCache.FieldVisitor;
import RealTime.SubSystemEpicSplit.ClassifyUsingController;
import RealTime.UI.Utilities.ImageDrawer;
import RealTime.UnnecessaryConstantsFields.java.GlobalVarList;
import RealTime.UnnecessaryConstantsFields.java.withinfile.UnnecessaryClassField;
import RealTime.UnnecessaryConstantsFields.javascript.JsUnused;

import com.github.javaparser.ParseException;


public class Functionalities {

	private JFrame frame;
	private JTextField textField;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	static Progress gitCall;
	static Map<String, Map<MultiKey, List<String>>> groupedFiles;
	private static JFrame passFrame;
	private static List<String> filesPutInToList=new ArrayList<>();
	private static List<String> constantList=new ArrayList<>();
	Map<String, List<String>> authorDetails=null;
	Map<String, Map<MultiKey, List<String>>> finalBrowseThroughFiles;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Functionalities window = new Functionalities();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void callFromBranch(Map<String, Map<MultiKey, List<String>>> groupedFilesPassed,Progress gitCallPassed, JFrame passedFrame){

		passFrame=passedFrame;
		groupedFiles=groupedFilesPassed;
		gitCall=gitCallPassed;
		Functionalities.main(null);
	}
	/**
	 * Create the application.
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public Functionalities() throws FileNotFoundException, IOException, ParseException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 */
	private void initialize() throws IOException{
		frame = new JFrame();

		frame.getContentPane().setBackground(new Color(255, 248, 220));
		frame.setBounds(100, 100, 1000, 750);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		//		frame.getContentPane().setBackground(new Color(0, 0, 0));
		//		frame.getContentPane().setForeground(new Color(0, 0, 0));
		frame.getContentPane().setLayout(null);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent windowEvent){
				if(JOptionPane.showConfirmDialog(frame, "Are You Sure to Close this window?","Really Closing?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION){
					try {

						ClassLoader classLoader=RepositoryBranchSelection.class.getClassLoader();
						URL url=RepositoryBranchSelection.class.getResource("/delete.bat");
						//String command="cmd /c start \"\" "+url.getPath().substring(1,url.getPath().length());
							//Process p=Runtime.getRuntime().exec(command);

						//Process p=Runtime.getRuntime().exec("cmd /c start \"\" C:\\HUE\\WorkSpace\\Personal\\Zeus\\src\\delete.bat");
					} catch (NullPointerException e) {
						e.printStackTrace();
						System.exit(0);
					}
					System.exit(0);
				}
			}
		});

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

		URL url=Login.class.getResource("/edd.jpg");

		JLabel lblChooseAFunctionality = new JLabel("Choose a Functionality!");
		lblChooseAFunctionality.setForeground(new Color(64, 224, 208));
		lblChooseAFunctionality.setBackground(Color.WHITE);
		lblChooseAFunctionality.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblChooseAFunctionality.setBounds(212, 11, 263, 31);
		frame.getContentPane().add(lblChooseAFunctionality);


		JButton btnBackToChooseL = new JButton("Logout");
		btnBackToChooseL.setFont(new Font("Eras Medium ITC", Font.BOLD, 12));
		btnBackToChooseL.setToolTipText("Move to Login Page");
		btnBackToChooseL.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//passFrame.setVisible(true);
				Login.main(null);
				frame.setVisible(false);
				frame.dispose();
			}
		});
		btnBackToChooseL.setBounds(580, 11, 80, 26);
		frame.getContentPane().add(btnBackToChooseL);


		BasicArrowButton btnBackToChoose = new BasicArrowButton(BasicArrowButton.WEST);
		btnBackToChoose.setToolTipText("Move Back and Reselect Different Branches...");
		btnBackToChoose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//passFrame.setVisible(true);
				RepositoryBranchSelection.main(null);
				frame.setVisible(false);
				frame.dispose();
			}
		});
		btnBackToChoose.setBounds(33, 11, 48, 23);

		frame.getContentPane().add(btnBackToChoose);

		JLabel lblNewLabel = new JLabel("Back");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setFont(new Font("Microsoft Tai Le", Font.BOLD | Font.ITALIC, 15));
		lblNewLabel.setBounds(20, 33, 74, 31);
		frame.getContentPane().add(lblNewLabel);

		AtomicInteger setChecked=new AtomicInteger(0);

		JButton btnClickToInput = new JButton("Click to Check in Specific Files");
		btnClickToInput.setBounds(379, 638, 204, 23);
		frame.getContentPane().add(btnClickToInput);
		btnClickToInput.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnClickToInput.setHorizontalAlignment(SwingConstants.LEFT);
		JCheckBox chckbxForAllFiles = new JCheckBox("For All Files in the Repo-in the Selected Branch");
		chckbxForAllFiles.setFont(new Font("Times New Roman", Font.BOLD, 12));
		chckbxForAllFiles.setBounds(379, 604, 312, 23);
		frame.getContentPane().add(chckbxForAllFiles);

		JButton btnSearch = new JButton("Output");
		btnSearch.setBounds(379, 682, 99, 23);
		frame.getContentPane().add(btnSearch);
		btnSearch.setForeground(new Color(0, 0, 0));
		btnSearch.setToolTipText("Click to Invoke the Functionality");
		btnSearch.setFont(new Font("Times New Roman", Font.BOLD, 15));

		JLabel lblMustMentionAny = new JLabel("<html>Make any of these choices before proceeding.</html>");
		lblMustMentionAny.setBounds(740, 592, 110, 62);
		frame.getContentPane().add(lblMustMentionAny);

		JLabel label = new JLabel(" <-");
		label.setBounds(709, 613, 46, 14);
		frame.getContentPane().add(label);

		JToggleButton tglbtnKk = new JToggleButton();
		tglbtnKk.setFont(new Font("Times New Roman", Font.BOLD, 15));
		tglbtnKk.setBounds(104, 45, 121, 31);
		frame.getContentPane().add(tglbtnKk);
		label.setVisible(false);
		tglbtnKk.setText("Optimus");
		//		"Package name should starts with com.worksap.company",
		//		"variable name should be minimum 3 characters in length",
		//		"Variable name should be in lowerCamelCase",
		//		"Comment is missing for the variable",
		//		"Dont use Constant for empty string. Use org.apache.commons.lang3.StringUtils.EMPTY",
		//		"Global variable should be private",
		//		"Use of \"CompanyCacheManager\",\"RedisCacheManager\",\"EhcacheManager\"",
		//		"\"ServiceSessionCacheSharedService\" variableTypes is restricted" ,
		//		"List variable name is not ending with list or plural form",
		//		"Lists.newArrayList() should not be used. Use new ArrayList() instead",
		//		"Map variable name is not ending with map",
		//		"Maps.newHashMap() should not be used. Use new HashMap() instead",
		//		"Set variable name is not ending with set",
		//		"Sets.newHashSet() should not be used. Use new Set() instead",
		//		"Queue variable name is not ending with queue",
		//		"Dqueue variable name is not ending with dqueue",
		//		"Class name of the Constant file should ends with Constant or Constants",
		//		"Service interface name should ends with Service",
		//		"Service Class name should ends with ServiceImpl and implemented interface name should ends with Service",
		//		"Dao interface name should ends with Dao.",
		//		"Dao Class name should ends with DaoImpl and implemented interface name should ends with Dao",
		//		"Controller class name should ends with Controller",
		//		"Dto class and Index class name should ends with Dto,Index respectively",
		//		"HueSerializable must be implemented in Model and Beans",
		//		"Missing @author,@since in the class comments",
		//		"No Comments added for the Class",
		//		"@Entity is Mandatory for Dto and Index files",
		//		"@Entity should not be used other than Dto,Index and DaoImpl",
		//		"Please remove @Autowired, use @RequiredArgsConstructor(onConstructor = @__(@Autowired))",
		//		"Method name should be in camelCase",
		//		"Remove search, searchAll functions in the method Use searchWithIterator() instead",
		//		"Don't use SearchConditions.ALL()",
		//		"Function should be private other than overrided method",
		//		"Should not use HttpServletRequest , HttpServletResponse ",
		//		"Comments is missing for the function",
		//		"Missing @param,@return,@throws in the function",
		//		"Use Objects.nonNull() instead of == null and !=null",
		//		"HardCoded Integer value : Use Constants instead of using Integer literal",
		//		"boolean comparision should be avoided",
		//		"In controller Use Pojo instead of using Map or List of Map in @RequestParam or @RequestBody",
		//		"Remove Print statement in the line or block",
		//		"printStackTrace should not be used. Use log instead",
		//		"Should not use KeyValueAccess other than Dao and DaoImpl files"

		List<String> bugCategoriesJava=Arrays.asList("Variable","Constants","VariableName","PackageClassName","HueSerializable","DocsComments",
				"Annotations","ShouldNotUse","AvoidsAndAlternatives","DaoNotAllowedChecks","Global"
				,"DtoEntityRelated","MethodViolations","WarningList","ImportIssue");

		List<String> bugCategoriesJavaScript=Arrays.asList(
				"Docs","RestrictedUsages","AccessOfPrivateMembers","VariableRelated","BrowserObjects","CommonBugs","Functions","Naming");

		List<String> bugCategoriesXML=Arrays.asList("NamingConvention","Hardcodes","Violations","FileRelated","Naming","Forbiddens","Comments");

		ArrayList<String> bugCategoriesJavaArray=new ArrayList<String>();
		int countErrorsJava=0;
		while(countErrorsJava<bugCategoriesJava.size()){
			//Commented Because while getting numbers also comes with it
			//StringBuilder item=new StringBuilder("<html>"+(countErrorsJava+1)+".&nbsp;"+"<i><b>"+bugCategoriesJava.get(countErrorsJava)+"</b></i>"+"</html>");
			bugCategoriesJavaArray.add(bugCategoriesJava.get(countErrorsJava));
			countErrorsJava++;
		}

		ArrayList<String> bugCategoriesJavaScriptArray=new ArrayList<String>();
		int countErrorsJavaScript=0;
		while(countErrorsJavaScript<bugCategoriesJavaScript.size()){

			//	StringBuilder item=new StringBuilder("<html>"+(countErrorsJavaScript+1)+".&nbsp;"+"<i><b>"+bugCategoriesJavaScript.get(countErrorsJavaScript)+"</b></i>"+"</html>");
			bugCategoriesJavaScriptArray.add(bugCategoriesJavaScript.get(countErrorsJavaScript));
			countErrorsJavaScript++;
		}

		ArrayList<String> bugCategoriesXMLArray=new ArrayList<String>();
		int countErrorsXML=0;
		while(countErrorsXML<bugCategoriesXML.size()){

			//	StringBuilder item=new StringBuilder("<html>"+(countErrorsXML+1)+".&nbsp;"+"<i><b>"+bugCategoriesXML.get(countErrorsXML)+"</b></i>"+"</html>");
			bugCategoriesXMLArray.add(bugCategoriesXML.get(countErrorsXML));
			countErrorsXML++;
		}

		//
		//				List<String> warningListActual=Arrays.asList("Duplicate constant variable with same value",
		//						"static final variable should in UPPER_CASE.",
		//						"Single quoted string is used",
		//						"Logger is Missing in Catch Clause",
		//						"Should not use any Dto other than Dao, DaoImpl and Entity files uses for Dto to Entity conversion.");
		//		
		//				ArrayList<StringBuilder> warningListArray=new ArrayList<StringBuilder>();
		//				int countWarnings=0;
		//				while(countWarnings<warningListActual.size()){
		//					StringBuilder item=new StringBuilder("<html>"+(countWarnings+1)+".&nbsp;"+"<i><b>"+warningListActual.get(countWarnings)+"</b></i>"+"</html>");
		//					warningListArray.add(item);
		//					countWarnings++;
		//				}

		JCheckBox chekcBoxAuthorsOf = new JCheckBox("Filter By Authors");
		chekcBoxAuthorsOf.setFont(new Font("Times New Roman", Font.BOLD, 12));
		chekcBoxAuthorsOf.setBounds(244, 638, 131, 23);
		frame.getContentPane().add(chekcBoxAuthorsOf);
		chekcBoxAuthorsOf.setActionCommand("AuthorFilter");
		chekcBoxAuthorsOf.setVisible(false);
		
		
		
		
	
		
		
		
		JPanel gravityFunctionalityPanel = new JPanel(
				
						new GridBagLayout()){
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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		
					}
					@Override
					public Dimension getPreferredSize(){
						return new Dimension(400,300);
					}
				};

		gravityFunctionalityPanel.setBackground(new Color(255, 248, 220));
		gravityFunctionalityPanel.setBounds(0, 81, 984, 500);
		frame.getContentPane().add(gravityFunctionalityPanel);
		gravityFunctionalityPanel.setLayout(null);
		//		gravityFunctionalityPanel.setLayout(null);
		//				BufferedImage img = ImageIO.read(url);
		//				Image dimg = img.getScaledInstance(1200, 1200, Image.SCALE_SMOOTH);
		//				ImageIcon imageIcon=new ImageIcon(dimg);
		//				frame.setContentPane(new JLabel(imageIcon));
		//		frame.pack();

		//						frame.setContentPane(new JLabel(){
		//							protected void paintComponent(Graphics g){
		//								URL url=Branch.class.getResource("/edd.jpg");
		//									BufferedImage img;
		//									try {
		//										img = ImageIO.read(url);
		//										Image dimg = img.getScaledInstance(2000, 1000, Image.SCALE_SMOOTH);
		//										ImageIcon imageIcon=new ImageIcon(dimg);
		//										if(imageIcon!=null){
		//											ImageDrawer.drawScaledImage(imageIcon.getImage(),this,g);
		//										}
		//									} catch (IOException e) {
		//				
		//								e.printStackTrace();
		//									}
		//								}
		//							});


		//BufferedImage img = ImageIO.read(new File("C:\\HUE\\WorkSpace\\Personal\\Zeus\\images\\nature1.jpg"));
		//Image dimg = img.getScaledInstance(600, 600, Image.SCALE_SMOOTH);
		//ImageIcon imageIcon=new ImageIcon(dimg);
		//frame.setContentPane(new JLabel(imageIcon));

		JRadioButton rdbtnSearchForA = new JRadioButton("Keyword Search");
		rdbtnSearchForA.setBounds(184, 39, 143, 27);
		gravityFunctionalityPanel.add(rdbtnSearchForA);
		buttonGroup.add(rdbtnSearchForA);
		rdbtnSearchForA.setBackground(SystemColor.menu);
		rdbtnSearchForA.setFont(new Font("Times New Roman", Font.BOLD, 16));

		rdbtnSearchForA.setActionCommand("Keyword");

		JRadioButton rdbtnSearchAMethod = new JRadioButton("Method Finder-Java");
		rdbtnSearchAMethod.setBounds(183, 76, 168, 27);
		gravityFunctionalityPanel.add(rdbtnSearchAMethod);
		buttonGroup.add(rdbtnSearchAMethod);
		rdbtnSearchAMethod.setBackground(SystemColor.menu);
		rdbtnSearchAMethod.setBorderPainted(false);
		rdbtnSearchAMethod.setFont(new Font("Times New Roman", Font.BOLD, 15));
		rdbtnSearchAMethod.setActionCommand("MethodCall");
		rdbtnSearchAMethod.setToolTipText("If you want to search methods inbetween such as filter in stream then use keyword search as \"filter(\"");

		textField = new JTextField();
		textField.setBounds(595, 57, 199, 20);
		gravityFunctionalityPanel.add(textField);
		textField.setForeground(Color.BLACK);
		textField.setBackground(new Color(255, 255, 255));
		textField.setVisible(false);
		textField.setColumns(10);

		JLabel lblKeyword = new JLabel("Keyword");
		lblKeyword.setBounds(475, 57, 143, 18);
		gravityFunctionalityPanel.add(lblKeyword);
		lblKeyword.setForeground(new Color(204, 255, 255));
		lblKeyword.setBackground(Color.WHITE);
		lblKeyword.setFont(new Font("Times New Roman", Font.BOLD, 15));

		JRadioButton rdbtnNewRadioButton = new JRadioButton("Statements Where Session or Cache is used.");
		rdbtnNewRadioButton.setBounds(184, 115, 317, 27);
		gravityFunctionalityPanel.add(rdbtnNewRadioButton);
		buttonGroup.add(rdbtnNewRadioButton);
		rdbtnNewRadioButton.setBackground(SystemColor.menu);
		rdbtnNewRadioButton.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnNewRadioButton.setFont(new Font("Times New Roman", Font.BOLD, 15));
		rdbtnNewRadioButton.setActionCommand("Session");
		
		JRadioButton jsUnusedRadio = new JRadioButton("Find Unused Constants in JavaScript");
		jsUnusedRadio.setBounds(184, 193, 283, 23);
		gravityFunctionalityPanel.add(jsUnusedRadio);
		buttonGroup.add(jsUnusedRadio);
		jsUnusedRadio.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jsUnusedRadio.setBackground(SystemColor.menu);
		jsUnusedRadio.setActionCommand("UnusedConstantsJS");
		
		

		JRadioButton rdbtnGenerateFolderStructure = new JRadioButton("Generate Folder Structure");
		rdbtnGenerateFolderStructure.setBounds(184, 266, 199, 27);
		gravityFunctionalityPanel.add(rdbtnGenerateFolderStructure);
		buttonGroup.add(rdbtnGenerateFolderStructure);
		rdbtnGenerateFolderStructure.setFont(new Font("Times New Roman", Font.BOLD, 15));
		rdbtnGenerateFolderStructure.setActionCommand("folderstructure");

		JRadioButton rdbtnNewRadioButton_2 = new JRadioButton("Check Git Files Locally");
		rdbtnNewRadioButton_2.setBounds(184, 299, 179, 27);
		gravityFunctionalityPanel.add(rdbtnNewRadioButton_2);
		buttonGroup.add(rdbtnNewRadioButton_2);
		rdbtnNewRadioButton_2.setToolTipText("The Cloned Local Copy will be opened and you can browse through it and drag necessary Files to eclipse IDE and check it conveniently...");
		rdbtnNewRadioButton_2.setFont(new Font("Times New Roman", Font.BOLD, 15));
		rdbtnNewRadioButton_2.setActionCommand("localChecking");

		JLabel lblLookInTo = new JLabel("Look in to D:\\FolderStructure");
		lblLookInTo.setForeground(new Color(0, 250, 154));
		lblLookInTo.setBounds(389, 273, 143, 14);
		gravityFunctionalityPanel.add(lblLookInTo);
		lblLookInTo.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		//	JavaScript Unused
		//		JRadioButton rdbtnJavascriptUnusedConstants = new JRadioButton("JavaScript Unused Constants ");
		//		rdbtnJavascriptUnusedConstants.setBounds(183, 195, 221, 27);
		//		gravityFunctionalityPanel.add(rdbtnJavascriptUnusedConstants);
		//		buttonGroup.add(rdbtnJavascriptUnusedConstants);
		//		rdbtnJavascriptUnusedConstants.setFont(new Font("Times New Roman", Font.BOLD, 15));
		//		rdbtnJavascriptUnusedConstants.setActionCommand("JsUnusedConstants");

		JRadioButton rdbtnNewRadioButton_3 = new JRadioButton("Find Unnecessary Fileds, Constants(Java)");
		rdbtnNewRadioButton_3.setBounds(183, 153, 304, 27);
		gravityFunctionalityPanel.add(rdbtnNewRadioButton_3);
		buttonGroup.add(rdbtnNewRadioButton_3);
		rdbtnNewRadioButton_3.setFont(new Font("Times New Roman", Font.BOLD, 15));
		rdbtnNewRadioButton_3.setActionCommand("UnnecessaryFields");

		//		JRadioButton rdbtnNewRadioButton_4 = new JRadioButton("Hardcodes.(Words in file seperated by Space)");
		//		rdbtnNewRadioButton_4.setFont(new Font("Times New Roman", Font.BOLD, 15));
		//		rdbtnNewRadioButton_4.setBounds(125, 276, 338, 23);
		//		frame.getContentPane().add(rdbtnNewRadioButton_4);

		rdbtnNewRadioButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblKeyword.setVisible(false);
				btnSearch.setVisible(true);
				textField.setVisible(false);
				chckbxForAllFiles.setSelected(false);
				chekcBoxAuthorsOf.setSelected(false);
				chckbxForAllFiles.setVisible(true);
				chekcBoxAuthorsOf.setVisible(true);
				btnClickToInput.setVisible(true);
				lblMustMentionAny.setVisible(true);
				label.setVisible(true);
			}
		});

		JRadioButton rdbtnIndexJavaFiles = new JRadioButton("Index Java Files Refers to Another Index File Errors");
		buttonGroup.add(rdbtnIndexJavaFiles);
		rdbtnIndexJavaFiles.setFont(new Font("Times New Roman", Font.BOLD, 15));
		rdbtnIndexJavaFiles.setBounds(184, 230, 376, 23);
		rdbtnIndexJavaFiles.setActionCommand("IndexJavaFilesError");
		gravityFunctionalityPanel.add(rdbtnIndexJavaFiles);

		JPanel optimusFunctionalitiesPanel = new JPanel(
				new GridBagLayout()){
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			@Override
			public Dimension getPreferredSize(){
				return new Dimension(400,300);
			}
		};



		//				){
		//			@Override
		//			protected void paintComponent(Graphics g){
		//				super.paintComponent(g);
		//				URL url=GravityLogin.class.getResource("/edd.jpg");
		//				BufferedImage img;
		//				try {
		//					img = ImageIO.read(url);
		//					Image dimg = img.getScaledInstance(1000, 1000, Image.SCALE_SMOOTH);
		//					g.drawImage(dimg, 0, 0, getWidth(), getHeight(), this);
		//					Graphics2D g2d=(Graphics2D)g;
		//					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
		//					//g2d.setColor(Color.yellow);
		//					//g2d.fillRect(0, 0, 1200, 600);
		//				} catch (IOException e) {
		//					// TODO Auto-generated catch block
		//					e.printStackTrace();
		//				}
		//			}
		//			@Override
		//			public Dimension getPreferredSize(){
		//				return new Dimension(400,300);
		//			}
		//		};
		optimusFunctionalitiesPanel.setBackground(new Color(255, 248, 220));
		optimusFunctionalitiesPanel.setVisible(false);
		optimusFunctionalitiesPanel.setBounds(0, 81, 984, 500);
		frame.getContentPane().add(optimusFunctionalitiesPanel);
		optimusFunctionalitiesPanel.setLayout(null);






		optimusFunctionalitiesPanel.setLayout(null);

		JLabel bugCategoriesJavaLabel = new JLabel("Java Bug Categories");
		bugCategoriesJavaLabel.setForeground(new Color(64, 224, 208));
		bugCategoriesJavaLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		bugCategoriesJavaLabel.setBounds(25, 175, 147, 28);
		optimusFunctionalitiesPanel.add(bugCategoriesJavaLabel);

		JScrollPane javaScrollPane = new JScrollPane();
		javaScrollPane.setBounds(25, 214, 286, 245);
		//errorListScrollPane.setViewportView(errorLists);
		optimusFunctionalitiesPanel.add(javaScrollPane);

		JList javaJList = new JList(bugCategoriesJavaArray.toArray());
		javaScrollPane.setViewportView(javaJList);
		javaJList.setFont(new Font("Times New Roman", Font.PLAIN, 12));

		//		JRadioButton rdbtnOptimusOut = new JRadioButton("Optimus Out");
		//		buttonGroup.add(rdbtnOptimusOut);
		//		rdbtnOptimusOut.setBounds(536, 511, 97, 23);
		//		optimusFunctionalitiesPanel.add(rdbtnOptimusOut);
		//		rdbtnOptimusOut.setActionCommand("optimusOut");

		JRadioButton rdbtnCategorisedOptimus = new JRadioButton("Categorised Optimus Ouput");


		buttonGroup.add(rdbtnCategorisedOptimus);
		rdbtnCategorisedOptimus.setBounds(25, 44, 179, 23);
		optimusFunctionalitiesPanel.add(rdbtnCategorisedOptimus);
		rdbtnCategorisedOptimus.setActionCommand("CategorisedOptimus");

		JScrollPane javaScriptScrollPane = new JScrollPane();
		javaScriptScrollPane.setBounds(591, 214, 215, 135);
		optimusFunctionalitiesPanel.add(javaScriptScrollPane);

		//		JRadioButton rdbtnFinalOut = new JRadioButton("Final Out");
		//		buttonGroup.add(rdbtnFinalOut);
		//		rdbtnFinalOut.setBounds(200, 511, 82, 23);
		//		optimusFunctionalitiesPanel.add(rdbtnFinalOut);
		//		rdbtnFinalOut.setActionCommand("finalOut");


		JList javaScriptJList = new JList(bugCategoriesJavaScriptArray.toArray());
		javaScriptScrollPane.setViewportView(javaScriptJList);
		javaScriptJList.setFont(new Font("Times New Roman", Font.PLAIN, 12));

		JScrollPane XMLScrollPane = new JScrollPane();
		XMLScrollPane.setBounds(347, 214, 208, 120);
		optimusFunctionalitiesPanel.add(XMLScrollPane);

		JList XMLJList = new JList(bugCategoriesXMLArray.toArray());
		XMLScrollPane.setViewportView(XMLJList);
		XMLJList.setFont(new Font("Times New Roman", Font.PLAIN, 12));

		JLabel lblJavascriptBugcategories = new JLabel("JavaScript BugCategories");
		lblJavascriptBugcategories.setForeground(new Color(64, 224, 208));
		lblJavascriptBugcategories.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblJavascriptBugcategories.setBounds(591, 173, 179, 33);
		optimusFunctionalitiesPanel.add(lblJavascriptBugcategories);

		JLabel lblXmlBugcategories = new JLabel("XML BugCategories");
		lblXmlBugcategories.setForeground(new Color(64, 224, 208));
		lblXmlBugcategories.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblXmlBugcategories.setBounds(347, 178, 147, 23);
		optimusFunctionalitiesPanel.add(lblXmlBugcategories);

		JButton btnClear = new JButton("Clear Selection");
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				javaJList.clearSelection();
				javaScriptJList.clearSelection();
				XMLJList.clearSelection();
			}
		});
		btnClear.setBounds(347, 436, 121, 23);
		optimusFunctionalitiesPanel.add(btnClear);

		JLabel lblFunctionality = new JLabel("Functionality");
		lblFunctionality.setForeground(new Color(64, 224, 208));
		lblFunctionality.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblFunctionality.setBounds(25, 11, 110, 29);
		optimusFunctionalitiesPanel.add(lblFunctionality);

		JRadioButton rdbtnGenerateDefectSheet = new JRadioButton("Generate Defect Sheet");
		buttonGroup.add(rdbtnGenerateDefectSheet);
		rdbtnGenerateDefectSheet.setBounds(25, 75, 147, 23);
		optimusFunctionalitiesPanel.add(rdbtnGenerateDefectSheet);
		rdbtnGenerateDefectSheet.setActionCommand("DefectSheet");

		JLabel lblIfNoCategory = new JLabel("If No Category is Selected all Categories will be considered.(AND ALSO THIS ASSUMES FOLDER STRUCTURE IS PROPER)");
		lblIfNoCategory.setForeground(new Color(0, 250, 154));
		lblIfNoCategory.setBounds(210, 48, 700, 14);
		optimusFunctionalitiesPanel.add(lblIfNoCategory);

		JPanel authorsPanel = new JPanel(
				new GridBagLayout()){
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			@Override
			public Dimension getPreferredSize(){
				return new Dimension(400,300);
			}
		};
		authorsPanel.setBackground(new Color(255, 248, 220));
		authorsPanel.setVisible(false);
		authorsPanel.setBounds(0, 81, 984, 500);
		frame.getContentPane().add(authorsPanel);
		authorsPanel.setLayout(null);

		JList authorsList = new JList();
		authorsList.setFont(new Font("Times New Roman", Font.BOLD, 12));
		authorsList.setBounds(107, 81, 223, 360);
	//	authorsPanel.add(authorsList);

		JScrollPane authorsListScrollPane = new JScrollPane();
		authorsListScrollPane.setBounds(107, 81, 223, 360);
	//	authorsPanel.add(authorsListScrollPane);

		authorsListScrollPane.setViewportView(authorsList);

		JLabel authorsListLabel = new JLabel("Authors List");
		authorsListLabel.setForeground(new Color(64, 224, 208));
		authorsListLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
		authorsListLabel.setBounds(177, 39, 93, 31);
		authorsPanel.add(authorsListLabel);

		JLabel lblCobaltGeneratedFiles = new JLabel("Cobalt Generated Files");
		lblCobaltGeneratedFiles.setForeground(new Color(64, 224, 208));
		lblCobaltGeneratedFiles.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblCobaltGeneratedFiles.setBounds(422, 81, 163, 53);
		authorsPanel.add(lblCobaltGeneratedFiles);

		JList authorLessFilesList = new JList();
		authorLessFilesList.setFont(new Font("Times New Roman", Font.BOLD, 12));
		authorLessFilesList.setBounds(658, 81, 273, 408);
		authorsPanel.add(authorLessFilesList);

		JScrollPane authorLessListScrollPane = new JScrollPane();
		authorLessListScrollPane.setBounds(658, 81, 273, 408);
		authorsPanel.add(authorLessListScrollPane);
		//authorLessListScrollPane.setViewportView(authorLessFilesList);

		JLabel lblAuthorlessFiles = new JLabel("AuthorLess Files");
		lblAuthorlessFiles.setForeground(new Color(64, 224, 208));
		lblAuthorlessFiles.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblAuthorlessFiles.setBounds(736, 37, 120, 34);
		//authorsPanel.add(lblAuthorlessFiles);

		JLabel countOfCobaltGeneratedFiles = new JLabel("0");
		countOfCobaltGeneratedFiles.setForeground(new Color(64, 224, 208));
		countOfCobaltGeneratedFiles.setHorizontalAlignment(SwingConstants.CENTER);
		countOfCobaltGeneratedFiles.setFont(new Font("Times New Roman", Font.BOLD, 40));
		countOfCobaltGeneratedFiles.setBounds(459, 127, 75, 70);
		authorsPanel.add(countOfCobaltGeneratedFiles);

		JButton btnGo = new JButton("Go");

		btnGo.setBounds(448, 466, 89, 23);
		authorsPanel.add(btnGo);

		JButton btnClearselection = new JButton("ClearSelection");
		btnClearselection.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				authorsList.clearSelection();
			}
		});
		btnClearselection.setBounds(10, 466, 120, 23);
		authorsPanel.add(btnClearselection);
		
//		JLabel lblFilterSection = new JLabel("Filter Section:");
//		lblFilterSection.setVerticalAlignment(SwingConstants.BOTTOM);
//		lblFilterSection.setFont(new Font("Times New Roman", Font.PLAIN, 30));
//		lblFilterSection.setBounds(20, 604, 205, 50);
//		lblFilterSection.setForeground(new Color(64, 224, 208));
//		frame.getContentPane().add(lblFilterSection);
		
		javaJList.setVisible(false);
		javaScrollPane.setVisible(false);

		XMLScrollPane.setVisible(false);
		javaScriptJList.setVisible(false);
		XMLJList.setVisible(false);
		javaScriptScrollPane.setVisible(false);


		bugCategoriesJavaLabel.setVisible(false);
		lblJavascriptBugcategories.setVisible(false);
		lblXmlBugcategories.setVisible(false);

		btnClear.setVisible(false);
		rdbtnCategorisedOptimus.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					javaJList.setVisible(true);
					javaScrollPane.setVisible(true);

					XMLScrollPane.setVisible(true);
					javaScriptJList.setVisible(true);
					XMLJList.setVisible(true);
					javaScriptScrollPane.setVisible(true);


					bugCategoriesJavaLabel.setVisible(true);
					lblJavascriptBugcategories.setVisible(true);
					lblXmlBugcategories.setVisible(true);

					btnClear.setVisible(true);
				}
				if(e.getStateChange()==ItemEvent.DESELECTED){
					javaJList.setVisible(false);
					javaScrollPane.setVisible(false);

					XMLScrollPane.setVisible(false);
					javaScriptJList.setVisible(false);
					XMLJList.setVisible(false);
					javaScriptScrollPane.setVisible(false);


					bugCategoriesJavaLabel.setVisible(false);
					lblJavascriptBugcategories.setVisible(false);
					lblXmlBugcategories.setVisible(false);

					btnClear.setVisible(false);
				}
			}
		});

		//		rdbtnOptimusOut.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//
		//				btnSearch.setVisible(true);
		//				chckbxForAllFiles.setVisible(true);
		//				btnClickToInput.setVisible(true);
		//				lblMustMentionAny.setVisible(true);
		//				label.setVisible(true);
		//			}
		//		});

		rdbtnCategorisedOptimus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chckbxForAllFiles.setSelected(false);
				btnSearch.setVisible(true);
				chckbxForAllFiles.setVisible(true);
				btnClickToInput.setVisible(true);
				chekcBoxAuthorsOf.setVisible(true);
				chekcBoxAuthorsOf.setSelected(false);
				lblMustMentionAny.setVisible(true);
				label.setVisible(true);


			}
		});

		rdbtnGenerateDefectSheet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chckbxForAllFiles.setSelected(false);
				btnSearch.setVisible(true);
				chckbxForAllFiles.setVisible(true);
				chekcBoxAuthorsOf.setVisible(true);
				chekcBoxAuthorsOf.setSelected(false);
				btnClickToInput.setVisible(true);
				lblMustMentionAny.setVisible(true);
				label.setVisible(true);

			}
		});


		rdbtnNewRadioButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblKeyword.setVisible(false);
				chckbxForAllFiles.setSelected(false);
				btnSearch.setVisible(true);
				textField.setVisible(false);
				btnClickToInput.setVisible(false);
				chekcBoxAuthorsOf.setVisible(false);
				chckbxForAllFiles.setVisible(false);
				btnClickToInput.setVisible(false);
				chekcBoxAuthorsOf.setSelected(false);
				lblMustMentionAny.setVisible(false);
				label.setVisible(false);
			}
		});



		rdbtnGenerateFolderStructure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblKeyword.setVisible(false);
				btnSearch.setVisible(true);
				chckbxForAllFiles.setSelected(false);
				textField.setVisible(false);
				chekcBoxAuthorsOf.setVisible(false);
				chekcBoxAuthorsOf.setSelected(false);
				chckbxForAllFiles.setVisible(false);
				btnClickToInput.setVisible(false);
				lblMustMentionAny.setVisible(false);
				label.setVisible(false);
			}
		});
		jsUnusedRadio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblKeyword.setVisible(false);
				btnSearch.setVisible(true);
				textField.setVisible(false);
				chckbxForAllFiles.setSelected(false);
				chekcBoxAuthorsOf.setVisible(true);
				chekcBoxAuthorsOf.setSelected(false);
				chckbxForAllFiles.setVisible(true);
				btnClickToInput.setVisible(true);
				lblMustMentionAny.setVisible(true);
				label.setVisible(true);
			}
		});
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblKeyword.setVisible(false);
				btnSearch.setVisible(true);
				textField.setVisible(false);
				chekcBoxAuthorsOf.setVisible(true);
				chekcBoxAuthorsOf.setSelected(false);
				chckbxForAllFiles.setVisible(true);
				btnClickToInput.setVisible(true);
				lblMustMentionAny.setVisible(true);
				label.setVisible(true);
			}
		});
		lblKeyword.setVisible(false);


		rdbtnSearchAMethod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField.setText("");
				textField.setToolTipText("Enter the Method Name");
				lblKeyword.setVisible(true);
				btnSearch.setVisible(true);
				chckbxForAllFiles.setSelected(false);
				textField.setVisible(true);
				chekcBoxAuthorsOf.setSelected(false);
				chckbxForAllFiles.setVisible(true);
				chekcBoxAuthorsOf.setVisible(true);
				btnClickToInput.setVisible(true);
				lblMustMentionAny.setVisible(true);
				label.setVisible(true);
				lblKeyword.setText("Method Name");
			}
		});
		rdbtnIndexJavaFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSearch.setVisible(true);
				chckbxForAllFiles.setSelected(false);
				chekcBoxAuthorsOf.setSelected(false);
				chckbxForAllFiles.setVisible(false);
				chekcBoxAuthorsOf.setVisible(false);
				btnClickToInput.setVisible(false);
				lblMustMentionAny.setVisible(false);
				label.setVisible(false);
				
			}
		});
		rdbtnSearchForA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField.setText("");
				textField.setToolTipText("Enter the Keyword");
				lblKeyword.setVisible(true);
				btnSearch.setVisible(true);
				textField.setVisible(true);
				chekcBoxAuthorsOf.setSelected(false);
				chckbxForAllFiles.setSelected(false);
				chekcBoxAuthorsOf.setVisible(true);
				chckbxForAllFiles.setVisible(true);
				btnClickToInput.setVisible(true);
				lblMustMentionAny.setVisible(true);
				label.setVisible(true);
				lblKeyword.setText("Keyword");
			}
		});

		AtomicInteger filterByAuthors=new AtomicInteger();
		chekcBoxAuthorsOf.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					chckbxForAllFiles.setVisible(false);
					btnClickToInput.setVisible(false);
					GiveInputToAuthors take=new GiveInputToAuthors();
					if(authorDetails==null){
						authorDetails = take.giveInputToAuthor(groupedFiles);
					}
					authorsList.setListData(authorDetails.get("authors").toArray());
					authorLessFilesList.setListData(authorDetails.get("authorLess").toArray());
					countOfCobaltGeneratedFiles.setText(Integer.toString(authorDetails.get("cobalt").size()));
					authorsPanel.setVisible(true);
					optimusFunctionalitiesPanel.setVisible(false);
					gravityFunctionalityPanel.setVisible(false);
					filterByAuthors.set(1);
				}
				else if(e.getStateChange()==ItemEvent.DESELECTED){
					chckbxForAllFiles.setVisible(true);
					btnClickToInput.setVisible(true);
					authorsPanel.setVisible(false);
					optimusFunctionalitiesPanel.setVisible(false);
					gravityFunctionalityPanel.setVisible(true);
					tglbtnKk.setSelected(false);
					tglbtnKk.setText("Optimus");
					filterByAuthors.set(0);
				}
			}
		});
		AtomicInteger gravityPanel=new AtomicInteger(1);
		lblMustMentionAny.setVisible(false);
		btnSearch.setVisible(false);
		tglbtnKk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				int state=e.getStateChange();
				if(state==ItemEvent.SELECTED){
					tglbtnKk.setText("Gravity");
					optimusFunctionalitiesPanel.setVisible(true);
					gravityFunctionalityPanel.setVisible(false);
					btnSearch.setVisible(false);
					chckbxForAllFiles.setVisible(false);
					chckbxForAllFiles.setSelected(false);
					chekcBoxAuthorsOf.setSelected(false);
					chekcBoxAuthorsOf.setVisible(false);
					btnClickToInput.setVisible(false);
					lblMustMentionAny.setVisible(false);
					label.setVisible(false);
					gravityPanel.set(0);
				}
				else if(state==ItemEvent.DESELECTED){
					tglbtnKk.setText("Optimus");
					optimusFunctionalitiesPanel.setVisible(false);
					chckbxForAllFiles.setSelected(false);
					gravityFunctionalityPanel.setVisible(true);
					btnSearch.setVisible(false);
					chckbxForAllFiles.setVisible(false);
					chekcBoxAuthorsOf.setVisible(false);
					chekcBoxAuthorsOf.setSelected(false);
					btnClickToInput.setVisible(false);
					lblMustMentionAny.setVisible(false);
					label.setVisible(false);
					gravityPanel.set(1);
				}
			}
		});

		
		btnGo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(gravityPanel.get()==1){
					gravityFunctionalityPanel.setVisible(true);
				}
				else if(gravityPanel.get()==0){
					optimusFunctionalitiesPanel.setVisible(true);
				}
				authorsPanel.setVisible(false);
//				gravityFunctionalityPanel.setVisible(true);
//				tglbtnKk.setText("Optimus");
//				tglbtnKk.setSelected(false);
			}
		});
		
		btnSearch.addMouseListener(new MouseAdapter() {
			Map<String, Map<MultiKey, List<String>>> finalBrowseThroughFiles;
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				//If the user selects Category of Bugs
				List<String> totalSelectedCategories=new ArrayList<>();
				List<String> javaCategoriesSelected = javaJList.getSelectedValuesList();
				List<String> jsCategoriesSelected = javaScriptJList.getSelectedValuesList();
				List<String> xmlCategoriesSelected = XMLJList.getSelectedValuesList();

				if(CollectionUtils.isNotEmpty(javaCategoriesSelected)){
					totalSelectedCategories.addAll(javaCategoriesSelected);
				}
				if(CollectionUtils.isNotEmpty(jsCategoriesSelected)){
					totalSelectedCategories.addAll(jsCategoriesSelected);
				}
				if(CollectionUtils.isNotEmpty(xmlCategoriesSelected)){
					totalSelectedCategories.addAll(xmlCategoriesSelected);
				}


				if(filterByAuthors.get()==1&&!authorsList.getSelectedValuesList().isEmpty()){
					List<String> authorsListSelected = authorsList.getSelectedValuesList();
					MakeFilesOfAnAuthor make=new MakeFilesOfAnAuthor();
					System.out.println("authorsListSelected "+authorsListSelected);

					finalBrowseThroughFiles=make.getFilesOfSelectedAuthor(authorsListSelected, groupedFiles);
					System.out.println("his files "+finalBrowseThroughFiles);
				}

				//				if(btnItsConstants.getActionCommand().equals("userputConstantFiles")){
				//					FileReader fr;
				//					String line;
				//					try {
				//						fr = new FileReader("D:\\Temp\\jsconstants.txt");
				//						BufferedReader read=new BufferedReader(fr);
				//						constantList=new ArrayList<>();
				//						line = read.readLine();
				//						line = read.readLine();
				//						while(line!=null){
				//							if(!line.isEmpty()){
				//								String eachConstant = StringUtils.EMPTY;
				//								if(line.contains(":")){
				//									eachConstant =(line.trim().substring(0,line.indexOf(":"))).trim();
				//								}
				//								// Because of lengthy values of constants
				////								else{
				////									eachConstant=line.trim();
				////								}
				////								
				//								System.out.println("--"+eachConstant+"--");
				//								if(!eachConstant.isEmpty()&&!(eachConstant.trim().length()==0)){
				//									constantList.add(eachConstant);
				//								}
				//								line=read.readLine();
				//							}else{
				//								line=read.readLine();
				//							}
				//						}
				//						if(fr!=null||read!=null){
				//							read.close();
				//							fr.close();
				//						}
				//					}
				//					catch (IOException e2) {
				//					
				//					}
				//					File theNotepadFile=new File("D:\\Temp\\jsconstants.txt");
				//					theNotepadFile.deleteOnExit();
				//					btnItsConstants.setActionCommand("");
				//
				//				}

				if(btnClickToInput.getActionCommand().equals("userClickedToInputFiles")){
					FileReader fr;
					String line;
					try {
						fr = new FileReader("D:\\Temp\\listoffiles.txt");
						BufferedReader read=new BufferedReader(fr);
						filesPutInToList=new ArrayList<>();
						line = read.readLine();
						line = read.readLine();
						while(line!=null){
							if(!line.isEmpty()){
								String[] filesList = line.split(",");
								int innercount=0;
								while(innercount<filesList.length){
									if(!filesList[innercount].isEmpty()&&!(filesList[innercount].trim().length()==0)){
										filesPutInToList.add(filesList[innercount].replace(" ", ""));
										innercount++;
									}
								}
							}
							line=read.readLine();
						}
						if(fr!=null||read!=null){
							read.close();
							fr.close();
						}
					} catch (IOException e2) {
					}

					//Filtering files based on files given in notepad
					Map<String, Map<MultiKey, List<String>>> filteredGroupedFiles=new HashMap<>();
					//System.out.println(repoBranchesAndgroupedFiles);
					groupedFiles.entrySet().stream().forEach(repo -> {
						Map<MultiKey,List<String>> branchAndListofFiles=new LinkedHashMap<>();
						repo.getValue().entrySet().stream().forEach(branch ->{
							List<String> filesList=new ArrayList<>();
							int countOfFiles=0;
							while(countOfFiles<branch.getValue().size()){
								String filePath=branch.getValue().get(countOfFiles);
								if(!filesPutInToList.isEmpty()){
									if(isThisTheFilePath(filePath)){
										filesList.add(filePath);
									}
								}else{
									filesList.add(filePath);
								}
								countOfFiles++;
							}
							branchAndListofFiles.put(branch.getKey(),filesList);
						});
						filteredGroupedFiles.put(repo.getKey(), branchAndListofFiles);
					});
					//System.out.println(filteredGroupedFiles);
					File theNotepadFile=new File("D:\\Temp\\listoffiles.txt");
					theNotepadFile.deleteOnExit();
					finalBrowseThroughFiles=filteredGroupedFiles;
					btnClickToInput.setActionCommand("");
				}
				else if(setChecked.get()==1){
					finalBrowseThroughFiles=groupedFiles;
					setChecked.set(0);
				}


				Map<String, Map<MultiKey, List<String>>> listOfFindings = new HashMap<>();
				switch(buttonGroup.getSelection().getActionCommand()){

				case "Keyword":
					
					
					//listOfFindings=	new GlobalVarList().findUnnecessaryFields(finalBrowseThroughFiles);
					
					
					
					FindKeywordInAFile findKeywordInAFile=new FindKeywordInAFile();
					String keyword;
					keyword=textField.getText();
					//Below is a filter
					Map<String,Map<MultiKey,List<String>>> repoMap=new HashMap<>();
					finalBrowseThroughFiles.entrySet().stream().forEach(repo ->{
						Map<MultiKey,List<String>> branchMap=new HashMap<>();
						repo.getValue().entrySet().stream().forEach(branch ->{
							branchMap.put(branch.getKey(), branch.getValue().stream().filter(file -> {
								if(((file.endsWith(".xml")&&!file.endsWith(".en.xml")&&!file.endsWith("ja.xml"))
										)&&file.contains("-front")){
									return true;
								}else{
									return false;
								}
							}).collect(Collectors.toList()));
						});
						repoMap.put(repo.getKey(), branchMap);
					});
					
					listOfFindings=findKeywordInAFile.findTheKeyword(keyword, repoMap);

					try {
						gitCall.makeListOfUrlsAndCreateExcel(listOfFindings);
						frame.setCursor(null);
					} catch (IOException e2) {
					}
					break;
				case "MethodCall":MethodFinder findAMethodCall=new MethodFinder();
				String methodName;
				methodName=textField.getText();
				try {
					
//					Map<String,Map<MultiKey,List<String>>> repoMdap=new HashMap<>();
//					finalBrowseThroughFiles.entrySet().stream().forEach(repo ->{
//						Map<MultiKey,List<String>> branchMap=new HashMap<>();
//						repo.getValue().entrySet().stream().forEach(branch ->{
//							branchMap.put(branch.getKey(), branch.getValue().stream().filter(file -> {
//								if(((file.endsWith(".js"))
//										)&&file.contains("-front")){
//									return true;
//								}else{
//									return false;
//								}
//							}).collect(Collectors.toList()));
//						});
//						repoMdap.put(repo.getKey(), branchMap);
//					});
//					
//					
					
					
					listOfFindings=findAMethodCall.findTheMethod(methodName, finalBrowseThroughFiles);
					gitCall.makeListOfUrlsAndCreateExcel(listOfFindings);
					frame.setCursor(null);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
				case "IndexJavaFilesError":
					finalBrowseThroughFiles=groupedFiles;
					IndexError indexFiles=new IndexError();
					try {
						listOfFindings=indexFiles.getJoinErrorFiles(finalBrowseThroughFiles);
						gitCall.makeListOfUrlsAndCreateExcel(listOfFindings);
						frame.setCursor(null);
					} catch (IOException e1) {
					}
					break;
				case "localChecking":
					frame.setCursor(null);
					List<String> localBranchPathsList = gitCall.getLocalBranchPathsList();
					for(String localBranchPath:localBranchPathsList){
						File localFolder=new File(localBranchPath);
						try {
							Desktop.getDesktop().open(localFolder);
						} catch (IOException e1) {
						}
					}
					break;
				case "Session":
					//FieldVisitor session=new FieldVisitor();
					listOfFindings=FieldVisitor.sessionFind(finalBrowseThroughFiles);
					try {
						gitCall.makeListOfUrlsAndCreateExcel(listOfFindings);
						frame.setCursor(null);
					} catch (IOException e2) {
					}
					break;
				case "Authors":
					//FieldVisitor session=new FieldVisitor();
					listOfFindings=FieldVisitor.sessionFind(finalBrowseThroughFiles);
					GiveInputToAuthors.giveInputToAuthor(finalBrowseThroughFiles);
					frame.setCursor(null);
					break;
				case "DefectSheet":
					DefectSheet defect=new DefectSheet();
					defect.printDefectSheet(finalBrowseThroughFiles,gitCall);
					frame.setCursor(null);
					break;
					
//				case "UnusedConstants":
//					finalBrowseThroughFiles=groupedFiles;
//					//FieldVisitor session=new FieldVisitor();
//					listOfFindings=ConstantFieldVisitor.unusedFind(finalBrowseThroughFiles);
//					try {
//						gitCall.makeListOfUrlsAndCreateExcel(listOfFindings);
//						frame.setCursor(null);
//					} catch (IOException e2) {
//					}
//					break;
				case "UnnecessaryFields":
					//FieldVisitor session=new FieldVisitor();
					listOfFindings=new UnnecessaryClassField().findUnnecessaryFields(finalBrowseThroughFiles);
					try {
						gitCall.makeListOfUrlsAndCreateExcel(listOfFindings);
						frame.setCursor(null);
					} catch (IOException e2) {
					}
					break;

//				case "optimusOut":
//					//FieldVisitor session=new FieldVisitor();
//					OptimusMain optimusExecute=new OptimusMain();
//					Map<String, Map<MultiKey, ErrorsAndWarnings>> outputForOptimusExcel = null;
//					try {
//						outputForOptimusExcel = optimusExecute.makeInputCompatibleToOptimus(finalBrowseThroughFiles,gitCall.gitConnection);
//						Categorizer obj=new Categorizer();
//						Map<String, Map<String, ErrorsCollection>> outForExcel = obj.makeIntoCategories(outputForOptimusExcel);
//
//						OptimusOutput.getOptimusOutput(outForExcel);
//						frame.setCursor(null);
//					} catch (IOException e4) {
//					}
//										try {
//										//	OptimusOutput.getOptimusOutput(outputForOptimusExcel);
//										} catch (IOException e3) {
//											// TODO Auto-generated catch block
//											e3.printStackTrace();
//										}
//					break;	
//				case "GlobalVariableValidity":
//					//FieldVisitor session=new FieldVisitor();
//					listOfFindings=GlobalVariablesValidity.globalValidityFind(finalBrowseThroughFiles);
//					try {
//						gitCall.makeListOfUrlsAndCreateExcel(listOfFindings);
//						frame.setCursor(null);
//					} catch (IOException e2) {
//					}
//					break;
//					
				case "CategorisedOptimus":
					
					  // Repository, Branch, ClassificationType, ClassifiedManipulatedNames
                    Map<String, Map<String, Map<String, Set<String>>>> classifications = new HashMap<>();
                    finalBrowseThroughFiles.entrySet().stream().forEach(repository -> {
                        Map<String, Map<String, Set<String>>> branchLevel = new HashMap<>();
                        repository.getValue().entrySet().forEach(branch -> {
                        	ClassifyUsingController branchClassify = new ClassifyUsingController();
                            branchLevel.put(branch.getKey().getKey(0).toString(),
                                    branchClassify.classifyUsingController(branch.getValue()));
                        });
                        classifications.put(repository.getKey(), branchLevel);
                    });
					
					Map<String, Map<MultiKey, List<FileUnit>>> fileUnitsList = new GroupingBy().desiredGroup(finalBrowseThroughFiles,gitCall,classifications);
					new CompatibleOutput().getOptimusOutput(new GroupingByy().entryPoint(fileUnitsList),Arrays.asList());

					break;
//				case "CategorisedOptimus":
//					List<String> notContainingHyphen=new ArrayList<>();
//					List<String> xmlXml=new ArrayList<>();
//					List<String> notLowerCase=new ArrayList<>();
//					List<String> correctFiles=new ArrayList<>();
//					
//					System.out.println("************************************Started***************************************");
//					finalBrowseThroughFiles.entrySet().stream().forEach(repo ->{
//						repo.getValue().entrySet().stream().forEach(branch ->{
//							List<String> files = branch.getValue();
//							//&&file.toLowerCase().contains("positionmanagement")
//							files.stream().forEach(file ->{
//								String fileName = file.substring(file.lastIndexOf("\\")+1);
//								if(!(fileName.endsWith("en.xml")||fileName.endsWith("ja.xml"))&&fileName.endsWith(".xml")){
//									if(!fileName.contains("-")){
//										notContainingHyphen.add(fileName);
//									}else if(fileName.toLowerCase().endsWith("xml.xml")){
//										xmlXml.add(fileName);
//									}else if (!fileName.equals(fileName.toLowerCase())){
//										
//										notLowerCase.add(fileName);
//									}else{
//										correctFiles.add(fileName);
//									}
//								}
//							});
//						});
//					});
//					System.out.println("-------------------Not Containing Hypher-----------------");
//					System.out.println(notContainingHyphen);
//					System.out.println("-------------------xml.xml-----------------");
//					System.out.println(xmlXml);
//					System.out.println("-------------------Not Lower Case-----------------");
//					System.out.println(notLowerCase);
//					System.out.println("-------------------Correct-----------------");
//					System.out.println(correctFiles);
//					System.out.println("************************************Ended***************************************");
//					frame.setCursor(null);
//					break;
				case "finalOut":
					  OptimusMain optimusExecutee = new OptimusMain();
		                Map<String, Map<MultiKey, ErrorsAndWarnings>> outputForOptimusExcell = null;
		                try {
		                    outputForOptimusExcell = optimusExecutee.makeInputCompatibleToOptimus(
		                            finalBrowseThroughFiles,
		                            gitCall.getGitConnection());

		                    // Repository, Branch, ClassificationType, ClassifiedManipulatedNames
		                    Map<String, Map<String, Map<String, Set<String>>>> classification = new HashMap<>();
		                    finalBrowseThroughFiles.entrySet().stream().forEach(repository -> {
		                        Map<String, Map<String, Set<String>>> branchLevel = new HashMap<>();
		                        repository.getValue().entrySet().forEach(branch -> {
		                        	ClassifyUsingController branchClassify = new ClassifyUsingController();
		                            branchLevel.put(branch.getKey().getKey(0).toString(),
		                                    branchClassify.classifyUsingController(branch.getValue()));
		                        });
		                        classification.put(repository.getKey(), branchLevel);
		                    });

		                    Categorizer obj = new Categorizer();
		                    Map<String, Map<String, ErrorsCollection>> outForExcel = obj
		                            .makeIntoCategories(outputForOptimusExcell);
		                    MoreCategoriser ob = new MoreCategoriser();
		                    Map<String, Map<String, ErrorsCollectionDetailed>> outted = ob
		                            .makeASubsystemEpicCategorisedOutput(outForExcel, classification);
		                    SubSystemEpicGrouping grou = new SubSystemEpicGrouping();

		                    Map<String, Map<String, ActualErrorCollectionDetailed>> actualOut =
		                            grou.grouper(outted, totalSelectedCategories);
		                    OptimusOutputMoreCategoriezed.getOptimusOutput(actualOut, gitCall);

		                    // Chart Creation
		                    MakeInputToChart chart = new MakeInputToChart();
		                    chart.giveInputToChart(actualOut, gitCall);

		                    // Create Pie Chart
		                    frame.setCursor(null);
		                } catch (IOException e4) {
		                }
					break;

				case "UnusedConstantsJS":
					//finalBrowseThroughFiles=groupedFiles;
					//					String jsFileName;
					//					//jsFileName=textField_1.getText();
					//					String jsFilePath = null;
					//					int flag=1;
					//					for(Map.Entry<String, Map<MultiKey, List<String>>> repoMap:finalBrowseThroughFiles.entrySet()){
					//						for(Map.Entry<MultiKey, List<String>> branchMap:repoMap.getValue().entrySet()){
					//							for(String filePath:branchMap.getValue()){
					//								if(filePath.contains(jsFileName)){
					//									jsFilePath=filePath;
					//									flag=2;
					//									break;
					//								}
					//							}
					//							if(flag==2){
					//								break;
					//							}
					//						}
					//						if(flag==2){
					//							break;
					//						}
					//					}
					//	UnusedConstantsJS uJS=new UnusedConstantsJS();
					//					try {
					//						uJS.generateUnusedConstants(jsFilePath,constantList);
					//					} catch (IOException e2) {
					//						// TODO Auto-generated catch block
					//						e2.printStackTrace();
					//					}
					
					JsUnused find = new JsUnused();
					listOfFindings=find.findUnusedJS(finalBrowseThroughFiles);
					try {
						gitCall.makeListOfUrlsAndCreateExcel(listOfFindings);
						frame.setCursor(null);
					} catch (IOException e2) {
					}
					
					break;
				case "folderstructure":
					frame.setCursor(null);
					   File theDir = new File("D:\\FolderStructure" + File.separator);
		                if (!theDir.exists()) {
		                    theDir.mkdir();
		                }
		                List<String> localBranchPathsListF = gitCall.getLocalBranchPathsList();
		                Map<String, List<String>> selectedRepoAndItsSelectedBranches = gitCall
		                        .getSelectedRepoAndItsSelectedBranches();
		                AtomicInteger count = new AtomicInteger(0);
		                selectedRepoAndItsSelectedBranches
		                        .entrySet()
		                        .stream()
		                        .forEach(
		                                repo -> {
		                                    Path repoPath = Paths.get("D:\\FolderStructure"
		                                            + File.separator
		                                            + "GravityOutput-"
		                                            + "sankraja"
		                                            + File.separator
		                                            + "GravityOutput"
		                                            + File.separator
		                                            + "FolderStructure-Use Notepad to Open Files"
		                                            + File.separator
		                                            +
		                                            repo.getKey()
		                                            + File.separator);
		                                    try {
		                                        Files.createDirectories(repoPath);
		                                    } catch (Exception ee) {

		                                    }
		                                    // File theDirRepo = new File(progress.getGravityStoragaePath()
		                                    // + File.separator
		                                    // + "GravityOutput" + File.separator + "FolderStructure"
		                                    // + File.separator
		                                    // +
		                                    // repo.getKey()
		                                    // + File.separator);
		                                    // if (!theDirRepo.exists()) {
		                                    // theDirRepo.mkdir();
		                                    // }

		                                repo.getValue().forEach(
		                                        branch -> {

		                                            Path branchPath = Paths.get("D:\\FolderStructure"
		                                                    + File.separator
		                                                    + "GravityOutput-"
		                                                    + "sankraja"
		                                                    + File.separator
		                                                    + "GravityOutput"
		                                                    + File.separator
		                                                    + "FolderStructure-Use Notepad to Open Files"
		                                                    + File.separator
		                                                    + repo.getKey()
		                                                    + File.separator
		                                                    + branch.replace("refs/heads/", "")
		                                                    + File.separator);
		                                            try {
		                                                Files.createDirectories(branchPath);
		                                            } catch (Exception ee) {

		                                            }
		                                            // File theDirBranch = new File(progress.getGravityStoragaePath()
		                                            // + File.separator
		                                            // + "GravityOutput"
		                                            // + File.separator
		                                            // + "FolderStructure"
		                                            // + File.separator
		                                            // + repo.getKey()
		                                            // + File.separator
		                                            // + branch.replace("refs/heads/", "")
		                                            // + File.separator);
		                                            // if (!theDirBranch.exists()) {
		                                            // theDirBranch.mkdir();
		                                        FolderStructure check = new FolderStructure();
		                                        try {
		                                            check.checkStructure(
		                                                    localBranchPathsListF.get(count.get()),
		                                                    branchPath.toFile());
		                                            count.getAndAdd(1);
		                                        } catch (IOException e1) {
		                                        }
		                                    }
		                                        // }
		                                        );
		                            });
		                break;
				default:
				}

				//				final String commandOne="rd /s /q ";
				//				final String commandTwo="mkdir ";
				//				final Runtime r=Runtime.getRuntime();
				//				try {
				//					
				//					r.exec(commandOne);
				//					r.exec(commandTwo);
				//				} catch (IOException e1) {
				//					// TODO Auto-generated catch block
				//					e1.printStackTrace();
				//				}

				//				if(){
				//					gitCall.openAllLinksInDefaultBrowser(listOfFindings);
				//				}
			}
		});

		chckbxForAllFiles.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					btnClickToInput.setVisible(false);
					setChecked.set(1);
					chekcBoxAuthorsOf.setVisible(false);
				}
				else if(e.getStateChange()==ItemEvent.DESELECTED){
					btnClickToInput.setVisible(true);
					chekcBoxAuthorsOf.setVisible(true);
				}
			}
		});

		chckbxForAllFiles.setVisible(false);
		btnClickToInput.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					chckbxForAllFiles.setVisible(false);
					File tempDirInD=new File("D:\\Temp\\");
					if(!tempDirInD.exists()){
						tempDirInD.mkdir();
					}
					File listOfFiles=new File("D:\\Temp\\listoffiles.txt");
					if(!listOfFiles.createNewFile()){
						listOfFiles.delete();
					}
					listOfFiles.createNewFile();
					FileWriter fw=new FileWriter("D:\\Temp\\listoffiles.txt");
					BufferedWriter writ=new BufferedWriter(fw);
					writ.write("Enter File Names seperated by comma from the next line below(CaSE SeNsITivE)...");
					if(fw!=null||writ!=null){
						writ.close();
						fw.close();
					}
					Desktop.getDesktop().open(listOfFiles);
				} catch (IOException e1) {
				}
				btnClickToInput.setActionCommand("userClickedToInputFiles");
			}
		});
		btnClickToInput.setVisible(false);
	}

	private static boolean isThisTheFilePath(String filePath) {
		for(String fileName:filesPutInToList){
			if(filePath.endsWith(fileName)){
				return true;
			}
		}
		return false;
	}
}

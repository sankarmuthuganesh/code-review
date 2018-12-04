package RealTime.UI.New;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import org.apache.commons.lang3.StringUtils;

public class Functionalities {

  private JFrame frame;
  private JTextField methodNameField;
  private JTextField textField;
  private JLabel lblBugs;
  private JTable filterList;
  private TableRowSorter<TableModel> filterSorter;
  private JTextField filterFilter;
  private final ButtonGroup filterButtonGroup = new ButtonGroup();
  private final ButtonGroup functionalityButtonGroup = new ButtonGroup();
  private String filterName = StringUtils.EMPTY;
  private List<String> filterValues = new ArrayList<>();

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

  /**
   * Create the application.
   */
  public Functionalities() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frame = new JFrame();
    frame.getContentPane().setFont(new Font("Times New Roman", Font.PLAIN, 15));
    frame.setBounds(100, 100, 750, 600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(null);

    JLabel lblSearches = new JLabel("Searches");
    lblSearches.setFont(new Font("Times New Roman", Font.BOLD, 20));
    Font searchFont = lblSearches.getFont();
    Map searchFontAttributes = searchFont.getAttributes();
    searchFontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    lblSearches.setFont(searchFont.deriveFont(searchFontAttributes));

    lblSearches.setBounds(64, 89, 82, 30);
    frame.getContentPane().add(lblSearches);

    JRadioButton keywordSearchRadio = new JRadioButton("Keyword");
    functionalityButtonGroup.add(keywordSearchRadio);
    keywordSearchRadio.setFont(new Font("Times New Roman", Font.BOLD, 12));
    keywordSearchRadio.setBounds(64, 126, 82, 23);
    frame.getContentPane().add(keywordSearchRadio);

    JRadioButton rdbtnMethodSearch = new JRadioButton("Method Search");
    functionalityButtonGroup.add(rdbtnMethodSearch);
    rdbtnMethodSearch.setFont(new Font("Times New Roman", Font.BOLD, 12));
    rdbtnMethodSearch.setBounds(64, 156, 109, 23);
    frame.getContentPane().add(rdbtnMethodSearch);

    methodNameField = new JTextField();
    methodNameField.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    methodNameField.setBounds(179, 157, 188, 20);
    frame.getContentPane().add(methodNameField);
    methodNameField.setColumns(10);
    methodNameField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        rdbtnMethodSearch.setSelected(true);
      }
      public void focusLost(FocusEvent e) {
        rdbtnMethodSearch.setSelected(false);
      }
    });

    textField = new JTextField();
    textField.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    textField.setBounds(179, 127, 188, 20);
    frame.getContentPane().add(textField);
    textField.setColumns(10);
    textField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        keywordSearchRadio.setSelected(true);
      }
      public void focusLost(FocusEvent e) {
        keywordSearchRadio.setSelected(false);
      }
    });

    lblBugs = new JLabel("Bugs");
    lblBugs.setBounds(64, 216, 82, 30);
    lblBugs.setFont(new Font("Times New Roman", Font.BOLD, 20));
    Font bugsFont = lblBugs.getFont();
    Map bugFontAttributes = bugsFont.getAttributes();
    bugFontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    lblBugs.setFont(bugsFont.deriveFont(bugFontAttributes));
    frame.getContentPane().add(lblBugs);

    JRadioButton generateFolderStructureRadio = new JRadioButton("Folder Structure");
    functionalityButtonGroup.add(generateFolderStructureRadio);
    generateFolderStructureRadio.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    generateFolderStructureRadio.setBounds(295, 253, 109, 23);
    frame.getContentPane().add(generateFolderStructureRadio);

    JLabel lblLookUps = new JLabel("Look Ups");
    lblLookUps.setBounds(285, 216, 82, 30);
    lblLookUps.setFont(new Font("Times New Roman", Font.BOLD, 20));
    Font lookUpFont = lblLookUps.getFont();
    Map lookUpFontAttributes = lookUpFont.getAttributes();
    lookUpFontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    lblLookUps.setFont(lookUpFont.deriveFont(lookUpFontAttributes));
    frame.getContentPane().add(lblLookUps);

    JButton outputButton = new JButton("GET");
    outputButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
    outputButton.setBounds(621, 502, 89, 23);
    frame.getContentPane().add(outputButton);

    JPanel filterPanel = new JPanel();
    filterPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
    filterPanel.setBounds(419, 89, 277, 377);
    frame.getContentPane().add(filterPanel);
    filterPanel.setLayout(null);
    filterPanel.setVisible(false);

    JLabel lblFilter = new JLabel("Filter");
    lblFilter.setHorizontalAlignment(SwingConstants.CENTER);
    lblFilter.setFont(new Font("Times New Roman", Font.BOLD, 20));
    lblFilter.setBounds(0, 11, 277, 26);
    filterPanel.add(lblFilter);



    JRadioButton byAuthorsRadio = new JRadioButton("By Authors");
    byAuthorsRadio.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          filterName = "Authors";
          filterValues =
              Arrays.asList("Sankar", "Muthu", "Ganesh", "Sankar", "Muthu", "Ganesh", "Sankar",
                  "Muthu", "Ganesh", "Sankar", "Muthu", "Ganesh", "Sankar", "Muthu", "Ganesh",
                  "Sankar", "Muthu", "Ganesh");
          makeFilterList(filterPanel);
        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
        }
      }
    });
    filterButtonGroup.add(byAuthorsRadio);
    byAuthorsRadio.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    byAuthorsRadio.setBounds(20, 44, 109, 23);
    filterPanel.add(byAuthorsRadio);

    JRadioButton byFilesRadio = new JRadioButton("By Files");
    byFilesRadio.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          filterName = "Files";
          filterValues = Arrays.asList("Sriram", "Jeyaraj");
          makeFilterList(filterPanel);
        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
        }
      }
    });
    filterButtonGroup.add(byFilesRadio);
    byFilesRadio.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    byFilesRadio.setBounds(131, 44, 109, 23);
    filterPanel.add(byFilesRadio);


    filterFilter = new JTextField();
    filterFilter.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    filterFilter.setBounds(20, 69, 234, 20);
    filterFilter.setColumns(10);
    filterPanel.add(filterFilter);
    filterFilter.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        filterFilterData();
      }

      public void insertUpdate(DocumentEvent e) {
        filterFilterData();
      }

      public void removeUpdate(DocumentEvent e) {
        filterFilterData();
      }
    });



    JRadioButton rdbtnAllBugsAnd = new JRadioButton("All Bugs and Details");
    functionalityButtonGroup.add(rdbtnAllBugsAnd);
    rdbtnAllBugsAnd.setFont(new Font("Times New Roman", Font.BOLD, 12));
    rdbtnAllBugsAnd.setBounds(64, 253, 151, 23);
    frame.getContentPane().add(rdbtnAllBugsAnd);

    JRadioButton rdbtnSpecificBugs = new JRadioButton("Specific Bugs");

    functionalityButtonGroup.add(rdbtnSpecificBugs);
    rdbtnSpecificBugs.setFont(new Font("Times New Roman", Font.BOLD, 12));
    rdbtnSpecificBugs.setBounds(64, 279, 109, 23);
    frame.getContentPane().add(rdbtnSpecificBugs);

    JCheckBox chckbxJavaUnnecessaryFields = new JCheckBox("Java Unnecessary Fields, Constants");
    chckbxJavaUnnecessaryFields.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    chckbxJavaUnnecessaryFields.setBounds(76, 301, 199, 23);
    chckbxJavaUnnecessaryFields.setVisible(false);


    frame.getContentPane().add(chckbxJavaUnnecessaryFields);

    JCheckBox chckbxJavascriptUnnecessaryConstants =
        new JCheckBox("Javascript Unnecessary Constants");
    chckbxJavascriptUnnecessaryConstants.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    chckbxJavascriptUnnecessaryConstants.setBounds(76, 327, 199, 23);
    chckbxJavascriptUnnecessaryConstants.setVisible(false);
    frame.getContentPane().add(chckbxJavascriptUnnecessaryConstants);

    JCheckBox chckbxOptimusBugs = new JCheckBox("Optimus Bugs");
    chckbxOptimusBugs.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    chckbxOptimusBugs.setBounds(76, 353, 97, 23);
    chckbxOptimusBugs.setVisible(false);
    frame.getContentPane().add(chckbxOptimusBugs);

    JCheckBox indexFileError = new JCheckBox("Index File Error");
    indexFileError.setFont(new Font("Times New Roman", Font.PLAIN, 12));
    indexFileError.setBounds(130, 600, 199, 23);
    indexFileError.setVisible(false);
    frame.getContentPane().add(indexFileError);

    
    rdbtnSpecificBugs.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          chckbxJavaUnnecessaryFields.setVisible(true);
          chckbxJavascriptUnnecessaryConstants.setVisible(true);
          indexFileError.setVisible(true);
          chckbxOptimusBugs.setVisible(true);
        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
          chckbxJavaUnnecessaryFields.setVisible(false);
          chckbxJavascriptUnnecessaryConstants.setVisible(false);
          indexFileError.setVisible(false);
          chckbxOptimusBugs.setVisible(false);
          chckbxJavaUnnecessaryFields.setSelected(false);
          chckbxJavascriptUnnecessaryConstants.setSelected(false);
          indexFileError.setSelected(false);
          chckbxOptimusBugs.setSelected(false);
        }
      }
    });

  }


  /**
   * Update the Repository List Based on the Filter on the Repository FilterTextBox.
   */
  private void filterFilterData() {
    RowFilter<TableModel, Object> rf = null;
    // If current expression doesn't parse, don't update.
    try {
      rf = RowFilter.regexFilter(filterFilter.getText(), 1);
    } catch (java.util.regex.PatternSyntaxException e) {
      return;
    }
    filterSorter.setRowFilter(rf);
  }

  public void makeFilterList(JPanel filterPanel) {
    TableModel filterModel = new TableModel(filterName, filterValues);
    filterList = new JTable(filterModel);
    filterSorter = new TableRowSorter<TableModel>(filterModel);
    filterList.setBounds(20, 100, 234, 266);
    filterList.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 20));
    filterList.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    filterList.setRowSelectionAllowed(false);
    filterList.setShowVerticalLines(false);
    filterList.setShowHorizontalLines(false);
    filterList.setShowGrid(false);
    filterList.setRowSorter(filterSorter);
    filterList.setFillsViewportHeight(true);
    filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    filterList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    filterList.setFocusable(false);
    TableColumnModel filterColumnModel = filterList.getColumnModel();
    ((DefaultTableCellRenderer) filterList.getTableHeader().getDefaultRenderer())
        .setHorizontalAlignment(JLabel.LEFT);
    filterColumnModel.getColumn(1).setPreferredWidth(500);
    JScrollPane repositoryScrollPane = new JScrollPane(filterList);
    repositoryScrollPane.setBounds(20, 100, 234, 266);
    filterPanel.add(repositoryScrollPane);
  }
}

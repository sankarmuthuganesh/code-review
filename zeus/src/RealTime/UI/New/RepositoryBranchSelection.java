package RealTime.UI.New;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.NumberFormatter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import RealTime.GitAccess.ProgressLife;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JProgressBar;



public class RepositoryBranchSelection {

  private JFrame frame;
  private JTextField repositoryFilter;
  private JTable repositoryList;
  private TableRowSorter<TableModel> repositorySorter;
  private JTable branchList;
  private JTextField branchFilter;
  private JFormattedTextField fromRiverVersion;
  private TableRowSorter<TableModel> branchSorter;
  private JFormattedTextField toRiverVersion;
  private JScrollPane branchScrollPane;
  private static List<String> userRepositories;
  private static ProgressLife life;
  private static String nameOfUser;

  /**
   * Launch the application.
   */
  public static void main(String username, List<String> repoList, ProgressLife life) {
    userRepositories = repoList;
    RepositoryBranchSelection.life = life;
    nameOfUser = username;

    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RepositoryBranchSelection window = new RepositoryBranchSelection();
          window.frame.setVisible(true);
        } catch (Exception e) {
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public RepositoryBranchSelection() {
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

    branchFilter = new JTextField();
    branchFilter.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    branchFilter.setBounds(303, 101, 220, 20);
    branchFilter.setColumns(10);
    frame.getContentPane().add(branchFilter);

    JLabel betweenLabel = new JLabel("-");
    betweenLabel.setHorizontalAlignment(SwingConstants.CENTER);
    betweenLabel.setFont(new Font("Times New Roman", Font.BOLD, 35));
    betweenLabel.setBounds(370, 448, 28, 20);
    betweenLabel.setVisible(false);
    frame.getContentPane().add(betweenLabel);

    String range[] = {"GreaterThan", "LesserThan", "Between", "Equals"};
    JComboBox rangeCombobox = new JComboBox(range);
    rangeCombobox.setFont(new Font("Times New Roman", Font.BOLD, 15));
    rangeCombobox.setBounds(303, 417, 113, 20);
    rangeCombobox.setVisible(false);
    frame.getContentPane().add(rangeCombobox);
    rangeCombobox.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        int state = e.getStateChange();
        if (state == ItemEvent.SELECTED) {
          String selection = e.getItem().toString();
          if (selection.equals("LesserThan") || selection.equals("GreaterThan")
              || selection.equals("Equals")) {
            fromRiverVersion.setVisible(true);
            betweenLabel.setVisible(false);
            toRiverVersion.setVisible(false);

          } else {
            fromRiverVersion.setVisible(true);
            betweenLabel.setVisible(true);
            toRiverVersion.setVisible(true);
          }
        }
      }
    });

    JCheckBox inputRiverVersion = new JCheckBox("river Version-Numbers");
    inputRiverVersion.setFont(new Font("Times New Roman", Font.BOLD, 18));
    inputRiverVersion.setBounds(303, 387, 220, 23);
    inputRiverVersion.setVisible(false);

    repositoryFilter = new JTextField();
    repositoryFilter.setToolTipText("Mention Repository Name To Filter...");
    repositoryFilter.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    repositoryFilter.setBounds(22, 101, 250, 20);
    repositoryFilter.setColumns(10);
    frame.getContentPane().add(repositoryFilter);

    String nameOfUser="";
    try {
      nameOfUser = life.getGitLabApi().getUser().getUsername();
    } catch (IOException e1) {
    }
    JLabel lblHi = new JLabel("Hi "+nameOfUser+"!");
    lblHi.setFont(new Font("Times New Roman", Font.BOLD, 25));
    lblHi.setBounds(22, 11, 529, 30);
    frame.getContentPane().add(lblHi);
    frame.getContentPane().add(inputRiverVersion);

    // Number Format for Inputting river versions.
    NumberFormat format = NumberFormat.getInstance();
    NumberFormatter formatter = new NumberFormatter(format);
    formatter.setValueClass(Integer.class);
    formatter.setMinimum(0);
    formatter.setMaximum(2999);
    formatter.setAllowsInvalid(false);
    formatter.setCommitsOnValidEdit(true);

    fromRiverVersion = new JFormattedTextField(formatter);
    fromRiverVersion.setFont(new Font("Times New Roman", Font.BOLD, 15));
    fromRiverVersion.setVisible(false);

    fromRiverVersion.setBounds(303, 448, 67, 20);
    fromRiverVersion.setColumns(10);
    frame.getContentPane().add(fromRiverVersion);

    TableModel repositoryModel = new TableModel("Repository", userRepositories);
    repositoryList = new JTable(repositoryModel);
    repositorySorter = new TableRowSorter<TableModel>(repositoryModel);
    repositoryList.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 20));
    repositoryList.setFont(new Font("Times New Roman", Font.PLAIN, 15));
    repositoryList.setRowSelectionAllowed(false);
    repositoryList.setShowVerticalLines(false);
    repositoryList.setShowHorizontalLines(false);
    repositoryList.setShowGrid(false);
    repositoryList.setRowSorter(repositorySorter);
    repositoryList.setFillsViewportHeight(true);
    repositoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    repositoryList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    repositoryList.setFocusable(false);
    TableColumnModel repositoyColumnModel = repositoryList.getColumnModel();
    repositoyColumnModel.getColumn(1).setPreferredWidth(500);
    // repositoyColumnModel.getColumn(2).setPreferredWidth(400);
    repositoryList.setBounds(22, 120, 250, 318);
    ((DefaultTableCellRenderer) repositoryList.getTableHeader().getDefaultRenderer())
        .setHorizontalAlignment(JLabel.LEFT);
    JScrollPane repositoryScrollPane = new JScrollPane(repositoryList);
    repositoryScrollPane.setBounds(22, 120, 250, 318);
    frame.getContentPane().add(repositoryScrollPane);
    repositoryFilter.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        filterRepository();
      }

      public void insertUpdate(DocumentEvent e) {
        filterRepository();
      }

      public void removeUpdate(DocumentEvent e) {
        filterRepository();
      }
    });

    Set<String> selectedRepositories = new HashSet<>();
    Set<String> selectedBranches = new HashSet<>();



    JLabel lblOptional = new JLabel("(Optional)");
    lblOptional.setHorizontalAlignment(SwingConstants.CENTER);
    lblOptional.setFont(new Font("Times New Roman", Font.BOLD, 18));
    lblOptional.setBounds(562, 101, 140, 18);
    frame.getContentPane().add(lblOptional);


    JButton cloneButton = new JButton("Functionality");

    cloneButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
    cloneButton.setBounds(562, 494, 140, 37);
    cloneButton.setVisible(false);
    frame.getContentPane().add(cloneButton);


    Panel filterPanel = new Panel();
    filterPanel.setBounds(562, 130, 140, 338);
    frame.getContentPane().add(filterPanel);
    filterPanel.setVisible(false);
    lblOptional.setVisible(false);
    filterPanel.setLayout(null);
    inputRiverVersion.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (Objects.nonNull(branchList)) {
          for (int branchIndex = 0; branchIndex < branchList.getRowCount(); branchIndex++) {
            branchList.setValueAt(Boolean.FALSE, branchIndex, 0);
          }
        }
        if (e.getStateChange() == ItemEvent.SELECTED) {
          filterPanel.setVisible(false);
          lblOptional.setVisible(false);
          fromRiverVersion.setVisible(true);
          rangeCombobox.setVisible(true);
          branchFilter.setVisible(false);
          try{
            branchList.setVisible(false);
            branchScrollPane.setVisible(false);
          }catch(NullPointerException nullex){
            
          }
        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
          cloneButton.setVisible(false);
          fromRiverVersion.setVisible(false);
          rangeCombobox.setVisible(false);
          betweenLabel.setVisible(false);
          toRiverVersion.setVisible(false);
          if (selectedRepositories.size() == 1) {
            branchFilter.setVisible(true);
            branchList.setVisible(true);
            branchScrollPane.setVisible(true);
          }
        }
      }
    });

    repositoryList.getModel().addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
        selectedBranches.clear();
        for (int repoIndex = 0; repoIndex < repositoryList.getRowCount(); repoIndex++) {
          Object repositorySelected = repositoryList.getValueAt(repoIndex, 0);
          String repository = repositoryList.getValueAt(repoIndex, 1).toString();
          if (repositorySelected.equals(Boolean.TRUE)) {
            selectedRepositories.add(repository);
          } else if (repositorySelected.equals(Boolean.FALSE)) {
            if (selectedRepositories.contains(repository)) {
              selectedRepositories.remove(repository);
            }
          }
        }
        if (selectedRepositories.isEmpty()) {
          inputRiverVersion.setVisible(false);
          fromRiverVersion.setVisible(false);
          inputRiverVersion.setSelected(false);
          branchFilter.setVisible(false);
          try{
          branchList.setVisible(false);
          branchScrollPane.setVisible(false);
          }catch(NullPointerException ex){
            
          }
          
        } else if (selectedRepositories.size() == 1) {
          frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//          Map<String, List<String>> repositoriesAndBranches =
//              life.getAllBranches(new ArrayList(selectedRepositories));

          List<String> branches = life.getBranches(selectedRepositories.stream().findFirst().get());     
          List<String> uiBranchNames =
              branches.stream().map(branch -> branch.replace("refs/heads/", StringUtils.EMPTY))
                  .collect(Collectors.toList());
          TableModel branchModel = new TableModel("Branch", uiBranchNames);
          branchList = new JTable(branchModel);
          branchSorter = new TableRowSorter<TableModel>(branchModel);
          branchList.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 20));
          ((DefaultTableCellRenderer) branchList.getTableHeader().getDefaultRenderer())
              .setHorizontalAlignment(JLabel.LEFT);
          TableColumnModel branchColumnModel = branchList.getColumnModel();
          branchColumnModel.getColumn(1).setPreferredWidth(500);
          branchList.setFont(new Font("Times New Roman", Font.PLAIN, 15));
          branchList.setRowSelectionAllowed(false);
          branchList.setShowVerticalLines(false);
          branchList.setShowHorizontalLines(false);
          branchList.setShowGrid(false);
          branchList.setRowSorter(branchSorter);
          branchList.setFillsViewportHeight(true);
          branchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          branchList.setBounds(331, 120, 220, 266);
          branchList.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
          branchList.setFocusable(false);
          // branchList.setVisible(false);
          branchScrollPane = new JScrollPane(branchList);
          branchScrollPane.setBounds(303, 120, 220, 266);
          branchScrollPane.setVisible(false);
          frame.getContentPane().add(branchScrollPane);

          branchList.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
              for (int branchIndex = 0; branchIndex < branchList.getRowCount(); branchIndex++) {
                Object branchSelected = branchList.getValueAt(branchIndex, 0);
                String branch = branchList.getValueAt(branchIndex, 1).toString();
                if (branchSelected.equals(Boolean.TRUE)) {
                  selectedBranches.add(branch);
                } else if (branchSelected.equals(Boolean.FALSE)) {
                  if (selectedBranches.contains(branch)) {
                    selectedBranches.remove(branch);
                  }
                }
              }
              if (!selectedBranches.isEmpty()) {
                cloneButton.setVisible(true);
              } else {
                cloneButton.setVisible(false);
              }
              if (selectedBranches.size() == 1) {
                filterPanel.setVisible(true);
                lblOptional.setVisible(true);
              } else {
                filterPanel.setVisible(false);
                lblOptional.setVisible(false);
              }
              // System.out.println(selectedBranches);
              // System.out.println("-----------------------------");
            }
          });


          inputRiverVersion.setVisible(true);
          inputRiverVersion.setEnabled(true);
          fromRiverVersion.setVisible(false);
          inputRiverVersion.setSelected(false);
          branchScrollPane.setVisible(true);
          branchFilter.setVisible(true);
          branchList.setVisible(true);
          frame.setCursor(null);
        } else {
          cloneButton.setVisible(false);
          filterPanel.setVisible(false);
          lblOptional.setVisible(false);
          inputRiverVersion.setVisible(true);
          inputRiverVersion.setEnabled(false);
          fromRiverVersion.setVisible(true);
          inputRiverVersion.setSelected(true);
          branchFilter.setVisible(false);
          branchList.setVisible(false);
          branchScrollPane.setVisible(false);
        }
        try{
        for (int branchIndex = 0; branchIndex < branchList.getRowCount(); branchIndex++) {
          branchList.setValueAt(Boolean.FALSE, branchIndex, 0);
        }
        }catch(NullPointerException ex){
          
        }
      }
    });


    JCheckBox chckbxSelectAllRepositories = new JCheckBox("Select All Repositories");
    chckbxSelectAllRepositories.setFont(new Font("Times New Roman", Font.BOLD, 15));
    chckbxSelectAllRepositories.setBounds(95, 71, 177, 23);
    chckbxSelectAllRepositories.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        int state = e.getStateChange();
        if (state == ItemEvent.SELECTED) {
          inputRiverVersion.setEnabled(false);
          inputRiverVersion.setVisible(true);
          inputRiverVersion.setSelected(true);
          repositoryList.setVisible(false);
          repositoryFilter.setVisible(false);
          repositoryScrollPane.setVisible(false);
          branchFilter.setVisible(false);
          try{
            branchList.setVisible(false);
          }catch(NullPointerException nullex){
            
          }
          branchFilter.setVisible(false);
        } else if (state == ItemEvent.DESELECTED) {
          cloneButton.setVisible(false);
          repositoryList.setVisible(true);
          inputRiverVersion.setVisible(false);
          repositoryFilter.setVisible(true);
          repositoryScrollPane.setVisible(true);
          inputRiverVersion.setSelected(false);
          inputRiverVersion.setEnabled(true);
          branchFilter.setVisible(false);
         
          try{
            branchList.setVisible(false);
            branchScrollPane.setVisible(false);
          }catch(NullPointerException nullex){
          }
          
          for (int repoIndex = 0; repoIndex < repositoryList.getRowCount(); repoIndex++) {
            repositoryList.setValueAt(Boolean.FALSE, repoIndex, 0);
          }
        }
      }
    });
    frame.getContentPane().add(chckbxSelectAllRepositories);

    toRiverVersion = new JFormattedTextField(formatter);
    toRiverVersion.setBounds(397, 448, 67, 20);
    toRiverVersion.setVisible(false);
    frame.getContentPane().add(toRiverVersion);
    toRiverVersion.setColumns(10);

    JLabel lblLicense = new JLabel("License");
    lblLicense.setHorizontalAlignment(SwingConstants.CENTER);
    lblLicense.setBounds(0, 11, 140, 19);
    filterPanel.add(lblLicense);
    lblLicense.setFont(new Font("Times New Roman", Font.BOLD, 15));

    JComboBox licenseCombo = new JComboBox();
    licenseCombo.setBounds(0, 30, 140, 20);
    filterPanel.add(licenseCombo);
    licenseCombo.setFont(new Font("Times New Roman", Font.BOLD, 12));

    JLabel lblLicenseGroup = new JLabel("License Group");
    lblLicenseGroup.setHorizontalAlignment(SwingConstants.CENTER);
    lblLicenseGroup.setBounds(0, 76, 140, 20);
    filterPanel.add(lblLicenseGroup);
    lblLicenseGroup.setFont(new Font("Times New Roman", Font.BOLD, 15));

    JComboBox licenseGroupCombo = new JComboBox();
    licenseGroupCombo.setBounds(0, 96, 140, 20);
    filterPanel.add(licenseGroupCombo);
    licenseGroupCombo.setFont(new Font("Times New Roman", Font.BOLD, 12));

    JLabel lblSubsystem = new JLabel("Subsystem");
    lblSubsystem.setHorizontalAlignment(SwingConstants.CENTER);
    lblSubsystem.setBounds(0, 142, 140, 20);
    filterPanel.add(lblSubsystem);
    lblSubsystem.setFont(new Font("Times New Roman", Font.BOLD, 15));

    JComboBox subsystemCombo = new JComboBox();
    subsystemCombo.setBounds(0, 162, 140, 20);
    filterPanel.add(subsystemCombo);
    subsystemCombo.setFont(new Font("Times New Roman", Font.BOLD, 12));

    JLabel lblEpic = new JLabel("Epic");
    lblEpic.setHorizontalAlignment(SwingConstants.CENTER);
    lblEpic.setBounds(0, 208, 140, 20);
    filterPanel.add(lblEpic);
    lblEpic.setFont(new Font("Times New Roman", Font.BOLD, 15));

    JComboBox epicCombo = new JComboBox();
    epicCombo.setBounds(0, 228, 140, 20);
    filterPanel.add(epicCombo);
    epicCombo.setFont(new Font("Times New Roman", Font.BOLD, 12));

    JProgressBar cloneProgress = new JProgressBar();
    cloneProgress.setFont(new Font("Times New Roman", Font.BOLD, 15));
    cloneProgress.setBounds(562, 494, 140, 37);
    cloneProgress.setVisible(false);
    frame.getContentPane().add(cloneProgress);


    fromRiverVersion.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        cloneButton.setVisible(true);
      }
    });

    branchFilter.setVisible(false);
    branchFilter.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        filterBranches();
      }

      public void insertUpdate(DocumentEvent e) {
        filterBranches();
      }

      public void removeUpdate(DocumentEvent e) {
        filterBranches();
      }
    });
    cloneButton.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        cloneProgress.setVisible(true);
        System.out.println(selectedRepositories);
        System.out.println(selectedBranches);
        if (selectedBranches.isEmpty()) {
          String range = rangeCombobox.getSelectedItem().toString();
          if (range.equals("Between")) {
            String from = fromRiverVersion.getText();
            String to = toRiverVersion.getText();

          } else {
            String from = fromRiverVersion.getText();
          }
        } else {

        }
      }
    });

  }

  /**
   * Update the Repository List Based on the Filter on the Repository FilterTextBox.
   */
  private void filterRepository() {
    RowFilter<TableModel, Object> rf = null;
    // If current expression doesn't parse, don't update.
    try {
      rf = RowFilter.regexFilter(repositoryFilter.getText(), 1);
    } catch (java.util.regex.PatternSyntaxException e) {
      return;
    }
    repositorySorter.setRowFilter(rf);
  }

  /**
   * Update the Branch List Based on the Filter on the Branch FilterTextBox.
   */
  private void filterBranches() {
    RowFilter<TableModel, Object> rf = null;
    // If current expression doesn't parse, don't update.
    try {
      rf = RowFilter.regexFilter(branchFilter.getText(), 1);
    } catch (java.util.regex.PatternSyntaxException e) {
      return;
    }
    branchSorter.setRowFilter(rf);
  }

}

package RealTime.UI.New;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class RepositoriesBranche {

	private JFrame frame;
	private JTextField repositoryFilter;
	private JTextField branchFilter;
	private JTextField riverVersionField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RepositoriesBranche window = new RepositoriesBranche();
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
	public RepositoriesBranche() {
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
		
		List<Integer> kk=Arrays.asList(1,2,3,4,5,6,7,8,9,0);
		
		
		JLabel branchesLabel = new JLabel("Branches");
		branchesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		branchesLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		branchesLabel.setBounds(331, 54, 220, 23);
		frame.getContentPane().add(branchesLabel);
		
		JList branchList = new JList(kk.toArray());
		branchList.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		branchList.setBounds(331, 122, 220, 264);
		frame.getContentPane().add(branchList);
		
		branchFilter = new JTextField();
		branchFilter.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		branchFilter.setBounds(331, 101, 220, 20);
		branchFilter.setColumns(10);
		frame.getContentPane().add(branchFilter);
		
		JCheckBox inputRiverVersion = new JCheckBox("Use River Version Numbers");
		inputRiverVersion.setFont(new Font("Times New Roman", Font.BOLD, 15));
		inputRiverVersion.setBounds(331, 393, 220, 23);
		inputRiverVersion.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					riverVersionField.setVisible(true);
//					branchFilter.setVisible(false);
//					branchList.setVisible(false);
				}
				else if(e.getStateChange()==ItemEvent.DESELECTED){
					riverVersionField.setVisible(false);
//					branchFilter.setVisible(true);
//					branchList.setVisible(true);
				}
			}
		});
		
		JList repositoryList = new JList(kk.toArray());
		repositoryList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JList selectedRepoList=(JList)e.getSource();
				if(selectedRepoList.getSelectedIndices().length==1){
					branchFilter.setVisible(true);
					branchList.setVisible(true);
					inputRiverVersion.setSelected(false);
					riverVersionField.setVisible(false);
				}else{
					branchFilter.setVisible(false);
					branchList.setVisible(false);
					inputRiverVersion.setSelected(true);
					riverVersionField.setVisible(true);
					
				}
			}
		});
		
		repositoryList.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		repositoryList.setBounds(22, 122, 250, 316);
		frame.getContentPane().add(repositoryList);
		
		JLabel repositoriesLabel = new JLabel("Repositories");
		repositoriesLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
		repositoriesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		repositoriesLabel.setBounds(22, 52, 250, 26);
		frame.getContentPane().add(repositoriesLabel);
		
		
		
		repositoryFilter = new JTextField();
		repositoryFilter.setBounds(22, 101, 250, 20);
		repositoryFilter.setColumns(10);
		frame.getContentPane().add(repositoryFilter);
		
		
		
		JLabel lblHi = new JLabel("Hi!");
		lblHi.setFont(new Font("Times New Roman", Font.BOLD, 25));
		lblHi.setBounds(22, 11, 529, 30);
		frame.getContentPane().add(lblHi);
		
		
		
		
		frame.getContentPane().add(inputRiverVersion);
		
		riverVersionField = new JTextField();
		riverVersionField.setVisible(false);
		riverVersionField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				
			}
		});
		riverVersionField.setBounds(331, 418, 220, 20);
		riverVersionField.setColumns(10);
		frame.getContentPane().add(riverVersionField);
		
	}
}

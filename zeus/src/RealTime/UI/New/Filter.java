package RealTime.UI.New;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.input.MouseEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataListener;

import com.jidesoft.swing.CheckBoxList;
import com.jidesoft.swing.CheckBoxListSelectionModel;
import com.sun.scenario.effect.Filterable;


public class Filter  extends CheckBoxList{
	protected static Border noFocusBorder=new EmptyBorder(1,1,1,1);
	public static void main(String args[]){
		List<String> names=new ArrayList<>();
		names.add("Sankar");
		names.add("Vijay");
		names.add("Sanjay");
		names.add("Sankar");
		names.add("Vijay");
		names.add("Sanjay");
		names.add("Sankar");
		names.add("Vijay");
		names.add("Sanjay");
		names.add("Sankar");
		names.add("Vijay");
		names.add("Sanjay");
		names.add("Sankar");
		names.add("Sanjay");
		names.add("Sankar");
		
		
		CheckBoxList checkBoxList=new CheckBoxList(names.toArray());

	
		
		DefaultListModel<CheckBoxUnit> model=new DefaultListModel();
		names.stream().forEach(name ->{
			model.addElement(new Filter().new CheckBoxUnit(name));
		});
		
		
		//JList<CheckBoxUnit> repositoryList=new JList<>(model);
	//	repositoryList.setCellRenderer(createListRenderer());
	//	repositoryList.setCellRenderer(new ListCheckboxRenderer());
		//repositoryList.setCellRenderer(new Filter().new CheckBoxCellRenderer());
		//JPanel panel=JListFilterDecorator.decorate(repositoryList, Filter::employeeFilter);
		//repositoryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JFrame frame=createFrame();
		//JListFilterDecorator.decorate(checkBoxList, Filter::filterCondition,frame);
		//frame.add(panel);
		
		frame.add(checkBoxList);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private static boolean filterCondition(CheckBoxUnit repository,String userInput){
		return repository.label.toLowerCase().contains(userInput);
	}
	
	class CheckBoxUnit{
		private String label;
		private boolean isSelected=false;
		public CheckBoxUnit(String label){
			this.label=label;
		}
		public boolean isSelected(){
			return isSelected;
		}
		public void setSelected(boolean isSelected){
			this.isSelected=isSelected;
		}
		@Override
		public String toString(){
			return label;
		}
	}
	
	private static ListCellRenderer<? super String> createListRenderer(){
		return new DefaultListCellRenderer(){
			private Color background = new Color(0,100,255,15);
		//	private Color defaultBackground = new Color(0,220, 20, 60);
			private Color defaultBackground = (Color)UIManager.get("List.background");
			@Override
			public Component getListCellRendererComponent(JList<?> list,Object value,int index, boolean isSelected, boolean cellHasFocus){
				Component c=super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
				if(c instanceof JLabel){
					JLabel label=(JLabel) c;
					String emp=(String)value;
					label.setText( emp);
					if(!isSelected){
						label.setBackground(index % 2 == 0 ? background:defaultBackground);	
					}	
				}
				return c;
			}
		};
	}
	
 class CheckBoxCellRenderer extends JCheckBox implements ListCellRenderer{
//		public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value,int index,boolean isSelected, boolean cellHasFocus){
//			JCheckBox checkbox=value;
//			checkbox.setBackground(isSelected ? list.getSelectionBackground():list.getBackground());
//			checkbox.setForeground(isSelected ? list.getSelectionForeground():list.getForeground());
//			checkbox.setEnabled(list.isEnabled());
//			checkbox.setFont(list.getFont());
//			checkbox.setFocusPainted(false);
//			checkbox.setBorderPainted(true);
//			checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder"):noFocusBorder);
//			return checkbox;
//		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setComponentOrientation(list.getComponentOrientation());
			setEnabled(list.isEnabled());
			setSelected(isSelected);
			setFont(list.getFont());
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			setText(value.toString());
			return this;
		}
	}
	
	private static JFrame createFrame(){
		JFrame frame=new JFrame("JList Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(600,300));
		return frame;
	}
}

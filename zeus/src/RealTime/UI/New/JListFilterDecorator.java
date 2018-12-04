package RealTime.UI.New;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataListener;

import com.jidesoft.swing.CheckBoxListSelectionModel;


public class JListFilterDecorator {
public static <T> JFrame decorate (JList<T> jList, BiPredicate<T,String> userFilter, JFrame frame){
	if(!(jList.getModel() instanceof DefaultListModel)){
		throw new IllegalArgumentException("List Model must be an instance of Default List Model");
		
	}
	DefaultListModel<T> model=(DefaultListModel<T>)jList.getModel();
	List<T> items = getItems(model);
	JTextField filterField=new JTextField();
	filterField.getDocument().addDocumentListener(new DocumentListener(){
		@Override
		public void insertUpdate(DocumentEvent e){
			filter();
		}
		@Override
		public void removeUpdate(DocumentEvent e){
			filter();
		}
		@Override
		public void changedUpdate(DocumentEvent e){
			filter();
		}
		private void filter(){
			model.clear();
			String filterText=filterField.getText();
			for(T item: items){
				if(userFilter.test(item, filterText)){
					model.addElement(item);
				}
			}
		}
	});
	//JPanel panel=new JPanel(new BorderLayout());
	frame.add(filterField,BorderLayout.NORTH);
	JScrollPane listScrollPane=new JScrollPane(jList);
	frame.add(listScrollPane);
	return frame;
}
private static <T> List<T> getItems(DefaultListModel<T> model){
	List<T> list=new ArrayList<>();
	for(int i=0;i<model.size();i++){
		list.add(model.elementAt(i));
	}
	return list;
}


}

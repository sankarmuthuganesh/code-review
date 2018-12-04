package RealTime.UI.New;

import java.util.List;

import javax.swing.table.AbstractTableModel;

class TableModel extends AbstractTableModel {
	
	  private boolean DEBUG = false;
	  private String[] columnNames; 
//	  = {"","Repositories"};
	  private Object[][] data;
//	  = {
//									{new Boolean(false),"hue-scm-project"},
//									{new Boolean(false),"hue-scm-cost"},
//									{new Boolean(false),"hue-scm-com"},
//									{new Boolean(false),"hue-ac-cashmanagement"},
//									{new Boolean(false),"hue-ac-fi"}
//								
//									};
//	  private Object[][] data= {
//				{"hue-scm-project",new Boolean(false)},
//				{"hue-scm-cost",new Boolean(false)},
//				{"hue-scm-com",new Boolean(false)},
//				{"hue-ac-cashmanagement",new Boolean(false)},
//				{"hue-ac-fi",new Boolean(false)}
//				};

	  
	  TableModel(String columnName, List<String> values){
	    	String[] columnNames = {"\u2713",columnName};
	    	this.columnNames=columnNames;
	    	Object[][] datas=new Object[values.size()][2];
	    	for(int valueIndex=0; valueIndex<values.size(); valueIndex++){
	    		datas[valueIndex][0]=new Boolean(false);
	    		datas[valueIndex][1]=values.get(valueIndex);
	    	}
	    	this.data=datas;
	    }
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
//    	System.out.println("C: "+c);
//    	System.out.println("getValueAt(0, c): "+getValueAt(0, c));
//    	System.out.println("getValueAt(0, c).getClass(): "+getValueAt(0, c).getClass());
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
//        if (col < 1) {
//            return false;
//        } else {
//            return true;
//        }
    	if(col==0){
    		return true;
    	}else{
    		return false;
    	}
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        if (DEBUG) {
            System.out.println("Setting value at " + row + "," + col
                               + " to " + value
                               + " (an instance of "
                               + value.getClass() + ")");
        }

        data[row][col] = value;
        fireTableCellUpdated(row, col);

        if (DEBUG) {
            System.out.println("New value of data:");
            printDebugData();
        }
    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i=0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j=0; j < numCols; j++) {
                System.out.print("  " + data[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }
    public void setColumnAndValues(String columnName, List<String> values){
    	String[] columnNames = {"",columnName};
    	this.columnNames=columnNames;
    	Object[][] datas=new Object[1000][];
    	for(int valueIndex=0; valueIndex<values.size(); valueIndex++){
    		data[valueIndex][0]=new Boolean(false);
    		data[valueIndex][1]=values.get(valueIndex);
    	}
    	this.data=datas;
    }
}
package jon.mrtg;

import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class LogFileTable extends JTable {
	
	public LogFileTable(Vector logData) {
		super();
		setModel(new LogFileTableModel(logData));
		LogFileTableColumnModel columnModel = new LogFileTableColumnModel();
		setColumnModel(columnModel);
		setTableHeader(new JTableHeader(columnModel));
	}
	
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	
	public boolean isCellSelected(int row, int col) {
		return false;
	}
	
	private class LogFileTableModel extends AbstractTableModel {
		private Vector logData = new Vector();
		
		public LogFileTableModel(Vector logData) {
			super();
			if (logData != null) this.logData = logData;
		}
		
		public void setLogData(Vector logData) {
			this.logData = logData;
			fireTableDataChanged();
		}
		
		public Vector getLogData() {
			return logData;
		}
		
		public int getColumnCount() {
			return 4;
		}
		
		public int getRowCount() {
			return logData.size();
		}
		
		public Object getValueAt(int row, int col) {
			if (row >= logData.size() || col >= 4) return null;
			LogDatum logDatum = (LogDatum)logData.elementAt(row);
			if (col == 0) return new Integer(logDatum.getAverageIn());
			else if (col == 1) return new Integer(logDatum.getAverageOut());
			else if (col == 2) return new Integer(logDatum.getMaxIn());
			else return new Integer(logDatum.getMaxOut());
		}
	}
	
	private class LogFileTableColumnModel extends DefaultTableColumnModel {
		
		public LogFileTableColumnModel() {
			super();
			TableColumn column0 = new TableColumn(0);
			column0.setHeaderValue("Average In");
			addColumn(column0);
			TableColumn column1 = new TableColumn(1);
			column1.setHeaderValue("Average Out");
			addColumn(column1);
			TableColumn column2 = new TableColumn(2);
			column2.setHeaderValue("Maximum In");
			addColumn(column2);
			TableColumn column3 = new TableColumn(3);
			column3.setHeaderValue("Maximum Out");
			addColumn(column3);
		}
	}
}
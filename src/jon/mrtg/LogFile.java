package jon.mrtg;

import java.util.Enumeration;
import java.util.Vector;

public class LogFile {
	private LogHeader logHeader = new LogHeader();
	private Vector logData = new Vector();
	
	public LogFile() {}
		
	public LogFile(LogHeader logHeader, Vector logData) {
		this.logHeader = logHeader;
		setLogData(logData);
	}
	
	public LogHeader getLogHeader() {
		return logHeader;
	}
	
	public Vector getLogData() {
		return logData;
	}
	
	public void setLogHeader(LogHeader logHeader) {
		this.logHeader = logHeader;
	}
	
	public void setLogData(Vector logData) {
		for (Enumeration e = logData.elements(); e.hasMoreElements();) {
			Object logDatum = e.nextElement();
			if (logDatum instanceof LogDatum)
				this.logData.addElement(logDatum);
		}
	}
	
	public void addLogDatum(LogDatum logDatum) {
		logData.addElement(logDatum);
	}
	
	public boolean removeLogDatum(LogDatum logDatum) {
		return logData.remove(logDatum);
	}
	
	public String toString() {
		String rtn = logHeader.toString() + "\n";
		for (Enumeration e = logData.elements(); e.hasMoreElements();)
			rtn += e.nextElement().toString() + "\n";
		return rtn;
	}
}
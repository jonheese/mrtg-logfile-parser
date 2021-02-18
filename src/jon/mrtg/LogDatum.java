package jon.mrtg;

public class LogDatum {
	private long timestamp = 0L;
	private int avgIn = 0, avgOut = 0, maxIn = 0, maxOut = 0;
	
	public LogDatum() {}
	
	public LogDatum(long timestamp, int avgIn, int avgOut, int maxIn, int maxOut) {
		this.timestamp = timestamp;
		this.avgIn = avgIn;
		this.avgOut = avgOut;
		this.maxIn = maxIn;
		this.maxOut = maxOut;
	}
	
	public long getTimeStamp() {
		return timestamp;
	}
	
	public int getAverageIn() {
		return avgIn;
	}
	
	public int getAverageOut() {
		return avgOut;
	}
	
	public int getMaxIn() {
		return maxIn;
	}
	
	public int getMaxOut() {
		return maxOut;
	}
	
	public void setTimeStamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setAverageIn(int avgIn) {
		this.avgIn = avgIn;
	}
	
	public void setAverageOut(int avgOut) {
		this.avgOut = avgOut;
	}
	
	public void setMaxIn(int maxIn) {
		this.maxIn = maxIn;
	}
	
	public void setMaxOut(int maxOut) {
		this.maxOut = maxOut;
	}
	
	public String toString() {
		return timestamp + " " + avgIn + " " + avgOut + " " + maxIn + " " + maxOut;
	}
}
package jon.mrtg;

public class LogHeader {
	private long timestamp = 0L, incoming = 0L, outgoing = 0L;
	
	public LogHeader() {}
	
	public LogHeader(long timestamp, long incoming, long outgoing) {
		this.timestamp = timestamp;
		this.incoming = incoming;
		this.outgoing = outgoing;
	}
	
	public long getTimeStamp() {
		return timestamp;
	}
	
	public long getIncomingBytesCounter() {
		return incoming;
	}
	
	public long getOutgoingBytesCounter() {
		return outgoing;
	}
	
	public void setTimeStamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setIncomingBytesCounter(long incoming) {
		this.incoming = incoming;
	}
	
	public void setOutgoingBytesCounter(long outgoing) {
		this.outgoing = outgoing;
	}
	
	public String toString() {
		return timestamp + " " + incoming + " " + outgoing;
	}
}
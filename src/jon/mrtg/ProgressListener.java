package jon.mrtg;

public interface ProgressListener {
	
	public void setProgressValue(int value);
		
	public int getProgressValue();
		
	public void progressFinished(int changes);
		
	public void setProgressBarEnabled(boolean enabled);
	
}
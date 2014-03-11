
public abstract class Decoder extends SADevice {
	
	private int maxTxcChannel;
	
	public void setMaxTxcChan(int chan) { maxTxcChannel = chan; }
	public int getMaxTxcChan() 			{ return maxTxcChannel; }

	boolean isDecoder() 	{ return true; }
	boolean isTranscoder() 	{ return false; }
	
	abstract void configDpmParam();
	abstract void createBackupFile();
	

}

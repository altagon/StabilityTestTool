

public abstract class SADevice {

	final public static String 	SA_PREFIX 		= "1.3.6.1.4.1.1429.2.2.5.";
	final public static int 	COMMENT_TAG 	= 999999;
	
	boolean isDecoder() 	{ return false; }
	boolean isTranscoder() 	{ return false; }
	
	abstract void createBackupFile();

	
}

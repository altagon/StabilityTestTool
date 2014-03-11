
public abstract class Transcoder extends Decoder {

	
	public boolean isDecoder() 			{ return false; }
	public boolean isTranscoder() 		{ return true; }
	
	abstract void configTxcParam();	

}

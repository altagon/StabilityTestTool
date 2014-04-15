/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   1.4
 *  Date:   	Mar 14, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	Transcoder.java
 *  
 *  Description: 
 *  	Abstract base class representing a generic transcoder device
 *  
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public abstract class Transcoder extends Decoder {

	@SuppressWarnings("unchecked")
	public ArrayList<String>[] txcParam 	= new ArrayList[2];
	
	private int maxTxcChannel;
	protected int txc1;
	protected int txc2;
	
	
	Transcoder(Date myday, ReadSchedule sched, int maxTcChan) 
	{
		super(myday, sched);
		
		this.dpm  = sched.getDpm1();
		this.txc1 = sched.getTxc1();
		this.txc2 = sched.getTxc2();
		maxTxcChannel = maxTcChan;
}	
	public boolean isDecoder() 			{ return false; }
	public boolean isTranscoder() 		{ return true; }

	void configTxcParam(int actChanNum)
	{
		
		@SuppressWarnings("serial")
		final Map<String,Integer> hResMap = new HashMap<String,Integer>() {{
			
			put("Full",0x780);
			put("3/4", 0x5A0);
			put("720", 0x2D0);
			put("704", 0x2C0);
			put("544", 0x220);
			put("480", 0x1E0);
			put("352", 0x160);
		}};
		
		@SuppressWarnings("serial")
		final Map<String,Integer> userGOPMap = new HashMap<String,Integer>() {{
			
			put("1:0",  0xA);
			put("12:2", 0x7A);
			put("15:2", 0x98);
			put("24:2", 0xF2);
			put("30:2", 0x12E);
		}};
	
		@SuppressWarnings("serial")
		final Map<String,Integer> arConvMap = new HashMap<String,Integer>() {{
			
			put("None",  		1);
			put("Auto", 		2);
			put("AFD", 			3);
			put("16:9L/B",		4);
			put("4:3P/B", 		5);
			put("13:6", 		6);
			put("4:3 CCO", 		7);
			put("16:9 SCALE", 	8);
		}};

		addComment("*** Start Transcode Section ***");
		
		// loop to distribute transcode case#1 to channel 1..N/2 and transcode  case#2 to channel (N/2+1)..N
		for(int i = 0; i < 2; i ++) {
			int iStart = i* actChanNum/2 + 1;
			int iFinish =(i+1) * maxTxcChannel/2;
			
			Integer quality = txcParam[i].get(5).equalsIgnoreCase("sd")?0x05:0x04;		
			Integer hRes = hResMap.get(txcParam[i].get(6));
			if(hRes == null) hRes = 0x780;		
			Integer bitRateMode = txcParam[i].get(7).equalsIgnoreCase("CBR")?1:2;
			Integer gopControl = 0;
			Integer userGOP    = 0; 	
			String gop = txcParam[i].get(9).toString();
			if(gop.equalsIgnoreCase("I-Frame")) {
				gopControl = 1;
			}
			else {
				gopControl = 2;
				userGOP = userGOPMap.get(gop);
				if(userGOP == null) userGOP = 0x98;
			}
			Integer rate = Double.valueOf(txcParam[i].get(8)).intValue()*1000000;
			Integer outputAR = txcParam[i].get(10).equalsIgnoreCase("16:9")?2:1;
			Integer convAR = arConvMap.get(txcParam[i].get(11));
			if(convAR == null) convAR = 3;
			Integer pullDown32 = txcParam[i].get(12).equalsIgnoreCase("on")?2:1;
			
			//---setOid("37.2.1.1.13.", iStart, iFinish, 0x01);	// transcoding enabled (YES - 1, NO - 0)
			
			setOid("37.2.1.1.8.",  iStart, iFinish, hRes);			// HD_HRES
			setOid("37.2.1.1.9.", iStart, iFinish, bitRateMode);	// HD_BitRateMode
			setOid("37.2.1.1.10.", iStart, iFinish, rate);			// HD_Rate
			setOid("37.2.1.1.11.", iStart, iFinish, gopControl);	// HD_UserGOP1 {!FrameSync - 1, User Gop(MN) - 2}
			setOid("37.2.1.1.12.", iStart, iFinish, userGOP);		// HD_UserGOP1 {1_0 - A, 12_2 - 7A, 15_2 - 98, 24_2 - F2, 30_2 - 12E}
			setOid("37.2.1.1.13.", iStart, iFinish, pullDown32);	// HD 3:2 Pulldown1 { Disable - 01, Enable - 02}
			setOid("37.2.1.1.17.", iStart, iFinish, hRes);			// SD_HRES
			setOid("37.2.1.1.18.", iStart, iFinish, bitRateMode);	// SD_BitRateMode
			setOid("37.2.1.1.19.", iStart, iFinish, rate);			// SD_Rate 
			setOid("37.2.1.1.20.", iStart, iFinish, gopControl);	// SD_UserGOP1 {!FrameSync - 1, User Gop(MN) - 2}
			setOid("37.2.1.1.21.", iStart, iFinish, userGOP);		// SD_UserGOP1 {1_0 - A, 12_2 - 7A, 15_2 - 98, 24_2 - F2, 30_2 - 12E}
			setOid("37.2.1.1.22.", iStart, iFinish, pullDown32);	// SD 3:2 Pulldown1 { Disable - 01, Enable - 02} 
			setOid("37.2.1.1.23.", iStart, iFinish, outputAR);		// SD_OutputAspectRatio 1 { 4:3 - 01, 16:9 - 02}
			setOid("37.2.1.1.24.", iStart, iFinish, convAR);		// SD_AspectRatioConversion1 {None - 01, Auto -02, AutoAFD - 03,16:9L/B - 04, 4:3P/B - 05, 14:6 - 06, 4:3 CCO - 07, 16:9 SCALE - 08}  
			setOid("37.2.1.1.4.", iStart, iFinish, 1);				// transcoderCfgPkt1 {None(1), CEA 708(2), SCTE-20(3)} - always None
			setOid("37.2.1.1.5.", iStart, iFinish, 1);				// transcoderCfgPkt2 {None(1), CEA 708(2), SCTE-20(3)} - always None		
			setOid("37.2.1.1.3.", iStart, iFinish, quality);		// HDSDOutput1 { SD - 04, HD - 05}
			
			if(actChanNum == 1)		// second set of transcoding settings does not have sense when only ONE channel is active
				break;
		} // i
		
		addComment("*** Ens Transcode Section ***");
		
	}


}

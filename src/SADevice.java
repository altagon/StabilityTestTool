/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   1.4
 *  Date:   	Mar 14, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	SADevice.java
 *  
 *  Description: 
 *  	Abstract base class representing a generic test device
 *  
 */

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


public abstract class SADevice {

	final public static String 	SA_PREFIX 		= "1.3.6.1.4.1.1429.2.2.";	// next number .5 or .6
	final public static int 	COMMENT_TAG 	= 999999;
	
	public String fileNameSuffix				= null;
	public Map<String,Integer> oids 			= new LinkedHashMap<String,Integer>(); 
	
	protected int channelNumber					= -1;
	protected int inp							= -1;
	protected int mpe							= -1;
	protected int vid							= -1;
	protected int rfSource						= -1;
	protected boolean asiScramb                 = false;

	boolean isDecoder() 	{ return false; }
	boolean isTranscoder() 	{ return false; }
		
    
	SADevice(Date myday) 
	{
		   SimpleDateFormat timeStamp = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss");
		   Calendar cal = Calendar.getInstance();
		   
		   cal.setTime(myday);
		   fileNameSuffix = "_Stability_Config_" + timeStamp.format(cal.getTime()) + ".bkp";

	}
	
	void createBackupFile(String devName, int designationNum)
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("<BKPRST>\n<FILE_INFO>\n<PLAT_TYPE>");
		if(devName.equalsIgnoreCase("D9854I"))
			buf.append("D9854-I");	//	exclusion: add '-' before I
		else 
			buf.append(devName);
		buf.append("</PLAT_TYPE>\n<DESIGNATION>");
		buf.append(designationNum);
		buf.append("</DESIGNATION>\n</FILE_INFO>\n<SETTINGS>\n");
		
		for (Entry<String, Integer> entry : oids.entrySet()) {
		    String  key   = entry.getKey();
		    Integer value = entry.getValue();
		    
		    if(value == COMMENT_TAG) {
		    	// process comment
		    	buf.append("<!--");
		    	buf.append(key);
		    	buf.append("-->\n");
		    }
		    else {
		    	// OID 
		    	buf.append("<RECORD>\n\t<OID>" + key + "</OID>\n\t<VALUE>");	
		    	String hexStr = String.format("%X", value);
		    	if(hexStr.charAt(0) != 0) hexStr  = '0'+ hexStr;	// hex number should have leading 0
		    	if(hexStr.length()%2 != 0) hexStr = '0'+ hexStr;	// hex number should have even digits
		    	buf.append(hexStr);
		    	buf.append("</VALUE>\n</RECORD>\n");
		    }
		}  // entry
		
		buf.append("</SETTINGS>\n</BKPRST>\n");
		
		try {
			File dir = new File(STS.outputDirectory);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			
			FileOutputStream out = new FileOutputStream(STS.outputDirectory + "\\" + devName + fileNameSuffix);
			out.write(buf.toString().getBytes());
			out.close();
		} catch(Exception e) {
			System.out.println("Can not write into file " + devName + fileNameSuffix);
		}
		
	} // createbackupFile
	
	// Convenience method
	
	public void addComment(String comment)
	{
		oids.put(comment,  COMMENT_TAG);
	}
	
	public void setOidGroup1(String shortOid, int startIdx, int endIdx, int[] value, int grpSize)
	{
		for(int i = 0; i <= endIdx-startIdx; i++) {
			for(int j = 0; j < grpSize; j++)
				oids.put(SADevice.SA_PREFIX + shortOid + (startIdx+i*grpSize+j),  value[i]);
		} // i,j
	}	
	
	public void setOidGroup2(String shortOid, int startIdx, int endIdx, int[] value, int grpSize)
	{
		for(int i = 0; i <= endIdx-startIdx; i++)
			for(int j = 0; j < grpSize; j++) {
				oids.put(SADevice.SA_PREFIX + shortOid + (startIdx+i*grpSize+j),  value[j]);
			} // j,i
	}	

	public void setOid(String shortOid, int startIdx, int endIdx, int[] value)
	{
		for(int i = startIdx; i <= endIdx; i++) {
			oids.put(SADevice.SA_PREFIX + shortOid + i,  value[i-startIdx]);
		} // i
	}	

	public void setOid(String shortOid, Decoder.Function f, int startIdx, int endIdx)
	{
		for(int i = startIdx; i <= endIdx; i++) {
			oids.put(SADevice.SA_PREFIX + shortOid + i, f.calculatePid(i));

		} // i
	}	
	
	public void setOid(String shortOid, int startIdx, int endIdx, int value)
	{
		for(int i = startIdx; i <= endIdx; i++) {
			oids.put(SADevice.SA_PREFIX + shortOid + i,  value);
		} // i
	}	

// ###AG : not in use any more !!!
//	public void setFullOid(String fullOid, int startIdx, int endIdx, int value)
//	{
//		for(int i = startIdx; i <= endIdx; i++) {
//			oids.put(fullOid + i,  value);
//		} // i
//	}	
//	
//	public void setFullOid(String fullOid, int value)
//	{
//		oids.put(fullOid,  value);
//	}	

	public void setOid(String shortOid, int value)
	{
		oids.put(SADevice.SA_PREFIX + shortOid,  value);
	}	

//	private int calculatePid(int idx)
//	{
//		return 6000+idx;
//	}

	public int indexOf(final String[] values, String s)
	{
		for(int i = 0; i < values.length; i++) {
			if(s.equalsIgnoreCase(values[i])) {
				return i+1;
			}
		} // i
		return 0;
	}

	int configRFParam(String devName, int maxChan)
	{
		ReadTestCaseParam inpTC = new ReadTestCaseParam(devName, 4, 13, inp);
		ArrayList<String> inpTCParam = inpTC.getParam();

		ReadTestCaseParam mpeTC = new ReadTestCaseParam(devName, 10, 9, mpe);
		ArrayList<String> mpeTCParam = mpeTC.getParam();

		ReadTestCaseParam vidTC = new ReadTestCaseParam(devName, 7, 7, vid);
		ArrayList<String> vidTCParam = vidTC.getParam();
		
		//---System.out.println(inpTCParam);
		String s1 = inpTCParam.get(1);
		String s2 = inpTCParam.get(2);
		String s3 = vidTCParam.get(5);
		String s4 = mpeTCParam.get(3);
		
		if(!devName.equalsIgnoreCase("D9865"))
			addComment("*** Beginning of RF section ***");
		
		if(devName.equalsIgnoreCase("D9865")) {
			rfSource = 1;	// for D9865 use RF1 only
		}
		
		switch(rfSource) {
		
		case 0:	// ASI test case
			setOid("5.5.1.1.0",    1);	
			setOid("5.34.1.1.6.1", 200000000); 		// ASI UserRate 0..206000000 (default 200 Mb/sec)
			setOid("5.34.1.1.7.1", 2);				// Null Packet Insert { no(1), yes (2)}
			setOid("5.34.1.1.5.1", 2);				// RateControl {auto(1), user(2)}
			setOid("5.34.1.1.3.1", 7);				// Output Mode { noOutput(1), passThrough(2), serviceChannelOnly(3), mapPassthrough(4), mapserviceChannelOnly(5), fullDpmControl(6), transcoding(7) 
			//!!!
			setOid("5.34.1.1.4.1", 1); 				// ASI Output Scrambling Mode { deScrambled(1), scrambled (2)}
			
			if(asiScramb) {
				
				setOid("6.5.18.3.1.1.3.1",  2);
				setOid("6.5.18.3.1.1.4.1",  2);	
				setOid("5.34.1.1.8.1", 2);		
				//!!!setOid("5.34.1.1.4.1", 2); 			// Scrambling Mode { deScrambled(1), scrambled (2)}
				          
				setOid("6.5.18.3.3.1.3.1", 1);
				setOid("6.5.18.3.3.1.4.1", 1);		
				setOid("6.5.18.3.3.1.6.1", 2);
				setOid("6.5.18.3.3.1.7.1", 3);
							
				setOid("6.5.18.3.4.1.3.1.",  1, 16, 2);	
			}
			else {
				
				setOid("6.5.18.3.1.1.3.1",  2);	
				setOid("6.5.18.3.1.1.4.1",  2);	
				setOid("5.34.1.1.8.1", 1);			
				//!!!setOid("5.34.1.1.4.1", 1); 				// Scrambling Mode { deScrambled(1), scrambled (2)}
				
				setOid("6.5.18.3.3.1.3.1", 1);
				setOid("6.5.18.3.3.1.4.1", 1);
				setOid("6.5.18.3.3.1.6.1", 1);
				setOid("6.5.18.3.3.1.7.1", 1);
				        
				setOid("6.5.18.3.4.1.3.1.",  1, 16, 2);	// 1
			}
			
			break;
			
		case 2:	// MPEGoIP test case
			setOid("5.5.1.1.0", 6);	
			addComment("*** Beginning of MPEGoIP section ***");
			setOid("5.41.1.1.0", 1); // MPEGoIP Enabled 1(Yes), 2(No)
			setOid("5.41.1.2.0", Utility.ipToInt(Utility.getPropVal(devName, "MulticastIP")));
			setOid("5.41.1.3.0", 1);
			setOid("5.41.1.4.0", 1);			
			setOid("5.41.1.5.0", Utility.getPropValInt(devName, "MulticastPort"));
			setOid("5.41.1.6.0", Utility.getPropValInt(devName, "MulticastPort")+2);	
			setOid("5.41.1.7.0", Utility.getPropValInt(devName, "MulticastPort")+4);
			setOid("5.41.1.8.0", 1);	
			setOid("5.41.1.9.0", 2);
			setOid("5.41.1.10.0", 0x6E);
			setOid("5.41.1.11.0", 3);
			addComment("*** End of MPEGoIP section ***");
			break;
			
		case 1:	// RF test case
			int inpSelIdx;
			
			if((inpSelIdx = Utility.getPropValInt(devName + ".RF")) == Utility.BAD_INT) {
				inpSelIdx = 0;
			}
		
			setOid("5.5.1.1.0",     inpSelIdx+1);										// set RF[1-4]/ASI source (starts with 1)
			setOid("5.5.4.1.1.5.1", 1 );												// Input Select: Def(1), UserCfg(2)
			setOid("5.5.2.1.1.3.1", Utility.getPropValInt("Uplink.LO1Frequency")); 		// Frequency (LO1) in kHz
			setOid("5.5.2.1.1.4.1", (int)(Utility.getDoubleVal(s2)*10000));				// Symbol rate in kSym
			setOid("5.5.2.1.1.5.1", 0x0A ); 											//  Active Tuner DVBS FEC : Audio(0A)
			setOid("5.5.2.1.1.6.1", s1.indexOf("DVB-S2")<0?1:2); 						// Modulation DVB-S(1), DVB-S2(2)
			setOid("5.5.2.1.1.7.1", Utility.getPropValInt("Uplink.RollOff")); 			// Roll Off: 0.35(1), 0.25(2), 0.2(3)
			setOid("5.5.2.1.1.8.1", 3); 												// Tuner IQ: Opposite(1), Normal(2), Auto(3)
			setOid("5.5.2.2.1.8.1", 2); 												// Active Input Local OSC Control: Off(1), On(2), Auto(3)
			setOid("5.5.2.3.1.3.1", 1); 												// Input Power Control: Off(0), 13V(1), 18V(2), H-NIT(3), V-NIT(4)	
			setOid("5.5.2.2.1.5.1", Utility.getPropValInt("Uplink.OscFrequency1")); 	// Active input local OSC Freq #1
			if(!devName.equalsIgnoreCase("D9865")) {
				setOid("5.5.2.2.1.5.2", Utility.getPropValInt("Uplink.OscFrequency2")); 	// Active input local OSC Freq #2
				setOid("5.5.2.2.1.5.3", Utility.getPropValInt("Uplink.OscFrequency3")); 	// Active input local OSC Freq #3
				setOid("5.5.2.2.1.5.4", Utility.getPropValInt("Uplink.OscFrequency4")); 	// Active input local OSC Freq #4
			}
			setOid("5.5.4.1.1.2.1", 2); 												// SI Receive Acquisition Modulation Mode (RF Tune Mode): Basic(1), Auto(2)
			setOid("5.5.4.1.1.4.1", 1); 												// Network ID
			setOid("5.8.1.5.0",     1); 												// CAM Mode: Std(1), Open(2)
			setOid("5.5.4.1.1.6.1", 2); 												// Receive Frequency Select Option: NIT(1), UserCfg(2)
			setOid("5.5.4.1.1.7.1", 1); 												// Receive Service List Mode: Rigorous(1), Degradate(1)
			setOid("5.5.4.1.1.8.1", 1); 												// Receive Option BAT: Yes(2), No(1)
			setOid("5.5.4.1.1.9.1", 2); 												// Receive Option NIT: Yes(2), No(1)
			setOid("5.5.4.1.1.10.1", 2); 												// Receive Option SDT: Yes(2), No(1)
			setOid("5.5.4.1.1.11.1", 2); 												// Receive Option PAT: Yes(2), No(1)
			break;
			
		default:
			setOid("5.5.5.1.1.0", 1);	
			System.out.println("Unknow video source - " + rfSource + "Set ASI as a default ...");
			break;
		} // case(rfSouce)
		
		int activeChannelNumber = 0;
		
		if(!devName.equalsIgnoreCase("D9865"))  {
		
			activeChannelNumber = Math.min(Utility.getActChanNum(Utility.getDoubleVal(s3), Utility.getDoubleVal(s4)), maxChan);
		}
		else {
			
			activeChannelNumber = 1;
		}
		if(STS.verbose > 0) {
			System.out.println(" * Number of active channels : " + activeChannelNumber);
		}
		if(activeChannelNumber < 1) {
			System.out.println("\nTest case bandwidth is too high, please check your settings\n");
			activeChannelNumber = 1;
		}
		setOid("5.4.2.1.1.2.", 1, activeChannelNumber, channelNumber);				// set channel		
		
		if(!devName.equalsIgnoreCase("D9865"))
			addComment("*** End of RF section ***");
		
		return activeChannelNumber;
	}

	void createDownlinkBackupFile(String devName, String downstreamDevName, int downlinkStat)
	{
		final int MAX_DUMMY_OIDS = 256;
		
		StringBuffer buf = new StringBuffer();
		int downlinkChannelNum = 0;
		
		if(downlinkStat == 1) {
			downlinkChannelNum = channelNumber;
		}
		else {
			downlinkChannelNum = 1;
		}
		
		buf.append("<BKPRST>\n<FILE_INFO>\n<PLAT_TYPE>");
		if(downstreamDevName.equalsIgnoreCase("D9854I"))
			buf.append("D9854-I");	//	exclusion: add '-' before I
		else 
			buf.append(downstreamDevName);
		buf.append("</PLAT_TYPE>\n<DESIGNATION>");
		buf.append(Utility.getDestinationNumberByDeviceName(downstreamDevName));
		buf.append("</DESIGNATION>\n</FILE_INFO>\n<SETTINGS>\n");
		
		// CSCuq02316 - BKPRTS will not process file less than 4Kb !!!
		buf.append("<!--*** Start Dummy Section - CSCuq02316 ***-->\n");
		for(int i = 1; i <= MAX_DUMMY_OIDS; i++) {
			buf.append("<RECORD>\n\t<OID>1.3.6.1.2.1.1.4.0</OID>\n\t<VALUE>416-321-xxxx</VALUE>\n</RECORD>\n");	// dummy lines to enlarge BKP file to 4Kb
		}
		buf.append("<!--*** End Dummy Section - CSCuq02316 ***-->\n");
		
    	buf.append("<RECORD>\n\t<OID>" + SA_PREFIX + "5.5.1.1.0" + "</OID>\n\t<VALUE>01</VALUE>\n</RECORD>\n");	// ASI input
	
    	for(int i = 1; i <= 8; i++) {
    		buf.append("<RECORD>\n\t<OID>" + SA_PREFIX + "5.4.2.1.1.2." + i + "</OID>\n\t<VALUE>" + String.format("%04X", downlinkChannelNum) + "</VALUE>\n</RECORD>\n");	// ASI
    	}	
		buf.append("</SETTINGS>\n</BKPRST>\n");
		
		try {
			File dir = new File(STS.outputDirectory);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			
			FileOutputStream out = new FileOutputStream(STS.outputDirectory + "\\" + devName + "_Downlink_Config.bkp");
			out.write(buf.toString().getBytes());
			out.close();
		} catch(Exception e) {
			System.out.println("Can not write into file " + devName + "_Downlink_Config.bkp");
		}
		
	}

} // class SADevice


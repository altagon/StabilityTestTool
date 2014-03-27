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

	final public static String 	SA_PREFIX 		= "1.3.6.1.4.1.1429.2.2.5.";
	final public static int 	COMMENT_TAG 	= 999999;
	
	public String fileNameSuffix				= null;
	public Map<String,Integer> oids 			= new LinkedHashMap<String,Integer>(); 
	public int channelNumber					= 999;
	
	boolean isDecoder() 	{ return false; }
	boolean isTranscoder() 	{ return false; }
	

	SADevice(Date myday) 
	{
		   SimpleDateFormat timeStamp = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss");
		   Calendar cal = Calendar.getInstance();
		   
		   cal.setTime(myday);
		   fileNameSuffix = "_Stability_Config_" + timeStamp.format(cal.getTime()) + ".bkp";

	}
	
	void createBackupFile(String devName)
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<BKPRST>\n<FILE_INFO>\n<PLAT_TYPE>");
		buf.append(devName.replace('_', '-'));
		buf.append("</PLAT_TYPE>\n<DESIGNATION>14</DESIGNATION>\n</FILE_INFO>\n<SETTINGS>\n");
		
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
			File dir = new File(StabilitySetup.outputDirectory);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			
			FileOutputStream out = new FileOutputStream(StabilitySetup.outputDirectory + "\\" + devName + fileNameSuffix);
			out.write(buf.toString().getBytes());
			out.close();
		} catch(Exception e) {
			System.err.println("Can not write into file " + devName + fileNameSuffix);
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

	public void setOidFunc1(String shortOid, int startIdx, int endIdx)
	{
		for(int i = startIdx; i <= endIdx; i++) {
			oids.put(SADevice.SA_PREFIX + shortOid + i, calculatePid(i));

		} // i
	}	
	public void setOid(String shortOid, int startIdx, int endIdx, int value)
	{
		for(int i = startIdx; i <= endIdx; i++) {
			oids.put(SADevice.SA_PREFIX + shortOid + i,  value);
		} // i
	}	
	
	public void setOid(String shortOid, int value)
	{
		oids.put(SADevice.SA_PREFIX + shortOid,  value);
	}	

	private int calculatePid(int idx)
	{
		return 6000+idx;
	}

	public int indexOf(final String[] values, String s)
	{
		for(int i = 0; i < values.length; i++) {
			if(s.equalsIgnoreCase(values[i])) {
				return i+1;
			}
		} // i
		return 0;
	}

	void configRFParam(String devName, int inpTCNum)
	{
		ReadTestCaseParam inpTC = new ReadTestCaseParam("D9859", 4, 13, inpTCNum);
		ArrayList<String> videoTCParam = inpTC.getParam();

		//---System.out.println(videoTCParam);
		String s1 = videoTCParam.get(1);
		String s2 = videoTCParam.get(2);
		//---String s3 = videoTCParam.get(3);
		
		addComment("*** Beginning of RF section ***");
		
		final String[] inpSelStr = new String[] {"ASI", "RF1", "RF2", "RF3", "RF4", "MPEGoIP"};
		int inpSelIdx = -1;
		for(int i = 0; i < inpSelStr.length; i++)
			if(s1.indexOf(inpSelStr[i]) > 0 ) {
				inpSelIdx = i;
				break;
			}
		if(inpSelIdx < 0)
			inpSelIdx = Utility.getIntVal(StabilitySetup.properties.get("AciveRFInput"));
					
		setOid("5.1.1.0", inpSelIdx);
								
		setOid("5.4.1.1.5.1", 1 );																	// Input Select: Def(1), UserCfg(2)
		setOid("5.2.1.1.3.1", Utility.getIntVal(StabilitySetup.properties.get("LO1Frequency"))); 	// Frequency (LO1) in kHz
		setOid("5.2.1.1.4.1", Utility.getIntVal(s2)*10000);											// Symbol rate in kSym
		setOid("5.2.1.1.5.1", 0x0A ); 																//  Active Tuner DVBS FEC : Audio(0A)
		setOid("5.2.1.1.6.1", s1.indexOf("DVB-S2")<0?1:2); 											// Modulation DVB-S(1), DVB-S2(2)
		setOid("5.2.1.1.7.1", Utility.getIntVal(StabilitySetup.properties.get("RollOff")) ); 		// Roll Off: 0.35(1), 0.25(2), 0.2(3)
		setOid("5.2.1.1.8.1", 1); 																	// Tuner IQ: Opposite(1), Normal(2), Auto(3)
		setOid("5.2.2.1.8.1", 1); 																	// Active Input Local OSC Control: Off(1), On(2), Auto(3)
		setOid("5.2.3.1.3.1", 1); 																	// Inp Poower Control: Off(0), 13V(1), 18V(2), H-NIT(3), V-NIT(4)	
		setOid("5.2.2.1.5.1", Utility.getIntVal(StabilitySetup.properties.get("OscFrequency1"))); 	// Active input local OSC Freq #1
		setOid("5.2.2.1.5.2", Utility.getIntVal(StabilitySetup.properties.get("OscFrequency2"))); 	// Active input local OSC Freq #2
		setOid("5.2.2.1.5.3", Utility.getIntVal(StabilitySetup.properties.get("OscFrequency3"))); 	// Active input local OSC Freq #3
		setOid("5.2.2.1.5.4", Utility.getIntVal(StabilitySetup.properties.get("OscFrequency4"))); 	// Active input local OSC Freq #4
		setOid("5.4.1.1.2.1", 1); 																	// SI Receive Acquisition Modulation Mode: Std(1), Open(2)
		setOid("5.4.1.1.4.1", 1); 																	// Network ID
		setOid("5.8.1.5.0",   1); 																	// CAM Mode: Std(1), Open(2)
		setOid("5.4.1.1.6.1", 1); 																	// Receive Frequency Select Option: NIT(1), UserCfg(2)
		setOid("5.4.1.1.7.1", 1); 																	// Receive Service List Mode: Rigorous(1), Degradate(1)
		setOid("5.4.1.1.8.1", 1); 																	// Receive Option BAT: Yes(2), No(1)
		setOid("5.4.1.1.9.1", 1); 																	// Receive Option NIT: Yes(2), No(1)
		setOid("5.4.1.1.10.1", 1); 																	// Receive Option SDT: Yes(2), No(1)
		setOid("5.4.1.1.11.1", 1); 																	// Receive Option PAT: Yes(2), No(1)
		
		addComment("*** End of RF section ***");
	}


} // class SADevice


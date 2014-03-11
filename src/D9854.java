
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;



public class D9854 extends Decoder {
	
	final static int 	MAX_CHAN 		= 16;
	final static int 	DPM_PER_CHAN	= 12;
	final static String DEVICE_NAME 	= "D9854";
	
	private ArrayList<String>[] dpmParam = new ArrayList[2];
	private int chanNumber;
	private String fileName;
	
	private static Map<String,Integer> oids = new LinkedHashMap<String,Integer>(); 

	// Convenience method
	
		private void addComment(String comment)
		{
			oids.put(comment,  COMMENT_TAG);
		}
		
		private void setOidGroup1(String shortOid, int startIdx, int endIdx, int[] value, int grpSize)
		{
			for(int i = 0; i <= endIdx-startIdx; i++) {
				for(int j = 0; j < grpSize; j++)
					oids.put(SADevice.SA_PREFIX + shortOid + (startIdx+i*grpSize+j),  value[i]);
			} // i,j
		}	
		
		private void setOidGroup2(String shortOid, int startIdx, int endIdx, int[] value, int grpSize)
		{
			for(int i = 0; i <= endIdx-startIdx; i++)
				for(int j = 0; j < grpSize; j++) {
					oids.put(SADevice.SA_PREFIX + shortOid + (startIdx+i*grpSize+j),  value[j]);
				} // j,i
		}	

		private void setOid(String shortOid, int startIdx, int endIdx, int[] value)
		{
			for(int i = startIdx; i <= endIdx; i++) {
				oids.put(SADevice.SA_PREFIX + shortOid + i,  value[i-startIdx]);
			} // i
		}	

		private void setOidFunc1(String shortOid, int startIdx, int endIdx)
		{
			for(int i = startIdx; i <= endIdx; i++) {
				oids.put(SADevice.SA_PREFIX + shortOid + i, calculatePid(i));

			} // i
		}	
		private void setOid(String shortOid, int startIdx, int endIdx, int value)
		{
			for(int i = startIdx; i <= endIdx; i++) {
				oids.put(SADevice.SA_PREFIX + shortOid + i,  value);
			} // i
		}	
		
		private void setOid(String shortOid, int value)
		{
			oids.put(SADevice.SA_PREFIX + shortOid,  value);
		}	
		
		private int indexOf(final String[] values, String s)
		{
			for(int i = 0; i < values.length; i++) {
				if(s.equalsIgnoreCase(values[i])) {
					return i+1;
				}
			} // i
			return 0;
		}
		
		private int calculatePid(int idx)
		{
			return 6000+idx;
		}

	
	D9854(String fileName) 
	{
		super.setMaxTxcChan(MAX_CHAN);
		this.fileName = fileName;
	}
	
	void makeBackupFile(int channelNumber, int dpm1,  int dpm2)
	{
	   ReadTestCaseParam testCaseParam = new ReadTestCaseParam(dpm1, dpm2);
	   
	   dpmParam = testCaseParam.getDpmParam();
		
	   configDpmParam();
	   
	   createBackupFile();
	   
	}
	
	void configDpmParam()
	{
		final String[] peMapAction = new String[] {"Drop", "Pass", "Map", "Xcode"};
		final String[] mapMode= new String[] {"Svc ID & PID", "Svc ID"};
		final String[] duplicMode = new String[] {"PSI Remap", "Pkt Copy"};
		final String[] unref = new String[] {"Drop", "Pass", "Model"};
		final String[] serviceId = new String[] {"Valid Ch", "All Ch"};
		final String[] psiRate = new String[] {"SA Std", "MPEG Min", "Auto"};
		final String[] psiOption = new String[] {"Pass All", "Drop All", "Ctl by Table"};
		final String[] gblCfgDPR = new String[] {"Drop", "Pass", "Regen"};
		final String[] gblCfgDPRP = new String[] {"Drop", "Pass", "Regen", "PwRC"};
		
		
		setOid("4.2.1.1.2.", 1, MAX_CHAN, chanNumber);
		addComment("*** Start DPM Section ***");
		
		setOid("36.2.2.1.3.1.", 1, MAX_CHAN, indexOf(peMapAction, dpmParam[0].get(4-1).toString()));	// C4
		
		final int dpmPemappingOpChannel[] = new  int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
		setOid("36.2.2.1.5.1.", 1, MAX_CHAN, dpmPemappingOpChannel);
		
		// PMT PID 6000+shift
		setOidFunc1("36.2.2.1.4.1.", 1, MAX_CHAN);
	
		setOid("34.1.1.5.1", 2);		// RateControl {auto(1), user(2)}
		setOid("34.1.1.6.1", 68500000); // ASI UserRate 0..206000000 (default 68.5 Mb/sec)
		setOid("34.1.1.3.1", 7);		// Output Mode { noOutput(1), passThrough(2), serviceChannelOnly(3), mapPassthrough(4), mapserviceChannelOnly(5), fullDpmControl(6), transcoding(7) }
		setOid("34.1.1.4.1", 1); 		// Scrambling Mode { deScrambled(1), scrambled (2)}
		setOid("34.1.1.7.1", 2);		// Null Packet Insert { no(1), yes (2)}

		setOid("36.2.1.1.3.1",  indexOf(mapMode, dpmParam[0].get(8-1).toString()));		// C8
		setOid("36.2.1.1.4.1",  indexOf(duplicMode, dpmParam[0].get(9-1).toString())); 	// C9
		setOid("36.2.1.1.6.1",  indexOf(unref, dpmParam[0].get(10-1).toString()));		// C10
		setOid("36.2.1.1.8.1",  indexOf(serviceId, dpmParam[0].get(13-1).toString()));	// C13
		setOid("36.2.1.1.5.1",  indexOf(psiRate, dpmParam[0].get(12-1).toString()));	// C12
		setOid("36.2.1.1.7.1",  indexOf(psiOption, dpmParam[0].get(11-1).toString()));	// C11
		setOid("36.2.1.1.9.1",  indexOf(gblCfgDPR, dpmParam[0].get(14-1).toString()));	// C14
		setOid("36.2.1.1.10.1", indexOf(gblCfgDPR, dpmParam[0].get(15-1).toString()));	// C15
		setOid("36.2.1.1.11.1", indexOf(gblCfgDPR, dpmParam[0].get(16-1).toString()));	// C16
		setOid("36.2.1.1.12.1", indexOf(gblCfgDPR, dpmParam[0].get(17-1).toString()));	// C17
		setOid("36.2.1.1.13.1", indexOf(gblCfgDPR, dpmParam[0].get(18-1).toString()));	// C18
		setOid("36.2.1.1.14.1", indexOf(gblCfgDPRP, dpmParam[0].get(19-1).toString()));	// C19
		setOid("36.2.1.1.15.1", indexOf(gblCfgDPRP, dpmParam[0].get(20-1).toString()));	// C20
		setOid("36.2.1.1.16.1", indexOf(gblCfgDPRP, dpmParam[0].get(21-1).toString()));	// C21
		setOid("36.2.1.1.17.1", indexOf(gblCfgDPRP, dpmParam[0].get(22-1).toString()));	// C22
		setOid("36.2.1.1.18.1", indexOf(gblCfgDPR, dpmParam[0].get(23-1).toString()));	// C23
		setOid("36.2.1.1.19.1", indexOf(gblCfgDPR, dpmParam[0].get(24-1).toString()));	// C24
		setOid("36.2.1.1.21.1", indexOf(gblCfgDPR, dpmParam[0].get(25-1).toString()));	// C25
		setOid("36.2.1.1.22.1", indexOf(gblCfgDPR, dpmParam[0].get(26-1).toString()));	// C26
		setOid("36.2.1.1.23.1", indexOf(gblCfgDPR, dpmParam[0].get(27-1).toString()));	// C27
		setOid("36.2.1.1.24.1", indexOf(gblCfgDPR, dpmParam[0].get(28-1).toString()));	// C28
		setOid("36.2.1.1.25.1", indexOf(gblCfgDPR, dpmParam[0].get(29-1).toString()));	// C29
		setOid("36.2.1.1.26.1", indexOf(gblCfgDPR, dpmParam[0].get(30-1).toString()));	// C30
		setOid("36.2.1.1.27.1", indexOf(gblCfgDPR, dpmParam[0].get(31-1).toString()));	// C31
		setOid("36.2.1.1.28.1", indexOf(gblCfgDPR, dpmParam[0].get(32-1).toString()));	// C32
		
		// from: uic table dpm_pidmap
		int peGrpId[] = new  int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
		setOidGroup1("36.2.3.1.3.", 1,MAX_CHAN, peGrpId, DPM_PER_CHAN);		// 15*8 PE
		
		int peStremCat[] = new int[] {2, 3, 4, 4, 4, 4, 5, 5, 6, 0xA, 8, 8, 8, 8, 8, 99, 99, 99, 99, 99, 99, 99, 99};
		setOidGroup2("36.2.3.1.6.", 1, MAX_CHAN, peStremCat, DPM_PER_CHAN);	// 15*8 Stream Category
		
		int peSteamInst[] = new int[] {1, 1, 1, 2, 3, 4, 1, 2, 1, 1, 1, 2, 3, 4, 5, 99, 99, 99, 99, 99, 99, 99, 99};
		setOidGroup2("36.2.3.1.7.", 1, MAX_CHAN, peSteamInst, DPM_PER_CHAN);	// 15*8 Stream Instance
		
		int peAction[] = new int[] { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
		setOidGroup2("36.2.3.1.8.", 1, MAX_CHAN, peAction, DPM_PER_CHAN);		// 15*8 Action (Map/Drop)
		
		int pidPattern[] = new int[] { 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15 }; 
		int counter = 1;
		for(int i = 0; i < MAX_CHAN; i++)
		for(int j = 0; j < DPM_PER_CHAN; j++) {
			int pidNum = 6000 + pidPattern[j]*100 + i + 1;
			setOid("36.2.3.1.9."+counter, pidNum);
			counter++;
		} // j,i

		// always in use
		setOid("36.2.3.1.10.", 1, MAX_CHAN*DPM_PER_CHAN, 1);		// 15*8 PID in use {yes(1), no(2)}
		
		// always ASI
		setOid("36.2.3.1.2.", 1,MAX_CHAN*DPM_PER_CHAN, 1);			// 15*8 Output instance Id  {ASI(1), MPEGoIP(2)}
		
		addComment("*** End DPM Section ***");
	}
	
	void createBackupFile()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<BKPRST>\n<FILE_INFO>\n<PLAT_TYPE>");
		buf.append(DEVICE_NAME);
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
			FileOutputStream out = new FileOutputStream(fileName);
			out.write(buf.toString().getBytes());
			out.close();
		} catch(Exception e) {
			System.err.println("Can not write into file " + fileName);
		}
		
	}
	
}

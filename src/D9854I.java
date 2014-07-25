
/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   1.4
 *  Date:   	Mar 14, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	D9854I.java (clone D9854.java)
 *  
 *  Description: 
 *  	D9854 decoder backup configuration and generation class
 *  
 */


import java.util.Date;


public class D9854I extends Decoder {
	
	final static int 	MAX_CHAN 		= 8;
	final static int 	DPM_PER_CHAN	= 15;
	final static String DEVICE_NAME 	= "D9854I";
	final static int	DESIGNATION_NUM = 9;
		
	D9854I(Date myDay, ReadSchedule sched) 
	{
		super(myDay, sched);
	}	
	
	int makeBackupFile()
	{
	   ReadTestCaseParam testCaseParam = new ReadTestCaseParam(dpm, DEVICE_NAME);
	   
	   dpmParam = testCaseParam.getDpmParam();
	   
	   if(dpmParam.size() > 0) {
		   configRFParam();		
		   configDpmParam();
		   createBackupFile();
	   }
	   else {
		   System.out.println("D9854: Can not obtain DPM parameters. Please check your configuration!\n");
		   return 1;
	   }
	   
	   return 0;
	}
	
	void configDpmParam()
	{
		int downlinkStat = super.configDpmParam(MAX_CHAN, DPM_PER_CHAN);
		String downstreamDevice;
		
		if((downstreamDevice = STS.properties.get(DEVICE_NAME + ".DownstreamDeviceType")) != null) {
			super.createDownlinkBackupFile(DEVICE_NAME, downstreamDevice, downlinkStat);
		}
	}

	void configRFParam()
	{
		super.configRFParam(DEVICE_NAME, MAX_CHAN);
	}

	void createBackupFile()
	{
		super.createBackupFile(DEVICE_NAME, DESIGNATION_NUM);	
	}
	
}

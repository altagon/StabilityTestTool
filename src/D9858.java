/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   1.4
 *  Date:   	Mar 14, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	D9858.java
 *  
 *  Description: 
 *  	D9858 transcoder backup configuration and generation class
 *  
 */

import java.util.Date;


public class D9858 extends Transcoder {
	
	final static int 	MAX_CHAN 		= 2;
	final static int 	DPM_PER_CHAN	= 15;
	final static String DEVICE_NAME 	= "D9858";
	final static int	DESIGNATION_NUM = 11;
	
	
		
	D9858(Date myDay, ReadSchedule sched) 
	{
		super(myDay, sched, MAX_CHAN);
	}
	
	int makeBackupFile()
	{
	   ReadTestCaseParam testCaseParam = new ReadTestCaseParam(txc1, txc2, dpm, DEVICE_NAME);
	   
	   txcParam = testCaseParam.getTxcParam();
	   dpmParam = testCaseParam.getDpmParam();
		
	   if(txcParam[0].size() > 0 && txcParam[1].size() > 0 && dpmParam.size() > 0 )	{
		   int actChanNum = configRFParam();
		   configTxcParam(actChanNum);
		   configDpmParam();
		   createBackupFile();
	   }
	   else {
		   System.out.println("D9858: Can not obtain TXC or DPM parameters. Please check your configuration! " + txcParam[0].size() + "  "+ txcParam[1].size() + "  "+ dpmParam.size() + "\n");
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

	int configRFParam()
	{
		return super.configRFParam(DEVICE_NAME, MAX_CHAN);
	}


	void createBackupFile()
	{
		super.createBackupFile(DEVICE_NAME, DESIGNATION_NUM);		
	}
	
}

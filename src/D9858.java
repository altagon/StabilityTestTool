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
	
	void makeBackupFile()
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
		   System.err.println("D9858: Can not obtain DPM parameters. Please check your configuration!\n");
	   }
	  
	}
	
	void configDpmParam()
	{
		super.configDpmParam(MAX_CHAN, DPM_PER_CHAN);
	}

	int configRFParam()
	{
		return super.configRFParam(DEVICE_NAME);
	}


	void createBackupFile()
	{
		super.createBackupFile(DEVICE_NAME, DESIGNATION_NUM);		
	}
	
}

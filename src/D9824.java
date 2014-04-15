
/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   1.4
 *  Date:   	Mar 14, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	D9824.java (clone D9854.class)
 *  
 *  Description: 
 *  	D9854 decoder backup configuration and generation class
 *  
 */

import java.util.Date;


public class D9824 extends Decoder {
	
	final static int 	MAX_CHAN 		= 8;
	final static int 	DPM_PER_CHAN	= 15;
	final static String DEVICE_NAME 	= "D9824";
	final static int	DESIGNATION_NUM = 13;
				
	D9824(Date myDay, ReadSchedule sched) 
	{
		super(myDay, sched);
		
	}	
	
	void makeBackupFile()
	{
	   ReadTestCaseParam testCaseParam = new ReadTestCaseParam(dpm, DEVICE_NAME);
	   
	   dpmParam = testCaseParam.getDpmParam();
	   
	   if(dpmParam.size() > 0) {
		   configRFParam();		
		   configDpmParam();
		   createBackupFile();
	   }
	   else {
		   System.out.println("D9824: Can not obtain DPM parameters. Please check your configuration!\n");
	   }
	   
	}
	
	void configRFParam()
	{
		super.configRFParam(DEVICE_NAME);
	}

	void configDpmParam()
	{
		super.configDpmParam(MAX_CHAN, DPM_PER_CHAN);
	}


	void createBackupFile()
	{
		super.createBackupFile(DEVICE_NAME, DESIGNATION_NUM);	
	}

	
}

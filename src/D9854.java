/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   1.4
 *  Date:   	Mar 14, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	D9854.java
 *  
 *  Description: 
 *  	D9854 decoder backup configuration and generation class
 *  
 */

import java.util.Date;


public class D9854 extends Decoder {
	
	final static int 	MAX_CHAN 		= 8;
	final static int 	DPM_PER_CHAN	= 15;
	final static String DEVICE_NAME 	= "D9854";
	final static int	DESIGNATION_NUM = 9;
	
	private int dpm;
	
	D9854(Date myDay, ReadSchedule sched) 
	{
		super(myDay);
		
		this.channelNumber = sched.getChan();
		this.dpm  = sched.getDpm2();
		this.inp  = sched.getInput();
		this.rfSource = sched.getRfSource();

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
		   System.out.println("D9854: Can not obtain DPM parameters. Please check your configuration!\n");
	   }
	   
	}
	
	void configDpmParam()
	{
		super.configDpmParam(MAX_CHAN, DPM_PER_CHAN);
	}

	void configRFParam()
	{
		super.configRFParam(DEVICE_NAME);
	}

	void createBackupFile()
	{
		super.createBackupFile(DEVICE_NAME, DESIGNATION_NUM);	
	}

}

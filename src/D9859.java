/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   1.4
 *  Date:   	Mar 14, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	D9859.java
 *  
 *  Description: 
 *  	D9859 transcoder backup configuration and generation class
 *  
 */

import java.util.Date;

public class D9859 extends Transcoder {
	
	final static int 	MAX_CHAN 		= 8;
	final static int 	DPM_PER_CHAN	= 15;
	final static String DEVICE_NAME 	= "D9859";
	
	private int txc1;
	private int txc2;
	private int dpm;
	private int inp;
	
	
	D9859(Date myDay, int channelNumber, int txc1,  int txc2,  int dpm, int inp) 
	{
		super(myDay);
		super.setMaxTxcChan(MAX_CHAN);
		
		this.channelNumber = channelNumber;
		this.txc1 = txc1;
		this.txc2 = txc2;
		this.dpm  = dpm;
		this.inp  = inp;
		
	}
	
	void makeBackupFile()
	{
	   ReadTestCaseParam testCaseParam = new ReadTestCaseParam(txc1, txc2, dpm, DEVICE_NAME);
	   
	   txcParam = testCaseParam.getTxcParam();
	   dpmParam = testCaseParam.getDpmParam();
	   if(txcParam[0].size() > 0 && txcParam[1].size() > 0 && dpmParam.size() > 0 )	{
		   configRFParam();
		   configTxcParam();
		   configDpmParam();
		   createBackupFile();
	   }
	   else {
		   System.out.println("D9859: Can not obtain DPM parameters. Please check your configuration!\n");
	   }
	}
	
	void configDpmParam()
	{
		super.configDpmParam(MAX_CHAN, DPM_PER_CHAN);
	}
	
	void configRFParam()
	{
		super.configRFParam(DEVICE_NAME, inp);
	}

	void createBackupFile()
	{
		super.createBackupFile(DEVICE_NAME);
	}
	
}

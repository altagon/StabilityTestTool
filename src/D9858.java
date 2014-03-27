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
	
	private int txc1;
	private int txc2;
	private int dpm;
	private int inp;
	
		
	D9858(Date myDay, int channelNumber, int txc1,  int txc2,  int dpm) 
	{
		super(myDay);
		super.setMaxTxcChan(MAX_CHAN);
		
		this.channelNumber = channelNumber;
		this.txc1 = txc1;
		this.txc2 = txc2;
		this.dpm  = dpm;
		
	}
	
	void makeBackupFile()
	{
	   ReadTestCaseParam testCaseParam = new ReadTestCaseParam(txc1, txc2, dpm, DEVICE_NAME);
	   
	   txcParam = testCaseParam.getTxcParam();
	   dpmParam = testCaseParam.getDpmParam();
		
	   if(txcParam[0].size() > 0 && txcParam[1].size() > 0 && dpmParam.size() > 0 )	{
		   configTxcParam();
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

	void configRFParam()
	{
		super.configRFParam(DEVICE_NAME, inp);
	}


	void createBackupFile()
	{
		super.createBackupFile(DEVICE_NAME);	
	}
	
}

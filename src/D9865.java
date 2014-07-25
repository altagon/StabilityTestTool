
/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   0.1 (###AG : D9854 clone!!!!)
 *  Date:   	20 June, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	D9854.java
 *  
 *  Description: 
 *  	D9865 decoder backup configuration and generation class
 *  
 */

import java.util.Date;


public class D9865 extends Decoder {
	
	final static int 	MAX_CHAN 		= 8;
	final static int 	DPM_PER_CHAN	= 15;
	final static String DEVICE_NAME 	= "D9865";
	final static int	DESIGNATION_NUM = 12;
		
	D9865(Date myDay, ReadSchedule sched) 
	{
		super(myDay, sched);
	}	
	
	int makeBackupFile()
	{
		configRFParam();	
		createBackupFile(); 
		
		return 0;
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

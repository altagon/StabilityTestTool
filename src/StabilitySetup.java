/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   1.4
 *  Date:   	Mar 14, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	StabilitySetup.java
 *  
 *  Description: 
 *  	Starter class for stability setup backup file generator. 
 *  	Reads desire test schedule date and activates backup generation for specified devices.
 *  	Uses command line arguments or content 'config.properies' file   
 *  
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class StabilitySetup {
	
	private  static final 	String copyRight	= "\n(c) Cisco Systems 2014 by A.Godin $Build 1.1.0  Apr 2 2014";
	private  static String 	propertyFileName	= "sts.properties";
	public  static boolean 	verbose				= false;
	public  static String 	outputDirectory		= ".";	
	
	public static  Map<String,String> properties = new HashMap<String, String>();
	
	private int    channelNumber   = -1;
	private Date   myDay 		   = new Date();
	private String[] propName = new String[] {
		
	    "Uplink.LO1Frequency", "Uplink.RollOff", "Uplink.OscFrequency1", "Uplink.OscFrequency2", "Uplink.OscFrequency3", "Uplink.OscFrequency4",
	    
		"D9824.channel",	"D9824.schedule", 	"D9824.schedule.StartLine", 	"D9824.scoreboard", 	"D9824.scoreboard.StartLineDpm", 										"D9824.RF",
	
		"D9854.channel", 	"D9854.schedule", 	"D9854.schedule.StartLine", 	"D9854.scoreboard", 	"D9854.scoreboard.StartLineDpm", 										"D9854.RF",
		
		"D9854_I.channel", 	"D9854_I.schedule", "D9854_.schedule.StartLine", 	"D9854_I.scoreboard", 	"D9854_I.scoreboard.StartLineDpm", 										"D9854_I.RF",
	
		"D9858.channel", 	"D9858.schedule", 	"D9858.schedule.StartLine", 	"D9858.scoreboard", 	"D9858.scoreboard.StartLineDpm", 	"D9858.scoreboard.StartLineTxc", 	"D9858.RF",
		
		"D9859.channel", 	"D9859.schedule", 	"D9859.schedule.StartLine", 	"D9859.scoreboard", 	"D9859.scoreboard.StartLineDpm", 	"D9859.scoreboard.StartLineTxc", 	"D9859.RF"
			
	};
	private String[] propDefaultVal = new String[] {
			
		"12030000", "1", "12030000", "12030000", "12030000", "12030000",
		"101", "StabilityTests Schedulle", "2", "StabilityTestPlan.xlsx", "StabiliyTests_Grid_Scoreboard.xlsm", "9", "1",
		"101", "StabilityTests Schedulle", "2", "StabiliyTests_Grid_Scoreboard.xlsm", "9", "1", 
		"101", "StabilityTests Schedulle", "2", "StabiliyTests_Grid_Scoreboard.xlsm", "9", "1",
		"101", "StabilityTests Schedulle", "2", "StabiliyTests_Grid_Scoreboard.xlsm", "9", "10", "1",
		"101", "StabilityTests Schedulle", "2", "StabiliyTests_Grid_Scoreboard.xlsm", "9", "10", "1"
	};
	private int rfSource;

	
	void printHelp()
	{
		System.out.println(copyRight);
		System.err.println("\nUsage: StabilitySetup  [-h] [-v] [-o output_directory] [-p property_file_name] [-c active_channel_number] [-d DD-MMM-YYYY]\n");
		System.err.println("\t -h or ?\t - show this help");
		System.err.println("\t -v\t\t - verbose output (default NO)");
		System.err.println("\t -o\t\t - output diectory (default - current one)");
		System.err.println("\t -p\t\t - property file (default - sts.properties)");
		System.err.println("\t -c\t\t - channel number (default - from property file");
		System.err.println("\t -d\t\t - setup generation date (default - today)");
		
		System.err.println("\n");
	}
	
	private void readProperties()
	{
		Properties prop = new Properties();
		InputStream input = null;
	 
		try { 
			input = new FileInputStream(propertyFileName);
	 
			// load a properties file
			prop.load(input);
			
			for(int i=0; i < propName.length; i++) {
				String key = propName[i];
				String val = prop.getProperty(propName[i]);
				
				if(val == null) {
					// property definition is missing - replaced it with a default value
					val = propDefaultVal[i];
				}
				if(properties.get(key) == null)
					properties.put(key, val );
				if(verbose) {
					System.out.println("* " + key + " = " + val);
				}
				
			} // i
			System.out.println();
			
//			if(channelNumber < 0)
//				channelNumber = Utility.getIntVal(prop.getProperty("D9859.channel"));

		} catch (IOException ex) {
			System.err.println("Can not open property file '" + propertyFileName + "' ... Using default paramenters!\n\n");
			for(int i=0; i < propName.length; i++) {
				String key = propName[i];
				String val = propDefaultVal[i];
				properties.put(key, val );
				if(verbose) {
					System.out.println("* " + key + " = " + val);
				}
				
			} // i
			System.out.println();

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	int parseCmdLine(String[] args) 
	{
		for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            
            if(arg.startsWith("-") && i < args.length) {
            	// process parameter(s)
            	
            	switch(arg.charAt(1)) {
            	case 'c':
            	case 'C':
            		int cn = Utility.getIntVal(args[++i]);
            		if(cn != Utility.BAD_INT)
            			channelNumber = cn;
            		break;
            		
            	case 'd':
            	case 'D':
            		try {
            			Date d = new SimpleDateFormat("dd-MMM-yyyy").parse(args[++i]);
    					myDay = d;
            		}
            		catch(ParseException e) {}
            		
            		break;
            		
            	case 'v':
            	case 'V':
            		verbose = true;
            		break;
            		
            	case 'p':
            	case 'P':
            		propertyFileName = args[++i];
            		break;
            		
            	case 'o':
            	case 'O':
            		outputDirectory = args[++i];
            		break;
            		
            	case 'h':
            	case 'H':
            		printHelp();
            		return 1;

            	default:
            		System.err.println("Err: unknown keyword '" + arg + "' - ignored");
         
            	} // switch()
            }
            else if(arg.equals("?")) {
            	printHelp();
            	return 1;
            }
             
		} // i
		
		return 0;
	}
	
	StabilitySetup(String[] args)
	{
		if(parseCmdLine(args) != 0)
			System.exit(1);
		
		readProperties();

	}
	
	void makeIt()
	{	
	   ReadSchedule sched = new ReadSchedule(myDay);
//	   if(!sched.isFound()) {
//		   System.err.println("Error: fail to find required day into test schedulle! Did you test schedule over?\n");
//		   System.exit(1);
//	   }
	   
	   if(channelNumber < 0)
		   channelNumber = sched.getChan();
	   rfSource = sched.getRfSource();
	   System.out.println("* Active channel : " + channelNumber + "  RF Source : " +  (rfSource==0?"ASI":"RF") + "\n");
	   
	   
	   // uplink setup
	   System.out.println("** Generating Uplink Setup Work Order ...");
	   Uplink uplink  = new Uplink(myDay, sched);
	   uplink.makeConfig();
       
		System.out.println("** Generating D9824 Stability config ...");
		D9824 d9824 = new D9824(myDay, sched); 
		d9824.makeBackupFile();
   
	   System.out.println("** Generating D9854 Stability config ...");
	   D9854 d9854 = new D9854(myDay, sched); 
	   d9854.makeBackupFile();
	  
		System.out.println("** Generating D9854i Stability config ...");
		D9854I d9854i = new D9854I(myDay, sched);  
		d9854i.makeBackupFile();

	   System.out.println("** Generating D9858 Stability config ...");
	   D9858 d9858 = new D9858(myDay, sched);   
	   d9858.makeBackupFile();
	   
	   System.out.println("** Generating D9859 Stability config ...");
	   D9859 d9859 = new D9859(myDay, sched);  
	   d9859.makeBackupFile();
	     
	   System.out.println("\n***  Stability configurations are completed  ***\n");
		   
	} // StabilityTest


	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{		
		StabilitySetup sts = new StabilitySetup(args);
		
		sts.makeIt();
		
	} // main
		     
}

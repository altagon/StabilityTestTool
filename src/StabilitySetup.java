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
	
	private  static final 		String copyRight	= "\n(c) Cisco Systems 2014 by A.Godin $Build 1.1.3  Apr 14 2014";
	private  static String 		propertyFileName	= "sts.properties";
	public   static boolean 	verbose				= false;
	public   static String 		outputDirectory		= ".";	
	
	public static  Map<String,String> properties = new HashMap<String, String>(); 
	
	private int    channelNumber   = -1;
	private Date   myDay 		   = new Date();
	private int rfSource;

	
	void printHelp()
	{
		System.out.println(copyRight);
		System.err.println("\nUsage: StabilitySetup  [-h] [-v] [-o output_directory] [-p property_file_name] [-c active_channel_number] [-d DD-MMM-YYYY]\n");
		System.err.println("\t -h or ?\t - show this help");
		System.err.println("\t -v\t\t - verbose output (default NOT VERBOSE)");
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

			for(Object propName : prop.keySet()) {
				String key = propName.toString();
				String val =prop.getProperty(key);
				
				if(verbose) 
					System.out.println("* " + key + " = " + val);
				
				properties.put(key, val);
			} // propName
			
		} catch (IOException ex) {
			System.err.println("Can not open property file '" + propertyFileName );
			System.exit(1);

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	} // readProperties
	
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
	   
	   if(channelNumber < 0) {
		   channelNumber = sched.getChan();
	   }

	   rfSource = sched.getRfSource();
	   System.out.println("* Active channel : " + channelNumber + "  RF Source : " +  (rfSource==0?"ASI":(rfSource==1?"RF":"MPEGoIP")) + "\n");
	   
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

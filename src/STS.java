/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   1.4
 *  Date:   	Mar 14, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	STS.java
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


public class STS {
	
	private static final String copyRight	= "\n(c) Cisco Systems 2014 by algodin Version: 1.2.1  Build: July 22 2014\n";
	
	private static final String fullDevList[] = {"D9824", "D9854", "D9854i", "D9858" ,"D9859", "D9865", "@UPLINK"};

	private  static String 		propertyFileName	= "sts.properties";
	public   static int 		verbose				= 0;
	public   static String 		outputDirectory		= ".";	
	
	public static  Map<String,String> properties = new HashMap<String, String>(); 
	
	private int    channelNumber   = -1;
	private Date   myDay 		   = new Date();
	private int rfSource;

	
	private void printHelp()   
	{
		System.err.println(copyRight);
		System.err.println("Usage: STS  [-h] [-v] [-o output_directory] [-p property_file_name] [-c active_channel_number] [-d DD-MMM-YYYY]\n");
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
				
				if(verbose > 0) 
					System.out.println("* " + key + " = " + val);
				
				properties.put(key, val);
			} // propName
			
			System.out.println();
			
		} catch (IOException ex) {
			System.out.println("Can not open property file '" + propertyFileName );
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
	
	private int parseCmdLine(String[] args) 
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
            		verbose = 1;
            		if(arg.length() > 2 && (arg.charAt(2) == 'v' || arg.charAt(2) == 'V' ))
            			verbose = 2;
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
            		System.out.println("Error: unknown keyword '" + arg + "' - ignored");
         
            	} // switch()
            }
            else if(arg.equals("?")) {
            	printHelp();
            	return 1;
            }
             
		} // i
		
		return 0;
	}
	
	
	//
	//	Constructor
	//
	STS(String[] args)
	{
		if(parseCmdLine(args) != 0)
			System.exit(1);
		
		readProperties();

	}
	
	private void makeIt()
	{	
	   String devices   = null;
	   String devList[] = null;
	   
	   ReadSchedule sched = new ReadSchedule(myDay);
	   
	   if(channelNumber < 0) {
		   channelNumber = sched.getChan();
	   }   

	   rfSource = sched.getRfSource();
	   System.out.println("* Active channel : " + channelNumber + "  RF Source : " +  Utility.inputToString(rfSource) + "\n");
	   
	   if((devices = properties.get("Devices")) == null) {
		   
		   devList = new String[fullDevList.length];
		   for(int i = 0; i < fullDevList.length; i++ )
			   devList[i] = new String(fullDevList[i]);
	   }
	   else {
		   devList = devices.split(",");
	   }
	   
	   int cc = -1;
	   
	   for(String s : devList) {
		   
		   System.out.println("** Generating " + s + " Stability config ...");
		   
		   if(s.equalsIgnoreCase(fullDevList[0])) {			// D9824
			   
				D9824 d9824 = new D9824(myDay, sched); 
				cc = d9824.makeBackupFile();
		   }
		   else if(s.equalsIgnoreCase(fullDevList[1])) {	// D9854
			   
			   D9854 d9854 = new D9854(myDay, sched); 
			   cc = d9854.makeBackupFile();
		   }
		   else if(s.equalsIgnoreCase(fullDevList[2])) {	// D9854i
			   
				D9854I d9854i = new D9854I(myDay, sched);  
				cc = d9854i.makeBackupFile();
		   }
		   else if(s.equalsIgnoreCase(fullDevList[3])) {	// D9858
			   
			   D9858 d9858 = new D9858(myDay, sched);   
			   cc = d9858.makeBackupFile();
		   }
		   else if(s.equalsIgnoreCase(fullDevList[4])) {	// D9859
			   
			   D9859 d9859 = new D9859(myDay, sched);  
			   cc = d9859.makeBackupFile();
		   }
		   else if(s.equalsIgnoreCase(fullDevList[5])) {	// D9865
			
			   D9865 d9865 = new D9865(myDay, sched);  
			   cc = d9865.makeBackupFile();
			   
			   if(rfSource == 0) {
					System.out.println("\n******  Warning: D9865 does not support ASI input. Program RF1 instead!");
			   }
		   }
		   else if(s.equalsIgnoreCase(fullDevList[6])) {	// UPLINK
			   
			   System.out.println("** Generating Uplink Setup Work Order ...");
			   Uplink uplink  = new Uplink(myDay, sched);
			   uplink.makeConfig();
		   }
		   else {
			   System.out.println("\n******  Unknow device '" + s +"'  - ignored!!!\n");
		   }
		   
		   if(cc != 0) { // Config generation error detected
			   break;
		   }
		   
	   } // s
	   	     
	   if(cc == 0) {
		   System.out.println("\n***  Stability configurations are completed  ***\n");
		   System.exit(0);
	   }
	   else {
		   System.out.println("\n---  Stability configurations FAILED! Please check your Test Plan setting and repeat this step! ---\n");
		   System.exit(1);
	   }
		   
	} // StabilityTest


	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{		
		System.out.println(copyRight);
		
		STS sts = new STS(args);
		
		sts.makeIt();
		
	} // main
		     
}

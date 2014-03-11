import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


public class StabilitySetup {
	
	private final static String PROPERTY = "config.properties";
	
	public static String d9859scoreBoard =  "\\Users\\algodin\\Documents\\Stability Test\\D9859-R1.10 Stab Tests_Grid_Scoreboard.xlsm";
	public static String scheduleFile    = "\\Users\\algodin\\Documents\\Stability Test\\D9859 R1.10 Stability Test Plan J1-J27.xlsx";
	public static int    channelNumber   = 100;
	
	public static boolean debug = true;

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Date myDay = new Date();	// default date is "TODAY"
		
		switch(args.length) {
		
		case 0: // no arguments - all parameters from config.properties
			break;
		case 1: // one argument - expect: integer channel#
			try {
				channelNumber = Integer.parseInt(args[0]);
			}
			catch(Exception e) {
				System.out.println("Unparsable 1st argument");
				System.out.println("Usage: StabilitySetup [channel#] [DD-MMM-YYYY|today]\n\n");
				System.exit(1);
			}
			break;
		case 2:  // 2 arguments expect: integer channel# and date DD-MM-YYYY or word 'today'
		default: // 3rd and fathers arguments are ignored
			try {
				channelNumber = Integer.parseInt(args[0]);
			}
			catch(Exception e) {
				System.out.println("Unparsable 1st argument");
				System.out.println("Usage: StabilitySetup [channel#] [DD-MMM-YYYY|today]\n\n");
				System.exit(1);
			}
			try {
				myDay = new SimpleDateFormat("dd-MMM-yyyy").parse(args[1]);
			}
			catch(Exception e) {
				System.out.println("Unparsable 2nd argument");
				System.out.println("Usage: StabilitySetup [channel#] [DD-MMM-YYYY|today]\n\n");
				System.exit(1);
			}			
			break;
		}
		   
//		if(debug) {
//			myDay.setMonth(1);	// Feb
//			myDay.setDate(25);	// 25th
//		}
		
		Properties prop = new Properties();
		InputStream input = null;
	 
		try { 
			input = new FileInputStream(PROPERTY);
	 
			// load a properties file
			prop.load(input);
	 
			// get the property value and print it out
			scheduleFile = prop.getProperty("scheduleFile");
			d9859scoreBoard = prop.getProperty("scoreBoardD9859");
			try {
				channelNumber = Integer.parseInt(prop.getProperty("channel"));
			} catch(Exception e) {}
			
			System.out.println(prop.getProperty("scheduleFile"));
			System.out.println(prop.getProperty("scoreBoardD9859"));
	
	 
		} catch (IOException ex) {
			System.out.println("Can not open 'config.properties' file. Using command line or default paramenters ...\n\n");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		     
	   ReadSchedule sched = new ReadSchedule(myDay);
       
	   if(debug) {
		   System.out.println("Transcode Case #1 = " + sched.getTxc1());	
		   System.out.println("Transcode Case #2 = " + sched.getTxc2());	
		   
		   System.out.println("DPM Case #1 = " + sched.getDpm1());			
		   System.out.println("DPM Case #2 = " + sched.getDpm2());			
	   }
	   
	   D9859 d9859 = new D9859("D9859_Mar10_BACKUP.bkp");
	   
	   d9859.makeBackupFile(channelNumber, sched.getTxc1(), sched.getTxc2(), sched.getDpm1(), sched.getDpm2());
	     
	   System.out.println("\n***  STABILITY BACKUP FILE PREPARATION IS OVER ***\n");
		   
	}
}

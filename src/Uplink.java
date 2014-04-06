

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Uplink {
	  
	private Date myDay;
	private int channelNumber;
	private ArrayList<String> param;
	private StringBuffer buf = new StringBuffer();
	private int rfSource;


	private void printTCParam(int tabNum, int startLine, int tcNum, String[] tcParamName)
	{
		ReadTestCaseParam inpTC = new ReadTestCaseParam("D9859", tabNum, startLine, tcNum);
		ArrayList<String> tcParamVal = inpTC.getParam();
		int iPrn = 1;
		
		for(int i = 0; i < Math.min(tcParamName.length, tcParamVal.size()) ; i++ ) {
			if(tcParamName[i].charAt(0) != '#')	// ignore "commented" parameters (their names started with '#')
				buf.append("\t"+ (iPrn++) + ". " +tcParamName[i] + " = " +  tcParamVal.get(i).trim().replaceAll("\n",  ";") + "\n");
		} // i

	}
	
	Uplink(Date myDay, ReadSchedule sched)
	{
		this.myDay = myDay;
		this.channelNumber = sched.getChan();
		this.param = sched.getParam();
		this.rfSource = sched.getRfSource();
	}
	

	void makeConfig()
	{
		// basic version - just print a work order
				
		buf.append("\t\tUplink Setup Work order for " + myDay.toString() + "  Rel 0.2\n");
		buf.append("\t\t-----------------------------------------------------------------\n\n");
		
		buf.append("\n1. Channel : " + channelNumber + " RF Source : " + (rfSource==0?"ASI":"RF") + "\n");
		buf.append("\n2. Physical Connection : " + param.get(16) + "\n");	
		
		
		buf.append("\n3. Input Test Case : " + Utility.getIntVal(param.get(1)) + "\n\n");
		String[] inpTCParamName = new String[] {"TC Name", "Format/Modulation", "Rate (Msym)", "Bandwidth (Mbit)"};
		printTCParam(4, 13, Utility.getIntVal(param.get(1)), inpTCParamName);
		
		buf.append("\n4. CA Test Case : " + Utility.getIntVal(param.get(2)) + "\n\n");
		String[] caTCParamName = new String[] {"Scrambling Mode", "#Signal Source", "#DCD", "#Algorithm", "MSK", "Channel Setup"};
		printTCParam(5, 10, Utility.getIntVal(param.get(2)), caTCParamName);

		buf.append("\n5. Video Test Case : " + Utility.getIntVal(param.get(3)) + "\n\n");
		String[] vidTCParamName = new String[] {"Video Format", "Picture (resolution)", "Profile/Feature", "GOP Settings", "3:2 PullDown", 
				                				"Video Bit Rate", "Video Rate", "#Encoder Parameters"};
		printTCParam(7, 8, Utility.getIntVal(param.get(3)), vidTCParamName);
	
		buf.append("\n6. Audio#1 Test Case : " + Utility.getIntVal(param.get(4)) + "\n\n");
		String[] audTCParamName = new String[] {"TC Name", "#Audio Type", "#Bit Rates", "#Encoder Flags #1", "#Encoder Flags #2",
                "#Encoder 1:Stereo", "#Encoder 2:Dual Mono", "#Encoder 3:Single Mono", 
                "#Encoder 4:3/2 Surround & 5.1", "#PCM/Compress", "#DDP Mode", "#1:Stereo", 
                "#2:Dual Mono", "#3:Single Mono", "#4:3/2 Surround & 5.1"};
		printTCParam(6, 10, Utility.getIntVal(param.get(4)), audTCParamName);
	
		buf.append("\n7. Audio#2 Test Case :" + Utility.getIntVal(param.get(5)) + "\n\n");
		printTCParam(6, 10, Utility.getIntVal(param.get(4)), audTCParamName);

		buf.append("\n8. Trancode#1 Test Case : " + Utility.getIntVal(param.get(6)) + "  subject of automatic setup\n");
		
		buf.append("\n9. Trancode#2 Test Case : " + Utility.getIntVal(param.get(7)) + "  subject of automatic setup\n");	
		
		buf.append("\n10. Subtitle Test Case : " + Utility.getIntVal(param.get(8)) + "\n\n");
		String[] sbtTCParamName = new String[] {"TC Name", "Video Std", "Subtitle Std", "#Stream/Subtitle enc"};
		printTCParam(8, 10, Utility.getIntVal(param.get(8)), sbtTCParamName);
	
		buf.append("\n11. VBI Test Case : " + Utility.getIntVal(param.get(9)) + "\n\n");
		String[] vbiTCParamName = new String[] {"TC Name", "#Video Frame Rate", "#NABTS", "#WST", "#Invert WST", "#VITC PAL(1)",
												"#AMOL-I(3)", "#AMOL-II(3)", "#VPS", "#WSS", "#VITS", "#GEMTAR(1x)(2)", 
												"#GEMTAR(2x)(2)", "#Monochrome", "#VBItotal bit rate (KBits)"};
		printTCParam(9, 9, Utility.getIntVal(param.get(9)), vbiTCParamName);
		
		buf.append("\n12. LSD Test Case : " + Utility.getIntVal(param.get(10)) + "\n\n");
		String[] lsdTCParamName = new String[] {"TC Name", "#Data rate"};
		printTCParam(10, 9, Utility.getIntVal(param.get(10)), lsdTCParamName);
	
		buf.append("\n13. MPE Test Case : " + Utility.getIntVal(param.get(11)) + "\n\n");
		String[] mpeTCParamName = new String[] {"TC Name", "#PIDs", "Rate per PID (Mbits)", "Aggregate Rate (Mbits)", "Frame Size", "Flow Type"};
		printTCParam(11, 8, Utility.getIntVal(param.get(11)), mpeTCParamName);
		
		buf.append("\n14. FPT Test Case : " + (Utility.getIntVal(param.get(12))==1?"Yes":"No") + "\n");
		
		buf.append("\n15. CC Test Case : " + (Utility.getIntVal(param.get(13))==1?"Yes":"No") + "\n");	
		
		buf.append("\n16. DPM 58/59 Test Case : " + Utility.getIntVal(param.get(14)) + " subject of automatic setup\n");	
		
		buf.append("\n17. DPM 24/54 Test Case : " + Utility.getIntVal(param.get(15)) + " subject of automatic setup\n");	

		buf.append("\n\n***  End of Uplink Setup Work order  ***\n");
		
		try {
			   SimpleDateFormat timeStamp = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss");
			   Calendar cal = Calendar.getInstance();
			   
			   cal.setTime(myDay);
			   String fileName = "Uplink_Setup_" + timeStamp.format(cal.getTime()) + ".txt";

			FileOutputStream out = new FileOutputStream(fileName);
			out.write(buf.toString().getBytes());
			out.close();
		} catch(Exception e) {
			System.err.println("Can not write into file " + "Uplink_Setup_");
		}

		
	}
	
}

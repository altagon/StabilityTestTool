/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   1.4
 *  Date:   	Mar 14, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	ReadSchedule.java
 *  
 *  Description: This class reads test case setup info from MS Excel schedule table
 *  
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadSchedule {
	
	private ArrayList<String> schedParam = new ArrayList<String>();
	private String devName = "D9859";
	private Date today = new Date();
	private boolean bFound = false;
	
	public ArrayList<String> getParam() { return schedParam; }
	
	public boolean isFound() 	{ return bFound; }
	
	public int getChan()  		{ return Utility.getIntVal(schedParam.get(1)); }
	public int getVid()  		{ return Utility.getIntVal(schedParam.get(4)); }
	public int getMpe()  		{ return Utility.getIntVal(schedParam.get(10)); }
	public int getInput() 		{ return Utility.getIntVal(schedParam.get(2)); }
	public int getTxc1()  		{ return Utility.getIntVal(schedParam.get(13)); }
	public int getTxc2()  		{ return Utility.getIntVal(schedParam.get(14)); }
	public int getDpm1()  		{ return Utility.getIntVal(schedParam.get(15)); }
	public int getDpm2()  		{ return Utility.getIntVal(schedParam.get(16)); }
	public int getRfSource() 	
	{ 
		if(schedParam.get(20).equalsIgnoreCase("ASI"))
			return 0;
		else if(schedParam.get(20).equalsIgnoreCase("RF"))
			return 1;
		else	// MPEGoIP
			return 2;
	}
	
	public ReadSchedule(String devName, Date today)
    {	
		this.devName = devName;
		this.today = today;
		
		readIt();
    }
	
	public ReadSchedule(Date today)
    {	
		this.today = today;
		
		readIt();
    }

	public ReadSchedule()
    {	
		readIt();
    }

    private void readIt()
    {	
    	Calendar calToday  = Calendar.getInstance();
    	Calendar calCurDay = Calendar.getInstance();
    	calToday.setTime(today);
    	
        try {
        	FileInputStream scheduleStream = new FileInputStream(Utility.getPropVal(devName, "schedule"));
            //Get the workbook instance for XLS file
            XSSFWorkbook workBook = new XSSFWorkbook(scheduleStream);
            //Get first sheet from the workbook
            XSSFSheet sheet = workBook.getSheetAt(0);   
            int startLine = 1;
            
            try {
            	startLine = Integer.parseInt(Utility.getPropVal(devName, "schedule.StartLine"));
            } catch(Exception e) {
            	System.out.println("Error: can not convert property 'schedule.StartLine' into int");
            	System.exit(1);
            }
            
            for (int lineId = startLine; ; lineId++) {
                Row row = sheet.getRow(lineId);
                if(row == null) {
                	//--- System.out.println("****  END OF SCHEDULE  ***");
                	break;
                }

                try {
                	Cell curDate = row.getCell(1);
                	//System.out.println(curDate.toString());
                	if(curDate.toString().trim().equals("")) continue; // skip week-ends and others "cosmetic" empty lines
                	
                	Date curDay = new SimpleDateFormat("dd-MMM-yyyy").parse(curDate.toString());
                 	calCurDay.setTime(curDay);
                	
                	if(calCurDay.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR)) {
                		bFound = true;
                   		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				if(STS.verbose > 1 )
                					System.out.println((i-1) + " >" + c.toString() + "<");
                				schedParam.add(c.toString());
                			}
                			else 
                				break;
                		} // i 
                   		if(STS.verbose > 1 )
                   			System.out.println();
                		
                		if(STS.verbose > 0)
                			System.out.println("\n* Today's test #" + lineId + "  Date : " + curDate.toString() + "   " + 
                								getTxc1() + "  " + getTxc2() + "  " + getDpm1() + "  " +getDpm2());
                   	    break;
                	}
               	
                } catch (ParseException e) {
					System.out.println("!!!  wrong date format");
                }	
                catch (NullPointerException e) {
                	// empty schedule content
                    break;
                }
     
            } // lineId
            
            // required test date was not found
            if(!bFound) {
            	System.out.println("Schedulle Error: Required date was not found : " + today.toString() + "!!! Your schedule may be expired ...\n");
            	System.exit(1);
            }
            
        }
        catch (IOException e) {
            e.printStackTrace();                 
        } 
        
    }

	public boolean getAsiScramb() 
	{
		return !schedParam.get(22).trim().isEmpty();
	}

}

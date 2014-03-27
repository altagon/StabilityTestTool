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
	
	public ArrayList<String> getParam() { return schedParam; }
	
	public int getInput() { return Utility.getIntVal(schedParam.get(1)); }
	public int getTxc1()  { return Utility.getIntVal(schedParam.get(6)); }
	public int getTxc2()  { return Utility.getIntVal(schedParam.get(7)); }
	public int getDpm1()  { return Utility.getIntVal(schedParam.get(14)); }
	public int getDpm2()  { return Utility.getIntVal(schedParam.get(15)); }
	
		
    public ReadSchedule(Date today)
    {
    	Calendar calToday  = Calendar.getInstance();
    	Calendar calCurDay = Calendar.getInstance();
    	calToday.setTime(today);
    	
        try {
        	FileInputStream scheduleStream = new FileInputStream(StabilitySetup.properties.get("testSchedule"));
            //Get the workbook instance for XLS file
            XSSFWorkbook workBook = new XSSFWorkbook(scheduleStream);
            //Get first sheet from the workbook
            XSSFSheet sheet = workBook.getSheetAt(0);   
            int startLine = 1;
            
            try {
            	startLine = Integer.parseInt(StabilitySetup.properties.get("testScheduleStartLine"));
            } catch(Exception e) {}
            
            for (int lineId = startLine; ; lineId++) {
                Row row = sheet.getRow(lineId);
                if(row == null) {
                	//--- System.out.println("****  END OF SCHEDULE  ***");
                	break;
                }
                if(lineId == 0) {
                	// check headers
                	continue;
                }
                try {
                	Cell curDate = row.getCell(1);
                	//System.out.println(curDate.toString());
                	if(curDate.toString().trim().equals("")) continue; // skip week-ends and others "cosmetic" empty lines
                	
                	Date curDay = new SimpleDateFormat("dd-MMM-yyyy").parse(curDate.toString());
                 	calCurDay.setTime(curDay);
                	
                	if(calCurDay.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR)) {
                		
                   		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				//System.out.println((i-1) + " >" + c.toString() + "<");
                				schedParam.add(c.toString());
                			}
                			else 
                				break;
                		} // i 
                		//System.out.println();
                		
                		if(StabilitySetup.verbose)
                			System.out.println("\n* Today's test #" + lineId + "  Date : " + curDate.toString() + "   " + 
                								getTxc1() + "  " + getTxc2() + "  " + getDpm1() + "  " +getDpm2());
                   	    break;
                	}
               	
                } catch (ParseException e) {
					System.err.println("!!!  wrong date format");
                }	
                catch (NullPointerException e) {
                	// empty schedule content
                    break;
                }
     
            } // lineId
            
            // required test date was not found
            if(getDpm1() < 0 || getDpm2() < 0 || getTxc1() < 0 || getTxc2() < 0) {
            	System.out.println("!!! Required date was not found : " +today.toString() + "\n");
            	System.exit(1);
            }
            
        }
        catch (IOException e) {
            e.printStackTrace();                 
        } 
        
    }

}

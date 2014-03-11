import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadSchedule {
	
	private int txc1 = -1;
	private int txc2 = -1;
	private int dpm1 = -1;
	private int dpm2 = -1;
	
	public int getTxc1() { return txc1; }
	public int getTxc2() { return txc2; }
	public int getDpm1() { return dpm1; }	
	public int getDpm2() { return dpm2; }
	
	
    public ReadSchedule(Date today)
    {
        try {
        	FileInputStream scheduleStream = new FileInputStream(StabilitySetup.scheduleFile);
            //Get the workbook instance for XLS file
            XSSFWorkbook workBook = new XSSFWorkbook(scheduleStream);
            //Get first sheet from the workbook
            XSSFSheet sheet = workBook.getSheetAt(0);   
 
            for (int lineId=0; ; lineId++) {
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
                	if(curDate.toString().equals("")) continue; // skip week-ends
                	
                	Cell txc1C 	 = row.getCell(7);
                	Cell txc2C 	 = row.getCell(8);
                	Cell dpm1C 	 = row.getCell(15);
                	Cell dpm2C 	 = row.getCell(16);
                	
                   	Date d1 = (today==null) ? new Date() : today;
                	Date d2 = new SimpleDateFormat("dd-MMM-yyyy").parse(curDate.toString());
                    
                	Calendar c1 = Calendar.getInstance();
                	Calendar c2 = Calendar.getInstance();
                	
                	c1.setTime(d1);
                	c1.setTime(d2);
                	
                	if(c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
                		
                		try {
	                   		txc1 = Double.valueOf(txc1C.toString()).intValue();
	                   		txc2 = Double.valueOf(txc2C.toString()).intValue();
	                   		dpm1 = Double.valueOf(dpm1C.toString()).intValue();
	                   		dpm2 = Double.valueOf(dpm2C.toString()).intValue();   
                		} catch(Exception e) {}
                		
                		System.out.println("*** Today's tests are :");
                   	    System.out.println("   " + lineId + " : " + curDate.toString() + "  " + txc1C.toString() + "  " + txc2C.toString() + "  " + dpm1C.toString() + "  " +dpm2C.toString());
                   	    break;
                	}
               	
                } catch (ParseException e) {
					System.err.println("!!!  wrong date format !!!");
                }	
                catch (NullPointerException e) {
                	// empty schedule content
                    break;
				}
                
     
            } // lineId
            
        }
        catch (IOException e) {
            e.printStackTrace();                 
        } 
        
    }

}

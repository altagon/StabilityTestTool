import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ReadTestCaseParam {

	private static final int DPM_TAB = 12;
	private static final int TXC_TAB = 13;
	
	private ArrayList<String> txcParam[]= new ArrayList[2]; 
	private ArrayList<String> dpmParam[]= new ArrayList[2]; 
	
	// Convenience method
	private int getVal(Cell cell) {return Double.valueOf(cell.toString()).intValue(); }		
	
	
	ArrayList<String>[] getTxcParam() { return txcParam; }
	ArrayList<String>[] getDpmParam() { return dpmParam; }
	
	
	public ReadTestCaseParam(int dpm1, int dpm2)
    {
		for(int i = 0; i < 2; i++) {
			dpmParam[i] = new ArrayList<String>();
		} // i
		
        try {
        	FileInputStream scoreBoardStream = new FileInputStream(StabilitySetup.d9859scoreBoard);
            //Get the workbook instance for XLS file
            XSSFWorkbook workBook = new XSSFWorkbook(scoreBoardStream);
            
            //
            // Get DPM test case parameters 
            //
            XSSFSheet sheet = workBook.getSheetAt(DPM_TAB);   

            for (int rowId=10; ; rowId++) {
            	//--- System.out.println("Row : " + rowId);
                Row row = sheet.getRow(rowId);
                if(row == null) {
                	break;
                }
 
                try {
                	Cell caseId = row.getCell(0);
                	//--- System.out.println(">"+caseId.toString()+"<");
                	if(caseId.toString().equals("")) continue; 
                	
                	int id = getVal(caseId);
                	
                	if(id == dpm1) {
                		System.out.println("DPM1 = " + caseId.toString() + '\n');
                		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				System.out.println((i-1) + " >" + c.toString() + "<");
                				dpmParam[0].add(c.toString());
                				//---System.out.println("    " + dpmParam[0].size());
                				
                			}
                			else 
                				break;
                		}
                		System.out.println();
                		
                	}
                	if(id == dpm2) {
                		System.out.println("DPM2 = " + caseId.toString() + '\n');
                		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				System.out.println((i-1) + " >" + c.toString() + "<");
                				dpmParam[1].add(c.toString());
                			}
                			else 
                				break;
                		}
                		System.out.println();
                		
                	}
                	
                } catch (NullPointerException e) {
                	// empty schedule content
                    break;
				}

     
            } // rowId
              
        }
        catch (IOException e) {
            e.printStackTrace();                 
        } 
        
    }

	public ReadTestCaseParam(int txc1, int txc2, int dpm1, int dpm2)
    {
		for(int i = 0; i < 2; i++) {
			txcParam[i] = new ArrayList<String>();
			dpmParam[i] = new ArrayList<String>();
		} // i
		
        try {
        	FileInputStream scoreBoardStream = new FileInputStream(StabilitySetup.d9859scoreBoard);
            //Get the workbook instance for XLS file
            XSSFWorkbook workBook = new XSSFWorkbook(scoreBoardStream);
            
            //
            // Get transcode test case parameters 
            //
            XSSFSheet sheet = workBook.getSheetAt(TXC_TAB);   
 
            for (int rowId=10; ; rowId++) {
            	//--- System.out.println("Row : " + rowId);
                Row row = sheet.getRow(rowId);
                if(row == null) {
                	break;
                }
 
                try {
                	Cell caseId = row.getCell(0);
                	//--- System.out.println(">"+caseId.toString()+"<");
                	if(caseId.toString().equals("")) continue; 
                	
                	int id = getVal(caseId);
                	
                	if(id == txc1) {
                		System.out.println("TXC1 = " + caseId.toString() + '\n');
                		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				System.out.println((i-1) + " >" + c.toString() + "<");
                				txcParam[0].add(c.toString());
                			}
                			else 
                				break;
                		}
                		System.out.println();
                		
                	}
                	if(id == txc2) {
                		System.out.println("TXC2 = " + caseId.toString() + '\n');
                		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				System.out.println((i-1) + " >" + c.toString() + "<");
                				txcParam[1].add(c.toString());
                			}
                			else 
                				break;
                		}
                		System.out.println();
                		
                	}
                	
                } catch (NullPointerException e) {
                	// empty schedule content
                    break;
				}

            } // rowId
            
            //
            // Get DPM test case parameters 
            //
            sheet = workBook.getSheetAt(DPM_TAB);   

            for (int rowId=10; ; rowId++) {
            	//--- System.out.println("Row : " + rowId);
                Row row = sheet.getRow(rowId);
                if(row == null) {
                	break;
                }
 
                try {
                	Cell caseId = row.getCell(0);
                	//--- System.out.println(">"+caseId.toString()+"<");
                	if(caseId.toString().equals("")) continue; 
                	
                	int id = getVal(caseId);
                	
                	if(id == dpm1) {
                		System.out.println("DPM1 = " + caseId.toString() + '\n');
                		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				System.out.println((i-1) + " >" + c.toString() + "<");
                				dpmParam[0].add(c.toString());
                				//---System.out.println("    " + dpmParam[0].size());
                				
                			}
                			else 
                				break;
                		}
                		System.out.println();
                		
                	}
                	if(id == dpm2) {
                		System.out.println("DPM2 = " + caseId.toString() + '\n');
                		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				System.out.println((i-1) + " >" + c.toString() + "<");
                				dpmParam[1].add(c.toString());
                			}
                			else 
                				break;
                		}
                		System.out.println();
                		
                	}
                	
                } catch (NullPointerException e) {
                	// empty schedule content
                    break;
				}

     
            } // rowId
              
        }
        catch (IOException e) {
            e.printStackTrace();                 
        } 
        
    }

}

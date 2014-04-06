/* Copyright (c) Cisco Systems, Inc., 2013-2014. All rights reserved.
 *
 *	
 *  Revision:   1.4
 *  Date:   	Mar 14, 2014  
 *  Author:   	algodin
 *  
 *  Workfile:	ReadTestcaseParam.java
 *  
 *  Description: 
 *  	This class reads test cases parameters from various MS Excel configuration tables
 *  
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ReadTestCaseParam {

	private static final int DPM_TAB = 12;
	private static final int TXC_TAB = 13;
	
	@SuppressWarnings("unchecked")
	private ArrayList<String> txcParam[]= new ArrayList[2]; 
	private ArrayList<String> dpmParam  = new ArrayList<String>(); 
	
	
	ArrayList<String>[] getTxcParam() 	{ return txcParam; }
	ArrayList<String> getDpmParam() 	{ return dpmParam; }
	ArrayList<String> getParam() 		{ return dpmParam; }
	
	public ReadTestCaseParam(String devName, int caseTab, int startRow, int caseNumber)
    {
	
        try {
      
        	FileInputStream scoreBoardStream = new FileInputStream(StabilitySetup.properties.get(devName +".scoreboard"));
            //Get the workbook instance for XLS file
            XSSFWorkbook workBook = new XSSFWorkbook(scoreBoardStream);
            
            //
            // Get DPM test case parameters 
            //
            XSSFSheet sheet = workBook.getSheetAt(caseTab);   

            for (int rowId = startRow; ; rowId++) {
            	//--- System.out.println("Row : " + rowId);
                Row row = sheet.getRow(rowId);
                if(row == null) {
                	break;
                }
 
                try {
                	Cell caseId = row.getCell(0);
                	//--- System.out.println(">"+caseId.toString()+"<");
                	if(caseId.toString().equals("")) continue; 
                	
                	int id = Utility.getIntVal(caseId);
                	
                	if(id == caseNumber) {
                		//System.out.println("DPM = " + caseId.toString() + '\n');
                		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				//System.out.println((i) + " >" + c.toString() + "<");
                				dpmParam.add(c.toString());
                			}
                			else 
                				break;
                		}
                		//System.out.println();
                		
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

	public ReadTestCaseParam(int dpm, String devName)
    {
	
        try {

        	FileInputStream scoreBoardStream = new FileInputStream(StabilitySetup.properties.get(devName + ".scoreboard"));
            //Get the workbook instance for XLS file
            XSSFWorkbook workBook = new XSSFWorkbook(scoreBoardStream);
            
            //
            // Get DPM test case parameters 
            //
            XSSFSheet sheet = workBook.getSheetAt(DPM_TAB);   
            int startRow = 9;
            try {
            	startRow = Integer.parseInt(StabilitySetup.properties.get( devName + ".scoreboard.StartLineDpm"));
            } catch(Exception e) {}

            for (int rowId = startRow; ; rowId++) {
            	//--- System.out.println("Row : " + rowId);
                Row row = sheet.getRow(rowId);
                if(row == null) {
                	break;
                }
 
                try {
                	Cell caseId = row.getCell(0);
                	//--- System.out.println(">"+caseId.toString()+"<");
                	if(caseId.toString().equals("")) continue; 
                	
                	int id = Utility.getIntVal(caseId);
                	
                	if(id == dpm) {
                		//System.out.println("DPM = " + caseId.toString() + '\n');
                		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				//System.out.println((i-1) + " >" + c.toString() + "<");
                				dpmParam.add(c.toString());
                				//---System.out.println("    " + dpmParam[0].size());
                				
                			}
                			else 
                				break;
                		}
                		//System.out.println();
                		
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

	public ReadTestCaseParam(int txc1, int txc2, int dpm, String devName)
    {
		for(int i = 0; i < 2; i++) {
			txcParam[i] = new ArrayList<String>();
		} // i
		
        try {
        	
        	FileInputStream scoreBoardStream = new FileInputStream(StabilitySetup.properties.get(devName + ".scoreboard"));
            //Get the workbook instance for XLS file
            XSSFWorkbook workBook = new XSSFWorkbook(scoreBoardStream);
            
            //
            // Get transcode test case parameters 
            //
            XSSFSheet sheet = workBook.getSheetAt(TXC_TAB);   
            int startRow = 10;
            try {
            	startRow = Integer.parseInt(StabilitySetup.properties.get(devName + ".scoreBoard.StartLineTxc"));
            }
            catch(Exception e) {}
            
            for (int rowId=startRow; ; rowId++) {
            	//--- System.out.println("Row : " + rowId);
                Row row = sheet.getRow(rowId);
                if(row == null) {
                	break;
                }
 
                try {
                	Cell caseId = row.getCell(0);
                	//--- System.out.println(">"+caseId.toString()+"<");
                	if(caseId.toString().equals("")) continue; 
                	
                	int id = Utility.getIntVal(caseId);
                	
                	if(id == txc1) {
                		//System.out.println("TXC1 = " + caseId.toString() + '\n');
                		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				//System.out.println((i-1) + " >" + c.toString() + "<");
                				txcParam[0].add(c.toString());
                			}
                			else 
                				break;
                		}
                		//System.out.println();
                		
                	}
                	if(id == txc2) {
                		//System.out.println("TXC2 = " + caseId.toString() + '\n');
                		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				//System.out.println((i-1) + " >" + c.toString() + "<");
                				txcParam[1].add(c.toString());
                			}
                			else 
                				break;
                		}
                		//System.out.println();
                		
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
            try {
            	startRow = Integer.parseInt(StabilitySetup.properties.get(devName + ".scoreboard.StartLineTxc"));
            } catch(Exception e) {
            	startRow = 9;
            }
            for (int rowId = startRow; ; rowId++) {
            	//--- System.out.println("Row : " + rowId);
                Row row = sheet.getRow(rowId);
                if(row == null) {
                	break;
                }
 
                try {
                	Cell caseId = row.getCell(0);
                	//--- System.out.println(">"+caseId.toString()+"<");
                	if(caseId.toString().equals("")) continue; 
                	
                	int id = Utility.getIntVal(caseId);
                	
                	if(id == dpm) {
                		//System.out.println("DPM = " + caseId.toString() + '\n');
                		for(int i = 1; ; i++) {
                			Cell c = row.getCell(i);
                			if(c != null) {
                				//System.out.println((i-1) + " >" + c.toString() + "<");
                				dpmParam.add(c.toString());
                				//---System.out.println("    " + dpmParam[0].size());
                				
                			}
                			else 
                				break;
                		}
                		//System.out.println();
                		
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

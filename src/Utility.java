import org.apache.poi.ss.usermodel.Cell;


public class Utility {
	
	public final static int BAD_INT = -9999999;
	
	public static int getIntVal(Cell cell) { return getIntVal(cell.toString().trim()); }
	
	public static int getIntVal(String s) 
	{
		int i;
		int val = -1;
		StringBuffer sNum = new StringBuffer();
		
		for(i = 0; i < s.length() && !Character.isDigit(s.charAt(i)) ; i++) ;
		for(; i < s.length() && Character.isDigit(s.charAt(i)); i++) sNum.append(s.charAt(i));
		
		try {
			val = Double.valueOf(sNum.toString()).intValue(); 	
		} catch(Exception e) {
			val = BAD_INT;
			//---System.err.println("Failed converting '" + sNum + "' to integer. Check test case location offset!");
		}
		
		return val;
		
	}	// getIntVal()


}	// class Utility


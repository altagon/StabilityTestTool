
import org.apache.poi.ss.usermodel.Cell;

public class Utility {

	public final static int BAD_INT = -9999999;

	public static int getIntVal(Cell cell) 
	{
		return getIntVal(cell.toString().trim());
	}

	public static int getIntVal(String s) 
	{
		int i;
		int val = -1;
		StringBuffer sNum = new StringBuffer();

		for (i = 0; i < s.length() && !Character.isDigit(s.charAt(i)); i++)	;	
		for (; i < s.length() && Character.isDigit(s.charAt(i)); i++)
			sNum.append(s.charAt(i));

		try {
			val = Double.valueOf(sNum.toString()).intValue();
		} catch (Exception e) {
			val = BAD_INT;
		}

		return val;

	} // getIntVal()

	public static double getDoubleVal(String s) 
	{
		int startPos, dashPos, endPos;
		double val1 = -1, val2 = -1;

		for (startPos = 0; startPos < s.length() && !(Character.isDigit(s.charAt(startPos)) || s.charAt(startPos) == '.'); startPos++) ;
		
		try {

			for(endPos = startPos; endPos < s.length() &&  (Character.isDigit(s.charAt(endPos)) || s.charAt(endPos) == '.'); endPos++) ;
			val1 = Double.valueOf(s.substring(startPos, endPos-startPos)).doubleValue();
			
			if((dashPos = s.substring(startPos).indexOf('-')) > 0 ) {	// interval "min-max" - take max
				
				for(endPos = startPos+dashPos+1; endPos < s.length() && (Character.isDigit(s.charAt(endPos)) || s.charAt(endPos) == '.'); endPos++) ;
				val2 = Double.valueOf(s.substring(startPos+dashPos+1, endPos-startPos)).doubleValue();
				return Math.max(val1, val2);
			}
		
		} catch (Exception e) {
			val1 = BAD_INT;
		}

		return val1;

	} // getIntVal()

	// TODO : empirical formula works OK for current test cases, could be changed in a future !!!
	public static int getActChanNum(double videoRate, double mpeRate) 
	{
		final double TOTAL_BANDWIDTH = 80; // decoders/transcoders should allow 80 Mb/s bandwidth

		int actChanNum = (int) (TOTAL_BANDWIDTH / (videoRate + mpeRate));

		if (actChanNum == 0) {
			System.out.println("Error : not enough bandwidth even for a single channel!!!\n");
			return 1; // can not return 0 it'll crash application
		}

		return actChanNum;
	}
	
	// Note: ip-address is encode as a "big-endian" int32
	public static int ipToInt(String ipAddress) 
	{
	    int result = 0;
	    String[] atoms = ipAddress.split("\\.");
	    
	    for (int i = 0; i < 4; i++) {
	        result |= (Integer.parseInt(atoms[i]) << (i * 8));
	    }
	    return result & 0xFFFFFFFF;
	  }


	public static int getPropValInt(String devName, String key)
	{
		return getIntVal(getPropVal(devName, key));
	}

	public static Integer getPropValInt(String key)
	{
		return getIntVal(getPropVal(key));
	}

	public static double getPropValDouble(String devName, String key)
	{
		return getDoubleVal(getPropVal(devName, key));
	}

	public static Double getPropValDouble(String key)
	{
		return getDoubleVal(getPropVal(key));
	}

	public static String getPropVal(String devName, String key)
	{
		return getPropVal(devName + "." + key);
	}
	
	public static String getPropVal(String key)
	{
		String val = STS.properties.get(key);
		
		if(val == null) {
			System.out.println("\nError: Can not retrive property '" + key + "'. Please check your property file!\n");
		}
		return val;
	}

	public static int getDestinationNumberByDeviceName(String devName)
	{
		switch(devName.toUpperCase().trim()) {
		
		case "D9010":  return 1;
		case "D9850":  return 2;
		case "D9834":  return 3;
		case "D9835R": return 4;
		case "D9835":  return 5;
		case "D9828":  return 6;
		case "D9838":  return 7;
		case "D9852":  return 8;
		case "D9854":  return 9;
		case "D9854I": return 9;
		case "MTR":    return 10;
		case "D9858":  return 11;
		case "D9865":  return 12;
		case "D9824":  return 13;
		case "D9859":  return 14;
		default:       return 255;
		}
	} // getDestinationNumberByDeviceName
	
	public static String inputToString(int rf)
	{
		switch(rf) {
			case 0:  return "ASI";
			case 1:  return "RF";
			case 2:  return "MPoIP";
			default: return "*UNKNOWN*";
		}

	} // inputToString

	
} // class Utility


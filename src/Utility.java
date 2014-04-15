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
			// ---System.err.println("Failed converting '" + sNum +
			// "' to integer. Check test case location offset!");
		}

		return val;

	} // getIntVal()

	public static double getDoubleVal(String s) 
	{
		int i;
		double val = -1;
		StringBuffer sNum = new StringBuffer();

		for (i = 0; i < s.length() && !(Character.isDigit(s.charAt(i)) || s.charAt(i) == '.' || s.charAt(i) == '-'); i++) ;
		for (; i < s.length() && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.'); i++)
			sNum.append(s.charAt(i));

		try {
			val = Double.valueOf(sNum.toString()).doubleValue();
		} catch (Exception e) {
			val = BAD_INT;
			// ---System.err.println("Failed converting '" + sNum +
			// "' to integer. Check test case location offset!");
		}

		return val;

	} // getIntVal()

	// empirical formula works OK for current test cases, could be changed in a
	// future
	public static int getActChanNum(double videoRate, double mpeRate) {
		final double TOTAL_BANDWIDTH = 80; // decoders/transcoders should allow
											// 80 Mb/s bandwidth

		int actChanNum = (int) (TOTAL_BANDWIDTH / (videoRate + mpeRate));

		if (actChanNum == 0) {
			System.err.println("Bad Test Case : not enough bandwidth even for a single channel!!!\n");
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

	public static int getPropValInt(String key)
	{
		return getIntVal(getPropVal(key));
	}

	public static double getPropValDouble(String devName, String key)
	{
		return getDoubleVal(getPropVal(devName, key));
	}

	public static double getPropValDouble(String key)
	{
		return getDoubleVal(getPropVal(key));
	}

	public static String getPropVal(String devName, String key)
	{
		return getPropVal(devName + "." + key);
	}
	
	public static String getPropVal(String key)
	{
		String val = StabilitySetup.properties.get(key);
		
		if(val == null) {
			System.err.println("\nError: Can not retrive proprerty '" + key + "'. Please check your property file!\n");
			System.exit(1);
		}
		return val;
	}

} // class Utility


package unitiacore.threadpool;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Util{
	
	static Map<Integer,Integer> data=new HashMap<Integer,Integer>();
	
	static{
		
		data.put(40, 0);
	}
	
	static final char[] GSM7CHARS = {
	        0x0040, 0x00A3, 0x0024, 0x00A5, 0x00E8, 0x00E9, 0x00F9, 0x00EC,
	        0x00F2, 0x00E7, 0x000A, 0x00D8, 0x00F8, 0x000D, 0x00C5, 0x00E5,
	        0x0394, 0x005F, 0x03A6, 0x0393, 0x039B, 0x03A9, 0x03A0, 0x03A8,
	        0x03A3, 0x0398, 0x039E, 0x00A0, 0x00C6, 0x00E6, 0x00DF, 0x00C9,
	        0x0020, 0x0021, 0x0022, 0x0023, 0x00A4, 0x0025, 0x0026, 0x0027,
	        0x0028, 0x0029, 0x002A, 0x002B, 0x002C, 0x002D, 0x002E, 0x002F,
	        0x0030, 0x0031, 0x0032, 0x0033, 0x0034, 0x0035, 0x0036, 0x0037,
	        0x0038, 0x0039, 0x003A, 0x003B, 0x003C, 0x003D, 0x003E, 0x003F,
	        0x00A1, 0x0041, 0x0042, 0x0043, 0x0044, 0x0045, 0x0046, 0x0047,
	        0x0048, 0x0049, 0x004A, 0x004B, 0x004C, 0x004D, 0x004E, 0x004F,
	        0x0050, 0x0051, 0x0052, 0x0053, 0x0054, 0x0055, 0x0056, 0x0057,
	        0x0058, 0x0059, 0x005A, 0x00C4, 0x00D6, 0x00D1, 0x00DC, 0x00A7,
	        0x00BF, 0x0061, 0x0062, 0x0063, 0x0064, 0x0065, 0x0066, 0x0067,
	        0x0068, 0x0069, 0x006A, 0x006B, 0x006C, 0x006D, 0x006E, 0x006F,
	        0x0070, 0x0071, 0x0072, 0x0073, 0x0074, 0x0075, 0x0076, 0x0077,
	        0x0078, 0x0079, 0x007A, 0x00E4, 0x00F6, 0x00F1, 0x00FC, 0x00E0};

	static final char[] ESCAPE = {
	        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
	        0x0000, 0x0000, '\n'  , 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
	        0x0000, 0x0000, 0x0000, 0x0000, '^'   , 0x0000, 0x0000, 0x0000,
	        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
	        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
	        '{'   , '}'   , 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
	        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, '\\',
	        0x0000, 0x0000, 0x0000, 0x0000, '['   , '~'   , ']'   , 0x0000,
	        '|'   , 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
	        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
	        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
	        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
	        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x20AC, 0x0000, 0x0000,
	        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
	        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
	        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000};
	        // or use -1 instead of 0x0000, depending on your preference

	//...

	  
	public static void main(String args[]) throws Exception{
		/*
		char i='`';
		int a=(int)i;
		
		System.out.println(a);
		System.out.println(URLEncoder.encode("Dear ‘Customer’, It’s our endeavor to serve you well.","UTF-8"));
		String data=toHexString("Dear ‘Customer’, It’s our endeavor to serve you well.");
		data=data.replaceAll("00", "");
		System.out.println(addKannelSpecialCharactertoHex(data));
	
		*/
		
		String text=URLEncoder.encode("Dear @ jaffer","UTF-8");
		System.out.println(text);
		//String text=URLEncoder.encode("Dear ‘Customer’, It’s our endeavor to serve you well.","UTF-8");

		//System.out.println(Integer.toHexString((int)'@'));
		//conversion1("Dear ‘Customer’, It’s our endeavor to serve you well. test@test");
	}
	
	  public static String toHexString(String str) {
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < str.length(); i++) {
	      sb.append(toHexString(str.charAt(i)));
	    }
	    return sb.toString();
	  }

	  public static String toHexString(char ch) {
	    String hex = Integer.toHexString((int) ch);
	    while (hex.length() < 4) {
	      hex = "0" + hex;
	    }
	    return hex;
	  
	  }
	  
	  private static String addKannelSpecialCharactertoHex(String hexa)  throws Exception
		{
			String kannelSpecCharacter="%";

			StringBuffer returnValues=new StringBuffer();
			
			for(int i=0;i<hexa.length();i=i+2)
			{
				returnValues.append(kannelSpecCharacter);
				returnValues.append(hexa.substring(i,i+2));
			}
			
			return returnValues.toString();
		}


	  private static void conversion(String test){
		  
			byte[] byteFinal =test.getBytes();
		
			StringBuilder sb = new StringBuilder();
			
			for(byte b : byteFinal){
			
				int d=(int)b;
				
				if(data.containsKey(d)){
					sb.append(data.get(d));
				}else{
					sb.append(d);
				}
			}
			
			System.out.println(sb.toString());


		  
	  }
	  
	  

	  private static void conversion1(String test){
		  
			byte[] byteFinal =test.getBytes();
			StringBuilder sb = new StringBuilder();
			boolean escape = false;
			for(byte b : byteFinal){
			    if (b >= 0) {
			            sb.append(ESCAPE[b] > 0 ? ESCAPE[b] : GSM7CHARS[b]);
			    }
			}
			System.out.println(sb.toString());


		  
	  }

}
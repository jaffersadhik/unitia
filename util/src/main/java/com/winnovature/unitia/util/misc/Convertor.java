package com.winnovature.unitia.util.misc;

public class Convertor {

	
	
	public static String getMessage(String message){
		

		try{
		    String str = "";
		    for(int i=0;i<message.length();i+=4)
		    {
		        String s = message.substring(i, (i + 4));
		        int decimal = Integer.parseInt(s, 16);
		        str = str + (char) decimal;
		    }       
		    return str;
		}catch(Exception e){
			
			return message;
		}
	}
}

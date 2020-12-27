package com.winnovature.unitia.util.http;
import java.io.UnsupportedEncodingException;


public class Hexaconvertor
{
 
	public static void main(String args[]) throws Exception{
		
		
	//	String messga="à®•à¯Šà®²à¯�à®•à®¤à¯�à®¤à®¾ à®�à®•à¯‹à®°à¯�à®Ÿà¯�à®Ÿà¯� à®ªà¯†à®£à¯� à®¨à¯€à®¤à®¿à®ªà®¤à®¿ à®…à®©à®¿ à®¨à®¿à®Ÿà¯�à®Ÿà®¾ à®°à®¾à®¯à¯� à®šà®°à®¸à¯�à®µà®¤à®¿. à®‡à®µà®°à¯� à®•à¯�à®Ÿà¯�à®®à¯�à®ªà®¤à¯�à®¤à¯�à®Ÿà®©à¯�...";
		//System.out.println(stringToHexString(messga));
	}
	
	public static String stringToHexString(String msg) throws Exception {

	    byte[] byteArr = null;
	    try {
	           byteArr = msg.getBytes("UTF-16");
	    } catch (UnsupportedEncodingException e) {
	           throw e;
	    }

	    StringBuffer sb = new StringBuffer(byteArr.length * 2);
	    for (int i = 0; i < byteArr.length; i++) {
	           int v = byteArr[i] & 0xff;
	           if (v < 16) {
	                 sb.append('0');
	           }
	           sb.append(Integer.toHexString(v));
	    }

	    // System.out.println(sb.toString().toUpperCase());
	    return sb.toString().toUpperCase();
	}
}

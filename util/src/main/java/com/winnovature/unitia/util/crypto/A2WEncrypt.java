package com.winnovature.unitia.util.crypto;

import java.util.*;

public class A2WEncrypt {
	
	char[] firstArray = {'3','m','W','J','z','n','5','0','b','d','2','c','4','o','p','a','S','t','7','v','w','x','h','B','C','e','D','6','E','F','G','l','Y','s','Z','9','H','I','8','K','T','L','q','y','M','u','N','f','O','P','A','g','Q','i','R','j','U','1','V','k','X','r'};
	char[] secondArray = {'Q','N','Y','2','L','i','c','S','U','I','C','y','m','1','g','5','d','l','7','j','o','p','a','K','x','h','B','D','z','F','4','t','G','Z','q','H','8','J','e','M','s','k','3','v','O','r','P','A','T','6','w','b','R','E','9','n','V','u','W','f','X','0'};
	char[] indexArray = {'W','8','O','K','E','k','1','S','b','5','g','H','G','y','Q','z','c','9','p','a','e','N','r','t','2','u','v','d','n','U','i','w','f','B','J','j','D','0','m','4','F','L','q','Y','x','V','Z','C','3','I','l','o','M','7','h','P','A','s','T','6','R','X'};
	char[] NumberConvArray = {'N','I','3','D','e','u','g','R','z','n','k','9','V','G','p','a','Q','i','5','m','S','t','2','U','c','x','B','X','f','j','1','o','l','Z','4','s','7','H','0','y','J','q','T','C','w','K','b','L','r','M','Y','O','P','A','8','d','F','W','h','E','v','6'};
	
	public static void main(String args[]) throws Exception {
		
		System.out.println("Encrypt String        : " + new A2WEncrypt().encrypt(args[0]));
		System.out.println("Encrypt String Length : " + new A2WEncrypt().encrypt(args[0]).length());
		System.out.println("Decrypt String: " + new A2WEncrypt().decrypt(new A2WEncrypt().encrypt(args[0])));
		
	}
	
	public String encrypt(String mobileNo) throws Exception {
		
		long mobile = Long.parseLong(mobileNo);
		
		int firstDigit = new Random().nextInt(52);

		String firstDigitStr = String.valueOf( indexArray[firstDigit] );
		String secondDigitStr = String.valueOf(secondArray[(int)(mobile % 8)]);
		String thirdDigitStr = String.valueOf(firstArray[(int)(mobile % 7)]);

		char[] mobileArray = mobileNo.toCharArray();
		StringBuffer buffer = new StringBuffer();
		buffer.append(firstDigitStr);
		buffer.append(secondDigitStr);
		buffer.append(thirdDigitStr);
		for(int i=0; i< mobileArray.length; i++)
			buffer.append(String.valueOf(NumberConvArray[(firstDigit + Integer.parseInt(String.valueOf(mobileArray[i])))]));

		return buffer.reverse().toString();
		
	}
	
	public String decrypt(String encryptedMobile) throws Exception {
		String mobile = null;
		
		StringBuffer encryptBuffer = new StringBuffer(encryptedMobile);
		encryptBuffer.reverse();
		
		String mobileStr = encryptBuffer.toString();
		
		int firstDigit = new String(indexArray).indexOf(mobileStr.substring(0,1));
		int secondDigit = new String(secondArray).indexOf(mobileStr.substring(1,2));
		int thirdDigit = new String(firstArray).indexOf(mobileStr.substring(2,3));
	
		char[] mobileArray = mobileStr.substring(3).toCharArray();
		StringBuffer mobileStrBuffer = new StringBuffer();
		
		for(int i=0; i< mobileArray.length; i++)
			mobileStrBuffer.append((new String(NumberConvArray).indexOf(String.valueOf(mobileArray[i]))) - firstDigit);

		long decryptMobNum = Long.parseLong(mobileStrBuffer.toString());
		mobile = String.valueOf(decryptMobNum);
		int mod52 = (int)(decryptMobNum % 8);
		int mod60 = (int) (decryptMobNum % 7);
		
		if(secondDigit == mod52 || thirdDigit == mod60)
			return mobile;
		else
			throw new Exception("Request Tampered");
			
		
	}
}
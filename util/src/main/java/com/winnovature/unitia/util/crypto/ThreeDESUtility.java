/**
 * 	@(#)ThreeDESUtility.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */

package com.winnovature.unitia.util.crypto;

// java imports
import java.util.Iterator;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.spec.*;

/**
 * 	This class is used to encrypt and decrypt messages  	
 * 	
 * 	@author 	M Ravikumar (ravikumar@air2web.co.in)
 *	@version 	ThreeDESUtility.java v1.0<br>
 *				Created: 26 Jun 2008 11:30<br>
 *				Last Modified 26 Jun 2008 11:30 by M Ravikumar
 */
public class ThreeDESUtility {

	private String fullAlgorithm;
	private String keyAlgorithm;
	private static final String DEFAULT_ALGORITHM = "Desede"; //"Blowfish/ECB/NoPadding";
	private static final int ACTION_ENCRYPT  =0;
	private static final int ACTION_DECRYPT  =1;
	private static final int ACTION_GENERATE =2;
	private static final int ACTION_TOKEN    =3;

	private static final String className = "[EncryptUtil] ";


   /**
   	*	Parameterised constructor.
    *	@param String algorithmName  - Full algorithm: Algorithm/BlockMode/Padding as defined by JCE
    */
    public ThreeDESUtility(String algorithm) {

        if( algorithm == null)        {
            //Logger.error(className+"Parameterised Constructor; No algorithm provided");
        }

        int slashIndex  = algorithm.indexOf("/");
        if( slashIndex == -1 )        {
           fullAlgorithm = algorithm;
           keyAlgorithm = algorithm;
        }
        else        {
            fullAlgorithm = algorithm;
            keyAlgorithm = algorithm.substring(0, slashIndex);
        }
    }


   /**
    *	This method is used to Encrypt data using key.
    *	@param byte[] key bytes
    *	@param byte[] data
    *	@return byte[] cipher text
    */
    public byte[] encrypt( byte [] keyBytes, byte [] data) throws Exception {

        byte[] cipherText = null;
        try {
            Key key = convertBytesToKey(keyBytes);
            Cipher c = Cipher.getInstance(fullAlgorithm);
            c.init(Cipher.ENCRYPT_MODE, key);
            cipherText = c.doFinal(data);
        } catch(Exception e)        {
            throw e;
        }

        return cipherText;
    }

   /**
    * 	This method is used to Encrypt data using key.
    *	Encrypt - pass in hex key and plaintext string instead of bytes
    * 	Key - passed in as hex String
    * 	PlainText - passed in as NORMAL string
    *	@param String hexkey
    *	@param String plain text
    *	@return String encrypted text.
    */
    public String encrypt( String hexKey, String plainText) throws Exception {

        byte [] result = null;
        String encryptedText = "";
        try {
            byte [] keyBytes = stringToBytes(hexKey);
            byte [] textBytes = plainText.getBytes();
            result = encrypt(keyBytes, textBytes);
            encryptedText = byteToHexString( result );
        } catch(Exception e)        {
             throw e;
        }

        return encryptedText;
    }


   /**
    *	This method is used to decrypt cipherText using key.
    *	@param byte[] key bytes
    *	@param byte[] encrypted data
    *	@return byte[] decrypted data
    */
    public byte[] decrypt(byte keyBytes[], byte encryptedData[]) throws Exception {
        byte[] text = null;
        try {
            Key key = convertBytesToKey(keyBytes);
            Cipher c = Cipher.getInstance(fullAlgorithm);
            c.init(Cipher.DECRYPT_MODE, key);
            text = c.doFinal(encryptedData);

        } catch(Exception e) {
           throw e;
        }

        return text;
    }

   /**
    * 	This method is used to decrypt the data by given hex key.
    *	@param String hex key
    *	@param String hex cipher text
    *	@return String decrypted data
    */
    public String decrypt(String hexKey, String hexCipherText) throws Exception {
        byte [] result = null;
        String decryptedText = "";
        try {
            byte [] keyBytes = stringToBytes(hexKey);
            byte [] cipherBytes = stringToBytes(hexCipherText);
            result = decrypt(keyBytes, cipherBytes);
            decryptedText = byteToCharString( result );

        } catch(Exception e) {
           throw e;
        }

        return decryptedText;
    }

   /**
    *	This method is used to parse the String tokens into HashMap.
    *	Parse token String and return hashMap of values
    *  	Token string in format:   name1=value1&name2=value2&...&name n=value n
    *	@param String token
    *	@return HashMap parsed tokens
    */
    public static HashMap parseToken(String token) throws Exception {
        HashMap result = new HashMap();
        StringTokenizer st = new StringTokenizer(token, "&");
        while(st.hasMoreTokens() )        {
            String nameValue = st.nextToken();
            StringTokenizer eTok = new StringTokenizer(nameValue, "=");
            String name= null;
            String value= null;

            if(eTok.hasMoreTokens() )	{
	            name = eTok.nextToken();
            }

            if(eTok.hasMoreTokens())	{
	            value = eTok.nextToken();
            }
            if(name != null || value != null)    {
                result.put(name, value);
            }
        }

        return result;
    }

   /**
    *	This method is used to Converts a byte encoded keySpec into a Key
    *	@param byte[] key bytes
    *	@return Key key
    **/
    public Key convertBytesToKey(byte [] keyBytes) throws Exception {
    	SecretKeySpec keySpec = null;
	    try	{
        	keySpec = new SecretKeySpec(keyBytes, keyAlgorithm);
        	
        } catch(Exception e) {
	        throw e;
        }
        
        return keySpec;
    }

   /**
    *	This method is used to Generates a random key and returns it as bytes, converted into String
    *	@return String key text
    **/
    public String generateKey() throws Exception {

	    String keyText = "";
	    try	{
        	KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm);
        	SecretKey key = keyGenerator.generateKey();
        	byte [] keyBytes= key.getEncoded();
        	//return keyBytes;
        	keyText = byteToHexString( keyBytes );

        } catch(Exception e) {
	        throw e;
        }

        return keyText;
    }


    /**
     * 	This method is used to convert a byte array from a string of hexadecimal digits.
     *	@param String hexadecimal digits
     *	@return byte[] byte array
     */
    public static byte[] stringToBytes(String hex) throws Exception {

        int len = hex.length();
        byte[] buf = new byte[((len + 1) / 2)];

        int i = 0, j = 0;
        if ((len % 2) == 1)	{
        	buf[j++] = (byte) fromDigit(hex.charAt(i++));
        }

        while (i < len) {
            buf[j++] = (byte) ((fromDigit(hex.charAt(i++)) << 4) |
                               fromDigit(hex.charAt(i++)));
        }

        return buf;
    }

    /**
     * 	This method is used to converts the number from 0 to 15 corresponding to the hex digit <i>ch</i>.
     *	@param char character
     *	@return int converted int value.
     */
    public static int fromDigit(char ch) throws Exception {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;
        throw new IllegalArgumentException("invalid hex digit '" + ch + "'");
    }

   /**
    * 	This method is used to convert byte array as hex.
    *	@param byte[] byte array
    *	@return String hexadecimal string
    */
    public static String byteToHexString(byte [] bytes) throws Exception {

        StringBuffer buf = new StringBuffer();
        for (int i=0; i<bytes.length; i++) {
            int b =  ((int)bytes[i]) & 0xff;
            String hexVal = Integer.toHexString( b);
            if(hexVal.length() == 1) {
	            hexVal = "0"+hexVal;
	        }
            buf.append( hexVal);
        }

        return buf.toString();
    }

   /**
    *	This method is used to convert Byte array as String of chars
    *	@param byte[] byte array
    *	@return String character string
    */
    public static String byteToCharString(byte [] bytes) throws Exception {

        StringBuffer buf = new StringBuffer();
        for (int i=0; i<bytes.length; i++) {
            buf.append( (char)bytes[i]);
        }

        return buf.toString();
    }

    public static void main(String args[]) throws Exception{

    	System.out.println(new ThreeDESUtility("Desede").encrypt("2a805eef49ec132907451698372010542a805eef49ec1329", "encrypt test"));
    }
}

package unitiasmpp.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author  unknown
 * @version SmppUtility.java v1.0 			
 *			Last Modified: Feb 26, 2010 by Aarthi
 *
 */
public class SmppUtility {

	/**
	 * Conveting the String Object in the Set to comma separated String
	 * @param clientSet
	 * @return list of clients in a String
	 */
	public String parseClientSet(Set clientSet) {
		
		StringBuffer sb = new StringBuffer();
		
		Iterator itr = clientSet.iterator();
		
		int ctr = 0;
		
		while(itr.hasNext()){
			String client = (String) itr.next();
			
			sb.append("'" + client + "'");
			
			if(ctr < clientSet.size()-1)
				sb.append(",");
			
			ctr++;
		}		
		
		return sb.toString();
	}

	/**
	 * Converting the string val to HEX val.
	 * @param _strObj
	 * @return hex String
	 */
	public String getHexStr(String _strObj) {
		String hex = null;
		
		if(_strObj != null){
			int decval = Integer.parseInt(_strObj.trim());
			hex = Integer.toHexString(decval);					
		}
		
		return hex;
		
	}
	
	private static final char hexChar[] = { '0', '1', '2', '3', '4', '5', '6',
		'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	public byte[] convertHexStringToBytes(String hexString, int offset,
			int endIndex) {
		String realHexString = hexString.substring(offset, endIndex)
				.toLowerCase();
		byte data[];
		if (realHexString.length() % 2 == 0)
			data = new byte[realHexString.length() / 2];
		else
			data = new byte[(int) Math
					.ceil((double) realHexString.length() / 2D)];
		int j = 0;
		for (int i = 0; i < realHexString.length(); i += 2) {
			char tmp[];
			try {
				tmp = realHexString.substring(i, i + 2).toCharArray();
			} catch (StringIndexOutOfBoundsException siob) {
				tmp = (new StringBuilder(String.valueOf(realHexString
						.substring(i)))).append("0").toString().toCharArray();
			}
			data[j] = (byte) ((Arrays.binarySearch(hexChar, tmp[0]) & 0xf) << 4);
			data[j++] |= (byte) (Arrays.binarySearch(hexChar, tmp[1]) & 0xf);
		}

		for (int i = realHexString.length(); i > 0; i -= 2)
			;
		return data;
	}
	
	public byte[] convertHexStringToBytes(String hexString) {
		return convertHexStringToBytes(hexString, 0, hexString.length());
	}	
	
/*	public ByteBuffer getUDH(String udhStr2) {
		ByteBuffer buffer = new ByteBuffer();
		StringBuffer strbuffer = new StringBuffer();
		strbuffer.append(udhStr2);
		buffer.appendBytes(convertHexStringToBytes(strbuffer.toString()));
		return buffer;
	}	*/
	
	/**
	 * This method is used to convert the given String to HexaDecimal format
	 * 
	 * @return HexaDecimal String
	 */
	public String pmToHexString(String msg) throws Exception {

		byte[] byteArr = null;
		byteArr = msg.getBytes();

		StringBuffer sb = new StringBuffer(byteArr.length * 2);
		for (int i = 0; i < byteArr.length; i++) {
			int v = byteArr[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}

		return sb.toString().toUpperCase();
	}		

}

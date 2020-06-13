
package com.winnovature.unitia.util.misc;

import com.winnovature.unitia.util.datacache.instance.InstanceInfoMemory;

public class ACKIdGenerator {

	public static int ackIdIncrement = 10000000;
	

    public static synchronized String getAckId() {
        
		StringBuffer s_buffer = new StringBuffer();		
		s_buffer.append(InstanceInfoMemory.INSTANCE_ID);		
		s_buffer.append(new WinDate().getTime());		
		if (ackIdIncrement > 99999990) ackIdIncrement = 10000000;
		s_buffer.append(++ackIdIncrement);
		
		return s_buffer.toString();
        
    }
    
   

    public static long generateRTS() {
    	
    	return System.currentTimeMillis() ;
    }
}

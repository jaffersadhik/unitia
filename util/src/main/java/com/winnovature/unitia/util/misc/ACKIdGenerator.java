
package com.winnovature.unitia.util.misc;

import com.winnovature.unitia.util.instance.InstanceInfoMemory;

public class ACKIdGenerator {

	public static int ackIdIncrement = 1000000;
	

    public static synchronized String getAckId() {
        
		StringBuffer s_buffer = new StringBuffer();		
		s_buffer.append(InstanceInfoMemory.INSTANCE_ID);		
		s_buffer.append(System.currentTimeMillis());		
		if (ackIdIncrement > 9999990) ackIdIncrement = 1000000;
		s_buffer.append(++ackIdIncrement);
		
		return s_buffer.toString();
        
    }
    
   

    public static long generateRTS() {
    	
    	return System.currentTimeMillis() ;
    }
}

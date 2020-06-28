
package com.winnovature.unitia.util.misc;

import java.util.concurrent.atomic.AtomicInteger;

import com.winnovature.unitia.util.instance.InstanceInfoMemory;

public class ACKIdGenerator {

    private static AtomicInteger count = new AtomicInteger();
	

    public static synchronized String getAckId() {
        
		StringBuffer s_buffer = new StringBuffer();		
		s_buffer.append(InstanceInfoMemory.INSTANCE_ID);		
		s_buffer.append(System.currentTimeMillis());
		int seq=count.getAndIncrement();
		if (seq > 9990){
			count.set(1000);
			seq=count.getAndIncrement();
		}
		s_buffer.append(seq);
		
		return s_buffer.toString();
        
    }
    
   

    public static long generateRTS() {
    	
    	return System.currentTimeMillis() ;
    }
}


package com.winnovature.unitia.util.misc;

import java.util.UUID;

public class ACKIdGenerator {

	

    public static synchronized String getAckId() {
        
		return UUID.randomUUID().toString();
        
    }
    
   

    public static long generateRTS() {
    	
    	return System.currentTimeMillis() ;
    }
    
    public static void main(String args[]){
    	
    	System.out.println(getAckId());
    }
}


package simulatar.util;

import java.util.UUID;

public class ACKIdGenerator {

	

    public static synchronized String getAckId() {
        
		return java.util.UUID.randomUUID().toString();
        
    }
    
   

    
    public static void main(String args[]){
    	
    	System.out.println(getAckId());
    }
}

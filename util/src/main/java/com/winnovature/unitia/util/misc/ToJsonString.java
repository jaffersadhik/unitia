package com.winnovature.unitia.util.misc;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ToJsonString {

	
	public static String toString(Object bean) {
		
		String jsonStr=null;
        ObjectMapper Obj = new ObjectMapper(); 
        
        try { 
        	  
            // get Oraganisation object as a json string 
        	jsonStr = Obj.writeValueAsString(bean); 
  
        } 
  
        catch (IOException e) { 
            e.printStackTrace(); 
        } 
        
        return jsonStr;
	}
}

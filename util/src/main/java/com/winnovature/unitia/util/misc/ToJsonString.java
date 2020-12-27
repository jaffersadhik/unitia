package com.winnovature.unitia.util.misc;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

	public static List<Map<String, Object>> toList(String jsonstring) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		   try { 
	        	  
	        	return  mapper.readValue(jsonstring, List.class);
	  
	        } 
	  
	        catch (IOException e) { 
	            e.printStackTrace(); 
	        } 
		return null;
		
		
		
	}
}

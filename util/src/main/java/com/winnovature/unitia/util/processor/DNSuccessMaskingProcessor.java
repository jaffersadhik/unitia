package com.winnovature.unitia.util.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DNSuccessMaskingProcessor
{

	public static final String  S_CNT            = "SUCCESS_COUNT";
    
    public static final String  T_CNT            = "TOTAL_COUNT";
    
	 
	private static DNSuccessMaskingProcessor singleton = new DNSuccessMaskingProcessor();

	
	private Map history=new HashMap();
	
	private DNSuccessMaskingProcessor()
	{
		
	}
	
	public static DNSuccessMaskingProcessor getInstance()
	{
		return singleton;
	}
	


	
	

	public synchronized Map getHistory(String username){
		
		Map result=null;
		if(history.containsKey(username)){
			result=(Map)history.get(username);
		}else{
			result=new HashMap();
			result.put(S_CNT, "0");
			result.put(T_CNT, "1");
			history.put(username, result);
		}
		return result;
			
	}
	
	public synchronized void incrementHistory(String username,boolean isSuccess){
		
		try{
		if(history.containsKey(username)){
			Map result=(Map)history.get(username);
			
			long scnt=Long.parseLong(result.get(S_CNT).toString());
			long tcnt=Long.parseLong(result.get(T_CNT).toString());
			tcnt++;
			if(isSuccess){
			scnt++;
			}
			if(tcnt==0){
				tcnt=1;
				scnt=0;
			}
			result.put(S_CNT, ""+scnt);
			result.put(T_CNT, ""+tcnt);
			history.put(username, result);
		}else{
			

			Map result=new HashMap();

			if(isSuccess){
				result.put(S_CNT, "1");
			}else{
				result.put(S_CNT, "0");
			}
			result.put(T_CNT, "1");
			history.put(username, result);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public synchronized void incrementHistoryForSuccess(String username) {
		
		Map result=(Map)history.get(username);
		
		long scnt=Long.parseLong(result.get(S_CNT).toString());
		
		scnt++;
		
		result.put(S_CNT, ""+scnt);
		
	}

	public void resetHistory() {
		
		history=new HashMap();
		
	}
}

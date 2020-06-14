package com.winnovature.unitia.util.processor;

import java.util.Map;

import com.winnovature.unitia.util.dao.Insert;

public class ScheduleProcessor  {
	
	Map<String,String> msgmap=null;
	
	Map<String,String> logmap=null;
	
	
	public ScheduleProcessor(Map<String,String> msgmap,Map<String,String> logmap){
		
		this.logmap=logmap;
		this.msgmap=msgmap;
	}
	
	public void doProcess(){
		
		untilSent();
		
		logmap.put("Schedule Processor Status ", "Message Persisted to Mysql Schedue Queue");
	}

	private void untilSent() {
		
		while(true){
		
			if(new Insert().insert("schedule", msgmap)){
				
				return;
			}
			
			gotosleep();
		}
		
	}

	private void gotosleep() {
		
		try{
			
			Thread.sleep(5L);
			
		}catch(Exception e){
			
		}
		
	}
	
}

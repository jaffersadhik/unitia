package com.winnovature.unitia.util.threadpool;

import java.util.Map;

import com.winnovature.unitia.util.redis.RedisReader;

public class RedisReceiver extends Thread {

	String poolname=null;
	public RedisReceiver(String poolname){
		
		this.poolname=poolname;
	}
	public void run(){
		
		RedisReader reader=new RedisReader();
		while(true){
			
			if(ThreadPoolTon.getInstance().isAvailable(poolname)){
				
			Map<String,String> data=reader.getData(poolname);
			
			if(data!=null){
				
				ThreadPoolTon.getInstance().doProcess(poolname, "sms", data);
			}else{
				
				gotosleep();
			}
			}else{
				
				gotosleep();
			}
			
		}
	}
	private void gotosleep() {
		
		try{
			
			Thread.sleep(5L);
		}catch(Exception e){
			
		}
	}
}

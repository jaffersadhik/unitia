package com.winnovature.unitia.httpinterface.servlet;

import com.winnovature.unitia.util.redis.RedisQueuePool;

public class T  extends Thread{

	public void run(){
		
		while(true){
			
			try{
				
				RedisQueuePool.getInstance().reload();
				
				gotosleep();
				
			}catch(Exception e){
				
			}
		}
	}

	private void gotosleep() {


		try{
		
			Thread.sleep(100L);
		}catch(Exception e){
			
		}
	}
}

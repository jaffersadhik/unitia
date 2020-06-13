package com.winnovature.unitia.util.datacache.instance;

import com.winnovature.unitia.util.dao.JVMUniqueId;

public class InstanceInfoMemory {

	public static  String INSTANCE_ID = null;

	static{
		
		INSTANCE_ID=new JVMUniqueId().getTransId();
		
		if(INSTANCE_ID==null){
			
			System.err.println("Unable to get JVM Id from Core DB ,System going to down");
			
			System.exit(-1);
		}
		
		new T().start();
	}
	
	static class T extends Thread{
		
		public void run(){
			
			while(true){
				
				new JVMUniqueId().upateJVMId(INSTANCE_ID);
			
				gotosleep();
			}
		}

		private void gotosleep() {
			
			try{
				
				Thread.sleep(10000L);
			}catch(Exception e){
				
			}
			
		}
	}

}

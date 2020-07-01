package unitiacore;

import unitiacore.threadpool.ThreadPoolTon;

public class T  extends Thread{

	public void run(){
		
		while(true){
			
			try{
	
				ThreadPoolTon.getInstance().reload();
				
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

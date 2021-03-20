package queuecheck;

import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;




public class TMysqlQueue  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				System.out.println("TMysqlQueue run ");
				TableCount.getInstance().tableCountCheck();
				
				gotosleep();
			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
	}

	

	private void gotosleep() {


		try{
			Thread.sleep(10L);
		}catch(Exception e){
			
		}
	}
}






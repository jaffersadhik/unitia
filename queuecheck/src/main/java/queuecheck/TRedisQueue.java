package queuecheck;

import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;




public class TRedisQueue  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				RedisQueueConnectionPool.getInstance().reloadnew();
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






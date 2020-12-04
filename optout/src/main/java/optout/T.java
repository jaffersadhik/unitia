package optout;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.queue.RedisQueue;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;




public class T  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				Refresh.getInsatnce().reload();
				RedisQueue.getInstance().reload();

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






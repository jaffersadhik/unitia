package dngencore;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.queue.RedisQueue;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;

import unitiacore.threadpool.QueueTon;

public class T  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				RedisQueue.getInstance().reload();
				QueueTon.getInstance().checkQueueAvailablity();
				Refresh.getInsatnce().reload();
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






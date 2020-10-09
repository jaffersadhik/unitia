package dngencore;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;

import unitiacore.threadpool.QueueTon;

public class T  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				QueueTon.getInstance().checkQueueAvailablity();
				Refresh.getInsatnce().reload();
				RedisQueueConnectionPool.getInstance().reload();
				com.winnovature.unitia.util.db.Kannel.getInstance().reload();
				com.winnovature.unitia.util.misc.kannel.reload();
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






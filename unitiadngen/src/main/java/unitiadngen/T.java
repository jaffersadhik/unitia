package unitiadngen;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;

public class T  extends Thread{

	public void run(){
		
		while(true){
			
			try{
				Refresh.getInsatnce().reload();
				RedisQueueConnectionPool.getInstance().reload();
			
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

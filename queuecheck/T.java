package queuecheck;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;




public class T  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				PushAccount.instance().reload();
				RedisQueueConnectionPool.getInstance().reload();
				com.winnovature.unitia.util.db.Kannel.getInstance().reload();

				kannelQueue.reload();

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






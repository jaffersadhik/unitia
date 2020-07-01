package unitiadnreceiver;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.redis.RedisQueuePool;
import com.winnovature.unitia.util.test.Account;

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

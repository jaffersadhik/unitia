package admin;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.redis.OtpMessageDNRegister;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;

import unitiaroute.ReRouting;

public class T  extends Thread{

	public void run(){
		
		while(true){
			
			try{
				RedisQueueConnectionPool.getInstance().reload();

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

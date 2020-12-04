package unitiadnreceiver;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.queue.RedisQueue;
import com.winnovature.unitia.util.redis.OtpMessageDNRegister;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;

import unitiaroute.ReRouting;

public class T  extends Thread{

	public void run(){
		
		while(true){
			
			try{
				RedisQueue.getInstance().reload();

				Refresh.getInsatnce().reload();
				new OtpMessageDNRegister().removeOldAckid();
				ReRouting.getInstance().reload();

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

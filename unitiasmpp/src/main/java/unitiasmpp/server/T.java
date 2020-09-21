package unitiasmpp.server;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;
import com.winnovature.unitia.util.redis.SmppBind;
import com.winnovature.unitia.util.test.Account;

public class T  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
			
				Refresh.getInsatnce().reload();
				
				Account.getInstance();
				
				RedisQueueConnectionPool.getInstance().reload();
			
			
				SmppBind.getInstance().print();
				gotosleep();

			}catch(Exception e){
				
			}
		}
	}

	private void gotosleep() {


		try{
			Thread.sleep(1000L);
		}catch(Exception e){
			
		}
	}
}






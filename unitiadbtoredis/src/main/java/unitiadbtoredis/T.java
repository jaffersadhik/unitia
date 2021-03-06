package unitiadbtoredis;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.queue.RedisQueue;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;

public class T  extends Thread{


	public void run(){
		
		while(true){
			
			try{
				Refresh.getInsatnce().reload();
				RedisQueue.getInstance().reload();

				
				DBReceiver.GRACESTOP=ConfigParams.getInstance().getProperty(ConfigKey.GRACE_STOP).equals("1");
				PollerStartup.updateUsers();

				gotosleep();

			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private void gotosleep() {


		try{
			Thread.sleep(1000L);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}






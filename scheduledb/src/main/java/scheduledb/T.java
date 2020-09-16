package scheduledb;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.reader.QueueTon;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;


public class T  extends Thread{


	public void run(){
		
		while(true){
			
			try{
				Refresh.getInsatnce().reload();
				RedisQueueConnectionPool.getInstance().reload();
				QueueTon.getInstance().checkQueueAvailablity();
				RedisReceiver.GRACESTOP=ConfigParams.getInstance().getProperty(ConfigKey.GRACE_STOP).equals("1");
				
				gotosleep();

			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private void gotosleep() {


		try{
			Thread.sleep(50L);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}






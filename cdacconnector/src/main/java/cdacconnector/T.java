package cdacconnector;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.db.Kannel;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.SMSCMaxQueue;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;

public class T  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				Refresh.getInsatnce().reload();
				unitiaroute.Refresh.getInsatnce().reload();
				DomesticCredit.getInstance().reload();
				RedisQueueConnectionPool.getInstance().reload();

				Kannel.getInstance().reload();
				SMSCMaxQueue.getInstance().reload();
				com.winnovature.unitia.util.misc.kannel.reload();
				gotosleep();
				
				//printrouteInfo();

			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
	}
/*
	private void printrouteInfo() {
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("username", "sys");
		logmap.put("logname", "routeinfo");
		logmap.putAll(Route.getInstance().getRoute());
		new FileWrite().write(logmap);
		
	}
*/
	private void gotosleep() {


		try{
			Thread.sleep(10L);
		}catch(Exception e){
			
		}
	}
}






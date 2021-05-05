package unitiacore;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.account.MissedCallForward;
import com.winnovature.unitia.util.account.MissedCallSMS;
import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.account.ShortCodeAccount;
import com.winnovature.unitia.util.account.VMNAccount;
import com.winnovature.unitia.util.db.Kannel;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.SMSCMaxQueue;
import com.winnovature.unitia.util.queue.kannelQueue;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;
import com.winnovature.unitia.util.template.Template;

import unitiacore.threadpool.DomesticCredit;
import unitiacore.threadpool.QueueTon;
import unitiaroute.Entity;

public class T10  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				Template.getInstance().reload();
				gotosleep();
				
			//	printrouteInfo();

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
			Thread.sleep(600000L);
		}catch(Exception e){
			
		}
	}
}






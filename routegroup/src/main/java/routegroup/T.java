package routegroup;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.db.kannelQueue;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;

import unitiaroute.RouteGroup;




public class T  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				PushAccount.instance().reload();
				RedisQueueConnectionPool.getInstance().reload();
				SenderidRouting.getInstance().reload();
				MobileRouting.getInstance().reload();
				Route.getInstance().reload();
				InternationalRoute.getInstance().reload();
				kannelQueue.getInstance().reload();
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






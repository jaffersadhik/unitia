package dnsql;


import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.misc.RouterLog;
import com.winnovature.unitia.util.queue.DLRQueue;
import com.winnovature.unitia.util.queue.RedisQueue;
import com.winnovature.unitia.util.reader.QueueTon;

import unitiaroute.ReRouting;



public class T  extends Thread{

	





	public void run(){
		
		while(true){
			
			try{
				new RouterLog().routerlog("dnsql", "thread", "execute Thread");
				Refresh.getInsatnce().reload();
				
				RedisQueue.getInstance().reload();
				DLRQueue.getInstance().reload();
				ReRouting.getInstance().reload();

				QueueTon.getInstance().checkQueueAvailablity();
				DLRQueue.getInstance().reload();
				com.winnovature.unitia.util.db.Kannel.getInstance().reload();
				PollerStartup.updateUsers();
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
			e.printStackTrace();
		}
	}
}






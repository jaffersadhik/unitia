package unitiacore;

import com.winnovature.unitia.util.account.MissedCallForward;
import com.winnovature.unitia.util.account.MissedCallSMS;
import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.account.ShortCodeAccount;
import com.winnovature.unitia.util.account.VMNAccount;
import com.winnovature.unitia.util.misc.SMSCMaxQueue;
import com.winnovature.unitia.util.queue.RedisQueue;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;

import unitiacore.threadpool.DomesticCredit;
import unitiacore.threadpool.QueueTon;

public class T  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				Refresh.getInsatnce().reload();
				unitiaroute.Refresh.getInsatnce().reload();
				DomesticCredit.getInstance().reload();
				RedisQueue.getInstance().reload();
				QueueTon.getInstance().checkQueueAvailablity();

				SMSCMaxQueue.getInstance().reload();
				MissedCallSMS.getInstance().reload();
				VMNAccount.getInstance().reload();
				MissedCallForward.getInstance().reload();
				ShortCodeAccount.getInstance().reload();
				App.printThreadStatus();
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






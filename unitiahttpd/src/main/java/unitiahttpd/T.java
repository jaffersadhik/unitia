package unitiahttpd;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.queue.RedisQueue;
import com.winnovature.unitia.util.test.Account;

public class T  extends Thread{

	public void run(){
		
		while(true){
			
			try{
				
				Account.getInstance().reload();

				Refresh.getInsatnce().reload();
				
				RedisQueue.getInstance().reload();
			
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

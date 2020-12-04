package unitiatablereader;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.queue.RedisQueue;

public class T  extends Thread{


	public void run(){
		
		while(true){
			
			try{
				Refresh.getInsatnce().reload();
				RedisQueue.getInstance().reload();

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






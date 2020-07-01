package unitiacore;

import com.winnovature.unitia.util.account.Refresh;

import unitiacore.threadpool.ThreadPoolTon;

public class T  extends Thread{

	public void run(){
		
		while(true){
			
			try{
	
				ThreadPoolTon.getInstance().reload();
				Refresh.getInsatnce().reload();
				unitiaroute.Refresh.getInsatnce().reload();
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

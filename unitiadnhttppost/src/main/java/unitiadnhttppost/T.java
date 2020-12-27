package unitiadnhttppost;


import com.winnovature.unitia.util.account.MissedCallForward;
import com.winnovature.unitia.util.account.MissedCallSMS;
import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.account.VMNAccount;

public class T  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				
				Refresh.getInsatnce().reload();
				
				VMNAccount.getInstance().reload();
				MissedCallForward.getInstance().reload();
				MissedCallSMS.getInstance().reload();
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






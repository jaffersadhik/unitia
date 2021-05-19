package queuecheck;

import com.winnovature.unitia.util.misc.TPSCheck;

public class TTPSControll extends Thread {

	public void run(){
		
		while(true){
			try{
				
				TPSCheck.getInstance().reload();
				TPSCheck.getInstance().clearCounter();
				gotosleep();
			}catch(Exception e){
				gotosleep();

			}
		}
	}
	
	public void gotosleep(){
		
		try{
			Thread.sleep((1*60*60*1000));
		}catch(Exception e){
			
		}
	}
}

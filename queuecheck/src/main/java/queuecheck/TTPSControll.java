package queuecheck;

import com.winnovature.unitia.util.misc.TPSCheck;

public class TTPSControll extends Thread {

	public void run(){
		
		while(true){
			try{
				
				TPSCheck.getInstance().reload();
				TPSCheck.getInstance().clearCounter();
				
			}catch(Exception e){
				
			}
		}
	}
	
	public void gotosleep(){
		
		try{
			Thread.sleep(10000L);
		}catch(Exception e){
			
		}
	}
}

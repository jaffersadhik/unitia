package com.winnovature.unitia.httpinterface.servlet;

import com.winnovature.unitia.util.account.PushAccount;

public class T  extends Thread{

	public void run(){
		
		while(true){
			
			try{
				
				PushAccount.instance().reload();
				
				gotosleep();
				
			}catch(Exception e){
				
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

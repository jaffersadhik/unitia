package com.winnovature.unitia.util;

import com.winnovature.unitia.util.account.PushAccount;

public class Refresh {

	private static Refresh obj=null;
	
	private Refresh(){
		
		new T().start();
	}
	
	public static Refresh getInsatnce(){
		
		if(obj==null){
			
			obj=new Refresh();
		}
		
		return obj;
	}
	
	public void reload(){
		
		NumberingPlan.getInstance().reload();
		Kannel.getInstance().reload();
		RouteGroup.getInstance().reload();
		Route.getInstance().reload();
		MobileRouting.getInstance().reload();
		SenderidRouting.getInstance().reload();
		SenderidSwapping.getInstance().reload();
		ReRouting.getInstance().reload();
		SenderidSwapScheduling.getInstance().reload();
		Countrycode.getInstance().reload();
		InternationalRoute.getInstance().reload();
		InternationalSenderidSwapping.getInstance().reload();
		WhiteListedSenderid.getInstance().reload();
		MobileBlackList.getInstance().reload();
		SenderidBlackList.getInstance().reload();
		SMSPatternBlackList.getInstance().reload();
		SMSPatternAllowed.getInstance().reload();
		SMSPatternFiltering.getInstance().reload();
		PushAccount.instance().reload();
		

	}
	
	class T extends Thread{
		
		public void run(){
			
			while(true){
				
				Refresh.getInsatnce().reload();
			
				gotosleep();
			}
		}

		private void gotosleep() {
			
			try{
				
				Thread.sleep(100L);
			}catch(Exception e){
				
			}
		}
	}
}

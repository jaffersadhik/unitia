package com.winnovature.unitia.util.datacache.account;

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
		
		PushAccount.instance().reload();
		BillingTableRouting.getInstance().reload();
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
				
				Thread.sleep(20L);
			}catch(Exception e){
				
			}
		}
	}
}

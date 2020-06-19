package com.winnovature.unitia.util.routing;

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
		SplitGroup.getInstance().reload();
		Kannel.getInstance().reload();
		RouteGroup.getInstance().reload();
		Route.getInstance().reload();
		MobileRouting.getInstance().reload();
		SenderidRouting.getInstance().reload();
		SenderidSwapping.getInstance().reload();
		ReRouting.getInstance().reload();
		SenderidSwapScheduling.getInstance().reload();
		Countrycode.getInstance().reload();
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

package queuecheck;

public class TReRouteKannel  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				ReRouteKannelCount.getInstance().doProcess();
				ReRouteKannelMax.getInstance().doProcess();
				gotosleep();
			}catch(Exception e){
				
				e.printStackTrace();
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






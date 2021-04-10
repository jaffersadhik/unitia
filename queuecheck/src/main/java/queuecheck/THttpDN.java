package queuecheck;

public class THttpDN  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				HttpDNCount.getInstance().doProcess();
				HttpDNMax.getInstance().doProcess();
				gotosleep();
			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
	}

	

	private void gotosleep() {


		try{
			Thread.sleep(10000L);
		}catch(Exception e){
			
		}
	}
}






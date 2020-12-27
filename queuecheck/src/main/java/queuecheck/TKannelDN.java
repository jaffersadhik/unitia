package queuecheck;

public class TKannelDN  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				DLRCount.getInstance().doProcess();
				gotosleep();
			}catch(Exception e){
				
				e.printStackTrace();
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






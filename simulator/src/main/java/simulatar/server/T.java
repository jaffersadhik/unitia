package simulatar.server;

public class T  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
			
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






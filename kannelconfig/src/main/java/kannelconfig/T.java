package kannelconfig;

public class T  extends Thread{

	
	
	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{

				
				
			

				
				
				gotosleep();

			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
	}


	private void gotosleep() {


		try{
			Thread.sleep(100L);
		}catch(Exception e){
			
		}
	}
}






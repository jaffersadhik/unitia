package smpp2;

public class T2  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
			
			
				BindDAO.getInstance().insert();
				
				SessionCount.getInstance().reload();
				
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






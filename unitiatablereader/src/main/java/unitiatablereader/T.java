package unitiatablereader;

import com.winnovature.unitia.util.account.Refresh;

public class T  extends Thread{


	public void run(){
		
		while(true){
			
			try{
				Refresh.getInsatnce().reload();
				
				PollerStartup.updateUsers();
				
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
			e.printStackTrace();
		}
	}
}






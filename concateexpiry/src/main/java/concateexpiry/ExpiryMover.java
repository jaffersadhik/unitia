package concateexpiry;

import java.util.List;
import java.util.Map;

public class ExpiryMover extends Thread {

	public void run(){
		
		
		while(true){
			
			
			doProcess();
			
			gotosleep();
		}
	}

	private void gotosleep() {
	try{
			Thread.sleep(5*60*1000);
		}catch(Exception e){
			
		}
		
	}

	private void doProcess() {
	
		try{
			List<Map<String,Object>> expirylist=getData();
			
			if(expirylist!=null&&expirylist.size()>0){
				
				for(int i=0;i<expirylist.size();i++){
					
					sendtoQueue(expirylist.get(i));
				}
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
	}

	private void sendtoQueue(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
	}

	private List<Map<String, Object>> getData() {
		// TODO Auto-generated method stub
		return null;
	}
}

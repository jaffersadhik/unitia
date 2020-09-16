package unitiacore.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;


public class SMSReader extends Thread{


	String poolname=null;
	public SMSReader(String tablename){
		this.poolname=tablename;
	}
	public void run(){
		System.out.println("SMSReader run start + tablename "+poolname);

		while(true){
			
			List<Map<String,Object>> datalist=getData();
			
			if(datalist!=null&& datalist.size()>0){
				
				untilPersist(datalist);
				
				
			}else{
				
				gotosleep();
			}
		}
	}

	
	private void untilPersist(List<Map<String, Object>> datalist) {


		for(int i=0,max=datalist.size();i<max;i++){
			
	
			if(poolname.equals("commonpool")||poolname.equals("kannelconnector")||poolname.equals("redissender")){
				
				new SMSWorker( poolname,  datalist.get(i)).doProcess();
				
			}else{
			
				new DNGenWorker( poolname,  datalist.get(i)).doProcess();
			}
		}
			
		
	}
	private void gotosleep() {
		
		try{
			Thread.sleep(50L);
			
		}catch(Exception e){
			
		}
		
	}
	private List<Map<String, Object>> getData() {
		
		try{
			
		BlockingQueue q = QueueTon.getInstance().getQ(poolname);

		List records = new ArrayList(5);

		q.drainTo(records,1000);
		
		return records;
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
}


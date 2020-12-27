package unitiacore.threadpool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.winnovature.unitia.util.db.InMemoryQueue;

public class QueueTon {

	private static QueueTon obj=null;
	
	private Map<String,BlockingQueue> availavleTable=new HashMap<String,BlockingQueue>();
	
	
	private QueueTon(){
		
	
	}
	
	public static QueueTon getInstance(){
		
		if(obj==null){
			
			obj=new QueueTon();
		}
		
		return obj;
	}
	
	public boolean isAvailable(String tablename){
		
		if(!availavleTable.containsKey(tablename)){
			
			start(tablename);
		}
		
		return availavleTable.containsKey(tablename);
	}
	
	
	public boolean mayPush(String tablename){
		
		if(!availavleTable.containsKey(tablename)){
			
			start(tablename);
		}
		return availavleTable.get(tablename).size()<15;
	}
	
	private void start(String tablename) {
		
		availavleTable.put(tablename, new LinkedBlockingQueue());
		int max=2;
		
		for(int i=0;i<max;i++){
			
			if(tablename.equals("commonpool")||tablename.equals("dngenpool")||tablename.equals("kannelconnector")||tablename.equals("redissender")){
				
				new SMSReader(tablename).start();
				
			}
		}
		
		
	}
	
	public BlockingQueue getQ(String tablename)
	{
		return availavleTable.get(tablename);
	}
	
	
	public void checkQueueAvailablity(){
		
	
		new InMemoryQueue().insertQueueintoDB(availavleTable);

	}
	
	public void add(String tablename,Map<String,Object> msgmap){
		
		if(!availavleTable.containsKey(tablename)){
			
			start(tablename);
		}

		
			availavleTable.get(tablename).offer(msgmap);
	}
}

package com.winnovature.unitia.util.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.winnovature.unitia.util.db.SplitupDAO;
import com.winnovature.unitia.util.db.SubmissionDAO;

public class SplitupReader extends Thread{

	String tablename=null;
	public SplitupReader(String tablename){
		this.tablename=tablename;
	}
	public void run(){
		System.out.println("SplitupReader run start + tablename "+tablename);

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


		while(true){
			
			if(new SplitupDAO().insert(tablename, datalist)){
			
				return;
			}else{
				
				gotosleep();
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
			
		BlockingQueue q = QueueTon.getInstance().getQ(tablename);

		List records = new ArrayList(100);

		q.drainTo(records,100);
		
		return records;
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
}


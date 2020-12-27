package com.winnovature.unitia.util.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.winnovature.unitia.util.dao.Insert;
import com.winnovature.unitia.util.db.SubmissionDAO;

public class ScheduleReader extends Thread{

	String poolname=null;
	public ScheduleReader(String poolname){
		this.poolname=poolname;
	}
	public void run(){
		System.out.println("ScheduleReader run start" + poolname+  "poolname");

		while(true){
			
			List<Map<String,Object>> datalist=getData();
			
			if(datalist!=null&& datalist.size()>0){
				
				untilPersist(datalist);
				
				System.out.println(datalist.size()+" perssisted in Schedule");
			}else{
				
				gotosleep();
			}
		}
	}
	private void untilPersist(List<Map<String, Object>> datalist) {


		while(true){
			
			if(new Insert().insert(poolname, datalist)){
			
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
			
		BlockingQueue q = QueueTon.getInstance().getQ(poolname);

		List records = new ArrayList(100);

		q.drainTo(records,100);
		
		return records;
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
}


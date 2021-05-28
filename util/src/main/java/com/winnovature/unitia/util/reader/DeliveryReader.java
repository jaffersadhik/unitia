package com.winnovature.unitia.util.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.winnovature.unitia.util.db.ReportDAO;
import com.winnovature.unitia.util.processor.DNProcessor;

public class DeliveryReader extends Thread{

	String poolname=null;
	public DeliveryReader(String poolname){
		this.poolname=poolname;
	}
	public void run(){
		
		System.out.println("DeliveryReader run start + poolname "+poolname);

		while(true){
			
			List<Map<String,Object>> datalist=getData();
			
			if(datalist!=null&& datalist.size()>0){
				
				System.out.println(" fetching size : "+datalist.size());

				untilPersist(datalist);
				
		
			}else{
				
				gotosleep();
			}
		}
	}
	
	private void updateMap(List<Map<String, Object>> datalist) throws IOException {
		
		
		for(int i=0,max=datalist.size();i<max;i++){
			
			Map<String, Object> data=datalist.get(i);
			
			new DNProcessor(data,new HashMap()).doProcess();
			
		
		}
		
		
	}
	
	
	private void untilPersist(List<Map<String, Object>> datalist) {


		while(true){
			
			if(datalist==null || datalist.size()<1){
				
				return;
			}
			
			System.out.println(" untilPersist : "+datalist.size());
			
			if(new ReportDAO().insert("reportlog_delivery",datalist)){
			
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

		List records = new ArrayList(15000);

		q.drainTo(records,15000);
		
		return records;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

}


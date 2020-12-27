package com.winnovature.unitia.util.reader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.winnovature.unitia.util.db.InMemoryQueue;
import com.winnovature.unitia.util.misc.MapKeys;

public class QueueTon {

	private static QueueTon obj=null;
	
	private Map<String,BlockingQueue> availavleTable=new HashMap<String,BlockingQueue>();
	
	boolean isVailable=true;
	
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
		
		return true;
	}
	
	
	
	public boolean isVailable() {
		
		return isVailable;
	}

	private void start(String tablename) {
		
		availavleTable.put(tablename, new LinkedBlockingQueue());
		int max=5;
		
	
		
		for(int i=0;i<max;i++){
			
			if(tablename.equals("submissionpool")){
				
				new SubmissionReader(tablename).start();
				
			}else if(tablename.equals("dnreceiverpool")){
				
				new DeliveryReader(tablename).start();
				
			}else if(tablename.equals("schedulepool")){
				
				new ScheduleReader(tablename).start();
				
			}else if(tablename.equals("dnpostpool")){
				
				new DNPostReader(tablename).start();
			}else if(tablename.equals("splitup")){
				
				new SplitupReader(tablename).start();
			}
		}
		
		
	}
	
	public BlockingQueue getQ(String tablename)
	{
		return availavleTable.get(tablename);
	}
	
	
	public void checkQueueAvailablity(){
		
		long size=0;
		
		Iterator itr=availavleTable.keySet().iterator();
		
		while(itr.hasNext()){
			
			size+=availavleTable.get(itr.next()).size();
		}
		
		if(size>1000){
			
			isVailable=false;
		}else{
			isVailable=true;
		}
		
		new InMemoryQueue().insertQueueintoDB(availavleTable);
	}
	
	public void add(String poolname,Map<String,Object> msgmap){
		
		if(!availavleTable.containsKey(poolname)){
			
			start(poolname);
		}

		
		if(poolname.equals("submissionpool")){

			if(msgmap.get(MapKeys.MSGLIST)!=null){
		
				List<Map<String,Object>> list=(List<Map<String,Object>>)msgmap.get(MapKeys.MSGLIST);
			
				for(int i=0,max=list.size();i<max;i++){
					
					QueueTon.getInstance().add("splitup",list.get(i) );

				}

			}
		}
		availavleTable.get(poolname).offer(msgmap);
	}

	public boolean mayPush(String poolname) {
		
		return availavleTable.get(poolname).size()<1000;
	}
}

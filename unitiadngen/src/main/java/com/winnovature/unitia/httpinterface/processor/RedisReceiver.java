package com.winnovature.unitia.httpinterface.processor;

import java.util.Map;

import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redis.RedisReader;

public class RedisReceiver extends Thread {

	String poolname=null;
	public RedisReceiver(String poolname){
		
		this.poolname=poolname;
	}
	public void run(){
		
		RedisReader reader=new RedisReader();
		while(true){
			
			if(ThreadPoolTon.getInstance().isAvailable(poolname)){
				
			Map<String,String> data=reader.getData(poolname);
			
			if(data!=null){
				
				if(poolname.equals("schedule")){
					
					ThreadPoolTon.getInstance().doProcess(poolname, "schedule", data);

				}else if(poolname.equals("billingpool")){
					
					ThreadPoolTon.getInstance().doProcess(poolname, "billing", data);

				}else if(poolname.equals("dngenpool")){
					
					ThreadPoolTon.getInstance().doProcess(poolname, "dngen", data);

				}else if(poolname.equals("dnreceiverpool")){
					

					ThreadPoolTon.getInstance().doProcess(poolname, "dnreceiver", data);
				}else if(poolname.equals("otpretry")){
					
					ThreadPoolTon.getInstance().doProcess(poolname, "otpretry", data);

				}else if(poolname.equals("msgretry")){
					
					ThreadPoolTon.getInstance().doProcess(poolname, "msgretry", data);

				}else if(poolname.equals("dnretry")){
					
					ThreadPoolTon.getInstance().doProcess(poolname, "dnretry", data);

				}else{

					String scheduletype=data.get(MapKeys.SCHEDULE_TYPE);
					
					if(scheduletype!=null&&scheduletype.equals("trai")){
						
						ThreadPoolTon.getInstance().doProcess(poolname, "trai", data);

					}else{
					
						ThreadPoolTon.getInstance().doProcess(poolname, "sms", data);


					}

				}
			}else{
				
				gotosleep();
			}
			}else{
				
				gotosleep();
			}
			
		}
	}
	private void gotosleep() {
		
		try{
			
			Thread.sleep(5L);
		}catch(Exception e){
			
		}
	}
}

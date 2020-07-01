package unitiacore.threadpool;

import java.util.Map;

import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redis.RedisReader;

public class RedisReceiver implements Runnable {

	String poolname=null;
	
	public RedisReceiver(String poolname){
		
		this.poolname=poolname;
	}
	public void run(){
		
		RedisReader reader=new RedisReader();
		
		while(true){
			
				
			Map<String,String> data=reader.getData(poolname);
			
			if(data!=null){
				
				String pooltype=getPoolType(poolname,data);
					
				if(pooltype.equals("sms")){
					
					new SMSWorker( poolname, pooltype, data).doProcess();
					
				}else if(pooltype.equals("billing")){
					
					new BillingWorker( poolname, pooltype, data).doProcess();
					
				}else if(pooltype.equals("dngen")){
					
					new DNGenWorker( poolname, pooltype, data).doProcess();
					
				}else if(pooltype.equals("dnreceiver")){
					
					new DNWorker( poolname, pooltype, data).doProcess();
					
				}else if(pooltype.equals("schedule")){
					
				}
			}else{
				
				return;
				
			}
			}
			
		
			
		}
	private String getPoolType(String poolname2, Map<String, String> data) {
			
			if(poolname.equals("schedule")){
				
				 return "schedule";

			}else if(poolname.equals("billingpool")){
				
				return "billing";

			}else if(poolname.equals("dngenpool")){
				
				return  "dngen";

			}else if(poolname.equals("dnreceiverpool")){
				

				return  "dnreceiver";
			}else if(poolname.equals("otpretry")){
				
				return  "otpretry";

			}else if(poolname.equals("msgretry")){
				
				return  "msgretry";

			}else if(poolname.equals("dnretry")){
				
				return  "dnretry";

			}else{

				String scheduletype=data.get(MapKeys.SCHEDULE_TYPE);
				
				if(scheduletype!=null&&scheduletype.equals("trai")){
					
					return  "trai";

				}else{
				
					return "sms";


				}

			}	
	}
	private void gotosleep2MS() {
		
		try{
			
			Thread.sleep(2L);
			
		}catch(Exception e){
			
		}
	
		
	}
	private void gotosleep() {
		
		try{
			
			Thread.sleep(750L);
		}catch(Exception e){
			
		}
	}
}

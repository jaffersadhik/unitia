package unitiacore;

import java.util.Iterator;
import java.util.Map;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.redis.RedisQueuePool;

import unitiacore.threadpool.ThreadPoolTon;

public class T  extends Thread{

	long start1=System.currentTimeMillis();
	long start2=System.currentTimeMillis();

	public void run(){
		
		while(true){
			
			try{
				long diff=System.currentTimeMillis()-start1;
				if(diff>1000){
				ThreadPoolTon.getInstance().reload();
				Refresh.getInsatnce().reload();
				unitiaroute.Refresh.getInsatnce().reload();
				start1=System.currentTimeMillis();
				}
				
				diff=System.currentTimeMillis()-start2;
				if(diff>250){
					start2=System.currentTimeMillis();

					RedisQueuePool.getInstance().reload();
					
					Map<String,String> queuemap=RedisQueuePool.getInstance().getQueueCount();
					
					Iterator itr=queuemap.keySet().iterator();
					
					while(itr.hasNext()){
					
						String queuename=itr.next().toString();
								
						if(!(queuename.startsWith("allqueuecount")||queuename.startsWith("_redisreader"))){
							try{
							if(ThreadPoolTon.getInstance().isAvailableForRetry(queuename+"_redisreader")){
								
								ThreadPoolTon.getInstance().doProcess(queuename+"_redisreader", "sms", null);
							}
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}
				}
				gotosleep();

			}catch(Exception e){
				
			}
		}
	}

	private void gotosleep() {


		try{
			Thread.sleep(250L);
		}catch(Exception e){
			
		}
	}
}






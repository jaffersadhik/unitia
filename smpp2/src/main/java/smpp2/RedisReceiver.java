package smpp2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.redis.RedisReader;

public class RedisReceiver extends Thread{

	String redisid=null;
	String queuename=null;
	String poolname=null;
	RedisReader reader=new RedisReader();
	DNWorker dnworker=new DNWorker();

	public RedisReceiver(String redisid,String queuename){
		
		this.redisid=redisid;
		this.queuename=queuename;
		this.poolname=redisid+"~"+queuename;
	}
	
	public void run(){
		
		while(true){
		
			List<Map<String,Object>> dnlist=getData();
			
			if(dnlist==null){
				
				PollerStartup.getInstance(redisid, queuename).runninguser.remove(poolname);
			
				return ;
			}
			
			dnworker.doProcess(dnlist);
		}
	}

	private List<Map<String, Object>> getData() {
		
		List<Map<String,Object>> dnlist=new ArrayList<Map<String,Object>>();
	
		long start=System.currentTimeMillis();
		while(true){
			Map<String,Object> data=reader.getData(queuename, redisid);
			if(data==null){
				
				return null;
			}
			
			dnlist.add(data);
			
			if(dnlist.size()>9||(System.currentTimeMillis()-start)>250){
			
				break;
			}
		}
		return dnlist;
	}
}

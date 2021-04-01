package smpp2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.redis.RedisReader;

public class RedisReceiver extends Thread{

	String systemid=null;
	String redisid=null;
	String queuename=null;
	String poolname=null;
	RedisReader reader=new RedisReader();
	DNWorker dnworker=new DNWorker();

	public RedisReceiver(String systemid,String redisid,String queuename){
		
		
		this.systemid=systemid;
		this.redisid=redisid;
		this.queuename=queuename;
		this.poolname=redisid+"~"+queuename;
	}
	
	public void run(){
		
		while(true){
		

			Map<String,Object> logmap=new HashMap<String,Object>();

			logmap.put("username","sys");
			logmap.put("redisid",redisid);
			logmap.put("queuename",queuename);
			logmap.put("logname","smppredispoller");
		

			List<Map<String,Object>> dnlist=getData();
			
			if(dnlist==null){
				
				logmap.put("data","null");

				new FileWrite().write(logmap);


				PollerStartup.getInstance(redisid, queuename).runninguser.remove(poolname);
			
				return ;
			}
			
			logmap.put("data size",dnlist.size());

			new FileWrite().write(logmap);
			
			dnworker.doProcess(systemid,dnlist);
		}
	}

	private List<Map<String, Object>> getData() {
		
		List<Map<String,Object>> dnlist=new ArrayList<Map<String,Object>>();
	
		long start=System.currentTimeMillis();
		while(true){
			
			Map<String,Object> data=reader.getData(queuename, redisid);
			if(data==null){

				break;
			}
			
			dnlist.add(data);
			
			if(dnlist.size()>9||(System.currentTimeMillis()-start)>250){
			
				break;
			}
		}
		if(dnlist.size()<1){
			
			return null;
		}
		return dnlist;
	}
}

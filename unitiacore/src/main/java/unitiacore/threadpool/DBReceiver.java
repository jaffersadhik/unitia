package unitiacore.threadpool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.dao.Select;
import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redis.QueueSender;
import com.winnovature.unitia.util.redis.RedisQueuePool;


public class DBReceiver extends Thread {

	String poolname=null;

	QueueSender queuesender=new QueueSender();
	
	Select select=new Select();
	
	Log log=new Log();
	
	public DBReceiver(String poolname){
		
		this.poolname=poolname;
	}
	public void run(){
		
		while(true){
			
			if(RedisQueuePool.getInstance().isAvailableQueue(poolname, true)){
				
			
			
			List<Map<String,String>> data=select.getData(poolname);
			
			if(data!=null&&data.size()>0){
				
				for(int i=0;i<data.size();i++){
					
					sendUntilSuccess(data.get(i));
				}
			}else{
				
				gotosleep();
			}
			
			}else{
				
				gotosleep();
			}
		}
	}
	private void sendUntilSuccess(Map<String, String> map) {
		
		Map<String,String> logmap=new HashMap<String,String>();
		logmap.put("module", "db reader");
	
		while(true){
			
			if(queuesender.sendL( poolname, map, true, logmap)){
				
				select.delete(poolname,Long.parseLong(map.get(MapKeys.MSGID)));
				logmap.putAll(map);
				logmap.put("db reader status", "successfully sent to "+poolname+" queue");
				log.log(logmap);
				return;
			}
			
			gotosleep();
		}
		
	}
	private void gotosleep() {
		
		try{
			
			Thread.sleep(5L);
		}catch(Exception e){
			
		}
	}
}

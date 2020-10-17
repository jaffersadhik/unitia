package logs;

import java.util.Iterator;
import java.util.Map;

import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;
import com.winnovature.unitia.util.redis.RedisQueuePool;

public class App 
{
	
	
	static FileWrite log =new FileWrite();
	
    public static void doProcess() 
    {
    	start("logspool");
	}
    
    

	private static void start(String poolname) {
		
	
		for(int i=0;i<5;i++){
			Map<String, RedisQueuePool> map=RedisQueueConnectionPool.getInstance().getPoolMap();

			Iterator itr=map.keySet().iterator();
			
			while(itr.hasNext()){
				
				String redisid=itr.next().toString();
				String logstring="poolname :"+poolname+" RedisReceiver startted for "+redisid;
				System.out.println(logstring);
		
			new RedisReceiver(poolname,redisid).start();



		}
		
		}
		
	}
    



	
}


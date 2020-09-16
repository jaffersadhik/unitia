package dngencore;

import java.util.Iterator;
import java.util.Map;

import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.Prop;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;
import com.winnovature.unitia.util.redis.RedisQueuePool;

import unitiacore.threadpool.RedisReceiver;

public class App 
{
 
    public static void main(String[] args) throws Exception
    {
    	Prop.getInstance();
    	new T().start();

    	start("dngenpool");


     }

	private static void start(String poolname) {
		
		for(int i=0;i<1;i++){
			
			Map<String, RedisQueuePool> map=RedisQueueConnectionPool.getInstance().getPoolMap();

			Iterator itr=map.keySet().iterator();
			
			while(itr.hasNext()){
				
				String redisid=itr.next().toString();
				String logstring="poolname :"+poolname+" RedisReceiver startted for "+redisid;
				System.out.println(logstring);
		
			Log.log(logstring);

				new RedisReceiver(i,poolname,redisid).start();
			
			}
		}
		
	}
    
}


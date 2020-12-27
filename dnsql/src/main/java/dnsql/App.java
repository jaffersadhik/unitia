package dnsql;


import java.util.Iterator;
import java.util.Map;

import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.Prop;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;
import com.winnovature.unitia.util.redis.RedisQueuePool;


public class App 
{
 
    public static void main(String[] args) throws Exception
    {
    	Prop.getInstance();
    	
    	new T().start();
    	
    	
    	start1("appspool");

    	

     }

	private static void start1(String poolname) {
		
		
		

		for(int i=0;i<1;i++){
			

			Map<String, RedisQueuePool> map=RedisQueueConnectionPool.getInstance().getPoolMap();

			Iterator itr=map.keySet().iterator();
			
			while(itr.hasNext()){
				
				String redisid=itr.next().toString();
				String logstring="poolname :"+poolname+" RedisReceiver startted for "+redisid;
				System.out.println(logstring);
		
			Log.log(logstring);
			new AppsReceiver(poolname,redisid).start();

			
			}
		
		}

	}    




}


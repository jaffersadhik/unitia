package dnpostdb;

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

		System.out.println("start");

    	start("dnpostpool");
    	
    
    	

     }

	private static void start(String poolname) {
		
		
		try{
		
		
		for(int i=0;i<3;i++){

			Map<String, RedisQueuePool> map=RedisQueueConnectionPool.getInstance().getPoolMap();

			System.out.println(map);
			Iterator itr=map.keySet().iterator();
			
			while(itr.hasNext()){
				
				String redisid=itr.next().toString();
				String logstring="poolname :"+poolname+" RedisReceiver startted for "+redisid;
				System.out.println(logstring);
		
			new RedisReceiver(poolname,redisid).start();

		}
			
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	}



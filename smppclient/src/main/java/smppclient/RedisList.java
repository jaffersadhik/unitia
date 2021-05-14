package smppclient;

import java.util.List;

import com.winnovature.unitia.util.misc.RedisInstance;
import com.winnovature.unitia.util.misc.RoundRobinTon;

public class RedisList {

	private static RedisList obj=new RedisList();
	
	List<String> redisidlist=RedisInstance.getInstance().getRedisInstanceList();

	private RedisList(){
		
	}
	
	
	public static RedisList getInstance(){
		
		if(obj==null){
			
			obj=new RedisList();
		}
		
		return obj;
	}
	

	public String getRedisId(){
		
		int index=RoundRobinTon.getInstance().getCurrentIndex("redisid.from.redis.queue.list", redisidlist.size());
	
		return redisidlist.get(index);
	}
}

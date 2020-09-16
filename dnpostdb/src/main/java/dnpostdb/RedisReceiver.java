package dnpostdb;

import java.util.Map;

import com.winnovature.unitia.util.reader.QueueTon;
import com.winnovature.unitia.util.redis.RedisReader;

public class RedisReceiver extends Thread {

	public static boolean GRACESTOP=false;


	String poolname=null;
	
	
	String redisid=null;
	
	public RedisReceiver(String poolname,String redisid){
		
		this.poolname=poolname;
		
		
		this.redisid=redisid;
	}
	public void run(){
		
		RedisReader reader=new RedisReader();
		
		while(!GRACESTOP){
			
				
			Map<String,Object> msgmap=null;
					
			if(QueueTon.getInstance().isVailable()){
				
				msgmap=reader.getData(poolname,redisid);
			}
			
			if(msgmap!=null){
				
				
				
				
				QueueTon.getInstance().add(poolname, msgmap);
				
				
			}else{
				
				gotosleep();		
				
				
			}
			
			}
			
		
			
		}
		
	private void gotosleep() {
		
		try{

			Thread.sleep(10L);
		}catch(Exception e){
			
		}
	}
}

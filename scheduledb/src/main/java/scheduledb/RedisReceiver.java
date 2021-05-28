package scheduledb;

import java.io.IOException;
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
				
				try {
					msgmap=reader.getData(poolname,redisid);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(-1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(-1);

				}
			}
			
			if(msgmap!=null){
				
				
				String tablename=null;
				
			 if(poolname.equals("schedulepool")){
					
					tablename="schedulepool";
					
				}
				
				QueueTon.getInstance().add(tablename, msgmap);
				
				
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

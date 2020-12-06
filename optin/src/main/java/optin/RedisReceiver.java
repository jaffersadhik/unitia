package optin;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redisinterface.RedisReader;

public class RedisReceiver extends  Thread {

	static boolean GRACESTOP=false;
	String poolname=null;
	String redisid =null;
	int threadid=0;
	long lastupdate=System.currentTimeMillis();
	String tname="";
	String id =ACKIdGenerator.getAckId();
	
	
	public RedisReceiver(int threadid,String poolname,String redisid){
		this.threadid=threadid;
		this.redisid=redisid;
		this.poolname=poolname;
		this.tname=this.threadid+" : "+this.redisid+" : "+ this.poolname;
	}
	public void run(){
		
		RedisReader reader=new RedisReader();
		
		while(!GRACESTOP){
			
			try{	
			long start=System.currentTimeMillis();	
			ping();	
			Map<String,Object> data=null;

			lastupdate=System.currentTimeMillis();
			
				data=reader.getData(poolname,redisid);

				
			
			if(data!=null){
				
				data.put("tname", tname);
				data.put(MapKeys.INSERT_TYPE, "submit");
	
					
					new SMSWorker(poolname,data).doOtp(redisid,tname);
					
					
				
				
				long end=System.currentTimeMillis();
				
				stats(poolname,redisid,start,end);
			}else{
				
				gotosleep();		
				
				
			}
			
			}catch(Exception e){
				
				ping(e);
			}
			}
			
		
			
		}

	private void stats(String poolname2, String redisid2, long start, long end) {
		
		Map<String,Object> logmap1=new HashMap<String,Object>();
		
		logmap1.put("username", "sys");
		logmap1.put("processtime",""+ (end-start));
		logmap1.put("queuename", poolname2);
		logmap1.put("redisid", redisid2);
		logmap1.put("logname", "totaltime");


        new FileWrite().write(logmap1);
		
	}
	private void ping(Exception e) {
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("logname", "routerpingerror");
		logmap.put("tname", tname);
		logmap.put("id",id);

		logmap.put("username", "sys");
		logmap.put("Threadname", this.getName());

		logmap.put("error", ErrorMessage.getMessage(e));

        new FileWrite().write(logmap);
		
	}
	private void ping() {
		
		lastupdate=System.currentTimeMillis();
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("username", "sys");
		logmap.put("logname", "routerping");
		logmap.put("tname", tname);
		logmap.put("id",id);

		logmap.put("Threadname", this.getName());

        new FileWrite().write(logmap);
		
	}
	private void gotosleep() {
		
		try{
			
			Thread.sleep(50L);
		}catch(Exception e){
			
		}
	}
	
	public boolean isDisplay(){
		
		long diff=System.currentTimeMillis()-lastupdate;
		
		if(diff>60000){
			
			return true;
		}
		
		return false;
	}
	
	public String getTName(){
		
		return tname;
	}
}

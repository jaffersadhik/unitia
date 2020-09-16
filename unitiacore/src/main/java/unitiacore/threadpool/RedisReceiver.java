package unitiacore.threadpool;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.processor.DNHttpPost;
import com.winnovature.unitia.util.redis.RedisReader;

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
	
				if(poolname.equals("commonpool")){
					
					new SMSWorker(poolname,data).doOtp();
					
					
				}else if(poolname.equals("dngenpool")){
					
					QueueTon.getInstance().add(poolname, data);

					
				}else if(poolname.equals("kannelretrypool")){
				
					
					try {
						new SMSProcessor().doRetryProcess(data);
					} catch (Exception e) {
						new FileWrite().logError("doRetryProcess", data, e);

					}
					
				}else if(poolname.equals("dnretrypool")){
					
					try {
						new SMSProcessor().doDNRetryProcess(data);
					} catch (Exception e) {
						new FileWrite().logError("doDNRetryProcess", data, e);

					}
					
				}else if(poolname.equals("otpretrypool")){
					
					try {
						new SMSProcessor().doOTPRetryProcess(data);
					} catch (Exception e) {
						new FileWrite().logError("doOTPRetryProcess", data, e);

					}
					
				}else if(poolname.equals("dnreceiverpool")){
					
					new DNGenWorker( poolname,  data).doProcess();
					
				}else if(poolname.equals("httpdn")){
					
					new DNHttpPost(data).doProcess();
					
				}else if(poolname.equals("otppool")){
					
					new SMSWorker(poolname,data).doOtp();
					
				}
				
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

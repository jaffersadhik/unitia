package smpp2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cloudhopper.smpp.SmppSession;
import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.QueueDBConnection;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.queue.RedisQueue;

public class PollerStartup {

	
	static Map<String,PollerStartup> instance=new HashMap<String,PollerStartup>();
	

	
	Set<String> runninguser=new HashSet<String>();
	
	String redisid=null;

	String queuename=null;
	
	String systemid=null;
	
	private PollerStartup(){
		
	}
	private PollerStartup(String redisid,String queuename){
	
		this.queuename=queuename;
		this.redisid=redisid;
		this.systemid=queuename.replace("smppdn_", "").trim();
	
	}
	
	

	public static PollerStartup getInstance(String redisid,String queuename) {
		
		String poolname=redisid+"~"+queuename;
		
		if(!instance.containsKey(poolname)){
			
			instance.put(poolname, new PollerStartup(redisid,queuename));
		}
		
		return instance.get(poolname);
		
	}
	
	public static void startConsumer() {
		
	
		try{
			
		
			Map<String,List<String>> queuemap=RedisQueue.getInstance().getSmppQueue();
			
		Iterator itr=queuemap.keySet().iterator();
		
		while(itr.hasNext()){
			
			String redisid=itr.next().toString();
			
			List<String> availableuser = queuemap.get(redisid);
			
			
			for(int i=0;i<availableuser.size();i++){
				
				Map<String,Object> logmap=new HashMap<String,Object>();

				String queuename=availableuser.get(i);
				logmap.put("queuename",queuename);
				logmap.put("logname","smppdnpollerstartup");
				String poolname=redisid+"~"+queuename;
			if(instance.get(poolname).isRunningUser(poolname)){
				logmap.put("status","poller already running ,skip the start poller ");
				new FileWrite().write(logmap);

				continue;
			}
			
			
			String systemid=instance.get(poolname).getSystemid();

			List<SmppSession> sessionlist=SessionStore.getInstance().rxsessionlist.get(systemid);
			
			if(sessionlist==null||sessionlist.size()<1){
				
				logmap.put("status","username not available in Sessionlist ,skip the start poller ");
				new FileWrite().write(logmap);
			
			}else{
				
				new RedisReceiver(systemid,redisid, queuename).start();
			
				instance.get(poolname).runninguser.add(poolname);
				logmap.put("status","the start poller ");
				new FileWrite().write(logmap);

			}
			
			}
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private String getSystemid() {
		// TODO Auto-generated method stub
		return systemid;
	}
	private boolean isRunningUser(String poolname) {
		return runninguser.contains(poolname);
	}

}

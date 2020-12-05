package unitiatablereader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.dao.Select;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redis.QueueSender;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;



public class DBReceiver extends Thread {

	String tablename=null;

	String username=null;
	
	String queuename=null;
	
	
	QueueSender queuesender=new QueueSender();
	
	Select select=new Select();
	
	FileWrite log=new FileWrite();
	
	public DBReceiver(String tablename,String username){
		
		this.tablename=tablename;
	
		this.username=username;
		
		queuename="commonpool";
			
	}
	
	
	
public String getQueueName(Map<String ,String > accountmap){
		
		

		if(accountmap.get(MapKeys.OPTIN_TYPE).equals("1")){
			
			return "optin";
			
		}else if(accountmap.get(MapKeys.OPTIN_TYPE).equals("2")){
			
			return "optout";
		}else if(!accountmap.get(MapKeys.DUPLICATE_TYPE).equals("0")){
			
			return "duplicate";
		}else{
		
			return "processor";
			
		}
	}


	public void run(){
		
		while(true){
			
			String redisid=RedisQueueConnectionPool.getInstance().getRedisId(queuename, true);
		
			Map<String,Object> logmap=new HashMap<String,Object>();

			logmap.put("username",username);
			logmap.put("tablename",tablename);
			logmap.put("tablereaderlog","y");
			logmap.put("redisid",redisid);

			if(redisid!=null){
				
			long start=System.currentTimeMillis();
			List<Map<String,Object>> data=select.getData(tablename,username,true,logmap);
			
			if(data!=null&&data.size()>0){
				
				long end=System.currentTimeMillis();
			
				logmap.put("record count ",""+data.size());
				
				stats("select",username,tablename,start,end,data.size());
				
				long start1=System.currentTimeMillis();

					for(int i=0;i<data.size();i++){
					
					sendUntilSuccess(data.get(i));
					
					}
					end=System.currentTimeMillis();
					
				stats("sendtoredis",username,tablename,start1,end,data.size());
						
				start1=System.currentTimeMillis();
				deleteUntilSuccess(data);
				end=System.currentTimeMillis();
				stats("delete",username,tablename,start1,end,data.size());
				end=System.currentTimeMillis();
				stats("total",username,tablename,start,end,data.size());
				logmap.put("status ","cycle completed");

				new FileWrite().write(logmap);
			}else{
				logmap.put("status ","no records available stop the poller");

				new FileWrite().write(logmap);
				PollerStartup.getInstance(tablename).runninguser.remove(username);
				
				return;
			}
			
			}else{
				logmap.put("status ","redis not available goto sleep");

				new FileWrite().write(logmap);
				gotosleep();
			}
		}
	}
	private void stats(String operation, String username2, String tablename2, long start, long end, int size) {
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("username", username2);
		logmap.put("operation", operation);
		logmap.put("tablename", tablename2);
		logmap.put("record count", ""+size);
		logmap.put("timetaken", ""+(end-start));
		logmap.put("logname", "tr_stats");
		new FileWrite().write(logmap);
	}
	private void deleteUntilSuccess(List<Map<String, Object>> data) {

		
		while(true){
			
			if(select.delete( tablename,data,true)){
				
				return;
			}
			
			gotosleep();
		}
		
	}
	private void sendUntilSuccess(Map<String, Object> map) {
	
		
		
		Map<String,Object> logmap=new HashMap<String,Object>();

		
		while(true){
		
			if(isNumber(map)){
			
				if(queuesender.sendL( getQueueName(PushAccount.instance().getPushAccount(map.get(MapKeys.USERNAME).toString())), map, true, logmap)){
				
				break;
				}
			}else{
				
				if(queuesender.sendL( "submissionpool", map, true, logmap)){
					
					break;
					}
			}
			
			gotosleep();
		}
		
		logmap.put("module", "db reader");
		logmap.putAll(map);
		logmap.put("logname","tablereadersubmit");

		new FileWrite().write(logmap);
	}
	private boolean isNumber(Map<String,Object> map) {
		try{
			String mobile=map.get(MapKeys.MOBILE).toString();
			StringTokenizer st=new StringTokenizer(mobile," ");
			StringBuffer sb=new StringBuffer();
			while(st.hasMoreTokens()){
				
				sb.append(st.nextToken());
			}
			
			mobile=sb.toString();
			Long.parseLong(mobile);
			map.put(MapKeys.MOBILE, mobile);
			return true;
		}catch(Exception e){
			
		}
		return false;
	}
	private void gotosleep() {
		
		try{
			
			Thread.sleep(100L);
		}catch(Exception e){
			
		}
	}
	
}

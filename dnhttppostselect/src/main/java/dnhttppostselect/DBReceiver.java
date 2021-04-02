package dnhttppostselect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.dao.Select;
import com.winnovature.unitia.util.dao.Table;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.processor.DNHttpPost;
import com.winnovature.unitia.util.redis.QueueSender;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;


public class DBReceiver extends Thread {

	public static boolean GRACESTOP=false;

	String actuval_username=null;
	
	String username=null;
	
	String poolname=null;

	String queuename=null;
	
	boolean isDNDelay=false;
	
	QueueSender queuesender=new QueueSender();
	
	Select select=new Select();
	
	FileWrite log=new FileWrite();
	
	public DBReceiver(String poolname,String username){
	
		this.username=username;
		
		this.actuval_username=username.substring(0,username.lastIndexOf("_"));
		
		
		this.poolname=poolname;
		if(poolname.equals("schedulepool")){
			
			queuename="commonpool";
			
		}else if(poolname.equals("dndelaypool")){
			
			queuename="dnreceiverpool";
			
			isDNDelay=true;
			
		}else if(poolname.equals("smppdn")){
			
			queuename="smppdn_"+username;
			
			isDNDelay=true;
			
		}else{
			
			queuename=poolname;
		}
	}
	public void run(){
		
		
	if(!Table.getInstance().isAvailableTable(poolname)){
			
			Table.getInstance().addTable(poolname);
	}
		while(!GRACESTOP){
			
			
			List<Map<String,Object>> data=select.getData(poolname,username);
			
			if(data!=null&&data.size()>0){
				
		
				String format=PushAccount.instance().getPushAccount(actuval_username).get("httpdn_format");
				if(format!=null&&format.equals("gson")){
					
					new DNHttpPostGSON(data).doProcess();
					
				}else{
				for(int i=0;i<data.size();i++){
					
					sendUntilSuccess(data.get(i));
					
					}
				}
				deleteUntilSuccess(data);
			}else{
				

				PollerStartup.getInstance(poolname).runninguser.remove(username);
				
				return;
			}
			
			
		}
	}
	private void deleteUntilSuccess(List<Map<String, Object>> data) {

		
		while(true){
			
			if(select.delete( poolname,data,false)){
				
				return;
			}
			
			gotosleep();
		}
		
	}
	
	
	public String getQueueName(Map<String ,String > accountmap){
		
		

		if(accountmap.get(MapKeys.OPTIN_TYPE).equals("1")){
			
			return "optin";
			
		}else if(accountmap.get(MapKeys.OPTIN_TYPE).equals("2")){
			
			return "optout";
		}else if(!accountmap.get(MapKeys.DUPLICATE_TYPE).equals("0")){
			
			return "duplicate";
		}else{
		
			return "commonpool";
			
		}
	}
	private void sendUntilSuccess(Map<String, Object> map) {
		
		
		new DNHttpPost(map).doProcess();
		
	}
	
	
	
	private void gotosleep() {
		
		try{
			
			Thread.sleep(100L);
		}catch(Exception e){
			
		}
	}
}

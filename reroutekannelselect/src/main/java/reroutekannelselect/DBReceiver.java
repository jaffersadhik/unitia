package reroutekannelselect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.dao.Select;
import com.winnovature.unitia.util.dao.Table;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.TPSCheck;
import com.winnovature.unitia.util.processor.DNHttpPost;
import com.winnovature.unitia.util.queue.kannelQueue;
import com.winnovature.unitia.util.redis.QueueSender;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;


public class DBReceiver extends Thread {

	public static boolean GRACESTOP=false;

	
	String username=null;
	
	String poolname=null;

	String queuename=null;
	
	boolean isDNDelay=false;
	
	QueueSender queuesender=new QueueSender();
	
	Select select=new Select();
	
	FileWrite log=new FileWrite();
	
	public DBReceiver(String poolname,String username){
	
		this.username=username;
		
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
				
				System.out.println("username : "+username+" poolname : "+poolname+" size "+data.size());
		
				
				for(int i=0;i<data.size();i++){
					
					sendUntilSuccess(data.get(i));
					
					
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
	
	
	private void sendUntilSuccess(Map<String, Object> map) {
		
		while(true){
			
		if(!kannelQueue.getInstance().isQueued(username,true)){
		
			if(TPSCheck.getInstance().isAllowed(username)){
				if(new QueueSender().sendL(map.get(MapKeys.REROUTE_KANNEL_QUEUE_NAME).toString(), map, false, new HashMap<String, Object>())){
					
					return;
				}else{
					gotosleep();
				}
			}else{
				
				gotosleep();
			}
			
		}else{
				
				gotosleep();
		}
		}
	}
	
	
	
	private void gotosleep() {
		
		try{
			
			Thread.sleep(3L);
		}catch(Exception e){
			
		}
	}
}

package optin;

import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.RouterLog;
import com.winnovature.unitia.util.optin.SMSProcessor;


public class SMSWorker  {


		
	Map<String,Object> msgmap = null;
	
	String poolname=null;
	
	public SMSWorker(String poolname,Map<String,Object> payloadPack) 
	{
		this.msgmap = payloadPack;
		this.poolname= poolname;
		
	}

	public void doOtp(String redisid, String tname){
		
try{
	
	msgmap.put(MapKeys.ATTEMPT_TYPE, "0");
	msgmap.put(MapKeys.TOTAL_MSG_COUNT,"1");

	String username=(String)msgmap.get(MapKeys.USERNAME);
	
	if(username==null||PushAccount.instance().getPushAccount(username)==null){
		
		new FileWrite().logError("routerinvalidusername", msgmap, new Exception("Invalid Username"));
		
		return;
		
	}
		

		SMSProcessor processor=new SMSProcessor("optin",msgmap,true);
		
		RouterLog.routerlog(redisid, tname, "start route.doOptin()");

		processor.doOptin();
		
		RouterLog.routerlog(redisid, tname, "start route.sentToNextLevel()");

		
		processor.sentToNextLevel();
		
		RouterLog.routerlog(redisid, tname, "end route.sentToNextLevel()");

}catch(Exception e){
	
	new FileWrite().logError("SMSWorker", msgmap, e);

}

	}
	
}

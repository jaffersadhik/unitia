package dlt;

import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.RouterLog;


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
	

	String username=(String)msgmap.get(MapKeys.USERNAME);
	
	if(username==null||PushAccount.instance().getPushAccount(username)==null){
		
		new FileWrite().logError("routerinvalidusername", msgmap, new Exception("Invalid Username"));
		
		return;
		
	}
		

		SMSProcessor processor=new SMSProcessor("dlt",msgmap,true);
		
		
		new RouterLog().routerlog(redisid, tname, "start route.doEntityValidation()");

		processor.doEntityValidation();
		
		new RouterLog().routerlog(redisid, tname, "end route.dodefaultEntityValidation()");
		
		new RouterLog().routerlog(redisid, tname, "start route.dodefaultEntityValidation()");

		processor.dodefaultEntityValidation();
		
		new RouterLog().routerlog(redisid, tname, "end route.doEntityValidation()");
	

		new RouterLog().routerlog(redisid, tname, "start route.isDLT()");

		processor.isDLT();
		
		new RouterLog().routerlog(redisid, tname, "end route.isDLT()");


		
		processor.sentToNextLevel();
		
		new RouterLog().routerlog(redisid, tname, "end route.sentToNextLevel()");

}catch(Exception e){
	
	new FileWrite().logError("SMSWorker", msgmap, e);

}

	}
	
}

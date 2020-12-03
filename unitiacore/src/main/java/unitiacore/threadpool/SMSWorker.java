package unitiacore.threadpool;

import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.RouterLog;

import unitiaroute.RouteProcessor;


public class SMSWorker  {


		
	Map<String,Object> msgmap = null;
	
	String poolname=null;
	
	public SMSWorker(String poolname,Map<String,Object> payloadPack) 
	{
		this.msgmap = payloadPack;
		this.poolname= poolname;
		
	}

	public void doProcess() 
	{

		try{
	

		if(poolname.equals("commonpool")){

			doPremilinaryValidation();
			
			QueueTon.getInstance().add("kannelconnector", msgmap);
			
		}else if(poolname.equals("kannelconnector")){
			
			doSubmitKannel();
			
			QueueTon.getInstance().add("redissender", msgmap);

		}else if(poolname.equals("redissender")){
			
			doNextLevel();
		}
		
		}catch(Exception e){
			
			new FileWrite().logError("SMSWorker", msgmap, e);
		}
	}

	

	
	
	public void doPremilinaryValidation() throws Exception{}
	
	public void doSubmitKannel() throws Exception{
		
		String isfurther=(String)msgmap.get("isfurther");
		
		if(isfurther!=null&&isfurther.equals("y")){
			SMSProcessor processor=new SMSProcessor(msgmap,true);
			processor.submitKannel();
		}else{
			SMSProcessor processor=new SMSProcessor(msgmap,false);
			processor.submitKannel();
			
		}
		
	}
	
	public void doNextLevel() throws Exception{
		
		SMSProcessor processor=new SMSProcessor(msgmap,true);
		processor.sentToNextLevel();
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
		RouteProcessor route=new RouteProcessor(msgmap,redisid,tname);
	
		
		new RouterLog().routerlog(redisid, tname, "start route.doRouteGroupAvailable()");

		route.doRouteGroupAvailable();
		
		new RouterLog().routerlog(redisid, tname, "end route.doRouteGroupAvailable()");

		
		new RouterLog().routerlog(redisid, tname, "start route.doSMSCIDAvailable()");

		route.doSMSCIDAvailable();
		
		new RouterLog().routerlog(redisid, tname, "end route.doSMSCIDAvailable()");

		
		new RouterLog().routerlog(redisid, tname, "start route.doKannelAvailable()");

		route.doKannelAvailable();
		
		new RouterLog().routerlog(redisid, tname, "end route.doKannelAvailable()");

		
		new RouterLog().routerlog(redisid, tname, "start route.doEntityValidation()");

		route.doEntityValidation();
		
		new RouterLog().routerlog(redisid, tname, "end route.dodefaultEntityValidation()");
		
		new RouterLog().routerlog(redisid, tname, "start route.dodefaultEntityValidation()");

		route.dodefaultEntityValidation();
		
		new RouterLog().routerlog(redisid, tname, "end route.doEntityValidation()");
	

		new RouterLog().routerlog(redisid, tname, "start route.isDLT()");

		route.isDLT();
		
		new RouterLog().routerlog(redisid, tname, "end route.isDLT()");
		
		SMSProcessor processor=new SMSProcessor(msgmap,route.isIsfurtherprocess());
		
	


		new RouterLog().routerlog(redisid, tname, "start route.doFeatureCodeIndentification()");

		processor.doFeatureCodeIndentification();
		
		new RouterLog().routerlog(redisid, tname, "end route.doFeatureCodeIndentification()");

		new RouterLog().routerlog(redisid, tname, "start route.doDNMessage()");

		processor.doDNMessage();
		
		new RouterLog().routerlog(redisid, tname, "end route.doDNMessage()");

		new RouterLog().routerlog(redisid, tname, "start route.doConcate()");

		processor.doConcate();
		
		new RouterLog().routerlog(redisid, tname, "end route.doConcate()");

		new RouterLog().routerlog(redisid, tname, "start route.setCredit()");

		processor.setCredit();
		
		new RouterLog().routerlog(redisid, tname, "end route.setCredit()");

		new RouterLog().routerlog(redisid, tname, "start route.submitKannel()");

		processor.submitKannel();
		
		new RouterLog().routerlog(redisid, tname, "end route.submitKannel()");

		new RouterLog().routerlog(redisid, tname, "start route.sentToNextLevel()");

		
		processor.sentToNextLevel();
		
		new RouterLog().routerlog(redisid, tname, "end route.sentToNextLevel()");

}catch(Exception e){
	
	new FileWrite().logError("SMSWorker", msgmap, e);

}

	}
	
}

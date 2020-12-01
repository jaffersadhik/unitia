package unitiacore.threadpool;

import java.util.HashMap;
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

	

	
	
	public void doPremilinaryValidation() throws Exception{
		

		msgmap.put(MapKeys.ATTEMPT_TYPE, "0");
		msgmap.put(MapKeys.TOTAL_MSG_COUNT,"1");

			RouteProcessor route=new RouteProcessor(msgmap,"a","a");
			
			route.doCountryCodeCheck();
			route.doNumberingPlan();
			route.doBlackListMobileNumber();
			route.doBlackListSenderid();
			route.doFilteringSMSPatternCheck();
			route.doBlackListSMSPattern();
			route.doAllowedSMSPatternCheck();
			route.doSenderCheck();
			route.doRouteGroupAvailable();
			route.doSMSCIDAvailable();
			route.doKannelAvailable();
			
			SMSProcessor processor=new SMSProcessor(msgmap,route.isIsfurtherprocess());
			
			processor.doDNDCheck();
			processor.doFeatureCodeIndentification();
			processor.doDNMessage();
			processor.doConcate();
			processor.setCredit();

	}
	
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
		RouterLog.routerlog(redisid, tname, "start route.doCountryCodeCheck()");
		route.doCountryCodeCheck();
		RouterLog.routerlog(redisid, tname, "end route.doCountryCodeCheck()");

		RouterLog.routerlog(redisid, tname, "start route.doNumberingPlan()");

		route.doNumberingPlan();
		
		RouterLog.routerlog(redisid, tname, "end route.doNumberingPlan()");

		
		RouterLog.routerlog(redisid, tname, "start route.doBlackListMobileNumber()");

		route.doBlackListMobileNumber();
		
		RouterLog.routerlog(redisid, tname, "end route.doBlackListMobileNumber()");

		
		RouterLog.routerlog(redisid, tname, "start route.doBlackListSenderid()");

		route.doBlackListSenderid();
		
		RouterLog.routerlog(redisid, tname, "end route.doBlackListSenderid()");

		
		RouterLog.routerlog(redisid, tname, "start route.doFilteringSMSPatternCheck()");

		route.doFilteringSMSPatternCheck();
		
		RouterLog.routerlog(redisid, tname, "end route.doFilteringSMSPatternCheck()");

		
		RouterLog.routerlog(redisid, tname, "start route.doBlackListSMSPattern()");

		route.doBlackListSMSPattern();
		
		RouterLog.routerlog(redisid, tname, "end route.doBlackListSMSPattern()");

		RouterLog.routerlog(redisid, tname, "start route.doAllowedSMSPatternCheck()");

		route.doAllowedSMSPatternCheck();
		
		RouterLog.routerlog(redisid, tname, "end route.doAllowedSMSPatternCheck()");

		RouterLog.routerlog(redisid, tname, "start route.doSenderCheck()");

		route.doSenderCheck();
		
		RouterLog.routerlog(redisid, tname, "end route.doSenderCheck()");

		
		RouterLog.routerlog(redisid, tname, "start route.doRouteGroupAvailable()");

		route.doRouteGroupAvailable();
		
		RouterLog.routerlog(redisid, tname, "end route.doRouteGroupAvailable()");

		
		RouterLog.routerlog(redisid, tname, "start route.doSMSCIDAvailable()");

		route.doSMSCIDAvailable();
		
		RouterLog.routerlog(redisid, tname, "end route.doSMSCIDAvailable()");

		
		RouterLog.routerlog(redisid, tname, "start route.doKannelAvailable()");

		route.doKannelAvailable();
		
		RouterLog.routerlog(redisid, tname, "end route.doKannelAvailable()");

		
		RouterLog.routerlog(redisid, tname, "start route.doEntityValidation()");

		route.doEntityValidation();
		
		RouterLog.routerlog(redisid, tname, "end route.dodefaultEntityValidation()");
		
		RouterLog.routerlog(redisid, tname, "start route.dodefaultEntityValidation()");

		route.dodefaultEntityValidation();
		
		RouterLog.routerlog(redisid, tname, "end route.doEntityValidation()");
	

		RouterLog.routerlog(redisid, tname, "start route.isDLT()");

		route.isDLT();
		
		RouterLog.routerlog(redisid, tname, "end route.isDLT()");
		
		SMSProcessor processor=new SMSProcessor(msgmap,route.isIsfurtherprocess());
		
	

		RouterLog.routerlog(redisid, tname, "start route.doDNDCheck()");

		processor.doDNDCheck();
		
		RouterLog.routerlog(redisid, tname, "end route.doDNDCheck()");

		RouterLog.routerlog(redisid, tname, "start route.doFeatureCodeIndentification()");

		processor.doFeatureCodeIndentification();
		
		RouterLog.routerlog(redisid, tname, "end route.doFeatureCodeIndentification()");

		RouterLog.routerlog(redisid, tname, "start route.doDNMessage()");

		processor.doDNMessage();
		
		RouterLog.routerlog(redisid, tname, "end route.doDNMessage()");

		RouterLog.routerlog(redisid, tname, "start route.doConcate()");

		processor.doConcate();
		
		RouterLog.routerlog(redisid, tname, "end route.doConcate()");

		RouterLog.routerlog(redisid, tname, "start route.setCredit()");

		processor.setCredit();
		
		RouterLog.routerlog(redisid, tname, "end route.setCredit()");

		RouterLog.routerlog(redisid, tname, "start route.submitKannel()");

		processor.submitKannel();
		
		RouterLog.routerlog(redisid, tname, "end route.submitKannel()");

		RouterLog.routerlog(redisid, tname, "start route.sentToNextLevel()");

		
		processor.sentToNextLevel();
		
		RouterLog.routerlog(redisid, tname, "end route.sentToNextLevel()");

}catch(Exception e){
	
	new FileWrite().logError("SMSWorker", msgmap, e);

}

	}
	
}

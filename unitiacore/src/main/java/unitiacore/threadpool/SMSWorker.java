package unitiacore.threadpool;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.RouterLog;
import com.winnovature.unitia.util.template.Template;

import unitiaroute.RouteProcessor;
import unitiaroute.SenderidMasking;


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
			route.doAllowedSMSPatternCheckB();
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
		new RouterLog().routerlog(redisid, tname, "start route.doCountryCodeCheck()");
		route.doCountryCodeCheck();
		new RouterLog().routerlog(redisid, tname, "end route.doCountryCodeCheck()");

		new RouterLog().routerlog(redisid, tname, "start route.doNumberingPlan()");

		route.doNumberingPlan();
		
		new RouterLog().routerlog(redisid, tname, "end route.doNumberingPlan()");

		
		new RouterLog().routerlog(redisid, tname, "start route.doBlackListMobileNumber()");

		route.doBlackListMobileNumber();
		
		new RouterLog().routerlog(redisid, tname, "end route.doBlackListMobileNumber()");

		
		new RouterLog().routerlog(redisid, tname, "start route.doBlackListSenderid()");

		route.doBlackListSenderid();
		
		new RouterLog().routerlog(redisid, tname, "end route.doBlackListSenderid()");

		
		new RouterLog().routerlog(redisid, tname, "start route.doFilteringSMSPatternCheck()");

		route.doFilteringSMSPatternCheck();
		
		new RouterLog().routerlog(redisid, tname, "end route.doFilteringSMSPatternCheck()");

		
		new RouterLog().routerlog(redisid, tname, "start route.doBlackListSMSPattern()");

		route.doBlackListSMSPattern();
		
		new RouterLog().routerlog(redisid, tname, "end route.doBlackListSMSPattern()");

		new RouterLog().routerlog(redisid, tname, "start route.doAllowedSMSPatternCheck()");

		String senderid=(String)msgmap.get(MapKeys.SENDERID);
		if(senderid!=null && Template.getInstance().isAvailableSenderid(senderid.toLowerCase())){
			route.doAllowedSMSPatternCheckC();
		}
		route.doAllowedSMSPatternCheckA();

		route.doAllowedSMSPatternCheckB();
		
		new RouterLog().routerlog(redisid, tname, "end route.doAllowedSMSPatternCheck()");

		new RouterLog().routerlog(redisid, tname, "start route.doSenderCheck()");

		route.doSenderCheck();
		
		new RouterLog().routerlog(redisid, tname, "end route.doSenderCheck()");

		
		new RouterLog().routerlog(redisid, tname, "start route.doRouteGroupAvailable()");

		route.doRouteGroupAvailable();
		
		new RouterLog().routerlog(redisid, tname, "end route.doRouteGroupAvailable()");

		
		new RouterLog().routerlog(redisid, tname, "start route.doSMSCIDAvailable()");

		route.doSMSCIDAvailable();
		
		new RouterLog().routerlog(redisid, tname, "end route.doSMSCIDAvailable()");

		
		new RouterLog().routerlog(redisid, tname, "start route.doKannelAvailable()");

		route.doKannelAvailable();
		
		new RouterLog().routerlog(redisid, tname, "end route.doKannelAvailable()");
		
		new SenderidMasking().doSenderIDMask(msgmap);
		

		
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
		
	

		new RouterLog().routerlog(redisid, tname, "start route.doDNDCheck()");

		processor.doDNDCheck();
		
		new RouterLog().routerlog(redisid, tname, "end route.doDNDCheck()");

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

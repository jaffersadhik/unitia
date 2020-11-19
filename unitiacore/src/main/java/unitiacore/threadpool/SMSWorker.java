package unitiacore.threadpool;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;

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

			RouteProcessor route=new RouteProcessor(msgmap);
			
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
			processor.doOptin();
			processor.doOptout();
			processor.doDuplicate();
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
	
	public void doOtp(){
		
try{
	
	msgmap.put(MapKeys.ATTEMPT_TYPE, "0");
	msgmap.put(MapKeys.TOTAL_MSG_COUNT,"1");

	String username=(String)msgmap.get(MapKeys.USERNAME);
	
	if(username==null||PushAccount.instance().getPushAccount(username)==null){
		
		new FileWrite().logError("routerinvalidusername", msgmap, new Exception("Invalid Username"));
		
		return;
		
	}
		RouteProcessor route=new RouteProcessor(msgmap);
		
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
		route.doEntityValidation();
		
		SMSProcessor processor=new SMSProcessor(msgmap,route.isIsfurtherprocess());
		processor.doOptin();
		processor.doOptout();
		processor.doDuplicate();
		processor.doDNDCheck();
		processor.doFeatureCodeIndentification();
		processor.doDNMessage();
		processor.doConcate();
		processor.setCredit();
		processor.submitKannel();
		processor.sentToNextLevel();
}catch(Exception e){
	
	new FileWrite().logError("SMSWorker", msgmap, e);

}

	}
	
}

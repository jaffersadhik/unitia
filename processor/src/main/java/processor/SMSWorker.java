package processor;

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

	msgmap.put(MapKeys.ATTEMPT_TYPE, "0");
	msgmap.put(MapKeys.TOTAL_MSG_COUNT,"1");

	String username=(String)msgmap.get(MapKeys.USERNAME);
	
	if(username==null||PushAccount.instance().getPushAccount(username)==null){
		
		new FileWrite().logError("routerinvalidusername", msgmap, new Exception("Invalid Username"));
		
		return;
		
	}
		

		SMSProcessor processor=new SMSProcessor("processor",msgmap,true);
		
		String isdnd=(String)msgmap.get("IS_DND");
		
		if(isdnd!=null){
			
			processor.setDND(true);
		}

		processor.doCountryCodeCheck();
		

		processor.doNumberingPlan();
		
		processor.doBlackListMobileNumber();
		
		processor.doBlackListSMSPattern();
		
		processor.doFilteringSMSPatternCheck();
		
		processor.doAllowedSMSPatternCheck();
		
		processor.doSenderCheck();
		
		processor.doSenderCheck();
		
		processor.doEntityValidation();
		
		processor.dodefaultEntityValidation();
		
		processor.isDLT();
		
		processor.doRouteGroupAvailable();
		
		processor.doFeatureCodeIndentification();
		
		processor.sentToNextLevel();
		

}catch(Exception e){
	
	new FileWrite().logError("SMSWorker", msgmap, e);

}

	}
	
}

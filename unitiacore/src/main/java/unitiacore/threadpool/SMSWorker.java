package unitiacore.threadpool;

import java.util.Map;

import com.winnovature.unitia.util.processor.SMSProcessor;

import unitiaroute.RouteProcessor;


public class SMSWorker  {


		
	Map<String,String> msgmap = null;
	
	String pooltype=null;
	String poolname=null;
	
	public SMSWorker(String poolname,String pooltype,Map<String,String> payloadPack) 
	{
		this.msgmap = payloadPack;
		this.poolname= poolname;
		this.pooltype= pooltype;
		
	}

	public void doProcess() 
	{

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
		processor.doDNDCheck();
		processor.doFeatureCodeIndentification();
		processor.doConcate();
		processor.submitKannel();
		
	}

	public Map<String,String> getPayload() {
		return msgmap;
	}

	public String getPooltype() {
		return pooltype;
	}

	public String getPoolname() {
		return poolname;
	}
	
	
	
}

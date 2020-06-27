package com.winnovature.unitia.util.threadpool;

import java.util.Map;

import com.winnovature.unitia.util.processor.SMSProcessor;


public class SMSWorker implements Runnable {


		
	Map<String,String> msgmap = null;
	
	String pooltype=null;
	String poolname=null;
	
	public SMSWorker(String poolname,String pooltype,Map<String,String> payloadPack) 
	{
		this.msgmap = payloadPack;
		this.poolname= poolname;
		this.pooltype= pooltype;
		
	}

	public void run() 
	{
		
		SMSProcessor processor=new SMSProcessor(msgmap);
		processor.doCountryCodeCheck();
		processor.doNumberingPlan();
		processor.doOptin();
		processor.doOptout();
		processor.doAllowedSMSPatternCheck();
		processor.doBlackListSMSPattern();
		processor.doBlackListSenderid();
		processor.doBlackListMobileNumber();
		processor.doFilteringSMSPatternCheck();
		processor.doDNDCheck();
		processor.doSenderCheck();
		processor.doRouteGroupAvailable();
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

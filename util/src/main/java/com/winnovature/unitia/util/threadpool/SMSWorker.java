package com.winnovature.unitia.util.threadpool;

import java.util.Map;

import com.winnovature.unitia.util.processor.SMSProcessor;


public class SMSWorker implements Runnable {


		
	Map<String,String> msgmap = null;
	
	String pooltype=null;
	String poolname=null;
	SMSWorker obj=null;
	
	public SMSWorker(String poolname,String pooltype,Map<String,String> payloadPack) 
	{
		this.msgmap = payloadPack;
		this.poolname= poolname;
		this.pooltype= pooltype;
		obj=this;
	}

	public void run() 
	{
		
		SMSProcessor processor=new SMSProcessor(msgmap);
		processor
		.doCountryCodeCheck()
		.doNumberingPlan()
		.doOptin()
		.doOptout()
		.doAllowedSMSPatternCheck()
		.doBlackListSMSPattern()
		.doBlackListSenderid()
		.doBlackListMobileNumber()
		.doFilteringSMSPatternCheck()
		.doDNDCheck()
		.doSenderCheck()
		.doRouteGroupAvailable()
		.doFeatureCodeIndentification()
		.doConcate()
		.submitKannel();
		
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
	
	public SMSWorker getInstance(){
		
		return obj;
	}

	
	
}

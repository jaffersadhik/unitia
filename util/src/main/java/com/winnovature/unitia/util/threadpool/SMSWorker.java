package com.winnovature.unitia.util.threadpool;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.processor.SMSProcessor;


public class SMSWorker implements Runnable {


		
	Map<String,String> msgmap = null;
	Map<String,String> logmap = new HashMap<String,String>();
	
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
		logmap.put("poolname", poolname);
		
		SMSProcessor processor=new SMSProcessor(msgmap,logmap);
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
		logmap.putAll(msgmap);
		
		new Log().log(logmap);
		
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

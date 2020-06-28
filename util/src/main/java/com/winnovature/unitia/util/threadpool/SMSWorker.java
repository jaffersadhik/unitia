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
		processor.doRouting();
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

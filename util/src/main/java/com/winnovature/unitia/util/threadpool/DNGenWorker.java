package com.winnovature.unitia.util.threadpool;

import java.util.Map;

import com.winnovature.unitia.util.processor.DNGenProcessor;


public class DNGenWorker implements Runnable {


		
	Map<String,String> payload = null;
	String pooltype=null;
	String poolname=null;
	DNGenWorker obj=null;
	public DNGenWorker(String poolname,String pooltype,Map<String,String> payloadPack) 
	{
		this.payload =  payloadPack;
		this.poolname= poolname;
		this.pooltype= pooltype;
		obj=this;
	}

	public void run() 
	{

		if(payload.size()>0)
		{
			new DNGenProcessor().handoverToDN(payload.get("dlrurl"));
		}
	}

	public Map<String,String> getPayload() {
		return payload;
	}

	public String getPooltype() {
		return pooltype;
	}

	public String getPoolname() {
		return poolname;
	}
	
	public DNGenWorker getInstance(){
		
		return obj;
	}

	
	
}

package unitiacore.threadpool;

import java.util.Map;

import com.winnovature.unitia.util.processor.DNProcessor;


public class DNWorker2 implements Runnable {


		
	Map<String,String> payload = null;
	String pooltype=null;
	String poolname=null;
	DNWorker2 obj=null;
	public DNWorker2(String poolname,String pooltype,Map<String,String> payloadPack) 
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
			new DNProcessor(payload).doProcess();
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
	
	public DNWorker2 getInstance(){
		
		return obj;
	}

	
	
}

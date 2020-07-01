package unitiacore.threadpool;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.misc.Log;
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
			Map<String,String> logmap=new HashMap<String,String>();
			new DNGenProcessor().handoverToDN(payload.get("dlrurl"));
			logmap.put("status", "DNGenWorker worker");
			logmap.put("dlrurl", payload.get("dlrurl"));
			new Log().log(logmap);
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

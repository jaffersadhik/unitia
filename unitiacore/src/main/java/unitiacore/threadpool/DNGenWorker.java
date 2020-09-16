package unitiacore.threadpool;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.dngen.ErrorCodeType;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.processor.DNGenProcessor;


public class DNGenWorker {


		
	Map<String,Object> payload = null;
	String poolname=null;
	DNGenWorker obj=null;
	public DNGenWorker(String poolname,Map<String,Object> payloadPack) 
	{
		this.payload =  payloadPack;
		this.poolname= poolname;
		obj=this;
	}

	public void doProcess() 
	{

		if(payload.size()>0)
		{
			Map<String,Object> logmap=new HashMap<String,Object>();
			new DNGenProcessor().handoverToDN(payload.get("dlrurl").toString());
			logmap.put("module", "DNGenWorker");
			logmap.put("dlrurl", payload.get("dlrurl"));
			logmap.put(MapKeys.USERNAME, "dngenworker");

			new FileWrite().write(logmap);
		}
	}

	

	public String getPoolname() {
		return poolname;
	}
	
	public DNGenWorker getInstance(){
		
		return obj;
	}

	
	
}

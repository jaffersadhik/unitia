package unitiacore.threadpool;

import java.util.List;
import java.util.Map;


public class MsgRetryWorker implements Runnable {


		
	List payload = null;
	String pooltype=null;
	String poolname=null;
	MsgRetryWorker obj=null;
	public MsgRetryWorker(Map<String,Object> payloadPack) 
	{
		this.payload = (List) payloadPack.get("data");
		this.poolname= (String)payloadPack.get("poolname");
		this.pooltype=(String)payloadPack.get("pooltype");
		obj=this;
	}

	public void run() 
	{

		if(payload.size()>0)
		{
		}
	}

	public List getPayload() {
		return payload;
	}

	public String getPooltype() {
		return pooltype;
	}

	public String getPoolname() {
		return poolname;
	}
	
	public MsgRetryWorker getInstance(){
		
		return obj;
	}

	
	
}

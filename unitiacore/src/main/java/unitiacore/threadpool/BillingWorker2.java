package unitiacore.threadpool;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.dao.SubmissionDAO;
import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.processor.SMSProcessor;


public class BillingWorker2 implements Runnable {


		
	Map<String,String> msgmap = null;
	Map<String,String> logmap = new HashMap<String,String>();
	
	String pooltype=null;
	String poolname=null;
	BillingWorker2 obj=null;
	
	public BillingWorker2(String poolname,String pooltype,Map<String,String> payloadPack) 
	{
		this.msgmap = payloadPack;
		this.poolname= poolname;
		this.pooltype= pooltype;
		obj=this;
	}

	public void run() 
	{
		logmap.putAll(msgmap);

		logmap.put("poolname", poolname);
		logmap.put("worker", "Billing Worker");


		
		new SubmissionDAO().insert(msgmap);
		
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
	
	public BillingWorker2 getInstance(){
		
		return obj;
	}

	
	
}

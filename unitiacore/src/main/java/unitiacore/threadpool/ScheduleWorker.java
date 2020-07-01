package unitiacore.threadpool;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.processor.SMSProcessor;
import com.winnovature.unitia.util.processor.ScheduleProcessor;


public class ScheduleWorker implements Runnable {


		
	Map<String,String> msgmap = null;
	Map<String,String> logmap = new HashMap<String,String>();
	
	String pooltype=null;
	String poolname=null;
	ScheduleWorker obj=null;
	
	public ScheduleWorker(String poolname,String pooltype,Map<String,String> payloadPack) 
	{
		this.msgmap = payloadPack;
		this.poolname= poolname;
		this.pooltype= pooltype;
		obj=this;
	}

	public void run() 
	{
		logmap.put("poolname", poolname);
		
		new ScheduleProcessor(msgmap, logmap).doProcess();
		
	
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
	
	public ScheduleWorker getInstance(){
		
		return obj;
	}

	
	
}

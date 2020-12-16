package simulatar.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public class QueueMap {

	private static QueueMap obj=null;
	
	private static Map<String,LinkedBlockingDeque<Map<String,Object>>> queue=new HashMap<String,LinkedBlockingDeque<Map<String,Object>>>();
	static{
		
		queue.put("smppclient1", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient2", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient3", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient4", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient5", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient6", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient7", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient8", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient9", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient10", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient11", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient12", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient13", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient14", new LinkedBlockingDeque<Map<String,Object>>());
		queue.put("smppclient15", new LinkedBlockingDeque<Map<String,Object>>());

	
	}
	private QueueMap(){
		
	}
	
	public static QueueMap getInstance(){
		
		if(obj==null){
			
			obj=new QueueMap();
		}
		
		return obj;
	}
	
	public void offer(String username,Map<String,Object> msg){
		
		queue.get(username).offer(msg);
	}
	
	public List<Map<String,Object>> getMesssages(String username){
		
		List<Map<String,Object>> msglist=new ArrayList<Map<String,Object>>();
		queue.get(username).drainTo(msglist, 500);		
		return msglist;
	}
	
}

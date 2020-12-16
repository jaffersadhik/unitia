package simulatar.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public class QueueMap {

	private static QueueMap obj=null;
	
	private LinkedBlockingDeque<Map<String,Object>> queue=new LinkedBlockingDeque<Map<String,Object>>();
	
	private QueueMap(){
		
	}
	
	public static QueueMap getInstance(){
		
		if(obj==null){
			
			obj=new QueueMap();
		}
		
		return obj;
	}
	
	public void offer(Map<String,Object> msg){
		
		queue.offer(msg);
	}
	
	public List<Map<String,Object>> getMesssages(){
		
		List<Map<String,Object>> msglist=new ArrayList<Map<String,Object>>();
		queue.drainTo(msglist, 500);		
		return msglist;
	}
	
}

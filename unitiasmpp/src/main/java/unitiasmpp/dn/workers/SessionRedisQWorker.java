package unitiasmpp.dn.workers;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.redis.QueueSender;
import com.winnovature.unitia.util.redis.RedisReader;

import unitiasmpp.event.handlers.SessionEventHandler;
import unitiasmpp.manager.SessionManager;

public class SessionRedisQWorker extends Thread {


	String systemId = null;
	RedisReader reader=new RedisReader();
	DNWorker worker;
	boolean done = false;
	SessionEventHandler handler=null;
	public SessionEventHandler getHandler() {
		return handler;
	}

	public void setHandler(SessionEventHandler handler) {
		this.handler = handler;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public SessionRedisQWorker(String systemId,SessionEventHandler handler) {
		this.systemId = systemId;
		worker = new DNWorker(systemId);
		setName("SessionRedisQWorker-"+handler.getSessionId());
		
		this.handler=handler;
	}

	public void run() {

		while(handler.getSession().isBinding() || handler.getSession().isOpen()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignore) {}
		}
		while (handler.getSession().isBound()) {

			Map<String, Object> aDn = null;
			
			try {
				
				aDn = reader.getData("smppdn_"+systemId);
				if(aDn!=null){
				CustomerRedisHBData.INST.heartBeat(getName(),systemId);
						try {
								if (handler.getSession().isBound()) {
									boolean flag =worker.sendMessage(handler.getSession(), aDn);
										
									
									if(flag)
										writeResponse(aDn,0);								
									else
										writeResponse(aDn,null);
								} else {
									writeResponse(aDn,null);
								}
							
						} catch(Exception exp) {
							writeResponse(aDn,null);
						}
					
				}else{
					
					gotosleep();
				}
			} catch (Exception e1) {
				gotosleep();
			}
			
		}
		CustomerRedisHBData.INST.remove(getName(),systemId);
		
		if(SessionManager.getInstance().getDnWorkerMap().get(systemId)!=null) {
			SessionManager.getInstance().getDnWorkerMap().get(systemId).remove(this);
			if(SessionManager.getInstance().getDnWorkerMap().get(systemId).size()==0) {
				SessionManager.getInstance().getDnWorkerMap().remove(systemId);
			}
		}
	}

	private void gotosleep() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		
	}

	private void writeResponse(Map<String,Object> aDn,Integer status) {
		
		try {	
				
			if(status==null&&aDn!=null)
				aDn.remove("STATUS");
			else if (status.equals(0))
				aDn.put("STATUS","SUCCESS");
			else if (status.equals(-2))
				aDn.put("STATUS","EXPIRED");
			else
				aDn.put("STATUS","FAILED");
			
			String msg_status=(String)aDn.get("STATUS");
			Map<String,Object> logmap=new HashMap<String,Object>();
			logmap.putAll(aDn);
			
			if(msg_status!=null&&msg_status.equals("SUCCESS")){
			new QueueSender().sendL("dnpostpool",aDn, false,logmap);
			logmap.put("smppdn_ status", "send to dnpostpool redis queue");

			}else{
				
				String queuename="smppdn_"+aDn.get(MapKeys.USERNAME).toString();
				new QueueSender().sendL(queuename, aDn, false,logmap);
				logmap.put("smppdn_ status", "send to "+queuename+" redis queue");

			}
			
			
			logmap.put("logname", "smpp_dlr_post");
			
			new FileWrite().write(logmap);

			
		} catch(Exception exp) {
			
			exp.printStackTrace();
		}
	}
}

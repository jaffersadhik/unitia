package simulatar.dn.workers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.PduResponse;

import simulatar.event.handlers.SessionEventHandler;
import simulatar.manager.SessionManager;
import simulatar.server.QueueMap;
import simulatar.util.MapKeys;

public class SessionRedisQWorker extends Thread {


	String systemId = null;
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
				List<Map<String,Object>> dnlist=QueueMap.getInstance().getMesssages(systemId);
				if(dnlist!=null&&dnlist.size()>0){
					
				for(int i=0;i<dnlist.size();i++){
					
				
				aDn = dnlist.get(i);
				CustomerRedisHBData.INST.heartBeat(getName(),systemId);
						try {
								if (handler.getSession().isBound()) {
									
									
									DNTempBean bean=send(aDn, handler);
									
									if(bean.getFuture()==null){
										
										writeResponse(bean.getDnMap(),null);
										
									}else {
										
									while(!bean.getFuture().isDone()){
										
										try{
											Thread.sleep(1L);
												
										}catch(Exception e){
											
										}
									}
										
										PduResponse response=bean.getFuture().getResponse();
										
										if(response!=null) {			
											if(response.getCommandStatus()==SmppConstants.STATUS_OK){
												
												writeResponse(bean.getDnMap(),0);	

												
											}else{
												
												writeResponse(bean.getDnMap(),response.getCommandStatus());	
											}
											bean.getEventHandler().setInUse(false);
										} else {
											response=bean.getFuture().getResponse();
											
											if(response==null) {
												writeResponse(bean.getDnMap(),null);
												
											} else {
												writeResponse(bean.getDnMap(),response.getCommandStatus());
												bean.getEventHandler().setInUse(false);
											}
										}
										
									}
									
								} else {
									writeResponse(aDn,null);
								}
							
						} catch(Exception exp) {
							writeResponse(aDn,null);
						}
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
			aDn.put(MapKeys.DNPOSTSTATUS, msg_status);
			Map<String,Object> logmap=new HashMap<String,Object>();
			logmap.putAll(aDn);
			
			if(msg_status!=null&&msg_status.equals("SUCCESS")){
			
			}else{
				
				QueueMap.getInstance().offer(aDn.get(MapKeys.USERNAME).toString(),aDn);

			}
			
			
			
			
			
		} catch(Exception exp) {
			
			exp.printStackTrace();
		}
	}
	
	
	
	private DNTempBean send(Map aDn,SessionEventHandler sessionHandler) {

		aDn.remove("DNRTS");
		aDn.remove("DNSTS");

		DNTempBean tempBean = new DNTempBean();
		tempBean.setEventHandler(sessionHandler);
		tempBean.setDnMap(aDn);
		tempBean.setFuture(worker.sendMessage(sessionHandler.getSession(), aDn));
		
		return tempBean;
	}
}

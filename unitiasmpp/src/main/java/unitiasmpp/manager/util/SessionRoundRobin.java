
package unitiasmpp.manager.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.impl.DefaultSmppSession;

import unitiasmpp.event.handlers.SessionEventHandler;

public class SessionRoundRobin {
	//add and get so first value will be zero
	AtomicInteger ai = new AtomicInteger(-1);
	AtomicInteger availableIndex = new AtomicInteger(-1);
	List<SessionEventHandler> sessionHandlers = Collections.synchronizedList(new LinkedList<SessionEventHandler>());
	int sleepIncFact=2;//incremental factor
	int minSleep=25;//sleep in millis
	String systemId;
	
	public List<SessionEventHandler> getSessionHandlers() {
		return sessionHandlers;
	}

	public void setSessionHandlers(LinkedList<SessionEventHandler> sessionHandlers) {
		this.sessionHandlers = sessionHandlers;
	}

	
	public boolean addSession(SessionEventHandler aSessionHandler) throws Exception {

		boolean added = false;
		if (aSessionHandler != null) {
			if(systemId==null || systemId.equals(aSessionHandler.getSession().getConfiguration().getSystemId())) {
				//aSessionHandler.getSession().getConfiguration().setWindowSize(-1);
				added = sessionHandlers.add(aSessionHandler);
			} else {
				throw new Exception(systemId + " round robin won't fit "+aSessionHandler.getSession().getConfiguration().getSystemId());
			}
		}
		return added;

	}
	
	public synchronized SessionEventHandler getAvailableSession() throws Exception {
		int index = availableIndex.addAndGet(1);
		SessionEventHandler aSessionHandler = null;
		if(sessionHandlers.size()==0)
			throw new Exception("no bind exists...");
		if (index > (sessionHandlers.size() - 1)) {
			availableIndex.set(0);
			index = 0;
		}
		aSessionHandler=sessionHandlers.get(index);
		
		if(!aSessionHandler.isInUse()) {
			
			aSessionHandler.setInUse(true);
			return aSessionHandler;
		} else {
			aSessionHandler=null;
		}
				
		for(SessionEventHandler handler:sessionHandlers) {
			if(!handler.isInUse()) {
				aSessionHandler=handler;
				handler.setInUse(true);
				break;
			}
		}
		return aSessionHandler;
	}

	public synchronized SessionEventHandler getNextSession() {

		int index = ai.addAndGet(1);
		SessionEventHandler aSessionHandler = null;
		
		if (index > (sessionHandlers.size() - 1)) {
			ai.set(0);
			index = 0;
		}
		
		
		try {
			aSessionHandler = sessionHandlers.get(index);
		} catch(IndexOutOfBoundsException aie) {//sessions may be removed if unbound or expired
			try {
				ai.set(0);
				index=0;
				aSessionHandler = sessionHandlers.get(0);
			} catch(IndexOutOfBoundsException ignore) {}
		}
		
		if(aSessionHandler!=null) {
			if(!aSessionHandler.isInUse()) {
				aSessionHandler.setInUse(true);
			} else {
				try {
					//check if any of other session is free
					for(int indx=ai.addAndGet(1);indx<sessionHandlers.size();indx=ai.addAndGet(1)) {
						aSessionHandler=sessionHandlers.get(indx);
						if(!aSessionHandler.isInUse()) {
							break;
						}
					}
				} catch(IndexOutOfBoundsException ignore) {}
				
				if(!aSessionHandler.isInUse()) {
					//mark it as in use and other threads will not use it
					aSessionHandler.setInUse(true);
				} else {
					try {
						//incremental sleep if all sessions are busy sending dn's
						minSleep*=sleepIncFact;
						Thread.sleep(minSleep);
						//reset round robin index to first
						ai.set(0);
						//wait and call recursively to get a free session
						aSessionHandler=getNextSession();
					} catch (InterruptedException ignore) {}
					
				}
			}
		}
		
		minSleep=25;
		
		return aSessionHandler;
	}

	public boolean removeSession(SmppServerSession session) {

		boolean removed = false;
		if (session != null)
			for(SessionEventHandler aSessionHandler:sessionHandlers) {
				if(aSessionHandler.getSession()==session) {
					removed = sessionHandlers.remove(aSessionHandler);
					break;
				}
			}
		return removed;

	}
	
	public synchronized int unbindExpired(long expiryTime) {
		
		int unboundCount=0;
		LinkedList<SessionEventHandler> unbindList=new LinkedList<SessionEventHandler>();
		
		for(SessionEventHandler anHandler:sessionHandlers) {
	
			if(anHandler.getSession().getStateName().equals("CLOSED") || expiryTime<=(System.currentTimeMillis()-anHandler.getLastUsedTime().getTime())) {
				unbindList.add(anHandler);
			}			
		}		
		
		for(SessionEventHandler anHandler:unbindList) {
			try{
			anHandler.setExpired(true);
			DefaultSmppSession session=(DefaultSmppSession)anHandler.getSession();
			session.resetCounters();
			anHandler.getSession().close();
			anHandler.getSession().destroy();
			anHandler.fireChannelUnexpectedlyClosed();
			
			}catch(Exception e){
				
			}
		}		
		
		unboundCount=unbindList.size();
		
		return unboundCount;
	}
	
	public void resetCounters() {
		for(SessionEventHandler anHandler:sessionHandlers) {
			anHandler.getSession().getCounters().reset();
		}				
	}
	
	public int getHandlersCount() {
		return sessionHandlers.size();
	}
}

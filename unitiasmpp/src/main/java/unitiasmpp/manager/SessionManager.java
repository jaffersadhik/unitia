package unitiasmpp.manager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppServerSession;

import unitiasmpp.dn.workers.SessionRedisQWorker;
import unitiasmpp.event.handlers.SessionEventHandler;
import unitiasmpp.manager.util.SessionRoundRobin;

public class SessionManager {
	
	ConcurrentHashMap<String,SessionRoundRobin> txSessionsMap=new ConcurrentHashMap<String,SessionRoundRobin>();
	ConcurrentHashMap<String,SessionRoundRobin> rxTrxSessionsMap=new ConcurrentHashMap<String,SessionRoundRobin>();
	boolean sessionThreadEnabled=true;
	Map<String,LinkedList<Thread>> dnWorkerMap=new ConcurrentHashMap<String,LinkedList<Thread>>();
	
	public Map<String, LinkedList<Thread>> getDnWorkerMap() {
		return dnWorkerMap;
	}


	private SessionManager(){
		
	}
	
	// just to ensure the "Double Checked Locking" visit
	// "http://en.wikipedia.org/wiki/Singleton_pattern" for more details
	private static class SingletonHolder {
		public static final SessionManager INSTANCE = new SessionManager();
	}

	/**
	 * This method is used to get the reference of the engine
	 * @return SessionManager
	 */
	public static SessionManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	
	public synchronized int getTotalBindCount() {
		Collection<SessionRoundRobin> txVals=txSessionsMap.values();
		int totalBinds=0;
		for(SessionRoundRobin arr:txVals) {
			totalBinds+=arr.getHandlersCount();
		}
		Collection<SessionRoundRobin> rxtrxVals=rxTrxSessionsMap.values();

		for(SessionRoundRobin arr:rxtrxVals) {
			totalBinds+=arr.getHandlersCount();
		}
		return totalBinds;
	}


	public synchronized void addSession(SessionEventHandler aSessionHandler) throws Exception {
		
		String systemId=aSessionHandler.getSystemId();
		ConcurrentHashMap<String,SessionRoundRobin> sessionsMap=null;
		
		if(aSessionHandler.getSession().getBindType().equals(SmppBindType.TRANSMITTER)) {
			sessionsMap=txSessionsMap;
		} else {
			sessionsMap=rxTrxSessionsMap;
		}
		
		SessionRoundRobin srrObj=sessionsMap.get(systemId);
		if(srrObj!=null){
			srrObj.addSession(aSessionHandler);
		}else {
			srrObj=new SessionRoundRobin();
			srrObj.addSession(aSessionHandler);
			sessionsMap.put(systemId,srrObj);
		}
		
		if(!aSessionHandler.getSession().getBindType().equals(SmppBindType.TRANSMITTER)) {
		
		
			
			SessionRedisQWorker sworker=new SessionRedisQWorker( systemId,aSessionHandler);
			aSessionHandler.setInUse(true);
			LinkedList<Thread> list=dnWorkerMap.get(systemId);
			if(list==null) {
				list=new LinkedList<Thread>();		
				dnWorkerMap.put(systemId, list);
			}
			list.add(sworker);
			
			sworker.start();
		}
		
	
			
		
	}
	
	public synchronized void removeSession(SmppServerSession session) {
		
		String systemId=session.getConfiguration().getSystemId();
		ConcurrentHashMap<String,SessionRoundRobin> sessionsMap=null;
		
		if(session.getBindType().equals(SmppBindType.TRANSMITTER)) {
			sessionsMap=txSessionsMap;
		} else {
			sessionsMap=rxTrxSessionsMap;
		}		
		
		SessionRoundRobin srrObj=sessionsMap.get(systemId);
		
		if(srrObj!=null) {
			if(srrObj.getHandlersCount()==0) {
				sessionsMap.remove(systemId);
				if(dnWorkerMap.containsKey(systemId) && !sessionThreadEnabled) {
					((SessionRedisQWorker)dnWorkerMap.get(systemId).get(0)).setDone(true);
					dnWorkerMap.remove(systemId);
				} 
			}
		}		

	}
	
	public SessionEventHandler getSession(String systemId) {
		
		SessionRoundRobin srrObj=rxTrxSessionsMap.get(systemId);
		SessionEventHandler aSessionHandler=null;
		
		if(srrObj!=null) {
			aSessionHandler=srrObj.getNextSession();
			if(aSessionHandler!=null)
				aSessionHandler.updateLastUsedTime();
		}
		
		return aSessionHandler;
		
	}
	
	
	public synchronized SessionEventHandler getAvailableSession(String systemId) throws Exception {
		
		SessionRoundRobin srrObj=rxTrxSessionsMap.get(systemId);
		SessionEventHandler aSessionHandler=null;
		
		if(srrObj!=null) {
			aSessionHandler=srrObj.getAvailableSession();
/*			if(aSessionHandler!=null)
				aSessionHandler.updateLastUsedTime();*/
		} else {
			throw new Exception("no bind exists");
		}
		
		return aSessionHandler;
		
	}
	
	public int getRxHandlersCount(String systemId) {
		
		int count=-1;
		SessionRoundRobin srrObj=rxTrxSessionsMap.get(systemId);
		if(srrObj!=null)
			count=srrObj.getHandlersCount();
		
		return count;
		
	}
	
	public int removeExpiredSessions(long expiryMillis) {
		int totalRemovedSession=0;
		Collection<SessionRoundRobin> col1=txSessionsMap.values();		
		for(SessionRoundRobin srr:col1) {
			totalRemovedSession+=srr.unbindExpired(expiryMillis);
		}		
		
		Collection<SessionRoundRobin> col2=rxTrxSessionsMap.values();
		for(SessionRoundRobin srr:col2) {
			totalRemovedSession+=srr.unbindExpired(expiryMillis);
		}	
		return totalRemovedSession;
	}
	
	public ConcurrentHashMap<String, SessionRoundRobin> getTxSessionsMap() {
		return txSessionsMap;
	}


	public ConcurrentHashMap<String, SessionRoundRobin> getRxTrxSessionsMap() {
		return rxTrxSessionsMap;
	}

	public int getTotalSessionCount(String systemId,int commandId) {		
		int sessionCount=0;
		if(commandId==SmppConstants.CMD_ID_BIND_TRANSMITTER) {
			SessionRoundRobin srr=txSessionsMap.get(systemId);
			if(srr!=null)
				sessionCount=srr.getHandlersCount();
		} else {
			SessionRoundRobin srr2=rxTrxSessionsMap.get(systemId);
			if(srr2!=null)
				sessionCount=srr2.getHandlersCount();
		}
		return sessionCount;
	}
	
	public void resetCounters() {		
		Collection<SessionRoundRobin> col1=txSessionsMap.values();
		for(SessionRoundRobin srr:col1) {
			srr.resetCounters();
		}
		Collection<SessionRoundRobin> col2=rxTrxSessionsMap.values();
		for(SessionRoundRobin srr:col2) {
			srr.resetCounters();
		}		
	}
}

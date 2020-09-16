package unitiasmpp.dn.workers;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public enum CustomerRedisHBData {
INST;
ConcurrentHashMap<String,Date> hbMap=new ConcurrentHashMap<String,Date>();
	public ConcurrentHashMap<String, Date> getHbMap() {
	return hbMap;
}

	public synchronized void heartBeat(String tname,String systemId) {
		hbMap.put(tname+"-"+systemId,new Date());
	}
	
	public void remove(String tname,String systemId) {
		hbMap.remove(tname+"-"+systemId);
	}
}

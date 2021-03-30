package smpp2;

import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.SmppSession;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.RoundRobinTon;

import java.util.*;

public class SessionStore  {

	private static SessionStore obj=new SessionStore();
	
	Map<String,List<SmppSession>> rxsessionlist=new HashMap<String,List<SmppSession>>();
	
	Map<String,List<SmppSession>> txsessionlist=new HashMap<String,List<SmppSession>>();

	Map<SmppSession,String> lastUpdate=new HashMap<SmppSession,String>();
	
	private SessionStore(){
		
	}
	
	public static SessionStore getInstance(){
		
		if(obj==null){
			
			obj=new SessionStore();
		}
		
		return obj;
	}
	
	public void add(SmppSession session){
		
		String username=session.getConfiguration().getSystemId();
		
		String hostname=session.getConfiguration().getHost();
		
		String bindType=getBindType(session);
		
		boolean isReceivable=isReceivable(bindType);
		
		add(username,isReceivable,session);
		
		
	}
	
	
	public void remove(SmppSession session){
		
		String username=session.getConfiguration().getSystemId();
		
		String bindType=getBindType(session);
		
		boolean isReceivable=isReceivable(bindType);
		
		remove(username,isReceivable,session);
		
		
	}
	
	public void lastPDUTime(SmppSession session){
		
		lastUpdate.put(session, ""+System.currentTimeMillis());
	}
	

	
	private void remove(String username,boolean isReceivable,SmppSession session){
		
		if(isReceivable){
			
			rxsessionlist.get(username).remove(session);
		}else{
		
			txsessionlist.get(username).remove(session);

		}
			
		lastUpdate.remove(session);
		
		
	}
	
	
	private void add(String username,boolean isReceivable,SmppSession session){
		
		List<SmppSession> sessionlist=null;
		
		if(isReceivable){
			
			sessionlist=rxsessionlist.get(username);
		}else{
		
			sessionlist=txsessionlist.get(username);

		}
		
		if(sessionlist==null){
			
			sessionlist=new ArrayList<SmppSession>();
			
			if(isReceivable){
				
				rxsessionlist.put(username, sessionlist);
			}else{

				txsessionlist.put(username, sessionlist);
			}
		}
		
		sessionlist.add(session);

	}

	private boolean isReceivable(String bindType) {
		if(bindType.equals("TX")){
			
			return false;
		}
		return true;
	}

	private String getBindType(SmppSession session) {
		 switch (session.getBindType()) {
         case TRANSCEIVER:
             return "TRX";
         case RECEIVER:
             return "RX";
         default:
        	 return "TX";  
     }
	}
	
	public List<Map<String,Object>> getBindData(){
	
		List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
	
		setBind(result,rxsessionlist);
		
		setBind(result,txsessionlist);
	
		return result;
	}

	private void setBind(List<Map<String,Object>> result,
			Map<String, List<SmppSession>> txsessionlist2) {
		
		Iterator itr=txsessionlist2.keySet().iterator();
		
		while(itr.hasNext()){
			
			String username=itr.next().toString();
					
			List<SmppSession> sessionlist=txsessionlist2.get(username);
			
			for(int i=0;i<sessionlist.size();i++){
				
				Map<String,Object> data=new HashMap<String,Object>();
				
				SmppSession session=sessionlist.get(i);
				
				data.put("bindtype", getBindType(session));
				data.put("username", session.getConfiguration().getSystemId());
				data.put("ip", session.getConfiguration().getHost());
				data.put("uptime", lastUpdate.get(session));
				data.put("ctime",""+session.getBoundTime());
				data.put("window",""+session.getSendWindow());
				data.put("status",""+session.getConfiguration().getRequestExpiryTimeout());

				data.put("ereqcount",""+session.getCounters().getRxEnquireLink().getRequest());
				data.put("erespcount",""+session.getCounters().getRxEnquireLink().getResponse());
				data.put("excount",""+session.getCounters().getRxEnquireLink().getRequestExpired());
				data.put("etime",""+session.getCounters().getRxEnquireLink().getRequestResponseTime());
				data.put("eptime",""+session.getCounters().getRxEnquireLink().getRequestEstimatedProcessingTime());
				data.put("ewtime",""+session.getCounters().getRxEnquireLink().getRequestWaitTime());
				data.put("estatus",session.getCounters().getRxEnquireLink().getResponseCommandStatusCounter().createSortedMapSnapshot());

				
				data.put("sreqcount", ""+session.getCounters().getRxSubmitSM().getRequest());
				data.put("srespcount", ""+session.getCounters().getRxSubmitSM().getResponse());
				data.put("sxcount", ""+session.getCounters().getRxSubmitSM().getRequestExpired());
				data.put("stime",""+session.getCounters().getRxSubmitSM().getRequestResponseTime());
				data.put("sptime",""+session.getCounters().getRxSubmitSM().getRequestEstimatedProcessingTime());
				data.put("swtime",""+session.getCounters().getRxSubmitSM().getRequestWaitTime());
				data.put("sstatus",session.getCounters().getRxSubmitSM().getResponseCommandStatusCounter().createSortedMapSnapshot());

				data.put("dreqcount", ""+session.getCounters().getTxDeliverSM().getRequest());
				data.put("drespcount", ""+session.getCounters().getTxDeliverSM().getResponse());
				data.put("dxcount", ""+session.getCounters().getTxDeliverSM().getRequestExpired());
				data.put("dtime",""+session.getCounters().getTxDeliverSM().getRequestResponseTime());
				data.put("dptime",""+session.getCounters().getTxDeliverSM().getRequestEstimatedProcessingTime());
				data.put("dwtime",""+session.getCounters().getTxDeliverSM().getRequestWaitTime());
				data.put("dstatus",session.getCounters().getTxDeliverSM().getResponseCommandStatusCounter().createSortedMapSnapshot());

				
				

			}
		}
		
	}
	
	
	private List<SmppSession> getExpiredSessionList(){
		List<SmppSession> result=new ArrayList<SmppSession>();
		Iterator<SmppSession> itr=lastUpdate.keySet().iterator();
		
		while(itr.hasNext()){
			
			SmppSession session=itr.next();
			String updatetime=lastUpdate.get(session);
			
			long ul=0;
			
			try{
				ul=Long.parseLong(updatetime);
			}catch(Exception e){
				
			}
			
			
			if(ul>0){
				
				if(ul<(System.currentTimeMillis()-(20*60*1000))){
					result.add(session);
				}
			}
		}
		
		return result;
	}
	
	public void removeExpiredSession(){
		
		List<SmppSession> result=getExpiredSessionList();
		
		for(int i=0;i<result.size();i++){
			
			remove(result.get(i));
		}
	}
	
	
	public SmppSession getSession(String systemid){
		
		if(rxsessionlist.get(systemid)!=null){
			
			return rxsessionlist.get(systemid).get(RoundRobinTon.getInstance().getCurrentIndex("smppdnsystemid_"+systemid, rxsessionlist.size()));
			
		}
		
		return null;
	}

	public void print(){
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("username", "sys");
		logmap.put("logname", "binddata");
		logmap.put("livesession", getBindData());
		new FileWrite().write(logmap);
	}
}

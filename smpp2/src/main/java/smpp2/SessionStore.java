package smpp2;

import com.cloudhopper.smpp.SmppServerSession;
import java.util.*;

public class SessionStore  {

	private static SessionStore obj=new SessionStore();
	
	Map<String,List<SmppServerSession>> rxsessionlist=new HashMap<String,List<SmppServerSession>>();
	
	Map<String,List<SmppServerSession>> txsessionlist=new HashMap<String,List<SmppServerSession>>();

	Map<SmppServerSession,String> lastUpdate=new HashMap<SmppServerSession,String>();
	
	private SessionStore(){
		
	}
	
	public static SessionStore getInstance(){
		
		if(obj==null){
			
			obj=new SessionStore();
		}
		
		return obj;
	}
	
	public void add(SmppServerSession session){
		
		String username=session.getConfiguration().getSystemId();
		
		String hostname=session.getConfiguration().getHost();
		
		String bindType=getBindType(session);
		
		boolean isReceivable=isReceivable(bindType);
		
		add(username,isReceivable,session);
		
		
	}
	
	
	private void remove(SmppServerSession session){
		
		String username=session.getConfiguration().getSystemId();
		
		String bindType=getBindType(session);
		
		boolean isReceivable=isReceivable(bindType);
		
		remove(username,isReceivable,session);
		
		
	}
	
	public void lastPDUTime(SmppServerSession session){
		
		lastUpdate.put(session, ""+System.currentTimeMillis());
	}
	
	private void remove(String username,boolean isReceivable,SmppServerSession session){
		
		if(isReceivable){
			
			rxsessionlist.get(username).remove(session);
		}else{
		
			txsessionlist.get(username).remove(session);

		}
			
		
	}
	
	
	private void add(String username,boolean isReceivable,SmppServerSession session){
		
		List<SmppServerSession> sessionlist=null;
		
		if(isReceivable){
			
			sessionlist=rxsessionlist.get(username);
		}else{
		
			sessionlist=txsessionlist.get(username);

		}
		
		if(sessionlist==null){
			
			sessionlist=new ArrayList<SmppServerSession>();
			
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

	private String getBindType(SmppServerSession session) {
		 switch (session.getBindType()) {
         case TRANSCEIVER:
             return "TRX";
         case RECEIVER:
             return "RX";
         default:
        	 return "TX";  
     }
	}
	
	public List<Map<String,String>> getBindData(){
	
		List<Map<String,String>> result=new ArrayList<Map<String,String>>();
	
		setBind(result,rxsessionlist);
		
		setBind(result,txsessionlist);
	
		return result;
	}

	private void setBind(List<Map<String,String>> result,
			Map<String, List<SmppServerSession>> txsessionlist2) {
		
		Iterator itr=txsessionlist2.keySet().iterator();
		
		while(itr.hasNext()){
			
			String username=itr.next().toString();
					
			List<SmppServerSession> sessionlist=txsessionlist2.get(username);
			
			for(int i=0;i<sessionlist.size();i++){
				
				Map<String,String> data=new HashMap<String,String>();
				
				SmppServerSession session=sessionlist.get(i);
				
				data.put("bindtype", getBindType(session));
				data.put("username", session.getConfiguration().getSystemId());
				data.put("ip", session.getConfiguration().getHost());
				data.put("uptime", lastUpdate.get(session));

			}
		}
		
	}
}

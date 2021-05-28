package concateselect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.dao.Select;
import com.winnovature.unitia.util.dao.Table;
import com.winnovature.unitia.util.misc.FeatureCode;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageType;
import com.winnovature.unitia.util.redis.QueueSender;


public class DBReceiver extends Thread {

	public static boolean GRACESTOP=false;
	
	String username=null;
	
	String poolname=null;

	String queuename=null;
	
	boolean isDNDelay=false;
	
	QueueSender queuesender=new QueueSender();
	
	Select select=new Select();
	
	FileWrite log=new FileWrite();
	
	public DBReceiver(String poolname,String username){
	
		this.username=username;
		
	
		this.poolname=poolname;
		if(poolname.equals("schedulepool")){
			
			queuename="commonpool";
			
		}else if(poolname.equals("dndelaypool")){
			
			queuename="dnreceiverpool";
			
			isDNDelay=true;
			
		}else if(poolname.equals("smppdn")){
			
			queuename="smppdn_"+username;
			
			isDNDelay=true;
			
		}else{
			
			queuename=poolname;
		}
	}
	public void run(){
		
		
	if(!Table.getInstance().isAvailableTable(poolname)){
			
			Table.getInstance().addTable(poolname);
	}
		while(!GRACESTOP){
			
			
			Map<String,List<Map<String,Object>>> data=select.getDataForConcate(poolname,username);
			
			if(data!=null&&data.size()>0){
				
				data=getQualifiedData(data);
				
				if(data!=null&&data.size()>0){
					
					List<Map<String,Object>> result=getSubmitList(data);
					
					for(int i=0;i<result.size();i++){
						
						sendUntilSuccess(result.get(i));
						
						}
					
					deleteUntilSuccess(result);
				}
			}else{
				

				PollerStartup.getInstance(poolname).runninguser.remove(username);
				
				return;
			}
			
			
		}
	}
	
	
	
	
private void sendUntilSuccess(Map<String, Object> map) {
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("module", "concateselect");
		logmap.put("logname", "concateselect");
		logmap.putAll(map);
		
	
		
		while(true){
			
			boolean result=false;
			try {
				result = queuesender.sendL( "commonpool", map, true, logmap);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
			if(result){
				
				new FileWrite().write(logmap);

				return;
			}
			
			gotosleep();
		}
		
	}


	private List<Map<String, Object>>  getSubmitList(Map<String, List<Map<String, Object>>> data) {
		
		 List<Map<String, Object>> result=new ArrayList<Map<String, Object>>();
	Iterator itr=data.keySet().iterator();
		
		while(itr.hasNext()){
		
			String msgid=itr.next().toString();
			
			List<Map<String,Object>> datalist=data.get(msgid);
			
			String ackidlist=getAckidList(datalist);
			
			int totalmsgcount=getTotalMessageCount(datalist.get(0));

			String fullmessage=getFullMessage(totalmsgcount,datalist);
			
			String featurecode=getFeatureCode(datalist.get(0).get(MapKeys.MSGTYPE).toString());
		
			List<Map<String,Object>> splitmaplist = getSplitMapList(datalist);

			Map<String,Object> msgmap=datalist.get(0);
			
			msgmap.put(MapKeys.MSGLIST, splitmaplist);
			
			msgmap.put(MapKeys.FEATURECODE, featurecode);
			
			msgmap.put(MapKeys.ACKID_LIST, ackidlist);
			
			msgmap.put(MapKeys.FULLMSG, fullmessage);

			result.add(msgmap);
		}
		
		
		return result;
	}
	private List<Map<String, Object>> getSplitMapList(List<Map<String, Object>> datalist) {
		
		List<Map<String, Object>> msgmaplist=new ArrayList<Map<String, Object>>();

		for(int i=0;i<datalist.size();i++)
		{
			
			Map<String,Object> cloneMsgMap = new HashMap<String,Object>();
									
			cloneMsgMap.put(MapKeys.MSGID, datalist.get(i).get(MapKeys.MSGID).toString());

			cloneMsgMap.put(MapKeys.SPLIT_SEQ,datalist.get(i).get(MapKeys.SPLIT_SEQ).toString());
		
			cloneMsgMap.put(MapKeys.FULLMSG,datalist.get(i).get(MapKeys.FULLMSG).toString());
			
			cloneMsgMap.put(MapKeys.UDH,datalist.get(i).get(MapKeys.UDH).toString());
			
			msgmaplist.add(cloneMsgMap);

		}
		
		return msgmaplist;
	}
	private String getFeatureCode(String msgtype) {
		
		
		if(msgtype.equals(MessageType.UM)){
			
			return FeatureCode.UMC;
			
		}else if(msgtype.equals(MessageType.UF)){
			
			return FeatureCode.UFC;

		}else if(msgtype.equals(MessageType.PUM)){
			
			return FeatureCode.PUMC;
		}else if(msgtype.equals(MessageType.BM)){
			
			return FeatureCode.BMC;
		}else 	if(msgtype.equals(MessageType.EM)){
			
			return FeatureCode.EMC;

		}else if(msgtype.equals(MessageType.PEM)){
			
			return FeatureCode.PEMC;

			
		}else if(msgtype.equals(MessageType.EF)){
			
			return FeatureCode.EFC;

			
		}
		return null;
	}
	private String getFullMessage(int totalmsgcount, List<Map<String, Object>> datalist) {
		
		StringBuffer sb=new StringBuffer();
		
		for(int i=1;i<=totalmsgcount;i++){
			
			sb.append(getMessage(i,datalist));
		}
		
		return sb.toString();
	}
	private String getMessage(int seq, List<Map<String, Object>> datalist) {

		for(int i=0;i<datalist.size();i++){
		
			try{
				int splitseq=Integer.parseInt(datalist.get(i).get(MapKeys.SPLIT_SEQ).toString());
				
				if(seq==splitseq){
				
					return datalist.get(i).get(MapKeys.FULLMSG).toString();
					
				}
			}catch(Exception e){
				
			}
		}
		
		return null;
	}
	private String getAckidList(List<Map<String, Object>> datalist) {
		
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<datalist.size();i++){
		
			sb.append(datalist.get(i).get(MapKeys.ACKID)).append("<<SPLIT>>");
		}
		
		return sb.toString();
	}
	private Map<String,List<Map<String,Object>>> getQualifiedData(Map<String,List<Map<String,Object>>> data){
		
		Map<String,List<Map<String,Object>>> result=new HashMap<String,List<Map<String,Object>>>();
		
		Iterator itr=data.keySet().iterator();
		
		while(itr.hasNext()){
			
			String msgid=itr.next().toString();
			
			List<Map<String,Object>> datalist=data.get(msgid);
			
			int totalmsgcount=getTotalMessageCount(datalist.get(0));
			
			if(totalmsgcount==datalist.size()){
				
				result.put(msgid, datalist);
			}
		}
	
		return result;
	}
	private int getTotalMessageCount(Map<String, Object> map) {
		try{
			
			return Integer.parseInt(map.get(MapKeys.TOTAL_MSG_COUNT).toString());
		}catch(Exception e){
			
		}
		return 0;
	}
	private void deleteUntilSuccess(List<Map<String, Object>> data) {

		
		while(true){
			
			if(select.delete( poolname,data,false)){
				
				return;
			}
			
			gotosleep();
		}
		
	}
	
	
	
	
	private void gotosleep() {
		
		try{
			
			Thread.sleep(50L);
		}catch(Exception e){
			
		}
	}
}

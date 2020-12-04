package unitiaroute;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.RoundRobinTon;
import com.winnovature.unitia.util.misc.ToJsonString;

public class RouteProcessor {
	
	Map<String,Object> msgmap=null;
	
	String redisid=null;
	String tname=null;
	
	private boolean isfurtherprocess=true;
	
	public RouteProcessor(Map<String,Object> msgmap, String redisid, String tname){
	
		this.redisid=redisid;
		this.tname=tname;
		this.msgmap=msgmap;
	}
	
	
	public void setIsfurtherprocess(boolean isfurtherprocess) {
		this.isfurtherprocess = isfurtherprocess;
	}




	
	public void doSMSCIDAvailable()throws Exception{
	
		

		if(isfurtherprocess){
	
		String routegroup=(String)msgmap.get(MapKeys.ROUTEGROUP);
		List<String> smscidlist=RouteGroup.getInstance().getSmscidList(routegroup);
		if(smscidlist!=null&&smscidlist.size()>0) {
			
			
			String key=(String)msgmap.get(MapKeys.ROUTEKEY);

			String pointer=getPointer(msgmap, smscidlist.size());
			if(pointer==null){
			
				String smscid=smscidlist.get(RoundRobinTon.getInstance().getCurrentIndex(key, smscidlist.size()));
				msgmap.put(MapKeys.SMSCID,smscid);
				msgmap.put(MapKeys.SMSCID_ORG,smscid);
				
			}else{
			
				String smscid=smscidlist.get(Integer.parseInt(pointer));
				msgmap.put(MapKeys.SMSCID,smscid);
				msgmap.put(MapKeys.SMSCID_ORG,smscid);
				
			}
			return;
			
		}
		
		isfurtherprocess =false;
		msgmap.put(MapKeys.KANNEL_POPTIME, ""+System.currentTimeMillis());
		msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_ROUTE_GROUP);
		}

		
	}


	private String getPointer(Map<String,Object> msgmap,int routelistsize){
		
	
		String udh=(String)msgmap.get(MapKeys.UDH);
		
		String pointerstring=null;
		
		if(udh!=null&&(udh.startsWith("050003")||udh.startsWith("060804"))){
			
			String refno="00";
			int pointer=0;
			
			try{
			if(udh.startsWith("0500")) {
				refno=udh.substring(6,8);
				BigInteger biref = new BigInteger(refno, 16);
				int refnoInt=Integer.parseInt(biref.toString());
				pointer=refnoInt%routelistsize;
                pointerstring=""+pointer;
				} else if(udh.startsWith("0608")) {
				refno=udh.substring(6,10);
				BigInteger biref = new BigInteger(refno, 16);
				int refnoInt=Integer.parseInt(biref.toString());
				pointer=refnoInt%routelistsize;
				pointerstring=""+pointer;
				}
			}catch(Exception ignore){

				return pointerstring;
			}
			
		}

		return pointerstring;
	}
	
	
	public void doKannelAvailable() throws Exception {
		
	
		if(isfurtherprocess){
			
		String route=(String)msgmap.get(MapKeys.SMSCID);
		
		msgmap.put(MapKeys.SMSCID_ORG, route);
		
		Map<String,String> kannelinfo=InternalKannel.getInstance().getKannelInfo(route);
			
		if(kannelinfo==null){
			
			kannelinfo=Kannel.getInstance().getKannelInfo(route);
		}else {
		
			msgmap.put(MapKeys.DN_IP, "dn");
			msgmap.put(MapKeys.DN_PORT, "8080");
		
		}
				
		
		if(kannelinfo!=null) {
			msgmap.put(MapKeys.KANNEL_IP, kannelinfo.get(MapKeys.KANNEL_IP));
			msgmap.put(MapKeys.KANNEL_PORT, kannelinfo.get(MapKeys.KANNEL_PORT));
			if(route.equals("apps")||route.equals("reapps")){
				
				msgmap.put(MapKeys.ROUTECLASS_ORG,"4");
	        	
			}else{
			
				msgmap.put(MapKeys.ROUTECLASS_ORG, kannelinfo.get(MapKeys.ROUTECLASS));
	        	
			}
			
			return;
		}
		msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_SMSCID);
		isfurtherprocess=false;

		}
		
	}

	
	public boolean isIsfurtherprocess() {
		return isfurtherprocess;
	}


	public String toString(){
		
		return ToJsonString.toString(msgmap);
	}

	
	}

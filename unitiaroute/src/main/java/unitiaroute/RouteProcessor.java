package unitiaroute;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.misc.Convertor;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.MessageType;
import com.winnovature.unitia.util.misc.RoundRobinTon;
import com.winnovature.unitia.util.misc.RouterLog;
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


	public void doRouteGroupAvailable() throws Exception {
		

		if(isfurtherprocess){

		String username=msgmap.get(MapKeys.USERNAME).toString();
		String superadmin=PushAccount.instance().getPushAccount(username).get(MapKeys.SUPERADMIN);
		String admin=PushAccount.instance().getPushAccount(username).get(MapKeys.ADMIN);

		String operator=(String)msgmap.get(MapKeys.OPERATOR);
		String circle=(String)msgmap.get(MapKeys.CIRCLE);

		String routegroup=MobileRouting.getInstance().getRouteGroup(msgmap.get(MapKeys.MOBILE).toString(), msgmap.get(MapKeys.ROUTECLASS).toString());
		
		if(routegroup!=null&&routegroup.trim().length()>0) {
			
			msgmap.put(MapKeys.ROUTEGROUP, routegroup.trim());
			
			msgmap.put(MapKeys.ROUTEKEY, "mobile");
			
			msgmap.put(MapKeys.ROUTELOGIC, "0");
			
			return ;
			
		}
		
		if(!msgmap.get(MapKeys.COUNTRYCODE).toString().startsWith("91")){


			for(int logic=1;logic<9;logic++) {
				
				String key=getKey(superadmin,admin,username,msgmap.get(MapKeys.COUNTRYCODE).toString(),logic);
				
				routegroup=InternationalRoute.getInstance().getRouteGroup(key);
				
				if(routegroup!=null&&routegroup.trim().length()>0) {
					
					msgmap.put(MapKeys.ROUTEGROUP, routegroup.trim());
					
					msgmap.put(MapKeys.ROUTEKEY, key);
					
					msgmap.put(MapKeys.ROUTELOGIC, "11"+logic);
					
					return ;
					
				}else{
					
					msgmap.put(MapKeys.ROUTEKEY, key);

				}
				
			}

		}
		
		if(msgmap.get(MapKeys.COUNTRYCODE).toString().startsWith("91")){

			for(int logic=1;logic<5;logic++) {
				
				String key=getKey(operator,circle,msgmap.get(MapKeys.SENDERID).toString(),logic);
				
				routegroup=SenderidRouting.getInstance().getRouteGroup(key, msgmap.get(MapKeys.SENDERID).toString());
				
				if(routegroup!=null&&routegroup.trim().length()>0) {
					
					msgmap.put(MapKeys.ROUTEGROUP, routegroup.trim());
					
					msgmap.put(MapKeys.ROUTEKEY, key);
					
					msgmap.put(MapKeys.ROUTELOGIC, "12"+logic);
					
					return ;
					
				}else{
					
					msgmap.put(MapKeys.ROUTEKEY, key);

				}
			}

		for(int logic=1;logic<17;logic++) {
			
			String key=getKey(superadmin,admin,username,operator,circle,logic);
			
			routegroup=Route.getInstance().getRouteGroup(key,msgmap.get(MapKeys.ROUTECLASS).toString());
			
			if(routegroup!=null&&routegroup.trim().length()>0) {
				
				msgmap.put(MapKeys.ROUTEGROUP, routegroup.trim());
				
				msgmap.put(MapKeys.ROUTEKEY, key);
				
				msgmap.put(MapKeys.ROUTELOGIC, ""+logic);
				
				return ;
				
			}else{
				
				msgmap.put(MapKeys.ROUTEKEY, key);

			}
		}
		}
	
		isfurtherprocess=false;

		msgmap.put(MapKeys.STATUSID, ""+MessageStatus.ROUTEGROUP_NOT_FOUND);
	
		}
		
		
		return ;
	}


	private String getKey(String operator, String circle, String senderid, int logic) {

		switch(logic) {
		
		case 1:
			 return Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION+senderid+Route.CONJUNCTION;
		case 2:
			 return Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+senderid+Route.CONJUNCTION;
		case 3:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION+senderid+Route.CONJUNCTION;
		case 4:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+senderid+Route.CONJUNCTION;
		}
		
		return "";
		
	}


	private String getKey(String superadmin,String admin,String username, String operator, String circle, int logic) {

		switch(logic) {
		
		case 1:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 2:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 3:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 4:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 5:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 6:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 7:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 8:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 9:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 10:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 11:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 12:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 13:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 14:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 15:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 16:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
	
				 
		
		}
		
		return "";
	}


	private String getKey(String superadmin,String admin,String username, String countrycode, int logic) {

		switch(logic) {
		
		case 1:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+countrycode+Route.CONJUNCTION;
		case 2:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 3:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+countrycode+Route.CONJUNCTION;
		case 4:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 5:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+countrycode+Route.CONJUNCTION;
		case 6:
			 return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 7:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+countrycode+Route.CONJUNCTION;
		case 8:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
			
		}
		
		return "";
	}


	
	public boolean isIsfurtherprocess() {
		return isfurtherprocess;
	}


	public String toString(){
		
		return ToJsonString.toString(msgmap);
	}


	public void doEntityValidation() {
		
		
			
			msgmap.put(MapKeys.ENTITYID, Entity.getInstance().getEntity(msgmap.get(MapKeys.USERNAME).toString(), msgmap.get(MapKeys.SENDERID).toString()));
		
	}
	
	public void dodefaultEntityValidation() {
		
		String entityid=(String)msgmap.get(MapKeys.ENTITYID);
		
		if(entityid==null||entityid.trim().length()<1){
		
			Map<String,String> data=Entity.getInstance().getEntity(msgmap.get(MapKeys.USERNAME).toString());
			
			if(data!=null){
				
				msgmap.put(MapKeys.SENDERID, data.get("senderid"));
				msgmap.put(MapKeys.ENTITYID, data.get("entityid"));

	
			}
		}
		
	}


	public void isDLT() {

		if(isfurtherprocess){
		
			
			String entityid=(String)msgmap.get(MapKeys.ENTITYID);
			
			if(entityid==null||entityid.trim().length()<1){
				
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.NO_ENTITYID);

				isfurtherprocess=false;
		
		}
		
	}
	}
	
	}

package com.winnovature.unitia.util.processor;

import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.RoundRobinTon;
import com.winnovature.unitia.util.redis.QueueSender;
import com.winnovature.unitia.util.routing.Kannel;
import com.winnovature.unitia.util.routing.NumberingPlan;
import com.winnovature.unitia.util.routing.Route;
import com.winnovature.unitia.util.routing.RouteGroup;
import com.winnovature.unitia.util.routing.SplitGroup;

public class TraiProcessor {
	
	private static String CONJUNCTION="~";

	Map<String,String> msgmap=null;
	
	Map<String,String> logmap=null;
	
	private boolean isfurtherprocess=true;

	private boolean isretrymsg=false;
	
	public TraiProcessor(Map<String,String> msgmap,Map<String,String> logmap){
		
		this.logmap=logmap;
		this.msgmap=msgmap;
	}
	
	public TraiProcessor doNumberingPlan(){
		
		if(isfurtherprocess){
		

			String mobile=msgmap.get(MapKeys.MOBILE);
			for(int i=7;i>5;i--) {
				
				String series =mobile.substring(2, i);
				
				Map<String,String> npinfo=NumberingPlan.getInstance().getNPInfo(series);
				
				if(npinfo!=null) {
					
					msgmap.put(MapKeys.OPERATOR, npinfo.get(MapKeys.OPERATOR));
					msgmap.put(MapKeys.CIRCLE, npinfo.get(MapKeys.CIRCLE));
		
					return this;
				}
			}
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.MOBILE_SERIES_NOT_REGISTERED_NP);

			isfurtherprocess=false;
		
		}
		
		return this;
	}
	
	public TraiProcessor doSMSCIDAvailable() {
		
		if(isfurtherprocess){
		String routegroup=msgmap.get(MapKeys.ROUTEGROUP).toString();
		List<String> smscidlist=RouteGroup.getInstance().getSmscidList(routegroup);
		if(smscidlist!=null&&smscidlist.size()>0) {
			
			String key=msgmap.get(MapKeys.ROUTEKEY).toString();

			String smscid=smscidlist.get(RoundRobinTon.getInstance().getCurrentIndex(key, smscidlist.size()));
			msgmap.put(MapKeys.ROUTE,smscid);
			return this;
		}
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_ROUTE_GROUP);

			isfurtherprocess=false;

		}
		

		
		return this;
	}

	public TraiProcessor doKannelAvailable() {
		
		if(isfurtherprocess){
		String route=msgmap.get(MapKeys.ROUTE);
			
		Map<String,String> kannelinfo=Kannel.getInstance().getKannelInfo(route);
		
		if(kannelinfo!=null) {
			
			msgmap.put(MapKeys.PORT, kannelinfo.get(MapKeys.PORT));
			msgmap.put(MapKeys.SPLITGROUP, kannelinfo.get(MapKeys.SPLITGROUP));
            return this; 
			
		}
		msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_SMSCID);
		
		isfurtherprocess=false;

		}

		return this;
	}

	
	public TraiProcessor doSplitGroupAvilable() {

		if(isfurtherprocess){
		String splitgroup=msgmap.get(MapKeys.SPLITGROUP);
		String msgtype=msgmap.get(MapKeys.MSGTYPE);

	
		Map<String,Map<String,String>> splitgroupinfo=SplitGroup.getInstance().getSplitGroup(splitgroup);
		
		if(splitgroupinfo!=null && splitgroup.trim().length()>0) {
			
			if(splitgroupinfo.containsKey(msgtype)) {
				Map<String,String> splitgroupdata=splitgroupinfo.get(msgtype);
				msgmap.put(MapKeys.SMSMAXLENGTH, splitgroupdata.get(MapKeys.SMSMAXLENGTH));
				msgmap.put(MapKeys.SMSSPLITLENGTH, splitgroupdata.get(MapKeys.SMSSPLITLENGTH));
				
				return this;
			}
		
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_MSGTYPE);

		}
		
		msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_SPLITGROUP);
		
		isfurtherprocess=false;

		}


		return this;
	}


	
	
	public TraiProcessor doRouteGroupAvailable() {
		

		if(isfurtherprocess){
		String superadmin=msgmap.get(MapKeys.SUPERADMIN);
		String admin=msgmap.get(MapKeys.ADMIN);
		String username=msgmap.get(MapKeys.USERNAME);

		String operator=msgmap.get(MapKeys.OPERATOR).toString();
		String circle=msgmap.get(MapKeys.CIRCLE).toString();

		for(int logic=1;logic<8;logic++) {
			
			String key=getKey(superadmin,admin,username,operator,circle,logic);
			
			String routegroup=Route.getInstance().getRouteGroup(key);
			
			if(routegroup!=null&&routegroup.trim().length()>0) {
				
				msgmap.put(MapKeys.ROUTEGROUP, routegroup.trim());
				
				msgmap.put(MapKeys.ROUTEKEY, key);
				
				msgmap.put(MapKeys.ROUTELOGIC, ""+logic);
				
				return this;
				
			}
		}
	
		isfurtherprocess=false;

		msgmap.put(MapKeys.STATUSID, ""+MessageStatus.ROUTEGROUP_NOT_FOUND);
	
		}
		
		
		return this;
	}

	public TraiProcessor doBilling(){
		
		if(isretrymsg){
		
			if(new QueueSender().sendL("billingpool", msgmap, false, logmap)){
				
				logmap.put("sms processor status", "Message Sent to billing Queue Successfully");
			}else{
				
				logmap.put("sms processor status", "Message Sent to billing Queue Failed message will be loss");

			}
		}
		
		
		return this;
	}
	private String getKey(String superadmin,String admin,String username, String operator, String circle, int logic) {

		switch(logic) {
		
		case 1:
			 return CONJUNCTION+superadmin+CONJUNCTION+admin+CONJUNCTION+username+CONJUNCTION+operator+CONJUNCTION+circle+CONJUNCTION;
		case 2:
			 return CONJUNCTION+superadmin+CONJUNCTION+admin+CONJUNCTION+username+CONJUNCTION+operator+CONJUNCTION+""+CONJUNCTION;
		case 3:
			 return CONJUNCTION+superadmin+CONJUNCTION+admin+CONJUNCTION+username+CONJUNCTION+""+CONJUNCTION+circle+CONJUNCTION;
		case 4:
			 return CONJUNCTION+superadmin+CONJUNCTION+admin+CONJUNCTION+username+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION;
		case 5:
			 return CONJUNCTION+superadmin+CONJUNCTION+admin+CONJUNCTION+""+CONJUNCTION+operator+CONJUNCTION+circle+CONJUNCTION;
		case 6:
			 return CONJUNCTION+superadmin+CONJUNCTION+admin+CONJUNCTION+""+CONJUNCTION+operator+CONJUNCTION+""+CONJUNCTION;
		case 7:
			 return CONJUNCTION+superadmin+CONJUNCTION+admin+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+circle+CONJUNCTION;
		case 8:
			 return CONJUNCTION+superadmin+CONJUNCTION+admin+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION;
		case 9:
			 return CONJUNCTION+superadmin+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+operator+CONJUNCTION+circle+CONJUNCTION;
		case 10:
			 return CONJUNCTION+superadmin+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+operator+CONJUNCTION+""+CONJUNCTION;
		case 11:
			 return CONJUNCTION+superadmin+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+circle+CONJUNCTION;
		case 12:
			 return CONJUNCTION+superadmin+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION;
		case 13:
			 return CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+operator+CONJUNCTION+circle+CONJUNCTION;
		case 14:
			 return CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+operator+CONJUNCTION+""+CONJUNCTION;
		case 15:
			 return CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+circle+CONJUNCTION;
		case 16:
			 return CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION+""+CONJUNCTION;
		
				 
		
		}
		
		return "";
	}

}

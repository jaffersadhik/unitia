package com.winnovature.unitia.util.processor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.winnovature.unitia.util.datacache.account.PushAccount;
import com.winnovature.unitia.util.dnd.DNDProcessoer;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.RoundRobinTon;
import com.winnovature.unitia.util.mobileblacklist.MobileBlackList;
import com.winnovature.unitia.util.optin.OptinProcessor;
import com.winnovature.unitia.util.optout.OptoutProcessor;
import com.winnovature.unitia.util.redis.QueueSender;
import com.winnovature.unitia.util.routing.Kannel;
import com.winnovature.unitia.util.routing.NumberingPlan;
import com.winnovature.unitia.util.routing.Route;
import com.winnovature.unitia.util.routing.RouteGroup;
import com.winnovature.unitia.util.routing.SplitGroup;
import com.winnovature.unitia.util.senderidblacklist.SenderidBlackList;
import com.winnovature.unitia.util.smspatternallowed.SMSPatternAllowed;
import com.winnovature.unitia.util.smspatternblacklist.SMSPatternBlackList;
import com.winnovature.unitia.util.smspatternfiltering.SMSPatternFiltering;

public class SMSProcessor {
	
	private static String CONJUNCTION="~";

	Map<String,String> msgmap=null;
	
	Map<String,String> logmap=null;
	
	private boolean isfurtherprocess=true;

	private boolean isretrymsg=false;
	
	public SMSProcessor(Map<String,String> msgmap,Map<String,String> logmap){
		
		this.logmap=logmap;
		this.msgmap=msgmap;
	}
	
	public SMSProcessor doNumberingPlan(){
		
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
	
	
	
	public SMSProcessor doBlackListSenderid(){
	
		
		if(isfurtherprocess){
		
			String senderid=msgmap.get(MapKeys.SENDERID);
			
			if(SenderidBlackList.getInstance().isBalckList(senderid)){
			
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.BLACKLIST_SENDERID);

				isfurtherprocess=false;
		
			}
		}
		
		return this;
	}

	
public SMSProcessor doDNDCheck(){
		

	
		
		if(isfurtherprocess){
		
			String routeclass=msgmap.get(MapKeys.ROUTE_CLASS);
			if(routeclass.equals("2")){
			if(new DNDProcessoer().isDND(msgmap.get(MapKeys.MOBILE))){
				
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.DND_REJECTED);

				
				isfurtherprocess=false;
			}
			}
		}

		return this;
	
		
	}

	
	
	public SMSProcessor doFilteringSMSPatternCheck(){
		

	
		
		if(isfurtherprocess){
		
			Set<String> patternset=SMSPatternFiltering.getInstance().getFilteringPaternSet(msgmap.get(MapKeys.USERNAME));
			
			Iterator itr= patternset.iterator();
			
			while(itr.hasNext()){
			
				String spamPattern=itr.next().toString();
				
				if(Pattern.compile(spamPattern, Pattern.CASE_INSENSITIVE).matcher(msgmap.get(MapKeys.MESSAGE)).matches())
				{
					msgmap.put(MapKeys.FILTERING_PATTERN_ID, SMSPatternFiltering.getInstance().getPatternId(spamPattern));
					msgmap.put(MapKeys.STATUSID, ""+MessageStatus.FILTERING_SMS_PATTERN);

					isfurtherprocess=false;
					return this;
				}
			}
			
		}
		msgmap.put(MapKeys.ROUTE_CLASS, "2");

		return this;
	
		
	}
	
	
	public SMSProcessor doAllowedSMSPatternCheck(){
		

	
		
		if(isfurtherprocess){
		
			Set<String> patternset=SMSPatternAllowed.getInstance().getAllowedPaternSet(msgmap.get(MapKeys.USERNAME));
			
			Iterator itr= patternset.iterator();
			
			while(itr.hasNext()){
			
				String spamPattern=itr.next().toString();
				
				if(Pattern.compile(spamPattern, Pattern.CASE_INSENSITIVE).matcher(msgmap.get(MapKeys.MESSAGE)).matches())
				{
					msgmap.put(MapKeys.ROUTE_CLASS, "1");
					msgmap.put(MapKeys.ALLOWED_PATTERN_ID, SMSPatternAllowed.getInstance().getPatternId(spamPattern));

					return this;
				}
			}
			
		}
		msgmap.put(MapKeys.ROUTE_CLASS, "2");

		return this;
	
		
	}
	
	public SMSProcessor doBlackListSMSPattern(){
	
		
		if(isfurtherprocess){
		
			Set<String> patternset=SMSPatternBlackList.getInstance().getBlacklistPaternSet();
			
			Iterator itr= patternset.iterator();
			
			while(itr.hasNext()){
			
				String spamPattern=itr.next().toString();
				
				if(Pattern.compile(spamPattern, Pattern.CASE_INSENSITIVE).matcher(msgmap.get(MapKeys.MESSAGE)).matches())
				{
					msgmap.put(MapKeys.STATUSID, ""+MessageStatus.BLACKLIST_SMS_PATTERN);

					isfurtherprocess=false;
					
					return this;
				}
			}
			
		}
		
		return this;
	}


	public SMSProcessor doBlackListMobileNumber(){
	
		
		if(isfurtherprocess){
		
			String mobile=msgmap.get(MapKeys.MOBILE);
			
			if(MobileBlackList.getInstance().isBalckList(mobile)){
			
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.BLACKLIST_MOBILE);

				isfurtherprocess=false;
		
			}
		}
		
		return this;
	}
	
	public SMSProcessor doOptin(){
		
		if(isfurtherprocess){
			
			String username=msgmap.get(MapKeys.USERNAME);
			String userid=msgmap.get(MapKeys.USERID);
			String mobile=msgmap.get(MapKeys.MOBILE);
			
			if(PushAccount.instance().getPushAccount(username).get(MapKeys.OPTIN_TYPE).equals("1")){
			if(new OptinProcessor().isOptin(userid, mobile)){
				
				return this;
			}else{
				
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.MOBILE_NOT_REGISTERED_OPTIN);

				isfurtherprocess=false;
			}
			
			}
		}
		
		return this;
	}
	
	public SMSProcessor doOptout(){
		
		if(isfurtherprocess){
			String username=msgmap.get(MapKeys.USERNAME);
			String userid=msgmap.get(MapKeys.USERID);
			String mobile=msgmap.get(MapKeys.MOBILE);
			
			if(PushAccount.instance().getPushAccount(username).get(MapKeys.OPTIN_TYPE).equals("2")){
				if(new OptoutProcessor().isOptout(userid, mobile)){

				isfurtherprocess=false;
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.OPTOUT_MOBILE_NUMBER);

				return this;
				}
			
			}
		}
		
		return this;
	}
	public SMSProcessor doSMSCIDAvailable() {
		
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

	public SMSProcessor doKannelAvailable() {
		
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

	
	public SMSProcessor doSplitGroupAvilable() {

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


	
	
	public SMSProcessor doRouteGroupAvailable() {
		

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

	public SMSProcessor doBilling(){
		
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

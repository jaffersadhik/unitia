package com.winnovature.unitia.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.dnd.DNDProcessoer;
import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.misc.FeatureCode;
import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.MessageType;
import com.winnovature.unitia.util.misc.RoundRobinTon;
import com.winnovature.unitia.util.misc.SpecialCharacters;
import com.winnovature.unitia.util.misc.ToJsonString;
import com.winnovature.unitia.util.optin.OptinProcessor;
import com.winnovature.unitia.util.optout.OptoutProcessor;
import com.winnovature.unitia.util.redis.QueueSender;

public class RouteProcessor {
	
	Map<String,String> msgmap=null;
	
	private boolean isfurtherprocess=true;
	
	public RouteProcessor(Map<String,String> msgmap){
		
		this.msgmap=msgmap;
	}
	
	
	public void doCountryCodeCheck(){
		
		if(isfurtherprocess){
		
			String mobile=msgmap.get(MapKeys.MOBILE);

			if(mobile.startsWith("91")&&mobile.length()==12){
				
				msgmap.put(MapKeys.COUNTRYCODE, "91");
				
				String countryname=Countrycode.getInstance().getCountryName("91");

				msgmap.put(MapKeys.COUNTRYNAME, countryname);

				return ;
			}
			
		
			for(int i=7;i>0;i--) {
				
				String series =mobile.substring(0, i);

				String countryname=Countrycode.getInstance().getCountryName(series);
				
				if(countryname!=null){
					
					msgmap.put(MapKeys.COUNTRYCODE, series)	;
					msgmap.put(MapKeys.COUNTRYNAME, countryname);
					
					return ;
				}
			}
			
			isfurtherprocess=false;
			
			msgmap.put(MapKeys.STATUSID,""+MessageStatus.INVALID_COUNTRYCODE);

		}
		
		return ;
	}
	public void doNumberingPlan(){
		
		if(isfurtherprocess){
		
			String mobile=msgmap.get(MapKeys.MOBILE);

			if(!mobile.startsWith("91")){
				
				return ;
			}
			for(int i=7;i>5;i--) {
				
				String series =mobile.substring(2, i);
				
				Map<String,String> npinfo=NumberingPlan.getInstance().getNPInfo(series);
				
				if(npinfo!=null) {
					
					msgmap.put(MapKeys.OPERATOR, npinfo.get(MapKeys.OPERATOR));
					msgmap.put(MapKeys.CIRCLE, npinfo.get(MapKeys.CIRCLE));
		            msgmap.put(MapKeys.OPERATOR_NAME, NumberingPlan.getInstance().getOperatorName(npinfo.get(MapKeys.OPERATOR)));
		            msgmap.put(MapKeys.CIRCLE_NAME, NumberingPlan.getInstance().getCircleName(npinfo.get(MapKeys.CIRCLE)));
		
					
					return ;
				}
			}
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.MOBILE_SERIES_NOT_REGISTERED_NP);

			isfurtherprocess=false;
		
		}
		
		return ;
	}
	
	
	
	public void doBlackListSenderid(){
	
		
		if(isfurtherprocess){
		
			String senderid=msgmap.get(MapKeys.SENDERID);
			
			if(SenderidBlackList.getInstance().isBalckList(senderid)){
			
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.BLACKLIST_SENDERID);

				isfurtherprocess=false;
		
			}
		}
		
		return ;
	}

	
	public void doFilteringSMSPatternCheck(){
		

	
		
		if(isfurtherprocess){
		
			Set<String> patternset=SMSPatternFiltering.getInstance().getFilteringPaternSet(msgmap.get(MapKeys.USERNAME));
			if(patternset !=null){
			Iterator itr= patternset.iterator();
			
			while(itr.hasNext()){
			
				String spamPattern=itr.next().toString();
				
				if(Pattern.compile(spamPattern, Pattern.CASE_INSENSITIVE).matcher(msgmap.get(MapKeys.MESSAGE)).matches())
				{
					msgmap.put(MapKeys.FILTERING_PATTERN_ID, SMSPatternFiltering.getInstance().getPatternId(spamPattern));
					msgmap.put(MapKeys.STATUSID, ""+MessageStatus.FILTERING_SMS_PATTERN);

					isfurtherprocess=false;
					return ;
				}
			}
			}
		}
		return ;
	
		
	}
	
	
	public void doAllowedSMSPatternCheck(){
		

	
		
		if(isfurtherprocess){
		
			Set<String> patternset=SMSPatternAllowed.getInstance().getAllowedPaternSet(msgmap.get(MapKeys.USERNAME));
			
			if(patternset!=null){
			Iterator itr= patternset.iterator();
			
			while(itr.hasNext()){
			
				String spamPattern=itr.next().toString();
				
				if(Pattern.compile(spamPattern, Pattern.CASE_INSENSITIVE).matcher(msgmap.get(MapKeys.MESSAGE)).matches())
				{
					msgmap.put(MapKeys.ROUTECLASS, "1");
					msgmap.put(MapKeys.ALLOWED_PATTERN_ID, SMSPatternAllowed.getInstance().getPatternId(spamPattern));

					return ;
				}
			}
			
			}
		}
		msgmap.put(MapKeys.ROUTECLASS, "2");

		return ;
	
		
	}
	
	public void doSenderCheck(){
		
		if(isfurtherprocess){
		
			String senderidtype=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME)).get(MapKeys.SENDERID_TYPE);
		
			String senderid=msgmap.get(MapKeys.SENDERID_ORG);

			if(senderidtype.equals("multiple")){
				
				if(senderid!=null){
					
					if(WhiteListedSenderid.getInstance().isWhiteListedSenderid(msgmap.get(MapKeys.USERNAME), senderid)){
					
						msgmap.put(MapKeys.STATUSID, ""+MessageStatus.SENDER_NOT_WHITELISTED);
					
						isfurtherprocess=false;
					}
			
				}
			}
			
			if(senderid==null){
			
				if(msgmap.get(MapKeys.ROUTECLASS).equals("1")){
					
					msgmap.put(MapKeys.SENDERID_ORG, PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME)).get(MapKeys.SENDERID_TRANS));
				}else{
					
					msgmap.put(MapKeys.SENDERID_ORG, PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME)).get(MapKeys.SENDERID_PROMO));

				}
			}
		}

		return ;
	}
	
	public void doBlackListSMSPattern(){
	
		
		if(isfurtherprocess){
		
			Set<String> patternset=SMSPatternBlackList.getInstance().getBlacklistPaternSet();
			
			Iterator itr= patternset.iterator();
			
			while(itr.hasNext()){
			
				String spamPattern=itr.next().toString();
				
				if(Pattern.compile(spamPattern, Pattern.CASE_INSENSITIVE).matcher(msgmap.get(MapKeys.MESSAGE)).matches())
				{
					msgmap.put(MapKeys.STATUSID, ""+MessageStatus.BLACKLIST_SMS_PATTERN);

					isfurtherprocess=false;
					
					return ;
				}
			}
			
		}
		
		return ;
	}


	public void doBlackListMobileNumber(){
	
		
		if(isfurtherprocess){
		
			String mobile=msgmap.get(MapKeys.MOBILE);
			
			if(MobileBlackList.getInstance().isBalckList(mobile)){
			
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.BLACKLIST_MOBILE);

				isfurtherprocess=false;
		
			}
		}
		
		return ;
	}
	
	public void doSMSCIDAvailable() {
	
		

		if(isfurtherprocess){
	
		String routegroup=msgmap.get(MapKeys.ROUTEGROUP).toString();
		List<String> smscidlist=RouteGroup.getInstance().getSmscidList(routegroup);
		if(smscidlist!=null&&smscidlist.size()>0) {
			
			
			String key=msgmap.get(MapKeys.ROUTEKEY).toString();

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
		
		}

		isfurtherprocess =false;
	
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_ROUTE_GROUP);
	}


	private String getPointer(Map<String,String> msgmap,int routelistsize){
		
	
		String udh=msgmap.get(MapKeys.UDH);
		
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
	public void doKannelAvailable() {
		
	
		if(isfurtherprocess){
			
		String route=msgmap.get(MapKeys.SMSCID);
		msgmap.put(MapKeys.SMSCID_ORG, route);	
		Map<String,String> kannelinfo=Kannel.getInstance().getKannelInfo(route);
		
		if(kannelinfo!=null) {
			msgmap.put(MapKeys.KANNEL_IP, kannelinfo.get(MapKeys.KANNEL_IP));
			msgmap.put(MapKeys.KANNEL_PORT, kannelinfo.get(MapKeys.KANNEL_PORT));
			msgmap.put(MapKeys.ROUTECLASS_ORG, kannelinfo.get(MapKeys.ROUTECLASS));
        	
			return;
		}
		
		}
		msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_SMSCID);
		isfurtherprocess=false;
		
	}


	public void doRouteGroupAvailable() {
		

		if(isfurtherprocess){
		String superadmin=msgmap.get(MapKeys.SUPERADMIN);
		String admin=msgmap.get(MapKeys.ADMIN);
		String username=msgmap.get(MapKeys.USERNAME);

		String operator=msgmap.get(MapKeys.OPERATOR).toString();
		String circle=msgmap.get(MapKeys.CIRCLE).toString();

		for(int logic=1;logic<17;logic++) {
			
			String key=getKey(superadmin,admin,username,operator,circle,logic);
			
			String routegroup=Route.getInstance().getRouteGroup(key,msgmap.get(MapKeys.ROUTECLASS));
			
			if(routegroup!=null&&routegroup.trim().length()>0) {
				
				msgmap.put(MapKeys.ROUTEGROUP, routegroup.trim());
				
				msgmap.put(MapKeys.ROUTEKEY, key);
				
				msgmap.put(MapKeys.ROUTELOGIC, ""+logic);
				
				return ;
				
			}else{
				
				msgmap.put(MapKeys.ROUTEKEY, key);

			}
			
		}
	
		isfurtherprocess=false;

		msgmap.put(MapKeys.STATUSID, ""+MessageStatus.ROUTEGROUP_NOT_FOUND);
	
		}
		
		
		return ;
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

	
	public boolean isIsfurtherprocess() {
		return isfurtherprocess;
	}


	public String toString(){
		
		return ToJsonString.toString(msgmap);
	}
	}

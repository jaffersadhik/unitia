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


	public void doCountryCodeCheck() throws Exception{
		
		if(isfurtherprocess){
		
			String mobile=msgmap.get(MapKeys.MOBILE).toString();

			if(mobile.startsWith("91")&&mobile.length()==12){
				
				msgmap.put(MapKeys.COUNTRYCODE, "91");
				
				String countryname=Countrycode.getInstance().getCountryName("91");

				msgmap.put(MapKeys.COUNTRYNAME, countryname);

				return ;
			}
			
		
			if(mobile.trim().length()<7){
		
				isfurtherprocess=false;
				
				msgmap.put(MapKeys.STATUSID,""+MessageStatus.INVALID_DESTINATION_ADDRESS);

				return;
			}
			
			for(int i=7;i>0;i--) {
				
				String series =mobile.substring(0, i);

				String countryname=Countrycode.getInstance().getCountryName(series);
				
				if(countryname!=null){
					
					msgmap.put(MapKeys.COUNTRYCODE, series)	;
					msgmap.put(MapKeys.COUNTRYNAME, countryname);
					msgmap.put(MapKeys.INTL, PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.INTL).toString());

					if(!series.startsWith("91")){
						
						if(msgmap.get(MapKeys.INTL).toString().equals("0")){
							
							isfurtherprocess=false;
							
							msgmap.put(MapKeys.STATUSID,""+MessageStatus.INTL_DELIVERY_DISABLED);
						}
					}
					return ;
				}
			}
			
			isfurtherprocess=false;
			
			msgmap.put(MapKeys.STATUSID,""+MessageStatus.INVALID_COUNTRYCODE);

		}
		
		return ;
	}
	public void doNumberingPlan() throws Exception{
		
		if(isfurtherprocess){
		
			String mobile=msgmap.get(MapKeys.MOBILE).toString();

			if(!mobile.startsWith("91")){
				
				return ;
			}
			if(mobile.trim().length()==12){
				
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
			}
			
			if(mobile.trim().length()==12){
				
				msgmap.put(MapKeys.OPERATOR, " ");
				msgmap.put(MapKeys.CIRCLE, " ");
	            msgmap.put(MapKeys.OPERATOR_NAME," ");
	            msgmap.put(MapKeys.CIRCLE_NAME, " ");
	
				return;
			}
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.MOBILE_SERIES_NOT_REGISTERED_NP);

			isfurtherprocess=false;
		
		}
		
		return ;
	}
	
	
	
	public void doBlackListSenderid() throws Exception{
	
		
		if(isfurtherprocess){
		
			String senderid=msgmap.get(MapKeys.SENDERID)==null?"":msgmap.get(MapKeys.SENDERID).toString();
			
			if(SenderidBlackList.getInstance().isBalckList(senderid)){
			
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.BLACKLIST_SENDERID);

				isfurtherprocess=false;
		
			}
		}
		
		return ;
	}

	
	public void doFilteringSMSPatternCheck() throws Exception {
		

	
		
		if(isfurtherprocess){
		
			Set<String> patternset=SMSPatternFiltering.getInstance().getFilteringPaternSet(msgmap.get(MapKeys.USERNAME).toString());
			if(patternset !=null){
			Iterator itr= patternset.iterator();
			
			while(itr.hasNext()){
			
				String spamPattern=itr.next().toString();
				
				if(Pattern.compile(spamPattern, Pattern.CASE_INSENSITIVE).matcher(msgmap.get(MapKeys.FULLMSG).toString()).matches())
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
	
	
	public void doAllowedSMSPatternCheck() throws Exception {
		

		String msgclass=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.MSGCLASS);

		new RouterLog().routerlog(redisid, tname, "msgmap : "+msgmap.toString());

		new RouterLog().routerlog(redisid, tname, "msgclass : "+msgclass);
	
		
		if(isfurtherprocess){

			String templateid=(String)msgmap.get(MapKeys.TEMPLATEID);

			if(msgclass!=null&&msgclass.equals("3")){
				
				msgmap.put(MapKeys.ROUTECLASS, "1");
				
				if(templateid!=null&&templateid.trim().length()>0){
					
					msgmap.put(MapKeys.DLT_TYPE, "customer");
					msgmap.put(MapKeys.ROUTECLASS, "1");

				}
				return ;
				
			}

			
			new RouterLog().routerlog(redisid, tname, "templateid : "+templateid);

			if(templateid!=null&&templateid.trim().length()>0){
				
				msgmap.put(MapKeys.DLT_TYPE, "customer");
				msgmap.put(MapKeys.ROUTECLASS, "1");

				return ;
			}
			
			String fullmsg=(String)msgmap.get(MapKeys.FULLMSG);
			
			new RouterLog().routerlog(redisid, tname, "fullmsg : "+fullmsg);

			try{
				if(MessageType.isHexa( (String)msgmap.get(MapKeys.MSGTYPE))){
					
					fullmsg=Convertor.getMessage(fullmsg);
				}
			}catch(Exception e){
				
			}
			List<Map<String,String>>  patternset=SMSPatternAllowed.getInstance().getAllowedPaternSet(msgmap.get(MapKeys.USERNAME).toString());
			
			new RouterLog().routerlog(redisid, tname, "patternset : "+patternset);

			if(patternset!=null){
			
			for(int i=0,max=patternset.size();i<max;i++){
			
				Map<String,String> data=patternset.get(i);
				String spamPattern=data.get("smspattern");
			
				new RouterLog().routerlog(redisid, tname, "smspattern : "+spamPattern);

				
					boolean status=Pattern.compile(spamPattern, Pattern.CASE_INSENSITIVE).matcher(fullmsg).matches();
				if(status)
				{
					msgmap.put(MapKeys.ROUTECLASS, "1");
					msgmap.put(MapKeys.ALLOWED_PATTERN_ID, data.get("pattern_id"));
					msgmap.put(MapKeys.TEMPLATEID, data.get("pattern_id"));
					msgmap.put(MapKeys.DLT_TYPE, "unitia");
					new RouterLog().routerlog(redisid, tname, "patternset : success");

					return ;
				}
				
				
			}
			
			}
		}
		

		String promorejectyn=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.PROMO_REJECT_YN);
		new RouterLog().routerlog(redisid, tname, "promorejectyn : "+promorejectyn);

		
		if(msgclass!=null&&msgclass.equals("1")&&promorejectyn!=null&&promorejectyn.equals("1")){
			
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.PROMO_DELIVERY_DISBALED);
			
			isfurtherprocess=false;
			
		}
		msgmap.put(MapKeys.ROUTECLASS, "2");

		return ;
	
		
	}
	
	public void doSenderCheck() throws Exception{
		
		if(isfurtherprocess){
		
			String senderidtype=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SENDERID_TYPE);
		
			String senderid=msgmap.get(MapKeys.SENDERID_ORG)==null?"":msgmap.get(MapKeys.SENDERID_ORG).toString();

			if(senderidtype.equals("multiple")){
				
				if(senderid!=null&&senderid.length()>1){
					
					if(!WhiteListedSenderid.getInstance().isWhiteListedSenderid(msgmap.get(MapKeys.USERNAME).toString(), senderid)){
					
						setDefaultSenderID();
						
					}else{
						
						msgmap.put(MapKeys.SENDERID, senderid);
					}
			
				}else{
				
					setDefaultSenderID();
				}
			}else if(senderidtype.equals("static")){
				
				setDefaultSenderID();
			}else{
				
				if(senderid!=null&&senderid.length()<1){

					setDefaultSenderID();
				}else{
					
					msgmap.put(MapKeys.SENDERID, senderid);

				}
			}
			
			senderid =msgmap.get(MapKeys.SENDERID).toString();
			
			if(msgmap.get(MapKeys.ROUTECLASS).toString().equals("2")){
				Pattern pattern = Pattern.compile(".*[^0-9].*");

				if(senderid==null ||senderid.trim().length()<1 ||  !pattern.matcher(senderid).matches()){
					senderid= PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SENDERID_PROMO);
				}
			}
			
			
			String senderidmask=null;
			
			if(msgmap.get(MapKeys.COUNTRYCODE).toString().equals("91")){
				
				for(int i=1;i<5;i++){
					String key=getKey(msgmap.get(MapKeys.OPERATOR).toString(),msgmap.get(MapKeys.CIRCLE).toString(),i);	
					senderidmask=SenderidSwapping.getInstance().getSwapingSenderid(key, senderid);
				
					if(senderidmask!=null){
						
						break;
					}
					
					}
				
			}else{
			
				senderidmask=InternationalSenderidSwapping.getInstance().getSwapingSenderid(msgmap.get(MapKeys.COUNTRYCODE).toString());
				
			}
			
			
			if(senderidmask==null||senderidmask.trim().length()<1){
				
				senderidmask=senderid;
			}
			if(msgmap.get(MapKeys.SENDERID_ORG)==null||msgmap.get(MapKeys.SENDERID_ORG).toString().trim().length()<1){
				
				msgmap.put(MapKeys.SENDERID_ORG, senderidmask);

			}
			msgmap.put(MapKeys.SENDERID, senderidmask);
		}

		return ;
	}
	
	private void setDefaultSenderID() {
		
		String senderid=null;
		if(msgmap.get(MapKeys.ROUTECLASS).toString().equals("1")){
			senderid= PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SENDERID_TRANS);
	}else{

			senderid= PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SENDERID_PROMO);
	}
		msgmap.put(MapKeys.SENDERID, senderid);
	}


	private String getKey(String operator, String circle, int logic) {
		switch(logic) {
		
		case 1:
			 return Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 2:
			 return Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
		case 3:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
		case 4:
			 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
				
		}
		
		return "";
	}


	public void doBlackListSMSPattern() throws Exception {
	
		
		if(isfurtherprocess){
		
			Set<String> patternset=SMSPatternBlackList.getInstance().getBlacklistPaternSet();
			
			Iterator itr= patternset.iterator();
			
			while(itr.hasNext()){
			
				String spamPattern=itr.next().toString();
				
				if(Pattern.compile(spamPattern, Pattern.CASE_INSENSITIVE).matcher(msgmap.get(MapKeys.FULLMSG).toString()).matches())
				{
					msgmap.put(MapKeys.STATUSID, ""+MessageStatus.BLACKLIST_SMS_PATTERN);

					isfurtherprocess=false;
					
					return ;
				}
			}
			
		}
		
		return ;
	}


	public void doBlackListMobileNumber() throws Exception{
	
		
		if(isfurtherprocess){
		
			String mobile=(String)msgmap.get(MapKeys.MOBILE);
			
			if(MobileBlackList.getInstance().isBalckList(mobile)){
			
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.BLACKLIST_MOBILE);

				isfurtherprocess=false;
		
			}
		}
		
		return ;
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
			
			String kannelid=kannelinfo.get(MapKeys.KANNELID);
			
			if(kannelid!=null&&kannelid.trim().length()>0){
				msgmap.put(MapKeys.KANNELID, kannelinfo.get(MapKeys.KANNELID));
	
			}
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
				
				routegroup=SenderidRouting.getInstance().getRouteGroup(key);
				
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

package processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.Convertor;
import com.winnovature.unitia.util.misc.FeatureCode;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.MessageType;
import com.winnovature.unitia.util.misc.SpecialCharacters;
import com.winnovature.unitia.util.redis.QueueSender;

import blacklist.MobileBlackList;
import blacklistsenderid.SenderidBlackList;
import blacklistsms.SMSPatternBlackList;
import dlt.Entity;
import numberingplan.NumberingPlan;
import routegroup.InternationalRoute;
import routegroup.MobileRouting;
import routegroup.Route;
import routegroup.SenderidRouting;
import senderidcheck.InternationalSenderidSwapping;
import senderidcheck.SenderidSwapping;
import senderidcheck.WhiteListedSenderid;
import spamfilter.SMSPatternFiltering;
import templatecheck.SMSPatternAllowed;


public class SMSProcessor {
	
	String logname=null;
	Map<String,Object> msgmap=null;
	
	private boolean isfurtherprocess=true;
	
	private boolean isDND=false;
	
	private boolean sendTODNDQueue=false;
	
	public SMSProcessor(){
		
	}
	public SMSProcessor(String logname,Map<String,Object> msgmap,boolean isfurtherprocess){
		
		this.logname=logname;
		this.msgmap=msgmap;
		this.isfurtherprocess=isfurtherprocess;
	}


	
	public void setDND(boolean isDND) {
		this.isDND = isDND;
	}
	public void doCountryCodeCheck() throws Exception{
		
		if(isfurtherprocess&&!isDND){
		
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
		
		if(isfurtherprocess&&!isDND){
		
			String mobile=msgmap.get(MapKeys.MOBILE).toString();

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
	


	public void doBlackListMobileNumber() throws Exception{
	
		
		if(isfurtherprocess&&!isDND){
		
			String mobile=(String)msgmap.get(MapKeys.MOBILE);
			
			if(mobile!=null&&mobile.trim().length()>10){
			
			if(MobileBlackList.getInstance().isBalckList(mobile)){
			
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.BLACKLIST_MOBILE);

				isfurtherprocess=false;
		
			}
			}
		}
		
		return ;
	}	
	


	public void doBlackListSMSPattern() throws Exception {
	
		
		if(isfurtherprocess&&!isDND){
		
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



	public void doFilteringSMSPatternCheck() throws Exception {
		

	
		
		if(isfurtherprocess&&!isDND){
		
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

		
		if(isfurtherprocess&&!isDND){

			String templateid=(String)msgmap.get(MapKeys.TEMPLATEID);

			if(msgclass!=null&&msgclass.equals("3")){
				
				msgmap.put(MapKeys.ROUTECLASS, "1");
				
				if(templateid!=null&&templateid.trim().length()>0){
					
					msgmap.put(MapKeys.DLT_TYPE, "customer");
					msgmap.put(MapKeys.ROUTECLASS, "1");

				}
				return ;
				
			}

			

			if(templateid!=null&&templateid.trim().length()>0){
				
				msgmap.put(MapKeys.DLT_TYPE, "customer");
				msgmap.put(MapKeys.ROUTECLASS, "1");

				return ;
			}
			
			String fullmsg=(String)msgmap.get(MapKeys.FULLMSG);
			

			try{
				if(MessageType.isHexa( (String)msgmap.get(MapKeys.MSGTYPE))){
					
					fullmsg=Convertor.getMessage(fullmsg);
				}
			}catch(Exception e){
				
			}
			List<Map<String,String>>  patternset=SMSPatternAllowed.getInstance().getAllowedPaternSet(msgmap.get(MapKeys.USERNAME).toString());
			

			if(patternset!=null){
			
			for(int i=0,max=patternset.size();i<max;i++){
			
				Map<String,String> data=patternset.get(i);
				String spamPattern=data.get("smspattern");
			

				
					boolean status=Pattern.compile(spamPattern, Pattern.CASE_INSENSITIVE).matcher(fullmsg).matches();
				if(status)
				{
					msgmap.put(MapKeys.ROUTECLASS, "1");
					msgmap.put(MapKeys.ALLOWED_PATTERN_ID, data.get("pattern_id"));
					msgmap.put(MapKeys.TEMPLATEID, data.get("pattern_id"));
					msgmap.put(MapKeys.DLT_TYPE, "unitia");

					return ;
				}
				
				
			}
			
			}
		}
		

		String promorejectyn=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.PROMO_REJECT_YN);

		isfurtherprocess=false;

		if(msgclass!=null&&msgclass.equals("1")&&promorejectyn!=null&&promorejectyn.equals("1")){
			
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.PROMO_DELIVERY_DISBALED);
			
			sendTODNDQueue=false;

		}else{
			
			sendTODNDQueue=true;

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


	


	public void doFeatureCodeIndentification()  throws Exception{

		if(isfurtherprocess){

			if(msgmap.get(MapKeys.FEATURECODE)==null){
				
				String msgtype=msgmap.get(MapKeys.MSGTYPE).toString();
				
				if(msgtype.equals(MessageType.EM)||msgtype.equals(MessageType.PEM)||msgtype.equals(MessageType.EF)){
					
					if(isSplitRequired()){
						
						if(msgtype.equals(MessageType.EM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.EMC);

						}else if(msgtype.equals(MessageType.PEM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.PEMC);

							
						}else if(msgtype.equals(MessageType.EF)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.EFC);

							
						}

					}else{
						
						if(msgtype.equals(MessageType.EM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.EMS);

						}else if(msgtype.equals(MessageType.PEM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.PEMS);

							
						}else if(msgtype.equals(MessageType.EF)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.EFS);

							
						}
					}
				}else{
			
					int messagelength=msgmap.get(MapKeys.FULLMSG).toString().length();
					
					if(messagelength>280){
						
						if(msgtype.equals(MessageType.UM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.UMC);
							
						}else if(msgtype.equals(MessageType.UF)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.UFC);

						}else if(msgtype.equals(MessageType.PUM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.PUMC);
						}else if(msgtype.equals(MessageType.BM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.BMC);
						}
					}else{
						
						if(msgtype.equals(MessageType.UM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.UMS);
							
						}else if(msgtype.equals(MessageType.UF)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.UFS);

						}else if(msgtype.equals(MessageType.PUM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.PUMS);
						}else if(msgtype.equals(MessageType.BM)){
							
							msgmap.put(MapKeys.FEATURECODE, FeatureCode.BMS);
						}
					}
				}
				
				if(msgmap.get(MapKeys.FEATURECODE)==null){
					
					msgmap.put(MapKeys.STATUSID, ""+MessageStatus.UNEBALE_TO_PREDICT_FEATURECODE);
				
					isfurtherprocess=false;
				}
			}
					
					
		}
		return ;
	}
	
	
	

	private boolean isSplitRequired()  throws Exception
	{
		int msgLength = msgmap.get(MapKeys.FULLMSG).toString().length();
		int noSplChar = 0;
		
		char strChar[] = msgmap.get(MapKeys.FULLMSG).toString().toCharArray();
		List splCharList = SpecialCharacters.instance().getSplCharacters();
		
		for(int i=0; i < strChar.length; i++)
		{
			if(splCharList.contains(""+strChar[i]))
			{
				noSplChar++;
			}
		}
		
	
		int totLength = (msgLength + noSplChar);
		
		
		if(totLength > 160)
			return true;
		else
			return false;
		
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




	private void gotosleep() {
		
		try{
			
			Thread.sleep(1000L);
			
		}catch(Exception e){
			
		}
		
	}

	public void sentToNextLevel() throws Exception {
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.putAll(msgmap);
		logmap.put("module", "SMSProcessor");
		logmap.put("logname", logname);
	
		if(isfurtherprocess){
			
		
				sendTOCommonPool("commonpool",logmap);
					
		}else{

			
		
			doBilling(logmap);
			

		}
		
		new FileWrite().write(logmap);

		
	}

	
	
	
	
		



	
	private void doBilling(Map<String,Object> logmap)  throws Exception {
	
		
			if(sendTODNDQueue){
				if(new QueueSender().sendL("dnd", msgmap, false, logmap)){
					
					logmap.put("sms processor status", "Message Sent to dnd Queue Successfully");
				
				}else{
					
					logmap.put("sms processor status", "Message Sent to dnd Queue Failed message will be loss");

				}
			}else{
			String queuename="submissionpool";
			
			String smscid="";
			
			if(msgmap.get(MapKeys.SMSCID)!=null){
				
				smscid=msgmap.get(MapKeys.SMSCID).toString();
			}
			
			if(smscid.equals("apps")||smscid.equals("reapps")){
				
				queuename="appspool";
			}
			if(new QueueSender().sendL(queuename, msgmap, false, logmap)){
				
				logmap.put("sms processor status", "Message Sent to billing Queue Successfully");
			
			}else{
				
				logmap.put("sms processor status", "Message Sent to billing Queue Failed message will be loss");

			}
		
			}
		
	}
	
	private void sendTOCommonPool(String queuename,Map<String,Object> logmap)  throws Exception{
		
	
		
		if(new QueueSender().sendL(queuename, msgmap, false, logmap)){
			
			logmap.put("sms processor status", "Message Sent to "+queuename+" Queue Successfully");
		
		}else{
			
			logmap.put("sms processor status", "Message Sent to "+queuename+" Queue Failed message will be loss");

		}
	
	
	
}

}

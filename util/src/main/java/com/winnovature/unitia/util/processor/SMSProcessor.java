package com.winnovature.unitia.util.processor;

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
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.MessageType;
import com.winnovature.unitia.util.misc.RoundRobinTon;
import com.winnovature.unitia.util.misc.SpecialCharacters;
import com.winnovature.unitia.util.mobileblacklist.MobileBlackList;
import com.winnovature.unitia.util.multiplesenderid.WhiteListedSenderid;
import com.winnovature.unitia.util.optin.OptinProcessor;
import com.winnovature.unitia.util.optout.OptoutProcessor;
import com.winnovature.unitia.util.redis.QueueSender;
import com.winnovature.unitia.util.routing.Countrycode;
import com.winnovature.unitia.util.routing.Kannel;
import com.winnovature.unitia.util.routing.NumberingPlan;
import com.winnovature.unitia.util.routing.Route;
import com.winnovature.unitia.util.routing.RouteGroup;
import com.winnovature.unitia.util.senderidblacklist.SenderidBlackList;
import com.winnovature.unitia.util.smspatternallowed.SMSPatternAllowed;
import com.winnovature.unitia.util.smspatternblacklist.SMSPatternBlackList;
import com.winnovature.unitia.util.smspatternfiltering.SMSPatternFiltering;

public class SMSProcessor {
	
	private static String SENT="Sent.";
	
	private static String KANNEL_URL="http://{0}:{1}/cgi-bin/sendsms?username=unitia&password=unitia&smsc={2}&from={3}&to={4}&text={5}&dlr-mask=27&dlr-url={6}";
	
	private static String APPS_URL="http://{0}:{1}/api/dngen?username=unitia&password=unitia&smsc={2}&from={3}&to={4}&text={5}&dlr-mask=27&dlr-url={6}";
	
	private static String RETRY_URL="http://{0}:{1}/retry/retry?username=unitia&password=unitia&smsc={2}&from={3}&to={4}&text={5}&dlr-mask=27&dlr-url={6}";

	private static String DLR_URL="http://{0}:{1}/api/dnreceiver?username={2}&senderidorg={3}&dnmsg={4}&ackid={5}&msgid={6}&mobile={7}&smscidorg={8}&rtime={9}&ktime={10}&carriersystemid={11}&carrierdr={12}&statuscd={13}&";
	
	private static String SYSTEMID="%o";
	
	private static String SMSCID="%i";
	
	private static String STATUSCD="%d";

	private static String DR="%a";
	
	private static int count =1;

	private static int _count =1;


	List<Map<String,String>> msgmaplist=null;
	
	Map<String,String> msgmap=null;
	
	Map<String,String> logmap=null;
	
	private boolean isfurtherprocess=true;
	
	public SMSProcessor(Map<String,String> msgmap,Map<String,String> logmap){
		
		this.logmap=logmap;
		this.msgmap=msgmap;
	}
	
	
	public SMSProcessor doCountryCodeCheck(){
		
		if(isfurtherprocess){
		
			String mobile=msgmap.get(MapKeys.MOBILE);

			if(mobile.startsWith("91")&&mobile.length()==12){
				
				msgmap.put(MapKeys.COUNTRYCODE, "91");
				
				String countryname=Countrycode.getInstance().getCountryName("91");

				msgmap.put(MapKeys.COUNTRYNAME, countryname);

				return this;
			}
			
		
			for(int i=7;i>0;i--) {
				
				String series =mobile.substring(0, i);

				String countryname=Countrycode.getInstance().getCountryName(series);
				
				if(countryname!=null){
					
					msgmap.put(MapKeys.COUNTRYCODE, series)	;
					msgmap.put(MapKeys.COUNTRYNAME, countryname);
					
					return this;
				}
			}
			
			isfurtherprocess=false;
			
			msgmap.put(MapKeys.STATUSID,""+MessageStatus.INVALID_COUNTRYCODE);

		}
		
		return this;
	}
	public SMSProcessor doNumberingPlan(){
		
		if(isfurtherprocess){
		
			String mobile=msgmap.get(MapKeys.MOBILE);

			if(!mobile.startsWith("91")){
				
				return this;
			}
			for(int i=7;i>5;i--) {
				
				String series =mobile.substring(2, i);
				
				Map<String,String> npinfo=NumberingPlan.getInstance().getNPInfo(series);
				
				if(npinfo!=null) {
					
					msgmap.put(MapKeys.OPERATOR, npinfo.get(MapKeys.OPERATOR));
					msgmap.put(MapKeys.CIRCLE, npinfo.get(MapKeys.CIRCLE));
		            msgmap.put(MapKeys.OPERATOR_NAME, NumberingPlan.getInstance().getOperatorName(npinfo.get(MapKeys.OPERATOR)));
		            msgmap.put(MapKeys.CIRCLE_NAME, NumberingPlan.getInstance().getCircleName(npinfo.get(MapKeys.CIRCLE)));
		
					
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
			if(patternset !=null){
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
		}
		return this;
	
		
	}
	
	
	public SMSProcessor doAllowedSMSPatternCheck(){
		

	
		
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

					return this;
				}
			}
			
			}
		}
		msgmap.put(MapKeys.ROUTECLASS, "2");

		return this;
	
		
	}
	
	public SMSProcessor doSenderCheck(){
		
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
	private void doSMSCIDAvailable(Map<String,String> msgmap) {
		
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
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_ROUTE_GROUP);
	}

	
	public void submitKannel(){
		
		if(isfurtherprocess){
			
		
		for(int i=0;i<msgmaplist.size();i++){
			
			Map<String,String> msgmap=msgmaplist.get(i);
			
			doSMSCIDAvailable(msgmap);
			
			if(msgmap.get(MapKeys.STATUSID)==null){
				
				doKannelAvailable(msgmap);
				
				if(msgmap.get(MapKeys.STATUSID)==null){
					
					doDNMessage(msgmap);
					
					setDLRURL(msgmap);
					
					setKannelURL(msgmap);
					
					if(connectKannel(msgmap.get(MapKeys.KANNEL_URL)).startsWith(SENT)){
						
						msgmap.put(MapKeys.STATUSID, ""+MessageStatus.KANNEL_SUBMIT_SUCCESS);
					
					}else{
						
						msgmap.put(MapKeys.STATUSID, ""+MessageStatus.KANNEL_SUBMIT_FAILED);

					}
					
					
				}
			}
			
		}
			
			if(msgmap.get(MapKeys.STATUSID).equals(""+MessageStatus.KANNEL_SUBMIT_FAILED)){
				
				doRetry(msgmap);
				
			}else{
				
				doBilling(msgmap);
			}
		}
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
	private void doKannelAvailable(Map<String,String> msgmap) {
		
		
		String route=msgmap.get(MapKeys.SMSCID);
		msgmap.put(MapKeys.SMSCID_ORG, route);	
		Map<String,String> kannelinfo=Kannel.getInstance().getKannelInfo(route);
		
		if(kannelinfo!=null) {
			msgmap.put(MapKeys.KANNEL_IP, kannelinfo.get(MapKeys.KANNEL_IP));
			msgmap.put(MapKeys.KANNEL_PORT, kannelinfo.get(MapKeys.KANNEL_PORT));
			msgmap.put(MapKeys.ROUTECLASS_ORG, kannelinfo.get(MapKeys.ROUTECLASS));
        	
			return;
		}
		msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_SMSCID);
		
		
	}

	
	public SMSProcessor doConcate(){
		
		if(isfurtherprocess){

			if(msgmap.get(MapKeys.UDH)==null){
				
				if(FeatureCode.isUDHMessage(msgmap.get(MapKeys.FEATURECODE))){
					
					List<String> splitMsgList=doSplit();
					
					msgmaplist=new ArrayList<Map<String,String>>();

					String msg_ref_no=getReferenceNumber();
					
					for(int i=0;i<splitMsgList.size();i++)
					{
						Map<String,String> cloneMsgMap = new HashMap(msgmap);
						
						int totalmsgcount=splitMsgList.size();
						
						cloneMsgMap.put(MapKeys.MSGID, ACKIdGenerator.getAckId());

						cloneMsgMap.put(MapKeys.TOTAL_MSG_COUNT,""+totalmsgcount);
						
						int splitseq=(i+1);
						cloneMsgMap.put(MapKeys.SPLIT_SEQ,""+splitseq);
					
						cloneMsgMap.put(MapKeys.MESSAGE,splitMsgList.get(i));
						
						cloneMsgMap.put(MapKeys.UDH,getUDH(msg_ref_no,totalmsgcount,splitseq));
						
						msgmaplist.add(cloneMsgMap);

					}
					
				}else{
			
					msgmaplist=new ArrayList<Map<String,String>>();
					
					msgmaplist.add(msgmap);
			
				}
				
			}else{
				
				msgmaplist=new ArrayList<Map<String,String>>();
				
				msgmaplist.add(msgmap);
			}
		}
		
		return this;
	}

	
	private String getUDH(String msg_ref_no, int totalmsgcount, int splitseq) {
		
		if(PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME)).get(MapKeys.UDHBIT).equals("16")){

			return getConcatMsg16BitUDH(msg_ref_no, totalmsgcount, splitseq);
			
		}else{
			
			return getConcatMsg8BitUDH(msg_ref_no, totalmsgcount, splitseq);
		}
	}


	private String getReferenceNumber() {
		
		if(PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME)).get(MapKeys.UDHBIT).equals("16")){

			return get16BitRunningHexaSequence();
			
		}else{
			
			return get8BitRunningHexaSequence();
		}
	}


	private List doSplit() 
	{
		String message =msgmap.get(MapKeys.FULLMSG);
		int splitLength=getSplitLength();
		List<String> splitMsgList = new ArrayList();
		List<String> splCharList = SpecialCharacters.instance().getSplCharacters();
		
		int length = 0;
		int startindex = 0;
		message = message.trim();
		char strChar[] = message.toCharArray();
		
		for(int i=0;i<strChar.length;i++)
		{
			//System.out.println("Char [" + i + "] - " + strChar[i]);
			
			if(length < splitLength && i == message.length()-1)
			{
				//System.out.println("inside");
				splitMsgList.add(message.substring(startindex));
				break;
			}
			
			if(splCharList.contains(""+strChar[i]))
			{
				length=length+2;
			}
			else
			{
				length=length+1;
			}
			
			if(length >= splitLength)
			{
				if(length == splitLength)
				{
					splitMsgList.add(message.substring(startindex, i+1));
					startindex = i+1;
					length = 0;
				}
				
				if(length > splitLength)
				{
					i = i - 1;
					splitMsgList.add(message.substring(startindex, i+1));
					startindex = i+1;
					length = 0;
				}
			}
		}
		
		return splitMsgList;
	}
	
	
	private int getSplitLength() {

		int splitlength=0;
		
		if(PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME)).get(MapKeys.UDHBIT).equals("16")){
			
			if(FeatureCode.isUnicodeConcate(msgmap.get(MapKeys.FEATURECODE))){
		
			splitlength=264;
			
			}else{
			
			splitlength=152;
			
			}
		}else{
		
			if(FeatureCode.isUnicodeConcate(msgmap.get(MapKeys.FEATURECODE))){
				
				splitlength=268;
				
			}else{
				
				splitlength=153;
				
			}
			
		}
		
		return splitlength;
	}


	public SMSProcessor doFeatureCodeIndentification(){

		if(isfurtherprocess){

			if(msgmap.get(MapKeys.FEATURECODE)==null){
				
				String msgtype=msgmap.get(MapKeys.MSGTYPE);
				
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
			
					int messagelength=msgmap.get(MapKeys.FULLMSG).length();
					
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
		return this;
	}
	
	private void doDNMessage(Map<String,String> msgmap){
		
		String msg=msgmap.get(MapKeys.FULLMSG);
		
		
		if (msgmap.get(MapKeys.ENGLISH_AS_HEX)!=null&&msgmap.get(MapKeys.ENGLISH_AS_HEX).toString().equals("true")) {
			msg = msg.length() > 40 ? msg.toString().substring(0, 40) : msg;
			msg =decodeHexString(msg);
		} else {
			msg = msg.length() > 20
					? msg.substring(0, 20) : msg;
		}
		
		msgmap.put(MapKeys.DNMSG, msg);
	}
	
	private String decodeHexString(String hexText) 
	{
		String decodedText=null;
		String chunk=null;

		try
		{
			
			if(hexText!=null && hexText.length()>0) {
				int numBytes = hexText.length()/2;

				byte[] rawToByte = new byte[numBytes];
				int offset=0;
				int bCounter=0;
				for(int i =0; i <numBytes; i++) {
					chunk = hexText.substring(offset,offset+2);
					offset+=2;
					rawToByte[i] = (byte) (Integer.parseInt(chunk,16) & 0x000000FF);
				}
				decodedText= new String(rawToByte);
			}
		}
		catch(Exception e){
			}
	
		return decodedText;
	}

	public SMSProcessor doRouteGroupAvailable() {
		

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
				
				return this;
				
			}else{
				
				msgmap.put(MapKeys.ROUTEKEY, key);

			}
			
		}
	
		isfurtherprocess=false;

		msgmap.put(MapKeys.STATUSID, ""+MessageStatus.ROUTEGROUP_NOT_FOUND);
	
		}
		
		
		return this;
	}

	private void doBilling(Map<String,String> msgmap){
		
	
			if(new QueueSender().sendL("billingpool", msgmap, false, logmap)){
				
				logmap.put("sms processor status", "Message Sent to billing Queue Successfully");
			
			}else{
				
				logmap.put("sms processor status", "Message Sent to billing Queue Failed message will be loss");

			}
		
		
		
	}
	
	private void doRetry(Map<String,String> msgmap){
		
		
		if(new QueueSender().sendL("kannelretrypool", msgmap, false, logmap)){
			
			logmap.put("sms processor status", "Message Sent to kannelretrypool Queue Successfully");
		
		}else{
			
			logmap.put("sms processor status", "Message Sent to kannelretrypool Queue Failed message will be loss");

		}
	
	
	
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

	private boolean isSplitRequired() 
	{
		int msgLength = msgmap.get(MapKeys.FULLMSG).length();
		int noSplChar = 0;
		
		char strChar[] = msgmap.get(MapKeys.FULLMSG).toCharArray();
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

	private synchronized  String get8BitRunningHexaSequence() 
	{

		if(count<255)
			count++;
		else
			count=1;
		
		return StringUtils.leftPad(Integer.toHexString(count),2,"0");
	}
	
	private synchronized  String get16BitRunningHexaSequence() 
	{

		if(_count<65535)
			_count++;
		else
			_count=1;
		
		return StringUtils.leftPad(Integer.toHexString(_count),4,"0");
	}
	
	private String getSpecificPortUDH(String port) throws Exception
	{
		return "060504" + StringUtils.leftPad(Integer.toHexString(Integer.parseInt(port)).toUpperCase(),4,"0") + "0000";
	}
	
	private String getConcatSpecificPortUDH(String port,String uniqueConcatId_,int totNoMsg, int nthNoMsg) throws Exception
	{
		return "0B0504" + StringUtils.leftPad(Integer.toHexString(Integer.parseInt(port)).toUpperCase(),4,"0") + "00000003"  +  uniqueConcatId_ +StringUtils.leftPad(Integer.toHexString(totNoMsg),2,"0")+StringUtils.leftPad(Integer.toHexString(nthNoMsg),2,"0");
	}
	
	private String getConcatMsg8BitUDH(String uniqueConcatId_,int totNoMsg, int nthNoMsg) 
	{		
		return "050003"+ uniqueConcatId_ +StringUtils.leftPad(Integer.toHexString(totNoMsg),2,"0")+StringUtils.leftPad(Integer.toHexString(nthNoMsg),2,"0");		
	}
	
	
	private String getConcatMsg16BitUDH(String uniqueConcatId_,int totNoMsg, int nthNoMsg) 
	{		
		return "060804"+ uniqueConcatId_ +StringUtils.leftPad(Integer.toHexString(totNoMsg),2,"0")+StringUtils.leftPad(Integer.toHexString(nthNoMsg),2,"0");		
	}

	
	private void setKannelURL(Map<String,String> msgmap){
		
		
		String msg=msgmap.get(MapKeys.MESSAGE);
		
		if ((msgmap.get(MapKeys.ENGLISH_AS_HEX)!=null && msgmap.get(MapKeys.ENGLISH_AS_HEX).toString().equals("true"))||FeatureCode.isHexa(msgmap.get(MapKeys.FEATURECODE))) {
			
			msg=addKannelSpecialCharactertoHex(msg);
		}
		
		
		String [] params=
			{
					URLEncoder.encode(msgmap.get(MapKeys.KANNEL_IP)),
					URLEncoder.encode(msgmap.get(MapKeys.KANNEL_PORT)),
					URLEncoder.encode(msgmap.get(MapKeys.SMSCID_ORG)),
					URLEncoder.encode(msgmap.get(MapKeys.SENDERID)),
					URLEncoder.encode(msgmap.get(MapKeys.MOBILE)),
					URLEncoder.encode(msg),
					URLEncoder.encode(msgmap.get(MapKeys.DLR_URL)),
			};
		
		String kannelurl="";
		
		if(msgmap.get(MapKeys.ROUTECLASS_ORG).equals("4")){
			
			kannelurl=MessageFormat.format(APPS_URL, params);

		}else if(msgmap.get(MapKeys.ROUTECLASS_ORG).equals("5")){
			
			kannelurl=MessageFormat.format(RETRY_URL, params);

		}else{
			
			kannelurl=MessageFormat.format(KANNEL_URL, params);
		}
				
		
		if(msgmap.get(MapKeys.UDH)!=null){
		
			kannelurl=kannelurl+"&udh="+addKannelSpecialCharactertoHex(msgmap.get(MapKeys.UDH));
		}
		
		String expiry=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME)).get(MapKeys.EXPIRY);
		int expirtInt=0;
		if(expiry!=null){
			expirtInt=Integer.parseInt(expiry);
		
		}
		if(expirtInt>0){
			
			kannelurl=kannelurl+"&validity="+expirtInt;
		}
	
		kannelurl=kannelurl+FeatureCode.getKannelUrlSuffix(msgmap.get(MapKeys.FEATURECODE));

		msgmap.put(MapKeys.KANNEL_URL, kannelurl);
		
	}
	
	
	private String addKannelSpecialCharactertoHex(String hexa) 
	{
		String kannelSpecCharacter="%";

		StringBuffer returnValues=new StringBuffer();
		
		for(int i=0;i<hexa.length();i=i+2)
		{
			returnValues.append(kannelSpecCharacter);
			returnValues.append(hexa.substring(i,i+2));
		}
		
		return returnValues.toString();
	}
	
	
	public String connectKannel(String sUrl) {

		String response = "";

		BufferedReader in = null;
		try {
			int httpConnectionTimeout = 2000;
			int httpResponseTimeout = 2000;
			URL url = new URL(sUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setConnectTimeout(httpConnectionTimeout);
			connection.setReadTimeout(httpResponseTimeout);

			int iGetResultCode = connection.getResponseCode();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer decodedString = new StringBuffer();
			String temp = null;

			while ((temp = in.readLine()) != null) {
				decodedString.append(temp);
			}

			if (iGetResultCode == 200 || iGetResultCode == 202) {

				if (decodedString.toString().length() != 0)
					response = decodedString.toString().trim();
				return response;
			} else {
			}

		} catch (Exception e) {
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}
		return response;
	}

	
	private void setDLRURL(Map<String,String> msgmap){
		
		msgmap.put(MapKeys.KTIME, ""+System.currentTimeMillis());
		
		String [] params=
		{
				URLEncoder.encode(ConfigParams.getInstance().getProperty(ConfigKey.LOADBALANCER_DN_IP)),
				URLEncoder.encode(ConfigParams.getInstance().getProperty(ConfigKey.LOADBALANCER_DN_PORT)),
				URLEncoder.encode(msgmap.get(MapKeys.USERNAME)),
				URLEncoder.encode(msgmap.get(MapKeys.SENDERID_ORG)),
				URLEncoder.encode(msgmap.get(MapKeys.DNMSG)),
				URLEncoder.encode(msgmap.get(MapKeys.ACKID)),
				URLEncoder.encode(msgmap.get(MapKeys.MSGID)),
				URLEncoder.encode(msgmap.get(MapKeys.MOBILE)),
				URLEncoder.encode(msgmap.get(MapKeys.SMSCID_ORG)),
				URLEncoder.encode(msgmap.get(MapKeys.RTIME)),
				URLEncoder.encode(msgmap.get(MapKeys.KTIME)),
				SYSTEMID,
				DR,
				STATUSCD				
		};
		
		String dlrurl=MessageFormat.format(DLR_URL, params);
		msgmap.put(MapKeys.DLR_URL, dlrurl);
		
		
	}
		
}

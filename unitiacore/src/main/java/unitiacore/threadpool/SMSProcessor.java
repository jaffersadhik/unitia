package unitiacore.threadpool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.db.DuplicateCheck;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.dnd.DNDProcessoer;
import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.misc.CreditProcessor;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FeatureCode;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.MessageType;
import com.winnovature.unitia.util.misc.SpecialCharacters;
import com.winnovature.unitia.util.optin.OptinProcessor;
import com.winnovature.unitia.util.optout.OptoutProcessor;
import com.winnovature.unitia.util.redis.OtpMessageDNRegister;
import com.winnovature.unitia.util.redis.QueueSender;

import unitiaroute.ReRouting;
import unitiaroute.RouteProcessor;

public class SMSProcessor {
	
	
	private static String SENT="Accepted for delivery";
	
	private static String KANNEL_URL="http://{0}:{1}/cgi-bin/sendsms?username={2}&password={3}&smsc={4}&from={5}&to={6}&text={7}&dlr-mask=23&dlr-url={8}";
	
	private static String APPS_URL="http://{0}:{1}/api/dngen?username={2}&password={3}&smsc={4}&from={5}&to={6}&text={7}&dlr-mask=23&dlr-url={8}";
	
	private static String RETRY_URL="http://{0}:{1}/retry/retry?username={2}&password={3}&smsc={4}&from={5}&to={6}&text={7}&dlr-mask=23&dlr-url={8}";

	private static String DLR_URL="http://{0}:{1}/api/dnreceiver?username={2}&senderidorg={3}&dnmsg={4}&ackid={5}&msgid={6}&mobile={7}&smscidorg={8}&rtime={9}&ktime={10}&carriersystemid={11}&carrierdr={12}&statuscd={13}&operator={14}&circle={15}&countrycode={16}&protocol={17}&msgtype={18}&featurecd={19}&fullmsg={20}&param1={21}&param2={22}&param3={23}&param4={24}&routeclass={25}&attempttype={26}&totalmsgcount={27}&splitseq={28}&credit={29}&templateid={30}&entityid={31}&dlttype={32}";
	
	private static String SYSTEMID="%o";
	
	private static String STATUSCD="%d";

	private static String DR="%a";
	
	private static int count =1;

	private static int _count =1;

	Map<String,Object> msgmap=null;
	
	private boolean isfurtherprocess=true;
	
	public SMSProcessor(){
		
	}
	public SMSProcessor(Map<String,Object> msgmap,boolean isfurtherprocess){
		
		this.msgmap=msgmap;
		this.isfurtherprocess=isfurtherprocess;
	}
	
	public void doDNDCheck() throws Exception{
		
		if(isfurtherprocess){
		
			String routeclass=msgmap.get(MapKeys.ROUTE_CLASS).toString();
			if(routeclass.equals("2")){
			if(new DNDProcessoer().isDND(msgmap.get(MapKeys.MOBILE).toString())){
				
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.DND_REJECTED);

				
				isfurtherprocess=false;
			}
			}
		}

		return ;
	
		
	}

		
	public void doOptin() throws Exception{
		
		if(isfurtherprocess){
			String username=msgmap.get(MapKeys.USERNAME).toString();
			String mobile=msgmap.get(MapKeys.MOBILE).toString();
			
			if(PushAccount.instance().getPushAccount(username).get(MapKeys.OPTIN_TYPE).equals("1")){
			if(new OptinProcessor().isOptin(username, mobile)){
				
				return ;
			}else{
				
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.MOBILE_NOT_REGISTERED_OPTIN);

				isfurtherprocess=false;
			}
			
			}
		}
		
		return ;
	}
	
	public void doOptout()  throws Exception{
		
		if(isfurtherprocess){
			String username=msgmap.get(MapKeys.USERNAME).toString();
			String mobile=msgmap.get(MapKeys.MOBILE).toString();
			
			if(PushAccount.instance().getPushAccount(username).get(MapKeys.OPTIN_TYPE).equals("2")){
				if(new OptoutProcessor().isOptout(username, mobile)){

				isfurtherprocess=false;
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.OPTOUT_MOBILE_NUMBER);

				return ;
				}
			
			}
		}
		
		return ;
	}
	
	
	public void submitKannel()  throws Exception{
		

		if(isfurtherprocess){
			
		List<Map<String,Object>> msgmaplist=(List<Map<String,Object>> )msgmap.get(MapKeys.MSGLIST);
		
		if(msgmaplist==null){
		
			dosingleSMS();
			
		}else{
			
			doMultiPartSMS();
		}
		
		}
			
		
	}
	
	
	private void doMultiPartSMS() throws Exception {

		
		if(hasCredit()){
			
			doSendMultiPart((List<Map<String,Object>> )msgmap.get(MapKeys.MSGLIST));
			
		}else{
			
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_CREDIT);

		}

		
	}
	private void doSendMultiPart(List<Map<String,Object>> msgmaplist) throws Exception {
		
		String statusid=null;
		
		for(int i=0;i<msgmaplist.size();i++){
		
			Map<String,Object> msgmap1=msgmaplist.get(i);
		
			setDLRURL(msgmap1);
			
			if(msgmap1.get(MapKeys.STATUSID)==null||msgmap1.get(MapKeys.STATUSID).toString().equals(MessageStatus.KANNEL_SUBMIT_FAILED)){
			
				connectKannel(msgmap1);
			
				if(msgmap1.get(MapKeys.STATUSID).toString().equals(MessageStatus.KANNEL_SUBMIT_FAILED)){

				statusid=msgmap1.get(MapKeys.STATUSID).toString();
				
				}
			
			}
		}
		
		if(statusid==null){
			
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.KANNEL_SUBMIT_SUCCESS);

		}else{
			
			msgmap.put(MapKeys.STATUSID, statusid);

		}
		
	}
	private void dosingleSMS() throws NumberFormatException, Exception {
		
		if(hasCredit()){
			
			setDLRURL();
			
			connectKannel();
			
			if(msgmap.get(MapKeys.STATUSID).toString().equals(MessageStatus.KANNEL_SUBMIT_FAILED)){
				
				new CreditProcessor().returnCredit(msgmap.get(MapKeys.USERNAME).toString(), Double.parseDouble(msgmap.get(MapKeys.CREDIT).toString()));

			}
		
		}else{
			
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_CREDIT);

		}

		
	}
	public void doRetryProcess(Map<String, Object> msgmap)  throws Exception{
		this.msgmap=msgmap;
		String attemptcount=(String)msgmap.get(MapKeys.ATTEMPT_COUNT);
		
		if(msgmap.get(MapKeys.STATUSID).equals(""+MessageStatus.INVALID_ROUTE_GROUP)){
			
						
			RouteProcessor route=new RouteProcessor(msgmap);
			route.setIsfurtherprocess(true);
			route.doRouteGroupAvailable();
			route.doSMSCIDAvailable();
			route.doKannelAvailable();
			
			SMSProcessor processor=new SMSProcessor(msgmap,route.isIsfurtherprocess());
			processor.doOptin();
			processor.doOptout();
			processor.doDuplicate();
			processor.doDNDCheck();
			processor.doFeatureCodeIndentification();
			processor.doDNMessage();
			processor.doConcate();
			processor.setCredit();
			processor.submitKannel();
			processor.sentToNextLevel();
	
		}else{
		int attempt=0;
		if(attemptcount!=null){
			
			attempt=Integer.parseInt(attemptcount);
		}
		
		attempt++;
		
		msgmap.put(MapKeys.ATTEMPT_COUNT, ""+attempt);
		
		if(attempt<8){
			
			long kannelpoptime=Long.parseLong(msgmap.get(MapKeys.KANNEL_POPTIME).toString());
			while(true){
				
				if((System.currentTimeMillis()-kannelpoptime)>(1*60*1000)){
					
					break;
				}
				
				gotosleep();
			}

			List<Map<String,Object>> msgmaplist=(List<Map<String,Object>>)msgmap.get(MapKeys.MSGLIST);
			if(msgmaplist==null){
				
				dosingleSMS();
				
			}else{
				
				doSendMultiPart(msgmaplist);
			}
			
		}else{
			
			msgmap.put(MapKeys.STATUSID,""+ MessageStatus.MAX_KANNEL_RETRY_EXCEEDED);
			
			if(msgmap.get(MapKeys.MSGLIST)!=null){
				
				new CreditProcessor().returnCredit(msgmap.get(MapKeys.USERNAME).toString(), Double.parseDouble(msgmap.get(MapKeys.CREDIT).toString()));

			}
			
		}
		
		sentToNextLevel();
		}

	}


	
	public void doOTPRetryProcess(Map<String, Object> msgmap)  throws Exception{
	
		this.msgmap=msgmap;
		long kannelpoptime=Long.parseLong(msgmap.get(MapKeys.OTPRETRY_POPUPTIME).toString());
		
		long otpdnwaittime=Long.parseLong(ConfigParams.getInstance().getProperty(ConfigKey.OTPDNWAITTIMEINMS));
		
		while(true){
				
				if((System.currentTimeMillis()-kannelpoptime)>otpdnwaittime){
					
					break;
				}
				
				gotosleep();
		}
			
		if(!new OtpMessageDNRegister().isRegister(msgmap.get(MapKeys.MSGID).toString())){
		
			msgmap.put(MapKeys.ATTEMPT_TYPE, "1");

			msgmap.put(MapKeys.CREDIT, "0.00");

			msgmap.put(MapKeys.MSGID, ACKIdGenerator.getAckId());

			
			List<Map<String,Object>> msgmaplist=(List<Map<String,Object>>)msgmap.get(MapKeys.MSGLIST);
			if(msgmaplist==null){
				
				dosingleSMS();
				
			}else{
				
				doSendMultiPart(msgmaplist);
			}
						

		}
		
		
	}

	
	
	private void gotosleep() {
		
		try{
			
			Thread.sleep(1000L);
			
		}catch(Exception e){
			
		}
		
	}

	public void sentToNextLevel() throws Exception {
		
		if(msgmap.get(MapKeys.STATUSID).equals(""+MessageStatus.KANNEL_SUBMIT_FAILED)||msgmap.get(MapKeys.STATUSID).equals(""+MessageStatus.INVALID_ROUTE_GROUP)){
			
			Map<String,Object> logmap=new HashMap<String,Object>();
			logmap.putAll(msgmap);
			logmap.put("module", "SMSProcessor");
			logmap.put("logname", "router");

			doRetry(logmap);
			
			new FileWrite().write(logmap);
			
		}else{

			
			Map<String,Object> logmap=new HashMap<String,Object>();
			logmap.putAll(msgmap);
			logmap.put("module", "SMSProcessor");
			if(msgmap.get(MapKeys.ISDNRETRY)==null){
				doOtpRetry(logmap);
			}
			
			logmap.put("logname", "router");
			doBilling(logmap);
			
			new FileWrite().write(logmap);

		}

		
	}

	private void doOtpRetry(Map<String, Object> logmap)  throws Exception {
		
		if(msgmap.get(MapKeys.ATTEMPT_TYPE).toString().equals("0")&&msgmap.get(MapKeys.STATUSID).toString().equals(""+MessageStatus.KANNEL_SUBMIT_SUCCESS)&&msgmap.get(MapKeys.UDH)==null&&PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.OTP_RETRY_YN).equals("1")){
			
		String reroutesmscid=ReRouting.getInstance().getReRouteSmscid(msgmap.get(MapKeys.USERNAME).toString(), msgmap.get(MapKeys.SMSCID_ORG).toString());	
	
		if(reroutesmscid!=null){
			
		 Map<String,Object> reroutemap=new HashMap(msgmap);
		 
		 reroutemap.put(MapKeys.SMSCID_ORG, reroutesmscid);
		 reroutemap.put(MapKeys.SMSCID, reroutesmscid);

		 int attempt=10;
		 String attemptstr=(String)reroutemap.get(MapKeys.MSG_DELIVERY_ATTEMPT);
		 if(attemptstr!=null){
		 try{
			 attempt= Integer.parseInt(attemptstr);
		 }catch(Exception e){
			 
		 }
		 }
		 
		 attempt++;
		 reroutemap.put(MapKeys.MSG_DELIVERY_ATTEMPT, ""+attempt);
		 reroutemap.put(MapKeys.OTPRETRY_POPUPTIME, ""+System.currentTimeMillis());
		if(new QueueSender().sendL("otpretrypool", reroutemap, false, logmap)){
			
			logmap.put("sms otpretrypool status", "Message Sent to otpretrypool Queue Successfully");
		
		}else{
			
			logmap.put("sms otpretrypool status", "Message Sent to otpretrypool Queue Failed otpretry never happen");

		}
		
		}
		}
	}
	
	
	
	private void connectKannel(Map<String,Object> splitupmsg)  throws Exception {
		
		
		setKannelURL(splitupmsg);
			
	
		connectKannelURL(splitupmsg);
	}
	
	
		



	private void connectKannelURL(Map<String, Object> splitupmsg) throws Exception {

		if(isfurtherprocess){
			
			String kannelresponse="sent.";
			
			if(!(msgmap.get(MapKeys.SMSCID).toString().equals("apps")||msgmap.get(MapKeys.SMSCID).toString().equals("reapps"))){
				
				kannelresponse=connectKannel(splitupmsg.get(MapKeys.KANNEL_URL).toString());
			}
		
			msgmap.put("kannelresponse", kannelresponse);

			if(kannelresponse==null){	

				msgmap.put(MapKeys.KANNEL_POPTIME, ""+System.currentTimeMillis());
				splitupmsg.put(MapKeys.STATUSID, ""+MessageStatus.KANNEL_SUBMIT_FAILED);

			}else{
				
			
				kannelresponse=kannelresponse.toLowerCase();
			
			if(kannelresponse.startsWith("sent")||kannelresponse.startsWith("0: accepted for delivery")||kannelresponse.startsWith("3: queued for later delivery")){
							
				splitupmsg.put(MapKeys.STATUSID, ""+MessageStatus.KANNEL_SUBMIT_SUCCESS);
			
			}else{

				msgmap.put(MapKeys.KANNEL_POPTIME, ""+System.currentTimeMillis());
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.KANNEL_RESPONSE_FAILED);

			}
			}
		}

		
	}
	private void connectKannel()  throws Exception {
		
		setKannelURL();

		connectKannelURL();
			
		
			
		
	}
	
	
	public void connectKannelURL() throws Exception{

		if(isfurtherprocess){
			
			String kannelresponse="sent.";
			
			if(!(msgmap.get(MapKeys.SMSCID).toString().equals("apps")||msgmap.get(MapKeys.SMSCID).toString().equals("reapps"))){
				
				kannelresponse=connectKannel(msgmap.get(MapKeys.KANNEL_URL).toString());
			}
			msgmap.put("kannelresponse", kannelresponse);
			
			if(kannelresponse==null){	
				msgmap.put(MapKeys.KANNEL_POPTIME, ""+System.currentTimeMillis());
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.KANNEL_SUBMIT_FAILED);
			}else{
			
				kannelresponse=kannelresponse.toLowerCase();
			
			if(kannelresponse.startsWith("sent")||kannelresponse.startsWith("0: accepted for delivery")|| kannelresponse.startsWith("3: queued for later delivery")){
			
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.KANNEL_SUBMIT_SUCCESS);
		
			}else{
				msgmap.put(MapKeys.KANNEL_POPTIME, ""+System.currentTimeMillis());
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.KANNEL_RESPONSE_FAILED);

			}
			}
	
		}
		}

	public void doDuplicate(){

		if(isfurtherprocess){
			
		
			String duplicatetype=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.DUPLICATE_TYPE);
			
			if(duplicatetype.equals("1")){
				
				if(new DuplicateCheck().isDuplicate(msgmap.get(MapKeys.USERNAME).toString(), msgmap.get(MapKeys.MOBILE).toString())){
					
					isfurtherprocess=false;
					
					msgmap.put(MapKeys.STATUSID, ""+MessageStatus.DUPLICATE_SMS);

					return;
					
				}
			}else if(duplicatetype.equals("2")){
				
				if(new DuplicateCheck().isDuplicate(msgmap.get(MapKeys.USERNAME).toString(), msgmap.get(MapKeys.MOBILE).toString(), msgmap.get(MapKeys.FULLMSG).toString())){
					
					isfurtherprocess=false;
					
					msgmap.put(MapKeys.STATUSID, ""+MessageStatus.DUPLICATE_SMS);

					return;
					
				}
			}
		}

	}
	private boolean hasCredit() throws Exception {

		if(!msgmap.get(MapKeys.ATTEMPT_TYPE).toString().equals("0") || PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.BILLTYPE).equalsIgnoreCase("postpaid")){
			
			return true;
			
		}else{
			
			return new CreditProcessor().hasBalance(msgmap.get(MapKeys.USERNAME).toString(), Double.parseDouble(msgmap.get(MapKeys.CREDIT).toString()));
		}
	}

	public void setCredit()  throws Exception {
		
		if(isfurtherprocess){
			
			String username=msgmap.get(MapKeys.USERNAME).toString();

			String superadmin=PushAccount.instance().getPushAccount(username).get(MapKeys.SUPERADMIN).toString();
			String admin=PushAccount.instance().getPushAccount(username).get(MapKeys.ADMIN).toString();
			String countrycode=msgmap.get(MapKeys.COUNTRYCODE).toString();

			String operator=(String)msgmap.get(MapKeys.OPERATOR);
			String circle=(String)msgmap.get(MapKeys.CIRCLE);
			
			String credit=null;
			
			if(countrycode.equals("91")){
			
				
			for(int logic=1;logic<17;logic++) {
				
				String key=getKey(superadmin,admin,username,operator,circle,logic);
				
				credit=DomesticCredit.getInstance().getCredit(key, msgmap.get(MapKeys.ROUTECLASS).toString());

				
				if(credit!=null&&credit.trim().length()>0) {
					
					break;
					
				}
				
			}
			}else{
				
				for(int logic=1;logic<9;logic++) {
					
					String key=getKey(superadmin,admin,username,countrycode,logic);
					
					credit=InternationalCredit.getInstance().getCredit(key);

					
					if(credit!=null&&credit.trim().length()>0) {
						
						break;
						
					}					
				}
			}
		
			if(credit==null){
				
				credit="1.0";
			}
			
			double msgcount=Double.parseDouble(msgmap.get(MapKeys.TOTAL_MSG_COUNT).toString());
			
			double totalcredit=msgcount*Double.parseDouble(credit);
			
			msgmap.put(MapKeys.CREDIT, ""+totalcredit);
			
			if(isfurtherprocess){
			msgmap.put("isfurther", "y");
			
			}
		}else{
			
			msgmap.put(MapKeys.CREDIT, "0");

		}
			return ;
		

	}

	
	private String getKey(String superadmin,String admin,String username, String operator, String circle, int logic) {

		switch(logic) {
		
		case 1:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+username+TableExsists.CONJUNCTION+operator+TableExsists.CONJUNCTION+circle+TableExsists.CONJUNCTION;
		case 2:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+username+TableExsists.CONJUNCTION+operator+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
		case 3:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+username+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+circle+TableExsists.CONJUNCTION;
		case 4:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+username+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
		case 5:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+operator+TableExsists.CONJUNCTION+circle+TableExsists.CONJUNCTION;
		case 6:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+operator+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
		case 7:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+circle+TableExsists.CONJUNCTION;
		case 8:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
		case 9:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+operator+TableExsists.CONJUNCTION+circle+TableExsists.CONJUNCTION;
		case 10:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+operator+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
		case 11:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+circle+TableExsists.CONJUNCTION;
		case 12:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
		case 13:
			 return TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+operator+TableExsists.CONJUNCTION+circle+TableExsists.CONJUNCTION;
		case 14:
			 return TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+operator+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
		case 15:
			 return TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+circle+TableExsists.CONJUNCTION;
		case 16:
			 return TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
	
				 
		
		}
		
		return "";
	}

	private String getKey(String superadmin,String admin,String username, String countrycode, int logic) {

		switch(logic) {
		
		case 1:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+username+TableExsists.CONJUNCTION+countrycode+TableExsists.CONJUNCTION;
		case 2:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+username+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
		case 3:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+countrycode+TableExsists.CONJUNCTION;
		case 4:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+admin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
		case 5:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+countrycode+TableExsists.CONJUNCTION;
		case 6:
			 return TableExsists.CONJUNCTION+superadmin+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
		case 7:
			 return TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+countrycode+TableExsists.CONJUNCTION;
		case 8:
			 return TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION+TableExsists.NULL+TableExsists.CONJUNCTION;
	
				 
		
		}
		
		return "";
	}


	
	private String getPointer(Map<String,String> msgmap,int routelistsize)  throws Exception{
		
	
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

	
	public void doConcate()  throws Exception {
		
		if(isfurtherprocess){


			if(msgmap.get(MapKeys.UDH)==null){
				
				if(FeatureCode.isUDHMessage(msgmap.get(MapKeys.FEATURECODE).toString())){
					
					List<String> splitMsgList=doSplit();
					
					List<Map<String,String>> msgmaplist=new ArrayList<Map<String,String>>();

					String msg_ref_no=getReferenceNumber();

					int totalmsgcount=splitMsgList.size();

					msgmap.put(MapKeys.TOTAL_MSG_COUNT,""+totalmsgcount);

					for(int i=0;i<totalmsgcount;i++)
					{
						Map<String,String> cloneMsgMap = new HashMap();
												
						cloneMsgMap.put(MapKeys.MSGID, msgmap.get(MapKeys.MSGID).toString());
						
						int splitseq=(i+1);
						
						cloneMsgMap.put(MapKeys.SPLIT_SEQ,""+splitseq);
					
						cloneMsgMap.put(MapKeys.FULLMSG,splitMsgList.get(i));
						
						cloneMsgMap.put(MapKeys.UDH,getUDH(msg_ref_no,totalmsgcount,splitseq));
						
						msgmaplist.add(cloneMsgMap);

					}
					
					msgmap.put(MapKeys.MSGLIST, msgmaplist);
					
				}
			}
		}
		
		return ;
	}

	
	private String getUDH(String msg_ref_no, int totalmsgcount, int splitseq)  throws Exception {
		
		return getConcatMsg8BitUDH(msg_ref_no, totalmsgcount, splitseq);
	}


	private String getReferenceNumber()   throws Exception{

		return get8BitRunningHexaSequence();
	}


	private List doSplit()  throws Exception
	{
		String message =msgmap.get(MapKeys.FULLMSG).toString();
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
	
	
	private int getSplitLength()  throws Exception{

		int splitlength=0;
		
		
			if(FeatureCode.isUnicodeConcate(msgmap.get(MapKeys.FEATURECODE).toString())){
				
				splitlength=268;
				
			}else{
				
				splitlength=153;
				
			}
		
		return splitlength;
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
	
	public void doDNMessage()  throws Exception{
		
		String msg=msgmap.get(MapKeys.FULLMSG).toString();
		
		
		if (msgmap.get(MapKeys.ENGLISH_AS_HEX)!=null&&msgmap.get(MapKeys.ENGLISH_AS_HEX).toString().equals("true")) {
			msg = msg.length() > 40 ? msg.toString().substring(0, 40) : msg;
			msg =decodeHexString(msg);
		} else {
			msg = msg.length() > 20
					? msg.substring(0, 20) : msg;
		}
		
		msgmap.put(MapKeys.DNMSG, msg);
	}
	
	private String decodeHexString(String hexText)  throws Exception
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

	private void doBilling(Map<String,Object> logmap)  throws Exception {
	
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
	
	private void doRetry(Map<String,Object> logmap)  throws Exception{
		
	
		
		if(new QueueSender().sendL("kannelretrypool", msgmap, false, logmap)){
			
			logmap.put("sms processor status", "Message Sent to kannelretrypool Queue Successfully");
		
		}else{
			
			logmap.put("sms processor status", "Message Sent to kannelretrypool Queue Failed message will be loss");

		}
	
	
	
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

	private synchronized  String get8BitRunningHexaSequence()  throws Exception
	{

		if(count<255)
			count++;
		else
			count=1;
		
		return StringUtils.leftPad(Integer.toHexString(count),2,"0");
	}
	
	private synchronized  String get16BitRunningHexaSequence()  throws Exception
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

	private void setKannelURL(Map<String,Object> splitupmsg)  throws Exception{
		
		
		String msg=getMessage(splitupmsg);
		
		String [] params=
			{
					URLEncoder.encode(msgmap.get(MapKeys.KANNEL_IP).toString(),"UTF-8"),
					URLEncoder.encode(msgmap.get(MapKeys.KANNEL_PORT).toString(),"UTF-8"),
					URLEncoder.encode(ConfigParams.getInstance().getProperty(ConfigKey.KANNEL_USERNAME),"UTF-8"),
					URLEncoder.encode(ConfigParams.getInstance().getProperty(ConfigKey.KANNEL_PASSWORD),"UTF-8"),
					URLEncoder.encode(msgmap.get(MapKeys.SMSCID_ORG).toString(),"UTF-8"),
					URLEncoder.encode(msgmap.get(MapKeys.SENDERID).toString(),"UTF-8"),
					URLEncoder.encode(msgmap.get(MapKeys.MOBILE).toString(),"UTF-8"),
					msg,
					URLEncoder.encode(msgmap.get(MapKeys.DLR_URL).toString(),"UTF-8"),
			};
		
		String kannelurl="";
		
		if(msgmap.get(MapKeys.ROUTECLASS_ORG).toString().equals("4")){
			
			kannelurl=MessageFormat.format(APPS_URL, params);

		}else if(msgmap.get(MapKeys.ROUTECLASS_ORG).toString().equals("5")){
			
			kannelurl=MessageFormat.format(RETRY_URL, params);

		}else{
			
			kannelurl=MessageFormat.format(KANNEL_URL, params);
		}
				
		
		if(splitupmsg.get(MapKeys.UDH)!=null){
		try{
			kannelurl=kannelurl+"&udh="+addKannelSpecialCharactertoHex(splitupmsg.get(MapKeys.UDH).toString());
		}catch(Exception e){
			msgmap.put(MapKeys.STATUSID, MessageStatus.INVALID_HEX_UDH);

			isfurtherprocess=false;
		}
		}
		
		
	if(msgmap.get(MapKeys.TEMPLATEID)!=null&&msgmap.get(MapKeys.ENTITYID)!=null){
			
			try{
				kannelurl=kannelurl+"&meta-data=%3Fsmpp%3Fentityid%3D"+msgmap.get(MapKeys.ENTITYID).toString().trim()+"%26templateid%3D"+msgmap.get(MapKeys.TEMPLATEID).toString().trim();
			
				}catch(Exception e){
					
				}
		}
	
		
		String expiry=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.EXPIRY);
		int expirtInt=0;
		if(expiry!=null){
			expirtInt=Integer.parseInt(expiry);
		
		}
		if(expirtInt>0){
			
			kannelurl=kannelurl+"&validity="+expirtInt;
		}
	
		kannelurl=kannelurl+FeatureCode.getKannelUrlSuffix(msgmap.get(MapKeys.FEATURECODE).toString());

		splitupmsg.put(MapKeys.KANNEL_URL, kannelurl);
			
	}
	

	
	private String getMessage(Map<String, Object> splitupmsg) throws UnsupportedEncodingException {
	
		
		String msg=splitupmsg.get(MapKeys.FULLMSG).toString();
		
		if(FeatureCode.isHexa(msgmap.get(MapKeys.FEATURECODE).toString())) 		
		{
			try{
				msg=addKannelSpecialCharactertoHex(msg);
				}catch(Exception e){
					msgmap.put(MapKeys.STATUSID, MessageStatus.INVALID_HEX_MESSAGE);
		
					isfurtherprocess=false;
				}
		}else{
			
			msg=URLEncoder.encode(msg,"UTF-8");
			msg=msg.replaceAll("%60", "%27");
			msg=msg.replaceAll("%E2%80%99", "%27");			
		}
		
		return msg;
	
	
	}
	
	
	private void setKannelURL()  throws Exception{
		
		
		String msg=getMessage();
		
		
		String [] params=
			{
					URLEncoder.encode(msgmap.get(MapKeys.KANNEL_IP).toString(),"UTF-8"),
					URLEncoder.encode(msgmap.get(MapKeys.KANNEL_PORT).toString(),"UTF-8"),
					URLEncoder.encode(ConfigParams.getInstance().getProperty(ConfigKey.KANNEL_USERNAME),"UTF-8"),
					URLEncoder.encode(ConfigParams.getInstance().getProperty(ConfigKey.KANNEL_PASSWORD),"UTF-8"),
					URLEncoder.encode(msgmap.get(MapKeys.SMSCID_ORG).toString(),"UTF-8"),
					URLEncoder.encode(msgmap.get(MapKeys.SENDERID).toString(),"UTF-8"),
					URLEncoder.encode(msgmap.get(MapKeys.MOBILE).toString(),"UTF-8"),
					msg,
					URLEncoder.encode(msgmap.get(MapKeys.DLR_URL).toString(),"UTF-8"),
			};
		
		String kannelurl="";
		
		if(msgmap.get(MapKeys.ROUTECLASS_ORG).toString().equals("4")){
			
			kannelurl=MessageFormat.format(APPS_URL, params);

		}else if(msgmap.get(MapKeys.ROUTECLASS_ORG).toString().equals("5")){
			
			kannelurl=MessageFormat.format(RETRY_URL, params);

		}else{
			
			kannelurl=MessageFormat.format(KANNEL_URL, params);
		}
				
		
		if(msgmap.get(MapKeys.UDH)!=null){
		
			try{
			kannelurl=kannelurl+"&udh="+addKannelSpecialCharactertoHex(msgmap.get(MapKeys.UDH).toString());
		
			}catch(Exception e){
				msgmap.put(MapKeys.STATUSID, MessageStatus.INVALID_HEX_UDH);
				isfurtherprocess=false;
			}
		}
		
		
		if(msgmap.get(MapKeys.TEMPLATEID)!=null&&msgmap.get(MapKeys.ENTITYID)!=null){
			
			try{
				kannelurl=kannelurl+"&meta-data=%3Fsmpp%3Fentityid%3D"+msgmap.get(MapKeys.ENTITYID).toString().trim()+"%26templateid%3D"+msgmap.get(MapKeys.TEMPLATEID).toString().trim();
			
				}catch(Exception e){
					
				}
		}
		String expiry=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.EXPIRY);
		int expirtInt=0;
		if(expiry!=null){
			expirtInt=Integer.parseInt(expiry);
		
		}
		if(expirtInt>0){
			
			kannelurl=kannelurl+"&validity="+expirtInt;
		}
	
		kannelurl=kannelurl+FeatureCode.getKannelUrlSuffix(msgmap.get(MapKeys.FEATURECODE).toString());

		msgmap.put(MapKeys.KANNEL_URL, kannelurl);
			
	}
	
	
	private String getMessage() throws UnsupportedEncodingException {
		
		String msg=msgmap.get(MapKeys.FULLMSG).toString();
		
		if(FeatureCode.isHexa(msgmap.get(MapKeys.FEATURECODE).toString())) 		
		{
			if(msgmap.get(MapKeys.ROUTECLASS_ORG).toString().equals("4")){
				msg=URLEncoder.encode(msg,"UTF-8");

			}else{
			try{
			msg=addKannelSpecialCharactertoHex(msg);
			}catch(Exception e){
				msgmap.put(MapKeys.STATUSID, MessageStatus.INVALID_HEX_MESSAGE);
				isfurtherprocess=false;
			}
			}
		}else{
			
			msg=URLEncoder.encode(msg,"UTF-8");
			msg=msg.replaceAll("%60", "%27");
			msg=msg.replaceAll("%E2%80%99", "%27");
		
		}
		
		return msg;
	}
	
	
	private String addKannelSpecialCharactertoHex(String hexa)  throws Exception
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
	
	
	public String connectKannel(String sUrl)  throws Exception{

		long start=System.currentTimeMillis();
		
		String response = "";

		BufferedReader in = null;
		try {
			int httpConnectionTimeout = 300000;
			int httpResponseTimeout = 300000;
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

				if (decodedString.toString().length() != 0){
					response = decodedString.toString().trim();
				}
				
			} else {
				

				Map<String,Object> logmap=new HashMap<String,Object>();
				logmap.put("iGetResultCode", ""+iGetResultCode);
				logmap.putAll(msgmap);
				logmap.put("logname", "kannelinvalidresponse");
				try{
	            	if (decodedString.toString().length() != 0){
						response = decodedString.toString().trim();
					}
	            }catch(Exception e){
	            	
	            }
	            
				logmap.put("kannelresponse",response);

	            
	            new FileWrite().write(logmap);
				
			}

		} catch (Exception e) {
			
			Map<String,Object> logmap=new HashMap<String,Object>();
			logmap.putAll(msgmap);
			logmap.put("url", sUrl);
			logmap.put("error", ErrorMessage.getMessage(e));
			logmap.put("logname", "kannelconnecterrorresponse");


            new FileWrite().write(logmap);
			
            response= null;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}
		
		long end=System.currentTimeMillis();

			
			
			Map<String,Object> logmap=new HashMap<String,Object>();
			logmap.put("username", msgmap.get(MapKeys.USERNAME));
			logmap.put("url", sUrl);
			logmap.put("kannelconnectingtime",""+ (end-start));
			logmap.put("logname", "kannelresponsetime");
            new FileWrite().write(logmap);

            return response;
	}

	
	public void setDLRURL()  throws Exception{
		
		msgmap.put(MapKeys.KTIME, ""+System.currentTimeMillis());
		String templateid=msgmap.get(MapKeys.TEMPLATEID)==null?"":msgmap.get(MapKeys.TEMPLATEID).toString();
		String entityid=msgmap.get(MapKeys.ENTITYID)==null?"":msgmap.get(MapKeys.ENTITYID).toString();
		String dlttype=msgmap.get(MapKeys.DLT_TYPE)==null?"":msgmap.get(MapKeys.DLT_TYPE).toString();

		String dnip=(String)msgmap.get(MapKeys.DN_IP);
		String dnport=(String)msgmap.get(MapKeys.DN_PORT);
		
		if(dnip==null){
			dnip=ConfigParams.getInstance().getProperty(ConfigKey.LOADBALANCER_DN_IP);
		}
		
		if(dnport==null){
			dnport=ConfigParams.getInstance().getProperty(ConfigKey.LOADBALANCER_DN_PORT);
		}
		String [] params=
		{
				URLEncoder.encode(dnip,"UTF-8"),//0
				URLEncoder.encode(dnport,"UTF-8"),//1
				URLEncoder.encode(msgmap.get(MapKeys.USERNAME).toString(),"UTF-8"),//2
				URLEncoder.encode(msgmap.get(MapKeys.SENDERID_ORG).toString(),"UTF-8"),//3
				URLEncoder.encode(msgmap.get(MapKeys.DNMSG).toString(),"UTF-8"),//4
				URLEncoder.encode(msgmap.get(MapKeys.ACKID).toString(),"UTF-8"),//5
				URLEncoder.encode(msgmap.get(MapKeys.MSGID).toString(),"UTF-8"),//6
				URLEncoder.encode(msgmap.get(MapKeys.MOBILE).toString(),"UTF-8"),//7
				URLEncoder.encode(msgmap.get(MapKeys.SMSCID_ORG).toString(),"UTF-8"),//8
				URLEncoder.encode(msgmap.get(MapKeys.RTIME).toString(),"UTF-8"),//9
				URLEncoder.encode(msgmap.get(MapKeys.KTIME).toString(),"UTF-8"),//10
				SYSTEMID,//11
				DR,//12
				STATUSCD,//13
				URLEncoder.encode(msgmap.get(MapKeys.OPERATOR)==null?"0":msgmap.get(MapKeys.OPERATOR).toString(),"UTF-8"),//14
				URLEncoder.encode(msgmap.get(MapKeys.CIRCLE)==null?"0":msgmap.get(MapKeys.CIRCLE).toString(),"UTF-8"),//15
				URLEncoder.encode(msgmap.get(MapKeys.COUNTRYCODE)==null?"0":msgmap.get(MapKeys.COUNTRYCODE).toString(),"UTF-8"),//16
				URLEncoder.encode(msgmap.get(MapKeys.PROTOCOL).toString(),"UTF-8"),//17
				URLEncoder.encode(msgmap.get(MapKeys.MSGTYPE).toString(),"UTF-8"),//18
				URLEncoder.encode(msgmap.get(MapKeys.FEATURECODE).toString(),"UTF-8"),//19
				URLEncoder.encode(msgmap.get(MapKeys.FULLMSG).toString(),"UTF-8"),//20
				URLEncoder.encode(msgmap.get(MapKeys.PARAM1)==null?"":msgmap.get(MapKeys.PARAM1).toString(),"UTF-8"),//21
				URLEncoder.encode(msgmap.get(MapKeys.PARAM2)==null?"":msgmap.get(MapKeys.PARAM2).toString(),"UTF-8"),//22
				URLEncoder.encode(msgmap.get(MapKeys.PARAM3)==null?"":msgmap.get(MapKeys.PARAM3).toString(),"UTF-8"),//23
				URLEncoder.encode(msgmap.get(MapKeys.PARAM4)==null?"":msgmap.get(MapKeys.PARAM4).toString(),"UTF-8"),//24
				URLEncoder.encode(msgmap.get(MapKeys.ROUTE_CLASS).toString(),"UTF-8"),//25
				URLEncoder.encode(msgmap.get(MapKeys.ATTEMPT_TYPE).toString(),"UTF-8"),//26
				URLEncoder.encode(msgmap.get(MapKeys.TOTAL_MSG_COUNT).toString(),"UTF-8"),//27
				URLEncoder.encode(""),//28,
				URLEncoder.encode(msgmap.get(MapKeys.CREDIT)==null?"0":msgmap.get(MapKeys.CREDIT).toString(),"UTF-8"),//29
				URLEncoder.encode(templateid,"UTF-8"),//30
				URLEncoder.encode(entityid,"UTF-8"),//31
				URLEncoder.encode(dlttype,"UTF-8")//32
					

		};
		
		String dlrurl=MessageFormat.format(DLR_URL, params);
		msgmap.put(MapKeys.DLR_URL, dlrurl);
		
		
	}
	

public void setDLRURL(Map<String,Object> splitmap)  throws Exception{
		
		msgmap.put(MapKeys.KTIME, ""+System.currentTimeMillis());
	
		String dnip=(String)msgmap.get(MapKeys.DN_IP);
		String dnport=(String)msgmap.get(MapKeys.DN_PORT);
		
		if(dnip==null){
			dnip=ConfigParams.getInstance().getProperty(ConfigKey.LOADBALANCER_DN_IP);
		}
		
		if(dnport==null){
			dnport=ConfigParams.getInstance().getProperty(ConfigKey.LOADBALANCER_DN_PORT);
		}
	
		String templateid=msgmap.get(MapKeys.TEMPLATEID)==null?"":msgmap.get(MapKeys.TEMPLATEID).toString();
		String entityid=msgmap.get(MapKeys.ENTITYID)==null?"":msgmap.get(MapKeys.ENTITYID).toString();
		String dlttype=msgmap.get(MapKeys.DLT_TYPE)==null?"":msgmap.get(MapKeys.DLT_TYPE).toString();
		
		String [] params=
		{
				URLEncoder.encode(dnip,"UTF-8"),//0
				URLEncoder.encode(dnport,"UTF-8"),//1
				URLEncoder.encode(msgmap.get(MapKeys.USERNAME).toString(),"UTF-8"),//2
				URLEncoder.encode(msgmap.get(MapKeys.SENDERID_ORG).toString(),"UTF-8"),//3
				URLEncoder.encode(msgmap.get(MapKeys.DNMSG).toString(),"UTF-8"),//4
				URLEncoder.encode(msgmap.get(MapKeys.ACKID).toString(),"UTF-8"),//5
				URLEncoder.encode(msgmap.get(MapKeys.MSGID).toString(),"UTF-8"),//6
				URLEncoder.encode(msgmap.get(MapKeys.MOBILE).toString(),"UTF-8"),//7
				URLEncoder.encode(msgmap.get(MapKeys.SMSCID_ORG).toString(),"UTF-8"),//8
				URLEncoder.encode(msgmap.get(MapKeys.RTIME).toString(),"UTF-8"),//9
				URLEncoder.encode(msgmap.get(MapKeys.KTIME).toString(),"UTF-8"),//10
				SYSTEMID,//11
				DR,//12
				STATUSCD,//13
				URLEncoder.encode(msgmap.get(MapKeys.OPERATOR)==null?"":msgmap.get(MapKeys.OPERATOR).toString(),"UTF-8"),//14
				URLEncoder.encode(msgmap.get(MapKeys.CIRCLE)==null?"":msgmap.get(MapKeys.CIRCLE).toString(),"UTF-8"),//15
				URLEncoder.encode(msgmap.get(MapKeys.COUNTRYCODE)==null?"":msgmap.get(MapKeys.COUNTRYCODE).toString(),"UTF-8"),//16
				URLEncoder.encode(msgmap.get(MapKeys.PROTOCOL).toString(),"UTF-8"),//17
				URLEncoder.encode(msgmap.get(MapKeys.MSGTYPE).toString(),"UTF-8"),//18
				URLEncoder.encode(msgmap.get(MapKeys.FEATURECODE).toString(),"UTF-8"),//19
				URLEncoder.encode(msgmap.get(MapKeys.FULLMSG).toString(),"UTF-8"),//20
				URLEncoder.encode(msgmap.get(MapKeys.PARAM1)==null?"":msgmap.get(MapKeys.PARAM1).toString(),"UTF-8"),//21
				URLEncoder.encode(msgmap.get(MapKeys.PARAM2)==null?"":msgmap.get(MapKeys.PARAM2).toString(),"UTF-8"),//22
				URLEncoder.encode(msgmap.get(MapKeys.PARAM3)==null?"":msgmap.get(MapKeys.PARAM3).toString(),"UTF-8"),//23
				URLEncoder.encode(msgmap.get(MapKeys.PARAM4)==null?"":msgmap.get(MapKeys.PARAM4).toString(),"UTF-8"),//24
				URLEncoder.encode(msgmap.get(MapKeys.ROUTE_CLASS).toString(),"UTF-8"),//25
				URLEncoder.encode(msgmap.get(MapKeys.ATTEMPT_TYPE).toString(),"UTF-8"),//26
				URLEncoder.encode(msgmap.get(MapKeys.TOTAL_MSG_COUNT).toString(),"UTF-8"),//27
				URLEncoder.encode(splitmap.get(MapKeys.SPLIT_SEQ).toString(),"UTF-8"),//28
				URLEncoder.encode(msgmap.get(MapKeys.CREDIT)==null?"0":msgmap.get(MapKeys.CREDIT).toString(),"UTF-8"),//29
				URLEncoder.encode(templateid,"UTF-8"),//30
				URLEncoder.encode(entityid,"UTF-8"),//31
				URLEncoder.encode(dlttype,"UTF-8")//32
		

		};
		
		String dlrurl=MessageFormat.format(DLR_URL, params);
		msgmap.put(MapKeys.DLR_URL, dlrurl);
		
		
	}

	public void doDNRetryProcess(Map<String, Object> msgmap)  throws Exception{
		
			
			this.msgmap=msgmap;
			
			RouteProcessor processor=new RouteProcessor(msgmap);
			Map logmap=new HashMap<String,Object>();
			
			msgmap.put(MapKeys.FULLMSG,URLDecoder.decode(msgmap.get(MapKeys.FULLMSG).toString(),"UTF-8"));

			msgmap.put(MapKeys.ISDNRETRY, "Y");
			processor.doSenderCheck();
			processor.doKannelAvailable();
			msgmap.put(MapKeys.ATTEMPT_TYPE, "2");

			msgmap.put(MapKeys.MSGID, ACKIdGenerator.getAckId());
			
			if(processor.isIsfurtherprocess()){
				
			
				doDNMessage();
				doConcate();
				
				  String rollback=(String)PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.CREDIT_ROLLBACK_YN);
			        if(rollback!=null && rollback.equals("1")){
			        	isfurtherprocess=true;
			        	setCredit();
			        }else{
						msgmap.put(MapKeys.CREDIT, "0.00");

			        }
			    
							


				List<Map<String,Object>> msgmaplist=(List<Map<String,Object>>)msgmap.get(MapKeys.MSGLIST);
			
				if(msgmaplist==null){
					
					dosingleSMS();
					
				}else{
					
					doSendMultiPart(msgmaplist);
				}
							
		
			
				sentToNextLevel();

			}else{
			
				logmap.put("status", "dnretry failed");
			}
			
			
			logmap.putAll(msgmap);
			logmap.put("logname", "dnretry");

			new FileWrite().write(logmap);
		
	
		
	}
		
}

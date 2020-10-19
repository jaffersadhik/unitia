package com.winnovature.unitia.util.misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class MessageStatus {
		
	public static final int PLATFORM_ACCEPTED = 100;
	public static final int INVALID_USERNAME = 101;
	public static final int INVALID_PASSWORD = 102;
	public static final int ACCOUNT_INACTIVATED = 103;
	public static final int HTTP_QS_EMPTY_MNUMBER = 104;
	public static final int HTTP_QS_EMPTY_MESSAGE = 105;
	public static final int SCHEDULETIME_PARSE_ERROR = 106;
	public static final int INVALID_SCHEDULE_TIME = 107;
	public static final int MSG_REJECTED_SCHEDULE_OPTION_DISABLE = 108;
	public static final int INVALID_DESTINATION_ADDRESS = 109;
	public static final int SENT_TO_QUEUE_FAILED = 110;
	public static final int MOBILE_SERIES_NOT_REGISTERED_NP = 111;
	public static final int ROUTEGROUP_NOT_FOUND = 112;
	public static final int INVALID_MSGTYPE = 113;
	public static final int INVALID_SPLITGROUP = 114;
	public static final int INVALID_ROUTE_GROUP = 115;
	public static final int INVALID_SMSCID = 116;
	public static final int OPTOUT_MOBILE_NUMBER = 117;
	public static final int MOBILE_NOT_REGISTERED_OPTIN = 118;
	public static final int BLACKLIST_MOBILE = 119;
	public static final int BLACKLIST_SENDERID = 120;
	public static final int BLACKLIST_SMS_PATTERN = 121;
	public static final int FILTERING_SMS_PATTERN = 122;
	public static final int DND_REJECTED = 123;
	public static final int INVALID_COUNTRYCODE = 124;
	public static final int UNEBALE_TO_PREDICT_FEATURECODE = 125;
	public static final int KANNEL_SUBMIT_FAILED = 126;
	public static final int SENDER_NOT_WHITELISTED = 127;
	public static final int INVALID_CREDIT = 128;
	public static final int MAX_KANNEL_RETRY_EXCEEDED = 129;
	public static final int KANNEL_SUBMIT_SUCCESS = 200;
	public static final int DUPLICATE_SMS = 130;
	public static final int INVALID_IP=131;
	public static final int INVALID_HEX_MESSAGE = 132;
	public static final int INVALID_HEX_UDH = 133;
	public static final int INTL_DELIVERY_DISABLED = 134;
	public static final int  PROMO_DELIVERY_DISBALED=135;
	public static final int KANNEL_RESPONSE_FAILED = 136;
	public static final int VMN_USERNAME_MAPPING_MISSING = 137;

	
	private static Map<String,String> status=new HashMap<String,String>();
	
	private  Map<String,String> state=new HashMap<String,String>();

	private static MessageStatus obj=null;
	
	private Map<String,String> reversestatusid=new HashMap<String,String>();
	
	Set<String> dnretrystatusid=new HashSet<String>();
	
	Set<String> successmaskingstatusid=new HashSet<String>();

	static{

		status.put(""+MessageStatus.PLATFORM_ACCEPTED, "Platform Accepted For SMS Delivery");
		status.put(""+MessageStatus.INVALID_USERNAME, "Username not Registered");
		status.put(""+MessageStatus.INVALID_PASSWORD, "Invalid password");
		status.put(""+MessageStatus.ACCOUNT_INACTIVATED, "account inactivated");
		status.put(""+MessageStatus.HTTP_QS_EMPTY_MNUMBER, "mobile parametered value invalid");
		status.put(""+MessageStatus.HTTP_QS_EMPTY_MESSAGE, "message parametered value invalid");
		status.put(""+MessageStatus.SCHEDULETIME_PARSE_ERROR, "scheduletime parametered parsing error,scheduletime should be in the format of yyyy/MM/dd/HH/mm");
		status.put(""+MessageStatus.INVALID_SCHEDULE_TIME, "Invalid scheduletime parameter value");
		status.put(""+MessageStatus.INVALID_DESTINATION_ADDRESS, "Invalid mobile value");
		status.put(""+MessageStatus.MSG_REJECTED_SCHEDULE_OPTION_DISABLE, "schedule feature disable for the account");
		status.put(""+MessageStatus.SENT_TO_QUEUE_FAILED, "Sent Queue fail in Interface Level");
		status.put(""+MessageStatus.MOBILE_SERIES_NOT_REGISTERED_NP, "Mobile Number Series Not Registered in Numbering Plan Table");
		status.put(""+MessageStatus.ROUTEGROUP_NOT_FOUND, "Route Group Not Available in Route table");
		status.put(""+MessageStatus.INVALID_MSGTYPE, "Msgtype not Available for particular split group in splitgroup table");
		status.put(""+MessageStatus.INVALID_SPLITGROUP, "split group not available in splitgroup table");
		status.put(""+MessageStatus.INVALID_ROUTE_GROUP, "routegroup not available in routegroup table");
		status.put(""+MessageStatus.INVALID_SMSCID, "smscid not available in kannel table");
		status.put(""+MessageStatus.OPTOUT_MOBILE_NUMBER, "Message Rejected due to Optout mobile number for that acoount");
		status.put(""+MessageStatus.MOBILE_NOT_REGISTERED_OPTIN, "Message Rejected due to mobile number not registered in optin list for that acoount");
		status.put(""+MessageStatus.BLACKLIST_MOBILE, "Message Rejected due to mobile number blacklisted by Platform");
		status.put(""+MessageStatus.BLACKLIST_SENDERID, "Message Rejected due to senderid blacklisted by Platform");
		status.put(""+MessageStatus.BLACKLIST_SMS_PATTERN, "Message Rejected due to sms pattern is blacklisted by Platform");
		status.put(""+MessageStatus.FILTERING_SMS_PATTERN, "Message Rejected due to sms pattern is filetred by Account Level");
		status.put(""+MessageStatus.DND_REJECTED, "Message Rejected due to Mobile number Registered As DND");
		status.put(""+MessageStatus.INVALID_COUNTRYCODE, "Message Rejected due to Invalid Country code");
		status.put(""+MessageStatus.UNEBALE_TO_PREDICT_FEATURECODE, "Message Rejected due to unable to predict the feature code for that message");
		status.put(""+MessageStatus.KANNEL_SUBMIT_FAILED, "Message Rejected due to unable to connect the Kannel");
		status.put(""+MessageStatus.SENDER_NOT_WHITELISTED, "Message Rejected due to senderid not registered in platform for that account");
		status.put(""+MessageStatus.INVALID_CREDIT, "Message Rejected due to Invalid Credit for that Account");
		status.put(""+MessageStatus.MAX_KANNEL_RETRY_EXCEEDED, "Message Rejected due to kannel unavailablity");
		status.put(""+MessageStatus.DUPLICATE_SMS, "Message Rejected due to Duplicate SMS received");
		status.put(""+MessageStatus.KANNEL_SUBMIT_SUCCESS, "Message Submitted to Kannel");
		status.put(""+MessageStatus.INVALID_IP, "Message Rejected due to Customer IP not Whitelisted in the Platform");
		status.put(""+MessageStatus.INVALID_HEX_MESSAGE, "Message Rejected due to Invalid Hexa Message Content ,message length must be even");
		status.put(""+MessageStatus.INVALID_HEX_UDH, "Message Rejected due to Invalid Hexa UDH Content ,udh length must be even");
		status.put(""+MessageStatus.INTL_DELIVERY_DISABLED, "Message Rejected due to International Delivery disbaled For this Account");
		status.put(""+MessageStatus.PROMO_DELIVERY_DISBALED, "Message Rejected due to Promo Delivery disbaled For this Account");
		status.put(""+MessageStatus.KANNEL_RESPONSE_FAILED, "Message Rejected due to Invalid Response from Kannel");
		status.put(""+MessageStatus.VMN_USERNAME_MAPPING_MISSING, "Missed Call Received But VMN Username Mapping Missing");

	}

	private MessageStatus(){
		
		init();
		
		reload();
		
	}
	public void reload() {

		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,String> reversestatusid=new HashMap<String,String>();
		Map<String,String> status=new HashMap<String,String>();
		Map<String,String> state=new HashMap<String,String>();

		Set<String> dnretrystatusid=new HashSet<String>();
		Set<String> successmaskingstatusid=new HashSet<String>();

		try{
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select status_id,status_description,upper(carrier) carrier ,stat,err,dnretry_yn,successmasking_yn from message_status");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				reversestatusid.put(resultset.getString("carrier")+"~"+resultset.getString("err"), resultset.getString("status_id"));
			
				if(resultset.getString("dnretry_yn").equals("1")){
					dnretrystatusid.add(resultset.getString("status_id"));
				}
				
				if(resultset.getString("successmasking_yn").equals("1")){
					successmaskingstatusid.add(resultset.getString("status_id"));
				}
			
				status.put(resultset.getString("status_id"), resultset.getString("status_description"));
				state.put(resultset.getString("status_id"), resultset.getString("stat"));

			}
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		}finally{
			
			Close.close(connection);
		}
		
		this.reversestatusid=reversestatusid;
		MessageStatus.status=status;
		this.state=state;
		this.dnretrystatusid=dnretrystatusid;
		this.successmaskingstatusid=successmaskingstatusid;
	}
	private void init() {

		Connection connection =null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "message_status")){
				
				if(table.create(connection, "create table message_status(status_id INT PRIMARY KEY AUTO_INCREMENT,status_description varchar(650),carrier varchar(50),stat varchar(50),err varchar(5),dnretry_yn varchar(1) default '0',successmasking_yn varchar(1) default '0',unique(carrier,err))", false)){
					
				
					Iterator itr=status.keySet().iterator();
					
					while(itr.hasNext()){
						
				
						String statusid=itr.next().toString();
						String statusdescription=status.get(statusid);
						String carrier="PLATFORM";
						String err=statusid;
						String stat="REJECTD";
						
						if(statusid.equals("0")){
							
							stat="DELIVRD";
							
						}else if(statusid.equals("100")){
							
							stat="ACCEPTD";

						}
						
						table.insertMessageStatus(connection,statusid,statusdescription,carrier,stat,err);
						
					}
				}
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	}
	public static MessageStatus getInstance() {
		
		if(obj==null){
			
			obj=new MessageStatus();
		}
		
		return obj;
		
	}
	
	public String getStatus(int statusId) {
		return status.get(""+statusId);
	
	}
	
	public String getStatusid(String carrier, String errorcode) {

		if(carrier!=null){
		return reversestatusid.get(carrier.toUpperCase()+"~"+errorcode);
		}else{
			
			return null;
		}
	}
	
	public boolean isDNRetry(String statusid){
		
		return dnretrystatusid.contains(statusid);
	}
	public String getState(String statusid) {
		if(statusid.equals("000")||statusid.equals("0")){
			return "DELIVRD";
		}else{
			return state.get(statusid);
		}
		
		}
	public boolean isSuccessMasking(String statusid) {
		return successmaskingstatusid.contains(statusid);

	}
}

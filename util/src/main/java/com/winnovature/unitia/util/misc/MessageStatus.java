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
		
	public static final int SMS_HANDOVER_SUCCESS = 0;
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
	public static final int KANNEL_SUBMIT_SUCCESS = 200;
	
	private static Map<String,String> status=new HashMap<String,String>();
	private static MessageStatus obj=null;
	
	private Map<String,String> reversestatusid=null;
	Set<String> dnretrystatusid=new HashSet<String>();

	static{

		status.put(""+MessageStatus.PLATFORM_ACCEPTED, "Platform Accepted For SMS Delivery");
		status.put(""+MessageStatus.SMS_HANDOVER_SUCCESS, "SMS Handover to Mobile Successfully");
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

		Set<String> dnretrystatusid=new HashSet<String>();
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select status_id,status_description,upper(smscid) smscid ,stat,err,dnretry_yn from message_status");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				reversestatusid.put(resultset.getString("smscid")+"~"+resultset.getString("err"), resultset.getString("status_id"));
			
				if(resultset.getString("dnretry_yn").equals("1")){
					dnretrystatusid.add(resultset.getString("status_id"));
				}
				status.put(resultset.getString("status_id"), resultset.getString("status_description"));
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		}finally{
			
			Close.close(connection);
		}
		
		this.reversestatusid=reversestatusid;
		MessageStatus.status=status;
		this.dnretrystatusid=dnretrystatusid;
	}
	private void init() {

		Connection connection =null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "message_status")){
				
				if(table.create(connection, "create table message_status(status_id INT PRIMARY KEY AUTO_INCREMENT,status_description varchar(650),smscid varchar(15),stat varchar(7),err varchar(3),dnretry_yn varchar(1) default '0',unique(smscid,err))", false)){
					
				
					Iterator itr=status.keySet().iterator();
					
					while(itr.hasNext()){
						
				
						String statusid=itr.next().toString();
						String statusdescription=status.get(statusid);
						String smscid="PLATFORM";
						String err=statusid;
						String stat="REJECTD";
						
						if(statusid.equals("0")){
							
							stat="DELIVRD";
							
						}else if(statusid.equals("100")){
							
							stat="ACCEPTD";

						}
						
						table.insertMessageStatus(connection,statusid,statusdescription,smscid,stat,err);
						
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
	
	public String getStatusid(String smscid, String errorcode) {

		return reversestatusid.get(smscid.toUpperCase()+"~"+errorcode);
		
	}
	
	public boolean isDNRetry(String statusid){
		
		return dnretrystatusid.contains(statusid);
	}
}

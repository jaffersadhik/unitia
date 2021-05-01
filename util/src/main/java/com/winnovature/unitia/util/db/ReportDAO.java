package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.ReportLogTable;
import com.winnovature.unitia.util.misc.Convertor;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FeatureCode;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.MessageType;

public class ReportDAO {

	private static String SQL="";
	
	private static String SQL_DNQUERYLOG="insert into {0}(username,ackid,code,timetaken,querysize,status,rtime,itime)values(?,?,?,?,?,?,?,?)";

	static{
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("insert into {0}");
		sb.append("(");
		sb.append("ackid,msgid,username,senderid,");
		sb.append("senderid_org,mobile,fullmessage,operator,circle,");
		sb.append("countrycode,smscid_org,smscid,rtime,ktime,");
		sb.append("itime,rtime_org,stime,customerip,");
		sb.append("featurecode,routegroup,udh,credit,");
		sb.append("totalmsgcount,routeclass,");
		sb.append("param1,param2,param3,param4,");
		sb.append("auth_id,attempttype,pattern_id,");
		sb.append("carrier_stime,carrier_dtime,carrier_dtime_org,");
		sb.append("carrier_sdate,carrier_ddate,carrier_stat,carrier_err,carrier_msgid,");
		sb.append("carrier_systemid,carrier_dr,sms_latency_slap,sms_latency_slap_org,carrier_latency_slap,");
		sb.append("paltform_latency_slap,statusid,statusid_org,carrier_stime_org,");
		sb.append("templateid,entityid,dlttype,interfacetype,kannel_resp,attemptcount,");
		sb.append("templateid_customer,entityid_customer,clength,salesid,msgtype,");
		sb.append("routertime,router_instance_id,kc_instance_id,customerid,");
		sb.append("ctype,method,protocol)");

		sb.append("values(");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,");
		sb.append("?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?)");

		SQL=sb.toString();

	}
	public boolean insert(String tablename,List<Map<String, Object>> datalist) {
		
		Connection connection =null;
		PreparedStatement statement=null;
		
		Map<String, Object> logmap=null;
		try{
		
			String sql=getSQL("billing."+tablename);
			
			if(!ReportLogTable.getInstance().isVailableTable(tablename)){
			
				ReportLogTable.getInstance().reload();
			}
			
			connection= BillingDBConnection.getInstance().getConnection();
			
			connection.setAutoCommit(false);
			
			statement= connection.prepareStatement(sql);
			
			for(int i=0;i<datalist.size();i++){
				
				Map<String,Object> msgmap=datalist.get(i);
				logmap=msgmap;

				statement.setString(1,(String) msgmap.get(MapKeys.ACKID));
			
				statement.setString(2, (String)msgmap.get(MapKeys.MSGID));
				statement.setString(3,(String) msgmap.get(MapKeys.USERNAME));
				String senderidorg=(String)msgmap.get(MapKeys.SENDERID_ORG);
				
				if(senderidorg!=null&&senderidorg.length()>15){
					
					senderidorg=senderidorg.substring(0,15);
				}
				if( msgmap.get(MapKeys.SENDERID)==null){
				statement.setString(4, senderidorg);
				}else{
					String senderid=(String)msgmap.get(MapKeys.SENDERID);
					if(senderid!=null&&senderid.length()>15){
						
						senderid=senderid.substring(0,15);
					}
					statement.setString(4,senderid);

				}
				statement.setString(5, senderidorg);
				statement.setString(6, (String)msgmap.get(MapKeys.MOBILE));
				
				String fullmsg=(String)msgmap.get(MapKeys.FULLMSG);
			/*
				try{
					
					if(MessageType.isHexa( (String)msgmap.get(MapKeys.MSGTYPE))){
						
						fullmsg=Convertor.getMessage(fullmsg);
					}
				}catch(Exception e){
					
				}
				
				*/
				if("reportlog_delivery".equals(tablename)){
					statement.setString(7, null);

				}else{
					statement.setString(7, fullmsg);

				}
			
				statement.setString(8, (String)msgmap.get(MapKeys.OPERATOR));
				statement.setString(9, (String)msgmap.get(MapKeys.CIRCLE));

				statement.setString(10, (String)msgmap.get(MapKeys.COUNTRYCODE));
				
				statement.setString(11, (String)msgmap.get(MapKeys.SMSCID_ORG));
				if(msgmap.get(MapKeys.SMSCID)==null){
					
					statement.setString(12, (String)msgmap.get(MapKeys.SMSCID_ORG));
					
				}else{
				
					statement.setString(12, (String)msgmap.get(MapKeys.SMSCID));
					
				}
				statement.setTimestamp(13,new Timestamp(Long.parseLong(msgmap.get(MapKeys.RTIME).toString())));
				if(msgmap.get(MapKeys.KTIME)!=null){
					statement.setTimestamp(14,new Timestamp(Long.parseLong(msgmap.get(MapKeys.KTIME).toString())));
				}else{
					statement.setTimestamp(14,null);
						
				}
				statement.setTimestamp(15, new Timestamp(System.currentTimeMillis()));
				if(msgmap.get(MapKeys.RTIME_ORG)!=null){
				statement.setTimestamp(16, new Timestamp(Long.parseLong(msgmap.get(MapKeys.RTIME_ORG).toString())));
				}else{
					
					statement.setTimestamp(16,null);

				}
				
								
				if(msgmap.get(MapKeys.SCHEDULE_TIME)!=null&&msgmap.get(MapKeys.SCHEDULE_TIME).toString().length()>0){
					statement.setTimestamp(17, new Timestamp(Long.parseLong(msgmap.get(MapKeys.SCHEDULE_TIME).toString())));

				}else{
					statement.setTimestamp(17, null);
					
				}
				
				if( msgmap.get(MapKeys.CUSTOMERIP)!=null){
					statement.setString(18, msgmap.get(MapKeys.CUSTOMERIP).toString());
				}else{
					
					statement.setString(18, null);

				}
				
				statement.setString(19, (String)msgmap.get(MapKeys.FEATURECODE));
				statement.setString(20,(String)msgmap.get(MapKeys.ROUTEGROUP));

				statement.setString(21,(String) msgmap.get(MapKeys.UDH));
				if(msgmap.get(MapKeys.STATUSID)!=null&&(msgmap.get(MapKeys.STATUSID).toString().equals(""+MessageStatus.KANNEL_SUBMIT_SUCCESS)||msgmap.get(MapKeys.STATUSID).toString().equals("000"))){
					statement.setString(22, (String) msgmap.get(MapKeys.CREDIT));
				}else{
					statement.setString(22, "0.00");
						
				}

				statement.setString(23,(String) msgmap.get(MapKeys.TOTAL_MSG_COUNT));

				statement.setString(24, (String)msgmap.get(MapKeys.ROUTECLASS));
				statement.setString(25, (String)msgmap.get(MapKeys.PARAM1));
				statement.setString(26, (String)msgmap.get(MapKeys.PARAM2));
				statement.setString(27, (String)msgmap.get(MapKeys.PARAM3));
				statement.setString(28, (String)msgmap.get(MapKeys.PARAM4));
				statement.setString(29, (String) PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.AUTH_ID));
				statement.setString(30, (String)msgmap.get(MapKeys.ATTEMPT_TYPE));
				statement.setString(31, (String)msgmap.get(MapKeys.ALLOWED_PATTERN_ID));

				
				if(msgmap.get(MapKeys.CARRIER_SUBMITTIME)!=null){
					statement.setTimestamp(32, new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_SUBMITTIME))));
					}else{
						statement.setTimestamp(32, null);
		
					}
					if(msgmap.get(MapKeys.CARRIER_DONETIME)!=null){
					statement.setTimestamp(33,  new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_DONETIME))));
					}else{
						statement.setTimestamp(33,null);

					}
					
					if(msgmap.get(MapKeys.CARRIER_DONETIME_ORG)!=null){
					statement.setTimestamp(34, new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_DONETIME_ORG))));
					}else{
						
						statement.setTimestamp(34, null);

					}
					statement.setString(35,(String) msgmap.get(MapKeys.CARRIER_SUBMITDATE));
					statement.setString(36,(String) msgmap.get(MapKeys.CARRIER_DONEDATE));
					statement.setString(37,(String) msgmap.get(MapKeys.CARRIER_STAT));
					statement.setString(38,(String) msgmap.get(MapKeys.CARRIER_ERR));
					statement.setString(39, (String)msgmap.get(MapKeys.CARRIER_MSGID));

					statement.setString(40, (String)msgmap.get(MapKeys.CARRIER_SYSTEMID));
//					statement.setString(41, (String)msgmap.get(MapKeys.CARRIER_DR));
					statement.setString(41, null);

					statement.setString(42, (String)msgmap.get(MapKeys.SMS_LATENCY));
					statement.setString(43, (String)msgmap.get(MapKeys.SMS_LATENCY_ORG));
					statement.setString(44, (String) msgmap.get(MapKeys.CARRIER_LATENCY));

					statement.setString(45, (String)msgmap.get(MapKeys.PLATFORM_LATENCY));
					
					String statusid=(String) msgmap.get(MapKeys.STATUSID);

					if(statusid!=null&&statusid.trim().length()>0){
						
						if(tablename.equals("reportlog_delivery")){

							if(statusid.equals("200")){
								statement.setString(46, "000");

							}else{
								
								statement.setString(46, msgmap.get(MapKeys.STATUSID).toString());

							}
						}else{
					
							statement.setString(46, msgmap.get(MapKeys.STATUSID).toString());
						}
					}else{
						
						statusid=(String) msgmap.get(MapKeys.CARRIER_ERR);

						if(tablename.equals("reportlog_delivery")){
							
							if(statusid!=null&&statusid.equals("200")){

								statement.setString(46, "000");

							}else{
								statement.setString(46, (String) msgmap.get(MapKeys.CARRIER_ERR));

							}
						}else{
							statement.setString(46, (String)msgmap.get(MapKeys.STATUSID));

						}
					}
					String stausidorg=(String)msgmap.get(MapKeys.STATUSID_ORG);
					
					if(stausidorg==null||stausidorg.equals("ERROR")||stausidorg.trim().length()<1){
						
						statement.setString(47, statusid);

					}else{
						statement.setString(47, (String)msgmap.get(MapKeys.STATUSID_ORG));

					}

					
					if(msgmap.get(MapKeys.CARRIER_SUBMITTIME_ORG)!=null){
					statement.setTimestamp(48, new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_SUBMITTIME_ORG))));
					}else{
						
						statement.setTimestamp(48, null);

					}
					statement.setString(49, (String)msgmap.get(MapKeys.TEMPLATEID));
					statement.setString(50, (String)msgmap.get(MapKeys.ENTITYID));
					statement.setString(51, (String)msgmap.get(MapKeys.DLT_TYPE));
					statement.setString(52, (String)msgmap.get(MapKeys.INTERFACE_TYPE));
					
					String kannelresponse=(String)msgmap.get("kannelresponse");
					if(kannelresponse!=null&&kannelresponse.trim().length()>100){
						
						kannelresponse=kannelresponse.substring(0,100);
					}
					statement.setString(53, kannelresponse);
					statement.setString(54, (String)msgmap.get(MapKeys.ATTEMPT_COUNT));

					statement.setString(55, (String)msgmap.get(MapKeys.TEMPLATEID_CUSTOMER));
					statement.setString(56, (String)msgmap.get(MapKeys.ENTITYID_CUSTOMER));

					Object clength=msgmap.get(MapKeys.CONTENT_LENGTH);
					String stringClength="";
					if(clength!=null){
						
						stringClength=clength.toString();
					}
					statement.setString(57,stringClength  );
					
					String salesid=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get("salesid");
				
					statement.setString(58,salesid);

					statement.setString(59,(String)msgmap.get(MapKeys.MSGTYPE));

					if(msgmap.get(MapKeys.ROUTERTIME)!=null&&msgmap.get(MapKeys.ROUTERTIME).toString().length()>0){
						statement.setTimestamp(60, new Timestamp(Long.parseLong(msgmap.get(MapKeys.ROUTERTIME).toString())));

					}else{
						statement.setTimestamp(60, null);
						
					}
					
					statement.setString(61, (String)msgmap.get(MapKeys.R_ID));
					statement.setString(62, (String)msgmap.get(MapKeys.KC_ID));

					
					String customerid=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get("customerid");
					
					statement.setString(63,customerid);

					statement.setString(64,(String)msgmap.get(MapKeys.CONTENT_TYPE));
					statement.setString(65,(String)msgmap.get(MapKeys.METHOD));
					statement.setString(66,(String)msgmap.get(MapKeys.PROTOCOL));

					
					statement.addBatch();
			}
			statement.executeBatch();
			connection.commit();
			return true;
		}catch(Exception e){
			
			
			
		     
					try{
						logmap.put("module", "inserterror");
						logmap.put("logname", "inserterror");
						logmap.put("error", tablename+"\n"+ErrorMessage.getMessage(e));

						new FileWrite().write(logmap);
					}catch(Exception e1){
						
					}
			
	        
			try{
				connection.rollback();
			}catch(Exception e1){
				
			}
			
		}finally{
			
			Close.close(statement);
			Close.close(connection);
		}
		
		return false;
	}

public boolean insertDNQueryLog(String tablename,List<Map<String, Object>> datalist) {
		
		Connection connection =null;
		PreparedStatement statement=null;
		
		Map<String, Object> logmap=null;
		try{
		
			String sql=getSQLforDNQueryLOg("billing."+tablename);
			
			if(!ReportLogTable.getInstance().isVailableTable(tablename)){
			
				ReportLogTable.getInstance().reload();
			}
			
			connection= BillingDBConnection.getInstance().getConnection();
			
			connection.setAutoCommit(false);
			
			statement= connection.prepareStatement(sql);
			
			for(int i=0;i<datalist.size();i++){
				
				Map<String,Object> msgmap=datalist.get(i);
				logmap=msgmap;

				statement.setString(1,(String) msgmap.get(MapKeys.USERNAME));

				statement.setString(2,(String) msgmap.get(MapKeys.ACKID));
			
				statement.setString(3,(String) msgmap.get("code"));
				statement.setString(4,(String) msgmap.get("timetaken"));
				statement.setString(5,(String) msgmap.get("querysize"));
				statement.setString(6,(String) msgmap.get("status"));

				statement.setTimestamp(7,new Timestamp(Long.parseLong(msgmap.get(MapKeys.RTIME).toString())));
				statement.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
				statement.addBatch();
			}
			statement.executeBatch();
			connection.commit();
			return true;
		}catch(Exception e){
			
			
			
		     
					try{
						logmap.put("module", "inserterror");
						logmap.put("logname", "inserterror");
						logmap.put("error", tablename+"\n"+ErrorMessage.getMessage(e));

						new FileWrite().write(logmap);
					}catch(Exception e1){
						
					}
			
	        
			try{
				connection.rollback();
			}catch(Exception e1){
				
			}
			
		}finally{
			
			Close.close(statement);
			Close.close(connection);
		}
		
		return false;
	}

	
	
	private String getSQL(String tablename) {

		String param[]={tablename};
		return MessageFormat.format(SQL, param);
	}

	private String getSQLforDNQueryLOg(String tablename) {

		String param[]={tablename};
		return MessageFormat.format(SQL_DNQUERYLOG, param);
	}
	
}

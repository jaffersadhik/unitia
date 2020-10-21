package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.ReportLogTable;
import com.winnovature.unitia.util.misc.Convertor;
import com.winnovature.unitia.util.misc.FeatureCode;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;

public class ReportDAO {

	private static String SQL="";
	
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
		sb.append("templateid,entityid,dlttype,interfacetype)");

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
		sb.append("?,?,?,?)");

		SQL=sb.toString();

	}
	public boolean insert(String tablename,List<Map<String, Object>> datalist) {
		
		Connection connection =null;
		PreparedStatement statement=null;
		
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
				

				statement.setString(1,(String) msgmap.get(MapKeys.ACKID));
			
				statement.setString(2, (String)msgmap.get(MapKeys.MSGID));
				statement.setString(3,(String) msgmap.get(MapKeys.USERNAME));
				
				if( msgmap.get(MapKeys.SENDERID)==null){
				statement.setString(4, (String) msgmap.get(MapKeys.SENDERID_ORG));
				}else{
					
					statement.setString(4,(String) msgmap.get(MapKeys.SENDERID));

				}
				statement.setString(5, (String)msgmap.get(MapKeys.SENDERID_ORG));
				statement.setString(6, (String)msgmap.get(MapKeys.MOBILE));
				
				String fullmsg=(String)msgmap.get(MapKeys.FULLMSG);
			
				try{
					if(FeatureCode.isHexa( (String)msgmap.get(MapKeys.FEATURECODE))){
						
						fullmsg=Convertor.getMessage(fullmsg);
					}
				}catch(Exception e){
					
				}
			
				statement.setString(7, fullmsg);
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
				statement.setString(29, PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.AUTH_ID).toString());
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
					if(msgmap.get(MapKeys.STATUSID)!=null){
					statement.setString(46, msgmap.get(MapKeys.STATUSID).toString());
					}else{
						statement.setString(46, null);

					}
					statement.setString(47, (String)msgmap.get(MapKeys.STATUSID_ORG));

					
					if(msgmap.get(MapKeys.CARRIER_SUBMITTIME_ORG)!=null){
					statement.setTimestamp(48, new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_SUBMITTIME_ORG))));
					}else{
						
						statement.setTimestamp(48, null);

					}
					statement.setString(49, (String)msgmap.get(MapKeys.TEMPLATEID));
					statement.setString(50, (String)msgmap.get(MapKeys.ENTITYID));
					statement.setString(51, (String)msgmap.get(MapKeys.DLT_TYPE));
					statement.setString(52, (String)msgmap.get(MapKeys.INTERFACE_TYPE));

				statement.addBatch();
			}
			statement.executeBatch();
			connection.commit();
			return true;
		}catch(Exception e){
			
			
			e.printStackTrace();
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
	
}

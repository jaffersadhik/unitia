package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.DNTable;
import com.winnovature.unitia.util.misc.MapKeys;

public class DeliveryDAO {

	private static String SQL="";
	
	static{
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("insert into {0}");
		sb.append("(");
		sb.append("msgid,username,senderid,");
		sb.append("senderid_org,mobile,message,operator,circle,");
		sb.append("countrycode,smscid_org,smscid,rtime,ktime,");
		sb.append("itime,carrier_stime,carrier_dtime,carrier_dtime_org,");
		sb.append("carrier_sdate,carrier_ddate,carrier_stat,carrier_err,carrier_msgid,");
		sb.append("carrier_systemid,carrier_dr,sms_latency_slap,sms_latency_slap_org,carrier_latency_slap,");
		sb.append("paltform_latency_slap,statusid,statusid_org)values(");
		sb.append("?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?)");

		SQL=sb.toString();

	}
	public boolean insert(String poolname,List<Map<String,Object>> datalist){
		
		Connection connection =null;
		PreparedStatement statement=null;
		
		try{
		
			String tablename="delivery";
			String sql=getSQL(tablename);
			
			if(!DNTable.getInstance().isVailableTable(tablename)){
			
				DNTable.getInstance().reload();
			}
			
			connection= BillingDBConnection.getInstance().getConnection();
		    connection.setAutoCommit(false);
			
			statement= connection.prepareStatement(sql);
			for(int i=0;i<datalist.size();i++){
				
				Map<String,Object> msgmap=datalist.get(i);
			
				statement.setString(1, (String)msgmap.get(MapKeys.MSGID));
				statement.setString(2, (String)msgmap.get(MapKeys.USERNAME));
				
				if( msgmap.get(MapKeys.SENDERID)==null){
				statement.setString(3, (String)msgmap.get(MapKeys.SENDERID_ORG));
				}else{
					
					statement.setString(3, (String)msgmap.get(MapKeys.SENDERID));

				}
				statement.setString(4, (String)msgmap.get(MapKeys.SENDERID_ORG));
				statement.setString(5, (String)msgmap.get(MapKeys.MOBILE));
				statement.setString(6, (String)msgmap.get(MapKeys.DNMSG));
				statement.setString(7, (String)msgmap.get(MapKeys.OPERATOR));
				statement.setString(8, (String)msgmap.get(MapKeys.CIRCLE));

				statement.setString(9, (String)msgmap.get(MapKeys.COUNTRYCODE));
				
				statement.setString(10, (String)msgmap.get(MapKeys.SMSCID_ORG));
				if(msgmap.get(MapKeys.SMSCID)==null){
					
					statement.setString(11,(String) msgmap.get(MapKeys.SMSCID_ORG));
					
				}else{
				
					statement.setString(11, (String)msgmap.get(MapKeys.SMSCID));
					
				}
				statement.setTimestamp(12,new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.RTIME))));
				statement.setTimestamp(13,new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.KTIME))));

				statement.setTimestamp(14, new Timestamp(System.currentTimeMillis()));
				if(msgmap.get(MapKeys.CARRIER_SUBMITTIME)!=null){
				statement.setTimestamp(15, new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_SUBMITTIME))));
				}else{
					statement.setTimestamp(15, null);
	
				}
				if(msgmap.get(MapKeys.CARRIER_DONETIME)!=null){
				statement.setTimestamp(16,  new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_DONETIME))));
				}else{
					statement.setTimestamp(16, null);

				}
				
				if(msgmap.get(MapKeys.CARRIER_DONETIME_ORG)!=null){
				statement.setTimestamp(17, new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_DONETIME_ORG))));
				}else{
					
					statement.setTimestamp(17, null);

				}
				statement.setString(18,(String) msgmap.get(MapKeys.CARRIER_SUBMITDATE));
				statement.setString(19,(String) msgmap.get(MapKeys.CARRIER_DONEDATE));
				statement.setString(20,(String) msgmap.get(MapKeys.CARRIER_STAT));
				statement.setString(21,(String) msgmap.get(MapKeys.CARRIER_ERR));
				statement.setString(22, (String)msgmap.get(MapKeys.CARRIER_MSGID));

				statement.setString(23, (String)msgmap.get(MapKeys.CARRIER_SYSTEMID));
				statement.setString(24, (String)msgmap.get(MapKeys.CARRIER_DR));
				statement.setString(25, (String)msgmap.get(MapKeys.SMS_LATENCY));
				statement.setString(26, (String)msgmap.get(MapKeys.SMS_LATENCY_ORG));
				statement.setString(27, (String) msgmap.get(MapKeys.CARRIER_LATENCY));

				statement.setString(28, (String)msgmap.get(MapKeys.PLATFORM_LATENCY));
				statement.setString(29, (String)msgmap.get(MapKeys.STATUSID));
				statement.setString(30, (String)msgmap.get(MapKeys.STATUSID_ORG));

				statement.addBatch();
			}			
			statement.executeBatch();
			connection.commit();
			return true;
		}catch(SQLException e){
			if(e.getNextException()!=null){
			System.err.println(e.getNextException().getMessage());
			}
			e.printStackTrace();
			
			}catch(Exception e){
		
			e.printStackTrace();
			
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

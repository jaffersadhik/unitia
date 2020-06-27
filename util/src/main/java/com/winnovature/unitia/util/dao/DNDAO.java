package com.winnovature.unitia.util.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import com.winnovature.unitia.util.account.BillingTableRouting;
import com.winnovature.unitia.util.account.DNTable;
import com.winnovature.unitia.util.db.BillingDBConnection;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.misc.MapKeys;

public class DNDAO {

	private static String SQL="";
	
	static{
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("insert into {0}");
		sb.append("values(");
		sb.append("ackid,ackid_org,msgid,username,senderid,");
		sb.append("senderid_org,mobile,message,operator,circle,");
		sb.append("countrycode,smscid_org,smscid,rtime,ktime,");
		sb.append("itime,carrier_stime,carrier_dtime,carrier_dtime_org,");
		sb.append("carrier_sdate,carrier_ddate,carrier_stat,carrier_err,carrier_msgid,");
		sb.append("carrier_systemid,carrier_dr,sms_latency_slap,sms_latency_slap_org,carrier_latency_slap,");
		sb.append("paltform_latency_slap,statusid,statusid_org)(");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?)");

		SQL=sb.toString();

	}
	public void insert(Map<String,String> msgmap){
		
		Connection connection =null;
		PreparedStatement statement=null;
		
		try{
			String tablename=BillingTableRouting.getInstance().getDNTableName(msgmap.get(MapKeys.USERNAME));
		
			String sql=getSQL(tablename);
			
			if(!DNTable.getInstance().isVailableTable(tablename)){
			
				DNTable.getInstance().reload();
			}
			
			connection=(Connection) BillingDBConnection.getInstance().getConnection();
			statement= connection.prepareStatement(sql);
			
			statement.setString(1, msgmap.get(MapKeys.ACKID));
			if(msgmap.get(MapKeys.ACKID_ORG)==null){
			statement.setString(2, msgmap.get(MapKeys.ACKID));
			}else{
				
				statement.setString(2, msgmap.get(MapKeys.ACKID_ORG));

			}
			statement.setString(3, msgmap.get(MapKeys.MSGID));
			statement.setString(4, msgmap.get(MapKeys.USERNAME));
			statement.setString(5, msgmap.get(MapKeys.SENDERID));

			statement.setString(6, msgmap.get(MapKeys.SENDERID_ORG));
			statement.setString(7, msgmap.get(MapKeys.MOBILE));
			statement.setString(8, msgmap.get(MapKeys.DNMSG));
			statement.setString(9, msgmap.get(MapKeys.OPERATOR));
			statement.setString(10, msgmap.get(MapKeys.CIRCLE));

			statement.setString(11, msgmap.get(MapKeys.COUNTRYCODE));
			statement.setString(12, msgmap.get(MapKeys.SMSCID_ORG));
			statement.setString(13, msgmap.get(MapKeys.SMSCID));
			statement.setTimestamp(14,new Timestamp(Long.parseLong(msgmap.get(MapKeys.RTIME))));
			statement.setTimestamp(15,new Timestamp(Long.parseLong(msgmap.get(MapKeys.KTIME))));

			statement.setTimestamp(16, new Timestamp(System.currentTimeMillis()));
			statement.setTimestamp(17, new Timestamp(Long.parseLong(msgmap.get(MapKeys.CARRIER_SUBMITTIME))));
			statement.setTimestamp(18,  new Timestamp(Long.parseLong(msgmap.get(MapKeys.CARRIER_DONETIME))));
			statement.setTimestamp(19, new Timestamp(Long.parseLong(msgmap.get(MapKeys.CARRIER_DONETIME_ORG))));

			statement.setString(20, msgmap.get(MapKeys.CARRIER_SUBMITDATE));
			statement.setString(21, msgmap.get(MapKeys.CARRIER_DONEDATE));
			statement.setString(22, msgmap.get(MapKeys.CARRIER_STAT));
			statement.setString(23, msgmap.get(MapKeys.CARRIER_ERR));
			statement.setString(24, msgmap.get(MapKeys.CARRIER_MSGID));

			statement.setString(25, msgmap.get(MapKeys.CARRIER_SYSTEMID));
			statement.setString(26, msgmap.get(MapKeys.CARRIER_DR));
			statement.setString(27, msgmap.get(MapKeys.SMS_LATENCY));
			statement.setString(28, msgmap.get(MapKeys.SMS_LATENCY_ORG));
			statement.setString(29, msgmap.get(MapKeys.CARRIER_LATENCY));

			statement.setString(30, msgmap.get(MapKeys.PLATFORM_LATENCY));
			statement.setString(31, msgmap.get(MapKeys.STATUSID));
			statement.setString(32, msgmap.get(MapKeys.STATUSID_ORG));

			statement.execute();
			
		}catch(SQLException e){
			
			System.err.println(e.getNextException().getMessage());
		}catch(Exception e){
		
			e.printStackTrace();
			
		}finally{
			
			Close.close(statement);
			Close.close(connection);
		}
	}
	private String getSQL(String tablename) {

		String param[]={tablename};
		return MessageFormat.format(SQL, param);
	}
}

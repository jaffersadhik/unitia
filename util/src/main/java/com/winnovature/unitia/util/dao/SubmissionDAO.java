package com.winnovature.unitia.util.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;

import com.winnovature.unitia.util.account.BillingTableRouting;
import com.winnovature.unitia.util.account.SubmissionTable;
import com.winnovature.unitia.util.db.BillingDBConnection;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.misc.MapKeys;

public class SubmissionDAO {

	private static String SQL="";
	
	static{
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("insert into {0}");
		sb.append("(");
		sb.append("ackid,ackid_org,msgid,username,senderid,");
		sb.append("senderid_org,mobile,message,operator,circle,");
		sb.append("countrycode,smscid_org,smscid,rtime,ktime,");
		sb.append("itime,rtime_org,ktime_org,stime,");
		sb.append("featurecode,msgtype,routegroup,udh,");
		sb.append("totalmsgcount,splitsequence,statusid,msgclass,routeclass)");
		sb.append("values(");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,");
		sb.append("?,?,?,?,?)");
		SQL=sb.toString();

	}
	public void insert(Map<String,String> msgmap){
		
		Connection connection =null;
		PreparedStatement statement=null;
		
		try{
			String tablename=BillingTableRouting.getInstance().getDNTableName(msgmap.get(MapKeys.USERNAME));
		
			String sql=getSQL(tablename);
			
			if(!SubmissionTable.getInstance().isVailableTable(tablename)){
			
				SubmissionTable.getInstance().reload();
			}
			
			connection= BillingDBConnection.getInstance().getConnection();
			statement= connection.prepareStatement(sql);
			
			statement.setString(1, msgmap.get(MapKeys.ACKID));
			if(msgmap.get(MapKeys.ACKID_ORG)==null){
			statement.setString(2, msgmap.get(MapKeys.ACKID));
			}else{
				
				statement.setString(2, msgmap.get(MapKeys.ACKID_ORG));

			}
			statement.setString(3, msgmap.get(MapKeys.MSGID));
			statement.setString(4, msgmap.get(MapKeys.USERNAME));
			
			if( msgmap.get(MapKeys.SENDERID)==null){
			statement.setString(5, msgmap.get(MapKeys.SENDERID_ORG));
			}else{
				
				statement.setString(5, msgmap.get(MapKeys.SENDERID));

			}
			statement.setString(6, msgmap.get(MapKeys.SENDERID_ORG));
			statement.setString(7, msgmap.get(MapKeys.MOBILE));
			statement.setString(8, msgmap.get(MapKeys.MESSAGE));
			statement.setString(9, msgmap.get(MapKeys.OPERATOR));
			statement.setString(10, msgmap.get(MapKeys.CIRCLE));

			statement.setString(11, msgmap.get(MapKeys.COUNTRYCODE));
			
			statement.setString(12, msgmap.get(MapKeys.SMSCID_ORG));
			if(msgmap.get(MapKeys.SMSCID)==null){
				
				statement.setString(13, msgmap.get(MapKeys.SMSCID_ORG));
				
			}else{
			
				statement.setString(13, msgmap.get(MapKeys.SMSCID));
				
			}
			statement.setTimestamp(14,new Timestamp(Long.parseLong(msgmap.get(MapKeys.RTIME))));
			statement.setTimestamp(15,new Timestamp(Long.parseLong(msgmap.get(MapKeys.KTIME))));

			statement.setTimestamp(16, new Timestamp(System.currentTimeMillis()));
			statement.setTimestamp(17, new Timestamp(Long.parseLong(msgmap.get(MapKeys.RTIME_ORG))));
			statement.setTimestamp(18,  new Timestamp(Long.parseLong(msgmap.get(MapKeys.KTIME_ORG))));
			statement.setTimestamp(19, new Timestamp(Long.parseLong(msgmap.get(MapKeys.SCHEDULE_TIME))));
			statement.setString(20, msgmap.get(MapKeys.FEATURECODE));
			statement.setString(21, msgmap.get(MapKeys.MSGTYPE));
			statement.setString(22, msgmap.get(MapKeys.ROUTEGROUP));

			statement.setString(23, msgmap.get(MapKeys.UDH));
			statement.setString(24, msgmap.get(MapKeys.TOTAL_MSG_COUNT));
			statement.setString(25, msgmap.get(MapKeys.SPLIT_SEQ));

			statement.setString(26, msgmap.get(MapKeys.STATUSID));
			statement.setString(27, msgmap.get(MapKeys.MSGCLASS));
			statement.setString(28, msgmap.get(MapKeys.ROUTECLASS));

			statement.executeUpdate();
			
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
	}
	private String getSQL(String tablename) {

		String param[]={tablename};
		return MessageFormat.format(SQL, param);
	}
}

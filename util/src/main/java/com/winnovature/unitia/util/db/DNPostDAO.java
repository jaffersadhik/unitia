package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.DNPostTable;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;

public class DNPostDAO {

	private static String SQL="";
	
	static{
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("insert into {0}");
		sb.append("(");
		sb.append("ackid,msgid,username,senderid_org,mobile,");
		sb.append("operator,circle,countrycode,rtime,carrier_dtime,");
		sb.append("itime,statusid,status,dnmsg");
		sb.append(")values(");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?,?,");
		sb.append("?,?,?,?)");
		SQL=sb.toString();

	}
	public boolean insert(String poolname,List<Map<String,Object>> datalist){
		
		Connection connection =null;
		PreparedStatement statement=null;
		
		try{
		
			String tablename="delivery_post";
			String sql=getSQL("billing."+tablename);
			
			if(!DNPostTable.getInstance().isVailableTable(tablename)){
			
				DNPostTable.getInstance().reload();
			}
			
			connection= BillingDBConnection.getInstance().getConnection();
		    connection.setAutoCommit(false);
			
			statement= connection.prepareStatement(sql);
			for(int i=0;i<datalist.size();i++){
				
				Map<String,Object> msgmap=datalist.get(i);
			

				statement.setString(1,(String) msgmap.get(MapKeys.ACKID));
				
				statement.setString(2,(String) msgmap.get(MapKeys.MSGID));
				statement.setString(3,(String) msgmap.get(MapKeys.USERNAME));
				
				statement.setString(4, (String)msgmap.get(MapKeys.SENDERID_ORG));
				statement.setString(5, (String)msgmap.get(MapKeys.MOBILE));
				statement.setString(6, (String)msgmap.get(MapKeys.OPERATOR));
				statement.setString(7, (String)msgmap.get(MapKeys.CIRCLE));
				statement.setString(8, (String)msgmap.get(MapKeys.COUNTRYCODE));
				
				statement.setTimestamp(9,new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.RTIME))));
				if(msgmap.get(MapKeys.CARRIER_DONETIME)!=null){
				statement.setTimestamp(10,  new Timestamp(Long.parseLong((String)msgmap.get(MapKeys.CARRIER_DONETIME))));
				}else{
					statement.setTimestamp(10, null);

				}
				statement.setTimestamp(11,new Timestamp(System.currentTimeMillis()));

				statement.setString(12, (String)msgmap.get(MapKeys.STATUSID));
				statement.setString(13, (String)msgmap.get(MapKeys.DNPOSTSTATUS));
				statement.setString(14, (String)msgmap.get(MapKeys.DNMSG));

				statement.addBatch();
			}			
			statement.executeBatch();
			connection.commit();
			return true;
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

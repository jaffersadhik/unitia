package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.ClientDNTable;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.SlapCalculator;

public class ClientDNDAO {

	public static final String DATE_FORMAT = "yyMMddHHmmss";

	private static String SQL="";
	
	static{
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("insert into clientdn");
		sb.append("(");
		sb.append("ackid,username,");
		sb.append("rtime,ctime,itime,");
		sb.append("statusid,sms_latency_slap");
		sb.append(")values(");
		sb.append("?,?,");
		sb.append("?,?,?,");
		sb.append("?,?)");
		SQL=sb.toString();

	}
	public boolean insert(String tablename,List<Map<String,Object>> datalist){
		
		Connection connection =null;
		PreparedStatement statement=null;
		
		try{
		
			String sql=getSQL(tablename);
			
			if(!ClientDNTable.getInstance().isVailableTable(tablename)){
			
				ClientDNTable.getInstance().reload();
			}
			
			connection= BillingDBConnection.getInstance().getConnection();
		    connection.setAutoCommit(false);
	    	SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT);

			statement= connection.prepareStatement(sql);
			for(int i=0;i<datalist.size();i++){
				
				Map<String,Object> msgmap=datalist.get(i);
			

				statement.setString(1,(String) msgmap.get(MapKeys.ACKID));
				
				statement.setString(2,(String) msgmap.get(MapKeys.USERNAME));

				long rL=sdf.parse((String)msgmap.get(MapKeys.RTIME)).getTime();
				statement.setTimestamp(3,  new Timestamp(rL));

				long cL=sdf.parse((String)msgmap.get("ctime")).getTime();
				statement.setTimestamp(4,  new Timestamp(cL));
				statement.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
				statement.setString(6,(String) msgmap.get(MapKeys.STATUSID));
				statement.setInt(7, new SlapCalculator().getSlap(cL-rL));
			

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

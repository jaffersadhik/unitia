package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.SplitupTable;
import com.winnovature.unitia.util.account.SubmissionTable;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;

public class SplitupDAO {

	private static String SQL="";
	
	static{
		
		StringBuffer sb=new StringBuffer();
		
		sb.append("insert into {0}");
		sb.append("(msgid,fullmessage,udh)");
		sb.append("values(?,?,?)");

		SQL=sb.toString();

	}
	public boolean insert(String tablename,List<Map<String,Object>> datalist){
		
		Connection connection =null;
		PreparedStatement statement=null;
		
		try{
		
			String sql=getSQL(tablename);
			
			if(!SplitupTable.getInstance().isVailableTable(tablename)){
			
				SplitupTable.getInstance().reload();
			}
			
			connection= BillingDBConnection.getInstance().getConnection();
			
			connection.setAutoCommit(false);
			
			statement= connection.prepareStatement(sql);
			
			for(int i=0;i<datalist.size();i++){
				
				Map<String,Object> msgmap=datalist.get(i);
				
				statement.setString(1, msgmap.get(MapKeys.MSGID).toString());
				statement.setString(2, msgmap.get(MapKeys.FULLMSG).toString());
				statement.setString(3,(String) msgmap.get(MapKeys.UDH));
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

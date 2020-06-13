package com.winnovature.unitia.util.dao;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.MessageFormat;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.QueueDBConnection;
import com.winnovature.unitia.util.misc.MapKeys;


public class Insert {

	static String INSERT_SQL_PATTERN="insert into {0}(msgid,username,scheduletime,pstatus,data) values(?,?,?,?,?)";
	
	public boolean insert(String tablename, Map<String,String> requestObject) {
		
		if(!Table.getInstance().isAvailableTable(tablename)){
			
			Table.getInstance().addTable(tablename);
		}
		Connection connection=null;
		PreparedStatement statement=null;

		try {
			connection =QueueDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement(getQuery(tablename));
			statement.setString(1, requestObject.get(MapKeys.MSGID));
			statement.setString(2, requestObject.get(MapKeys.USERNAME));
			String scheduletime=requestObject.get(MapKeys.SCHEDULE_TIME);
			if(scheduletime==null||scheduletime.trim().length()<1){
				scheduletime="0";
			}
			statement.setString(3, scheduletime);
			statement.setString(4, "0");
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
            
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            
            oos.writeObject(requestObject);
            
            byte[] Bytes = bos.toByteArray();

			statement.setBytes(5, Bytes);
			statement.execute();
			
			return true;
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			
			Close.close(statement);
			Close.close(connection);

			
		}
		
		return false;
	}

	private String getQuery(String tablename) {

		String params[]= {tablename};
		
		return MessageFormat.format(INSERT_SQL_PATTERN, params);
	}
}

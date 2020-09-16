package com.winnovature.unitia.util.misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class KannelMessageStatus {

	private static KannelMessageStatus obj=null;
	Map<String,String> result=new HashMap<String,String>();

	private KannelMessageStatus(){
	
		init();
		reload();
	}
	
	public void reload() {
		

		Map<String,String> result=new HashMap<String,String>();
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select dr,statusid from kannel_message_status");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				result.put(resultset.getString("dr"),resultset.getString("statusid"));
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}

		this.result=result;
	}

	private void init() {

		Connection connection=null;
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			if(!table.isExsists(connection, "kannel_message_status")){
				
				if(table.create(connection, "create table kannel_message_status(statusid INT PRIMARY KEY AUTO_INCREMENT,dr varchar(650) not null,unique(dr) )", false)){
					
					table.insertKannelMessageStatus(connection,"1001","test");
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	}

	public static KannelMessageStatus getInstance(){
		
		if(obj==null){
			
			obj=new KannelMessageStatus();
		}
		
		return obj;
	}
	
	
	public String getErrorCode(String dr){
		
		return result.get(dr);
	}

	public void insert(String dr) {

		Connection connection=null;
		PreparedStatement statement=null;
		try{
			
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("insert into kannel_message_status(dr) values(?)");
			statement.setString(1, dr);
			statement.execute();
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		}finally{
			
			Close.close(statement);
			Close.close(connection);
		}
	}
}


package com.winnovature.unitia.util.misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class RedisInstance {

	private static RedisInstance obj=null;
	
	private RedisInstance(){
	
		init();
	}
	
	public static RedisInstance getInstance(){
		
		if(obj==null){
			
			obj=new RedisInstance();
		}
		
		return obj;
	}
	
	
	private void init() {
		
		Connection connection =null;
		
		try{
			
			connection= CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			if(!table.isExsists(connection, "instance_redis")){
			
				if(table.create(connection, "create table instance_redis(redisid varchar(100) primary key)", false)){
			
				insert(connection);
				}
			}
			
		}catch(Exception e){
			
		}finally{
			
			Close.close(connection);
		}
		
	}


	private void insert(Connection connection) {

		PreparedStatement statement=null;
		try{
			
			connection.setAutoCommit(false);
			statement=connection.prepareStatement("insert into instance_redis(redisid) values(?)");
			
			
			for(int i=1;i<2;i++){
				statement.setString(1, "redisqueue"+(i));
				
				statement.addBatch();
			}
			
			statement.executeBatch();
			connection.commit();
			
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(statement);
		}
	}


	public List<String> getRedisInstanceList(){
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		List<String> result=new ArrayList();
		try{

			connection= CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select redisid from instance_redis");
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				
				result.add(resultset.getString("redisid"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		return result;
	}

	


}

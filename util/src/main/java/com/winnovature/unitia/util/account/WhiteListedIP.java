package com.winnovature.unitia.util.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;


public class WhiteListedIP {

	private static final String CREATE_SQL = "create table customer_ip_pattern(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(16),ip_pattern varchar(15),itime timestamp default CURRENT_TIMESTAMP)";

	private static  WhiteListedIP obj=null;
	
	private Map<String,Set<String>> ippattern=new HashMap<String,Set<String>>();
	
	private WhiteListedIP(){
		
		init();
		reload();
	}
	
	public void reload() {
		
		 Map<String,Set<String>> _tmpPushAcc = loadIPPattern();
		if(_tmpPushAcc!=null&&_tmpPushAcc.size()>0){
			ippattern = _tmpPushAcc;
		}
		
	}

	
	private  Map<String,Set<String>> loadIPPattern() 
	{
		Map<String,Set<String>> _pushAccMap = new HashMap<String,Set<String>>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
				
			String sql = "select username,ip_pattern from customer_ip_pattern";
		
			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			resultSet = statement.executeQuery();
			
			while(resultSet.next()) 
			{
				String key = resultSet.getString("username").toLowerCase();
				
				Set<String> data=_pushAccMap.get(key);
				if(data==null){
					
					data=new HashSet<String>();
					_pushAccMap.put(key, data);
				}
				data.add(resultSet.getString("ip_pattern"));
				
			}
			
		} 
		catch(Exception e)
		{
		
			_pushAccMap = null;
		} 
		finally 
		{
			Close.close(resultSet);
			Close.close(statement);
			Close.close(connection);
		}//end of finally
		
		return _pushAccMap;
		
	}


	private void init() {
		
		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "customer_ip_pattern")){
		
			loadip(connection, table);

			}

		} catch (Exception e) {

		
			e.printStackTrace();

		} finally {

			Close.close(connection);
		}
		
	}
	
	
	private void loadip(Connection connection, TableExsists table) {

		if (!table.isExsists(connection, table.getTableName(CREATE_SQL))) {
			if (table.create(connection, CREATE_SQL, false)) {

				table.create(connection, "insert into customer_ip_pattern(username,ip_pattern) values ('unitia','.*')", false);
				table.create(connection, "insert into customer_ip_pattern(username,ip_pattern) values ('testuser','.*')", false);
	
			}
		}
	}


	public static WhiteListedIP getInstance(){
		
		if(obj==null){
			
			obj=new WhiteListedIP();
		}
		
		return obj;
	}
	
	public boolean isWhiteListedIP(String username,String ip){
		
		
		if(ippattern.containsKey(username)){
			
			Set<String> ippatternlist=ippattern.get(username);
		
			Iterator itr=ippatternlist.iterator();
			while(itr.hasNext()){
			
				String ipPatternstring=itr.next().toString();
					
					if(Pattern.compile(ipPatternstring, Pattern.CASE_INSENSITIVE).matcher(ip).matches())
					{
						
						return true;
					}
				
				}
			
			}
		
		return false;
		
	}
}

package com.winnovature.unitia.util.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class MissedCallForward {

	private static final String SQL = "create table missedcall_forward(id INT PRIMARY KEY AUTO_INCREMENT,vmn decimal(15,0) unique key,forwardurl varchar(750))";

	private static MissedCallForward obj=null;
	
	
	private Map<String,String> vmnforwardurl=new HashMap<String,String>();
	
	private MissedCallForward(){
		
		init();
		
		reload();
	}
	
	private void reload() {
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultset=null;
		Map<String,String> temp=new HashMap<String,String>();
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select * from missedcall_forward");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				String vmn=resultset.getString("vmn");

				String forwardurl=resultset.getString("forwardurl");
				temp.put(vmn, forwardurl);
			}
		} catch (Exception e) {

			temp=null;
			e.printStackTrace();

		} finally {

			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		if(temp!=null){
			
			vmnforwardurl=temp;
		}
	}

	private void init() {



		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "missedcall_forward")){
				
					if (table.create(connection, SQL, false)) {

						table.create(connection, "insert into missedcall_forward(vmn,forwardurl) values('919487660738','http://http1:8080/api/clientdn?')", false);
					}
			
			}
			
			

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	
		
	}

	
	public String getUrl(String vmn){
		
		return vmnforwardurl.get(vmn);
		
	}
	public static MissedCallForward getInstance(){
		
		if(obj==null){
			
			obj=new MissedCallForward();
		}
		
		return obj;
	}
}

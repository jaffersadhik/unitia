package com.winnovature.unitia.util.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class JVMUniqueId {
	
	public JVMUniqueId(){
		
	}

	public String getTransId() {

		String transid=null;
		Connection connection = null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		Connection connection1 = null;
		PreparedStatement statement1=null;
		ResultSet resultset1=null;
		try {

			connection = CoreDBConnection.getInstance().getConnection();
			checkTableAvailabilty();
			connection.setAutoCommit(false);
			statement=connection.prepareStatement("select * from interface_jvm_uniqueid_lock for UPDATE");
		    resultset=statement.executeQuery();
		    if(resultset.next()){
		    
		    	connection1=CoreDBConnection.getInstance().getConnection();
		    	statement1=connection1.prepareStatement("select min(id) transid from interface_jvm_uniqueid where updateid<?");
		    
		    	long updateid=System.currentTimeMillis()-(5*60*1000);
		    	
		    	statement1.setString(1, ""+updateid);
		
		    	resultset1=statement1.executeQuery();
		    	
		    	if(resultset1.next()){
		    		
		    		transid=resultset1.getString("transid");
		    	}
		    }
			
		} catch (Exception e) {

			e.printStackTrace();
			
		} finally {
		
			Close.close(resultset1);
			Close.close(resultset);
			Close.close(statement1);
			Close.close(statement);			
			Close.close(connection1);
			Close.close(connection);
		}

		if(transid!=null){
			
			upateJVMId(transid);
		}
	
		return transid;
		

	}

	private void checkTableAvailabilty() {
		
		Connection connection = null;
		try {
		
			connection=CoreDBConnection.getInstance().getConnection();
		TableExsists table=new TableExsists();
		
		if(!table.isExsists(connection, "interface_jvm_uniqueid")){
			
			table.create(connection, "create table interface_jvm_uniqueid(id INT(3),updateid INT(13) default 0)", true);
			table.insertjvmid(connection);
		}
	
		} catch (Exception e) {


			
		} finally {
		
			Close.close(connection);
		}

	}

	public void upateJVMId(String id){

		Connection connection = null;
		PreparedStatement statement=null;

		try {
		
			connection=CoreDBConnection.getInstance().getConnection(); 
			statement=connection.prepareStatement("update interface_jvm_uniqueid set updateid=? where id=?");
			statement.setString(1, ""+System.currentTimeMillis());
			statement.setString(2, id);
						statement.execute();
		} catch (Exception e) {


			
		} finally {
		
			Close.close(statement);
			Close.close(connection);
		}


	}
}

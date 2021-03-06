package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.winnovature.unitia.util.misc.Prop;

public class CoreDBConnection {

	private static CoreDBConnection obj = null;

	private static BasicDataSource datasource = null;

	private CoreDBConnection() {

		while(!isAvailable()){
    		try {
				Thread.sleep(10L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		createDataSource();
	}


	public static boolean isAvailable(){
		Connection con=null;
		try{
		Class.forName("com.mysql.jdbc.Driver"); 
		Properties prop=Prop.getInstance().getCoreDBProp();
		con=DriverManager.getConnection(  
				prop.getProperty("url"),prop.getProperty("username"),prop.getProperty("password"));
	    return !con.isClosed();
		}catch(Exception e){
			
		}finally{
			
			Close.close(con);
		}
		return false;
	}

	
	public static void main(String args[]) {

		TableExsists table = new TableExsists();

	}

	
	private void createDataSource() {

		if (datasource == null) {
			try {
				datasource = (BasicDataSource) BasicDataSourceFactory
						.createDataSource(Prop.getInstance().getCoreDBProp());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void reload() {

	}

	public static CoreDBConnection getInstance() {

		if (obj == null) {

			obj = new CoreDBConnection();

		}

		return obj;
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

}

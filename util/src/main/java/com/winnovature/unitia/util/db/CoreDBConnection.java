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


	private CoreDBConnection() {

	}


	
	public static CoreDBConnection getInstance() {

		if (obj == null) {

			obj = new CoreDBConnection();

		}

		return obj;
	}

	public Connection getConnection() throws SQLException {
		try{
		Class.forName("com.mysql.jdbc.Driver");

		Properties prop=Prop.getInstance().getCoreDBProp();
		return DriverManager.getConnection
				(prop.getProperty("url"),prop.getProperty("username"),prop.getProperty("password"));

		}catch(Exception e){
			e.printStackTrace();
			
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		return null;
		}

}

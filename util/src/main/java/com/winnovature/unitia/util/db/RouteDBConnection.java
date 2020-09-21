package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.winnovature.unitia.util.misc.Prop;

public class RouteDBConnection {

	private static RouteDBConnection obj = null;


	private RouteDBConnection() {
	}


	

	


	public void reload() {

	}

	public static RouteDBConnection getInstance() {

		if (obj == null) {

			obj = new RouteDBConnection();

		}

		return obj;
	}

	public Connection getConnection() throws SQLException {
		try{
		Class.forName("com.mysql.jdbc.Driver");

		Properties prop=Prop.getInstance().getRouteDBProp();
		return DriverManager.getConnection
				(prop.getProperty("url"),prop.getProperty("username"),prop.getProperty("password"));

		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		return null;

	}

}

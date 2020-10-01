package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.winnovature.unitia.util.misc.Prop;

public class KannelStoreDBConnection {


	private BasicDataSource datasource = null;

	Properties prop=null;
	
	public KannelStoreDBConnection(Properties prop) {

		this.prop=prop;

		createDataSource();
	
	}




	
	private void createDataSource() {

		if (datasource == null) {
			try {
				datasource = (BasicDataSource) BasicDataSourceFactory
						.createDataSource(prop);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

}

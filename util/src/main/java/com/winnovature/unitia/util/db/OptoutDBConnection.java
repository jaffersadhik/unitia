package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.winnovature.unitia.util.misc.Prop;

public class OptoutDBConnection {

	private static OptoutDBConnection obj = null;

	private static BasicDataSource datasource = null;

	private OptoutDBConnection() {

		
	}


	public static OptoutDBConnection getInstance() {

		if (obj == null) {

			obj = new OptoutDBConnection();

		}

		return obj;
	}

	public Connection getConnection() throws SQLException {
		return CoreDBConnection.getInstance().getConnection();
	}

}

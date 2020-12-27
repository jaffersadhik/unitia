package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.winnovature.unitia.util.misc.Prop;

public class DNDDBConnection {

	private static DNDDBConnection obj = null;

	private static BasicDataSource datasource = null;

	private DNDDBConnection() {

	}


		public static DNDDBConnection getInstance() {

		if (obj == null) {

			obj = new DNDDBConnection();

		}

		return obj;
	}

	public Connection getConnection() throws SQLException {
		return BillingDBConnection.getInstance().getConnection();
	}

}

package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.winnovature.unitia.util.misc.Prop;

public class DuplicateDBConnection {

	private static DuplicateDBConnection obj = null;

	private static BasicDataSource datasource = null;

	private DuplicateDBConnection() {

	}


	public static DuplicateDBConnection getInstance() {

		if (obj == null) {

			obj = new DuplicateDBConnection();

		}

		return obj;
	}

	public Connection getConnection() throws SQLException {
		return BillingDBConnection.getInstance().getConnection();
	}

}

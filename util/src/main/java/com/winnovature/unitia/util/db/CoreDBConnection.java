package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.winnovature.unitia.util.misc.Prop;

public class CoreDBConnection {

	private static CoreDBConnection obj = null;

	private static BasicDataSource datasource = null;

	private CoreDBConnection() {

		createDataSource();
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

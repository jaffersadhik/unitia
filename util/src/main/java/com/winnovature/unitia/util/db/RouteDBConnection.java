package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.winnovature.unitia.util.misc.Prop;

public class RouteDBConnection {

	private static RouteDBConnection obj = null;

	private static BasicDataSource datasource = null;

	private RouteDBConnection() {

		createDataSource();
	}


	

	
	public static void main(String args[]) {

		TableExsists table = new TableExsists();

	}

	
	private void createDataSource() {

		if (datasource == null) {
			try {
				datasource = (BasicDataSource) BasicDataSourceFactory
						.createDataSource(Prop.getInstance().getRouteDBProp());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

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
		return datasource.getConnection();
	}

}

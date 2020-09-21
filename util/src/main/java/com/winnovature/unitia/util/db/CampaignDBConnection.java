package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

public class CampaignDBConnection {

	private static CampaignDBConnection obj = null;

	private static BasicDataSource datasource = null;

	private CampaignDBConnection() {

		
	}


	

	

	public static CampaignDBConnection getInstance() {

		if (obj == null) {

			obj = new CampaignDBConnection();

		}

		return obj;
	}

	public Connection getConnection() throws SQLException {
		return BillingDBConnection.getInstance().getConnection();
	}

}

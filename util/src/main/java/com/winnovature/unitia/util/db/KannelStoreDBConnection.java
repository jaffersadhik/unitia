package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

public class KannelStoreDBConnection {

	static Map<String,KannelStoreDBConnection> map=new HashMap<String,KannelStoreDBConnection>();

	private BasicDataSource datasource = null;

	Properties prop=null;
	
	private KannelStoreDBConnection(Properties prop) {

		this.prop=prop;
	
		createDataSource();
	}



	public static KannelStoreDBConnection getInstance(String kannelid,Properties prop){
		
		if(!map.containsKey(kannelid)){
			
			map.put(kannelid, new KannelStoreDBConnection(prop));
		}
		
		return map.get(kannelid);
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

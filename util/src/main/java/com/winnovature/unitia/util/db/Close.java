package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Close {
	

public static void close(Connection connection){
	
		
	try {
		if (connection != null) {
		
			if(!connection.getAutoCommit()) {
			connection.commit();
			
			}
			
			connection.setAutoCommit(true);
			connection.close();
			}
			
		
	} catch (Exception e) { 
		System.err.println(" connection closing error ");
		e.printStackTrace();
	}

}

public static void close(ResultSet resultset) {

	
	try {
		if (resultset != null)
			resultset.close();
	} catch (Exception e) { 
		System.err.println(" resultset closing error ");
		e.printStackTrace();
	}
}

public static void close(PreparedStatement statement) {

	try {
		if (statement != null)
			statement.close();
	} catch (Exception e) { 
		System.err.println(" PreparedStatement closing error ");
		e.printStackTrace();
	}
}
	
}

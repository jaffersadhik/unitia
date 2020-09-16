package com.winnovature.unitia.util.db;


import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;




public class BillingDBConnection 
{
    private static BillingDBConnection    obj        = null;
    
    private static BasicDataSource datasource = null;
    
    private BillingDBConnection() 
    {
        
      
    }
    



    public static BillingDBConnection getInstance()
    {
        
        if (obj == null)
        {
            
            obj = new BillingDBConnection();
			
        }
        return obj;
    }
    
    public Connection getConnection() throws SQLException
    {
    	return CoreDBConnection.getInstance().getConnection();
    }


    
  
}

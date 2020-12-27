package com.winnovature.unitia.util.db;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;




public class QueueDBConnection 
{

	private static QueueDBConnection    obj        = null;
    
    private static BasicDataSource datasource = null;
    
    private List<String> dnportlist=new ArrayList<String>();
  
    private QueueDBConnection() 
    {
     
    }
    
    

	
	public static QueueDBConnection getInstance()
    {
        
        if (obj == null)
        {
            
            obj = new QueueDBConnection();
			
        }
        return obj;
    }
    
    public Connection getConnection() throws SQLException
    {
    	return KannelDBConnection.getInstance().getConnection();
    }



  
}

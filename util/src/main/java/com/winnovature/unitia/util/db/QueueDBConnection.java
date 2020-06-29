package com.winnovature.unitia.util.db;


import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import com.winnovature.unitia.util.misc.Prop;
import com.winnovature.unitia.util.misc.RoundRobinTon;




public class QueueDBConnection 
{

	private static QueueDBConnection    obj        = null;
    
    private static BasicDataSource datasource = null;
    
    private Set<String> transtable=new HashSet<String>();
    private Set<String> promotable=new HashSet<String>();
    private Set<String> otptable=new HashSet<String>();
    private Set<String> scheduletable=new HashSet<String>();
    private List<String> dnportlist=new ArrayList<String>();
  
    private List<String> transtablelist=new ArrayList<String>();
    private List<String> promotablelist=new ArrayList<String>();
    private List<String> otptablelist=new ArrayList<String>();
    private List<String> scheduletablelist=new ArrayList<String>();
    private List<String> dnportlistlist=new ArrayList<String>();
  
    private QueueDBConnection() 
    {
        
       createDataSource();        
        reload();
    }
    
    
    private static void createDataSource() {
    	
    	 if (datasource == null)
         {
             try {
				datasource = (BasicDataSource) BasicDataSourceFactory.createDataSource(Prop.getInstance().getQueueDBProp());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             
             
         }
		
	}


	public void reload() {
    	
		Connection connection =null;
		try {
			connection=datasource.getConnection();
			TableExsists table=new TableExsists();

			Map<String,String> tablenamemap=table.getPoolTableName();
		
			
		}catch(Exception e) {
			
			e.printStackTrace();
			
		}finally {
			
			Close.close(connection);
		}
    

    }
	
	public static QueueDBConnection getInstance()
    {
        
        if (obj == null)
        {
            
            obj = new QueueDBConnection();
			
        }
        createDataSource();
        return obj;
    }
    
    public Connection getConnection() throws SQLException
    {
    	return datasource.getConnection();
    }


	public Object getDNPort() {
		return dnportlist.get(RoundRobinTon.getInstance().getCurrentIndex("dnportlist", dnportlist.size()));

	}
	
	
	

	public void watchQueue() {
		
	}

  
}

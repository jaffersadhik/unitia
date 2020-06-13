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
    private static final String RR_TABLE = "create table {0}(ackid numeric(30,0) primary key,msgid numeric(30,0),rtime numeric(13),username varchar(16),userid numeric(10,0),mobile numeric(21,0),senderid varchar(15),message TEXT,scheduletime numeric(13,0),scheduletype varchar(10),udh varchar(20),port numeric(10,0),msgtype varchar(3),featurecd varchar(3),dlr varchar(1),msgclass numeric(1),esmclass numeric(3),dcs numeric(3),priority numeric(3),param1 varchar(100),param2 varchar(100),param3 varchar(100),param4 varchar(100),param5 varchar(100),uploaderid varchar(16),campaignid varchar(50),fileid varchar(50),interfaceid varchar(50),msgsrc varchar(10),processstatus numeric(1),INDEX(scheduletime,processstatus))";

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
	
	

	private String getQuery(String tablename) {

		String [] params= {tablename};
		
		return MessageFormat.format(RR_TABLE, params);
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

package com.winnovature.unitia.util.routing;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.SQLQuery;
import com.winnovature.unitia.util.db.TableExsists;



public class Kannel
{
    private static Kannel    obj        = null;
    
    private Map<String, Map<String, String>> kannel=new HashMap<String,Map<String,String>>();
    
    private Kannel()
    {
        
    	addMaster();
    }
    
	private void addMaster() {
		
		Connection connection =null;
		try {
			connection=RouteDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "kannel")) {
			addMaster(connection,table);
			}
			
			loadData(connection, table);
		}catch(Exception e) {
			
			e.printStackTrace();
			
		}finally {
			
			Close.close(connection);
		}
		
	}

	private void addMaster(Connection connection,TableExsists table) {

				
			loadSMSCID(connection,table);
	}

	private void loadSMSCID(Connection connection, TableExsists table) {

		if(table.create(connection, SQLQuery.CREATE_SMSCID_TABLE, false)) {

			table.insertKannel(connection,"apps","127.0.0.1",8080,"4");
			table.insertKannel(connection,"reapps","127.0.0.1",8080,"4");
			table.insertKannel(connection,"retry","127.0.0.1",1111,"5");

		}
	}

	private void loadData(Connection connection,TableExsists table) {

    	reloadSMSCID(connection,table);
	}
	

	private void reloadSMSCID(Connection connection, TableExsists table) {
		Map<String, Map<String, String>> temp_kannel=table.getKannel(connection);
		if(temp_kannel!=null) {
			kannel=temp_kannel;
		}
		
	}


	public void reload() {
	
		Connection connection=null;
		
		try {
			connection=RouteDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(kannel.isEmpty()) {
				
				addMaster(connection, table);
			}
			loadData(connection,table);
		}catch(Exception e) {
			
		}finally {
			
			Close.close(connection);
		}
				
				
	}
	public static Kannel getInstance() {
        
        if (obj == null)
        {
            
            obj = new Kannel();
        }
        
        return obj;
    }
    
    
	public Map<String, String> getKannelInfo(String smscid) {
		
		
		return kannel.get(smscid);
	}

}

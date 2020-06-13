package com.winnovature.unitia.util.routing;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.SQLQuery;
import com.winnovature.unitia.util.db.TableExsists;



public class SplitGroup
{
    private static SplitGroup    obj        = null;
    
    Map<String,  Map<String,Map<String, String>>> splitgroup=new HashMap<String,  Map<String,Map<String, String>>>();
    private SplitGroup()
    
    {
        
        
       addMaster();
    }
    
	private void addMaster() {
		
		Connection connection =null;
		try {
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "splitgroup")) {
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

				
	    	loadSplitGroup(connection,table);

	}

	private void loadSplitGroup(Connection connection, TableExsists table) {

		if(table.create(connection, SQLQuery.CREATE_SPLITGROUP_TABLE, false)) {
			
			table.insertSplitGroup(connection,"default_group","PM",153,160);
			table.insertSplitGroup(connection,"default_group","UC",153,160);

		}
	}

	
	private void loadData(Connection connection,TableExsists table) {

    	reloadSplitGroup(connection,table);

	}
	
		private void reloadSplitGroup(Connection connection, TableExsists table) {

		Map<String,  Map<String,Map<String, String>>> temp_splitgroup=table.getSplitGroup(connection);
		if(temp_splitgroup!=null) {
			splitgroup=temp_splitgroup;
		}
	}

	public void reload() {
	
		Connection connection=null;
		
		try {
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(splitgroup.isEmpty()) {
				
				addMaster(connection, table);
			}
			loadData(connection,table);
		}catch(Exception e) {
			
		}finally {
			
			Close.close(connection);
		}
				
				
	}

	public static SplitGroup getInstance() {
        
        if (obj == null)
        {
            
            obj = new SplitGroup();
        }
        
        return obj;
    }
    
	public Map<String,Map<String, Map<String, Map<String, String>>>> getSplitgrouptoString() {
		Map<String,Map<String, Map<String, Map<String, String>>>> result=new HashMap<String,Map<String, Map<String, Map<String, String>>>>();
		result.put("splitgroup", splitgroup);
		return result;
	}

	public Map<String, Map<String, String>> getSplitGroup(String splitgroupname) {
		
		return splitgroup.get(splitgroupname);
	}
}

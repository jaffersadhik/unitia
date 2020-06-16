package com.winnovature.unitia.util.routing;


import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.SQLQuery;
import com.winnovature.unitia.util.db.TableExsists;




public class RouteGroup
{
    private static RouteGroup    obj        = null;
    
	private Map<String, List<String>> routegroup=new HashMap<String,List<String>>();

    
    private RouteGroup()
    {
       addMaster();
    }
    
	private void addMaster() {
		
		Connection connection =null;
		try {
			connection=RouteDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "loadbalancer_http")) {
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

				
	    	loadRouteGroup(connection,table);
	}
	private void loadRouteGroup(Connection connection, TableExsists table) {

		if(table.create(connection, SQLQuery.CREATE_ROUTEGROUP_TABLE, false)) {
			
			table.insertRouteGroup(connection,"retry_group","retry");
			table.insertRouteGroup(connection,"appa_group","apps");
		}
	}

	private void loadData(Connection connection,TableExsists table) {

		reloadRouteGroup(connection,table);
    

	}
	
	private void reloadRouteGroup(Connection connection, TableExsists table) {

		Map<String, List<String>> temp_routegroup=table.getRouteGroup(connection);
		if(temp_routegroup!=null) {
			routegroup =temp_routegroup;
		}
		
	}


	public void reload() {
	
		Connection connection=null;
		
		try {
			connection=RouteDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(routegroup.isEmpty()) {
				
				addMaster(connection, table);
			}
			loadData(connection,table);
		}catch(Exception e) {
			
		}finally {
			
			Close.close(connection);
		}
				
				
	}
	public static RouteGroup getInstance() {
        
        if (obj == null)
        {
            
            obj = new RouteGroup();
        }
        
        return obj;
    }
    
	public Map<String,Map<String, List<String>>> getRoutegrouptoString() {
		Map<String,Map<String, List<String>>> result=new HashMap<String,Map<String, List<String>>>();
		result.put("routegroup", routegroup);
		return result;
	}


	public List<String> getSmscidList(String routegroupname) {
		
		return routegroup.get(routegroupname);
	}

}

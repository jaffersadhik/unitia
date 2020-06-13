package com.winnovature.unitia.util.routing;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.SQLQuery;
import com.winnovature.unitia.util.db.TableExsists;



public class Route
{
    private static Route    obj        = null;
    
   
	private Map<String, String> route=new HashMap<String, String> ();

	private Route()
    {
        
        addMaster();
    }
    
	private void addMaster() {
		
		Connection connection =null;
		try {
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "route")) {
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

				
		loadRoute(connection,table);
	
	}

	private void loadRoute(Connection connection, TableExsists table) {

		if(table.create(connection, SQLQuery.CREATE_ROUTE_TABLE, false)) {
		
			table.insertRoute(connection,"apps_group", "","","","","");
		}
	}

	private void loadData(Connection connection,TableExsists table) {

			reloadRoute(connection,table);

	}
	
	private void reloadRoute(Connection connection, TableExsists table) {
		
		Map<String,String> temp_route=table.getRoute(connection);
		if(temp_route!=null) {
			route=temp_route;
		}
	}

	
	
	public void reload() {
	
		Connection connection=null;
		
		try {
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(route.isEmpty()) {
				
				addMaster(connection, table);
			}
			loadData(connection,table);
		}catch(Exception e) {
			
		}finally {
			
			Close.close(connection);
		}
				
				
	}
	public static Route getInstance() {
        
        if (obj == null)
        {
            
            obj = new Route();
        }
        
        return obj;
    }
    

	public String getRouteGroup(String key){
		
		return route.get(key);
	}
	public Map<String,Map<String, String>> getRoutetoString() {
		Map<String,Map<String, String>> result=new HashMap<String,Map<String, String>>();
		result.put("route", route);
		return result;
	}

}

package com.winnovature.unitia.util.routing;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.SQLQuery;
import com.winnovature.unitia.util.db.TableExsists;



public class Route
{
    private static Route    obj        = null;
    
   
	private Map<String, Map<String, String>> route=new HashMap<String, Map<String, String>> ();

	private Route()
    {
        
        addMaster();
    }
    
	private void addMaster() {
		
		Connection connection =null;
		try {
			connection=RouteDBConnection.getInstance().getConnection();
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
		
			table.insertRoute(connection,"apps_group","apps_group", "","","","","");
		}
	}

	private void loadData(Connection connection,TableExsists table) {

			reloadRoute(connection,table);

	}
	
	private void reloadRoute(Connection connection, TableExsists table) {
		
		Map<String,Map<String, String>> temp_route=table.getRoute(connection);
		if(temp_route!=null) {
			route=temp_route;
		}
	}

	
	
	public void reload() {
	
		Connection connection=null;
		
		try {
			connection=RouteDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
		
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
    

	public String getRouteGroup(String key,String routeclass){
		
		if(route.containsKey(key)){
			
			if(routeclass.equals("1")){
				route.get(key).get("trans");
			}else{
				route.get(key).get("promo");

			}
			}
		
		return null;
	}

	public String getRoute(){
		
		return route.toString();
	}
}

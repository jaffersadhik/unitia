package com.winnovature.unitia.util.routing;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.SQLQuery;
import com.winnovature.unitia.util.db.TableExsists;



public class InternationalRoute
{
    private static InternationalRoute    obj        = null;
    
   
	private Map<String, String> route=new HashMap<String, String> ();

	private InternationalRoute()
    {
        
        addMaster();
    }
    
	private void addMaster() {
		
		Connection connection =null;
		try {
			connection=RouteDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "international_route")) {
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

		table.create(connection, SQLQuery.CREATE_INTL_ROUTE_TABLE, false);
	}

	private void loadData(Connection connection,TableExsists table) {

			reloadRoute(connection,table);

	}
	
	private void reloadRoute(Connection connection, TableExsists table) {
		
		Map<String,String> temp_route=table.getIntlRoute(connection);
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
	public static InternationalRoute getInstance() {
        
        if (obj == null)
        {
            
            obj = new InternationalRoute();
        }
        
        return obj;
    }
    

	public String getRouteGroup(String key){
		
		return route.get(key);
	}

}

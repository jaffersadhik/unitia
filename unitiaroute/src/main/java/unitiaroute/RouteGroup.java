package unitiaroute;


import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.SQLQuery;
import com.winnovature.unitia.util.db.TableExsists;




public class RouteGroup
{
    private static RouteGroup    obj        = null;
    
	private Map<String, List<String>> routegroup=new HashMap<String,List<String>>();

    
    private RouteGroup()
    {
    }
    

	private void addMaster(Connection connection,TableExsists table) {

				
	    	loadRouteGroup(connection,table);
	}
	private void loadRouteGroup(Connection connection, TableExsists table) {

		if(table.create(connection, SQLQuery.CREATE_ROUTEGROUP_TABLE, false)) {
			
			table.insertRouteGroup(connection,"retry_group","retry");
			table.insertRouteGroup(connection,"apps_group","apps");
			table.insertRouteGroup(connection,"unitia_group","unitia");

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

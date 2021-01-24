package kannelconnector;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.SQLQuery;
import com.winnovature.unitia.util.db.TableExsists;




public class DomesticCredit
{
	public static String CONJUNCTION="~";
	
	public static String ROUTE_TRANS="trans";

	public static String ROUTE_PROMO="promo";
	
	public static String NULL="null";
	
	private static Map<String, Map<String, String>> credit=new HashMap<String, Map<String, String>> ();

    private static DomesticCredit    obj        = null;
    
   

	private DomesticCredit()
    {
        
        addMaster();
    
        reload();
    }
    
	private void addMaster() {
		
		Connection connection =null;
		try {
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "credit_domestic")) {
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

		if(table.create(connection, SQLQuery.CREATE_CREDIT_DOMESTIC_TABLE, false)) {
		
			table.insertCredit(connection,1.00,1.00, null,null,null,null,null);
		}
	}

	private void loadData(Connection connection,TableExsists table) {

			reloadRoute(connection,table);

	}
	
	private void reloadRoute(Connection connection, TableExsists table) {
		
		Map<String,Map<String, String>> temp_route=table.getCreditDomestic(connection);
		if(temp_route!=null) {
			credit=temp_route;
		}
	}

	
	
	public void reload() {
	
		Connection connection=null;
		
		try {
			connection=CoreDBConnection.getInstance().getConnection();
			
			TableExsists table=new TableExsists();
		
			loadData(connection,table);
		}catch(Exception e) {
			
		}finally {
			
			Close.close(connection);
		}
				
				
	}
	public static DomesticCredit getInstance() {
        
        if (obj == null)
        {
            
            obj = new DomesticCredit();
        }
        
        return obj;
    }
    

	public String getCredit(String key,String routeclass){
		
		if(credit.containsKey(key)){
			
			if(routeclass.equals("1")){
				
				return (credit.get(key)).get(ROUTE_TRANS);
				
			}else{
				
				return (credit.get(key)).get(ROUTE_PROMO);
				

			}
			}
		
		return null;
	}

	public String getRoute(){
		
		return credit.toString();
	}

	public String getRouteGroup(String key) {
		
		return credit.get(key).toString();
	}
}

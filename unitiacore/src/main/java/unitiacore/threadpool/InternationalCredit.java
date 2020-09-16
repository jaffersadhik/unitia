package unitiacore.threadpool;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.SQLQuery;
import com.winnovature.unitia.util.db.TableExsists;




public class InternationalCredit
{
	public static String CONJUNCTION="~";
	

	
	public static String NULL="null";
	
	private static Map<String, String> credit=new HashMap<String, String> ();

    private static InternationalCredit    obj        = null;
    
   

	private InternationalCredit()
    {
        
        addMaster();
    
        reload();
    }
    
	private void addMaster() {
		
		Connection connection =null;
		try {
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "credit_international")) {
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

		if(table.create(connection, SQLQuery.CREATE_CREDIT_INTERNATIONAL_TABLE, false)) {
		
			table.insertCreditInternational(connection,1.00, null,null,null,null);
		}
	}

	private void loadData(Connection connection,TableExsists table) {

			reloadRoute(connection,table);

	}
	
	private void reloadRoute(Connection connection, TableExsists table) {
		
		Map<String, String> temp_route=table.getCreditInternational(connection);
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
	public static InternationalCredit getInstance() {
        
        if (obj == null)
        {
            
            obj = new InternationalCredit();
        }
        
        return obj;
    }
    

	public String getCredit(String key){
		
		return credit.get(key);
	
	}

	
}

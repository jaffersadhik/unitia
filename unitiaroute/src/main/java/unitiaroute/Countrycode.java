package unitiaroute;


import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.SQLQuery;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.Prop;



public class Countrycode
{
    private static Countrycode    obj        = null;
    
   
	private Map<String, String> countrycodemap=new HashMap<String, String> ();

	private Countrycode()
    {
        
        addMaster();
    }
    
	private void addMaster() {
		
		Connection connection =null;
		try {
			connection=RouteDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			if(!table.isExsists(connection, "countrycode")) {
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

				
		loadCountrycode(connection,table);
	
	}

	private void loadCountrycode(Connection connection, TableExsists table) {

		if(table.create(connection,"create table countrycode(countrycode varchar(10) not null,countryname varchar(50) not null)", false)) {
		
			add(connection,table,Prop.getInstance().getCountryCode());
		}
	}

	
	private void add(Connection connection, TableExsists table,List<String> data) {

		for(int i=0;i<data.size();i++) {
			try {
			String str=data.get(i);
			
			StringTokenizer st=new StringTokenizer(str,"~");
			
			String countryname=st.nextToken().toString().trim();
			String countrycode="";
			try {
				countrycode=st.nextToken().toString().trim();
			}catch(Exception ignore) {
				
			}
			
					
			table.insertCountryCode(connection, countryname, countrycode);
			}catch(Exception e) {
				
			}
		}
	}


	private void loadData(Connection connection,TableExsists table) {

			reloadRoute(connection,table);

	}
	
	private void reloadRoute(Connection connection, TableExsists table) {
		
		Map<String,String> temp_route=table.getCountrycode(connection);
		if(temp_route!=null) {
			countrycodemap=temp_route;
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
	public static Countrycode getInstance() {
        
        if (obj == null)
        {
            
            obj = new Countrycode();
        }
        
        return obj;
    }
    
	public boolean isExsistingCountryCode(String countrycode){
		
		return countrycodemap.containsKey(countrycode);
	}

	public String getCountryName(String countrycode){
		
		if(countrycodemap.containsKey(countrycode)){
		
			return countrycodemap.get(countrycode);
		}else{
			
			return null;
		}
		
	}
	

}

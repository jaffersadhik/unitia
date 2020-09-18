package unitiaroute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class MobileRouting {

	private static MobileRouting obj=null;
	
	private static Map<String, Map<String, String>> mobilerouting=new HashMap<String, Map<String, String>> ();

	private boolean isTableAvailable=false;
	
	private MobileRouting(){
	
		reload();
	}
	
	public static MobileRouting getInstance(){
		
		if(obj==null){
			
			obj=new MobileRouting();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,Map<String,String>> mobilemap=new HashMap<String,Map<String,String>>();
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =RouteDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "mobile_routing")){
					
					if(table.create(connection, " create table mobile_routing(mobile numeric(21,0) primary key,routegroup_trans varchar(50),routegroup_promo varchar(50))", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select mobile,routegroup_trans,routegroup_promo from mobile_routing");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				 Map<String, String> data=new HashMap<String, String>();
				 data.put(Route.ROUTE_TRANS,resultset.getString("routegroup_trans").trim());
				 data.put(Route.ROUTE_PROMO,resultset.getString("routegroup_promo").trim());

				mobilemap.put(resultset.getString("mobile"),data);
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		if(mobilemap!=null){
			
			this.mobilerouting=mobilemap;
		}
		
	}
	
	
	public String getRouteGroup(String mobile,String routeclass){
		
		if(mobilerouting.containsKey(mobile)){
			
			if(routeclass.equals("1")){
				
				return (mobilerouting.get(mobile)).get(Route.ROUTE_TRANS);
				
			}else{
				
				return (mobilerouting.get(mobile)).get(Route.ROUTE_PROMO);
				

			}
			
		}else{
			
			return null;
		}
	}
	
	
	
}

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

public class SenderidRouting {

	private static SenderidRouting obj=null;
	
	private Map<String,String> senderidrouting=new HashMap<String,String>();
	
	private boolean isTableAvailable=false;
	
	private SenderidRouting(){
	
		reload();
	}
	
	public static SenderidRouting getInstance(){
		
		if(obj==null){
			
			obj=new SenderidRouting();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,String> senderidrouting=new HashMap<String,String>();
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =RouteDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "senderid_routing")){
					
					if(table.create(connection, " create table senderid_routing(id INT PRIMARY KEY AUTO_INCREMENT ,operator varchar(2),circle varchar(2),senderid varchar(15) , routegroup varchar(25))", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select operator,circle,senderid,routegroup from senderid_routing");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
			
				String operator =resultset.getString("operator");
				String circle =resultset.getString("circle");
				String senderid =resultset.getString("senderid");
				String routegroup =resultset.getString("routegroup");

				if(operator==null||operator.trim().length()<1){
					operator=Route.NULL;
				}
				if(circle==null||circle.trim().length()<1){
					circle=Route.NULL;
				}
				
				String key=Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION+senderid+Route.CONJUNCTION;
				
				senderidrouting.put(key,routegroup);
				
				
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		this.senderidrouting=senderidrouting;
	}
	
	
	public String getRouteGroup(String key){
		
			return senderidrouting.get(key);

	}
	
	
	
}

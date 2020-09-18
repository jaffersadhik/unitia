package unitiaroute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.MapKeys;

public class ReRouting {

	private static ReRouting obj=null;
	
	private Map<String,Map<String,String>> rerouting=new HashMap<String,Map<String,String>>();
	
	private boolean isTableAvailable=false;
	
	private ReRouting(){
	
		reload();
	}
	
	public static ReRouting getInstance(){
		
		if(obj==null){
			
			obj=new ReRouting();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,Map<String,String>> reroutemap=new HashMap<String,Map<String,String>>();
		
		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =RouteDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "rerouting")){
					
					if(table.create(connection, " create table rerouting(superadmin varchar(21),admin varchar(21),username varchar(21),smscid varchar(10),reroute_smscid varchar(10)) "
							+ "", false)){
					
						table.insertReroute(connection,"apps","reapps");
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select superadmin,admin,username,smscid,reroute_smscid from rerouting");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				String superadmin=resultset.getString("superadmin");
				String admin=resultset.getString("admin");
				String username=resultset.getString("username");
				
                if(superadmin==null||superadmin.trim().length()<1) {
					superadmin=Route.NULL;
				}
				
                superadmin=superadmin.toLowerCase();
                
				if(admin==null||admin.trim().length()<1) {
					admin=Route.NULL;
				}
				
				admin=admin.toLowerCase();
				
				if(username==null||username.trim().length()<1) {
					username=Route.NULL;
				}
				
				username=username.toLowerCase();
		
				String key=Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION;
				Map<String,String> map=reroutemap.get(key);
				
				if(map==null){
					map=new HashMap<String,String>();
					reroutemap.put(key, map);
					
				}
				
				map.put(resultset.getString("smscid"),resultset.getString("reroute_smscid"));
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
			
			this.rerouting=reroutemap;
	
	}
	
	
	public String getReRouteSmscid(String username,String smscid){
		

		String admin=PushAccount.instance().getPushAccount(username).get(MapKeys.ADMIN);
		String superadmin=PushAccount.instance().getPushAccount(username).get(MapKeys.SUPERADMIN);
		Map<String,String> map=null;
		for(int i=1;i<5;i++){
			
		String key=	getKey(superadmin,admin,username,i);
		map=rerouting.get(key);

		if(map!=null){

			break;
		}
		
		}
		
		if(map==null){
			
			return null;
		}
		
		return map.get(smscid);
	}

	private String getKey(String superadmin, String admin, String username, int i) {

		switch(i){
		
		case 1:
			return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+username+Route.CONJUNCTION;			
		case 2:
			return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+admin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;			
		case 3:
			return Route.CONJUNCTION+superadmin+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;			
		case 4:
			return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;			
			
		}
		return "";
	}
	
	
	
}

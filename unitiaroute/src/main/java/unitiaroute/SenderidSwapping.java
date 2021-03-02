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
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;

public class SenderidSwapping {

	private static SenderidSwapping obj=null;
	

	private Map<String,Map<String,String>> senderidswapping=new HashMap<String,Map<String,String>>();
	
	private boolean isTableAvailable=false;
	
	private SenderidSwapping(){
	
		reload();
	}
	
	public static SenderidSwapping getInstance(){
		
		if(obj==null){
			
			obj=new SenderidSwapping();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,Map<String,String>> temp=getData();
		
		if(temp!=null&&temp.size()>1){
			
			senderidswapping=temp;
		}
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("module", "senderidswapping");
		
		logmap.put("username", "sys");

		logmap.putAll(senderidswapping);
		logmap.put("logname", "senderidswapping");
		
		new FileWrite().write(logmap);
	}
	
	
	private Map<String, Map<String, String>> getData() {
		Map<String,Map<String,String>> senderidswap=new HashMap<String,Map<String,String>>();

		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =RouteDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "senderid_swapping_withsmsc")){
					
					if(table.create(connection, " create table senderid_swapping_withsmsc(id INT PRIMARY KEY AUTO_INCREMENT ,smscid varchar(60),operator varchar(2),circle varchar(2),senderid varchar(15) , senderid_swap varchar(15))", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select smscid,operator,circle,senderid,senderid_swap from senderid_swapping");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
			
				String operator =resultset.getString("operator");
				String circle =resultset.getString("circle");
				String smscid =resultset.getString("smscid");
				if(smscid==null||smscid.trim().length()<1) {
					smscid=Route.NULL;
				}
				if(operator==null||operator.trim().length()<1) {
					operator=Route.NULL;
				}
				
				if(circle==null||circle.trim().length()<1) {
					circle=Route.NULL;
				}
				
			
				
				String key=Route.CONJUNCTION+smscid+Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
				
				Map<String,String> map1=senderidswap.get(key);
				
				if(map1==null){
					
					map1=new HashMap<String,String>();
					map1.put(resultset.getString("senderid"), resultset.getString("senderid_swap"));
					senderidswap.put(key, map1);

				}else{
					
					map1.put(resultset.getString("senderid"), resultset.getString("senderid_swap"));
					senderidswap.put(key, map1);
				}

					
			}
		}catch(Exception e){
			
			e.printStackTrace();
			
			return null;
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		return senderidswap;
		
	}

	public String getSwapingSenderid(String key,String senderid){
		
		if(senderidswapping.containsKey(key)){
			return senderidswapping.get(key).get(senderid);
		
			}else{
				
				return null; 
			}
		}
	
	
	
	
	
}

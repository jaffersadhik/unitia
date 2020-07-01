package unitiaroute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class SenderidSwapping {

	private static SenderidSwapping obj=null;
	
	private Map<String,Map<String,String>> senderidreswapping=new HashMap<String,Map<String,String>>();

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
		
		Map<String,Map<String,String>> senderidswap=new HashMap<String,Map<String,String>>();
		Map<String,Map<String,String>> senderidreswap=new HashMap<String,Map<String,String>>();

		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =RouteDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "senderid_swapping")){
					
					if(table.create(connection, " create table senderid_swapping(id INT PRIMARY KEY AUTO_INCREMENT ,operator varchar(2),circle varchar(2),senderid varchar(15) , senderid_swap varchar(15))", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select operator,circle,senderid,senderid_swap from senderid_swapping");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
			
				String operator =resultset.getString("operator");
				String circle =resultset.getString("circle");
				if(operator==null){
					operator="";
				}
				if(circle==null){
					circle="";
				}
				
				String key=operator.trim()+"~"+circle.trim();
				
				Map<String,String> map1=senderidswap.get(key);
				
				if(map1==null){
					
					map1=new HashMap<String,String>();
					senderidswap.put(key, map1);
				}
				map1.put(resultset.getString("senderid"), resultset.getString("senderid_swapping"));
			
				Map<String,String> map2=senderidreswap.get(key);
				
				if(map2==null){
					
					map2=new HashMap<String,String>();
					
					senderidreswap.put(key, map2);
					
				}
				map2.put(resultset.getString("senderid_swapping"), resultset.getString("senderid"));
			
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		this.senderidswapping=senderidswap;
		this.senderidreswapping=senderidreswap;
	}
	
	
	public String getSwapingSenderid(String key,String senderid){
		
		if(senderidswapping.containsKey(key)){
			return senderidswapping.get(key).get(senderid);
		
			}else{
				
				return null; 
			}
		}
	
	
	public String getReSwapingSenderid(String key,String senderid){
		
		if(senderidreswapping.containsKey(key)){
		return senderidreswapping.get(key).get(senderid);
	
		}else{
			
			return null; 
		}
		}
	
	
}

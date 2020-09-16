package unitiaroute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class InternationalSenderidSwapping {

	private static InternationalSenderidSwapping obj=null;
	

	private Map<String,String> senderidswapping=new HashMap<String,String>();
	
	private boolean isTableAvailable=false;
	
	private InternationalSenderidSwapping(){
	
		reload();
	}
	
	public static InternationalSenderidSwapping getInstance(){
		
		if(obj==null){
			
			obj=new InternationalSenderidSwapping();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,String> senderidswap=new HashMap<String,String>();

		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =RouteDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "international_senderid_swapping")){
					
					if(table.create(connection, " create table international_senderid_swapping(id INT PRIMARY KEY AUTO_INCREMENT ,countrycode varchar(10),senderid varchar(15))", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select countrycode,senderid from international_senderid_swapping");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
			
				String countrycode =resultset.getString("countrycode");
				
				senderidswap.put(countrycode, resultset.getString("senderid"));
			
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		this.senderidswapping=senderidswap;
	}
	
	
	public String getSwapingSenderid(String countrycode){
		
			return senderidswapping.get(countrycode);
	}
	}

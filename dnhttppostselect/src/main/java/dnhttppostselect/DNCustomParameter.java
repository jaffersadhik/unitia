package dnhttppostselect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class DNCustomParameter {

	private static DNCustomParameter obj=new DNCustomParameter();
	
	private static String SQL="create table dn_custom_param(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(100) not null,parameter_name varchar(100) not null)";
	
	Map<String,Set<String>> customparam=new HashMap<String,Set<String>>();
	
	private DNCustomParameter(){
		
		init();
		reload();
	}
	
	private void init() {
		


		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "dn_custom_param")){
				
				table.create(connection, SQL, false);
			}
			
	

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	
		
	}

	public static DNCustomParameter getInstance(){
		
		if(obj==null){
			obj=new DNCustomParameter();
		}
		
		return obj;
	}
	
	public void reload(){
		

		
		Map<String,Set<String>> _tmpPushAcc = getData();
		if(_tmpPushAcc!=null){
		customparam = _tmpPushAcc;
		}	
		_tmpPushAcc = null;
		
	
	}

	private Map<String, Set<String>> getData() {	
	
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
	
		Map<String,Set<String>> result=new HashMap<String,Set<String>>();
	try
	{
			
		String sql = "select * from dn_custom_param";
	
		connection  = CoreDBConnection.getInstance().getConnection();
		statement = connection.prepareStatement(sql);
		resultSet = statement.executeQuery();
		
		while(resultSet.next()) 
		{
			String username = resultSet.getString("username").toLowerCase();
			
			Set<String> parameterset=result.get(username);
			
			if(parameterset==null){
				
				parameterset=new HashSet<String>();
				result.put(username, parameterset);
			}

			parameterset.add(resultSet.getString("parameter_name"));
			
		}
		
	} 
	catch(Exception e)
	{
	
		result = null;
	} 
	finally 
	{
		Close.close(resultSet);
		Close.close(statement);
		Close.close(connection);
	}//end of finally
	
	return result;

	}
	
	public Set<String> getCustomParam(String username){
		
		return customparam.get(username);
	}
}

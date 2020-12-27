package unitiaroute;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;



public class InternalKannel
{
    private static InternalKannel    obj        = null;
    
    private Map<String, Map<String, String>> kannel=new HashMap<String,Map<String,String>>();
    
    private InternalKannel()
    {
        
    	addMaster();
    }
    
	private void addMaster() {
		
		Connection connection =null;
		try {
			connection=RouteDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			
			loadData(connection, table);
		}catch(Exception e) {
			
			e.printStackTrace();
			
		}finally {
			
			Close.close(connection);
		}
		
	}


	
	private void loadData(Connection connection,TableExsists table) {

    	reloadSMSCID(connection,table);
	}
	

	private void reloadSMSCID(Connection connection, TableExsists table) {
		Map<String, Map<String, String>> temp_kannel=table.getInternalKannel(connection);
		if(temp_kannel!=null) {
			kannel=temp_kannel;
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
	public static InternalKannel getInstance() {
        
        if (obj == null)
        {
            
            obj = new InternalKannel();
        }
        
        return obj;
    }
    
    
	public Map<String, String> getKannelInfo(String smscid) {
		
		
		return kannel.get(smscid);
	}

}

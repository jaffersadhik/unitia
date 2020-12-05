package statuslog;

import java.sql.Connection;
import java.util.Iterator;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.StatusLogDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.Log;
import com.winnovature.unitia.util.misc.Prop;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;
import com.winnovature.unitia.util.redis.RedisQueuePool;

public class App 
{
 
    public static void main(String[] args) throws Exception
    {
    	Prop.getInstance();
    	
    	reload();
    	new T().start();

    	
    	
    	start("statuslog");
    
     }
    
    
    
public static void reload(){
		
		Connection connection=null;
		
		try{
			
		
			connection=StatusLogDBConnection.getInstance().getConnection();
			
		
			
			
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "reportlog_status")){
				
					if(table.create(connection, getSQL(), false)){
						
						
					}
				}
				
			
		}
		
		catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
	}


private static String getSQL() {
	 StringBuffer sb=new StringBuffer();
	 
	 sb.append("create table reportlog_status(");
	 sb.append("ackid varchar(40),");
	 sb.append("msgid varchar(40),");
	 sb.append("username varchar(16),");
	 sb.append("itime datetime default CURRENT_TIMESTAMP,");
	 sb.append("rtime datetime,");
	 sb.append("customerip varchar(50),");
	 sb.append("priorityorder varchar(2),");
	 sb.append("nextlevel varchar(50)");		
	 sb.append(")");

	 System.out.print(sb.toString());
	 return sb.toString();
}

	private static void start(String poolname) {
		
	
		for(int i=0;i<5;i++){
			Map<String, RedisQueuePool> map=RedisQueueConnectionPool.getInstance().getPoolMap();

			Iterator itr=map.keySet().iterator();
			
			while(itr.hasNext()){
				
				String redisid=itr.next().toString();
				String logstring="poolname :"+poolname+" RedisReceiver startted for "+redisid;
				System.out.println(logstring);
		
			Log.log(logstring);
			new RedisReceiver(poolname,redisid).start();



		}
		
		}
		
	}
    
}


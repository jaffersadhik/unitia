package queuecheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;


public class TPSLog {


	 private static String MODE="";
		
		static {
			
			String mode=System.getenv("mode");
			
			if(mode==null||mode.trim().length()<1){
				
				MODE="production";
			}else{
				
				MODE=mode;
			}
			
			

		}
	
	private static TPSLog obj=null;


	private TPSLog(){
		
		checkQueueTableAvailable();
	}
	
	

	public static TPSLog getInstance(){
		
		
		if(obj==null){
			
			obj=new TPSLog();
		}
		
		return obj;
	}
	
	
	private void checkQueueTableAvailable() {
		Connection connection=null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			if(!table.isExsists(connection, "tps_log")){
				
				table.create(connection, "create table tps_log(smscid varchar(50),qsize numeric(10,0),tps numeric(10,0),itime timestamp default CURRENT_TIMESTAMP)", false);
			}
			
			
			
		}catch(Exception e){
			 e.printStackTrace();
		}finally{
		
			Close.close(connection);
		}
		
	}
	public void doProcess() throws SQLException{
			
		Connection connection=null;
		try{
			connection=CoreDBConnection.getInstance().getConnection();

			List<Map<String,String>> result=new ArrayList<Map<String,String>>();
			
			Map<String,Map<String,String>> map=TPSSmscidCount.getInstance().getResult();
			
			Iterator itr=map.keySet().iterator();
			
			while(itr.hasNext()){
				try{
					String smscid=itr.next().toString();
					String tps=map.get(smscid).get("tps");				
					long tpsL=Long.parseLong(tps);
					
					Map<String,String> data=new HashMap<String,String>();
					
					if(tpsL>0){
						
						data.put("smscid", smscid);
						data.put("tps", tps);

						String qcount=kannelQueue.smscqueue.get(smscid).get("queued");
						
						if(qcount!=null){
							
							data.put("qsize", qcount);

							result.add(data);
							
						}
					}
					

				}catch(Exception e){
					
				}
			}
			
			insertQueueintoDB(connection,result);
		
		}catch(Exception e){
			
		}finally{
			Close.close(connection);
		}
		
		
	}

	


public void insertQueueintoDB(Connection connection, List<Map<String,String>> queueCount) {
		
		
		try
		
		{
			

			
			
			for(int i=0;i<queueCount.size();i++){
				Map<String,String> result=queueCount.get(i);
				String smscid=result.get("smscid");
				String qsize=result.get("qsize");
				String tps=result.get("tps");
	
					insertQueueintoDB(connection,smscid, tps,qsize);
				}

				
			
			
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		
			Close.close(connection);
		}
	}

private void insertQueueintoDB(Connection connection,String smscid,String tps,String qsize) {
		

		PreparedStatement insert=null;
		
		try
		
		{
			long updatetime=System.currentTimeMillis();
			
			insert=connection.prepareStatement("insert into tps_log(smscid,tps ,qsize) values(?,?,?)");
			insert.setString(1, smscid);
			insert.setString(2, tps);
			insert.setString(3, qsize);

			insert.execute();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(insert);
		}
	}

	
	private int updateDB(Connection connection,String queuename,String count) {
		

		PreparedStatement update=null;
		
		try
		
		{
			
			long updatetime=System.currentTimeMillis();
			
			update=connection.prepareStatement("update tps_max_smscid set count=?,updatetime=? where smscid=? and mode=?");

			update.setString(1, count);
			update.setString(2, ""+updatetime);
			update.setString(3, ""+queuename);
			update.setString(4, MODE);
			

			return update.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			Close.close(update);
		}
		
		return 0;
	}



}

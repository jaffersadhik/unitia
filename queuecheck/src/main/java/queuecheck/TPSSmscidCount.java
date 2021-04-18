package queuecheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;


public class TPSSmscidCount {


	 private static String MODE="";
		
		static {
			
			String mode=System.getenv("mode");
			
			if(mode==null||mode.trim().length()<1){
				
				MODE="production";
			}else{
				
				MODE=mode;
			}
			
			

		}
	
	private static TPSSmscidCount obj=null;

	private  Map<String,Map<String,String>> result=new HashMap<String,Map<String,String>>();

	private TPSSmscidCount(){
		
		checkQueueTableAvailable();
	}
	
	

	public static TPSSmscidCount getInstance(){
		
		
		if(obj==null){
			
			obj=new TPSSmscidCount();
		}
		
		return obj;
	}
	
	
	private void checkQueueTableAvailable() {
		Connection connection=null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			if(!table.isExsists(connection, "tps_count_smscid")){
				
				table.create(connection, "create table tps_count_smscid(smscid varchar(50),count numeric(10,0),updatetime numeric(13,0),mode varchar(25) default 'production')", false);
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

			setCurrentSMSCIDCount();
			insertQueueintoDB(connection);
		
		}catch(Exception e){
			
		}finally{
			Close.close(connection);
		}
		
		
	}



	private void setCurrentSMSCIDCount() {
		
		Map<String, Map<String, String>> smscqueue=kannelQueue.smscqueue;
		
		Iterator itr=smscqueue.keySet().iterator();
		
		while(itr.hasNext()){
			
			String smscid=itr.next().toString();
			
			Map<String, String> data=smscqueue.get(smscid);
			
			Map<String, String> data1=result.get(smscid);
			
			if(data1==null){
				
				data1=new HashMap<String,String>();
				
				result.put(smscid, data1);
			}
			String pcount=data1.get("ccount");
			String ccount=data.get("sent");

			data1.put("pcount", pcount);
			data1.put("ccount", ccount);
			long tps=0;

			if(pcount!=null){
				
				try{
					long pInt=Integer.parseInt(pcount);
					long cInt=Integer.parseInt(ccount);
					long diff=cInt-pInt;
					if(diff>0){
					
						String ptime=data1.get("ptime");
						
						if(ptime==null){
							
							ptime=""+System.currentTimeMillis();
							
							
						}
						
						long tdiff=System.currentTimeMillis()-Long.parseLong(ptime);
						
						if(tdiff>1000){
							
							tdiff=tdiff/1000;
							
							tps=diff/tdiff;
						}
						
					}
				}catch(Exception e){
					
				}
			}
			
			data1.put("ptime", ""+System.currentTimeMillis());
			data1.put("tps", ""+tps);
		}
	}
	
	
	
	

private String getQuery(String sQL2, String tablename) {
		String param[]={tablename};
		return MessageFormat.format(sQL2, param);
	}







	


public void insertQueueintoDB(Connection connection) {
		
		
		try
		
		{
			

			
			Iterator itr=result.keySet().iterator();
			
			while(itr.hasNext()){
				
				String smscid=itr.next().toString();
				String tps=result.get(smscid).get("tps");
				if(updateDB(connection, smscid, tps)<1){
					
					insertQueueintoDB(connection,smscid, tps);
				}

				
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		
			Close.close(connection);
		}
	}

private void insertQueueintoDB(Connection connection,String queuename,String count) {
		

		PreparedStatement insert=null;
		
		try
		
		{
			long updatetime=System.currentTimeMillis();
			
			insert=connection.prepareStatement("insert into tps_count_smscid(smscid,count ,updatetime,mode) values(?,?,?,?)");
			insert.setString(1, queuename);
			insert.setString(2, count);
			insert.setString(3, ""+updatetime);
			insert.setString(4, MODE);

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
			
			update=connection.prepareStatement("update tps_count_smscid set count=?,updatetime=? where smscid=? and mode=?");

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

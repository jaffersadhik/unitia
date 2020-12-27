package unitiaroute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class SenderidSwapScheduling {

	private static SenderidSwapScheduling obj=null;
	
	private Map<String,List<String>> senderidswapschedule=new HashMap<String,List<String>>();

	private boolean isTableAvailable=false;
	
	private SenderidSwapScheduling(){
	
		reload();
	}
	
	public static SenderidSwapScheduling getInstance(){
		
		if(obj==null){
			
			obj=new SenderidSwapScheduling();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Map<String,List<String>> senderidswapschedule=new HashMap<String,List<String>>();

		Connection connection =null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			connection =RouteDBConnection.getInstance().getConnection();
			
			if(!isTableAvailable){
				
				TableExsists table=new TableExsists();
				
				if(!table.isExsists(connection, "senderid_swapping_schedule")){
					
					if(table.create(connection, " create table senderid_swapping_schedule(id INT PRIMARY KEY AUTO_INCREMENT ,senderid varchar(15) ,schedule_starttime varchar(5),schedule_endtime varchar(5) ,unique(senderid,schedule_starttime,schedule_endtime))", false)){
					
						isTableAvailable=true;
					}
				}else{
					
					isTableAvailable=true;
				}
			}
			
			statement =connection.prepareStatement("select senderid,schedule_starttime,schedule_endtime from senderid_swapping_schedule");
			resultset=statement.executeQuery();
			while(resultset.next()){
			
				String senderid=resultset.getString("senderid");
				String schedule_starttime=resultset.getString("schedule_starttime");
				String schedule_endtime=resultset.getString("schedule_endtime");

				List<String> schedule=senderidswapschedule.get(senderid);
				
				if(schedule==null){
					schedule=new ArrayList();
				}
				
				schedule.add(schedule_starttime+"~"+schedule_endtime);
			}
			
			this.senderidswapschedule=senderidswapschedule;
		}catch(Exception e){
			
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
	}
	
	
	public List<String> getSwapingScheduleTime(String senderid){
		
		return senderidswapschedule.get(senderid);
	}
	
	
	
}

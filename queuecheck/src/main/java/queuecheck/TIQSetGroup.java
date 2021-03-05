package queuecheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.queue.IQGroup;

public class TIQSetGroup  extends Thread{

	long start=System.currentTimeMillis();
	
	public void run(){
		
		while(true){
			
			try{
				
				IQGroup.getInstance().reload();
				IQGroup.getInstance().allocateGroupForQueues();

				gotosleep();
			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
	}

	

	private List<Map<String, String>> getQueueName() {
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		
		try{
			
			
			
		}catch(Exception e){
			
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
			
		}
		return null;
	}



	private void gotosleep() {


		try{
			Thread.sleep(10L);
		}catch(Exception e){
			
		}
	}
}






package queuecheck;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.winnovature.unitia.util.dao.Table;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.QueueDBConnection;

public class TConcateUpdate extends Thread{

	public void run(){
		
		while(true){
			
			doProcess();
			
			gotosleep();
		}
	}

	private void doProcess() {

		
		
		if(!Table.getInstance().isAvailableTable("concatedata")){
			
			Table.getInstance().addTable("concatedata");
		}
		Connection connection=null;
		PreparedStatement statement=null;

		try {
		
				
			connection=QueueDBConnection.getInstance().getConnection();
					
			statement=connection.prepareStatement("update concatedata set pstatus=1 where msgid in (select msgid from (select msgid,cc,count(*) from concatedata where pstatus=0 group by msgid,cc having count(*)=cc)a)");
			
			statement.execute();
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			
			Close.close(statement);
			Close.close(connection);

			
		}
		
	
	}

	private void gotosleep() {

		try{
			Thread.sleep(50L);
		}catch(Exception e){
			
		}
	}
}

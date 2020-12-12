package dnsql;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.account.Refresh;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.KannelDBConnection;
import com.winnovature.unitia.util.reader.QueueTon;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;

import queuecheck.DLRCount;
import queuecheck.TableCount;
import unitiaroute.ReRouting;



public class T  extends Thread{

	static String SQL_COUNT ="select count(*) cnt from {0}";
	
	static List<String> TABLES=new ArrayList<String>();
	
	static{

		TABLES.add("optin");
		TABLES.add("optout");
		TABLES.add("duplicate");
		TABLES.add("shortcodepool");
		TABLES.add("missedcallpool");
		TABLES.add("appspool");
		TABLES.add("dngenpool");
		TABLES.add("submissionpool");
		TABLES.add("smppdn");
		TABLES.add("httpdn");
		TABLES.add("dnreceiverpool");
		TABLES.add("logspool");
		TABLES.add("dnpostpool");
    	TABLES.add("clientdnpool");
    	TABLES.add("schedulepool");
    	TABLES.add("kannelretrypool");
    	TABLES.add("commonpool");
    	TABLES.add("otppool");
    	TABLES.add("otpretrypool");
    	TABLES.add("dngenpool");
    	TABLES.add("clientdnpool");
    	TABLES.add("dnretrypool");
    	TABLES.add("dlr_unitia");

	}

	public void run(){
		
		while(true){
			
			try{
				Refresh.getInsatnce().reload();
				RedisQueueConnectionPool.getInstance().reload();
				
				TableCount.getInstance().tableCountCheck();
				
				ReRouting.getInstance().reload();
				RedisQueueConnectionPool.getInstance().print();

				QueueTon.getInstance().checkQueueAvailablity();
				
				com.winnovature.unitia.util.db.Kannel.getInstance().reload();
				com.winnovature.unitia.util.misc.kannel.reload();
				DLRCount.getInstance().doProcess();
				PollerStartup.updateUsers();
				gotosleep();
					
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private void gotosleep() {


		try{
			Thread.sleep(10L);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}






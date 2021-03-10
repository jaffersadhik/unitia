package creditupdate;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.Kannel;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.Carrier;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.Prop;

public class App 
{
	
	
	static FileWrite log =new FileWrite();
	
    public static void doProcess() 
    {
    
		

    	
    	new SyncBal().start();
    	
    	log.log("creditupdate.App.doProcess() SyncBal thread started");

    	new TopupBalance().start();

    	log.log("creditupdate.App.doProcess() TopBalance thread started");

		
    	
	}
    
	
}


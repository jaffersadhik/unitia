package unitiadbtoredis;

import java.util.List;

import com.winnovature.unitia.util.db.Kannel;
import com.winnovature.unitia.util.misc.Prop;


public class App 
{
 
    public static void main(String[] args) throws Exception
    {
    	Prop.getInstance();
    	
    
    	start("optin");
    	start("optout");
    	start("duplicate");

    	start("dnreceiverpool");
    	start("dnpostpool");
    	start("clientdnpool");
    	start("schedulepool");
    	start("kannelretrypool");
    	start("commonpool");
    	start("otppool");
    	start("otpretrypool");
    	start("dngenpool");
    	start("clientdnpool");
    	start("dnretrypool");
    	start("smppdn");
    	start("httpdn");
    	start("logspool");
    	start("appspool");
    	start("submissionpool");
    	start("missedcallpool");
    	start("shortcodepool");

    	List<String> kannelidlist =Kannel.getInstance().getKannelIdList();
    	
    	for(int i=0;i<kannelidlist.size();i++){
    		
        	start(kannelidlist.get(i));

    	}
    	new T().start();
    	

     }

	private static void start(String poolname) {
		
		PollerStartup.getInstance(poolname);

		
	}
    
}


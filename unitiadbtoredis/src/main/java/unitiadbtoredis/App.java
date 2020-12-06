package unitiadbtoredis;

import com.winnovature.unitia.util.misc.Prop;


public class App 
{
 
    public static void main(String[] args) throws Exception
    {
    	Prop.getInstance();
    	
    
    	start("optin");
    	start("optout");
    	start("duplicate");
    	start("processor");

    	start("dnreceiverpool");
    	start("dnpostpool");
    	start("clientdnpool");
    	start("schedulepool");
    	start("kannelretrypool");
    	start("commonpool");
    	start("otppool");
    	start("otpretrypool");
    	start("clientdnpool");
    	start("dnretrypool");
    	start("smppdn");
    	start("httpdn");
    	start("appspool");
    	start("submissionpool");
    	start("missedcallpool");
    	start("shortcodepool");

    	new T().start();
    	

     }

	private static void start(String poolname) {
		
		PollerStartup.getInstance(poolname);

		
	}
    
}


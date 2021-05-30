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
    	start("commonpool_1");
    	start("commonpool_2");

    	start("cdac");

    	start("otppool");
    	start("otpretrypool");
    	start("dngenpool");
    	start("clientdnpool");
    	start("dnretrypool");
    	start("smppdn");
    	start("logspool");
    	start("appspool");
    	start("submissionpool");
    	start("requestlog");
    	start("missedcallpool");
    	start("shortcodepool");
    	start("kl_kannel2_1");
    	start("kl_kannel2_2");
    	start("kl_vedioconkannel_1");
    	start("kl_vedioconkannel_2");
    	start("kl_kannelA_1");
    	start("kl_kannelA_2");
    	start("kl_kannelB_1");
    	start("kl_kannelB_2");
    	start("kl_kannelC_1");
    	start("kl_kannelC_2");
    	start("kl_kannelD_1");
    	start("kl_kannelD_2");
      
    	start("concatepool");

    	
    	new T().start();
    	

     }

	private static void start(String poolname) {
		
		PollerStartup.getInstance(poolname);

		
	}
    
}


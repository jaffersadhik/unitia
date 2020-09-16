package unitiadbtoredis;

import com.winnovature.unitia.util.misc.Prop;


public class App 
{
 
    public static void main(String[] args) throws Exception
    {
    	Prop.getInstance();
    	
    

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

    	new T().start();
    	

     }

	private static void start(String poolname) {
		
		PollerStartup.getInstance(poolname);

		
	}
    
}


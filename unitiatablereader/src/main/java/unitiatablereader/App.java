package unitiatablereader;

import com.winnovature.unitia.util.misc.Prop;

public class App 
{
 
    public static void main(String[] args) throws Exception
    {
    	Prop.getInstance();
    

    	start("uiq_campaign_immediate");
    	start("uiq_campaign_quick");
    	start("uiq_campaign_schedule");
    	start("uiq_campaign_small");
  
    	
    	new T().start();

    	

     }

	private static void start(String poolname) {
	
		
		PollerStartup.getInstance(poolname);
	
	}
    
}


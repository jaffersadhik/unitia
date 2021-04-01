package dnhttppostselect;

import com.winnovature.unitia.util.misc.Prop;


public class App 
{
 
    public static void main(String[] args) throws Exception
    {
    	Prop.getInstance();
    	
    	start("httpdn");
    
    	
    	new T().start();
    	

     }

	private static void start(String poolname) {
		
		PollerStartup.getInstance(poolname);

		
	}
    
}


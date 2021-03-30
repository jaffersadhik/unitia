package smpp2;

import com.winnovature.unitia.util.misc.Prop;

public class App 
{
 
    public static void doProcess() throws Exception
    {
    	Prop.getInstance();
    	
    	new ServerMain().doProcess();
    }

	    
}


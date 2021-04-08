package queuecheck;

import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.Prop;


public class App 
{
 
	static FileWrite log =new FileWrite();
	
	
    public static void doProcess() 
    {
    	log.log("unitiacore.App.doProcess()");
    	Prop.getInstance();
    	log.log("unitiacore.App.doProcess() properties loaded");

    	new TKannelDN().start();
    	new TKannelSMSCID().start();
    	new TMysqlQueue().start();
    	new TRedisQueue().start();
    	new TUsers().start();
    	new THttpDN().start();
    	new TConcateUpdate().start();

     }
}


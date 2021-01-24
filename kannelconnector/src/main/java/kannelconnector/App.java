
package kannelconnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.Prop;


public class App 
{
 
	static FileWrite log =new FileWrite();
	
	static List<RedisReceiver> pollerlist=new ArrayList<RedisReceiver>();
	
    public static void doProcess() 
    {
    	log.log("unitiacore.App.doProcess()");
    	Prop.getInstance();
    	log.log("unitiacore.App.doProcess() properties loaded");

    	new T().start();
    	
       	start();
    	log.log("unitiacore.App.doProcess() commonpool thread started");

    
    	if(Pattern.compile("(.*){0,}.*", Pattern.CASE_INSENSITIVE).matcher("test test").matches())
		{
			System.out.println("Template Mattched ok");
		}else{
			
			System.out.println("no Template Mattched ");

		}
	

     }

	private static void start() {
		
		try{
    	
		String redisid=System.getenv("redis");
		String kannelid=System.getenv("kannelid");

    	log.log("unitiacore.App.start() "+kannelid);

				
			for(int i=0;i<2;i++){
		
				RedisReceiver obj=new RedisReceiver(i,kannelid,redisid);
				obj.start();

				pollerlist.add(obj);
			}
		
		}catch(Exception e){
		
		log.log("unitiacore.App.start() err : "+ErrorMessage.getMessage(e));

		
	}
	
	}
		
	
}


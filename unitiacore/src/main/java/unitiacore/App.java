
package unitiacore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.Prop;

import unitiacore.threadpool.MissedCallRedisReceiver;
import unitiacore.threadpool.RedisReceiver;
import unitiacore.threadpool.ShortCodeRedisReceiver;

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
    	
    	log.log("unitiacore.App.doProcess() memory refresh thread started");

     	start("shortcodepool");

     	start("missedcallpool");

    	
     	start("kannelretrypool");
    	log.log("unitiacore.App.doProcess() kannelretrypool thread started");

    	start("commonpool");
    	log.log("unitiacore.App.doProcess() commonpool thread started");

    	start("otppool");
    	log.log("unitiacore.App.doProcess() otppool thread started");

    	start("otpretrypool");
    	log.log("unitiacore.App.doProcess() otpretrypool thread started");

    	start("dnretrypool");

    	log.log("unitiacore.App.doProcess() dnretrypool thread started");

    	if(Pattern.compile("(.*){0,}.*", Pattern.CASE_INSENSITIVE).matcher("test test").matches())
		{
			System.out.println("Template Mattched ok");
		}else{
			
			System.out.println("no Template Mattched ");

		}
	

     }

	private static void start(String poolname) {
		
		try{
    	log.log("unitiacore.App.start() "+poolname);
    	
		String redisid=System.getenv("redis");


		if(poolname.equals("otppool") || poolname.equals("dnretrypool") || poolname.equals("kannelretrypool") || poolname.equals("otpretrypool") ){
			
					RedisReceiver obj=new RedisReceiver(1,poolname,"redisqueue1");
					obj.start();
					pollerlist.add(obj);
			
		}else if(poolname.equals("missedcallpool")){
			
			
			MissedCallRedisReceiver obj=new MissedCallRedisReceiver(1,poolname,redisid);
			obj.start();

		}else if(poolname.equals("shortcodepool")){
			
			
			ShortCodeRedisReceiver obj=new ShortCodeRedisReceiver(1,poolname,redisid);
			obj.start();

		}else{
		
			for(int i=0;i<2;i++){
		
				RedisReceiver obj=new RedisReceiver(i,poolname,redisid);
				obj.start();

				pollerlist.add(obj);
			}
		
		}
		}catch(Exception e){
		
		log.log("unitiacore.App.start() err : "+ErrorMessage.getMessage(e));

		
	}
	
	}
	
	public static void printThreadStatus(){
		
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("username", "sys");
		logmap.put("logname", "routerthreadmonitor");
		boolean print=false;
		for(int i=0,max=pollerlist.size();i<max;i++){
			
			if(pollerlist.get(i).isDisplay()){
				
				try{
				logmap.put(pollerlist.get(i).getTName(), pollerlist.get(i).getState().toString());
				StackTraceElement stack[]=pollerlist.get(i).getStackTrace();
				
				StringBuffer sb=new StringBuffer();
				for(int j=0;i<stack.length;j++){
					sb.append(stack[j].toString());
				}

				logmap.put(pollerlist.get(i).getTName()+" Stack [] ",sb.toString());
				print=true;
				}catch(Exception e){
					
				}
			}
		}
		
		if(print){
		new FileWrite().write(logmap);
		}
	}
	
	
}


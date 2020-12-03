package templatecheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.Prop;
import com.winnovature.unitia.util.misc.RedisInstance;


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

     	start("templatecheck");

    	log.log("unitiacore.App.doProcess() dnretrypool thread started");
	

     }

	private static void start(String poolname) {
		
		try{
    	log.log("unitiacore.App.start() "+poolname);
    	


		List<String> redisidlist=RedisInstance.getInstance().getRedisInstanceList();

	
		for(int j=0;j<redisidlist.size();j++){
		
			String redisid=redisidlist.get(j);

			for(int i=0;i<7;i++){
		
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


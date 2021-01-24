package unitiad;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.misc.FileWrite;

public class App {
	
	static FileWrite log =new FileWrite();


	public static void main(String args[]) {
		try{
		String module=System.getenv("module");
		System.out.println("App.main() module : "+module);
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("module", module);
		logmap.put("username", "sys");
		
		
	
		if(module.equals("http")){
			
			unitiahttpd.App.main(args);
			
		}else if(module.equals("smpp")){
			
			unitiasmpp.server.App.doProcess();
			
			logmap.put("started", "smpp");
			
		}else if(module.equals("simulator")){
			
			simulatar.server.App.doProcess();
			
			logmap.put("started", "simulator");
			
		}else if(module.equals("router")){
			
			unitiacore.App.doProcess();
			
			logmap.put("started", "router");

			
		}else if(module.equals("kannelconnector")){
			
			kannelconnector.App.doProcess();
			
			logmap.put("started", "kannelconnector");

			
		}else if(module.equals("optin")){
			
			optin.App.doProcess();
			
			logmap.put("started", "optin");

			
		}else if(module.equals("optout")){
			
			optout.App.doProcess();
			
			logmap.put("started", "optout");

			
		}else if(module.equals("duplicate")){
			
			duplicate.App.doProcess();
			
			logmap.put("started", "duplicate");

			
		}else if(module.equals("dngen")){
			
			unitiadngen.App.main(args);
			
		}else if(module.equals("dngencore")){
			
			dngencore.App.main(args);
			
		}else if(module.equals("dnreceiver")){
			
			unitiadnreceiver.App.main(args);
			
		}else if(module.equals("scheduledb")){
			
			scheduledb.App.main(args);
			
		}else if(module.equals("submission")){
			
			submission.App.main(args);
			
		}else if(module.equals("delivery")){
			
			delivery.App.main(args);
			
		}else if(module.equals("dnpostdb")){
			
			dnpostdb.App.main(args);
			
			logmap.put("started", "dnpostdb");

			
		}else if(module.equals("dbtoredis")){
			
			unitiadbtoredis.App.main(args);
		}else if(module.equals("tablereader")){
			
			unitiatablereader.App.main(args);
		}else if(module.equals("dnhttppost")){
			
			unitiadnhttppost.App.main(args);
			
		}else if(module.equals("kannelconfig")){
			
			kannelconfig.App.doProcess();
		}else if(module.equals("logs")){
			System.out.println("inside selected logs");
			logs.App.doProcess();
		}else if(module.equals("dnsql")){
			
			dnsql.App.main(args);
		}else if(module.equals("demo")){
			
			demo.App.main(args);
		}else if(module.equals("queuecheck")){
			System.out.println("inside selected logs");
			queuecheck.App.doProcess();
		}
		
		
		System.out.println(logmap);
	
		}catch(Exception e){
			
			e.printStackTrace();
		}
		}
}

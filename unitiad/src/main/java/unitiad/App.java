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
			
		}else if(module.equals("admin")){
			
			admin.App.main(args);
			
		}else if(module.equals("smpp")){
			
			unitiasmpp.server.App.doProcess();
			
			logmap.put("started", "smpp");
			
		}else if(module.equals("smpp2")){
			
			smpp2.App.doProcess();
			
			logmap.put("started", "smpp2");
			
		}else if(module.equals("simulator")){
			
			simulatar.server.App.doProcess();
			
			logmap.put("started", "simulator");
			
		}else if(module.equals("router")){
			
			unitiacore.App.doProcess();
			
			logmap.put("started", "router");

			
		}else if(module.equals("kannelconnector")){
			
			kannelconnector.App.doProcess();
			
			logmap.put("started", "kannelconnector");

			
		}else if(module.equals("cdacconnector")){
			
			cdacconnector.App.doProcess();
			
			logmap.put("started", "cdacconnector");

			
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
			
		}else if(module.equals("requestlog")){
			
			requestlog.App.main(args);
			
		}else if(module.equals("dnquerylog")){
			
			dnquerylog.App.main(args);
			
		}else if(module.equals("delivery")){
			
			delivery.App.main(args);
			
		}else if(module.equals("concateinsert")){
			
			concateinsert.App.main(args);
			
		}else if(module.equals("concateexpiry")){
			
			concateexpiry.App.main(args);
			
		}else if(module.equals("concateselect")){
			
			concateselect.App.main(args);
			
		}else if(module.equals("reroutekannelinsert")){
			
			reroutekannelinsert.App.main(args);
			
		}else if(module.equals("reroutekannelselect")){
			
			reroutekannelselect.App.main(args);
			
		}else if(module.equals("dnhttppostinsert")){
			
			dnhttppostinsert.App.main(args);
			
		}else if(module.equals("dnhttppostselect")){
			
			dnhttppostselect.App.main(args);
			
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
		}else if(module.equals("creditupdate")){
			
			creditupdate.App.doProcess();
			
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

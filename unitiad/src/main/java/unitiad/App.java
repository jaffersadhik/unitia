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
			
		}else if(module.equals("router")){
			
			unitiacore.App.doProcess();
			
			logmap.put("started", "router");

			
		}else if(module.equals("optin")){
			
			optin.App.doProcess();
			
			logmap.put("started", "optin");

			
		}else if(module.equals("optout")){
			
			optout.App.doProcess();
			
			logmap.put("started", "optout");

			
		}else if(module.equals("duplicate")){
			
			duplicate.App.doProcess();
			
			logmap.put("started", "duplicate");

			
		}else if(module.equals("countrycode")){
			
			countrycode.App.doProcess();
			
			logmap.put("started", "countrycode");

			
		}else if(module.equals("numberingplan")){
			
			numberingplan.App.doProcess();
			
			logmap.put("started", "numberingplan");

			
		}else if(module.equals("blacklistmobile")){
			
			blacklist.App.doProcess();
			
			logmap.put("started", "blacklistmobile");

			
		}else if(module.equals("blacklistsms")){
			
			blacklistsms.App.doProcess();
			
			logmap.put("started", "blacklistsms");

			
		}else if(module.equals("blacklistsenderid")){
			
			blacklistsenderid.App.doProcess();
			
			logmap.put("started", "blacklistsenderid");

			
		}else if(module.equals("spamfilter")){
			
			spamfilter.App.doProcess();
			
			logmap.put("started", "spamfilter");

			
		}else if(module.equals("queuecheck")){
			
			queuecheck.App.doProcess();
			
			logmap.put("started", "queuecheck");

			
		}else if(module.equals("senderidcheck")){
			
			senderidcheck.App.doProcess();
			
			logmap.put("started", "senderidcheck");

			
		}else if(module.equals("templatecheck")){
			
			templatecheck.App.doProcess();
			
			logmap.put("started", "templatecheck");

			
		}else if(module.equals("dnd")){
			
			dnd.App.doProcess();
			
			logmap.put("started", "dnd");

			
		}else if(module.equals("dlt")){
			
			dlt.App.doProcess();
			
			logmap.put("started", "dlt");

			
		}else if(module.equals("routegroup")){
			
			routegroup.App.doProcess();
			
			logmap.put("started", "routegroup");

			
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
			
		}else if(module.equals("statuslog")){
			
			statuslog.App.main(args);
			
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
		}
		
		
		System.out.println(logmap);
	
		}catch(Exception e){
			
			e.printStackTrace();
		}
		}
}

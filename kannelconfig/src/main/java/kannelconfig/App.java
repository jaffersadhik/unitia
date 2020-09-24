package kannelconfig;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.Carrier;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.Prop;
import com.winnovature.unitia.util.redis.RedisQueueConnectionPool;
import com.winnovature.unitia.util.redis.RedisQueuePool;

public class App 
{
	
	
	static FileWrite log =new FileWrite();
	
    public static void doProcess() 
    {
    	System.out.println("unitiacore.App.doProcess()");
    	
    	Prop.getInstance();
    	Carrier.getInstance();
    	checkQueueTableAvailable();
    	System.out.println("unitiacore.App.doProcess() properties loaded");
		TableAvailability.instance();
		
		
		
		createKannelConfigurationFile();
		
    	new T().start();
    	
    
    	start("logspool");
	}
    
    

	private static void start(String poolname) {
		
	
		for(int i=0;i<5;i++){
			Map<String, RedisQueuePool> map=RedisQueueConnectionPool.getInstance().getPoolMap();

			Iterator itr=map.keySet().iterator();
			
			while(itr.hasNext()){
				
				String redisid=itr.next().toString();
				String logstring="poolname :"+poolname+" RedisReceiver startted for "+redisid;
				System.out.println(logstring);
		
			new RedisReceiver(poolname,redisid).start();



		}
		
		}
		
	}
    

    private static void checkQueueTableAvailable() {

		Connection connection=null;
		
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			TableExsists table=new TableExsists();
			
			if(!table.isExsists(connection, "queue_count_mysql")){
				
				table.create(connection, "create table queue_count_mysql(queuename varchar(50),count numeric(10,0),updatetime numeric(13,0),mode varchar(25),unique(mode,queuename)  )", false);
			}
			
			
			if(!table.isExsists(connection, "queue_max_mysql")){
				
				table.create(connection, "create table queue_max_mysql(queuename varchar(50),count numeric(10,0),updatetime numeric(13,0),mode varchar(25),unique(mode,queuename)  )", false);
			}
			
			
			if(!table.isExsists(connection, "queue_count_dlr")){
				
				table.create(connection, "create table queue_count_dlr(queuename varchar(50),count numeric(10,0),updatetime numeric(13,0),mode varchar(25),unique(mode,queuename)  )", false);
			}
			
			
			if(!table.isExsists(connection, "queue_max_dlr")){
				
				table.create(connection, "create table queue_max_dlr(queuename varchar(50),count numeric(10,0),updatetime numeric(13,0),mode varchar(25),unique(mode,queuename)  )", false);
			}
		}catch(Exception e){
			 e.printStackTrace();
		}finally{
		
			Close.close(connection);
		}
		
	}
	private static void createKannelConfigurationFile() {

    	System.out.println("unitiacore.App.createKannelConfigurationFile() properties loaded");
		
    	
    	
    	Map<String,List<Map<String,String>>> map=TableAvailability.instance().getSMSIDList();
    	
    	Iterator itr=map.keySet().iterator();
    	
    	while(itr.hasNext()){
    	
    		String kannelid=itr.next().toString();
    		List<Map<String,String>> list=map.get(kannelid);
    	StringBuffer sb=new StringBuffer();
    	
    	sb.append(getCoreConfig());
    	sb.append(getSMSBoxConfig());
    	sb.append(getUserConfig());
    	sb.append(getMysqlConfig());
    	sb.append(getTableConfig());
    	sb.append(getTLVConfig());

    	for(int i=0;i<list.size();i++){
    		
    		Map<String,String> data=list.get(i);
    		int sessioncount=1;
    		try{
    			
    			sessioncount=Integer.parseInt(data.get("sessioncount"));
    			
    		}catch(Exception e){
    			
    		}
    		
    		for(int j=0;j<sessioncount;j++){
    			
    	    	sb.append(getSMSCConfig(data));

    		}
    	}
    	
    	
    	String kannelfile=sb.toString();
    	
    	System.out.println(kannelfile);
    	new FileWrite().savekannelfile(kannelid,kannelfile);
    	}
    	
    	
    	
    	new SyncBal().start();
    	
    	log.log("unitiacore.App.doProcess() SyncBal thread started");

    	new TopupBalance().start();

    	log.log("unitiacore.App.doProcess() TopBalance thread started");


	}
	private static Object getTLVConfig() {
		
	StringBuffer sb=new StringBuffer();
    	
		sb.append("\n");
    	sb.append("\n");
    


    	sb.append("group = smpp-tlv").append("\n");	
    	sb.append("name = entityid").append("\n");		
    	sb.append("tag = 0x1400").append("\n");		
    	sb.append("type = octetstring").append("\n");		
    	sb.append("length = 19").append("\n");
    	
        sb.append("\n");
    	sb.append("\n");
    	sb.append("group = smpp-tlv").append("\n");	
    	sb.append("name = templateid").append("\n");		
    	sb.append("tag = 0x1401").append("\n");
    	sb.append("type = octetstring").append("\n");		
    	sb.append("length = 30").append("\n");		
        sb.append("\n");
    	sb.append("\n");
    	
    	return sb.toString();
	}



	private static String getSMSCConfig(Map<String, String> data) {


		
		StringBuffer sb=new StringBuffer();
    	
		sb.append("\n");
    	sb.append("\n");
    	
    	sb.append("group = smsc").append("\n");	
    	sb.append("smsc = smpp").append("\n");		
    	sb.append("smsc-id = "+data.get("smscid")+"").append("\n");		
    	sb.append("allowed-smsc-id = "+data.get("smscid")+"").append("\n");	
    	sb.append("host = "+data.get("ip")+"").append("\n");		
    	sb.append("port="+data.get("port")+"").append("\n");		
      	sb.append("system-type =\"\"").append("\n");		
      	sb.append("enquire-link-interval=20").append("\n");		
    	sb.append("source-addr-ton=5").append("\n");		
      	sb.append("source-addr-npi=0").append("\n");		
      	sb.append("dest-addr-ton=1").append("\n");		
     	sb.append("dest-addr-npi=0").append("\n");		
      	sb.append("alt-charset = utf-8").append("\n");		
      	sb.append("smsc-username =\""+data.get("username")+"\"").append("\n");		
      	sb.append("smsc-password =\""+data.get("password")+"\"").append("\n");		
      	sb.append("transceiver-mode=true").append("\n");		
    	sb.append("log-file = \"/var/log/kannel/"+data.get("smscid")+".log\"").append("\n");		

      	
    	sb.append("\n");
    	sb.append("\n");
    	
    	return sb.toString();
	
		
	
		
	}
	private static String getTableConfig() {

		
		StringBuffer sb=new StringBuffer();
    	
		sb.append("\n");
    	sb.append("\n");
    	
    	sb.append("group = dlr-db").append("\n");	
    	sb.append("id = mytestdlr").append("\n");		
    	sb.append("table = dlr_unitia").append("\n");		
    	sb.append("field-smsc = smsc").append("\n");		
    	sb.append("field-timestamp = ts").append("\n");		
      	sb.append("field-destination = destination").append("\n");		
      	sb.append("field-source = source").append("\n");		
    	sb.append("field-service = service").append("\n");		
      	sb.append("field-url = url").append("\n");		
      	sb.append("field-mask = mask").append("\n");		
     	sb.append("field-status = status").append("\n");		
      	sb.append("field-boxc-id = boxc").append("\n");		
   	
    	sb.append("\n");
    	sb.append("\n");
    	
    	return sb.toString();
	
		
	}
	private static String getMysqlConfig() {
		
		StringBuffer sb=new StringBuffer();
    	
		sb.append("\n");
    	sb.append("\n");
    	
    	sb.append("group = mysql-connection").append("\n");	
    	sb.append("id = mytestdlr").append("\n");		
    	sb.append("host = unitiadb").append("\n");		
    	sb.append("username = root").append("\n");		
    	sb.append("password = kannel").append("\n");		
      	sb.append("database = mysql").append("\n");		
      	sb.append("max-connections = 35").append("\n");		
     	
    	sb.append("\n");
    	sb.append("\n");
    	
    	return sb.toString();
	}
	private static String getUserConfig() {
		
		StringBuffer sb=new StringBuffer();
    	
		sb.append("\n");
    	sb.append("\n");
    	
    	sb.append("group = sendsms-user").append("\n");	
    	sb.append("username = test").append("\n");		
    	sb.append("password = pass123").append("\n");		
    	sb.append("max-messages = 50").append("\n");		
    	sb.append("concatenation = true").append("\n");		

    	sb.append("\n");
    	sb.append("\n");
    	
    	return sb.toString();
	}
	private static String getSMSBoxConfig() {
		StringBuffer sb=new StringBuffer();
    	sb.append("\n");
    	sb.append("\n");
    	sb.append("group = smsbox").append("\n");	
    	sb.append("bearerbox-host = localhost").append("\n");		
    	sb.append("sendsms-port = 13013").append("\n");	
    	sb.append("immediate-sendsms-reply = true").append("\n");	
    	
    	sb.append("\n");
    	sb.append("\n");
    	return sb.toString();
    }
	
	private static String getCoreConfig() {

		StringBuffer sb=new StringBuffer();
    	sb.append("\n");
    	sb.append("\n");
    	sb.append("group = core").append("\n");	
    	sb.append("admin-port = 13000 ").append("\n");		
    	sb.append("admin-password = admin123").append("\n");		
    	sb.append("dlr-storage=mysql").append("\n");		
    	sb.append("smsbox-port = 13001").append("\n");		
    	sb.append("log-file = \"/var/log/kannel/bearerbox.log\"").append("\n");		
    	sb.append("log-level = 0").append("\n");
    	sb.append("access-log = \"/var/log/kannel/access.log\"").append("\n");		
    	sb.append("store-type = spool").append("\n");		
    	sb.append("store-location = /var/spool/kannel").append("\n");		
    	sb.append("sms-outcoming-queue-limit = 3500").append("\n");		

    	
    	
    	
    			

    	sb.append("\n");
    	sb.append("\n");
    	return sb.toString();
    	
    	
    			

	}
	
	
}


package kannelconfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.Kannel;
import com.winnovature.unitia.util.db.KannelDBConnection;
import com.winnovature.unitia.util.db.KannelStoreDBConnection;
import com.winnovature.unitia.util.db.RouteDBConnection;
import com.winnovature.unitia.util.db.TableExsists;



public class TableAvailability {
	
	 private static String MODE="";
		
		static {
			
			String mode=System.getenv("mode");
			
			if(mode==null||mode.trim().length()<1){
				
				MODE="production";
			}else{
				
				MODE=mode;
			}
			
			

		}


	private static final String DLR = "CREATE TABLE dlr_unitia (smsc varchar(40) DEFAULT NULL,ts varchar(65) DEFAULT NULL,destination varchar(40) DEFAULT NULL,source varchar(40) DEFAULT NULL,service varchar(40) DEFAULT NULL,url text,mask int(10) DEFAULT NULL,status int(10) DEFAULT NULL,boxc varchar(40) DEFAULT NULL,itime timestamp default CURRENT_TIMESTAMP ,KEY dlr_itime_index (itime),KEY dlr_smsc_index (smsc),KEY dlr_ts_index (ts)) ENGINE=InnoDB DEFAULT CHARSET=utf8";

	private static final String KANNEL_CONFIG = "CREATE TABLE kannel_config (smscid varchar(40) PRIMARY KEY,ip varchar(1000) NOT NULL,port decimal(10,0),username varchar(40) NOT NULL,password varchar(40) NOT NULL,sessioncount decimal(2,0) default 1,kannelid varchar(50) default 'kannel1',routeclass varchar(1),max_queue decimal(6,0) default '500',itime timestamp default CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8";

	private static TableAvailability pushAccount = new TableAvailability();
	
	

	private TableAvailability()
	{

		dlravailability();
		
		kannelconfigAvailability();
	}

	private void kannelconfigAvailability() {
		
		Connection connection = null;
		try {
			connection = RouteDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "kannel_config")){
				
					if (table.create(connection, KANNEL_CONFIG, false)) {

						table.create(connection, "insert into kannel_config(smscid,ip,port,username,password,routeclass) values('unitia','smpp','8080','unitia','unitia','1')", false);
					}
			}
			
			
		

		} catch (Exception e) {

			pushAccount=null;
			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	
	}

	private void dlravailability() {
		
		
		try {
			dlravailability( KannelDBConnection.getInstance().getConnection());
					

			Map<String,Properties> connprop=Kannel.getInstance().getKannelMysqlmap();
			Iterator itr=connprop.keySet().iterator();
			
			while(itr.hasNext()){
				String key=itr.next().toString();
				Properties prop=connprop.get(key);
				
				Connection conn=KannelStoreDBConnection.getInstance(key, prop).getConnection();
				dlravailability(conn);
				
			}

		} catch (Exception e) {

		
		} finally {
		}

	
	}

	private void dlravailability(Connection connection) {
		
		try {
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "dlr_unitia")){
				
					if (table.create(connection, DLR, false)) {

					}
			}
			
			
		

		} catch (Exception e) {

			pushAccount=null;
			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	
	}
	public static TableAvailability instance()
	{
		if(pushAccount == null){
			pushAccount = new TableAvailability();
		}
		return pushAccount;
	}
	
	
	public Map<String,List<Map<String,String>>> getSMSIDList(){
		
		Map<String,List<Map<String,String>>> map=new HashMap<String,List<Map<String,String>>>();
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		try{
			
			connection=RouteDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select * from kannel_config a,carrier_smscid_mapping b,carrier c where a.smscid = b.smscid COLLATE utf8_unicode_ci  and b.carrier = c.carrier and c.mode=?");
			statement.setString(1,MODE);
			resultset=statement.executeQuery();
			
			while(resultset.next()){
				
				Map<String,String> data=new HashMap<String,String>();
				
				data.put("smscid", resultset.getString("smscid"));
				data.put("ip", resultset.getString("ip"));
				data.put("port", resultset.getString("port"));
				data.put("username", resultset.getString("username"));
				data.put("password", resultset.getString("password"));
				data.put("sessioncount", resultset.getString("sessioncount"));
				data.put("kannelid", resultset.getString("kannelid"));

				List<Map<String,String>> list=map.get(resultset.getString("kannelid"));
				
				if(list==null){
					list=new ArrayList<Map<String,String>>();
					map.put(resultset.getString("kannelid"), list);
				}
				
				list.add(data);
			}
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		return map;
	}
	}

package com.winnovature.unitia.util.db;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.misc.kannel;




public class TableExsists {
	
	public static String CONJUNCTION="~";
	
	public static String ROUTE_TRANS="trans";

	public static String ROUTE_PROMO="promo";
	
	public static String NULL="null";


	private static final String SQL_TEMPLATE_USERWISE = "select username,count(*) from {0} group by username";
	private static final String WORKERPOOL_INSERT_2 = "insert into workerpool(poolname,pooltype) values(?,?)";
	private static final String WORKERPOOLROUTING_INSERT = "insert into workerpool_routing(poolname,poolclass) values(?,?)";
	private static final String TABLENAME = "select poolname,tablecount from workerpool";
	private static final String TABLENAME1 = "select distinct submission_tablename,dn_tablename,dnpost_tablename billingtable_routing";

	private static String SQL_TEMPLATE="select count(*) cnt from {0}";
	
	
	private static String getQuery(String tablename) {
		
		String params[]= {tablename};
		
		return MessageFormat.format(SQL_TEMPLATE, params);
		
	}
	
	public boolean isExsists(Connection connection,String tablename) {
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		try {
			
			statement=connection.prepareStatement(getQuery(tablename));
			resultset=statement.executeQuery();
			if(resultset.next()) {
				
				return true;
			}
		}catch(Exception e) {
		
		}finally{
			
			Close.close(statement);
			Close.close(resultset);
		}
		
		return false;
	}
	
	
 public long getCount(Connection connection,String tablename) {
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		try {
			
			statement=connection.prepareStatement(getQuery(tablename));
			resultset=statement.executeQuery();
			if(resultset.next()) {
				
				return resultset.getLong("cnt");
			}
		}catch(Exception e) {
			Close.close(statement);
			Close.close(resultset);
			e.printStackTrace();
			return -1;
		}
		
		return 0;
	}
	
	public static void main(String args[]) {
		
		
		String poolname="trans_redisreader";
		System.out.print(poolname.substring(0,poolname.indexOf("_redisreader")));
		System.out.println(getQuery("test"));
	}

	public boolean create(Connection connection, String sql,boolean islock) {
		
		PreparedStatement statement=null;
		PreparedStatement createstatement=null;

		PreparedStatement insertstatement=null;

		String sqlstatement=null;
		String sqlcreatestatement=null;
		String sqlinsertstatement=null;

		try {
			sqlstatement=sql;
			statement=connection.prepareStatement(sql);
			statement.execute();
			if(islock) {
				sqlcreatestatement=SQLQuery.getSQLForLockCreate(SQLQuery.getTableName(sql)+"_lock");
				createstatement=connection.prepareStatement(sqlcreatestatement);
				createstatement.execute();
				sqlinsertstatement=SQLQuery.getSQLForLockInsert(SQLQuery.getTableName(sql)+"_lock");
				insertstatement=connection.prepareStatement(sqlinsertstatement);
				insertstatement.execute(); 
			}
			return true;
			
		}catch(Exception e) {
			e.printStackTrace();
			
			System.err.print("sqlstatement :"+sqlstatement);
			System.err.print("sqlcreatestatement :"+sqlcreatestatement);
			System.err.print("sqlinsertstatement :"+sqlinsertstatement);

			Close.close(statement);
			Close.close(insertstatement);
			Close.close(createstatement);

		}
	
		return false;
	}

	public void insertCircle(Connection connection,String code,String name,String category,String coverd_area) {
		
		PreparedStatement statement=null;

		try {
			
			statement=connection.prepareStatement(SQLQuery.INSERT_CIRCLE_TABLE);
			statement.setString(1, code);
			statement.setString(2, name);
			statement.setString(3, category);
			statement.setString(4, coverd_area);

			statement.execute();
			
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);

		}
	
	}
	
	public void insertOperator(Connection connection,String code,String network) {
		
		PreparedStatement statement=null;

		try {
			
			statement=connection.prepareStatement(SQLQuery.INSERT_OPERATOR_TABLE);
			statement.setString(1, code);
			statement.setString(2, network);
			statement.execute();
			
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);

		}
	
	}
	
	
	public void insertNumberingPlan(Connection connection,String series,String operator,String circle) {
		
		PreparedStatement statement=null;

		try {
			
			statement=connection.prepareStatement(SQLQuery.INSERT_NUMBERINGPLAN_TABLE);
			statement.setString(1, series.trim());
			statement.setString(2, operator.trim());
			statement.setString(3, circle.trim());

			statement.execute();
			
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);

		}
	
	}
	public Set<String> getAccount(Connection connection,boolean isSchedule) {
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Set<String> account=new HashSet();
		try {
			if(isSchedule) {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_ACCOUNT_TABLE_SCHEDULE_RELEASE);

			}else {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_ACCOUNT_TABLE);

			}
			resultset=statement.executeQuery();
			while(resultset.next()) {
				
				account.add(resultset.getString("username").toLowerCase());
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return account;
	}

	public Map<String, Map<String, String>> getScheduleAccount(Connection connection) {

		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String, Map<String, String>> account=new HashMap<String, Map<String, String>>();
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_ACCOUNT_TABLE_SCHEDULE);

			resultset=statement.executeQuery();

			while(resultset.next()) {
				HashMap data=new HashMap();
				data.put("start", resultset.getString("block_start"));
				data.put("end", resultset.getString("block_start"));

				account.put(resultset.getString("username").toLowerCase(),data);
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return account;
	
	}

	public Map<String, Map<String, String>> getNP(Connection connection) {

		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String, Map<String, String>> account=new HashMap<String, Map<String, String>>();
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_NP_TABLE);

			resultset=statement.executeQuery();

			while(resultset.next()) {
				
				String series=resultset.getString("series");
				String operator=resultset.getString("operator");
				String circle=resultset.getString("circle");

				if(operator==null) {
					operator="";
				}
				
				if(circle==null) {
					circle="";
				}
				
				HashMap data=new HashMap();
				data.put("operator", operator);
				data.put("circle", circle);

				account.put(series,data);
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return account;		
	}
	public void insertKannel(Connection connection, String smscid,String ip, int port, String routeclass) {

		PreparedStatement statement=null;

		try {
			
			statement=connection.prepareStatement(SQLQuery.INSERT_SMSCID_TABLE);
			statement.setString(1, smscid);
			statement.setString(2, ip);
			statement.setInt(3, port);
			statement.setString(4, routeclass);
			statement.execute();
			
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);

		}
	}

	public void insertRouteGroup(Connection connection, String groupname, String smscid) {

		PreparedStatement statement=null;

		try {
			
			statement=connection.prepareStatement(SQLQuery.INSERT_ROUTEGROUP_TABLE);
			statement.setString(1, groupname);
			statement.setString(2, smscid);

			statement.execute();
			
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);

		}
	}

	public void insertRoute(Connection connection, String routegrouptrans,String routegrouppromo,String username,String superadmin, String admin, String operator,
			String circle) {
		PreparedStatement statement=null;

		try {
			
			statement=connection.prepareStatement(SQLQuery.INSERT_ROUTE_TABLE);
			statement.setString(1, routegrouptrans);
			statement.setString(2, routegrouppromo);

			statement.setString(3,superadmin);
			statement.setString(4, admin);
			statement.setString(5, username);
			statement.setString(6, operator);
			statement.setString(7, circle);
			statement.execute();
			
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);

		}
	}

	public void insertKannelLB(Connection connection, String ip) {
		PreparedStatement statement=null;

		try {
			
			statement=connection.prepareStatement(SQLQuery.INSERT_KANNEL_LOADBALANCER);
			statement.setString(1, ip);		
			statement.execute();
			
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);

		}		
	}

	public void insertDnLB(Connection connection, String ip, int port) {
		PreparedStatement statement=null;

		try {
			
			statement=connection.prepareStatement(SQLQuery.INSERT_DN_LOADBALANCER);
			statement.setString(1, ip);	
			statement.setInt(2, port);		
			statement.execute();
			
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);

		}
		
	}

	public void insertVsmscLB(Connection connection, String ip, int port) {
		PreparedStatement statement=null;

		try {
			
			statement=connection.prepareStatement(SQLQuery.INSERT_VSMSC_LOADBALANCER);
			statement.setString(1, ip);	
			statement.setInt(2, port);		
			statement.execute();
			
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);

		}
		
	}

	public Map<String,  Map<String,Map<String, String>>> getSplitGroup(Connection connection) {


		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,  Map<String,Map<String, String>>> splitgroup=new HashMap<String,  Map<String,Map<String, String>>> ();
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_SPLITGROUP_TABLE);

			resultset=statement.executeQuery();

			while(resultset.next()) {
				
				String groupname=resultset.getString("groupname");
				String msgtype=resultset.getString("msgtype");
				String splitlength=resultset.getString("splitlength");
				String maxlength=resultset.getString("maxlength");

				if(groupname!=null&&msgtype!=null&&splitlength!=null&&maxlength!=null) {
				
					Map<String,Map<String, String>> msgtypemap=splitgroup.get(groupname.trim());
					if(msgtypemap==null) {
						msgtypemap=new HashMap<String,Map<String, String>>();
						splitgroup.put(groupname.trim(), msgtypemap);
					}
					Map<String,String> data=new HashMap<String,String>();
					data.put("splitlength", splitlength);
					data.put("maxlength", maxlength);
					msgtypemap.put(msgtype.trim(), data);

				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return splitgroup;		
	
		
	}

	public Map<String, Map<String, String>> getKannel(Connection connection) {


		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String, Map<String,String>> kannel=new HashMap<String,Map<String, String>> ();
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_KANNEL_TABLE);

			resultset=statement.executeQuery();

			while(resultset.next()) {
				
				String smscid=resultset.getString("smscid");
				String port=resultset.getString("port");
				String ip=resultset.getString("ip");
				String routeclass=resultset.getString("routeclass");
				
				if(smscid!=null&&port!=null) {
				

					Map<String,String> data=new HashMap<String,String>();
					data.put("kannel_port", port);
					data.put("kannel_ip", ip.trim());
					data.put("routeclass", routeclass.trim());
					kannel.put(smscid.trim(), data);

				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return kannel;		
	
		
	}
	
	
	public Map<String, Map<String, String>> getInternalKannel(Connection connection) {


		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String, Map<String,String>> kannel=new HashMap<String,Map<String, String>> ();
		Map<String,String> data1=new HashMap<String,String>();
		data1.put("kannel_port", "8080");
		data1.put("kannel_ip", "dngen1");
		data1.put("routeclass", "4");

		kannel.put("apps", data1);
		kannel.put("reapps", data1);
		
		try {
				
				statement=connection.prepareStatement("select smscid,kannelid,routeclass from kannel_config");

			resultset=statement.executeQuery();

			while(resultset.next()) {
				
				String smscid=resultset.getString("smscid");
				String kannelid=resultset.getString("kannelid");
				String routeclass=resultset.getString("routeclass");

				

					Map<String,String> data=new HashMap<String,String>();
					data.put("kannel_port", "13013");
					data.put("kannel_ip", kannelid);
					data.put("routeclass", routeclass);

					kannel.put(smscid.trim(), data);

				
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return kannel;		
	
		
	}
	

	
	public Map<String, Map<String, String>> getCreditDomestic(Connection connection) {

		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,  Map<String, String>> routegroup=new HashMap<String, Map<String, String>> ();
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_CREDIT_DOMESTIC_TABLE);

			resultset=statement.executeQuery();

			while(resultset.next()) {
				
				String superadmin=resultset.getString("superadmin");
				String admin=resultset.getString("admin");
				String username=resultset.getString("username");
				String credit_trans=resultset.getString("credit_trans");
				String credit_promo=resultset.getString("credit_promo");

				String operator= resultset.getString("operator");
                String circle= resultset.getString("circle");
			
                if(superadmin==null||superadmin.trim().length()<1) {
					superadmin=NULL;
				}
				
                superadmin=superadmin.toLowerCase();
                
				if(admin==null||admin.trim().length()<1) {
					admin=NULL;
				}
				
				admin=admin.toLowerCase();
				
				if(username==null||username.trim().length()<1) {
					username=NULL;
				}
				
				username=username.toLowerCase();
				
				if(operator==null||operator.trim().length()<1) {
					operator=NULL;
				}
				
				if(circle==null||circle.trim().length()<1) {
					circle=NULL;
				}
				
				 Map<String, String> data=new HashMap<String, String>();
				 data.put(ROUTE_TRANS,credit_trans.trim());
				 data.put(ROUTE_PROMO,credit_promo.trim());

				routegroup.put(CONJUNCTION+superadmin.trim()+CONJUNCTION+admin.trim()+CONJUNCTION+username.trim()+CONJUNCTION+operator.trim()+CONJUNCTION+circle.trim()+CONJUNCTION,data);
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return routegroup;
	
	}


	public  Map<String, String> getCreditInternational(Connection connection) {

		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String, String> routegroup=new HashMap<String,  String> ();
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_CREDIT_INTERNATIONAL_TABLE);

			resultset=statement.executeQuery();

			while(resultset.next()) {
				
				String superadmin=resultset.getString("superadmin");
				String admin=resultset.getString("admin");
				String username=resultset.getString("username");
				String credit=resultset.getString("credit");

				String countrycode= resultset.getString("countrycode");
			
                if(superadmin==null||superadmin.trim().length()<1) {
					superadmin=NULL;
				}
				
                superadmin=superadmin.toLowerCase();
                
				if(admin==null||admin.trim().length()<1) {
					admin=NULL;
				}
				
				admin=admin.toLowerCase();
				
				if(username==null||username.trim().length()<1) {
					username=NULL;
				}
				
				username=username.toLowerCase();
			
				
				if(countrycode==null||countrycode.trim().length()<1) {
					countrycode=NULL;
				}
				
				String key=CONJUNCTION+superadmin.trim()+CONJUNCTION+admin.trim()+CONJUNCTION+username.trim()+CONJUNCTION+countrycode.trim()+CONJUNCTION;
				
				routegroup.put(key,credit);
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return routegroup;
	
	}


	
	public Map<String, List<String>> getRouteGroup(Connection connection) {

		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String, List<String>> routegroup=new HashMap<String,List<String>> ();
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_ROUTEGROUP_TABLE);

			resultset=statement.executeQuery();

			while(resultset.next()) {
				
				String smscid=resultset.getString("smscid");
				String groupname=resultset.getString("groupname");

				String auto_reroute=resultset.getString("auto_reroute");
				
				if(groupname!=null&&smscid!=null) {
					
					List<String> smscidlist=routegroup.get(groupname.trim());
					
					if(smscidlist==null) {
						
						smscidlist=new ArrayList<String>();
						routegroup.put(groupname.trim(), smscidlist);
					}
					if(auto_reroute.equals("1")){
				
						if(!kannel.isQueued(smscid)){
							smscidlist.add(smscid);	
						}
					}else{
					smscidlist.add(smscid);	
				
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return routegroup;
	
	}


	public String getKannelIP(Connection connection) {


		PreparedStatement statement=null;
		ResultSet resultset=null;
		String routegroup=null;
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_KANNEL_LB_IP_TABLE);

			resultset=statement.executeQuery();

			if(resultset.next()) {
				return resultset.getString("ip");
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return routegroup;
	
	
	}

	public Map<String, String> getDnIp(Connection connection) {


		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,String> ipport=new HashMap<String,String>();
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_DN_LB_IP_TABLE);

			resultset=statement.executeQuery();

			if(resultset.next()) {
				 ipport.put("ip", resultset.getString("ip"));
				 ipport.put("port", resultset.getString("port"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return ipport;
	
	
	}

	public Map<String, String> getVsmscIp(Connection connection) {


		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,String> ipport=new HashMap<String,String>();
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_VSMSC_LB_IP_TABLE);

			resultset=statement.executeQuery();

			if(resultset.next()) {
				
				 ipport.put("ip", resultset.getString("ip"));
				 ipport.put("port", resultset.getString("port"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return ipport;
	
	
	}

	public void insertHttpLB(Connection connection, String ip, int port) {
		PreparedStatement statement=null;

		try {
			
			statement=connection.prepareStatement(SQLQuery.INSERT_HTTP_LOADBALANCER);
			statement.setString(1, ip);	
			statement.setInt(2, port);		
			statement.execute();
			
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);

		}
		
	}

	public Map<String, String> getHttpIp(Connection connection) {


		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,String> ipport=new HashMap<String,String>();
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_HTTP_LB_IP_TABLE);

			resultset=statement.executeQuery();

			if(resultset.next()) {
				
				 ipport.put("ip", resultset.getString("ip"));
				 ipport.put("port", resultset.getString("port"));
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return ipport;
	
	
	}

	public Map<String, Map<String, String>> getAccountCredential(Connection connection, boolean b) {




		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String, Map<String, String>> account=new HashMap<String, Map<String, String>>();
		
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_CREDENTIAL_TABLE);

			resultset=statement.executeQuery();

			while(resultset.next()) {
				
				String username=resultset.getString("username").trim().toLowerCase();
				String password=resultset.getString("password");
				String auth_type=resultset.getString("auth_type");
				String status=resultset.getString("status");
				String max_bind=resultset.getString("max_bind");
				String max_sms_thread=resultset.getString("max_sms_thread");
				String max_dn_thread=resultset.getString("max_dn_thread");

				Map<String, String> data=account.get(username);
				
				if(data==null) {
					
					data=new HashMap<String, String>();
					account.put(username, data);
				}
				
				data.put("password", getPassword(password));
				data.put("auth_type", auth_type);
				data.put("status", status);
				data.put("max_bind", max_bind);
				data.put("max_sms_thread", max_sms_thread);
				data.put("max_dn_thread", max_dn_thread);
				
				

				 
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return account;
	
	
	}

	private String getPassword(String password) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, List<String>> getAccountUserAuthorizedIps(Connection connection, boolean b) {

		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String, List<String>> account=new HashMap<String, List<String>>();
		
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_AUTHORIZED_IP_TABLE);

			resultset=statement.executeQuery();

			while(resultset.next()) {
				
				String username=resultset.getString("username").trim().toLowerCase();
				String ip_pattern=resultset.getString("ip_pattern ").trim().toLowerCase();;
			

				List<String> data=account.get(username);
				
				if(data==null) {
					
					data=new ArrayList<String>();
					account.put(username, data);
				}
				
				data.add(ip_pattern);

				 
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return account;
	
	
	}

	public Map<String, List<String>> getAppsAuthorizedIps(Connection connection, boolean b) {

		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String, List<String>> account=new HashMap<String, List<String>>();
		
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_APPS_AUTHORIZED_IP_TABLE);

			resultset=statement.executeQuery();

			while(resultset.next()) {
				
				String username=resultset.getString("username").trim().toLowerCase();
				String ip_pattern=resultset.getString("ip_pattern ").trim().toLowerCase();;
			

				List<String> data=account.get(username);
				
				if(data==null) {
					
					data=new ArrayList<String>();
					account.put(username, data);
				}
				
				data.add(ip_pattern);

				 
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return account;
	
	}

	public String getTableName(String superadminSql) {

		StringTokenizer st=new StringTokenizer(superadminSql," ");
		st.nextToken();
		st.nextToken();
		String tablename =st.nextToken();
		if(tablename.indexOf("(")>-1){
			
			tablename=tablename.substring(0,tablename.indexOf("("));
		}
		
		return tablename;
	}

public void persistQueueCounttoDB(Map<String,String> queuemap,String queuetype,String dbttype){
		
		Connection connection=null;
		PreparedStatement statement=null;
		
		try{
			
			
			if(queuemap!=null&&queuemap.size()>0){
				
				long id=System.currentTimeMillis();
				String ip=InetAddress.getLocalHost().getHostAddress();
				String sql="insert into queueu_history(ip,insertid,queuename,qtype,dbtype,count) values(?,?,?,?,?,?)";
			
				connection=CoreDBConnection.getInstance().getConnection();
				statement =connection.prepareStatement(sql);
				connection.setAutoCommit(false);
				Iterator itr=queuemap.keySet().iterator();
				
				while(itr.hasNext()){
				
					String queuename=itr.next().toString();
					String count=queuemap.get(queuemap);
					
					statement.setString(1, ip);
					statement.setString(2, ""+id);
					statement.setString(3, queuename);
					statement.setString(4, queuetype);
					statement.setString(5, dbttype);
					statement.setString(6, count);
					statement.addBatch();
				}
				
				statement.executeBatch();
				
			}
		}catch(Exception e){
			
			
		}finally{
			
			Close.close(statement);
			
			Close.close(connection);
		}
	}


private void add(Connection connection,Set<String> tableset,Map<String,Map<String,String>> map) {
	
	Iterator itr=tableset.iterator();
	
	while(itr.hasNext()){
		
		String tablename=itr.next().toString();
		
		map.put(tablename, getUserwiseCount(connection,tablename));
	
		
	}
	
}

private Map<String, String> getUserwiseCount(Connection connection, String tablename) {

	
	PreparedStatement statement=null;
	ResultSet resultset=null;
	Map<String,String> map=new HashMap<String,String>();
	try {
		
		statement=connection.prepareStatement(getUserwiseQuery(tablename));
		resultset=statement.executeQuery();
		while(resultset.next()) {
			
			map.put(resultset.getString("username"), resultset.getString("cnt"));
		}
	}catch(Exception e) {
		Close.close(statement);
		Close.close(resultset);
		
	}
	
	return map;

}

private String getUserwiseQuery(String tablename) {
	String params[]= {tablename};
	
	return MessageFormat.format(SQL_TEMPLATE_USERWISE, params);
}

public void persistQueueCounttoDB(Connection connection,long id, String queuename, String username, String count) {
	

	
	PreparedStatement statement=null;
	
	try{
		
		
			
			String ip=InetAddress.getLocalHost().getHostAddress();
			String sql="insert into queueu_history(ip,insertid,queuename,qtype,dbtype,count) values(?,?,?,?,?,?)";
		
			connection=CoreDBConnection.getInstance().getConnection();
			statement =connection.prepareStatement(sql);
		
				
				statement.setString(1, ip);
				statement.setString(2, ""+id);
				statement.setString(3, queuename);
				statement.setString(4, username);
				statement.setString(5, "mysql");
				statement.setString(6, count);
						
			statement.execute();
			
		
	}catch(Exception e){
		
		
	}finally{
		
		Close.close(statement);
		Close.close(connection);
	}

}

public void insertworkerpool(Connection connection) {
	
	/*
	insert(connection, "billingpool", "billing");
	insert(connection, "transpool", "sms");
	insert(connection, "promopool", "sms");
	insert(connection, "credittranspool", "sms");
	insert(connection, "creditpromopool", "sms");
	insert(connection, "bulktranspool", "sms");
	insert(connection, "bulkpromopool", "sms");
	insert(connection, "otppool", "sms");
	insert(connection, "optinpool", "sms");
	insert(connection, "duplicatepool", "sms");
	insert(connection, "httpdnpostpool", "dnhttppost");
	insert(connection, "dngenpool", "dngen");
	insert(connection, "dnreceiverpool", "dnreceiver");
	
	*/
	insert(connection, "billingpool_redisreader", "billing");
	insert(connection, "transpool_redisreader", "sms");
	insert(connection, "promopool_redisreader", "sms");
	insert(connection, "credittranspool_redisreader", "sms");
	insert(connection, "creditpromopool_redisreader", "sms");
	insert(connection, "bulktranspool_redisreader", "sms");
	insert(connection, "bulkpromopool_redisreader", "sms");
	insert(connection, "otppool_redisreader", "sms");
	insert(connection, "optinpool_redisreader", "sms");
	insert(connection, "duplicatepool_redisreader", "sms");
	insert(connection, "httpdnpostpool_redisreader", "dnhttppost");
	insert(connection, "dngenpool_redisreader", "dngen");
	insert(connection, "dnreceiverpool_redisreader", "dnreceiver");

}

private void insert(Connection connection,String poolname,String pooltype){
	
	PreparedStatement statement=null;
	
	try{
		
		statement=connection.prepareStatement(WORKERPOOL_INSERT_2);
		
		statement.setString(1, poolname);
		statement.setString(2, pooltype);
		statement.execute();

	}catch(Exception e){
		
	}finally{
		
		Close.close(statement);
	}
	
}

public void insertworkerroutingpool(Connection connection) {

	insertworkerroutingpool(connection, "transpool", "1");
	insertworkerroutingpool(connection, "promopool", "2");
	insertworkerroutingpool(connection, "credittranspool", "3");
	insertworkerroutingpool(connection, "creditpromopool", "4");
	insertworkerroutingpool(connection, "bulktranspool", "5");
	insertworkerroutingpool(connection, "bulkpromopool", "6");
	insertworkerroutingpool(connection, "otppool", "7");
	insertworkerroutingpool(connection, "optinpool", "8");
	insertworkerroutingpool(connection, "optoutpool", "9");
	insertworkerroutingpool(connection, "duplicatepool", "10");
	insertworkerroutingpool(connection, "schedulepool", "10");
	insertworkerroutingpool(connection, "blockoutpool", "10");
	insertworkerroutingpool(connection, "traipool", "10");
	insertworkerroutingpool(connection, "otpretrypool", "10");
	insertworkerroutingpool(connection, "dnretrypool", "10");
	insertworkerroutingpool(connection, "httpdnpostpool", "11");

	
}

private void insertworkerroutingpool(Connection connection, String poolname, String poolclass) {
PreparedStatement statement=null;
	
	try{
		
		statement=connection.prepareStatement(WORKERPOOLROUTING_INSERT);
		
		statement.setString(1, poolname);
		statement.setString(2, poolclass);
		statement.execute();

	}catch(Exception e){
		
	}finally{
		
		Close.close(statement);
	}
	
}

public Map<String,String> getPoolTableName(){
	
	Connection connection=null;
	Map<String,String> tablemap=new HashMap<String,String>();
	try{
		
		tablemap.putAll(getCountMap(connection));
		tablemap.putAll(getCountMap1(connection));

		
	}catch(Exception e){
		
	}finally{
		
		Close.close(connection);
	}
	return tablemap;
}

private Map<String, String> getCountMap1(Connection connection) {
	
	PreparedStatement statement=null;
	ResultSet resultset=null;
	Map<String,String> tablemap=new HashMap<String,String>();
	tablemap.put("submission_default","1" );
	tablemap.put("dn_default","1" );
	tablemap.put("dnpost_default","1" );


	try {
		
		statement=connection.prepareStatement(TABLENAME1);
		resultset=statement.executeQuery();
		while(resultset.next()) {
			
			tablemap.put(resultset.getString("submission_tablename"),"1" );
			tablemap.put(resultset.getString("dn_tablename"),"1" );
			tablemap.put(resultset.getString("dnpost_tablename"),"1" );
		}
		
	}catch(Exception e) {
		Close.close(statement);
		Close.close(resultset);
	}
	
	return tablemap;

}

private Map<String, String> getCountMap(Connection connection) {
	
	PreparedStatement statement=null;
	ResultSet resultset=null;
	Map<String,String> tablemap=new HashMap<String,String>();

	try {
		
		statement=connection.prepareStatement(TABLENAME);
		resultset=statement.executeQuery();
		while(resultset.next()) {
			
			tablemap.put(resultset.getString("poolname"),resultset.getString("1") );
		}
	}catch(Exception e) {
		Close.close(statement);
		Close.close(resultset);
		
	}
	
	return tablemap;
}

public void insertjvmid(Connection connection) {
	
	PreparedStatement statement=null;
	
	try{
		
		connection.setAutoCommit(false);
		statement=connection.prepareStatement("insert into interface_jvm_uniqueid(id,updateid)values(?,?)");
		
		for(int i=100;i<1000;i++){
			
			statement.setString(1, ""+i);
			statement.setString(2, "0");
			statement.addBatch();

		}
		statement.executeBatch();
		connection.commit();
		
	}catch(Exception e){
		
	}finally{
		
		Close.close(statement);
	}
	
}

public void insertReroute(Connection connection, String smscid, String reroutesmscid) {

	PreparedStatement statement=null;
	
	try{
		
		statement=connection.prepareStatement("insert into rerouting(username,smscid,reroute_smscid) values(?,?,?)");
		
		statement.setString(1, "");
		statement.setString(2, smscid);
		statement.setString(3, reroutesmscid);

		statement.execute();

	}catch(Exception e){
		
	}finally{
		
		Close.close(statement);
	}
	
}

public void insertCountryCode(Connection connection, String countryname, String countrycode) {
	


	PreparedStatement statement=null;
	
	try{
		
		statement=connection.prepareStatement("insert into countrycode(countryname,countrycode) values(?,?)");
		
		statement.setString(1, countryname);
		statement.setString(2, countrycode);

		statement.execute();

	}catch(Exception e){
		
	}finally{
		
		Close.close(statement);
	}
	

	
}

public Map<String, String> getCountrycode(Connection connection) {
	PreparedStatement statement=null;
	ResultSet resultset=null;
	Map<String,String> countrycodemap=new HashMap<String,String>();
 	try{
		statement=connection.prepareStatement("select countrycode,countryname from countrycode");
		resultset=statement.executeQuery();
		
		while(resultset.next()){
			
			String countrycode=resultset.getString("countrycode").trim();
			String countryname=resultset.getString("countryname").trim();
			countrycodemap.put(countrycode, countryname);
		}
	}catch(Exception e){
		
	}finally{
		
		Close.close(statement);
		Close.close(resultset);
	}
 	
 	return countrycodemap;
}


public void insertMessageStatus(Connection connection, String statusid, String statusdescription, String carrier,
		String stat, String err) {
	PreparedStatement statement=null;
	try{
		statement=connection.prepareStatement("insert into message_status(status_id,status_description,carrier,stat,err) values(?,?,?,?,?)");
		statement.setString(1, statusid);
		statement.setString(2, statusdescription);
		statement.setString(3, carrier);
		statement.setString(4, stat);
		statement.setString(5, err);
		statement.execute();
		
	}catch(Exception e){
		
		e.printStackTrace();
		
	}finally{
		
		Close.close(statement);
	}
}

public void insertMessageStatus(String carrier, String errorcode, String stat) {
	
	Connection connection=null;
	PreparedStatement statement=null;
	try{
		connection=CoreDBConnection.getInstance().getConnection();
		statement=connection.prepareStatement("insert into message_status(carrier,stat,err,status_description) values(?,?,?,?)");
	
		statement.setString(1, carrier);
		statement.setString(2, stat);
		statement.setString(3, errorcode);
		statement.setString(4, "SMS Delivery Failed Due to Carrier side Rejection");
		
		statement.execute();
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		Close.close(statement);
		Close.close(connection);
		
	}
	
}

public void insertKannelMessageStatus(Connection connection, String statusid, String dr) {

	PreparedStatement statement=null;
	try{
		statement=connection.prepareStatement("insert into kannel_message_status(statusid,dr) values(?,?)");

		statement.setString(1, statusid);
		statement.setString(2, dr);
		statement.execute();
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		Close.close(connection);
	}
}

public Map<String, String> getOperator(Connection connection) {

	PreparedStatement statement=null;
	ResultSet resultset=null;
	Map<String, String> operator=new HashMap<String,String> ();
	try {
			
			statement=connection.prepareStatement("select code,network from operator");

		resultset=statement.executeQuery();

		while(resultset.next()) {
			
			String code=resultset.getString("code");
			String network=resultset.getString("network");
			operator.put(code,network);
		}
	}catch(Exception e) {
		e.printStackTrace();
		Close.close(statement);
		Close.close(resultset);
		return null;
	}
	
	return operator;

}

public Map<String, String> getCircle(Connection connection) {



	PreparedStatement statement=null;
	ResultSet resultset=null;
	Map<String, String> circle=new HashMap<String,String> ();
	try {
			
			statement=connection.prepareStatement("select code,name from circle");

		resultset=statement.executeQuery();

		while(resultset.next()) {
			
			String code=resultset.getString("code");
			String name=resultset.getString("name");
			circle.put(code,name);
		}
	}catch(Exception e) {
		e.printStackTrace();
		Close.close(statement);
		Close.close(resultset);
		return null;
	}
	
	return circle;


}

public void updatePoolSize(Connection connection, String poolname, String currentPoolSize) {
	PreparedStatement statement=null;
	try{
		statement=connection.prepareStatement("update workerpool set currentpoolsize=? where poolname=?");
		statement.setString(1, currentPoolSize);
		statement.setString(2, poolname);
		statement.executeUpdate();
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		
		Close.close(statement);
	}
	
}

public void insertCredit(Connection connection, double d, double e, String superadmin, String admin, String username,
		String operator, String circle) {
	

	PreparedStatement statement=null;

	try {
		
		statement=connection.prepareStatement(SQLQuery.INSERT_CREDIT_DOMESTIC_TABLE);
		statement.setDouble(1, d);
		statement.setDouble(2, e);

		statement.setString(3,superadmin);
		statement.setString(4, admin);
		statement.setString(5, username);
		statement.setString(6, operator);
		statement.setString(7, circle);
		statement.execute();
		
	}catch(Exception e1) {
		e1.printStackTrace();
		Close.close(statement);

	}

}


public void insertCreditInternational(Connection connection, double d, String superadmin, String admin, String username,
		String countrycode) {
	

	PreparedStatement statement=null;

	try {
		
		statement=connection.prepareStatement(SQLQuery.INSERT_ROUTE_TABLE);
		statement.setDouble(1, d);

		statement.setString(2,superadmin);
		statement.setString(3, admin);
		statement.setString(4, username);
		statement.setString(5, countrycode);
		statement.execute();
		
	}catch(Exception e1) {
		e1.printStackTrace();
		Close.close(statement);

	}

}

public void createLockTable(Connection connection, String username) {

	PreparedStatement createstatement=null;

	PreparedStatement insertstatement=null;
	String sqlcreatestatement=null;
	String sqlinsertstatement=null;

	try {
			sqlcreatestatement=SQLQuery.getSQLForLockCreate(username+"_lock");
			createstatement=connection.prepareStatement(sqlcreatestatement);
			createstatement.execute();
			sqlinsertstatement=SQLQuery.getSQLForLockInsert(username+"_lock");
			insertstatement=connection.prepareStatement(sqlinsertstatement);
			insertstatement.execute(); 
		
		
	}catch(Exception e) {
		e.printStackTrace();
		
		System.err.print("sqlcreatestatement :"+sqlcreatestatement);
		System.err.print("sqlinsertstatement :"+sqlinsertstatement);

		Close.close(insertstatement);
		Close.close(createstatement);

	}
	
}

public Map<String, Map<String, String>> getRoute(Connection connection) {

		PreparedStatement statement=null;
		ResultSet resultset=null;
		Map<String,  Map<String, String>> routegroup=new HashMap<String, Map<String, String>> ();
		try {
				
				statement=connection.prepareStatement(SQLQuery.SELECT_ROUTE_TABLE);

			resultset=statement.executeQuery();

			while(resultset.next()) {
				
				String superadmin=resultset.getString("superadmin");
				String admin=resultset.getString("admin");
				String username=resultset.getString("username");
				String groupname_trans=resultset.getString("routegroup_trans");
				String groupname_promo=resultset.getString("routegroup_promo");

				String operator= resultset.getString("operator");
                String circle= resultset.getString("circle");
			
                if(superadmin==null||superadmin.trim().length()<1) {
					superadmin=Route.NULL;
				}
				
                superadmin=superadmin.toLowerCase();
                
				if(admin==null||admin.trim().length()<1) {
					admin=Route.NULL;
				}
				
				admin=admin.toLowerCase();
				
				if(username==null||username.trim().length()<1) {
					username=Route.NULL;
				}
				
				username=username.toLowerCase();
				
				if(operator==null||operator.trim().length()<1) {
					operator=Route.NULL;
				}
				
				if(circle==null||circle.trim().length()<1) {
					circle=Route.NULL;
				}
				
				 Map<String, String> data=new HashMap<String, String>();
				 data.put(Route.ROUTE_TRANS,groupname_trans.trim());
				 data.put(Route.ROUTE_PROMO,groupname_promo.trim());

				routegroup.put(Route.CONJUNCTION+superadmin.trim()+Route.CONJUNCTION+admin.trim()+Route.CONJUNCTION+username.trim()+Route.CONJUNCTION+operator.trim()+Route.CONJUNCTION+circle.trim()+Route.CONJUNCTION,data);
			}
		}catch(Exception e) {
			e.printStackTrace();
			Close.close(statement);
			Close.close(resultset);
			return null;
		}
		
		return routegroup;
	
	}


}

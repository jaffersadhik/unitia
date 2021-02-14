package com.winnovature.unitia.util.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.MD5;
import com.winnovature.unitia.util.misc.ResultSetToHashMapConverter;



public class PushAccount {

	 private static String MODE="";
		
		static {
			
			String mode=System.getenv("mode");
			
			if(mode==null||mode.trim().length()<1){
				
				MODE="production";
			}else{
				
				MODE=mode;
			}
			
			

		}

	private int i=0;
	private static final String SUPERADMIN_SQL = "create table superadmin(superadmin_id INT PRIMARY KEY AUTO_INCREMENT,superadmin_name varchar(16),superadmin_password varchar(8),mode varchar(50) default 'production' ,itime timestamp default CURRENT_TIMESTAMP)";

	private static final String ADMIN_SQL = "create table admin(admin_id INT PRIMARY KEY AUTO_INCREMENT,superadmin_id INT,admin_name varchar(16),admin_password varchar(8),itime timestamp default CURRENT_TIMESTAMP,CONSTRAINT fk_admin FOREIGN KEY (superadmin_id) REFERENCES superadmin(superadmin_id) ON DELETE CASCADE ON UPDATE CASCADE)";

	private static final String CHANGE_SQL = "create table password_change_log(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(16),oldpassword varchar(8),newpassword varchar(8),itime datetime default CURRENT_TIMESTAMP)";

	
	private static String USER_SQL = null;


	static {

		StringBuffer sb = new StringBuffer();
		sb.append("create table users( ");
		sb.append("userid INT PRIMARY KEY AUTO_INCREMENT, ");
		sb.append("auth_id varchar(11) default '0', ");
		sb.append("username  varchar(16), ");
		sb.append("password varchar(8), ");
		sb.append("status varchar(1) default '1', ");
		sb.append("bill_type varchar(25) default 'postpaid', ");
		sb.append("admin_id INT, ");
		sb.append("smpp_maxbind varchar(2) default '0' ,");
		sb.append("msgclass varchar(1) default '1' ,");
		sb.append("otp_yn varchar(1) default '0', ");
		sb.append("otpretry_yn varchar(1) default '0', ");
		sb.append("dnretry_yn varchar(1) default '0', ");
		sb.append("intl varchar(1) default '0', ");
		sb.append("senderid_type varchar(15) default 'dynamic' , ");
		sb.append("senderid_trans varchar(15) , ");
		sb.append("senderid_promo varchar(15) , ");
		sb.append("prefix91 varchar(1) default '1', ");
		sb.append("schedule_yn varchar(1) default '0', ");
		sb.append("optin_type varchar(1) default '0', ");
		sb.append("duplicate_type varchar(1) default '0', ");
		sb.append("duplicate_lifetime_in_ms varchar(6) default '0', ");
		sb.append("blockout_yn varchar(1) default '0', ");
		sb.append("blockout_start varchar(5) default '00:00',");
		sb.append("blockout_end varchar(5) default '00:00', ");
		sb.append("db_encrypt_yn varchar(1) default '0', ");
		sb.append("encrypt_key varchar(50), ");
		sb.append("encrypt_algorithm varchar(50), ");
		sb.append("dlr_post_yn varchar(1) default '0', ");
		sb.append("dlr_post_url varchar(1000), ");
		sb.append("dlr_post_method varchar(6) default 'GET', ");
		sb.append("credit_rollback_yn varchar(1) default '0', ");
		sb.append("promo_reject_yn varchar(1) default '0', ");
		sb.append("itime timestamp default CURRENT_TIMESTAMP, ");
		sb.append("CONSTRAINT fk_u_admin_id FOREIGN KEY (admin_id) REFERENCES admin(admin_id) ON DELETE CASCADE ON UPDATE CASCADE) ");
		USER_SQL = sb.toString();

	}


	/*
	 * The Singleton Object
	 */
	private static PushAccount pushAccount = new PushAccount();
	
	/*
	 * The Memory Holder
	 */
	private Map<String,Map<String,String>> pushAccountMap = new HashMap();
	

	private PushAccount()
	{

		init();
		load();
	}

	private void init() {

		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "users")){
				loadsuperadmin(connection, table);
				loadadmin(connection, table);
				loaduser(connection, table);

			}
			
			
			if(!table.isExsists(connection, "password_change_log")){
				loadpasswordchangelog(connection, table);

			}

		} catch (Exception e) {

			pushAccount=null;
			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	}

	

	private void loadpasswordchangelog(Connection connection, TableExsists table) {
		if (!table.isExsists(connection, table.getTableName(CHANGE_SQL))) {
			if (table.create(connection, CHANGE_SQL, false)) {

			}
		}
		
	}

	private synchronized void load() 
	{
		pushAccountMap = loadPushAccountInfo();	
	}
	public synchronized void reload()
	{
		//load();
		Map _tmpPushAcc = loadPushAccountInfo();
		if(_tmpPushAcc!=null&&_tmpPushAcc.size()>0){
		pushAccountMap = _tmpPushAcc;
		}	_tmpPushAcc = null;
		
	}

	/**
	 * 
	 * Method : instance
	 * @return
	 *       usage :returning the singleton object
	 */
	public static PushAccount instance()
	{
		if(pushAccount == null)
			pushAccount = new PushAccount();
		
		return pushAccount;
	}
	
	public Map<String,String> getPushAccount(String username)
	{
			
		if(pushAccountMap != null && username !=null && pushAccountMap.containsKey(username)  )		{
			return (Map) pushAccountMap.get(username);
		}
		else
		{
			return null;
		}
	}
	
	public Map getPushAccount()
	{

		return pushAccountMap;
	}
	
	public void changePassword(String username,String oldpassword,String newpassword) 
	{
		Connection connection = null;
		PreparedStatement statement = null;
		PreparedStatement insertstatement = null;

		try
		{
				
			String sql = "update users set password=? where username=?";
		
			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, newpassword);
			statement.setString(2, username);		
			statement.execute();
			insertstatement=connection.prepareStatement("insert into password_change_log(username,oldpassword,newpassword,itime) values(?,?,?,?)");
			insertstatement.setString(1, username);
			insertstatement.setString(2, oldpassword);
			insertstatement.setString(3, newpassword);
			insertstatement.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
			insertstatement.execute();
			reload();
		} 
		catch(Exception e)
		{} 
		finally 
		{
			Close.close(insertstatement);			
			Close.close(statement);
			Close.close(connection);
		}//end of finally
	
	}

	

	
	
	private Map loadPushAccountInfo() 
	{
		Map<String,Map<String,String>> _pushAccMap = new HashMap<String,Map<String,String>>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try
		{
				
			String sql = "select * from superadmin a,admin b ,users c where a.superadmin_id=b.superadmin_id and b.admin_id=c.admin_id and a.mode=?";
		
			connection  = CoreDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, MODE);
			resultSet = statement.executeQuery();
			ResultSetToHashMapConverter rsConverter = new ResultSetToHashMapConverter();
			
			while(resultSet.next()) 
			{
				String key = resultSet.getString("username").toLowerCase();
				
				Map<String,String> data=rsConverter.toObject(resultSet);
				data.put("superadmin",data.get("superadmin_name").toLowerCase());
				data.put("admin",data.get("admin_name").toLowerCase());
				data.put("encrypt_1_password",MD5.MD5( data.get("password").trim()));

				_pushAccMap.put(key,data);	
				
			}
			
		} 
		catch(Exception e)
		{
		
			_pushAccMap = null;
		} 
		finally 
		{
			Close.close(resultSet);
			Close.close(statement);
			Close.close(connection);
		}//end of finally
		
		return _pushAccMap;
		
	}

	
	private void loaduser(Connection connection, TableExsists table) {

		if (!table.isExsists(connection, table.getTableName(USER_SQL))) {
			if (table.create(connection, USER_SQL, false)) {

			}
		}
	}

	private void loadadmin(Connection connection, TableExsists table) {
		if (!table.isExsists(connection, table.getTableName(ADMIN_SQL))) {
			if (table.create(connection, ADMIN_SQL, false)) {

			}
		}

	}

	private void loadsuperadmin(Connection connection, TableExsists table) {

		if (!table.isExsists(connection, table.getTableName(SUPERADMIN_SQL))) {
			if (table.create(connection, SUPERADMIN_SQL, false)) {

			}
		}
	}
	
}

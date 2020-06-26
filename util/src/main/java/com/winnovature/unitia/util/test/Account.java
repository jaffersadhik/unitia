package com.winnovature.unitia.util.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;
import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.TestcaseUserName;

public class Account {

	private static final String TEST_SUPERADMIN_SQL = "create table test_superadmin(superadmin_id INT,superadmin_name varchar(16),password varchar(8),itime timestamp default CURRENT_TIMESTAMP)";

	private static final String TEST_ADMIN_SQL = "create table test_admin(admin_id INT,superadmin_id INT,admin_name varchar(16),password varchar(8),itime timestamp default CURRENT_TIMESTAMP)";

	private static final String TEST_USERS_SQL = "create table test_users(testcaseid INT PRIMARY KEY AUTO_INCREMENT,username varchar(16),password varchar(8),testcasetype varchar(25),itime timestamp default CURRENT_TIMESTAMP)";


	private static Account obj=null;
	
	private static String SUPERADMIN=null;
	
	private static int SUPERADMIN_ID=-1;
	
	private static String ADMIN=null;
	
	private static int ADMIN_ID=-1;

	
	private static Set<String> userstype=new HashSet<String>();
	
	static{
		
		userstype.add("otp");
		userstype.add("optin");
		userstype.add("optout");
		userstype.add("duplicate_mobile");
		userstype.add("duplicate_mobilemsg");
		userstype.add("schedule");
		userstype.add("intl");
		userstype.add("blockout");
		userstype.add("trans");
		userstype.add("promo");
		userstype.add("credittrans");
		userstype.add("creditpromo");

	}
	
	private Account(){
		
		init();
	}

	private void init() {
		
		PushAccount.instance().getPushAccount();
		
		if(SUPERADMIN_ID==-1){
			
			setSuperAdmin();
		}
		
		
	}

	private void setSuperAdmin() {
		
		Connection connection=null;
		try{
			TableExsists table=new TableExsists();
			
			connection=CoreDBConnection.getInstance().getConnection();
			
			if(!table.isExsists(connection, "test_superadmin")){
				
				table.create(connection, TEST_SUPERADMIN_SQL, false);
				table.create(connection, TEST_ADMIN_SQL, false);
				table.create(connection, TEST_USERS_SQL, false);

				String ackid=TestcaseUserName.encryptString(ACKIdGenerator.getAckId());
				createSuperAdmin(connection,ackid);
				SUPERADMIN_ID=getSuperAdminId(connection,ackid);
				insert(connection,"insert into test_superadmin(superadmin_id) values("+SUPERADMIN_ID+")");
				SUPERADMIN=ackid;
				createAdmin(connection,ackid,SUPERADMIN_ID);
				ADMIN=ackid;
				ADMIN_ID=getAdminId(connection,ackid);
				insert(connection,"insert into test_admin(superadmin_id,admin_id) values("+SUPERADMIN_ID+","+ADMIN_ID+")");
				createUser(connection,ADMIN_ID);
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(connection);
		}
		
	}

	private void createUser(Connection connection, int adminid) {
		//""
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','http','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'2','1','http','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,optin_type) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','http','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,optin_type) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','http','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','2')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,otp_yn) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','http','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,intl) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','http','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,schedule_yn) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','http','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,blockout_yn) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','http','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,duplicate_type,duplicate_lifetime_in_ms) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','http','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1','60000')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,duplicate_type,duplicate_lifetime_in_ms) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','http','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','2','60000')");

		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','smpp','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'2','1','smpp','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,optin_type) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','smpp','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,optin_type) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','smpp','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','2')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,otp_yn) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','smpp','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,intl) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','smpp','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,schedule_yn) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','smpp','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,blockout_yn) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','smpp','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,duplicate_type,duplicate_lifetime_in_ms) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','smpp','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','1','60000')");
		insert(connection,"insert into users(username,password,admin_id,msgclass,dlr_post_yn,dlr_post_protocol,dlr_post_url,duplicate_type,duplicate_lifetime_in_ms) values('"+TestcaseUserName.encryptString(ACKIdGenerator.getAckId())+"','rdqgga',"+adminid+",'1','1','smpp','http://127.0.0.1:8080/api/testdn?ackid={0}&statusid={1}','2','60000')");

	}

	private void insert(Connection connection, String sql) {
		
		PreparedStatement statement=null;
		
		try{
			statement=connection.prepareStatement(sql);
			statement.execute();	
		}catch(Exception e){
			
			System.err.println(sql);
			e.printStackTrace();
		}finally{
			
			Close.close(statement);
		}
	}

	private int getAdminId(Connection connection, String ackid) {
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		try{
			statement=connection.prepareStatement("select admin_id from admin where admin_name=?");
			statement.setString(1, ackid);
			resultset=statement.executeQuery();
			
			if(resultset.next()){
				
				return resultset.getInt("admin_id");
			}
		}catch(Exception e){
			
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
		}
		return 0;
	
	}

	private int getSuperAdminId(Connection connection, String ackid) {
		
		PreparedStatement statement=null;
		ResultSet resultset=null;
		try{
			statement=connection.prepareStatement("select superadmin_id from superadmin where superadmin_name=?");
			statement.setString(1, ackid);
			resultset=statement.executeQuery();
			
			if(resultset.next()){
				
				return resultset.getInt("superadmin_id");
			}
		}catch(Exception e){
			
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
		}
		return 0;
	}

	private void createAdmin(Connection connection,String ackid,int superadminid) {
		

		PreparedStatement  statement=null;
		
		try{
		
			statement=connection.prepareStatement("insert into admin(admin_name,superadmin_id) values(?,?)");
			statement.setString(1,ackid);
			statement.setInt(2,superadminid);

			statement.execute();
			
		}catch(Exception e){
			
		}finally{
			
			Close.close(statement);
		}

		
	}

	private void createSuperAdmin(Connection connection,String ackid) {
		

		
		PreparedStatement  statement=null;
		
		try{
		
			statement=connection.prepareStatement("insert into superadmin(superadmin_name) values(?)");
			statement.setString(1,ackid);
			statement.execute();
			
		}catch(Exception e){
			
		}finally{
			
			Close.close(statement);
		}
		
			
	}

	public static Account getInstance(){
		
		if(obj==null){
			
			obj=new Account();
			
		}
		
		return obj;
	}
	
}


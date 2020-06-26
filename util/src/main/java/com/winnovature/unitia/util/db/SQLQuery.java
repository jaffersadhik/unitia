package com.winnovature.unitia.util.db;

import java.text.MessageFormat;
import java.util.StringTokenizer;

public class SQLQuery {
	
	
	public static final String RR_TABLE="create {}";
	
    public static final String SELECT_NUMBERINGPLAN = "select series,operator,circle from numberingplan";

    public static final String SELECT_ACCOUNT_TABLE_SCHEDULE = "select username,block_start,block_end from account";
    
    public static final String SELECT_SPLITGROUP_TABLE = "select groupname,msgtype,splitlength,maxlength from splitgroup";

	public static final String CREATE_CIRCLE_TABLE = "create table circle(code varchar(2) primary key,name varchar(50),category varchar(5),covered_area varchar(500))";
	
	public static final String CREATE_OPERATOR_TABLE = "create table operator(code varchar(2) primary key,network varchar(500))";

	public static final String CREATE_NUMBERINGPLAN_TABLE = "create table numberingplan(series varchar(10) primary key,operator varchar(5),circle varchar(2))";

	public static final String INSERT_CIRCLE_TABLE="insert into circle(code,name,category,covered_area) values(?,?,?,?)";

	public static final String INSERT_OPERATOR_TABLE="insert into operator(code,network) values(?,?)";

	public static final String INSERT_NUMBERINGPLAN_TABLE="insert into numberingplan(series,operator,circle) values(?,?,?)";

	public static final String CREATE_SPLITGROUP_TABLE = "create table splitgroup(groupname varchar(25) ,msgtype varchar(3),splitlength numeric(3),maxlength numeric(3))";
	
	public static final String INSERT_SPLITGROUP_TABLE = "insert into splitgroup(groupname,msgtype,splitlength,maxlength) values(?,?,?,?)";

	public static final String CREATE_SMSCID_TABLE = "create table kannel(smscid varchar(10) primary key,ip varchar(15) default '127.0.0.1',port numeric(6),routeclass varchar(1) default '4')";
	
	public static final String INSERT_SMSCID_TABLE = "insert into kannel(smscid,ip,port,routeclass) values(?,?,?,?)";

	public static final String CREATE_ROUTEGROUP_TABLE = "create table routegroup(groupname varchar(25) ,smscid varchar(10))";

	public static final String INSERT_ROUTEGROUP_TABLE = "insert into routegroup(groupname ,smscid ) values(?,?)";

	public static final String CREATE_ROUTE_TABLE = "create table route(id INT PRIMARY KEY AUTO_INCREMENT,routegroup varchar(25),superadmin varchar(21),admin varchar(21),username varchar(21),operator varchar(2),circle varchar(2),index(superadmin,admin,username,operator,circle))";
	
	public static final String CREATE_INTL_ROUTE_TABLE = "create table international_route(id INT PRIMARY KEY AUTO_INCREMENT,routegroup varchar(25),superadmin varchar(21),admin varchar(21),username varchar(21),countrycode varchar(10),index(superadmin,admin,username,countrycode))";

	public static final String INSERT_ROUTE_TABLE = "insert into route(routegroup,superadmin,admin,username,operator,circle ) values(?,?,?,?,?,?)";

	public static final String CREATE_KANNEL_LOADBALANCER = "create table loadbalancer_kannel(ip varchar(15) primary key)";
	
	public static final String INSERT_KANNEL_LOADBALANCER = "insert into loadbalancer_kannel(ip) values(?)";
	
	public static final String CREATE_DN_LOADBALANCER = "create table loadbalancer_dn(ip varchar(15) primary key,port int(6))";
	
	public static final String INSERT_DN_LOADBALANCER = "insert into loadbalancer_dn(ip,port) values(?,?)";
	
	public static final String INSERT_HTTP_LOADBALANCER = "insert into loadbalancer_http(ip,port) values(?,?)";

	public static final String CREATE_VSMSC_LOADBALANCER = "create table loadbalancer_vsmsc(ip varchar(15) primary key,port int(6))";
	
	public static final String INSERT_VSMSC_LOADBALANCER = "insert into loadbalancer_vsmsc(ip,port) values(?,?)";

	public static final String SELECT_KANNEL_TABLE = "select smscid,ip,port,routeclass from kannel";

	public static final String SELECT_ROUTEGROUP_TABLE = "select groupname,smscid from routegroup";

	public static final String SELECT_ROUTE_TABLE = "select routegroup,superadmin,admin,username,operator,circle from route";

	public static final String SELECT_INTL_ROUTE_TABLE = "select routegroup,superadmin,admin,username,countrycode from international_route";

	public static final String SELECT_DN_LB_IP_TABLE = "select ip,port from loadbalancer_dn";

	public static final String SELECT_VSMSC_LB_IP_TABLE = "select ip,port from loadbalancer_vsmsc";

	public static final String SELECT_HTTP_LB_IP_TABLE = "select ip,port from loadbalancer_http";

	public static final String SELECT_NP_TABLE = "select series,operator,circle  from numberingplan";

	public static final String CREATE_HTTP_LOADBALANCER = "create table loadbalancer_http(ip varchar(15) primary key,port int(6))";

	public static final String CREATE_ACCOUNT_CREDENTIAL_TABLE = "create table account(username varchar(16),password varchar(8),auth_type varchar(4),status numeric(1),max_bind numeric(2),max_sms_thread numeric(2),max_dn_thread numeric(2),ctime TIMESTAMP default CURRENT_TIMESTAMP,utime TIMESTAMP )";

	public static final String SELECT_CREDENTIAL_TABLE = "select username,password,auth_type,status,max_bind,max_sms_thread,max_dn_thread from account";

	public static final String CREATE_AUTHORIZED_IP_TABLE = "create table users_authorized_ip_pattern( username varchar(16),ip_pattern varchar(15))";
	
	public static final String CREATE_APPS_AUTHORIZED_IP_TABLE = "create table apps_authorized_ip_pattern( modulename  varchar(16),ip_pattern varchar(15))";

	public static final String SELECT_AUTHORIZED_IP_TABLE = "select username,ip_pattern from users_authorized_ip_pattern";
	
	public static final String SELECT_APPS_AUTHORIZED_IP_TABLE = "select modulename,ip_pattern from apps_authorized_ip_pattern";

	public static String CREATE_SCHEDULE_USER_COUNT_TABLE_LOCK = "create table schedule_count_lock(message varchar(16) )";

	public static String CREATE_SCHEDULE_USER_COUNT_TABLE = "create table schedule_count(username varchar(16) ,count numeric(10),ctime Timestamp default now())";

	public static String CREATE_RELEASED_USER_TABLE="create table user_released(username varchar(16) primary key,ctime Timestamp default now())";

    public static String CREATE_ACCOUNT_TABLE_SCHEDULE="create table account(username varchar(16),block_start varchar(5),block_end varchar(5),ctime Timestamp default now())";

    public static String CREATE_ACCOUNT_TABLE="create table account(username varchar(16),ctime Timestamp default now())";
	
    public static String SELECT_ACCOUNT_TABLE="select username from account";
    
    public static String SELECT_ACCOUNT_TABLE_SCHEDULE_RELEASE="select username from user_released";

	private static String CREATE_OPTIN_TABLE_TEMPLATE="create table {0}(mobile numeric(21,0),ctime Timestamp default now())";

	private static String CREATE_OPTOUT_TABLE_TEMPLATE="create table {0}(mobile numeric(21,0),ctime Timestamp default now())";

    private static String LOCK_INSERT_TEMPLATE="insert into {0}(lockstring) values(''dont delete the record it used for lock'') ";

    private static String LOCK_CREATE_TEMPLATE="create table {0}(lockstring varchar(1000))";

	public static String SELECT_KANNEL_LB_IP_TABLE="select ip from loadbalancer_kannel limit 1";
	
	
	
    public static String getSQLForOptin(String tablename) {
	
		String params [] = {tablename};
		return MessageFormat.format(CREATE_OPTIN_TABLE_TEMPLATE,params);
	}
	
    public static String getSQLForLockInsert(String tablename) {
		
		String params [] = {tablename};
		return MessageFormat.format(LOCK_INSERT_TEMPLATE,params);
	}
    public static String getSQLForLockCreate(String tablename) {
		
		String params [] = {tablename};
		return MessageFormat.format(LOCK_CREATE_TEMPLATE,params);
	}
	public static String getSQLForOptout(String tablename) {
		
		String params [] = {tablename};
		return MessageFormat.format(CREATE_OPTOUT_TABLE_TEMPLATE,params);
	}
	
	public static String getTableName(String SQL) {
		
		String tablename=SQL.substring(0,SQL.indexOf("("));

		StringTokenizer st=new StringTokenizer(tablename," ");
		st.nextToken();
		st.nextToken();
		tablename=st.nextToken().trim();
		
		return tablename;
		
	}
	public static void main(String args[]) {
		
		String tablename=CREATE_SCHEDULE_USER_COUNT_TABLE.substring(0,CREATE_SCHEDULE_USER_COUNT_TABLE.indexOf("("));
		
		StringTokenizer st=new StringTokenizer(tablename," ");
		st.nextToken();
		st.nextToken();
		tablename=st.nextToken();
		
		System.out.println(tablename);
	}
}

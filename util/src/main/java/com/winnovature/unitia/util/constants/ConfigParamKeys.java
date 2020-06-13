/**

 * 	@(#)ConfigParamKeys.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */
package com.winnovature.unitia.util.constants;

/**
 * 	This interface is declaration of Configurable parameter keys 
 * 	
 *
 * 	@author 	M Ravikumar (ravikumar@air2web.co.in)
 *	@version 	ConfigParamKeys.java v1.0<br>
 *				Created: 25 Jun 2008 19:15<br>
 *				Last Modified 25 Jun 2008 19:15 by M Ravikumar
 */
public interface ConfigParamKeys {

	/**
	 * bsnl.dlr.ipport
	 */
	public final String BSNL_DLR_IPPORT="bsnl.dlr.ipport";
	/**
	 * bsnl.mr.url
	 */
	public final String  BSNL_MR_URL="bsnl.mr.url";
	/**
	 * bsnl.mr.url.resp
	 */
	public final String  BSNL_MR_URL_RESP="bsnl.mr.url.resp";
	
	/**
	 * SMPP interface VSMSC listen port
	 * This will be configured based on instance id
	 */
	public final String SMPP_INTERFACE_VSMSC_LISTEN_PORT = "smpp.interface.vsmsc.listen.port";

	/**
	 * SMPP interface layer's admin server's client socket timeout (in minutes)
	 * This will be configured based on instance id
	 */
	public final String SMPP_INTERFACE_AS_CLIENT_SOCKET_TIMEOUT = "smpp.interface.as.client.socket.timeout";

	/**
	 * SMPP interface layer's admin server port
	 * This will be configured based on instance id
	 */
	public final String SMPP_INTERFACE_ADMIN_SERVER_PORT = "smpp.interface.admin.server.port";
	
	/**
	 * VSMSC mysql pool connections property file path
	 */
	public final String VSMSC_MYSQL_POOL_PROPERTY_FILE = "vsmsc.mysql.pool.property.file";
	
	/**
	 * VSMSC mysql connection pool jndi name
	 */
	public final String VSMSC_MYSQL_CONNECTION_POOL_NAME = "vsmsc.mysql.connection.pool.name";

	/**
	 * Email Retry Maximum Row Fetch Size
	 */
	public final String EMAIL_RETRY_MAX_ROW_FETCH_SIZE="email.retry.max.row.fetchsize"; 
	
	/**
	 * Email Retry fetching message delay time (in minutes)
	 */
	public final String EMAIL_RETRY_FETCH_DELAY_TIME="email.retry.fetch.delay.time";
	
	/**
	 * Email Retry Seelp Time (in minutes)
	 */
	public final String EMAIL_RETRY_SLEEP_TIME="email.retry.sleep.time";

	/**
	 * Database system and current system time difference in milli seconds
	 * This will be configured based on instance id
	 */
	public final String DATABASE_TIME_DIFFERENCE = "database.time.difference";
	
	/**
	 * mail dns server ip
	 * This will be configured under SHARED key
	 */
	public final String MAIL_DNSSERVER_IP = "mail.dnsserver.ip";

	/**
	 * mail relay server ip
	 * This will be configured under SHARED key
	 */
	public final String MAIL_RELAYSERVER_IP = "mail.relayserver.ip";
	
	/**
	 * Socket time out period for SocketClient (in seconds)
	 * This will be configured based on instance id
	 */
	public final String SOCKET_READING_TIME_OUT	=	"socket.reading.timeout";
	
	/**
	 * UDP Server listen Ip
	 */
	public final String UDP_LISTEN_IP = "udp.server.listen.ip";
	
	/**
	 * UDP Server listen port
	 */
	public final String UDP_LISTEN_PORT = "udp.server.listen.port";
	
	/**
	 * UDP Error mail sending Time Interval (in milli seconds)
	 */
	public final String UDP_ALERT_TIME_DIFF = "udp.alert.time.diff";
	
	/**
	 * UDP Alert EMAIL From Address
	 */
	public final String UDP_EMAIL_FROM = "udp.alert.email.from";
	
	/**
	 * UDP Alert Email To Address with tilda separated values
	 */
	public final String UDP_EMAIL_TO = "udp.alert.email.to";
	
	/**
	 * UDP Alert Email Content
	 */
	public final String UDP_EMAIL_CONTENT = "udp.alert.email.content";
	
	/**
	 * UDP Alert Email Subject
	 */
	public final String UDP_EMAIL_SUBJECT = "udp.alert.email.subject";
	
	/**
	 * UDP Alert SMTP IP 
	 */
	public final String UDP_EMAIL_SMTPHOST = "udp.alert.smtp.ip";
	
	/**
	 * UDP Alert DNS Server IP
	 */
	public final String UDP_EMAIL_DNSSERVER = "udp.alert.dns.ip";
	
	/**
	 * Email billing default table name
	 */
	public final String EMAIL_DEFAULT_TABLE_NAME ="email.default.table.name";
	
	public final String EMAIL_DEFAULT_DSN_TABLE_NAME ="email.default.dsn.table.name";

	
	/**
	 * Email billing table sequence name
	 */
	public final String EMAIL_DEFAULT_SEQ_NAME ="email.default.seq.name";
	
	/**
	 * Maximum EMAIL RetryCount
	 */
	public final String EMAIL_MAX_RETRY_COUNT ="email.max.retry.count";
	
	/**
	 * SD Message router sleep time (in minutes)
	 */
	public final String SDMSGROUTER_SLEEP_TIME = "sdmsgrouter.sleep.time";
	
	/**
	 * SD Message router max row fetch size
	 */
	public final String SDMSGROUTER_MAX_ROW_FETCH_SIZE = "sdmsgrouter.max.row.fetchsize";

	/**
	 * Deliver SM Listener Thread Sleep time (in seconds)  
	 */
	public final String DELIVER_SM_LISTENER_SLEEP_INTERVAL="deliversm.listener.sleep.interval";
	
	/**
	 * Client Ack Dn retry maximum rows fetch size
	 */
	public final String CLIENT_ACKDN_RETRY_MAX_ROW_FETCH_SIZE="client.ackdn.retry.max.row.fetchsize"; 
	/*
	 * Client Ack Dn retry fetch delay time (in minutes)
	 */
	public final String CLIENT_ACKDN_RETRY_FETCH_DELAY_TIME="client.ackdn.retry.fetch.delay.time";
	/*
	 * Client Ack Dn retry max retry count
	 */
	public final String CLIENT_ACKDN_MAX_RETRY_COUNT="client.ackdn.retry.count";
	/**
	 * Reliance SMSC URL 
	 */
	public final String RELIANCE_MR_URL	=	"reliance.url";
	/**
	 * Reliance URL response
	 */
	public final String RELIANCE_MR_URL_RESP	=	"reliance.url.resp";
	
	/**
	 * Reliance MR smscid 
	 */
	public final String RELIANCEURL	=	"reliance.smsc.id";

	/**
	 * Credential to store the carrier ack in carrier_submit table. (Y or N)
	 */
	public final String	CARR_SUBMIT_REQ_YN	= "carr.submit.store.yn";


	/**
	 * Pull generic error message.
	 */
	public final String PULL_GENRIC_ERR_MSG	= "pull.generic.err.msg";
	
	/**
	 *  ICICI Maximum Row Fetch Count
	 */
	public final String ICICI_MAX_ROW_FETCH_COUNT="icici.max.row.fetch.count";
	
	/**
	 * Request Router Sleep Time (in minutes)
	 */
	public final String RR_SLEEP_TIME="rr.sleep.time";

	/**
	 * DB Cleaner Sleep Time (in minutes)
	 */
	
	public final String DB_CLEANER_SLEEP_TIME="db.cleaner.sleep.time";

	/**
	 * Schedule delivery or block out table name
	 */
	public final String SCHEDULE_DELIVERY_TABLE_NAME="sdmsgrouter.table.name";

	/**
	 * ICICI Default PID 
	 */
	public final String ICICI_DEFAULT_PID="icici.default.pid";

	/**
	 * ICICI default error AID
	 */
	public final String ICICI_DEFAULT_ERROR_AID="icici.default.error.aid";
	
	/**
	 * ICICI Sleep time (in seconds)
	 */
	public final String ICICI_SLEEP_TIME="icici.sleep.time";
	
	/**
	 * ICICI Duplicate table insert required or not - 0 or 1
	 */
	public final String ICICI_DUPLICATE_LOG_YN="icici.duplicate.log.yn";
	
	/**
	 * ICICI Request Log table insert required or not - 0 or 1
	 */
	public final String ICICI_REQUESTLOG_LEVEL="icici.request.log.yn";

	/**
	 * Rejection XML persistance path
	 */
	public final String REJECTION_XML_PERSISTENCE_PATH = "rejection.xml.persistence.path";
	

	/**
	 * Maximum message length
	 */ 
	public final String MAX_MESSAGE_LENGTH_ALLOWED = "max.message.length.allowed";
	
	/**
	 * Maximum message length
	 */ 
	public final String MAX_FULLMESSAGE_LENGTH_ALLOWED = "max.fullmessage.length.allowed";
	
	/**
	 * Maximum Expiry Minutes Allowed
	 */
	public final String MAX_EXPIRY_MINUTES_ALLOWED = "max.expiry.minutes.allowed";
	
	/**
	 * Maximum Customer Reference ID Length Allowed 
	 */
	public final String MAX_REFID_LENGTH_ALLOWED = "max.refid.length.allowed";
	
	/**
	 * Maximum Bill id Length Allowed
	 */
	public final String MAX_BILLID_LENGTH_ALLOWED = "max.billid.length.allowed";
	
	/**
	 * Maximum PORT Length allowed
	 */
	public final String MAX_PORT_LENGTH_ALLOWED = "max.port.length.allowed";

	/**
	 * Maximum Dynamic SenderId Length Allowed
	 */
	public final String MAX_DYNAMIC_SENDERID_LENGTH_ALLOWED = "max.dynamic.senderid.length.allowed";
	
	/**
	 * BPL SMSC ID
	 */
	public final String BPL_SMSC_ID = "bpl.smsc.id";
	
	/**
	 * Internal ACK DN Receiver URL
	 */
	public final String INTERNAL_ACK_DN_RECEIVER_URL = "internal.ack.dn.receiver.url";
	
	/**
	 * Maximum number of retry attempts for pull handover 
	 */
	public static String	PULL_RETRY_MAX_ATTEMPT	= "pull.retry.max.attempt";
	
	/**
	 * Pull retry maximum row fetch size 
	 */
	public final String PULL_RETRY_MAX_ROW_FETCH_SIZE = "pull.retry.max.row.retch.size";
	
	/**
	 * Pull retry fetch delay time (in minutes)
	 */
	public final String PULL_RETRY_FETCH_DELAY_TIME = "pull.retry.fetch.delay.time";
	
	/**
	 * Pull retry thread sleeping time (in minutes)
	 */
	public final String PULL_RETRY_SLEEP_TIME_MINS = "pull.retry.sleep.time.mins";
	
	/**
	 * MBL Default table name
	 */
	public final String MBL_DEFAULT_TABLENAME = "mbl.default.tablename";

	/**
	 * MBL Default Sequence Name
	 */
	public final String MBL_DEFAULT_SEQNAME = "mbl.default.seqname";
	
	/**
	 * Schedule Delivery Table
	 */
	public final String SCHEDULE_DELIVERY_TABLENAME = "schedule.delivery.tablename";
	
	/**
	 * Block Delivery Table
	 */
	public final String BLOCKOUT_DELIVERY_TABLENAME = "blockout.delivery.tablename";
	
	/**
	 * Number of characters to be fetched to find out the carrier and circle details.
	 */
	public final String NSN_INFO_QUERY_SIZE = "nsn.info.query.size";
	
	/**
	 * Message retry max row fetch count
	 */
	public final String MSG_RETRY_MAX_ROW_FETCH_SIZE = "msg.retry.max.row.fetch.size";
	
	/**
	 * Message retry fetch delay time (in mins)
	 */
	public final String MSG_RETRY_FETCH_DELAY_TIME = "msg.retry.fetch.delay.time";
	
	/**
	 * Message retry thread sleep time (in mins)
	 */
	public final String MSG_RETRY_SLEEP_TIME = "msg.retry.sleep.time";
	
	/**
	 * Message retry max attempt count
	 */
	public final String MSG_RETRY_MAX_ATTEMPT_COUNT = "msg.retry.max.attempt.count";
	
	/**
	 * Default sdpop instance id for schedule delivery
	 */
	public final String DEFAULT_SDPOP_INATANCE_SD = "default.sdpop.instance.sd";
	
	/**
	 * Default sdpop instance id for blockout delivery
	 */
	public final String DEFAULT_SDPOP_INSTANCE_BLOCKOUT = "default.sdpop.instance.blockout";
	
	/**
	 * Time difference to verify Pull Response Id (in hours)
	 */
	public final String PULL_RESPONSE_TIME_DIFF = "pull.response.time.diff";
	
	/**
	 * Default DN TableName
	 */
	
		
	/**
	 * clientackdn retry sleep time - in seconds
	 */
	public final String CLIENT_ACKDN_RETRY_SLEEP_TIME	=	"client.ackdn.retry.sleep.time";
	
	/**
	 * Request Router Fetch size
	 */
	public final String RR_FETCH_SIZE = "rr.fetch.size";

	/**
	 * ACKDN ZIP processor in minutes
	 */
	public final String ACKDN_ZIP_PROCESSOR_SLEEP_TIME = "ackdn.zip.processor.sleep.time";

	/**
	 * ACKDN FTP Processor in minutes
	 */
	public final String ACKDN_FTP_PROCESSOR_SLEEP_TIME = "ackdn.ftp.processor.sleep.time";
	
	/*
	 * SFTP Email From Address
	 */
	
	public final String SFTP_EMAIL_FROM_ADDRESS="sftp.email.from.address";
	
	/*
	 * SFTP Email To Address
	 */
	
	public final String SFTP_EMAIL_TO_ADDRESS="sftp.email.to.address";
	
	/*
	 * SFTP Email Subject 
	 */
	
	public final String SFTP_EMAIL_SUBJECT="sftp.email.subject";
	
	/*
	 * SFTP Email SMTP Host 
	 */
	
	public final String SFTP_EMAIL_SMTP_HOST="sftp.email.smtp.host";
	
	
	/*
	 * SFTP Email DNS Host 
	 */
	
	public final String SFTP_EMAIL_DNS_HOST="sftp.email.dns.host";
	
	/**
	 * Sequence for MBL_JUNK table
	 */
	public final String MBL_JUNK_SEQUENCE = "mbl.junk.sequence";
	
	/**
	 * Unknown DN Error code
	 */
	public final String UNKNOWN_DN_ERR_CODE = "unknown.dn.err.code";
	
	/**
	 * Unknown DN Error Description
	 */
	public final String UNKNOWN_DN_ERR_DES = "unknown.dn.err.des";
	
	/**
	 * Default DLR Internal Route
	 */

	public final String DEFAULT_DLR_INTERNAL_ROUTE = "default.dlr.internal.route";
	
	/**
	 * Default DLR Internal Route
	 */

	public final String HTTP_CONNECTION_TIMEOUT = "http.connection.timeout";
	
	/**
	 * PREFIXED CLI LENGTH
	 */
	public final String PREFIXED_CLI_LENGTH = "prefixed.cli.length";
	
	/**
	 *	MYSQL POOL DRIVER 
	 */
	public final String VSMSC_MYSQL_POOL_DRIVER = "vsmsc.mysql.pool.driver";
	
	/**
	 * POOL LOG FILE PATH
	 */
	public final String VSMSC_MYSQL_POOL_LOG_FILE = "vsmsc.mysql.pool.log.file";
	
	/**
	 * POOL CONNECTION EXPIRY VALUE
	 */
	public final String VSMSC_MYSQL_POOL_EXPIRY = "vsmsc.mysql.pool.expiry";
	
	/**
	 * POOL CONNECTION INITIALIZATION VALUE
	 */
	public final String VSMSC_MYSQL_POOL_INIT = "vsmsc.mysql.pool.init";
	
	/**
	 * POOL VALIDATOR
	 */
	public final String VSMSC_MYSQL_POOL_VALIDATOR = "vsmsc.mysql.pool.validator";
	
	/**
	 * POOL CACHE VALUE (BOOLEAN VALUE)
	 */
	public final String VSMSC_MYSQL_POOL_CACHE = "vsmsc.mysql.pool.cache";
	
	/**
	 * POOL DEBU VALUE (BOOLEAN VALUE)
	 */
	public final String VSMSC_MYSQL_POOL_DEBUG = "vsmsc.mysql.pool.debug";
	
	/**
	 * INVALID CLI_ERR_CD
	 */
	public final String INVALID_CLI_ERR_CD = "invalid.cli.err.cd";
	
	/**
	 * Maximum Pull Params
	 */
	public final String MAX_PULL_PARAMS = "max.pull.params";
	
	/**
	 * HTTP RESPONSE TIME OUT
	 */
	
	/**
	 * HTTP CONNECTION TIMEOUT FOR KANNEL
	 */
	public final String KANNEL_HTTP_CONNECTION_TIMEOUT = "kannel.http.connection.timeout";
	
	/**
	 * HTTP RESPONSE TIMEOUT FOR KANNEL
	 */
	public final String KANNEL_HTTP_RESPONSE_TIMEOUT = "kannel.http.response.timeout";
	
	/**
	 * TRANS_MBL_POLLER_SLEEP_TIME 
	 */
	public final String TRANS_MBL_POLLER_SLEEP_TIME = "trans.mbl.poller.sleep.time";
	
	/**
	 * TRANS_MBL_POLLER_FETCH_SIZE
	 */
	public final String TRANS_MBL_POLLER_FETCH_SIZE = "trans.mbl.poller.fetch.size";
	
	/**
	 * MAX_MOBILE_LENGTH_ALLOWED - added on 24-FEB-2010
	 */
	public final String MAX_MOBILE_LENGTH_ALLOWED = "max.mobile.length.allowed";
	
	/**
	 * MAX_SENDERID_LENGTH_ALLOWED - added on 24-FEB-2010
	 */
	public final String MAX_SENDERID_LENGTH_ALLOWED = "max.senderid.length.allowed";
	
	/**
	 * MIN_MOBILE_LENGTH_ALLOWED - added on 24-FEB-2010
	 */
	public final String MIN_MOBILE_LENGTH_ALLOWED = "min.mobile.length.allowed";
	
	/**
	 * MAX_MOBILE_MINUS_COUNTRYCD_LENGTH_ALLOWED
	 */
	
	public final String MAX_MOBILE_MINUS_COUNTRYCD_LENGTH_ALLOWED = "max.mobile.minus.countrycd.length.allowed";
	
	/**
     * Time interval for smpp accounts to be refreshed in memory in sec
     */
    public final String VSMSC_ACCOUNT_MEMORY_REFRESH = "vsmsc.account.memory.refresh";

    /**
     * Max number of DNs to fetch for a client at a time
     */
    public final String VSMSC_FETCH_DN_LIMIT = "vsmsc.fetch.dn.limit";

    /**
     * Automatically kills a session if inactive for below given time, in sec
     */
    public final String VSMSC_SESSION_KILL_TIME = "vsmsc.session.kill.time"; 

    /**
     * Automatically kills a session if inactive for below given time, in sec
     */
    public final String VSMSC_SESSION_COUNT_UPDATE_TIME = "vsmsc.session.count.update.time";
    /**
     * Monitor http server listens on this port
     */
    public final String VSMSC_MONITOR_PORT = "vsmsc.monitor.port";
    
	public final String MAX_ODREQID_LENGTH_ALLOWED = "max.odreqid.length.allowed";
	
	/**
	 * Transmbl poller sleep time
	 */
	public final String TRANSMBL_POLLER_SLEEP_TIME = "transmbl.poller.sleep.time";
	
	/**
	 * Transmbl poller fetch size
	 */
	public final String TRANSMBL_POLLER_FETCH_SIZE = "transmbl.poller.fetch.size";
	
	/**
	 * Msg retry poller sleep time
	 */
	public final String MSGRETRY_POLLER_SLEEP_TIME = "msgretry.poller.sleep.time";
	
	/**
	 * Msg retry poller fetch size
	 */
	public final String MSGRETRY_POLLER_FETCH_SIZE = "msgretry.poller.fetch.size";
	
	/**
	 * Msg retry poller fetch delay time
	 */
	public final String MSGRETRY_POLLER_FETCH_DELAY_TIME ="msgretry.poller.fetch.delay.time";
	
	/**
	 * "msgretry.poller.handover.split.size
	 */
	public final String MSGRETRY_POLLER_HANDOVER_SPLIT_SIZE = "msgretry.poller.handover.split.size";
	
	/**
	 * MSGRETRY_DEFAULT_CONNECTORID
	 */
	public final String MSGRETRY_DEFAULT_CONNECTORID = "msgretry.default.connectorid";
	
	/**
	 * MSGRETRY_DEFAULT_TABLENAME
	 */
	public final String MSGRETRY_DEFAULT_TABLENAME = "msgretry.default.tablename";
	
	//NG change made for TRAI
	/**
	 * BLOCKOUTRETRY_DEFAULT_CONNECTORID
	 */
	public final String BLOCKOUTRETRY_DEFAULT_CONNECTORID = "blockoutretry.default.connectorid";
	
	/**
	 * BLOCKOUTRETRY_DEFAULT_TABLENAME
	 */
	public final String BLOCKOUTRETRY_DEFAULT_TABLENAME = "blockoutretry.default.tablename";
	
	/**
	 * Blockout retry poller sleep time
	 */
	public final String BLOCKOUTRETRY_POLLER_SLEEP_TIME = "blockoutretry.poller.sleep.time";
	
	/**
	 * Blockout retry poller fetch size
	 */
	public final String BLOCKOUTRETRY_POLLER_FETCH_SIZE = "blockoutretry.poller.fetch.size";
	
	/**
	 * Blockout retry poller fetch delay time
	 */
	public final String BLOCKOUTRETRY_POLLER_FETCH_DELAY_TIME ="blockoutretry.poller.fetch.delay.time";
	
	/**
	 * CORE_WT_HANDOVER_SPLIT_SIZE
	 */
	public final String CORE_WT_HANDOVER_SPLIT_SIZE = "core.wt.handover.split.size";
	
	/**
	 * EMAILROUTER_WT_HANDOVER_SPLIT_SIZE
	 */
	public final String EMAILROUTER_WT_HANDOVER_SPLIT_SIZE = "emailrouter.wt.handover.split.size";
	
    /**
     * Time interval for live users data to be dumped in database in sec
     */
    public final String VSMSC_LIVE_USERS_DUMP = "vsmsc.live.users.dump";
     
	//String MAX_ODREQID_LENGTH_ALLOWED = "max.odreqid.length.allowed";	 
	
    public final String DEFAULT_SCHEDULE_TABLENAME = "default.schedule.tablename";
    
    public final String SCHEDULE_SPAWNER_INTERVAL = "schedule.spawner.interval";
    
    public final String SCHEDULE_POLLER_INTERVAL = "schedule.poller.interval";
    
    public final String SCHEDULE_DELIVERY_POLLER_FETCH_SIZE = "schedule.delivery.poller.fetch.size";
    
    public final String TRANS_Q_FETCH_SIZE = "trans.q.fetch.size";
    
    public final String TIME_TO_CLOSE_TRANS_FILE_IN_SEC = "time.to.close.trans.file.in.sec";
     
    public final String TRANS_FILE_DELIMITER = "trans.file.delimiter";
    
    public final String TRANS_FILE_MAX_ROWS = "trans.file.max.rows";
    
    public final String WRITE_BUFFER_SIZE = "write.buffer.size";
    
    public final String TRANS_REAPER_POLLING_INTERVAL_IN_SEC = "trans.reaper.polling.interval.in.sec";
    
    public final String TRANS_FILE_PATH = "trans.file.path";
    
    public final String TRANS_FILEPOLLER_POLLING_INTERVAL_IN_SEC = "trans.filepoller.polling.interval.in.sec";
    
    public final String READ_BUFFER_SIZE = "read.buffer.size";
    
    public final String TRANS_MBL_WT_HANDOVER_SPLIT_SIZE = "trans.mbl.wt.handover.split.size";
    
    public final String VSMSC_DN_RETRIES = "vsmsc.dn.retries";
    
    public final String ICICI_REQ_REAPER_POLLING_INTERVAL_IN_SEC = "icici.req.reaper.polling.interval.in.sec";
    
    
    public final String ICICI_REQ_Q_FETCH_SIZE = "icici.req.q.fetch.size";
    
    public final String ICICI_SPAWNER_INTERVAL = "icici.spawner.interval";
    
    public final String ICICI_POLLER_FETCH_SIZE = "icici.poller.fetch.size";
    
    public final String ICICI_POLLER_INTERVAL = "icici.poller.interval";
    
    public final String AUTO_ACCOUNT_MEMORY_REFRESH_INTERVAL_IN_SEC = "auto.account.memory.refresh.interval.in.sec";
	
    public final String SNMP_OBJECT_IDENTIFER = "snmp.object.identifier";
    
    public final String TRAI_BLOCKLOUT_START = "trai.blockout.start";

    public final String TRAI_BLOCKLOUT_END = "trai.blockout.end";
    
	public final String TRAI_ROUTE_SENDERID_YN = "TRAI_ROUTE_SENDERID_YN";
	
	public final String SCHEDULE_PRE_DEFAULT_TABLENAME = "schedule.pre.default.tablename";
	
	public final String BLOCKOUT_PRE_DEFAULT_TABLENAME = "blockout.pre.default.tablename";
	
	public final String SCHEDULE_DELIVERY_PRE_POLLER_FETCH_SIZE = "schedule.delivery.pre.poller.fetch.size";
	
	public final String SCHEDULE_DELIVERY_PRE_POLLER_DELAY_TIME_IN_HOUR = "schedule.delivery.pre.poller.delay.time.in.hour";
	
	public final String TRAI_TEMPLATE_CHECK_YN = "TRAI_TEMPLATE_CHECK_YN";	
	
	public final String DND_ROUTER_PRIMARY_GROUPID = "dnd.router.primary.groupid";	
	
	public final String DND_ROUTER_SECONDARY_GROUPID = "dnd.router.second.groupid";

	
	public final String SCHEDULE_POST_DEFAULT_TABLENAME = "schedule.post.default.tablename";
	
	public final String BLOCKOUT_POST_DEFAULT_TABLENAME = "blockout.post.default.tablename";
	
	public final String MAX_PROMO_SENDERID_LENGTH_ALLOWED = "max.promo.senderid.length.allowed";
	
	public final String TRAI_DND_CHECK_YN = "TRAI_DND_CHECK_YN";
	

	public final String DN_MSGRETRY_POLLER_SLEEP_TIME = "dn.msgretry.poller.sleep.time";
	
	public final String DN_MSGRETRY_POLLER_FETCH_SIZE = "dn.msgretry.poller.fetch.size";
	
	public final String DN_MSGRETRY_POLLER_FETCH_DELAY_TIME = "dn.msgretry.poller.fetch.delaytime";

	public final String DN_MSGRETRY_MAX_ATTEMPT = "dn.msgretry.poller.max.attempt";

	
	
	public final String DELIVERYSM_FAILOVER_SLEEP_TIME = "deliverysm.failover.sleep.time";
	
    public final String DELIVERYSM_FAILOVER_FETCH_SIZE = "deliverysm.failover.fetch.size";
    
    public final String GLOBAL_DND_CHECK = "global.dnd.check";
    
    public final String GLOBAL_BLOCK_LIST_CHECK = "global.block.list.check";

    /**
     * Prepaid & Credit Configuration added on 28-07-2011 by sreedhar
     */
    
    public final String CREDIT_PREPAID_TABLE_LOAD_TIME="credit.prepaid.table.load.time";
    
    public final String CREDIT_Q_FETCH_SIZE="credit.q.fetch.size";

    public final String TIME_TO_CLOSE_CREDIT_FILE_IN_SEC="time.to.close.credit.file.in.sec";
    
    public final String CREDIT_FILE_MAX_ROWS="credit.file.max.rows";
    
    public final String CREDIT_FILE_WRITE_BUFFER_SIZE="credit.file.write.buffer.size";
    
    public final String TRAI_DND_DELIVERY_YN = "TRAI_DND_DELIVERY_YN";	
    
    public final String MAX_TRANS_SENDERID_LENGTH_ALLOWED = "max.trans.senderid.length.allowed";
	
	public final String MAX_EMAIL_DELIVERY_MESSAGE_LENGTH_ALLOWED ="max.email..delivery.message.length.allowed";
	
	/**
     * 	Route based senderid validation configuration added on 24-11-2011 by sreedhar
     */
    
	
	public final String ROUTE_BASED_SENDERID_VALIDATION="route.based.senderid.validation";
	
	public final String TRANS_SENDERID_REGEX="trans.senderid.regex";
	
	public final String PROMO_SENDERID_REGEX="promo.senderid.regex";
	
	
	/*
	 * SNMP Key values added by jaffer on 12apr2012
	 */

	public static final String SNMP_KEY_TYPE = "snmp.Key.type";

	public static final String SNMP_STACK_TRACE_LENGTH = "snmp.stacktrace.length";

	public static final String SNMP_TRAP_SENDER_TIME_DIFF_SEC = "snmp.trap.send.time.diff.sec";

	public static final String SNMP_TRAP_MAP_CLEAR_TIME_HOUR_MIN = "snmp.trap.map.clear.time";

	public static final String SNMP_TRAP_MAP_CLEAR_TIME_INTERVAL_HOURS = "snmp.trap.map.clear.time.interval.hours";

	/**
	 * default port of snmp manager.
	 */
	public final String SNMP_DEFAULT_PORT = "snmp.default.port";

	/**
	 * snmp manager host
	 */
	public final String SNMP_MANAGER_HOST = "snmp.managerhost";

	/**
	 * snmp manager listen port
	 */
	public final String SNMP_MANAGER_PORT = "snmp.managerport";

	/**
	 * SNMP community
	 */
	public final String SNMP_COMMUNITY = "snmp.community";
	
	
	public final String AIRTEL_TEMPLATE_SCHEDULE_MIN = "airtel.template.schedule.min";
	
	public final String AIRTEL_TEMPLATE_DATASOURCE = "airtel.template.datasource";

	public final String EMAIL_TEMPLATE_KEY ="email.template.key";

	
	public final String CREDIT_CHECK_MODE="credit.check.mode";
	
	public final String MONITOR_ENABLED="monitor.enabled";
	
	public final String SEGMENT_NAME="segment.name";
	
	public final String STATS_POST_URL="stats.post.url";
	
	public final String STATS_DN_POST_URL="stats.dn.post.url";
	
	public final String STATS_QUEUE_MAX_SIZE="stats.queue.max.size";
	
	public final String STATS_QUEUE_REAPER_COUNT="stats.queue.reaper.count";
	
	public final String STATS_DATA_FETCH_SIZE="stats.data.fetch.size";
	
	public final String STATS_POST_CONNECTION_TIMEOUT="stats.post.connection.timeout";
	
	public final String STATS_POST_RESPONSE_TIMEOUT="stats.post.response.timeout";	

	public final String REDIS_HEALTHCHECK_IP="redis.healthcheck.ip";

	public final String REDIS_HEALTHCHECK_PORT="redis.healthcheck.port";

	public final String REDIS_HEALTHCHECK_PASSWORD="redis.healthcheck.password";

	public final String REDIS_HEALTHCHECK_DB="redis.healthcheck.db";

	public final String REDIS_HEALTHCHECK_TIMEOUT_INSEC="redis.healthcheck.timeout.insec";

	public final String REDIS_HEALTHCHECK_MAXPOOL_SIZE="redis.healthcheck.max.pool.size";

	public final String REDIS_HEALTHCHECK_MAXWAIT_FORCONNECTION_INSEC="redis.healthcheck.max.wait.forconnection.insec";
	
	
	


	public final String REDIS_CREDIT_NG_DS="redis.credit.ng.ds.name";
	
	public final String REDIS_CREDIT_MAX_FRACTION_DIGITS_FOR_CREDITACCOUNT="redis.credit.max.fraction.digits.for.creditaccount";
	
	public final String REDIS_CREDIT_MASTER_IP="redis.credit.master.ip";
	
	public final String REDIS_CREDIT_MASTER_PORT="redis.credit.master.port";
	
	public final String REDIS_CREDIT_MASTER_PASSWORD="redis.credit.master.password";
	
	public final String REDIS_CREDIT_MASTER_DB="redis.credit.master.db";
	
	public final String REDIS_CREDIT_SLAVE_IP="redis.credit.slave.ip";
	
	public final String REDIS_CREDIT_SLAVE_PORT="redis.credit.slave.port";
	
	public final String REDIS_CREDIT_SLAVE_PASSWORD="redis.credit.slave.password";
	
	public final String REDIS_CREDIT_SLAVE_DB="redis.credit.slave.db";
	
	public final String REDIS_CREDIT_TIMEOUT_INSEC="redis.credit.timeout.insec";
	
	public final String REDIS_CREDIT_MAXPOOL_SIZE="redis.credit.max.pool.size";
	
	public final String REDIS_CREDIT_MAXWAIT_FORCONNECTION_INSEC="redis.credit.max.wait.forconnection.insec";
	
	public final String REDIS_CREDIT_SYNC_BAL_POLLING_INTERVAL_INSEC="redis.credit.sync.bal.polling.interval.insec";
	
	public final String REDIS_CREDIT_SYNC_CONSUMED_POLLING_INTERVAL_INSEC="redis.credit.sync.consumed.polling.interval.insec";
	
	public final String REDIS_CREDIT_SYNC_RETURNEDCOUNT_POLLING_INTERVAL_INSEC="redis.credit.sync.returnedcount.polling.interval.insec";


	
	public final String REDIS_MONIT_MT_MASTER_IP="redis.monit.mt.master.ip";
	
	public final String REDIS_MONIT_MT_MASTER_PORT="redis.monit.mt.master.port";
	
	public final String REDIS_MONIT_MT_MASTER_PASSWORD="redis.monit.mt.master.password";
	
	public final String REDIS_MONIT_MT_MASTER_DB="redis.monit.mt.master.db";
	
	public final String REDIS_MONIT_MT_SLAVE_IP="redis.monit.mt.slave.ip";
	
	public final String REDIS_MONIT_MT_SLAVE_PORT="redis.monit.mt.slave.port";
	
	public final String REDIS_MONIT_MT_SLAVE_PASSWORD="redis.monit.mt.slave.password";
	
	public final String REDIS_MONIT_MT_SLAVE_DB="redis.monit.mt.slave.db";
	
	public final String REDIS_MONIT_MT_TIMEOUT_INSEC="redis.monit.mt.timeout.insec";
	
	public final String REDIS_MONIT_MT_MAXPOOL_SIZE="redis.monit.mt.max.pool.size";
	
	public final String REDIS_MONIT_MT_MAXWAIT_FORCONNECTION_INSEC="redis.monit.mt.max.wait.forconnection.insec";
	
	
	public final String REDIS_MONIT_DN_MASTER_IP="redis.monit.dn.master.ip";
	
	public final String REDIS_MONIT_DN_MASTER_PORT="redis.monit.dn.master.port";
	
	public final String REDIS_MONIT_DN_MASTER_PASSWORD="redis.monit.dn.master.password";
	
	public final String REDIS_MONIT_DN_MASTER_DB="redis.monit.dn.master.db";
	
	public final String REDIS_MONIT_DN_SLAVE_IP="redis.monit.dn.slave.ip";
	
	public final String REDIS_MONIT_DN_SLAVE_PORT="redis.monit.dn.slave.port";
	
	public final String REDIS_MONIT_DN_SLAVE_PASSWORD="redis.monit.dn.slave.password";
	
	public final String REDIS_MONIT_DN_SLAVE_DB="redis.monit.dn.slave.db";
	
	public final String REDIS_MONIT_DN_TIMEOUT_INSEC="redis.monit.dn.timeout.insec";
	
	public final String REDIS_MONIT_DN_MAXPOOL_SIZE="redis.monit.dn.max.pool.size";
	
	public final String REDIS_MONIT_DN_MAXWAIT_FORCONNECTION_INSEC="redis.monit.dn.max.wait.forconnection.insec";

	

	public final String REDIS_LIMIT_TRAFFIC_NG_DS="redis.limit.traffic.ng.ds.name";

	public final String REDIS_LIMIT_TRAFFIC_MASTER_IP="redis.limit.traffic.master.ip";

	public final String REDIS_LIMIT_TRAFFIC_MASTER_PORT="redis.limit.traffic.master.port";

	public final String REDIS_LIMIT_TRAFFIC_MASTER_PASSWORD="redis.limit.traffic.master.password";

	public final String REDIS_LIMIT_TRAFFIC_MASTER_DB="redis.limit.traffic.master.db";

	public final String REDIS_LIMIT_TRAFFIC_SLAVE_IP="redis.limit.traffic.slave.ip";

	public final String REDIS_LIMIT_TRAFFIC_SLAVE_PORT="redis.limit.traffic.slave.port";

	public final String REDIS_LIMIT_TRAFFIC_SLAVE_PASSWORD="redis.limit.traffic.slave.password";

	public final String REDIS_LIMIT_TRAFFIC_SLAVE_DB="redis.limit.traffic.slave.db";

	public final String REDIS_LIMIT_TRAFFIC_TIMEOUT_INSEC="redis.limit.traffic.timeout.insec";

	public final String REDIS_LIMIT_TRAFFIC_MAXPOOL_SIZE="redis.limit.traffic.max.pool.size";
	
	public final String REDIS_LIMIT_TRAFFIC_MAXWAIT_FORCONNECTION_INSEC="redis.max.wait.forconnection.insec";

	public final String LIMIT_TRAFFIC_ALERT_NG_URL="limit.traffic.alert.ng.url";
	
	public final String LIMIT_TRAFFIC_ALERT_SLEEP_TIME="limit.traffic.alert.sleep.time";
	
	public final String CONCATMESSAGE_WAIT_TIME_SECS="concatmessage.wait.time.secs";

	public final String REDIS_MEMORY_ALLOWED="redis.allowed.memory.bytes";
	
	public final String TRANSMBL_VERSION="transmbl.version";
	
	public final String TRANSMBL_RMQ_IPLIST="transmbl.rmq.ip.list";
	
	public final String TRANSMBL_RMQ_USERNAME="transmbl.rmq.username";
	
	public final String TRANSMBL_RMQ_PASSWORD="transmbl.rmq.password";
	
	public final String TRANSMBL_RMQ_EXCHANGE_NAME="transmbl.rmq.exchange.name";
	
	public final String TRANSMBL_RMQ_QUEUE_NAME="transmbl.rmq.queue.name";

	public final String TRANSMBL_RMQ_MAX_CONSUMER="transmbl.rmq.max.consumer";
	
	public final String TRANSMBL_MEMORYQ_MAX_CONSUMER="transmbl.memoryq.max.consumer";

	public final String MNP_LOOKUP_YN="mnp.lookup.yn";

	public final String MNP_MODULUS="mnp.modulus";
	
	public final String MNP_RMQ_SERVER_ID="mnp.rmq.server.id";
	
	public final String MNP_PREROUTER_MOBILE_POST_YN="mnp.prerouter.mobile.post.yn";

	public final String MNP_CONSUMER_RMQ_SERVER_ID="mnp.consumer.rmq.server.id";
	
	public final String MNP_RMQ_MAX_CONSUMER="mnp.rmq.max.consumer";

	public final String MNP_IMQ_MAX_CONSUMER="mnp.imq.max.consumer";

	public final String STATS_CHRONICLE_PATH="stats.chronicle.path";
	

}

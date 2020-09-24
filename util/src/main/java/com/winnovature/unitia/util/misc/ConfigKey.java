package com.winnovature.unitia.util.misc;

public interface ConfigKey {

	public static final String DN_32_QS="dn.status.32.qs";

	public static final String PREFIX_START_NUMBER="91prefix.start.number";

	public final String HTTP_CONNECTION_TIMEOUT = "http.connection.timeout";

	public final String HTTP_RESPONSE_TIMEOUT = "http.response.timeout";
	
	public final String DEFAULT_CREDIT_POINTS = "default.credit.points";

	public final String MBL_DEFAULT_TABLENAME = "default.kannelsubmit.tablename";

	public final String DN_DEFAULT_TABLENAME = "default.dn.tablename";

	public final String DEFAULT_POST_TABLENAME = "default.dn.post.tablename";

    public final String TRAI_BLOCKLOUT_START = "trai.blockout.start";

    public final String TRAI_BLOCKLOUT_END = "trai.blockout.end";
    
	public final String MAX_SCHEDULE_TIME_ALLOWED_MINS = "max.schedule.time.allowed.mins";

	public final String SPCECIAL_CHAR_WORD_COUNT="special.char.word.count";
	
	
	public final static String PRIORITY_Q_REPEAR_COUNT="qutil.priority.repear.count"; 

	public final static String PROMOTIONAL_Q_REPEAR_COUNT="qutil.promo.repear.count"; 

	public final static String TRANSACTIONAL_Q_REPEAR_COUNT="qutil.trans.repear.count"; 

	public final static String INMEMORY_THRESHOLD_SIZE="qutil.inmemory.threashold.size";

	public static final String LOGMODE = "apps.log.yn";

	public static final String LOGPATH = "log.path";

	public static final String MAX_QUEUE = "max.queue.length"; 
	
	public static final String MAX_COMMONPOOL = "commonpool.max.queue.length"; 
	public static final String MAX_SUBMISSIONPOOL = "submissionpool.max.queue.length"; 
	public static final String MAX_DNRECEIVERPOOL = "dnreceiverpool.max.queue.length"; 
	public static final String MAX_DNPOSTPOOL = "dnpostpool.max.queue.length"; 
	public static final String MAX_HTTPDN = "httpdn.max.queue.length"; 
	public static final String MAX_KANNELRETRYPOOL = "kannelretrypool.max.queue.length"; 
	public static final String MAX_LOGSPOOL = "logspool.max.queue.length"; 
	public static final String MAX_SMPPDN = "smppdn.max.queue.length"; 

	
	public static final String MAX_RETRY_QUEUE = "max.retry.queue.length";

	public static final String LOADBALANCER_DN_IP = "loadbalancer.dn.ip";

	public static final String LOADBALANCER_DN_PORT = "loadbalancer.dn.port";

	public static final String OTPDNWAITTIMEINMS = "otp.dn.waittime.in.millisecond";

	public static final String KANNEL_USERNAME = "kannel.username"; 

	public static final String KANNEL_PASSWORD = "kannel.password";

	public static final String KANNEL_RESPONSE = "kannel.response";

	public static final String GRACE_STOP = "grace.stop"; 

	public final String MAX_MOBILE_LENGTH_ALLOWED = "max.mobile.length.allowed";
	
	public final String MAX_SENDERID_LENGTH_ALLOWED = "max.senderid.length.allowed";
	
	public final String MIN_MOBILE_LENGTH_ALLOWED = "min.mobile.length.allowed";
	
	public final String MAX_UDH_LENGTH_ALLOWED ="max.udh.length.allowed";
	
	public final String MAX_MSG_LENGTH_ALLOWED ="max.msg.length.allowed";



}

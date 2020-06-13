package com.winnovature.unitia.util.misc;

public class QueueName {

	public static final String CLIENT_LOG_Q_NAME = "_iClientLogQueue";;

	public static String DN_REQ_Q_NAME = "_idnQueue";

	public static String DN_DB_Q_NAME = "_idnDBQueue";
	public static String MONITOR_Q_NAME = "_iMonitorQueue";

	public static String TELCO_RETRY_Q_NAME = "_iTelcoRetryQueue";

	public static String SINGLE_DN_Q_NAME = "_SINGLE_DN_CONTROLLER_QUEUE";

	// This key used for push/pull the DN data in Interim RMQ
	public static String FINALDN_INTERIM_QUEUENAME = "_FINALDN_INTERIM_QUEUE";

	// This key used for push/pull the In Process DN data In-process RQM
	public static String FINALDN_INPROCESS_QUEUENAME = "_FINALDN_INPROCESS_QUEUE";

	// This key used for push/pull the Processed DN data in Processed RQM
	public static String FINALDN_PROCESSED_QUEUENAME = "_FINALDN_PROCESSED_QUEUE";

	// This key used for push/pull the DN data in Interim RMQ
	public static String VOICEDN_SMS_QUEUENAME = "_VOICEDN_QUEUE";
}

package com.winnovature.unitia.util.misc;

import java.util.HashMap;
import java.util.Map;

public class GroupName {

	public static Map<String,String> NAME=new HashMap<String,String>();
	
	static {
		
		NAME.put("module", "apps");
		NAME.put("error", "apps");
		NAME.put("logtime", "datetime");
		NAME.put("app_status", "apps");
	
		NAME.put("smpp_maxbind", "account");
		NAME.put("statusid", "status");
		NAME.put("ackid", "request");
		NAME.put("msgid", "request");
		NAME.put("username", "request");
		
		NAME.put("promo_reject_yn", "account");
		NAME.put("mobile", "request");
		NAME.put("operator", "route");
		NAME.put("circle", "route");
		NAME.put("routegroup", "route");
	
		NAME.put("routekey", "route");
		NAME.put("smscid", "route");
		NAME.put("routelogic", "route");
		NAME.put("port", "request");
		NAME.put("splitgroup", "account");
		
		NAME.put("msgtype", "route");
		NAME.put("maxsmslength", "route");
		NAME.put("splitsmslength", "route");
		NAME.put("password", "request");
		NAME.put("senderid", "request");
	
		NAME.put("scheduletime", "datetime");
		NAME.put("customerip", "request");
		NAME.put("status_desc", "request");
		NAME.put("protocol", "request");
		NAME.put("rtime", "datetime");
		
		NAME.put("superadmin", "account");
		NAME.put("admin", "account");
		NAME.put("duplicate_type", "account");
		NAME.put("duplicate_lifetime_in_ms", "account");
		NAME.put("optin_type", "");
	
		NAME.put("otp_yn", "account");
		NAME.put("msgclass", "request");
		NAME.put("bill_type", "account");
		NAME.put("scheduletime_string", "datetime");
		NAME.put("blockout_yn", "account");
	
		NAME.put("blockout_start", "account");
		NAME.put("blockout_end", "account");
		NAME.put("userid", "account");
		NAME.put("registereddelivery", "request");
		NAME.put("expiry", "request");
		
		NAME.put("templateid", "request");
		NAME.put("entityid", "request");
		NAME.put("udh", "request");
		NAME.put("msgsrc", "request");
		NAME.put("schedule_yn", "account");
		NAME.put("log_yn", "account");

		
		NAME.put("rtime_org", "datetime");
		NAME.put("schedule_type", "request");
		NAME.put("routeclass", "route");
		NAME.put("allowedpatternid", "request");
		NAME.put("filteringpatternid", "request");
		NAME.put("statuscd", "dn");
		NAME.put("ackidorg", "request");

		
		NAME.put("ktime", "datetime");
		NAME.put("fullmsg", "request");
		NAME.put("carrierdr", "dn");
		NAME.put("carrier_msgid", "dn");
		NAME.put("carrier_submitdate", "dn");
		NAME.put("carrier_donedate", "dn");
		NAME.put("carrier_stat", "dn");
		NAME.put("carrier_err", "dn");
		
		NAME.put("carrier_submittime", "datetime");
		NAME.put("carrier_submittime_org", "datetime");
		NAME.put("carrier_donetime", "datetime");
		NAME.put("carrier_donetime_org", "datetime");
		NAME.put("sms_latency_in_ms", "latency");
		NAME.put("adjustment_indicator", "latency");
		NAME.put("sms_latency", "latency");
		NAME.put("sms_latency_org", "latency");
		NAME.put("carrier_latency", "latency");
		
		
		NAME.put("statusid_org", "request");
		NAME.put("senderidorg", "request");
		NAME.put("countrycode", "route");
		NAME.put("smscidorg", "route");
		NAME.put("carriersystemid", "dn");
		NAME.put("carrierdr", "dn");
		NAME.put("platform_latency", "latency");
		NAME.put("kannel_ip", "route");
		
		
		NAME.put("kannel_port", "route");		
		NAME.put("routeclass", "route");		
		NAME.put("countryname", "route");		
		NAME.put("operator_name", "route");		
		NAME.put("circle_name", "route");		
		NAME.put("featurecd", "route");		
		NAME.put("totalmsgcount", "request");		
		NAME.put("splitseq", "splitup");		
		NAME.put("hex", "splitup");
		
		
		NAME.put("statusidfromplatform", "request");		
		NAME.put("senderid_type", "account");		
		NAME.put("senderid_trans", "account");		
		NAME.put("senderid_promo", "account");		
		NAME.put("routeclassorg", "route");		
		NAME.put("ktime_org", "datetime");		
		NAME.put("dnstime", "datetime");		
		NAME.put("dlr_post_yn", "account");		
		NAME.put("dlr_post_protocol", "account");
	
		
		NAME.put("credit", "request");		
		NAME.put("attempt_count", "request");		
		NAME.put("kannel_poptime", "datetime");
		NAME.put("dnpoststatus", "dn");		
		NAME.put("otpretry_yn", "account");		
		NAME.put("dnretry_yn", "account");
		NAME.put("msg_delivery_attempt", "request");		
		NAME.put("otpretry_popuptime", "datetime");		
		NAME.put("isdnretry", "request");

	
		NAME.put("param1", "request");		
		NAME.put("param2", "request");
		NAME.put("param3", "request");		
		NAME.put("param4", "request");
		NAME.put("customerip", "request");		
		NAME.put("auth_id", "account");
		NAME.put("attempttype", "request");		
		NAME.put("intl", "account");
		NAME.put("credit_rollback_yn", "account");		
		NAME.put("dnip", "route");
		NAME.put("dnport", "route");		
		NAME.put("dlttype", "request");		
		NAME.put("inserttype", "request");
		

	}

	
	public static String getGroupName(String key){
		
		return NAME.get(key);
	}
}

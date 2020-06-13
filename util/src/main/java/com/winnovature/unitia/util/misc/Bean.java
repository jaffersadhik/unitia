package com.winnovature.unitia.util.misc;

import java.util.Map;

public class Bean {

	public static void setDefaultValues(Map<String,String> msgmap){
		WinDate date= new WinDate();
		msgmap.put(MapKeys.ACKID, ACKIdGenerator.getAckId());
		msgmap.put(MapKeys.MSGID, msgmap.get(MapKeys.ACKID));
		long sysdate=System.currentTimeMillis();
		msgmap.put(MapKeys.RTIME, ""+sysdate);
		msgmap.put(MapKeys.RDATE,date.getDate());
		msgmap.put(MapKeys.RHOUR,date.getHour());
		msgmap.put(MapKeys.RMINUTE,date.getMinute());
		msgmap.put(MapKeys.RSECOND,date.getSecond());

	}
}

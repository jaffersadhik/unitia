package com.winnovature.unitia.util.misc;

import java.util.Map;

public class Bean {

	public static void setDefaultValues(Map<String,Object> msgmap){
		WinDate date= new WinDate();
		msgmap.put(MapKeys.ACKID, ACKIdGenerator.getAckId());
		msgmap.put(MapKeys.MSGID, msgmap.get(MapKeys.ACKID));
		long sysdate=System.currentTimeMillis();
		msgmap.put(MapKeys.RTIME, ""+sysdate);

	}
}

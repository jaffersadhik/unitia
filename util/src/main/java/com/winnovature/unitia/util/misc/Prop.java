package com.winnovature.unitia.util.misc;

import java.util.List;
import java.util.Properties;

public class Prop {

	private static Prop obj=null;
	
	private Prop() {
		
	}
	
	public static Prop getInstance() {
		
		if(obj==null) {
			
			obj=new Prop();
		}
		
		return obj;
	}
	
 
	public Properties getQueueDBProp() {
		return new FileReader().getProperties("common/queuedb.prop");
	}

	public Properties getBillingDBProp() {
		return new FileReader().getProperties("common/billingdb.prop");
	}

	public Properties getCoreDBProp() {
		return new FileReader().getProperties("common/coredb.prop");
	}

	public Properties getRedisQueueProp() {
		return new FileReader().getProperties("common/redisqueue.prop");
	}	
	
	

}

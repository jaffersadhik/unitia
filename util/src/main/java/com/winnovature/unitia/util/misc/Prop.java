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
	
	
	public List<String> get9Series(){
		
  		return new FileReader().readFile("numberingplan/9series.prop");

	}
	public List<String> get8series(){
	
		return new FileReader().readFile("numberingplan/8series.prop");

	}

	public List<String> get7series(){
	
		return new FileReader().readFile("numberingplan/7series.prop");

	}

	public List<String> get6series(){
	
		return new FileReader().readFile("numberingplan/6series.prop");

	}
	
	public List<String> getCircle(){
		
  		return new FileReader().readFile("numberingplan/circle.prop");

	}

	public List<String> getOperator(){
	
		return new FileReader().readFile("numberingplan/operator.prop");

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

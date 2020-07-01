package com.winnovature.unitia.util.instance;

import com.winnovature.unitia.util.dao.JVMUniqueId;

public class InstanceInfoMemory {

	public static  String INSTANCE_ID = null;

	static{
		
		INSTANCE_ID=new JVMUniqueId().getTransId();
		
		if(INSTANCE_ID==null){
			
			System.err.println("Unable to get JVM Id from Core DB ,System going to down");
			
			System.exit(-1);
		}
		
	}


	public static void update(){
		
		new JVMUniqueId().upateJVMId(INSTANCE_ID);
	}

}

package com.winnovature.unitia.util.misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
		
  		return new FileReader().readFile("9series.prop");

	}
	public List<String> get8series(){
	
		return new FileReader().readFile("8series.prop");

	}

	public List<String> get7series(){
	
		return new FileReader().readFile("7series.prop");

	}

	public List<String> get6series(){
	
		return new FileReader().readFile("6series.prop");

	}
	
	public List<String> getCircle(){
		
  		return new FileReader().readFile("circle.prop");

	}

	public List<String> getOperator(){
	
		return new FileReader().readFile("operator.prop");

	}


 
	public Properties getQueueDBProp() {
		
		String fileName="/unitia/queuedb.prop";
		
		Properties result= new FileReader().getProperties(fileName);
	
		if(result==null){
			
			File source=new File("/opt/tomcat/conf/queuedb.prop");
			File dest=new File(fileName);
			try {
				Files.copy(source.toPath(), dest.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			result= new FileReader().getProperties(fileName);
		}
		
		return result;
	}

	public Properties getBillingDBProp() {
		
		String fileName="/unitia/billingdb.prop";
		
		Properties result= new FileReader().getProperties(fileName);
	
		if(result==null){
			
			File source=new File("/opt/tomcat/conf/billingdb.prop");
			File dest=new File(fileName);
			try {
				Files.copy(source.toPath(), dest.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			result= new FileReader().getProperties(fileName);
		}
		
		return result;
	}

	public Properties getCoreDBProp() {
		
		String fileName="/unitia/coredb.prop";
		
		Properties result= new FileReader().getProperties(fileName);
	
		if(result==null){
			
			File source=new File("/opt/tomcat/conf/coredb.prop");
			File dest=new File(fileName);
			try {
				Files.copy(source.toPath(), dest.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			result= new FileReader().getProperties(fileName);
		}
		
		return result;
	}

	public Properties getRedisQueueProp() {
		
		String fileName="/unitia/redisqueue.prop";
		
		Properties result= new FileReader().getProperties(fileName);
	
		if(result==null){
			
			File source=new File("/opt/tomcat/conf/redisqueue.prop");
			File dest=new File(fileName);
			try {
				Files.copy(source.toPath(), dest.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			result= new FileReader().getProperties(fileName);
		}
		
		return result;
	}	
	
	

}

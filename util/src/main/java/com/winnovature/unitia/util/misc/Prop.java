package com.winnovature.unitia.util.misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

public class Prop {

	private static Prop obj=null;
	
	private Prop() {
		
		makeready();
	}
	
	
public void saveFile() {
		
			String file1="/unitia/httpd_haproxy.cfg";
			String file2="/unitia/dngen_haproxy.cfg";
			String file3="/unitia/dnreceiver_haproxy.cfg";
			

			File dest1=new File("/unitia/httpd_haproxy.cfg");
			File dest2=new File("/unitia/dngen_haproxy.cfg");
			File dest3=new File("/unitia/dnreceiver_haproxy.cfg");
			Properties result1= new FileReader().getProperties(file1);
			Properties result2= new FileReader().getProperties(file1);
			Properties result3= new FileReader().getProperties(file1);

			if(result1==null){
				
				File source=new File("/httpd_haproxy.cfg");
				File dest=new File(file1);
				try {
					Files.copy(source.toPath(), dest.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}
			
	if(result2==null){
				
				File source=new File("/dngen_haproxy.cfg");
				File dest=new File(file2);
				try {
					Files.copy(source.toPath(), dest.toPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}
	
	if(result3==null){
		
		File source=new File("/dnreceiver_haproxy.cfg");
		File dest=new File(file3);
		try {
			Files.copy(source.toPath(), dest.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
		
}
		
	

	private void makeready() {
		//saveFile();
		getCoreDBProp();
		getRedisQueue1Prop();
		
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
		
		String fileName="queuedb.prop";
		
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
		
		String fileName="billingdb.prop";
		
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
		
		String fileName="coredb.prop";
		
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
	
	
	public Properties getKannelDBProp() {
		
		String fileName="kanneldb.prop";
		
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
	public Properties getRedisSmppBindProp() {
		
		String fileName="redissmppbind.prop";
		
		Properties result= new FileReader().getProperties(fileName);
	
		if(result==null){
			
			File source=new File("/opt/tomcat/conf/redissmppbind.prop");
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
	
	public Properties getRedisCreditProp() {
		
		String fileName="rediscredit.prop";
		
		Properties result= new FileReader().getProperties(fileName);
	
		if(result==null){
			
			File source=new File("/opt/tomcat/conf/redissmppbind.prop");
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
	public Properties getRedisQueue1Prop() {
		
		String fileName="redisqueue1.prop";
		
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

public Properties getRedisQueue2Prop() {
		
		String fileName="redisqueue2.prop";
		
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
	public Properties getOptinDBProp() {
		
		String fileName="optindb.prop";
		
		Properties result= new FileReader().getProperties(fileName);
	
		if(result==null){
			
			File source=new File("/opt/tomcat/conf/optindb.prop");
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
	

	public Properties getOptoutDBProp() {
		
		String fileName="optoutdb.prop";
		
		Properties result= new FileReader().getProperties(fileName);
	
		if(result==null){
			
			File source=new File("/opt/tomcat/conf/optoutdb.prop");
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

	public Properties getDNDDBProp() {
		
		String fileName="dnddb.prop";
		
		Properties result= new FileReader().getProperties(fileName);
	
		if(result==null){
			
			File source=new File("/opt/tomcat/conf/dnddb.prop");
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

	public Properties getRouteDBProp() {
		
		String fileName="routedb.prop";
		
		Properties result= new FileReader().getProperties(fileName);
	
		if(result==null){
			
			File source=new File("/opt/tomcat/conf/routedb.prop");
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

	public Properties getDuplicateDBProp() {
		

		
		String fileName="duplicatedb.prop";
		
		Properties result= new FileReader().getProperties(fileName);
	
		if(result==null){
			
			File source=new File("/opt/tomcat/conf/duplicatedb.prop");
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

	public List<String> getCountryCode() {
		
  		return new FileReader().readFile("countrycode.prop");

	}


	public Properties getCampaignDBProp() {

		
		String fileName="uiqcampaigndb.prop";
		
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


	public Properties getRedisQueue3Prop() {

		String fileName="redisqueue3.prop";
		
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


	public Properties getRedisQueue4Prop() {

		
		String fileName="redisqueue4.prop";
		
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


	public Properties getRedisQueue5Prop() {
		
		String fileName="redisqueue5.prop";
		
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
	
	public Properties getRedisQueue6Prop() {
		
		String fileName="redisqueue6.prop";
		
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

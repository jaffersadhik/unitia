package com.winnovature.unitia.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SampleTemplateAndSMS {

	private static String CREATE_SQL="create table sample_template_sms(id INT PRIMARY KEY,username varchar(30) not null,senderid varchar(15) not null,template_id varchar(30) not null,template text,sms text)";
	
	private static SampleTemplateAndSMS obj=new SampleTemplateAndSMS();
	
	private Set<String> senderidtemplateidset=new HashSet<String>();
	
	private BlockingQueue<Map<String,String>> queue=new  LinkedBlockingQueue(); 
	
	private SampleTemplateAndSMS(){
		
		init();
		
		reload();
	}
	
	private void init() {
		


		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "sample_template_sms")){

				table.create(connection, CREATE_SQL, false);

			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	
		new T().start();
		
	}

	public static SampleTemplateAndSMS getInstance(){
		
		if(obj==null){
			
			obj=new SampleTemplateAndSMS();
		}
		
		return obj;
	}
	
	public void reload(){
		
		Set<String> temp=getData();
		
		if(temp!=null){
			
			senderidtemplateidset=temp;
		}
	}

	private Set<String> getData() {
		
		Connection connection=null;
		PreparedStatement statement=null;
		ResultSet resultset=null;
		Set<String> result=new HashSet<String>();
		try{
			connection=CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select username,senderid,template_id from sample_template_sms");
			resultset=statement.executeQuery();
			while(resultset.next()){
			
				result.add(resultset.getString("username").toLowerCase()+"~"+resultset.getString("senderid").toLowerCase()+"~"+resultset.getString("template_id"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		return result;
	}

	public void add(String username,String senderid,String template_id,String template,String fullmessage){
	
		if(!senderidtemplateidset.contains(username.toLowerCase()+"~"+senderid.toLowerCase()+"~"+template_id)){
		
			if(queue.size()<3000){
				
				Map<String,String> data=new HashMap<String,String>();
				data.put("senderid", senderid);
				data.put("template_id", template_id);
				data.put("template", template);
				data.put("fullmessage", fullmessage);
				data.put("username", username);

				queue.offer(data);
			}
		}
	}
	
	
	class T extends Thread{
	
		public void run(){
			
			
			while(true){
				
				if(queue.size()>0){
					
					doProcess();
					
				}else{
					gotosleep();
				}
			}
		}
		
		private void doProcess() {
			
			
			try{
				Map<String,String> data=queue.poll();
				if(data!=null){
				String username=data.get("username");
				String senderid=data.get("senderid");
				String template_id=data.get("template_id");
				if(!senderidtemplateidset.contains(username.toLowerCase()+"~"+senderid.toLowerCase()+"~"+template_id)){

					insert(data);
					
					SampleTemplateAndSMS.getInstance().reload();
				}

				}
			}catch(Exception e){
				
			}
			
		}

		private void insert(Map<String, String> data) {

			Connection connection=null;
			PreparedStatement statement=null;
			try{
				connection=CoreDBConnection.getInstance().getConnection();
				statement=connection.prepareStatement("insert into sample_template_sms(username,senderid,template_id,template,sms) values(?,?,?,?,?)");
						
				statement.setString(1, data.get("username"));
				statement.setString(2, data.get("senderid"));
				statement.setString(3, data.get("template_id"));
				statement.setString(4, data.get("template"));
				statement.setString(5, data.get("fullmessage"));
				statement.execute();
			}catch(Exception igmore){
				
			}finally{
				Close.close(statement);
				Close.close(connection);
			}
		}

		public void gotosleep(){
			
			try{
				Thread.sleep(500L);
			}catch(Exception e){
				
			}
		}
	}
}


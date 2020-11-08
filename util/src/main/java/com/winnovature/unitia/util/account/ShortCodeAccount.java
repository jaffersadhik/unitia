package com.winnovature.unitia.util.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.CoreDBConnection;
import com.winnovature.unitia.util.db.TableExsists;

public class ShortCodeAccount {

	private static final String SQL = "create table users_shortcode(id INT PRIMARY KEY AUTO_INCREMENT,username varchar(16),shortcode decimal(15,0),message_pattern varchar(300) ,post_yn varchar(1) default '0',post_url varchar(500),sms_send_yn varchar(1) default '0',sms_content varchar(500),senderid varchar(20) default 'WECARE',unique key(shortcode,message_pattern))";

	private static ShortCodeAccount obj=null;
	
	
	private Map<String,List<Map<String,String>>> shortcode=new HashMap<String,List<Map<String,String>>>();
	
	private ShortCodeAccount(){
		
		init();
		
		reload();
	}
	
	public void reload() {
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultset=null;
		Map<String,List<Map<String,String>>> temp=new HashMap<String,List<Map<String,String>>>();
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			statement=connection.prepareStatement("select username,shortcode,message_pattern,post_yn,post_url,sms_send_yn,sms_content,senderid,lpad(length(message_pattern),5,'0') o  from users_shortcode order by o desc");
			resultset=statement.executeQuery();
			while(resultset.next()){
				
				String shortcode=resultset.getString("shortcode");
				String message_pattern=resultset.getString("message_pattern");
				String post_yn=resultset.getString("post_yn");
				String post_url=resultset.getString("post_url");
				String sms_send_yn=resultset.getString("sms_send_yn");
				String sms_content=resultset.getString("sms_content");
				String username=resultset.getString("username");
				String senderid=resultset.getString("senderid");

				Map<String,String> data=new HashMap<String,String>();
				data.put("shortcode", shortcode);
				data.put("message_pattern", message_pattern);
				data.put("post_yn", post_yn);
				data.put("post_url", post_url);
				data.put("sms_send_yn", sms_send_yn);
				data.put("sms_content", sms_content);
				data.put("username", username);

				List<Map<String,String>> datalist=temp.get(shortcode);
				
				if(datalist==null){
					
					datalist=new ArrayList<Map<String,String>>();
					
					temp.put(shortcode, datalist);
				}
				
				datalist.add(data);
			}
		} catch (Exception e) {

			temp=null;
			e.printStackTrace();

		} finally {

			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}
		
		
		if(temp!=null){
			
			shortcode=temp;
		}
	}

	private void init() {



		Connection connection = null;
		try {
			connection = CoreDBConnection.getInstance().getConnection();
			TableExsists table = new TableExsists();
			if(!table.isExsists(connection, "users_shortcode")){
				
					if (table.create(connection, SQL, false)) {

						table.create(connection, "insert into users_shortcode(username,shortcode,message_pattern,post_yn,post_url,sms_send_yn,sms_content) values('unitia','56767','test test.*','1','http://http1:8080/api/clientdn?','1','test message content')", false);
					}
			
			}
			
			

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			Close.close(connection);
		}

	
		
	}

	
	public List<Map<String,String>> getData(String shortcodekey){
		
		return shortcode.get(shortcodekey);
	}
	public static ShortCodeAccount getInstance(){
		
		if(obj==null){
			
			obj=new ShortCodeAccount();
		}
		
		return obj;
	}
}
